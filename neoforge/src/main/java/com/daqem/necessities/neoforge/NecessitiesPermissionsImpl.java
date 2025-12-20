package com.daqem.necessities.neoforge;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NecessitiesPermissionsImpl {

    private static final Map<String, PermissionNode<Boolean>> NODES = new ConcurrentHashMap<>();

    public static boolean check(CommandSourceStack source, String permissionNode, int fallbackLevel) {
        if (source.getEntity() instanceof ServerPlayer player) {
            PermissionNode<Boolean> node = NODES.computeIfAbsent(permissionNode, id ->
                    new PermissionNode<>(
                            "necessities",
                            id.replace("necessities.", ""),
                            PermissionTypes.BOOLEAN,
                            (p, uuid, context) -> false
                    )
            );

            try {
                return PermissionAPI.getPermission(player, node);
            } catch (Exception e) {
                return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(fallbackLevel)));
            }
        }

        return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(fallbackLevel)));
    }
}