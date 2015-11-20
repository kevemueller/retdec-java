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
 * Class encapsulating information for a raw decompilation request.
 * 
 * @see <a href=
 *      "https://retdec.com/api/docs/decompiler.html#parameters-only-for-the-raw-mode">
 *      API</a>
 */
public final class RawDecompilationRequest extends AbstractDecompilationRequest {
    /**
     * Endianness of the machine code.
     */
    public enum Endianness {
        /** Little endian, e.g. x86. */
        LITTLE, /** Big endian, e.g. PPC */
        BIG;
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };
    }

    /**
     * The decompilation mode.
     */
    private static final String MODE = "raw";

    /**
     * The architecture.
     */
    private Architecture architecture;
    /**
     * The file format.
     */
    private FileFormat fileFormat;
    /**
     * The endianness of the file.
     */
    private Endianness rawEndian;
    /**
     * The entry point.
     */
    private Long rawEntryPoint;
    /**
     * The virtual address of the raw file.
     */
    private Long rawSectionVma;

    /**
     * Construct a raw decompilation request.
     * 
     * @param input
     *            the input file.
     * @param architecture
     *            the architecture.
     * @param fileFormat
     *            the file format.
     * @param rawEntryPoint
     *            the entry point.
     * @param rawSectionVMA
     *            the virtuall address.
     */
    public RawDecompilationRequest(final File input, final Architecture architecture, final FileFormat fileFormat,
            final Long rawEntryPoint, final Long rawSectionVMA) {
        super(MODE, input);
        this.architecture = architecture;
        this.fileFormat = fileFormat;
        this.rawEntryPoint = rawEntryPoint;
        this.rawSectionVma = rawSectionVMA;
    }

    /**
     * Construct a raw decompilation request.
     * 
     * @param input
     *            the input file.
     * @param architecture
     *            the architecture.
     * @param fileFormat
     *            the file format.
     */
    public RawDecompilationRequest(final File input, final Architecture architecture, final FileFormat fileFormat) {
        this(input, architecture, fileFormat, null, null);
    }

    @Override
    public Map<String, Object> getFormData() {
        Map<String, Object> map = super.getFormData();
        map.put("architecture", architecture);
        map.put("file_format", fileFormat);
        map.put("raw_endian", rawEndian);
        map.put("raw_entry_point", rawEntryPoint);
        map.put("raw_section_vma", rawSectionVma);
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

    public Endianness getRawEndian() {
        return rawEndian;
    }

    public void setRawEndian(final Endianness rawEndian) {
        this.rawEndian = rawEndian;
    }

    public Long getRawEntryPoint() {
        return rawEntryPoint;
    }

    public void setRawEntryPoint(final Long rawEntryPoint) {
        this.rawEntryPoint = rawEntryPoint;
    }

    public Long getRawSectionVma() {
        return rawSectionVma;
    }

    public void setRawSectionVma(final Long rawSectionVma) {
        this.rawSectionVma = rawSectionVma;
    }
}