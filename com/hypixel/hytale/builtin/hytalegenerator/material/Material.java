/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.material;

import com.hypixel.hytale.builtin.hytalegenerator.material.FluidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class Material {
    @Nonnull
    private final SolidMaterial solid;
    @Nonnull
    private final FluidMaterial fluid;
    private Hash hashCode;
    private Hash materialIdsHash;

    public Material(@Nonnull SolidMaterial solid, @Nonnull FluidMaterial fluid) {
        this.solid = solid;
        this.fluid = fluid;
        this.hashCode = new Hash(this);
        this.materialIdsHash = new Hash(this);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Material)) {
            return false;
        }
        Material material = (Material)o;
        return Objects.equals(this.solid, material.solid) && Objects.equals(this.fluid, material.fluid);
    }

    public int hashCode() {
        if (this.hashCode.isCalculated) {
            return this.hashCode.value;
        }
        this.hashCode.value = Material.hashCode(this.solid, this.fluid);
        this.hashCode.isCalculated = true;
        return this.hashCode.value;
    }

    public int hashMaterialIds() {
        if (this.materialIdsHash.isCalculated) {
            return this.materialIdsHash.value;
        }
        this.materialIdsHash.value = Material.hashMaterialIds(this.solid, this.fluid);
        this.materialIdsHash.isCalculated = true;
        return this.materialIdsHash.value;
    }

    public static int hashCode(@Nonnull SolidMaterial solid, @Nonnull FluidMaterial fluid) {
        int result = solid.hashCode();
        result = 31 * result + fluid.hashCode();
        return result;
    }

    public static int hashMaterialIds(@Nonnull SolidMaterial solid, @Nonnull FluidMaterial fluid) {
        return Objects.hash(solid.blockId, fluid.fluidId);
    }

    public SolidMaterial solid() {
        return this.solid;
    }

    public FluidMaterial fluid() {
        return this.fluid;
    }

    public String toString() {
        return "Material[solid=" + String.valueOf(this.solid) + ", fluid=" + String.valueOf(this.fluid) + "]";
    }

    private class Hash {
        int value = 0;
        boolean isCalculated = false;

        private Hash(Material material) {
        }
    }
}

