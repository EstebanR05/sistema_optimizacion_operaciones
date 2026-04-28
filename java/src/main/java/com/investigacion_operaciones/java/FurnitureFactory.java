package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class FurnitureFactory  extends Helpers {

    public void handler() {
        String[] codes = {"X1", "X2"};
        String[] names = {"Sillas", "Mesas"};
        String[] descriptions = {
            "número de sillas que debe producir la fábrica diariamente",
            "número de mesas que debe producir la fábrica diariamente"
        };
        int[] utility = {30, 50};
        int[] time = {2, 5};
        int[] material = {1, 3};
        int[] demand = {20, 10};

        printSection("1. VARIABLES DE DECISIÓN");
        for (int i = 0; i < codes.length; i++) {
            System.out.println(codes[i] + " = " + descriptions[i]);
        }
        printBlankLine();

        List<String> objectiveTerms = new ArrayList<>();
        for (int i = 0; i < codes.length; i++) {
            objectiveTerms.add(utility[i] + codes[i]);
        }
        printSection("2. FUNCIÓN OBJETIVO");
        System.out.println("Max Z = " + joinTerms(objectiveTerms));
        printBlankLine();

        printSection("3. RESTRICCIONES");
        System.out.println(time[0] + codes[0] + " + " + time[1] + codes[1] + " <= 100   -> Restricción de tiempo");
        System.out.println(material[0] + codes[0] + " + " + material[1] + codes[1] + " <= 60   -> Restricción de material");
        for (int i = 0; i < codes.length; i++) {
            System.out.println(codes[i] + " <= " + demand[i] + "           -> Demanda máxima de " + names[i].toLowerCase());
        }
        System.out.println(String.join(", ", codes) + " >= 0");
        printBlankLine();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[] variables = new Variable[codes.length];
        for (int i = 0; i < codes.length; i++) {
            variables[i] = model.newVariable(codes[i] + "_" + names[i]).lower(0).integer(true).weight(utility[i]);
        }

        model.addExpression("Restriccion_Tiempo")
                .set(variables[0], time[0])
                .set(variables[1], time[1])
                .upper(100);

        model.addExpression("Restriccion_Material")
                .set(variables[0], material[0])
                .set(variables[1], material[1])
                .upper(60);

        for (int i = 0; i < codes.length; i++) {
            model.addExpression("Demanda_" + names[i])
                    .set(variables[i], 1)
                    .upper(demand[i]);
        }

        Optimisation.Result result = model.maximise();

        int[] values = new int[codes.length];
        for (int i = 0; i < codes.length; i++) {
            values[i] = (int) Math.round(variables[i].getValue().doubleValue());
        }
        int objective = (int) Math.round(result.getValue());

        printSection("4. SOLUCIÓN DEL MODELO");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        printBlankLine();
        printSection("Valores óptimos encontrados:");
        for (int i = 0; i < codes.length; i++) {
            System.out.println(codes[i] + " (" + names[i] + ") = " + values[i]);
        }
        printBlankLine();
        printSection("Utilidad máxima:");
        System.out.println("Z = " + objective);
        System.out.println("\n" + "=".repeat(60) + "\n");

        printSection("5. VERIFICACIÓN DE RESTRICCIONES");
        System.out.println("Tiempo usado: " + time[0] + "(" + values[0] + ") + " + time[1] + "(" + values[1] + ") = " + (time[0] * values[0] + time[1] * values[1]) + " <= 100");
        System.out.println("Material usado: " + material[0] + "(" + values[0] + ") + " + material[1] + "(" + values[1] + ") = " + (material[0] * values[0] + material[1] * values[1]) + " <= 60");
        for (int i = 0; i < codes.length; i++) {
            System.out.println("Demanda " + names[i].toLowerCase() + ": " + values[i] + " <= " + demand[i]);
        }
        printBlankLine();

        printSection("7. RESPUESTA FINAL");
        System.out.println("La fábrica debe producir " + values[0] + " sillas y " + values[1] + " mesas por día.");
        System.out.println("La utilidad máxima que obtiene es de " + objective + ".");
    }
}
