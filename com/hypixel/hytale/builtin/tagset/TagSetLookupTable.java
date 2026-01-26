/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.tagset;

import com.hypixel.hytale.builtin.tagset.TagSet;
import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
import com.hypixel.hytale.common.util.StringUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TagSetLookupTable<T extends TagSet> {
    @Nonnull
    private Int2ObjectMap<IntSet> tagMatcher = new Int2ObjectOpenHashMap<IntSet>();

    public TagSetLookupTable(@Nonnull Map<String, T> tagSetMap, @Nonnull Object2IntMap<String> tagSetIndexMap, @Nonnull Object2IntMap<String> tagIndexMap) {
        this.createTagMap(tagSetMap, tagSetIndexMap, tagIndexMap);
    }

    private void createTagMap(@Nonnull Map<String, T> tagSetMap, @Nonnull Object2IntMap<String> tagSetIndexMap, @Nonnull Object2IntMap<String> tagIndexMap) {
        IntArrayList path = new IntArrayList();
        tagSetMap.forEach((key, entry) -> {
            int id = tagSetIndexMap.getOrDefault(key, -1);
            if (id >= 0 && this.tagMatcher.containsKey(id)) {
                return;
            }
            try {
                this.createTagSet(entry, tagSetMap, tagSetIndexMap, tagIndexMap, path);
            }
            catch (IllegalStateException e) {
                throw new IllegalStateException(key + ": ", e);
            }
            path.clear();
        });
    }

    @Nonnull
    private IntSet createTagSet(@Nonnull T tagSet, @Nonnull Map<String, T> tagSetMap, @Nonnull Object2IntMap<String> tagSetIndexMap, @Nonnull Object2IntMap<String> tagIndexMap, @Nonnull IntArrayList path) {
        IntOpenHashSet set = new IntOpenHashSet();
        int index = tagSetIndexMap.getInt(tagSet.getId());
        if (path.contains(index)) {
            throw new IllegalStateException("Cyclic reference to set detected: " + (String)tagSet.getId());
        }
        path.add(index);
        this.tagMatcher.put(index, (IntSet)set);
        if (!tagIndexMap.isEmpty()) {
            String[] excludedTags;
            String[] includedTags;
            String[] excludedTagSets;
            String[] includedTagSets = tagSet.getIncludedTagSets();
            if (includedTagSets != null) {
                for (String tag : includedTagSets) {
                    this.consumeSet(tag, tagSetMap, tagSetIndexMap, tagIndexMap, path, set::addAll);
                }
            }
            if ((excludedTagSets = tagSet.getExcludedTagSets()) != null) {
                for (String tag : excludedTagSets) {
                    this.consumeSet(tag, tagSetMap, tagSetIndexMap, tagIndexMap, path, set::removeAll);
                }
            }
            if ((includedTags = tagSet.getIncludedTags()) != null) {
                for (String tag : includedTags) {
                    this.consumeTag(tag, tagSet, tagIndexMap, set::add);
                }
            }
            if ((excludedTags = tagSet.getExcludedTags()) != null) {
                for (String tag : excludedTags) {
                    this.consumeTag(tag, tagSet, tagIndexMap, set::remove);
                }
            }
        }
        return set;
    }

    private void consumeSet(String tag, @Nonnull Map<String, T> tagSetMap, @Nonnull Object2IntMap<String> tagSetIndexMap, @Nonnull Object2IntMap<String> tagIndexMap, @Nonnull IntArrayList path, @Nonnull Consumer<IntSet> predicate) {
        IntSet s = this.getOrCreateTagSet(tag, tagSetMap, tagSetIndexMap, tagIndexMap, path);
        if (s != null) {
            predicate.accept(s);
        }
    }

    private void consumeTag(@Nonnull String tag, @Nonnull T tagSet, @Nonnull Object2IntMap<String> tagIndexMap, @Nonnull IntConsumer predicate) {
        if (StringUtil.isGlobPattern(tag)) {
            ObjectIterator<Object2IntMap.Entry<String>> it = Object2IntMaps.fastIterator(tagIndexMap);
            while (it.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)it.next();
                if (!StringUtil.isGlobMatching(tag, (String)entry.getKey())) continue;
                predicate.accept(entry.getIntValue());
            }
            return;
        }
        int index = tagIndexMap.getOrDefault((Object)tag, -1);
        if (index >= 0) {
            predicate.accept(index);
            return;
        }
        TagSetPlugin.get().getLogger().at(Level.WARNING).log("Tag Set '%s' references '%s' which is not a pattern and does not otherwise exist", tagSet.getId(), (Object)tag);
    }

    @Nullable
    private IntSet getOrCreateTagSet(String identifier, @Nonnull Map<String, T> tagSetMap, @Nonnull Object2IntMap<String> tagSetIndexMap, @Nonnull Object2IntMap<String> tagIndexMap, @Nonnull IntArrayList path) {
        int tagSetIndex = tagSetIndexMap.getOrDefault((Object)identifier, -1);
        IntSet intSet = null;
        if (tagSetIndex >= 0 && this.tagMatcher.containsKey(tagSetIndex)) {
            if (path.contains(tagSetIndex)) {
                throw new IllegalStateException("Cyclic reference to set detected: " + identifier);
            }
            path.add(tagSetIndex);
            intSet = (IntSet)this.tagMatcher.get(tagSetIndex);
        } else {
            TagSet set = (TagSet)tagSetMap.get(identifier);
            if (set != null) {
                intSet = this.createTagSet(set, tagSetMap, tagSetIndexMap, tagIndexMap, path);
            } else {
                TagSetPlugin.get().getLogger().at(Level.WARNING).log("Creating tag sets: Tag Set '%s' does not exist, but is being referenced as a tag", identifier);
            }
        }
        path.removeInt(path.size() - 1);
        return intSet;
    }

    @Nonnull
    public Int2ObjectMap<IntSet> getFlattenedSet() {
        return this.tagMatcher;
    }
}

