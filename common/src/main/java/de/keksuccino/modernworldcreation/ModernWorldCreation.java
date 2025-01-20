package de.keksuccino.modernworldcreation;

import de.keksuccino.modernworldcreation.platform.Services;
import de.keksuccino.modernworldcreation.util.GameDirectoryUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class ModernWorldCreation {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String VERSION = "2.0.0";
    public static final String LOADER = Services.PLATFORM.getPlatformName().toUpperCase();
    public static final String MOD_ID = "modernworldcreation";
    public static final File MOD_DIR = createDirectory(new File(GameDirectoryUtils.getGameDirectory(), "/config/modernworldcreation"));

    private static Options options;

    public static void init() {

        if (Services.PLATFORM.isOnClient()) {

            LOGGER.info("[MODERN WORLD CREATION] Starting version " + VERSION + " on " + Services.PLATFORM.getPlatformDisplayName() + "..");

        } else {

            LOGGER.warn("[MODERN WORLD CREATION] Disabling 'Modern World Creation' since it's a client-side mod and current environment is server!");

        }

    }

    public static void updateOptions() {
        options = new Options();
    }

    @NotNull
    public static Options getOptions() {
        if (options == null) updateOptions();
        return options;
    }

    private static File createDirectory(@NotNull File file) {
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

}
