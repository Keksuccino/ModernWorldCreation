package de.keksuccino.modernworldcreation;

public class CreateWorldScreenUtils {
	
	public static enum Gamemode {
		SURVIVAL,
		CREATIVE,
		HARDCORE;
	}
	
	public static enum Direction {
		LEFT,
		RIGHT;
	}
	
	public static Object getVanillaGameModeEnumElement(Gamemode gm) {
		try {
			Class<?> c = getVanillaGameModeEnumClass();
			Object[] enumElements = c.getEnumConstants();
			if (gm == Gamemode.HARDCORE) {
				return enumElements[1];
			} else if (gm == Gamemode.CREATIVE) {
				return enumElements[2];
			} else {
				return enumElements[0];
			}
		} catch (Exception e) {
			ModernWorldCreation.LOGGER.error("ERROR IN: CreateWorldScreenUtils#getVanillaGameModeEnumElement");
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getVanillaGameModeEnumClass() {
		try {
			return Class.forName("net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$SelectedGameMode");
		} catch (ClassNotFoundException e) {
			try {
				return Class.forName("net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$SelectedGameMode");
			} catch (ClassNotFoundException e2) {
				ModernWorldCreation.LOGGER.error("ERROR IN: CreateWorldScreenUtils#getVanillaGameModeEnumClass");
				e2.printStackTrace();
			}
		}
		return null;
	}

}
