package codehealth.model;

public class CodeReview {
    private int id;
    private String titulo;
    private String autor;
    private String snippet;
    private String estadoSalud;
    private String observacion;

    public CodeReview(int id, String titulo, String autor, String snippet, String estadoSalud, String observacion) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.snippet = snippet;
        this.estadoSalud = estadoSalud;
        this.observacion = observacion;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getSnippet() { return snippet; }
    public String getEstadoSalud() { return estadoSalud; }
    public String getObservacion() { return observacion; }
}