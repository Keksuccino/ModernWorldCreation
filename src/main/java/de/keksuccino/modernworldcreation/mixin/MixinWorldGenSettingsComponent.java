package de.keksuccino.modernworldcreation.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;

@Mixin(value = WorldGenSettingsComponent.class)
public abstract class MixinWorldGenSettingsComponent {

	@Shadow private int width;
	
	@Shadow private Button importSettingsButton;
	
	@Inject(at = @At(value = "TAIL"), method = "init")
	protected void onInit(CreateWorldScreen s, Minecraft mc, Font font, CallbackInfo info) {
		
		int j = this.width / 2 + 5;
		
		this.importSettingsButton.x = j;
		this.importSettingsButton.y = 151;
		
	}
	
}
