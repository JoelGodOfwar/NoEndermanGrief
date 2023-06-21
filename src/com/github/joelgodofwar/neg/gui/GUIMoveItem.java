package com.github.joelgodofwar.neg.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import com.github.joelgodofwar.neg.NoEndermanGrief;
 
public class GUIMoveItem implements Listener {
	String strTrue, strFalse, strLang, strSetLang = "en_US";
	boolean update = false, Debug = false, Console = false,
			Trader = false, Pillager = false, Ender = false,
			Ghast = false, Horse = false, Phantom = false,
			Creeper = false, Longname = false;
	NoEndermanGrief plugin;
	private final int 	UPDATE_TRUE = 1,	UPDATE_FALSE = 2,	DEBUG_TRUE = 10,	DEBUG_FALSE = 11,
						CONSOLE_TRUE = 19, 	CONSOLE_FALSE = 20,	TRADER_TRUE = 22,	TRADER_FALSE = 23,
						PILLAGER_TRUE = 25,	PILLAGER_FALSE = 26,ENDER_TRUE = 28,	ENDER_FALSE = 29,
						GHAST_TRUE = 31,	GHAST_FALSE = 32, 	HORSE_TRUE = 37,	HORSE_FALSE = 38,
						PHANTOM_TRUE = 40,	PHANTOM_FALSE = 41,	CREEPER_TRUE = 46,	CREEPER_FALSE = 47,
						LANG_CZ = 4,		LANG_DE = 5,		LANG_EN = 6,		LANG_FR = 7,
						LANG_LOL = 13,		LANG_NL = 14,		LANG_BR = 15,
						BTN_SAVE = 52,		BTN_CANCEL = 53,	LONGNAME_TRUE = 49,	LONGNAME_FALSE = 50;
	
	/** 0  1  2  3  4  5  6  7  8
		9  10 11 12 13 14 15 16 17
		18 19 20 21 22 23 24 25 26
		27 28 29 30 31 32 33 34 35
		36 37 38 39 40 41 42 43 44
		45 46 47 48 49 50 51 52 53 */
	public GUIMoveItem(NoEndermanGrief plugin){
		this.plugin = plugin;
	}

	@EventHandler
    public void clickEvent(InventoryClickEvent event){
 
        //Check to see if its the GUI menu
		//System.out.println(e.getView().getTitle());
        if( event.getView().getTitle().equalsIgnoreCase("Configurations") && !event.getSlotType().equals(SlotType.OUTSIDE)){
        	
        	
        	event.setCancelled(true); //So they cant take the items
            Player player = (Player) event.getWhoClicked();
            //Determine what they selected and what to do
            if( !(event.getCurrentItem() != null) ) {return;}
            //System.out.println(e.getCurrentItem().getType());
            ItemStack curItem = event.getCurrentItem();
            if(!(curItem != null)) {
            	return;
            }
            
            switch( event.getSlot() ){ 
            	case UPDATE_TRUE:
            		event.getInventory().getItem(UPDATE_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		update = true;
            		break;
            	case UPDATE_FALSE:
            		event.getInventory().getItem(UPDATE_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.RED_STAINED_GLASS_PANE);
            		update = false;
            		break;
            	case DEBUG_TRUE:
            		event.getInventory().getItem(DEBUG_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Debug = true;
            		break;
            	case DEBUG_FALSE:
            		event.getInventory().getItem(DEBUG_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.RED_STAINED_GLASS_PANE);
            		Debug = false;
            		break;
            	case CONSOLE_TRUE:
            		event.getInventory().getItem(CONSOLE_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Console = true;
            		break;
            	case CONSOLE_FALSE:
            		event.getInventory().getItem(CONSOLE_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Console = false;
            		break;
            	case TRADER_TRUE:
            		event.getInventory().getItem(TRADER_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Trader = true;
            		break;
            	case TRADER_FALSE:
            		event.getInventory().getItem(TRADER_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Trader = false;
            		break;
            	case PILLAGER_TRUE:
            		event.getInventory().getItem(PILLAGER_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Pillager = true;
            		break;
            	case PILLAGER_FALSE:
            		event.getInventory().getItem(PILLAGER_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Pillager = false;
            		break;
            	case ENDER_TRUE:
            		event.getInventory().getItem(ENDER_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Ender = true;
            		break;
            	case ENDER_FALSE:
            		event.getInventory().getItem(ENDER_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Ender = false;
            		break;
            	case GHAST_TRUE:
            		event.getInventory().getItem(GHAST_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Ghast = true;
            		break;
            	case GHAST_FALSE:
            		event.getInventory().getItem(GHAST_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Ghast = false;
            		break;
            	case HORSE_TRUE:
            		event.getInventory().getItem(HORSE_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Horse = true;
            		break;
            	case HORSE_FALSE:
            		event.getInventory().getItem(HORSE_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Horse = false;
            		break;
            	case PHANTOM_TRUE:
            		event.getInventory().getItem(PHANTOM_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Phantom = true;
            		break;
            	case PHANTOM_FALSE:
            		event.getInventory().getItem(PHANTOM_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Phantom = false;
            		break;
            	case CREEPER_TRUE:
            		event.getInventory().getItem(CREEPER_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Creeper = true;
            		break;
            	case CREEPER_FALSE:
            		event.getInventory().getItem(CREEPER_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Creeper = false;
            		break;
            	case LANG_CZ:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_CZ).setType(Material.LIME_WOOL);
                    strSetLang = "cs_CZ";
                    break;
                case LANG_DE:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_DE).setType(Material.LIME_WOOL);
                    strSetLang = "de_DE";
                    break;
                case LANG_EN:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_EN).setType(Material.LIME_WOOL);
                    strSetLang = "en_US";
                    break;
                case LANG_FR:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_FR).setType(Material.LIME_WOOL);
                    strSetLang = "fr_FR";
                    break;
                case LANG_LOL:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_LOL).setType(Material.LIME_WOOL);
                    strSetLang = "lol_US";
                    break;
                case LANG_NL:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_NL).setType(Material.LIME_WOOL);
                    strSetLang = "nl_NL";
                    break;
                case LANG_BR:
            		fixLanguages(event);
                    event.getInventory().getItem(LANG_BR).setType(Material.LIME_WOOL);
                    strSetLang = "pt_BR";
                    break;
                case LONGNAME_TRUE:
                	event.getInventory().getItem(LONGNAME_FALSE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Longname = true;
            		break;
                case LONGNAME_FALSE:
                	event.getInventory().getItem(LONGNAME_TRUE).setType(Material.GRAY_STAINED_GLASS_PANE);
            		curItem.setType(Material.LIME_STAINED_GLASS_PANE);
            		Longname = false;
            		break;
                case BTN_SAVE:
                	// Save config.yml
                	player.closeInventory();
                	plugin.saveConfig(update, Debug, Console, Longname, Trader, Pillager, Ender, Ghast, Horse, Phantom, Creeper, strSetLang);
                	break;
                case BTN_CANCEL:
                	// Cancel
                	player.closeInventory();
                	break;
                default:
                	break;
                
            }
        }
    }
	public void fixLanguages(InventoryClickEvent event) {
		for (int i = LANG_CZ; i <= LANG_BR; i++) {
		    if (i != event.getSlot()) { // only update language slots that are not the current one
		        if ( event.getInventory().getItem(i) != null && event.getInventory().getItem(i).getType().name().contains("wool") ) {
		            event.getInventory().getItem(i).setType(Material.GRAY_WOOL);
		        }
		    }
		}
	}
 
}