package com.tap.utilities;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {

    public static Connection con;

    public static Connection getConnection() {

        try {

         Class.forName("com.mysql.cj.jdbc.Driver");

        con = DriverManager.getConnection( "jdbc:mysql://localhost:3306/food_delivery_application", "root","root");


        } 
        catch(ClassNotFoundException e){
        	e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return con;
    }
}