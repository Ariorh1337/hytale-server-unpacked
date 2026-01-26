/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringListHelpers {
    @Nonnull
    private static Pattern listSplitter = Pattern.compile("[,; \t]");
    @Nonnull
    private static Pattern listListSplitter = Pattern.compile("\\|");

    private StringListHelpers() {
    }

    @Nonnull
    public static String stringListToString(@Nullable Collection<String> list) {
        if (list == null) {
            return "";
        }
        return list.stream().map(String::trim).collect(Collectors.joining(", "));
    }

    @Nonnull
    public static List<String> splitToStringList(String string, @Nullable Function<String, String> mapper) {
        if (mapper == null) {
            mapper = Function.identity();
        }
        return listSplitter.splitAsStream(string).filter(s -> !s.isEmpty()).map(mapper).collect(Collectors.toList());
    }

    public static void splitToStringList(String string, @Nullable Function<String, String> mapper, @Nonnull Collection<String> result) {
        if (mapper == null) {
            mapper = Function.identity();
        }
        listSplitter.splitAsStream(string).filter(s -> !s.isEmpty()).map(mapper).forEachOrdered(result::add);
    }

    @Nonnull
    public static String stringListListToString(@Nonnull Collection<Collection<String>> list) {
        return list.stream().map(StringListHelpers::stringListToString).collect(Collectors.joining("| "));
    }

    @Nonnull
    public static List<List<String>> splitToStringListList(@Nullable String string, Function<String, String> mapper) {
        if (string == null || string.isEmpty()) {
            return Collections.emptyList();
        }
        return listListSplitter.splitAsStream(string).filter(s -> !s.isEmpty()).map(s -> StringListHelpers.splitToStringList(s, mapper)).filter(l -> l != null && !l.isEmpty()).collect(Collectors.toList());
    }

    public static void splitToStringListList(String string, Function<String, String> mapper, @Nonnull Collection<Collection<String>> result, @Nonnull Supplier<Collection<String>> supplier) {
        listListSplitter.splitAsStream(string).filter(s -> !s.isEmpty()).map(s -> {
            Collection r = (Collection)supplier.get();
            StringListHelpers.splitToStringList(s, mapper, r);
            return r;
        }).filter(l -> l != null && !l.isEmpty()).forEachOrdered(result::add);
    }

    @Nonnull
    public static Set<String> stringListToStringSet(@Nonnull List<String> list) {
        return new HashSet<String>(list);
    }

    @Nonnull
    public static Set<String> splitToStringSet(@Nullable String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> list = StringListHelpers.splitToStringList(input, null);
        return new HashSet<String>(list);
    }

    @Nonnull
    public static <T> Set<T> splitToStringSet(@Nullable String input, Function<String, T> transform) {
        if (input == null || input.isEmpty()) {
            return Collections.emptySet();
        }
        return StringListHelpers.splitToStringList(input, null).stream().map(transform).collect(Collectors.toSet());
    }

    @Nonnull
    public static List<Set<String>> stringListListToStringSetList(@Nonnull List<List<String>> group) {
        return group.stream().map(HashSet::new).collect(Collectors.toList());
    }
}

