package de.keksuccino.modernworldcreation;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.konkrete.gui.content.AdvancedImageButton;
import de.keksuccino.konkrete.rendering.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ToggleModeButton extends AdvancedButton {
	
	protected static final ResourceLocation INFO_BACK_TEXTURE = new ResourceLocation("modernworldcreation", "info_back.png");

	protected ResourceLocation texture;
	protected String label;
	protected boolean selected = false;
	protected int animationTicker = 1;
	protected Color labelBackgroundColor;
	public int addToHeightWhenHovered = 10;
	public int labelBackgroundHeight = 16;
	public boolean darkenWhenUnfocused = true;
	
	public boolean showInfo = true;
	protected AdvancedImageButton infoButton;
	
	protected Color borderColor;
	
	public ToggleModeButton(int x, int y, int widthIn, int heightIn, ResourceLocation texture, String label, boolean handleClick, OnPress onPress) {
		super(x, y, widthIn, heightIn, "", handleClick, onPress);
		this.texture = texture;
		this.label = label;
		this.labelBackgroundColor = new Color(0, 0, 0, 140);
		
		Color c = new Color(0, 0, 0, 0);
		this.setBackgroundColor(c, c, c, c, 0);
		
		this.infoButton = new AdvancedImageButton(0, 0, 10, 10, INFO_BACK_TEXTURE, (press) -> {
		});
		this.infoButton.active = false;
		
		Color infoBack = new Color(0, 0, 0, 255);
		Color infoBorder = new Color(224, 224, 224, 255);
		this.infoButton.setBackgroundColor(infoBack, infoBack, infoBorder, infoBorder, 0.4F);
		
		this.borderColor = RenderUtils.getColorFromHexString(ModernWorldCreation.config.getOrDefault("button_border_hex_color", "#ffffff"));
		if (this.borderColor == null) {
			this.borderColor = new Color(255, 255, 255, 255);
		}
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		
		if (!this.visible) {
			return;
		}
		
		if (this.showInfo) {
			this.infoButton.visible = true;
		} else {
			this.infoButton.visible = false;
		}
		
		int w = this.width;
		int h = this.height;
		int x = this.x;
		int y = this.y;
		Font font = Minecraft.getInstance().font;

		if (this.isHovered() || this.selected) {
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
		
		this.infoButton.x = this.x + this.width - this.infoButton.getWidth() - 2;
		if (this.animationTicker > 1) {
			this.infoButton.y = this.y - (this.animationTicker / 2) + 2;
		} else {
			this.infoButton.y = this.y + 2;
		}
		
		super.render(matrix, mouseX, mouseY, partialTicks);
		
		RenderUtils.bindTexture(this.texture);

		if (!this.isHovered() && !this.selected && this.darkenWhenUnfocused) {
			RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1.0F);
		} else {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
		blit(matrix, x, y, 0.0F, 0.0F, w, h, this.width, this.height + this.addToHeightWhenHovered);
		
		//Draw black label background
		int backY = y + h - 10 - labelBackgroundHeight;
		if (this.animationTicker > 1) {
			backY += (this.animationTicker / 2);
		}
		fill(matrix, x, backY, x + w, backY + labelBackgroundHeight, this.labelBackgroundColor.getRGB());
		
		String labelString = "§f§l" + this.label;
		TextComponent comp = new TextComponent(labelString);
		int sWidth = font.width(comp);
		int sHeight = font.lineHeight;
		int sX = x + (w / 2) - (sWidth / 2);
		int sY = y + h - 10 - (labelBackgroundHeight / 2) - (sHeight / 2);
		if (this.animationTicker > 1) {
			sY += (this.animationTicker / 2);
		}
		
		//Draw label
		font.draw(matrix, comp.getVisualOrderText(), sX, sY, 0);
		
		this.renderBorder(matrix);
		
		if (this.showInfo) {
			this.infoButton.render(matrix, mouseX, mouseY, partialTicks);
		}
		
	}
	
	protected void renderBorder(PoseStack matrix) {
		float thickness = ModernWorldCreation.config.getOrDefault("button_border_thickness", 0.7F);
		int bY = this.y;
		int heightOffset = 0;
		if (this.animationTicker > 1) {
			bY -= this.animationTicker / 2;
			heightOffset += this.animationTicker;
		}
		//top
		RenderUtils.fill(matrix, this.x, bY - thickness, this.x + this.width, bY, this.borderColor.getRGB(), 1.0F);
		//left
		RenderUtils.fill(matrix, this.x - thickness, bY - thickness, this.x, bY + this.height + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
		//bottom
		RenderUtils.fill(matrix, this.x, bY + this.height + heightOffset, this.x + this.width, bY + this.height + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
		//right
		RenderUtils.fill(matrix, this.x + this.width, bY - thickness, this.x + this.width + thickness, bY + this.height + thickness + heightOffset, this.borderColor.getRGB(), 1.0F);
	}

	public void setImage(ResourceLocation texture) {
		this.texture = texture;
	}
	
	public void setSelected(boolean b) {
		this.selected = b;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
	
	public void setInfoText(String... info) {
		this.infoButton.setDescription(info);
	}

}
