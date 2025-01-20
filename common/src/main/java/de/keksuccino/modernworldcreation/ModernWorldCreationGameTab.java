package de.keksuccino.modernworldcreation;

import de.keksuccino.modernworldcreation.mixin.mixins.common.client.IMixinCreateWorldScreen;
import de.keksuccino.modernworldcreation.mixin.mixins.common.client.IMixinScreen;
import de.keksuccino.modernworldcreation.util.fancymenu.FMAccess;
import de.keksuccino.modernworldcreation.util.fancymenu.FMUtils;
import de.keksuccino.modernworldcreation.util.rendering.gui.widgets.ArrowButton;
import de.keksuccino.modernworldcreation.util.rendering.gui.widgets.ToggleModeButton;
import de.keksuccino.modernworldcreation.util.rendering.screens.InitializableCreateWorldScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

public class ModernWorldCreationGameTab extends GridLayoutTab {

    private static final ResourceLocation BUTTON_TEXTURE_SURVIVAL = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/gamemodes/background_survival.png");
    private static final ResourceLocation BUTTON_TEXTURE_CREATIVE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/gamemodes/background_creative.png");
    private static final ResourceLocation BUTTON_TEXTURE_HARDCORE = ResourceLocation.fromNamespaceAndPath("modernworldcreation", "textures/gamemodes/background_hardcore.png");

    private static final Component TOOLTIP_GAMEMODE_SURVIVAL_LINE1 = Component.translatable("selectWorld.gameMode.survival.line1");
    private static final Component TOOLTIP_GAMEMODE_SURVIVAL_LINE2 = Component.translatable("selectWorld.gameMode.survival.line2");
    private static final Component TOOLTIP_GAMEMODE_CREATIVE_LINE1 = Component.translatable("selectWorld.gameMode.creative.line1");
    private static final Component TOOLTIP_GAMEMODE_CREATIVE_LINE2 = Component.translatable("selectWorld.gameMode.creative.line2");
    private static final Component TOOLTIP_GAMEMODE_HARDCORE_LINE1 = Component.translatable("selectWorld.gameMode.hardcore.line1");
    private static final Component TOOLTIP_GAMEMODE_HARDCORE_LINE2 = Component.translatable("selectWorld.gameMode.hardcore.line2");

    private static final Component SURVIVAL_LABEL = Component.translatable("modernworldcreation.gamemodes.survival").setStyle(Style.EMPTY.withBold(true));
    private static final Component CREATIVE_LABEL = Component.translatable("modernworldcreation.gamemodes.creative").setStyle(Style.EMPTY.withBold(true));
    private static final Component HARDCORE_LABEL = Component.translatable("modernworldcreation.gamemodes.hardcore").setStyle(Style.EMPTY.withBold(true));

    protected static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
    protected static final Component EXPERIMENTS_LABEL = Component.translatable("selectWorld.experiments");
    protected static final Component ALLOW_COMMANDS_INFO = Component.translatable("selectWorld.allowCommands.info");
    protected static final Component TITLE = Component.translatable("createWorld.tab.game.title");
    protected static final Component ALLOW_COMMANDS = Component.translatable("selectWorld.allowCommands");

    protected final Font font = Minecraft.getInstance().font;

    protected EditBox nameEdit;
    protected ArrowButton arrowLeftButton;
    protected ArrowButton arrowRightButton;
    protected ToggleModeButton gamemodeSurvivalButton;
    protected ToggleModeButton gamemodeCreativeButton;
    protected ToggleModeButton gamemodeHardcoreButton;
    protected CycleButton<Boolean> allowCheatsButton;
    protected CycleButton<Difficulty> difficultyButton;

    //TODO Fixen: Wenn tab gewechselt wird, bleiben GameMode buttons sichtbar
    //TODO Info tooltips noch richtig rendern, wenn hover

