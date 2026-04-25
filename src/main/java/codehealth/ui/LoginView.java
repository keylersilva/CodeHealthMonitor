package codehealth.ui;

import codehealth.security.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("login")
@PageTitle("Ingreso | CodeHealth")
public class LoginView extends VerticalLayout {

    public LoginView() {
        getElement().getThemeList().add(Lumo.DARK);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#0f172a");


        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.getStyle()
                .set("background", "rgba(30, 41, 59, 0.6)")
                .set("backdrop-filter", "blur(12px)")
                .set("padding", "40px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 25px 50px -12px rgba(0, 0, 0, 0.5)")
                .set("border", "1px solid #334155");
        card.setAlignItems(Alignment.CENTER);

        H1 titulo = new H1("CODEHEALTH");
        titulo.getStyle()
                .set("color", "#818cf8")
                .set("margin-top", "0")
                .set("letter-spacing", "3px")
                .set("font-size", "24px");

        Paragraph subtitulo = new Paragraph("Autenticación de Auditores");
        subtitulo.getStyle().set("color", "#94a3b8").set("margin-bottom", "30px");


        LoginForm loginForm = new LoginForm();
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Acceder");
        i18n.getForm().setUsername("Usuario");
        i18n.getForm().setPassword("Contraseña");
        i18n.getForm().setSubmit("ENTRAR AL SISTEMA");
        i18n.getErrorMessage().setTitle("Usuario/Contraseña incorrectos");
        i18n.getErrorMessage().setMessage("Verifica que tus datos sean correctos.");
        loginForm.setI18n(i18n);


        loginForm.addLoginListener(e -> {
            boolean exitoso = AuthService.autenticar(e.getUsername(), e.getPassword());
            if (exitoso) {

                UI.getCurrent().navigate(MainView.class);
            } else {
                loginForm.setError(true);
            }
        });


        Paragraph credencialesHint = new Paragraph("User: tu_nombre | Pass: admin123");
        credencialesHint.getStyle()
                .set("color", "#64748b")
                .set("font-size", "12px")
                .set("margin-top", "20px");

        card.add(titulo, subtitulo, loginForm, credencialesHint);
        add(card);
    }
}