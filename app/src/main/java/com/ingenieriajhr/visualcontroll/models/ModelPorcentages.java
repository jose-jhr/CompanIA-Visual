package com.ingenieriajhr.visualcontroll.models;
public class ModelPorcentages {

    private float porcentajeX = 0;
    private float porcentajeY = 0;
    private Boolean isClick = false;

    private Boolean isHome = false;

    private Boolean isOpenApps = false;



    public ModelPorcentages(float porcentajeX, float porcentajeY,Boolean isClick,Boolean isHome,Boolean isOpenApps){
        this.porcentajeX = porcentajeX;
        this.porcentajeY = porcentajeY;
        this.isClick = isClick;
        this.isHome = isHome;
        this.isOpenApps = isOpenApps;

    }

    public Boolean getIsOpenApps(){return isOpenApps;}

    public void setIsOpenApps(Boolean isOpenApps){this.isOpenApps = isOpenApps;}


    public Boolean getIsHome(){return isHome;}

    public void setIsHome(Boolean isHome){this.isHome = isHome;}


    public Boolean getClick(){return isClick;}

    public void setClick(Boolean isClick){this.isClick = isClick;}

    // Métodos get y set para x_position
    public float getPorcentajeX() {
        return porcentajeX;
    }

    public void setPorcentajeX(float porcentajeX) {
        this.porcentajeX = porcentajeX;
    }

    // Métodos get y set para y_position
    public float getPorcentajeY() {
        return porcentajeY;
    }

    public void setPorcentajeY(float porcentajeY) {
        this.porcentajeY = porcentajeY;
    }



}
