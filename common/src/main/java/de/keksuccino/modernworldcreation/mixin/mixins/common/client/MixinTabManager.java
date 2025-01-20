package de.keksuccino.modernworldcreation.mixin.mixins.common.client;

import de.keksuccino.modernworldcreation.util.rendering.gui.ExtendedTabManager;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Consumer;

@Mixin(TabManager.class)
public class MixinTabManager implements ExtendedTabManager {

    @Unique private Consumer<Tab> tabChangeListener_ModernWorldCreation = null;

    @Inject(method = "setCurrentTab", at = @At("RETURN"))
    private void after_setCurrentTab_ModernWorldCreation(Tab tab, boolean playClickSound, CallbackInfo info) {
        if (this.tabChangeListener_ModernWorldCreation != null) {
            this.tabChangeListener_ModernWorldCreation.accept(tab);
        }
    }

    @Unique
    @Override
    public void setTabChangeListener_ModernWorldCreation(@Nullable Consumer<Tab> listener) {
        this.tabChangeListener_ModernWorldCreation = listener;
    }

    @Unique
    @Override
    public boolean hasTabChangeListener_ModernWorldCreation() {
        return (this.tabChangeListener_ModernWorldCreation != null);
    }

}
