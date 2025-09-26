package org.educa.ffegen.helper;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import javax.xml.namespace.QName;

public class CheckboxHelper {

    private final XWPFDocument document;

    public CheckboxHelper(XWPFDocument document) {
        this.document = document;
    }

    /**
     * Inserta un checkbox interactivo en un párrafo.
     *
     * @param paragraph Párrafo donde se insertará
     * @param checked   Si el checkbox estará marcado inicialmente
     * @param label     Texto opcional que aparecerá después del checkbox
     */
    public void createCheckbox(XWPFParagraph paragraph, boolean checked, String label) {
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraph.setVerticalAlignment(TextAlignment.CENTER);

        // Run 1: BEGIN
        XWPFRun runBegin = paragraph.createRun();
        CTFldChar fldCharBegin = runBegin.getCTR().addNewFldChar();
        fldCharBegin.setFldCharType(STFldCharType.BEGIN);

        CTFFData ffData = fldCharBegin.addNewFfData();
        CTFFCheckBox cb = ffData.addNewCheckBox();
        cb.addNewSizeAuto(); // Tamaño automático
        cb.addNewDefault().setVal(checked ? STOnOff1.ON : STOnOff1.OFF);

        // Run 2: INSTRTEXT -> "FORMCHECKBOX"
        XWPFRun runInstr = paragraph.createRun();
        runInstr.getCTR().addNewInstrText().setStringValue("FORMCHECKBOX");

        // Run 3: SEPARATE
        XWPFRun runSep = paragraph.createRun();
        CTFldChar fldCharSep = runSep.getCTR().addNewFldChar();
        fldCharSep.setFldCharType(STFldCharType.SEPARATE);

        // Run 4: END
        XWPFRun runEnd = paragraph.createRun();
        CTFldChar fldCharEnd = runEnd.getCTR().addNewFldChar();
        fldCharEnd.setFldCharType(STFldCharType.END);

        // Texto de la etiqueta
        if (label != null && !label.isEmpty()) {
            XWPFRun runLabel = paragraph.createRun();
            runLabel.setText(" " + label);
        }
    }

    /**
     * Busca un checkbox SDT dentro del párrafo y lo marca/desmarca.
     * Si no encuentra un SDT w14:checkbox, escribe un símbolo (fallback).
     */
    public void activateCheckbox(XWPFParagraph paragraph, boolean checked) {
        CTP ctp = paragraph.getCTP();
        for (CTSdtRun sdtRun : ctp.getSdtList()) {
            if (W14Checkbox.isW14Checkbox(sdtRun)) {
                new W14Checkbox(sdtRun).setChecked(checked);
                return;
            }
        }
        // fallback: si no hay SDT, insertar símbolo simple
        replaceParagraphWithSymbol(paragraph, checked);
    }

    /**
     * Versión conveniente: busca el SDT checkbox en los párrafos que ya existen en la celda.
     * Devuelve true si encontró y actualizó el checkbox real; false si hizo fallback (insert simbólico).
     */
    public boolean activateCheckboxInCell(XWPFTableCell cell, boolean checked) {
        for (XWPFParagraph p : cell.getParagraphs()) {
            CTP ctp = p.getCTP();
            for (CTSdtRun sdtRun : ctp.getSdtList()) {
                if (W14Checkbox.isW14Checkbox(sdtRun)) {
                    new W14Checkbox(sdtRun).setChecked(checked);
                    return true;
                }
            }
        }
        // No encontramos SDT: añadimos un párrafo con símbolo
        XWPFParagraph p = cell.addParagraph();
        replaceParagraphWithSymbol(p, checked);
        return false;
    }

    private void replaceParagraphWithSymbol(XWPFParagraph paragraph, boolean checked) {
        // limpiar runs previos
        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        XWPFRun run = paragraph.createRun();
        run.setText(checked ? "\u2612" : "\u2610", 0); // ☒ / ☐
        run.setFontFamily("MS Gothic"); // o "Segoe UI Symbol" según plantilla
    }

    /**
     * Helper que opera directamente sobre CTSdtRun con w14:checkbox
     */
    static class W14Checkbox {
        private final CTSdtRun sdtRun;
        private final CTSdtContentRun sdtContentRun;
        private final XmlObject w14CheckboxChecked; // el nodo w14:checked
        private static final String W14_NS = "http://schemas.microsoft.com/office/word/2010/wordml";


        W14Checkbox(CTSdtRun sdtRun) {
            this.sdtRun = sdtRun;
            this.sdtContentRun = sdtRun.getSdtContent();
            String declare = "declare namespace w14='http://schemas.microsoft.com/office/word/2010/wordml'";
            XmlObject[] sel = sdtRun.getSdtPr().selectPath(declare + ".//w14:checkbox/w14:checked");
            this.w14CheckboxChecked = (sel != null && sel.length > 0) ? sel[0] : null;
        }

        static boolean isW14Checkbox(CTSdtRun sdtRun) {
            String declare = "declare namespace w14='http://schemas.microsoft.com/office/word/2010/wordml'";
            XmlObject[] sel = sdtRun.getSdtPr().selectPath(declare + ".//w14:checkbox");
            return sel != null && sel.length > 0;
        }

        boolean getChecked() {
            if (w14CheckboxChecked == null) return false;
            XmlCursor cursor = w14CheckboxChecked.newCursor();
            try {
                String val = cursor.getAttributeText(new QName(W14_NS, "val", "w14"));
                return "1".equals(val) || "true".equalsIgnoreCase(val);
            } finally {
                cursor.dispose();
            }
        }

        void setChecked(boolean checked) {
            if (w14CheckboxChecked != null) {
                XmlCursor cursor = w14CheckboxChecked.newCursor();
                try {
                    cursor.setAttributeText(new QName(W14_NS, "val", "w14"), checked ? "1" : "0");
                } finally {
                    cursor.dispose();
                }
            }
            // además actualizamos el contenido visual dentro de sdtContent (primer run/text)
            setContentSymbol(checked);
        }

        private void setContentSymbol(boolean checked) {
            // asegurarnos que existe al menos un run y un text
            if (sdtContentRun.sizeOfRArray() == 0) {
                sdtContentRun.addNewR();
            }
            CTR firstR = sdtContentRun.getRArray(0);
            if (firstR.sizeOfTArray() == 0) {
                firstR.addNewT();
            }
            CTText t = firstR.getTArray(0);
            t.setStringValue(checked ? "\u2612" : "\u2610");
        }
    }
}