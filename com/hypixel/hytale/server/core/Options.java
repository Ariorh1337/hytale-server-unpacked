/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core;

import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
import com.hypixel.hytale.server.core.io.transport.TransportType;
import com.hypixel.hytale.server.core.universe.world.ValidationOption;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class Options {
    public static final OptionParser PARSER = new OptionParser();
    public static final OptionSpec<Void> HELP = PARSER.accepts("help", "Print's this message.").forHelp();
    public static final OptionSpec<Void> VERSION = PARSER.accepts("version", "Prints version information.");
    public static final OptionSpec<Void> BARE = PARSER.accepts("bare", "Runs the server bare. For example without loading worlds, binding to ports or creating directories. (Note: Plugins will still be loaded which may not respect this flag)");
    public static final OptionSpec<Map.Entry<String, Level>> LOG_LEVELS = PARSER.accepts("log", "Sets the logger level.").withRequiredArg().withValuesSeparatedBy(',').withValuesConvertedBy(new LevelValueConverter());
    public static final OptionSpec<InetSocketAddress> BIND = PARSER.acceptsAll(List.of("b", "bind"), "Port to listen on").withRequiredArg().withValuesSeparatedBy(',').withValuesConvertedBy(new SocketAddressValueConverter()).defaultsTo(new InetSocketAddress(5520), (InetSocketAddress[])new InetSocketAddress[0]);
    public static final OptionSpec<TransportType> TRANSPORT = PARSER.acceptsAll(List.of("t", "transport"), "Transport type").withRequiredArg().ofType(TransportType.class).defaultsTo(TransportType.QUIC, (TransportType[])new TransportType[0]);
    public static final OptionSpec<Void> DISABLE_CPB_BUILD = PARSER.accepts("disable-cpb-build", "Disables building of compact prefab buffers");
    public static final OptionSpec<Path> PREFAB_CACHE_DIRECTORY = PARSER.accepts("prefab-cache", "Prefab cache directory for immutable assets").withRequiredArg().withValuesConvertedBy(new PathConverter(PathConverter.PathType.ANY));
    public static final OptionSpec<Path> ASSET_DIRECTORY = PARSER.acceptsAll(List.of("assets"), "Asset directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR_OR_ZIP)).defaultsTo(Paths.get("../HytaleAssets", new String[0]), (Path[])new Path[0]);
    public static final OptionSpec<Path> MODS_DIRECTORIES = PARSER.acceptsAll(List.of("mods"), "Additional mods directories").withRequiredArg().withValuesSeparatedBy(',').withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR));
    public static final OptionSpec<Void> ACCEPT_EARLY_PLUGINS = PARSER.accepts("accept-early-plugins", "You acknowledge that loading early plugins is unsupported and may cause stability issues.");
    public static final OptionSpec<Path> EARLY_PLUGIN_DIRECTORIES = PARSER.accepts("early-plugins", "Additional early plugin directories to load from").withRequiredArg().withValuesSeparatedBy(',').withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR));
    public static final OptionSpec<Void> VALIDATE_ASSETS = PARSER.accepts("validate-assets", "Causes the server to exit with an error code if any assets are invalid.");
    public static final OptionSpec<ValidationOption> VALIDATE_PREFABS = PARSER.accepts("validate-prefabs", "Causes the server to exit with an error code if any prefabs are invalid.").withOptionalArg().withValuesSeparatedBy(',').ofType(ValidationOption.class);
    public static final OptionSpec<Void> VALIDATE_WORLD_GEN = PARSER.accepts("validate-world-gen", "Causes the server to exit with an error code if default world gen is invalid.");
    public static final OptionSpec<Void> SHUTDOWN_AFTER_VALIDATE = PARSER.accepts("shutdown-after-validate", "Automatically shutdown the server after asset and/or prefab validation.");
    public static final OptionSpec<Void> GENERATE_SCHEMA = PARSER.accepts("generate-schema", "Causes the server generate schema, save it into the assets directory and then exit");
    public static final OptionSpec<Path> WORLD_GEN_DIRECTORY = PARSER.accepts("world-gen", "World gen directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR));
    public static final OptionSpec<Void> DISABLE_FILE_WATCHER = PARSER.accepts("disable-file-watcher");
    public static final OptionSpec<Void> DISABLE_SENTRY = PARSER.accepts("disable-sentry");
    public static final OptionSpec<Void> DISABLE_ASSET_COMPARE = PARSER.accepts("disable-asset-compare");
    public static final OptionSpec<Void> BACKUP = PARSER.accepts("backup");
    public static final OptionSpec<Integer> BACKUP_FREQUENCY_MINUTES = PARSER.accepts("backup-frequency").withRequiredArg().ofType(Integer.class).defaultsTo(30, (Integer[])new Integer[0]);
    public static final OptionSpec<Path> BACKUP_DIRECTORY = PARSER.accepts("backup-dir").requiredIf(BACKUP, new OptionSpec[0]).withRequiredArg().withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR));
    public static final OptionSpec<Integer> BACKUP_MAX_COUNT = PARSER.accepts("backup-max-count").withRequiredArg().ofType(Integer.class).defaultsTo(5, (Integer[])new Integer[0]);
    public static final OptionSpec<Void> SINGLEPLAYER = PARSER.accepts("singleplayer");
    public static final OptionSpec<String> OWNER_NAME = PARSER.accepts("owner-name").withRequiredArg();
    public static final OptionSpec<UUID> OWNER_UUID = PARSER.accepts("owner-uuid").withRequiredArg().withValuesConvertedBy(new UUIDConverter());
    public static final OptionSpec<Integer> CLIENT_PID = PARSER.accepts("client-pid").withRequiredArg().ofType(Integer.class);
    public static final OptionSpec<Path> UNIVERSE = PARSER.accepts("universe").withRequiredArg().withValuesConvertedBy(new PathConverter(PathConverter.PathType.DIR));
    public static final OptionSpec<Void> EVENT_DEBUG = PARSER.accepts("event-debug");
    public static final OptionSpec<Boolean> FORCE_NETWORK_FLUSH = PARSER.accepts("force-network-flush").withRequiredArg().ofType(Boolean.class).defaultsTo(true, (Boolean[])new Boolean[0]);
    public static final OptionSpec<Map<String, Path>> MIGRATIONS = PARSER.accepts("migrations", "The migrations to run").withRequiredArg().withValuesConvertedBy(new StringToPathMapConverter());
    public static final OptionSpec<String> MIGRATE_WORLDS = PARSER.accepts("migrate-worlds", "Worlds to migrate").availableIf(MIGRATIONS, new OptionSpec[0]).withRequiredArg().withValuesSeparatedBy(',');
    public static final OptionSpec<String> BOOT_COMMAND = PARSER.accepts("boot-command", "Runs command on boot. If multiple commands are provided they are executed synchronously in order.").withRequiredArg().withValuesSeparatedBy(',');
    public static final String ALLOW_SELF_OP_COMMAND_STRING = "allow-op";
    public static final OptionSpec<Void> ALLOW_SELF_OP_COMMAND = PARSER.accepts("allow-op");
    public static final OptionSpec<AuthMode> AUTH_MODE = PARSER.accepts("auth-mode", "Authentication mode").withRequiredArg().withValuesConvertedBy(new AuthModeConverter()).defaultsTo(AuthMode.AUTHENTICATED, (AuthMode[])new AuthMode[0]);
    public static final OptionSpec<String> SESSION_TOKEN = PARSER.accepts("session-token", "Session token for Session Service API").withRequiredArg().ofType(String.class);
    public static final OptionSpec<String> IDENTITY_TOKEN = PARSER.accepts("identity-token", "Identity token (JWT)").withRequiredArg().ofType(String.class);
    private static OptionSet optionSet;

    public static OptionSet getOptionSet() {
        return optionSet;
    }

    public static <T> T getOrDefault(OptionSpec<T> optionSpec, @Nonnull OptionSet optionSet, T def) {
        if (!optionSet.has(optionSpec)) {
            return def;
        }
        return optionSet.valueOf(optionSpec);
    }

    public static boolean parse(String[] args) throws IOException {
        optionSet = PARSER.parse(args);
        if (optionSet.has(HELP)) {
            PARSER.printHelpOn(System.out);
            return true;
        }
        if (optionSet.has(VERSION)) {
            String version = ManifestUtil.getImplementationVersion();
            String patchline = ManifestUtil.getPatchline();
            String environment = "release";
            if ("release".equals(patchline)) {
                System.out.println("HytaleServer v" + version + " (" + patchline + ")");
            } else {
                System.out.println("HytaleServer v" + version + " (" + patchline + ", " + environment + ")");
            }
            return true;
        }
        List<?> nonOptionArguments = optionSet.nonOptionArguments();
        if (!nonOptionArguments.isEmpty()) {
            System.err.println("Unknown arguments: " + String.valueOf(nonOptionArguments));
            System.exit(1);
            return true;
        }
        if (optionSet.has(LOG_LEVELS)) {
            HytaleLoggerBackend.loadLevels(optionSet.valuesOf(LOG_LEVELS));
        } else if (optionSet.has(SHUTDOWN_AFTER_VALIDATE)) {
            HytaleLoggerBackend.loadLevels(List.of(Map.entry("", Level.WARNING)));
        }
        return false;
    }

    public static class LevelValueConverter
    implements ValueConverter<Map.Entry<String, Level>> {
        private static final Map.Entry<String, Level> ENTRY = Map.entry("", Level.ALL);

        @Override
        @Nonnull
        public Map.Entry<String, Level> convert(@Nonnull String value) {
            if (!value.contains(":")) {
                return Map.entry("", Level.parse(value.toUpperCase()));
            }
            String[] split = value.split(":");
            return Map.entry(split[0], Level.parse(split[1].toUpperCase()));
        }

        @Override
        @Nonnull
        public Class<Map.Entry<String, Level>> valueType() {
            return ENTRY.getClass();
        }

        @Override
        @Nullable
        public String valuePattern() {
            return null;
        }
    }

    public static class SocketAddressValueConverter
    implements ValueConverter<InetSocketAddress> {
        @Override
        @Nonnull
        public InetSocketAddress convert(@Nonnull String value) {
            if (value.contains(":")) {
                String[] split = value.split(":");
                return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
            }
            try {
                return new InetSocketAddress(Integer.parseInt(value));
            }
            catch (NumberFormatException e) {
                return new InetSocketAddress(value, 5520);
            }
        }

        @Override
        @Nonnull
        public Class<? extends InetSocketAddress> valueType() {
            return InetSocketAddress.class;
        }

        @Override
        @Nullable
        public String valuePattern() {
            return null;
        }
    }

    public static class PathConverter
    implements ValueConverter<Path> {
        private final PathType pathType;

        public PathConverter(PathType pathType) {
            this.pathType = pathType;
        }

        @Override
        @Nonnull
        public Path convert(@Nonnull String s) {
            try {
                Path path = PathUtil.get(s);
                if (Files.exists(path, new LinkOption[0])) {
                    switch (this.pathType.ordinal()) {
                        case 0: {
                            if (Files.isRegularFile(path, new LinkOption[0])) break;
                            throw new ValueConversionException("Path must be a file!");
                        }
                        case 1: {
                            if (Files.isDirectory(path, new LinkOption[0])) break;
                            throw new ValueConversionException("Path must be a directory!");
                        }
                        case 2: {
                            if (Files.isDirectory(path, new LinkOption[0]) || Files.exists(path, new LinkOption[0]) && path.getFileName().toString().endsWith(".zip")) break;
                            throw new ValueConversionException("Path must be a directory or zip!");
                        }
                    }
                }
                return path;
            }
            catch (InvalidPathException e) {
                throw new ValueConversionException("Failed to parse '" + s + "' to path!", e);
            }
        }

        @Override
        @Nonnull
        public Class<? extends Path> valueType() {
            return Path.class;
        }

        @Override
        @Nullable
        public String valuePattern() {
            return null;
        }

        public static enum PathType {
            FILE,
            DIR,
            DIR_OR_ZIP,
            ANY;

        }
    }

    public static class UUIDConverter
    implements ValueConverter<UUID> {
        @Override
        @Nonnull
        public UUID convert(@Nonnull String s) {
            return UUID.fromString(s);
        }

        @Override
        @Nonnull
        public Class<? extends UUID> valueType() {
            return UUID.class;
        }

        @Override
        @Nullable
        public String valuePattern() {
            return null;
        }
    }

    public static class StringToPathMapConverter
    implements ValueConverter<Map<String, Path>> {
        private static final Map<String, Level> MAP = new Object2ObjectOpenHashMap<String, Level>();

        @Override
        @Nonnull
        public Map<String, Path> convert(@Nonnull String value) {
            String[] strings;
            HashMap<String, Path> map = new HashMap<String, Path>();
            for (String string : strings = value.split(",")) {
                String[] split = string.split("=");
                if (split.length != 2) continue;
                if (map.containsKey(split[0])) {
                    throw new ValueConversionException("String '" + split[0] + "' has already been specified!");
                }
                Path path = PathUtil.get(split[1]);
                if (!Files.exists(path, new LinkOption[0])) {
                    throw new ValueConversionException("No file found for '" + split[1] + "'!");
                }
                map.put(split[0], path);
            }
            return map;
        }

        @Override
        @Nonnull
        public Class<Map<String, Path>> valueType() {
            return MAP.getClass();
        }

        @Override
        @Nullable
        public String valuePattern() {
            return null;
        }
    }

    private static class AuthModeConverter
    implements ValueConverter<AuthMode> {
        private AuthModeConverter() {
        }

        @Override
        public AuthMode convert(String value) {
            return AuthMode.valueOf(value.toUpperCase());
        }

        @Override
        public Class<? extends AuthMode> valueType() {
            return AuthMode.class;
        }

        @Override
        public String valuePattern() {
            return "authenticated|offline|insecure";
        }
    }

    public static enum AuthMode {
        AUTHENTICATED,
        OFFLINE,
        INSECURE;

    }
}

