/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.flock;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.Flock;
import com.hypixel.hytale.server.flock.FlockPlugin;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.support.DebugSupport;
import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlockMembership
implements Component<EntityStore>,
DebugSupport.DebugFlagsChangeListener {
    public static final int VERSION = 5;
    public static final BuilderCodec<FlockMembership> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FlockMembership.class, FlockMembership::new).legacyVersioned()).codecVersion(5)).append(new KeyedCodec<UUID>("FlockId", Codec.UUID_BINARY), (membership, uuid) -> {
        membership.flockId = uuid;
    }, membership -> membership.flockId).setVersionRange(5, 5).add()).append(new KeyedCodec<Type>("Type", new EnumCodec<Type>(Type.class, EnumCodec.EnumStyle.LEGACY)), (membership, type) -> {
        membership.membershipType = type;
    }, membership -> membership.membershipType).add()).build();
    private UUID flockId;
    private Type membershipType;
    @Nullable
    private Ref<EntityStore> flockRef;
    private transient boolean wasVisFlock;
    private transient boolean listenerRegistered;

    public static ComponentType<EntityStore, FlockMembership> getComponentType() {
        return FlockPlugin.get().getFlockMembershipComponentType();
    }

    public UUID getFlockId() {
        return this.flockId;
    }

    public void setFlockId(UUID flockId) {
        this.flockId = flockId;
    }

    @Nullable
    public Ref<EntityStore> getFlockRef() {
        return this.flockRef;
    }

    public void setFlockRef(@Nullable Ref<EntityStore> flockRef) {
        this.flockRef = flockRef;
    }

    public void setMembershipType(Type membershipType) {
        this.membershipType = membershipType;
    }

    public Type getMembershipType() {
        return this.membershipType;
    }

    public void unload() {
        this.flockRef = null;
    }

    public void registerAsDebugListener(@Nonnull DebugSupport debugSupport, @Nonnull Flock flock) {
        if (this.listenerRegistered) {
            return;
        }
        this.wasVisFlock = debugSupport.isDebugFlagSet(RoleDebugFlags.VisFlock);
        debugSupport.registerDebugFlagsListener(this);
        this.listenerRegistered = true;
        if (this.wasVisFlock) {
            flock.incrementVisFlockMemberCount();
        }
    }

    public void unregisterAsDebugListener(@Nonnull DebugSupport debugSupport, @Nonnull Flock flock) {
        if (!this.listenerRegistered) {
            return;
        }
        debugSupport.removeDebugFlagsListener(this);
        this.listenerRegistered = false;
        if (this.wasVisFlock) {
            flock.decrementVisFlockMemberCount();
        }
    }

    @Override
    public void onDebugFlagsChanged(EnumSet<RoleDebugFlags> newFlags) {
        boolean isVisFlock = newFlags.contains(RoleDebugFlags.VisFlock);
        if (isVisFlock == this.wasVisFlock) {
            return;
        }
        this.wasVisFlock = isVisFlock;
        if (this.flockRef == null || !this.flockRef.isValid()) {
            return;
        }
        Flock flock = this.flockRef.getStore().getComponent(this.flockRef, Flock.getComponentType());
        if (flock == null) {
            return;
        }
        if (isVisFlock) {
            flock.incrementVisFlockMemberCount();
        } else {
            flock.decrementVisFlockMemberCount();
        }
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        FlockMembership membership = new FlockMembership();
        membership.flockId = this.flockId;
        membership.flockRef = this.flockRef;
        membership.membershipType = this.membershipType;
        return membership;
    }

    public static enum Type {
        JOINING(false),
        MEMBER(false),
        LEADER(true),
        INTERIM_LEADER(true);

        private final boolean actsAsLeader;

        private Type(boolean actsAsLeader) {
            this.actsAsLeader = actsAsLeader;
        }

        public boolean isActingAsLeader() {
            return this.actsAsLeader;
        }
    }
}

