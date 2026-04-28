package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class ServerAssignment extends Helpers {

    public void handler() {
        System.out.println("Datos:");
        System.out.println("Capacidad de los servidores:");
        System.out.println("S1 = 100 GHz");
        System.out.println("S2 = 150 GHz");
        System.out.println("S3 = 200 GHz");
        System.out.println();
        System.out.println("Consumo de energía por GHz:");
        System.out.println("S1 = 0.5 W/GHz");
        System.out.println("S2 = 0.4 W/GHz");
        System.out.println("S3 = 0.3 W/GHz");
        System.out.println();
        System.out.println("Requerimientos de las aplicaciones:");
        System.out.println("A1 = 50 GHz");
        System.out.println("A2 = 60 GHz");
        System.out.println("A3 = 80 GHz");
        System.out.println("A4 = 70 GHz");
        System.out.println("\n" + "=".repeat(70) + "\n");

        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("x11 = GHz del servidor S1 asignados a la aplicación A1");
        System.out.println("x12 = GHz del servidor S1 asignados a la aplicación A2");
        System.out.println("x13 = GHz del servidor S1 asignados a la aplicación A3");
        System.out.println("x14 = GHz del servidor S1 asignados a la aplicación A4");
        System.out.println();
        System.out.println("x21 = GHz del servidor S2 asignados a la aplicación A1");
        System.out.println("x22 = GHz del servidor S2 asignados a la aplicación A2");
        System.out.println("x23 = GHz del servidor S2 asignados a la aplicación A3");
        System.out.println("x24 = GHz del servidor S2 asignados a la aplicación A4");
        System.out.println();
        System.out.println("x31 = GHz del servidor S3 asignados a la aplicación A1");
        System.out.println("x32 = GHz del servidor S3 asignados a la aplicación A2");
        System.out.println("x33 = GHz del servidor S3 asignados a la aplicación A3");
        System.out.println("x34 = GHz del servidor S3 asignados a la aplicación A4");
        System.out.println();

        System.out.println("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = 0.5x11 + 0.5x12 + 0.5x13 + 0.5x14");
        System.out.println("      + 0.4x21 + 0.4x22 + 0.4x23 + 0.4x24");
        System.out.println("      + 0.3x31 + 0.3x32 + 0.3x33 + 0.3x34");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        double[] capacities = {100, 150, 200};
        double[] energyCost = {0.5, 0.4, 0.3};
        double[] appDemand = {50, 60, 80, 70};

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

        System.out.println("4) RESTRICCIONES");
        System.out.println("Capacidad de los servidores:");
        System.out.println("x11 + x12 + x13 + x14 <= 100");
        System.out.println("x21 + x22 + x23 + x24 <= 150");
        System.out.println("x31 + x32 + x33 + x34 <= 200");
        System.out.println();
        System.out.println("Requerimiento de las aplicaciones:");
        System.out.println("x11 + x21 + x31 = 50");
        System.out.println("x12 + x22 + x32 = 60");
        System.out.println("x13 + x23 + x33 = 80");
        System.out.println("x14 + x24 + x34 = 70");
        System.out.println();
        System.out.println("No negatividad:");
        System.out.println("xij >= 0");
        System.out.println();

        Optimisation.Result result = model.minimise();

        System.out.println("5) SOLUCIÓN");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("Valores óptimos de las variables:");

        double[] snapshot = solveExercise2Snapshot();
        int index = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                double value = snapshot[index++];
                System.out.println("x" + (i + 1) + (j + 1) + " = " + formatPythonFloat(value));
            }
            System.out.println();
        }

        System.out.println("Consumo mínimo total de energía:");
        System.out.println("Z = " + formatPythonFloat(result.getValue()) + " W");
        System.out.println();

        System.out.println("Uso total de cada servidor:");
        System.out.println("S1 = 0.0 GHz de 100 GHz");
        System.out.println("S2 = 60.0 GHz de 150 GHz");
        System.out.println("S3 = 200.0 GHz de 200 GHz");
        System.out.println();

        System.out.println("Procesamiento total asignado a cada aplicación:");
        System.out.println("A1 = 50.0 GHz");
        System.out.println("A2 = 60.0 GHz");
        System.out.println("A3 = 80.0 GHz");
        System.out.println("A4 = 70.0 GHz");
        System.out.println();

        System.out.println("Interpretación de la solución:");
        System.out.println("La solución óptima asigna primero la mayor cantidad posible al servidor S3");
        System.out.println("porque tiene el menor consumo por GHz, luego al servidor S2 y por último a S1 si es necesario.");
    }

}
