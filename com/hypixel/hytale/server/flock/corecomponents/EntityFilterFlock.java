/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock.corecomponents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.FlockMembership;
import com.hypixel.hytale.server.flock.FlockMembershipSystems;
import com.hypixel.hytale.server.flock.corecomponents.builders.BuilderEntityFilterFlock;
import com.hypixel.hytale.server.npc.corecomponents.EntityFilterBase;
import com.hypixel.hytale.server.npc.movement.FlockMembershipType;
import com.hypixel.hytale.server.npc.movement.FlockPlayerMembership;
import com.hypixel.hytale.server.npc.role.Role;
import javax.annotation.Nonnull;

public class EntityFilterFlock
extends EntityFilterBase {
    public static final int COST = 100;
    protected static final ComponentType<EntityStore, FlockMembership> FLOCK_MEMBERSHIP_COMPONENT_TYPE = FlockMembership.getComponentType();
    protected static final ComponentType<EntityStore, Player> PLAYER_COMPONENT_TYPE = Player.getComponentType();
    protected static final ComponentType<EntityStore, EntityGroup> ENTITY_GROUP_COMPONENT_TYPE = EntityGroup.getComponentType();
    protected final FlockMembershipType flockMembership;
    protected final FlockPlayerMembership flockPlayerMembership;
    protected final int[] size;
    protected final boolean checkCanJoin;

    public EntityFilterFlock(@Nonnull BuilderEntityFilterFlock builder) {
        this.flockMembership = builder.getFlockMembership();
        this.flockPlayerMembership = builder.getFlockPlayerMembership();
        this.size = builder.getSize();
        this.checkCanJoin = builder.isCheckCanJoin();
    }

    @Override
    public boolean matchesEntity(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull Role role, @Nonnull Store<EntityStore> store) {
        Ref<EntityStore> flockReference;
        FlockMembership membership;
        block20: {
            membership = store.getComponent(targetRef, FLOCK_MEMBERSHIP_COMPONENT_TYPE);
            switch (this.flockMembership) {
                default: {
                    throw new MatchException(null, null);
                }
                case Leader: {
                    if (membership == null || !membership.getMembershipType().isActingAsLeader()) {
                        break;
                    }
                    break block20;
                }
                case Follower: {
                    if (membership == null || membership.getMembershipType().isActingAsLeader()) {
                        break;
                    }
                    break block20;
                }
                case Member: {
                    if (membership == null) {
                        break;
                    }
                    break block20;
                }
                case NotMember: {
                    if (membership != null) {
                        break;
                    }
                    break block20;
                }
                case Any: {
                    break block20;
                }
            }
            return false;
        }
        EntityGroup group = null;
        if (membership != null && (flockReference = membership.getFlockRef()) != null && flockReference.isValid()) {
            group = store.getComponent(flockReference, ENTITY_GROUP_COMPONENT_TYPE);
        }
        if (this.size != null && group != null && (group.size() < this.size[0] || group.size() > this.size[1])) {
            return false;
        }
        if (this.checkCanJoin && (membership == null || !FlockMembershipSystems.canJoinFlock(targetRef, membership.getFlockRef(), store))) {
            return false;
        }
        Ref<EntityStore> leaderRef = group != null ? group.getLeaderRef() : null;
        boolean leaderIsPlayer = leaderRef != null && leaderRef.isValid() && store.getArchetype(leaderRef).contains(PLAYER_COMPONENT_TYPE);
        return switch (this.flockPlayerMembership) {
            default -> throw new MatchException(null, null);
            case FlockPlayerMembership.Member -> leaderIsPlayer;
            case FlockPlayerMembership.NotMember -> {
                if (!leaderIsPlayer) {
                    yield true;
                }
                yield false;
            }
            case FlockPlayerMembership.Any -> true;
        };
    }

    @Override
    public int cost() {
        return 100;
    }
}

