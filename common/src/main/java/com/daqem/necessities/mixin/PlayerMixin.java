package com.daqem.necessities.mixin;

import com.daqem.necessities.level.NecessitiesServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Component> cir) {
        if (this instanceof NecessitiesServerPlayer serverPlayer && serverPlayer.necessities$hasNick()) {
            cir.setReturnValue(serverPlayer.necessities$getName());
        }
    }
}
