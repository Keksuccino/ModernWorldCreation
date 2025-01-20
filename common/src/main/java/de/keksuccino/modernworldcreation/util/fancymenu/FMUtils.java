package de.keksuccino.modernworldcreation.util.fancymenu;

import de.keksuccino.modernworldcreation.ModernWorldCreation;

public class FMUtils {

    public static boolean isFancyMenuLoaded() {
        try {
            Class.forName("de.keksuccino.fancymenu.FancyMenu", false, ModernWorldCreation.class.getClassLoader());
            return true;
        } catch (Exception ignored) {}
        return false;
    }

}
