package com.daqem.necessities.data;

import com.daqem.necessities.Necessities;
import com.daqem.necessities.model.Kit;
import com.daqem.yamlconfig.YamlConfigExpectPlatform;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KitManager extends SimplePreparableReloadListener<List<Kit>> {

    private ImmutableMap<ResourceLocation, Kit> kits = ImmutableMap.of();

    private static KitManager instance;

    public KitManager() {
        instance = this;
    }

    @Override
    protected @NotNull List<Kit> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        // 1. Load from Datapacks
        Map<ResourceLocation, Resource> resourceMap = resourceManager.listResources("necessities/kits", (resourceLocation) ->
                        resourceLocation.getPath().endsWith(".json")).entrySet().stream()
                .collect(Collectors.toMap(entry ->
                                ResourceLocation.fromNamespaceAndPath(
                                        entry.getKey().getNamespace(),
                                        entry.getKey().getPath()
                                                .substring(0, entry.getKey().getPath().length() - ".json".length())
                                                .substring("necessities/kits/".length())),
                        Map.Entry::getValue));

        Map<ResourceLocation, JsonObject> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : resourceMap.entrySet()) {
            ResourceLocation location = entry.getKey();
            try {
                JsonObject jsonElement = GsonHelper.parse(entry.getValue().openAsReader());
                map.put(location, jsonElement);
            } catch (Exception runtimeException) {
                Necessities.LOGGER.error("Parsing error loading kit {}", location, runtimeException);
            }
        }

        try {
            Path configDir = YamlConfigExpectPlatform.getConfigDirectory().resolve(Necessities.MOD_ID).resolve("kits");
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            try (Stream<Path> paths = Files.walk(configDir)) {
                paths.filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            try (BufferedReader reader = Files.newBufferedReader(path)) {
                                JsonObject jsonElement = GsonHelper.parse(reader);
                                String relativePath = configDir.relativize(path).toString();
                                relativePath = relativePath.replace("\\", "/");
                                relativePath = relativePath.substring(0, relativePath.length() - ".json".length());
                                String namespace;
                                String resourcePath;
                                int firstSlashIndex = relativePath.indexOf('/');
                                if (firstSlashIndex > 0) {
                                    namespace = relativePath.substring(0, firstSlashIndex);
                                    resourcePath = relativePath.substring(firstSlashIndex + 1);
                                } else {
                                    namespace = Necessities.MOD_ID;
                                    resourcePath = relativePath;
                                }
                                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, resourcePath);
                                // Config files override datapack files with the same ID
                                map.put(location, jsonElement);
                            } catch (Exception e) {
                                Necessities.LOGGER.error("Parsing error loading kit from config {}", path, e);
                            }
                        });
            }
        } catch (Exception e) {
            Necessities.LOGGER.error("Error loading kits from config", e);
        }

        // 3. Parse gathered JSON objects
        List<Kit> kits = new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonObject> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            JsonObject jsonObject = entry.getValue();

            try {
                // Parse using Codec
                Kit kitData = Kit.CODEC.parse(JsonOps.INSTANCE, jsonObject)
                        .getOrThrow(JsonParseException::new);

                // Assign the ID from the file location
                kits.add(kitData.withId(location));
            } catch (Exception e) {
                Necessities.LOGGER.error("Parsing error loading kit {}", location, e);
            }
        }

        return kits;
    }

    @Override
    protected void apply(List<Kit> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Necessities.LOGGER.info("Loaded {} kits", object.size());
        this.kits = object.stream()
                .collect(ImmutableMap.toImmutableMap(
                        Kit::getId,
                        kit -> kit
                ));
    }

    public static KitManager getInstance() {
        return instance != null ? instance : new KitManager();
    }

    public List<Kit> getKits() {
        return kits.values().asList();
    }

    public Optional<Kit> getKit(ResourceLocation location) {
        return Optional.ofNullable(kits.get(location));
    }
}