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
package hu.keve.retdecjava;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map.Entry;

import hu.keve.retdecjava.binding.AbstractDecompilationRequest;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.Architecture;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.FileFormat;
import hu.keve.retdecjava.binding.BinDecompilationRequest;
import hu.keve.retdecjava.binding.CDecompilationRequest;
import hu.keve.retdecjava.binding.CDecompilationRequest.CompCompiler;
import hu.keve.retdecjava.binding.CDecompilationRequest.CompOptimizations;
import hu.keve.retdecjava.binding.DecompilationRequestMode;
import hu.keve.retdecjava.binding.DecompilationResponse;
import hu.keve.retdecjava.binding.DefaultDecompilationResult;
import hu.keve.retdecjava.binding.RawDecompilationRequest;
import hu.keve.retdecjava.binding.RetdecService;
import hu.keve.retdecjava.binding.StatusPhase;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;

/**
 * Client application for the retdec API.
 *
 */
public final class RetdecClient {
    /**
     * The service.
     */
    private final RetdecService retdecService;

    /**
     * Class for processing decompilation state and results. Displays progress
     * on stderr and saves output in outDir using the suggested names.
     */
    private final class FileSaveDecompilationResult extends DefaultDecompilationResult {
        /**
         * The output directory.
         */
        private final File outDir;

        /**
         * Construct an instance.
         * 
         * @param outDir
         *            the output directory.
         */
        FileSaveDecompilationResult(final File outDir) {
            this.outDir = outDir;
        }

        @Override
        public void started() {
            System.err.println("Started decompilation with unique identifier " + getId());
        }

        @Override
        public void phaseChange(final StatusPhase phase) {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("[%3d] %s", phase.getCompletion(), phase.getName()));
            if (!phase.getName().equals(phase.getDescription())) {
                sb.append(' ').append(phase.getDescription());
            }
            if (phase.getWarnings().length > 0) {
                sb.append(" ! warnings");
            }
            System.err.println(sb);
        }

        @Override
        public boolean acceptOutput(final DecompilationOutput key) {
            return true;
        };

        @Override
        public void consumeOutput(final String fileName, final String mediaType, final InputStream in)
                throws IOException {
            System.err.println("Consuming " + fileName);
            File outFile = new File(outDir, fileName);
            Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        @Override
        public void finished() {
            System.err.println("Decompilation finished.");
        }

    }

