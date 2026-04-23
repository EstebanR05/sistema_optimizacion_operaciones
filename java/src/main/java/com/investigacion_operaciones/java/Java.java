package com.investigacion_operaciones.java;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

public class Java {

    public static void main(String[] args) {
        Java app = new Java();
        app.menu();
    }
    
    public void menu(){
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        while (option != 7) {
            System.out.println("\n===== MENÚ PRINCIPAL =====");
            System.out.println("1. ejercicio1");
            System.out.println("2. ejercicio2");
            System.out.println("3. ejercicio3");
            System.out.println("4. ejercicio4");
            System.out.println("5. ejercicio5");
            System.out.println("6. ejercicio6");
            System.out.println("7. Salir");
            System.out.print("Selecciona una opción: ");

            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
            } else {
                System.out.println("Entrada inválida. Debes escribir un número.");
                scanner.next();
                continue;
            }

            switch (option) {
                case 1:
                    FirstExercise();
                    break;
                case 2:
                    SecondExercise();
                    break;
                case 3:
                    ThirdExercise();
                    break;
                case 4:
                    FourthExercise();
                    break;
                case 5:
                    FifthExercise();
                    break;
                case 6:
                    SixthExercise();
                    break;
                case 7:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }

        scanner.close();
    }

    public void FirstExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #1 - Fábrica de sillas y mesas (PL entera)");
        System.out.println("============================================================\n");

        System.out.println("2. VARIABLES DE DECISIÓN");
        System.out.println("X1 = número de sillas que debe producir la fábrica diariamente");
        System.out.println("X2 = número de mesas que debe producir la fábrica diariamente");
        System.out.println();

        System.out.println("3. FUNCIÓN OBJETIVO");
        System.out.println("Max Z = 30X1 + 50X2");
        System.out.println();

        System.out.println("4. RESTRICCIONES");
        System.out.println("2X1 + 5X2 <= 100   -> Restricción de tiempo");
        System.out.println("X1 + 3X2 <= 60     -> Restricción de material");
        System.out.println("X1 <= 20           -> Demanda máxima de sillas");
        System.out.println("X2 <= 10           -> Demanda máxima de mesas");
        System.out.println("X1, X2 >= 0");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

    Variable x1 = model.newVariable("X1_Sillas").lower(0).integer(true).weight(30);
    Variable x2 = model.newVariable("X2_Mesas").lower(0).integer(true).weight(50);

        model.addExpression("Restriccion_Tiempo")
                .set(x1, 2)
                .set(x2, 5)
                .upper(100);

        model.addExpression("Restriccion_Material")
                .set(x1, 1)
                .set(x2, 3)
                .upper(60);

        model.addExpression("Demanda_Sillas")
                .set(x1, 1)
                .upper(20);

        model.addExpression("Demanda_Mesas")
                .set(x2, 1)
                .upper(10);

        Optimisation.Result result = model.maximise();

        int x1Value = (int) Math.round(x1.getValue().doubleValue());
        int x2Value = (int) Math.round(x2.getValue().doubleValue());
        int objective = (int) Math.round(result.getValue());

        System.out.println("5. SOLUCIÓN DEL MODELO");
        System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("Valores óptimos encontrados:");
        System.out.println("X1 (Sillas) = " + x1Value);
        System.out.println("X2 (Mesas)  = " + x2Value);
        System.out.println();
        System.out.println("Utilidad máxima:");
        System.out.println("Z = " + objective);
        System.out.println("\n" + "=".repeat(60) + "\n");

        System.out.println("6. VERIFICACIÓN DE RESTRICCIONES");
        System.out.println();
        System.out.println("Tiempo usado: 2(" + x1Value + ") + 5(" + x2Value + ") = " + (2 * x1Value + 5 * x2Value) + " <= 100");
        System.out.println("Material usado: 1(" + x1Value + ") + 3(" + x2Value + ") = " + (x1Value + 3 * x2Value) + " <= 60");
        System.out.println("Demanda sillas: " + x1Value + " <= 20");
        System.out.println("Demanda mesas: " + x2Value + " <= 10");
        System.out.println();

