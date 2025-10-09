package org.educa.ffegen.enums;

public enum RADataEnum {

    MODULO( 0),
    CODIGO( 1),
    RA( 2),
    NOMBRE_RA( 3),
    CONTENIDOS( 4),
    COMPLETO( 5);

    private final int excelPosition;

    RADataEnum(int excelPosition) {
        this.excelPosition = excelPosition;
    }

    public String getTag() {
        return "##" + name() + "##";
    }

    public int getExcelPosition() {
        return excelPosition;
    }
}
