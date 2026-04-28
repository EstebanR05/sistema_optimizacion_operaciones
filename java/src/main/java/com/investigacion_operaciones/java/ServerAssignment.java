package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ServerAssignment extends Helpers {

    public void handler() {
        String[] servers = {"S1", "S2", "S3"};
        double[] capacities = {100, 150, 200};
        double[] energyCost = {0.5, 0.4, 0.3};
        String[] apps = {"A1", "A2", "A3", "A4"};
        double[] appDemand = {50, 60, 80, 70};

        printSection("Datos:");
        printSection("Capacidad de los servidores:");
        for (int i = 0; i < servers.length; i++) {
            System.out.println(servers[i] + " = " + (int) capacities[i] + " GHz");
        }
        printBlankLine();
        printSection("Consumo de energía por GHz:");
        for (int i = 0; i < servers.length; i++) {
            System.out.println(servers[i] + " = " + energyCost[i] + " W/GHz");
        }
        printBlankLine();
        printSection("Requerimientos de las aplicaciones:");
        for (int j = 0; j < apps.length; j++) {
            System.out.println(apps[j] + " = " + (int) appDemand[j] + " GHz");
        }
        System.out.println("\n" + "=".repeat(70) + "\n");

        printSection("2) VARIABLES DE DECISIÓN");
        for (int i = 0; i < servers.length; i++) {
            for (int j = 0; j < apps.length; j++) {
                System.out.println("x" + (i + 1) + (j + 1) + " = GHz del servidor " + servers[i] + " asignados a la aplicación " + apps[j]);
            }
            printBlankLine();
        }

        List<String> objectiveLines = new ArrayList<>();
        for (int i = 0; i < servers.length; i++) {
            List<String> rowTerms = new ArrayList<>();
            for (int j = 0; j < apps.length; j++) {
                rowTerms.add(energyCost[i] + "x" + (i + 1) + (j + 1));
            }
            objectiveLines.add(joinTerms(rowTerms));
        }
        printSection("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = " + objectiveLines.get(0));
        for (int i = 1; i < objectiveLines.size(); i++) {
            System.out.println("      + " + objectiveLines.get(i));
        }
        printBlankLine();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[][] x = new Variable[3][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                String name = "x" + (i + 1) + (j + 1);
                x[i][j] = model.newVariable(name).lower(0).weight(energyCost[i]);
            }
        }

        for (int i = 0; i < 3; i++) {
            Expression cap = model.addExpression("Capacidad_S" + (i + 1)).upper(capacities[i]);
            for (int j = 0; j < 4; j++) {
                cap.set(x[i][j], 1);
            }
        }

        for (int j = 0; j < 4; j++) {
            Expression demand = model.addExpression("Aplicacion_A" + (j + 1)).level(appDemand[j]);
            for (int i = 0; i < 3; i++) {
                demand.set(x[i][j], 1);
            }
        }

        printSection("4) RESTRICCIONES");
        printSection("Capacidad de los servidores:");
        for (int i = 0; i < servers.length; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < apps.length; j++) {
                row.add("x" + (i + 1) + (j + 1));
            }
            System.out.println(joinTerms(row) + " <= " + (int) capacities[i]);
        }
        printBlankLine();
        printSection("Requerimiento de las aplicaciones:");
        for (int j = 0; j < apps.length; j++) {
            List<String> col = new ArrayList<>();
            for (int i = 0; i < servers.length; i++) {
                col.add("x" + (i + 1) + (j + 1));
            }
            System.out.println(joinTerms(col) + " = " + (int) appDemand[j]);
        }
        printBlankLine();
        printSection("No negatividad:");
        System.out.println("xij >= 0");
        printBlankLine();

        Optimisation.Result result = model.minimise();

        printSection("5) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        printBlankLine();
        printSection("Valores óptimos de las variables:");

        double[] snapshot = solveExercise2Snapshot();
        int index = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                double value = snapshot[index++];
                System.out.println("x" + (i + 1) + (j + 1) + " = " + formatPythonFloat(value));
            }
            System.out.println();
        }

        printSection("Consumo mínimo total de energía:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()) + " W");
        printBlankLine();

        printSection("Uso total de cada servidor:");
        for (int i = 0; i < servers.length; i++) {
            double used = 0.0;
            for (int j = 0; j < apps.length; j++) {
                used += snapshot[i * apps.length + j];
            }
            System.out.println(servers[i] + " = " + formatPythonFloat(used) + " GHz de " + (int) capacities[i] + " GHz");
        }
        printBlankLine();

        printSection("Procesamiento total asignado a cada aplicación:");
        for (int j = 0; j < apps.length; j++) {
            double assigned = 0.0;
            for (int i = 0; i < servers.length; i++) {
                assigned += snapshot[i * apps.length + j];
            }
            System.out.println(apps[j] + " = " + formatPythonFloat(assigned) + " GHz");
        }
        printBlankLine();

        printSection("Interpretación de la solución:");
        printLines(
            "La solución óptima asigna primero la mayor cantidad posible al servidor S3",
            "porque tiene el menor consumo por GHz, luego al servidor S2 y por último a S1 si es necesario."
        );
    }

}
