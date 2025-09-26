package org.educa.ffegen.entity;


import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

@Data
public class StyleData {
    private int size;
    private ParagraphAlignment paragraphAlignment;
    private boolean bold;

    public StyleData() {
        this.size = 9;
        this.paragraphAlignment = ParagraphAlignment.LEFT;
        this.bold = false;
    }

    public StyleData(boolean bold, ParagraphAlignment paragraphAlignment) {
        this.size = 9;
        this.paragraphAlignment = paragraphAlignment;
        this.bold = bold;
    }
}
