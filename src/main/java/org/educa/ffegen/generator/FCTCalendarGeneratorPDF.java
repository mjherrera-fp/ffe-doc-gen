package org.educa.ffegen.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.educa.ffegen.entity.MonthFFE;
import org.educa.ffegen.entity.MonthRowData;
import org.educa.ffegen.enums.DayTypeFFE;
import org.educa.ffegen.helper.FCTDataHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class FCTCalendarGeneratorPDF {
    public void generateCalendar(List<MonthFFE> months, String curso, File outputPath) throws Exception {
        String html = generateHTML(months, curso);

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(outputPath));
             PdfDocument pdfDoc = new PdfDocument(writer)) {

            // Establecer el tamaño de página en landscape (A4 horizontal)
            pdfDoc.setDefaultPageSize(PageSize.A4.rotate());
            ConverterProperties props = new ConverterProperties();
            props.setBaseUri("src/main/resources/");

            // Convertir HTML a PDF usando iText
            HtmlConverter.convertToPdf(html, pdfDoc, props);

        }
    }

    private String generateHTML(List<MonthFFE> months, String curso) {
        StringBuilder html = new StringBuilder();
        FCTDataHelper fctDataHelper = new FCTDataHelper();
        List<MonthRowData> monthsRowData = fctDataHelper.prepareCalendarDataForReport(months);
        // HTML Header
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'/>\n");
        html.append("<style>\n");
        html.append(getCSS());
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        // Cabecera con logotipos distribuida con tabla (compatible con iText)
        html.append("    <table class=\"cabecera\">\n");
        html.append("        <tr>\n");
        html.append("            <td style=\"text-align:left;\">\n");
        html.append("                <img class=\"img-h\" src=\"imagenes/logo_cm.svg\" alt=\"Logo Comunidad de Madrid\">\n");
        html.append("            </td>\n");
        html.append("            <td style=\"text-align:center;\">\n");
        html.append("                <img class=\"img-h\" src=\"imagenes/logo_ue.png\" alt=\"Logo de la Unión Europea\">\n");
        html.append("            </td>\n");
        html.append("            <td style=\"text-align:right;\">\n");
        html.append("                <img class=\"img-w\" src=\"imagenes/logo_fe.png\" alt=\"Logo Fondos Europeos\">\n");
        html.append("            </td>\n");
        html.append("        </tr>\n");
        html.append("    </table>\n");

        // Título
        html.append("<h1>Calendario FFE. Curso ").append(curso).append("</h1>\n");

        // Tabla
        html.append("<table class=\"calendar\">\n");

        // Encabezado con números de días
        html.append("<thead>\n<tr>\n");
        html.append("<th></th>\n"); // Columna para nombre del mes
        for (int i = 1; i <= 31; i++) {
            html.append("<th>").append(i).append("</th>\n");
        }
        html.append("</tr>\n</thead>\n");

        // Cuerpo de la tabla con los meses
        html.append("<tbody>\n");
        for (MonthRowData rowData : monthsRowData) {
            html.append("<tr>\n");

            // Nombre del mes
            html.append("<td class='month-cell'>").append(rowData.getMonthName()).append("</td>\n");

            // Días del mes
            for (int day = 1; day <= 31; day++) {
                DayTypeFFE dayType = rowData.getDayType(day);
                String label = rowData.getDayLabel(day);

                if (label != null && !label.isEmpty()) {
                    String cssClass = getCSSClassForDayType(dayType);
                    html.append("<td class='").append(cssClass).append("'>")
                            .append(label)
                            .append("</td>\n");
                } else {
                    html.append("<td class='empty-cell'></td>\n");
                }
            }

            html.append("</tr>\n");
        }
        html.append("</tbody>\n");
        html.append("</table>\n");

        // Leyenda
        html.append(getLegendHTML());

        html.append("</body>\n</html>");

        return html.toString();
    }

    private static String getCSS() {
        return """
                @page { 
                    margin-top: 10pt; 
                    margin-left: 40pt; 
                    margin-right: 40pt; 
                    margin-bottom: 40pt; 
                }
                                
                table.cabecera { 
                    width: 100%; 
                    border-collapse: collapse; 
                    margin-bottom: 30px; 
                }
                                
                table.cabecera td { 
                    vertical-align: middle; 
                    text-align: center; 
                }
                    
                table.cabecera img { 
                    height: 80px; 
                    object-fit: contain; 
                }
                                
                .img-h { 
                    height: 80px; 
                }
                                
                .img-w { 
                    width: 180px; 
                }
                            
                body {
                    font-family: Arial, sans-serif;
                    margin: 20px;
                }
                            
                h1 {
                    text-align: center;
                    color: #333;
                    margin-bottom: 30px;
                }
                            
                table.calendar {
                    border-collapse: collapse;
                    width: 100%;
                    margin-bottom: 30px;
                }
                            
                table.calendar th, table.calendar td {
                    border: 1px solid #000;
                    padding: 5px;
                    text-align: center;
                    font-size: 10px;
                }
                            
                table.calendar th {
                    background-color: #5DADE2;
                    font-weight: bold;
                    color: white;
                    height: 25px;
                }
                            
                .month-cell {
                    background-color: #f0f0f0;
                    font-weight: bold;
                    width: 80px;
                    font-size: 12px;
                }
                            
                .laboral-cell {
                    background-color: #D4EDDA;
                }
                            
                .festivo-cell {
                    background-color: #F8D7DA;
                }
                            
                .tutoria-cell {
                    background-color: #CCE5FF;
                }
                            
                .fin-semana-cell {
                    background-color: #FFD699;
                }
                            
                .inicio-fin-cell {
                    background-color: #FFFF99;
                }
                            
                .empty-cell {
                    background-color: #FFFFFF;
                }
                            
                .legend {
                    margin-top: 30px;
                }
                            
                .legend h3 {
                    margin-bottom: 15px;
                }
                            
                .legend-item {
                    display: flex;
                    align-items: center;
                    margin-bottom: 8px;
                }
                            
                .legend-color {
                    width: 30px;
                    height: 15px;
                    border: 1px solid #000;
                    margin-right: 10px;
                }
                            
                .legend-text {
                    font-size: 11px;
                }
                """;
    }

    private static String getCSSClassForDayType(DayTypeFFE dayType) {
        if (dayType == null) {
            return "empty-cell";
        }

        return switch (dayType) {
            case LABORAL -> "laboral-cell";
            case FESTIVO -> "festivo-cell";
            case TUTORIA -> "tutoria-cell";
            case FIN_SEMANA -> "fin-semana-cell";
            case INICIO_FIN -> "inicio-fin-cell";
            default -> "empty-cell";
        };
    }

    private static String getLegendHTML() {
        return """
                <div class='legend'>
                    <h3>Leyenda:</h3>
                    <div class='legend-item'>
                        <div class='legend-color' style='background-color: #FFFF99;'></div>
                        <span class='legend-text'>Fecha de inicio y Fin</span>
                    </div>
                    <div class='legend-item'>
                        <div class='legend-color' style='background-color: #D4EDDA;'></div>
                        <span class='legend-text'>Días de asistencia a las FCT</span>
                    </div>
                    <div class='legend-item'>
                        <div class='legend-color' style='background-color: #F8D7DA;'></div>
                        <span class='legend-text'>Días festivos</span>
                    </div>
                    <div class='legend-item'>
                        <div class='legend-color' style='background-color: #CCE5FF;'></div>
                        <span class='legend-text'>Días de asistencia al centro para tutorías</span>
                    </div>
                    <div class='legend-item'>
                        <div class='legend-color' style='background-color: #FFD699;'></div>
                        <span class='legend-text'>Fines de semana</span>
                    </div>
                </div>
                """;
    }
}
