package org.educa.ffegen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthFFE {
    private String nombreMes;
    private List<DayFFE> dias;
}
