package org.educa.ffegen.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.educa.ffegen.enums.DayTypeFFE;

import java.time.LocalDate;

// Clase que representa un día
@Data
@AllArgsConstructor
public class DayFFE {
    private LocalDate fecha;
    private DayTypeFFE type;
}
