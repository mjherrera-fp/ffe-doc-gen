package org.educa.ffegen.enums;

public enum ExcelDataEnum {

    PRE_TEXT(-1),
    FECHA_SEGUIMIENTO(-1),
    EMPRESA_TUTOR_NOMBRE_COMPLETO(-1),
    ALUMNADO_APELLIDOS_NOMBRE(0),
    ALUMNADO_NOMBRE(1),
    ALUMNADO_APELLIDOS(2),
    ALUMNADO_NIF(4),
    ALUMNADO_TELEFONO(6),
    ALUMNADO_EMAIL(7),
    ALUMNADO_FECHA_NACIMIENTO(9),
    EMPRESA(10),
    EMPRESA_DIRECCION(11),
    EMPRESA_CIF(12),
    EMPRESA_EMAIL(13),
    EMPRESA_TELEFONO(14),
    NUMERO_CONVENIO(20),
    FECHA_CONVENIO(21),
    NUMERO_RELACION_ALUMNO(22),
    EMPRESA_TUTOR_NOMBRE(23),
    EMPRESA_TUTOR_APELLIDOS(24),
    EMPRESA_TUTOR_NIF(25),
    EMPRESA_TUTOR_EMAIL(26),
    EMPRESA_TUTOR_TELEFONO(27),
    FECHA_INI(29),
    TOTAL_HORAS(30),
    DIAS_SEMANA(38),
    HORAS_SEMANA(39),
    FECHA_FIN(41),
    HORA_INI(42),
    HORA_FIN(43),
    RESUMEN_HORARIO(44),
    COMENTARIOS(50);

    private final int excelPosition;

    ExcelDataEnum(int excelPosition) {
        this.excelPosition = excelPosition;
    }

    public String getTag() {
        return "##" + name() + "##";
    }

    public int getExcelPosition() {
        return excelPosition;
    }
}
