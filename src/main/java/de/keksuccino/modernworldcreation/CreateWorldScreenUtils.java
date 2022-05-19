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
			e.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getVanillaGameModeEnumClass() {
		try {
			return Class.forName("net.minecraft.client.gui.screen.CreateWorldScreen$GameMode");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
