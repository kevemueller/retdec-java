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

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the callbacks that process the results of decompilation. The
 * order of methods is grouped in three stages. In the initialization phase
 * setId and started are called in this order. In the working phase setStatus,
 * phaseChange, acceptOutput and consumeOutput functions are called in arbitrary
 * order. In the closing phase, finished is called.
 */
public interface DecompilationResult {
    /**
     * Enumeration of decompilation output keys.
     */
    enum DecompilationOutput {
        /** source code in high-level-language. */
        hll, /** disassembled file. */
        dsm, /** call graph. */
        cg, /** control-flow-graph. */
        cfgs, /** archive. */
        archive, /** The binary file compiled from provided source. */
        binary;
    }

    /**
     * Set the unique identifier of the decompilation.
     * 
     * @param id
     *            the unique identifier.
     */
    void setId(String id);

    /**
     * Called after setId, signaling that the decompilation is started.
     */
    void started();

    /**
     * Set the status of the decompilation.
     * 
     * @param status
     *            the status of the decompilation.
     */
    void setStatus(StatusResponse status);

    /**
     * Inform about a change in the phase of the decompilation.
     * 
     * @param phase
     *            the completed phase.
     */
    void phaseChange(StatusPhase phase);

    /**
     * Inform about the availability of a decompilation output, requesting
     * feedback if the corresponding stream shall be fetched.
     * 
     * @param key
     *            the type of the available DecompilationOutput.
     * @return true if the corresponding stream shall be fetched, false it not.
     */
    boolean acceptOutput(DecompilationOutput key);

    /**
     * Provide the a stream to the decompilation result. The implementing method
     * should strive to fetch the stream as eager as possible.
     * 
     * @param fileName
     *            the suggested filename, possibly null if no suggestion can be
     *            made.
     * @param mediaType
     *            the provided media type.
     * @param in
     *            the stream.
     * @throws IOException
     *             if an I/O error occurs.
     */
    void consumeOutput(String fileName, String mediaType, InputStream in) throws IOException;

    /**
     * Called when the decompilation finished.
     */
    void finished();

    /**
     * Called when the decompilation failed.
     * 
     * @param e
     *            the cause of the failure.
     */
    void failed(Exception e);
}
