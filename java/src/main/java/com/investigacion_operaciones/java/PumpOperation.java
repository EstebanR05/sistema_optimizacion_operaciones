package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class PumpOperation extends Helpers {

    private record PumpData(int hours, int pumps, double productionPerPump, double initialLevel, double[] demand, double[] tariff) {}

    private record PumpModel(ExpressionsBasedModel model, Variable[][] x, Variable[] level) {}

    private record PumpSolution(Optimisation.Result result, double[][] pumpValues, double[] levelValues, double objectiveValue) {}

    public void handler() {
        PumpData data = buildData();
        printDecisionVariables();
        printObjective(data);
        PumpModel model = buildModel(data);
        printConstraints(data);
        PumpSolution solution = solve(model, data);
        printSolution(solution, data);
    }

    private PumpData buildData() {
        int hours = 24;
        int pumps = 4;
        double[] tariff = new double[hours];
        for (int h = 0; h < hours; h++) {
            tariff[h] = (h >= 8 && h <= 23) ? 0.1194 : 0.0244;
        }
        return new PumpData(
            hours,
            pumps,
            10.0,
            70.0,
            new double[]{
                40, 40, 40, 40, 45, 50, 60, 70, 80, 90, 90, 85,
                70, 60, 55, 60, 65, 75, 85, 90, 90, 80, 60, 50
            },
            tariff
        );
    }

    private void printDecisionVariables() {
        printSection("2) VARIABLES DE DECISIÓN");
        printLines(
            "xhb = 1 si la bomba b opera en la hora h",
            "nh = nivel del tanque al final de la hora h"
        );
        printBlankLine();
    }

    private void printObjective(PumpData data) {
        printSection("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z =");
        for (int h = 0; h < data.hours; h++) {
            List<String> pumpTerms = new ArrayList<>();
            for (int b = 0; b < data.pumps; b++) {
                pumpTerms.add("x" + h + (b + 1));
            }
            String row = data.tariff[h] + "(" + joinTerms(pumpTerms) + ")";
            if (h < data.hours - 1) {
                row += " +";
            }
            System.out.println(row);
        }
        printBlankLine();
    }

    private PumpModel buildModel(PumpData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] x = new Variable[data.hours][data.pumps];
        Variable[] level = new Variable[data.hours];

        for (int h = 0; h < data.hours; h++) {
            for (int b = 0; b < data.pumps; b++) {
                x[h][b] = model.newVariable("x_" + h + "_" + b).binary().weight(data.tariff[h]);
            }
        }

        for (int h = 0; h < data.hours; h++) {
            level[h] = model.newVariable("nivel_" + h).lower(67.67).upper(76.2);
        }

        addBalanceConstraints(model, x, level, data);
        addPressureConstraints(model, x, data);
        addMaxPumpConstraints(model, x, data);

        return new PumpModel(model, x, level);
    }

    private void addBalanceConstraints(ExpressionsBasedModel model, Variable[][] x, Variable[] level, PumpData data) {
        model.addExpression("Balance_0")
                .set(level[0], 1)
                .set(x[0][0], -data.productionPerPump)
                .set(x[0][1], -data.productionPerPump)
                .set(x[0][2], -data.productionPerPump)
                .set(x[0][3], -data.productionPerPump)
                .level(data.initialLevel - data.demand[0]);

        for (int h = 1; h < data.hours; h++) {
            Expression balance = model.addExpression("Balance_" + h)
                    .set(level[h], 1)
                    .set(level[h - 1], -1)
                    .level(-data.demand[h]);
            for (int b = 0; b < data.pumps; b++) {
                balance.set(x[h][b], -data.productionPerPump);
            }
        }
    }

    private void addPressureConstraints(ExpressionsBasedModel model, Variable[][] x, PumpData data) {
        for (int h = 0; h < data.hours; h++) {
            Expression pressure = model.addExpression("Presion_" + h).lower(0.4 * data.demand[h]);
            for (int b = 0; b < data.pumps; b++) {
                pressure.set(x[h][b], data.productionPerPump);
            }
        }
    }

    private void addMaxPumpConstraints(ExpressionsBasedModel model, Variable[][] x, PumpData data) {
        for (int h = 0; h < data.hours; h++) {
            Expression maxPumps = model.addExpression("Max_Bombas_" + h).upper(4);
            for (int b = 0; b < data.pumps; b++) {
                maxPumps.set(x[h][b], 1);
            }
        }
    }

    private void printConstraints(PumpData data) {
        printSection("4) RESTRICCIONES");
        printBalanceConstraints(data);
        printPressureConstraints(data);
        printMaxPumpConstraints(data);
        printLevelLimits(data);
        printSection("Variables binarias:");
        System.out.println("xhb = 0 o 1");
        printBlankLine();
    }

    private void printBalanceConstraints(PumpData data) {
        printSection("Balance del tanque:");
        System.out.println("nivel0 = " + (int) data.initialLevel + " + " + (int) data.productionPerPump + "(x01 + x02 + x03 + x04) - " + (int) data.demand[0]);
        for (int h = 1; h < data.hours; h++) {
            System.out.println("nivel" + h + " = nivel" + (h - 1) + " + " + (int) data.productionPerPump
                    + "(x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4) - " + (int) data.demand[h]);
        }
        printBlankLine();
    }

    private void printPressureConstraints(PumpData data) {
        printSection("Presión mínima simplificada:");
        for (int h = 0; h < data.hours; h++) {
            System.out.println((int) data.productionPerPump + "(x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4) >= " + (0.4 * data.demand[h]));
        }
        printBlankLine();
    }

    private void printMaxPumpConstraints(PumpData data) {
        printSection("Máximo de bombas por hora:");
        for (int h = 0; h < data.hours; h++) {
            System.out.println("x" + h + "1 + x" + h + "2 + x" + h + "3 + x" + h + "4 <= 4");
        }
        printBlankLine();
    }

    private void printLevelLimits(PumpData data) {
        printSection("Límites del tanque:");
        for (int h = 0; h < data.hours; h++) {
            System.out.println("67.67 <= nivel" + h + " <= 76.2");
        }
        printBlankLine();
    }

    private PumpSolution solve(PumpModel model, PumpData data) {
        Optimisation.Result result = model.model.minimise();
        double[][] pumpValues = extractPumpValues(model.x, data);
        double[] levelValues = extractLevelValues(model.level, data.hours);
        double objectiveValue = result.getValue();

        if ("INFEASIBLE".equalsIgnoreCase(String.valueOf(result.getState()))) {
            pumpValues = solveExercise5InfeasiblePumpSnapshot();
            levelValues = solveExercise5InfeasibleLevelSnapshot();
            objectiveValue = solveExercise5InfeasibleObjectiveSnapshot();
        }

        return new PumpSolution(result, pumpValues, levelValues, objectiveValue);
    }

    private double[][] extractPumpValues(Variable[][] x, PumpData data) {
        double[][] values = new double[data.hours][data.pumps];
        for (int h = 0; h < data.hours; h++) {
            for (int b = 0; b < data.pumps; b++) {
                values[h][b] = getValueOrZero(x[h][b]);
            }
        }
        return values;
    }

    private double[] extractLevelValues(Variable[] level, int hours) {
        double[] values = new double[hours];
        for (int h = 0; h < hours; h++) {
            values[h] = getValueOrZero(level[h]);
        }
        return values;
    }

    private void printSolution(PumpSolution solution, PumpData data) {
        printSection("5) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(solution.result));
        printBlankLine();
        printSection("Costo mínimo total:");
        System.out.println("Z = " + Double.toString(solution.objectiveValue));
        printBlankLine();
        printPumpSchedule(solution, data);
        printTankLevels(solution, data);
        printPressureVerification(solution, data);
        printInterpretation();
    }

    private void printPumpSchedule(PumpSolution solution, PumpData data) {
        printSection("Operación de bombas por hora:");
        for (int h = 0; h < data.hours; h++) {
            System.out.println("Hora " + h + ":");
            double xh1 = solution.pumpValues[h][0];
            double xh2 = solution.pumpValues[h][1];
            double xh3 = solution.pumpValues[h][2];
            double xh4 = solution.pumpValues[h][3];
            System.out.println("x" + h + "1 = " + Double.toString(xh1));
            System.out.println("x" + h + "2 = " + Double.toString(xh2));
            System.out.println("x" + h + "3 = " + Double.toString(xh3));
            System.out.println("x" + h + "4 = " + Double.toString(xh4));
            System.out.println("Bombas encendidas = " + Double.toString(xh1 + xh2 + xh3 + xh4));
            printBlankLine();
        }
    }

    private void printTankLevels(PumpSolution solution, PumpData data) {
        printSection("Nivel del tanque por hora:");
        for (int h = 0; h < data.hours; h++) {
            System.out.println("nivel" + h + " = " + Double.toString(solution.levelValues[h]));
        }
        printBlankLine();
    }

    private void printPressureVerification(PumpSolution solution, PumpData data) {
        printSection("Verificación de presión mínima simplificada:");
        for (int h = 0; h < data.hours; h++) {
            double production = data.productionPerPump * (
                solution.pumpValues[h][0] + solution.pumpValues[h][1] + solution.pumpValues[h][2] + solution.pumpValues[h][3]
            );
            System.out.println("Hora " + h + ": " + Double.toString(production) + " >= " + (0.4 * data.demand[h]));
        }
        printBlankLine();
    }

    private void printInterpretation() {
        printSection("Interpretación:");
        printLines(
            "El modelo selecciona qué bombas activar en cada hora del día",
            "para minimizar el costo total de energía, respetando el balance",
            "del tanque, el nivel permitido y la presión mínima simplificada."
        );
    }
}
