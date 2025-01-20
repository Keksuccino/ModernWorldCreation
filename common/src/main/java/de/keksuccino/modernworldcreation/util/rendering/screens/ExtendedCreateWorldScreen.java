package de.keksuccino.modernworldcreation.util.rendering.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface ExtendedCreateWorldScreen {

    void setOnInitBody_ModernWorldCreation(@Nullable Consumer<CreateWorldScreen> body);

    void postPostRenderTask_ModernWorldCreation(@NotNull MWCRenderTask task);

    @FunctionalInterface
    public static interface MWCRenderTask {
        void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial);
    }

}
