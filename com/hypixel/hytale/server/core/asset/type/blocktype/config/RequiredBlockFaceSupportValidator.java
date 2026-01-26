/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.codec.validation.LegacyValidator;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RequiredBlockFaceSupport;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class RequiredBlockFaceSupportValidator
implements LegacyValidator<Map<BlockFace, RequiredBlockFaceSupport[]>> {
    static final RequiredBlockFaceSupportValidator INSTANCE = new RequiredBlockFaceSupportValidator();

    RequiredBlockFaceSupportValidator() {
    }

    @Override
    public void accept(@Nullable Map<BlockFace, RequiredBlockFaceSupport[]> support, @Nonnull ValidationResults results) {
        if (support == null) {
            return;
        }
        for (Map.Entry<BlockFace, RequiredBlockFaceSupport[]> entry : support.entrySet()) {
            BlockFace blockFace = entry.getKey();
            RequiredBlockFaceSupport[] requiredBlockFaceSupports = entry.getValue();
            for (int i = 0; i < requiredBlockFaceSupports.length; ++i) {
                boolean noRequirements;
                RequiredBlockFaceSupport blockFaceSupport = requiredBlockFaceSupports[i];
                if (blockFaceSupport == null) {
                    results.fail("Value for 'Support." + String.valueOf((Object)blockFace) + "[" + i + "]' can't be null!");
                    continue;
                }
                boolean bl = noRequirements = blockFaceSupport.getFaceType() == null && blockFaceSupport.getBlockSetId() == null && blockFaceSupport.getBlockTypeId() == null && blockFaceSupport.getFluidId() == null && blockFaceSupport.getMatchSelf() == RequiredBlockFaceSupport.Match.IGNORED && blockFaceSupport.getTagId() == null;
                if (blockFaceSupport.getSupport() != RequiredBlockFaceSupport.Match.IGNORED && noRequirements) {
                    results.warn("Value for 'Support." + String.valueOf((Object)blockFace) + "[" + i + "]' doesn't define any requirements and should be removed!");
                }
                if (blockFaceSupport.getSupport() != RequiredBlockFaceSupport.Match.IGNORED || blockFaceSupport.allowsSupportPropagation()) continue;
                results.warn("Value for 'Support." + String.valueOf((Object)blockFace) + "[" + i + "]' doesn't allow support or support propagation so should be removed!");
            }
        }
    }
}

