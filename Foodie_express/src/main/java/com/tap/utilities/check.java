package com.tap.utilities;

public class check {
	public static void main(String []args) {
		if (DBconnection .getConnection() != null) {
			System.out.print ("connection has set up");
		}
	}
}
