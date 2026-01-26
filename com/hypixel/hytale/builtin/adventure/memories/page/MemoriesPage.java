/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.memories.page;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesGameplayConfig;
import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
import com.hypixel.hytale.builtin.adventure.memories.memories.npc.NPCMemory;
import com.hypixel.hytale.builtin.adventure.memories.page.MemoriesUnlockedPage;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MemoriesPage
extends InteractiveCustomUIPage<PageEventData> {
    @Nullable
    private String currentCategory;
    @Nullable
    private Memory selectedMemory;
    private Vector3d recordMemoriesParticlesPosition;

    public MemoriesPage(@Nonnull PlayerRef playerRef, BlockPosition blockPosition) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, PageEventData.CODEC);
        this.recordMemoriesParticlesPosition = new Vector3d(blockPosition.x, blockPosition.y, blockPosition.z);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
        if (this.currentCategory == null) {
            commandBuilder.append("Pages/Memories/MemoriesCategoryPanel.ui");
            Map<String, Set<Memory>> allMemories = memoriesPlugin.getAllMemories();
            Set<Memory> recordedMemories = memoriesPlugin.getRecordedMemories();
            int totalMemories = 0;
            for (Set<Memory> value : allMemories.values()) {
                totalMemories += value.size();
            }
            commandBuilder.set("#MemoriesProgressBar.Value", (float)recordedMemories.size() / (float)totalMemories);
            commandBuilder.set("#MemoriesProgressBarTexture.Value", (float)recordedMemories.size() / (float)totalMemories);
            commandBuilder.set("#TotalCollected.Text", String.valueOf(recordedMemories.size()));
            commandBuilder.set("#MemoriesTotal.Text", String.valueOf(totalMemories));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemoriesInfoButton", new EventData().append("Action", PageAction.MemoriesInfo));
            GameplayConfig gameplayConfig = store.getExternalData().getWorld().getGameplayConfig();
            PlayerMemories playerMemories = store.getComponent(ref, PlayerMemories.getComponentType());
            int i = 0;
            for (Map.Entry<String, Set<Memory>> entry : allMemories.entrySet()) {
                boolean isCategoryComplete;
                String category = entry.getKey();
                Set<Memory> memoriesInCategory = entry.getValue();
                String selector = "#IconList[" + i++ + "] ";
                int recordedMemoriesCount = 0;
                for (Memory memory : memoriesInCategory) {
                    if (!recordedMemories.contains(memory)) continue;
                    ++recordedMemoriesCount;
                }
                commandBuilder.append("#IconList", "Pages/Memories/MemoriesCategory.ui");
                commandBuilder.set(selector + "#Button.Text", Message.translation("server.memories.categories." + category + ".title"));
                commandBuilder.set(selector + "#CurrentMemoryCountNotComplete.Text", String.valueOf(recordedMemoriesCount));
                commandBuilder.set(selector + "#CurrentMemoryCountComplete.Text", String.valueOf(recordedMemoriesCount));
                commandBuilder.set(selector + "#TotalMemoryCountNotComplete.Text", String.valueOf(memoriesInCategory.size()));
                commandBuilder.set(selector + "#TotalMemoryCountComplete.Text", String.valueOf(memoriesInCategory.size()));
                boolean bl = isCategoryComplete = recordedMemoriesCount == memoriesInCategory.size();
                if (isCategoryComplete) {
                    commandBuilder.set(selector + "#CategoryIcon.Background", "Pages/Memories/categories/" + category + "Complete.png");
                    commandBuilder.set(selector + "#CompleteCategoryBackground.Visible", true);
                    commandBuilder.set(selector + "#CompleteCategoryCounter.Visible", true);
                } else {
                    commandBuilder.set(selector + "#CategoryIcon.Background", "Pages/Memories/categories/" + category + ".png");
                    commandBuilder.set(selector + "#NotCompleteCategoryCounter.Visible", true);
                }
                if (playerMemories != null) {
                    Set<Memory> newMemories = playerMemories.getRecordedMemories();
                    for (Memory memory : memoriesInCategory) {
                        if (!newMemories.contains(memory)) continue;
                        commandBuilder.set(selector + "#NewMemoryIndicator.Visible", true);
                        break;
                    }
                }
                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector + "#Button", new EventData().append("Action", PageAction.ViewCategory).append("Category", category));
            }
            commandBuilder.set("#RecordButton.Visible", true);
            commandBuilder.set("#RecordButton.Disabled", playerMemories == null || !playerMemories.hasMemories());
            MemoriesPage.buildChestMarkers(commandBuilder, gameplayConfig, totalMemories);
            if (playerMemories != null && playerMemories.hasMemories()) {
                commandBuilder.set("#RecordButton.Text", Message.translation("server.memories.general.recordNum").param("count", playerMemories.getRecordedMemories().size()));
            }
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#RecordButton", new EventData().append("Action", PageAction.Record));
        } else {
            commandBuilder.append("Pages/Memories/MemoriesPanel.ui");
            Set<Memory> memoriesSet = memoriesPlugin.getAllMemories().get(this.currentCategory);
            ObjectArrayList<Memory> memories = new ObjectArrayList<Memory>(memoriesSet);
            memories.sort(Comparator.comparing(Memory::getTitle));
            Set<Memory> recordedMemories = memoriesPlugin.getRecordedMemories();
            int recordedMemoriesCount = 0;
            for (Memory memory : memories) {
                if (!recordedMemories.contains(memory)) continue;
                ++recordedMemoriesCount;
            }
            commandBuilder.set("#CategoryTitle.Text", Message.translation("server.memories.categories." + this.currentCategory + ".title"));
            commandBuilder.set("#CategoryCount.Text", recordedMemoriesCount + "/" + memories.size());
            for (int i = 0; i < memories.size(); ++i) {
                String buttonSelector;
                Memory memory;
                memory = memories.get(i);
                String selector = "#IconList[" + i + "] ";
                commandBuilder.append("#IconList", "Pages/Memories/Memory.ui");
                boolean isDiscovered = recordedMemories.contains(memory);
                boolean isSelected = this.selectedMemory != null && this.selectedMemory.equals(memory);
                String string = buttonSelector = isSelected ? selector + "#ButtonSelected" : selector + "#ButtonNotSelected";
                if (isDiscovered) {
                    commandBuilder.set(buttonSelector + ".Visible", true);
                    commandBuilder.set(buttonSelector + ".TooltipText", memory.getTooltipText());
                    commandBuilder.setNull(buttonSelector + ".Background");
                    String iconPath = memory.getIconPath();
                    if (iconPath != null && !iconPath.isEmpty()) {
                        commandBuilder.set(selector + "#Icon.AssetPath", iconPath);
                    }
                    eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonSelector, new EventData().append("Action", PageAction.SelectMemory).append("MemoryId", memory.getId()));
                    continue;
                }
                commandBuilder.set(selector + "#EmptyBackground.Visible", true);
            }
            if (this.selectedMemory != null && recordedMemories.contains(this.selectedMemory)) {
                MemoriesPage.updateMemoryDetailsPanel(commandBuilder, this.selectedMemory);
            }
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", new EventData().append("Action", PageAction.Back));
        }
    }

    private static void buildChestMarkers(@Nonnull UICommandBuilder commandBuilder, @Nonnull GameplayConfig gameplayConfig, int totalMemories) {
        MemoriesGameplayConfig memoriesConfig = MemoriesGameplayConfig.get(gameplayConfig);
        if (memoriesConfig == null) {
            return;
        }
        int[] memoriesAmountPerLevel = memoriesConfig.getMemoriesAmountPerLevel();
        if (memoriesAmountPerLevel == null || memoriesAmountPerLevel.length <= 1) {
            return;
        }
        MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
        int recordedMemoriesCount = memoriesPlugin.getRecordedMemories().size();
        int PROGRESS_BAR_PADDING = 18;
        int PROGRESS_BAR_WIDTH = 1018;
        int CHEST_POSITION_AREA = 1000;
        for (int i = 0; i < memoriesAmountPerLevel.length; ++i) {
            int memoryAmount = memoriesAmountPerLevel[i];
            boolean hasReachedLevel = recordedMemoriesCount >= memoryAmount;
            String selector = "#ChestMarkers[" + i + "]";
            Anchor anchor = new Anchor();
            int left = memoryAmount * 1000 / totalMemories;
            commandBuilder.append("#ChestMarkers", "Pages/Memories/ChestMarker.ui");
            anchor.setLeft(Value.of(left));
            commandBuilder.setObject(selector + ".Anchor", anchor);
            Message rewardsMessage = Message.translation("server.memories.general.chestActive.level" + (i + 1) + ".rewards");
            if (hasReachedLevel) {
                Message memoriesUnlockedMessage = Message.translation("server.memories.general.chestActive.tooltipText").param("count", memoryAmount);
                Message activeTooltipMessage = memoriesUnlockedMessage.insert("\n").insert(rewardsMessage);
                commandBuilder.set(selector + " #Arrow.Visible", true);
                commandBuilder.set(selector + " #ChestActive.Visible", true);
                commandBuilder.set(selector + " #ChestActive.TooltipTextSpans", activeTooltipMessage);
                continue;
            }
            commandBuilder.set(selector + " #ChestDisabled.Visible", true);
            Message memoriesToUnlockMessage = Message.translation("server.memories.general.chestLocked.tooltipText").param("count", memoryAmount);
            Message disabledTooltipMessage = memoriesToUnlockMessage.insert("\n").insert(rewardsMessage);
            commandBuilder.set(selector + " #ChestDisabled.TooltipTextSpans", disabledTooltipMessage);
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert (player != null);
        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        assert (transformComponent != null);
        switch (data.action.ordinal()) {
            case 0: {
                PlayerMemories playerMemories = store.getComponent(ref, PlayerMemories.getComponentType());
                if (playerMemories == null) {
                    this.sendUpdate();
                    return;
                }
                if (!MemoriesPlugin.get().recordPlayerMemories(playerMemories)) {
                    this.rebuild();
                    return;
                }
                MemoriesGameplayConfig memoriesGameplayConfig = MemoriesGameplayConfig.get(store.getExternalData().getWorld().getGameplayConfig());
                if (memoriesGameplayConfig != null) {
                    SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = store.getResource(EntityModule.get().getPlayerSpatialResourceType());
                    ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
                    playerSpatialResource.getSpatialStructure().collect(this.recordMemoriesParticlesPosition, 75.0, results);
                    ParticleUtil.spawnParticleEffect(memoriesGameplayConfig.getMemoriesRecordParticles(), this.recordMemoriesParticlesPosition, results, store);
                }
                this.close();
                break;
            }
            case 1: {
                this.currentCategory = data.category;
                this.selectedMemory = null;
                this.rebuild();
                break;
            }
            case 2: {
                this.currentCategory = null;
                this.selectedMemory = null;
                this.rebuild();
                break;
            }
            case 3: {
                BlockPosition blockPostion = new BlockPosition((int)this.recordMemoriesParticlesPosition.x, (int)this.recordMemoriesParticlesPosition.y, (int)this.recordMemoriesParticlesPosition.z);
                player.getPageManager().openCustomPage(ref, store, new MemoriesUnlockedPage(this.playerRef, blockPostion));
                break;
            }
            case 4: {
                int newIndex;
                int previousIndex;
                if (data.memoryId == null || this.currentCategory == null) {
                    return;
                }
                Set<Memory> memoriesSet = MemoriesPlugin.get().getAllMemories().get(this.currentCategory);
                if (memoriesSet == null) {
                    return;
                }
                ObjectArrayList<Memory> memories = new ObjectArrayList<Memory>(memoriesSet);
                memories.sort(Comparator.comparing(Memory::getTitle));
                Set<Memory> recordedMemories = MemoriesPlugin.get().getRecordedMemories();
                if (recordedMemories == null) {
                    return;
                }
                Memory newSelection = null;
                for (Memory memory : recordedMemories) {
                    if (!memory.getId().equals(data.memoryId)) continue;
                    newSelection = memory;
                    break;
                }
                if (newSelection == null) {
                    return;
                }
                if (!memories.contains(newSelection) || newSelection.equals(this.selectedMemory)) {
                    return;
                }
                UICommandBuilder commandBuilder = new UICommandBuilder();
                if (this.selectedMemory != null && recordedMemories.contains(this.selectedMemory) && (previousIndex = memories.indexOf(this.selectedMemory)) >= 0) {
                    MemoriesPage.updateMemoryButtonSelection(commandBuilder, previousIndex, this.selectedMemory, false);
                }
                if ((newIndex = memories.indexOf(newSelection)) >= 0) {
                    MemoriesPage.updateMemoryButtonSelection(commandBuilder, newIndex, newSelection, true);
                }
                MemoriesPage.updateMemoryDetailsPanel(commandBuilder, newSelection);
                this.selectedMemory = newSelection;
                this.sendUpdate(commandBuilder);
            }
        }
    }

    private static void updateMemoryButtonSelection(@Nonnull UICommandBuilder commandBuilder, int index, @Nonnull Memory memory, boolean isSelected) {
        String selector = "#IconList[" + index + "] ";
        if (isSelected) {
            commandBuilder.set(selector + "#ButtonNotSelected.Visible", false);
            commandBuilder.set(selector + "#ButtonSelected.Visible", true);
            commandBuilder.setNull(selector + "#ButtonSelected.Background");
            commandBuilder.set(selector + "#ButtonSelected.TooltipText", memory.getTooltipText());
            return;
        }
        commandBuilder.set(selector + "#ButtonSelected.Visible", false);
        commandBuilder.set(selector + "#ButtonNotSelected.Visible", true);
        commandBuilder.setNull(selector + "#ButtonNotSelected.Background");
        commandBuilder.set(selector + "#ButtonNotSelected.TooltipText", memory.getTooltipText());
    }

    private static void updateMemoryDetailsPanel(@Nonnull UICommandBuilder commandBuilder, @Nonnull Memory memory) {
        String iconPath;
        commandBuilder.set("#MemoryName.Text", Message.translation(memory.getTitle()));
        commandBuilder.set("#MemoryTimeLocation.Text", "");
        if (memory instanceof NPCMemory) {
            NPCMemory npcMemory = (NPCMemory)memory;
            Message locationNameKey = npcMemory.getLocationMessage();
            long capturedTimestamp = npcMemory.getCapturedTimestamp();
            String timeString = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault()).format(Instant.ofEpochMilli(capturedTimestamp).atZone(ZoneOffset.UTC));
            Message memoryLocationTimeText = Message.translation("server.memories.general.foundIn").param("location", locationNameKey).param("time", timeString);
            commandBuilder.set("#MemoryTimeLocation.TextSpans", memoryLocationTimeText);
        }
        if ((iconPath = memory.getIconPath()) != null && !iconPath.isEmpty()) {
            commandBuilder.set("#MemoryIcon.AssetPath", iconPath);
            return;
        }
        commandBuilder.setNull("#MemoryIcon.AssetPath");
    }

    public static class PageEventData {
        public static final String KEY_ACTION = "Action";
        public static final String KEY_CATEGORY = "Category";
        public static final String KEY_MEMORY_ID = "MemoryId";
        public static final BuilderCodec<PageEventData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageEventData.class, PageEventData::new).append(new KeyedCodec<PageAction>("Action", PageAction.CODEC), (pageEventData, pageAction) -> {
            pageEventData.action = pageAction;
        }, pageEventData -> pageEventData.action).add()).append(new KeyedCodec<String>("Category", Codec.STRING), (pageEventData, s) -> {
            pageEventData.category = s;
        }, pageEventData -> pageEventData.category).add()).append(new KeyedCodec<String>("MemoryId", Codec.STRING), (pageEventData, id) -> {
            pageEventData.memoryId = id;
        }, pageEventData -> pageEventData.memoryId).add()).build();
        public PageAction action;
        public String category;
        public String memoryId;
    }

    public static enum PageAction {
        Record,
        ViewCategory,
        Back,
        MemoriesInfo,
        SelectMemory;

        public static final Codec<PageAction> CODEC;

        static {
            CODEC = new EnumCodec<PageAction>(PageAction.class);
        }
    }
}

