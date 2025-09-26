package org.educa.ffegen.generator;

import org.educa.ffegen.entity.EntryValue;
import org.educa.ffegen.entity.ExcelData;
import org.educa.ffegen.entity.TableRA;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface DocxGenerator {
    void generateForSeguimiento(InputStream templateStream,
                                Map<String, EntryValue> replacements,
                                TableRA tableRA, File outFile) throws Exception;

    void generateForRelacion(InputStream templateStream,
                             Map<String, EntryValue> replacements,
                             List<ExcelData> alumnos, File outFile) throws Exception;

    void generateForPlanFormativo(InputStream templateStream, Map<String, EntryValue> replacements, TableRA tableRA,
                                  File out) throws Exception;

    void generateForValoracionFinal(InputStream template, Map<String, EntryValue> replacements, TableRA tableRA, File out) throws Exception;
}
