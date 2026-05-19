package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ProductionAndPlantOpening extends Helpers {

    private record PlantData(
        String[] plants,
        int[] capacities,
        int[] fixedCost,
        String[] products,
        int[] profits,
        int[] minDemand,
        int[][] resources,
        String[] resourceNames,
        int[] resourceLimits
    ) {}

    private record PlantModel(ExpressionsBasedModel model, Variable[][] x, Variable[] y) {}

    public void handler() {
        PlantData data = buildData();
        printDecisionVariables(data);
        printObjective(data);
        PlantModel model = buildModel(data);
        printConstraints(data);
        printSolution(data, model);
    }

    private PlantData buildData() {
        return new PlantData(
            new String[]{"1", "2"},
            new int[]{80, 100},
            new int[]{600, 500},
            new String[]{"A", "B"},
            new int[]{30, 40},
            new int[]{40, 30},
            new int[][]{{3, 2}, {2, 3}},
            new String[]{"Mano de obra", "Materia prima"},
            new int[]{200, 200}
        );
    }

    private void printDecisionVariables(PlantData data) {
        printSection("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades del producto j producidas en la planta i");
        for (String plant : data.plants) {
            System.out.println("y" + plant + " = 1 si se abre Planta " + plant);
        }
        printBlankLine();
    }

    private void printObjective(PlantData data) {
        List<String> gainTerms = new ArrayList<>();
        List<String> costTerms = new ArrayList<>();
        for (String plant : data.plants) {
            for (int j = 0; j < data.products.length; j++) {
                gainTerms.add(data.profits[j] + "x" + plant + data.products[j]);
            }
        }
        for (int i = 0; i < data.plants.length; i++) {
            costTerms.add(data.fixedCost[i] + "y" + data.plants[i]);
        }

        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Max Z = " + joinTerms(gainTerms) + " - " + String.join(" - ", costTerms));
        printBlankLine();
    }

    private PlantModel buildModel(PlantData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] x = new Variable[data.plants.length][data.products.length];
        Variable[] y = new Variable[data.plants.length];

        for (int i = 0; i < data.plants.length; i++) {
            y[i] = model.newVariable("y" + data.plants[i]).binary().weight(-data.fixedCost[i]);
            for (int j = 0; j < data.products.length; j++) {
                x[i][j] = model.newVariable("x" + data.plants[i] + data.products[j]).lower(0).weight(data.profits[j]);
            }
        }

        for (int r = 0; r < data.resourceNames.length; r++) {
            Expression expr = model.addExpression("Restriccion_" + data.resourceNames[r].replace(" ", "_")).upper(data.resourceLimits[r]);
            for (int i = 0; i < data.plants.length; i++) {
                for (int j = 0; j < data.products.length; j++) {
                    expr.set(x[i][j], data.resources[r][j]);
                }
            }
        }

        for (int i = 0; i < data.plants.length; i++) {
            Expression cap = model.addExpression("Capacidad_Planta_" + data.plants[i]).upper(0);
            for (int j = 0; j < data.products.length; j++) {
                cap.set(x[i][j], 1);
            }
            cap.set(y[i], -data.capacities[i]);
        }

        for (int j = 0; j < data.products.length; j++) {
            Expression minDemand = model.addExpression("Demanda_Minima_" + data.products[j]).lower(data.minDemand[j]);
            for (int i = 0; i < data.plants.length; i++) {
                minDemand.set(x[i][j], 1);
            }
        }

        return new PlantModel(model, x, y);
    }

    private void printConstraints(PlantData data) {
        printSection("3) RESTRICCIONES");

        for (int r = 0; r < data.resourceNames.length; r++) {
            printSection(data.resourceNames[r] + ":");
            List<String> terms = new ArrayList<>();
            for (String plant : data.plants) {
                for (int j = 0; j < data.products.length; j++) {
                    terms.add(data.resources[r][j] + "x" + plant + data.products[j]);
                }
            }
            System.out.println(joinTerms(terms) + " <= " + data.resourceLimits[r]);
            printBlankLine();
        }

        printSection("Capacidad por planta:");
        for (int i = 0; i < data.plants.length; i++) {
            List<String> terms = new ArrayList<>();
            for (String product : data.products) {
                terms.add("x" + data.plants[i] + product);
            }
            System.out.println(joinTerms(terms) + " <= " + data.capacities[i] + "y" + data.plants[i]);
        }
        printBlankLine();

        printSection("Demanda mínima:");
        for (int j = 0; j < data.products.length; j++) {
            List<String> terms = new ArrayList<>();
            for (String plant : data.plants) {
                terms.add("x" + plant + data.products[j]);
            }
            System.out.println(joinTerms(terms) + " >= " + data.minDemand[j]);
        }
        printBlankLine();

        printSection("No negatividad y binarias");
        System.out.println("xij >= 0, yi = 0 o 1");
        printBlankLine();
    }

    private void printSolution(PlantData data, PlantModel model) {
        Optimisation.Result result = model.model.maximise();

        printSection("4) SOLUCIÓN");
        String estado = toPythonLikeStatus(result);
        System.out.println("Estado: " + estado);
        printBlankLine();

        if ("Optimal".equals(estado)) {
            printSection("Variables óptimas:");
            for (int i = 0; i < data.plants.length; i++) {
                for (int j = 0; j < data.products.length; j++) {
                    System.out.println("x" + data.plants[i] + data.products[j] + " = " + getValueOrZero(model.x[i][j]));
                }
            }
            printBlankLine();

            for (int i = 0; i < data.plants.length; i++) {
                System.out.println("y" + data.plants[i] + " (Planta " + data.plants[i] + ") = " + getValueOrZero(model.y[i]));
            }
            printBlankLine();

            printSection("Ganancia máxima:");
            System.out.println("Z = " + result.getValue());
            printBlankLine();

            printSection("Uso de recursos:");
            for (int r = 0; r < data.resourceNames.length; r++) {
                double used = 0.0;
                for (int i = 0; i < data.plants.length; i++) {
                    for (int j = 0; j < data.products.length; j++) {
                        used += data.resources[r][j] * getValueOrZero(model.x[i][j]);
                    }
                }
                System.out.println(data.resourceNames[r] + " usada = " + used + " de " + data.resourceLimits[r]);
            }
            printBlankLine();

            printSection("Producción total:");
            for (int j = 0; j < data.products.length; j++) {
                double production = 0.0;
                for (int i = 0; i < data.plants.length; i++) {
                    production += getValueOrZero(model.x[i][j]);
                }
                System.out.println("Producto " + data.products[j] + " = " + production);
            }
            printBlankLine();

            printSection("Capacidad utilizada:");
            for (int i = 0; i < data.plants.length; i++) {
                double used = 0.0;
                for (int j = 0; j < data.products.length; j++) {
                    used += getValueOrZero(model.x[i][j]);
                }
                System.out.println("Planta " + data.plants[i] + " = " + used + " de " + data.capacities[i]);
            }
            printBlankLine();

            printSection("Interpretación:");
            for (int i = 0; i < data.plants.length; i++) {
                if (getValueOrZero(model.y[i]) >= 0.5) {
                    System.out.println("Se abre Planta " + data.plants[i]);
                } else {
                    System.out.println("No se abre Planta " + data.plants[i]);
                }
            }
        } else {
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
}
