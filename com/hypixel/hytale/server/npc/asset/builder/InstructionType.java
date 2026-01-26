/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import java.util.EnumSet;
import java.util.function.Supplier;

public enum InstructionType implements Supplier<String>
{
    Default("the default behaviour instruction"),
    Interaction("the interaction instruction"),
    Death("the death instruction"),
    Component("a component"),
    StateTransitions("state transition actions");

    private final String description;
    public static final EnumSet<InstructionType> Any;
    public static final EnumSet<InstructionType> MotionAllowedInstructions;
    public static final EnumSet<InstructionType> StateChangeAllowedInstructions;

    private InstructionType(String description) {
        this.description = description;
    }

    @Override
    public String get() {
        return this.description;
    }

    static {
        Any = EnumSet.allOf(InstructionType.class);
        MotionAllowedInstructions = EnumSet.of(Default);
        StateChangeAllowedInstructions = EnumSet.of(Default, Interaction, Death, Component);
    }
}

