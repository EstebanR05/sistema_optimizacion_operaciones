package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ProductionAndPlantOpening  extends Helpers {

    public void handler() {
        System.out.println("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades del producto j producidas en la planta i");
        System.out.println("y1 = 1 si se abre Planta 1");
        System.out.println("y2 = 1 si se abre Planta 2");
        System.out.println();

        System.out.println("2) FUNCIÓN OBJETIVO");
        System.out.println("Max Z = 30x1A + 40x1B + 30x2A + 40x2B - 600y1 - 500y2");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable x1A = model.newVariable("x1A").lower(0).weight(30);
        Variable x1B = model.newVariable("x1B").lower(0).weight(40);
        Variable x2A = model.newVariable("x2A").lower(0).weight(30);
        Variable x2B = model.newVariable("x2B").lower(0).weight(40);
        Variable y1 = model.newVariable("y1").binary().weight(-600);
        Variable y2 = model.newVariable("y2").binary().weight(-500);

        model.addExpression("Mano_Obra")
                .set(x1A, 3)
                .set(x1B, 2)
                .set(x2A, 3)
                .set(x2B, 2)
                .upper(200);

        model.addExpression("Materia_Prima")
                .set(x1A, 2)
                .set(x1B, 3)
                .set(x2A, 2)
                .set(x2B, 3)
                .upper(150);

        model.addExpression("Capacidad_P1")
                .set(x1A, 1)
                .set(x1B, 1)
                .set(y1, -80)
                .upper(0);

        model.addExpression("Capacidad_P2")
                .set(x2A, 1)
                .set(x2B, 1)
                .set(y2, -100)
                .upper(0);

        model.addExpression("Demanda_A")
                .set(x1A, 1)
                .set(x2A, 1)
                .lower(40);

        model.addExpression("Demanda_B")
                .set(x1B, 1)
                .set(x2B, 1)
                .lower(30);

        System.out.println("3) RESTRICCIONES");
        System.out.println("Mano de obra:");
        System.out.println("3x1A + 2x1B + 3x2A + 2x2B <= 200");
        System.out.println();
        System.out.println("Materia prima:");
        System.out.println("2x1A + 3x1B + 2x2A + 3x2B <= 150");
        System.out.println();
        System.out.println("Capacidad por planta:");
        System.out.println("x1A + x1B <= 80y1");
        System.out.println("x2A + x2B <= 100y2");
        System.out.println();
        System.out.println("Demanda mínima:");
        System.out.println("x1A + x2A >= 40");
        System.out.println("x1B + x2B >= 30");
        System.out.println();
        System.out.println("No negatividad y binarias");
        System.out.println("xij >= 0, yi = 0 o 1");
        System.out.println();

        Optimisation.Result result = model.maximise();

        System.out.println("4) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("El modelo no tiene una solución factible.");
        System.out.println("No se deben interpretar los valores de las variables como solución óptima.");
        System.out.println();
        System.out.println("Posible causa:");
        System.out.println("Con los recursos disponibles no se puede cumplir simultáneamente");
        System.out.println("la demanda mínima de A y B junto con las demás restricciones.");
    }

}