    private static final ValueConverter<Boolean> BOOLEANCONVERTER = new ValueConverter<Boolean>() {
        @Override
        public Class<? extends Boolean> valueType() {
            return Boolean.class;
        }

        @Override
        public String valuePattern() {
            return "yes|no";
        }

        @Override
        public Boolean convert(final String value) {
            if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
    };

    private static class EnumConverter implements ValueConverter<Enum> {
        private final Class<? extends Enum> enumClass;

        public EnumConverter(final Class<? extends Enum> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Enum convert(final String value) {
            for (Enum e : enumClass.getEnumConstants()) {
                if (value.equalsIgnoreCase(e.toString())) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public Class<? extends Enum> valueType() {
            return enumClass;
        }

        @Override
        public String valuePattern() {
            StringBuffer sb = new StringBuffer();
            for (Object ec : enumClass.getEnumConstants()) {
                if (0 != sb.length()) {
                    sb.append('|');
                }
                sb.append(ec.toString());
            }
            return sb.toString();
        }
    }

    /**
     * Construct an instance using the provided key.
     * 
     * @param apikey
     *            the API key.
     */
    public RetdecClient(final String apikey) {
        retdecService = new RetdecService(apikey);
    }

    /**
     * Invoke the decompilation on a request.
     * 
     * @param request
     *            the decompilation request.
     * @param outDir
     *            the output directory.
     * @throws Exception
     *             if an exception occurs.
     */
    private void invoke(final AbstractDecompilationRequest request, final File outDir) throws Exception {
        DecompilationResponse resp = retdecService.decompile(request);
        invoke(resp, outDir);
    }

    /**
     * Invoke the decompilation on a previous request's response.
     * 
     * @param resp
     *            the response of a previous decompilation request.
     * @param outDir
     *            the output directory.
     * @throws Exception
     *             if an exception occurs.
     */
    private void invoke(final DecompilationResponse resp, final File outDir) throws Exception {
        FileSaveDecompilationResult res = new FileSaveDecompilationResult(outDir);
        Thread t = retdecService.decompileAsync(resp, res);
        t.join();
        if (null != res.getException()) {
            throw res.getException();
        }
    }

    /**
     * Main entry point for the retdec api client application.
     * 
     * @param args
     *            the command line arguments.
     * @throws Exception
     *             if an exception occurs.
     */
    public static void main(final String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> apikeyOption = parser.accepts("apikey", "use API key").withRequiredArg()
                .ofType(String.class).describedAs("the API key");
        ArgumentAcceptingOptionSpec<String> idOption = parser.accepts("id", "use a previous compilation's id")
                .withRequiredArg().ofType(String.class).describedAs("the id");
        ArgumentAcceptingOptionSpec<File> inputOption = parser.accepts("input", "decompilation input")
                .requiredUnless(idOption).withRequiredArg().ofType(File.class).describedAs("the input file");
        ArgumentAcceptingOptionSpec<File> outdirOption = parser.accepts("outdir", "output directory").withRequiredArg()
                .ofType(File.class).defaultsTo(new File(".")).describedAs("the output directory");
        parser.accepts("help", "Show help.").forHelp();

        addOptions(parser, AbstractDecompilationRequest.class);
        addOptions(parser, CDecompilationRequest.class);
        addOptions(parser, BinDecompilationRequest.class);
        addOptions(parser, RawDecompilationRequest.class);

        // mode is special
        ArgumentAcceptingOptionSpec<DecompilationRequestMode> modeOption = (ArgumentAcceptingOptionSpec<DecompilationRequestMode>) parser
                .recognizedOptions().get("mode");
        modeOption.defaultsTo(DecompilationRequestMode.C);

        parser.printHelpOn(System.out);

        OptionSet options = parser.parse(args);
        RetdecClient rdc = new RetdecClient(options.valueOf(apikeyOption));
        if (options.has(idOption)) {
            DecompilationResponse resp = new DecompilationResponse(options.valueOf(idOption));
            rdc.invoke(resp, options.valueOf(outdirOption));
        } else {
            AbstractDecompilationRequest req;
            DecompilationRequestMode mode = options.valueOf(modeOption);
            switch (mode) {
            case C:
                req = new CDecompilationRequest(options.valueOf(inputOption));
                break;
            case BIN:
                req = new BinDecompilationRequest(options.valueOf(inputOption));
                break;
            case RAW:
                req = new RawDecompilationRequest(options.valueOf(inputOption));
                break;
            default:
                throw new IllegalArgumentException();
            }
            for (Entry<OptionSpec<?>, List<?>> opt : options.asMap().entrySet()) {
                if (options.has(opt.getKey()) && opt.getKey() != apikeyOption && opt.getKey() != outdirOption
                        && opt.getKey() != inputOption && opt.getKey() != modeOption) {
                    String fieldName = opt.getKey().options().get(0);
                    Object fieldValue = opt.getValue().get(0);
                    System.err.println(fieldName + " --> " + opt.getValue());
                    Method setter = req.getClass().getMethod(
                            "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
                            fieldValue.getClass());
                    setter.invoke(req, fieldValue);
                }
            }

            rdc.invoke(req, options.valueOf(outdirOption));
        }

        // NXOY3mDgal -- C compilation with non-options
        // XnRDb0Wj06 -- C compilation with full non-options
        // r8OWzekjb0 -- C compilation with call graphs.
    }

    /**
     * Add options corresponding to the declared fields of the provided request
     * class.
     * 
     * @param parser
     *            the options parser to add options to.
     * @param reqClass
     *            the request class to retrieve fields from.
     */
    @SuppressWarnings("unchecked")
    private static void addOptions(final OptionParser parser,
            final Class<? extends AbstractDecompilationRequest> reqClass) {
        for (Field f : reqClass.getDeclaredFields()) {
            if (f.getType().isAssignableFrom(Boolean.class)) {
                parser.accepts(f.getName()).withRequiredArg().ofType(f.getType())
                        .withValuesConvertedBy(BOOLEANCONVERTER);
            } else if (f.getType().isEnum()) {
                parser.accepts(f.getName()).withRequiredArg().ofType(f.getType())
                        .withValuesConvertedBy(new EnumConverter((Class<? extends Enum>) f.getType()));
            } else if (f.getType().isAssignableFrom(List.class)) {
                parser.accepts(f.getName()).withRequiredArg().ofType(String.class).withValuesSeparatedBy(',')
                        .describedAs("s1,s2,...");
            } else {
                parser.accepts(f.getName()).withRequiredArg().ofType(f.getType());
            }
        }
    }
}
