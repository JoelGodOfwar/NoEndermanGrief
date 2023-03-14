package com.github.joelgodofwar.neg.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.github.joelgodofwar.neg.NoEndermanGrief;
 
public class GUIMoveItem implements Listener {
	String strTrue, strFalse, strLang, strSetLang = "en_US";
	boolean update = false, Debug = false, Console = false,
			Trader = false, Pillager = false, Ender = false,
			Ghast = false, Horse = false, Phantom = false,
			Creeper = false;
 

	@EventHandler
    public void clickEvent(InventoryClickEvent e){
 
        //Check to see if its the GUI menu
		System.out.println(e.getView().getTitle());
        if( e.getView().getTitle().equalsIgnoreCase("Configurations") ){
        	
        	
        	e.setCancelled(true); //So they cant take the items
            Player player = (Player) e.getWhoClicked();
            //Determine what they selected and what to do
            if( !(e.getCurrentItem() != null) ) {return;}
            System.out.println(e.getCurrentItem().getType());
            ItemStack curItem = e.getCurrentItem();
            if(!(curItem != null)) {
            	return;
            }
            System.out.println(curItem.getItemMeta().getDisplayName());
            switch(curItem.getType()){ // .getItemMeta().getDisplayName()
                case LIME_STAINED_GLASS_PANE:
                	strTrue = curItem.getItemMeta().getDisplayName();
                	if(strTrue.equalsIgnoreCase("Set auto_update_check to True")) {
                		update = true;
                	}else if(strTrue.equalsIgnoreCase("Set debug to True")) {
                		Debug = true;
                	}else if(strTrue.equalsIgnoreCase("Set colorful_console to True")) {
                		Console = true;
                	}else if(strTrue.equalsIgnoreCase("Set wandering_trader_spawn to True")) {
                		Trader = true;
                	}else if(strTrue.equalsIgnoreCase("Set pillager_patrol_spawn to True")) {
                		Pillager = true;
                	}else if(strTrue.equalsIgnoreCase("Set enderman_grief to True")) {
                		Ender = true;
                	}else if(strTrue.equalsIgnoreCase("Set ghast_grief to True")) {
                		Ghast = true;
                	}else if(strTrue.equalsIgnoreCase("Set skeleton_horse_spawn to True")) {
                		Horse = true;
                	}else if(strTrue.equalsIgnoreCase("Set phantom_spawn to True")) {
                		Phantom = true;
                	}else if(strTrue.equalsIgnoreCase("Set creeper_grief to True")) {
                		Creeper = true;
                	}
                    // get DisplayName then set config
                    break;
                case RED_STAINED_GLASS_PANE:
                	strFalse = curItem.getItemMeta().getDisplayName();
                	if(strFalse.equalsIgnoreCase("Set auto_update_check to False")) {
                		update = false;
                	}else if(strFalse.equalsIgnoreCase("Set debug to False")) {
                		Debug = false;
                	}else if(strFalse.equalsIgnoreCase("Set colorful_console to False")) {
                		Console = false;
                	}else if(strFalse.equalsIgnoreCase("Set wandering_trader_spawn to False")) {
                		Trader = false;
                	}else if(strFalse.equalsIgnoreCase("Set pillager_patrol_spawn to False")) {
                		Pillager = false;
                	}else if(strFalse.equalsIgnoreCase("Set enderman_grief to False")) {
                		Ender = false;
                	}else if(strFalse.equalsIgnoreCase("Set ghast_grief to False")) {
                		Ghast = false;
                	}else if(strFalse.equalsIgnoreCase("Set skeleton_horse_spawn to False")) {
                		Horse = false;
                	}else if(strFalse.equalsIgnoreCase("Set phantom_spawn to False")) {
                		Phantom = false;
                	}else if(strFalse.equalsIgnoreCase("Set creeper_grief to False")) {
                		Creeper = false;
                	}
                    // get DisplayName then set config
                    break;
                case PAPER:
                	strLang = curItem.getItemMeta().getDisplayName();
                	if(strLang.endsWith("(cs-CZ)")) {
                		strSetLang = "cs_CZ";
                	}else if(strLang.endsWith("(de_DE)")) {
                		strSetLang = "de_DE";
                	}else if(strLang.endsWith("(en_US)")) {
                		strSetLang = "en_US";
                	}else if(strLang.endsWith("(fr_FR)")) {
                		strSetLang = "fr_FR";
                	}else if(strLang.endsWith("(lol_US)")) {
                		strSetLang = "lol_US";
                	}else if(strLang.endsWith("(nl_NL)")) {
                		strSetLang = "nl_NL";
                	}else if(strLang.endsWith("(pt_BR)")) {
                		strSetLang = "pt_BR";
                	}
                    // get DisplayName then set lang then set config
                    break;
                case SLIME_BLOCK:
                	// Save config.yml
                	player.closeInventory();
                	NoEndermanGrief neg = new NoEndermanGrief();
                	neg.saveConfig(update, Debug, Console, Trader, Pillager, Ender, Ghast, Horse, Phantom, Creeper, strSetLang);
                	break;
                case BARRIER:
                	// Cancel
                	player.closeInventory();
                	break;
			default:
				break;
                
            }
 
 
            
        }
 
    }
 
}