package org.educa.ffegen.entity;

import lombok.Data;

@Data
public class SignEntity {
    private String keystorePath;
    private char[] password;
}
