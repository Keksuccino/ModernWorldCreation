package de.keksuccino.modernworldcreation.mixin.mixins.common.client;

import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface IMixinTabNavigationBar {

    @Accessor("tabManager") TabManager get_tabManager_ModernWorldCreation();

}
