package com.daqem.necessities.mixin;

import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.Position;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements NecessitiesServerPlayer {

    @Shadow public abstract void teleportTo(ServerLevel serverLevel, double d, double e, double f, float g, float h);

    @Shadow public abstract ServerLevel serverLevel();

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Override
    public NecessitiesServerLevel necessities$getLevel() {
        return (NecessitiesServerLevel) this.level();
    }

    @Override
    public Position necessities$getPosition() {
        Vec3 vec3 = this.position();
        return new Position(vec3.x, vec3.y, vec3.z, this.getYRot(), this.getXRot());
    }

    @Override
    public void necessities$teleport(Position position) {
        this.teleportTo(this.serverLevel(), position.x, position.y, position.z, position.yaw, position.pitch);
    }
}
