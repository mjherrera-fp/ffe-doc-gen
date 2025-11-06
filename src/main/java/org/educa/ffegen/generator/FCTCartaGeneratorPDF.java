package org.educa.ffegen.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.educa.ffegen.entity.ExtraData;
import org.educa.ffegen.entity.RAData;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FCTCartaGeneratorPDF {

    public void generateCarta(ExtraData extraData, List<RAData> ras, File out) throws Exception {
        String html = generateHTML(extraData, ras);

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(out));
             PdfDocument pdfDoc = new PdfDocument(writer)) {
            // Establecer el tamaño de página en landscape (A4 horizontal)
            pdfDoc.setDefaultPageSize(PageSize.A4);
            ConverterProperties props = new ConverterProperties();
            props.setBaseUri("src/main/resources/");

            // Convertir HTML a PDF usando iText
            HtmlConverter.convertToPdf(html, pdfDoc, props);

        }
    }

    private String generateHTML(ExtraData extraData, List<RAData> ras) {
        StringBuilder html = new StringBuilder();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                Locale.of("es", "ES"));


        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"es\">\n");
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Carta de inicio a la empresa</title>\n");
        html.append("    <style>\n");
        html.append("        @page { margin-top: 10pt; margin-left: 40pt; margin-right: 40pt; margin-bottom: 40pt; }\n");
        html.append("        body { font-family: Arial, sans-serif; font-size: 15px; line-height: 1.4; color: #333; }\n");
        html.append("        h1 { text-align: center; margin-top: 20px; }\n");
        html.append("        p { text-align: justify; }\n");
        html.append("        table.cabecera { width: 100%; border-collapse: collapse; margin-bottom: 30px; }\n");
        html.append("        table.cabecera td { vertical-align: middle; text-align: center; width: 33%; }\n");
        html.append("        table.cabecera img { height: 80px; object-fit: contain; }\n");
        html.append("        .content { text-align: justify; text-justify: inter-word; }\n");
        html.append("        .img-h { height: 80px; }\n");
        html.append("        .img-w { width: 180px; }\n");
        html.append("        .fecha { text-align: right; font-style: italic; margin-bottom: 20px; }\n");
        html.append("        ul, ol { margin-left: 20px; }\n");
        html.append("        .firma { margin-top: 50px; float: right;}\n");
        html.append("        .both { clear: both;}\n");
        html.append("        .margin-top { margin-top: 30pt;}\n");
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

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

        html.append("    <div class=\"fecha\">").append(extraData.getCiudad()).append(", ").append(formatter.format(today)).append("</div>\n");

        html.append("    <p>Estimadas empresas,</p>\n");
        html.append("    <p>Antes de nada, agradecer su colaboración, la cual es imprescindible para la formación profesional de nuestros alumnos.</p>\n");
        html.append("    <p>A continuación, le detallo algunos datos importantes, que me gustaría que usted conociera:</p>\n");

        html.append("    <div class=\"content\"><ol>\n");
        html.append("        <li>En la <strong>Relación de alumnos</strong> usted puede ver la siguiente información: el incumplimiento puede ser motivo de rescisión del Convenio General.</li>\n");
        html.append("        <ul>\n");
        html.append("             <li>El día de inicio y fin de las FFE (Fase de Formación en la Empresa).</li>\n");
        html.append("             <li>Los días de la semana que debe acudir a la empresa.</li>\n");
        html.append("             <li>El horario que debe cumplir el alumno.</li>\n");
        html.append("        </ul>\n");
        html.append("        <li>En el <strong>Anexo Programa Formativo</strong> usted puede ver los resultados de aprendizaje que el alumno debe poner en práctica durante las FFE.</li>\n");
        html.append("        <li>En el <strong>Calendario de FFE</strong> usted podrá ver los días que el alumno debe acudir a su empresa y los días festivos con los que cuenta el alumno (según calendario escolar).</li>\n");
        html.append("        <li>En el <strong>Anexo Ficha de seguimiento periódico</strong>, se deberá ir anotando las actividades que van desarrollando en la empresa y, el día que finalice las FFE, deben presentar la ficha firmada por el alumno, así como firmadas y selladas por el Tutor de la Empresa. Si el alumno no tiene certificado digital, deberá firmar primero el alumno usando raíces y después el tutor. El documento tiene informado los RA y los módulos, podéis ver la correlación entre los módulos y los códigos de los módulos en el plan formativo. Simplemente sería rellenar la actividad o actividades realizadas para completar ese RA y si está o no superado.</li>\n");
        html.append("        <li>El <strong>Anexo Informe de valoración final</strong> se entregará cumplimentado, firmado y sellado al alumno antes de finalizar sus FCT. Este informe de valoración recogerá la evaluación del alumno y se plantean tres grandes áreas de observación: valoración del desempeño, valoración de competencias transversales (actitud o habilidades sociales) y cualquier otra observación que desee realizar. Luego, por cada módulo que tenga un resultado de aprendizaje, deberá indicar si está superado o no.</li>\n");
        html.append("        <li>Anexo a este documento usted tiene información relativa a los Resultados de Aprendizaje que se deben impartir en la empresa. Tenga en cuenta que será necesario rellenar las actividades formativas en la ficha de seguimiento periódico.</li>\n");
        html.append("        <li>En caso de accidente laboral se adjunta el protocolo a seguir en caso de accidente de trabajo. Adicionalmente se adjunta el protocolo de actuación en caso de reclamación patrimonial.</li>\n");
        html.append("    </ol></div>\n");

        html.append("    <p>Muchas gracias por su colaboración y no dude en realizar cualquier tipo de consulta si así lo considera.</p>\n");
        html.append("    <p>Le saluda atentamente el tutor de FFE,</p>\n");
        html.append("    <div class=\"firma\">\n");
        html.append("        <p><strong>").append(extraData.getNombreTutor()).append(" ").append(extraData.getApellidosTutor()).append("</strong><br>\n");
        html.append("        <a href=\"mailto:").append(extraData.getEmailTutor()).append("\">").append(extraData.getEmailTutor()).append("</a></p>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"both\">\n");
        html.append("        <h3 class=\"margin-top\">A continuación se muestra información sobre los RAs</h3>\n");
        for (RAData raData : ras) {
            html.append("        <p><strong>").append(raData.getModulo()).append(": ").append(raData.getRa()).append(" - ").append(raData.getNombreRA()).append("</strong></p>\n");
            String contenidosHtml = raData.getContenidos().replace("\n", "<br/>");
            html.append("        <p>").append(contenidosHtml).append("</p>\n");
        }
        html.append("    </div>\n");

        html.append("</body>\n");
        html.append("</html>");


        return html.toString();
    }


}
