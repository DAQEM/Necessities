package com.daqem.necessities.mixin;

import com.daqem.necessities.command.NecessitiesCommandSourceStack;
import com.daqem.necessities.level.NecessitiesServerLevel;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.level.storage.NecessitiesLevelData;
import com.daqem.necessities.model.Position;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements SharedSuggestionProvider, NecessitiesCommandSourceStack {

    @Shadow @Nullable
    public abstract ServerPlayer getPlayer();

    @Shadow
    public abstract Vec3 getPosition();

    @Shadow
    public abstract ServerLevel getLevel();

    @Override
    public NecessitiesServerLevel necessities$getLevel() {
        return (NecessitiesServerLevel) getLevel();
    }

    @Override
    public Position necessities$getPosition() {
        if (getPlayer() instanceof NecessitiesServerPlayer serverPlayer) {
            return serverPlayer.necessities$getPosition();
        }
        Vec3 vec3 = getPosition();
        return new Position(vec3.x, vec3.y, vec3.z, 0, 0, necessities$getLevel().necessities$getDimension());
    }

    @Override
    public NecessitiesLevelData necessities$getLevelData() {
        return necessities$getOverworld().necessities$getLevelData();
    }

    @Override
    public NecessitiesServerLevel necessities$getOverworld() {
        return (NecessitiesServerLevel) getLevel().getServer().getLevel(ServerLevel.OVERWORLD);
    }
}
