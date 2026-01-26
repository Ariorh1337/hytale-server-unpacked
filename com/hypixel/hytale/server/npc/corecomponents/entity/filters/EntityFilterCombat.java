/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.corecomponents.entity.filters;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.blackboard.view.combat.CombatViewSystems;
import com.hypixel.hytale.server.npc.blackboard.view.combat.InterpretedCombatData;
import com.hypixel.hytale.server.npc.corecomponents.EntityFilterBase;
import com.hypixel.hytale.server.npc.corecomponents.entity.filters.builders.BuilderEntityFilterCombat;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.EntityPositionProvider;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class EntityFilterCombat
extends EntityFilterBase {
    public static final int COST = 100;
    protected final String sequence;
    protected final double minTimeElapsed;
    protected final double maxTimeElapsed;
    protected final Mode combatMode;
    protected final EntityPositionProvider positionProvider = new EntityPositionProvider();

    public EntityFilterCombat(@Nonnull BuilderEntityFilterCombat builder, @Nonnull BuilderSupport builderSupport) {
        this.sequence = builder.getSequence(builderSupport);
        double[] timeElapsedRange = builder.getTimeElapsedRange(builderSupport);
        this.minTimeElapsed = timeElapsedRange[0];
        this.maxTimeElapsed = timeElapsedRange[1];
        this.combatMode = builder.getCombatMode(builderSupport);
    }

    @Override
    public boolean matchesEntity(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull Role role, @Nonnull Store<EntityStore> store) {
        List<InterpretedCombatData> combatData = CombatViewSystems.getCombatData(targetRef, store);
        for (int i = 0; i < combatData.size(); ++i) {
            InterpretedCombatData data = combatData.get(i);
            boolean matches = switch (this.combatMode.ordinal()) {
                default -> throw new MatchException(null, null);
                case 6 -> true;
                case 7 -> false;
                case 1 -> {
                    if (!data.isCharging()) {
                        yield false;
                    }
                    float currentTime = data.getCurrentElapsedTime();
                    if ((double)currentTime >= this.minTimeElapsed && (double)currentTime <= this.maxTimeElapsed) {
                        yield true;
                    }
                    yield false;
                }
                case 2 -> {
                    if (data.isPerformingMeleeAttack() || data.isPerformingRangedAttack()) {
                        yield true;
                    }
                    yield false;
                }
                case 3 -> data.isPerformingMeleeAttack();
                case 4 -> data.isPerformingRangedAttack();
                case 5 -> data.isPerformingBlock();
                case 0 -> {
                    if (!data.getAttack().equals(this.sequence)) {
                        yield false;
                    }
                    float time = data.getCurrentElapsedTime();
                    if ((double)time >= this.minTimeElapsed && (double)time <= this.maxTimeElapsed) {
                        yield true;
                    }
                    yield false;
                }
            };
            if (!matches) continue;
            return true;
        }
        return this.combatMode == Mode.None;
    }

    @Override
    public int cost() {
        return 100;
    }

    public static enum Mode implements Supplier<String>
    {
        Sequence("Combat sequence"),
        Charging("Weapon charging"),
        Attacking("Attacking"),
        Melee("Melee"),
        Ranged("Ranged"),
        Blocking("Blocking"),
        Any("Any"),
        None("None");

        private final String description;

        private Mode(String description) {
            this.description = description;
        }

        @Override
        public String get() {
            return this.description;
        }
    }
}

