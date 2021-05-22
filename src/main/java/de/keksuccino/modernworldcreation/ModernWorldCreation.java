package de.keksuccino.modernworldcreation;

import java.io.File;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.fml.common.Mod;

@Mod("modernworldcreation")
public class ModernWorldCreation {
	
	public static final String VERSION = "1.0.0";
	
	public static final File HOME_DIR = new File("config/modernworldcreation");
	
	public static Config config;
	
	public ModernWorldCreation() {
		
		//TODO client-side handling einbauen
		
		if (!HOME_DIR.exists()) {
			HOME_DIR.mkdirs();
		}
		
		updateConfig();
		
	}
	
	public static void updateConfig() {
		
		try {
			
			config = new Config(HOME_DIR.getPath() + "/config.cfg");
			
			//-- VALUES
			
			config.registerValue("show_gamemode_info", true, "general");
			config.registerValue("show_allowcheats_tooltip", true, "general");
			config.registerValue("bold_menu_title", true, "general");
			config.registerValue("show_header", true, "general");
			config.registerValue("show_footer", true, "general");
			config.registerValue("button_border_thickness", 1.0F, "general");
			config.registerValue("button_border_hex_color", "#e0e0e0", "general");
			
			//-- SYNC CONFIG TO FILE
			
			config.syncConfig();
			
			//-- UPDATE OLD CATEGORIES
			
			config.setCategory("show_gamemode_info", "general");
			config.setCategory("show_allowcheats_tooltip", "general");
			config.setCategory("bold_menu_title", "general");
			config.setCategory("show_header", "general");
			config.setCategory("show_footer", "general");
			config.setCategory("button_border_thickness", "general");
			config.setCategory("button_border_hex_color", "general");
			
			//-- CLEAR UNUSED VALUES FROM FILE
			
			config.clearUnusedValues();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
