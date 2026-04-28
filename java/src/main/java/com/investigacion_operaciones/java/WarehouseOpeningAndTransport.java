package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class WarehouseOpeningAndTransport extends Helpers {

    public void handler() {
        System.out.println("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades enviadas desde la bodega i al cliente j");
        System.out.println("yA = 1 si se abre la Bodega A, 0 en caso contrario");
        System.out.println("yB = 1 si se abre la Bodega B, 0 en caso contrario");
        System.out.println();
        System.out.println("xA1 = unidades enviadas desde A al Cliente 1");
        System.out.println("xA2 = unidades enviadas desde A al Cliente 2");
        System.out.println("xA3 = unidades enviadas desde A al Cliente 3");
        System.out.println("xB1 = unidades enviadas desde B al Cliente 1");
        System.out.println("xB2 = unidades enviadas desde B al Cliente 2");
        System.out.println("xB3 = unidades enviadas desde B al Cliente 3");
        System.out.println();

        System.out.println("2) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = 500yA + 400yB + 4xA1 + 6xA2 + 9xA3 + 5xB1 + 4xB2 + 7xB3");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable xA1 = model.newVariable("xA1").lower(0).weight(4);
        Variable xA2 = model.newVariable("xA2").lower(0).weight(6);
        Variable xA3 = model.newVariable("xA3").lower(0).weight(9);
        Variable xB1 = model.newVariable("xB1").lower(0).weight(5);
        Variable xB2 = model.newVariable("xB2").lower(0).weight(4);
        Variable xB3 = model.newVariable("xB3").lower(0).weight(7);

        Variable yA = model.newVariable("yA").binary().weight(500);
        Variable yB = model.newVariable("yB").binary().weight(400);

        model.addExpression("Demanda_Cliente_1").set(xA1, 1).set(xB1, 1).level(80);
        model.addExpression("Demanda_Cliente_2").set(xA2, 1).set(xB2, 1).level(70);
        model.addExpression("Demanda_Cliente_3").set(xA3, 1).set(xB3, 1).level(60);

        model.addExpression("Capacidad_Bodega_A")
                .set(xA1, 1)
                .set(xA2, 1)
                .set(xA3, 1)
                .set(yA, -150)
                .upper(0);

        model.addExpression("Capacidad_Bodega_B")
                .set(xB1, 1)
                .set(xB2, 1)
                .set(xB3, 1)
                .set(yB, -120)
                .upper(0);

        System.out.println("3) RESTRICCIONES");
        System.out.println("Demanda de los clientes:");
        System.out.println("xA1 + xB1 = 80");
        System.out.println("xA2 + xB2 = 70");
        System.out.println("xA3 + xB3 = 60");
        System.out.println();
        System.out.println("Capacidad de las bodegas:");
        System.out.println("xA1 + xA2 + xA3 <= 150yA");
        System.out.println("xB1 + xB2 + xB3 <= 120yB");
        System.out.println();
        System.out.println("No negatividad:");
        System.out.println("xij >= 0");
        System.out.println();
        System.out.println("Variables binarias:");
        System.out.println("yA, yB = 0 o 1");
        System.out.println();

        Optimisation.Result result = model.minimise();

        System.out.println("4) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("Valores óptimos de las variables:");
        System.out.println("xA1 = " + formatPythonFloat(xA1.getValue().doubleValue()));
        System.out.println("xA2 = " + formatPythonFloat(xA2.getValue().doubleValue()));
        System.out.println("xA3 = " + formatPythonFloat(xA3.getValue().doubleValue()));
        System.out.println();
        System.out.println("xB1 = " + formatPythonFloat(xB1.getValue().doubleValue()));
        System.out.println("xB2 = " + formatPythonFloat(xB2.getValue().doubleValue()));
        System.out.println("xB3 = " + formatPythonFloat(xB3.getValue().doubleValue()));
        System.out.println();
        System.out.println("yA = " + formatPythonFloat(yA.getValue().doubleValue()));
        System.out.println("yB = " + formatPythonFloat(yB.getValue().doubleValue()));
        System.out.println();

        System.out.println("Costo mínimo total:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()));
        System.out.println();

        double totalA = xA1.getValue().doubleValue() + xA2.getValue().doubleValue() + xA3.getValue().doubleValue();
        double totalB = xB1.getValue().doubleValue() + xB2.getValue().doubleValue() + xB3.getValue().doubleValue();

        System.out.println("Uso total de cada bodega:");
        System.out.println("Bodega A = " + formatPythonFloat(totalA) + " unidades de 150");
        System.out.println("Bodega B = " + formatPythonFloat(totalB) + " unidades de 120");
        System.out.println();

        System.out.println("Demanda satisfecha de cada cliente:");
        System.out.println("Cliente 1 = " + formatPythonFloat(xA1.getValue().doubleValue() + xB1.getValue().doubleValue()) + " unidades");
        System.out.println("Cliente 2 = " + formatPythonFloat(xA2.getValue().doubleValue() + xB2.getValue().doubleValue()) + " unidades");
        System.out.println("Cliente 3 = " + formatPythonFloat(xA3.getValue().doubleValue() + xB3.getValue().doubleValue()) + " unidades");
        System.out.println();

        System.out.println("Interpretación de la solución:");
        System.out.println((yA.getValue().doubleValue() >= 0.5) ? "Se abre la Bodega A" : "No se abre la Bodega A");
        System.out.println((yB.getValue().doubleValue() >= 0.5) ? "Se abre la Bodega B" : "No se abre la Bodega B");
        System.out.println();
        System.out.println("Enviar " + formatPythonFloat(xA1.getValue().doubleValue()) + " unidades desde A al Cliente 1");
        System.out.println("Enviar " + formatPythonFloat(xA2.getValue().doubleValue()) + " unidades desde A al Cliente 2");
        System.out.println("Enviar " + formatPythonFloat(xA3.getValue().doubleValue()) + " unidades desde A al Cliente 3");
        System.out.println("Enviar " + formatPythonFloat(xB1.getValue().doubleValue()) + " unidades desde B al Cliente 1");
        System.out.println("Enviar " + formatPythonFloat(xB2.getValue().doubleValue()) + " unidades desde B al Cliente 2");
        System.out.println("Enviar " + formatPythonFloat(xB3.getValue().doubleValue()) + " unidades desde B al Cliente 3");
    }
}
