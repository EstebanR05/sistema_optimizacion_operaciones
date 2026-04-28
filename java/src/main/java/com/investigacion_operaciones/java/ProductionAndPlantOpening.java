package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ProductionAndPlantOpening  extends Helpers {

    public void handler() {
        String[] plants = {"1", "2"};
        int[] capacities = {80, 100};
        int[] fixedCost = {600, 500};
        String[] products = {"A", "B"};
        int[] profits = {30, 40};
        int[] minDemand = {40, 30};
        int[][] resources = {
            {3, 2},
            {2, 3}
        };
        String[] resourceNames = {"Mano de obra", "Materia prima"};
        int[] resourceLimits = {200, 150};

        printSection("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades del producto j producidas en la planta i");
        for (String plant : plants) {
            System.out.println("y" + plant + " = 1 si se abre Planta " + plant);
        }
        printBlankLine();

        List<String> gainTerms = new ArrayList<>();
        List<String> costTerms = new ArrayList<>();
        for (String plant : plants) {
            for (int j = 0; j < products.length; j++) {
                gainTerms.add(profits[j] + "x" + plant + products[j]);
            }
        }
        for (int i = 0; i < plants.length; i++) {
            costTerms.add(fixedCost[i] + "y" + plants[i]);
        }
        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Max Z = " + joinTerms(gainTerms) + " - " + String.join(" - ", costTerms));
        printBlankLine();

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

        printSection("3) RESTRICCIONES");
        for (int r = 0; r < resourceNames.length; r++) {
            List<String> terms = new ArrayList<>();
            for (String plant : plants) {
                for (int j = 0; j < products.length; j++) {
                    terms.add(resources[r][j] + "x" + plant + products[j]);
                }
            }
            System.out.println(resourceNames[r] + ":");
            System.out.println(joinTerms(terms) + " <= " + resourceLimits[r]);
            printBlankLine();
        }
        printSection("Capacidad por planta:");
        for (int i = 0; i < plants.length; i++) {
            List<String> terms = new ArrayList<>();
            for (String product : products) {
                terms.add("x" + plants[i] + product);
            }
            System.out.println(joinTerms(terms) + " <= " + capacities[i] + "y" + plants[i]);
        }
        printBlankLine();
        printSection("Demanda mínima:");
        for (int j = 0; j < products.length; j++) {
            List<String> terms = new ArrayList<>();
            for (String plant : plants) {
                terms.add("x" + plant + products[j]);
            }
            System.out.println(joinTerms(terms) + " >= " + minDemand[j]);
        }
        printBlankLine();
        printSection("No negatividad y binarias");
        System.out.println("xij >= 0, yi = 0 o 1");
        printBlankLine();

        Optimisation.Result result = model.maximise();

        printSection("4) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(result));
        printBlankLine();
        printLines(
            "El modelo no tiene una solución factible.",
            "No se deben interpretar los valores de las variables como solución óptima."
        );
        printBlankLine();
        printSection("Posible causa:");
        printLines(
            "Con los recursos disponibles no se puede cumplir simultáneamente",
            "la demanda mínima de A y B junto con las demás restricciones."
        );
    }

}
