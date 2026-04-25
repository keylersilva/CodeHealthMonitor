package codehealth.security;

import com.vaadin.flow.server.VaadinSession;

public class AuthService {

    private static final String USER_SESSION_ATTRIBUTE = "LOGGED_USER";

    public static boolean autenticar(String username, String password) {


        if (username != null && !username.trim().isEmpty() && "admin123".equals(password)) {

            VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, username);
            return true;
        }
        return false;
    }

    public static String getUsuarioLogueado() {

        return (String) VaadinSession.getCurrent().getAttribute(USER_SESSION_ATTRIBUTE);
    }

    public static void cerrarSesion() {
        VaadinSession.getCurrent().setAttribute(USER_SESSION_ATTRIBUTE, null);
    }
}