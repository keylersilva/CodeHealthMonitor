package codehealth.ui;

import codehealth.model.CodeReview;
import codehealth.repository.ReviewRepository;
import codehealth.security.AuthService;
import codehealth.service.HealthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@PageTitle("Panel Auditoría | CodeHealth")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private ReviewRepository repo = new ReviewRepository();
    private FlexLayout galeria = new FlexLayout();
    private String autorActual; // Quien tiene sesión iniciada

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // --- FILTRO DE SEGURIDAD ---
        autorActual = AuthService.getUsuarioLogueado();
        if (autorActual == null) {
            // Si nadie inició sesión, expulsarlo a la vista de login
            event.forwardTo(LoginView.class);
        }
    }

    public MainView() {
        getElement().getThemeList().add(Lumo.DARK);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#0f172a");

        // --- HEADER CON USERNAME Y LOGOUT ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN); // Para separar logo de la derecha
        header.getStyle()
                .set("background", "rgba(15, 23, 42, 0.8)")
                .set("backdrop-filter", "blur(10px)")
                .set("border-bottom", "1px solid #1e293b");

        // Logo
        HorizontalLayout logoWrapper = new HorizontalLayout();
        logoWrapper.setAlignItems(Alignment.CENTER);
        H2 logo = new H2("CODEHEALTH");
        logo.getStyle().set("color", "#818cf8").set("margin", "0").set("font-weight", "800");
        Span version = new Span("PRO 1.0");
        version.getStyle().set("color", "#64748b").set("font-size", "12px").set("background", "#1e293b").set("padding", "4px").set("border-radius", "4px");
        logoWrapper.add(logo, version);

        // Opciones de Perfil (Botón Logout)
        HorizontalLayout perfilInfo = new HorizontalLayout();
        perfilInfo.setAlignItems(Alignment.CENTER);

        // Muestra dinámicamente quién inició sesión
        Span lblUser = new Span("👨‍💻 " + AuthService.getUsuarioLogueado());
        lblUser.getStyle().set("color", "#cbd5e1").set("font-weight", "600");

        Button btnSalir = new Button("Cerrar Sesión", e -> {
            AuthService.cerrarSesion();
            UI.getCurrent().getPage().reload(); // Recarga la página y el filtro bloquea y echa al user.
        });
        btnSalir.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        perfilInfo.add(lblUser, btnSalir);

        header.add(logoWrapper, perfilInfo);
        add(header);

        HorizontalLayout body = new HorizontalLayout();
        body.setSizeFull();
        body.setSpacing(false);

        // --- FORMULARIO IZQUIERDO ---
        VerticalLayout formulario = new VerticalLayout();
        formulario.setWidth("450px");
        formulario.setHeightFull();
        formulario.getStyle().set("background", "#111827").set("border-right", "1px solid #1e293b");
        formulario.setPadding(true);

        H4 formTitle = new H4("Nueva Auditoría");
        formTitle.getStyle().set("color", "#f8fafc").set("margin-top", "10px");

        TextField txtTitulo = new TextField("Título del Proyecto");
        txtTitulo.setWidthFull();
        txtTitulo.setClearButtonVisible(true);

        TextArea txtCodigo = new TextArea("Snippet de Código");
        txtCodigo.setHeight("350px");
        txtCodigo.setWidthFull();
        txtCodigo.getStyle().set("font-family", "monospace");

        Button btnAnalizar = new Button("ANALIZAR CÓDIGO", e -> {
            if(!txtCodigo.getValue().isEmpty()) {
                String estado = HealthService.analizar(txtCodigo.getValue());
                String obs = HealthService.generarObservacion(estado);
                // Le pasamos dinámicamente el nombre de la sesión
                CodeReview nueva = new CodeReview(0, txtTitulo.getValue().isEmpty() ? "Sin Título" : txtTitulo.getValue(), autorActual, txtCodigo.getValue(), estado, obs);
                repo.guardar(nueva);
                actualizarGaleria();
                txtTitulo.clear();
                txtCodigo.clear();
            }
        });
        btnAnalizar.setWidthFull();
        btnAnalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAnalizar.getStyle().set("background", "linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)").set("height", "45px").set("margin-top", "15px");

        formulario.add(formTitle, txtTitulo, txtCodigo, btnAnalizar);

        // --- GALERÍA DERECHA ---
        VerticalLayout scrollContainer = new VerticalLayout();
        scrollContainer.setSizeFull();
        scrollContainer.getStyle().set("overflow-y", "auto").set("padding", "30px");

        H3 galeriaTitle = new H3("Historial de Análisis");
        galeriaTitle.getStyle().set("color", "#f1f5f9");

        galeria.setWidthFull();
        galeria.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        galeria.setJustifyContentMode(JustifyContentMode.START);
        galeria.getStyle().set("gap", "20px");

        actualizarGaleria();

        scrollContainer.add(galeriaTitle, galeria);
        body.add(formulario, scrollContainer);
        add(body);
    }

    private void actualizarGaleria() {
        galeria.removeAll();
        for (CodeReview cr : repo.findAll()) {
            VerticalLayout card = new VerticalLayout();
            card.setWidth("320px");
            card.getStyle()
                    .set("background", "rgba(30, 41, 59, 0.7)")
                    .set("border", "1px solid #334155")
                    .set("border-radius", "12px")
                    .set("padding", "20px");

            // Insignia de Estado
            String color = "Óptimo".equalsIgnoreCase(cr.getEstadoSalud()) ? "#4ade80" : "Advertencia".equalsIgnoreCase(cr.getEstadoSalud()) ? "#fbbf24" : "#f87171";
            Span badge = new Span(cr.getEstadoSalud().toUpperCase());
            badge.getStyle().set("color", color).set("background", color + "22").set("padding", "4px 10px").set("border-radius", "20px").set("font-size", "11px").set("font-weight", "bold");

            H4 tituloInfo = new H4(cr.getTitulo());
            tituloInfo.getStyle().set("color", "#f8fafc").set("margin", "10px 0 5px 0");

            // Quién lo creó (usando el campo Autor del model)
            Span pAutor = new Span("Por: " + cr.getAutor());
            pAutor.getStyle().set("color", "#475569").set("font-size", "11px");

            Paragraph observacion = new Paragraph(cr.getObservacion());
            observacion.getStyle().set("color", "#94a3b8").set("font-size", "14px").set("line-height", "1.5");

            card.add(badge, tituloInfo, pAutor, observacion);
            galeria.add(card);
        }
    }
}