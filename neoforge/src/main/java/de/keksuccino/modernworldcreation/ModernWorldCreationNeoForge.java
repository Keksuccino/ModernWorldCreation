package de.keksuccino.modernworldcreation;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(ModernWorldCreation.MOD_ID)
public class ModernWorldCreationNeoForge {
    
    public ModernWorldCreationNeoForge(@NotNull IEventBus eventBus) {

        ModernWorldCreation.init();

    }

}