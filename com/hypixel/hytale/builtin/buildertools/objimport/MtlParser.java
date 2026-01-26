/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.objimport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class MtlParser {
    private MtlParser() {
    }

    @Nonnull
    public static Map<String, MtlMaterial> parse(@Nonnull Path path) throws IOException {
        HashMap<String, MtlMaterial> materials = new HashMap<String, MtlMaterial>();
        String currentMaterial = null;
        float[] currentKd = null;
        String currentMapKd = null;
        try (BufferedReader reader = Files.newBufferedReader(path);){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts;
                if ((line = line.trim()).isEmpty() || line.startsWith("#") || (parts = line.split("\\s+", 2)).length == 0) continue;
                switch (parts[0]) {
                    case "newmtl": {
                        if (currentMaterial != null) {
                            materials.put(currentMaterial, new MtlMaterial(currentMaterial, currentKd, currentMapKd));
                        }
                        currentMaterial = parts.length > 1 ? parts[1].trim() : "";
                        currentKd = null;
                        currentMapKd = null;
                        break;
                    }
                    case "Kd": {
                        if (parts.length <= 1) break;
                        currentKd = MtlParser.parseColor(parts[1]);
                        break;
                    }
                    case "map_Kd": {
                        if (parts.length <= 1) break;
                        currentMapKd = parts[1].trim();
                    }
                }
            }
            if (currentMaterial != null) {
                materials.put(currentMaterial, new MtlMaterial(currentMaterial, currentKd, currentMapKd));
            }
        }
        return materials;
    }

    @Nullable
    private static float[] parseColor(String colorStr) {
        String[] parts = colorStr.trim().split("\\s+");
        if (parts.length < 3) {
            return null;
        }
        try {
            float r = Float.parseFloat(parts[0]);
            float g = Float.parseFloat(parts[1]);
            float b = Float.parseFloat(parts[2]);
            return new float[]{r, g, b};
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public record MtlMaterial(@Nonnull String name, @Nullable float[] diffuseColor, @Nullable String diffuseTexturePath) {
        @Nullable
        public int[] getDiffuseColorRGB() {
            if (this.diffuseColor == null) {
                return null;
            }
            return new int[]{Math.max(0, Math.min(255, Math.round(this.diffuseColor[0] * 255.0f))), Math.max(0, Math.min(255, Math.round(this.diffuseColor[1] * 255.0f))), Math.max(0, Math.min(255, Math.round(this.diffuseColor[2] * 255.0f)))};
        }
    }
}

