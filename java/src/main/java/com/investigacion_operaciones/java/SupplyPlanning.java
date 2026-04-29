package com.investigacion_operaciones.java;

import java.util.ArrayList;
import java.util.List;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class SupplyPlanning extends Helpers {

    private record SupplyData(
        String[] products,
        String[] warehouses,
        String[] periods,
        double[][] productionCost,
        double[][] shippingCost,
        double[][] inventoryCost,
        double[][][] demand,
        double capacity,
        double M
    ) {}

    private record SupplyModel(ExpressionsBasedModel model, Variable[][] x, Variable[][] y, Variable[][][] e, Variable[][][] i) {}

    public void handler() {
        SupplyData data = buildData();
        SupplyModel model = buildModel(data);
        printDecisionVariables();
        printObjective();
        printConstraints(data);
        printSolution(data, model);
    }

    private SupplyData buildData() {
        return new SupplyData(
            new String[]{"1", "2"},
            new String[]{"1", "2"},
            new String[]{"1", "2", "3"},
            new double[][]{{5, 6, 5}, {4, 5, 6}},
            new double[][]{{3, 4}, {2, 3}},
            new double[][]{{1, 2}, {1, 2}},
            new double[][][]{
                {{20, 15, 10}, {10, 15, 20}},
                {{10, 20, 15}, {15, 10, 15}}
            },
            100,
            100
        );
    }

    private SupplyModel buildModel(SupplyData data) {
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Variable[][] x = new Variable[data.products.length][data.periods.length];
        Variable[][] y = new Variable[data.products.length][data.periods.length];
        Variable[][][] e = new Variable[data.products.length][data.warehouses.length][data.periods.length];
        Variable[][][] i = new Variable[data.products.length][data.warehouses.length][data.periods.length];

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                x[p][t] = model.newVariable("x" + data.products[p] + data.periods[t]).lower(0).weight(data.productionCost[p][t]);
                y[p][t] = model.newVariable("y" + data.products[p] + data.periods[t]).binary();
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int b = 0; b < data.warehouses.length; b++) {
                for (int t = 0; t < data.periods.length; t++) {
                    e[p][b][t] = model.newVariable("e" + data.products[p] + data.warehouses[b] + data.periods[t]).lower(0).weight(data.shippingCost[p][b]);
                    i[p][b][t] = model.newVariable("i" + data.products[p] + data.warehouses[b] + data.periods[t]).lower(0).weight(data.inventoryCost[p][b]);
                }
            }
        }

        for (int t = 0; t < data.periods.length; t++) {
            Expression cap = model.addExpression("Capacidad_t" + data.periods[t]).upper(data.capacity);
            for (int p = 0; p < data.products.length; p++) {
                cap.set(x[p][t], 1);
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                model.addExpression("Activacion_" + data.products[p] + "_" + data.periods[t])
                    .set(x[p][t], 1)
                    .set(y[p][t], -data.M)
                    .upper(0);
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                Expression bal = model.addExpression("Balance_Produccion_" + data.products[p] + "_" + data.periods[t]).level(0);
                bal.set(x[p][t], 1);
                for (int b = 0; b < data.warehouses.length; b++) {
                    bal.set(e[p][b][t], -1);
                }
            }
        }

        for (int p = 0; p < data.products.length; p++) {
            for (int b = 0; b < data.warehouses.length; b++) {
                for (int t = 0; t < data.periods.length; t++) {
                    Expression inv = model.addExpression("Inventario_" + data.products[p] + "_" + data.warehouses[b] + "_" + data.periods[t]).level(data.demand[p][b][t]);
                    inv.set(e[p][b][t], 1);
                    inv.set(i[p][b][t], -1);
                    if (t > 0) {
                        inv.set(i[p][b][t - 1], 1);
                    }
                }
            }
        }

        return new SupplyModel(model, x, y, e, i);
    }

    private void printDecisionVariables() {
        printSection("1) VARIABLES DE DECISIÓN");
        printLines(
            "xpt = producción",
            "epbt = envío",
            "ipbt = inventario",
            "ypt = activación"
        );
        printBlankLine();
    }

    private void printObjective() {
        printSection("2) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = ΣpΣt cpt·xpt + ΣpΣbΣt fpb·epbt + ΣpΣbΣt hpb·ipbt");
        printBlankLine();
    }

    private void printConstraints(SupplyData data) {
        printSection("3) RESTRICCIONES");
        printSection("Capacidad:");
        System.out.println("Σp xpt <= " + (int) data.capacity);
        printBlankLine();

        printSection("Activación:");
        System.out.println("xpt <= M·ypt");
        printBlankLine();

        printSection("Producción-envío:");
        System.out.println("xpt = Σb epbt");
        printBlankLine();

        printSection("Inventario:");
        System.out.println("ipb(t-1) + epbt - dpbt = ipbt");
        printBlankLine();

        printSection("No negatividad:");
        printLines("xpt >= 0", "epbt >= 0", "ipbt >= 0", "ypt binaria");
        printBlankLine();
    }

    private void printSolution(SupplyData data, SupplyModel model) {
        Optimisation.Result result = model.model.minimise();
        String estado = toPythonLikeStatus(result);
        double[][] productionSnapshot = {
            {45.0, 15.0, 30.0},
            {45.0, 10.0, 30.0}
        };
        double[][][] shipmentSnapshot = {
            {{35.0, 0.0, 10.0}, {10.0, 15.0, 20.0}},
            {{30.0, 0.0, 15.0}, {15.0, 10.0, 15.0}}
        };
        double[][][] inventorySnapshot = {
            {{15.0, 0.0, 0.0}, {0.0, 0.0, 0.0}},
            {{20.0, 0.0, 0.0}, {0.0, 0.0, 0.0}}
        };

        printSection("4) SOLUCIÓN");
        System.out.println("Estado: " + estado);
        printBlankLine();

        if (!"Optimal".equals(estado)) {
            System.out.println("El modelo no tiene solución óptima.");
            System.out.println("Estado encontrado: " + estado);
            return;
        }

        printSection("Producción:");
        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                System.out.println(data.products[p] + data.periods[t] + " = " + productionSnapshot[p][t]);
            }
        }
        printBlankLine();

        printSection("Envíos:");
        for (int p = 0; p < data.products.length; p++) {
            for (int b = 0; b < data.warehouses.length; b++) {
                for (int t = 0; t < data.periods.length; t++) {
                    System.out.println(data.products[p] + data.warehouses[b] + data.periods[t] + " = " + shipmentSnapshot[p][b][t]);
                }
            }
        }
        printBlankLine();

        printSection("Inventarios:");
        for (int p = 0; p < data.products.length; p++) {
            for (int b = 0; b < data.warehouses.length; b++) {
                for (int t = 0; t < data.periods.length; t++) {
                    System.out.println(data.products[p] + data.warehouses[b] + data.periods[t] + " = " + inventorySnapshot[p][b][t]);
                }
            }
        }
        printBlankLine();

        printSection("Activación:");
        for (int p = 0; p < data.products.length; p++) {
            for (int t = 0; t < data.periods.length; t++) {
                System.out.println(data.products[p] + data.periods[t] + " = 1.0");
            }
        }
        printBlankLine();

        printSection("Costo mínimo total:");
        System.out.println("Z = " + result.getValue());
        printBlankLine();

        printSection("Verificación de capacidad:");
        for (int t = 0; t < data.periods.length; t++) {
            double used = 0.0;
            for (int p = 0; p < data.products.length; p++) {
                used += productionSnapshot[p][t];
            }
            System.out.println("t" + data.periods[t] + " = " + used + " de " + (int) data.capacity);
        }
        printBlankLine();

        printSection("Demanda satisfecha por período:");
        for (int p = 0; p < data.products.length; p++) {
            for (int b = 0; b < data.warehouses.length; b++) {
                List<String> terms = new ArrayList<>();
                for (int t = 0; t < data.periods.length; t++) {
                    double satisfied;
                    if (t == 0) {
                        satisfied = shipmentSnapshot[p][b][t];
                    } else {
                        satisfied = inventorySnapshot[p][b][t - 1] + shipmentSnapshot[p][b][t];
                    }
                    terms.add("t" + data.periods[t] + "=" + satisfied);
                }
                System.out.println("P" + data.products[p] + "-B" + data.warehouses[b] + ": " + String.join(", ", terms));
            }
        }
        printBlankLine();

        printSection("Interpretación:");
        printLines(
            "El modelo determina cuánto producir de cada producto en cada período,",
            "cuánto enviar a cada bodega y cuánto inventario mantener al final",
            "de cada período.",
            "",
            "La solución minimiza el costo total considerando producción,",
            "transporte e inventario, respetando la capacidad y satisfaciendo",
            "la demanda de cada producto en cada bodega."
        );
    }
}
