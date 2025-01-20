package de.keksuccino.modernworldcreation.mixin.mixins.common.client;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;

@Mixin(Screen.class)
public interface IMixinScreen {

    @Accessor("renderables") List<Renderable> get_renderables_ModernWorldCreation();

    @Accessor("children") List<GuiEventListener> get_children_ModernWorldCreation();

    @Accessor("narratables") List<NarratableEntry> get_narratables_ModernWorldCreation();

    @Invoker("setInitialFocus") void invoke_setInitialFocus_ModernWorldCreation(GuiEventListener widget);

}
