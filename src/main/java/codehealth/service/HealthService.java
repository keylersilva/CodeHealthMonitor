package codehealth.service;

public class HealthService {
    public static String analizar(String codigo) {
        if (codigo.contains("while(true)") || codigo.contains("catch(Exception e) {}")) return "Crítico";
        if (codigo.contains("System.out.println")) return "Advertencia";
        return "Óptimo";
    }

    public static String generarObservacion(String estado) {
        return switch (estado) {
            case "Crítico" -> "Riesgo de bloqueo o pérdida de errores. ¡Corregir urgente!";
            case "Advertencia" -> "Código funcional pero con malas prácticas detectadas.";
            default -> "Código limpio y sigue estándares de POO.";
        };
    }
}