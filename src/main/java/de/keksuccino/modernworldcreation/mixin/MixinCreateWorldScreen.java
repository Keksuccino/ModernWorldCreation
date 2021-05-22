package de.keksuccino.modernworldcreation.mixin;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils;
import de.keksuccino.modernworldcreation.ToggleModeButton;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils.Direction;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils.Gamemode;
import de.keksuccino.modernworldcreation.ModernWorldCreation;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen {

	private static final ITextComponent ENTER_SEED_TEXT = new TranslationTextComponent("selectWorld.enterSeed");
	private static final ITextComponent DESC_ENTER_SEED = new TranslationTextComponent("selectWorld.seedInfo");
	private static final ITextComponent ENTER_WORLD_TEXT = new TranslationTextComponent("selectWorld.enterName");
//	private static final ITextComponent SAVE_WORLD_AS_TEXT = new TranslationTextComponent("selectWorld.resultFolder");
	
	private static final ITextComponent DESC_ALLOW_CHEATS = new TranslationTextComponent("selectWorld.allowCommands.info");
	
	private static final ITextComponent DESC_GAMEMODE_SURVIVAL_LINE1 = new TranslationTextComponent("selectWorld.gameMode.survival.line1");
	private static final ITextComponent DESC_GAMEMODE_SURVIVAL_LINE2 = new TranslationTextComponent("selectWorld.gameMode.survival.line2");
	private static final ITextComponent DESC_GAMEMODE_CREATIVE_LINE1 = new TranslationTextComponent("selectWorld.gameMode.creative.line1");
	private static final ITextComponent DESC_GAMEMODE_CREATIVE_LINE2 = new TranslationTextComponent("selectWorld.gameMode.creative.line2");
	private static final ITextComponent DESC_GAMEMODE_HARDCORE_LINE1 = new TranslationTextComponent("selectWorld.gameMode.hardcore.line1");
	private static final ITextComponent DESC_GAMEMODE_HARDCORE_LINE2 = new TranslationTextComponent("selectWorld.gameMode.hardcore.line2");
	
	private static final ResourceLocation BTN_TEXTURE_SURVIVAL = new ResourceLocation("modernworldcreation", "gamemodes/background_survival.png");
	private static final ResourceLocation BTN_TEXTURE_CREATIVE = new ResourceLocation("modernworldcreation", "gamemodes/background_creative.png");
	private static final ResourceLocation BTN_TEXTURE_HARDCORE = new ResourceLocation("modernworldcreation", "gamemodes/background_hardcore.png");
	
	private static final ResourceLocation BTN_TEXTURE_ARROW_LEFT_NORMAL = new ResourceLocation("modernworldcreation", "arrow_left_normal.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_RIGHT_NORMAL = new ResourceLocation("modernworldcreation", "arrow_right_normal.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_LEFT_HOVER = new ResourceLocation("modernworldcreation", "arrow_left_hover.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_RIGHT_HOVER = new ResourceLocation("modernworldcreation", "arrow_right_hover.png");
	
	@Shadow private TextFieldWidget worldNameField;
	@Shadow private boolean inMoreWorldOptionsDisplay;
	@Shadow private WorldOptionsScreen field_238934_c_;
	@Shadow private String saveDirName;
	@Shadow private boolean allowCheatsWasSetByUser;
	@Shadow public boolean hardCoreMode;
	@Shadow private boolean allowCheats;
	
	@Shadow private Button btnAllowCommands;
	@Shadow private Button btnGameMode;
	@Shadow private Button field_238929_E_; //difficultyButton
	@Shadow private Button btnMoreOptions;
	@Shadow private Button field_238931_H_; //datapacksButton
	@Shadow private Button field_238930_G_; //gamerulesButton
	@Shadow private Button btnCreateWorld;
	
	private ToggleModeButton gamemodeSurvivalBtn;
	private ToggleModeButton gamemodeCreativeBtn;
	private ToggleModeButton gamemodeHardcoreBtn;
	
	private AdvancedButton gamemodeLeftBtn;
	private AdvancedButton gamemodeRightBtn;
	
	private Widget cancelButton = null;
	private AdvancedButton backButton;
	
	private Color headerFooterColor;
	
	protected MixinCreateWorldScreen(ITextComponent titleIn) {
		super(titleIn);
	}
	
	@Inject(at = @At("TAIL"), method = "init()V", cancellable = true)
	private void onInit(CallbackInfo info) {
		
		this.headerFooterColor = new Color(0, 0, 0, 190);
		
		this.cancelButton = null;
		for (Widget b : this.buttons) {
			if (b.getMessage().getString().equals(DialogTexts.GUI_CANCEL.getString())) {
				cancelButton = b;
				break;
			}
		}
		
		this.buttons.remove(this.btnGameMode);
		this.children.remove(this.btnGameMode);
		
		this.buttons.remove(this.btnAllowCommands);
		this.children.remove(this.btnAllowCommands);
		
		this.worldNameField.y = 40;
		
		int midX = this.width / 2;
		int topY = this.worldNameField.y;
		
		this.gamemodeLeftBtn = this.addButton(new AdvancedButton(midX - 145 - 40 - 10, topY + 38 + (45 / 2) - 20, 40, 40, "", (press) -> {
			this.switchToNextGamemode(Direction.LEFT);
		}));
		this.gamemodeLeftBtn.setBackgroundTexture(BTN_TEXTURE_ARROW_LEFT_NORMAL, BTN_TEXTURE_ARROW_LEFT_HOVER);
		
		this.gamemodeRightBtn = this.addButton(new AdvancedButton(midX + 55 + 90 + 10, topY + 38 + (45 / 2) - 20, 40, 40, "", (press) -> {
			this.switchToNextGamemode(Direction.RIGHT);
		}));
		this.gamemodeRightBtn.setBackgroundTexture(BTN_TEXTURE_ARROW_RIGHT_NORMAL, BTN_TEXTURE_ARROW_RIGHT_HOVER);
		
		Gamemode cachedSelectedGamemode = this.getVanillaGameMode();
		
		this.gamemodeSurvivalBtn = this.addButton(new ToggleModeButton(midX - 145, topY + 38, 90, 45, BTN_TEXTURE_SURVIVAL, "Survival", false, (press) -> {
			this.setGamemode(Gamemode.SURVIVAL);
		}));
		this.gamemodeSurvivalBtn.setInfoText(DESC_GAMEMODE_SURVIVAL_LINE1.getString(), DESC_GAMEMODE_SURVIVAL_LINE2.getString());
		if (cachedSelectedGamemode == Gamemode.SURVIVAL) {
			this.gamemodeSurvivalBtn.setSelected(true);
		}
		
		this.gamemodeCreativeBtn = this.addButton(new ToggleModeButton(midX - 45, topY + 38, 90, 45, BTN_TEXTURE_CREATIVE, "Creative", false, (press) -> {
			this.setGamemode(Gamemode.CREATIVE);
		}));
		this.gamemodeCreativeBtn.setInfoText(DESC_GAMEMODE_CREATIVE_LINE1.getString(), DESC_GAMEMODE_CREATIVE_LINE2.getString());
		if (cachedSelectedGamemode == Gamemode.CREATIVE) {
			this.gamemodeCreativeBtn.setSelected(true);
		}
		
		this.gamemodeHardcoreBtn = this.addButton(new ToggleModeButton(midX + 55, topY + 38, 90, 45, BTN_TEXTURE_HARDCORE, "Hardcore", false, (press) -> {
			this.setGamemode(Gamemode.HARDCORE);
		}));
		this.gamemodeHardcoreBtn.setInfoText(DESC_GAMEMODE_HARDCORE_LINE1.getString(), DESC_GAMEMODE_HARDCORE_LINE2.getString());
		if (cachedSelectedGamemode == Gamemode.HARDCORE) {
			this.gamemodeHardcoreBtn.setSelected(true);
		}
		
		if (!ModernWorldCreation.config.getOrDefault("show_gamemode_info", true)) {
			this.gamemodeSurvivalBtn.showInfo = false;
			this.gamemodeCreativeBtn.showInfo = false;
			this.gamemodeHardcoreBtn.showInfo = false;
		}
		
		//Data Packs Button
		this.field_238931_H_.x = midX + 5;
		this.field_238931_H_.y = topY + 38 + 50 + 10;

		//Difficulty Button
		this.field_238929_E_.x = midX - this.field_238931_H_.getWidth() - 5;
		this.field_238929_E_.y = topY + 38 + 50 + 10;
		
		//Allow Cheats Button
		int allowCheatsX = midX - this.btnAllowCommands.getWidth() - 5;
		int allowCheatsY = topY + 38 + 50 + 10 + 20 + 4;
		this.btnAllowCommands = this.addButton(new AdvancedButton(allowCheatsX, allowCheatsY, 150, 20, new TranslationTextComponent("selectWorld.allowCommands").getString(), false, (press) -> {
			this.allowCheatsWasSetByUser = true;
			this.allowCheats = !this.allowCheats;
			press.queueNarration(250);
		}) {
			public ITextComponent getMessage() {
				return DialogTexts.getComposedOptionMessage(super.getMessage(), MixinCreateWorldScreen.this.allowCheats && !MixinCreateWorldScreen.this.hardCoreMode);
			}

			protected IFormattableTextComponent getNarrationMessage() {
				return super.getNarrationMessage().appendString(". ").append(new TranslationTextComponent("selectWorld.allowCommands.info"));
			}
		});
		if (ModernWorldCreation.config.getOrDefault("show_allowcheats_tooltip", false)) {
			((AdvancedButton)this.btnAllowCommands).setDescription(DESC_ALLOW_CHEATS.getString());
		}
		
		//Gamerules Button
		this.field_238930_G_.x = midX + 5;
		this.field_238930_G_.y = topY + 38 + 50 + 10 + 20 + 4;
		
		//More World Options Button
		this.btnMoreOptions.x = midX - (this.btnMoreOptions.getWidth() / 2);
		this.btnMoreOptions.y = topY + 38 + 50 + 10 + 20 + 4 + 20 + 4;
		
		//Back Button (More World Options -> Normal Menu)
		this.backButton = this.addButton(new AdvancedButton((this.width / 2) + 5, this.height - 28 + 3, 150, 20, DialogTexts.GUI_BACK.getString(), false, (press) -> {
			this.toggleMoreWorldOptions();
		}));
		
		//Handle big screen layout
		if ((this.btnCreateWorld.y - this.btnMoreOptions.y) >= 100) {
			
			this.gamemodeSurvivalBtn.y += 10;
			this.gamemodeCreativeBtn.y += 10;
			this.gamemodeHardcoreBtn.y += 10;
			
			this.gamemodeLeftBtn.y += 10;
			this.gamemodeRightBtn.y += 10;
			
			this.field_238929_E_.y += 20;
			this.field_238931_H_.y += 20;
			
			this.btnAllowCommands.y += 20;
			this.field_238930_G_.y += 20;
			
			this.btnMoreOptions.y += 50;

		}
		
		this.btnCreateWorld.y += 3;
		if (this.cancelButton != null) {
			this.cancelButton.y += 3;
		}
		
	}
	
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(MatrixStack matrix, int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
		info.cancel();
		
		this.renderBackground(matrix);
		
		if (this.inMoreWorldOptionsDisplay) {
			this.gamemodeSurvivalBtn.visible = false;
			this.gamemodeCreativeBtn.visible = false;
			this.gamemodeHardcoreBtn.visible = false;
			this.btnAllowCommands.visible = false;
			this.cancelButton.visible = false;
			this.btnMoreOptions.visible = false;
			this.backButton.visible = true;
			this.gamemodeLeftBtn.visible = false;
			this.gamemodeRightBtn.visible = false;
		} else {
			this.gamemodeSurvivalBtn.visible = true;
			this.gamemodeCreativeBtn.visible = true;
			this.gamemodeHardcoreBtn.visible = true;
			this.btnAllowCommands.visible = true;
			this.cancelButton.visible = true;
			this.btnMoreOptions.visible = true;
			this.backButton.visible = false;
			this.gamemodeLeftBtn.visible = true;
			this.gamemodeRightBtn.visible = true;
		}
		
		//Render header
		if (ModernWorldCreation.config.getOrDefault("show_header", true)) {
			fill(matrix, 0, 0, this.width, 10 + 12, this.headerFooterColor.getRGB());
		}
		
		//Render title
		if (ModernWorldCreation.config.getOrDefault("bold_menu_title", true)) {
			String titleString = "§l" + this.title.getString();
			int titleWidth = font.getStringWidth(titleString);
			font.drawString(matrix, titleString, (this.width / 2) - (titleWidth / 2), 7, -1);
		} else {
			drawCenteredString(matrix, this.font, this.title, this.width / 2, 7, -1);
		}
		
		if (this.inMoreWorldOptionsDisplay) {
			
			drawString(matrix, this.font, ENTER_SEED_TEXT, this.width / 2 - 100, 47, -6250336);
			drawString(matrix, this.font, DESC_ENTER_SEED, this.width / 2 - 100, 85, -6250336);
			this.field_238934_c_.render(matrix, mouseX, mouseY, partialTicks);
			
		} else {
			
			drawString(matrix, this.font, ENTER_WORLD_TEXT, this.width / 2 - 100, this.worldNameField.y - 12, -6250336);
			this.worldNameField.render(matrix, mouseX, mouseY, partialTicks);
			
		}
		
		//Render footer
		if (ModernWorldCreation.config.getOrDefault("show_footer", true)) {
			fill(matrix, 0, this.btnCreateWorld.y - 5, this.width, this.height, this.headerFooterColor.getRGB());
		}
		
		for(int i = 0; i < this.buttons.size(); ++i) {
			this.buttons.get(i).render(matrix, mouseX, mouseY, partialTicks);
		}
		
	}
	
	private void switchToNextGamemode(Direction direction) {
		if (direction == Direction.LEFT) {
			if (this.getSelectedGamemode() == Gamemode.SURVIVAL) {
				this.setGamemode(Gamemode.HARDCORE);
			} else if (this.getSelectedGamemode() == Gamemode.CREATIVE) {
				this.setGamemode(Gamemode.SURVIVAL);
			} else if (this.getSelectedGamemode() == Gamemode.HARDCORE) {
				this.setGamemode(Gamemode.CREATIVE);
			}
		}
		if (direction == Direction.RIGHT) {
			if (this.getSelectedGamemode() == Gamemode.SURVIVAL) {
				this.setGamemode(Gamemode.CREATIVE);
			} else if (this.getSelectedGamemode() == Gamemode.CREATIVE) {
				this.setGamemode(Gamemode.HARDCORE);
			} else if (this.getSelectedGamemode() == Gamemode.HARDCORE) {
				this.setGamemode(Gamemode.SURVIVAL);
			}
		}
	}
	
	private void setGamemode(Gamemode gamemode) {
		if (gamemode == Gamemode.CREATIVE) {
			this.gamemodeCreativeBtn.setSelected(true);
			this.gamemodeSurvivalBtn.setSelected(false);
			this.gamemodeHardcoreBtn.setSelected(false);
		} else if (gamemode == Gamemode.HARDCORE) {
			this.gamemodeHardcoreBtn.setSelected(true);
			this.gamemodeSurvivalBtn.setSelected(false);
			this.gamemodeCreativeBtn.setSelected(false);
		} else {
			this.gamemodeSurvivalBtn.setSelected(true);
			this.gamemodeCreativeBtn.setSelected(false);
			this.gamemodeHardcoreBtn.setSelected(false);
		}
		this.setVanillaGameMode(gamemode);
	}
	
	private Gamemode getSelectedGamemode() {
		if (this.gamemodeCreativeBtn.isSelected()) {
			return Gamemode.CREATIVE;
		}
		if (this.gamemodeHardcoreBtn.isSelected()) {
			return Gamemode.HARDCORE;
		}
		return Gamemode.SURVIVAL;
	}
	
	private void setVanillaGameMode(Gamemode gm) {
		try {
			Method m = ObfuscationReflectionHelper.findMethod(CreateWorldScreen.class, "func_228200_a_", CreateWorldScreenUtils.getVanillaGameModeEnumClass());
			m.invoke(this, CreateWorldScreenUtils.getVanillaGameModeEnumElement(gm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Gamemode getVanillaGameMode() {
		try {
			Field f = ObfuscationReflectionHelper.findField(CreateWorldScreen.class, "field_228197_f_");
			Object mode = f.get(this);
			
			if (mode != null) {
				Field f2 = CreateWorldScreenUtils.getVanillaGameModeEnumClass().getDeclaredField("field_228217_e_");
				f2.setAccessible(true);
				String modeString = (String) f2.get(mode);
				if (modeString != null) {
					if (modeString.equals("survival")) {
						return Gamemode.SURVIVAL;
					}
					if (modeString.equals("hardcore")) {
						return Gamemode.HARDCORE;
					}
					if (modeString.equals("creative")) {
						return Gamemode.CREATIVE;
					}
					return Gamemode.SURVIVAL;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Shadow protected abstract void toggleMoreWorldOptions();

}
