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

import java.util.HashMap;

/**
 * Response wrapper for the Decompilation request.
 *
 */
public final class DecompilationResponse extends AbstractLinksResponse {

    /**
     * Construct empty DecompilationResponse.
     */
    public DecompilationResponse() {
    }

    /**
     * Construct DecompilationResponse with id of a previous decompilation.
     * 
     * @param id
     *            the id of the previous decompilation.
     */
    public DecompilationResponse(final String id) {
        HashMap<String, Object> l = new HashMap<String, Object>();

        String decompilationBase = RetdecService.URL + "decompiler/decompilations/" + id;
        l.put("decompilation", decompilationBase);
        l.put("outputs", decompilationBase + "/outputs");
        l.put("status", decompilationBase + "/status");
        setLinks(l);
    }

    public String getDecompilationUrl() {
        return (String) getLinks().get("decompilation");
    }

    public String getStatusUrl() {
        return (String) getLinks().get("status");
    }

    public String getOutputsUrl() {
        return (String) getLinks().get("outputs");
    }
}
