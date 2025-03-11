package de.keksuccino.modernworldcreation.util.rendering.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import de.keksuccino.modernworldcreation.ModernWorldCreation;
import de.keksuccino.konkrete.rendering.RenderUtils;
import de.keksuccino.modernworldcreation.util.rendering.screens.ExtendedCreateWorldScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.List;

public class ToggleModeButton extends Button {
	
	protected static final ResourceLocation INFO_BACKGROUND_TEXTURE = new ResourceLocation("modernworldcreation", "textures/info_back.png");

	@NotNull
	protected final CreateWorldScreen parent;
	@NotNull
	protected ResourceLocation texture;
	@NotNull
	protected Component label;
	protected boolean selected = false;
	protected int animationTicker = 1;
	@NotNull
	protected Color labelBackgroundColor = new Color(0, 0, 0, 140);
	protected int addToHeightWhenHovered = 10;
	protected int labelBackgroundHeight = 16;
	protected boolean darkenWhenUnfocused = true;
	protected boolean showInfo = true;
	@NotNull
	protected Color borderColor;
	@Nullable
	protected List<Component> infoTooltip = null;
	protected int infoX = 0;
	protected int infoY = 0;
	protected int infoWidth = 10;
	protected int infoHeight = 10;
	protected float infoBorderWidth = 0.4F;
	@NotNull
	protected Color infoBackgroundColor = new Color(0, 0, 0, 255);
	@NotNull
	protected Color infoBorderColor = new Color(224, 224, 224, 255);
	protected boolean infoHovered = false;
	
	public ToggleModeButton(@NotNull CreateWorldScreen parent, int x, int y, int width, int height, @NotNull ResourceLocation texture, @NotNull Component label, @NotNull OnPress clickAction) {

		super(x, y, width, height, label, clickAction, DEFAULT_NARRATION);

		this.parent = parent;
		this.texture = texture;
		this.label = label;
		
		this.borderColor = RenderUtils.getColorFromHexString(ModernWorldCreation.getOptions().buttonBorderHexColor.getValue());
		if (this.borderColor == null) {
			this.borderColor = new Color(255, 255, 255, 255);
		}

	}

	@Override
	protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

