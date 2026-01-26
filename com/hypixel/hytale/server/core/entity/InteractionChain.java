/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ForkedChainId;
import com.hypixel.hytale.protocol.InteractionChainData;
import com.hypixel.hytale.protocol.InteractionCooldown;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.server.core.entity.ChainSyncStorage;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.InteractionEntry;
import com.hypixel.hytale.server.core.entity.InteractionManager;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InteractionChain
implements ChainSyncStorage {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long NULL_FORK_ID = InteractionChain.forkedIdToIndex(new ForkedChainId(-1, Integer.MAX_VALUE, null));
    private final InteractionType type;
    private InteractionType baseType;
    private final InteractionChainData chainData;
    private int chainId;
    private final ForkedChainId forkedChainId;
    private final ForkedChainId baseForkedChainId;
    private boolean predicted;
    private final InteractionContext context;
    @Nonnull
    private final Long2ObjectMap<InteractionChain> forkedChains = new Long2ObjectOpenHashMap<InteractionChain>();
    @Nonnull
    private final Long2ObjectMap<TempChain> tempForkedChainData = new Long2ObjectOpenHashMap<TempChain>();
    @Nonnull
    private final Long2LongMap forkedChainsMap = new Long2LongOpenHashMap();
    @Nonnull
    private final List<InteractionChain> newForks = new ObjectArrayList<InteractionChain>();
    @Nonnull
    private final RootInteraction initialRootInteraction;
    private RootInteraction rootInteraction;
    private int operationCounter = 0;
    @Nonnull
    private final List<CallState> callStack = new ObjectArrayList<CallState>();
    private int simulatedCallStack = 0;
    private final boolean requiresClient;
    private int simulatedOperationCounter = 0;
    private RootInteraction simulatedRootInteraction;
    private int operationIndex = 0;
    private int operationIndexOffset = 0;
    private int clientOperationIndex = 0;
    @Nonnull
    private final List<InteractionEntry> interactions = new ObjectArrayList<InteractionEntry>();
    @Nonnull
    private final List<InteractionSyncData> tempSyncData = new ObjectArrayList<InteractionSyncData>();
    private int tempSyncDataOffset = 0;
    private long timestamp = System.nanoTime();
    private long waitingForServerFinished;
    private long waitingForClientFinished;
    private InteractionState clientState = InteractionState.NotFinished;
    private InteractionState serverState = InteractionState.NotFinished;
    private InteractionState finalState = InteractionState.Finished;
    @Nullable
    private Runnable onCompletion;
    private boolean sentInitial;
    private boolean desynced;
    private float timeShift;
    private boolean firstRun = true;
    private boolean isFirstRun = true;
    private boolean completed = false;
    private boolean preTicked;
    boolean skipChainOnClick;

    public InteractionChain(InteractionType type, InteractionContext context, InteractionChainData chainData, @Nonnull RootInteraction rootInteraction, @Nullable Runnable onCompletion, boolean requiresClient) {
        this(null, null, type, context, chainData, rootInteraction, onCompletion, requiresClient);
    }

    public InteractionChain(ForkedChainId forkedChainId, ForkedChainId baseForkedChainId, InteractionType type, InteractionContext context, InteractionChainData chainData, @Nonnull RootInteraction rootInteraction, @Nullable Runnable onCompletion, boolean requiresClient) {
        this.type = this.baseType = type;
        this.context = context;
        this.chainData = chainData;
        this.forkedChainId = forkedChainId;
        this.baseForkedChainId = baseForkedChainId;
        this.onCompletion = onCompletion;
        this.rootInteraction = this.simulatedRootInteraction = rootInteraction;
        this.initialRootInteraction = this.simulatedRootInteraction;
        this.requiresClient = requiresClient || rootInteraction.needsRemoteSync();
        this.forkedChainsMap.defaultReturnValue(NULL_FORK_ID);
    }

    public InteractionType getType() {
        return this.type;
    }

    public int getChainId() {
        return this.chainId;
    }

    public ForkedChainId getForkedChainId() {
        return this.forkedChainId;
    }

    public ForkedChainId getBaseForkedChainId() {
        return this.baseForkedChainId;
    }

    @Nonnull
    public RootInteraction getInitialRootInteraction() {
        return this.initialRootInteraction;
    }

    public boolean isPredicted() {
        return this.predicted;
    }

    public InteractionContext getContext() {
        return this.context;
    }

    public InteractionChainData getChainData() {
        return this.chainData;
    }

    public InteractionState getServerState() {
        return this.serverState;
    }

    public boolean requiresClient() {
        return this.requiresClient;
    }

    public RootInteraction getRootInteraction() {
        return this.rootInteraction;
    }

    public RootInteraction getSimulatedRootInteraction() {
        return this.simulatedRootInteraction;
    }

    public int getOperationCounter() {
        return this.operationCounter;
    }

    public void setOperationCounter(int operationCounter) {
        this.operationCounter = operationCounter;
    }

    public int getSimulatedOperationCounter() {
        return this.simulatedOperationCounter;
    }

    public void setSimulatedOperationCounter(int simulatedOperationCounter) {
        this.simulatedOperationCounter = simulatedOperationCounter;
    }

    public boolean wasPreTicked() {
        return this.preTicked;
    }

    public void setPreTicked(boolean preTicked) {
        this.preTicked = preTicked;
    }

    public int getOperationIndex() {
        return this.operationIndex;
    }

    public void nextOperationIndex() {
        ++this.operationIndex;
        ++this.clientOperationIndex;
    }

    public int getClientOperationIndex() {
        return this.clientOperationIndex;
    }

    @Nullable
    public InteractionChain findForkedChain(@Nonnull ForkedChainId chainId, @Nullable InteractionChainData data) {
        InteractionChain chain;
        long id = InteractionChain.forkedIdToIndex(chainId);
        long altId = this.forkedChainsMap.get(id);
        if (altId != NULL_FORK_ID) {
            id = altId;
        }
        if ((chain = (InteractionChain)this.forkedChains.get(id)) != null || chainId.subIndex >= 0 || data == null) {
            return chain;
        }
        InteractionEntry entry = this.getInteraction(chainId.entryIndex);
        if (entry == null) {
            return null;
        }
        int rootId = entry.getServerState().rootInteraction;
        int opCounter = entry.getServerState().operationCounter;
        RootInteraction root = RootInteraction.getAssetMap().getAsset(rootId);
        Operation op = root.getOperation(opCounter).getInnerOperation();
        if (op instanceof Interaction) {
            Interaction interaction = (Interaction)op;
            this.context.initEntry(this, entry, null);
            chain = interaction.mapForkChain(this.context, data);
            this.context.deinitEntry(this, entry, null);
            if (chain != null) {
                this.forkedChainsMap.put(id, InteractionChain.forkedIdToIndex(chain.getBaseForkedChainId()));
            }
            return chain;
        }
        return null;
    }

    public InteractionChain getForkedChain(@Nonnull ForkedChainId chainId) {
        long altId;
        long id = InteractionChain.forkedIdToIndex(chainId);
        if (chainId.subIndex < 0 && (altId = this.forkedChainsMap.get(id)) != NULL_FORK_ID) {
            id = altId;
        }
        return (InteractionChain)this.forkedChains.get(id);
    }

    public void putForkedChain(@Nonnull ForkedChainId chainId, @Nonnull InteractionChain chain) {
        this.newForks.add(chain);
        this.forkedChains.put(InteractionChain.forkedIdToIndex(chainId), chain);
    }

    @Nullable
    public TempChain getTempForkedChain(@Nonnull ForkedChainId chainId) {
        InteractionEntry entry = this.getInteraction(chainId.entryIndex);
        if (entry != null ? chainId.subIndex < entry.getNextForkId() : chainId.entryIndex < this.operationIndexOffset) {
            return null;
        }
        return this.tempForkedChainData.computeIfAbsent(InteractionChain.forkedIdToIndex(chainId), i -> new TempChain());
    }

    @Nullable
    TempChain removeTempForkedChain(@Nonnull ForkedChainId chainId, InteractionChain forkChain) {
        TempChain found;
        long id = InteractionChain.forkedIdToIndex(chainId);
        long altId = this.forkedChainsMap.get(id);
        if (altId != NULL_FORK_ID) {
            id = altId;
        }
        if ((found = (TempChain)this.tempForkedChainData.remove(id)) != null) {
            return found;
        }
        InteractionEntry iEntry = this.context.getEntry();
        RootInteraction root = RootInteraction.getAssetMap().getAsset(iEntry.getState().rootInteraction);
        Operation op = root.getOperation(iEntry.getState().operationCounter).getInnerOperation();
        if (op instanceof Interaction) {
            Interaction interaction = (Interaction)op;
            ObjectIterator<Long2ObjectMap.Entry<TempChain>> it = Long2ObjectMaps.fastIterator(this.getTempForkedChainData());
            while (it.hasNext()) {
                int entryId;
                Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)it.next();
                TempChain tempChain = (TempChain)entry.getValue();
                if (tempChain.baseForkedChainId == null || (entryId = tempChain.baseForkedChainId.entryIndex) != iEntry.getIndex()) continue;
                InteractionChain chain = interaction.mapForkChain(this.getContext(), tempChain.chainData);
                if (chain != null) {
                    this.forkedChainsMap.put(InteractionChain.forkedIdToIndex(tempChain.baseForkedChainId), InteractionChain.forkedIdToIndex(chain.getBaseForkedChainId()));
                }
                if (chain != forkChain) continue;
                it.remove();
                return tempChain;
            }
        }
        return null;
    }

    public boolean hasSentInitial() {
        return this.sentInitial;
    }

    public void setSentInitial(boolean sentInitial) {
        this.sentInitial = sentInitial;
    }

    public float getTimeShift() {
        return this.timeShift;
    }

    public void setTimeShift(float timeShift) {
        this.timeShift = timeShift;
    }

    public boolean consumeFirstRun() {
        this.isFirstRun = this.firstRun;
        this.firstRun = false;
        return this.isFirstRun;
    }

    public boolean isFirstRun() {
        return this.isFirstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.isFirstRun = firstRun;
    }

    public int getCallDepth() {
        return this.callStack.size();
    }

    public int getSimulatedCallDepth() {
        return this.simulatedCallStack;
    }

    public void pushRoot(RootInteraction nextInteraction, boolean simulate) {
        if (simulate) {
            this.simulatedRootInteraction = nextInteraction;
            this.simulatedOperationCounter = 0;
            ++this.simulatedCallStack;
            return;
        }
        this.callStack.add(new CallState(this.rootInteraction, this.operationCounter));
        this.operationCounter = 0;
        this.rootInteraction = nextInteraction;
    }

    public void popRoot() {
        CallState state = this.callStack.removeLast();
        this.rootInteraction = state.rootInteraction;
        this.operationCounter = state.operationCounter + 1;
        this.simulatedRootInteraction = this.rootInteraction;
        this.simulatedOperationCounter = this.operationCounter;
        --this.simulatedCallStack;
    }

    public float getTimeInSeconds() {
        if (this.timestamp == 0L) {
            return 0.0f;
        }
        long diff = System.nanoTime() - this.timestamp;
        return (float)diff / 1.0E9f;
    }

    public void setOnCompletion(Runnable onCompletion) {
        this.onCompletion = onCompletion;
    }

    void onCompletion(CooldownHandler cooldownHandler, boolean isRemote) {
        if (this.completed) {
            return;
        }
        this.completed = true;
        if (this.onCompletion != null) {
            this.onCompletion.run();
            this.onCompletion = null;
        }
        if (isRemote) {
            CooldownHandler.Cooldown cooldownTracker;
            InteractionCooldown cooldown = this.initialRootInteraction.getCooldown();
            String cooldownId = this.initialRootInteraction.getId();
            if (cooldown != null && cooldown.cooldownId != null) {
                cooldownId = cooldown.cooldownId;
            }
            if ((cooldownTracker = cooldownHandler.getCooldown(cooldownId)) != null) {
                cooldownTracker.tick(0.016666668f);
            }
        }
    }

    void updateServerState() {
        if (this.serverState != InteractionState.NotFinished) {
            return;
        }
        if (this.operationCounter >= this.rootInteraction.getOperationMax()) {
            this.serverState = this.finalState;
            return;
        }
        InteractionEntry entry = this.getOrCreateInteractionEntry(this.operationIndex);
        this.serverState = switch (entry.getServerState().state) {
            case InteractionState.NotFinished, InteractionState.Finished -> InteractionState.NotFinished;
            default -> InteractionState.Failed;
        };
    }

    void updateSimulatedState() {
        if (this.clientState != InteractionState.NotFinished) {
            return;
        }
        if (this.simulatedOperationCounter >= this.rootInteraction.getOperationMax()) {
            this.clientState = this.finalState;
            return;
        }
        InteractionEntry entry = this.getOrCreateInteractionEntry(this.clientOperationIndex);
        this.clientState = switch (entry.getSimulationState().state) {
            case InteractionState.NotFinished, InteractionState.Finished -> InteractionState.NotFinished;
            default -> InteractionState.Failed;
        };
    }

    @Override
    public InteractionState getClientState() {
        return this.clientState;
    }

    @Override
    public void setClientState(InteractionState state) {
        this.clientState = state;
    }

    @Nonnull
    public InteractionEntry getOrCreateInteractionEntry(int index) {
        InteractionEntry entry;
        int oIndex = index - this.operationIndexOffset;
        if (oIndex < 0) {
            throw new IllegalArgumentException("Trying to access removed interaction entry");
        }
        InteractionEntry interactionEntry = entry = oIndex < this.interactions.size() ? this.interactions.get(oIndex) : null;
        if (entry == null) {
            if (oIndex != this.interactions.size()) {
                throw new IllegalArgumentException("Trying to add interaction entry at a weird location: " + oIndex + " " + this.interactions.size());
            }
            entry = new InteractionEntry(index, this.operationCounter, RootInteraction.getRootInteractionIdOrUnknown(this.rootInteraction.getId()));
            this.interactions.add(entry);
        }
        return entry;
    }

    @Override
    @Nullable
    public InteractionEntry getInteraction(int index) {
        if ((index -= this.operationIndexOffset) < 0 || index >= this.interactions.size()) {
            return null;
        }
        return this.interactions.get(index);
    }

    public void removeInteractionEntry(@Nonnull InteractionManager interactionManager, int index) {
        int oIndex = index - this.operationIndexOffset;
        if (oIndex != 0) {
            this.flagDesync();
            return;
        }
        InteractionEntry entry = this.interactions.remove(oIndex);
        ++this.operationIndexOffset;
        this.tempForkedChainData.values().removeIf(fork -> {
            if (fork.baseForkedChainId.entryIndex != entry.getIndex()) {
                return false;
            }
            interactionManager.sendCancelPacket(this.getChainId(), fork.forkedChainId);
            return true;
        });
    }

    @Override
    public void putInteractionSyncData(int index, InteractionSyncData data) {
        if ((index -= this.tempSyncDataOffset) >= 0) {
            if (index < this.tempSyncData.size()) {
                this.tempSyncData.set(index, data);
            } else if (index == this.tempSyncData.size()) {
                this.tempSyncData.add(data);
            } else {
                LOGGER.at(Level.WARNING).log("Temp sync data sent out of order: " + index + " " + this.tempSyncData.size());
            }
        }
    }

    @Override
    public void clearInteractionSyncData(int operationIndex) {
        int tempIdx = operationIndex - this.tempSyncDataOffset;
        if (!this.tempSyncData.isEmpty()) {
            for (int end = this.tempSyncData.size() - 1; end >= tempIdx && end >= 0; --end) {
                this.tempSyncData.remove(end);
            }
        }
        int idx = operationIndex - this.operationIndexOffset;
        for (int i = Math.max(idx, 0); i < this.interactions.size(); ++i) {
            this.interactions.get(i).setClientState(null);
        }
    }

    @Nullable
    public InteractionSyncData removeInteractionSyncData(int index) {
        if ((index -= this.tempSyncDataOffset) != 0) {
            return null;
        }
        if (this.tempSyncData.isEmpty()) {
            return null;
        }
        if (this.tempSyncData.get(index) == null) {
            return null;
        }
        ++this.tempSyncDataOffset;
        return this.tempSyncData.remove(index);
    }

    @Override
    public void updateSyncPosition(int index) {
        if (this.tempSyncDataOffset == index) {
            this.tempSyncDataOffset = index + 1;
        } else if (index > this.tempSyncDataOffset) {
            throw new IllegalArgumentException("Temp sync data sent out of order: " + index + " " + this.tempSyncData.size());
        }
    }

    @Override
    public boolean isSyncDataOutOfOrder(int index) {
        return index > this.tempSyncDataOffset + this.tempSyncData.size();
    }

    @Override
    public void syncFork(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionManager manager, @Nonnull SyncInteractionChain packet) {
        ForkedChainId baseId = packet.forkedId;
        while (baseId.forkedId != null) {
            baseId = baseId.forkedId;
        }
        InteractionChain fork = this.findForkedChain(baseId, packet.data);
        if (fork != null) {
            manager.sync(ref, fork, packet);
        } else {
            TempChain temp = this.getTempForkedChain(baseId);
            if (temp == null) {
                return;
            }
            temp.setForkedChainId(packet.forkedId);
            temp.setBaseForkedChainId(baseId);
            temp.setChainData(packet.data);
            manager.sync(ref, temp, packet);
        }
    }

    public void copyTempFrom(@Nonnull TempChain temp) {
        this.setClientState(temp.clientState);
        this.tempSyncData.addAll(temp.tempSyncData);
        this.getTempForkedChainData().putAll(temp.tempForkedChainData);
    }

    private static long forkedIdToIndex(@Nonnull ForkedChainId chainId) {
        return (long)chainId.entryIndex << 32 | (long)chainId.subIndex & 0xFFFFFFFFL;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public InteractionType getBaseType() {
        return this.baseType;
    }

    public void setBaseType(InteractionType baseType) {
        this.baseType = baseType;
    }

    @Nonnull
    public Long2ObjectMap<InteractionChain> getForkedChains() {
        return this.forkedChains;
    }

    @Nonnull
    public Long2ObjectMap<TempChain> getTempForkedChainData() {
        return this.tempForkedChainData;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getWaitingForServerFinished() {
        return this.waitingForServerFinished;
    }

    public void setWaitingForServerFinished(long waitingForServerFinished) {
        this.waitingForServerFinished = waitingForServerFinished;
    }

    public long getWaitingForClientFinished() {
        return this.waitingForClientFinished;
    }

    public void setWaitingForClientFinished(long waitingForClientFinished) {
        this.waitingForClientFinished = waitingForClientFinished;
    }

    public void setServerState(InteractionState serverState) {
        this.serverState = serverState;
    }

    public InteractionState getFinalState() {
        return this.finalState;
    }

    public void setFinalState(InteractionState finalState) {
        this.finalState = finalState;
    }

    void setPredicted(boolean predicted) {
        this.predicted = predicted;
    }

    public void flagDesync() {
        this.desynced = true;
        this.forkedChains.forEach((k, c) -> c.flagDesync());
    }

    public boolean isDesynced() {
        return this.desynced;
    }

    @Nonnull
    public List<InteractionChain> getNewForks() {
        return this.newForks;
    }

    @Nonnull
    public String toString() {
        return "InteractionChain{type=" + String.valueOf((Object)this.type) + ", chainData=" + String.valueOf(this.chainData) + ", chainId=" + this.chainId + ", forkedChainId=" + String.valueOf(this.forkedChainId) + ", predicted=" + this.predicted + ", context=" + String.valueOf(this.context) + ", forkedChains=" + String.valueOf(this.forkedChains) + ", tempForkedChainData=" + String.valueOf(this.tempForkedChainData) + ", initialRootInteraction=" + String.valueOf(this.initialRootInteraction) + ", rootInteraction=" + String.valueOf(this.rootInteraction) + ", operationCounter=" + this.operationCounter + ", callStack=" + String.valueOf(this.callStack) + ", simulatedCallStack=" + this.simulatedCallStack + ", requiresClient=" + this.requiresClient + ", simulatedOperationCounter=" + this.simulatedOperationCounter + ", simulatedRootInteraction=" + String.valueOf(this.simulatedRootInteraction) + ", operationIndex=" + this.operationIndex + ", operationIndexOffset=" + this.operationIndexOffset + ", clientOperationIndex=" + this.clientOperationIndex + ", interactions=" + String.valueOf(this.interactions) + ", tempSyncData=" + String.valueOf(this.tempSyncData) + ", tempSyncDataOffset=" + this.tempSyncDataOffset + ", timestamp=" + this.timestamp + ", waitingForServerFinished=" + this.waitingForServerFinished + ", waitingForClientFinished=" + this.waitingForClientFinished + ", clientState=" + String.valueOf((Object)this.clientState) + ", serverState=" + String.valueOf((Object)this.serverState) + ", onCompletion=" + String.valueOf(this.onCompletion) + ", sentInitial=" + this.sentInitial + ", desynced=" + this.desynced + ", timeShift=" + this.timeShift + ", firstRun=" + this.firstRun + ", skipChainOnClick=" + this.skipChainOnClick + "}";
    }

    static class TempChain
    implements ChainSyncStorage {
        final Long2ObjectMap<TempChain> tempForkedChainData = new Long2ObjectOpenHashMap<TempChain>();
        final List<InteractionSyncData> tempSyncData = new ObjectArrayList<InteractionSyncData>();
        ForkedChainId forkedChainId;
        InteractionState clientState = InteractionState.NotFinished;
        ForkedChainId baseForkedChainId;
        InteractionChainData chainData;

        TempChain() {
        }

        @Nonnull
        public TempChain getOrCreateTempForkedChain(@Nonnull ForkedChainId chainId) {
            return this.tempForkedChainData.computeIfAbsent(InteractionChain.forkedIdToIndex(chainId), i -> new TempChain());
        }

        @Override
        public InteractionState getClientState() {
            return this.clientState;
        }

        @Override
        public void setClientState(InteractionState state) {
            this.clientState = state;
        }

        @Override
        @Nullable
        public InteractionEntry getInteraction(int index) {
            return null;
        }

        @Override
        public void putInteractionSyncData(int index, InteractionSyncData data) {
            if (index < this.tempSyncData.size()) {
                this.tempSyncData.set(index, data);
            } else if (index == this.tempSyncData.size()) {
                this.tempSyncData.add(data);
            } else {
                throw new IllegalArgumentException("Temp sync data sent out of order: " + index + " " + this.tempSyncData.size());
            }
        }

        @Override
        public void updateSyncPosition(int index) {
        }

        @Override
        public boolean isSyncDataOutOfOrder(int index) {
            return index > this.tempSyncData.size();
        }

        @Override
        public void syncFork(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionManager manager, @Nonnull SyncInteractionChain packet) {
            ForkedChainId baseId = packet.forkedId;
            while (baseId.forkedId != null) {
                baseId = baseId.forkedId;
            }
            TempChain temp = this.getOrCreateTempForkedChain(baseId);
            temp.setForkedChainId(packet.forkedId);
            temp.setBaseForkedChainId(baseId);
            temp.setChainData(packet.data);
            manager.sync(ref, temp, packet);
        }

        @Override
        public void clearInteractionSyncData(int index) {
            for (int end = this.tempSyncData.size() - 1; end >= index; --end) {
                this.tempSyncData.remove(end);
            }
        }

        public InteractionChainData getChainData() {
            return this.chainData;
        }

        public void setChainData(InteractionChainData chainData) {
            this.chainData = chainData;
        }

        public ForkedChainId getBaseForkedChainId() {
            return this.baseForkedChainId;
        }

        public void setBaseForkedChainId(ForkedChainId baseForkedChainId) {
            this.baseForkedChainId = baseForkedChainId;
        }

        public void setForkedChainId(ForkedChainId forkedChainId) {
            this.forkedChainId = forkedChainId;
        }

        @Nonnull
        public String toString() {
            return "TempChain{tempForkedChainData=" + String.valueOf(this.tempForkedChainData) + ", tempSyncData=" + String.valueOf(this.tempSyncData) + ", clientState=" + String.valueOf((Object)this.clientState) + "}";
        }
    }

    private record CallState(RootInteraction rootInteraction, int operationCounter) {
    }
}

