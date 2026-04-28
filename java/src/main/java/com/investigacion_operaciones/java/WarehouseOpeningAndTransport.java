package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class WarehouseOpeningAndTransport extends Helpers {

    public void handler() {
        String[] warehouses = {"A", "B"};
        int[] capacities = {150, 120};
        int[] fixedCost = {500, 400};
        String[] clients = {"1", "2", "3"};
        int[] demand = {80, 70, 60};
        int[][] transportCost = {
            {4, 6, 9},
            {5, 4, 7}
        };

        printSection("1) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades enviadas desde la bodega i al cliente j");
        for (String warehouse : warehouses) {
            System.out.println("y" + warehouse + " = 1 si se abre la Bodega " + warehouse + ", 0 en caso contrario");
        }
        printBlankLine();
        for (String warehouse : warehouses) {
            for (String client : clients) {
                System.out.println("x" + warehouse + client + " = unidades enviadas desde " + warehouse + " al Cliente " + client);
            }
        }
        printBlankLine();

        List<String> objectiveTerms = new ArrayList<>();
        for (int i = 0; i < warehouses.length; i++) {
            objectiveTerms.add(fixedCost[i] + "y" + warehouses[i]);
        }
        for (int i = 0; i < warehouses.length; i++) {
            for (int j = 0; j < clients.length; j++) {
                objectiveTerms.add(transportCost[i][j] + "x" + warehouses[i] + clients[j]);
            }
        }
        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = " + joinTerms(objectiveTerms));
        printBlankLine();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[][] x = new Variable[warehouses.length][clients.length];
        Variable[] y = new Variable[warehouses.length];

        for (int i = 0; i < warehouses.length; i++) {
            y[i] = model.newVariable("y" + warehouses[i]).binary().weight(fixedCost[i]);
            for (int j = 0; j < clients.length; j++) {
                x[i][j] = model.newVariable("x" + warehouses[i] + clients[j]).lower(0).weight(transportCost[i][j]);
            }
        }

        for (int j = 0; j < clients.length; j++) {
            var expr = model.addExpression("Demanda_Cliente_" + clients[j]).level(demand[j]);
            for (int i = 0; i < warehouses.length; i++) {
                expr.set(x[i][j], 1);
            }
        }

        for (int i = 0; i < warehouses.length; i++) {
            var expr = model.addExpression("Capacidad_Bodega_" + warehouses[i]).upper(0);
            for (int j = 0; j < clients.length; j++) {
                expr.set(x[i][j], 1);
            }
            expr.set(y[i], -capacities[i]);
        }

        printSection("3) RESTRICCIONES");
        printSection("Demanda de los clientes:");
        for (int j = 0; j < clients.length; j++) {
            List<String> terms = new ArrayList<>();
            for (String warehouse : warehouses) {
                terms.add("x" + warehouse + clients[j]);
            }
            System.out.println(joinTerms(terms) + " = " + demand[j]);
        }
        printBlankLine();
        printSection("Capacidad de las bodegas:");
        for (int i = 0; i < warehouses.length; i++) {
            List<String> terms = new ArrayList<>();
            for (String client : clients) {
                terms.add("x" + warehouses[i] + client);
            }
            System.out.println(joinTerms(terms) + " <= " + capacities[i] + "y" + warehouses[i]);
        }
        printBlankLine();
        printSection("No negatividad:");
        System.out.println("xij >= 0");
        printBlankLine();
        printSection("Variables binarias:");
        System.out.println("yA, yB = 0 o 1");
        printBlankLine();

        Optimisation.Result result = model.minimise();

        printSection("4) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        printBlankLine();
        printSection("Valores óptimos de las variables:");
        for (int i = 0; i < warehouses.length; i++) {
            for (int j = 0; j < clients.length; j++) {
                System.out.println("x" + warehouses[i] + clients[j] + " = " + formatPythonFloat(x[i][j].getValue().doubleValue()));
            }
            printBlankLine();
        }
        for (int i = 0; i < warehouses.length; i++) {
            System.out.println("y" + warehouses[i] + " = " + formatPythonFloat(y[i].getValue().doubleValue()));
        }
        printBlankLine();

        printSection("Costo mínimo total:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()));
        printBlankLine();

        printSection("Uso total de cada bodega:");
        for (int i = 0; i < warehouses.length; i++) {
            double used = 0.0;
            for (int j = 0; j < clients.length; j++) {
                used += x[i][j].getValue().doubleValue();
            }
            System.out.println("Bodega " + warehouses[i] + " = " + formatPythonFloat(used) + " unidades de " + capacities[i]);
        }
        printBlankLine();

        printSection("Demanda satisfecha de cada cliente:");
        for (int j = 0; j < clients.length; j++) {
            double satisfied = 0.0;
            for (int i = 0; i < warehouses.length; i++) {
                satisfied += x[i][j].getValue().doubleValue();
            }
            System.out.println("Cliente " + clients[j] + " = " + formatPythonFloat(satisfied) + " unidades");
        }
        printBlankLine();

        printSection("Interpretación de la solución:");
        for (int i = 0; i < warehouses.length; i++) {
            System.out.println((y[i].getValue().doubleValue() >= 0.5)
                    ? "Se abre la Bodega " + warehouses[i]
                    : "No se abre la Bodega " + warehouses[i]);
        }
        printBlankLine();
        for (int i = 0; i < warehouses.length; i++) {
            for (int j = 0; j < clients.length; j++) {
                System.out.println("Enviar " + formatPythonFloat(x[i][j].getValue().doubleValue()) + " unidades desde " + warehouses[i] + " al Cliente " + clients[j]);
            }
        }
    }
}
