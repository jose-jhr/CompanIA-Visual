package com.ingenieriajhr.visualcontroll.accessibility.utils.adapters;


import android.widget.ImageView;

public class ModelCiberPaz {
    private String titulo;
    private String descripcion;
    private int imagen;

    private String url;

    private int colorFondo;//color terminado

    public int getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(int colorFondo) {
        this.colorFondo = colorFondo;
    }

    public ModelCiberPaz(String titulo,
                         String descripcion,
                         int imagen,
                         String url,
                         int colorFondo){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.url = url;
        this.colorFondo = colorFondo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}