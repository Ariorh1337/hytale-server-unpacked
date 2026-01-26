/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config.bench;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingBench
extends Bench {
    public static final BuilderCodec<CraftingBench> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(CraftingBench.class, CraftingBench::new, Bench.BASE_CODEC).append(new KeyedCodec<T[]>("Categories", new ArrayCodec<BenchCategory>(BenchCategory.CODEC, BenchCategory[]::new)), (bench, s) -> {
        bench.categories = s;
    }, bench -> bench.categories).addValidator(Validators.nonNull()).add()).build();
    protected BenchCategory[] categories;

    public BenchCategory[] getCategories() {
        return this.categories;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CraftingBench that = (CraftingBench)o;
        return Arrays.equals(this.categories, that.categories);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.categories);
        return result;
    }

    public static class BenchCategory {
        public static final BuilderCodec<BenchCategory> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BenchCategory.class, BenchCategory::new).addField(new KeyedCodec<String>("Id", Codec.STRING), (benchCategory, s) -> {
            benchCategory.id = s;
        }, benchCategory -> benchCategory.id)).append(new KeyedCodec<String>("Name", Codec.STRING), (benchCategory, s) -> {
            benchCategory.name = s;
        }, benchCategory -> benchCategory.name).metadata(new UIEditor(new UIEditor.LocalizationKeyField("server.benchCategories.{id}"))).add()).append(new KeyedCodec<String>("Icon", Codec.STRING), (benchCategory, s) -> {
            benchCategory.icon = s;
        }, benchCategory -> benchCategory.icon).addValidator(CommonAssetValidator.ICON_CRAFTING).add()).addField(new KeyedCodec<T[]>("ItemCategories", new ArrayCodec<BenchItemCategory>(BenchItemCategory.CODEC, BenchItemCategory[]::new)), (benchCategory, s) -> {
            benchCategory.itemCategories = s;
        }, benchCategory -> benchCategory.itemCategories)).build();
        protected String id;
        protected String name;
        protected String icon;
        protected BenchItemCategory[] itemCategories;

        public BenchCategory(String id, String name, String icon, BenchItemCategory[] itemCategories) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.itemCategories = itemCategories;
        }

        protected BenchCategory() {
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getIcon() {
            return this.icon;
        }

        public BenchItemCategory[] getItemCategories() {
            return this.itemCategories;
        }

        @Nonnull
        public String toString() {
            return "BenchCategory{id='" + this.id + "', name='" + this.name + "', icon='" + this.icon + "', itemCategories='" + Arrays.toString(this.itemCategories) + "'}";
        }
    }

    public static class BenchItemCategory {
        public static final BuilderCodec<BenchItemCategory> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BenchItemCategory.class, BenchItemCategory::new).addField(new KeyedCodec<String>("Id", Codec.STRING), (benchItemCategory, s) -> {
            benchItemCategory.id = s;
        }, benchItemCategory -> benchItemCategory.id)).addField(new KeyedCodec<String>("Name", Codec.STRING), (benchItemCategory, s) -> {
            benchItemCategory.name = s;
        }, benchItemCategory -> benchItemCategory.name)).append(new KeyedCodec<String>("Icon", Codec.STRING), (benchItemCategory, s) -> {
            benchItemCategory.icon = s;
        }, benchItemCategory -> benchItemCategory.icon).addValidator(CommonAssetValidator.ICON_CRAFTING).add()).append(new KeyedCodec<String>("Diagram", Codec.STRING), (benchItemCategory, s) -> {
            benchItemCategory.diagram = s;
        }, benchItemCategory -> benchItemCategory.diagram).addValidator(CommonAssetValidator.UI_CRAFTING_DIAGRAM).add()).addField(new KeyedCodec<Integer>("Slots", Codec.INTEGER), (benchItemCategory, s) -> {
            benchItemCategory.slots = s;
        }, benchItemCategory -> benchItemCategory.slots)).addField(new KeyedCodec<Boolean>("SpecialSlot", Codec.BOOLEAN), (benchItemCategory, s) -> {
            benchItemCategory.specialSlot = s;
        }, benchItemCategory -> benchItemCategory.specialSlot)).build();
        protected String id;
        protected String name;
        protected String icon;
        protected String diagram;
        protected int slots = 1;
        protected boolean specialSlot = true;

        public BenchItemCategory(String id, String name, String icon, String diagram, int slots, boolean specialSlot) {
            this.id = id;
            this.name = name;
            this.icon = icon;
            this.diagram = diagram;
            this.slots = slots;
            this.specialSlot = specialSlot;
        }

        protected BenchItemCategory() {
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getIcon() {
            return this.icon;
        }

        public String getDiagram() {
            return this.diagram;
        }

        public int getSlots() {
            return this.slots;
        }

        public boolean isSpecialSlot() {
            return this.specialSlot;
        }

        @Nonnull
        public String toString() {
            return "BenchItemCategory{id='" + this.id + "', name='" + this.name + "', icon='" + this.icon + "', diagram='" + this.diagram + "', slots='" + this.slots + "', specialSlot='" + this.specialSlot + "'}";
        }
    }
}

