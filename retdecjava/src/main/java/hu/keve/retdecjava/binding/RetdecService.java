/*
Copyright (c) 2015, Keve Müller
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the author nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL KEVE MÜLLER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package hu.keve.retdecjava.binding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import hu.keve.retdecjava.binding.DecompilationResult.DecompilationOutput;

/**
 * Class binding to the Retdec.com REST API.
 * 
 */
public final class RetdecService {
    /**
     * The main entry point for the service.
     */
    public static final String URL = "https://retdec.com/service/api";
    /**
     * Time we wait until re-polling the status. 15 seconds.
     */
    private static final int POLL_INTERVAL_MS = 15 * 1000;
    /**
     * The top level web-target for the service.
     */
    private final WebTarget retdecTarget;
    /**
     * ObjectMapper instance for converting JSON to POJO.
     */
    private final ObjectMapper objectMapper;

    /**
     * Construct a service instance using the provided API key.
     * 
     * @param apiKey
     *            The API key used for authentication against the service.
     */
    public RetdecService(final String apiKey) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(MultiPartFeature.class);

        Client client = ClientBuilder.newClient(clientConfig);
        // client.register(new LoggingFilter());
        retdecTarget = client.register(HttpAuthenticationFeature.basic(apiKey, "")).target(URL);
        objectMapper = new ObjectMapper();
    }

    /**
     * Invoke the test/echo service.
     * 
     * @param args
     *            the arguments to send to the service.
     * @return the arguments channeled back from the service.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> test(final Map<String, String> args) {
        WebTarget echoTarget = retdecTarget.path("test/echo");
        for (Entry<String, String> arg : args.entrySet()) {
            echoTarget = echoTarget.queryParam(arg.getKey(), arg.getValue());
        }
        Invocation.Builder invocationBuilder = echoTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        ObjectNode respNode = response.readEntity(ObjectNode.class);
        return objectMapper.convertValue(respNode, Map.class);
    }

    public void fileInfo() {
        // TODO: write me.
    }

    public DecompilationResponse decompile(final AbstractDecompilationRequest request)
            throws IOException, ServiceException {
        WebTarget decompilationTarget = retdecTarget.path("decompiler/decompilations");
        FormDataMultiPart mp = new FormDataMultiPart();

        Map<String, Object> fd = request.getFormData();

        for (Entry<String, Object> requestField : fd.entrySet()) {
            Object value = requestField.getValue();
            if (null != value) {
                if (value instanceof String) {
                    mp.field(requestField.getKey(), (String) value);
                } else if (value instanceof Boolean) {
                    mp.field(requestField.getKey(), ((Boolean) value).booleanValue() ? "yes" : "no");
                } else if (value instanceof Enum) {
                    mp.field(requestField.getKey(), value.toString());
                } else if (value instanceof File) {
                    mp.bodyPart(new FileDataBodyPart(requestField.getKey(), (File) value, MediaType.TEXT_PLAIN_TYPE));
                } else {
                    throw new IllegalArgumentException("Cannot post value of class " + value.getClass());
                }
            }
        }

        Invocation.Builder invocationBuilder = decompilationTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response response = invocationBuilder.post(Entity.entity(mp, mp.getMediaType()), Response.class);
        int status = response.getStatus();
        ObjectNode respNode = response.readEntity(ObjectNode.class);
        switch (status) {
        case 400:
        case 422:
            ErrorResponse err = objectMapper.convertValue(respNode, ErrorResponse.class);
            throw new ServiceException(status, err);
        default:
            return objectMapper.convertValue(respNode, DecompilationResponse.class);
        }
    }

    /**
     * Obtain the results of decompilation. Currently the retdec.com API only
     * supports polling.
     * 
     * @param resp
     *            the initial response to the decompilation request.
     * @param res
     *            the result object accumulating the results of the compilation.
     * @throws InterruptedException
     *             when the polling was interrupted.
     * @throws IOException
     *             when an I/O error occured.
     * @throws BindingException
     *             when an API binding exception occurs
     */
    private void decompilePoll(final DecompilationResponse resp, final DecompilationResult res)
            throws InterruptedException, IOException, BindingException {
        res.started();
        String statusUrl = resp.getStatusUrl();
        ArrayList<StatusPhase> reportedPhases = new ArrayList<StatusPhase>();
        StatusResponse status;
        do {
            status = getResponse(statusUrl, StatusResponse.class);
            res.setStatus(status);
            for (StatusPhase phase : status.getPhases()) {
                if (!reportedPhases.contains(phase)) {
                    res.phaseChange(phase);
                    reportedPhases.add(phase);
                }
            }
            if (status.isFinished()) {
                break;
            }
            Thread.sleep(POLL_INTERVAL_MS);
        } while (true);

        OutputsResponse outputs = getResponse(resp.getOutputsUrl(), OutputsResponse.class);

        for (Entry<String, Object> output : outputs.getLinks().entrySet()) {
            String key = output.getKey();
            Object value = output.getValue();
            if (value instanceof String) {
                // FIXME:
                // This is not intuitive. Output returns links to outputs that
                // are not existing.
                // Only looking at outputs should be sufficient!
                boolean stop = false;
                if ("cg".equals(key)) {
                    stop = null == status.getCg();
                }

                if (!stop && res.acceptOutput(DecompilationOutput.valueOf(key))) {
                    WebTarget outputTarget = retdecTarget.path(((String) value).substring(URL.length()));

                    Invocation.Builder invocationBuilder = outputTarget.request();
                    Response response = invocationBuilder.get();
                    int statusCode = response.getStatus();
                    MediaType mt = response.getMediaType();
                    String cds = response.getHeaderString("Content-Disposition");
                    String fileName;
                    try {
                        ContentDisposition cd = new ContentDisposition(cds);
                        fileName = cd.getFileName();
                    } catch (ParseException e) {
                        fileName = null;
                    }
                    InputStream in = response.readEntity(InputStream.class);
                    res.consumeOutput(fileName, mt.toString(), in);
                }
            } else if (value instanceof Map) {
                for (Map.Entry<Object, Object> e : ((Map<Object, Object>) value).entrySet()) {
                    Object skey = e.getKey();
                    Object svalue = e.getValue();
                }

            } else {
                throw new BindingException("Unhandled value of class " + value.getClass());
            }
        }
        res.finished();
    }

    /**
     * Obtain the results of decompilation. The results are accumulated in a
     * separate thread, i.e. this method returns immediately.
     * 
     * @param resp
     *            the initial response to the decompilation request.
     * @param res
     *            the result object accumulating the results of the compilation.
     * @return the main thread in which the decompilation is controlled.
     */
    public Thread decompileAsync(final DecompilationResponse resp, final DecompilationResult res) {
        res.setId(resp.getId());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO: In async mode we could spawn output consumers
                    // parallel as soon as partial results are available.
                    // This requires load management throttle.
                    decompilePoll(resp, res);
                } catch (InterruptedException | IOException | BindingException e) {
                    res.failed(e);
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * Obtain the results of decompilation. The method returns when the
     * decompilation finished.
     * 
     * @param resp
     *            the initial response to the decompilation request.
     * @param res
     *            the result object accumulating the results of the compilation.
     */
    public void decompileSync(final DecompilationResponse resp, final DecompilationResult res) {
        res.setId(resp.getId());
        try {
            decompilePoll(resp, res);
        } catch (InterruptedException | IOException | BindingException e) {
            res.failed(e);
        }
    }

    /**
     * Get a response.
     * 
     * @param absoluteURL
     *            the absolute URL the response is to be fetched from.
     * @param respClass
     *            the class the object the response is to be parsed into.
     * @return the response object.
     * @throws BindingException
     *             when an API binding exception occurs
     */
    private <T> T getResponse(final String absoluteURL, final Class<T> respClass) throws BindingException {
        if (!absoluteURL.startsWith(URL)) {
            throw new IllegalArgumentException("wrong prefix " + absoluteURL);
        }
        WebTarget statusTarget = retdecTarget.path(absoluteURL.substring(URL.length()));
        Invocation.Builder invocationBuilder = statusTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response response = invocationBuilder.get();
        int statusCode = response.getStatus();
        ObjectNode respNode = response.readEntity(ObjectNode.class);
        try {
            switch (statusCode) {
            case 200:
                return objectMapper.convertValue(respNode, respClass);
            default:
                throw new BindingException("Unhandled HTTP status " + statusCode);
            }
        } catch (IllegalArgumentException ex) {
            throw new BindingException(ex, respNode.toString());
        }
    }
}
