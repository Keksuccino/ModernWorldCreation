package de.keksuccino.modernworldcreation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.WorldOptionsScreen;
import net.minecraft.client.gui.widget.button.Button;

@Mixin(value = WorldOptionsScreen.class)
public abstract class MixinWorldOptionsScreen {

	@Shadow private int field_239032_f_; //width
	
	@Shadow private Button field_239037_k_; //importSettings Button
	
	@Inject(at = @At(value = "TAIL"), method = "func_239048_a_")
	protected void onInit(CreateWorldScreen s, Minecraft mc, FontRenderer font, CallbackInfo info) {
		
		int j = this.field_239032_f_ / 2 + 5;
		
		this.field_239037_k_.x = j;
		this.field_239037_k_.y = 151;
		
	}
	
}
