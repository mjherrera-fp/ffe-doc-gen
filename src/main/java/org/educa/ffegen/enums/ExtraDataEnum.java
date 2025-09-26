package org.educa.ffegen.enums;

public enum ExtraDataEnum {

    TUTOR_NOMBRE_COMPLETO( -1),
    FECHA_CURSO( -1),
    TUTOR_NOMBRE( 0),
    TUTOR_APELLIDOS( 1),
    TUTOR_NIF( 2),
    TUTOR_EMAIL( 3),
    TUTOR_TELEFONO( 4),
    CURSO( 5),
    CENTRO( 6),
    CENTRO_TELEFONO( 7),
    CENTRO_EMAIL( 8),
    CICLO( 9),
    CICLO_CODIGO( 10),
    NIVEL( 11),
    GRADO( 12),
    REGIMEN( 13),
    GRUPO_CODIGO( 14);

    private final int excelPosition;

    ExtraDataEnum(int excelPosition) {
        this.excelPosition = excelPosition;
    }

    public String getTag() {
        return "##" + name() + "##";
    }

    public int getExcelPosition() {
        return excelPosition;
    }
}
