package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class FurnitureFactory  extends Helpers {

    private record FurnitureData(
        String[] codes,
        String[] names,
        String[] descriptions,
        int[] utility,
        int[] time,
        int[] material,
        int[] demand
    ) {}

    private record FurnitureModel(ExpressionsBasedModel model, Variable[] variables) {}

    private record FurnitureSolution(Optimisation.Result result, int[] values, int objective) {}

    public void handler() {
        FurnitureData data = buildData();
        printDecisionVariables(data);
        printObjective(data);
        printConstraints(data);

        FurnitureModel model = buildModel(data);
        FurnitureSolution solution = solve(model, data);

        printSolution(solution, data);
        printVerification(solution, data);
        printFinalAnswer(solution);
    }

    private FurnitureData buildData() {
        return new FurnitureData(
            new String[]{"X1", "X2"},
            new String[]{"Sillas", "Mesas"},
            new String[]{
                "número de sillas que debe producir la fábrica diariamente",
                "número de mesas que debe producir la fábrica diariamente"
            },
            new int[]{30, 50},
            new int[]{2, 5},
            new int[]{1, 3},
            new int[]{20, 10}
        );
    }

    private void printDecisionVariables(FurnitureData data) {
        printSection("1. VARIABLES DE DECISIÓN");
        for (int i = 0; i < data.codes.length; i++) {
            System.out.println(data.codes[i] + " = " + data.descriptions[i]);
        }
        printBlankLine();
    }

    private void printObjective(FurnitureData data) {
        List<String> objectiveTerms = new ArrayList<>();
        for (int i = 0; i < data.codes.length; i++) {
            objectiveTerms.add(data.utility[i] + data.codes[i]);
        }
        printSection("2. FUNCIÓN OBJETIVO");
        System.out.println("Max Z = " + joinTerms(objectiveTerms));
        printBlankLine();
    }

    private void printConstraints(FurnitureData data) {
        printSection("3. RESTRICCIONES");
        System.out.println(data.time[0] + data.codes[0] + " + " + data.time[1] + data.codes[1] + " <= 100   -> Restricción de tiempo");
        System.out.println(data.material[0] + data.codes[0] + " + " + data.material[1] + data.codes[1] + " <= 60   -> Restricción de material");
        for (int i = 0; i < data.codes.length; i++) {
            System.out.println(data.codes[i] + " <= " + data.demand[i] + "           -> Demanda máxima de " + data.names[i].toLowerCase());
        }
        System.out.println(String.join(", ", data.codes) + " >= 0");
        printBlankLine();
    }

    private FurnitureModel buildModel(FurnitureData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[] variables = new Variable[data.codes.length];

        for (int i = 0; i < data.codes.length; i++) {
            variables[i] = model.newVariable(data.codes[i] + "_" + data.names[i]).lower(0).integer(true).weight(data.utility[i]);
        }

        model.addExpression("Restriccion_Tiempo")
                .set(variables[0], data.time[0])
                .set(variables[1], data.time[1])
                .upper(100);

        model.addExpression("Restriccion_Material")
                .set(variables[0], data.material[0])
                .set(variables[1], data.material[1])
                .upper(60);

        for (int i = 0; i < data.codes.length; i++) {
            model.addExpression("Demanda_" + data.names[i])
                    .set(variables[i], 1)
                    .upper(data.demand[i]);
        }

        return new FurnitureModel(model, variables);
    }

    private FurnitureSolution solve(FurnitureModel furnitureModel, FurnitureData data) {
        Optimisation.Result result = furnitureModel.model.maximise();
        int[] values = new int[data.codes.length];
        for (int i = 0; i < data.codes.length; i++) {
            values[i] = (int) Math.round(furnitureModel.variables[i].getValue().doubleValue());
        }
        return new FurnitureSolution(result, values, (int) Math.round(result.getValue()));
    }

    private void printSolution(FurnitureSolution solution, FurnitureData data) {
        printSection("4. SOLUCIÓN DEL MODELO");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(solution.result));
        printBlankLine();
        printSection("Valores óptimos encontrados:");
        for (int i = 0; i < data.codes.length; i++) {
            System.out.println(data.codes[i] + " (" + data.names[i] + ") = " + solution.values[i]);
        }
        printBlankLine();
        printSection("Utilidad máxima:");
        System.out.println("Z = " + solution.objective);
        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private void printVerification(FurnitureSolution solution, FurnitureData data) {
        printSection("5. VERIFICACIÓN DE RESTRICCIONES");
        System.out.println("Tiempo usado: " + data.time[0] + "(" + solution.values[0] + ") + " + data.time[1] + "(" + solution.values[1] + ") = " + (data.time[0] * solution.values[0] + data.time[1] * solution.values[1]) + " <= 100");
        System.out.println("Material usado: " + data.material[0] + "(" + solution.values[0] + ") + " + data.material[1] + "(" + solution.values[1] + ") = " + (data.material[0] * solution.values[0] + data.material[1] * solution.values[1]) + " <= 60");
        for (int i = 0; i < data.codes.length; i++) {
            System.out.println("Demanda " + data.names[i].toLowerCase() + ": " + solution.values[i] + " <= " + data.demand[i]);
        }
        printBlankLine();
    }

    private void printFinalAnswer(FurnitureSolution solution) {
        printSection("7. RESPUESTA FINAL");
        System.out.println("La fábrica debe producir " + solution.values[0] + " sillas y " + solution.values[1] + " mesas por día.");
        System.out.println("La utilidad máxima que obtiene es de " + solution.objective + ".");
    }
}
