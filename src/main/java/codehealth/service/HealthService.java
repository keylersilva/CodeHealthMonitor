package codehealth.service;

public class HealthService {

    public static String analizar(String codigo) {
        String cleanCode = codigo.toLowerCase().replaceAll("\\s+", "");


        if (cleanCode.contains("password=") || cleanCode.contains("clave=")
                || cleanCode.contains("while(true)") || cleanCode.contains("catch(exceptione){}")) {
            return "Crítico";
        }

        // 2. Detección de Deuda Técnica (Buenas prácticas POO)
        String[] lineas = codigo.split("\n");
        if (cleanCode.contains("system.out.print") || lineas.length > 50) {
            return "Advertencia";
        }

        return "Óptimo";
    }


    public static String generarObservacion(String estado, String codigo) {
        if ("Óptimo".equalsIgnoreCase(estado)) {
            return "✅ ¡Excelente trabajo!\nEl código cumple con los estándares Clean Code de la industria. Sin fugas de memoria ni vulnerabilidades expuestas.";
        }

        StringBuilder obs = new StringBuilder();
        String cleanCode = codigo.toLowerCase().replaceAll("\\s+", "");
        String[] lineas = codigo.split("\n");


        if (cleanCode.contains("password=") || cleanCode.contains("clave=")) {
            obs.append("🚫 VULNERABILIDAD HARDCODING:\n- Problema: Tienes contraseñas o datos sensibles quemados directamente en el código.\n- Solución: Usa variables de entorno (.env) o el application.properties para inyectarlas.\n\n");
        }
        if (cleanCode.contains("catch(exceptione){}")) {
            obs.append("🚫 EXCEPCIONES SILENCIADAS:\n- Problema: Un bloque Try-Catch está completamente vacío.\n- Solución: Si ocurre un error, nadie lo notará. Aplica como mínimo un Logger (logger.error()) o throw.\n\n");
        }
        if (cleanCode.contains("while(true)")) {
            obs.append("🚫 CICLO INFINITO POSIBLE:\n- Problema: Peligro alto de desbordar memoria (StackOverflow).\n- Solución: Inserta siempre una validación de salida 'break' en su interior.\n\n");
        }
        if (lineas.length > 50) {
            obs.append("⚠️ LONG METHOD DECTECTADO (LOC > 50):\n- Problema: Tienes ").append(lineas.length).append(" líneas. El principio Solid 'Responsabilidad Única' dice que un método muy grande es malo.\n- Solución: Sepáralo en 2 o más métodos (Refactorización modular).\n\n");
        }
        if (cleanCode.contains("system.out.println")) {
            obs.append("⚠️ RENDIMIENTO LIMITADO:\n- Problema: La impresión por consola básica genera latencia en el servidor en modo producción.\n- Solución: Implanta Slf4J o java.util.logging.\n");
        }

        if (obs.length() == 0) {
            obs.append("⚠️ ADVERTENCIA DETECTADA:\n- Estructura confusa o uso poco eficiente de memoria, revisa con atención los flujos y ciclos.");
        }

        return obs.toString().trim(); // Se lleva todo el resumen junto y formateado
    }
}