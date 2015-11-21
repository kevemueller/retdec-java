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
import java.util.Map;

/**
 * Class encapsulating information for a c decompilation request.
 * 
 * @see <a href=
 *      "https://retdec.com/api/docs/decompiler.html#parameters-only-for-the-c-mode">
 *      API</a>
 *
 */
public final class CDecompilationRequest extends AbstractDecompilationRequest {
    /**
     * Compiler.
     */
    public enum CompCompiler {
        /** gcc. */
        GCC, /** Clang. */
        CLANG;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };
    }

    /**
     * Optimizations.
     */
    public enum CompOptimizations {
        /** O0. */
        O0, /** O1. */
        O1, /** O2. */
        O2, /** O3. */
        O3;
        @Override
        public String toString() {
            return "-" + super.toString();
        }
    }

    /**
     * The architecture.
     */
    private Architecture architecture;
    /**
     * The file format.
     */
    private FileFormat fileFormat;
    /**
     * The compiler to use.
     */
    private CompCompiler compCompiler;
    /**
     * The compiler optimizations to set.
     */
    private CompOptimizations compOptimizations;
    /**
     * Compile with debugging information?
     */
    private Boolean compDebug;
    /**
     * Strip the executable?
     */
    private Boolean compStrip;

    /**
     * Construct a C (compilation/decompilation) request.
     * 
     * @param input
     *            the file to compile and decompile.
     */
    public CDecompilationRequest(final File input) {
        super(DecompilationRequestMode.C, input);
    }

    @Override
    public Map<String, Object> getFormData() {
        Map<String, Object> map = super.getFormData();
        map.put("architecture", architecture);
        map.put("file_format", fileFormat);
        map.put("comp_compiler", compCompiler);
        map.put("comp_optimizations", compOptimizations);
        map.put("comp_debug", compDebug);
        map.put("comp_strip", compStrip);
        return map;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    public void setArchitecture(final Architecture architecture) {
        this.architecture = architecture;
    }

    public FileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(final FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    public CompCompiler getCompCompiler() {
        return compCompiler;
    }

    public void setCompCompiler(final CompCompiler compCompiler) {
        this.compCompiler = compCompiler;
    }

    public CompOptimizations getCompOptimizations() {
        return compOptimizations;
    }

    public void setCompOptimizations(final CompOptimizations compOptimizations) {
        this.compOptimizations = compOptimizations;
    }

    public Boolean getCompDebug() {
        return compDebug;
    }

    public void setCompDebug(final Boolean compDebug) {
        this.compDebug = compDebug;
    }

    public Boolean getCompStrip() {
        return compStrip;
    }

    public void setCompStrip(final Boolean compStrip) {
        this.compStrip = compStrip;
    }
}
