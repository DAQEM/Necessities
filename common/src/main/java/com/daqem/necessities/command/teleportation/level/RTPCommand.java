package com.daqem.necessities.command.teleportation.level;

import java.util.concurrent.ThreadLocalRandom;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.command.Command;
import com.daqem.necessities.config.NecessitiesConfig;
import com.daqem.necessities.level.NecessitiesServerPlayer;
import com.daqem.necessities.model.Position;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class RTPCommand implements Command {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .executes(context -> rtp(context.getSource())));
    }

    private int rtp(CommandSourceStack source) {
        if (!(source.getPlayer() instanceof NecessitiesServerPlayer serverPlayer)) {
            source.sendFailure(NEEDS_PLAYER_ERROR);
            return 0;
        }

        if (!source.hasPermission(2)) {
            long lastRTP = serverPlayer.necessities$getLastRTPTime();
            long currentTime = System.currentTimeMillis();
            long cooldown = NecessitiesConfig.rtpCooldown.get() * 1000L;

            if (currentTime - lastRTP < cooldown) {
                long remaining = (cooldown - (currentTime - lastRTP)) / 1000;
                serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.rtp.cooldown", Necessities.coloredFailure(String.valueOf(remaining))));
                return 0;
            }
        }

        ServerLevel level = (ServerLevel) serverPlayer.necessities$getLevel();
        int minRadius = NecessitiesConfig.rtpMinRadius.get();
        int maxRadius = NecessitiesConfig.rtpMaxRadius.get();

        serverPlayer.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.rtp.searching"), false);

        attemptRtp(serverPlayer, level, minRadius, maxRadius, 15);

        return 1;
    }

    private void attemptRtp(NecessitiesServerPlayer serverPlayer, ServerLevel level, int minRadius, int maxRadius, int attemptsLeft) {
        if (attemptsLeft <= 0) {
            serverPlayer.necessities$sendFailedSystemMessage(Necessities.prefixedFailureTranslatable("commands.rtp.failed"));
            return;
        }

        double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;
        double distance = ThreadLocalRandom.current().nextDouble(minRadius, maxRadius);
        int x = (int) (Math.cos(angle) * distance);
        int z = (int) (Math.sin(angle) * distance);

        ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
        ServerChunkCache chunkSource = level.getChunkSource();

        chunkSource.addTicketAndLoadWithRadius(TicketType.PORTAL, chunkPos, 2).thenAccept(result -> {
            level.getServer().execute(() -> {
                // If player disconnected while waiting, stop.
                if (((ServerPlayer) serverPlayer).isRemoved()) return;

                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                BlockPos targetPos = new BlockPos(x, y, z);
                BlockState blockState = level.getBlockState(targetPos.below());

                if (isSafe(blockState, level, targetPos)) {
                    int delay = NecessitiesConfig.rtpDelay.get();
                    serverPlayer.necessities$scheduleTeleport(new Position(
                            x + 0.5,
                            y + 0.5,
                            z + 0.5,
                            serverPlayer.necessities$getPosition().yaw,
                            serverPlayer.necessities$getPosition().pitch,
                            level.dimension().location()
                    ), delay, null, 0, (player) -> {
                        player.necessities$sendSystemMessage(Necessities.prefixedTranslatable("commands.rtp.success", x, y, z), false);
                        player.necessities$setLastRTPTime(System.currentTimeMillis());
                    });
                } else {
                    attemptRtp(serverPlayer, level, minRadius, maxRadius, attemptsLeft - 1);
                }
            });
        }).exceptionally(e -> {
            Necessities.LOGGER.error("Error loading chunk for RTP command", e);
            level.getServer().execute(() ->
                    attemptRtp(serverPlayer, level, minRadius, maxRadius, attemptsLeft - 1)
            );
            return null;
        });
    }

    private boolean isSafe(BlockState ground, ServerLevel level, BlockPos pos) {
        if (ground.isAir() || ground.is(BlockTags.FIRE) || ground.is(Blocks.MAGMA_BLOCK) || ground.is(Blocks.CACTUS)) {
            return false;
        }
        if (!ground.getFluidState().isEmpty()) {
            return false;
        }
        return level.getBlockState(pos).isAir() && level.getBlockState(pos.above()).isAir();
    }
}