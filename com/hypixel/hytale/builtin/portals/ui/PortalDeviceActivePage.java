/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.ui;

import com.hypixel.hytale.builtin.portals.components.PortalDevice;
import com.hypixel.hytale.builtin.portals.components.PortalDeviceConfig;
import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.builtin.portals.ui.PortalDeviceSummonPage;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalType;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nonnull;

public class PortalDeviceActivePage
extends InteractiveCustomUIPage<Data> {
    @Nonnull
    private final PortalDeviceConfig config;
    @Nonnull
    private final Ref<ChunkStore> blockRef;

    public PortalDeviceActivePage(@Nonnull PlayerRef playerRef, @Nonnull PortalDeviceConfig config, @Nonnull Ref<ChunkStore> blockRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, Data.CODEC);
        this.config = config;
        this.blockRef = blockRef;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        boolean bl;
        Object portalWorld;
        World world;
        State state = this.computeState(ref, store);
        if (state == Error.INVALID_BLOCK) {
            return;
        }
        commandBuilder.append("Pages/PortalDeviceActive.ui");
        if (!(state instanceof PortalIsOpen)) {
            commandBuilder.set("#Error.Visible", true);
            commandBuilder.set("#ErrorLabel.Text", Message.translation("server.customUI.portalDevice.unknownError").param("state", state.toString()));
            return;
        }
        PortalIsOpen portalIsOpen = (PortalIsOpen)state;
        try {
            Object object = portalIsOpen.world();
            world = object;
            portalWorld = object = portalIsOpen.portalWorld();
            boolean bl2 = bl = portalIsOpen.diedInside();
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
        boolean diedInIt = bl;
        PortalType portalType = ((PortalWorld)portalWorld).getPortalType();
        commandBuilder.set("#PortalPanel.Visible", true);
        if (diedInIt) {
            commandBuilder.set("#Died.Visible", true);
        }
        commandBuilder.set("#PortalTitle.TextSpans", Message.translation("server.customUI.portalDevice.portalTitle").param("name", portalType.getDisplayName()));
        commandBuilder.set("#PortalDescription.TextSpans", PortalDeviceSummonPage.createDescription(portalType, ((PortalWorld)portalWorld).getTimeLimitSeconds()));
        Message playerCountMsg = PortalDeviceActivePage.createPlayerCountMsg(world);
        commandBuilder.set("#PlayersInside.TextSpans", playerCountMsg);
        double remainingSeconds = ((PortalWorld)portalWorld).getRemainingSeconds(world);
        if (!(remainingSeconds < (double)((PortalWorld)portalWorld).getTimeLimitSeconds())) {
            commandBuilder.set("#PortalIsOpen.Visible", true);
            commandBuilder.set("#RemainingDuration.TextSpans", Message.translation("server.customUI.portalDevice.beTheFirst").color("#ea4fa46b"));
            return;
        }
        int remainingMinutes = (int)Math.round(remainingSeconds / 60.0);
        Message remainingTimeMsg = remainingMinutes <= 1 ? Message.translation("server.customUI.portalDevice.lessThanAMinute") : Message.translation("server.customUI.portalDevice.remainingMinutes").param("time", remainingMinutes);
        commandBuilder.set("#RemainingDuration.TextSpans", Message.translation("server.customUI.portalDevice.remainingDuration").param("remaining", remainingTimeMsg.color("#ea4fa46b")));
    }

    private static Message createPlayerCountMsg(World world) {
        int playerCount = world.getPlayerCount();
        String pinkEnoughColor = "#ea4fa46b";
        if (playerCount == 0) {
            return Message.translation("server.customUI.portalDevice.playersInside").param("count", Message.translation("server.customUI.portalDevice.playersInsideNone").color(pinkEnoughColor));
        }
        if (playerCount <= 4) {
            Message msg = Message.translation("server.customUI.portalDevice.playersInside").param("count", Message.raw(playerCount + "!").color(pinkEnoughColor));
            Collection<PlayerRef> playerRefs = world.getPlayerRefs();
            for (PlayerRef ref : playerRefs) {
                msg.insert(Message.raw("- ").color("#6b6b6b6b")).insert(ref.getUsername());
            }
            return msg;
        }
        return Message.translation("server.customUI.portalDevice.playersInside").param("count", Message.raw(playerCount + "!").color(pinkEnoughColor));
    }

    private State computeState(Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!this.blockRef.isValid()) {
            return Error.INVALID_BLOCK;
        }
        Store<ChunkStore> chunkStore = this.blockRef.getStore();
        PortalDevice portalDevice = chunkStore.getComponent(this.blockRef, PortalDevice.getComponentType());
        if (portalDevice == null) {
            return Error.INVALID_BLOCK;
        }
        World destinationWorld = portalDevice.getDestinationWorld();
        if (destinationWorld == null) {
            return Error.INVALID_WORLD;
        }
        Store<EntityStore> destinationStore = destinationWorld.getEntityStore().getStore();
        PortalWorld portalWorld = destinationStore.getResource(PortalWorld.getResourceType());
        if (!portalWorld.exists()) {
            return Error.DESTINATION_NOT_FRAGMENT;
        }
        UUIDComponent uuidComponent = componentAccessor.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        UUID playerUUID = uuidComponent.getUuid();
        boolean diedInside = portalWorld.getDiedInWorld().contains(playerUUID);
        return new PortalIsOpen(destinationWorld, portalWorld, diedInside);
    }

    protected static class Data {
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new).build();

        protected Data() {
        }
    }

    private static sealed interface State
    permits PortalIsOpen, Error {
    }

    private static enum Error implements State
    {
        INVALID_BLOCK,
        INVALID_WORLD,
        DESTINATION_NOT_FRAGMENT,
        INACTIVE_PORTAL;

    }

    private record PortalIsOpen(World world, PortalWorld portalWorld, boolean diedInside) implements State
    {
    }
}

