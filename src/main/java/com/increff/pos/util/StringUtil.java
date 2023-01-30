package com.increff.pos.util;

public class StringUtil {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String toLowerCase(String s) {
		return s == null ? null : s.trim().toLowerCase();
	}

	public static String generateBarcode(){ // todo remove randomly barcode generation
		String barcode = "",parent = "0123456789" + "abcdefghijklmnopqrstuvxyz";
		for(int i = 0;i < 8;i++){
			barcode += parent.charAt((int)(parent.length()*Math.random()));
		}
		return barcode;
	}
}
