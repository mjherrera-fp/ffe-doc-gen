package org.educa.ffegen.enums;

public enum ExtraDataEnum {

    TUTOR_NOMBRE_COMPLETO(-1),
    FECHA_CURSO(-1),
    TUTOR_NOMBRE(0),
    TUTOR_APELLIDOS(1),
    TUTOR_NIF(2),
    TUTOR_EMAIL(3),
    TUTOR_TELEFONO(4),
    CURSO(5),
    CENTRO(6),
    CENTRO_CIUDAD(7),
    CENTRO_TELEFONO(8),
    CENTRO_EMAIL(9),
    CICLO(10),
    CICLO_CODIGO(11),
    NIVEL(12),
    GRADO(13),
    REGIMEN(14),
    GRUPO_CODIGO(15);

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
