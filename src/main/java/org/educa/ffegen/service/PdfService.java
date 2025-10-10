package org.educa.ffegen.service;

import org.educa.ffegen.entity.*;
import org.educa.ffegen.enums.DayTypeFFE;
import org.educa.ffegen.generator.FCTCalendarGeneratorPDF;
import org.educa.ffegen.generator.FCTCartaGeneratorPDF;
import org.educa.ffegen.helper.SanitizerHelper;

import java.io.File;
import java.nio.file.FileSystems;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static org.educa.ffegen.helper.Constantes.EXTENSION_PDF;
import static org.educa.ffegen.helper.Constantes.WELCOME_PACK;

public class PdfService {

    FCTCalendarGeneratorPDF fctCalendarGeneratorPDF = new FCTCalendarGeneratorPDF();
    FCTCartaGeneratorPDF fctCartaGeneratorPDF = new FCTCartaGeneratorPDF();

    public void generateCalendar(File folder, List<RowData> seleccionados, ExtraData extraData, Map<String, List<LocalDate>> holidays,
                                 List<LocalDate> tutorias) throws Exception {
        for (RowData row : seleccionados) {
            ExcelData data = row.getExcelData();
            if (data.getFechaInicio() != null && !data.getFechaInicio().isEmpty() &&
                    data.getFechaFinVal() != null && !data.getFechaFinVal().isEmpty()) {
                LocalDate start = LocalDate.parse(data.getFechaInicio(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate end = LocalDate.parse(data.getFechaFinVal(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                String apellidosNombre = data.getAlumnadoApellidosNombre().replace(",", "")
                        .replace(" ", "_");
                String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                        + SanitizerHelper.sanitize(data.getEmpresa()) + FileSystems.getDefault().getSeparator()
                        + WELCOME_PACK;
                File newFolder = new File(folderPath);
                newFolder.mkdirs();
                File out = new File(newFolder, "Calendario_" + apellidosNombre + EXTENSION_PDF);

                fctCalendarGeneratorPDF.generateCalendar(generateMonthFFE(start, end,
                        holidays.get(data.getEmpresaCiudad()), tutorias), extraData.getCurso(), out);
            }
        }
    }

    public void generateCarta(File folder, Map<String, List<ExcelData>> groupByEmp, ExtraData extraData, List<RAData> excelRA)
            throws Exception {
        for (String empresa : groupByEmp.keySet()) {

            String folderPath = folder.getAbsolutePath() + FileSystems.getDefault().getSeparator()
                    + SanitizerHelper.sanitize(empresa) + FileSystems.getDefault().getSeparator()
                    + WELCOME_PACK;
            File newFolder = new File(folderPath);
            newFolder.mkdirs();
            File out = new File(newFolder, "Carta inicio a la empresa" + EXTENSION_PDF);
            fctCartaGeneratorPDF.generateCarta(extraData, excelRA, out);

        }
    }

    /**
     * Método de ejemplo para generar datos de prueba
     */
    private List<MonthFFE> generateMonthFFE(LocalDate start, LocalDate end, List<LocalDate> holidays, List<LocalDate> tutorias) {
        Map<Month, List<DayFFE>> monthMap = new LinkedHashMap<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek dow = date.getDayOfWeek();
            DayTypeFFE type;

            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                type = DayTypeFFE.FIN_SEMANA;
            } else if (holidays != null && holidays.contains(date)) {
                type = DayTypeFFE.FESTIVO;
            } else if (tutorias != null && tutorias.contains(date)) {
                type = DayTypeFFE.TUTORIA;
            } else if (date.equals(start) || date.equals(end)) {
                type = DayTypeFFE.INICIO_FIN;
            } else {
                type = DayTypeFFE.LABORAL;
            }

            monthMap.computeIfAbsent(date.getMonth(), k -> new ArrayList<>())
                    .add(new DayFFE(date, type));
        }

        List<MonthFFE> months = new ArrayList<>();
        for (Map.Entry<Month, List<DayFFE>> entry : monthMap.entrySet()) {
            String monthName = entry.getKey().getDisplayName(TextStyle.FULL, Locale.of("es", "ES"));
            months.add(new MonthFFE(capitalize(monthName), entry.getValue()));
        }

        return months;
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
