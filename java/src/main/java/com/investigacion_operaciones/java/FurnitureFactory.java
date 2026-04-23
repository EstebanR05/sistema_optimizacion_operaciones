package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;

public class FurnitureFactory  extends Helpers {

    public void handler() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #1 - Fábrica de sillas y mesas (PL entera)");
        System.out.println("============================================================\n");

        System.out.println("2. VARIABLES DE DECISIÓN");
        System.out.println("X1 = número de sillas que debe producir la fábrica diariamente");
        System.out.println("X2 = número de mesas que debe producir la fábrica diariamente");
        System.out.println();

        System.out.println("3. FUNCIÓN OBJETIVO");
        System.out.println("Max Z = 30X1 + 50X2");
        System.out.println();

        System.out.println("4. RESTRICCIONES");
        System.out.println("2X1 + 5X2 <= 100   -> Restricción de tiempo");
        System.out.println("X1 + 3X2 <= 60     -> Restricción de material");
        System.out.println("X1 <= 20           -> Demanda máxima de sillas");
        System.out.println("X2 <= 10           -> Demanda máxima de mesas");
        System.out.println("X1, X2 >= 0");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable x1 = model.newVariable("X1_Sillas").lower(0).integer(true).weight(30);
        Variable x2 = model.newVariable("X2_Mesas").lower(0).integer(true).weight(50);

        model.addExpression("Restriccion_Tiempo")
                .set(x1, 2)
                .set(x2, 5)
                .upper(100);

        model.addExpression("Restriccion_Material")
                .set(x1, 1)
                .set(x2, 3)
                .upper(60);

        model.addExpression("Demanda_Sillas")
                .set(x1, 1)
                .upper(20);

        model.addExpression("Demanda_Mesas")
                .set(x2, 1)
                .upper(10);

        Optimisation.Result result = model.maximise();

        int x1Value = (int) Math.round(x1.getValue().doubleValue());
        int x2Value = (int) Math.round(x2.getValue().doubleValue());
        int objective = (int) Math.round(result.getValue());

        System.out.println("5. SOLUCIÓN DEL MODELO");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("Valores óptimos encontrados:");
        System.out.println("X1 (Sillas) = " + x1Value);
        System.out.println("X2 (Mesas)  = " + x2Value);
        System.out.println();
        System.out.println("Utilidad máxima:");
        System.out.println("Z = " + objective);
        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("6. VERIFICACIÓN DE RESTRICCIONES");
        System.out.println();
        System.out.println("Tiempo usado: 2(" + x1Value + ") + 5(" + x2Value + ") = " + (2 * x1Value + 5 * x2Value) + " <= 100");
        System.out.println("Material usado: 1(" + x1Value + ") + 3(" + x2Value + ") = " + (x1Value + 3 * x2Value) + " <= 60");
        System.out.println("Demanda sillas: " + x1Value + " <= 20");
        System.out.println("Demanda mesas: " + x2Value + " <= 10");
        System.out.println();

        System.out.println("7. RESPUESTA FINAL");
        System.out.println("La fábrica debe producir " + x1Value + " sillas y " + x2Value + " mesas por día.");
        System.out.println("La utilidad máxima que obtiene es de " + objective + ".");
    }
}
