package org.educa.ffegen;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.educa.ffegen.entity.ExcelData;
import org.educa.ffegen.entity.ExtraData;
import org.educa.ffegen.entity.RAData;
import org.educa.ffegen.enums.ExcelDataEnum;
import org.educa.ffegen.enums.ExtraDataEnum;
import org.educa.ffegen.enums.RADataEnum;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExcelReader {

    public List<ExcelData> readDataFromExcel(String path) throws Exception {
        try (InputStream is = new FileInputStream(path);
             Workbook wb = new XSSFWorkbook(is)) {

            DataFormatter df = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = wb.getSheetAt(0);

            List<ExcelData> result = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                ExcelData excelData = new ExcelData();
                excelData.setAlumnadoApellidosNombre(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_APELLIDOS_NOMBRE.getExcelPosition()), evaluator));
                excelData.setAlumnadoNombre(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_NOMBRE.getExcelPosition())));
                excelData.setAlumnadoApellidos(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_APELLIDOS.getExcelPosition())));
                excelData.setAlumnadoNif(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_NIF.getExcelPosition())));
                excelData.setAlumnadoTelefono(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_TELEFONO.getExcelPosition())));
                excelData.setAlumnadoEmail(df.formatCellValue(row.getCell(ExcelDataEnum.ALUMNADO_EMAIL.getExcelPosition())));
                excelData.setAlumnadoFechaNacimiento(getDateValue(row.getCell(ExcelDataEnum.ALUMNADO_FECHA_NACIMIENTO.getExcelPosition()), df, evaluator));
                excelData.setEmpresa(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA.getExcelPosition())));
                excelData.setEmpresaDireccion(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_DIRECCION.getExcelPosition())));
                excelData.setEmpresaCif(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_CIF.getExcelPosition())));
                excelData.setEmpresaEmail(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_EMAIL.getExcelPosition())));
                excelData.setEmpresaTelefono(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TELEFONO.getExcelPosition())));
                excelData.setNumeroConvenio(df.formatCellValue(row.getCell(ExcelDataEnum.NUMERO_CONVENIO.getExcelPosition())));
                excelData.setFechaConvenio(getDateValue(row.getCell(ExcelDataEnum.FECHA_CONVENIO.getExcelPosition()), df, evaluator));
                excelData.setNumeroRelacionAlumno(df.formatCellValue(row.getCell(ExcelDataEnum.NUMERO_RELACION_ALUMNO.getExcelPosition())));
                excelData.setEmpresaTutorNombre(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TUTOR_NOMBRE.getExcelPosition())));
                excelData.setEmpresaTutorApellidos(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TUTOR_APELLIDOS.getExcelPosition())));
                excelData.setEmpresaTutorNif(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TUTOR_NIF.getExcelPosition())));
                excelData.setEmpresaTutorEmail(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TUTOR_EMAIL.getExcelPosition())));
                excelData.setEmpresaTutorTelefono(df.formatCellValue(row.getCell(ExcelDataEnum.EMPRESA_TUTOR_TELEFONO.getExcelPosition())));
                excelData.setFechaInicio(getDateValue(row.getCell(ExcelDataEnum.FECHA_INI.getExcelPosition()), df, evaluator));
                excelData.setTotalHoras(df.formatCellValue(row.getCell(ExcelDataEnum.TOTAL_HORAS.getExcelPosition())));
                excelData.setDiasSemana(df.formatCellValue(row.getCell(ExcelDataEnum.DIAS_SEMANA.getExcelPosition()), evaluator));
                excelData.setTotalHorasSem(df.formatCellValue(row.getCell(ExcelDataEnum.HORAS_SEMANA.getExcelPosition()), evaluator));
                excelData.setFechaFinVal(getDateValue(row.getCell(ExcelDataEnum.FECHA_FIN.getExcelPosition()), df, evaluator));
                excelData.setHoraInicio(df.formatCellValue(row.getCell(ExcelDataEnum.HORA_INI.getExcelPosition())));
                excelData.setHoraFin(df.formatCellValue(row.getCell(ExcelDataEnum.HORA_FIN.getExcelPosition())));
                excelData.setResumenHorario(df.formatCellValue(row.getCell(ExcelDataEnum.RESUMEN_HORARIO.getExcelPosition())));

                if (excelData.getAlumnadoNif() != null && !excelData.getAlumnadoNif().isBlank()) {
                    result.add(excelData);
                }
            }
            return result;
        }
    }

    public List<RAData> readRAFromExcel(String path) throws Exception {
        try (InputStream is = new FileInputStream(path);
             Workbook wb = new XSSFWorkbook(is)) {

            DataFormatter df = new DataFormatter();
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = wb.getSheetAt(2);

            List<RAData> result = new ArrayList<>();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                RAData raData = new RAData();
                raData.setModulo(df.formatCellValue(row.getCell(RADataEnum.MODULO.getExcelPosition()), evaluator));
                raData.setCodigo(df.formatCellValue(row.getCell(RADataEnum.CODIGO.getExcelPosition())));
                raData.setRa(df.formatCellValue(row.getCell(RADataEnum.RA.getExcelPosition())));
                raData.setCompleto(Boolean.valueOf(df.formatCellValue(row.getCell(RADataEnum.COMPLETO.getExcelPosition()))));

                if (raData.getModulo() != null && !raData.getModulo().isBlank()) {
                    result.add(raData);
                }
            }
            return result;
        }
    }

    public ExtraData readExtraDataFromExcel(String path) throws Exception {
        try (InputStream is = new FileInputStream(path);
             Workbook wb = new XSSFWorkbook(is)) {

            DataFormatter df = new DataFormatter();
            Sheet sheet = wb.getSheetAt(1);

            Row row = sheet.getRow(1);

            ExtraData extraData = new ExtraData();
            extraData.setNombreTutor(df.formatCellValue(row.getCell(ExtraDataEnum.TUTOR_NOMBRE.getExcelPosition())));
            extraData.setApellidosTutor(df.formatCellValue(row.getCell(ExtraDataEnum.TUTOR_APELLIDOS.getExcelPosition())));
            extraData.setNifTutor(df.formatCellValue(row.getCell(ExtraDataEnum.TUTOR_NIF.getExcelPosition())));
            extraData.setEmailTutor(df.formatCellValue(row.getCell(ExtraDataEnum.TUTOR_EMAIL.getExcelPosition())));
            extraData.setTelefonoTutor(df.formatCellValue(row.getCell(4)));
            extraData.setCurso(df.formatCellValue(row.getCell(ExtraDataEnum.CURSO.getExcelPosition())));
            extraData.setCentro(df.formatCellValue(row.getCell(ExtraDataEnum.CENTRO.getExcelPosition())));
            extraData.setTelefonoCentro(df.formatCellValue(row.getCell(ExtraDataEnum.CENTRO_TELEFONO.getExcelPosition())));
            extraData.setEmailCentro(df.formatCellValue(row.getCell(ExtraDataEnum.CENTRO_EMAIL.getExcelPosition())));
            extraData.setCiclo(df.formatCellValue(row.getCell(ExtraDataEnum.CICLO.getExcelPosition())));
            extraData.setCodigoCiclo(df.formatCellValue(row.getCell(ExtraDataEnum.CICLO_CODIGO.getExcelPosition())));
            extraData.setNivel(df.formatCellValue(row.getCell(ExtraDataEnum.NIVEL.getExcelPosition())));
            extraData.setGrado(df.formatCellValue(row.getCell(ExtraDataEnum.GRADO.getExcelPosition())));
            extraData.setRegimen(df.formatCellValue(row.getCell(ExtraDataEnum.REGIMEN.getExcelPosition())));
            extraData.setCodigoGrupo(df.formatCellValue(row.getCell(ExtraDataEnum.GRUPO_CODIGO.getExcelPosition())));

            return extraData;
        }
    }

    private String getDateValue(Cell cell, DataFormatter df, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        try {
            if (Objects.requireNonNull(cell.getCellType()) == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date fecha = cell.getDateCellValue();
                    return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
                } else {
                    // Si es número normal
                    return df.formatCellValue(cell, evaluator);
                }
            }// Para FORMULA, BLANK, BOOLEAN, etc. usamos el DataFormatter
            return df.formatCellValue(cell, evaluator);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
