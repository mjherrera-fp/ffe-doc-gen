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
    EMPRESA_CIUDAD(12),
    EMPRESA_CIF(13),
    EMPRESA_EMAIL(14),
    EMPRESA_TELEFONO(15),
    NUMERO_CONVENIO(21),
    FECHA_CONVENIO(22),
    NUMERO_RELACION_ALUMNO(23),
    EMPRESA_TUTOR_NOMBRE(24),
    EMPRESA_TUTOR_APELLIDOS(25),
    EMPRESA_TUTOR_NIF(26),
    EMPRESA_TUTOR_EMAIL(27),
    EMPRESA_TUTOR_TELEFONO(28),
    FECHA_INI(30),
    TOTAL_HORAS(31),
    DIAS_SEMANA(39),
    HORAS_SEMANA(40),
    FECHA_FIN(42),
    HORA_INI(43),
    HORA_FIN(44),
    RESUMEN_HORARIO(45),
    COMENTARIOS(51);

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
