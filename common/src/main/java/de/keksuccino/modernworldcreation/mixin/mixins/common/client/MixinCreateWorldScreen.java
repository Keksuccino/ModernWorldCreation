package de.keksuccino.modernworldcreation.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.keksuccino.modernworldcreation.ModernWorldCreationGameTab;
import de.keksuccino.modernworldcreation.util.rendering.screens.InitializableCreateWorldScreen;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen extends Screen implements InitializableCreateWorldScreen {

    @Unique private Consumer<CreateWorldScreen> onInitBody_ModernWorldCreation = null;

    private MixinCreateWorldScreen(Component $$0) {
        super($$0);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;addTabs([Lnet/minecraft/client/gui/components/tabs/Tab;)Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;"))
    private TabNavigationBar.Builder wrap_addTabs_ModernWorldCreation(TabNavigationBar.Builder instance, Tab[] tabs, Operation<TabNavigationBar.Builder> original) {
        List<Tab> newTabs = new ArrayList<>();
        //Remove original Game tab from tabs
        for (Tab t : tabs) {
            if (t.getTabTitle().getContents() instanceof TranslatableContents c) {
                if (!c.getKey().equals("createWorld.tab.game.title")) {
                    newTabs.add(t);
                }
            } else {
                newTabs.add(t);
            }
        }
        //Add Modern World Creation Game tab to tabs
        newTabs.addFirst(new ModernWorldCreationGameTab((CreateWorldScreen)((Object)this)));
        return original.call(instance, newTabs.toArray(new Tab[0]));
    }

    @Inject(method = "repositionElements", at = @At("RETURN"))
    private void after_repositionElements_ModernWorldCreation(CallbackInfo info) {
        if (this.onInitBody_ModernWorldCreation != null) {
            this.onInitBody_ModernWorldCreation.accept((CreateWorldScreen)((Object)this));
        }
    }

    @Unique
    @Override
    public void setOnInitBody_ModernWorldCreation(@Nullable Consumer<CreateWorldScreen> body) {
        this.onInitBody_ModernWorldCreation = body;
    }


}
