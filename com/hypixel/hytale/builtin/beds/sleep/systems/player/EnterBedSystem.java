/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.protocol.BlockMountType;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.sleep.SleepConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * Uses jvm11+ dynamic constants - pseudocode provided - see https://www.benf.org/other/cfr/dynamic-constants.html
 */
public class EnterBedSystem
extends RefChangeSystem<EntityStore, MountedComponent> {
    @Nonnull
    private static final Message MESSAGE_SERVER_INTERACTIONS_SLEEP_GAME_TIME_PAUSED = Message.translation("server.interactions.sleep.gameTimePaused");
    @Nonnull
    private static final Message MESSAGE_SERVER_INTERACTIONS_SLEEP_NOT_WITHIN_HOURS = Message.translation("server.interactions.sleep.notWithinHours");
    @Nonnull
    private static final Message MESSAGE_SERVER_INTERACTIONS_SLEEP_DISABLED = Message.translation("server.interactions.sleep.disabled");
    @Nonnull
    private final ComponentType<EntityStore, MountedComponent> mountedComponentType;
    @Nonnull
    private final ComponentType<EntityStore, PlayerRef> playerRefComponentType;
    @Nonnull
    private final Query<EntityStore> query;

    public EnterBedSystem(@Nonnull ComponentType<EntityStore, MountedComponent> mountedComponentType, @Nonnull ComponentType<EntityStore, PlayerRef> playerRefComponentType) {
        this.mountedComponentType = mountedComponentType;
        this.playerRefComponentType = playerRefComponentType;
        this.query = Query.and(mountedComponentType, playerRefComponentType);
    }

    @Override
    @Nonnull
    public ComponentType<EntityStore, MountedComponent> componentType() {
        return this.mountedComponentType;
    }

    @Override
    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull MountedComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        EnterBedSystem.check(ref, component, store, this.playerRefComponentType);
    }

    @Override
    public void onComponentSet(@Nonnull Ref<EntityStore> ref, @Nullable MountedComponent oldComponent, @Nonnull MountedComponent newComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        EnterBedSystem.check(ref, newComponent, store, this.playerRefComponentType);
    }

    @Override
    public void onComponentRemoved(@Nonnull Ref<EntityStore> ref, @Nonnull MountedComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    }

    private static void check(@Nonnull Ref<EntityStore> ref, @Nonnull MountedComponent component, @Nonnull Store<EntityStore> store, @Nonnull ComponentType<EntityStore, PlayerRef> playerRefComponentType) {
        if (component.getBlockMountType() == BlockMountType.Bed) {
            EnterBedSystem.onEnterBed(ref, store, playerRefComponentType);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void onEnterBed(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull ComponentType<EntityStore, PlayerRef> playerRefComponentType) {
        Object sleepConfig;
        World world = store.getExternalData().getWorld();
        CanSleepInWorld.Result canSleepResult = CanSleepInWorld.check(world);
        if (!canSleepResult.isNegative()) return;
        PlayerRef playerRefComponent = store.getComponent(ref, playerRefComponentType);
        assert (playerRefComponent != null);
        if (!(canSleepResult instanceof CanSleepInWorld.NotDuringSleepHoursRange)) {
            Message msg = EnterBedSystem.getMessage(canSleepResult);
            playerRefComponent.sendMessage(msg);
            return;
        }
        CanSleepInWorld.NotDuringSleepHoursRange notDuringSleepHoursRange = (CanSleepInWorld.NotDuringSleepHoursRange)canSleepResult;
        try {
            Object object = notDuringSleepHoursRange.worldTime();
            LocalDateTime worldTime = object;
            sleepConfig = object = notDuringSleepHoursRange.sleepConfig();
            LocalTime startTime = ((SleepConfig)sleepConfig).getSleepStartTime();
            Duration untilSleep = ((SleepConfig)sleepConfig).computeDurationUntilSleep(worldTime);
            Message msg = Message.translation("server.interactions.sleep.sleepAtTheseHours").param("timeValue", startTime.toString()).param("until", EnterBedSystem.formatDuration(untilSleep));
            playerRefComponent.sendMessage(msg.color("#F2D729"));
        }
        catch (Throwable throwable) {
            throw new MatchException(throwable.toString(), throwable);
        }
        SoundUtil.playSoundEvent2dToPlayer(playerRefComponent, ((SleepConfig)sleepConfig).getSounds().getFailIndex(), SoundCategory.UI);
    }

    /*
     * Exception decompiling
     */
    @Nonnull
    private static Message getMessage(@Nonnull CanSleepInWorld.Result result) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Can't turn ConstantPoolEntry into Literal - got DynamicInfo value=1,331
         *     at org.benf.cfr.reader.bytecode.analysis.parse.literal.TypedLiteral.getConstantPoolEntry(TypedLiteral.java:340)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.getBootstrapArg(Op02WithProcessedDataAndRefs.java:538)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.getVarArgs(Op02WithProcessedDataAndRefs.java:671)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeBootstrapArgs(Op02WithProcessedDataAndRefs.java:630)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeDynamic(Op02WithProcessedDataAndRefs.java:411)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.buildInvokeDynamic(Op02WithProcessedDataAndRefs.java:392)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.createStatement(Op02WithProcessedDataAndRefs.java:1215)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.access$100(Op02WithProcessedDataAndRefs.java:57)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs$11.call(Op02WithProcessedDataAndRefs.java:2080)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs$11.call(Op02WithProcessedDataAndRefs.java:2077)
         *     at org.benf.cfr.reader.util.graph.AbstractGraphVisitorFI.process(AbstractGraphVisitorFI.java:60)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.convertToOp03List(Op02WithProcessedDataAndRefs.java:2089)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:469)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Nonnull
    private static Message formatDuration(@Nonnull Duration duration) {
        long totalMinutes = duration.toMinutes();
        long hours = totalMinutes / 60L;
        long minutes = totalMinutes % 60L;
        String msgKey = hours > 0L ? "server.interactions.sleep.durationHours" : "server.interactions.sleep.durationMins";
        return Message.translation(msgKey).param("hours", hours).param("mins", hours == 0L ? String.valueOf(minutes) : String.format("%02d", minutes));
    }
}

