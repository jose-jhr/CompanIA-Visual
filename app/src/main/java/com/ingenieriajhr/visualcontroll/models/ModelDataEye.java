package com.ingenieriajhr.visualcontroll.models;
public class ModelDataEye {

    private float limit_xi = 0;
    private float limit_xf = 0;

    private float limit_yi = 0;
    private float limit_yf = 0;
    private float iris_center_x = 0;
    private float iris_center_y = 0;
    public ModelDataEye(float limit_xi, float limit_yi, float limit_xf, float limit_yf,float iris_center_x,float iris_center_y){
        this.limit_xi = limit_xi;
        this.limit_yi = limit_yi;
        this.limit_xf = limit_xf;
        this.limit_yf = limit_yf;
        this.iris_center_x = iris_center_x;
        this.iris_center_y = iris_center_y;

    }

    // Métodos get y set para iris_center_x
    public float getIrisCenterX() {
        return iris_center_x;
    }

    public void setIrisCenterX(Integer iris_center_x) {
        this.iris_center_x = iris_center_x;
    }

    // Métodos get y set para iris_center_y
    public float getIrisCenterY() {
        return iris_center_y;
    }

    public void setIrisCenterY(Integer iris_center_y) {
        this.iris_center_y = iris_center_y;
    }

    // Métodos get y set para limit_xi
    public float getLimitXi() {
        return limit_xi;
    }

    public void setLimitXi(float limit_xi) {
        this.limit_xi = limit_xi;
    }

    // Métodos get y set para limit_xf
    public float getLimitXf() {
        return limit_xf;
    }

    public void setLimitXf(float limit_xf) {
        this.limit_xf = limit_xf;
    }

    // Métodos get y set para limit_yi
    public float getLimitYi() {
        return limit_yi;
    }

    public void setLimitYi(float limit_yi) {
        this.limit_yi = limit_yi;
    }

    // Métodos get y set para limit_yf
    public float getLimitYf() {
        return limit_yf;
    }

    public void setLimitYf(float limit_yf) {
        this.limit_yf = limit_yf;
    }

}
