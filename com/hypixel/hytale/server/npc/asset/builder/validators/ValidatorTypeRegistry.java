/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.npc.asset.builder.validators.AnyBooleanValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AnyPresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ArrayNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ArraysOneSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AtMostOneBooleanValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.AttributeRelationValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.BooleanImplicationValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ComponentOnlyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleOrValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.EnumArrayNoDuplicatesValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ExistsIfParameterSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.InstructionContextValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntOrValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntRangeValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.OneOrNonePresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.OnePresentValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresFeatureIfEnumValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresFeatureIfValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.RequiresOneOfFeaturesValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StateStringValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayNoEmptyStringsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringArrayNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsAtMostOneValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsNotEmptyValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringsOneSetValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.SubTypeTypeAdapterFactory;
import com.hypixel.hytale.server.npc.asset.builder.validators.TemporalSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ValidateAssetIfEnumIsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.ValidateIfEnumIsValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.Validator;
import javax.annotation.Nonnull;

public class ValidatorTypeRegistry {
    @Nonnull
    public static GsonBuilder registerTypes(@Nonnull GsonBuilder gsonBuilder) {
        SubTypeTypeAdapterFactory factory = SubTypeTypeAdapterFactory.of(Validator.class, "Type");
        factory.registerSubType(StringNotEmptyValidator.class, "StringNotEmpty");
        factory.registerSubType(StringNullOrNotEmptyValidator.class, "StringNullOrNotEmpty");
        factory.registerSubType(StringsAtMostOneValidator.class, "StringsAtMostOne");
        factory.registerSubType(StringsOneSetValidator.class, "StringsOneSet");
        factory.registerSubType(StringsNotEmptyValidator.class, "NotAllStringsEmpty");
        factory.registerSubType(IntSingleValidator.class, "Int");
        factory.registerSubType(IntOrValidator.class, "IntOr");
        factory.registerSubType(IntRangeValidator.class, "IntRange");
        factory.registerSubType(DoubleSingleValidator.class, "Double");
        factory.registerSubType(DoubleOrValidator.class, "DoubleOr");
        factory.registerSubType(DoubleRangeValidator.class, "DoubleRange");
        factory.registerSubType(AttributeRelationValidator.class, "NumericRelation");
        factory.registerSubType(ArrayNotEmptyValidator.class, "ArrayNotEmpty");
        factory.registerSubType(AnyPresentValidator.class, "AnyPresent");
        factory.registerSubType(OnePresentValidator.class, "OnePresent");
        factory.registerSubType(OneOrNonePresentValidator.class, "OneOrNonePresent");
        factory.registerSubType(AnyBooleanValidator.class, "AnyTrue");
        factory.registerSubType(StringArrayNotEmptyValidator.class, "StringListNotEmpty");
        factory.registerSubType(StringArrayNoEmptyStringsValidator.class, "StringListNoEmptyStrings");
        factory.registerSubType(DoubleSequenceValidator.class, "DoubleSequenceValidator");
        factory.registerSubType(IntSequenceValidator.class, "IntSequenceValidator");
        factory.registerSubType(ExistsIfParameterSetValidator.class, "ExistsIfParameterSet");
        factory.registerSubType(TemporalSequenceValidator.class, "TemporalSequenceValidator");
        factory.registerSubType(RequiresFeatureIfValidator.class, "RequiresFeatureIf");
        factory.registerSubType(RequiresOneOfFeaturesValidator.class, "RequiresOneOfFeatures");
        factory.registerSubType(StateStringValidator.class, "StateString");
        factory.registerSubType(ValidateIfEnumIsValidator.class, "ValidateIfEnumIs");
        factory.registerSubType(ValidateAssetIfEnumIsValidator.class, "ValidateAssetIfEnumIs");
        factory.registerSubType(ComponentOnlyValidator.class, "ComponentOnly");
        factory.registerSubType(RequiresFeatureIfEnumValidator.class, "RequiresFeatureIfEnum");
        factory.registerSubType(EnumArrayNoDuplicatesValidator.class, "EnumArrayNoDuplicates");
        factory.registerSubType(ArraysOneSetValidator.class, "ArraysOneSet");
        factory.registerSubType(BooleanImplicationValidator.class, "BooleanImplication");
        factory.registerSubType(InstructionContextValidator.class, "InstructionContext");
        factory.registerSubType(AtMostOneBooleanValidator.class, "AtMostOneBoolean");
        gsonBuilder.registerTypeAdapterFactory(factory);
        return gsonBuilder;
    }
}

