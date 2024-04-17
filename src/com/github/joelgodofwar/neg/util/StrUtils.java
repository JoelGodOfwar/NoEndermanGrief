package com.github.joelgodofwar.neg.util;

public class StrUtils {
	/** StringRight */
	public static String Right(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Right input=" + input);
			return input.substring(input.length() - chars);
		} 
		else{
			return input;
		}
	}
	
	/** StringLeft */
	public static String Left(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Left input=" + input);
			//System.out.println("Left chars=" + chars);
			return input.substring(0, chars);
		} 
		else{
			return input;
		}
	}
	
	/** */
	public  static boolean stringContains(String string, String string2){
		string = getOrDefault(string);
		String[] string3 = string.split(", ");
		for(int i = 0; i < string3.length; i++){
			if(string3[i].equals(string2)){
				return true;
			}
		}
		return false;
	}
	
	public static String getOrDefault(String key) {
    	//String obj = key;
    	//return obj==null?"":obj.toString();
		return key==null?"":key;
	}

}
