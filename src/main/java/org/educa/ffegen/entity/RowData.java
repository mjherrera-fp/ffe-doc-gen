package org.educa.ffegen.entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RowData {
    private BooleanProperty seleccionado = new SimpleBooleanProperty();
    private StringProperty empresa = new SimpleStringProperty();
    private StringProperty nombre = new SimpleStringProperty();
    private StringProperty nif = new SimpleStringProperty();
    private ExcelData excelData;

    public RowData(String nombre, String nif, String empresa, ExcelData excelData) {
        this.nombre.set(nombre);
        this.empresa.set(empresa);
        this.nif.set(nif);
        this.excelData = excelData;
        this.seleccionado = new SimpleBooleanProperty(this.empresa != null && !this.empresa.getValue().isBlank());
    }

    public BooleanProperty seleccionadoProperty() {
        return seleccionado;
    }

    public boolean isSeleccionado() {
        return seleccionado.get();
    }

    public void setSeleccionado(boolean s) {
        seleccionado.set(s);
    }

    public StringProperty empresaProperty() {
        return empresa;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty nifProperty() {
        return nif;
    }

    public ExcelData getExcelData() {
        return excelData;
    }
}