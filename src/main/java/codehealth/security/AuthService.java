package codehealth.security;

import com.vaadin.flow.server.VaadinSession;

public class AuthService {

    private static final String USER_SESSION_ATTRIBUTE = "LOGGED_USER";

    public static boolean autenticar(String username, String password) {
        // AQUÍ PUEDES CONECTAR TU DATABASE.
        // Para pruebas, el acceso será: user: dev, password: admin123
        if (username != null && !username.trim().isEmpty() && "admin123".equals(password)) {
            // Guardamos al usuario en la sesión de Vaadin
            VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, username);
            return true;
        }
        return false;
    }

    public static String getUsuarioLogueado() {
        // Retorna el nombre de usuario, o null si nadie está logueado
        return (String) VaadinSession.getCurrent().getAttribute(USER_SESSION_ATTRIBUTE);
    }

    public static void cerrarSesion() {
        VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, null);
    }
}