/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.i18n.commands;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.modules.i18n.event.GenerateDefaultLanguageEvent;
import com.hypixel.hytale.server.core.modules.i18n.generator.TranslationMap;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class GenerateI18nCommand
extends AbstractAsyncCommand {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    protected final FlagArg cleanArg = this.withFlagArg("clean", "server.commands.i18n.gen.clean.desc");

    public GenerateI18nCommand() {
        super("gen", "server.commands.i18n.gen.desc");
    }

    @Override
    @Nonnull
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
        CommandSender commandSender = context.sender();
        AssetPack baseAssetPack = AssetModule.get().getBaseAssetPack();
        if (baseAssetPack.isImmutable()) {
            commandSender.sendMessage(Message.translation("server.commands.i18n.gen.immutable"));
            return CompletableFuture.completedFuture(null);
        }
        Path baseAssetPackRoot = baseAssetPack.getRoot();
        boolean cleanOldKeys = (Boolean)this.cleanArg.get(context);
        ConcurrentHashMap<String, TranslationMap> translationFiles = new ConcurrentHashMap<String, TranslationMap>();
        HytaleServer.get().getEventBus().dispatchFor(GenerateDefaultLanguageEvent.class).dispatch(new GenerateDefaultLanguageEvent(translationFiles));
        return CompletableFuture.runAsync(() -> {
            try {
                for (Map.Entry entry : translationFiles.entrySet()) {
                    String filename = (String)entry.getKey();
                    TranslationMap generatedMap = (TranslationMap)entry.getValue();
                    Path path = baseAssetPackRoot.resolve(I18nModule.DEFAULT_GENERATED_PATH).resolve(filename + ".lang");
                    TranslationMap mergedMap = this.mergei18nWithOnDisk(path, generatedMap, cleanOldKeys);
                    mergedMap.sortByKeyBeforeFirstDot();
                    this.writeTranslationMap(path, mergedMap);
                    LOGGER.at(Level.INFO).log("Wrote %s translation(s) to %s", mergedMap.size(), (Object)path.toAbsolutePath());
                }
                LOGGER.at(Level.INFO).log("Wrote %s generated translation file(s)", translationFiles.size());
                commandSender.sendMessage(Message.translation(cleanOldKeys ? "server.commands.i18n.gen.cleaned" : "server.commands.i18n.gen.done"));
            }
            catch (Throwable t) {
                throw new RuntimeException("Error writing generated translation file(s)", t);
            }
        });
    }

    @Nonnull
    private TranslationMap mergei18nWithOnDisk(@Nonnull Path path, @Nonnull TranslationMap generated, boolean cleanOldKeys) throws Exception {
        TranslationMap mergedMap = new TranslationMap();
        if (Files.exists(path, new LinkOption[0])) {
            Properties diskAsProperties = new Properties();
            diskAsProperties.load(new FileInputStream(path.toFile()));
            TranslationMap diskTranslationMap = new TranslationMap(diskAsProperties);
            if (cleanOldKeys) {
                Set<String> extraneousDiskKeys = GenerateI18nCommand.difference(diskTranslationMap.asMap().keySet(), generated.asMap().keySet());
                diskTranslationMap.removeKeys(extraneousDiskKeys);
            }
            mergedMap.putAbsentKeys(diskTranslationMap);
        }
        mergedMap.putAbsentKeys(generated);
        return mergedMap;
    }

    private void writeTranslationMap(@Nonnull Path path, @Nonnull TranslationMap translationMap) throws Exception {
        Files.createDirectories(path.getParent(), new FileAttribute[0]);
        Map<String, String> map = translationMap.asMap();
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            for (Map.Entry<String, String> e : map.entrySet()) {
                String k = e.getKey();
                String v = e.getValue();
                writer.write(k);
                writer.write(" = ");
                writer.write(v);
                writer.write(System.lineSeparator());
            }
        }
    }

    @Nonnull
    private static <T> Set<T> difference(@Nonnull Set<T> a, @Nonnull Set<T> b) {
        HashSet<T> difference = new HashSet<T>(a);
        difference.removeAll(b);
        return difference;
    }
}

