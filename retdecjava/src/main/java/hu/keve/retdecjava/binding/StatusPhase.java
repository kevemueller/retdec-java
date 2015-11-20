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

import java.util.Arrays;

/**
 * Phase detail of Status.
 */
public final class StatusPhase {
    /**
     * Part.
     */
    private String part;
    /**
     * Phase name.
     */
    private String name;
    /**
     * Phase description.
     */
    private String description;
    /**
     * Completion percentage of this phase towards overall completion.
     */
    private int completion;
    /**
     * Warnings.
     */
    private String[] warnings;

    public String getPart() {
        return part;
    }

    public void setPart(final String part) {
        this.part = part;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(final int completion) {
        this.completion = completion;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(final String[] warnings) {
        this.warnings = warnings;
    }

    /**
     * Utility function for comparison.
     * 
     * @param a
     *            an Object, possibly null
     * @param b
     *            another Object, possibly null
     * @return true, if a equals b
     */
    private boolean isEqual(final Object a, final Object b) {
        return a == null ? b == null : a.equals(b);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || !(obj instanceof StatusPhase)) {
            return false;
        }
        StatusPhase spObj = (StatusPhase) obj;
        return completion == spObj.completion && isEqual(part, spObj.part) && isEqual(name, spObj.name)
                && isEqual(description, spObj.description) && Arrays.equals(warnings, spObj.warnings);
    }

    @Override
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        sb.append(part);
        sb.append(name);
        sb.append(description);
        sb.append(completion);
        sb.append(warnings);
        return sb.hashCode();
    }
}
