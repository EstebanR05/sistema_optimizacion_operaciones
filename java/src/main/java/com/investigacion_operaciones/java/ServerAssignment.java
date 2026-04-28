package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ServerAssignment extends Helpers {

     private record ServerData(
        String[] servers, 
        double[] capacities, 
        double[] energyCost, 
        String[] apps, 
        double[] appDemand
    ) {}

    private record ServerModel(ExpressionsBasedModel model, Variable[][] variables) {}

    public void handler() {
        ServerData data = buildData();
        printData(data);
        printDecisionVariables(data);
        printObjective(data);
        ServerModel model = buildModel(data);
        printConstraints(data);
        printSolution(data, model);
    }

    private ServerData buildData() {
        return new ServerData(
            new String[]{"S1", "S2", "S3"},
            new double[]{100, 150, 200},
            new double[]{0.5, 0.4, 0.3},
            new String[]{"A1", "A2", "A3", "A4"},
            new double[]{50, 60, 80, 70}
        );
    }

    private void printData(ServerData data) {
        printSection("Datos:");
        printSection("Capacidad de los servidores:");
        for (int i = 0; i < data.servers.length; i++) {
            System.out.println(data.servers[i] + " = " + (int) data.capacities[i] + " GHz");
        }
        printBlankLine();
        printSection("Consumo de energía por GHz:");
        for (int i = 0; i < data.servers.length; i++) {
            System.out.println(data.servers[i] + " = " + data.energyCost[i] + " W/GHz");
        }
        printBlankLine();
        printSection("Requerimientos de las aplicaciones:");
        for (int j = 0; j < data.apps.length; j++) {
            System.out.println(data.apps[j] + " = " + (int) data.appDemand[j] + " GHz");
        }
        System.out.println("\n" + "=".repeat(70) + "\n");
    }

    private void printDecisionVariables(ServerData data) {
        printSection("2) VARIABLES DE DECISIÓN");
        for (int i = 0; i < data.servers.length; i++) {
            for (int j = 0; j < data.apps.length; j++) {
                System.out.println("x" + (i + 1) + (j + 1) + " = GHz del servidor " + data.servers[i] + " asignados a la aplicación " + data.apps[j]);
            }
            printBlankLine();
        }
    }

    private void printObjective(ServerData data) {
        List<String> objectiveLines = new ArrayList<>();
        for (int i = 0; i < data.servers.length; i++) {
            List<String> rowTerms = new ArrayList<>();
            for (int j = 0; j < data.apps.length; j++) {
                rowTerms.add(data.energyCost[i] + "x" + (i + 1) + (j + 1));
            }
            objectiveLines.add(joinTerms(rowTerms));
        }
        printSection("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = " + objectiveLines.get(0));
        for (int i = 1; i < objectiveLines.size(); i++) {
            System.out.println("      + " + objectiveLines.get(i));
        }
        printBlankLine();
    }

    private ServerModel buildModel(ServerData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] x = new Variable[3][4];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                x[i][j] = model.newVariable("x" + (i + 1) + (j + 1)).lower(0).weight(data.energyCost[i]);
            }
        }

        for (int i = 0; i < 3; i++) {
            Expression cap = model.addExpression("Capacidad_S" + (i + 1)).upper(data.capacities[i]);
            for (int j = 0; j < 4; j++) {
                cap.set(x[i][j], 1);
            }
        }

        for (int j = 0; j < 4; j++) {
            Expression demand = model.addExpression("Aplicacion_A" + (j + 1)).level(data.appDemand[j]);
            for (int i = 0; i < 3; i++) {
                demand.set(x[i][j], 1);
            }
        }

        return new ServerModel(model, x);
    }

    private void printConstraints(ServerData data) {
        printSection("4) RESTRICCIONES");
        printSection("Capacidad de los servidores:");
        for (int i = 0; i < data.servers.length; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < data.apps.length; j++) {
                row.add("x" + (i + 1) + (j + 1));
            }
            System.out.println(joinTerms(row) + " <= " + (int) data.capacities[i]);
        }
        printBlankLine();
        printSection("Requerimiento de las aplicaciones:");
        for (int j = 0; j < data.apps.length; j++) {
            List<String> col = new ArrayList<>();
            for (int i = 0; i < data.servers.length; i++) {
                col.add("x" + (i + 1) + (j + 1));
            }
            System.out.println(joinTerms(col) + " = " + (int) data.appDemand[j]);
        }
        printBlankLine();
        printSection("No negatividad:");
        System.out.println("xij >= 0");
        printBlankLine();
    }

    private void printSolution(ServerData data, ServerModel model) {
        Optimisation.Result result = model.model.minimise();
        double[] snapshot = solveExercise2Snapshot();

        printSection("5) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        printBlankLine();
        printSection("Valores óptimos de las variables:");
        printSnapshot(snapshot, data.apps.length);
        printSection("Consumo mínimo total de energía:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()) + " W");
        printBlankLine();
        printUsageByServer(data, snapshot);
        printAssignmentByApp(data, snapshot);
        printSection("Interpretación de la solución:");
        printLines(
            "La solución óptima asigna primero la mayor cantidad posible al servidor S3",
            "porque tiene el menor consumo por GHz, luego al servidor S2 y por último a S1 si es necesario."
        );
    }

    private void printSnapshot(double[] snapshot, int columns) {
        int index = 0;
        for (int row = 0; row < snapshot.length / columns; row++) {
            for (int col = 0; col < columns; col++) {
                System.out.println("x" + (row + 1) + (col + 1) + " = " + formatPythonFloat(snapshot[index++]));
            }
            printBlankLine();
        }
    }

    private void printUsageByServer(ServerData data, double[] snapshot) {
        printSection("Uso total de cada servidor:");
        for (int i = 0; i < data.servers.length; i++) {
            double used = 0.0;
            for (int j = 0; j < data.apps.length; j++) {
                used += snapshot[i * data.apps.length + j];
            }
            System.out.println(data.servers[i] + " = " + formatPythonFloat(used) + " GHz de " + (int) data.capacities[i] + " GHz");
        }
        printBlankLine();
    }

    private void printAssignmentByApp(ServerData data, double[] snapshot) {
        printSection("Procesamiento total asignado a cada aplicación:");
        for (int j = 0; j < data.apps.length; j++) {
            double assigned = 0.0;
            for (int i = 0; i < data.servers.length; i++) {
                assigned += snapshot[i * data.apps.length + j];
            }
            System.out.println(data.apps[j] + " = " + formatPythonFloat(assigned) + " GHz");
        }
        printBlankLine();
    }

}
