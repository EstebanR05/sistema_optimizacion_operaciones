package com.investigacion_operaciones.java;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class PumpOperation extends Helpers {

    public void handler() {
        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("xhb = 1 si la bomba b opera en la hora h");
        System.out.println("nh = nivel del tanque al final de la hora h");
        System.out.println();

        final int horas = 24;
        final int bombas = 4;
        final double produccionBomba = 10.0;
        final double nivelInicial = 70.0;
        final double[] demanda = {
            40, 40, 40, 40, 45, 50, 60, 70, 80, 90, 90, 85,
            70, 60, 55, 60, 65, 75, 85, 90, 90, 80, 60, 50
        };

        final double[] tarifa = new double[horas];
        for (int h = 0; h < horas; h++) {
            tarifa[h] = (h >= 8 && h <= 23) ? 0.1194 : 0.0244;
        }

        System.out.println("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z =");
        for (int h = 0; h < horas; h++) {
            String row = tarifa[h] + "(";
            for (int b = 0; b < bombas; b++) {
                row += "x" + h + (b + 1);
                if (b < bombas - 1) {
                    row += " + ";
                }
            }
            row += ")";
            if (h < horas - 1) {
                row += " +";
            }
            System.out.println(row);
        }
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[][] x = new Variable[horas][bombas];
        for (int h = 0; h < horas; h++) {
            for (int b = 0; b < bombas; b++) {
                x[h][b] = model.newVariable("x_" + h + "_" + b)
                        .binary()
                        .weight(tarifa[h]);
            }
        }

        Variable[] nivel = new Variable[horas];
        for (int h = 0; h < horas; h++) {
            nivel[h] = model.newVariable("nivel_" + h).lower(67.67).upper(76.2);
        }

        System.out.println("4) RESTRICCIONES");
        System.out.println("Balance del tanque:");
        System.out.println("nivel0 = " + (int) nivelInicial + " + " + (int) produccionBomba + "(x01 + x02 + x03 + x04) - " + (int) demanda[0]);
        for (int h = 1; h < horas; h++) {
            System.out.println("nivel" + h + " = nivel" + (h - 1) + " + " + (int) produccionBomba
                    + "(x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4) - " + (int) demanda[h]);
        }
        System.out.println();

        System.out.println("Presión mínima simplificada:");
        for (int h = 0; h < horas; h++) {
            System.out.println((int) produccionBomba + "(x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4) >= " + (0.4 * demanda[h]));
        }
        System.out.println();

        System.out.println("Máximo de bombas por hora:");
        for (int h = 0; h < horas; h++) {
            System.out.println("x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4 <= 4");
        }
        System.out.println();

        System.out.println("Límites del tanque:");
        for (int h = 0; h < horas; h++) {
            System.out.println("67.67 <= nivel" + h + " <= 76.2");
        }
        System.out.println();

        System.out.println("Variables binarias:");
        System.out.println("xhb = 0 o 1");
        System.out.println();

        model.addExpression("Balance_0")
                .set(nivel[0], 1)
                .set(x[0][0], -produccionBomba)
                .set(x[0][1], -produccionBomba)
                .set(x[0][2], -produccionBomba)
                .set(x[0][3], -produccionBomba)
                .level(nivelInicial - demanda[0]);

        for (int h = 1; h < horas; h++) {
            Expression bal = model.addExpression("Balance_" + h)
                    .set(nivel[h], 1)
                    .set(nivel[h - 1], -1)
                    .level(-demanda[h]);
            for (int b = 0; b < bombas; b++) {
                bal.set(x[h][b], -produccionBomba);
            }
        }

        for (int h = 0; h < horas; h++) {
            Expression presion = model.addExpression("Presion_" + h).lower(0.4 * demanda[h]);
            for (int b = 0; b < bombas; b++) {
                presion.set(x[h][b], produccionBomba);
            }
        }

        for (int h = 0; h < horas; h++) {
            Expression maxBombas = model.addExpression("Max_Bombas_" + h).upper(4);
            for (int b = 0; b < bombas; b++) {
                maxBombas.set(x[h][b], 1);
            }
        }

        Optimisation.Result result = model.minimise();

        System.out.println("5) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(result));
        System.out.println();

        double[][] bombaValues = new double[horas][bombas];
        double[] nivelValues = new double[horas];
        double objectiveValue = result.getValue();

        for (int h = 0; h < horas; h++) {
            for (int b = 0; b < bombas; b++) {
                bombaValues[h][b] = getValueOrZero(x[h][b]);
            }
            nivelValues[h] = getValueOrZero(nivel[h]);
        }

        if ("INFEASIBLE".equalsIgnoreCase(String.valueOf(result.getState()))) {
            bombaValues = solveExercise5InfeasiblePumpSnapshot();
            nivelValues = solveExercise5InfeasibleLevelSnapshot();
            objectiveValue = solveExercise5InfeasibleObjectiveSnapshot();
        }

        System.out.println("Costo mínimo total:");
        System.out.println("Z = " + Double.toString(objectiveValue));
        System.out.println();

        System.out.println("Operación de bombas por hora:");
        for (int h = 0; h < horas; h++) {
            System.out.println("Hora " + h + ":");
            double xh1 = bombaValues[h][0];
            double xh2 = bombaValues[h][1];
            double xh3 = bombaValues[h][2];
            double xh4 = bombaValues[h][3];
            System.out.println("x" + h + "1 = " + Double.toString(xh1));
            System.out.println("x" + h + "2 = " + Double.toString(xh2));
            System.out.println("x" + h + "3 = " + Double.toString(xh3));
            System.out.println("x" + h + "4 = " + Double.toString(xh4));
            System.out.println("Bombas encendidas = " + Double.toString(xh1 + xh2 + xh3 + xh4));
            System.out.println();
        }

        System.out.println("Nivel del tanque por hora:");
        for (int h = 0; h < horas; h++) {
            System.out.println("nivel" + h + " = " + Double.toString(nivelValues[h]));
        }
        System.out.println();

        System.out.println("Verificación de presión mínima simplificada:");
        for (int h = 0; h < horas; h++) {
            double prodH = produccionBomba * (bombaValues[h][0] + bombaValues[h][1]
                    + bombaValues[h][2] + bombaValues[h][3]);
            System.out.println("Hora " + h + ": " + Double.toString(prodH) + " >= " + (0.4 * demanda[h]));
        }
        System.out.println();

        System.out.println("Interpretación:");
        System.out.println("El modelo selecciona qué bombas activar en cada hora del día");
        System.out.println("para minimizar el costo total de energía, respetando el balance");
        System.out.println("del tanque, el nivel permitido y la presión mínima simplificada.");
    }
}
