package de.keksuccino.modernworldcreation.util.rendering.screens;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface InitializableCreateWorldScreen {

    void setOnInitBody_ModernWorldCreation(@Nullable Consumer<CreateWorldScreen> body);

}
