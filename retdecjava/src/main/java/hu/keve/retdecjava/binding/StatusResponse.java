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

/**
 * Response to the Status request.
 */
public final class StatusResponse {
    /**
     * The unique id of the decompilation.
     */
    private String id;
    /**
     * The pending state flag.
     */
    private boolean pending;
    /**
     * The running state flag.
     */
    private boolean running;
    /**
     * The finished state flag.
     */
    private boolean finished;
    /**
     * The succeeded state flag.
     */
    private boolean succeeded;
    /**
     * The failed state flag.
     */
    private boolean failed;
    /**
     * The error string.
     */
    private String error;
    /**
     * The completion percentage.
     */
    private int completion;
    /**
     * The details of the decompilation phases.
     */
    private StatusPhase[] phases;
    /**
     * The details of the call graphs.
     */
    private StatusCgArchive[] cg;
    /**
     * The details of the archive.
     * 
     * @return
     */
    private StatusCgArchive archive;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(final boolean pending) {
        this.pending = pending;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(final boolean succeeded) {
        this.succeeded = succeeded;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(final int completion) {
        this.completion = completion;
    }

    public StatusPhase[] getPhases() {
        return phases;
    }

    public void setPhases(final StatusPhase[] phases) {
        this.phases = phases;
    }

    public StatusCgArchive[] getCg() {
        return cg;
    }

    public void setCg(final StatusCgArchive[] cg) {
        this.cg = cg;
    }

    public StatusCgArchive getArchive() {
        return archive;
    }

    public void setArchive(final StatusCgArchive archive) {
        this.archive = archive;
    }
}
