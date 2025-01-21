package de.keksuccino.modernworldcreation.util.rendering.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ArrowButton extends Button {

    //All textures are 20x20 pixels
    protected static final ResourceLocation ARROW_LEFT_NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/arrow_left_normal.png");
    protected static final ResourceLocation ARROW_LEFT_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/arrow_left_hover.png");
    protected static final ResourceLocation ARROW_RIGHT_NORMAL_TEXTURE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/arrow_right_normal.png");
    protected static final ResourceLocation ARROW_RIGHT_HOVER_TEXTURE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/arrow_right_hover.png");

    protected static final Component ARROW_LEFT_LABEL = Component.translatable("modernworldcreation.arrow_button.left");
    protected static final Component ARROW_RIGHT_LABEL = Component.translatable("modernworldcreation.arrow_button.right");

    @NotNull
    protected ArrowDirection direction;

    public ArrowButton(int x, int y, @NotNull ArrowDirection direction, @NotNull OnPress clickAction) {
        super(x, y, 40, 40, (direction == ArrowDirection.LEFT) ? ARROW_LEFT_LABEL : ARROW_RIGHT_LABEL, clickAction, DEFAULT_NARRATION);
        this.direction = direction;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

        ResourceLocation loc = ARROW_LEFT_NORMAL_TEXTURE;
        if ((this.direction == ArrowDirection.LEFT) && this.isHoveredOrFocused()) loc = ARROW_LEFT_HOVER_TEXTURE;
        if ((this.direction == ArrowDirection.RIGHT) && !this.isHoveredOrFocused()) loc = ARROW_RIGHT_NORMAL_TEXTURE;
        if ((this.direction == ArrowDirection.RIGHT) && this.isHoveredOrFocused()) loc = ARROW_RIGHT_HOVER_TEXTURE;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        graphics.blit(loc, this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

    }

    @NotNull
    public ArrowDirection getDirection() {
        return this.direction;
    }

    public enum ArrowDirection {
        LEFT,
        RIGHT
    }

}
