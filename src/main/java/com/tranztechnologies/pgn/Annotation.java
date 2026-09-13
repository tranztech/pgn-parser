package com.tranztechnologies.pgn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class Annotation {
    @JsonProperty("NAME")
    private String NAME;
    @JsonProperty("SYMBOL")
    private String SYMBOL;
    @JsonProperty("CODE")
    private String CODE;
    private static Map<String, Annotation> annotations;
    private static Map<String, Annotation> annotationMap;

    public Annotation() { }
    public String getName() { return NAME; }
    public String getSymbol() { return SYMBOL; }
    public String getCode() { return CODE; }
    public void setNAME(String name) { this.NAME = name; }
    public void setSYMBOL(String symbol) { this.SYMBOL = symbol; }
    public void setCODE(String code) { this.CODE = code; }

    public static Annotation getAnnotation(String code) {
        return getAnnotations().get(code);
    }

    private static Map<String, Annotation> getAnnotations() {
        if (annotations == null) {
            try (InputStream input = Annotation.class.getResourceAsStream("/annotations.json")) {
                if (input == null) throw new IOException("Missing resource /annotations.json");
                annotations = new ObjectMapper().readValue(input, new TypeReference<Map<String, Annotation>>() { });
            } catch (IOException exception) {
                throw new UncheckedIOException("Unable to read annotations.json", exception);
            }
        }
        return annotations;
    }

    public static Map<String, Annotation> getAnnotationMap() {
        if (annotationMap == null) {
            annotationMap = new HashMap<>();
            for (Annotation annotation : getAnnotations().values()) {
                if (!annotation.getSymbol().isEmpty()) annotationMap.put(annotation.getSymbol(), annotation);
            }
        }
        return annotationMap;
    }
}
