package vn.edu.ktpm.minishop.shop;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ket qua kiem tra hop le thong tin giao hang (FR-04).
 */
public class ValidationResult {

    private final Map<String, String> errors = new LinkedHashMap<>();

    void addError(String field, String message) {
        errors.put(field, message);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public boolean hasError(String field) {
        return errors.containsKey(field);
    }

    public String errorOf(String field) {
        return errors.get(field);
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return isValid() ? "VALID" : errors.toString();
    }
}
