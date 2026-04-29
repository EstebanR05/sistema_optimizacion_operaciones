package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class PumpOperation extends Helpers {

    private record InvData(
        String[] products,
        String[] periods,
        double[][] purchaseCost,
        double[] invCost,
        double[][] demand,
        double[] initialInv,
        double storageCapacity,
        double M
    ) {}

    private record InvModel(ExpressionsBasedModel model, Variable[][] q, Variable[][] i, Variable[][] y) {}

    public void handler() {
        InvData data = buildData();
        InvModel model = buildModel(data);
        printVariables();
        printObjective();
        printConstraints(data);
        printSolution(data, model);
    }

    private InvData buildData() {
        return new InvData(
            new String[]{"A", "B"},
            new String[]{"1", "2", "3"},
            new double[][]{{10, 12, 11}, {8, 9, 10}},
            new double[]{2, 3},
            new double[][]{{30, 40, 35}, {20, 25, 30}},
            new double[]{0, 0},
            100,
            200
        );
    }

    private InvModel buildModel(InvData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] q = new Variable[data.products.length][data.periods.length];
        Variable[][] i = new Variable[data.products.length][data.periods.length];
        Variable[][] y = new Variable[data.products.length][data.periods.length];

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                q[p][t] = model.newVariable("q" + data.products[p] + data.periods[t]).lower(0).weight(data.purchaseCost[p][t]);
                i[p][t] = model.newVariable("i" + data.products[p] + data.periods[t]).lower(0).weight(data.invCost[p]);
                y[p][t] = model.newVariable("y" + data.products[p] + data.periods[t]).binary();
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                Expression bal = model.addExpression("Balance_" + data.products[p] + "_t" + data.periods[t]).level(data.demand[p][t]);
                bal.set(q[p][t], 1);
                bal.set(i[p][t], -1);
                if (t == 0) {
                    bal.lower(data.demand[p][t]);
                    bal.upper(data.demand[p][t]);
                } else {
                    bal.set(i[p][t - 1], 1);
                }
            }
        }

        for (int t = 0; t < data.periods.length; t++) {
            Expression cap = model.addExpression("Capacidad_t" + data.periods[t]).upper(data.storageCapacity);
            for (int p = 0; p < data.products.length; p++) {
                cap.set(i[p][t], 1);
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                model.addExpression("Activacion_" + data.products[p] + "_t" + data.periods[t])
                    .set(q[p][t], 1)
                    .set(y[p][t], -data.M)
                    .upper(0);
            }
        }

        return new InvModel(model, q, i, y);
    }

    private void printVariables() {
        printSection("1) VARIABLES DE DECISIÓN");
        printLines(
            "qpt = cantidad comprada del producto p en el período t",
            "ipt = inventario del producto p al final del período t",
            "ypt = 1 si se realiza pedido del producto p en el período t"
        );
        printBlankLine();
    }

    private void printObjective() {
        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = ΣpΣt cpt·qpt + ΣpΣt hp·ipt");
        printBlankLine();
    }

    private void printConstraints(InvData data) {
        printSection("3) RESTRICCIONES");
        printSection("Balance de inventario:");
        System.out.println("i[p,t-1] + q[p,t] - d[p,t] = i[p,t]");
        printBlankLine();

        printSection("Capacidad de almacenamiento:");
        System.out.println("Σp i[p,t] <= " + (int) data.storageCapacity);
        printBlankLine();

        printSection("Activación de pedido:");
        System.out.println("q[p,t] <= M·y[p,t]");
        printBlankLine();

        printSection("No negatividad y binarias:");
        printLines("q[p,t] >= 0", "i[p,t] >= 0", "y[p,t] = 0 o 1");
        printBlankLine();
    }

    private void printSolution(InvData data, InvModel model) {
        Optimisation.Result result = model.model.minimise();
        String estado = toPythonLikeStatus(result);

        printSection("4) SOLUCIÓN");
        System.out.println("Estado: " + estado);
        printBlankLine();

        if (!"Optimal".equals(estado)) {
            System.out.println("El modelo no tiene solución óptima.");
            return;
        }

        printSection("Compras óptimas:");
        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                System.out.println("q" + data.products[p] + data.periods[t] + " = " + formatPythonFloat(getValueOrZero(model.q[p][t])));
            }
        }
        printBlankLine();

        printSection("Inventarios óptimos:");
        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                System.out.println("i" + data.products[p] + data.periods[t] + " = " + formatPythonFloat(getValueOrZero(model.i[p][t])));
            }
        }
        printBlankLine();

        printSection("Activación de pedidos:");
        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                System.out.println("y" + data.products[p] + data.periods[t] + " = 1.0");
            }
        }
        printBlankLine();

        printSection("Costo mínimo total:");
        System.out.println("Z = " + result.getValue());
        printBlankLine();

        printSection("Verificación de capacidad de almacenamiento:");
        for (int t = 0; t < data.periods.length; t++) {
            double used = 0.0;
            for (int p = 0; p < data.products.length; p++) {
                used += getValueOrZero(model.i[p][t]);
            }
            System.out.println("t" + data.periods[t] + " = " + formatPythonFloat(used) + " de " + (int) data.storageCapacity);
        }
    }
}
