package de.keksuccino.modernworldcreation.mixin;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldGenSettingsComponent;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.keksuccino.konkrete.gui.content.AdvancedButton;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils;
import de.keksuccino.modernworldcreation.ToggleModeButton;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils.Direction;
import de.keksuccino.modernworldcreation.CreateWorldScreenUtils.Gamemode;
import de.keksuccino.modernworldcreation.ModernWorldCreation;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen {

	private static final BaseComponent ENTER_SEED_TEXT = new TranslatableComponent("selectWorld.enterSeed");
	private static final BaseComponent DESC_ENTER_SEED = new TranslatableComponent("selectWorld.seedInfo");
	private static final BaseComponent ENTER_WORLD_TEXT = new TranslatableComponent("selectWorld.enterName");
	
	private static final BaseComponent DESC_ALLOW_CHEATS = new TranslatableComponent("selectWorld.allowCommands.info");
	
	private static final BaseComponent DESC_GAMEMODE_SURVIVAL_LINE1 = new TranslatableComponent("selectWorld.gameMode.survival.line1");
	private static final BaseComponent DESC_GAMEMODE_SURVIVAL_LINE2 = new TranslatableComponent("selectWorld.gameMode.survival.line2");
	private static final BaseComponent DESC_GAMEMODE_CREATIVE_LINE1 = new TranslatableComponent("selectWorld.gameMode.creative.line1");
	private static final BaseComponent DESC_GAMEMODE_CREATIVE_LINE2 = new TranslatableComponent("selectWorld.gameMode.creative.line2");
	private static final BaseComponent DESC_GAMEMODE_HARDCORE_LINE1 = new TranslatableComponent("selectWorld.gameMode.hardcore.line1");
	private static final BaseComponent DESC_GAMEMODE_HARDCORE_LINE2 = new TranslatableComponent("selectWorld.gameMode.hardcore.line2");
	
	private static final ResourceLocation BTN_TEXTURE_SURVIVAL = new ResourceLocation("modernworldcreation", "gamemodes/background_survival.png");
	private static final ResourceLocation BTN_TEXTURE_CREATIVE = new ResourceLocation("modernworldcreation", "gamemodes/background_creative.png");
	private static final ResourceLocation BTN_TEXTURE_HARDCORE = new ResourceLocation("modernworldcreation", "gamemodes/background_hardcore.png");
	
	private static final ResourceLocation BTN_TEXTURE_ARROW_LEFT_NORMAL = new ResourceLocation("modernworldcreation", "arrow_left_normal.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_RIGHT_NORMAL = new ResourceLocation("modernworldcreation", "arrow_right_normal.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_LEFT_HOVER = new ResourceLocation("modernworldcreation", "arrow_left_hover.png");
	private static final ResourceLocation BTN_TEXTURE_ARROW_RIGHT_HOVER = new ResourceLocation("modernworldcreation", "arrow_right_hover.png");
	
	@Shadow private EditBox nameEdit;
	@Shadow private boolean worldGenSettingsVisible;
	@Shadow private WorldGenSettingsComponent worldGenSettingsComponent;
	@Shadow private String resultFolder;
	@Shadow private boolean commandsChanged;
	@Shadow public boolean hardCore;
	@Shadow private boolean commands;
	
	@Shadow private CycleButton commandsButton;
	@Shadow private CycleButton modeButton;
	@Shadow private CycleButton difficultyButton;
	@Shadow private Button moreOptionsButton;
	@Shadow private Button dataPacksButton;
	@Shadow private Button gameRulesButton;
	@Shadow private Button createButton;
	
	private ToggleModeButton gamemodeSurvivalBtn;
	private ToggleModeButton gamemodeCreativeBtn;
	private ToggleModeButton gamemodeHardcoreBtn;
	
	private AdvancedButton gamemodeLeftBtn;
	private AdvancedButton gamemodeRightBtn;
	
	private AbstractWidget cancelButton = null;
	private AdvancedButton customCommandsButton = null;
	private AdvancedButton backButton;
	
	private Color headerFooterColor;
	
	protected MixinCreateWorldScreen(BaseComponent titleIn) {
		super(titleIn);
	}
	
	@Inject(at = @At("TAIL"), method = "init()V", cancellable = true)
	private void onInit(CallbackInfo info) {
		
		this.headerFooterColor = new Color(0, 0, 0, 190);
		
		this.cancelButton = null;
		for (Widget b : this.renderables) {
			if (b instanceof AbstractWidget) {
				if (((AbstractWidget) b).getMessage().getString().equals(CommonComponents.GUI_CANCEL.getString())) {
					this.cancelButton = (AbstractWidget) b;
					break;
				}
			}
		}

		this.removeWidget(this.modeButton);

		this.removeWidget(this.commandsButton);
		
		this.nameEdit.y = 40;
		
		int midX = this.width / 2;
		int topY = this.nameEdit.y;
		
		this.gamemodeLeftBtn = this.addRenderableWidget(new AdvancedButton(midX - 145 - 40 - 10, topY + 38 + (45 / 2) - 20, 40, 40, "", (press) -> {
			this.switchToNextGamemode(Direction.LEFT);
		}));
		this.gamemodeLeftBtn.setBackgroundTexture(BTN_TEXTURE_ARROW_LEFT_NORMAL, BTN_TEXTURE_ARROW_LEFT_HOVER);
		
		this.gamemodeRightBtn = this.addRenderableWidget(new AdvancedButton(midX + 55 + 90 + 10, topY + 38 + (45 / 2) - 20, 40, 40, "", (press) -> {
			this.switchToNextGamemode(Direction.RIGHT);
		}));
		this.gamemodeRightBtn.setBackgroundTexture(BTN_TEXTURE_ARROW_RIGHT_NORMAL, BTN_TEXTURE_ARROW_RIGHT_HOVER);
		
		Gamemode cachedSelectedGamemode = this.getVanillaGameMode();
		
		this.gamemodeSurvivalBtn = this.addRenderableWidget(new ToggleModeButton(midX - 145, topY + 38, 90, 45, BTN_TEXTURE_SURVIVAL, "Survival", false, (press) -> {
			this.setGamemode(Gamemode.SURVIVAL);
		}));
		this.gamemodeSurvivalBtn.setInfoText(DESC_GAMEMODE_SURVIVAL_LINE1.getString(), DESC_GAMEMODE_SURVIVAL_LINE2.getString());
		if (cachedSelectedGamemode == Gamemode.SURVIVAL) {
			this.gamemodeSurvivalBtn.setSelected(true);
		}
		
		this.gamemodeCreativeBtn = this.addRenderableWidget(new ToggleModeButton(midX - 45, topY + 38, 90, 45, BTN_TEXTURE_CREATIVE, "Creative", false, (press) -> {
			this.setGamemode(Gamemode.CREATIVE);
		}));
		this.gamemodeCreativeBtn.setInfoText(DESC_GAMEMODE_CREATIVE_LINE1.getString(), DESC_GAMEMODE_CREATIVE_LINE2.getString());
		if (cachedSelectedGamemode == Gamemode.CREATIVE) {
			this.gamemodeCreativeBtn.setSelected(true);
		}
		
		this.gamemodeHardcoreBtn = this.addRenderableWidget(new ToggleModeButton(midX + 55, topY + 38, 90, 45, BTN_TEXTURE_HARDCORE, "Hardcore", false, (press) -> {
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
		this.dataPacksButton.x = midX + 5;
		this.dataPacksButton.y = topY + 38 + 50 + 10;

		//Difficulty Button
		this.difficultyButton.x = midX - this.dataPacksButton.getWidth() - 5;
		this.difficultyButton.y = topY + 38 + 50 + 10;
		
		//Allow Cheats Button
		int allowCheatsX = midX - this.commandsButton.getWidth() - 5;
		int allowCheatsY = topY + 38 + 50 + 10 + 20 + 4;
		this.customCommandsButton = this.addRenderableWidget(new AdvancedButton(allowCheatsX, allowCheatsY, this.commandsButton.getWidth(), this.commandsButton.getHeight(), new TranslatableComponent("selectWorld.allowCommands").getString(), false, (press) -> {
			this.commandsChanged = true;
			this.commands = !this.commands;
		}) {
			@Override
			public Component getMessage() {
				return optionStatus(super.getMessage(), MixinCreateWorldScreen.this.commands && !MixinCreateWorldScreen.this.hardCore);
			}

			protected MutableComponent createNarrationMessage() {
				return super.createNarrationMessage().append(". ").append(new TranslatableComponent("selectWorld.allowCommands.info"));
			}
		});
		if (ModernWorldCreation.config.getOrDefault("show_allowcheats_tooltip", false)) {
			this.customCommandsButton.setDescription(DESC_ALLOW_CHEATS.getString());
		}
		
		//Gamerules Button
		this.gameRulesButton.x = midX + 5;
		this.gameRulesButton.y = topY + 38 + 50 + 10 + 20 + 4;
		
		//More World Options Button
		this.moreOptionsButton.x = midX - (this.moreOptionsButton.getWidth() / 2);
		this.moreOptionsButton.y = topY + 38 + 50 + 10 + 20 + 4 + 20 + 4;
		
		//Back Button (More World Options -> Normal Menu)
		this.backButton = this.addRenderableWidget(new AdvancedButton((this.width / 2) + 5, this.height - 28 + 3, 150, 20, CommonComponents.GUI_BACK.getString(), false, (press) -> {
			this.toggleWorldGenSettingsVisibility();
		}));
		
		//Handle big screen layout
		if ((this.createButton.y - this.moreOptionsButton.y) >= 100) {
			
			this.gamemodeSurvivalBtn.y += 10;
			this.gamemodeCreativeBtn.y += 10;
			this.gamemodeHardcoreBtn.y += 10;
			
			this.gamemodeLeftBtn.y += 10;
			this.gamemodeRightBtn.y += 10;
			
			this.difficultyButton.y += 20;
			this.dataPacksButton.y += 20;
			
			this.customCommandsButton.y += 20;
			this.gameRulesButton.y += 20;
			
			this.moreOptionsButton.y += 50;

		}
		
		this.createButton.y += 3;
		if (this.cancelButton != null) {
			this.cancelButton.y += 3;
		}

		this.refreshWorldGenSettingsVisibility();
		
	}
	
	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void onRender(PoseStack matrix, int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
		info.cancel();
		
		this.renderBackground(matrix);
		
		if (this.worldGenSettingsVisible) {
			this.gamemodeSurvivalBtn.visible = false;
			this.gamemodeCreativeBtn.visible = false;
			this.gamemodeHardcoreBtn.visible = false;
			this.customCommandsButton.visible = false;
			this.cancelButton.visible = false;
			this.moreOptionsButton.visible = false;
			this.backButton.visible = true;
			this.gamemodeLeftBtn.visible = false;
			this.gamemodeRightBtn.visible = false;
		} else {
			this.gamemodeSurvivalBtn.visible = true;
			this.gamemodeCreativeBtn.visible = true;
			this.gamemodeHardcoreBtn.visible = true;
			this.customCommandsButton.visible = true;
			this.cancelButton.visible = true;
			this.moreOptionsButton.visible = true;
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
			int titleWidth = font.width(titleString);
			font.draw(matrix, titleString, (this.width / 2) - (titleWidth / 2), 7, -1);
		} else {
			drawCenteredString(matrix, this.font, this.title, this.width / 2, 7, -1);
		}
		
		if (this.worldGenSettingsVisible) {
			
			drawString(matrix, this.font, ENTER_SEED_TEXT, this.width / 2 - 100, 47, -6250336);
			drawString(matrix, this.font, DESC_ENTER_SEED, this.width / 2 - 100, 85, -6250336);
			this.worldGenSettingsComponent.render(matrix, mouseX, mouseY, partialTicks);
			
		} else {
			
			drawString(matrix, this.font, ENTER_WORLD_TEXT, this.width / 2 - 100, this.nameEdit.y - 12, -6250336);
			this.nameEdit.render(matrix, mouseX, mouseY, partialTicks);
			
		}
		
		//Render footer
		if (ModernWorldCreation.config.getOrDefault("show_footer", true)) {
			fill(matrix, 0, this.createButton.y - 5, this.width, this.height, this.headerFooterColor.getRGB());
		}
		
		for(int i = 0; i < this.renderables.size(); ++i) {
			this.renderables.get(i).render(matrix, mouseX, mouseY, partialTicks);
		}
		
	}

	@Inject(at = @At("TAIL"), method = "setWorldGenSettingsVisible")
	private void onSetWorldGenSettingsVisible(boolean b, CallbackInfo info) {

		if (this.worldGenSettingsComponent.isDebug()) {
			if (this.customCommandsButton != null) {
				this.customCommandsButton.visible = false;
			}
		} else {
			if (this.customCommandsButton != null) {
				this.customCommandsButton.visible = !b;
			}
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
			Method m = ObfuscationReflectionHelper.findMethod(CreateWorldScreen.class, "m_100900_", CreateWorldScreenUtils.getVanillaGameModeEnumClass());
			m.invoke(this, CreateWorldScreenUtils.getVanillaGameModeEnumElement(gm));
		} catch (Exception e) {
			ModernWorldCreation.LOGGER.error("ERROR IN: MixinCreateWorldScreen#setVanillaGameMode");
			ModernWorldCreation.printStackTrace(e);
		}
	}
	
	private Gamemode getVanillaGameMode() {
		try {
			Field f = ObfuscationReflectionHelper.findField(CreateWorldScreen.class, "f_100858_");
			Object mode = f.get(this);
			
			if (mode != null) {
				Field f2 = CreateWorldScreenUtils.getVanillaGameModeEnumClass().getDeclaredField("f_101028_");
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
			ModernWorldCreation.LOGGER.error("ERROR IN: MixinCreateWorldScreen#getVanillaGameMode");
			ModernWorldCreation.printStackTrace(e);
		}
		return null;
	}
	
	@Shadow protected abstract void toggleWorldGenSettingsVisibility();

	@Shadow public abstract void refreshWorldGenSettingsVisibility();

	private static MutableComponent optionStatus(Component c, boolean b) {
		return new TranslatableComponent(b ? "options.on.composed" : "options.off.composed", c);
	}

}
