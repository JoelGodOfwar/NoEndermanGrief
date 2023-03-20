package com.github.joelgodofwar.neg.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

@SuppressWarnings("static-access")
public class Language {
	
	//private final NoEndermanGrief plugin;
	private static String lang;
	
	public Language(String lang){
		//this.plugin = plugin;
		this.lang = lang;
	}
	public static String get(String key) {
		return ResourceBundle.getBundle("lang/messages", new Locale(lang)).getString(key);
	}

}