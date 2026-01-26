/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.tagset;

import com.hypixel.hytale.assetstore.JsonAsset;

public interface TagSet
extends JsonAsset<String> {
    public String[] getIncludedTagSets();

    public String[] getExcludedTagSets();

    public String[] getIncludedTags();

    public String[] getExcludedTags();
}

