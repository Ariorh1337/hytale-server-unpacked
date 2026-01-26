/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.decisionmaker.stateevaluator;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.npc.decisionmaker.core.Option;
import javax.annotation.Nonnull;

public class StateOption
extends Option {
    public static final BuilderCodec<StateOption> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StateOption.class, StateOption::new, Option.ABSTRACT_CODEC).append(new KeyedCodec<String>("State", Codec.STRING), (option, s) -> {
        option.state = s;
    }, option -> option.state).documentation("The main state name.").addValidator(Validators.nonNull()).addValidator(Validators.nonEmptyString()).add()).append(new KeyedCodec<String>("SubState", Codec.STRING), (option, s) -> {
        option.state = s;
    }, option -> option.state).documentation("The (optional) substate name.").add()).build();
    protected String state;
    protected String subState;
    protected int stateIndex;
    protected int subStateIndex;

    protected StateOption() {
    }

    public String getState() {
        return this.state;
    }

    public String getSubState() {
        return this.subState;
    }

    public int getStateIndex() {
        return this.stateIndex;
    }

    public int getSubStateIndex() {
        return this.subStateIndex;
    }

    public void setStateIndex(int stateIndex, int subStateIndex) {
        this.stateIndex = stateIndex;
        this.subStateIndex = subStateIndex;
    }

    @Override
    @Nonnull
    public String toString() {
        return "StateOption{state=" + this.state + ", stateIndex=" + this.stateIndex + "} " + super.toString();
    }
}

