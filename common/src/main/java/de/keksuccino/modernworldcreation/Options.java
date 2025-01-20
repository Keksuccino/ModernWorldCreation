package de.keksuccino.modernworldcreation;

import de.keksuccino.modernworldcreation.util.AbstractOptions;
import de.keksuccino.konkrete.config.Config;

public class Options extends AbstractOptions {

    protected final Config config = new Config(ModernWorldCreation.MOD_DIR.getAbsolutePath().replace("\\", "/") + "/config.txt");

    public final Option<Boolean> boldMenuTitle = new Option<>(config, "bold_menu_title", true, "general");
    public final Option<Boolean> showGameModeInfo = new Option<>(config, "show_gamemode_info", true, "general");
    public final Option<Float> buttonBorderThickness = new Option<>(config, "button_border_thickness", 1.0F, "general");
    public final Option<String> buttonBorderHexColor = new Option<>(config, "button_border_hex_color", "#e0e0e0", "general");

    public Options() {
        this.config.syncConfig();
        this.config.clearUnusedValues();
    }

}
