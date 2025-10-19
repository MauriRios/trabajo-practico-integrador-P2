/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.trabajo.practico.integrador.p2;

import com.mycompany.trabajo.practico.integrador.p2.config.DatabaseConnection;

/**
 *
 * @author mauri_bcda
 */
public class TrabajoPracticoIntegradorP2 {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  VEHICLE INSURANCE MANAGEMENT SYSTEM              ║");
        System.out.println("║  Version 1.0                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("Ruta actual: " + new java.io.File(".").getAbsolutePath());
        System.out.println("Recurso encontrado? " + (DatabaseConnection.class.getClassLoader().getResource("database.properties") != null));
        AppMenu menu = new AppMenu();
        menu.start();
    }
}
