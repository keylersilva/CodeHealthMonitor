package codehealth.ui;

import codehealth.model.CodeReview;
import codehealth.repository.ReviewRepository;
import codehealth.service.HealthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
public class MainView extends VerticalLayout {
    private ReviewRepository repo = new ReviewRepository();
    private FlexLayout galeria = new FlexLayout();

    public MainView() {
        // 1. ACTIVAR EL TEMA OSCURO NATIVO DE VAADIN (Esto arregla las cajas de texto mágicamente)
        getElement().getThemeList().add(Lumo.DARK);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#0f172a"); // Fondo General Deep Night
        getStyle().set("font-family", "'Inter', 'Segoe UI', Roboto, sans-serif");

        // --- HEADER ELEGANTE ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.getStyle()
                .set("background", "rgba(15, 23, 42, 0.8)")
                .set("backdrop-filter", "blur(10px)") // Efecto cristal moderno
                .set("border-bottom", "1px solid #1e293b");

        H2 logo = new H2("CODEHEALTH");
        logo.getStyle()
                .set("color", "#818cf8")
                .set("margin", "0")
                .set("font-weight", "800")
                .set("letter-spacing", "2px");

        Span version = new Span("MONITOR 1.0");
        version.getStyle()
                .set("color", "#64748b")
                .set("font-size", "12px")
                .set("font-weight", "600")
                .set("letter-spacing", "1px")
                .set("background", "#1e293b")
                .set("padding", "4px 8px")
                .set("border-radius", "6px");

        header.add(logo, version);
        header.setVerticalComponentAlignment(Alignment.CENTER, logo, version);
        header.setAlignItems(Alignment.CENTER);
        add(header);

        HorizontalLayout body = new HorizontalLayout();
        body.setSizeFull();
        body.setSpacing(false);

        // --- PANEL IZQUIERDO: FORMULARIO (Moderno) ---
        VerticalLayout formulario = new VerticalLayout();
        formulario.setWidth("450px");
        formulario.setHeightFull();
        formulario.getStyle()
                .set("background", "#111827")
                .set("border-right", "1px solid #1e293b");
        formulario.setPadding(true);
        formulario.setSpacing(true);

        H4 formTitle = new H4("Nueva Auditoría");
        formTitle.getStyle()
                .set("color", "#f8fafc")
                .set("margin-top", "10px")
                .set("margin-bottom", "20px");

        // --- CONFIGURACIÓN DE INPUTS (Nativos y limpios) ---
        TextField txtTitulo = new TextField("Título del Proyecto");
        txtTitulo.setWidthFull();
        txtTitulo.setPlaceholder("Ej: Mi Clase POO");
        txtTitulo.setClearButtonVisible(true); // Botón de limpiar "X" profesional

        TextArea txtCodigo = new TextArea("Snippet de Código Java");
        txtCodigo.setHeight("350px");
        txtCodigo.setWidthFull();
        txtCodigo.setPlaceholder("public class Ejemplo {\n    // Tu código aquí...\n}");
        // Hacemos que parezca un editor de código real
        txtCodigo.getStyle()
                .set("font-family", "'JetBrains Mono', Consolas, 'Courier New', monospace")
                .set("font-size", "13px");

        Button btnAnalizar = new Button("ANALIZAR CÓDIGO", e -> {
            if(!txtCodigo.getValue().isEmpty()) {
                String estado = HealthService.analizar(txtCodigo.getValue());
                String obs = HealthService.generarObservacion(estado);
                CodeReview nueva = new CodeReview(0, txtTitulo.getValue().isEmpty() ? "Sin Título" : txtTitulo.getValue(), "DonKeiler", txtCodigo.getValue(), estado, obs);
                repo.guardar(nueva);
                actualizarGaleria();
                txtTitulo.clear();
                txtCodigo.clear();
            }
        });

        // Estilo de botón profesional
        btnAnalizar.setWidthFull();
        btnAnalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY); // Tema principal nativo
        btnAnalizar.getStyle()
                .set("background", "linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)")
                .set("font-weight", "600")
                .set("letter-spacing", "1px")
                .set("margin-top", "15px")
                .set("height", "45px");

        formulario.add(formTitle, txtTitulo, txtCodigo, btnAnalizar);

        // --- PANEL DERECHO: GALERÍA (Elegante) ---
        VerticalLayout scrollContainer = new VerticalLayout();
        scrollContainer.setSizeFull();
        scrollContainer.getStyle()
                .set("overflow-y", "auto")
                .set("padding", "30px");

        H3 galeriaTitle = new H3("Historial de Análisis");
        galeriaTitle.getStyle().set("color", "#f1f5f9").set("margin-top", "0");

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
                    .set("padding", "20px")
                    .set("transition", "all 0.3s ease")
                    .set("cursor", "default");

            // Indicador de Salud
            String color;
            String bgColor;
            if ("Óptimo".equalsIgnoreCase(cr.getEstadoSalud())) {
                color = "#4ade80"; // Verde moderno
                bgColor = "rgba(74, 222, 128, 0.1)";
            } else if ("Advertencia".equalsIgnoreCase(cr.getEstadoSalud())) {
                color = "#fbbf24"; // Amarillo/Naranja
                bgColor = "rgba(251, 191, 36, 0.1)";
            } else {
                color = "#f87171"; // Rojo
                bgColor = "rgba(248, 113, 113, 0.1)";
            }

            Span badge = new Span(cr.getEstadoSalud().toUpperCase());
            badge.getStyle()
                    .set("background", bgColor)
                    .set("color", color)
                    .set("padding", "4px 12px")
                    .set("border-radius", "20px")
                    .set("font-size", "11px")
                    .set("font-weight", "700")
                    .set("letter-spacing", "0.5px")
                    .set("border", "1px solid " + "rgba(255,255,255,0.1)");

            H4 t = new H4(cr.getTitulo());
            t.getStyle()
                    .set("color", "#f8fafc")
                    .set("margin", "15px 0 5px 0")
                    .set("font-size", "18px");

            Paragraph p = new Paragraph(cr.getObservacion());
            p.getStyle()
                    .set("color", "#94a3b8")
                    .set("font-size", "14px")
                    .set("line-height", "1.5")
                    .set("margin", "0");

            card.add(badge, t, p);

            // Simular hover suave en Java (opcional pero le da toque premium)
            card.getElement().addEventListener("mouseenter", e -> {
                card.getStyle().set("transform", "translateY(-4px)");
                card.getStyle().set("box-shadow", "0 10px 20px -5px rgba(0, 0, 0, 0.4)");
                card.getStyle().set("border", "1px solid #475569");
            });
            card.getElement().addEventListener("mouseleave", e -> {
                card.getStyle().set("transform", "translateY(0)");
                card.getStyle().set("box-shadow", "none");
                card.getStyle().set("border", "1px solid #334155");
            });

            galeria.add(card);
        }
    }
}