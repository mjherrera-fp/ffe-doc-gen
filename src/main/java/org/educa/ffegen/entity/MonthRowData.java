package org.educa.ffegen.entity;

import lombok.Data;
import org.educa.ffegen.enums.DayTypeFFE;

public class MonthRowData {
    private String monthName;
    private DayTypeFFE[] days; // Array de 31 elementos
    private String[] dayLabels; // Array con las etiquetas de los días (Lu, Ma, etc.)

    public MonthRowData(String monthName) {
        this.monthName = monthName;
        this.days = new DayTypeFFE[31];
        this.dayLabels = new String[31];
    }

    public String getMonthName() {
        return monthName;
    }

    public void setDayType(int dayNumber, DayTypeFFE type) {
        if (dayNumber >= 1 && dayNumber <= 31) {
            days[dayNumber - 1] = type;
        }
    }

    public DayTypeFFE getDayType(int dayNumber) {
        if (dayNumber >= 1 && dayNumber <= 31) {
            return days[dayNumber - 1];
        }
        return null;
    }

    public void setDayLabel(int dayNumber, String label) {
        if (dayNumber >= 1 && dayNumber <= 31) {
            dayLabels[dayNumber - 1] = label;
        }
    }

    public String getDayLabel(int dayNumber) {
        if (dayNumber >= 1 && dayNumber <= 31) {
            return dayLabels[dayNumber - 1];
        }
        return "";
    }

    // Métodos getter para cada día (necesarios para JasperReports)
    public DayTypeFFE getDay1() { return days[0]; }
    public DayTypeFFE getDay2() { return days[1]; }
    public DayTypeFFE getDay3() { return days[2]; }
    public DayTypeFFE getDay4() { return days[3]; }
    public DayTypeFFE getDay5() { return days[4]; }
    public DayTypeFFE getDay6() { return days[5]; }
    public DayTypeFFE getDay7() { return days[6]; }
    public DayTypeFFE getDay8() { return days[7]; }
    public DayTypeFFE getDay9() { return days[8]; }
    public DayTypeFFE getDay10() { return days[9]; }
    public DayTypeFFE getDay11() { return days[10]; }
    public DayTypeFFE getDay12() { return days[11]; }
    public DayTypeFFE getDay13() { return days[12]; }
    public DayTypeFFE getDay14() { return days[13]; }
    public DayTypeFFE getDay15() { return days[14]; }
    public DayTypeFFE getDay16() { return days[15]; }
    public DayTypeFFE getDay17() { return days[16]; }
    public DayTypeFFE getDay18() { return days[17]; }
    public DayTypeFFE getDay19() { return days[18]; }
    public DayTypeFFE getDay20() { return days[19]; }
    public DayTypeFFE getDay21() { return days[20]; }
    public DayTypeFFE getDay22() { return days[21]; }
    public DayTypeFFE getDay23() { return days[22]; }
    public DayTypeFFE getDay24() { return days[23]; }
    public DayTypeFFE getDay25() { return days[24]; }
    public DayTypeFFE getDay26() { return days[25]; }
    public DayTypeFFE getDay27() { return days[26]; }
    public DayTypeFFE getDay28() { return days[27]; }
    public DayTypeFFE getDay29() { return days[28]; }
    public DayTypeFFE getDay30() { return days[29]; }
    public DayTypeFFE getDay31() { return days[30]; }

    // Métodos getter para las etiquetas de los días
    public String getLabel1() { return dayLabels[0]; }
    public String getLabel2() { return dayLabels[1]; }
    public String getLabel3() { return dayLabels[2]; }
    public String getLabel4() { return dayLabels[3]; }
    public String getLabel5() { return dayLabels[4]; }
    public String getLabel6() { return dayLabels[5]; }
    public String getLabel7() { return dayLabels[6]; }
    public String getLabel8() { return dayLabels[7]; }
    public String getLabel9() { return dayLabels[8]; }
    public String getLabel10() { return dayLabels[9]; }
    public String getLabel11() { return dayLabels[10]; }
    public String getLabel12() { return dayLabels[11]; }
    public String getLabel13() { return dayLabels[12]; }
    public String getLabel14() { return dayLabels[13]; }
    public String getLabel15() { return dayLabels[14]; }
    public String getLabel16() { return dayLabels[15]; }
    public String getLabel17() { return dayLabels[16]; }
    public String getLabel18() { return dayLabels[17]; }
    public String getLabel19() { return dayLabels[18]; }
    public String getLabel20() { return dayLabels[19]; }
    public String getLabel21() { return dayLabels[20]; }
    public String getLabel22() { return dayLabels[21]; }
    public String getLabel23() { return dayLabels[22]; }
    public String getLabel24() { return dayLabels[23]; }
    public String getLabel25() { return dayLabels[24]; }
    public String getLabel26() { return dayLabels[25]; }
    public String getLabel27() { return dayLabels[26]; }
    public String getLabel28() { return dayLabels[27]; }
    public String getLabel29() { return dayLabels[28]; }
    public String getLabel30() { return dayLabels[29]; }
    public String getLabel31() { return dayLabels[30]; }
}
