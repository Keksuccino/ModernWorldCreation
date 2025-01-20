package de.keksuccino.modernworldcreation.mixin.mixins.common.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateWorldScreen.class)
public interface IMixinCreateWorldScreen {

    @Accessor("uiState") WorldCreationUiState get_uiState_ModernWorldCreation();

    @Invoker("openExperimentsScreen") void invoke_openExperimentsScreen_ModernWorldCreation(WorldDataConfiguration worldDataConfiguration);

}
