package org.educa.ffegen.generator;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.educa.ffegen.entity.EntryValue;
import org.educa.ffegen.entity.ExcelData;
import org.educa.ffegen.entity.RAData;
import org.educa.ffegen.entity.TableRA;
import org.educa.ffegen.enums.ExcelDataEnum;
import org.educa.ffegen.helper.CheckboxHelper;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocxPoiGenerator implements DocxGenerator {

    private static final String COLOR_BLACK = "000000";
    private static final int MAX_RA = 20;

    @Override
    public void generateForSeguimiento(InputStream templateStream,
                                       Map<String, EntryValue> replacements,
                                       TableRA tableRA, File outFile) throws Exception {

        try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(templateStream))) {

            // Reemplazar en párrafos
            replaceData(replacements, doc);

            if (tableRA != null) {
                XWPFTable table = doc.getTables().get(tableRA.getNumTableInDoc());
                for (int i = 0; i < tableRA.getExcelRA().size(); i++) {
                    RAData raData = tableRA.getExcelRA().get(i);
                    XWPFTableRow row = table.getRows().get(i + 4);

                    XWPFTableCell cellCodModProf = row.getTableCells().get(1);
                    setCellText(cellCodModProf, raData.getCodigo(), 9, ParagraphAlignment.CENTER);

                    XWPFTableCell cellRA = row.getTableCells().get(2);
                    setCellText(cellRA, raData.getRa(), 9, ParagraphAlignment.CENTER);
                }
                if (tableRA.getExcelRA().size() < MAX_RA) {
                    for (int i = tableRA.getExcelRA().size(); i < MAX_RA; i++) {
                        int pos = tableRA.getExcelRA().size() + 4;
                        table.removeRow(pos);

                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.write(fos);
            }
        }
    }

    @Override
    public void generateForRelacion(InputStream templateStream,
                                    Map<String, EntryValue> replacements,
                                    List<ExcelData> alumnos, File outFile) throws Exception {

        try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(templateStream))) {

            // Reemplazar en párrafos
            replaceData(replacements, doc);

            if (alumnos != null) {
                XWPFTable table = doc.getTables().get(6);
                for (int i = 0; i < alumnos.size(); i++) {
                    ExcelData excelData = alumnos.get(i);
                    // crear nuevas celdas para la tabla con la información por alumno

                    // Número real de columnas = columnas de la última fila
                    int lastRowIndex = table.getNumberOfRows() - 1;
                    XWPFTableRow lastRow = table.getRow(lastRowIndex);
                    int realCols = lastRow.getTableCells().size();
                    // Ahora creamos filas nuevas con esas columnas
                    XWPFTableRow newRow = table.createRow();
                    while (newRow.getTableCells().size() < realCols) {
                        newRow.addNewTableCell();
                    }
                    // Rellenar las celdas
                    setCellText(newRow.getCell(0), String.valueOf(i + 1), 8, ParagraphAlignment.START);
                    setCellText(newRow.getCell(1), excelData.getAlumnadoApellidosNombre(), 8, ParagraphAlignment.START);
                    setCellText(newRow.getCell(2), excelData.getAlumnadoNif(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(3), excelData.getAlumnadoFechaNacimiento(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(4), excelData.getFechaInicio(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(5), excelData.getFechaFinVal(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(6), excelData.getDiasSemana(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(7), excelData.getHoraInicio(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(8), excelData.getHoraFin(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(9), excelData.getTotalHorasSem(), 8, ParagraphAlignment.END);
                    setCellText(newRow.getCell(10), excelData.getTotalHoras(), 8, ParagraphAlignment.END);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                doc.write(fos);
            }
        }
    }

    @Override
    public void generateForPlanFormativo(InputStream template, Map<String, EntryValue> replacements, TableRA tableRA,
                                         File out) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(template))) {
            CheckboxHelper checkboxHelper = new CheckboxHelper(doc);

            // Reemplazar
            replaceData(replacements, doc);

            if (tableRA != null) {
                XWPFTable table = doc.getTables().get(tableRA.getNumTableInDoc());
                for (int i = 0; i < tableRA.getExcelRA().size(); i++) {
                    RAData raData = tableRA.getExcelRA().get(i);
                    XWPFTableRow row = table.getRow(i + 1);
                    setCellText(row.getCell(0), raData.getModulo(), 9, ParagraphAlignment.START);
                    setCellText(row.getCell(1), raData.getCodigo(), 9, ParagraphAlignment.CENTER);
                    setCellText(row.getCell(2), raData.getRa(), 9, ParagraphAlignment.CENTER);
                    checkboxHelper.activateCheckboxInCell(row.getCell(3), raData.getCompleto());
                    checkboxHelper.activateCheckboxInCell(row.getCell(4), !raData.getCompleto());

                }
                if (tableRA.getExcelRA().size() < MAX_RA) {
                    for (int i = tableRA.getExcelRA().size(); i < MAX_RA; i++) {
                        int pos = tableRA.getExcelRA().size() + 1;
                        table.removeRow(pos);

                    }
                }
            }

            XWPFTableCell cell = doc.getTables().get(0).getRow(18).getCell(0);

            XWPFParagraph p = cell.addParagraph();

            // Ahora creo el cursor justo después de ese párrafo
            // Crear la tabla dentro de la celda
            XmlCursor cursor = p.getCTP().newCursor();
            XWPFTable innerTable = cell.insertNewTbl(cursor);
            innerTable.setCellMargins(5, 5, 5, 5);
            // Bordes básicos
            innerTable.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, COLOR_BLACK); // negro
            innerTable.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, COLOR_BLACK);
            innerTable.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, COLOR_BLACK);
            innerTable.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, COLOR_BLACK);
            innerTable.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, COLOR_BLACK);
            innerTable.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 2, 0, COLOR_BLACK);

            // Ancho de la tabla (en twips: 1 cm = 567 twips)
            //innerTable.setWidth("5000"); // ≈ 8.8 cm
            setTableWidth(innerTable, 9550);
            cursor.dispose();

            p.setAlignment(ParagraphAlignment.CENTER);
            innerTable.setCellMargins(
                    100,  // top
                    100,  // left
                    100,  // bottom
                    100   // right
            );

            // Asegurarse de que exista al menos una fila
            XWPFTableRow rowFirst = innerTable.createRow();  // crea la primera fila con una celda vacía

            // Ahora aseguramos la primera celda
            XWPFTableCell firstCell = rowFirst.getCell(0);
            if (firstCell == null) {
                firstCell = rowFirst.createCell();
            }
            setCellText(firstCell, "Nº periodo", 9, ParagraphAlignment.START);

            // Creamos la segunda celda
            XWPFTableCell secondCell = rowFirst.addNewTableCell();
            setCellText(secondCell, "Calendario", 9, ParagraphAlignment.START);

            // Creamos la tercera celda
            XWPFTableCell thirdCell = rowFirst.addNewTableCell();
            setCellText(thirdCell, "Horario", 9, ParagraphAlignment.START);


            // Asegurarse de que exista al menos una fila
            XWPFTableRow rowSecond = innerTable.createRow();  // crea la segunda fila con una celda vacía

            // Ahora aseguramos la primera celda
            XWPFTableCell firstCell2 = rowSecond.getCell(0);
            setCellText(firstCell2, "1", 9, ParagraphAlignment.START);

            // Creamos la segunda celda
            XWPFTableCell secondCell2 = rowSecond.getCell(1);
            setCellText(secondCell2, "Del " + replacements.get(ExcelDataEnum.FECHA_INI.getTag()).getValue()
                    + "al " + replacements.get(ExcelDataEnum.FECHA_FIN.getTag()).getValue(), 9, ParagraphAlignment.START);

            // Creamos la tercera celda
            XWPFTableCell thirdCell2 = rowSecond.getCell(2);
            setCellText(thirdCell2, replacements.get(ExcelDataEnum.RESUMEN_HORARIO.getTag()).getValue(), 9, ParagraphAlignment.START);


            try (FileOutputStream fos = new FileOutputStream(out)) {
                doc.write(fos);
            }
        }
    }

    @Override
    public void generateForValoracionFinal(InputStream template, Map<String, EntryValue> replacements, TableRA tableRA, File out) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(template))) {
            CheckboxHelper checkboxHelper = new CheckboxHelper(doc);

            // Reemplazar
            replaceData(replacements, doc);

            if (tableRA != null) {
                XWPFTable table = doc.getTables().get(tableRA.getNumTableInDoc());
                System.out.println("Tabla: " + table.getRow(0).getCell(0).getText());
                for (int i = 0; i < tableRA.getExcelRA().size(); i++) {
                    RAData raData = tableRA.getExcelRA().get(i);
                    XWPFTableRow row = table.getRow(i + 2);
                    setCellText(row.getCell(0), raData.getCodigo(), 9, ParagraphAlignment.CENTER);
                    setCellText(row.getCell(1), raData.getRa(), 9, ParagraphAlignment.CENTER);
                }
                if (tableRA.getExcelRA().size() < MAX_RA) {
                    for (int i = tableRA.getExcelRA().size(); i < MAX_RA; i++) {
                        int pos = tableRA.getExcelRA().size() + 2;
                        table.removeRow(pos);
                    }
                }
            }
            try (FileOutputStream fos = new FileOutputStream(out)) {
                doc.write(fos);
            }
        }
    }

    private void setCellText(XWPFTableCell cell, String text, int fontSize, ParagraphAlignment alignment) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        if (alignment != null) {
            p.setAlignment(alignment);
        }
        p.setVerticalAlignment(TextAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setFontSize(fontSize);
        r.setText(text != null ? text : "");
    }


    /**
     * Reemplaza los placeholders establecidos en replacement en el documento doc, aplicando el estilo
     *
     * @param replacements {@link Map} con los replacements
     * @param doc          {@link XWPFDocument} plantilla
     */
    private void replaceData(Map<String, EntryValue> replacements, XWPFDocument doc) {

        for (XWPFParagraph p : doc.getParagraphs()) {
            replaceInParagraph(replacements, p);
        }

        // Procesar tablas
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        replaceInParagraph(replacements, p);
                    }
                }
            }
        }

    }

    private void replaceInParagraph(Map<String, EntryValue> replacements, XWPFParagraph paragraph) {

        StringBuilder fullText = new StringBuilder();
        List<XWPFRun> textRuns = new ArrayList<>();

        // 1. Construir el texto completo solo de runs de texto normal
        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getCTR().getTList().size() > 0) { // solo runs con texto
                String text = run.getText(0);
                if (text != null) {
                    fullText.append(text);
                    textRuns.add(run);
                }
            }
        }

        String newText = fullText.toString();

        // 2. Aplicar los reemplazos
        for (var entry : replacements.entrySet()) {
            if (newText.contains(entry.getKey())) {
                newText = newText.replace(entry.getKey(), entry.getValue().getValue());

                // 3. Reescribir solo los runs de texto plano
                // limpiar los runs de texto plano
                for (XWPFRun run : textRuns) {
                    paragraph.removeRun(paragraph.getRuns().indexOf(run));
                }
                // nuevo run con el texto reemplazado
                XWPFRun newRun = paragraph.createRun();
                newRun.setText(newText);
                newRun.setFontSize(entry.getValue().getStyle().getSize());
                newRun.setBold(entry.getValue().getStyle().isBold());
                if (entry.getValue().getStyle().getParagraphAlignment() != null) {
                    paragraph.setAlignment(entry.getValue().getStyle().getParagraphAlignment());
                }
                paragraph.setVerticalAlignment(TextAlignment.CENTER);
            }
        }

    }

    private void setTableWidth(XWPFTable table, int widthTwips) {
        // 1. Obtener las propiedades de la tabla
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        // 2. Obtener o crear el nodo de ancho
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();

        // 3. Establecer ancho
        tblWidth.setW(BigInteger.valueOf(widthTwips)); // ancho en twips (1 cm ≈ 567 twips)
        tblWidth.setType(STTblWidth.DXA);               // unidad DXA = twips


        // 4. centrar
        CTJcTable jc = tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc();
        jc.setVal(STJcTable.CENTER);
    }

}
