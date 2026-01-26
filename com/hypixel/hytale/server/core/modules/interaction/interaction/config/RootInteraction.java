/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.codec.validation.validator.FloatArrayValidator;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.InteractionCooldown;
import com.hypixel.hytale.protocol.RootInteractionSettings;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionRules;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RootInteraction
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, RootInteraction>>,
NetworkSerializable<com.hypixel.hytale.protocol.RootInteraction> {
    @Nonnull
    public static final BuilderCodec<InteractionCooldown> COOLDOWN_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(InteractionCooldown.class, InteractionCooldown::new).appendInherited(new KeyedCodec<String>("Id", Codec.STRING), (interactionCooldown, s) -> {
        interactionCooldown.cooldownId = s;
    }, interactionCooldown -> interactionCooldown.cooldownId, (interactionCooldown, parent) -> {
        interactionCooldown.cooldownId = parent.cooldownId;
    }).documentation("The Id for the cooldown.\n\nCooldowns can be used on different interactions but share a cooldown.").add()).appendInherited(new KeyedCodec<Float>("Cooldown", Codec.FLOAT), (interactionCooldown, s) -> {
        interactionCooldown.cooldown = s.floatValue();
    }, interactionCooldown -> Float.valueOf(interactionCooldown.cooldown), (interactionCooldown, parent) -> {
        interactionCooldown.cooldown = parent.cooldown;
    }).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f))).documentation("The time in seconds this cooldown should last for.").add()).appendInherited(new KeyedCodec<float[]>("Charges", Codec.FLOAT_ARRAY), (interactionCharges, s) -> {
        interactionCharges.chargeTimes = s;
    }, interactionCharges -> interactionCharges.chargeTimes, (interactionCharges, parent) -> {
        interactionCharges.chargeTimes = parent.chargeTimes;
    }).addValidator(new FloatArrayValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0f)))).documentation("The charge times available for this interaction.").add()).appendInherited(new KeyedCodec<Boolean>("SkipCooldownReset", Codec.BOOLEAN), (interactionCharges, s) -> {
        interactionCharges.skipCooldownReset = s;
    }, interactionCharges -> interactionCharges.skipCooldownReset, (interactionCharges, parent) -> {
        interactionCharges.skipCooldownReset = parent.skipCooldownReset;
    }).documentation("Determines whether resetting cooldown should be skipped.").add()).appendInherited(new KeyedCodec<Boolean>("InterruptRecharge", Codec.BOOLEAN), (interactionCharges, s) -> {
        interactionCharges.interruptRecharge = s;
    }, interactionCharges -> interactionCharges.interruptRecharge, (interactionCharges, parent) -> {
        interactionCharges.interruptRecharge = parent.interruptRecharge;
    }).documentation("Determines whether recharge is interrupted by use.").add()).appendInherited(new KeyedCodec<Boolean>("ClickBypass", Codec.BOOLEAN), (interactionCooldown, s) -> {
        interactionCooldown.clickBypass = s;
    }, interactionCooldown -> interactionCooldown.clickBypass, (interactionCooldown, parent) -> {
        interactionCooldown.clickBypass = parent.clickBypass;
    }).documentation("Whether this cooldown can be bypassed by clicking.").add()).build();
    @Nonnull
    public static final AssetBuilderCodec<String, RootInteraction> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(RootInteraction.class, RootInteraction::new, Codec.STRING, (o, i) -> {
        o.id = i;
    }, o -> o.id, (o, i) -> {
        o.data = i;
    }, o -> o.data).documentation("A **RootInteraction** serves as an entry point into a set of **Interaction**s.\n\nIn order to start an interaction chain a **RootInteraction** is required.\nA basic **RootInteraction** can simply contain a reference to single interaction within _Interactions_ field which will be the entire chain. More complex cases can configure the other fields.\n\nMost fields configured here apply to all **Interaction**s contained the root and any **Interaction**s they contain as well. Systems that look at tags for interactions may also check the root interaction as well reducing the need to duplicate them on all nested interactions.")).appendInherited(new KeyedCodec<String[]>("Interactions", Interaction.CHILD_ASSET_CODEC_ARRAY), (o, i) -> {
        o.interactionIds = i;
    }, o -> o.interactionIds, (o, p) -> {
        o.interactionIds = p.interactionIds;
    }).addValidator(Validators.nonNull()).addValidator(Validators.nonEmptyArray()).addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getArrayValidator().late()).documentation("The list of interactions that will be run when starting a chain with this root interaction. Interactions in this list will be run in sequence.").add()).appendInherited(new KeyedCodec<InteractionCooldown>("Cooldown", COOLDOWN_CODEC), (o, i) -> {
        o.cooldown = i;
    }, o -> o.cooldown, (o, p) -> {
        o.cooldown = p.cooldown;
    }).documentation("Cooldowns are used to prevent an interaction from running repeatedly too quickly.\n\nDuring a cooldown attempting to run an interaction with the same cooldown id will fail.").add()).appendInherited(new KeyedCodec<InteractionRules>("Rules", InteractionRules.CODEC), (o, i) -> {
        o.rules = i;
    }, o -> o.rules, (o, p) -> {
        o.rules = p.rules;
    }).documentation("A set of rules that control when this root interaction can run or what interactions this root being active prevents.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("Settings", new EnumMapCodec(GameMode.class, ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RootInteractionSettings.class, RootInteractionSettings::new).appendInherited(new KeyedCodec<InteractionCooldown>("Cooldown", COOLDOWN_CODEC), (o, i) -> {
        o.cooldown = i;
    }, o -> o.cooldown, (o, p) -> {
        o.cooldown = p.cooldown;
    }).documentation("Cooldowns are used to prevent an interaction from running repeatedly too quickly.\n\nDuring a cooldown attempting to run an interaction with the same cooldown id will fail.").add()).appendInherited(new KeyedCodec<Boolean>("AllowSkipChainOnClick", Codec.BOOLEAN), (o, i) -> {
        o.allowSkipChainOnClick = i;
    }, o -> o.allowSkipChainOnClick, (o, p) -> {
        o.allowSkipChainOnClick = p.allowSkipChainOnClick;
    }).documentation("Whether to skip the whole interaction chain when another click is sent.").add()).build())), (o, i) -> {
        o.settings = i;
    }, o -> o.settings, (o, p) -> {
        o.settings = p.settings;
    }).documentation("Per a gamemode settings.").add()).appendInherited(new KeyedCodec<Float>("ClickQueuingTimeout", Codec.FLOAT), (interaction, s) -> {
        interaction.clickQueuingTimeout = s.floatValue();
    }, interaction -> Float.valueOf(interaction.clickQueuingTimeout), (interaction, parent) -> {
        interaction.clickQueuingTimeout = parent.clickQueuingTimeout;
    }).documentation("Controls the amount of time this root interaction can remain in the click queue before being discarded.").add()).appendInherited(new KeyedCodec<Boolean>("RequireNewClick", Codec.BOOLEAN), (o, i) -> {
        o.requireNewClick = i;
    }, o -> o.requireNewClick, (o, p) -> {
        o.requireNewClick = p.requireNewClick;
    }).documentation("Requires the user to click again before running another root interaction of the same type.").add()).build();
    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(RootInteraction::getAssetStore));
    @Nonnull
    public static final ContainedAssetCodec<String, RootInteraction, ?> CHILD_ASSET_CODEC = new ContainedAssetCodec(RootInteraction.class, CODEC);
    @Nonnull
    public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec(CHILD_ASSET_CODEC, String[]::new);
    @Nonnull
    public static final MapCodec<String, HashMap<String, String>> CHILD_ASSET_CODEC_MAP = new MapCodec(CHILD_ASSET_CODEC, HashMap::new);
    private static AssetStore<String, RootInteraction, IndexedLookupTableAssetMap<String, RootInteraction>> ASSET_STORE;
    protected String id;
    protected AssetExtraInfo.Data data;
    @Nonnull
    protected String[] interactionIds = ArrayUtil.EMPTY_STRING_ARRAY;
    @Nullable
    protected InteractionCooldown cooldown;
    @Nonnull
    protected Map<GameMode, RootInteractionSettings> settings = Collections.emptyMap();
    protected boolean requireNewClick;
    protected float clickQueuingTimeout;
    @Nonnull
    protected InteractionRules rules = InteractionRules.DEFAULT_RULES;
    protected Operation[] operations;
    protected boolean needsRemoteSync;

    public RootInteraction() {
    }

    public RootInteraction(@Nonnull String id, String ... interactionIds) {
        this.id = id;
        this.interactionIds = interactionIds;
        this.data = new AssetExtraInfo.Data(RootInteraction.class, id, null);
    }

    public RootInteraction(@Nonnull String id, @Nullable InteractionCooldown cooldown, String ... interactionIds) {
        this.id = id;
        this.cooldown = cooldown;
        this.interactionIds = interactionIds;
        this.data = new AssetExtraInfo.Data(RootInteraction.class, id, null);
    }

    @Nonnull
    public static AssetStore<String, RootInteraction, IndexedLookupTableAssetMap<String, RootInteraction>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(RootInteraction.class);
        }
        return ASSET_STORE;
    }

    @Nonnull
    public static IndexedLookupTableAssetMap<String, RootInteraction> getAssetMap() {
        return RootInteraction.getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean needsRemoteSync() {
        return this.needsRemoteSync;
    }

    public boolean resetCooldownOnStart() {
        return this.cooldown == null || !this.cooldown.skipCooldownReset;
    }

    @Nullable
    public Operation getOperation(int index) {
        if (index >= this.operations.length) {
            return null;
        }
        return this.operations[index];
    }

    public int getOperationMax() {
        return this.operations.length;
    }

    public String[] getInteractionIds() {
        return this.interactionIds;
    }

    @Nonnull
    public Map<GameMode, RootInteractionSettings> getSettings() {
        return this.settings;
    }

    public float getClickQueuingTimeout() {
        return this.clickQueuingTimeout;
    }

    @Nonnull
    public InteractionRules getRules() {
        return this.rules;
    }

    @Nullable
    public InteractionCooldown getCooldown() {
        return this.cooldown;
    }

    public AssetExtraInfo.Data getData() {
        return this.data;
    }

    public void build(@Nonnull Set<String> modifiedInteractions) {
        if (this.operations == null) {
            return;
        }
        this.build();
    }

    public void build() {
        if (this.interactionIds == null) {
            return;
        }
        OperationsBuilder builder = new OperationsBuilder();
        boolean needsSyncRemote = false;
        for (String string : this.interactionIds) {
            Interaction interaction = (Interaction)Interaction.getAssetMap().getAsset(string);
            if (interaction == null) continue;
            interaction.compile(builder);
            needsSyncRemote |= interaction.needsRemoteSync();
        }
        this.operations = builder.build();
        this.needsRemoteSync = needsSyncRemote;
        for (Operation operation : this.operations) {
            Interaction inter;
            Operation operation2 = operation.getInnerOperation();
            if (!(operation2 instanceof Interaction) || (inter = (Interaction)operation2).getWaitForDataFrom() != WaitForDataFrom.Client || inter.needsRemoteSync()) continue;
            throw new IllegalArgumentException(String.valueOf(inter) + " needs client data but isn't marked as requiring syncing to remote clients");
        }
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.RootInteraction toPacket() {
        com.hypixel.hytale.protocol.RootInteraction packet = new com.hypixel.hytale.protocol.RootInteraction();
        packet.id = this.id;
        packet.interactions = new int[this.interactionIds.length];
        for (int i = 0; i < this.interactionIds.length; ++i) {
            packet.interactions[i] = Interaction.getInteractionIdOrUnknown(this.interactionIds[i]);
        }
        packet.clickQueuingTimeout = this.clickQueuingTimeout;
        packet.requireNewClick = this.requireNewClick;
        packet.rules = this.rules.toPacket();
        packet.settings = this.settings;
        packet.cooldown = this.cooldown;
        if (this.data != null) {
            packet.tags = this.data.getTags().keySet().toIntArray();
        }
        return packet;
    }

    @Nullable
    @Deprecated
    public static RootInteraction getRootInteractionOrUnknown(@Nonnull String id) {
        return RootInteraction.getAssetMap().getAsset(RootInteraction.getRootInteractionIdOrUnknown(id));
    }

    @Deprecated
    public static int getRootInteractionIdOrUnknown(@Nullable String id) {
        if (id == null) {
            return Integer.MIN_VALUE;
        }
        IndexedLookupTableAssetMap<String, RootInteraction> assetMap = RootInteraction.getAssetMap();
        int interactionId = assetMap.getIndex(id);
        if (interactionId == Integer.MIN_VALUE) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Missing root interaction %s", id);
            RootInteraction.getAssetStore().loadAssets("Hytale:Hytale", List.of(new RootInteraction(id, new String[0])));
            int index = assetMap.getIndex(id);
            if (index == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! " + id);
            }
            interactionId = index;
        }
        return interactionId;
    }

    @Nonnull
    public String toString() {
        return "RootInteraction{id='" + this.id + "', interactionIds=" + Arrays.toString(this.interactionIds) + ", settings=" + String.valueOf(this.settings) + ", requireNewClick=" + this.requireNewClick + ", clickQueuingTimeout=" + this.clickQueuingTimeout + ", rules=" + String.valueOf(this.rules) + ", operations=" + Arrays.toString(this.operations) + ", needsRemoteSync=" + this.needsRemoteSync + "}";
    }
}

