package com.github.joelgodofwar.neg.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

public class PluginUtils {

	public static Map<String, Object> getInfo(Plugin plugin) {
        Map<String, Object> info = new LinkedHashMap<>();

        // Get plugin information
        PluginDescriptionFile description = plugin.getDescription();
        File file = null;
        try {
	        Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
	        getFileMethod.setAccessible(true);
	        file = new File(((JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("" + description.getName())).getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Plugin name and version
        info.put("Name", description.getName());
        info.put("Version", description.getVersion());

        // File name
        info.put("FileName", "" + file.getName());
        //info.put("FileName", "" + getJarNameByPluginName(description.getName()));

        // Main class
        info.put("Main", description.getMain());

        // Enabled and API version
        info.put("Enabled", plugin.isEnabled());
        info.put("API-Version", description.getAPIVersion());

        // Description, authors, and website
        info.put("Description", description.getDescription());
        info.put("Authors", description.getAuthors());
        info.put("Website", description.getWebsite());

        // Dependencies
        info.put("Depends", description.getDepend());
        info.put("SoftDepends", description.getSoftDepend());

        // Commands and permissions
        Map<String, Object> commands = new LinkedHashMap<>();
        Map<String, Map<String, Object>> pluginCommands = plugin.getDescription().getCommands();
        for (Map.Entry<String, Map<String, Object>> entry : pluginCommands.entrySet()) {
            Map<String, Object> commandInfo = new LinkedHashMap<>();
            commandInfo.put("description", entry.getValue().get("description"));
            commandInfo.put("usage", entry.getValue().get("usage"));
            commandInfo.put("permission", entry.getValue().get("permission"));
            commandInfo.put("permission message", entry.getValue().get("permission message"));
            commandInfo.put("aliases", entry.getValue().get("aliases"));
            commands.put(entry.getKey(), commandInfo);
        }
        info.put("Commands", commands);

        // Load order and provides
        info.put("Load", description.getLoad());
        info.put("LoadBefore", description.getLoadBefore());
        info.put("Provides", description.getProvides());

        return info;
    }
	
	private static Map<String, String> pluginJarNames = new HashMap<>();

    public static void loadPluginJarNames() {
        File pluginsFolder = new File("plugins");
        if (!pluginsFolder.exists() || !pluginsFolder.isDirectory()) {
            return;
        }
        File[] jarFiles = pluginsFolder.listFiles(file -> file.isFile() && file.getName().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            return;
        }
        for (File jarFile : jarFiles) {
            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry pluginYmlEntry = jar.getJarEntry("plugin.yml");
                if (pluginYmlEntry == null) {
                    continue;
                }
                try (InputStream is = jar.getInputStream(pluginYmlEntry)) {
                    Yaml yaml = new Yaml();
                    Map<String, Object> pluginData = yaml.load(is);
                    String pluginName = pluginData.get("name").toString();
                    String mainClass = pluginData.get("main").toString();
                    pluginJarNames.put(pluginName.toLowerCase(), jarFile.getName());
                    pluginJarNames.put(mainClass.toLowerCase(), jarFile.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getJarNameByPluginName(String pluginName) {
        return pluginJarNames.get(pluginName.toLowerCase());
    }

    public static String getJarNameByMainClass(String mainClass) {
        return pluginJarNames.get(mainClass.toLowerCase());
    }
}
