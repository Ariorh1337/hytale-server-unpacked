/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.function.consumer.TriIntConsumer;
import com.hypixel.hytale.function.predicate.TriIntPredicate;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import javax.annotation.Nonnull;

public class FillerBlockUtil {
    public static final float THRESHOLD = 0.0f;
    public static final int NO_FILLER = 0;
    private static final int BITS_PER_AXIS = 5;
    private static final int MASK = 31;
    private static final int INVERT = -32;

    public static void forEachFillerBlock(@Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntConsumer consumer) {
        FillerBlockUtil.forEachFillerBlock(0.0f, blockBoundingBoxes, consumer);
    }

    public static void forEachFillerBlock(float threshold, @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntConsumer consumer) {
        if (threshold < 0.0f || threshold >= 1.0f) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1");
        }
        Box boundingBox = blockBoundingBoxes.getBoundingBox();
        int minX = (int)boundingBox.min.x;
        int minY = (int)boundingBox.min.y;
        int minZ = (int)boundingBox.min.z;
        if ((double)minX - boundingBox.min.x > (double)threshold) {
            --minX;
        }
        if ((double)minY - boundingBox.min.y > (double)threshold) {
            --minY;
        }
        if ((double)minZ - boundingBox.min.z > (double)threshold) {
            --minZ;
        }
        int maxX = (int)boundingBox.max.x;
        int maxY = (int)boundingBox.max.y;
        int maxZ = (int)boundingBox.max.z;
        if (boundingBox.max.x - (double)maxX > (double)threshold) {
            ++maxX;
        }
        if (boundingBox.max.y - (double)maxY > (double)threshold) {
            ++maxY;
        }
        if (boundingBox.max.z - (double)maxZ > (double)threshold) {
            ++maxZ;
        }
        int blockWidth = Math.max(maxX - minX, 1);
        int blockHeight = Math.max(maxY - minY, 1);
        int blockDepth = Math.max(maxZ - minZ, 1);
        for (int x = 0; x < blockWidth; ++x) {
            for (int y = 0; y < blockHeight; ++y) {
                for (int z = 0; z < blockDepth; ++z) {
                    consumer.accept(minX + x, minY + y, minZ + z);
                }
            }
        }
    }

    public static boolean testFillerBlocks(@Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntPredicate predicate) {
        return FillerBlockUtil.testFillerBlocks(0.0f, blockBoundingBoxes, predicate);
    }

    public static boolean testFillerBlocks(float threshold, @Nonnull BlockBoundingBoxes.RotatedVariantBoxes blockBoundingBoxes, @Nonnull TriIntPredicate predicate) {
        if (threshold < 0.0f || threshold >= 1.0f) {
            throw new IllegalArgumentException("Threshold must be between 0 and 1");
        }
        Box boundingBox = blockBoundingBoxes.getBoundingBox();
        int minX = (int)boundingBox.min.x;
        int minY = (int)boundingBox.min.y;
        int minZ = (int)boundingBox.min.z;
        if ((double)minX - boundingBox.min.x > (double)threshold) {
            --minX;
        }
        if ((double)minY - boundingBox.min.y > (double)threshold) {
            --minY;
        }
        if ((double)minZ - boundingBox.min.z > (double)threshold) {
            --minZ;
        }
        int maxX = (int)boundingBox.max.x;
        int maxY = (int)boundingBox.max.y;
        int maxZ = (int)boundingBox.max.z;
        if (boundingBox.max.x - (double)maxX > (double)threshold) {
            ++maxX;
        }
        if (boundingBox.max.y - (double)maxY > (double)threshold) {
            ++maxY;
        }
        if (boundingBox.max.z - (double)maxZ > (double)threshold) {
            ++maxZ;
        }
        int blockWidth = Math.max(maxX - minX, 1);
        int blockHeight = Math.max(maxY - minY, 1);
        int blockDepth = Math.max(maxZ - minZ, 1);
        for (int x = 0; x < blockWidth; ++x) {
            for (int y = 0; y < blockHeight; ++y) {
                for (int z = 0; z < blockDepth; ++z) {
                    if (predicate.test(minX + x, minY + y, minZ + z)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    public static <A, B> ValidationResult validateBlock(int x, int y, int z, int blockId, int rotation, int filler, A a, B b, @Nonnull FillerFetcher<A, B> fetcher) {
        if (blockId == 0) {
            return ValidationResult.OK;
        }
        BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
        BlockType blockType = blockTypeAssetMap.getAsset(blockId);
        if (blockType == null) {
            return ValidationResult.OK;
        }
        String id = blockType.getId();
        IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
        if (filler != 0) {
            int fillerZ;
            int fillerY;
            int fillerX = FillerBlockUtil.unpackX(filler);
            int baseBlockId = fetcher.getBlock(a, b, x - fillerX, y - (fillerY = FillerBlockUtil.unpackY(filler)), z - (fillerZ = FillerBlockUtil.unpackZ(filler)));
            BlockType baseBlock = blockTypeAssetMap.getAsset(baseBlockId);
            if (baseBlock == null) {
                return ValidationResult.INVALID_BLOCK;
            }
            String baseId = baseBlock.getId();
            BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(baseBlock.getHitboxTypeIndex());
            if (hitbox == null) {
                return ValidationResult.OK;
            }
            int baseFiller = fetcher.getFiller(a, b, x - fillerX, y - fillerY, z - fillerZ);
            int baseRotation = fetcher.getRotationIndex(a, b, x - fillerX, y - fillerY, z - fillerZ);
            if (baseFiller != 0 || baseRotation != rotation || !id.equals(baseId) || !hitbox.get(baseRotation).getBoundingBox().containsBlock(fillerX, fillerY, fillerZ)) {
                return ValidationResult.INVALID_BLOCK;
            }
            return ValidationResult.OK;
        }
        BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex());
        if (hitbox == null || !hitbox.protrudesUnitBox()) {
            return ValidationResult.OK;
        }
        boolean result = FillerBlockUtil.testFillerBlocks(hitbox.get(rotation), (x1, y1, z1) -> {
            if (x1 == 0 && y1 == 0 && z1 == 0) {
                return true;
            }
            int worldX = x + x1;
            int worldY = y + y1;
            int worldZ = z + z1;
            int fillerBlockId = fetcher.getBlock(a, b, worldX, worldY, worldZ);
            BlockType fillerBlock = (BlockType)blockTypeAssetMap.getAsset(fillerBlockId);
            int expectedFiller = FillerBlockUtil.pack(x1, y1, z1);
            if (fetcher.getFiller(a, b, worldX, worldY, worldZ) != expectedFiller) {
                return false;
            }
            if (fetcher.getRotationIndex(a, b, worldX, worldY, worldZ) != rotation) {
                return false;
            }
            if (fillerBlock == null) {
                return false;
            }
            String blockTypeKey = fillerBlock.getId();
            return blockTypeKey.equals(id);
        });
        return result ? ValidationResult.OK : ValidationResult.INVALID_FILLER;
    }

    public static int pack(int x, int y, int z) {
        return x & 0x1F | (z & 0x1F) << 5 | (y & 0x1F) << 10;
    }

    public static int unpackX(int val) {
        int result = val & 0x1F;
        if ((result & 0x10) != 0) {
            result |= 0xFFFFFFE0;
        }
        return result;
    }

    public static int unpackY(int val) {
        int result = val >> 10 & 0x1F;
        if ((result & 0x10) != 0) {
            result |= 0xFFFFFFE0;
        }
        return result;
    }

    public static int unpackZ(int val) {
        int result = val >> 5 & 0x1F;
        if ((result & 0x10) != 0) {
            result |= 0xFFFFFFE0;
        }
        return result;
    }

    public static enum ValidationResult {
        OK,
        INVALID_BLOCK,
        INVALID_FILLER;

    }

    public static interface FillerFetcher<A, B> {
        public int getBlock(A var1, B var2, int var3, int var4, int var5);

        public int getFiller(A var1, B var2, int var3, int var4, int var5);

        public int getRotationIndex(A var1, B var2, int var3, int var4, int var5);
    }
}

