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
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for requests sent to the retdec.com REST API.
 *
 */
public abstract class AbstractDecompilationRequest implements RetdecFormRequest {
    /**
     * Type of the target high-level language.
     * <li>{@link #C}</li>
     * <li>{@link #PY}</li>
     */
    public enum TargetLanguage {
        /**
         * The C language (C99).
         */
        C, /**
            * A Python-like language.
            */
        PY;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };
    }

    /**
     * Format of the generated call and control-flow graphs.
     */
    public enum GraphFormat {
        /** PNG format. */
        PNG, /** SVG format. */
        SVG, /** PDF format. */
        PDF;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };

    }

    /**
     * Variable naming style.
     */
    public enum DecompVarNames {
        /** readable. */
        READABLE, /** address. */
        ADDRESS, /** hungarian. */
        HUNGARIAN, /** simple. */
        SIMPLE, /** unified. */
        UNIFIED;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };

    }

    /**
     * Optimizations.
     */
    public enum DecompOptimizations {
        /** None. */
        NONE, /** Limited. */
        LIMITED, /** Normal. */
        NORMAL, /** Aggressive. */
        AGGRESSIVE;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };

    }

    /**
     * Architecture.
     */
    public enum Architecture {
        /** Autodetect. */
        AUTO, /** x86. */
        X86, /** arm. */
        ARM, /** arm+thumb. */
        THUMB, /** mips. */
        MIPS, /** pic32. */
        PIC32, /** ppc. */
        POWERPC;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };

    }

    /**
     * File format.
     */
    public enum FileFormat {
        /** ELF. */
        ELF, /** PE. */
        PE;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };

    }

    /**
     * The decompilation mode.
     */
    private final DecompilationRequestMode mode;
    /**
     * The file to decompile.
     */
    private File input;
    /**
     * The target language for decompilation.
     */
    private TargetLanguage targetLanguage;
    /**
     * The format of the generated call and control-flow graphs.
     */
    private GraphFormat graphFormat;
    /**
     * Variable naming convention.
     */
    private DecompVarNames decompVarNames;
    /**
     * Type of optimizations performed by the compiler.
     */
    private DecompOptimizations decompOptimizations;
    /**
     * Should all functions be decompiled?
     */
    private Boolean decompUnreachableFunctions;
    /**
     * Should comments indicating the address of the decompiled line be emitted?
     */
    private Boolean decompEmitAddress;
    /**
     * Generate call graphs.
     */
    private Boolean generateCallGraph;
    /**
     * Generate control flow graphs?
     */
    private Boolean generateControlFlowGraphs;
    /**
     * Generate archive?
     */
    private Boolean generateArchive;

    /**
     * Construct a decompilation request with the only required field input set.
     * 
     * @param mode
     *            the decompilation mode.
     * @param input
     *            the input file.
     */
    public AbstractDecompilationRequest(final DecompilationRequestMode mode, final File input) {
        this.mode = mode;
        this.input = input;
    }

    /**
     * Retrieve the form data.
     * 
     * @return the form data.
     */
    public Map<String, Object> getFormData() {
        // For now prefer plain enumeration instead of annotations and
        // reflection.
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("mode", mode);
        map.put("input", input);
        map.put("target_language", targetLanguage);
        map.put("graph_format", graphFormat);
        map.put("decomp_var_names", decompVarNames);
        map.put("decomp_optimizations", decompOptimizations);
        map.put("decomp_unreachable_functions", decompUnreachableFunctions);
        map.put("decomp_emit_address", decompEmitAddress);
        map.put("generate_call_graph", generateCallGraph);
        map.put("generate_control_flow_graphs", generateControlFlowGraphs);
        map.put("generate_archive", generateArchive);
        return map;
    }

    public final DecompilationRequestMode getMode() {
        return mode;
    }

    public final File getInput() {
        return input;
    }

    public final void setInput(final File input) {
        this.input = input;
    }

    public final TargetLanguage getTargetLanguage() {
        return targetLanguage;
    }

    public final void setTargetLanguage(final TargetLanguage targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public final GraphFormat getGraphFormat() {
        return graphFormat;
    }

    public final void setGraphFormat(final GraphFormat graphFormat) {
        this.graphFormat = graphFormat;
    }

    public final DecompVarNames getDecompVarNames() {
        return decompVarNames;
    }

    public final void setDecompVarNames(final DecompVarNames decompVarNames) {
        this.decompVarNames = decompVarNames;
    }

    public final DecompOptimizations getDecompOptimizations() {
        return decompOptimizations;
    }

    public final void setDecompOptimizations(final DecompOptimizations decompOptimizations) {
        this.decompOptimizations = decompOptimizations;
    }

    public final Boolean getDecompUnreachableFunctions() {
        return decompUnreachableFunctions;
    }

    public final void setDecompUnreachableFunctions(final Boolean decompUnreachableFunctions) {
        this.decompUnreachableFunctions = decompUnreachableFunctions;
    }

    public final Boolean getDecompEmitAddress() {
        return decompEmitAddress;
    }

    public final void setDecompEmitAddress(final Boolean decompEmitAddress) {
        this.decompEmitAddress = decompEmitAddress;
    }

    public final Boolean getGenerateCallGraph() {
        return generateCallGraph;
    }

    public final void setGenerateCallGraph(final Boolean generateCallGraph) {
        this.generateCallGraph = generateCallGraph;
    }

    public final Boolean getGenerateControlFlowGraphs() {
        return generateControlFlowGraphs;
    }

    public final void setGenerateControlFlowGraphs(final Boolean generateControlFlowGraphs) {
        this.generateControlFlowGraphs = generateControlFlowGraphs;
    }

    public final Boolean getGenerateArchive() {
        return generateArchive;
    }

    public final void setGenerateArchive(final Boolean generateArchive) {
        this.generateArchive = generateArchive;
    }
}
