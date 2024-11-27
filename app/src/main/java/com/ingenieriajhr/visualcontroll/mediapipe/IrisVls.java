package com.ingenieriajhr.visualcontroll.mediapipe;

//Iris values
public enum IrisVls {
    //Limit
    LIMITEYEXL(33),
    LIMITEYEXR(133),
    LIMITEYEYT(173),
    LIMITEYEYB(154),

    //LIMITE EYE IZQUIERDO

    //AQUI SE DETECTARA PARPADEO PARA CLICK PUNTO ABAJO CENTRO IRIS
    // 468, PUNTOCENTRA 159, PUNTO ARRIBA IRIS 470,

    POINCENTERTIRIS(468),
    POINTDOWNPARP(145),
    POINTCENTERPARP(159),
    POINTTOPIRIS(470),
    //PUNTO CENTRO DE REFERENCIA X
    POINTCENTEREYEX(4),
    //PUNTO IZQUIERDA DE REFERENCIA

    POINTLEFTLIM(137),
    //PUNTO DERECHA DE REFERENCIA

    POINTRIGHTLIM(366),
    //PUNTO ARRIBA DE REFERENCIA

    //PUNTO CENTRO DE REFERENCIA X
    POINTCENTEREYEY(4),
    POINTTOPLIM(10),
    //PUNTO ABAJO DE REFERENCIA

    POINTBOTTOMLIM(175),

    /** --------Boca Home--------- **/
    POINTBOCATOP(0),
    POINTBOCADOWN(12),

    POINTBOCAHTOP(13),
    POINTBOCAHDOWN(14),


    POINTLABIOIZQUIERDO(57),
    POINTCENTROLABIO(0),

    //set iris points //eye left
    //left
    IIL(471),
    IIC(468),//center
    IIR(469),//right
    IID(472),//down
    IIU(470),//up

        //eye right
    IDL(476),//left
    IDC(473),//center
    IDR(474),//right
    IDD(475),//down
    IDU(477);//up



    private final Integer position;

    private IrisVls(Integer position){
        this.position = position;
    }

    public Integer getPosition(){
        return position;
    }
}