		this._renderWidget(graphics, mouseX, mouseY, partial);

	}

	protected void _renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {
		
		if (!this.visible) {
			return;
		}
		
		int w = this.getWidth();
		int h = this.getHeight();
		int x = this.getX();
		int y = this.getY();
		Font font = Minecraft.getInstance().font;

		if (this.isHoveredOrFocused() || this.selected) {
			if (this.animationTicker < this.addToHeightWhenHovered) {
				this.animationTicker++;
			}
			
			h += this.animationTicker;
			y -= this.animationTicker / 2;
		} else {
			if (this.animationTicker > 1) {
				h += this.animationTicker;
				y -= this.animationTicker / 2;
				this.animationTicker--;
			}
		}
		
		this.infoX = this.getX() + this.width - this.infoWidth - 2;
		if (this.animationTicker > 1) {
			this.infoY = this.getY() - (this.animationTicker / 2) + 2;
		} else {
			this.infoY = this.getY() + 2;
		}

		if (!this.isHoveredOrFocused() && !this.selected && this.darkenWhenUnfocused) {
			RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, this.alpha);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		}
		graphics.blit(this.texture, x, y, 0.0F, 0.0F, w, h, this.getWidth(), this.getHeight() + this.addToHeightWhenHovered);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		//Draw black label background
		int backY = y + h - 10 - this.labelBackgroundHeight;
		if (this.animationTicker > 1) {
			backY += (this.animationTicker / 2);
		}
		graphics.fill(RenderType.gui(), x, backY, x + w, backY + this.labelBackgroundHeight, this.labelBackgroundColor.getRGB());

		Component labelComponent = this.label.copy().setStyle(Style.EMPTY.withBold(true));
		int sWidth = font.width(labelComponent);
		int sHeight = font.lineHeight;
		int sX = x + (w / 2) - (sWidth / 2);
		int sY = y + h - 10 - (this.labelBackgroundHeight / 2) - (sHeight / 2);
		if (this.animationTicker > 1) {
			sY += (this.animationTicker / 2);
		}
		
		//Draw label
		graphics.drawString(font, labelComponent, sX, sY, -1, false);
		
		this.renderBorder(graphics);
		
		if (this.showInfo) {
			this.renderInfo(graphics, mouseX, mouseY, partial);
		}
		
	}

	protected void renderInfo(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partial) {

		this.infoHovered = isXYInArea(mouseX, mouseY, this.infoX, this.infoY, this.infoWidth, this.infoHeight);

		//Render background
		graphics.fill(this.infoX, this.infoY, this.infoX + this.infoWidth, this.infoY + this.infoHeight, infoBackgroundColor.getRGB());

		graphics.flush();

		//Render border
		//Top
		RenderUtils.fill(graphics, (float)this.infoX, (float)this.infoY, (float)(this.infoX + this.infoWidth), ((float)this.infoY) + this.infoBorderWidth, this.infoBorderColor.getRGB(), this.alpha);
		graphics.flush();
		//Bottom
		RenderUtils.fill(graphics, (float)this.infoX, (float)(this.infoY + this.infoHeight) - infoBorderWidth, (float)(this.infoX + this.infoWidth), (float)(this.infoY + this.infoHeight), infoBorderColor.getRGB(), this.alpha);
		graphics.flush();
		//Left
		RenderUtils.fill(graphics, (float)this.infoX, (float)this.infoY + infoBorderWidth, (float)this.infoX + infoBorderWidth, (float)(this.infoY + this.infoHeight) - infoBorderWidth, infoBorderColor.getRGB(), this.alpha);
		graphics.flush();
		//Right
		RenderUtils.fill(graphics, (float)(this.infoX + this.infoWidth) - infoBorderWidth, (float)this.infoY + infoBorderWidth, (float)(this.infoX + this.infoWidth), (float)(this.infoY + this.infoHeight) - infoBorderWidth, infoBorderColor.getRGB(), this.alpha);
		graphics.flush();

		//Render texture
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
		graphics.blit(INFO_BACKGROUND_TEXTURE, this.infoX, this.infoY, 0.0F, 0.0F, this.infoWidth, this.infoHeight, this.infoWidth, this.infoHeight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		//Render info tooltip after CreateWorldScreen rendering
		if (this.isInfoHovered() && (this.infoTooltip != null)) {
			((ExtendedCreateWorldScreen)this.parent).postPostRenderTask_ModernWorldCreation((graphics1, mouseX1, mouseY1, partial1) -> {
				if (this.infoTooltip.isEmpty()) return;
				if (this.infoTooltip.size() == 1) {
					graphics1.renderTooltip(Minecraft.getInstance().font, Tooltip.splitTooltip(Minecraft.getInstance(), this.infoTooltip.get(0)), mouseX1, mouseY1);
				} else {
					graphics1.renderComponentTooltip(Minecraft.getInstance().font, this.infoTooltip, mouseX1, mouseY1);
				}
			});
		}

	}

	public static boolean isXYInArea(double targetX, double targetY, double x, double y, double width, double height) {
		return (targetX >= x) && (targetX < (x + width)) && (targetY >= y) && (targetY < (y + height));
	}
	
	protected void renderBorder(@NotNull GuiGraphics graphics) {

		float thickness = ModernWorldCreation.getOptions().buttonBorderThickness.getValue();
		int bY = this.getY();
		int heightOffset = 0;
		if (this.animationTicker > 1) {
			bY -= this.animationTicker / 2;
			heightOffset += this.animationTicker;
		}

		graphics.flush();

		//top
		RenderUtils.fill(graphics, this.getX(), bY - thickness, this.getX() + this.getWidth(), bY, this.borderColor.getRGB(), 1.0F);
		graphics.flush();
		//left
		RenderUtils.fill(graphics, this.getX() - thickness, bY - thickness, this.getX(), bY + this.getHeight() + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
		graphics.flush();
		//bottom
		RenderUtils.fill(graphics, this.getX(), bY + this.getHeight() + heightOffset, this.getX() + this.getWidth(), bY + this.getHeight() + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
		graphics.flush();
		//right
		RenderUtils.fill(graphics, this.getX() + this.getWidth(), bY - thickness, this.getX() + this.getWidth() + thickness, bY + this.getHeight() + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
		graphics.flush();

	}

	public void setTexture(@NotNull ResourceLocation texture) {
		this.texture = texture;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public void setInfoTooltip(@Nullable List<Component> tooltip) {
		this.infoTooltip = tooltip;
	}

	@Nullable
	public List<Component> getInfoTooltip() {
		return this.infoTooltip;
	}

	public boolean isInfoHovered() {
		if (!this.showInfo) return false;
		return this.infoHovered;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo = showInfo;
	}

	public boolean isShowInfo() {
		return this.showInfo;
	}

	@NotNull
	@Override
	protected MutableComponent createNarrationMessage() {
		if (this.infoTooltip == null) return super.createNarrationMessage();
		MutableComponent c = super.createNarrationMessage();
		this.infoTooltip.forEach(component -> {
			c.append(" ");
			c.append(component.copy());
		});
		return c;
	}

}
