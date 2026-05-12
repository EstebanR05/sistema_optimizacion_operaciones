package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class WarehouseOpeningAndTransport extends Helpers {

    private record WarehouseData(String[] warehouses, int[] capacities, int[] fixedCost, String[] clients, int[] demand, int[][] transportCost) {}

    private record WarehouseModel(ExpressionsBasedModel model, Variable[][] x, Variable[] y) {}

    public void handler() {
        WarehouseData data = buildData();
        printDecisionVariables(data);
        printObjective(data);
        WarehouseModel model = buildModel(data);
        printConstraints(data);
        printSolution(data, model);
    }

    private WarehouseData buildData() {
        return new WarehouseData(
            new String[]{"A", "B"},
            new int[]{150, 120},
            new int[]{500, 400},
            new String[]{"1", "2", "3"},
            new int[]{80, 70, 60},
            new int[][]{{4, 6, 9}, {5, 4, 7}}
        );
    }

    private void printDecisionVariables(WarehouseData data) {
        printSection("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades enviadas desde la bodega i al cliente j");
        for (String warehouse : data.warehouses) {
            System.out.println("y" + warehouse + " = 1 si se abre la Bodega " + warehouse + ", 0 en caso contrario");
        }
        printBlankLine();
        for (String warehouse : data.warehouses) {
            for (String client : data.clients) {
                System.out.println("x" + warehouse + client + " = unidades enviadas desde " + warehouse + " al Cliente " + client);
            }
        }
        printBlankLine();
    }

    private void printObjective(WarehouseData data) {
        List<String> objectiveTerms = new ArrayList<>();
        for (int i = 0; i < data.warehouses.length; i++) {
            objectiveTerms.add(data.fixedCost[i] + "y" + data.warehouses[i]);
        }
        for (int i = 0; i < data.warehouses.length; i++) {
            for (int j = 0; j < data.clients.length; j++) {
                objectiveTerms.add(data.transportCost[i][j] + "x" + data.warehouses[i] + data.clients[j]);
            }
        }
        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = " + joinTerms(objectiveTerms));
        printBlankLine();
    }

    private WarehouseModel buildModel(WarehouseData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] x = new Variable[data.warehouses.length][data.clients.length];
        Variable[] y = new Variable[data.warehouses.length];

        for (int i = 0; i < data.warehouses.length; i++) {
            y[i] = model.newVariable("y" + data.warehouses[i]).binary().weight(data.fixedCost[i]);
            for (int j = 0; j < data.clients.length; j++) {
                x[i][j] = model.newVariable("x" + data.warehouses[i] + data.clients[j]).lower(0).weight(data.transportCost[i][j]);
            }
        }

        for (int j = 0; j < data.clients.length; j++) {
            var expr = model.addExpression("Demanda_Cliente_" + data.clients[j]).level(data.demand[j]);
            for (int i = 0; i < data.warehouses.length; i++) {
                expr.set(x[i][j], 1);
            }
        }

        for (int i = 0; i < data.warehouses.length; i++) {
            var expr = model.addExpression("Capacidad_Bodega_" + data.warehouses[i]).upper(0);
            for (int j = 0; j < data.clients.length; j++) {
                expr.set(x[i][j], 1);
            }
            expr.set(y[i], -data.capacities[i]);
        }

        return new WarehouseModel(model, x, y);
    }

    private void printConstraints(WarehouseData data) {
        printSection("3) RESTRICCIONES");
        printSection("Demanda de los clientes:");
        for (int j = 0; j < data.clients.length; j++) {
            List<String> terms = new ArrayList<>();
            for (String warehouse : data.warehouses) {
                terms.add("x" + warehouse + data.clients[j]);
            }
            System.out.println(joinTerms(terms) + " = " + data.demand[j]);
        }
        printBlankLine();
        printSection("Capacidad de las bodegas:");
        for (int i = 0; i < data.warehouses.length; i++) {
            List<String> terms = new ArrayList<>();
            for (String client : data.clients) {
                terms.add("x" + data.warehouses[i] + client);
            }
            System.out.println(joinTerms(terms) + " <= " + data.capacities[i] + "y" + data.warehouses[i]);
        }
        printBlankLine();
        printSection("No negatividad:");
        System.out.println("xij >= 0");
        printBlankLine();
        printSection("Variables binarias:");
        System.out.println("yA, yB = 0 o 1");
        printBlankLine();
    }

    private void printSolution(WarehouseData data, WarehouseModel model) {
        Optimisation.Result result = model.model.minimise();
        printSection("4) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        printBlankLine();
        printSection("Valores óptimos de las variables:");
        for (int i = 0; i < data.warehouses.length; i++) {
            for (int j = 0; j < data.clients.length; j++) {
                System.out.println("x" + data.warehouses[i] + data.clients[j] + " = " + formatPythonFloat(model.x[i][j].getValue().doubleValue()));
            }
            printBlankLine();
        }
        for (int i = 0; i < data.warehouses.length; i++) {
            System.out.println("y" + data.warehouses[i] + " = " + formatPythonFloat(model.y[i].getValue().doubleValue()));
        }
        printBlankLine();

        printSection("Costo mínimo total:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()));
        printBlankLine();
        printWarehouseUsage(data, model);
        printSatisfiedDemand(data, model);
        printInterpretation(data, model);
    }

    private void printWarehouseUsage(WarehouseData data, WarehouseModel model) {
        printSection("Uso total de cada bodega:");
        for (int i = 0; i < data.warehouses.length; i++) {
            double used = 0.0;
            for (int j = 0; j < data.clients.length; j++) {
                used += model.x[i][j].getValue().doubleValue();
            }
            System.out.println("Bodega " + data.warehouses[i] + " = " + formatPythonFloat(used) + " unidades de " + data.capacities[i]);
        }
        printBlankLine();
    }

    private void printSatisfiedDemand(WarehouseData data, WarehouseModel model) {
        printSection("Demanda satisfecha de cada cliente:");
        for (int j = 0; j < data.clients.length; j++) {
            double satisfied = 0.0;
            for (int i = 0; i < data.warehouses.length; i++) {
                satisfied += model.x[i][j].getValue().doubleValue();
            }
            System.out.println("Cliente " + data.clients[j] + " = " + formatPythonFloat(satisfied) + " unidades");
        }
        printBlankLine();
    }

    private void printInterpretation(WarehouseData data, WarehouseModel model) {
        printSection("Interpretación de la solución:");
        for (int i = 0; i < data.warehouses.length; i++) {
            System.out.println((model.y[i].getValue().doubleValue() >= 0.5)
                    ? "Se abre la Bodega " + data.warehouses[i]
                    : "No se abre la Bodega " + data.warehouses[i]);
        }
        printBlankLine();
        for (int i = 0; i < data.warehouses.length; i++) {
            for (int j = 0; j < data.clients.length; j++) {
                System.out.println("Enviar " + formatPythonFloat(model.x[i][j].getValue().doubleValue()) + " unidades desde " + data.warehouses[i] + " al Cliente " + data.clients[j]);
            }
        }
    }
}
