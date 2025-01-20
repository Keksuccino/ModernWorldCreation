package de.keksuccino.modernworldcreation.util.fancymenu;

import de.keksuccino.fancymenu.util.rendering.ui.widget.UniqueWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.NotNull;

public class FMAccess {

    @NotNull
    public static <T extends AbstractWidget> T setUniqueIdentifierToWidget(@NotNull T widget, @NotNull String identifier) {
        if (widget instanceof UniqueWidget w) {
            w.setWidgetIdentifierFancyMenu(identifier);
        }
        return widget;
    }

}