    public ModernWorldCreationGameTab(@NotNull CreateWorldScreen parent) {

        super(TITLE);

        final WorldCreationUiState uiState = Objects.requireNonNull(((IMixinCreateWorldScreen)parent).get_uiState_ModernWorldCreation());

        GridLayout.RowHelper rowHelper = this.layout.rowSpacing(8).createRowHelper(1);
        LayoutSettings layoutSettings = rowHelper.newCellSettings();

        this.nameEdit = new EditBox(this.font, 208, 20, Component.translatable("selectWorld.enterName"));
        this.nameEdit.setValue(uiState.getName());
        this.nameEdit.setResponder(uiState::setName);
        uiState.addListener((worldCreationUiState) -> this.nameEdit.setTooltip(Tooltip.create(Component.translatable("selectWorld.targetFolder", new Object[]{Component.literal(worldCreationUiState.getTargetFolder()).withStyle(ChatFormatting.ITALIC)}))));
        ((IMixinScreen)parent).invoke_setInitialFocus_ModernWorldCreation(this.nameEdit);
        rowHelper.addChild(CommonLayouts.labeledElement(this.font, this.nameEdit, NAME_LABEL), rowHelper.newCellSettings().alignHorizontallyCenter());

        this.difficultyButton = rowHelper.addChild(CycleButton.builder(Difficulty::getDisplayName).withValues(Difficulty.values()).create(0, 0, 170, 20, Component.translatable("options.difficulty"), (cycleButtonx, difficulty) -> uiState.setDifficulty(difficulty)), layoutSettings);
        uiState.addListener((worldCreationUiState) -> {
            this.difficultyButton.setValue(uiState.getDifficulty());
            this.difficultyButton.active = !uiState.isHardcore();
            this.difficultyButton.setTooltip(Tooltip.create(uiState.getDifficulty().getInfo()));
        });

        this.allowCheatsButton = rowHelper.addChild(CycleButton.onOffBuilder().withTooltip((boolean_) -> Tooltip.create(ALLOW_COMMANDS_INFO)).create(0, 0, 170, 20, ALLOW_COMMANDS, (cycleButtonx, boolean_) -> uiState.setAllowCommands(boolean_)));
        uiState.addListener((worldCreationUiState) -> {
            this.allowCheatsButton.setValue(uiState.isAllowCommands());
            this.allowCheatsButton.active = !uiState.isDebug() && !uiState.isHardcore();
        });

        //TODO handle this button
        if (!SharedConstants.getCurrentVersion().isStable()) {
            rowHelper.addChild(Button.builder(EXPERIMENTS_LABEL, (button) -> ((IMixinCreateWorldScreen)parent).invoke_openExperimentsScreen_ModernWorldCreation(uiState.getSettings().dataConfiguration())).width(210).build());
        }

        ((InitializableCreateWorldScreen)parent).setOnInitBody_ModernWorldCreation(screen -> {

            int midX = screen.width / 2;
            int topY = this.nameEdit.getY();

            removeScreenWidget(screen, this.arrowLeftButton);
            this.arrowLeftButton = addRenderableWidgetToScreen(screen, new ArrowButton(midX - 145 - 40 - 10, topY + 38 + (45 / 2) - 20, ArrowButton.ArrowDirection.LEFT, button -> {
                this.switchToNextGameMode(uiState, ArrowButton.ArrowDirection.LEFT);
            }));
            if (FMUtils.isFancyMenuLoaded()) FMAccess.setUniqueIdentifierToWidget(this.arrowLeftButton, "mwc_arrow_left_button");

            removeScreenWidget(screen, this.arrowRightButton);
            this.arrowRightButton = addRenderableWidgetToScreen(screen, new ArrowButton(midX + 55 + 90 + 10, topY + 38 + (45 / 2) - 20, ArrowButton.ArrowDirection.RIGHT, button -> {
                this.switchToNextGameMode(uiState, ArrowButton.ArrowDirection.RIGHT);
            }));
            if (FMUtils.isFancyMenuLoaded()) FMAccess.setUniqueIdentifierToWidget(this.arrowRightButton, "mwc_arrow_right_button");

            WorldCreationUiState.SelectedGameMode selectedGamemode = uiState.getGameMode();

            removeScreenWidget(screen, this.gamemodeSurvivalButton);
            this.gamemodeSurvivalButton = addRenderableWidgetToScreen(screen, new ToggleModeButton(midX - 145, topY + 38, 90, 45, BUTTON_TEXTURE_SURVIVAL, SURVIVAL_LABEL, button -> {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
            }));
            this.gamemodeSurvivalButton.setInfoTooltip(List.of(TOOLTIP_GAMEMODE_SURVIVAL_LINE1, TOOLTIP_GAMEMODE_SURVIVAL_LINE2));
            if (selectedGamemode == WorldCreationUiState.SelectedGameMode.SURVIVAL) {
                this.gamemodeSurvivalButton.setSelected(true);
            }
            if (FMUtils.isFancyMenuLoaded()) FMAccess.setUniqueIdentifierToWidget(this.gamemodeSurvivalButton, "mwc_gamemode_survival_button");

            removeScreenWidget(screen, this.gamemodeCreativeButton);
            this.gamemodeCreativeButton = addRenderableWidgetToScreen(screen, new ToggleModeButton(midX - 45, topY + 38, 90, 45, BUTTON_TEXTURE_CREATIVE, CREATIVE_LABEL, button -> {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
            }));
            this.gamemodeCreativeButton.setInfoTooltip(List.of(TOOLTIP_GAMEMODE_CREATIVE_LINE1, TOOLTIP_GAMEMODE_CREATIVE_LINE2));
            if (selectedGamemode == WorldCreationUiState.SelectedGameMode.CREATIVE) {
                this.gamemodeCreativeButton.setSelected(true);
            }
            if (FMUtils.isFancyMenuLoaded()) FMAccess.setUniqueIdentifierToWidget(this.gamemodeCreativeButton, "mwc_gamemode_creative_button");

            removeScreenWidget(screen, this.gamemodeHardcoreButton);
            this.gamemodeHardcoreButton = addRenderableWidgetToScreen(screen, new ToggleModeButton(midX + 55, topY + 38, 90, 45, BUTTON_TEXTURE_HARDCORE, HARDCORE_LABEL, button -> {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
            }));
            this.gamemodeHardcoreButton.setInfoTooltip(List.of(TOOLTIP_GAMEMODE_HARDCORE_LINE1, TOOLTIP_GAMEMODE_HARDCORE_LINE2));
            if (selectedGamemode == WorldCreationUiState.SelectedGameMode.HARDCORE) {
                this.gamemodeHardcoreButton.setSelected(true);
            }
            if (FMUtils.isFancyMenuLoaded()) FMAccess.setUniqueIdentifierToWidget(this.gamemodeHardcoreButton, "mwc_gamemode_hardcore_button");

            this.gamemodeSurvivalButton.setShowInfo(ModernWorldCreation.getOptions().showGameModeInfo.getValue());
            this.gamemodeCreativeButton.setShowInfo(ModernWorldCreation.getOptions().showGameModeInfo.getValue());
            this.gamemodeHardcoreButton.setShowInfo(ModernWorldCreation.getOptions().showGameModeInfo.getValue());

            //Difficulty Button
            this.difficultyButton.setX(midX - this.difficultyButton.getWidth() - 3);
            this.difficultyButton.setY(topY + 38 + 50 + 10);

            //Allow Cheats Button
            this.allowCheatsButton.setX(midX + 3);
            this.allowCheatsButton.setY(topY + 38 + 50 + 10);

            //Handle big screen layout
            if ((screen.height - this.allowCheatsButton.getY()) >= 100) {

                this.gamemodeSurvivalButton.setY(this.gamemodeSurvivalButton.getY() + 10);
                this.gamemodeCreativeButton.setY(this.gamemodeCreativeButton.getY() + 10);
                this.gamemodeHardcoreButton.setY(this.gamemodeHardcoreButton.getY() + 10);

                this.arrowLeftButton.setY(this.arrowLeftButton.getY() + 10);
                this.arrowRightButton.setY(this.arrowRightButton.getY() + 10);

                this.difficultyButton.setY(this.difficultyButton.getY() + 20);
                this.allowCheatsButton.setY(this.allowCheatsButton.getY() + 20);

            }

        });

    }

