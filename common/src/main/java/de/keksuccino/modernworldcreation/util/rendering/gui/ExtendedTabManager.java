package de.keksuccino.modernworldcreation.util.rendering.gui;

import net.minecraft.client.gui.components.tabs.Tab;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public interface ExtendedTabManager {

    void setTabChangeListener_ModernWorldCreation(@Nullable Consumer<Tab> listener);

    boolean hasTabChangeListener_ModernWorldCreation();

}
