/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.instructions;

import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.random.RandomExtra;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.instructions.Instruction;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.instructions.builders.BuilderInstructionRandomized;
import com.hypixel.hytale.server.npc.role.Role;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InstructionRandomized
extends Instruction {
    @Nonnull
    protected final IWeightedMap<InstructionHolder> weightedInstructionMap;
    protected final boolean resetOnStateChange;
    protected final double minExecuteTime;
    protected final double maxExecuteTime;
    protected double timeout;
    @Nullable
    protected InstructionHolder current;

    public InstructionRandomized(@Nonnull BuilderInstructionRandomized builder, Sensor sensor, @Nonnull Instruction[] instructionList, @Nonnull BuilderSupport support) {
        super(builder, sensor, instructionList, support);
        WeightedMap.Builder<InstructionHolder> mapBuilder = WeightedMap.builder(InstructionHolder.EMPTY_ARRAY);
        for (Instruction instruction : instructionList) {
            mapBuilder.put(new InstructionHolder(instruction), instruction.getWeight());
        }
        this.weightedInstructionMap = mapBuilder.build();
        this.resetOnStateChange = builder.getResetOnStateChange(support);
        double[] executeTimeRange = builder.getExecuteFor(support);
        this.minExecuteTime = executeTimeRange[0];
        this.maxExecuteTime = executeTimeRange[1];
    }

    @Override
    public void execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, double dt, @Nonnull Store<EntityStore> store) {
        Instruction instruction;
        if (this.instructionList.length == 0) {
            return;
        }
        this.timeout -= dt;
        if (this.timeout <= 0.0 || this.current == null) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            this.current = this.weightedInstructionMap.get(random.nextDouble());
            this.timeout = RandomExtra.randomRange(this.minExecuteTime, this.maxExecuteTime);
        }
        if ((instruction = this.current.instruction).matches(ref, role, dt, store)) {
            instruction.onMatched(role);
            instruction.execute(ref, role, dt, store);
            instruction.onCompleted(role);
        }
    }

    @Override
    public void clearOnce() {
        super.clearOnce();
        if (this.resetOnStateChange) {
            this.current = null;
        }
    }

    @Override
    public void reset() {
        super.clearOnce();
        this.current = null;
    }

    protected static class InstructionHolder {
        protected static final InstructionHolder[] EMPTY_ARRAY = new InstructionHolder[0];
        private final Instruction instruction;

        protected InstructionHolder(Instruction instruction) {
            this.instruction = instruction;
        }
    }
}