    protected void switchToNextGameMode(@NotNull WorldCreationUiState uiState, @NotNull ArrowButton.ArrowDirection direction) {
        if (direction == ArrowButton.ArrowDirection.LEFT) {
            if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
                this.gamemodeSurvivalButton.setSelected(false);
                this.gamemodeCreativeButton.setSelected(false);
                this.gamemodeHardcoreButton.setSelected(true);
            } else if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
                this.gamemodeSurvivalButton.setSelected(true);
                this.gamemodeCreativeButton.setSelected(false);
                this.gamemodeHardcoreButton.setSelected(false);
            } else if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
                this.gamemodeSurvivalButton.setSelected(false);
                this.gamemodeCreativeButton.setSelected(true);
                this.gamemodeHardcoreButton.setSelected(false);
            }
        }
        if (direction == ArrowButton.ArrowDirection.RIGHT) {
            if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.SURVIVAL) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.CREATIVE);
                this.gamemodeSurvivalButton.setSelected(false);
                this.gamemodeCreativeButton.setSelected(true);
                this.gamemodeHardcoreButton.setSelected(false);
            } else if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.CREATIVE) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.HARDCORE);
                this.gamemodeSurvivalButton.setSelected(false);
                this.gamemodeCreativeButton.setSelected(false);
                this.gamemodeHardcoreButton.setSelected(true);
            } else if (uiState.getGameMode() == WorldCreationUiState.SelectedGameMode.HARDCORE) {
                uiState.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL);
                this.gamemodeSurvivalButton.setSelected(true);
                this.gamemodeCreativeButton.setSelected(false);
                this.gamemodeHardcoreButton.setSelected(false);
            }
        }
    }

    protected static <T extends AbstractWidget> T addRenderableWidgetToScreen(@NotNull Screen screen, @NotNull T widget) {
        ((IMixinScreen)screen).get_renderables_ModernWorldCreation().add(widget);
        ((IMixinScreen)screen).get_children_ModernWorldCreation().add(widget);
        ((IMixinScreen)screen).get_narratables_ModernWorldCreation().add(widget);
        return widget;
    }

    protected static void removeScreenWidget(@NotNull Screen screen, @Nullable AbstractWidget widget) {
        if (widget == null) return;
        ((IMixinScreen)screen).get_renderables_ModernWorldCreation().remove(widget);
        ((IMixinScreen)screen).get_children_ModernWorldCreation().remove(widget);
        ((IMixinScreen)screen).get_narratables_ModernWorldCreation().remove(widget);
    }

}
