/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.singleplayer;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.serveraccess.Access;
import com.hypixel.hytale.protocol.packets.serveraccess.RequestServerAccess;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.ClientDelegatingProvider;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerRequestAccessEvent;
import com.hypixel.hytale.server.core.modules.singleplayer.commands.PlayCommand;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.ProcessUtil;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class SingleplayerModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(SingleplayerModule.class).depends(AccessControlModule.class).optDepends(InteractionModule.class).build();
    private static SingleplayerModule instance;
    private Access access;
    private Access requestedAccess;
    private List<InetSocketAddress> publicAddresses = new CopyOnWriteArrayList<InetSocketAddress>();

    public static SingleplayerModule get() {
        return instance;
    }

    public SingleplayerModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        if (Constants.SINGLEPLAYER) {
            AccessControlModule.get().registerAccessProvider(new ClientDelegatingProvider());
        }
        this.getCommandRegistry().registerCommand(new PlayCommand(this));
    }

    @Override
    protected void start() {
        Integer pid = Options.getOptionSet().valueOf(Options.CLIENT_PID);
        if (pid != null) {
            this.getLogger().at(Level.INFO).log("Client PID: %d", pid);
            HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
                try {
                    SingleplayerModule.checkClientPid();
                }
                catch (Exception e) {
                    ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to check client PID!");
                }
            }, 60L, 60L, TimeUnit.SECONDS);
        }
    }

    public Access getAccess() {
        return this.access;
    }

    public Access getRequestedAccess() {
        return this.requestedAccess;
    }

    public void requestServerAccess(Access access) {
        IEventDispatcher<SingleplayerRequestAccessEvent, SingleplayerRequestAccessEvent> dispatchFor;
        if (!Constants.SINGLEPLAYER) {
            throw new IllegalArgumentException("Server access can only be modified in singleplayer!");
        }
        ServerManager serverManager = ServerManager.get();
        short externalPort = 0;
        if (access != Access.Private) {
            if (!serverManager.bind(new InetSocketAddress(0))) {
                this.requestServerAccess(Access.Private);
                return;
            }
            try {
                InetSocketAddress boundAddress = serverManager.getNonLoopbackAddress();
                if (boundAddress != null) {
                    externalPort = (short)boundAddress.getPort();
                }
            }
            catch (Exception e) {
                ((HytaleLogger.Api)this.getLogger().at(Level.WARNING).withCause(e)).log("Failed to get bound port");
            }
        } else {
            for (Channel channel : serverManager.getListeners()) {
                InetSocketAddress inetSocketAddress;
                SocketAddress socketAddress = channel.localAddress();
                if (!(socketAddress instanceof InetSocketAddress) || !(inetSocketAddress = (InetSocketAddress)socketAddress).getAddress().isAnyLocalAddress()) continue;
                serverManager.unbind(channel);
            }
        }
        if ((dispatchFor = HytaleServer.get().getEventBus().dispatchFor(SingleplayerRequestAccessEvent.class)).hasListener()) {
            dispatchFor.dispatch(new SingleplayerRequestAccessEvent(access));
        }
        Universe.get().getPlayer(SingleplayerModule.getUuid()).getPacketHandler().writeNoCache(new RequestServerAccess(access, externalPort));
        this.requestedAccess = access;
    }

    public void setPublicAddresses(List<InetSocketAddress> publicAddresses) {
        this.publicAddresses = publicAddresses;
    }

    public void updateAccess(@Nonnull Access access) {
        if (this.requestedAccess != access) {
            Universe.get().sendMessage(Message.translation("server.modules.sp.requestAccessDifferent").param("requestedAccess", this.requestedAccess.toString()).param("access", access.toString()));
        }
        Universe.get().sendMessage(Message.translation("server.modules.sp.serverAccessUpdated").param("access", access.toString()));
        this.access = access;
    }

    public static void checkClientPid() {
        if (!ProcessUtil.isProcessRunning(Options.getOptionSet().valueOf(Options.CLIENT_PID))) {
            HytaleServer.get().shutdownServer(ShutdownReason.CLIENT_GONE);
        }
    }

    public static UUID getUuid() {
        return Options.getOptionSet().valueOf(Options.OWNER_UUID);
    }

    public static String getUsername() {
        return Options.getOptionSet().valueOf(Options.OWNER_NAME);
    }

    public static boolean isOwner(@Nonnull PlayerRef player) {
        return SingleplayerModule.isOwner(player.getPacketHandler().getAuth(), player.getUuid());
    }

    public static boolean isOwner(PlayerAuthentication playerAuth, @Nonnull UUID playerUuid) {
        return playerUuid.equals(SingleplayerModule.getUuid());
    }
}

