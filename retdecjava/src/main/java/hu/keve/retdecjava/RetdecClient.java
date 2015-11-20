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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import hu.keve.retdecjava.binding.AbstractDecompilationRequest.Architecture;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.DecompOptimizations;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.DecompVarNames;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.FileFormat;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.GraphFormat;
import hu.keve.retdecjava.binding.AbstractDecompilationRequest.TargetLanguage;
import hu.keve.retdecjava.binding.CDecompilationRequest;
import hu.keve.retdecjava.binding.CDecompilationRequest.CompCompiler;
import hu.keve.retdecjava.binding.CDecompilationRequest.CompOptimizations;
import hu.keve.retdecjava.binding.DecompilationResponse;
import hu.keve.retdecjava.binding.DefaultDecompilationResult;
import hu.keve.retdecjava.binding.RetdecService;
import hu.keve.retdecjava.binding.StatusPhase;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

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
     * Invoke the decompilation on an input file.
     * 
     * @param input
     *            the input file for decompilation.
     * @param outDir
     *            the output directory.
     * @throws Exception
     *             if an exception occurs.
     */
    private void invoke(final File input, final File outDir) throws Exception {
        CDecompilationRequest request = new CDecompilationRequest(input);
        // request non-defaults.
//        request.setTargetLanguage(TargetLanguage.PY);
//        request.setGraphFormat(GraphFormat.PDF);
//        request.setDecompVarNames(DecompVarNames.SIMPLE);
//        request.setDecompOptimizations(DecompOptimizations.AGGRESSIVE);
//        request.setDecompUnreachableFunctions(true);
//        request.setDecompEmitAddress(false);
        request.setGenerateCallGraph(true);
        request.setGenerateControlFlowGraphs(true);
//        request.setGenerateArchive(true);

        // C request specific non-defaults.
        request.setArchitecture(Architecture.POWERPC);
        request.setFileFormat(FileFormat.ELF);
        request.setCompCompiler(CompCompiler.CLANG);
        request.setCompOptimizations(CompOptimizations.O0);
//        request.setCompDebug(false);
//        request.setCompStrip(true);

        DecompilationResponse resp = retdecService.decompile(request);
        FileSaveDecompilationResult res = new FileSaveDecompilationResult(outDir);
        Thread t = retdecService.decompileAsync(resp, res);
        t.join();
        if (null != res.getException()) {
            throw res.getException();
        }

    }

    /**
     * Invoke the decompilation on a previous decompilation identifier.
     * 
     * @param previousId
     *            the identifier of the previous decompilation.
     * @param outDir
     *            the output directory.
     * @throws Exception
     *             if an exception occurs.
     */
    private void invoke(final String previousId, final File outDir) throws Exception {
        FileSaveDecompilationResult res = new FileSaveDecompilationResult(outDir);
        Thread t = retdecService.decompileAsync(new DecompilationResponse(previousId), res);
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

        parser.printHelpOn(System.out);

        OptionSet options = parser.parse(args);
        RetdecClient rdc = new RetdecClient(options.valueOf(apikeyOption));
        if (options.has(idOption)) {
            rdc.invoke(options.valueOf(idOption), options.valueOf(outdirOption));
        } else {
            rdc.invoke(options.valueOf(inputOption), options.valueOf(outdirOption));
        }
        
        // NXOY3mDgal -- C compilation with non-options
        // XnRDb0Wj06 -- C compilation with full non-options
        // r8OWzekjb0 -- C compilation with call graphs.
    }
}