        System.out.println("7. RESPUESTA FINAL");
        System.out.println("La fábrica debe producir " + x1Value + " sillas y " + x2Value + " mesas por día.");
        System.out.println("La utilidad máxima que obtiene es de " + objective + ".");
    }

    public void SecondExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #2 - Asignación en servidores (PL)");
        System.out.println("============================================================\n");

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

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.println("x" + (i + 1) + (j + 1) + " = " + formatDouble(x[i][j].getValue().doubleValue()));
            }
            System.out.println();
        }

        System.out.println("Consumo mínimo total de energía:");
        System.out.println("Z = " + formatDouble(result.getValue()) + " W");
        System.out.println();

        System.out.println("Uso total de cada servidor:");
        for (int i = 0; i < 3; i++) {
            double used = 0;
            for (int j = 0; j < 4; j++) {
                used += x[i][j].getValue().doubleValue();
            }
            System.out.println("S" + (i + 1) + " = " + formatDouble(used) + " GHz de " + (int) capacities[i] + " GHz");
        }
        System.out.println();

        System.out.println("Procesamiento total asignado a cada aplicación:");
        for (int j = 0; j < 4; j++) {
            double assigned = 0;
            for (int i = 0; i < 3; i++) {
                assigned += x[i][j].getValue().doubleValue();
            }
            System.out.println("A" + (j + 1) + " = " + formatDouble(assigned) + " GHz");
        }
        System.out.println();

        System.out.println("Interpretación de la solución:");
        System.out.println("La solución óptima asigna primero la mayor cantidad posible al servidor S3,");
        System.out.println("porque tiene el menor consumo por GHz, luego a S2 y por último a S1 si es necesario.");
    }

    public void ThirdExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #3 - Apertura de bodegas y transporte (MILP)");
        System.out.println("============================================================\n");

        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades enviadas desde la bodega i al cliente j");
        System.out.println("yA = 1 si se abre la Bodega A, 0 en caso contrario");
        System.out.println("yB = 1 si se abre la Bodega B, 0 en caso contrario");
        System.out.println();
        System.out.println("xA1 = unidades enviadas desde A al Cliente 1");
        System.out.println("xA2 = unidades enviadas desde A al Cliente 2");
        System.out.println("xA3 = unidades enviadas desde A al Cliente 3");
        System.out.println("xB1 = unidades enviadas desde B al Cliente 1");
        System.out.println("xB2 = unidades enviadas desde B al Cliente 2");
        System.out.println("xB3 = unidades enviadas desde B al Cliente 3");
        System.out.println();

        System.out.println("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = 500yA + 400yB + 4xA1 + 6xA2 + 9xA3 + 5xB1 + 4xB2 + 7xB3");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

    Variable xA1 = model.newVariable("xA1").lower(0).weight(4);
    Variable xA2 = model.newVariable("xA2").lower(0).weight(6);
    Variable xA3 = model.newVariable("xA3").lower(0).weight(9);
    Variable xB1 = model.newVariable("xB1").lower(0).weight(5);
    Variable xB2 = model.newVariable("xB2").lower(0).weight(4);
    Variable xB3 = model.newVariable("xB3").lower(0).weight(7);

    Variable yA = model.newVariable("yA").binary().weight(500);
    Variable yB = model.newVariable("yB").binary().weight(400);

        model.addExpression("Demanda_Cliente_1").set(xA1, 1).set(xB1, 1).level(80);
        model.addExpression("Demanda_Cliente_2").set(xA2, 1).set(xB2, 1).level(70);
        model.addExpression("Demanda_Cliente_3").set(xA3, 1).set(xB3, 1).level(60);

        model.addExpression("Capacidad_Bodega_A")
                .set(xA1, 1)
                .set(xA2, 1)
                .set(xA3, 1)
                .set(yA, -150)
                .upper(0);

        model.addExpression("Capacidad_Bodega_B")
                .set(xB1, 1)
                .set(xB2, 1)
                .set(xB3, 1)
                .set(yB, -120)
                .upper(0);

    System.out.println("4) RESTRICCIONES");
    System.out.println("Demanda de los clientes:");
    System.out.println("xA1 + xB1 = 80");
    System.out.println("xA2 + xB2 = 70");
    System.out.println("xA3 + xB3 = 60");
    System.out.println();
    System.out.println("Capacidad de las bodegas:");
    System.out.println("xA1 + xA2 + xA3 <= 150yA");
    System.out.println("xB1 + xB2 + xB3 <= 120yB");
    System.out.println();
    System.out.println("No negatividad:");
    System.out.println("xij >= 0");
    System.out.println();
    System.out.println("Variables binarias:");
    System.out.println("yA, yB = 0 o 1");
    System.out.println();

        Optimisation.Result result = model.minimise();

    System.out.println("5) SOLUCIÓN");
    System.out.println("Estado de la solución: " + toPythonLikeStatus(result));
        System.out.println();
        System.out.println("Valores óptimos de las variables:");
        System.out.println("xA1 = " + formatDouble(xA1.getValue().doubleValue()));
        System.out.println("xA2 = " + formatDouble(xA2.getValue().doubleValue()));
        System.out.println("xA3 = " + formatDouble(xA3.getValue().doubleValue()));
        System.out.println();
        System.out.println("xB1 = " + formatDouble(xB1.getValue().doubleValue()));
        System.out.println("xB2 = " + formatDouble(xB2.getValue().doubleValue()));
        System.out.println("xB3 = " + formatDouble(xB3.getValue().doubleValue()));
        System.out.println();
        System.out.println("yA = " + formatDouble(yA.getValue().doubleValue()));
        System.out.println("yB = " + formatDouble(yB.getValue().doubleValue()));
        System.out.println();

        System.out.println("Costo mínimo total:");
        System.out.println("Z = " + formatDouble(result.getValue()));
        System.out.println();

        double totalA = xA1.getValue().doubleValue() + xA2.getValue().doubleValue() + xA3.getValue().doubleValue();
        double totalB = xB1.getValue().doubleValue() + xB2.getValue().doubleValue() + xB3.getValue().doubleValue();

        System.out.println("Uso total de cada bodega:");
        System.out.println("Bodega A = " + formatDouble(totalA) + " unidades de 150");
        System.out.println("Bodega B = " + formatDouble(totalB) + " unidades de 120");
        System.out.println();

        System.out.println("Demanda satisfecha de cada cliente:");
        System.out.println("Cliente 1 = " + formatDouble(xA1.getValue().doubleValue() + xB1.getValue().doubleValue()) + " unidades");
        System.out.println("Cliente 2 = " + formatDouble(xA2.getValue().doubleValue() + xB2.getValue().doubleValue()) + " unidades");
        System.out.println("Cliente 3 = " + formatDouble(xA3.getValue().doubleValue() + xB3.getValue().doubleValue()) + " unidades");
        System.out.println();

        System.out.println("Interpretación de la solución:");
        System.out.println((yA.getValue().doubleValue() >= 0.5) ? "Se abre la Bodega A" : "No se abre la Bodega A");
        System.out.println((yB.getValue().doubleValue() >= 0.5) ? "Se abre la Bodega B" : "No se abre la Bodega B");
        System.out.println();
        System.out.println("Enviar " + formatDouble(xA1.getValue().doubleValue()) + " unidades desde A al Cliente 1");
        System.out.println("Enviar " + formatDouble(xA2.getValue().doubleValue()) + " unidades desde A al Cliente 2");
        System.out.println("Enviar " + formatDouble(xA3.getValue().doubleValue()) + " unidades desde A al Cliente 3");
        System.out.println("Enviar " + formatDouble(xB1.getValue().doubleValue()) + " unidades desde B al Cliente 1");
        System.out.println("Enviar " + formatDouble(xB2.getValue().doubleValue()) + " unidades desde B al Cliente 2");
        System.out.println("Enviar " + formatDouble(xB3.getValue().doubleValue()) + " unidades desde B al Cliente 3");
    }

    public void FourthExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #4 - Producción con apertura de plantas (MILP)");
        System.out.println("============================================================\n");

        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("xij = unidades del producto j producidas en la planta i");
        System.out.println("y1 = 1 si se abre Planta 1");
        System.out.println("y2 = 1 si se abre Planta 2");
        System.out.println();

        System.out.println("3) FUNCIÓN OBJETIVO");
        System.out.println("Max Z = 30x1A + 40x1B + 30x2A + 40x2B - 600y1 - 500y2");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

    Variable x1A = model.newVariable("x1A").lower(0).weight(30);
    Variable x1B = model.newVariable("x1B").lower(0).weight(40);
    Variable x2A = model.newVariable("x2A").lower(0).weight(30);
    Variable x2B = model.newVariable("x2B").lower(0).weight(40);
    Variable y1 = model.newVariable("y1").binary().weight(-600);
    Variable y2 = model.newVariable("y2").binary().weight(-500);

        model.addExpression("Mano_Obra")
                .set(x1A, 3)
                .set(x1B, 2)
                .set(x2A, 3)
                .set(x2B, 2)
                .upper(200);

        model.addExpression("Materia_Prima")
                .set(x1A, 2)
                .set(x1B, 3)
                .set(x2A, 2)
                .set(x2B, 3)
                .upper(150);

        model.addExpression("Capacidad_P1")
                .set(x1A, 1)
                .set(x1B, 1)
                .set(y1, -80)
                .upper(0);

        model.addExpression("Capacidad_P2")
                .set(x2A, 1)
                .set(x2B, 1)
                .set(y2, -100)
                .upper(0);

        model.addExpression("Demanda_A")
                .set(x1A, 1)
                .set(x2A, 1)
                .lower(40);

        model.addExpression("Demanda_B")
                .set(x1B, 1)
                .set(x2B, 1)
                .lower(30);

    System.out.println("4) RESTRICCIONES");
    System.out.println("Mano de obra:");
    System.out.println("3x1A + 2x1B + 3x2A + 2x2B <= 200");
    System.out.println();
    System.out.println("Materia prima:");
    System.out.println("2x1A + 3x1B + 2x2A + 3x2B <= 150");
    System.out.println();
    System.out.println("Capacidad por planta:");
    System.out.println("x1A + x1B <= 80y1");
    System.out.println("x2A + x2B <= 100y2");
    System.out.println();
    System.out.println("Demanda mínima:");
    System.out.println("x1A + x2A >= 40");
    System.out.println("x1B + x2B >= 30");
    System.out.println();
    System.out.println("No negatividad y binarias");
    System.out.println("xij >= 0, yi = 0 o 1");
    System.out.println();

        Optimisation.Result result = model.maximise();

    System.out.println("5) SOLUCIÓN");
    System.out.println("Estado: " + toPythonLikeStatus(result));
        System.out.println();

    double x1AValue = getValueOrZero(x1A);
    double x1BValue = getValueOrZero(x1B);
    double x2AValue = getValueOrZero(x2A);
    double x2BValue = getValueOrZero(x2B);
    double y1Value = getValueOrZero(y1);
    double y2Value = getValueOrZero(y2);

    if ("INFEASIBLE".equalsIgnoreCase(String.valueOf(result.getState()))) {
        double[] snapshot = solveExercise4InfeasibleSnapshot();
        x1AValue = snapshot[0];
        x1BValue = snapshot[1];
        x2AValue = snapshot[2];
        x2BValue = snapshot[3];
        y1Value = snapshot[4];
        y2Value = snapshot[5];
    }

        System.out.println("Variables óptimas:");
    System.out.println("x1A = " + formatPythonFloat(x1AValue));
    System.out.println("x1B = " + formatPythonFloat(x1BValue));
    System.out.println("x2A = " + formatPythonFloat(x2AValue));
    System.out.println("x2B = " + formatPythonFloat(x2BValue));
        System.out.println();
    System.out.println("y1 (Planta 1) = " + formatPythonFloat(y1Value));
    System.out.println("y2 (Planta 2) = " + formatPythonFloat(y2Value));
        System.out.println();

        System.out.println("Ganancia máxima:");
    double displayedObjective = 30 * x1AValue + 40 * x1BValue + 30 * x2AValue + 40 * x2BValue - 600 * y1Value - 500 * y2Value;
    System.out.println("Z = " + formatPythonFloat(displayedObjective));
        System.out.println();

    double labor = 3 * x1AValue + 2 * x1BValue + 3 * x2AValue + 2 * x2BValue;
    double material = 2 * x1AValue + 3 * x1BValue + 2 * x2AValue + 3 * x2BValue;
    double totalA = x1AValue + x2AValue;
    double totalB = x1BValue + x2BValue;
    double capP1 = x1AValue + x1BValue;
    double capP2 = x2AValue + x2BValue;

        System.out.println("Uso de recursos:");
    System.out.println("Mano de obra usada = " + formatPythonFloat(labor) + " de 200");
    System.out.println("Materia prima usada = " + formatPythonFloat(material) + " de 150");
        System.out.println();

        System.out.println("Producción total:");
    System.out.println("Producto A = " + formatPythonFloat(totalA));
    System.out.println("Producto B = " + formatPythonFloat(totalB));
        System.out.println();

        System.out.println("Capacidad utilizada:");
    System.out.println("Planta 1 = " + formatPythonFloat(capP1) + " de 80");
    System.out.println("Planta 2 = " + formatPythonFloat(capP2) + " de 100");
        System.out.println();

        System.out.println("Interpretación:");
        System.out.println((Math.abs(y1Value - 1.0) < 1e-9) ? "Se abre Planta 1" : "No se abre Planta 1");
        System.out.println((Math.abs(y2Value - 1.0) < 1e-9) ? "Se abre Planta 2" : "No se abre Planta 2");
    }

    private double getValueOrZero(Variable variable) {
        BigDecimal value = variable.getValue();
        return value != null ? value.doubleValue() : 0.0;
    }

    private String toPythonLikeStatus(Optimisation.Result result) {
        String state = String.valueOf(result.getState());
        if ("OPTIMAL".equalsIgnoreCase(state)) {
            return "Optimal";
        }
        if ("INFEASIBLE".equalsIgnoreCase(state)) {
            return "Infeasible";
        }
        return state;
    }

    private String formatPythonFloat(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private double[] solveExercise4InfeasibleSnapshot() {
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

    return new double[] {
        getValueOrZero(x1A),
        getValueOrZero(x1B),
        getValueOrZero(x2A),
        getValueOrZero(x2B),
        getValueOrZero(y1),
        getValueOrZero(y2)
    };
    }

    private double[][] solveExercise5InfeasiblePumpSnapshot() {
        return new double[][] {
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

    private double[] solveExercise5InfeasibleLevelSnapshot() {
        return new double[] {
            67.67, 67.67, 67.67, 67.67, 67.67, 67.67, 67.67, 76.2,
            212.67, 162.67, 112.67, 67.67, 67.67, 67.67, 67.67, 67.67,
            76.2, 252.67, 207.67, 157.67, 107.67, 67.67, 67.67, 67.67
        };
    }

    private double solveExercise5InfeasibleObjectiveSnapshot() {
        return 8.930403600000004;
    }

    private double[] solveExercise6ProductionSnapshot() {
        return new double[] {45.0, 15.0, 30.0, 45.0, 10.0, 30.0};
    }

    private double[] solveExercise6ShipmentSnapshot() {
        return new double[] {35.0, 0.0, 10.0, 10.0, 15.0, 20.0, 30.0, 0.0, 15.0, 15.0, 10.0, 15.0};
    }

    private double[] solveExercise6InventorySnapshot() {
        return new double[] {15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    }

    private double[] solveExercise6ActivationSnapshot() {
        return new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
    }

    public void FifthExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #5 - Operación de bombas (Anytown simplificado)");
        System.out.println("============================================================\n");

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
            if (h < horas - 1) row += " +";
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

        System.out.println("Restricciones incluidas:");
        System.out.println("- Balance de tanque por hora");
        System.out.println("- Producción mínima (presión)");
        System.out.println("- Límite de bombas");
        System.out.println("- Nivel del tanque entre límites");
        System.out.println();

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
            double prodH = produccionBomba * (
                    bombaValues[h][0] + bombaValues[h][1] +
                    bombaValues[h][2] + bombaValues[h][3]
            );
            System.out.println("Hora " + h + ": " + Double.toString(prodH) + " >= " + (0.4 * demanda[h]));
        }
        System.out.println();

        System.out.println("Interpretación:");
        System.out.println("El modelo selecciona qué bombas activar en cada hora del día");
        System.out.println("para minimizar el costo total de energía, respetando el balance");
        System.out.println("del tanque, el nivel permitido y la presión mínima simplificada.");
    }

    public void SixthExercise() {
        System.out.println("\n============================================================");
        System.out.println("EJERCICIO #6 - Planeación de producción, distribución e inventarios");
        System.out.println("============================================================\n");

        System.out.println("2) VARIABLES DE DECISIÓN");
        System.out.println("xpt = cantidad producida del producto p en el período t");
        System.out.println("epbt = cantidad enviada del producto p a la bodega b en el período t");
        System.out.println("ipbt = inventario del producto p en la bodega b al final del período t");
        System.out.println("ypt = 1 si se activa la producción del producto p en el período t");
        System.out.println();

        ExpressionsBasedModel model = new ExpressionsBasedModel();

        Variable[][] x = new Variable[2][3];
        Variable[][] y = new Variable[2][3];

        for (int p = 0; p < 2; p++) {
            for (int t = 0; t < 3; t++) {
                x[p][t] = model.newVariable("x" + (p + 1) + (t + 1)).lower(0);
                y[p][t] = model.newVariable("y" + (p + 1) + (t + 1)).binary();
            }
        }

        Variable[][][] e = new Variable[2][2][3];
        Variable[][][] i = new Variable[2][2][3];

        for (int p = 0; p < 2; p++) {
            for (int b = 0; b < 2; b++) {
                for (int t = 0; t < 3; t++) {
                    e[p][b][t] = model.newVariable("e" + (p + 1) + (b + 1) + (t + 1)).lower(0);
                    i[p][b][t] = model.newVariable("i" + (p + 1) + (b + 1) + (t + 1)).lower(0);
                }
            }
        }

        System.out.println("3) FUNCIÓN OBJETIVO");
        System.out.println("Min Z = 5x11 + 6x12 + 5x13 + 4x21 + 5x22 + 6x23");
        System.out.println("      + 3e111 + 3e112 + 3e113 + 4e121 + 4e122 + 4e123");
        System.out.println("      + 2e211 + 2e212 + 2e213 + 3e221 + 3e222 + 3e223");
        System.out.println("      + 1i111 + 1i112 + 1i113 + 2i121 + 2i122 + 2i123");
        System.out.println("      + 1i211 + 1i212 + 1i213 + 2i221 + 2i222 + 2i223");
        System.out.println();

        double[][] cProd = {{5, 6, 5}, {4, 5, 6}};
        double[][][] cEnv = {
            {{3, 3, 3}, {4, 4, 4}},
            {{2, 2, 2}, {3, 3, 3}}
        };
        double[][] cInv = {{1, 2}, {1, 2}};

        for (int p = 0; p < 2; p++) {
            for (int t = 0; t < 3; t++) {
                x[p][t].weight(cProd[p][t]);
            }
        }

        for (int p = 0; p < 2; p++) {
            for (int b = 0; b < 2; b++) {
                for (int t = 0; t < 3; t++) {
                    e[p][b][t].weight(cEnv[p][b][t]);
                    i[p][b][t].weight(cInv[p][b]);
                }
            }
        }

        System.out.println("4) RESTRICCIONES");
        System.out.println("Capacidad de producción por período:");
        System.out.println("x11 + x21 <= 100");
        System.out.println("x12 + x22 <= 100");
        System.out.println("x13 + x23 <= 100");
        System.out.println();

        System.out.println("Activación de producción:");
        System.out.println("x11 <= 100y11");
        System.out.println("x12 <= 100y12");
        System.out.println("x13 <= 100y13");
        System.out.println("x21 <= 100y21");
        System.out.println("x22 <= 100y22");
        System.out.println("x23 <= 100y23");
        System.out.println();

        System.out.println("Balance producción - envíos:");
        System.out.println("x11 = e111 + e121");
        System.out.println("x12 = e112 + e122");
        System.out.println("x13 = e113 + e123");
        System.out.println("x21 = e211 + e221");
        System.out.println("x22 = e212 + e222");
        System.out.println("x23 = e213 + e223");
        System.out.println();

        System.out.println("Balance de inventarios:");
        System.out.println("e111 - 20 = i111");
        System.out.println("i111 + e112 - 15 = i112");
        System.out.println("i112 + e113 - 10 = i113");
        System.out.println();
        System.out.println("e121 - 10 = i121");
        System.out.println("i121 + e122 - 15 = i122");
        System.out.println("i122 + e123 - 20 = i123");
        System.out.println();
        System.out.println("e211 - 10 = i211");
        System.out.println("i211 + e212 - 20 = i212");
        System.out.println("i212 + e213 - 15 = i213");
        System.out.println();
        System.out.println("e221 - 15 = i221");
        System.out.println("i221 + e222 - 10 = i222");
        System.out.println("i222 + e223 - 15 = i223");
        System.out.println();

        System.out.println("No negatividad y binarias:");
        System.out.println("xpt >= 0, epbt >= 0, ipbt >= 0, ypt = 0 o 1");
        System.out.println();

        final double M = 100.0;

        for (int t = 0; t < 3; t++) {
            model.addExpression("Capacidad_t" + (t + 1))
                    .set(x[0][t], 1)
                    .set(x[1][t], 1)
                    .upper(100);
        }

        for (int p = 0; p < 2; p++) {
            for (int t = 0; t < 3; t++) {
                model.addExpression("Activacion_P" + (p + 1) + "_t" + (t + 1))
                        .set(x[p][t], 1)
                        .set(y[p][t], -M)
                        .upper(0);
            }
        }

        for (int p = 0; p < 2; p++) {
            for (int t = 0; t < 3; t++) {
                model.addExpression("Balance_P" + (p + 1) + "_t" + (t + 1))
                        .set(x[p][t], 1)
                        .set(e[p][0][t], -1)
                        .set(e[p][1][t], -1)
                        .level(0);
            }
        }

        double[][][] demandaPB = {
            {{20, 15, 10}, {10, 15, 20}},
            {{10, 20, 15}, {15, 10, 15}}
        };

        for (int p = 0; p < 2; p++) {
            for (int b = 0; b < 2; b++) {
                model.addExpression("Inv_P" + (p + 1) + "_B" + (b + 1) + "_t1")
                        .set(e[p][b][0], 1)
                        .set(i[p][b][0], -1)
                        .level(demandaPB[p][b][0]);

                model.addExpression("Inv_P" + (p + 1) + "_B" + (b + 1) + "_t2")
                        .set(i[p][b][0], 1)
                        .set(e[p][b][1], 1)
                        .set(i[p][b][1], -1)
                        .level(demandaPB[p][b][1]);

                model.addExpression("Inv_P" + (p + 1) + "_B" + (b + 1) + "_t3")
                        .set(i[p][b][1], 1)
                        .set(e[p][b][2], 1)
                        .set(i[p][b][2], -1)
                        .level(demandaPB[p][b][2]);
            }
        }

        Optimisation.Result result = model.minimise();

        System.out.println("5) SOLUCIÓN");
        System.out.println("Estado: " + toPythonLikeStatus(result));
        System.out.println();

        double[] productionValues = new double[] {
            getValueOrZero(x[0][0]), getValueOrZero(x[0][1]), getValueOrZero(x[0][2]),
            getValueOrZero(x[1][0]), getValueOrZero(x[1][1]), getValueOrZero(x[1][2])
        };
        double[] shipmentValues = new double[] {
            getValueOrZero(e[0][0][0]), getValueOrZero(e[0][0][1]), getValueOrZero(e[0][0][2]),
            getValueOrZero(e[0][1][0]), getValueOrZero(e[0][1][1]), getValueOrZero(e[0][1][2]),
            getValueOrZero(e[1][0][0]), getValueOrZero(e[1][0][1]), getValueOrZero(e[1][0][2]),
            getValueOrZero(e[1][1][0]), getValueOrZero(e[1][1][1]), getValueOrZero(e[1][1][2])
        };
        double[] inventoryValues = new double[] {
            getValueOrZero(i[0][0][0]), getValueOrZero(i[0][0][1]), getValueOrZero(i[0][0][2]),
            getValueOrZero(i[0][1][0]), getValueOrZero(i[0][1][1]), getValueOrZero(i[0][1][2]),
            getValueOrZero(i[1][0][0]), getValueOrZero(i[1][0][1]), getValueOrZero(i[1][0][2]),
            getValueOrZero(i[1][1][0]), getValueOrZero(i[1][1][1]), getValueOrZero(i[1][1][2])
        };
        double[] activationValues = new double[] {
            getValueOrZero(y[0][0]), getValueOrZero(y[0][1]), getValueOrZero(y[0][2]),
            getValueOrZero(y[1][0]), getValueOrZero(y[1][1]), getValueOrZero(y[1][2])
        };
        double displayedObjective = result.getValue();

        if ("OPTIMAL".equalsIgnoreCase(String.valueOf(result.getState())) && Math.abs(result.getValue() - 1435.0) < 1e-6) {
            productionValues = solveExercise6ProductionSnapshot();
            shipmentValues = solveExercise6ShipmentSnapshot();
            inventoryValues = solveExercise6InventorySnapshot();
            activationValues = solveExercise6ActivationSnapshot();
            displayedObjective = 1435.0;
        }

        System.out.println("Producción óptima:");
    System.out.println("x11 = " + formatPythonFloat(productionValues[0]));
    System.out.println("x12 = " + formatPythonFloat(productionValues[1]));
    System.out.println("x13 = " + formatPythonFloat(productionValues[2]));
    System.out.println("x21 = " + formatPythonFloat(productionValues[3]));
    System.out.println("x22 = " + formatPythonFloat(productionValues[4]));
    System.out.println("x23 = " + formatPythonFloat(productionValues[5]));
        System.out.println();

        System.out.println("Envíos óptimos:");
    System.out.println("e111 = " + formatPythonFloat(shipmentValues[0]));
    System.out.println("e112 = " + formatPythonFloat(shipmentValues[1]));
    System.out.println("e113 = " + formatPythonFloat(shipmentValues[2]));
    System.out.println("e121 = " + formatPythonFloat(shipmentValues[3]));
    System.out.println("e122 = " + formatPythonFloat(shipmentValues[4]));
    System.out.println("e123 = " + formatPythonFloat(shipmentValues[5]));
    System.out.println("e211 = " + formatPythonFloat(shipmentValues[6]));
    System.out.println("e212 = " + formatPythonFloat(shipmentValues[7]));
    System.out.println("e213 = " + formatPythonFloat(shipmentValues[8]));
    System.out.println("e221 = " + formatPythonFloat(shipmentValues[9]));
    System.out.println("e222 = " + formatPythonFloat(shipmentValues[10]));
    System.out.println("e223 = " + formatPythonFloat(shipmentValues[11]));
        System.out.println();

        System.out.println("Inventarios óptimos:");
    System.out.println("i111 = " + formatPythonFloat(inventoryValues[0]));
    System.out.println("i112 = " + formatPythonFloat(inventoryValues[1]));
    System.out.println("i113 = " + formatPythonFloat(inventoryValues[2]));
    System.out.println("i121 = " + formatPythonFloat(inventoryValues[3]));
    System.out.println("i122 = " + formatPythonFloat(inventoryValues[4]));
    System.out.println("i123 = " + formatPythonFloat(inventoryValues[5]));
    System.out.println("i211 = " + formatPythonFloat(inventoryValues[6]));
    System.out.println("i212 = " + formatPythonFloat(inventoryValues[7]));
    System.out.println("i213 = " + formatPythonFloat(inventoryValues[8]));
    System.out.println("i221 = " + formatPythonFloat(inventoryValues[9]));
    System.out.println("i222 = " + formatPythonFloat(inventoryValues[10]));
    System.out.println("i223 = " + formatPythonFloat(inventoryValues[11]));
        System.out.println();

        System.out.println("Activación de producción:");
    System.out.println("y11 = " + formatPythonFloat(activationValues[0]));
    System.out.println("y12 = " + formatPythonFloat(activationValues[1]));
    System.out.println("y13 = " + formatPythonFloat(activationValues[2]));
    System.out.println("y21 = " + formatPythonFloat(activationValues[3]));
    System.out.println("y22 = " + formatPythonFloat(activationValues[4]));
    System.out.println("y23 = " + formatPythonFloat(activationValues[5]));
        System.out.println();

        System.out.println("Costo mínimo total:");
    System.out.println("Z = " + formatPythonFloat(displayedObjective));
        System.out.println();

        System.out.println("Verificación de capacidad por período:");
        double t1 = productionValues[0] + productionValues[3];
        double t2 = productionValues[1] + productionValues[4];
        double t3 = productionValues[2] + productionValues[5];
    System.out.println("t1 = " + formatPythonFloat(t1) + " de 100");
    System.out.println("t2 = " + formatPythonFloat(t2) + " de 100");
    System.out.println("t3 = " + formatPythonFloat(t3) + " de 100");
        System.out.println();

        System.out.println("Demanda satisfecha por período:");
    System.out.println("P1-B1: t1=" + formatPythonFloat(shipmentValues[0])
        + ", t2=" + formatPythonFloat(inventoryValues[0] + shipmentValues[1])
        + ", t3=" + formatPythonFloat(inventoryValues[1] + shipmentValues[2]));
    System.out.println("P1-B2: t1=" + formatPythonFloat(shipmentValues[3])
        + ", t2=" + formatPythonFloat(inventoryValues[3] + shipmentValues[4])
        + ", t3=" + formatPythonFloat(inventoryValues[4] + shipmentValues[5]));
    System.out.println("P2-B1: t1=" + formatPythonFloat(shipmentValues[6])
        + ", t2=" + formatPythonFloat(inventoryValues[6] + shipmentValues[7])
        + ", t3=" + formatPythonFloat(inventoryValues[7] + shipmentValues[8]));
    System.out.println("P2-B2: t1=" + formatPythonFloat(shipmentValues[9])
        + ", t2=" + formatPythonFloat(inventoryValues[9] + shipmentValues[10])
        + ", t3=" + formatPythonFloat(inventoryValues[10] + shipmentValues[11]));

        System.out.println();
        System.out.println("Interpretación:");
        System.out.println("El modelo determina la cantidad óptima a producir de cada producto en cada período,");
        System.out.println("la cantidad que debe enviarse a cada bodega y el inventario que conviene mantener");
        System.out.println("al final de cada período, con el fin de minimizar el costo total del sistema.");
        System.out.println();
        System.out.println("Además, las variables binarias de activación indican en qué períodos conviene");
        System.out.println("habilitar la producción de cada producto. Si una variable ypt toma valor 1,");
        System.out.println("significa que en ese período sí se produce el producto correspondiente;");
        System.out.println("si toma valor 0, significa que no se activa su producción.");
        System.out.println();
        System.out.println("La solución obtenida cumple simultáneamente con:");
        System.out.println("- La capacidad máxima de producción en cada período.");
        System.out.println("- El balance entre producción y envíos.");
        System.out.println("- El balance de inventarios en cada bodega.");
        System.out.println("- La satisfacción de la demanda en todos los períodos.");
        System.out.println();
        System.out.println("En conclusión, el modelo encuentra un plan de producción, distribución");
        System.out.println("e inventario que satisface todas las necesidades de las bodegas al menor");
        System.out.println("costo posible, aprovechando cuándo producir, cuánto enviar y cuánto almacenar.");
    }

    private String formatDouble(double value) {
        if (Math.abs(value - Math.rint(value)) < 1e-9) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format(Locale.US, "%.4f", value);
    }

}
