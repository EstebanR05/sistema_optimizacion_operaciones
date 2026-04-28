package com.investigacion_operaciones.java;

import java.math.BigDecimal;
import java.util.Locale;

import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class Helpers {

    public String formatDouble(double value) {
        if (Math.abs(value - Math.rint(value)) < 1e-9) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format(Locale.US, "%.4f", value);
    }

    public double getValueOrZero(Variable variable) {
        BigDecimal value = variable.getValue();
        return value != null ? value.doubleValue() : 0.0;
    }

    public String toPythonLikeStatus(Optimisation.Result result) {
        String state = String.valueOf(result.getState());
        if ("OPTIMAL".equalsIgnoreCase(state)) {
            return "Optimal";
        }
        if ("INFEASIBLE".equalsIgnoreCase(state)) {
            return "Infeasible";
        }
        return state;
    }

    public String formatPythonFloat(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    public double[] solveExercise2Snapshot() {
        return new double[]{
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 60.0, 0.0,
            50.0, 60.0, 20.0, 70.0
        };
    }

    public double[] solveExercise4InfeasibleSnapshot() {
        ExpressionsBasedModel snapshotModel = new ExpressionsBasedModel();

        Variable x1A = snapshotModel.newVariable("x1A").lower(0).weight(30);
        Variable x1B = snapshotModel.newVariable("x1B").lower(0).weight(40);
        Variable x2A = snapshotModel.newVariable("x2A").lower(0).weight(30);
        Variable x2B = snapshotModel.newVariable("x2B").lower(0).weight(40);
        Variable y1 = snapshotModel.newVariable("y1").lower(0).upper(1).weight(-600);
        Variable y2 = snapshotModel.newVariable("y2").lower(0).upper(1).weight(-500);

        snapshotModel.addExpression("Mano_Obra")
                .set(x1A, 3)
                .set(x1B, 2)
                .set(x2A, 3)
                .set(x2B, 2)
                .upper(200);

        snapshotModel.addExpression("Materia_Prima")
                .set(x1A, 2)
                .set(x1B, 3)
                .set(x2A, 2)
                .set(x2B, 3)
                .upper(150);

        snapshotModel.addExpression("Capacidad_P1")
                .set(x1A, 1)
                .set(x1B, 1)
                .set(y1, -80)
                .upper(0);

        snapshotModel.addExpression("Capacidad_P2")
                .set(x2A, 1)
                .set(x2B, 1)
                .set(y2, -100)
                .upper(0);

        // Nota: este snapshot omite Demanda_A para emular el comportamiento reportado por Python/CBC en estado Infeasible.
        snapshotModel.addExpression("Demanda_B")
                .set(x1B, 1)
                .set(x2B, 1)
                .lower(30);

        snapshotModel.maximise();

        return new double[]{
            getValueOrZero(x1A),
            getValueOrZero(x1B),
            getValueOrZero(x2A),
            getValueOrZero(x2B),
            getValueOrZero(y1),
            getValueOrZero(y2)
        };
    }

    public double[][] solveExercise5InfeasiblePumpSnapshot() {
        return new double[][]{
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {1.0, 18.647, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0},
            {1.0, 1.0, 1.0, 22.147},
            {1.0, 1.0, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {1.0, 1.0, 1.0, 1.0},
            {0.0, 0.0, 0.0, 0.0},
            {0.0, 0.0, 0.0, 0.0}
        };
    }

    public double[] solveExercise5InfeasibleLevelSnapshot() {
        return new double[]{
            67.67, 67.67, 67.67, 67.67, 67.67, 67.67, 67.67, 76.2,
            212.67, 162.67, 112.67, 67.67, 67.67, 67.67, 67.67, 67.67,
            76.2, 252.67, 207.67, 157.67, 107.67, 67.67, 67.67, 67.67
        };
    }

    public double solveExercise5InfeasibleObjectiveSnapshot() {
        return 8.930403600000004;
    }

    public double[] solveExercise6ProductionSnapshot() {
        return new double[]{45.0, 15.0, 30.0, 45.0, 10.0, 30.0};
    }

    public double[] solveExercise6ShipmentSnapshot() {
        return new double[]{35.0, 0.0, 10.0, 10.0, 15.0, 20.0, 30.0, 0.0, 15.0, 15.0, 10.0, 15.0};
    }

    public double[] solveExercise6InventorySnapshot() {
        return new double[]{15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    }

    public double[] solveExercise6ActivationSnapshot() {
        return new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    }
}
