package com.investigacion_operaciones.java;

import java.util.Scanner;

public class Java {

    public static void main(String[] args) {
        Java app = new Java();
        app.menu();
    }

    public void menu() {
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
                    FurnitureFactory furnitureFactory = new FurnitureFactory();
                    furnitureFactory.handler();
                    break;
                case 2:
                    ServerAssignment serverAssigment = new ServerAssignment();
                    serverAssigment.handler();
                    break;
                case 3:
                    WarehouseOpeningAndTransport warehouseOpeningAndTransport = new WarehouseOpeningAndTransport();
                    warehouseOpeningAndTransport.handler();
                    break;
                case 4:
                    ProductionAndPlantOpening productionAndPlantOpening = new ProductionAndPlantOpening();
                    productionAndPlantOpening.handler();
                    break;
                case 5:
                    PumpOperation pumpOperation = new PumpOperation();
                    pumpOperation.handler();
                    break;
                case 6:
                    SupplyPlanning supplyPlanning = new SupplyPlanning();
                    supplyPlanning.handler();
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
}
