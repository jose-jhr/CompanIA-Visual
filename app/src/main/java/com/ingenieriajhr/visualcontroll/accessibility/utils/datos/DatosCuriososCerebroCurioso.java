package com.ingenieriajhr.visualcontroll.accessibility.utils.datos;

import java.util.Random;

public class DatosCuriososCerebroCurioso {

    /**
     * Estos datos seran obtenidos por un servidor en el futuro en este momento se generan de manera aleatoria
     * debido a que es un DEMO.
     */
    private static String[] datosCuriosos = {
            "Sabías que la inteligencia artificial (IA) está ayudando a predecir patrones climáticos y desastres naturales, permitiendo a los científicos anticipar fenómenos como huracanes o sequías?",
            "Sabías que se estima que las emisiones de carbono asociadas con la fabricación y el uso de la inteligencia artificial podrían representar hasta el 14% de las emisiones globales de gases de efecto invernadero para 2040?",
            "Sabías que el uso de IA para optimizar el consumo de energía en edificios y ciudades está ayudando a reducir la huella de carbono a nivel global?",
            "Sabías que un solo ataque de ransomware puede paralizar a una gran empresa durante días, costando miles de millones de dólares en pérdidas y recuperaciones?",
            "Sabías que la minería de criptomonedas consume más energía que algunos países enteros, lo que plantea preocupaciones sobre el impacto ambiental de las tecnologías emergentes?",
            "Sabías que en 2019, los algoritmos de IA fueron responsables de reducir en un 30% los residuos de productos no vendidos en grandes almacenes?",
            "Sabías que la ciberseguridad juega un papel crucial en la protección de infraestructuras críticas como las plantas de energía y los sistemas de transporte, evitando desastres que podrían afectar a millones de personas?",
            "Sabías que en 2020, se registraron más de 1.000 millones de ataques de phishing a nivel mundial, lo que demuestra el aumento de las amenazas en línea?",
            "Sabías que el uso de la IA en el reciclaje está mejorando la eficiencia de los centros de clasificación de desechos, permitiendo separar materiales reciclables de manera más efectiva?",
            "Sabías que el cambio climático está afectando el crecimiento y la distribución de las especies de animales, y los datos obtenidos mediante IA pueden ayudar a rastrear estos cambios en tiempo real?",
            "Sabías que las plataformas de redes sociales pueden ser un caldo de cultivo para la desinformación y los ataques cibernéticos, por lo que la ciberseguridad es vital para la protección de datos personales?",
            "Sabías que el 65% de las organizaciones han experimentado al menos un ataque de seguridad cibernética en los últimos 12 meses, lo que refleja un aumento en las amenazas a nivel global?",
            "Sabías que la IA también se está utilizando para predecir y mitigar los efectos del cambio climático mediante modelos que ayudan a planificar infraestructuras más sostenibles?",
            "Sabías que en 2023, las estadísticas muestran que más del 90% de los ataques cibernéticos en línea comenzaron con un correo electrónico de phishing?",
            "Sabías que los combustibles fósiles, utilizados en la minería de criptomonedas y otras tecnologías, siguen siendo una de las principales fuentes de contaminación en el planeta?",
            "Sabías que cerca de 50 millones de dispositivos IoT están siendo conectados a internet cada día, lo que incrementa las vulnerabilidades de ciberseguridad en los hogares y empresas?",
            "Sabías que los ataques de 'denegación de servicio' (DDoS) pueden inundar servidores con tráfico innecesario, afectando la disponibilidad de servicios digitales a nivel mundial?",
            "Sabías que las tecnologías emergentes como blockchain tienen el potencial de transformar la forma en que gestionamos los derechos digitales y la privacidad de los usuarios en línea?",
            "Sabías que en las últimas décadas, la automatización impulsada por la IA ha reemplazado muchos trabajos en industrias como la manufactura, pero también ha generado nuevos roles en la tecnología y la innovación?",
            "Sabías que la inteligencia artificial está ayudando a restaurar ecosistemas dañados, como los bosques y las zonas costeras, utilizando algoritmos para identificar las áreas que necesitan atención urgente?",
            "Sabías que la ciberseguridad móvil es ahora una de las principales preocupaciones, dado que los dispositivos móviles son más vulnerables a ataques como el malware y el robo de información personal?",
            "Sabías que el reciclaje de dispositivos electrónicos está siendo facilitado por tecnologías de IA que identifican materiales valiosos como el oro, la plata y el cobre, que se pueden recuperar de manera más eficiente?",
            "Sabías que los satélites, que son cruciales para la observación del medio ambiente, también dependen de la ciberseguridad para proteger los datos sensibles que recogen sobre el clima y la biodiversidad?",
            "Sabías que la mayoría de las ciudades del mundo están implementando soluciones tecnológicas para gestionar mejor sus recursos naturales, como el agua y la energía, utilizando sistemas basados en IA?",
            "Sabías que la inteligencia artificial no solo puede analizar datos grandes, sino también ayudar a predecir futuras necesidades energéticas, contribuyendo a la eficiencia y la sostenibilidad del sector energético?",
            "Sabías que las vulnerabilidades en las infraestructuras tecnológicas pueden ser explotadas por cibercriminales para acceder a información privada o incluso robar recursos financieros a gran escala?",
            "Sabías que el consumo de energía por parte de los servidores que procesan datos de inteligencia artificial está contribuyendo significativamente al calentamiento global, lo que ha generado un debate sobre la sostenibilidad de la IA?",
            "Sabías que según la ONU, las industrias tecnológicas contribuyen a más del 3% de las emisiones globales de carbono, lo que destaca la necesidad urgente de innovar hacia modelos más sostenibles?",
            "Sabías que a medida que aumenta el uso de la tecnología, también lo hacen los riesgos de ciberseguridad, con un incremento en los ataques a infraestructuras críticas como hospitales y redes eléctricas?",
            "Sabías que la IA está desempeñando un papel fundamental en la lucha contra el cambio climático, ayudando a predecir patrones de emisiones y a optimizar la distribución de energía renovable?",
            "Sabías que los hackers utilizan IA y machine learning para crear ataques cibernéticos más sofisticados que pueden evadir los sistemas tradicionales de seguridad?",
            "Sabías que se espera que en los próximos años, el 75% de las infraestructuras tecnológicas implementen algún tipo de tecnología basada en inteligencia artificial para aumentar la seguridad y la sostenibilidad?",
            "Sabías que el 60% de los desechos electrónicos del mundo no se reciclan adecuadamente, lo que crea un gran problema ambiental, pero la IA está mejorando la recolección y clasificación de estos materiales?",
            "Sabías que la automatización de procesos mediante IA está reduciendo significativamente el uso de recursos, como el papel y los combustibles fósiles, en industrias que antes dependían en gran medida de ellos?"
    };

        public static String getDatoCurioso() {
            Random rand = new Random();
            int index = rand.nextInt(datosCuriosos.length);
            return datosCuriosos[index];
        }

}
