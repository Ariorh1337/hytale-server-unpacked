/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.commands;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.commands.NPCWorldCommandBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class NPCMultiSelectCommandBase
extends NPCWorldCommandBase {
    protected static final float DEFAULT_CONE_ANGLE = 30.0f;
    protected static final float DEFAULT_RANGE = 8.0f;
    protected static final float RANGE_MIN = 0.0f;
    protected static final float RANGE_MAX = 2048.0f;
    protected static final float CONE_ANGLE_MIN = 0.0f;
    protected static final float CONE_ANGLE_MAX = 180.0f;
    @Nonnull
    protected final OptionalArg<Float> coneAngleArg = this.withOptionalArg("angle", "server.commands.npc.command.angle.desc", ArgTypes.FLOAT);
    @Nonnull
    protected final OptionalArg<Float> rangeArg = this.withOptionalArg("range", "server.commands.npc.command.range.desc", ArgTypes.FLOAT);
    @Nonnull
    private final OptionalArg<String> rolesArg = this.withOptionalArg("roles", "server.commands.npc.command.roles.desc", ArgTypes.STRING);
    @Nonnull
    private final FlagArg nearestArg = this.withFlagArg("nearest", "server.commands.npc.command.nearest.desc");
    @Nonnull
    private final FlagArg presetCone30 = this.withFlagArg("cone", "server.commands.npc.command.preset.cone.desc");
    @Nonnull
    private final FlagArg presetCone30all = this.withFlagArg("coneAll", "server.commands.npc.command.preset.cone_all.desc");
    @Nonnull
    private final FlagArg presetSphere = this.withFlagArg("sphere", "server.commands.npc.command.preset.sphere.desc");
    @Nonnull
    private final FlagArg presetRay = this.withFlagArg("ray", "server.commands.npc.command.preset.ray.desc");

    public NPCMultiSelectCommandBase(@Nonnull String name, @Nonnull String description) {
        super(name, description);
    }

    public NPCMultiSelectCommandBase(@Nonnull String name, @Nonnull String description, boolean requiresConfirmation) {
        super(name, description, requiresConfirmation);
    }

    public NPCMultiSelectCommandBase(@Nonnull String description) {
        super(description);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        Vector3d eyePosition;
        boolean nearest;
        float coneAngleDeg;
        float range;
        if (this.entityArg.provided(context)) {
            Ref<EntityStore> ref2 = this.entityArg.get(store, context);
            if (ref2 == null) {
                return;
            }
            NPCEntity npc = NPCMultiSelectCommandBase.ensureIsNPC(context, store, ref2);
            if (npc == null) {
                return;
            }
            this.execute(context, npc, world, store, ref2);
            return;
        }
        Ref<EntityStore> playerRef = null;
        if (context.isPlayer()) {
            playerRef = context.senderAsPlayerRef();
        }
        if (playerRef == null || !playerRef.isValid()) {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_OR_ARG);
            return;
        }
        HashSet<String> roleSet = new HashSet<String>();
        if (this.rolesArg.provided(context)) {
            String[] roles;
            String roleString = (String)this.rolesArg.get(context);
            if (roleString == null || roleString.isEmpty()) {
                context.sendMessage(Message.translation("server.commands.errors.npc.no_role_list_provided"));
                return;
            }
            for (String role : roles = roleString.split(",")) {
                if (role.isBlank()) continue;
                if (!NPCPlugin.get().hasRoleName(role)) {
                    context.sendMessage(Message.translation("server.commands.errors.npc.unknown_role").param("role", role));
                    return;
                }
                roleSet.add(role);
            }
        }
        float f = range = this.rangeArg.provided(context) ? ((Float)this.rangeArg.get(context)).floatValue() : 8.0f;
        if (range < 0.0f || range > 2048.0f) {
            context.sendMessage(Message.translation("server.commands.errors.validation.range.between_inclusive").param("param", "range").param("min", 0.0f).param("max", 2048.0f).param("value", range));
            return;
        }
        if (this.presetCone30.provided(context)) {
            coneAngleDeg = 30.0f;
            nearest = true;
        } else if (this.presetCone30all.provided(context)) {
            coneAngleDeg = 30.0f;
            nearest = false;
        } else if (this.presetSphere.provided(context)) {
            coneAngleDeg = 180.0f;
            nearest = false;
        } else if (this.presetRay.provided(context)) {
            coneAngleDeg = 0.0f;
            nearest = true;
        } else {
            float f2 = coneAngleDeg = this.coneAngleArg.provided(context) ? (float)((Float)this.coneAngleArg.get(context)).intValue() : 30.0f;
            if (coneAngleDeg < 0.0f || coneAngleDeg > 180.0f) {
                context.sendMessage(Message.translation("server.commands.errors.validation.range.between_inclusive").param("param", "angle").param("min", 0.0f).param("max", 180.0f).param("value", coneAngleDeg));
                return;
            }
            nearest = this.nearestArg.provided(context);
        }
        List<Ref<EntityStore>> refs = null;
        ComponentType<EntityStore, NPCEntity> npcEntityComponentType = NPCEntity.getComponentType();
        assert (npcEntityComponentType != null);
        if (coneAngleDeg == 0.0f) {
            Ref<EntityStore> ref3 = TargetUtil.getTargetEntity(playerRef, range, store);
            if (ref3 != null && store.getComponent(ref3, npcEntityComponentType) != null) {
                refs = new ArrayList<Ref<EntityStore>>();
                refs.add(ref3);
            }
            eyePosition = Vector3d.ZERO;
        } else {
            TransformComponent playerTransform = store.getComponent(playerRef, TransformComponent.getComponentType());
            assert (playerTransform != null);
            Transform viewTransform = TargetUtil.getLook(playerRef, store);
            eyePosition = viewTransform.getPosition();
            Vector3d eyeDirection = viewTransform.getDirection();
            assert (eyePosition.length() == 1.0);
            refs = TargetUtil.getAllEntitiesInSphere(eyePosition, range, store);
            float cosineConeAngle = (float)Math.cos((float)Math.toRadians(coneAngleDeg));
            assert (coneAngleDeg != 180.0f || cosineConeAngle == -1.0f);
            refs.removeIf(entityRef -> {
                if (store.getComponent((Ref<EntityStore>)entityRef, npcEntityComponentType) == null) {
                    return true;
                }
                if (cosineConeAngle <= -1.0f) {
                    return false;
                }
                TransformComponent entityTransform = store.getComponent((Ref<EntityStore>)entityRef, TransformComponent.getComponentType());
                assert (entityTransform != null);
                Vector3d direction = Vector3d.directionTo(eyePosition, entityTransform.getPosition());
                double lengthDirection = direction.length();
                if (lengthDirection < 1.0E-4) {
                    return true;
                }
                return eyeDirection.dot(direction) < (double)cosineConeAngle * lengthDirection;
            });
        }
        if (refs != null && !refs.isEmpty() && !roleSet.isEmpty()) {
            refs.removeIf(ref -> {
                NPCEntity npc = (NPCEntity)store.getComponent((Ref<EntityStore>)ref, npcEntityComponentType);
                return !roleSet.contains(npc.getRoleName());
            });
        }
        if (refs == null || refs.isEmpty()) {
            context.sendMessage(MESSAGE_COMMANDS_ERRORS_NO_ENTITY_IN_VIEW);
            return;
        }
        if (nearest && refs.size() > 1) {
            Ref<EntityStore> nearestRef = (Ref<EntityStore>)refs.getFirst();
            double nearestDistanceSq = Double.MAX_VALUE;
            for (Ref<EntityStore> ref4 : refs) {
                TransformComponent npcTransform = store.getComponent(ref4, TransformComponent.getComponentType());
                assert (npcTransform != null);
                double distanceSq = Vector3d.directionTo(eyePosition, npcTransform.getPosition()).squaredLength();
                if (!(distanceSq < nearestDistanceSq)) continue;
                nearestDistanceSq = distanceSq;
                nearestRef = ref4;
            }
            refs = List.of(nearestRef);
        }
        this.processEntityList(context, world, store, refs);
    }

    protected void processEntityList(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store, @Nonnull List<Ref<EntityStore>> refs) {
        refs.forEach(ref -> {
            NPCEntity npc = store.getComponent((Ref<EntityStore>)ref, NPCEntity.getComponentType());
            assert (npc != null);
            this.execute(context, npc, world, store, (Ref<EntityStore>)ref);
        });
    }
}

