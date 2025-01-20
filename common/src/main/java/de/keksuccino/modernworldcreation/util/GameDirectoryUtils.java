package de.keksuccino.modernworldcreation.util;

import de.keksuccino.modernworldcreation.platform.Services;
import net.minecraft.client.Minecraft;
import java.io.File;

public class GameDirectoryUtils {

    public static File getGameDirectory() {
        if (Services.PLATFORM.isOnClient()) {
            return Minecraft.getInstance().gameDirectory;
        } else {
            return new File("");
        }
    }

}
