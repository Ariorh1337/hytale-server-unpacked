/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;

public class ProcessingBench
extends Bench {
    public static final BuilderCodec<ProcessingBench> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ProcessingBench.class, ProcessingBench::new, Bench.BASE_CODEC).append(new KeyedCodec<T[]>("Input", new ArrayCodec<ProcessingSlot>(ProcessingSlot.CODEC, ProcessingSlot[]::new)), (bench, s) -> {
        bench.input = s;
    }, bench -> bench.input).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<T[]>("Fuel", new ArrayCodec<ProcessingSlot>(ProcessingSlot.CODEC, ProcessingSlot[]::new)), (bench, s) -> {
        bench.fuel = s;
    }, bench -> bench.fuel).add()).append(new KeyedCodec<Integer>("MaxFuel", Codec.INTEGER), (bench, s) -> {
        bench.maxFuel = s;
    }, bench -> bench.maxFuel).add()).append(new KeyedCodec<String>("FuelDropItemId", Codec.STRING), (bench, s) -> {
        bench.fuelDropItemId = s;
    }, bench -> bench.fuelDropItemId).add()).append(new KeyedCodec<Integer>("OutputSlotsCount", Codec.INTEGER), (bench, s) -> {
        bench.outputSlotsCount = s;
    }, bench -> bench.outputSlotsCount).addValidator(Validators.greaterThanOrEqual(1)).add()).append(new KeyedCodec<ExtraOutput>("ExtraOutput", ExtraOutput.CODEC), (bench, s) -> {
        bench.extraOutput = s;
    }, bench -> bench.extraOutput).add()).append(new KeyedCodec<Boolean>("AllowNoInputProcessing", Codec.BOOLEAN), (bench, s) -> {
        bench.allowNoInputProcessing = s;
    }, bench -> bench.allowNoInputProcessing).add()).append(new KeyedCodec<String>("IconItem", Codec.STRING), (bench, s) -> {
        bench.iconItem = s;
    }, bench -> bench.iconItem).add()).append(new KeyedCodec<String>("Icon", Codec.STRING), (bench, s) -> {
        bench.icon = s;
    }, bench -> bench.icon).add()).append(new KeyedCodec<String>("IconName", Codec.STRING), (bench, s) -> {
        bench.iconName = s;
    }, bench -> bench.iconName).add()).append(new KeyedCodec<String>("IconId", Codec.STRING), (bench, s) -> {
        bench.iconId = s;
    }, bench -> bench.iconId).add()).append(new KeyedCodec<String>("EndSoundEventId", Codec.STRING), (bench, s) -> {
        bench.endSoundEventId = s;
    }, bench -> bench.endSoundEventId).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).add()).afterDecode(p -> {
        if (p.icon != null) {
            String name;
            if (p.iconId == null) {
                name = p.icon.substring(0, p.icon.indexOf(46));
                p.iconId = name.toLowerCase(Locale.ROOT);
            }
            if (p.iconName == null) {
                name = p.icon.substring(0, p.icon.indexOf(46));
                p.iconName = name.toLowerCase(Locale.ROOT);
            }
        }
        if (p.endSoundEventId != null) {
            p.endSoundEventIndex = SoundEvent.getAssetMap().getIndex(p.endSoundEventId);
        }
    })).build();
    protected ProcessingSlot[] input;
    protected ProcessingSlot[] fuel;
    protected boolean allowNoInputProcessing;
    protected ExtraOutput extraOutput;
    protected int maxFuel = -1;
    protected String fuelDropItemId;
    protected int outputSlotsCount = 1;
    protected String iconItem;
    protected String icon;
    protected String iconName;
    protected String iconId;
    @Nullable
    protected String endSoundEventId = null;
    protected transient int endSoundEventIndex = 0;

    public String getIconItem() {
        return this.iconItem;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getIconName() {
        return this.iconName;
    }

    public String getIconId() {
        return this.iconId;
    }

    public ProcessingSlot[] getInput(int tierLevel) {
        if (this.tierLevels == null) {
            return this.input;
        }
        if (tierLevel > this.tierLevels.length) {
            return this.input;
        }
        Object[] result = new ProcessingSlot[this.input.length + this.tierLevels[tierLevel - 1].extraInputSlot];
        Arrays.fill(result, this.input[0]);
        return result;
    }

    public ProcessingSlot[] getFuel() {
        return this.fuel;
    }

    public int getMaxFuel() {
        return this.maxFuel;
    }

    public String getFuelDropItemId() {
        return this.fuelDropItemId;
    }

    public int getOutputSlotsCount(int tierLevel) {
        if (this.tierLevels == null) {
            return this.outputSlotsCount;
        }
        if (tierLevel > this.tierLevels.length) {
            return this.outputSlotsCount;
        }
        return this.outputSlotsCount + this.tierLevels[tierLevel - 1].extraOutputSlot;
    }

    public ExtraOutput getExtraOutput() {
        return this.extraOutput;
    }

    @Nullable
    public String getEndSoundEventId() {
        return this.endSoundEventId;
    }

    public int getEndSoundEventIndex() {
        return this.endSoundEventIndex;
    }

    public boolean shouldAllowNoInputProcessing() {
        return this.allowNoInputProcessing;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ProcessingBench that = (ProcessingBench)o;
        return this.allowNoInputProcessing == that.allowNoInputProcessing && this.maxFuel == that.maxFuel && this.outputSlotsCount == that.outputSlotsCount && this.endSoundEventIndex == that.endSoundEventIndex && Arrays.equals(this.input, that.input) && Arrays.equals(this.fuel, that.fuel) && Objects.equals(this.extraOutput, that.extraOutput) && Objects.equals(this.fuelDropItemId, that.fuelDropItemId) && Objects.equals(this.iconItem, that.iconItem) && Objects.equals(this.icon, that.icon) && Objects.equals(this.iconName, that.iconName) && Objects.equals(this.iconId, that.iconId) && Objects.equals(this.endSoundEventId, that.endSoundEventId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.input);
        result = 31 * result + Arrays.hashCode(this.fuel);
        result = 31 * result + Boolean.hashCode(this.allowNoInputProcessing);
        result = 31 * result + Objects.hashCode(this.extraOutput);
        result = 31 * result + this.maxFuel;
        result = 31 * result + Objects.hashCode(this.fuelDropItemId);
        result = 31 * result + this.outputSlotsCount;
        result = 31 * result + Objects.hashCode(this.iconItem);
        result = 31 * result + Objects.hashCode(this.icon);
        result = 31 * result + Objects.hashCode(this.iconName);
        result = 31 * result + Objects.hashCode(this.iconId);
        result = 31 * result + Objects.hashCode(this.endSoundEventId);
        result = 31 * result + this.endSoundEventIndex;
        return result;
    }

    public static class ProcessingSlot
    extends Bench.BenchSlot {
        public static final BuilderCodec<ProcessingSlot> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ProcessingSlot.class, ProcessingSlot::new, Bench.BenchSlot.CODEC).append(new KeyedCodec<String>("ResourceTypeId", Codec.STRING), (benchSlot, s) -> {
            benchSlot.resourceTypeId = s;
        }, benchSlot -> benchSlot.resourceTypeId).add()).append(new KeyedCodec<Boolean>("FilterValidIngredients", Codec.BOOLEAN), (benchSlot, b) -> {
            benchSlot.filterValidIngredients = b;
        }, benchSlot -> benchSlot.filterValidIngredients).add()).build();
        protected boolean filterValidIngredients;
        protected String resourceTypeId;

        public String getResourceTypeId() {
            return this.resourceTypeId;
        }

        public boolean shouldFilterValidIngredients() {
            return this.filterValidIngredients;
        }
    }

    public static class ExtraOutput {
        public static final BuilderCodec<ExtraOutput> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ExtraOutput.class, ExtraOutput::new).append(new KeyedCodec<T[]>("Outputs", new ArrayCodec<MaterialQuantity>(MaterialQuantity.CODEC, MaterialQuantity[]::new)), (o, i) -> {
            o.outputs = i;
        }, o -> o.outputs).addValidator(Validators.nonNull()).add()).append(new KeyedCodec<Integer>("PerFuelItemsConsumed", Codec.INTEGER), (o, i) -> {
            o.perFuelItemsConsumed = i;
        }, o -> o.perFuelItemsConsumed).addValidator(Validators.greaterThanOrEqual(1)).add()).append(new KeyedCodec<T[]>("IgnoredFuelSources", new ArrayCodec<MaterialQuantity>(MaterialQuantity.CODEC, MaterialQuantity[]::new)), (o, i) -> {
            o.ignoredFuelSources = i;
        }, o -> o.ignoredFuelSources).add()).build();
        private MaterialQuantity[] outputs;
        private int perFuelItemsConsumed = 1;
        private MaterialQuantity[] ignoredFuelSources;

        public MaterialQuantity[] getOutputs() {
            return this.outputs;
        }

        public int getPerFuelItemsConsumed() {
            return this.perFuelItemsConsumed;
        }

        public boolean isIgnoredFuelSource(Item id) {
            if (this.ignoredFuelSources == null) {
                return false;
            }
            for (MaterialQuantity mat : this.ignoredFuelSources) {
                if (mat.getItemId() == null || !mat.getItemId().equals(id.getBlockId())) continue;
                return true;
            }
            return false;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ExtraOutput that = (ExtraOutput)o;
            if (this.perFuelItemsConsumed != that.perFuelItemsConsumed) {
                return false;
            }
            return Arrays.equals(this.outputs, that.outputs);
        }

        public int hashCode() {
            int result = Arrays.hashCode(this.outputs);
            result = 31 * result + this.perFuelItemsConsumed;
            return result;
        }
    }
}

