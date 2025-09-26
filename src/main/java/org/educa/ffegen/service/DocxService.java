package org.educa.ffegen.service;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.educa.ffegen.entity.*;
import org.educa.ffegen.enums.ExcelDataEnum;
import org.educa.ffegen.enums.ExtraDataEnum;
import org.educa.ffegen.generator.DocxGenerator;
import org.educa.ffegen.generator.DocxPoiGenerator;
import org.educa.ffegen.helper.SanitizerHelper;

import java.io.File;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class DocxService {

    private static final String EXTENSION_DOCX = ".docx";
    DocxGenerator docxPoiGenerator = new DocxPoiGenerator();

    public void generateRelacion(File folder, List<RowData> seleccionados, ExtraData extraData) throws Exception {
        Map<String, List<ExcelData>> groupByEmp = new HashMap<>();
        for (RowData row : seleccionados) {
            ExcelData data = row.getExcelData();
            if (data.getEmpresa() != null && !data.getEmpresa().isEmpty()) {
                if (groupByEmp.containsKey(data.getEmpresa())) {
                    groupByEmp.get(data.getEmpresa()).add(data);
                } else {
                    List<ExcelData> dataForEmp = new ArrayList<>();
                    dataForEmp.add(data);
                    groupByEmp.put(data.getEmpresa(), dataForEmp);
                }
            }
        }

        for (List<ExcelData> row : groupByEmp.values()) {
            ExcelData data = row.getFirst();
            //Campos comunes para todos los alumnos que van a la misma empresa
            var replacements = Map.ofEntries(
                    Map.entry(ExcelDataEnum.PRE_TEXT.getTag(), new EntryValue("Relación de alumnos⁽³⁾ acogidos al Convenio o Acuerdo identificado en este documento y suscrito con fecha "
                            + data.getFechaConvenio() + " por el centro docente " + extraData.getCentro() +
                            " y la empresa u organismo equiparado " + data.getEmpresa() +
                            " que desarrollarán la fase de formación en empresa u organismo equiparado en el período y centro de trabajo indicados en este anexo.", false, ParagraphAlignment.BOTH)),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NOMBRE.getTag(), new EntryValue(data.getEmpresaTutorNombre())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_APELLIDOS.getTag(), new EntryValue(data.getEmpresaTutorApellidos())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NIF.getTag(), new EntryValue(data.getEmpresaTutorNif())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_EMAIL.getTag(), new EntryValue(data.getEmpresaTutorEmail())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_TELEFONO.getTag(), new EntryValue(data.getEmpresaTutorTelefono())),
                    Map.entry(ExcelDataEnum.EMPRESA_DIRECCION.getTag(), new EntryValue(data.getEmpresaDireccion())),
                    Map.entry(ExcelDataEnum.NUMERO_CONVENIO.getTag(), new EntryValue(data.getNumeroConvenio(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.NUMERO_RELACION_ALUMNO.getTag(), new EntryValue(data.getNumeroRelacionAlumno(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExtraDataEnum.TUTOR_APELLIDOS.getTag(), new EntryValue(extraData.getApellidosTutor())),
                    Map.entry(ExtraDataEnum.TUTOR_NOMBRE.getTag(), new EntryValue(extraData.getNombreTutor())),
                    Map.entry(ExtraDataEnum.TUTOR_NIF.getTag(), new EntryValue(extraData.getNifTutor())),
                    Map.entry(ExtraDataEnum.TUTOR_EMAIL.getTag(), new EntryValue(extraData.getEmailTutor())),
                    Map.entry(ExtraDataEnum.CURSO.getTag(), new EntryValue(extraData.getCurso(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExtraDataEnum.CICLO.getTag(), new EntryValue(extraData.getCiclo())),
                    Map.entry(ExtraDataEnum.NIVEL.getTag(), new EntryValue(extraData.getNivel())),
                    Map.entry(ExtraDataEnum.CICLO_CODIGO.getTag(), new EntryValue(extraData.getCodigoCiclo())),
                    Map.entry(ExtraDataEnum.GRADO.getTag(), new EntryValue(extraData.getGrado())),
                    Map.entry(ExtraDataEnum.REGIMEN.getTag(), new EntryValue(extraData.getRegimen()))

            );

            var template = getClass().getResourceAsStream("/templates/relacion_alumnos.docx");
            String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(data.getEmpresa());
            File newFolder = new File(folderPath);
            newFolder.mkdirs();
            File out = new File(newFolder, "RELA_"
                    + data.getNumeroRelacionAlumno() + EXTENSION_DOCX);

            docxPoiGenerator.generateForRelacion(template, replacements, row, out);
        }
    }

    public void generateSeguimiento(File folder, List<RowData> seleccionados, List<RAData> excelRA, ExtraData extraData) throws Exception {

        for (RowData row : seleccionados) {
            ExcelData data = row.getExcelData();
            var replacements = Map.ofEntries(
                    Map.entry(ExcelDataEnum.ALUMNADO_APELLIDOS.getTag(), new EntryValue(data.getAlumnadoApellidos())),
                    Map.entry(ExcelDataEnum.ALUMNADO_NOMBRE.getTag(), new EntryValue(data.getAlumnadoNombre())),
                    Map.entry(ExcelDataEnum.ALUMNADO_EMAIL.getTag(), new EntryValue(data.getAlumnadoEmail())),
                    Map.entry(ExcelDataEnum.NUMERO_CONVENIO.getTag(), new EntryValue(data.getNumeroConvenio(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.NUMERO_RELACION_ALUMNO.getTag(), new EntryValue(data.getNumeroRelacionAlumno(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.EMPRESA.getTag(), new EntryValue(data.getEmpresa())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_APELLIDOS.getTag(), new EntryValue(data.getEmpresaTutorApellidos())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NOMBRE.getTag(), new EntryValue(data.getEmpresaTutorNombre())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_EMAIL.getTag(), new EntryValue(data.getEmpresaTutorEmail())),
                    Map.entry(ExcelDataEnum.FECHA_SEGUIMIENTO.getTag(), new EntryValue("De " + data.getFechaInicio() + " a " + data.getFechaFinVal())),
                    Map.entry(ExtraDataEnum.TUTOR_NOMBRE.getTag(), new EntryValue(extraData.getNombreTutor() + " " + extraData.getApellidosTutor())),
                    Map.entry(ExtraDataEnum.CURSO.getTag(), new EntryValue(extraData.getCurso(), false, ParagraphAlignment.CENTER))
            );
            var template = getClass().getResourceAsStream("/templates/ficha_de_seguimiento_periodico.docx");
            String apellidosNombre = data.getAlumnadoApellidosNombre().replace(",", "")
                    .replace(" ", "_");
            String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(data.getEmpresa()) + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(apellidosNombre);
            File newFolder = new File(folderPath);
            newFolder.mkdirs();
            File out = new File(newFolder, "SEGP_" + apellidosNombre + EXTENSION_DOCX);

            TableRA tableRA = new TableRA(excelRA, 3);
            docxPoiGenerator.generateForSeguimiento(template, replacements, tableRA, out);
        }
    }

    public void generatePlanFormativo(File folder, List<RowData> seleccionados, List<RAData> excelRA, ExtraData extraData) throws Exception {

        for (RowData row : seleccionados) {
            ExcelData data = row.getExcelData();
            var replacements = Map.ofEntries(
                    Map.entry(ExtraDataEnum.FECHA_CURSO.getTag(), new EntryValue("Fecha "
                            + new SimpleDateFormat("dd/MM/yyyy").format(Date.from(Instant.now())) +
                            " / Curso " + extraData.getCurso(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.FECHA_INI.getTag(), new EntryValue(data.getFechaInicio())),
                    Map.entry(ExcelDataEnum.FECHA_FIN.getTag(), new EntryValue(data.getFechaFinVal())),
                    Map.entry(ExcelDataEnum.ALUMNADO_APELLIDOS_NOMBRE.getTag(), new EntryValue(data.getAlumnadoApellidosNombre(), true)),
                    Map.entry(ExcelDataEnum.ALUMNADO_EMAIL.getTag(), new EntryValue(data.getAlumnadoEmail())),
                    Map.entry(ExcelDataEnum.ALUMNADO_TELEFONO.getTag(), new EntryValue(data.getAlumnadoTelefono())),
                    Map.entry(ExcelDataEnum.TOTAL_HORAS.getTag(), new EntryValue(data.getTotalHoras())),
                    Map.entry(ExcelDataEnum.RESUMEN_HORARIO.getTag(), new EntryValue(data.getResumenHorario())),
                    Map.entry(ExcelDataEnum.EMPRESA.getTag(), new EntryValue(data.getEmpresa(), true)),
                    Map.entry(ExcelDataEnum.EMPRESA_CIF.getTag(), new EntryValue(data.getEmpresaCif())),
                    Map.entry(ExcelDataEnum.EMPRESA_EMAIL.getTag(), new EntryValue(data.getEmpresaEmail())),
                    Map.entry(ExcelDataEnum.EMPRESA_TELEFONO.getTag(), new EntryValue(data.getEmpresaTelefono())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NOMBRE_COMPLETO.getTag(), new EntryValue(data.getEmpresaTutorNombre() + " " + data.getEmpresaTutorApellidos())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NIF.getTag(), new EntryValue(data.getEmpresaTutorNif())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_EMAIL.getTag(), new EntryValue(data.getEmpresaTutorEmail())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_TELEFONO.getTag(), new EntryValue(data.getEmpresaTutorTelefono())),
                    Map.entry(ExtraDataEnum.TUTOR_NOMBRE_COMPLETO.getTag(), new EntryValue(extraData.getNombreTutor() + " " + extraData.getApellidosTutor())),
                    Map.entry(ExtraDataEnum.TUTOR_TELEFONO.getTag(), new EntryValue(extraData.getTelefonoTutor())),
                    Map.entry(ExtraDataEnum.TUTOR_EMAIL.getTag(), new EntryValue(extraData.getEmailTutor())),
                    Map.entry(ExtraDataEnum.CICLO.getTag(), new EntryValue(extraData.getCiclo())),
                    Map.entry(ExtraDataEnum.NIVEL.getTag(), new EntryValue(extraData.getNivel())),
                    Map.entry(ExtraDataEnum.CICLO_CODIGO.getTag(), new EntryValue(extraData.getCodigoCiclo())),
                    Map.entry(ExtraDataEnum.REGIMEN.getTag(), new EntryValue(extraData.getRegimen(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExtraDataEnum.CENTRO.getTag(), new EntryValue(extraData.getCentro(), true)),
                    Map.entry(ExtraDataEnum.CENTRO_EMAIL.getTag(), new EntryValue(extraData.getEmailCentro())),
                    Map.entry(ExtraDataEnum.CENTRO_TELEFONO.getTag(), new EntryValue(extraData.getTelefonoCentro()))

            );
            var template = getClass().getResourceAsStream("/templates/plan_formacion.docx");
            String apellidosNombre = data.getAlumnadoApellidosNombre().replace(",", "")
                    .replace(" ", "_");
            String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(data.getEmpresa()) + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(apellidosNombre);
            File newFolder = new File(folderPath);
            newFolder.mkdirs();
            File out = new File(newFolder, "PLFO_" + apellidosNombre + EXTENSION_DOCX);

            //TableRA tableRA = new TableRA(excelRA, 1);//Nº periodo
            TableRA tableRA = new TableRA(excelRA, 1);
            docxPoiGenerator.generateForPlanFormativo(template, replacements, tableRA, out);
        }
    }

    public void generateValoracionFinal(File folder, List<RowData> seleccionados, List<RAData> excelRA, ExtraData extraData) throws Exception {

        for (RowData row : seleccionados) {
            ExcelData data = row.getExcelData();
            var replacements = Map.ofEntries(
                    Map.entry(ExtraDataEnum.CURSO.getTag(), new EntryValue(extraData.getCurso(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.NUMERO_CONVENIO.getTag(), new EntryValue(data.getNumeroConvenio(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.NUMERO_RELACION_ALUMNO.getTag(), new EntryValue(data.getNumeroRelacionAlumno(), false, ParagraphAlignment.CENTER)),
                    Map.entry(ExcelDataEnum.ALUMNADO_APELLIDOS.getTag(), new EntryValue(data.getAlumnadoApellidos())),
                    Map.entry(ExcelDataEnum.ALUMNADO_NOMBRE.getTag(), new EntryValue(data.getAlumnadoNombre())),
                    Map.entry(ExcelDataEnum.ALUMNADO_EMAIL.getTag(), new EntryValue(data.getAlumnadoEmail())),
                    Map.entry(ExcelDataEnum.EMPRESA.getTag(), new EntryValue(data.getEmpresa(), true)),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_APELLIDOS.getTag(), new EntryValue(data.getEmpresaTutorApellidos())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_NOMBRE.getTag(), new EntryValue(data.getEmpresaTutorNombre())),
                    Map.entry(ExcelDataEnum.EMPRESA_TUTOR_EMAIL.getTag(), new EntryValue(data.getEmpresaTutorEmail())),
                    Map.entry(ExtraDataEnum.CICLO.getTag(), new EntryValue(extraData.getCiclo())),
                    Map.entry(ExtraDataEnum.NIVEL.getTag(), new EntryValue(extraData.getNivel())),
                    Map.entry(ExtraDataEnum.CICLO_CODIGO.getTag(), new EntryValue(extraData.getCodigoCiclo())),
                    Map.entry(ExtraDataEnum.GRADO.getTag(), new EntryValue(extraData.getGrado())),
                    Map.entry(ExcelDataEnum.TOTAL_HORAS.getTag(), new EntryValue(data.getTotalHoras()))
            );
            var template = getClass().getResourceAsStream("/templates/valoracion_final_del_tutor_de_la_empresa.docx");
            String apellidosNombre = data.getAlumnadoApellidosNombre().replace(",", "")
                    .replace(" ", "_");
            String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(data.getEmpresa()) + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(apellidosNombre);
            File newFolder = new File(folderPath);
            newFolder.mkdirs();
            File out = new File(newFolder, "VALO_" + apellidosNombre + EXTENSION_DOCX);

            TableRA tableRA = new TableRA(excelRA, 6);
            docxPoiGenerator.generateForValoracionFinal(template, replacements, tableRA, out);
        }
    }
}
