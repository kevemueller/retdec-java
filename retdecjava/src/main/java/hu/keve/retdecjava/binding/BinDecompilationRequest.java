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
import java.util.List;
import java.util.Map;

/**
 * Class encapsulating information for a bin decompilation request.
 * 
 * @see <a href=
 *      "https://retdec.com/api/docs/decompiler.html#parameters-only-for-the-bin-mode">
 *      API</a>
 *
 */
public final class BinDecompilationRequest extends AbstractDecompilationRequest {
    /**
     * What instructions should be decoded?
     */
    public enum SelectiveDecompilationDecoding {
        /** everything. */
        EVERYTHING, /** only instructions in the provided range. */
        ONLY;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };
    }
    
    /**
     * The architecture.
     */
    private Architecture architecture;
    /**
     * List of functions to decompile in selective decompilation mode.
     */
    private List<String> selDecompFuncs;
    /**
     * List of address ranges to decompile in selective decompilation mode.
     */
    private List<String> selDecompRanges;
    /**
     * What instructions should be decoded?
     */
    private SelectiveDecompilationDecoding selDecompDecoding;
    /**
     * PDB file from the Microsoft compiler.
     */
    private File pdb;

    /**
     * Construct a bin decompilation request with the required parameter input.
     * 
     * @param input the input file.
     */
    public BinDecompilationRequest(final File input) {
        super(DecompilationRequestMode.BIN, input);
    }

    @Override
    public Map<String, Object> getFormData() {
        Map<String, Object> map = super.getFormData();
        map.put("architecture", architecture);
        map.put("sel_decomp_funcs", selDecompFuncs);
        map.put("sel_decomp_ranges", selDecompRanges);
        map.put("sel_decomp_decoding", selDecompDecoding);
        map.put("pdb", pdb);
        return map;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    public void setArchitecture(final Architecture architecture) {
        this.architecture = architecture;
    }

    public List<String> getSelDecompFuncs() {
        return selDecompFuncs;
    }

    public void setSelDecompFuncs(final List<String> selDecompFuncs) {
        this.selDecompFuncs = selDecompFuncs;
    }

    public List<String> getSelDecompRanges() {
        return selDecompRanges;
    }

    public void setSelDecompRanges(final List<String> selDecompRanges) {
        this.selDecompRanges = selDecompRanges;
    }

    public SelectiveDecompilationDecoding getSelDecompDecoding() {
        return selDecompDecoding;
    }

    public void setSelDecompDecoding(final SelectiveDecompilationDecoding selDecompDecoding) {
        this.selDecompDecoding = selDecompDecoding;
    }

    public File getPdb() {
        return pdb;
    }

    public void setPdb(final File pdb) {
        this.pdb = pdb;
    }
}
