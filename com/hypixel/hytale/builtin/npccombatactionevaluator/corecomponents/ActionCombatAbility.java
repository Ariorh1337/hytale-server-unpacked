/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents;

import com.hypixel.hytale.builtin.npccombatactionevaluator.corecomponents.builders.BuilderActionCombatAbility;
import com.hypixel.hytale.builtin.npccombatactionevaluator.evaluator.CombatActionEvaluator;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionChain;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.IInteractionSimulationHandler;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.SingleCollector;
import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.corecomponents.combat.ActionAttack;
import com.hypixel.hytale.server.npc.interactions.NPCInteractionSimulationHandler;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.DoubleParameterProvider;
import com.hypixel.hytale.server.npc.sensorinfo.parameterproviders.ParameterProvider;
import com.hypixel.hytale.server.npc.util.AimingData;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ActionCombatAbility
extends ActionBase {
    protected static final ComponentType<EntityStore, CombatActionEvaluator> COMPONENT_TYPE = CombatActionEvaluator.getComponentType();
    protected static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
    protected static final float POSITIONING_ANGLE_THRESHOLD = 0.08726646f;
    protected final int id;
    protected final int positioningAngleProviderSlot;
    protected final double meleeConeAngle = 0.2617993950843811;
    @Nullable
    protected String attack;
    protected DoubleParameterProvider cachedPositioningAngleProvider;
    protected boolean initialised;

    public ActionCombatAbility(@Nonnull BuilderActionCombatAbility builder, @Nonnull BuilderSupport builderSupport) {
        super(builder);
        this.id = builderSupport.getNextAttackIndex();
        this.positioningAngleProviderSlot = builderSupport.getParameterSlot("PositioningAngle");
    }

    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nullable InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        if (!this.initialised) {
            ParameterProvider parameterProvider;
            if (sensorInfo != null && (parameterProvider = sensorInfo.getParameterProvider(this.positioningAngleProviderSlot)) instanceof DoubleParameterProvider) {
                this.cachedPositioningAngleProvider = (DoubleParameterProvider)parameterProvider;
            }
            this.initialised = true;
        }
        if (!super.canExecute(ref, role, sensorInfo, dt, store)) {
            return false;
        }
        CombatActionEvaluator combatActionEvaluator = ref.getStore().getComponent(ref, COMPONENT_TYPE);
        if (combatActionEvaluator == null) {
            return false;
        }
        return combatActionEvaluator.getCurrentAttack() != null;
    }

    @Override
    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nullable InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        boolean damageFriendlies;
        Ref<EntityStore> target;
        Vector3f rotation;
        CombatActionEvaluator combatActionEvaluatorComponent = store.getComponent(ref, COMPONENT_TYPE);
        assert (combatActionEvaluatorComponent != null);
        InteractionManager interactionManagerComponent = store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
        assert (interactionManagerComponent != null);
        AimingData aimingDataInfo = sensorInfo != null ? sensorInfo.getPassedExtraInfo(AimingData.class) : null;
        AimingData aimingData = aimingDataInfo != null && aimingDataInfo.isClaimedBy(this.id) ? aimingDataInfo : null;
        boolean requireAiming = combatActionEvaluatorComponent.requiresAiming();
        String nextAttack = combatActionEvaluatorComponent.getCurrentAttack();
        if (!nextAttack.equals(this.attack)) {
            this.attack = nextAttack;
            if (requireAiming && aimingData != null) {
                SingleCollector<BallisticData> collector = ActionAttack.THREAD_LOCAL_COLLECTOR.get();
                interactionManagerComponent.walkChain(ref, collector, InteractionType.Primary, (RootInteraction)RootInteraction.getAssetMap().getAsset(this.attack), store);
                BallisticData ballisticData = collector.getResult();
                if (ballisticData != null) {
                    aimingData.requireBallistic(ballisticData);
                    aimingData.setUseFlatTrajectory(true);
                } else {
                    double chargeDistance = combatActionEvaluatorComponent.getChargeDistance();
                    if (chargeDistance > 0.0) {
                        aimingData.setChargeDistance(chargeDistance);
                        aimingData.setDesiredHitAngle(0.2617993950843811);
                    }
                    aimingData.requireCloseCombat();
                }
                return false;
            }
        }
        TransformComponent transformComponent = store.getComponent(ref, TRANSFORM_COMPONENT_TYPE);
        assert (transformComponent != null);
        HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());
        assert (headRotationComponent != null);
        Vector3f vector3f = rotation = aimingData != null && aimingData.getChargeDistance() > 0.0 ? transformComponent.getRotation() : headRotationComponent.getRotation();
        if (aimingData != null && !aimingData.isOnTarget(rotation.getYaw(), rotation.getPitch(), 0.2617993950843811)) {
            aimingData.clearSolution();
            return false;
        }
        Ref<EntityStore> ref2 = target = aimingData != null ? aimingData.getTarget() : null;
        if (requireAiming && (target == null || !role.getPositionCache().hasLineOfSight(ref, target, store))) {
            if (aimingData != null) {
                aimingData.clearSolution();
            }
            return false;
        }
        if (combatActionEvaluatorComponent.shouldPositionFirst()) {
            double positioningAngle = Double.MAX_VALUE;
            if (this.cachedPositioningAngleProvider != null) {
                positioningAngle = this.cachedPositioningAngleProvider.getDoubleParameter();
            }
            if (positioningAngle != Double.MAX_VALUE) {
                if (target == null) {
                    return false;
                }
                TransformComponent targetTransformComponent = store.getComponent(target, TRANSFORM_COMPONENT_TYPE);
                assert (targetTransformComponent != null);
                Vector3d targetPosition = targetTransformComponent.getPosition();
                float selfYaw = NPCPhysicsMath.lookatHeading(transformComponent.getPosition(), targetPosition, transformComponent.getRotation().getYaw());
                float difference = PhysicsMath.normalizeTurnAngle(targetTransformComponent.getRotation().getYaw() - selfYaw - (float)positioningAngle);
                if (Math.abs(difference) > 0.08726646f) {
                    return false;
                }
            }
        }
        if (!(damageFriendlies = combatActionEvaluatorComponent.shouldDamageFriendlies()) && target != null && role.getPositionCache().isFriendlyBlockingLineOfSight(ref, target, store)) {
            aimingData.clearSolution();
            return false;
        }
        IInteractionSimulationHandler interactionSimulationHandler = interactionManagerComponent.getInteractionSimulationHandler();
        if (interactionSimulationHandler instanceof NPCInteractionSimulationHandler) {
            NPCInteractionSimulationHandler npcInteractionSimulationHandler = (NPCInteractionSimulationHandler)interactionSimulationHandler;
            npcInteractionSimulationHandler.requestChargeTime(combatActionEvaluatorComponent.getChargeFor());
        }
        InteractionType interactionType = combatActionEvaluatorComponent.getCurrentInteractionType();
        InteractionContext context = InteractionContext.forInteraction(interactionManagerComponent, ref, interactionType, store);
        context.setInteractionVarsGetter(combatActionEvaluatorComponent.getCurrentInteractionVarsGetter());
        InteractionChain chain = interactionManagerComponent.initChain(interactionType, context, RootInteraction.getRootInteractionOrUnknown(this.attack), false);
        interactionManagerComponent.queueExecuteChain(chain);
        role.getCombatSupport().setExecutingAttack(chain, damageFriendlies, 0.0);
        if (aimingData != null) {
            aimingData.setHaveAttacked(true);
        }
        combatActionEvaluatorComponent.completeCurrentAction(false, true);
        this.attack = null;
        return true;
    }

    @Override
    public void activate(Role role, @Nullable InfoProvider infoProvider) {
        super.activate(role, infoProvider);
        if (infoProvider == null) {
            return;
        }
        AimingData aimingData = infoProvider.getPassedExtraInfo(AimingData.class);
        if (aimingData != null) {
            aimingData.tryClaim(this.id);
        }
    }

    @Override
    public void deactivate(Role role, @Nullable InfoProvider infoProvider) {
        super.deactivate(role, infoProvider);
        if (infoProvider == null) {
            return;
        }
        AimingData aimingData = infoProvider.getPassedExtraInfo(AimingData.class);
        if (aimingData != null) {
            aimingData.release();
        }
    }
}

