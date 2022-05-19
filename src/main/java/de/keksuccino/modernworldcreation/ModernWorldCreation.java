package de.keksuccino.modernworldcreation;

import java.io.File;

import org.apache.commons.lang3.tuple.Pair;

import de.keksuccino.konkrete.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod("modernworldcreation")
public class ModernWorldCreation {
	
	public static final String VERSION = "1.0.0";
	
	public static final File HOME_DIR = new File("config/modernworldcreation");
	
	public static Config config;
	
	public ModernWorldCreation() {
		
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		
		if (FMLEnvironment.dist == Dist.CLIENT) {
			
			if (!HOME_DIR.exists()) {
				HOME_DIR.mkdirs();
			}

			updateConfig();
			
		} else {
			System.out.println("## WARNING ## 'Modern World Creation' is a client mod and has no effect when loaded on a server!");
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

}
