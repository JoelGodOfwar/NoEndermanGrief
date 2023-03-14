package com.github.joelgodofwar.neg.util;

import org.bukkit.ChatColor;

public class ChatColorUtils 
{
	public static String setColorsByCode(String s)
	{
	    return s.replace("&0", "" + ChatColor.BLACK).replace("&1", "" + ChatColor.DARK_BLUE)
	    		.replace("&2", "" + ChatColor.DARK_GREEN).replace("&3", "" + ChatColor.DARK_AQUA)
	    		.replace("&4", "" + ChatColor.DARK_RED).replace("&5", "" + ChatColor.DARK_PURPLE)
	    		.replace("&6", "" + ChatColor.GOLD).replace("&7", "" + ChatColor.GRAY)
	    		.replace("&8", "" + ChatColor.DARK_GRAY).replace("&9", "" + ChatColor.BLUE)
	    		.replace("&a", "" + ChatColor.GREEN).replace("&b", "" + ChatColor.AQUA)
	    		.replace("&c", "" + ChatColor.RED).replace("&d", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&e", "" + ChatColor.YELLOW).replace("&f", "" + ChatColor.WHITE)
	    		.replace("&k", "" + ChatColor.MAGIC).replace("&l", "" + ChatColor.BOLD)
	    		.replace("&m", "" + ChatColor.STRIKETHROUGH).replace("&n", "" + ChatColor.UNDERLINE)
	    		.replace("&o", "" + ChatColor.ITALIC).replace("&r", "" + ChatColor.RESET)
	    		.replace("&A", "" + ChatColor.GREEN).replace("&B", "" + ChatColor.AQUA)
	    		.replace("&C", "" + ChatColor.RED).replace("&D", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&E", "" + ChatColor.YELLOW).replace("&F", "" + ChatColor.WHITE)
	    		.replace("&K", "" + ChatColor.MAGIC).replace("&L", "" + ChatColor.BOLD)
	    		.replace("&M", "" + ChatColor.STRIKETHROUGH).replace("&N", "" + ChatColor.UNDERLINE)
	    		.replace("&O", "" + ChatColor.ITALIC).replace("&R", "" + ChatColor.RESET);
	}
	public static String setColorsByName(String s){
	  return s.replace("BLACK", "" + ChatColor.BLACK).replace("DARK_BLUE", "" + ChatColor.DARK_BLUE)
	    		.replace("DARK_GREEN", "" + ChatColor.DARK_GREEN).replace("DARK_AQUA", "" + ChatColor.DARK_AQUA)
	    		.replace("DARK_RED", "" + ChatColor.DARK_RED).replace("DARK_PURPLE", "" + ChatColor.DARK_PURPLE)
	    		.replace("GOLD", "" + ChatColor.GOLD).replace("GRAY", "" + ChatColor.GRAY)
	    		.replace("DARK_GRAY", "" + ChatColor.DARK_GRAY).replace("BLUE", "" + ChatColor.BLUE)
	    		.replace("GREEN", "" + ChatColor.GREEN).replace("AQUA", "" + ChatColor.AQUA)
	    		.replace("RED", "" + ChatColor.RED).replace("LIGHT_PURPLE", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("YELLOW", "" + ChatColor.YELLOW).replace("WHITE", "" + ChatColor.WHITE)
	    		.replace("MAGIC", "" + ChatColor.MAGIC).replace("BOLD", "" + ChatColor.BOLD)
	    		.replace("STRIKETHROUGH", "" + ChatColor.STRIKETHROUGH).replace("UNDERLINE", "" + ChatColor.UNDERLINE)
	    		.replace("ITALIC", "" + ChatColor.ITALIC).replace("RESET", "" + ChatColor.RESET);
	}
	public static String setColors(String s)
	{
	    return s.replace("&0", "" + ChatColor.BLACK).replace("&1", "" + ChatColor.DARK_BLUE)
	    		.replace("&2", "" + ChatColor.DARK_GREEN).replace("&3", "" + ChatColor.DARK_AQUA)
	    		.replace("&4", "" + ChatColor.DARK_RED).replace("&5", "" + ChatColor.DARK_PURPLE)
	    		.replace("&6", "" + ChatColor.GOLD).replace("&7", "" + ChatColor.GRAY)
	    		.replace("&8", "" + ChatColor.DARK_GRAY).replace("&9", "" + ChatColor.BLUE)
	    		.replace("&a", "" + ChatColor.GREEN).replace("&b", "" + ChatColor.AQUA)
	    		.replace("&c", "" + ChatColor.RED).replace("&d", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&e", "" + ChatColor.YELLOW).replace("&f", "" + ChatColor.WHITE)
	    		.replace("&k", "" + ChatColor.MAGIC).replace("&l", "" + ChatColor.BOLD)
	    		.replace("&m", "" + ChatColor.STRIKETHROUGH).replace("&n", "" + ChatColor.UNDERLINE)
	    		.replace("&o", "" + ChatColor.ITALIC).replace("&r", "" + ChatColor.RESET)
	    		.replace("&A", "" + ChatColor.GREEN).replace("&B", "" + ChatColor.AQUA)
	    		.replace("&C", "" + ChatColor.RED).replace("&D", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&E", "" + ChatColor.YELLOW).replace("&F", "" + ChatColor.WHITE)
	    		.replace("&K", "" + ChatColor.MAGIC).replace("&L", "" + ChatColor.BOLD)
	    		.replace("&M", "" + ChatColor.STRIKETHROUGH).replace("&N", "" + ChatColor.UNDERLINE)
	    		.replace("&O", "" + ChatColor.ITALIC).replace("&R", "" + ChatColor.RESET)
	    		.replace("BLACK", "" + ChatColor.BLACK).replace("DARK_BLUE", "" + ChatColor.DARK_BLUE)
	    		.replace("DARK_GREEN", "" + ChatColor.DARK_GREEN).replace("DARK_AQUA", "" + ChatColor.DARK_AQUA)
	    		.replace("DARK_RED", "" + ChatColor.DARK_RED).replace("DARK_PURPLE", "" + ChatColor.DARK_PURPLE)
	    		.replace("GOLD", "" + ChatColor.GOLD).replace("GRAY", "" + ChatColor.GRAY)
	    		.replace("DARK_GRAY", "" + ChatColor.DARK_GRAY).replace("BLUE", "" + ChatColor.BLUE)
	    		.replace("GREEN", "" + ChatColor.GREEN).replace("AQUA", "" + ChatColor.AQUA)
	    		.replace("RED", "" + ChatColor.RED).replace("LIGHT_PURPLE", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("YELLOW", "" + ChatColor.YELLOW).replace("WHITE", "" + ChatColor.WHITE)
	    		.replace("MAGIC", "" + ChatColor.MAGIC).replace("BOLD", "" + ChatColor.BOLD)
	    		.replace("STRIKETHROUGH", "" + ChatColor.STRIKETHROUGH).replace("UNDERLINE", "" + ChatColor.UNDERLINE)
	    		.replace("ITALIC", "" + ChatColor.ITALIC).replace("RESET", "" + ChatColor.RESET);
	}
}