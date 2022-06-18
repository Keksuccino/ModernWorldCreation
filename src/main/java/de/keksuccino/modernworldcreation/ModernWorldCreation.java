package de.keksuccino.modernworldcreation;

import java.io.File;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("modernworldcreation")
public class ModernWorldCreation {
	
	public static final String VERSION = "1.0.0";
	
	public static final File HOME_DIR = new File("config/modernworldcreation");

	public static final Logger LOGGER = LogManager.getLogger("modernworldcreation/ModernWorldCreation");
	
	public static Config config;
	
	public ModernWorldCreation() {
		
		if (FMLEnvironment.dist == Dist.CLIENT) {
			
			if (!HOME_DIR.exists()) {
				HOME_DIR.mkdirs();
			}

			updateConfig();
			
		} else {
			LOGGER.warn("WARNING: 'Modern World Creation' is a client mod and has no effect when loaded on a server!");
		}

	}
	
	public static void updateConfig() {
		
		try {
			
			config = new Config(HOME_DIR.getPath() + "/config.cfg");
			
			config.registerValue("show_gamemode_info", true, "general");
			config.registerValue("show_allowcheats_tooltip", true, "general");
			config.registerValue("bold_menu_title", true, "general");
			config.registerValue("show_header", true, "general");
			config.registerValue("show_footer", true, "general");
			config.registerValue("button_border_thickness", 1.0F, "general");
			config.registerValue("button_border_hex_color", "#e0e0e0", "general");
			
			config.syncConfig();
			
			config.clearUnusedValues();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void printStackTrace(Exception e) {
		for (StackTraceElement s : e.getStackTrace()) {
			LOGGER.error(s.toString());
		}
	}

}
