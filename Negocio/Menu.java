package Negocio;

import Datos.PostgreSQLConnection;
import Datos.MySQLConnection;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private Scanner scanner = new Scanner(System.in);
    String cone;

    public void iniciar() {
        int opcion = 0;
        while (opcion != 3) {
            System.out.println(cTxt(color("|~|")));
            System.out.println(cTxt(color("|                 Bienvenido a Aroma y Cafe                 |")));
            System.out.println(cTxt(color("|                  \"Un cafe, mil momentos\"                |")));
            System.out.println(cTxt(color("|~|")));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt("|    ( (  |"));
            System.out.println(cTxt("|     ) )  |"));
            System.out.println(cTxt("|  ______  |"));
            System.out.println(cTxt("| |      |] |"));
            System.out.println(cTxt("| |      |  |"));
            System.out.println(cTxt("| \\/  |"));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("| Seleccione la base de datos a la que desea conectarse:    |")));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("| 1. PostgreSQL                                             |")));
            System.out.println(cTxt(color("| 2. MySQL                                                  |")));
            System.out.println(cTxt(color("| 3. Salir                                                  |")));
            System.out.println(cTxt(color("|                                                           |")));
            System.out.println(cTxt(color("|~|")));
            System.out.print("Selecciona una opcion: ");
            opcion = scanner.nextInt();
    
            switch (opcion) {
                case 1:
                    mostraMenu();
                    break;
                case 2:
                    conectarMySQL();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opcion no válida. Intente de nuevo.");
            }
        }
    }    

    private void mostraMenu() {
        PostgreSQLConnection postgres = new PostgreSQLConnection();

        try (Connection conn = postgres.getConnection()) {
            if (conn != null) {
                System.out.println("\nConexion exitosa a PostgreSQL.");
                System.out.println(cTxt(color("|~|")));
                System.out.println(cTxt(color("|                           Menu                            |")));
                System.out.println(cTxt(color("|                       Aroma y Cafe                        |")));
                System.out.println(cTxt(color("|                 \"Un cafe, mil momentos\"                 |")));
                System.out.println(cTxt(color("|~|")));
                System.out.println(cTxt(color("|                                                           |")));
                System.out.println(cTxt(color("| ¿Alguna preferencia de tu eleccion en este momento?       |")));
                System.out.println(cTxt(color("|                                                           |")));
                postgres.listCategorias();
                System.out.println(cTxt(color("|                                                           |")));
                System.out.println(cTxt(color("|~|")));
                System.out.print("Selecciona una opcion: ");
                int opcionCat = scanner.nextInt();

                if (opcionCat == 6) {
                    System.out.println("Gracias por visitarnos. ¡Hasta pronto!");
                }
                if (opcionCat >= 1 && opcionCat <= 5) {
                    while (true) {
                        postgres.listProductos(opcionCat);
                        System.out.print("Selecciona una opcion: ");
                        int opcionProd = scanner.nextInt();
                        postgres.mostrarProductoPorId(opcionProd);
                        System.out.println(color("~"));
                        System.out.println("\n¿Desea agregar? SI/NO");
                        System.out.print("Selecciona: ");
                        String opcionAdd = scanner.next();
                        ArrayList<Integer> productosSeleccionados = new ArrayList<>();
                        if (opcionAdd.equals("si")) {
                            productosSeleccionados.add(opcionProd);
                            System.out.println("\n¿Desea agregar otro producto? SI/NO");
                            String opcionAdd2 = scanner.next();
                            if (opcionAdd2.equals("no")) {
                                break;
                            }
                        }
                    }

                } else {
                    System.out.println("Opción inválida, intenta de nuevo.");
                }

            }
        } catch (Exception e) {
            System.out.println("Error al conectar a PostgreSQL: " + e.getMessage());
        }
    }

    private void conectarMySQL() {
        MySQLConnection mysql = new MySQLConnection();

        try (Connection conn = mysql.getConnection()) {
            if (conn != null) {
                System.out.println("Conexión exitosa a MySQL.");

            }
        } catch (Exception e) {
            System.out.println("Error al conectar a MySQL: " + e.getMessage());
        }
    }

    // centrar texto
    public static String cTxt(String texto) {
        int anchoPantalla = 80; 
        int espacio = (anchoPantalla - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
        
        // Agregar espacios antes del texto para centrarlo
        for (int i = 0; i < espacio; i++) {
            sb.append(" ");
        }
        
        sb.append(texto);
        return sb.toString();
    }

    // color de ~ a gris
    public static String color(String texto) {
        return texto.replace("", "\033[38;5;238m\033[0m");
    }

}