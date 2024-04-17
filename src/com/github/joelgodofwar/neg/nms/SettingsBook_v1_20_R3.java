package com.github.joelgodofwar.neg.nms;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class SettingsBook_v1_20_R3 implements SettingsBook{


	@Override public ItemStack giveBook() {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK); //Create bukkit book
		net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(book); //Get the nms version
		NBTTagCompound tag = new NBTTagCompound(); //Create the NMS Stack's NBT (item data)
		tag.setString("title", "NoEndermanGrief - Settings"); //Set the book's title
		tag.setString("author", "JoelGodOfWar"); //Set the book's author
		NBTTagList pages = new NBTTagList();
		pages.add(NBTTagString.a("[{\"text\":\"Enderman Grief: \"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg eg false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable \",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg eg true\"}},{\"text\":\"\\n\",\"color\":\"reset\"},{\"text\":\"Skeleton Horse Spawn: \",\"color\":\"black\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg sh false\"}},{\"text\":\"/\",\"color\":\"black\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg sh true\"}},{\"text\":\" \\nCreeper Grief: \",\"color\":\"reset\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg cg false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg cg true\"}},{\"text\":\"\\nWandering Trader: \",\"color\":\"reset\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg wt false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg wt true\"}},{\"text\":\"\\nGhast Grief: \",\"color\":\"reset\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg gg false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg gg true\"}},{\"text\":\"\\nPhantom Spawn: \",\"color\":\"reset\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg pg false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg pg true\"}},{\"text\":\"\\nPillager Patrol: \",\"color\":\"reset\"},{\"text\":\"Enable\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg pp false\"}},{\"text\":\"/\",\"color\":\"reset\"},{\"text\":\"Disable\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/neg pp true\"}}]"));
		tag.set("pages", pages); //Add the pages to the tag
		stack.setTag(tag); //Apply the tag to the item
		ItemStack is = CraftItemStack.asCraftMirror(stack); //Get the bukkit version of the stack
		return is;
	}
}
