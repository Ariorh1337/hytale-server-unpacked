/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.asset.type.ambiencefx.config.AmbienceFX;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
import com.hypixel.hytale.server.core.auth.ServerAuthManager;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.ArgWrapper;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.AssetArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.Coord;
import com.hypixel.hytale.server.core.command.system.arguments.types.EntityWrappedArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.EnumArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.GameModeArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.IntCoord;
import com.hypixel.hytale.server.core.command.system.arguments.types.ListArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.MultiArgumentContext;
import com.hypixel.hytale.server.core.command.system.arguments.types.MultiArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.ProcessedArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeDoublePosition;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeFloat;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeIntPosition;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeInteger;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeIntegerRange;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeVector3i;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.WrappedArgumentType;
import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
import com.hypixel.hytale.server.core.command.system.suggestion.SuggestionResult;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.hitboxcollision.HitboxCollisionConfig;
import com.hypixel.hytale.server.core.modules.entity.repulsion.RepulsionConfig;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import it.unimi.dsi.fastutil.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ArgTypes {
    public static final SingleArgumentType<Boolean> BOOLEAN = new SingleArgumentType<Boolean>("server.commands.parsing.argtype.boolean.name", "server.commands.parsing.argtype.boolean.usage", new String[]{"true", "false"}){
        private static final String TRUE_STRING = "true";
        private static final String FALSE_STRING = "false";

        @Override
        @Nonnull
        public Boolean parse(String input, ParseResult parseResult) {
            return Boolean.parseBoolean(input);
        }

        @Override
        public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
            if (FALSE_STRING.startsWith(textAlreadyEntered = textAlreadyEntered.toLowerCase())) {
                result.suggest(FALSE_STRING);
                result.suggest(TRUE_STRING);
            } else {
                result.suggest(TRUE_STRING);
                result.suggest(FALSE_STRING);
            }
        }
    };
    public static final SingleArgumentType<Integer> INTEGER = new SingleArgumentType<Integer>("server.commands.parsing.argtype.integer.name", "server.commands.parsing.argtype.integer.usage", new String[]{"-27432", "-1", "0", "1", "56346"}){

        @Override
        @Nullable
        public Integer parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.integer.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<String> STRING = new SingleArgumentType<String>("server.commands.parsing.argtype.string.name", "server.commands.parsing.argtype.string.usage", new String[]{"\"Hytale is really cool!\"", "\"Numbers work 2!\"", "\"If you can type it...\""}){

        @Override
        public String parse(String input, ParseResult parseResult) {
            return input;
        }
    };
    public static final SingleArgumentType<Float> FLOAT = new SingleArgumentType<Float>("server.commands.parsing.argtype.float.name", "server.commands.parsing.argtype.float.usage", new String[]{"3.14159", "-2.5", "7"}){

        @Override
        @Nullable
        public Float parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return Float.valueOf(Float.parseFloat(input));
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.float.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<Double> DOUBLE = new SingleArgumentType<Double>("server.commands.parsing.argtype.double.name", "server.commands.parsing.argtype.double.usage", new String[]{"-3.14", "0.0", "3.141596"}){

        @Override
        @Nullable
        public Double parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return Double.parseDouble(input);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.double.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<UUID> UUID = new SingleArgumentType<UUID>("server.commands.parsing.argtype.uuid.name", "server.commands.parsing.argtype.uuid.usage", new String[]{java.util.UUID.randomUUID().toString()}){

        @Override
        @Nullable
        public UUID parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return java.util.UUID.fromString(input);
            }
            catch (IllegalArgumentException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.uuid.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<UUID> PLAYER_UUID = new SingleArgumentType<UUID>("server.commands.parsing.argtype.playerUuid.name", "server.commands.parsing.argtype.playerUuid.usage", new String[]{java.util.UUID.randomUUID().toString(), "john_doe", "user123"}){

        @Override
        @Nullable
        public UUID parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            try {
                return java.util.UUID.fromString(input);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                for (World world : Universe.get().getWorlds().values()) {
                    Collection<PlayerRef> playerRefs = world.getPlayerRefs();
                    PlayerRef playerRef = NameMatching.DEFAULT.find(playerRefs, input, PlayerRef::getUsername);
                    if (playerRef == null) continue;
                    return playerRef.getUuid();
                }
                parseResult.fail(Message.translation("server.commands.parsing.argtype.playerUuid.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<CompletableFuture<ProfileServiceClient.PublicGameProfile>> GAME_PROFILE_LOOKUP_ASYNC = new SingleArgumentType<CompletableFuture<ProfileServiceClient.PublicGameProfile>>("server.commands.parsing.argtype.playerUuidLookup.name", "server.commands.parsing.argtype.playerUuidLookup.usage", new String[]{java.util.UUID.randomUUID().toString(), "john_doe", "user123"}){

        @Override
        @Nullable
        public CompletableFuture<ProfileServiceClient.PublicGameProfile> parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            for (World world : Universe.get().getWorlds().values()) {
                Collection<PlayerRef> playerRefs = world.getPlayerRefs();
                PlayerRef playerRef = NameMatching.DEFAULT.find(playerRefs, input, PlayerRef::getUsername);
                if (playerRef == null) continue;
                return CompletableFuture.completedFuture(new ProfileServiceClient.PublicGameProfile(playerRef.getUuid(), playerRef.getUsername()));
            }
            UUID inputAsUuid = null;
            try {
                inputAsUuid = java.util.UUID.fromString(input);
            }
            catch (IllegalArgumentException world) {
                // empty catch block
            }
            ServerAuthManager authManager = ServerAuthManager.getInstance();
            String sessionToken = authManager.getSessionToken();
            if (sessionToken == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("No session token available for profile lookup"));
            }
            ProfileServiceClient profileClient = authManager.getProfileServiceClient();
            CompletableFuture<ProfileServiceClient.PublicGameProfile> future = inputAsUuid != null ? profileClient.getProfileByUuidAsync(inputAsUuid, sessionToken) : profileClient.getProfileByUsernameAsync(input, sessionToken);
            return future.thenApply(profile -> {
                if (profile == null || profile.getUuid() == null) {
                    throw new IllegalArgumentException("Player not found: " + input);
                }
                return profile;
            });
        }
    };
    public static final SingleArgumentType<ProfileServiceClient.PublicGameProfile> GAME_PROFILE_LOOKUP = new SingleArgumentType<ProfileServiceClient.PublicGameProfile>("server.commands.parsing.argtype.playerUuidLookup.name", "server.commands.parsing.argtype.playerUuidLookup.usage", new String[]{java.util.UUID.randomUUID().toString(), "john_doe", "user123"}){

        @Override
        @Nullable
        public ProfileServiceClient.PublicGameProfile parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            try {
                return GAME_PROFILE_LOOKUP_ASYNC.parse(input, parseResult).join();
            }
            catch (Exception e) {
                Throwable cause;
                Throwable throwable = cause = e.getCause() != null ? e.getCause() : e;
                if (cause instanceof IllegalStateException) {
                    parseResult.fail(Message.translation("server.commands.parsing.argtype.playerUuidLookup.noAuth").param("input", input));
                } else if (cause instanceof IllegalArgumentException) {
                    parseResult.fail(Message.translation("server.commands.parsing.argtype.playerUuidLookup.notFound").param("input", input));
                } else {
                    parseResult.fail(Message.translation("server.commands.parsing.argtype.playerUuidLookup.lookupError").param("input", input).param("error", cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName()));
                }
                return null;
            }
        }
    };
    public static final SingleArgumentType<Coord> RELATIVE_DOUBLE_COORD = new SingleArgumentType<Coord>("server.commands.parsing.argtype.doubleCoordinate.name", "server.commands.parsing.argtype.doubleCoordinate.usage", new String[]{"5.0", "~-2.3", "0.0"}){

        @Override
        @Nullable
        public Coord parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return Coord.parse(input);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.doubleCoordinate.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<IntCoord> RELATIVE_INT_COORD = new SingleArgumentType<IntCoord>("server.commands.parsing.argtype.integerCoordinate.name", "server.commands.parsing.argtype.integerCoordinate.usage", new String[]{"5", "~-2", "0"}){

        @Override
        @Nullable
        public IntCoord parse(@Nonnull String input, ParseResult parseResult) {
            try {
                return IntCoord.parse(input);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.integerCoordinate.fail").param("input", input));
                return null;
            }
        }
    };
    public static final SingleArgumentType<RelativeInteger> RELATIVE_INTEGER = new SingleArgumentType<RelativeInteger>("Relative Integer", "A tilde to mark an integer as relative to a base", new String[]{"5", "~-2", "0"}){

        @Override
        @Nullable
        public RelativeInteger parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            return RelativeInteger.parse(input, parseResult);
        }
    };
    public static final SingleArgumentType<RelativeFloat> RELATIVE_FLOAT = new SingleArgumentType<RelativeFloat>("server.commands.parsing.argtype.relativeFloat.name", "server.commands.parsing.argtype.relativeFloat.usage", new String[]{"90.0", "~-45.5", "~"}){

        @Override
        @Nullable
        public RelativeFloat parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            return RelativeFloat.parse(input, parseResult);
        }
    };
    public static final SingleArgumentType<PlayerRef> PLAYER_REF = new SingleArgumentType<PlayerRef>("server.commands.parsing.argtype.player.name", "server.commands.parsing.argtype.player.usage", new String[]{"john_doe", "user123"}){

        @Override
        @Nullable
        public PlayerRef parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            World world;
            Collection<PlayerRef> playerRefs;
            PlayerRef playerRef = null;
            Iterator<World> iterator = Universe.get().getWorlds().values().iterator();
            while (iterator.hasNext() && (playerRef = NameMatching.DEFAULT.find(playerRefs = (world = iterator.next()).getPlayerRefs(), input, PlayerRef::getUsername)) == null) {
            }
            if (playerRef == null) {
                parseResult.fail(Message.translation("server.commands.errors.noSuchPlayer").param("username", input));
                return null;
            }
            return playerRef;
        }

        @Override
        @Nonnull
        public PlayerRef processedGet(CommandSender sender, @Nonnull CommandContext context, Argument<?, PlayerRef> argument) {
            PlayerRef playerRef = context.get(argument);
            if (playerRef != null) {
                return playerRef;
            }
            if (!(sender instanceof Player)) {
                throw new GeneralCommandException(Message.translation("server.commands.errors.playerOrArg").param("option", "player"));
            }
            Player player = (Player)sender;
            return player.getPlayerRef();
        }
    };
    public static final SingleArgumentType<World> WORLD = new SingleArgumentType<World>("server.commands.parsing.argtype.world.name", "server.commands.parsing.argtype.world.usage", new String[]{"default"}){

        @Override
        @Nullable
        public World parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            World world = Universe.get().getWorld(input);
            if (world == null) {
                parseResult.fail(Message.translation("server.commands.errors.noSuchWorld").param("name", input));
                return null;
            }
            return world;
        }

        @Override
        @Nullable
        public World processedGet(CommandSender sender, @Nonnull CommandContext context, @Nonnull Argument<?, World> argument) {
            Iterator<World> iterator;
            World world = argument.get(context);
            if (world != null) {
                return world;
            }
            if (sender instanceof Player) {
                return ((Player)sender).getWorld();
            }
            Universe universe = Universe.get();
            if (universe.getWorlds().size() == 1 && (iterator = universe.getWorlds().values().iterator()).hasNext()) {
                return iterator.next();
            }
            throw new GeneralCommandException(Message.translation("server.commands.errors.playerOrArg").param("option", "world"));
        }
    };
    public static final SingleArgumentType<ModelAsset> MODEL_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.model.name", ModelAsset.class, "server.commands.parsing.argtype.asset.model.usage");
    public static final SingleArgumentType<Weather> WEATHER_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.weather.name", Weather.class, "server.commands.parsing.argtype.asset.weather.usage");
    public static final SingleArgumentType<Interaction> INTERACTION_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.interaction.name", Interaction.class, "server.commands.parsing.argtype.asset.interaction.usage");
    public static final SingleArgumentType<RootInteraction> ROOT_INTERACTION_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.rootinteraction.name", RootInteraction.class, "server.commands.parsing.argtype.asset.interaction.usage");
    public static final SingleArgumentType<EntityEffect> EFFECT_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.effect.name", EntityEffect.class, "server.commands.parsing.argtype.asset.effect.usage");
    public static final SingleArgumentType<Environment> ENVIRONMENT_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.environment.name", Environment.class, "server.commands.parsing.argtype.asset.environment.usage");
    public static final SingleArgumentType<Item> ITEM_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.item.name", Item.class, "server.commands.parsing.argtype.asset.item.usage");
    public static final SingleArgumentType<BlockType> BLOCK_TYPE_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.blocktype.name", BlockType.class, "server.commands.parsing.argtype.asset.blocktype.usage");
    public static final SingleArgumentType<ParticleSystem> PARTICLE_SYSTEM = new AssetArgumentType("server.commands.parsing.argtype.asset.particlesystem.name", ParticleSystem.class, "server.commands.parsing.argtype.asset.particlesystem.usage");
    public static final SingleArgumentType<HitboxCollisionConfig> HITBOX_COLLISION_CONFIG = new AssetArgumentType("server.commands.parsing.argtype.asset.hitboxcollisionconfig.name", HitboxCollisionConfig.class, "server.commands.parsing.argtype.asset.hitboxcollisionconfig.usage");
    public static final SingleArgumentType<RepulsionConfig> REPULSION_CONFIG = new AssetArgumentType("server.commands.parsing.argtype.asset.repulsionconfig.name", RepulsionConfig.class, "server.commands.parsing.argtype.asset.repulsionconfig.usage");
    public static final SingleArgumentType<SoundEvent> SOUND_EVENT_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.soundevent.name", SoundEvent.class, "server.commands.parsing.argtype.asset.soundevent.usage");
    public static final SingleArgumentType<AmbienceFX> AMBIENCE_FX_ASSET = new AssetArgumentType("server.commands.parsing.argtype.asset.ambiencefx.name", AmbienceFX.class, "server.commands.parsing.argtype.asset.ambiencefx.usage");
    public static final SingleArgumentType<SoundCategory> SOUND_CATEGORY = ArgTypes.forEnum("server.commands.parsing.argtype.soundcategory.name", SoundCategory.class);
    public static final ArgWrapper<EntityWrappedArg, UUID> ENTITY_ID = new ArgWrapper<EntityWrappedArg, UUID>(UUID.withOverriddenUsage("server.commands.parsing.argtype.entityid.usage"), EntityWrappedArg::new);
    public static final SingleArgumentType<IntegerComparisonOperator> INTEGER_COMPARISON_OPERATOR = new SingleArgumentType<IntegerComparisonOperator>("Integer Comparison Operator", "A mathematical sign for integer comparison", new String[]{">", "<", ">=", "<=", "%", "=", "!="}){

        @Override
        @Nullable
        public IntegerComparisonOperator parse(String input, @Nonnull ParseResult parseResult) {
            IntegerComparisonOperator integerComparisonOperator = IntegerComparisonOperator.getFromStringRepresentation(input);
            if (integerComparisonOperator == null) {
                parseResult.fail(Message.raw("Could not find an integer comparison operator for value: '" + input + "'."));
                return null;
            }
            return integerComparisonOperator;
        }
    };
    public static final SingleArgumentType<IntegerOperation> INTEGER_OPERATION = new SingleArgumentType<IntegerOperation>("Integer Operator", "A mathematical sign for performing an operation", new String[]{"+", "-", "*", "/", "%", "="}){

        @Override
        @Nullable
        public IntegerOperation parse(String input, @Nonnull ParseResult parseResult) {
            IntegerOperation integerOperation = IntegerOperation.getFromStringRepresentation(input);
            if (integerOperation == null) {
                parseResult.fail(Message.raw("Could not find an integer operator for value: '" + input + "'."));
                return null;
            }
            return integerOperation;
        }
    };
    public static final ArgumentType<Pair<Integer, Integer>> INT_RANGE = new MultiArgumentType<Pair<Integer, Integer>>("Integer Range", "Two integers representing a minimum and maximum of a range", new String[]{"-2 8", "5 5", "1 5"}){
        private final WrappedArgumentType<Integer> minValue = this.withParameter("min", "Minimum value, must be less than or equal to max value", INTEGER);
        private final WrappedArgumentType<Integer> maxValue = this.withParameter("max", "Maximum value, must be greater than or equal to min value", INTEGER);

        @Override
        @Nullable
        public Pair<Integer, Integer> parse(@Nonnull MultiArgumentContext context, @Nonnull ParseResult parseResult) {
            if (context.get(this.minValue) > context.get(this.maxValue)) {
                parseResult.fail(Message.raw("You cannot set the minimum value as larger than the maximum value. Min: " + String.valueOf(context.get(this.minValue)) + " Max: " + String.valueOf(context.get(this.maxValue))));
                return null;
            }
            return Pair.of(context.get(this.minValue), context.get(this.maxValue));
        }
    };
    public static final ArgumentType<RelativeIntegerRange> RELATIVE_INT_RANGE = new MultiArgumentType<RelativeIntegerRange>("Integer Range", "Two integers representing a minimum and maximum of a range", new String[]{"~-2 ~8", "~5 ~5", "~1 ~5"}){
        private final WrappedArgumentType<RelativeInteger> minValue = this.withParameter("min", "Minimum value, must be less than or equal to max value", RELATIVE_INTEGER);
        private final WrappedArgumentType<RelativeInteger> maxValue = this.withParameter("max", "Maximum value, must be greater than or equal to min value", RELATIVE_INTEGER);

        @Override
        @Nullable
        public RelativeIntegerRange parse(@Nonnull MultiArgumentContext context, @Nonnull ParseResult parseResult) {
            RelativeInteger min = this.minValue.get(context);
            RelativeInteger max = this.maxValue.get(context);
            if (min == null || max == null) {
                parseResult.fail(Message.raw("Could not parse min or max value of the range."));
                return null;
            }
            if (min.isRelative() != max.isRelative()) {
                parseResult.fail(Message.raw("Your range must have both min and max as relative, or both as not relative. You can not mix relatives in ranges."));
                return null;
            }
            if (min.getRawValue() > max.getRawValue()) {
                parseResult.fail(Message.raw("You cannot set the minimum value as larger than the maximum value. Min: " + String.valueOf(context.get(this.minValue)) + " Max: " + String.valueOf(context.get(this.maxValue))));
                return null;
            }
            return new RelativeIntegerRange(context.get(this.minValue), context.get(this.maxValue));
        }
    };
    public static final ArgumentType<Vector2i> VECTOR2I = new MultiArgumentType<Vector2i>("Integer Vector 2D", "Two integers, generally corresponding to x/z axis", new String[]{"124 232", "5 -3", "1 1"}){
        private final WrappedArgumentType<Integer> xValue = this.withParameter("x", "X value", INTEGER);
        private final WrappedArgumentType<Integer> zValue = this.withParameter("z", "Z value", INTEGER);

        @Override
        @Nonnull
        public Vector2i parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new Vector2i(context.get(this.xValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<Vector3i> VECTOR3I = new MultiArgumentType<Vector3i>("Integer Vector", "Three integers, generally corresponding to x/y/z axis", new String[]{"124 232 234", "5 0 -3", "1 1 1"}){
        private final WrappedArgumentType<Integer> xValue = this.withParameter("x", "X value", INTEGER);
        private final WrappedArgumentType<Integer> yValue = this.withParameter("y", "Y value", INTEGER);
        private final WrappedArgumentType<Integer> zValue = this.withParameter("z", "Z value", INTEGER);

        @Override
        @Nonnull
        public Vector3i parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new Vector3i(context.get(this.xValue), context.get(this.yValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<RelativeVector3i> RELATIVE_VECTOR3I = new MultiArgumentType<RelativeVector3i>("Relative Integer Vector", "Three optionally relative integers, generally corresponding to x/y/z axis", new String[]{"124 ~232 234", "~5 0 ~-3", "1 ~1 1"}){
        private final WrappedArgumentType<RelativeInteger> xValue = this.withParameter("x", "X value", RELATIVE_INTEGER);
        private final WrappedArgumentType<RelativeInteger> yValue = this.withParameter("y", "Y value", RELATIVE_INTEGER);
        private final WrappedArgumentType<RelativeInteger> zValue = this.withParameter("z", "Z value", RELATIVE_INTEGER);

        @Override
        @Nonnull
        public RelativeVector3i parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new RelativeVector3i(context.get(this.xValue), context.get(this.yValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<RelativeIntPosition> RELATIVE_BLOCK_POSITION = new MultiArgumentType<RelativeIntPosition>("server.commands.parsing.argtype.relativeBlockPosition.name", "server.commands.parsing.argtype.relativeBlockPosition.usage", new String[]{"124 232 234", "~5 ~ ~-3", "~ ~ ~"}){
        private final WrappedArgumentType<IntCoord> xValue = this.withParameter("x", "server.commands.parsing.argtype.xCoord.usage", RELATIVE_INT_COORD);
        private final WrappedArgumentType<IntCoord> yValue = this.withParameter("y", "server.commands.parsing.argtype.yCoord.usage", RELATIVE_INT_COORD);
        private final WrappedArgumentType<IntCoord> zValue = this.withParameter("z", "server.commands.parsing.argtype.zCoord.usage", RELATIVE_INT_COORD);

        @Override
        @Nonnull
        public RelativeIntPosition parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new RelativeIntPosition(context.get(this.xValue), context.get(this.yValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<RelativeDoublePosition> RELATIVE_POSITION = new MultiArgumentType<RelativeDoublePosition>("server.commands.parsing.argtype.relativePosition.name", "server.commands.parsing.argtype.relativePosition.usage", new String[]{"124.63 232.27 234.22", "~5.5 ~ ~", "~ ~ ~"}){
        private final WrappedArgumentType<Coord> xValue = this.withParameter("x", "server.commands.parsing.argtype.xCoord.usage", RELATIVE_DOUBLE_COORD);
        private final WrappedArgumentType<Coord> yValue = this.withParameter("y", "server.commands.parsing.argtype.yCoord.usage", RELATIVE_DOUBLE_COORD);
        private final WrappedArgumentType<Coord> zValue = this.withParameter("z", "server.commands.parsing.argtype.zCoord.usage", RELATIVE_DOUBLE_COORD);

        @Override
        @Nonnull
        public RelativeDoublePosition parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new RelativeDoublePosition(context.get(this.xValue), context.get(this.yValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<RelativeChunkPosition> RELATIVE_CHUNK_POSITION = new MultiArgumentType<RelativeChunkPosition>("server.commands.parsing.argtype.relativeChunkPosition.name", "server.commands.parsing.argtype.relativeChunkPosition.usage", new String[]{"5 10", "~c2 ~c-3", "~ ~"}){
        private final WrappedArgumentType<IntCoord> xValue = this.withParameter("x", "server.commands.parsing.argtype.xCoord.usage", RELATIVE_INT_COORD);
        private final WrappedArgumentType<IntCoord> zValue = this.withParameter("z", "server.commands.parsing.argtype.zCoord.usage", RELATIVE_INT_COORD);

        @Override
        @Nonnull
        public RelativeChunkPosition parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new RelativeChunkPosition(context.get(this.xValue), context.get(this.zValue));
        }
    };
    public static final ArgumentType<Vector3f> ROTATION = new MultiArgumentType<Vector3f>("server.commands.parsing.argtype.rotation.name", "server.commands.parsing.argtype.rotation.usage", new String[]{"124.63 232.27 234.22"}){
        private final WrappedArgumentType<Float> pitch = this.withParameter("server.commands.parsing.argtype.pitch.name", "server.commands.parsing.argtype.pitch.usage", FLOAT);
        private final WrappedArgumentType<Float> yaw = this.withParameter("server.commands.parsing.argtype.yaw.name", "server.commands.parsing.argtype.yaw.usage", FLOAT);
        private final WrappedArgumentType<Float> roll = this.withParameter("server.commands.parsing.argtype.roll.name", "server.commands.parsing.argtype.roll.usage", FLOAT);

        @Override
        @Nonnull
        public Vector3f parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return new Vector3f(context.get(this.pitch).floatValue(), context.get(this.yaw).floatValue(), context.get(this.roll).floatValue());
        }
    };
    public static final SingleArgumentType<String> BLOCK_TYPE_KEY = new SingleArgumentType<String>("Block Type Key", "A block type", new String[]{"Wood_Drywood_Planks_Half"}){

        @Override
        @Nullable
        public String parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            try {
                return input;
            }
            catch (Exception e) {
                parseResult.fail(Message.raw(e.getMessage()));
                return null;
            }
        }
    };
    public static final ArgumentType<Integer> BLOCK_ID = new ProcessedArgumentType<String, Integer>("Block Id", Message.raw("A block type, converted to an int id"), BLOCK_TYPE_KEY, new String[]{"Wood_Drywood_Planks_Half"}){

        @Override
        @Nonnull
        public Integer processInput(String blockTypeKey) {
            return BlockType.getAssetMap().getIndex(blockTypeKey);
        }
    };
    public static final SingleArgumentType<Integer> COLOR = new SingleArgumentType<Integer>("server.commands.parsing.argtype.color.name", "server.commands.parsing.argtype.color.usage", new String[]{"#FF0000", "#00FF00FF", "16711680", "0xFF0000"}){

        @Override
        @Nullable
        public Integer parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            if ((input = input.trim()).isEmpty()) {
                parseResult.fail(Message.raw("Color cannot be empty"));
                return null;
            }
            if (input.charAt(0) == '#') {
                try {
                    String hexString = input.substring(1);
                    long value = Long.parseLong(hexString, 16);
                    switch (hexString.length()) {
                        case 3: {
                            int r = (int)(value >> 8 & 0xFL);
                            int g = (int)(value >> 4 & 0xFL);
                            int b = (int)(value & 0xFL);
                            return 0xFF000000 | r << 20 | r << 16 | g << 12 | g << 8 | b << 4 | b;
                        }
                        case 4: {
                            int r4 = (int)(value >> 12 & 0xFL);
                            int g4 = (int)(value >> 8 & 0xFL);
                            int b4 = (int)(value >> 4 & 0xFL);
                            int a4 = (int)(value & 0xFL);
                            return r4 << 28 | r4 << 24 | g4 << 20 | g4 << 16 | b4 << 12 | b4 << 8 | a4 << 4 | a4;
                        }
                        case 6: {
                            return 0xFF000000 | (int)value;
                        }
                        case 8: {
                            return (int)value;
                        }
                    }
                    parseResult.fail(Message.raw("Invalid hex color format. Expected #RGB, #RGBA, #RRGGBB, or #RRGGBBAA, got: '" + input + "'"));
                    return null;
                }
                catch (NumberFormatException e) {
                    parseResult.fail(Message.raw("Invalid hex color: '" + input + "'. " + e.getMessage()));
                    return null;
                }
            }
            if (input.length() > 2 && (input.startsWith("0x") || input.startsWith("0X"))) {
                try {
                    return Integer.parseUnsignedInt(input.substring(2), 16);
                }
                catch (NumberFormatException e) {
                    parseResult.fail(Message.raw("Invalid hex integer color: '" + input + "'. " + e.getMessage()));
                    return null;
                }
            }
            try {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.raw("Invalid color format. Expected hex color (#RRGGBB), hex integer (0xFF0000), or decimal integer (16711680), got: '" + input + "'"));
                return null;
            }
        }
    };
    public static final ArgumentType<Pair<Integer, String>> WEIGHTED_BLOCK_TYPE = new MultiArgumentType<Pair<Integer, String>>("Weighted Block Type", "A weight corresponding to a blocktype", new String[]{"5 Empty", "20 Rock_Stone", "2 Rock_Shale"}){
        private final WrappedArgumentType<Integer> weight = this.withParameter("weight", "The relative weight of this entry. Think of it as a lottery ticket", INTEGER);
        private final WrappedArgumentType<String> blockType = this.withParameter("blockType", "The BlockTypeKey associated with the weight", BLOCK_TYPE_KEY);

        @Override
        @Nonnull
        public Pair<Integer, String> parse(@Nonnull MultiArgumentContext context, ParseResult parseResult) {
            return Pair.of(context.get(this.weight), context.get(this.blockType));
        }
    };
    private static final ArgumentType<String> WEIGHTED_BLOCK_ENTRY = new SingleArgumentType<String>("Weighted Block Entry", "A block with optional weight prefix (e.g., 20%Rock_Stone or Rock_Stone)", new String[]{"Rock_Stone", "20%Rock_Stone", "50%Rock_Stone|Yaw=90"}){

        @Override
        @Nullable
        public String parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            int blockId;
            int pipeIndex;
            if (input.isEmpty()) {
                parseResult.fail(Message.raw("Block entry cannot be empty"));
                return null;
            }
            String blockName = input;
            int percentIndex = blockName.indexOf(37);
            if (percentIndex != -1) {
                blockName = blockName.substring(percentIndex + 1);
            }
            if ((pipeIndex = blockName.indexOf(124)) != -1) {
                blockName = blockName.substring(0, pipeIndex);
            }
            if ((blockId = BlockType.getAssetMap().getIndex(blockName)) == Integer.MIN_VALUE) {
                parseResult.fail(Message.translation("server.builderTools.invalidBlockType").param("name", "").param("key", blockName));
                return null;
            }
            return input;
        }
    };
    public static final ArgumentType<BlockPattern> BLOCK_PATTERN = new ProcessedArgumentType<List<String>, BlockPattern>("Block Pattern", Message.raw("A list of blocks with optional weights (e.g., [20%Rock_Stone, 80%Rock_Shale])"), new ListArgumentType<String>(WEIGHTED_BLOCK_ENTRY), new String[]{"[Rock_Stone]", "[20%Rock_Stone, 80%Rock_Shale]", "[50%Rock_Stone|Yaw=90, 50%Fluid_Water]"}){

        @Override
        @Nonnull
        public BlockPattern processInput(@Nonnull List<String> entries) {
            String patternString = String.join((CharSequence)",", entries);
            return BlockPattern.parse(patternString);
        }
    };
    private static final ArgumentType<BlockMask> INDIVIDUAL_BLOCK_MASK = new SingleArgumentType<BlockMask>("Block Mask", "Create a block mask using symbols and block names", new String[]{">Grass_Full", "!Fluid_Water", "!^Fluid_Lava", "!#"}){

        @Override
        @Nullable
        public BlockMask parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            try {
                return BlockMask.parse(input);
            }
            catch (Exception e) {
                parseResult.fail(Message.raw("There was an error in the parsing of your block mask: " + input + ", please try again."));
                return null;
            }
        }
    };
    public static final ArgumentType<BlockMask> BLOCK_MASK = new ProcessedArgumentType<List<BlockMask>, BlockMask>("Block Mask", Message.raw("A list of block masks that combine together"), new ListArgumentType<BlockMask>(INDIVIDUAL_BLOCK_MASK), new String[]{"[!Fluid_Water, !^Fluid_Lava]", "[>Grass_Full, !#]"}){

        @Override
        public BlockMask processInput(@Nonnull List<BlockMask> masks) {
            return BlockMask.combine((BlockMask[])masks.toArray(BlockMask[]::new));
        }
    };
    public static final SingleArgumentType<Integer> TICK_RATE = new SingleArgumentType<Integer>("server.commands.parsing.argtype.tickrate.name", "server.commands.parsing.argtype.tickrate.usage", new String[]{"30tps", "33ms", "60", "20tps", "50ms"}){

        @Override
        @Nullable
        public Integer parse(@Nonnull String input, @Nonnull ParseResult parseResult) {
            String trimmed = input.trim().toLowerCase();
            if (trimmed.isEmpty()) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.tickrate.empty"));
                return null;
            }
            try {
                if (trimmed.endsWith("tps")) {
                    String value = trimmed.substring(0, trimmed.length() - 3).trim();
                    return Integer.parseInt(value);
                }
                if (trimmed.endsWith("ms")) {
                    String value = trimmed.substring(0, trimmed.length() - 2).trim();
                    double ms = Double.parseDouble(value);
                    if (ms <= 0.0) {
                        parseResult.fail(Message.translation("server.commands.parsing.argtype.tickrate.invalidMs").param("input", input));
                        return null;
                    }
                    return (int)Math.round(1000.0 / ms);
                }
                return Integer.parseInt(trimmed);
            }
            catch (NumberFormatException e) {
                parseResult.fail(Message.translation("server.commands.parsing.argtype.tickrate.fail").param("input", input));
                return null;
            }
        }

        @Override
        public void suggest(@Nonnull CommandSender sender, @Nonnull String textAlreadyEntered, int numParametersTyped, @Nonnull SuggestionResult result) {
            result.suggest("30tps");
            result.suggest("60tps");
            result.suggest("20tps");
            result.suggest("33ms");
            result.suggest("16ms");
            result.suggest("50ms");
        }
    };
    public static final SingleArgumentType<GameMode> GAME_MODE = new GameModeArgumentType();

    @Nonnull
    public static <E extends Enum<E>> SingleArgumentType<E> forEnum(String name, @Nonnull Class<E> enumType) {
        return new EnumArgumentType<E>(name, enumType);
    }

    public static enum IntegerOperation {
        ADD(Integer::sum, "+"),
        SUBTRACT((previous, modifier) -> previous - modifier, "-"),
        MULTIPLY((previous, modifier) -> previous * modifier, "*"),
        DIVIDE((previous, modifier) -> previous / modifier, "/"),
        MODULUS((previous, modifier) -> previous % modifier, "%"),
        SET((previous, modifier) -> modifier, "=");

        @Nonnull
        private final BiFunction<Integer, Integer, Integer> operationFunction;
        @Nonnull
        private final String stringRepresentation;

        private IntegerOperation(BiFunction<Integer, Integer, Integer> operationFunction, String stringRepresentation) {
            this.operationFunction = operationFunction;
            this.stringRepresentation = stringRepresentation;
        }

        public int operate(int previous, int modifier) {
            return this.operationFunction.apply(previous, modifier);
        }

        public String getStringRepresentation() {
            return this.stringRepresentation;
        }

        @Nullable
        public static IntegerOperation getFromStringRepresentation(@Nonnull String stringRepresentation) {
            for (IntegerOperation value : IntegerOperation.values()) {
                if (!value.stringRepresentation.equals(stringRepresentation)) continue;
                return value;
            }
            return null;
        }
    }

    public static enum IntegerComparisonOperator {
        GREATER_THAN((left, right) -> left > right, ">"),
        GREATER_THAN_EQUAL_TO((left, right) -> left >= right, ">="),
        LESS_THAN((left, right) -> left < right, "<"),
        LESS_THAN_EQUAL_TO((left, right) -> left <= right, "<="),
        MOD_EQUAL_ZERO((left, right) -> left % right == 0, "%"),
        MOD_NOT_EQUAL_ZERO((left, right) -> left % right != 0, "!%"),
        EQUAL_TO(Integer::equals, "="),
        NOT_EQUAL_TO((left, right) -> !left.equals(right), "!=");

        private final BiFunction<Integer, Integer, Boolean> comparisonFunction;
        private final String stringRepresentation;

        private IntegerComparisonOperator(BiFunction<Integer, Integer, Boolean> comparisonFunction, String stringRepresentation) {
            this.comparisonFunction = comparisonFunction;
            this.stringRepresentation = stringRepresentation;
        }

        public boolean compare(int left, int right) {
            return this.comparisonFunction.apply(left, right);
        }

        public String getStringRepresentation() {
            return this.stringRepresentation;
        }

        @Nullable
        public static IntegerComparisonOperator getFromStringRepresentation(String stringRepresentation) {
            for (IntegerComparisonOperator value : IntegerComparisonOperator.values()) {
                if (!value.stringRepresentation.equals(stringRepresentation)) continue;
                return value;
            }
            return null;
        }
    }
}

