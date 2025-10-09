package org.educa.ffegen.helper;

import org.educa.ffegen.entity.DayFFE;
import org.educa.ffegen.entity.MonthFFE;
import org.educa.ffegen.entity.MonthRowData;
import org.educa.ffegen.enums.DayTypeFFE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FCTDataHelper {

    /**
     * Convierte una lista de MonthFFE en una lista de MonthRowData
     * preparada para el informe
     */
    public List<MonthRowData> prepareCalendarDataForReport(List<MonthFFE> months) {
        List<MonthRowData> rowDataList = new ArrayList<>();

        for (MonthFFE month : months) {
            MonthRowData rowData = new MonthRowData(month.getNombreMes());

            // Crear un mapa para acceso rápido por día del mes
            Map<Integer, DayFFE> dayMap = new HashMap<>();
            for (DayFFE day : month.getDias()) {
                int dayOfMonth = day.getFecha().getDayOfMonth();
                dayMap.put(dayOfMonth, day);
            }

            // Si hay días en el mes, obtener el año y mes para calcular etiquetas
            LocalDate referenceDate = null;
            if (!month.getDias().isEmpty()) {
                referenceDate = month.getDias().get(0).getFecha();
            }

            // Llenar los 31 días
            for (int i = 1; i <= 31; i++) {
                if (dayMap.containsKey(i)) {
                    DayFFE day = dayMap.get(i);
                    rowData.setDayType(i, day.getType());
                    rowData.setDayLabel(i, getDayLabel(day.getFecha()));
                } else if (referenceDate != null) {
                    // Para días que no existen en el mes (ej: 31 en febrero)
                    // o que no están en la lista
                    LocalDate testDate = LocalDate.of(
                            referenceDate.getYear(),
                            referenceDate.getMonth(),
                            1
                    );

                    // Verificar si ese día existe en ese mes
                    if (i <= testDate.lengthOfMonth()) {
                        testDate = testDate.withDayOfMonth(i);
                        DayOfWeek dayOfWeek = testDate.getDayOfWeek();

                        // Si es fin de semana y no está en la lista, marcarlo como fin de semana
                        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                            rowData.setDayType(i, DayTypeFFE.FIN_SEMANA);
                        }
                        rowData.setDayLabel(i, getDayLabel(testDate));
                    }
                }
            }

            rowDataList.add(rowData);
        }

        return rowDataList;
    }

    /**
     * Obtiene la etiqueta del día de la semana
     */
    private String getDayLabel(LocalDate date) {
        return switch (date.getDayOfWeek()) {
            case MONDAY -> "Lu";
            case TUESDAY -> "Ma";
            case WEDNESDAY -> "Mi";
            case THURSDAY -> "Ju";
            case FRIDAY -> "Vi";
            case SATURDAY -> "Sa";
            case SUNDAY -> "Do";
        };
    }
}