package org.educa.ffegen.entity;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class EntryValue {
    String value;
    StyleData style;

    public EntryValue(String value) {
        this.value = value;
        this.style = new StyleData();
    }

    public EntryValue(String value, boolean bold) {
        this.value = value;
        this.style = new StyleData(bold, ParagraphAlignment.LEFT);
    }

    public EntryValue(String value, boolean bold, ParagraphAlignment paragraphAlignment) {
        this.value = value;
        this.style = new StyleData(bold, paragraphAlignment);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StyleData getStyle() {
        return style;
    }

    public void setStyle(StyleData style) {
        this.style = style;
    }
}
