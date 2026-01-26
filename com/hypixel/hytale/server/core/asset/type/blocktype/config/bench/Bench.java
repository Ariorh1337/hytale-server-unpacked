/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.lookup.ObjectCodecMapCodec;
import com.hypixel.hytale.protocol.BenchType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchTierLevel;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchUpgradeRequirement;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class Bench
implements NetworkSerializable<com.hypixel.hytale.protocol.Bench> {
    public static final ObjectCodecMapCodec<BenchType, Bench> CODEC = new ObjectCodecMapCodec("Type", new EnumCodec<BenchType>(BenchType.class));
    public static final BuilderCodec<Bench> BASE_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.abstractBuilder(Bench.class).addField(new KeyedCodec<String>("Id", Codec.STRING), (bench, s) -> {
        bench.id = s;
    }, bench -> bench.id)).addField(new KeyedCodec<String>("DescriptiveLabel", Codec.STRING), (bench, s) -> {
        bench.descriptiveLabel = s;
    }, bench -> bench.descriptiveLabel)).appendInherited(new KeyedCodec<T[]>("TierLevels", new ArrayCodec<BenchTierLevel>(BenchTierLevel.CODEC, BenchTierLevel[]::new)), (bench, u) -> {
        bench.tierLevels = u;
    }, bench -> bench.tierLevels, (bench, parent) -> {
        bench.tierLevels = parent.tierLevels;
    }).add()).append(new KeyedCodec<String>("LocalOpenSoundEventId", Codec.STRING), (bench, s) -> {
        bench.localOpenSoundEventId = s;
    }, bench -> bench.localOpenSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.ONESHOT).add()).append(new KeyedCodec<String>("LocalCloseSoundEventId", Codec.STRING), (bench, s) -> {
        bench.localCloseSoundEventId = s;
    }, bench -> bench.localCloseSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.ONESHOT).add()).append(new KeyedCodec<String>("CompletedSoundEventId", Codec.STRING), (bench, s) -> {
        bench.completedSoundEventId = s;
    }, bench -> bench.completedSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).add()).append(new KeyedCodec<String>("FailedSoundEventId", Codec.STRING), (bench, s) -> {
        bench.failedSoundEventId = s;
    }, bench -> bench.failedSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).add()).append(new KeyedCodec<String>("BenchUpgradeSoundEventId", Codec.STRING), (bench, s) -> {
        bench.benchUpgradeSoundEventId = s;
    }, bench -> bench.benchUpgradeSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).add()).append(new KeyedCodec<String>("BenchUpgradeCompletedSoundEventId", Codec.STRING), (bench, s) -> {
        bench.benchUpgradeCompletedSoundEventId = s;
    }, bench -> bench.benchUpgradeCompletedSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).add()).afterDecode(bench -> {
        bench.type = (BenchType)((Object)((Object)CODEC.getIdFor(bench.getClass())));
        if (bench.localOpenSoundEventId != null) {
            bench.localOpenSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.localOpenSoundEventId);
        }
        if (bench.localCloseSoundEventId != null) {
            bench.localCloseSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.localCloseSoundEventId);
        }
        if (bench.completedSoundEventId != null) {
            bench.completedSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.completedSoundEventId);
        }
        if (bench.benchUpgradeSoundEventId != null) {
            bench.benchUpgradeSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.benchUpgradeSoundEventId);
        }
        if (bench.benchUpgradeCompletedSoundEventId != null) {
            bench.benchUpgradeCompletedSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.benchUpgradeCompletedSoundEventId);
        }
        if (bench.failedSoundEventId != null) {
            bench.failedSoundEventIndex = SoundEvent.getAssetMap().getIndex(bench.failedSoundEventId);
        }
    })).build();
    @Deprecated(forRemoval=true)
    protected static final Map<BenchType, RootInteraction> BENCH_INTERACTIONS = new EnumMap<BenchType, RootInteraction>(BenchType.class);
    @Nonnull
    protected BenchType type = BenchType.Crafting;
    protected String id;
    protected String descriptiveLabel;
    protected BenchTierLevel[] tierLevels;
    @Nullable
    protected String localOpenSoundEventId = null;
    protected transient int localOpenSoundEventIndex = 0;
    @Nullable
    protected String localCloseSoundEventId = null;
    protected transient int localCloseSoundEventIndex = 0;
    @Nullable
    protected String completedSoundEventId = null;
    protected transient int completedSoundEventIndex = 0;
    @Nullable
    protected String failedSoundEventId = null;
    protected transient int failedSoundEventIndex = 0;
    @Nullable
    protected String benchUpgradeSoundEventId = null;
    protected transient int benchUpgradeSoundEventIndex = 0;
    @Nullable
    protected String benchUpgradeCompletedSoundEventId = null;
    protected transient int benchUpgradeCompletedSoundEventIndex = 0;

    public BenchType getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getDescriptiveLabel() {
        return this.descriptiveLabel;
    }

    public BenchTierLevel getTierLevel(int tierLevel) {
        if (this.tierLevels == null || tierLevel < 1 || tierLevel > this.tierLevels.length) {
            return null;
        }
        return this.tierLevels[tierLevel - 1];
    }

    public BenchUpgradeRequirement getUpgradeRequirement(int tierLevel) {
        BenchTierLevel currentTierLevel = this.getTierLevel(tierLevel);
        if (currentTierLevel == null) {
            return null;
        }
        return currentTierLevel.upgradeRequirement;
    }

    @Nullable
    public String getLocalOpenSoundEventId() {
        return this.localOpenSoundEventId;
    }

    public int getLocalOpenSoundEventIndex() {
        return this.localOpenSoundEventIndex;
    }

    @Nullable
    public String getLocalCloseSoundEventId() {
        return this.localCloseSoundEventId;
    }

    public int getLocalCloseSoundEventIndex() {
        return this.localCloseSoundEventIndex;
    }

    @Nullable
    public String getCompletedSoundEventId() {
        return this.completedSoundEventId;
    }

    public int getCompletedSoundEventIndex() {
        return this.completedSoundEventIndex;
    }

    @Nullable
    public String getFailedSoundEventId() {
        return this.failedSoundEventId;
    }

    public int getFailedSoundEventIndex() {
        return this.failedSoundEventIndex;
    }

    @Nullable
    public String getBenchUpgradeSoundEventId() {
        return this.benchUpgradeSoundEventId;
    }

    public int getBenchUpgradeSoundEventIndex() {
        return this.benchUpgradeSoundEventIndex;
    }

    @Nullable
    public String getBenchUpgradeCompletedSoundEventId() {
        return this.benchUpgradeCompletedSoundEventId;
    }

    public int getBenchUpgradeCompletedSoundEventIndex() {
        return this.benchUpgradeCompletedSoundEventIndex;
    }

    @Nullable
    public RootInteraction getRootInteraction() {
        return BENCH_INTERACTIONS.get((Object)this.type);
    }

    @Override
    public com.hypixel.hytale.protocol.Bench toPacket() {
        com.hypixel.hytale.protocol.Bench packet = new com.hypixel.hytale.protocol.Bench();
        if (this.tierLevels != null && this.tierLevels.length > 0) {
            packet.benchTierLevels = new com.hypixel.hytale.protocol.BenchTierLevel[this.tierLevels.length];
            for (int i = 0; i < this.tierLevels.length; ++i) {
                packet.benchTierLevels[i] = this.tierLevels[i].toPacket();
            }
        }
        return packet;
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Bench bench = (Bench)o;
        return this.localOpenSoundEventIndex == bench.localOpenSoundEventIndex && this.localCloseSoundEventIndex == bench.localCloseSoundEventIndex && this.completedSoundEventIndex == bench.completedSoundEventIndex && this.benchUpgradeSoundEventIndex == bench.benchUpgradeSoundEventIndex && this.benchUpgradeCompletedSoundEventIndex == bench.benchUpgradeCompletedSoundEventIndex && this.type == bench.type && Objects.equals(this.id, bench.id) && Objects.equals(this.descriptiveLabel, bench.descriptiveLabel) && Objects.deepEquals(this.tierLevels, bench.tierLevels) && Objects.equals(this.localOpenSoundEventId, bench.localOpenSoundEventId) && Objects.equals(this.localCloseSoundEventId, bench.localCloseSoundEventId) && Objects.equals(this.completedSoundEventId, bench.completedSoundEventId) && Objects.equals(this.failedSoundEventId, bench.failedSoundEventId) && Objects.equals(this.benchUpgradeSoundEventId, bench.benchUpgradeSoundEventId) && Objects.equals(this.benchUpgradeCompletedSoundEventId, bench.benchUpgradeCompletedSoundEventId);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.id, this.descriptiveLabel, Arrays.hashCode(this.tierLevels), this.localOpenSoundEventId, this.localOpenSoundEventIndex, this.localCloseSoundEventId, this.localCloseSoundEventIndex, this.completedSoundEventId, this.completedSoundEventIndex, this.failedSoundEventId, this.failedSoundEventIndex, this.benchUpgradeSoundEventId, this.benchUpgradeSoundEventIndex, this.benchUpgradeCompletedSoundEventId, this.benchUpgradeCompletedSoundEventIndex});
    }

    public String toString() {
        return "Bench{type=" + String.valueOf((Object)this.type) + ", id='" + this.id + "', descriptiveLabel='" + this.descriptiveLabel + "', tierLevels=" + Arrays.toString(this.tierLevels) + ", localOpenSoundEventId='" + this.localOpenSoundEventId + "', localOpenSoundEventIndex=" + this.localOpenSoundEventIndex + ", localCloseSoundEventId='" + this.localCloseSoundEventId + "', localCloseSoundEventIndex=" + this.localCloseSoundEventIndex + ", completedSoundEventId='" + this.completedSoundEventId + "', completedSoundEventIndex=" + this.completedSoundEventIndex + ", failedSoundEventId='" + this.failedSoundEventId + "', failedSoundEventIndex=" + this.failedSoundEventIndex + ", benchUpgradeSoundEventId='" + this.benchUpgradeSoundEventId + "', benchUpgradeSoundEventIndex=" + this.benchUpgradeSoundEventIndex + ", benchUpgradeCompletedSoundEventId='" + this.benchUpgradeCompletedSoundEventId + "', benchUpgradeCompletedSoundEventIndex=" + this.benchUpgradeCompletedSoundEventIndex + "}";
    }

    @Deprecated(forRemoval=true)
    public static void registerRootInteraction(BenchType benchType, RootInteraction interaction) {
        BENCH_INTERACTIONS.put(benchType, interaction);
    }

    public static class BenchSlot {
        public static final BuilderCodec<BenchSlot> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BenchSlot.class, BenchSlot::new).addField(new KeyedCodec<String>("Icon", Codec.STRING), (benchSlot, s) -> {
            benchSlot.icon = s;
        }, benchSlot -> benchSlot.icon)).build();
        protected String icon;

        protected BenchSlot() {
        }

        public String getIcon() {
            return this.icon;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            BenchSlot benchSlot = (BenchSlot)o;
            return this.icon != null ? this.icon.equals(benchSlot.icon) : benchSlot.icon == null;
        }

        public int hashCode() {
            return this.icon != null ? this.icon.hashCode() : 0;
        }

        @Nonnull
        public String toString() {
            return "BenchSlot{icon='" + this.icon + "'}";
        }
    }
}

