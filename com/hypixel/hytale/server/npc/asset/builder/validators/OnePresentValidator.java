/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.hypixel.hytale.server.npc.asset.builder.BuilderObjectHelper;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import java.util.Objects;
import java.util.function.IntPredicate;
import javax.annotation.Nonnull;

public class OnePresentValidator
extends Validator {
    @Nonnull
    private final String[] attributes;

    private OnePresentValidator(String ... attributes) {
        this.attributes = Objects.requireNonNull(attributes, "Attributes in OnePresentValidator must not be null");
    }

    public static int countPresent(int size, @Nonnull IntPredicate presentPredicate) {
        int count = 0;
        for (int i = 0; i < size; ++i) {
            if (!presentPredicate.test(i)) continue;
            ++count;
        }
        return count;
    }

    public static boolean test(@Nonnull BuilderObjectHelper<?>[] objects) {
        return OnePresentValidator.countPresent(objects.length, (int i) -> objects[i].isPresent()) == 1;
    }

    public static boolean test(@Nonnull boolean[] readStatus) {
        return OnePresentValidator.countPresent(readStatus.length, (int i) -> readStatus[i]) == 1;
    }

    public static int countPresent(@Nonnull BuilderObjectHelper<?> objectHelper) {
        return objectHelper.isPresent() ? 1 : 0;
    }

    public static int countPresent(@Nonnull BuilderObjectHelper<?> objectHelper1, @Nonnull BuilderObjectHelper<?> objectHelper2) {
        return OnePresentValidator.countPresent(objectHelper1) + OnePresentValidator.countPresent(objectHelper2);
    }

    public static int countPresent(@Nonnull BuilderObjectHelper<?> objectHelper1, @Nonnull BuilderObjectHelper<?> objectHelper2, @Nonnull BuilderObjectHelper<?> objectHelper3) {
        return OnePresentValidator.countPresent(objectHelper1) + OnePresentValidator.countPresent(objectHelper2, objectHelper3);
    }

    public static boolean test(@Nonnull BuilderObjectHelper<?> objectHelper1, @Nonnull BuilderObjectHelper<?> objectHelper2) {
        return OnePresentValidator.countPresent(objectHelper1, objectHelper2) == 1;
    }

    public static boolean test(@Nonnull BuilderObjectHelper<?> objectHelper1, @Nonnull BuilderObjectHelper<?> objectHelper2, @Nonnull BuilderObjectHelper<?> objectHelper3) {
        return OnePresentValidator.countPresent(objectHelper1, objectHelper2, objectHelper3) == 1;
    }

    @Nonnull
    public static String errorMessage(@Nonnull String[] attributes, BuilderObjectHelper<?>[] objects) {
        return OnePresentValidator.errorMessage(attributes, i -> objects[i].isPresent());
    }

    @Nonnull
    public static String errorMessage(@Nonnull String[] attributes, boolean[] readStatus) {
        return OnePresentValidator.errorMessage(attributes, i -> readStatus[i]);
    }

    @Nonnull
    public static String errorMessage(@Nonnull String[] attributes, @Nonnull IntPredicate presentPredicate) {
        StringBuilder result = new StringBuilder("Exactly one of ");
        String sep = ", ";
        for (int i = 0; i < attributes.length; ++i) {
            if (i == attributes.length - 1) {
                sep = "";
            }
            result.append(String.format("'%s'%s%s", attributes[i], presentPredicate.test(i) ? "(Present)" : "", sep));
        }
        return String.valueOf(result) + " must be present";
    }

    @Nonnull
    public static OnePresentValidator withAttributes(String ... attributes) {
        return new OnePresentValidator(attributes);
    }
}

