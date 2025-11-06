package org.educa.ffegen.config;

import lombok.Data;

import java.io.*;
import java.util.Properties;

@Data
public class AppConfig {
    private static final String CONFIG_FILE = "config.properties";
    public static final String DATA_FILE = "file.data";
    public static final String OUTPUT_PATH = "path.output";
    private final Properties props = new Properties();

    public AppConfig() {
        load();
    }

    public void load() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la configuración: " + e.getMessage());
        }
    }

    public void save() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "Configuración de la aplicación");
        } catch (IOException e) {
            System.err.println("No se pudo guardar la configuración: " + e.getMessage());
        }
    }

    public String get(String key) {
        return props.getProperty(key, null);
    }

    public void set(String key, String value) {
        props.setProperty(key, value);
    }

}
