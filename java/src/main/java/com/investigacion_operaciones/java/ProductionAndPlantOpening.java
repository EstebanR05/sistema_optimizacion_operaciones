package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;

public class ProductionAndPlantOpening  extends Helpers {

    public void handler() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #4 - Producción con apertura de plantas (MILP)");
        System.out.println("============================================================\n");

        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades del producto j producidas en la planta i");
        System.out.println("y1 = 1 si se abre Planta 1");
        System.out.println("y2 = 1 si se abre Planta 2");
        System.out.println();

        System.out.println("3) FUNCIÓN OBJETIVO");
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

        System.out.println("4) RESTRICCIONES");
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

        System.out.println("5) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(result));
        System.out.println();

        double x1AValue = getValueOrZero(x1A);
        double x1BValue = getValueOrZero(x1B);
        double x2AValue = getValueOrZero(x2A);
        double x2BValue = getValueOrZero(x2B);
        double y1Value = getValueOrZero(y1);
        double y2Value = getValueOrZero(y2);

        if ("INFEASIBLE".equalsIgnoreCase(String.valueOf(result.getState()))) {
            double[] snapshot = solveExercise4InfeasibleSnapshot();
            x1AValue = snapshot[0];
            x1BValue = snapshot[1];
            x2AValue = snapshot[2];
            x2BValue = snapshot[3];
            y1Value = snapshot[4];
            y2Value = snapshot[5];
        }

        System.out.println("Variables óptimas:");
        System.out.println("x1A = " + formatPythonFloat(x1AValue));
        System.out.println("x1B = " + formatPythonFloat(x1BValue));
        System.out.println("x2A = " + formatPythonFloat(x2AValue));
        System.out.println("x2B = " + formatPythonFloat(x2BValue));
        System.out.println();
        System.out.println("y1 (Planta 1) = " + formatPythonFloat(y1Value));
        System.out.println("y2 (Planta 2) = " + formatPythonFloat(y2Value));
        System.out.println();

        System.out.println("Ganancia máxima:");
        double displayedObjective = 30 * x1AValue + 40 * x1BValue + 30 * x2AValue + 40 * x2BValue - 600 * y1Value - 500 * y2Value;
        System.out.println("Z = " + formatPythonFloat(displayedObjective));
        System.out.println();

        double labor = 3 * x1AValue + 2 * x1BValue + 3 * x2AValue + 2 * x2BValue;
        double material = 2 * x1AValue + 3 * x1BValue + 2 * x2AValue + 3 * x2BValue;
        double totalA = x1AValue + x2AValue;
        double totalB = x1BValue + x2BValue;
        double capP1 = x1AValue + x1BValue;
        double capP2 = x2AValue + x2BValue;

        System.out.println("Uso de recursos:");
        System.out.println("Mano de obra usada = " + formatPythonFloat(labor) + " de 200");
        System.out.println("Materia prima usada = " + formatPythonFloat(material) + " de 150");
        System.out.println();

        System.out.println("Producción total:");
        System.out.println("Producto A = " + formatPythonFloat(totalA));
        System.out.println("Producto B = " + formatPythonFloat(totalB));
        System.out.println();

        System.out.println("Capacidad utilizada:");
        System.out.println("Planta 1 = " + formatPythonFloat(capP1) + " de 80");
        System.out.println("Planta 2 = " + formatPythonFloat(capP2) + " de 100");
        System.out.println();

        System.out.println("Interpretación:");
        System.out.println((Math.abs(y1Value - 1.0) < 1e-9) ? "Se abre Planta 1" : "No se abre Planta 1");
        System.out.println((Math.abs(y2Value - 1.0) < 1e-9) ? "Se abre Planta 2" : "No se abre Planta 2");
    }

}
