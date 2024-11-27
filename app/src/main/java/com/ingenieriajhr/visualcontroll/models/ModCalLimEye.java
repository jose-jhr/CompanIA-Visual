package com.ingenieriajhr.visualcontroll.models;

public class ModCalLimEye {

    private float xi = 0;
    private float xf = 0;
    private float yi = 0;
    private float yf = 0;

    public ModCalLimEye(float xi, float yi, float xf, float yf){
        this.xi = xi;
        this.yi = yi;
        this.xf = xf;
        this.yf = yf;
    }

    // Métodos getter y setter para xi
    public float getXi() {
        return xi;
    }

    public void setXi(float xi) {
        this.xi = xi;
    }

    // Métodos getter y setter para xf
    public float getXf() {
        return xf;
    }

    public void setXf(float xf) {
        this.xf = xf;
    }

    // Métodos getter y setter para yi
    public float getYi() {
        return yi;
    }

    public void setYi(float yi) {
        this.yi = yi;
    }

    // Métodos getter y setter para yf
    public float getYf() {
        return yf;
    }

    public void setYf(float yf) {
        this.yf = yf;
    }

}
