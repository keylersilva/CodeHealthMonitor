package codehealth.ui;

import codehealth.model.CodeReview;
import codehealth.repository.ReviewRepository;
import codehealth.security.AuthService;
import codehealth.service.HealthService;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Route("")
@PageTitle("Panel Auditoría | CodeHealth")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private ReviewRepository repo = new ReviewRepository();
    private FlexLayout galeria = new FlexLayout();
    private String autorActual;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        autorActual = AuthService.getUsuarioLogueado();
        if (autorActual == null) {
            event.forwardTo(LoginView.class);
        }
    }

    public MainView() {
        getElement().getThemeList().add(Lumo.DARK);
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "#0f172a");

        // --- HEADER ---
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle().set("background", "rgba(15, 23, 42, 0.8)").set("border-bottom", "1px solid #1e293b");

        HorizontalLayout logoWrapper = new HorizontalLayout();
        logoWrapper.setAlignItems(Alignment.CENTER);
        H2 logo = new H2("CODEHEALTH");
        logo.getStyle().set("color", "#818cf8").set("margin", "0").set("font-weight", "800");
        Span version = new Span("PRO V-EXPERT");
        version.getStyle().set("color", "#64748b").set("font-size", "11px").set("background", "#1e293b").set("padding", "4px 8px").set("border-radius", "4px").set("margin-left", "15px").set("letter-spacing", "1px");
        logoWrapper.add(logo, version);

        HorizontalLayout perfilInfo = new HorizontalLayout();
        perfilInfo.setAlignItems(Alignment.CENTER);
        Span lblUser = new Span("👨‍💻 " + autorActual);
        lblUser.getStyle().set("color", "#cbd5e1").set("font-weight", "600");

        Button btnSalir = new Button("Cerrar Sesión", e -> {
            AuthService.cerrarSesion();
            UI.getCurrent().getPage().reload();
        });
        btnSalir.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        perfilInfo.add(lblUser, btnSalir);
        header.add(logoWrapper, perfilInfo);
        add(header);

        HorizontalLayout body = new HorizontalLayout();
        body.setSizeFull();
        body.setSpacing(false);

        // --- PANEL FORMULARIO IZQUIERDO ---
        VerticalLayout formulario = new VerticalLayout();
        formulario.setWidth("480px"); // Ligeramente más grande para alojar la Zona Drag & Drop
        formulario.setHeightFull();
        formulario.getStyle().set("background", "#111827").set("border-right", "1px solid #1e293b").set("overflow-y", "auto");
        formulario.setPadding(true);

        H4 formTitle = new H4("Central de Análisis Inteligente");
        formTitle.getStyle().set("color", "#f8fafc").set("margin-top", "5px");

        // ZONA DRAG AND DROP - Carga de Archivos Reales .java
        MemoryBuffer buffer = new MemoryBuffer(); // Zona de la memoria Ram donde subirá el archivo
        Upload cargaArchivos = new Upload(buffer);
        cargaArchivos.setAcceptedFileTypes(".java", ".txt"); // Restringe que le pasen basura
        cargaArchivos.setDropLabel(new Span("Arrastra un archivo .java (O haz click)"));
        cargaArchivos.setWidthFull();

        TextField txtTitulo = new TextField("Título de Auditoría");
        txtTitulo.setWidthFull();
        txtTitulo.setClearButtonVisible(true);

        TextArea txtCodigo = new TextArea("Snippet / Source Code Java");
        txtCodigo.setHeight("250px");
        txtCodigo.setWidthFull();
        txtCodigo.getStyle().set("font-family", "'JetBrains Mono', Consolas, monospace").set("font-size", "13px");
        txtCodigo.setPlaceholder("El código se insertará aquí automáticamente al soltar tu archivo .java...");

        // Evento Mágico que "Lé y extrae" el texto del archivo a nuestra Pantalla
        cargaArchivos.addSucceededListener(e -> {
            try {
                InputStream archivoFlujo = buffer.getInputStream();
                // Lo convertimos del binario real a Cadena normal humana
                String textoJavaLeido = new String(archivoFlujo.readAllBytes(), StandardCharsets.UTF_8);
                txtCodigo.setValue(textoJavaLeido); // Llenamos la caja gorda negra!
                txtTitulo.setValue("Análisis del módulo: " + e.getFileName()); // Pone de titulo "Analisis Module.java"
                Notification.show("Lectura Completa (I/O). Archivo Listo.", 3000, Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("Error en flujo Input Stream al leer archivo.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button btnAnalizar = new Button("Auditar Proyecto I/O", e -> {
            if(!txtCodigo.getValue().isEmpty()) {
                String estado = HealthService.analizar(txtCodigo.getValue());
                // Fijate que ahora mandamos las dos variables a 'generarObservacion'
                String obs = HealthService.generarObservacion(estado, txtCodigo.getValue());

                CodeReview nueva = new CodeReview(0, txtTitulo.getValue().isEmpty() ? "Inspección Directa" : txtTitulo.getValue(), autorActual, txtCodigo.getValue(), estado, obs);
                repo.guardar(nueva);
                actualizarGaleria();
                txtTitulo.clear();
                txtCodigo.clear();
                cargaArchivos.clearFileList(); // resetea el visual de Vaadin para soltar mas archivos

                Notification toast = Notification.show("Evaluado (Nivel: " + estado + ")", 4000, Notification.Position.TOP_END);
                toast.addThemeVariants("Crítico".equals(estado) ? NotificationVariant.LUMO_ERROR : "Advertencia".equals(estado) ? NotificationVariant.LUMO_WARNING : NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification error = Notification.show("Por favor, soltar archivo o pegar un script primero.");
                error.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        btnAnalizar.setWidthFull();
        btnAnalizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnAnalizar.getStyle().set("background", "linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)").set("height", "50px").set("margin-top", "10px").set("font-weight", "bold").set("font-size", "15px").set("letter-spacing", "1px").set("box-shadow", "0 4px 15px rgba(99, 102, 241, 0.4)");

        // Observa como el 'cargaArchivos' (nuestro Vaadin Upload) se metio al final del layout
        formulario.add(formTitle, cargaArchivos, txtTitulo, txtCodigo, btnAnalizar);

        // --- GALERÍA DERECHA ---
        VerticalLayout scrollContainer = new VerticalLayout();
        scrollContainer.setSizeFull();
        scrollContainer.getStyle().set("overflow-y", "auto").set("padding", "30px");

        H3 galeriaTitle = new H3("Tus Auditorías");
        galeriaTitle.getStyle().set("color", "#f1f5f9").set("margin-top", "0px");

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
            card.setWidth("380px");
            card.getStyle()
                    .set("background", "rgba(30, 41, 59, 0.7)")
                    .set("border", "1px solid #334155")
                    .set("border-radius", "12px")
                    .set("padding", "20px")
                    .set("transition", "transform 0.2s");

            card.getElement().addEventListener("mouseenter", ev -> card.getStyle().set("border-color", "#64748b"));
            card.getElement().addEventListener("mouseleave", ev -> card.getStyle().set("border-color", "#334155"));

            HorizontalLayout cardTop = new HorizontalLayout();
            cardTop.setWidthFull();
            cardTop.setJustifyContentMode(JustifyContentMode.BETWEEN);
            cardTop.setAlignItems(Alignment.CENTER);

            String color = "Óptimo".equalsIgnoreCase(cr.getEstadoSalud()) ? "#4ade80" : "Advertencia".equalsIgnoreCase(cr.getEstadoSalud()) ? "#fbbf24" : "#f87171";
            Span badge = new Span("● " + cr.getEstadoSalud().toUpperCase());
            badge.getStyle().set("color", color).set("background", "rgba(15,23,42,0.6)").set("padding", "4px 10px").set("border-radius", "6px").set("font-size", "11px").set("font-weight", "bold").set("border", "1px solid " + color+"44");

            Button btnBorrar = new Button("🗑️");
            btnBorrar.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
            btnBorrar.getStyle().set("cursor", "pointer").set("font-size", "14px").set("color", "#ef4444");
            btnBorrar.addClickListener(e -> {
                repo.eliminar(cr.getId());
                actualizarGaleria();
                Notification.show("Registro Borrado Permanente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            });

            cardTop.add(badge, btnBorrar);

            H4 tituloInfo = new H4(cr.getTitulo());
            tituloInfo.getStyle().set("color", "#f8fafc").set("margin", "15px 0 5px 0").set("line-height", "1.2");

            Span pAutor = new Span("Ref: #" + cr.getId() + " - Por " + cr.getAutor());
            pAutor.getStyle().set("color", "#64748b").set("font-size", "11px");

            Div observacionWrapper = new Div();
            observacionWrapper.setText(cr.getObservacion());
            // Pre-Wrap formatea el texto espectacular como de la terminal!
            observacionWrapper.getStyle().set("color", "#94a3b8").set("font-size", "13px").set("line-height", "1.5").set("margin-top", "15px").set("white-space", "pre-wrap").set("background", "rgba(0,0,0,0.1)").set("padding", "10px").set("border-left", "2px solid " + color).set("border-radius", "4px");

            Div bloqueDeCodigoHtml = new Div();
            bloqueDeCodigoHtml.setText(cr.getSnippet());
            bloqueDeCodigoHtml.getStyle().set("background", "#0f172a").set("color", "#4ade80").set("padding", "15px").set("border-radius", "8px").set("font-family", "'JetBrains Mono', monospace").set("font-size", "11px").set("white-space", "pre-wrap").set("max-height", "300px").set("overflow-y", "auto");

            Details acordeonVisualizadorCodigo = new Details("Inspección de Fragmento Local", bloqueDeCodigoHtml);
            acordeonVisualizadorCodigo.addThemeVariants(DetailsVariant.REVERSE, DetailsVariant.SMALL);
            acordeonVisualizadorCodigo.getStyle().set("margin-top", "20px");

            card.add(cardTop, tituloInfo, pAutor, observacionWrapper, acordeonVisualizadorCodigo);
            galeria.add(card);
        }
    }
}