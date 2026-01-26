/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.builtin.buildertools.BlockColorIndex;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPacketHandler;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsSystems;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsUserData;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsUserDataSystem;
import com.hypixel.hytale.builtin.buildertools.EditOperation;
import com.hypixel.hytale.builtin.buildertools.PrefabCopyException;
import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
import com.hypixel.hytale.builtin.buildertools.commands.ClearBlocksCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ClearEditHistory;
import com.hypixel.hytale.builtin.buildertools.commands.ClearEntitiesCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ContractSelectionCommand;
import com.hypixel.hytale.builtin.buildertools.commands.CopyCommand;
import com.hypixel.hytale.builtin.buildertools.commands.CutCommand;
import com.hypixel.hytale.builtin.buildertools.commands.DeselectCommand;
import com.hypixel.hytale.builtin.buildertools.commands.EditLineCommand;
import com.hypixel.hytale.builtin.buildertools.commands.EnvironmentCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ExpandCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ExtendFaceCommand;
import com.hypixel.hytale.builtin.buildertools.commands.FillCommand;
import com.hypixel.hytale.builtin.buildertools.commands.FlipCommand;
import com.hypixel.hytale.builtin.buildertools.commands.GlobalMaskCommand;
import com.hypixel.hytale.builtin.buildertools.commands.HollowCommand;
import com.hypixel.hytale.builtin.buildertools.commands.HotbarSwitchCommand;
import com.hypixel.hytale.builtin.buildertools.commands.MoveCommand;
import com.hypixel.hytale.builtin.buildertools.commands.PasteCommand;
import com.hypixel.hytale.builtin.buildertools.commands.Pos1Command;
import com.hypixel.hytale.builtin.buildertools.commands.Pos2Command;
import com.hypixel.hytale.builtin.buildertools.commands.PrefabCommand;
import com.hypixel.hytale.builtin.buildertools.commands.RedoCommand;
import com.hypixel.hytale.builtin.buildertools.commands.RepairFillersCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ReplaceCommand;
import com.hypixel.hytale.builtin.buildertools.commands.RotateCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SelectChunkCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SelectChunkSectionCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SelectionHistoryCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SetCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SetToolHistorySizeCommand;
import com.hypixel.hytale.builtin.buildertools.commands.ShiftCommand;
import com.hypixel.hytale.builtin.buildertools.commands.StackCommand;
import com.hypixel.hytale.builtin.buildertools.commands.SubmergeCommand;
import com.hypixel.hytale.builtin.buildertools.commands.TintCommand;
import com.hypixel.hytale.builtin.buildertools.commands.UndoCommand;
import com.hypixel.hytale.builtin.buildertools.commands.UpdateSelectionCommand;
import com.hypixel.hytale.builtin.buildertools.commands.WallsCommand;
import com.hypixel.hytale.builtin.buildertools.imageimport.ImageImportCommand;
import com.hypixel.hytale.builtin.buildertools.interactions.PickupItemInteraction;
import com.hypixel.hytale.builtin.buildertools.objimport.ObjImportCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabAnchor;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabDirtySystems;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSession;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditSessionManager;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabEditorCreationSettings;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabMarkerProvider;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabSelectionInteraction;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.PrefabSetAnchorInteraction;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditCommand;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigCommandExecutor;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfigEditStore;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.ScriptedBrushAsset;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.commands.BrushConfigCommand;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.global.DebugBrushOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.global.DisableHoldInteractionOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.global.IgnoreExistingBrushDataOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.BlockPatternOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.BreakpointOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ClearOperationMaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.DeleteOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.EchoOnceOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.EchoOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ErodeOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.HeightmapLayerOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.LayerOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.LiftOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.LoadIntFromToolArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.LoadMaterialFromToolArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.MaterialOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.MeltOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.PastePrefabOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ReplaceOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.RunCommandOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.SetDensity;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.SetOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ShapeOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.SmoothOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.dimensions.DimensionsOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.dimensions.RandomizeDimensionsOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.ExitOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpIfBlockTypeOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpIfClickType;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpIfCompareOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpIfStringMatchOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpIfToolArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpToIndexOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.JumpToRandomIndex;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.loops.CircleOffsetAndLoopOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.loops.CircleOffsetFromArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.loops.LoadLoopFromToolArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.loops.LoopOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.flowcontrol.loops.LoopRandomOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.AppendMaskFromToolArgOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.AppendMaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.HistoryMaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.MaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.UseBrushMaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.masks.UseOperationMaskOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.offsets.OffsetOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.offsets.RandomOffsetOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload.LoadBrushConfigOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload.LoadOperationsFromAssetOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload.PersistentDataOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload.SaveBrushConfigOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.saveandload.SaveIndexOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.BrushOperation;
import com.hypixel.hytale.builtin.buildertools.snapshot.BlockSelectionSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.ClipboardBoundsSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.ClipboardContentsSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityAddSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityRemoveSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityTransformSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.SelectionSnapshot;
import com.hypixel.hytale.builtin.buildertools.tooloperations.PaintOperation;
import com.hypixel.hytale.builtin.buildertools.tooloperations.ToolOperation;
import com.hypixel.hytale.builtin.buildertools.utils.Material;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.CompletableFutureUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.block.BlockCubeUtil;
import com.hypixel.hytale.math.block.BlockSphereUtil;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.iterator.LineIterator;
import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.math.vector.VectorBoxUtil;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricResults;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.DrawType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.buildertools.BrushOrigin;
import com.hypixel.hytale.protocol.packets.buildertools.BrushShape;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgUpdate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.protocol.packets.interface_.BlockChange;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderToolData;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.BlockArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.BoolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.BrushOriginArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.BrushShapeArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.FloatArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.IntArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.MaskArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.OptionArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.StringArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArg;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.args.ToolArgException;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.modules.prefabspawner.PrefabSpawnerState;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.prefab.PrefabLoadException;
import com.hypixel.hytale.server.core.prefab.PrefabSaveException;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.event.PrefabPasteEvent;
import com.hypixel.hytale.server.core.prefab.selection.SelectionManager;
import com.hypixel.hytale.server.core.prefab.selection.SelectionProvider;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockMask;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.prefab.selection.standard.FeedbackConsumer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.accessor.OverridableChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.AbstractCachedAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import com.hypixel.hytale.server.core.util.MessageUtil;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.core.util.PrefabUtil;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
import com.hypixel.hytale.sneakythrow.consumer.ThrowableConsumer;
import com.hypixel.hytale.sneakythrow.consumer.ThrowableTriConsumer;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderToolsPlugin
extends JavaPlugin
implements SelectionProvider,
MetricProvider {
    public static final String EDITOR_BLOCK = "Editor_Block";
    public static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
    public static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";
    protected static final float SPHERE_SIZE = 1.0f;
    private static final FeedbackConsumer FEEDBACK_CONSUMER = BuilderToolsPlugin::sendFeedback;
    private static final MetricsRegistry<BuilderToolsPlugin> PLUGIN_METRICS_REGISTRY = new MetricsRegistry<BuilderToolsPlugin>().register("BuilderStates", plugin -> (BuilderState[])plugin.builderStates.values().toArray(BuilderState[]::new), new ArrayCodec<BuilderState>(BuilderState.STATE_METRICS_REGISTRY, BuilderState[]::new));
    private static final long RETAIN_BUILDER_STATE_TIMESTAMP = Long.MAX_VALUE;
    private static final long MIN_CLEANUP_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1L);
    private final Map<UUID, BuilderState> builderStates = new ConcurrentHashMap<UUID, BuilderState>();
    private PrefabEditSessionManager prefabEditSessionManager;
    private final BlockColorIndex blockColorIndex = new BlockColorIndex();
    private static BuilderToolsPlugin instance;
    private int historyCount;
    private long toolExpireTimeNanos;
    @Nullable
    private ScheduledFuture<?> cleanupTask;
    private ComponentType<EntityStore, BuilderToolsUserData> userDataComponentType;
    private ComponentType<EntityStore, PrefabAnchor> prefabAnchorComponentType;
    private final Int2ObjectConcurrentHashMap<ConcurrentHashMap<UUID, UUID>> pastedPrefabPathUUIDMap = new Int2ObjectConcurrentHashMap();
    private final Int2ObjectConcurrentHashMap<ConcurrentHashMap<String, UUID>> pastedPrefabPathNameToUUIDMap = new Int2ObjectConcurrentHashMap();
    private static final float SMOOTHING_KERNEL_TOTAL = 27.0f;
    private static final int[] SMOOTHING_KERNEL;
    private final Config<BuilderToolsConfig> config = this.withConfig("BuilderToolsModule", BuilderToolsConfig.CODEC);
    private ResourceType<EntityStore, PrefabEditSession> prefabEditSessionResourceType;

    public BuilderToolsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        this.getLogger().setLevel(Level.FINE);
    }

    public static BuilderToolsPlugin get() {
        return instance;
    }

    @Nonnull
    public BlockColorIndex getBlockColorIndex() {
        return this.blockColorIndex;
    }

    public static void invalidateWorldMapForSelection(@Nonnull BlockSelection selection, @Nonnull World world) {
        BuilderToolsPlugin.invalidateWorldMapForBounds(selection.getSelectionMin(), selection.getSelectionMax(), world);
    }

    static void invalidateWorldMapForBounds(@Nonnull Vector3i min, @Nonnull Vector3i max, @Nonnull World world) {
        LongOpenHashSet affectedChunks = new LongOpenHashSet();
        int minChunkX = min.x >> 5;
        int maxChunkX = max.x >> 5;
        int minChunkZ = min.z >> 5;
        int maxChunkZ = max.z >> 5;
        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                affectedChunks.add(ChunkUtil.indexChunk(cx, cz));
            }
        }
        world.getWorldMapManager().clearImagesInChunks(affectedChunks);
        for (Player worldPlayer : world.getPlayers()) {
            worldPlayer.getWorldMapTracker().clearChunks(affectedChunks);
        }
    }

    @Nonnull
    public static BuilderState getState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        return instance.getBuilderState(player, playerRef);
    }

    public static <T extends Throwable> void addToQueue(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderState, ComponentAccessor<EntityStore>, T> task) {
        BuilderToolsPlugin.getState(player, playerRef).addToQueue(task);
    }

    @Override
    protected void setup() {
        CommandRegistry commandRegistry = this.getCommandRegistry();
        EventRegistry eventRegistry = this.getEventRegistry();
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        ServerManager.get().registerSubPacketHandlers(BuilderToolsPacketHandler::new);
        eventRegistry.register(PlayerConnectEvent.class, this::onPlayerConnect);
        eventRegistry.register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        eventRegistry.registerGlobal(AddWorldEvent.class, event -> event.getWorld().getWorldMapManager().addMarkerProvider("prefabs", PrefabMarkerProvider.INSTANCE));
        entityStoreRegistry.registerSystem(new PrefabPasteEventSystem(this));
        entityStoreRegistry.registerSystem(new PrefabDirtySystems.BlockBreakDirtySystem());
        entityStoreRegistry.registerSystem(new PrefabDirtySystems.BlockPlaceDirtySystem());
        this.prefabAnchorComponentType = entityStoreRegistry.registerComponent(PrefabAnchor.class, "PrefabAnchor", PrefabAnchor.CODEC);
        Interaction.CODEC.register("PrefabSelectionInteraction", PrefabSelectionInteraction.class, PrefabSelectionInteraction.CODEC);
        Interaction.CODEC.register("PrefabSetAnchorInteraction", PrefabSetAnchorInteraction.class, PrefabSetAnchorInteraction.CODEC);
        Interaction.CODEC.register("PickupItem", PickupItemInteraction.class, PickupItemInteraction.CODEC);
        Interaction.getAssetStore().loadAssets("Hytale:Hytale", List.of(new PickupItemInteraction("*PickupItem")));
        RootInteraction.getAssetStore().loadAssets("Hytale:Hytale", List.of(PickupItemInteraction.DEFAULT_ROOT));
        this.prefabEditSessionManager = new PrefabEditSessionManager(this);
        this.prefabEditSessionResourceType = entityStoreRegistry.registerResource(PrefabEditSession.class, "PrefabEditSession", PrefabEditSession.CODEC);
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(PrefabEditorCreationSettings.class, new DefaultAssetMap()).setPath("PrefabEditorCreationSettings")).setKeyFunction(PrefabEditorCreationSettings::getId)).setCodec((AssetCodec)PrefabEditorCreationSettings.CODEC)).build());
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(ScriptedBrushAsset.class, new DefaultAssetMap()).setPath("ScriptedBrushes")).setKeyFunction(ScriptedBrushAsset::getId)).setCodec((AssetCodec)ScriptedBrushAsset.CODEC)).build());
        commandRegistry.registerCommand(new ClearBlocksCommand());
        commandRegistry.registerCommand(new ClearEntitiesCommand());
        commandRegistry.registerCommand(new ClearEditHistory());
        commandRegistry.registerCommand(new ContractSelectionCommand());
        commandRegistry.registerCommand(new CopyCommand());
        commandRegistry.registerCommand(new DeselectCommand());
        commandRegistry.registerCommand(new CutCommand());
        commandRegistry.registerCommand(new EditLineCommand());
        commandRegistry.registerCommand(new EnvironmentCommand());
        commandRegistry.registerCommand(new ExpandCommand());
        commandRegistry.registerCommand(new ExtendFaceCommand());
        commandRegistry.registerCommand(new FlipCommand());
        commandRegistry.registerCommand(new MoveCommand());
        commandRegistry.registerCommand(new PasteCommand());
        commandRegistry.registerCommand(new Pos1Command());
        commandRegistry.registerCommand(new Pos2Command());
        commandRegistry.registerCommand(new PrefabCommand());
        commandRegistry.registerCommand(new RedoCommand());
        commandRegistry.registerCommand(new ReplaceCommand());
        commandRegistry.registerCommand(new RotateCommand());
        commandRegistry.registerCommand(new SelectChunkCommand());
        commandRegistry.registerCommand(new SelectChunkSectionCommand());
        commandRegistry.registerCommand(new SelectionHistoryCommand());
        commandRegistry.registerCommand(new SetCommand());
        commandRegistry.registerCommand(new ShiftCommand());
        commandRegistry.registerCommand(new StackCommand());
        commandRegistry.registerCommand(new SubmergeCommand());
        commandRegistry.registerCommand(new TintCommand());
        commandRegistry.registerCommand(new UndoCommand());
        commandRegistry.registerCommand(new UpdateSelectionCommand());
        commandRegistry.registerCommand(new GlobalMaskCommand());
        commandRegistry.registerCommand(new RepairFillersCommand());
        commandRegistry.registerCommand(new PrefabEditCommand());
        commandRegistry.registerCommand(new HotbarSwitchCommand());
        commandRegistry.registerCommand(new WallsCommand());
        commandRegistry.registerCommand(new HollowCommand());
        commandRegistry.registerCommand(new FillCommand());
        commandRegistry.registerCommand(new BrushConfigCommand());
        commandRegistry.registerCommand(new SetToolHistorySizeCommand());
        commandRegistry.registerCommand(new ObjImportCommand());
        commandRegistry.registerCommand(new ImageImportCommand());
        OpenCustomUIInteraction.registerBlockCustomPage(this, PrefabSpawnerState.PrefabSpawnerSettingsPage.class, "PrefabSpawner", PrefabSpawnerState.class, (playerRef, state) -> new PrefabSpawnerState.PrefabSpawnerSettingsPage(playerRef, (PrefabSpawnerState)state, CustomPageLifetime.CanDismissOrCloseThroughInteraction));
        SelectionManager.setSelectionProvider(this);
        ToolArg.CODEC.register("Bool", (Class<ToolArg>)BoolArg.class, (Codec<ToolArg>)BoolArg.CODEC);
        ToolArg.CODEC.register("String", (Class<ToolArg>)StringArg.class, (Codec<ToolArg>)StringArg.CODEC);
        ToolArg.CODEC.register("Int", (Class<ToolArg>)IntArg.class, (Codec<ToolArg>)IntArg.CODEC);
        ToolArg.CODEC.register("Float", (Class<ToolArg>)FloatArg.class, (Codec<ToolArg>)FloatArg.CODEC);
        ToolArg.CODEC.register("Block", (Class<ToolArg>)BlockArg.class, (Codec<ToolArg>)BlockArg.CODEC);
        ToolArg.CODEC.register("Mask", (Class<ToolArg>)MaskArg.class, (Codec<ToolArg>)MaskArg.CODEC);
        ToolArg.CODEC.register("BrushShape", (Class<ToolArg>)BrushShapeArg.class, (Codec<ToolArg>)BrushShapeArg.CODEC);
        ToolArg.CODEC.register("BrushOrigin", (Class<ToolArg>)BrushOriginArg.class, (Codec<ToolArg>)BrushOriginArg.CODEC);
        ToolArg.CODEC.register("Option", (Class<ToolArg>)OptionArg.class, (Codec<ToolArg>)OptionArg.CODEC);
        this.registerBrushOperations();
        this.userDataComponentType = entityStoreRegistry.registerComponent(BuilderToolsUserData.class, "BuilderTools", BuilderToolsUserData.CODEC);
        entityStoreRegistry.registerSystem(new BuilderToolsSystems.EnsureBuilderTools());
        entityStoreRegistry.registerSystem(new BuilderToolsUserDataSystem());
    }

    private void registerBrushOperations() {
        BrushOperation.OPERATION_CODEC.register("dimensions", (Class<BrushOperation>)DimensionsOperation.class, (Codec<BrushOperation>)DimensionsOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("randomdimensions", (Class<BrushOperation>)RandomizeDimensionsOperation.class, (Codec<BrushOperation>)RandomizeDimensionsOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("runcommand", (Class<BrushOperation>)RunCommandOperation.class, (Codec<BrushOperation>)RunCommandOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("historymask", (Class<BrushOperation>)HistoryMaskOperation.class, (Codec<BrushOperation>)HistoryMaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("mask", (Class<BrushOperation>)MaskOperation.class, (Codec<BrushOperation>)MaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("clearoperationmask", (Class<BrushOperation>)ClearOperationMaskOperation.class, (Codec<BrushOperation>)ClearOperationMaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("usebrushmask", (Class<BrushOperation>)UseBrushMaskOperation.class, (Codec<BrushOperation>)UseBrushMaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("useoperationmask", (Class<BrushOperation>)UseOperationMaskOperation.class, (Codec<BrushOperation>)UseOperationMaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("appendmask", (Class<BrushOperation>)AppendMaskOperation.class, (Codec<BrushOperation>)AppendMaskOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("appendmaskfromtoolarg", (Class<BrushOperation>)AppendMaskFromToolArgOperation.class, (Codec<BrushOperation>)AppendMaskFromToolArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("ignorebrushsettings", (Class<BrushOperation>)IgnoreExistingBrushDataOperation.class, (Codec<BrushOperation>)IgnoreExistingBrushDataOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("debug", (Class<BrushOperation>)DebugBrushOperation.class, (Codec<BrushOperation>)DebugBrushOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loop", (Class<BrushOperation>)LoopOperation.class, (Codec<BrushOperation>)LoopOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loadloop", (Class<BrushOperation>)LoadLoopFromToolArgOperation.class, (Codec<BrushOperation>)LoadLoopFromToolArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("looprandom", (Class<BrushOperation>)LoopRandomOperation.class, (Codec<BrushOperation>)LoopRandomOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loopcircle", (Class<BrushOperation>)CircleOffsetAndLoopOperation.class, (Codec<BrushOperation>)CircleOffsetAndLoopOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loopcirclefromarg", (Class<BrushOperation>)CircleOffsetFromArgOperation.class, (Codec<BrushOperation>)CircleOffsetFromArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("savebrushconfig", (Class<BrushOperation>)SaveBrushConfigOperation.class, (Codec<BrushOperation>)SaveBrushConfigOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loadbrushconfig", (Class<BrushOperation>)LoadBrushConfigOperation.class, (Codec<BrushOperation>)LoadBrushConfigOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("saveindex", (Class<BrushOperation>)SaveIndexOperation.class, (Codec<BrushOperation>)SaveIndexOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loadoperationsfromasset", (Class<BrushOperation>)LoadOperationsFromAssetOperation.class, (Codec<BrushOperation>)LoadOperationsFromAssetOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("jump", (Class<BrushOperation>)JumpToIndexOperation.class, (Codec<BrushOperation>)JumpToIndexOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("exit", (Class<BrushOperation>)ExitOperation.class, (Codec<BrushOperation>)ExitOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumprandom", (Class<BrushOperation>)JumpToRandomIndex.class, (Codec<BrushOperation>)JumpToRandomIndex.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumpifequal", (Class<BrushOperation>)JumpIfStringMatchOperation.class, (Codec<BrushOperation>)JumpIfStringMatchOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumpifclicktype", (Class<BrushOperation>)JumpIfClickType.class, (Codec<BrushOperation>)JumpIfClickType.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumpifcompare", (Class<BrushOperation>)JumpIfCompareOperation.class, (Codec<BrushOperation>)JumpIfCompareOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumpifblocktype", (Class<BrushOperation>)JumpIfBlockTypeOperation.class, (Codec<BrushOperation>)JumpIfBlockTypeOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("jumpiftoolarg", (Class<BrushOperation>)JumpIfToolArgOperation.class, (Codec<BrushOperation>)JumpIfToolArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("pattern", (Class<BrushOperation>)BlockPatternOperation.class, (Codec<BrushOperation>)BlockPatternOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loadmaterial", (Class<BrushOperation>)LoadMaterialFromToolArgOperation.class, (Codec<BrushOperation>)LoadMaterialFromToolArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("loadint", (Class<BrushOperation>)LoadIntFromToolArgOperation.class, (Codec<BrushOperation>)LoadIntFromToolArgOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("lift", (Class<BrushOperation>)LiftOperation.class, (Codec<BrushOperation>)LiftOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("density", (Class<BrushOperation>)SetDensity.class, (Codec<BrushOperation>)SetDensity.CODEC);
        BrushOperation.OPERATION_CODEC.register("set", (Class<BrushOperation>)SetOperation.class, (Codec<BrushOperation>)SetOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("smooth", (Class<BrushOperation>)SmoothOperation.class, (Codec<BrushOperation>)SmoothOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("shape", (Class<BrushOperation>)ShapeOperation.class, (Codec<BrushOperation>)ShapeOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("offset", (Class<BrushOperation>)OffsetOperation.class, (Codec<BrushOperation>)OffsetOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("layer", (Class<BrushOperation>)LayerOperation.class, (Codec<BrushOperation>)LayerOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("heightmaplayer", (Class<BrushOperation>)HeightmapLayerOperation.class, (Codec<BrushOperation>)HeightmapLayerOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("melt", (Class<BrushOperation>)MeltOperation.class, (Codec<BrushOperation>)MeltOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("material", (Class<BrushOperation>)MaterialOperation.class, (Codec<BrushOperation>)MaterialOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("delete", (Class<BrushOperation>)DeleteOperation.class, (Codec<BrushOperation>)DeleteOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("disableonhold", (Class<BrushOperation>)DisableHoldInteractionOperation.class, (Codec<BrushOperation>)DisableHoldInteractionOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("randomoffset", (Class<BrushOperation>)RandomOffsetOperation.class, (Codec<BrushOperation>)RandomOffsetOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("erode", (Class<BrushOperation>)ErodeOperation.class, (Codec<BrushOperation>)ErodeOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("persistentdata", (Class<BrushOperation>)PersistentDataOperation.class, (Codec<BrushOperation>)PersistentDataOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("pasteprefab", (Class<BrushOperation>)PastePrefabOperation.class, (Codec<BrushOperation>)PastePrefabOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("echo", (Class<BrushOperation>)EchoOperation.class, (Codec<BrushOperation>)EchoOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("echoonce", (Class<BrushOperation>)EchoOnceOperation.class, (Codec<BrushOperation>)EchoOnceOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("replace", (Class<BrushOperation>)ReplaceOperation.class, (Codec<BrushOperation>)ReplaceOperation.CODEC);
        BrushOperation.OPERATION_CODEC.register("breakpoint", (Class<BrushOperation>)BreakpointOperation.class, (Codec<BrushOperation>)BreakpointOperation.CODEC);
    }

    public ResourceType<EntityStore, PrefabEditSession> getPrefabEditSessionResourceType() {
        return this.prefabEditSessionResourceType;
    }

    @Override
    protected void start() {
        BuilderToolsConfig config = this.config.get();
        this.historyCount = config.historyCount;
        this.toolExpireTimeNanos = TimeUnit.SECONDS.toNanos(config.toolExpireTime);
        if (this.toolExpireTimeNanos > 0L) {
            long intervalNanos = Math.max(MIN_CLEANUP_INTERVAL_NANOS, this.toolExpireTimeNanos);
            this.cleanupTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(this::cleanup, intervalNanos, intervalNanos, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    protected void shutdown() {
        if (this.cleanupTask != null) {
            this.cleanupTask.cancel(false);
        }
    }

    private void cleanup() {
        long expire = System.nanoTime() - this.toolExpireTimeNanos;
        Iterator<Map.Entry<UUID, BuilderState>> iterator = this.builderStates.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, BuilderState> entry = iterator.next();
            BuilderState state = entry.getValue();
            if (state.timestamp >= expire) continue;
            iterator.remove();
            this.getLogger().at(Level.FINE).log("[%s] Expired and removed builder tool", state.getDisplayName());
        }
    }

    public void setToolHistorySize(int size) {
        this.historyCount = size;
    }

    private void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        this.retainBuilderState(event.getPlayer(), event.getPlayerRef());
    }

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        this.releaseBuilderState(event.getPlayerRef().getUuid());
    }

    public void onToolArgUpdate(@Nonnull PlayerRef playerRef, @Nonnull Player player, @Nonnull BuilderToolArgUpdate packet) {
        ItemContainer section = player.getInventory().getSectionById(packet.section);
        ItemStack itemStack = section.getItemStack((short)packet.slot);
        if (itemStack == null) {
            MessageUtil.sendFailureReply(playerRef, packet.token, Message.translation("server.builderTools.invalidTool").param("item", "Empty"));
            return;
        }
        Item item = itemStack.getItem();
        BuilderToolData builderToolData = item.getBuilderToolData();
        if (builderToolData == null) {
            Message itemMessage = Message.translation(item.getTranslationKey());
            MessageUtil.sendFailureReply(playerRef, packet.token, Message.translation("server.builderTools.invalidTool").param("item", itemMessage));
            return;
        }
        BuilderTool tool = builderToolData.getTools()[0];
        try {
            ItemStack updatedItemStack = tool.updateArgMetadata(itemStack, packet.group, packet.id, packet.value);
            section.setItemStackForSlot((short)packet.slot, updatedItemStack);
            MessageUtil.sendSuccessReply(playerRef, packet.token);
        }
        catch (ToolArgException e) {
            MessageUtil.sendFailureReply(playerRef, packet.token, e.getTranslationMessage());
        }
        catch (IllegalArgumentException e) {
            MessageUtil.sendFailureReply(playerRef, packet.token, Message.translation("server.builderTools.toolArgParseError").param("arg", packet.id).param("value", packet.value));
        }
    }

    @Nonnull
    public BuilderState getBuilderState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        return this.builderStates.computeIfAbsent(playerRef.getUuid(), k -> new BuilderState(player, playerRef));
    }

    @Nullable
    public BuilderState clearBuilderState(UUID uuid) {
        BuilderState state = this.builderStates.remove(uuid);
        if (state != null) {
            this.getLogger().at(Level.FINE).log("[%s] Removed builder tool for", state.getDisplayName());
        }
        return state;
    }

    private void retainBuilderState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
        this.builderStates.compute(playerRef.getUuid(), (id, state) -> {
            if (state == null) {
                return null;
            }
            state.retain(player, playerRef);
            this.getLogger().at(Level.FINE).log("[%s] Retained builder tool", state.getDisplayName());
            return state;
        });
    }

    private void releaseBuilderState(@Nonnull UUID uuid) {
        if (this.toolExpireTimeNanos <= 0L) {
            this.clearBuilderState(uuid);
            return;
        }
        this.builderStates.compute(uuid, (id, state) -> {
            if (state == null) {
                return null;
            }
            state.release();
            this.getLogger().at(Level.FINE).log("[%s] Marked builder tool for removal", state.getDisplayName());
            return state;
        });
    }

    public ComponentType<EntityStore, BuilderToolsUserData> getUserDataComponentType() {
        return this.userDataComponentType;
    }

    public static void sendFeedback(@Nonnull Message message, @Nullable CommandSender feedback, @Nonnull NotificationStyle notificationStyle, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (feedback instanceof Player) {
            Player playerComponent = (Player)feedback;
            Ref<EntityStore> ref = playerComponent.getReference();
            if (ref == null || !ref.isValid()) {
                return;
            }
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), message, notificationStyle);
        } else if (feedback != null) {
            feedback.sendMessage(message);
        }
    }

    public static void sendFeedback(@Nonnull String key, int total, CommandSender feedback, ComponentAccessor<EntityStore> componentAccessor) {
        if (feedback instanceof Player) {
            Player playerComponent = (Player)feedback;
            Ref<EntityStore> ref = playerComponent.getReference();
            if (ref == null || !ref.isValid()) {
                return;
            }
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.builderTools.blocksEdited").param("key", key), Message.raw(String.valueOf(total)), NotificationStyle.Success);
        } else if (feedback != null) {
            feedback.sendMessage(Message.translation("server.builderTools.blocksEdited").param("key", key));
        }
    }

    public static void sendFeedback(@Nonnull String key, int total, int num, CommandSender feedback, ComponentAccessor<EntityStore> componentAccessor) {
        if (num % 100000 == 0) {
            if (feedback instanceof Player) {
                Player playerComponent = (Player)feedback;
                Ref<EntityStore> ref = playerComponent.getReference();
                if (ref == null || !ref.isValid()) {
                    return;
                }
                PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
                assert (playerRefComponent != null);
                NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.builderTools.doneEditing").param("key", key), Message.translation("server.builderTools.blocksChanged").param("total", total), NotificationStyle.Success);
            } else if (feedback != null) {
                feedback.sendMessage(Message.translation("server.builderTools.editingStatus").param("key", key).param("percent", MathUtil.round((double)num / (double)total * 100.0, 2)).param("count", num).param("total", total));
            }
        }
    }

    @Override
    public <T extends Throwable> void computeSelectionCopy(@Nonnull Ref<EntityStore> ref, @Nonnull Player player, @Nonnull ThrowableConsumer<BlockSelection, T> task, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (this.isEnabled()) {
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            this.getBuilderState(player, playerRefComponent).computeSelectionCopy(task);
        }
    }

    @Override
    @Nonnull
    public MetricResults toMetricResults() {
        return PLUGIN_METRICS_REGISTRY.toMetricResults(this);
    }

    public ComponentType<EntityStore, PrefabAnchor> getPrefabAnchorComponentType() {
        return this.prefabAnchorComponentType;
    }

    public PrefabEditSessionManager getPrefabEditSessionManager() {
        return this.prefabEditSessionManager;
    }

    @Nullable
    @Deprecated
    public static Holder<ChunkStore> createBlockComponent(WorldChunk chunk, int x, int y, int z, int newId, int oldId, @Nullable Holder<ChunkStore> oldHolder, boolean copy) {
        String stateId;
        if (newId == 0) {
            return null;
        }
        BlockType type = BlockType.getAssetMap().getAsset(newId);
        if (type.getBlockEntity() != null) {
            return type.getBlockEntity().clone();
        }
        String string = stateId = type.getState() != null ? type.getState().getId() : null;
        if (stateId == null) {
            return null;
        }
        if (copy && oldHolder != null) {
            String currentStateId;
            BlockType currentType = BlockType.getAssetMap().getAsset(oldId);
            String string2 = currentStateId = currentType.getState() != null ? currentType.getState().getId() : null;
            if (stateId.equals(currentStateId)) {
                return oldHolder.clone();
            }
        }
        Vector3i position = new Vector3i(x, y, z);
        BlockState state = BlockStateModule.get().createBlockState(stateId, chunk, position, type);
        if (state == null) {
            return null;
        }
        return state.toHolder();
    }

    public static void forEachCopyableInSelection(@Nonnull World world, int minX, int minY, int minZ, int width, int height, int depth, @Nonnull Consumer<Ref<EntityStore>> action) {
        int encompassingWidth = width + 1;
        int encompassingHeight = height + 1;
        int encompassingDepth = depth + 1;
        if (world.isInThread()) {
            BuilderToolsPlugin.internalForEachCopyableInSelection(world, minX, minY, minZ, encompassingWidth, encompassingHeight, encompassingDepth, action);
        } else {
            CompletableFuture.runAsync(() -> BuilderToolsPlugin.internalForEachCopyableInSelection(world, minX, minY, minZ, encompassingWidth, encompassingHeight, encompassingDepth, action), world).join();
        }
    }

    private static void internalForEachCopyableInSelection(@Nonnull World world, int minX, int minY, int minZ, int encompassingWidth, int encompassingHeight, int encompassingDepth, @Nonnull Consumer<Ref<EntityStore>> action) {
        world.getEntityStore().getStore().forEachChunk(Archetype.of(PrefabCopyableComponent.getComponentType(), TransformComponent.getComponentType()), (archetypeChunk, commandBuffer) -> {
            int size = archetypeChunk.size();
            for (int index = 0; index < size; ++index) {
                Vector3d vector = archetypeChunk.getComponent(index, TransformComponent.getComponentType()).getPosition();
                Ref ref = archetypeChunk.getReferenceTo(index);
                if (!VectorBoxUtil.isInside(minX, minY, minZ, 0.0, 0.0, 0.0, encompassingWidth, encompassingHeight, encompassingDepth, vector)) continue;
                action.accept(ref);
            }
        });
    }

    private static int getNonEmptyNeighbourBlock(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
        int blockId = accessor.getBlock(x, y, z + 1);
        if (blockId > 0) {
            return blockId;
        }
        blockId = accessor.getBlock(x, y, z - 1);
        if (blockId > 0) {
            return blockId;
        }
        blockId = accessor.getBlock(x, y + 1, z);
        if (blockId > 0) {
            return blockId;
        }
        blockId = accessor.getBlock(x, y - 1, z);
        if (blockId > 0) {
            return blockId;
        }
        blockId = accessor.getBlock(x - 1, y, z);
        if (blockId > 0) {
            return blockId;
        }
        blockId = accessor.getBlock(x + 1, y, z);
        if (blockId > 0) {
            return blockId;
        }
        return 0;
    }

    @Nonnull
    public UUID getNewPathIdOnPrefabPasted(@Nullable UUID id, String name, int prefabId) {
        ConcurrentHashMap<UUID, UUID> prefabIdMap = this.pastedPrefabPathUUIDMap.get(prefabId);
        if (id != null) {
            return prefabIdMap.computeIfAbsent(id, k -> UUID.randomUUID());
        }
        ConcurrentHashMap<String, UUID> prefabNameMap = this.pastedPrefabPathNameToUUIDMap.get(prefabId);
        UUID newId = prefabNameMap.computeIfAbsent(name, k -> UUID.randomUUID());
        prefabIdMap.put(newId, newId);
        return newId;
    }

    public static boolean onPasteStart(int prefabId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PrefabPasteEvent event = new PrefabPasteEvent(prefabId, true);
        componentAccessor.invoke(event);
        return !event.isCancelled();
    }

    public void onPasteEnd(int prefabId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PrefabPasteEvent event = new PrefabPasteEvent(prefabId, false);
        componentAccessor.invoke(event);
    }

    public Int2ObjectConcurrentHashMap<ConcurrentHashMap<UUID, UUID>> getPastedPrefabPathUUIDMap() {
        return this.pastedPrefabPathUUIDMap;
    }

    static {
        SMOOTHING_KERNEL = new int[]{1, 2, 1, 2, 3, 2, 1, 2, 1, 2, 3, 2, 3, 4, 3, 2, 3, 2, 1, 2, 1, 2, 3, 2, 1, 2, 1};
    }

    public static class BuilderToolsConfig {
        public static final BuilderCodec<BuilderToolsConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BuilderToolsConfig.class, BuilderToolsConfig::new).append(new KeyedCodec<Integer>("HistoryCount", Codec.INTEGER), (o, i) -> {
            o.historyCount = i;
        }, o -> o.historyCount).documentation("The number of builder tool edit operations to keep in the undo/redo history").add()).append(new KeyedCodec<Long>("ToolExpireTime", Codec.LONG), (o, l) -> {
            o.toolExpireTime = l;
        }, o -> o.toolExpireTime).documentation("The minimum time (in seconds) that a user's builder tool data will be persisted for after they disconnect from the server. If set to zero the player's data is removed immediately on disconnect").addValidator(Validators.greaterThanOrEqual(0L)).add()).build();
        private int historyCount = 50;
        private long toolExpireTime = 3600L;
    }

    public static class BuilderState {
        private static final MetricsRegistry<BuilderState> STATE_METRICS_REGISTRY = new MetricsRegistry<BuilderState>().register("Uuid", state -> state.player.getUuid(), Codec.UUID_STRING).register("Username", BuilderState::getDisplayName, Codec.STRING).register("ActivePrefabPath", BuilderState::getActivePrefabPath, Codec.UUID_STRING).register("Selection", BuilderState::getSelection, BlockSelection.METRICS_REGISTRY).register("TaskFuture", state -> Objects.toString(state.getTaskFuture()), Codec.STRING).register("TaskCount", BuilderState::getTaskCount, Codec.INTEGER).register("UndoCount", BuilderState::getUndoCount, Codec.INTEGER).register("RedoCount", BuilderState::getRedoCount, Codec.INTEGER);
        private Player player;
        private PlayerRef playerRef;
        @Nonnull
        private final BuilderToolsUserData userData;
        private final StampedLock undoLock = new StampedLock();
        private final ObjectArrayFIFOQueue<ActionEntry> undo = new ObjectArrayFIFOQueue();
        private final ObjectArrayFIFOQueue<ActionEntry> redo = new ObjectArrayFIFOQueue();
        private final StampedLock taskLock = new StampedLock();
        private final ObjectArrayFIFOQueue<QueuedTask> tasks = new ObjectArrayFIFOQueue();
        @Nullable
        private volatile CompletableFuture<Void> taskFuture;
        private volatile long timestamp = Long.MAX_VALUE;
        private BlockSelection selection;
        private BlockMask globalMask;
        @Nonnull
        private Random random = new Random(26061984L);
        private UUID activePrefabPath;
        @Nullable
        private Path prefabListRoot;
        @Nullable
        private Path prefabListPath;
        @Nullable
        private String prefabListSearchQuery;

        private BuilderState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
            this.player = player;
            this.playerRef = playerRef;
            this.userData = BuilderToolsUserData.get(player);
        }

        private void release() {
            this.timestamp = System.nanoTime();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void retain(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
            long stamp = this.taskLock.writeLock();
            try {
                this.player = player;
                this.playerRef = playerRef;
                this.timestamp = Long.MAX_VALUE;
                if (this.selection != null) {
                    this.sendArea();
                }
            }
            finally {
                this.taskLock.unlockWrite(stamp);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public <T extends Throwable> void addToQueue(@Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderState, ComponentAccessor<EntityStore>, T> task) {
            long stamp = this.taskLock.writeLock();
            try {
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("[%s] Add task with ComponentAccessor to queue %s: %s, %s, %s", this.getDisplayName(), task, this.taskFuture, this.tasks);
                this.tasks.enqueue(new QueuedTask(task));
                if (this.taskFuture == null || this.taskFuture.isDone()) {
                    this.taskFuture = CompletableFutureUtil._catch(CompletableFuture.runAsync(this::runTask, this.player.getWorld()));
                }
            }
            finally {
                this.taskLock.unlockWrite(stamp);
            }
        }

        public <T extends Throwable> void computeSelectionCopy(@Nonnull ThrowableConsumer<BlockSelection, T> task) {
            this.addToQueue((r, b, componentAccessor) -> {
                long start = System.nanoTime();
                if (this.selection == null) {
                    this.selection = new BlockSelection();
                }
                BlockSelection oldSelection = this.selection;
                this.pushHistory(Action.COPY, BlockSelectionSnapshot.copyOf(this.selection));
                this.selection = new BlockSelection();
                this.selection.setPosition(oldSelection.getX(), oldSelection.getY(), oldSelection.getZ());
                this.selection.setSelectionArea(oldSelection.getSelectionMin(), oldSelection.getSelectionMax());
                task.accept(this.selection);
                long diff = System.nanoTime() - start;
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute computeSelectionCopy for %s which copied %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), task, this.selection.getBlockCount());
                this.sendUpdate();
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void runTask() {
            Ref<EntityStore> ref = this.player.getReference();
            if (ref == null || !ref.isValid()) {
                this.taskFuture = null;
                return;
            }
            Store<EntityStore> store = ref.getStore();
            while (true) {
                long stamp = this.taskLock.readLock();
                try {
                    if (this.tasks.isEmpty()) {
                        break;
                    }
                }
                finally {
                    this.taskLock.unlockRead(stamp);
                }
                try {
                    QueuedTask task;
                    long stamp2 = this.taskLock.writeLock();
                    try {
                        task = this.tasks.dequeue();
                        BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("[%s] Run task from queue: %s, %s, %s", this.getDisplayName(), task, this.taskFuture, this.tasks);
                    }
                    finally {
                        this.taskLock.unlockWrite(stamp2);
                    }
                    task.execute(ref, this, store);
                }
                catch (Throwable e) {
                    ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.SEVERE).withCause(e)).log("Failed to execute builder tools task for: %s", this.getDisplayName());
                }
            }
            this.taskFuture = null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int getTaskCount() {
            long stamp = this.taskLock.readLock();
            try {
                int n = this.tasks.size();
                return n;
            }
            finally {
                this.taskLock.unlockRead(stamp);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int getUndoCount() {
            long stamp = this.taskLock.readLock();
            try {
                int n = this.undo.size();
                return n;
            }
            finally {
                this.taskLock.unlockRead(stamp);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int getRedoCount() {
            long stamp = this.taskLock.readLock();
            try {
                int n = this.redo.size();
                return n;
            }
            finally {
                this.taskLock.unlockRead(stamp);
            }
        }

        public String getDisplayName() {
            return this.playerRef.getUsername();
        }

        @Nonnull
        public BuilderToolsUserData getUserData() {
            return this.userData;
        }

        @Nullable
        public CompletableFuture<Void> getTaskFuture() {
            return this.taskFuture;
        }

        public BlockSelection getSelection() {
            return this.selection;
        }

        public BlockMask getGlobalMask() {
            return this.globalMask;
        }

        @Nonnull
        public Random getRandom() {
            return this.random;
        }

        public void setSelection(@Nonnull BlockSelection selection) {
            this.selection = selection;
        }

        public void sendSelectionToClient() {
            this.sendUpdate();
        }

        private void sendErrorFeedback(@Nonnull Ref<EntityStore> ref, @Nonnull Message message, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.sendFeedback(ref, message, "CREATE_ERROR", NotificationStyle.Warning, componentAccessor);
        }

        private void sendFeedback(@Nonnull Ref<EntityStore> ref, @Nonnull Message message, @Nullable String sound, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.sendFeedback(message, componentAccessor);
            if (sound != null) {
                SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex(sound), SoundCategory.UI, componentAccessor);
            }
        }

        private void sendFeedback(@Nonnull Ref<EntityStore> ref, @Nonnull Message message, @Nullable String sound, @Nonnull NotificationStyle notificationStyle, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.sendFeedback(message, notificationStyle, componentAccessor);
            if (sound != null) {
                SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex(sound), SoundCategory.UI, componentAccessor);
            }
        }

        private void sendFeedback(@Nonnull Message message, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            BuilderToolsPlugin.sendFeedback(message, this.player, NotificationStyle.Default, componentAccessor);
        }

        private void sendFeedback(@Nonnull Message message, @Nonnull NotificationStyle notificationStyle, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            BuilderToolsPlugin.sendFeedback(message, this.player, notificationStyle, componentAccessor);
        }

        private void sendFeedback(@Nonnull String key, int total, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            BuilderToolsPlugin.sendFeedback(key, total, this.player, componentAccessor);
        }

        private void sendFeedback(@Nonnull String key, int total, int num, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            BuilderToolsPlugin.sendFeedback(key, total, num, this.player, componentAccessor);
        }

        public void setActivePrefabPath(UUID path) {
            this.activePrefabPath = path;
        }

        public UUID getActivePrefabPath() {
            return this.activePrefabPath;
        }

        @Nullable
        public Path getPrefabListRoot() {
            return this.prefabListRoot;
        }

        public void setPrefabListRoot(@Nullable Path prefabListRoot) {
            this.prefabListRoot = prefabListRoot;
        }

        @Nullable
        public Path getPrefabListPath() {
            return this.prefabListPath;
        }

        public void setPrefabListPath(@Nullable Path prefabListPath) {
            this.prefabListPath = prefabListPath;
        }

        @Nullable
        public String getPrefabListSearchQuery() {
            return this.prefabListSearchQuery;
        }

        public void setPrefabListSearchQuery(@Nullable String prefabListSearchQuery) {
            this.prefabListSearchQuery = prefabListSearchQuery;
        }

        public int edit(@Nonnull Ref<EntityStore> ref, @Nonnull BuilderToolOnUseInteraction packet, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            Vector3i currentPosition;
            Vector3i lastPosition;
            List<Vector3i> positionsToExecute;
            ToolOperation toolOperation;
            World world = componentAccessor.getExternalData().getWorld();
            UUIDComponent uuidComponent = componentAccessor.getComponent(ref, UUIDComponent.getComponentType());
            assert (uuidComponent != null);
            long start = System.nanoTime();
            try {
                toolOperation = ToolOperation.fromPacket(ref, this.player, packet, componentAccessor);
            }
            catch (Exception e) {
                this.player.sendMessage(Message.translation("server.builderTools.interaction.toolParseError").param("error", e.getMessage()));
                return 0;
            }
            PrototypePlayerBuilderToolSettings protoSettings = ToolOperation.PROTOTYPE_TOOL_SETTINGS.get(uuidComponent.getUuid());
            if (protoSettings != null && toolOperation instanceof PaintOperation) {
                BuilderTool builderTool = BuilderTool.getActiveBuilderTool(this.player);
                if (protoSettings.isLoadingBrush()) {
                    return 0;
                }
                if (builderTool != null && builderTool.getBrushConfigurationCommand() != null && !builderTool.getBrushConfigurationCommand().isEmpty()) {
                    String brushConfigId = builderTool.getBrushConfigurationCommand();
                    String loadedBrushConfig = protoSettings.getCurrentlyLoadedBrushConfigName();
                    if (loadedBrushConfig.equalsIgnoreCase(brushConfigId)) {
                        toolOperation.executeAsBrushConfig(protoSettings, packet, componentAccessor);
                    } else {
                        ScriptedBrushAsset scriptedBrush = ScriptedBrushAsset.get(brushConfigId);
                        if (scriptedBrush != null) {
                            protoSettings.setCurrentlyLoadedBrushConfigName(brushConfigId);
                            BrushConfigCommandExecutor brushConfigCommandExecutor = protoSettings.getBrushConfigCommandExecutor();
                            scriptedBrush.loadIntoExecutor(brushConfigCommandExecutor);
                            protoSettings.setUsePrototypeBrushConfigurations(false);
                            toolOperation.executeAsBrushConfig(protoSettings, packet, componentAccessor);
                        } else {
                            protoSettings.setCurrentlyLoadedBrushConfigName(brushConfigId);
                            BrushConfigCommandExecutor brushConfigCommandExecutor = protoSettings.getBrushConfigCommandExecutor();
                            brushConfigCommandExecutor.getSequentialOperations().clear();
                            brushConfigCommandExecutor.getGlobalOperations().clear();
                            protoSettings.setLoadingBrush(true);
                            CommandManager.get().handleCommand(this.player, brushConfigId).thenAccept(unused -> {
                                PrototypePlayerBuilderToolSettings protoSettingsIntl = ToolOperation.PROTOTYPE_TOOL_SETTINGS.get(uuidComponent.getUuid());
                                protoSettingsIntl.setLoadingBrush(false);
                                protoSettingsIntl.setUsePrototypeBrushConfigurations(false);
                                toolOperation.executeAsBrushConfig(protoSettingsIntl, packet, componentAccessor);
                            });
                        }
                    }
                    return 0;
                }
                if (protoSettings.usePrototypeBrushConfigurations()) {
                    toolOperation.executeAsBrushConfig(protoSettings, packet, componentAccessor);
                    return 0;
                }
            }
            if ((positionsToExecute = ToolOperation.calculateInterpolatedPositions(lastPosition = protoSettings != null && packet.isHoldDownInteraction ? protoSettings.getLastBrushPosition() : null, currentPosition = toolOperation.getPosition(), toolOperation.getBrushWidth(), toolOperation.getBrushHeight(), toolOperation.getBrushSpacing())).isEmpty()) {
                return 0;
            }
            for (Vector3i position : positionsToExecute) {
                toolOperation.executeAt(position.getX(), position.getY(), position.getZ(), componentAccessor);
            }
            if (protoSettings != null) {
                if (packet.isHoldDownInteraction) {
                    protoSettings.setLastBrushPosition(positionsToExecute.get(positionsToExecute.size() - 1));
                } else {
                    protoSettings.clearLastBrushPosition();
                }
            }
            EditOperation edit = toolOperation.getEditOperation();
            BlockSelection before = edit.getBefore();
            BlockSelection after = edit.getAfter();
            this.pushHistory(Action.EDIT, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Use Builder Tool ?/?", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            int size = after.getBlockCount();
            int interpolatedCount = positionsToExecute.size();
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute edit of %d blocks (%d positions)", diff, TimeUnit.NANOSECONDS.toMillis(diff), size, interpolatedCount);
            if (protoSettings != null && protoSettings.isShouldShowEditorSettings()) {
                this.sendFeedback("Edit", size, componentAccessor);
            }
            return size;
        }

        public void placeBrushConfig(@Nonnull Ref<EntityStore> ref, long startTime, @Nonnull BrushConfigEditStore brushConfigEditStore, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            World world = componentAccessor.getExternalData().getWorld();
            BlockSelection after = brushConfigEditStore.getAfter();
            BlockSelection before = brushConfigEditStore.getBefore();
            this.pushHistory(Action.EDIT, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Use Builder Tool ?/?", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - startTime;
            int size = after.getBlockCount();
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute edit of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
            PrototypePlayerBuilderToolSettings prototypePlayerBuilderToolSettings = ToolOperation.PROTOTYPE_TOOL_SETTINGS.get(playerRefComponent.getUuid());
            if (prototypePlayerBuilderToolSettings != null && prototypePlayerBuilderToolSettings.isShouldShowEditorSettings()) {
                this.sendFeedback("Edit", size, componentAccessor);
            }
        }

        public void flood(@Nonnull EditOperation editOperation, int x, int y, int z, int shapeWidth, int shapeHeight, @Nonnull BlockPattern pattern, int targetBlockId) {
            int halfWidth = shapeWidth / 2;
            int halfHeight = shapeHeight / 2;
            Vector3i min = new Vector3i(x - halfWidth, y - halfHeight, z - halfWidth);
            Vector3i max = new Vector3i(x + halfWidth, y + halfHeight, z + halfWidth);
            OverridableChunkAccessor accessor = editOperation.getAccessor();
            LongOpenHashSet checkedPositions = new LongOpenHashSet();
            LongArrayList floodPositions = new LongArrayList();
            floodPositions.push(BlockUtil.pack(x, y, z));
            do {
                long south;
                long north;
                long bottom;
                long top;
                long west;
                long packedPosition = floodPositions.popLong();
                checkedPositions.add(packedPosition);
                int px = BlockUtil.unpackX(packedPosition);
                int py = BlockUtil.unpackY(packedPosition);
                int pz = BlockUtil.unpackZ(packedPosition);
                int blockId = pattern.nextBlock(this.random);
                long east = BlockUtil.pack(px + 1, py, pz);
                if (this.isFloodPossible(accessor, east, min, max, blockId, targetBlockId) && !checkedPositions.contains(east)) {
                    floodPositions.push(east);
                }
                if (this.isFloodPossible(accessor, west = BlockUtil.pack(px - 1, py, pz), min, max, blockId, targetBlockId) && !checkedPositions.contains(west)) {
                    floodPositions.push(west);
                }
                if (this.isFloodPossible(accessor, top = BlockUtil.pack(px, py + 1, pz), min, max, blockId, targetBlockId) && !checkedPositions.contains(top)) {
                    floodPositions.push(top);
                }
                if (this.isFloodPossible(accessor, bottom = BlockUtil.pack(px, py - 1, pz), min, max, blockId, targetBlockId) && !checkedPositions.contains(bottom)) {
                    floodPositions.push(bottom);
                }
                if (this.isFloodPossible(accessor, north = BlockUtil.pack(px, py, pz + 1), min, max, blockId, targetBlockId) && !checkedPositions.contains(north)) {
                    floodPositions.push(north);
                }
                if (this.isFloodPossible(accessor, south = BlockUtil.pack(px, py, pz - 1), min, max, blockId, targetBlockId) && !checkedPositions.contains(south)) {
                    floodPositions.push(south);
                }
                if (!this.isFloodPossible(accessor, packedPosition, min, max, blockId, targetBlockId)) continue;
                editOperation.setBlock(px, py, pz, blockId);
            } while (!floodPositions.isEmpty());
        }

        private boolean isFloodPossible(@Nonnull ChunkAccessor accessor, long blockPosition, @Nonnull Vector3i min, @Nonnull Vector3i max, int blockId, int targetBlockId) {
            int x = BlockUtil.unpackX(blockPosition);
            int y = BlockUtil.unpackY(blockPosition);
            int z = BlockUtil.unpackZ(blockPosition);
            if (x < min.getX() || y < min.getY() || z < min.getZ() || x > max.getX() || y > max.getY() || z > max.getZ()) {
                return false;
            }
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            BlockType blockType = assetMap.getAsset(accessor.getBlock(x, y, z));
            return accessor.getBlock(x, y, z) == targetBlockId || blockType.getDrawType() != DrawType.Cube && blockType.getDrawType() != DrawType.CubeWithModel;
        }

        public boolean isAsideAir(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
            return accessor.getBlock(x + 1, y, z) <= 0 || accessor.getBlock(x - 1, y, z) <= 0 || accessor.getBlock(x, y + 1, z) <= 0 || accessor.getBlock(x, y - 1, z) <= 0 || accessor.getBlock(x, y, z + 1) <= 0 || accessor.getBlock(x, y, z - 1) <= 0;
        }

        public boolean isAsideBlock(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
            return accessor.getBlock(x, y, z) <= 0 && (accessor.getBlock(x + 1, y, z) > 0 || accessor.getBlock(x - 1, y, z) > 0 || accessor.getBlock(x, y + 1, z) > 0 || accessor.getBlock(x, y - 1, z) > 0 || accessor.getBlock(x, y, z + 1) > 0 || accessor.getBlock(x, y, z - 1) > 0);
        }

        @Nonnull
        public BlocksSampleData getBlocksSampleData(@Nonnull ChunkAccessor accessor, int x, int y, int z, int radius) {
            BlocksSampleData data = new BlocksSampleData();
            Int2IntOpenHashMap blockCounts = new Int2IntOpenHashMap();
            for (int ix = x - radius; ix <= x + radius; ++ix) {
                for (int iz = z - radius; iz <= z + radius; ++iz) {
                    for (int iy = y - radius; iy <= y + radius; ++iy) {
                        int currentBlock = accessor.getBlock(ix, iy, iz);
                        blockCounts.put(currentBlock, blockCounts.getOrDefault(currentBlock, 0) + 1);
                    }
                }
            }
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            for (Int2IntMap.Entry pair : Int2IntMaps.fastIterable(blockCounts)) {
                int block = pair.getIntKey();
                int count = pair.getIntValue();
                if (count > data.mainBlockCount) {
                    data.mainBlock = block;
                    data.mainBlockCount = count;
                }
                BlockType blockType = assetMap.getAsset(block);
                if (count <= data.mainBlockNotAirCount || block == 0) continue;
                data.mainBlockNotAir = block;
                data.mainBlockNotAirCount = count;
            }
            return data;
        }

        @Nonnull
        public SmoothSampleData getBlocksSmoothData(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
            SmoothSampleData data = new SmoothSampleData();
            Int2IntOpenHashMap blockCounts = new Int2IntOpenHashMap();
            int kernelIndex = 0;
            for (int ix = x - 1; ix <= x + 1; ++ix) {
                for (int iy = y - 1; iy <= y + 1; ++iy) {
                    for (int iz = z - 1; iz <= z + 1; ++iz) {
                        int currentBlock = accessor.getBlock(ix, iy, iz);
                        blockCounts.put(currentBlock, blockCounts.getOrDefault(currentBlock, 0) + SMOOTHING_KERNEL[kernelIndex++]);
                    }
                }
            }
            float solidCount = 0.0f;
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            for (Int2IntMap.Entry pair : Int2IntMaps.fastIterable(blockCounts)) {
                int block = pair.getIntKey();
                int count = pair.getIntValue();
                BlockType blockType = assetMap.getAsset(block);
                if (blockType.getMaterial() == BlockMaterial.Solid) {
                    solidCount += (float)count;
                    if (count <= data.solidBlockCount) continue;
                    data.solidBlock = block;
                    data.solidBlockCount = count;
                    continue;
                }
                if (count <= data.fillerBlockCount) continue;
                data.fillerBlock = block;
                data.fillerBlockCount = count;
            }
            data.solidStrength = solidCount / 27.0f;
            return data;
        }

        public void editLine(int x1, int y1, int z1, int x2, int y2, int z2, BlockPattern material, int lineWidth, int lineHeight, int wallThickness, BrushShape shape, BrushOrigin origin, int spacing, int density, ComponentAccessor<EntityStore> componentAccessor) {
            this.editLine(x1, y1, z1, x2, y2, z2, material, lineWidth, lineHeight, wallThickness, shape, origin, spacing, density, this.getGlobalMask(), componentAccessor);
        }

        public void editLine(int x1, int y1, int z1, int x2, int y2, int z2, BlockPattern material, int lineWidth, int lineHeight, int wallThickness, BrushShape shape, BrushOrigin origin, int spacing, int density, @Nullable BlockMask mask, ComponentAccessor<EntityStore> componentAccessor) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            float halfWidth = (float)lineWidth / 2.0f;
            float halfHeight = (float)lineHeight / 2.0f;
            int iHalfWidth = MathUtil.fastCeil(halfWidth);
            int iHalfHeight = MathUtil.fastCeil(halfHeight);
            int maxRadius = Math.max(iHalfWidth, iHalfHeight);
            Vector3i min = new Vector3i(Math.min(x1, x2) - maxRadius, Math.min(y1, y2) - maxRadius, Math.min(z1, z2) - maxRadius);
            Vector3i max = new Vector3i(Math.max(x1, x2) + maxRadius, Math.max(y1, y2) + maxRadius, Math.max(z1, z2) + maxRadius);
            BlockSelection before = new BlockSelection();
            before.setPosition(x1, y1, z1);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.EDIT_LINE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            int originOffset = 0;
            if (origin == BrushOrigin.Bottom) {
                originOffset = iHalfHeight + 1;
            } else if (origin == BrushOrigin.Top) {
                originOffset = -iHalfHeight;
            }
            float innerHalfWidth = Math.max(0.0f, halfWidth - (float)wallThickness);
            float innerHalfHeight = Math.max(0.0f, halfHeight - (float)wallThickness);
            Predicate<Vector3i> isInShape = this.createShapePredicate(shape, halfWidth, halfHeight, innerHalfWidth, innerHalfHeight, wallThickness > 0);
            int lineDistX = x2 - x1;
            int lineDistZ = z2 - z1;
            int halfLineDistX = lineDistX / 2;
            int halfLineDistZ = lineDistZ / 2;
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, x1 + halfLineDistX, z1 + halfLineDistZ, Math.max(Math.abs(lineDistX), Math.abs(lineDistZ)) + maxRadius + 1);
            Vector3i rel = new Vector3i();
            LineIterator line = new LineIterator(x1, y1, z1, x2, y2, z2);
            int stepCount = 0;
            while (line.hasNext()) {
                Vector3i coord = line.next();
                if (stepCount % spacing != 0) {
                    ++stepCount;
                    continue;
                }
                ++stepCount;
                for (int sx = -iHalfWidth; sx <= iHalfWidth; ++sx) {
                    for (int sz = iHalfWidth; sz >= -iHalfWidth; --sz) {
                        int blockX = coord.getX() + sx;
                        int blockZ = coord.getZ() + sz;
                        WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(blockX, blockZ));
                        for (int sy = -iHalfHeight; sy <= iHalfHeight; ++sy) {
                            rel.assign(sx, sy, sz);
                            if (!isInShape.test(rel)) continue;
                            int blockY = coord.getY() + sy + originOffset;
                            int currentBlockId = chunk.getBlock(blockX, blockY, blockZ);
                            int currentFluidId = chunk.getFluidId(blockX, blockY, blockZ);
                            if (mask != null && mask.isExcluded(accessor, blockX, blockY, blockZ, min, max, currentBlockId, currentFluidId) || this.random.nextInt(100) >= density) continue;
                            int blockId = material.nextBlock(this.random);
                            before.addBlockAtWorldPos(blockX, blockY, blockZ, currentBlockId, chunk.getRotationIndex(blockX, blockY, blockZ), chunk.getFiller(blockX, blockY, blockZ), chunk.getSupportValue(blockX, blockY, blockZ), chunk.getBlockComponentHolder(blockX, blockY, blockZ));
                            after.addBlockAtWorldPos(blockX, blockY, blockZ, blockId, 0, 0, 0);
                        }
                    }
                }
            }
            after.placeNoReturn("Edit 1/1", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            int size = after.getBlockCount();
            double length = new Vector3i(x1, y1, z1).distanceTo(x2, y2, z2);
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute editLine of %d blocks with length %s", diff, TimeUnit.NANOSECONDS.toMillis(diff), size, length);
            this.sendFeedback(Message.translation("server.builderTools.drawLineOf").param("count", length), componentAccessor);
        }

        private Predicate<Vector3i> createShapePredicate(BrushShape shape, float halfWidth, float halfHeight, float innerHalfWidth, float innerHalfHeight, boolean hollow) {
            float hw = halfWidth + 0.41f;
            float hh = halfHeight + 0.41f;
            float ihw = innerHalfWidth + 0.41f;
            float ihh = innerHalfHeight + 0.41f;
            return switch (shape) {
                default -> throw new MatchException(null, null);
                case BrushShape.Cube -> coord -> {
                    boolean inOuter;
                    double ax = Math.abs(coord.getX());
                    double ay = Math.abs(coord.getY());
                    double az = Math.abs(coord.getZ());
                    boolean bl = inOuter = ax <= (double)hw && ay <= (double)hh && az <= (double)hw;
                    if (!hollow) {
                        return inOuter;
                    }
                    boolean inInner = ax < (double)ihw && ay < (double)ihh && az < (double)ihw;
                    return inOuter && !inInner;
                };
                case BrushShape.Sphere -> coord -> {
                    boolean inOuter;
                    double sz;
                    double sy;
                    double sx = coord.getX();
                    double outerDist = sx * sx / (double)(hw * hw) + (sy = (double)coord.getY()) * sy / (double)(hh * hh) + (sz = (double)coord.getZ()) * sz / (double)(hw * hw);
                    boolean bl = inOuter = outerDist <= 1.0;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerDist = sx * sx / (double)(ihw * ihw) + sy * sy / (double)(ihh * ihh) + sz * sz / (double)(ihw * ihw);
                    boolean inInner = ihw > 0.0f && ihh > 0.0f && innerDist <= 1.0;
                    return inOuter && !inInner;
                };
                case BrushShape.Cylinder -> coord -> {
                    boolean inOuterRadius;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double outerRadialDist = (sx * sx + sz * sz) / (double)(hw * hw);
                    boolean bl = inOuterRadius = outerRadialDist <= 1.0 && Math.abs(sy) <= (double)hh;
                    if (!hollow) {
                        return inOuterRadius;
                    }
                    double innerRadialDist = (sx * sx + sz * sz) / (double)(ihw * ihw);
                    boolean inInnerRadius = ihw > 0.0f && innerRadialDist <= 1.0 && Math.abs(sy) < (double)ihh;
                    return inOuterRadius && !inInnerRadius;
                };
                case BrushShape.Cone -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double normalizedY = (sy + (double)hh) / (double)(2.0f * hh);
                    if (normalizedY < 0.0 || normalizedY > 1.0) {
                        return false;
                    }
                    double currentRadius = (double)hw * (1.0 - normalizedY);
                    double radialDist = Math.sqrt(sx * sx + sz * sz);
                    boolean bl = inOuter = radialDist <= currentRadius;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerRadius = Math.max(0.0, currentRadius - (double)(hw - ihw));
                    boolean inInner = radialDist < innerRadius;
                    return inOuter && !inInner;
                };
                case BrushShape.InvertedCone -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double normalizedY = (sy + (double)hh) / (double)(2.0f * hh);
                    if (normalizedY < 0.0 || normalizedY > 1.0) {
                        return false;
                    }
                    double currentRadius = (double)hw * normalizedY;
                    double radialDist = Math.sqrt(sx * sx + sz * sz);
                    boolean bl = inOuter = radialDist <= currentRadius;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerRadius = Math.max(0.0, currentRadius - (double)(hw - ihw));
                    boolean inInner = radialDist < innerRadius;
                    return inOuter && !inInner;
                };
                case BrushShape.Pyramid -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double normalizedY = (sy + (double)hh) / (double)(2.0f * hh);
                    if (normalizedY < 0.0 || normalizedY > 1.0) {
                        return false;
                    }
                    double currentHalfSize = (double)hw * (1.0 - normalizedY);
                    boolean bl = inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerHalfSize = Math.max(0.0, currentHalfSize - (double)(hw - ihw));
                    boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
                    return inOuter && !inInner;
                };
                case BrushShape.InvertedPyramid -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double normalizedY = (sy + (double)hh) / (double)(2.0f * hh);
                    if (normalizedY < 0.0 || normalizedY > 1.0) {
                        return false;
                    }
                    double currentHalfSize = (double)hw * normalizedY;
                    boolean bl = inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerHalfSize = Math.max(0.0, currentHalfSize - (double)(hw - ihw));
                    boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
                    return inOuter && !inInner;
                };
                case BrushShape.Dome -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    if (sy < 0.0) {
                        return false;
                    }
                    double outerDist = sx * sx / (double)(hw * hw) + sy * sy / (double)(hh * hh) + sz * sz / (double)(hw * hw);
                    boolean bl = inOuter = outerDist <= 1.0;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerDist = sx * sx / (double)(ihw * ihw) + sy * sy / (double)(ihh * ihh) + sz * sz / (double)(ihw * ihw);
                    boolean inInner = ihw > 0.0f && ihh > 0.0f && innerDist <= 1.0;
                    return inOuter && !inInner;
                };
                case BrushShape.InvertedDome -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    if (sy > 0.0) {
                        return false;
                    }
                    double outerDist = sx * sx / (double)(hw * hw) + sy * sy / (double)(hh * hh) + sz * sz / (double)(hw * hw);
                    boolean bl = inOuter = outerDist <= 1.0;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerDist = sx * sx / (double)(ihw * ihw) + sy * sy / (double)(ihh * ihh) + sz * sz / (double)(ihw * ihw);
                    boolean inInner = ihw > 0.0f && ihh > 0.0f && innerDist <= 1.0;
                    return inOuter && !inInner;
                };
                case BrushShape.Diamond -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double normalizedY = Math.abs(sy) / (double)hh;
                    if (normalizedY > 1.0) {
                        return false;
                    }
                    double currentHalfSize = (double)hw * (1.0 - normalizedY);
                    boolean bl = inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerHalfSize = Math.max(0.0, currentHalfSize - (double)(hw - ihw));
                    boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
                    return inOuter && !inInner;
                };
                case BrushShape.Torus -> coord -> {
                    boolean inOuter;
                    double sx = coord.getX();
                    double sy = coord.getY();
                    double sz = coord.getZ();
                    double minorRadius = Math.max(1.0f, hh / 2.0f);
                    double majorRadius = Math.max(1.0, (double)hw - minorRadius);
                    double minorRadiusAdjusted = minorRadius + (double)0.41f;
                    double distFromCenter = Math.sqrt(sx * sx + sz * sz);
                    double distFromRing = distFromCenter - majorRadius;
                    double distFromTube = Math.sqrt(distFromRing * distFromRing + sy * sy);
                    boolean bl = inOuter = distFromTube <= minorRadiusAdjusted;
                    if (!hollow) {
                        return inOuter;
                    }
                    double innerMinorRadius = Math.max(0.0, minorRadiusAdjusted - (double)(ihw > 0.0f ? hw - ihw : 0.0f));
                    boolean inInner = innerMinorRadius > 0.0 && distFromTube < innerMinorRadius;
                    return inOuter && !inInner;
                };
            };
        }

        public void extendFace(int x, int y, int z, int normalX, int normalY, int normalZ, int extrudeDepth, int radiusAllowed, int blockId, @Nullable Vector3i min, @Nullable Vector3i max, ComponentAccessor<EntityStore> componentAccessor) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, x, z, radiusAllowed);
            if (min == null) {
                min = new Vector3i(x - radiusAllowed, y - radiusAllowed, z - radiusAllowed);
            } else {
                int minZ;
                int minY;
                int minX = min.getX();
                if (x - radiusAllowed > minX) {
                    minX = x - radiusAllowed;
                }
                if (y - radiusAllowed > (minY = min.getY())) {
                    minY = y - radiusAllowed;
                }
                if (z - radiusAllowed > (minZ = min.getZ())) {
                    minZ = z - radiusAllowed;
                }
                min = new Vector3i(minX, minY, minZ);
            }
            if (max == null) {
                max = new Vector3i(x + radiusAllowed, y + radiusAllowed, z + radiusAllowed);
            } else {
                int maxZ;
                int maxY;
                int maxX = max.getX();
                if (x + radiusAllowed < maxX) {
                    maxX = x + radiusAllowed;
                }
                if (y + radiusAllowed < (maxY = max.getY())) {
                    maxY = y + radiusAllowed;
                }
                if (z + radiusAllowed < (maxZ = max.getZ())) {
                    maxZ = z + radiusAllowed;
                }
                max = new Vector3i(maxX, maxY, maxZ);
            }
            int totalBlocks = (max.getX() - min.getX() + 1) * (max.getZ() - min.getZ() + 1) * (max.getY() - min.getY() + 1);
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(x + normalX, y + normalY, z + normalZ);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.EXTRUDE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            this.extendFaceFindBlocks(accessor, BlockType.getAssetMap(), before, after, x + normalX, y + normalY, z + normalZ, normalX, normalY, normalZ, extrudeDepth, blockId, min, max);
            Vector3i offset = new Vector3i(0, 0, 0);
            for (int i = 0; i < extrudeDepth; ++i) {
                offset.x = normalX * i;
                offset.y = normalY * i;
                offset.z = normalZ * i;
                after.placeNoReturn("Set", this.player, FEEDBACK_CONSUMER, world, offset, null, componentAccessor);
            }
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        private void extendFaceFindBlocks(@Nonnull ChunkAccessor accessor, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, @Nonnull BlockSelection before, @Nonnull BlockSelection after, int x, int y, int z, int normalX, int normalY, int normalZ, int extrudeDepth, int blockId, @Nonnull Vector3i min, @Nonnull Vector3i max) {
            if (x < min.getX() || x > max.getX()) {
                return;
            }
            if (y < min.getY() || y > max.getY()) {
                return;
            }
            if (z < min.getZ() || z > max.getZ()) {
                return;
            }
            int block = accessor.getBlock(x, y, z);
            int testBlock = accessor.getBlock(x - normalX, y - normalY, z - normalZ);
            BlockType testBlockType = assetMap.getAsset(testBlock);
            if (testBlockType == null || testBlockType.getDrawType() != DrawType.Cube && testBlockType.getDrawType() != DrawType.CubeWithModel) {
                return;
            }
            if (before.hasBlockAtWorldPos(x, y, z)) {
                return;
            }
            Object blocks = accessor.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
            if (blocks == null) {
                return;
            }
            before.addBlockAtWorldPos(x, y, z, block, blocks.getRotationIndex(x, y, z), blocks.getFiller(x, y, z), blocks.getSupportValue(x, y, z), blocks.getBlockComponentHolder(x, y, z));
            after.addBlockAtWorldPos(x, y, z, blockId, 0, 0, 0);
            for (Vector3i side : Vector3i.BLOCK_SIDES) {
                if (normalX != 0 && side.getX() != 0 || normalY != 0 && side.getY() != 0 || normalZ != 0 && side.getZ() != 0) continue;
                this.extendFaceFindBlocks(accessor, assetMap, before, after, x + side.getX(), y + side.getY(), z + side.getZ(), normalX, normalY, normalZ, extrudeDepth, blockId, min, max);
            }
        }

        public void update(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
            if (this.selection == null) {
                this.selection = new BlockSelection();
            }
            this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            this.selection.setSelectionArea(new Vector3i(xMin, yMin, zMin), new Vector3i(xMax, yMax, zMax));
        }

        public void tint(@Nonnull Ref<EntityStore> ref, int color, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            World world = componentAccessor.getExternalData().getWorld();
            int count = 0;
            int minX = this.selection.getSelectionMin().getX();
            int minZ = this.selection.getSelectionMin().getZ();
            int maxX = this.selection.getSelectionMax().getX();
            int maxZ = this.selection.getSelectionMax().getZ();
            for (int cx = ChunkUtil.chunkCoordinate(minX); cx <= ChunkUtil.chunkCoordinate(maxX); ++cx) {
                for (int cz = ChunkUtil.chunkCoordinate(minZ); cz <= ChunkUtil.chunkCoordinate(maxZ); ++cz) {
                    int startX = Math.max(0, minX - ChunkUtil.minBlock(cx));
                    int startZ = Math.max(0, minZ - ChunkUtil.minBlock(cz));
                    int endX = Math.min(32, maxX - ChunkUtil.minBlock(cx));
                    int endZ = Math.min(32, maxZ - ChunkUtil.minBlock(cz));
                    Object chunk = world.getNonTickingChunk(ChunkUtil.indexChunk(cx, cz));
                    for (int z = startZ; z < endZ; ++z) {
                        for (int x = startX; x < endX; ++x) {
                            ((WorldChunk)chunk).getBlockChunk().setTint(x, z, color);
                            ++count;
                        }
                    }
                    world.getNotificationHandler().updateChunk(((WorldChunk)chunk).getIndex());
                }
            }
            this.sendFeedback(Message.translation("server.builderTools.setColumnsTint").param("count", count), componentAccessor);
        }

        public void tint(int x, int y, int z, int color, @Nonnull BrushShape shape, int shapeRange, int shapeHeight, ComponentAccessor<EntityStore> componentAccessor) {
            if (y < 0 || y >= 320) {
                return;
            }
            World world = componentAccessor.getExternalData().getWorld();
            LongOpenHashSet dirtyChunks = new LongOpenHashSet();
            AtomicInteger count = new AtomicInteger(0);
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, x, z, shapeRange + 1);
            TriIntObjPredicate<Void> tintBlock = (px, py, pz, aVoid) -> {
                WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(px, pz));
                chunk.getBlockChunk().setTint(px, pz, color);
                dirtyChunks.add(chunk.getIndex());
                count.getAndIncrement();
                return true;
            };
            if (shapeRange <= 1) {
                tintBlock.test(x, y, z, null);
            } else {
                int radiusXZ = shapeRange / 2;
                block0 : switch (shape) {
                    case Cube: 
                    case Pyramid: 
                    case InvertedPyramid: 
                    case Diamond: {
                        for (int px2 = -radiusXZ; px2 <= radiusXZ; ++px2) {
                            for (int pz2 = -radiusXZ; pz2 <= radiusXZ; ++pz2) {
                                if (!tintBlock.test(x + px2, y, z + pz2, null)) break block0;
                            }
                        }
                        break;
                    }
                    case Sphere: 
                    case Cylinder: 
                    case Cone: 
                    case InvertedCone: 
                    case Dome: 
                    case InvertedDome: {
                        BlockSphereUtil.forEachBlock(x, y, z, radiusXZ, 1, radiusXZ, null, tintBlock);
                        break;
                    }
                    case Torus: {
                        int minorRadius = Math.max(1, shapeHeight / 4);
                        int outerRadius = radiusXZ;
                        int majorRadius = Math.max(1, outerRadius - minorRadius);
                        int sizeXZ = majorRadius + minorRadius;
                        float minorRadiusAdjusted = (float)minorRadius + 0.5f;
                        for (int px3 = -sizeXZ; px3 <= sizeXZ; ++px3) {
                            for (int pz3 = -sizeXZ; pz3 <= sizeXZ; ++pz3) {
                                double distFromCenter = Math.sqrt(px3 * px3 + pz3 * pz3);
                                double distFromRing = Math.abs(distFromCenter - (double)majorRadius);
                                if (!(distFromRing <= (double)minorRadiusAdjusted)) continue;
                                tintBlock.test(x + px3, y, z + pz3, null);
                            }
                        }
                        break;
                    }
                    default: {
                        this.sendFeedback(Message.translation("server.builderTools.errorWithUsedShape"), componentAccessor);
                        return;
                    }
                }
            }
            dirtyChunks.forEach(value -> world.getNotificationHandler().updateChunk(value));
            this.sendFeedback(Message.translation("server.builderTools.setColumnsTint").param("count", count.intValue()), componentAccessor);
        }

        public void environment(@Nonnull Ref<EntityStore> ref, int environmentId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            World world = componentAccessor.getExternalData().getWorld();
            LongOpenHashSet dirtyChunks = new LongOpenHashSet();
            int count = 0;
            for (int x = this.selection.getSelectionMin().getX(); x < this.selection.getSelectionMax().getX(); ++x) {
                for (int z = this.selection.getSelectionMin().getZ(); z < this.selection.getSelectionMax().getZ(); ++z) {
                    Object chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    dirtyChunks.add(((WorldChunk)chunk).getIndex());
                    for (int y = this.selection.getSelectionMin().getY(); y < this.selection.getSelectionMax().getY(); ++y) {
                        ((WorldChunk)chunk).getBlockChunk().setEnvironment(x, y, z, environmentId);
                        ++count;
                    }
                }
            }
            dirtyChunks.forEach(value -> world.getNotificationHandler().updateChunk(value));
            this.sendFeedback(Message.translation("server.builderTools.setEnvironment").param("count", count), componentAccessor);
        }

        public int copyOrCut(@Nonnull Ref<EntityStore> ref, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, int settings, @Nonnull ComponentAccessor<EntityStore> componentAccessor) throws PrefabCopyException {
            return this.copyOrCut(ref, xMin, yMin, zMin, xMax, yMax, zMax, settings, null, componentAccessor);
        }

        public int copyOrCut(@Nonnull Ref<EntityStore> ref, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, int settings, @Nullable Vector3i playerAnchor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) throws PrefabCopyException {
            int entityCount;
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            if (this.selection == null) {
                this.selection = new BlockSelection();
            }
            BlockSelection before = null;
            BlockSelection after = null;
            List<SelectionSnapshot<?>> snapshots = Collections.emptyList();
            boolean cut = (settings & 2) != 0;
            boolean empty = (settings & 4) != 0;
            boolean blocks = (settings & 8) != 0;
            boolean entities = (settings & 0x10) != 0;
            boolean keepAnchors = (settings & 0x40) != 0;
            int width = xMax - xMin;
            int height = yMax - yMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            if (cut) {
                before = new BlockSelection();
                before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
                after = new BlockSelection(before);
                snapshots = new ObjectArrayList();
                this.pushHistory(Action.CUT_COPY, ClipboardContentsSnapshot.copyOf(this.selection));
            } else {
                this.pushHistory(Action.COPY, ClipboardContentsSnapshot.copyOf(this.selection));
            }
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            int editorBlock = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK);
            if (editorBlock == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! Editor_Block");
            }
            int editorBlockPrefabAir = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_AIR);
            if (editorBlockPrefabAir == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! Editor_Empty");
            }
            int editorBlockPrefabAnchor = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_ANCHOR);
            if (editorBlockPrefabAnchor == Integer.MIN_VALUE) {
                throw new IllegalArgumentException("Unknown key! Editor_Anchor");
            }
            HashSet<Vector3i> anchors = new HashSet<Vector3i>();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            this.selection = new BlockSelection();
            this.selection.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            this.selection.setSelectionArea(min, max);
            int count = 0;
            int counter = 0;
            int top = Math.max(yMin, yMax);
            int bottom = Math.min(yMin, yMax);
            int totalBlocks = (width + 1) * (depth + 1) * (top - bottom + 1);
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    Store<ChunkStore> store = chunk.getReference().getStore();
                    ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
                    int lastSection = -1;
                    BlockPhysics blockPhysics = null;
                    for (int y = top; y >= bottom; --y) {
                        int block = chunk.getBlock(x, y, z);
                        int fluid = chunk.getFluidId(x, y, z);
                        if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                            lastSection = ChunkUtil.chunkCoordinate(y);
                            Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                            blockPhysics = section != null ? store.getComponent(section, BlockPhysics.getComponentType()) : null;
                        }
                        if (blocks && cut && (block != 0 || fluid != 0 || empty)) {
                            before.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                            after.addEmptyAtWorldPos(x, y, z);
                        }
                        if (block == editorBlockPrefabAnchor && !keepAnchors && playerAnchor == null) {
                            anchors.add(new Vector3i(x, y, z));
                            this.selection.setAnchorAtWorldPos(x, y, z);
                            if (blocks) {
                                int id = BuilderToolsPlugin.getNonEmptyNeighbourBlock(accessor, x, y, z);
                                if (id > 0 && id != editorBlockPrefabAir) {
                                    this.selection.addBlockAtWorldPos(x, y, z, id, 0, 0, 0);
                                    ++count;
                                } else if (id == editorBlockPrefabAir) {
                                    this.selection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                    ++count;
                                }
                            }
                        } else if (blocks && (block != 0 || fluid != 0 || empty) && block != editorBlock) {
                            if (block == editorBlockPrefabAir) {
                                this.selection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                            } else {
                                this.selection.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                            }
                            ++count;
                        }
                        this.sendFeedback(cut ? "Gather 1/2" : "Gather 1/1", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            if (anchors.size() > 1 && playerAnchor == null) {
                StringBuilder sb = new StringBuilder("Anchors: ");
                boolean first = true;
                for (Vector3i anchor : anchors) {
                    if (!first) {
                        sb.append(", ");
                    }
                    first = false;
                    sb.append('[').append(anchor.getX()).append(", ").append(anchor.getY()).append(", ").append(anchor.getZ()).append(']');
                }
                throw new PrefabCopyException("Prefab has multiple anchor blocks!\n" + String.valueOf(sb));
            }
            if (playerAnchor != null) {
                this.selection.setAnchorAtWorldPos(playerAnchor.getX(), playerAnchor.getY(), playerAnchor.getZ());
            }
            if (entities) {
                List<SelectionSnapshot<?>> snapshotsList = snapshots;
                Store<EntityStore> store = world.getEntityStore().getStore();
                ArrayList entitiesToRemove = cut ? new ArrayList() : null;
                BuilderToolsPlugin.forEachCopyableInSelection(world, xMin, yMin, zMin, width, height, depth, e -> {
                    Holder<EntityStore> holder = store.copyEntity((Ref<EntityStore>)e);
                    this.selection.addEntityFromWorld(holder);
                    if (cut) {
                        snapshotsList.add(new EntityRemoveSnapshot((Ref<EntityStore>)e));
                        entitiesToRemove.add(e);
                    }
                });
                if (cut && entitiesToRemove != null) {
                    for (Ref e2 : entitiesToRemove) {
                        store.removeEntity(e2, RemoveReason.UNLOAD);
                    }
                }
            }
            if (cut) {
                snapshots.add(new BlockSelectionSnapshot(before));
                this.pushHistory(Action.CUT_REMOVE, snapshots);
            }
            if (after != null) {
                after.placeNoReturn("Cut 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
                BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            }
            long end = System.nanoTime();
            long diff = end - start;
            int size = count;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute copy of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
            if (cut) {
                this.sendUpdate();
            } else {
                this.player.getPlayerConnection().write((Packet)Objects.requireNonNullElseGet(this.selection, BlockSelection::new).toPacketWithSelection());
            }
            int n = entityCount = entities ? this.selection.getEntityCount() : 0;
            String translationKey = cut ? (entityCount > 0 ? "server.builderTools.cutWithEntities" : "server.builderTools.cut") : (entityCount > 0 ? "server.builderTools.copiedWithEntities" : "server.builderTools.copied");
            this.sendFeedback(ref, Message.translation(translationKey).param("blockCount", size).param("entityCount", entityCount), cut ? "SFX_CREATE_CUT" : "SFX_CREATE_COPY", componentAccessor);
            return count;
        }

        public int clear(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(new Vector3i(xMin, yMin, zMin), new Vector3i(xMax, yMax, zMax));
            this.pushHistory(Action.CLEAR, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int top = Math.max(yMin, yMax);
            int bottom = Math.min(yMin, yMax);
            int height = top - bottom;
            int totalBlocks = (width + 1) * (depth + 1) * (height + 1);
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    Store<ChunkStore> store = chunk.getReference().getStore();
                    ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
                    int lastSection = -1;
                    BlockPhysics blockPhysics = null;
                    for (int y = top; y >= bottom; --y) {
                        int block = chunk.getBlock(x, y, z);
                        int fluid = chunk.getFluidId(x, y, z);
                        if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                            lastSection = ChunkUtil.chunkCoordinate(y);
                            Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                            blockPhysics = section != null ? store.getComponent(section, BlockPhysics.getComponentType()) : null;
                        }
                        if (block != 0 || fluid != 0) {
                            before.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                            after.addEmptyAtWorldPos(x, y, z);
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Clear 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            int size = after.getBlockCount();
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute clear of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
            this.sendFeedback("Clear", size, componentAccessor);
            return size;
        }

        public static RotationTuple transformRotation(RotationTuple prevRot, Matrix4d transformationMatrix) {
            Vector3f forwardVec = new Vector3f(1.0f, 0.0f, 0.0f);
            Vector3f upVec = new Vector3f(0.0f, 1.0f, 0.0f);
            forwardVec = Rotation.rotate(forwardVec, prevRot.yaw(), prevRot.pitch(), prevRot.roll());
            upVec = Rotation.rotate(upVec, prevRot.yaw(), prevRot.pitch(), prevRot.roll());
            Vector3f newForward = transformationMatrix.multiplyDirection(forwardVec.toVector3d()).toVector3f();
            Vector3f newUp = transformationMatrix.multiplyDirection(upVec.toVector3d()).toVector3f();
            float bestScore = Float.MIN_VALUE;
            RotationTuple bestRot = prevRot;
            for (RotationTuple rotation : RotationTuple.VALUES) {
                Vector3f rotForward = Rotation.rotate(new Vector3f(1.0f, 0.0f, 0.0f), rotation.yaw(), rotation.pitch(), rotation.roll());
                Vector3f rotUp = Rotation.rotate(new Vector3f(0.0f, 1.0f, 0.0f), rotation.yaw(), rotation.pitch(), rotation.roll());
                float score = rotForward.dot(newForward) + rotUp.dot(newUp);
                if (!(score > bestScore)) continue;
                bestScore = score;
                bestRot = rotation;
            }
            return bestRot;
        }

        public void transformThenPasteClipboard(@Nonnull BlockChange[] blockChanges, @Nullable PrototypePlayerBuilderToolSettings.FluidChange[] fluidChanges, @Nonnull Matrix4d transformationMatrix, @Nonnull Vector3f rotationOrigin, @Nonnull Vector3i initialPastePoint, boolean keepEmptyBlocks, ComponentAccessor<EntityStore> componentAccessor) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            int editorBlockPrefabAir = keepEmptyBlocks ? assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_AIR) : 0;
            int yOffsetOutOfGround = 0;
            for (BlockChange blockChange : blockChanges) {
                if (blockChange.y >= 0 || Math.abs(blockChange.y) <= yOffsetOutOfGround) continue;
                yOffsetOutOfGround = Math.abs(blockChange.y);
            }
            Vector4d translationEndResult = new Vector4d(0.0, 0.0, 0.0, 1.0);
            transformationMatrix.multiply(translationEndResult);
            translationEndResult.x += (double)rotationOrigin.x;
            translationEndResult.y += (double)rotationOrigin.y;
            translationEndResult.z += (double)rotationOrigin.z;
            BlockSelection before = new BlockSelection();
            before.setPosition((int)translationEndResult.x, (int)translationEndResult.y, (int)translationEndResult.z);
            BlockSelection after = new BlockSelection(before);
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, (int)translationEndResult.x, (int)translationEndResult.z, 50);
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            Vector4d mutable4d = new Vector4d(0.0, 0.0, 0.0, 1.0);
            for (BlockChange blockChange : blockChanges) {
                BlockBoundingBoxes hitbox;
                BlockType blockType;
                mutable4d.assign((double)((float)blockChange.x - rotationOrigin.x + (float)initialPastePoint.x) + 0.5, (double)((float)blockChange.y - rotationOrigin.y + (float)initialPastePoint.y) + 0.5 + (double)yOffsetOutOfGround, (double)((float)blockChange.z - rotationOrigin.z + (float)initialPastePoint.z) + 0.5, 1.0);
                transformationMatrix.multiply(mutable4d);
                Vector3i rotatedLocation = new Vector3i((int)Math.floor(mutable4d.x + 0.1 + (double)rotationOrigin.x - 0.5), (int)Math.floor(mutable4d.y + 0.1 + (double)rotationOrigin.y - 0.5), (int)Math.floor(mutable4d.z + 0.1 + (double)rotationOrigin.z - 0.5));
                minX = Math.min(minX, rotatedLocation.x);
                minY = Math.min(minY, rotatedLocation.y);
                minZ = Math.min(minZ, rotatedLocation.z);
                maxX = Math.max(maxX, rotatedLocation.x);
                maxY = Math.max(maxY, rotatedLocation.y);
                maxZ = Math.max(maxZ, rotatedLocation.z);
                WorldChunk currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(rotatedLocation.x, rotatedLocation.z));
                Holder<ChunkStore> holder = currentChunk.getBlockComponentHolder(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                int blockIdInRotatedLocation = currentChunk.getBlock(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                int filler = currentChunk.getFiller(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                int rotation = currentChunk.getRotationIndex(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                before.addBlockAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, blockIdInRotatedLocation, rotation, filler, 0, holder);
                int originalFluidId = currentChunk.getFluidId(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                byte originalFluidLevel = currentChunk.getFluidLevel(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
                before.addFluidAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, originalFluidId, originalFluidLevel);
                int newRotation = BuilderState.transformRotation(RotationTuple.get(blockChange.rotation), transformationMatrix).index();
                int blockIdToPlace = blockChange.block;
                if (blockChange.block == 0 && keepEmptyBlocks) {
                    blockIdToPlace = editorBlockPrefabAir;
                }
                if ((blockType = assetMap.getAsset(blockIdToPlace)) == null || (hitbox = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex())) == null) continue;
                int finalBlockIdToPlace = blockIdToPlace;
                if (hitbox.protrudesUnitBox()) {
                    FillerBlockUtil.forEachFillerBlock(hitbox.get(newRotation), (x, y, z) -> after.addBlockAtWorldPos(rotatedLocation.x + x, rotatedLocation.y + y, rotatedLocation.z + z, finalBlockIdToPlace, newRotation, FillerBlockUtil.pack(x, y, z), 0, holder));
                    continue;
                }
                after.addBlockAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, blockIdToPlace, newRotation, 0, 0, holder);
            }
            int finalYOffsetOutOfGround = yOffsetOutOfGround;
            if (fluidChanges != null) {
                for (PrototypePlayerBuilderToolSettings.FluidChange fluidChange : fluidChanges) {
                    mutable4d.assign((double)((float)fluidChange.x() - rotationOrigin.x + (float)initialPastePoint.x) + 0.5, (double)((float)fluidChange.y() - rotationOrigin.y + (float)initialPastePoint.y) + 0.5 + (double)finalYOffsetOutOfGround, (double)((float)fluidChange.z() - rotationOrigin.z + (float)initialPastePoint.z) + 0.5, 1.0);
                    transformationMatrix.multiply(mutable4d);
                    Vector3i rotatedLocation = new Vector3i((int)Math.floor(mutable4d.x + 0.1 + (double)rotationOrigin.x - 0.5), (int)Math.floor(mutable4d.y + 0.1 + (double)rotationOrigin.y - 0.5), (int)Math.floor(mutable4d.z + 0.1 + (double)rotationOrigin.z - 0.5));
                    after.addFluidAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, fluidChange.fluidId(), fluidChange.fluidLevel());
                }
            }
            if (minX != Integer.MAX_VALUE) {
                before.setSelectionArea(new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ));
            }
            this.pushHistory(Action.ROTATE, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Transform 1/1", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        public void transformSelectionPoints(@Nonnull Matrix4d transformationMatrix, @Nonnull Vector3f rotationOrigin) {
            Vector3i newMin = this.transformBlockLocation(this.selection.getSelectionMin(), transformationMatrix, rotationOrigin);
            Vector3i newMax = this.transformBlockLocation(this.selection.getSelectionMax(), transformationMatrix, rotationOrigin);
            this.selection.setSelectionArea(Vector3i.min(newMin, newMax), Vector3i.max(newMin, newMax));
            this.sendUpdate();
            this.sendArea();
        }

        @Nonnull
        public Vector3i transformBlockLocation(@Nonnull Vector3i blockLocation, @Nonnull Matrix4d transformationMatrix, @Nonnull Vector3f rotationOrigin) {
            Vector4d relativeOffset = new Vector4d((double)((float)blockLocation.x - rotationOrigin.x) + 0.5, (double)((float)blockLocation.y - rotationOrigin.y) + 0.5, (double)((float)blockLocation.z - rotationOrigin.z) + 0.5, 1.0);
            transformationMatrix.multiply(relativeOffset);
            return new Vector3i((int)Math.floor(relativeOffset.x + (double)rotationOrigin.x - 0.5 + 0.1), (int)Math.floor(relativeOffset.y + (double)rotationOrigin.y - 0.5 + 0.1), (int)Math.floor(relativeOffset.z + (double)rotationOrigin.z - 0.5 + 0.1));
        }

        public int paste(@Nonnull Ref<EntityStore> ref, int x, int y, int z, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            return this.paste(ref, x, y, z, false, componentAccessor);
        }

        public int paste(@Nonnull Ref<EntityStore> ref, int x, int y, int z, boolean technicalPaste, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            World world = componentAccessor.getExternalData().getWorld();
            if (this.selection != null) {
                long start = System.nanoTime();
                Vector3i selMin = this.selection.getSelectionMin();
                Vector3i selMax = this.selection.getSelectionMax();
                int origPosX = (selMin.x + selMax.x) / 2;
                int origPosY = selMin.y;
                int origPosZ = (selMin.z + selMax.z) / 2;
                int offsetX = x - origPosX;
                int offsetY = y - origPosY;
                int offsetZ = z - origPosZ;
                Vector3i pasteMin = new Vector3i(selMin.x + offsetX, selMin.y + offsetY, selMin.z + offsetZ);
                Vector3i pasteMax = new Vector3i(selMax.x + offsetX, selMax.y + offsetY, selMax.z + offsetZ);
                BlockSelection selectionToPlace = this.selection;
                if (technicalPaste) {
                    selectionToPlace = this.convertEmptyBlocksToEditorEmpty(this.selection);
                }
                selectionToPlace.setPosition(x, y, z);
                int prefabId = PrefabUtil.getNextPrefabId();
                selectionToPlace.setPrefabId(prefabId);
                if (!BuilderToolsPlugin.onPasteStart(prefabId, componentAccessor)) {
                    this.sendErrorFeedback(ref, Message.translation("server.builderTools.pasteCancelledByEvent"), componentAccessor);
                    return 0;
                }
                int entityCount = selectionToPlace.getEntityCount();
                ObjectArrayList snapshots = new ObjectArrayList(entityCount + 1);
                Consumer<Ref<EntityStore>> collector = BlockSelection.DEFAULT_ENTITY_CONSUMER;
                if (entityCount > 0) {
                    collector = e -> snapshots.add(new EntityAddSnapshot((Ref<EntityStore>)e));
                }
                BlockSelection before = selectionToPlace.place(this.player, world, Vector3i.ZERO, this.globalMask, collector);
                before.setSelectionArea(pasteMin, pasteMax);
                snapshots.add(new BlockSelectionSnapshot(before));
                this.pushHistory(Action.PASTE, snapshots);
                BuilderToolsPlugin.invalidateWorldMapForBounds(pasteMin, pasteMax, world);
                BuilderToolsPlugin.get().onPasteEnd(prefabId, componentAccessor);
                selectionToPlace.setPrefabId(-1);
                selectionToPlace.setPosition(0, 0, 0);
                long end = System.nanoTime();
                long diff = end - start;
                int size = selectionToPlace.getBlockCount();
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute paste of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
                this.sendFeedback(Message.translation("server.builderTools.pastedBlocks").param("count", size), componentAccessor);
                return size;
            }
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            return 0;
        }

        private BlockSelection convertEmptyBlocksToEditorEmpty(@Nonnull BlockSelection original) {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            int editorBlockPrefabAir = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_AIR);
            if (editorBlockPrefabAir == Integer.MIN_VALUE) {
                return original;
            }
            BlockSelection converted = new BlockSelection(original.getBlockCount(), original.getEntityCount());
            converted.setPosition(original.getX(), original.getY(), original.getZ());
            converted.setAnchor(original.getAnchorX(), original.getAnchorY(), original.getAnchorZ());
            converted.setSelectionArea(original.getSelectionMin(), original.getSelectionMax());
            original.forEachBlock((x, y, z, block) -> {
                int blockId = block.blockId() == 0 ? editorBlockPrefabAir : block.blockId();
                converted.addBlockAtLocalPos(x, y, z, blockId, block.rotation(), block.filler(), block.supportValue(), block.holder());
            });
            original.forEachFluid((x, y, z, fluidId, fluidLevel) -> converted.addFluidAtLocalPos(x, y, z, fluidId, fluidLevel));
            original.forEachEntity(holder -> converted.addEntityHolderRaw((Holder<EntityStore>)holder.clone()));
            return converted;
        }

        public void rotate(@Nonnull Ref<EntityStore> ref, @Nonnull Axis axis, int angle, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null) {
                long start = System.nanoTime();
                this.pushHistory(Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
                this.selection = this.selection.rotate(axis, angle);
                long end = System.nanoTime();
                long diff = end - start;
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute rotate of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
                this.sendUpdate();
                this.sendFeedback(Message.translation("server.builderTools.clipboardRotatedBy").param("angle", angle).param("axis", axis.toString()), componentAccessor);
            } else {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            }
        }

        public void rotate(@Nonnull Ref<EntityStore> ref, @Nonnull Axis axis, int angle, @Nonnull Vector3f originOfRotation, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null) {
                long start = System.nanoTime();
                this.pushHistory(Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
                this.selection = this.selection.rotate(axis, angle, originOfRotation);
                long end = System.nanoTime();
                long diff = end - start;
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute rotate of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
                this.sendUpdate();
                this.sendFeedback(Message.translation("server.builderTools.clipboardRotatedBy").param("angle", angle).param("axis", axis.toString()), componentAccessor);
            } else {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            }
        }

        public void rotateArbitrary(@Nonnull Ref<EntityStore> ref, float yaw, float pitch, float roll, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null) {
                long start = System.nanoTime();
                this.pushHistory(Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
                this.selection = this.selection.rotateArbitrary(yaw, pitch, roll);
                long end = System.nanoTime();
                long diff = end - start;
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute arbitrary rotate of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
                this.sendUpdate();
                Message message = Message.translation("server.builderTools.clipboardRotatedArbitrary").param("yaw", yaw).param("pitch", pitch).param("roll", roll);
                this.sendFeedback(message, componentAccessor);
            } else {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            }
        }

        public void flip(@Nonnull Ref<EntityStore> ref, @Nonnull Axis axis, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null) {
                long start = System.nanoTime();
                this.pushHistory(Action.FLIP, ClipboardContentsSnapshot.copyOf(this.selection));
                this.selection = this.selection.flip(axis);
                long end = System.nanoTime();
                long diff = end - start;
                BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute flip of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
                this.sendUpdate();
                this.sendFeedback(Message.translation("server.builderTools.clipboardFlipped").param("axis", axis.toString()), componentAccessor);
            } else {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            }
        }

        public void hollow(@Nonnull Ref<EntityStore> ref, final int blockId, int thickness, boolean setTop, boolean setBottom, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            final Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final BlockSelection before = new BlockSelection();
            before.setPosition(min.x, min.y, min.z);
            before.setSelectionArea(min, max);
            final BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            final LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, max.x + 1 - min.x, max.z + 1 - min.z, Math.max(max.x + 1 - min.x, Math.max(max.y + 1 - min.y, max.z + 1 - min.z)));
            BlockCubeUtil.forEachBlock(min, max, thickness, !setTop, !setBottom, true, null, new TriIntObjPredicate<Void>(){
                private int previousX = Integer.MIN_VALUE;
                private int previousZ = Integer.MIN_VALUE;
                @Nullable
                private WorldChunk currentChunk;
                final /* synthetic */ BuilderState this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean test(int x, int y, int z, Void unused) {
                    if (this.previousX != x || this.previousZ != z) {
                        this.previousX = x;
                        this.previousZ = z;
                        this.currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    }
                    int currentBlockId = this.currentChunk.getBlock(x, y, z);
                    int currentFluidId = this.currentChunk.getFluidId(x, y, z);
                    if (this.this$0.globalMask != null && this.this$0.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlockId, currentFluidId)) {
                        return true;
                    }
                    Holder<ChunkStore> holder = this.currentChunk.getBlockComponentHolder(x, y, z);
                    Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(this.currentChunk, x, y, z, blockId, currentBlockId, holder, false);
                    int supportValue = this.currentChunk.getSupportValue(x, y, z);
                    int filler = this.currentChunk.getFiller(x, y, z);
                    int rotation = this.currentChunk.getRotationIndex(x, y, z);
                    before.addBlockAtWorldPos(x, y, z, currentBlockId, filler, rotation, supportValue, holder);
                    after.addBlockAtWorldPos(x, y, z, blockId, 0, 0, 0, newHolder);
                    return true;
                }
            });
            this.pushHistory(Action.HOLLOW, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Hollow 1/1", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        public void walls(@Nonnull Ref<EntityStore> ref, int blockId, int thickness, boolean cappedTop, boolean cappedBottom, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.walls(ref, BlockPattern.parse(BlockType.getAssetMap().getAsset(blockId).getId()), thickness, cappedTop, cappedBottom, componentAccessor);
        }

        public void walls(@Nonnull Ref<EntityStore> ref, final @Nonnull BlockPattern pattern, int thickness, boolean cappedTop, boolean cappedBottom, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (pattern.isEmpty()) {
                return;
            }
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            final Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final BlockSelection before = new BlockSelection();
            before.setPosition(min.x, min.y, min.z);
            before.setSelectionArea(min, max);
            final BlockSelection after = new BlockSelection(before);
            final LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, max.x + 1 - min.x, max.z + 1 - min.z, Math.max(max.x + 1 - min.x, Math.max(max.y + 1 - min.y, max.z + 1 - min.z)));
            BlockCubeUtil.forEachBlock(min, max, thickness, cappedTop, cappedBottom, false, null, new TriIntObjPredicate<Void>(){
                private int previousX = Integer.MIN_VALUE;
                private int previousZ = Integer.MIN_VALUE;
                @Nullable
                private WorldChunk currentChunk;
                final /* synthetic */ BuilderState this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public boolean test(int x, int y, int z, Void unused) {
                    if (this.previousX != x || this.previousZ != z) {
                        this.previousX = x;
                        this.previousZ = z;
                        this.currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    }
                    int currentBlockId = this.currentChunk.getBlock(x, y, z);
                    int currentFluidId = this.currentChunk.getFluidId(x, y, z);
                    if (this.this$0.globalMask != null && this.this$0.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlockId, currentFluidId)) {
                        return true;
                    }
                    Material material = Material.fromPattern(pattern, this.this$0.random);
                    if (material.isFluid()) {
                        byte currentFluidLevel = this.currentChunk.getFluidLevel(x, y, z);
                        before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                        after.addFluidAtWorldPos(x, y, z, material.getFluidId(), material.getFluidLevel());
                    } else {
                        int newBlockId = material.getBlockId();
                        int newRotation = material.getRotation();
                        Holder<ChunkStore> holder = this.currentChunk.getBlockComponentHolder(x, y, z);
                        Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(this.currentChunk, x, y, z, newBlockId, currentBlockId, holder, false);
                        int supportValue = this.currentChunk.getSupportValue(x, y, z);
                        int filler = this.currentChunk.getFiller(x, y, z);
                        int rotation = this.currentChunk.getRotationIndex(x, y, z);
                        before.addBlockAtWorldPos(x, y, z, currentBlockId, rotation, filler, supportValue, holder);
                        after.addBlockAtWorldPos(x, y, z, newBlockId, newRotation, 0, 0, newHolder);
                        if (newBlockId == 0) {
                            int fluidId = this.currentChunk.getFluidId(x, y, z);
                            byte fluidLevel = this.currentChunk.getFluidLevel(x, y, z);
                            if (fluidId != 0) {
                                before.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                                after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                            }
                        }
                    }
                    return true;
                }
            });
            this.pushHistory(Action.WALLS, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Walls 1/1", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute walls of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        public void set(int blockId, ComponentAccessor<EntityStore> componentAccessor) {
            this.set(BlockPattern.parse(BlockType.getAssetMap().getAsset(blockId).getId()), componentAccessor);
        }

        public void set(@Nonnull BlockPattern pattern, ComponentAccessor<EntityStore> componentAccessor) {
            if (pattern.isEmpty()) {
                return;
            }
            if (this.selection == null) {
                this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.SET, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        int currentBlock = chunk.getBlock(x, y, z);
                        int currentFluid = chunk.getFluidId(x, y, z);
                        if (this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlock, currentFluid)) {
                            ++counter;
                            continue;
                        }
                        Material material = Material.fromPattern(pattern, this.random);
                        if (material.isFluid()) {
                            byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                            before.addFluidAtWorldPos(x, y, z, currentFluid, currentFluidLevel);
                            after.addFluidAtWorldPos(x, y, z, material.getFluidId(), material.getFluidLevel());
                        } else {
                            int newBlockId = material.getBlockId();
                            int newRotation = material.getRotation();
                            Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                            Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, newBlockId, currentBlock, holder, false);
                            int supportValue = chunk.getSupportValue(x, y, z);
                            int filler = chunk.getFiller(x, y, z);
                            int rotation = chunk.getRotationIndex(x, y, z);
                            before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, filler, supportValue, holder);
                            after.addBlockAtWorldPos(x, y, z, newBlockId, newRotation, 0, 0, newHolder);
                            if (newBlockId == 0) {
                                int fluidId = chunk.getFluidId(x, y, z);
                                byte fluidLevel = chunk.getFluidLevel(x, y, z);
                                if (fluidId != 0) {
                                    before.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                                    after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                                }
                            }
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Set 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
            this.sendUpdate();
            this.sendArea();
        }

        public void fill(@Nonnull BlockPattern pattern, ComponentAccessor<EntityStore> componentAccessor) {
            if (pattern.isEmpty()) {
                return;
            }
            if (this.selection == null) {
                this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.EDIT, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        Material material = Material.fromPattern(pattern, this.random);
                        if (material.isFluid()) {
                            int currentFluidId = chunk.getFluidId(x, y, z);
                            if (currentFluidId == 0) {
                                byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                                before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                                after.addFluidAtWorldPos(x, y, z, material.getFluidId(), material.getFluidLevel());
                            }
                        } else {
                            int currentBlock = chunk.getBlock(x, y, z);
                            if (currentBlock == 0) {
                                int newBlockId = material.getBlockId();
                                int newRotation = material.getRotation();
                                Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                                Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, newBlockId, currentBlock, holder, false);
                                int supportValue = chunk.getSupportValue(x, y, z);
                                int filler = chunk.getFiller(x, y, z);
                                int rotation = chunk.getRotationIndex(x, y, z);
                                before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, filler, supportValue, holder);
                                after.addBlockAtWorldPos(x, y, z, newBlockId, newRotation, 0, 0, newHolder);
                            }
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Fill 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute fill of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
            this.sendUpdate();
            this.sendArea();
        }

        public void replace(@Nonnull Ref<EntityStore> ref, @Nonnull Material from, @Nonnull Material to, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.REPLACE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        int currentBlock;
                        int currentFiller = chunk.getFiller(x, y, z);
                        if (currentFiller != 0) {
                            this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                            continue;
                        }
                        boolean shouldReplace = false;
                        if (from.isFluid()) {
                            int currentFluidId = chunk.getFluidId(x, y, z);
                            shouldReplace = currentFluidId == from.getFluidId();
                        } else {
                            currentBlock = chunk.getBlock(x, y, z);
                            boolean bl = shouldReplace = currentBlock == from.getBlockId();
                        }
                        if (shouldReplace) {
                            currentBlock = chunk.getBlock(x, y, z);
                            int currentFluidId = chunk.getFluidId(x, y, z);
                            byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                            if (to.isFluid()) {
                                if (currentBlock != 0) {
                                    holder = chunk.getBlockComponentHolder(x, y, z);
                                    int rotation = chunk.getRotationIndex(x, y, z);
                                    supportValue = chunk.getSupportValue(x, y, z);
                                    before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, currentFiller, supportValue, holder);
                                    after.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                    this.clearFillerBlocksIfNeeded(x, y, z, currentBlock, rotation, accessor, before, after);
                                }
                                before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                                after.addFluidAtWorldPos(x, y, z, to.getFluidId(), to.getFluidLevel());
                            } else if (to.isEmpty()) {
                                holder = chunk.getBlockComponentHolder(x, y, z);
                                int rotation = chunk.getRotationIndex(x, y, z);
                                supportValue = chunk.getSupportValue(x, y, z);
                                before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, currentFiller, supportValue, holder);
                                after.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                this.clearFillerBlocksIfNeeded(x, y, z, currentBlock, rotation, accessor, before, after);
                                if (currentFluidId != 0) {
                                    before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                                    after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                                }
                            } else {
                                if (currentFluidId != 0) {
                                    before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                                    after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                                }
                                holder = chunk.getBlockComponentHolder(x, y, z);
                                Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, to.getBlockId(), currentBlock, holder, true);
                                int rotation = chunk.getRotationIndex(x, y, z);
                                int supportValue = chunk.getSupportValue(x, y, z);
                                before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, currentFiller, supportValue, holder);
                                after.addBlockAtWorldPos(x, y, z, to.getBlockId(), rotation, 0, 0, newHolder);
                                this.replaceMultiBlockStructure(x, y, z, currentBlock, to.getBlockId(), rotation, accessor, before, after);
                            }
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Replace 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute replace", diff, TimeUnit.NANOSECONDS.toMillis(diff));
            this.sendUpdate();
            this.sendArea();
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_SELECTION_FILL"), SoundCategory.SFX, componentAccessor);
        }

        private void clearFillerBlocksIfNeeded(int baseX, int baseY, int baseZ, int oldBlockId, int rotationIndex, LocalCachedChunkAccessor accessor, BlockSelection before, BlockSelection after) {
            this.replaceMultiBlockStructure(baseX, baseY, baseZ, oldBlockId, 0, rotationIndex, accessor, before, after);
        }

        private void replaceMultiBlockStructure(int baseX, int baseY, int baseZ, int oldBlockId, int newBlockId, int rotationIndex, LocalCachedChunkAccessor accessor, BlockSelection before, BlockSelection after) {
            BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
            IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
            BlockType oldBlockType = blockTypeAssetMap.getAsset(oldBlockId);
            BlockBoundingBoxes oldHitbox = null;
            if (oldBlockType != null) {
                oldHitbox = hitboxAssetMap.getAsset(oldBlockType.getHitboxTypeIndex());
            }
            BlockType newBlockType = blockTypeAssetMap.getAsset(newBlockId);
            BlockBoundingBoxes newHitbox = null;
            if (newBlockType != null) {
                newHitbox = hitboxAssetMap.getAsset(newBlockType.getHitboxTypeIndex());
            }
            if (oldHitbox != null && oldHitbox.protrudesUnitBox()) {
                BlockBoundingBoxes finalNewHitbox = newHitbox;
                FillerBlockUtil.forEachFillerBlock(oldHitbox.get(rotationIndex), (fx, fy, fz) -> {
                    if (fx == 0 && fy == 0 && fz == 0) {
                        return;
                    }
                    int fillerX = baseX + fx;
                    int fillerY = baseY + fy;
                    int fillerZ = baseZ + fz;
                    WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                    int fillerBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                    int fillerFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                    if (fillerFiller != 0) {
                        boolean willBeFilledByNewStructure;
                        Holder<ChunkStore> fillerHolder = fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ);
                        before.addBlockAtWorldPos(fillerX, fillerY, fillerZ, fillerBlock, rotationIndex, fillerFiller, fillerChunk.getSupportValue(fillerX, fillerY, fillerZ), fillerHolder);
                        boolean bl = willBeFilledByNewStructure = finalNewHitbox != null && finalNewHitbox.protrudesUnitBox() && finalNewHitbox.get(rotationIndex).getBoundingBox().containsBlock(fx, fy, fz);
                        if (!willBeFilledByNewStructure) {
                            after.addBlockAtWorldPos(fillerX, fillerY, fillerZ, 0, 0, 0, 0);
                        }
                    }
                });
            }
            if (newHitbox != null && newHitbox.protrudesUnitBox()) {
                FillerBlockUtil.forEachFillerBlock(newHitbox.get(rotationIndex), (fx, fy, fz) -> {
                    if (fx == 0 && fy == 0 && fz == 0) {
                        return;
                    }
                    int fillerX = baseX + fx;
                    int fillerY = baseY + fy;
                    int fillerZ = baseZ + fz;
                    WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                    int existingBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                    int existingFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                    if (existingFiller == 0 && !before.hasBlockAtWorldPos(fillerX, fillerY, fillerZ)) {
                        Holder<ChunkStore> fillerHolder = fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ);
                        before.addBlockAtWorldPos(fillerX, fillerY, fillerZ, existingBlock, rotationIndex, existingFiller, fillerChunk.getSupportValue(fillerX, fillerY, fillerZ), fillerHolder);
                    }
                    int newFiller = FillerBlockUtil.pack(fx, fy, fz);
                    after.addBlockAtWorldPos(fillerX, fillerY, fillerZ, newBlockId, rotationIndex, newFiller, 0);
                });
            }
        }

        public void replace(@Nonnull Ref<EntityStore> ref, @Nullable IntPredicate doReplace, @Nonnull BlockPattern toPattern, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.REPLACE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        int filler = chunk.getFiller(x, y, z);
                        if (filler != 0) {
                            this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                            continue;
                        }
                        int block = chunk.getBlock(x, y, z);
                        if (doReplace == null && block != 0 || doReplace != null && doReplace.test(block)) {
                            Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                            Material material = Material.fromPattern(toPattern, this.random);
                            int newBlockId = material.getBlockId();
                            int newRotation = material.hasRotation() ? material.getRotation() : chunk.getRotationIndex(x, y, z);
                            Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, newBlockId, block, holder, true);
                            int rotationIndex = chunk.getRotationIndex(x, y, z);
                            before.addBlockAtWorldPos(x, y, z, block, rotationIndex, filler, chunk.getSupportValue(x, y, z), chunk.getBlockComponentHolder(x, y, z));
                            after.addBlockAtWorldPos(x, y, z, newBlockId, newRotation, 0, 0, newHolder);
                            this.replaceMultiBlockStructure(x, y, z, block, newBlockId, newRotation, accessor, before, after);
                            if (newBlockId == 0) {
                                int fluidId = chunk.getFluidId(x, y, z);
                                byte fluidLevel = chunk.getFluidLevel(x, y, z);
                                if (fluidId != 0) {
                                    before.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                                    after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                                }
                            }
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Replace 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute replace of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_SELECTION_FILL"), SoundCategory.SFX, componentAccessor);
        }

        public void replace(@Nonnull Ref<EntityStore> ref, @Nonnull Int2IntFunction function, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.REPLACE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        int replace;
                        int filler = chunk.getFiller(x, y, z);
                        if (filler != 0) {
                            this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                            continue;
                        }
                        int block = chunk.getBlock(x, y, z);
                        if (block != (replace = function.applyAsInt(block))) {
                            Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                            Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, replace, block, holder, true);
                            int rotationIndex = chunk.getRotationIndex(x, y, z);
                            before.addBlockAtWorldPos(x, y, z, block, rotationIndex, filler, chunk.getSupportValue(x, y, z), holder);
                            after.addBlockAtWorldPos(x, y, z, replace, rotationIndex, 0, 0, newHolder);
                            this.replaceMultiBlockStructure(x, y, z, block, replace, rotationIndex, accessor, before, after);
                            if (replace == 0) {
                                int fluidId = chunk.getFluidId(x, y, z);
                                byte fluidLevel = chunk.getFluidLevel(x, y, z);
                                if (fluidId != 0) {
                                    before.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                                    after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                                }
                            }
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.placeNoReturn("Replace 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute replace of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        public void move(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, boolean empty, boolean entities, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            BlockSelection selected = new BlockSelection();
            int width = xMax - xMin;
            int height = yMax - yMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            int xPos = xMin + halfWidth;
            int yPos = yMin;
            int zPos = zMin + halfDepth;
            selected.setPosition(xPos, yPos, zPos);
            BlockSelection cleared = new BlockSelection(selected);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth) + 16);
            BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
            IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        BlockBoundingBoxes hitbox;
                        BlockType blockType;
                        int block = chunk.getBlock(x, y, z);
                        int fluidId = chunk.getFluidId(x, y, z);
                        byte fluidLevel = chunk.getFluidLevel(x, y, z);
                        if (block == 0 && fluidId == 0 && !empty || this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, block, fluidId)) continue;
                        int filler = chunk.getFiller(x, y, z);
                        int rotationIndex = chunk.getRotationIndex(x, y, z);
                        selected.addBlockAtWorldPos(x, y, z, block, rotationIndex, filler, chunk.getSupportValue(x, y, z), chunk.getBlockComponentHolder(x, y, z));
                        selected.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                        cleared.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                        cleared.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                        if (filler != 0 || block == 0 || (blockType = blockTypeAssetMap.getAsset(block)) == null || (hitbox = hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex())) == null || !hitbox.protrudesUnitBox()) continue;
                        int baseX = x;
                        int baseY = y;
                        int baseZ = z;
                        FillerBlockUtil.forEachFillerBlock(hitbox.get(rotationIndex), (fx, fy, fz) -> {
                            if (fx == 0 && fy == 0 && fz == 0) {
                                return;
                            }
                            int fillerX = baseX + fx;
                            int fillerY = baseY + fy;
                            int fillerZ = baseZ + fz;
                            if (fillerX >= xMin && fillerX <= xMax && fillerY >= yMin && fillerY <= yMax && fillerZ >= zMin && fillerZ <= zMax) {
                                return;
                            }
                            WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                            int fillerBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                            int fillerFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                            if (fillerFiller != 0) {
                                int fillerRotation = fillerChunk.getRotationIndex(fillerX, fillerY, fillerZ);
                                selected.addBlockAtWorldPos(fillerX, fillerY, fillerZ, fillerBlock, fillerRotation, fillerFiller, fillerChunk.getSupportValue(fillerX, fillerY, fillerZ), fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ));
                                cleared.addBlockAtWorldPos(fillerX, fillerY, fillerZ, 0, 0, 0, 0);
                            }
                        });
                    }
                }
            }
            BlockSelection beforeCleared = cleared.place(this.player, world);
            selected.setPosition(xPos + direction.getX(), yPos + direction.getY(), zPos + direction.getZ());
            BlockSelection beforePlace = selected.place(this.player, world);
            ObjectArrayList snapshots = new ObjectArrayList();
            if (entities) {
                List<Ref<EntityStore>> targetEntities = TargetUtil.getAllEntitiesInBox(min.toVector3d(), max.toVector3d(), componentAccessor);
                for (Ref<EntityStore> targetEntityRef : targetEntities) {
                    snapshots.add(new EntityTransformSnapshot(targetEntityRef, componentAccessor));
                    TransformComponent transformComponent = componentAccessor.getComponent(targetEntityRef, TransformComponent.getComponentType());
                    if (transformComponent == null) continue;
                    transformComponent.getPosition().add(direction);
                }
            }
            beforePlace.add(beforeCleared);
            ClipboardBoundsSnapshot clipboardSnapshot = new ClipboardBoundsSnapshot(min, max);
            Vector3i destMin = min.clone().add(direction);
            Vector3i destMax = max.clone().add(direction);
            beforePlace.setSelectionArea(Vector3i.min(min, destMin), Vector3i.max(max, destMax));
            snapshots.add(new BlockSelectionSnapshot(beforePlace));
            snapshots.add(clipboardSnapshot);
            this.pushHistory(Action.MOVE, snapshots);
            BuilderToolsPlugin.invalidateWorldMapForSelection(cleared, world);
            BuilderToolsPlugin.invalidateWorldMapForSelection(selected, world);
            this.selection.setSelectionArea(min.add(direction), max.add(direction));
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute move of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), cleared.getBlockCount());
            this.sendUpdate();
            this.sendArea();
            this.sendFeedback(Message.translation("server.builderTools.selectionMovedBy").param("x", direction.getX()).param("y", direction.getY()).param("z", direction.getZ()), componentAccessor);
        }

        public void shift(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            this.selection.setSelectionArea(this.selection.getSelectionMin().add(direction), this.selection.getSelectionMax().add(direction));
            this.sendArea();
            this.sendFeedback(Message.translation("server.builderTools.selectionShiftedBy").param("x", direction.getX()).param("y", direction.getY()).param("z", direction.getZ()), componentAccessor);
        }

        public void pos1(@Nonnull Vector3i pos1, ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null && !this.selection.getSelectionMax().equals(Vector3i.ZERO)) {
                this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
                this.selection.setSelectionArea(pos1, this.selection.getSelectionMax());
                this.sendArea();
            } else {
                if (this.selection == null) {
                    this.selection = new BlockSelection();
                }
                this.pushHistory(Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
                this.selection.setSelectionArea(pos1, pos1);
                this.sendArea();
            }
            this.sendFeedback(Message.translation("server.builderTools.setPosTo").param("num", 1).param("x", pos1.getX()).param("y", pos1.getY()).param("z", pos1.getZ()), componentAccessor);
        }

        public void pos2(@Nonnull Vector3i pos2, ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null && !this.selection.getSelectionMin().equals(Vector3i.ZERO)) {
                this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
                this.selection.setSelectionArea(this.selection.getSelectionMin(), pos2);
                this.sendArea();
            } else {
                if (this.selection == null) {
                    this.selection = new BlockSelection();
                }
                this.pushHistory(Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
                this.selection.setSelectionArea(pos2, pos2);
                this.sendArea();
            }
            this.sendFeedback(Message.translation("server.builderTools.setPosTo").param("num", 2).param("x", pos2.getX()).param("y", pos2.getY()).param("z", pos2.getZ()), componentAccessor);
        }

        public void select(@Nonnull Vector3i pos1, @Nonnull Vector3i pos2, @Nullable String reason, ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null && !this.selection.getSelectionMax().equals(Vector3i.ZERO)) {
                this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
                this.selection.setSelectionArea(pos1, pos2);
                this.sendArea();
            } else {
                if (this.selection == null) {
                    this.selection = new BlockSelection();
                }
                this.pushHistory(Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
                this.selection.setSelectionArea(pos1, pos2);
                this.sendArea();
            }
            if (reason != null) {
                Message reasonMessage = Message.translation(reason);
                this.sendFeedback(Message.translation("server.builderTools.selectedWithReason").param("reason", reasonMessage).param("x1", pos1.getX()).param("y1", pos1.getY()).param("z1", pos1.getZ()).param("x2", pos2.getX()).param("y2", pos2.getY()).param("z2", pos2.getZ()), componentAccessor);
            } else {
                this.sendFeedback(Message.translation("server.builderTools.selected").param("x1", pos1.getX()).param("y1", pos1.getY()).param("z1", pos1.getZ()).param("x2", pos2.getX()).param("y2", pos2.getY()).param("z2", pos2.getZ()), componentAccessor);
            }
        }

        public void deselect(ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection != null && this.selection.hasSelectionBounds()) {
                this.selection.setSelectionArea(Vector3i.ZERO, Vector3i.ZERO);
                EditorBlocksChange packet = new EditorBlocksChange();
                packet.selection = null;
                this.player.getPlayerConnection().write((Packet)packet);
                this.sendFeedback(Message.translation("server.builderTools.deselected"), componentAccessor);
            } else {
                this.sendFeedback(Message.translation("server.builderTools.noSelectionToDeselect"), componentAccessor);
            }
        }

        public void stack(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, int count, boolean empty, int spacing, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            BlockSelection selected = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            selected.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    for (int y = yMax; y >= yMin; --y) {
                        int block = chunk.getBlock(x, y, z);
                        int fluidId = chunk.getFluidId(x, y, z);
                        byte fluidLevel = chunk.getFluidLevel(x, y, z);
                        if (block == 0 && fluidId == 0 && !empty || this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, block, fluidId)) continue;
                        selected.addBlockAtWorldPos(x, y, z, block, chunk.getRotationIndex(x, y, z), chunk.getFiller(x, y, z), chunk.getSupportValue(x, y, z), chunk.getBlockComponentHolder(x, y, z));
                        selected.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                    }
                }
            }
            BlockSelection before = new BlockSelection();
            before.setAnchor(selected.getAnchorX(), selected.getAnchorY(), selected.getAnchorZ());
            before.setPosition(selected.getX(), selected.getY(), selected.getZ());
            Vector3i size = max.subtract(min).add(1, 1, 1);
            for (int i = 1; i <= count; ++i) {
                selected.setPosition(before.getX() + (size.getX() + spacing) * direction.getX() * i, before.getY() + (size.getY() + spacing) * direction.getY() * i, before.getZ() + (size.getZ() + spacing) * direction.getZ() * i);
                before.add(selected.place(this.player, world));
            }
            Vector3i stackOffset = new Vector3i((size.getX() + spacing) * direction.getX() * count, (size.getY() + spacing) * direction.getY() * count, (size.getZ() + spacing) * direction.getZ() * count);
            Vector3i totalMin = Vector3i.min(min, min.add(stackOffset));
            Vector3i totalMax = Vector3i.max(max, max.add(stackOffset));
            before.setSelectionArea(totalMin, totalMax);
            this.pushHistory(Action.STACK, new BlockSelectionSnapshot(before));
            BuilderToolsPlugin.invalidateWorldMapForSelection(before, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute stack of %d blocks %d times", diff, TimeUnit.NANOSECONDS.toMillis(diff), selected.getBlockCount(), count);
            this.sendUpdate();
            this.sendArea();
            this.sendFeedback(Message.translation("server.builderTools.selectionStacked").param("count", count).param("x", direction.getX()).param("y", direction.getY()).param("z", direction.getZ()), componentAccessor);
        }

        public void expand(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            if (direction.getX() < 0) {
                min = min.add(direction.getX(), 0, 0);
            } else if (direction.getX() > 0) {
                max = max.add(direction.getX(), 0, 0);
            }
            if (direction.getY() < 0) {
                min = min.add(0, direction.getY(), 0);
            } else if (direction.getY() > 0) {
                max = max.add(0, direction.getY(), 0);
            }
            if (direction.getZ() < 0) {
                min = min.add(0, 0, direction.getZ());
            } else if (direction.getZ() > 0) {
                max = max.add(0, 0, direction.getZ());
            }
            this.selection.setSelectionArea(min, max);
            this.sendArea();
            this.sendFeedback(Message.translation("server.builderTools.selectionExpanded").param("x", direction.getX()).param("y", direction.getY()).param("z", direction.getZ()), componentAccessor);
        }

        public void contract(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            this.pushHistory(Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            if (direction.getX() > 0) {
                min = min.add(direction.getX(), 0, 0);
            } else if (direction.getX() < 0) {
                max = max.add(direction.getX(), 0, 0);
            }
            if (direction.getY() > 0) {
                min = min.add(0, direction.getY(), 0);
            } else if (direction.getY() < 0) {
                max = max.add(0, direction.getY(), 0);
            }
            if (direction.getZ() > 0) {
                min = min.add(0, 0, direction.getZ());
            } else if (direction.getZ() < 0) {
                max = max.add(0, 0, direction.getZ());
            }
            this.selection.setSelectionArea(min, max);
            this.sendArea();
            this.sendFeedback(ref, Message.translation("server.builderTools.selectionContracted").param("x", direction.getX()).param("y", direction.getY()).param("z", direction.getZ()), direction.length() > 0.0 ? "CREATE_SCALE_INCREASE" : "CREATE_SCALE_DECREASE", componentAccessor);
        }

        public void repairFillers(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            if (!this.selection.hasSelectionBounds()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int xMax = max.getX();
            int yMin = min.getY();
            int yMax = max.getY();
            int zMin = min.getZ();
            int zMax = max.getZ();
            int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
            int width = xMax - xMin;
            int height = yMax - yMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            int halfDepth = depth / 2;
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(Action.SET, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            World world = componentAccessor.getExternalData().getWorld();
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            CachedAccessor cachedAccessor = CachedAccessor.of(chunkStore, ChunkUtil.chunkCoordinate(xMin + halfWidth), ChunkUtil.chunkCoordinate(yMin + halfHeight), ChunkUtil.chunkCoordinate(zMin + halfDepth), Math.max(Math.max(width, depth), height));
            BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
            IndexedLookupTableAssetMap<String, BlockBoundingBoxes> blockHitboxMap = BlockBoundingBoxes.getAssetMap();
            int counter = 0;
            for (int x = xMin; x <= xMax; ++x) {
                int cx = ChunkUtil.chunkCoordinate(x);
                for (int z = zMin; z <= zMax; ++z) {
                    int cz = ChunkUtil.chunkCoordinate(z);
                    Ref<ChunkStore> chunkRef = cachedAccessor.getChunk(cx, cz);
                    WorldChunk wc = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
                    for (int y = yMax; y >= yMin; --y) {
                        int block;
                        BlockType blockType;
                        int cy = ChunkUtil.chunkCoordinate(y);
                        BlockSection chunk = cachedAccessor.getBlockSection(cx, cy, cz);
                        if (chunk == null || (blockType = blockTypeMap.getAsset(block = chunk.get(x, y, z))) == null) continue;
                        BlockPhysics physics = cachedAccessor.getBlockPhysics(cx, cy, cz);
                        BlockBoundingBoxes hitbox = blockHitboxMap.getAsset(blockType.getHitboxTypeIndex());
                        if (chunk.getFiller(x, y, z) != 0) {
                            before.copyFromAtWorld(x, y, z, wc, physics);
                            after.copyFromAtWorld(x, y, z, wc, physics);
                        } else if (hitbox != null && hitbox.protrudesUnitBox()) {
                            before.copyFromAtWorld(x, y, z, wc, physics);
                            after.copyFromAtWorld(x, y, z, wc, physics);
                            int finalX = x;
                            int finalY = y;
                            int finalZ = z;
                            FillerBlockUtil.forEachFillerBlock(hitbox.get(chunk.getRotationIndex(x, y, z)), (x1, y1, z1) -> before.copyFromAtWorld(finalX + x1, finalY + y1, finalZ + z1, wc, physics));
                        }
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                    }
                }
            }
            after.tryFixFiller(false);
            after.placeNoReturn("Set 2/2", this.player, FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute repair of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
        }

        @Nonnull
        public List<ActionEntry> undo(@Nonnull Ref<EntityStore> ref, int count, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            ActionEntry action;
            long start = System.nanoTime();
            BlockSelection before = this.selection;
            ObjectArrayList<ActionEntry> list = new ObjectArrayList<ActionEntry>();
            for (int i = 0; i < count && (action = this.historyAction(ref, this.undo, this.redo, componentAccessor)) != null; ++i) {
                list.add(action);
            }
            if (before != this.selection) {
                this.sendUpdate();
            }
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute undo of %d actions", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
            if (list.isEmpty()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.nothingToUndo"), componentAccessor);
            } else {
                int i = 0;
                for (ActionEntry pair : list) {
                    this.sendFeedback(ref, Message.translation("server.builderTools.undoStatus").param("index", ++i).param("action", pair.getAction().name()), "CREATE_UNDO", componentAccessor);
                }
            }
            return list;
        }

        @Nonnull
        public List<ActionEntry> redo(@Nonnull Ref<EntityStore> ref, int count, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            ActionEntry action;
            long start = System.nanoTime();
            ObjectArrayList<ActionEntry> list = new ObjectArrayList<ActionEntry>();
            for (int i = 0; i < count && (action = this.historyAction(ref, this.redo, this.undo, componentAccessor)) != null; ++i) {
                list.add(action);
            }
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute redo of %d actions", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
            if (list.isEmpty()) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.nothingToRedo"), componentAccessor);
            } else {
                int i = 0;
                for (ActionEntry pair : list) {
                    this.sendFeedback(ref, Message.translation("server.builderTools.redoStatus").param("index", ++i).param("action", pair.getAction().name()), "CREATE_REDO", componentAccessor);
                }
            }
            return list;
        }

        public void save(@Nonnull Ref<EntityStore> ref, @Nonnull String name, boolean relativize, boolean overwrite, ComponentAccessor<EntityStore> componentAccessor) {
            PrefabStore prefabStore;
            Path serverPrefabsPath;
            if (this.selection == null) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
                return;
            }
            long start = System.nanoTime();
            if (!((String)name).endsWith(".prefab.json")) {
                name = (String)name + ".prefab.json";
            }
            if (!PathUtil.isChildOf(serverPrefabsPath = (prefabStore = PrefabStore.get()).getServerPrefabsPath(), serverPrefabsPath.resolve((String)name)) && !SingleplayerModule.isOwner(this.playerRef)) {
                this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
                return;
            }
            try {
                BlockSelection postClone = relativize ? this.selection.relativize() : this.selection.cloneSelection();
                prefabStore.saveServerPrefab((String)name, postClone, overwrite);
                this.sendUpdate();
                this.sendFeedback(Message.translation("server.builderTools.savedSelectionToPrefab").param("name", (String)name), componentAccessor);
            }
            catch (PrefabSaveException e) {
                switch (e.getType()) {
                    case ERROR: {
                        ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("Exception saving prefab %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.errorSavingPrefab").param("name", (String)name).param("message", e.getCause().getMessage()), componentAccessor);
                        break;
                    }
                    case ALREADY_EXISTS: {
                        BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.prefabAlreadyExists"), componentAccessor);
                    }
                }
            }
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute save of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
        }

        public void saveFromSelection(@Nonnull Ref<EntityStore> ref, @Nonnull String name, boolean relativize, boolean overwrite, boolean includeEntities, boolean includeEmpty, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.saveFromSelection(ref, name, relativize, overwrite, includeEntities, includeEmpty, null, componentAccessor);
        }

        public void saveFromSelection(@Nonnull Ref<EntityStore> ref, @Nonnull String name, boolean relativize, boolean overwrite, boolean includeEntities, boolean includeEmpty, @Nullable Vector3i playerAnchor, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            PrefabStore prefabStore;
            Path serverPrefabsPath;
            if (this.selection == null || this.selection.getSelectionMin().equals(Vector3i.ZERO) && this.selection.getSelectionMax().equals(Vector3i.ZERO)) {
                this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
                return;
            }
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            if (!((String)name).endsWith(".prefab.json")) {
                name = (String)name + ".prefab.json";
            }
            if (!PathUtil.isChildOf(serverPrefabsPath = (prefabStore = PrefabStore.get()).getServerPrefabsPath(), serverPrefabsPath.resolve((String)name)) && !SingleplayerModule.isOwner(this.playerRef)) {
                this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
                return;
            }
            Vector3i min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.getX();
            int yMin = min.getY();
            int zMin = min.getZ();
            int xMax = max.getX();
            int yMax = max.getY();
            int zMax = max.getZ();
            int width = xMax - xMin;
            int height = yMax - yMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            int editorBlock = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK);
            int editorBlockPrefabAir = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_AIR);
            int editorBlockPrefabAnchor = assetMap.getIndex(BuilderToolsPlugin.EDITOR_BLOCK_PREFAB_ANCHOR);
            BlockSelection tempSelection = new BlockSelection();
            tempSelection.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            tempSelection.setSelectionArea(min, max);
            int count = 0;
            int top = Math.max(yMin, yMax);
            int bottom = Math.min(yMin, yMax);
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                    Store<ChunkStore> store = chunk.getReference().getStore();
                    ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
                    int lastSection = -1;
                    BlockPhysics blockPhysics = null;
                    for (int y = top; y >= bottom; --y) {
                        int block = chunk.getBlock(x, y, z);
                        int fluid = chunk.getFluidId(x, y, z);
                        if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                            lastSection = ChunkUtil.chunkCoordinate(y);
                            Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                            blockPhysics = section != null ? store.getComponent(section, BlockPhysics.getComponentType()) : null;
                        }
                        if (block == editorBlockPrefabAnchor && playerAnchor == null) {
                            tempSelection.setAnchorAtWorldPos(x, y, z);
                            int id = BuilderToolsPlugin.getNonEmptyNeighbourBlock(accessor, x, y, z);
                            if (id > 0 && id != editorBlockPrefabAir) {
                                tempSelection.addBlockAtWorldPos(x, y, z, id, 0, 0, 0);
                                ++count;
                                continue;
                            }
                            if (id != editorBlockPrefabAir) continue;
                            tempSelection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                            ++count;
                            continue;
                        }
                        if (block == 0 && fluid == 0 && !includeEmpty || block == editorBlock) continue;
                        if (block == editorBlockPrefabAir) {
                            tempSelection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                        } else {
                            tempSelection.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                        }
                        ++count;
                    }
                }
            }
            if (playerAnchor != null) {
                tempSelection.setAnchorAtWorldPos(playerAnchor.getX(), playerAnchor.getY(), playerAnchor.getZ());
            }
            if (includeEntities) {
                Store<EntityStore> entityStore = world.getEntityStore().getStore();
                BuilderToolsPlugin.forEachCopyableInSelection(world, xMin, yMin, zMin, width, height, depth, e -> {
                    Holder<EntityStore> holder = entityStore.copyEntity((Ref<EntityStore>)e);
                    tempSelection.addEntityFromWorld(holder);
                });
            }
            try {
                BlockSelection postClone = relativize ? tempSelection.relativize() : tempSelection.cloneSelection();
                prefabStore.saveServerPrefab((String)name, postClone, overwrite);
                this.sendFeedback(Message.translation("server.builderTools.savedSelectionToPrefab").param("name", (String)name), componentAccessor);
            }
            catch (PrefabSaveException e2) {
                switch (e2.getType()) {
                    case ERROR: {
                        ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e2)).log("Exception saving prefab %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.errorSavingPrefab").param("name", (String)name).param("message", e2.getCause().getMessage()), componentAccessor);
                        break;
                    }
                    case ALREADY_EXISTS: {
                        BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.prefabAlreadyExists"), componentAccessor);
                    }
                }
            }
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute saveFromSelection of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
        }

        public void load(@Nonnull String name, ComponentAccessor<EntityStore> componentAccessor) {
            if (!((String)name).endsWith(".prefab.json")) {
                name = (String)name + ".prefab.json";
            }
            this.load((String)name, PrefabStore.get().getServerPrefab((String)name), componentAccessor);
        }

        public void load(@Nonnull String name, @Nonnull BlockSelection serverPrefab, ComponentAccessor<EntityStore> componentAccessor) {
            long start = System.nanoTime();
            try {
                Vector3i min = Vector3i.ZERO;
                Vector3i max = Vector3i.ZERO;
                if (this.selection != null) {
                    Objects.requireNonNull(this.selection.getSelectionMin(), "min is null");
                    Objects.requireNonNull(this.selection.getSelectionMax(), "max is null");
                    min = Vector3i.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
                    max = Vector3i.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
                }
                this.selection = serverPrefab.cloneSelection();
                this.selection.setSelectionArea(min, max);
                this.sendUpdate();
                this.sendFeedback(Message.translation("server.general.loadedPrefab").param("name", name), componentAccessor);
            }
            catch (PrefabLoadException e) {
                switch (e.getType()) {
                    case ERROR: {
                        ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("Exception loading prefab %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.errorSavingPrefab").param("name", name).param("message", e.getCause().getMessage()), componentAccessor);
                        break;
                    }
                    case NOT_FOUND: {
                        BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab doesn't exist %s", name);
                        this.sendFeedback(Message.translation("server.builderTools.prefabDoesNotExist").param("name", name), componentAccessor);
                    }
                }
            }
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute load of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clearHistory(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            long stamp = this.undoLock.writeLock();
            try {
                this.undo.clear();
                this.redo.clear();
            }
            finally {
                this.undoLock.unlockWrite(stamp);
            }
            this.sendFeedback(Message.translation("server.builderTools.historyCleared"), componentAccessor);
        }

        public void setGlobalMask(@Nullable BlockMask mask, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
            this.globalMask = mask;
            if (this.globalMask == null) {
                this.sendFeedback(Message.translation("server.builderTools.maskDisabled"), componentAccessor);
            } else {
                this.sendFeedback(Message.translation("server.builderTools.maskSet"), componentAccessor);
            }
        }

        private void sendUpdate() {
            this.player.getPlayerConnection().write((Packet)Objects.requireNonNullElseGet(this.selection, BlockSelection::new).toPacket());
        }

        public void sendArea() {
            if (this.selection != null) {
                this.player.getPlayerConnection().write((Packet)this.selection.toSelectionPacket());
            } else {
                EditorBlocksChange packet = new EditorBlocksChange();
                packet.selection = null;
                this.player.getPlayerConnection().write((Packet)packet);
            }
        }

        private void pushHistory(Action action, SelectionSnapshot<?> snapshot) {
            this.pushHistory(action, Collections.singletonList(snapshot));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void pushHistory(Action action, List<SelectionSnapshot<?>> snapshots) {
            if (action == Action.UPDATE_SELECTION && !this.getUserData().isRecordingSelectionHistory()) {
                return;
            }
            long stamp = this.undoLock.writeLock();
            try {
                this.undo.enqueue(new ActionEntry(action, snapshots));
                this.redo.clear();
                while (this.undo.size() > BuilderToolsPlugin.get().historyCount) {
                    this.undo.dequeue();
                }
            }
            finally {
                this.undoLock.unlockWrite(stamp);
            }
            if (action != Action.UPDATE_SELECTION && action != Action.COPY && action != Action.CUT_COPY) {
                this.markPrefabsDirtyFromSnapshots(snapshots);
            }
        }

        private void markPrefabsDirtyFromSnapshots(@Nonnull List<SelectionSnapshot<?>> snapshots) {
            PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
            Map<UUID, PrefabEditSession> activeEditSessions = prefabEditSessionManager.getActiveEditSessions();
            if (activeEditSessions.isEmpty()) {
                return;
            }
            for (SelectionSnapshot<?> snapshot : snapshots) {
                if (!(snapshot instanceof BlockSelectionSnapshot)) continue;
                BlockSelectionSnapshot blockSnapshot = (BlockSelectionSnapshot)snapshot;
                BlockSelection blockSelection = blockSnapshot.getBlockSelection();
                Vector3i min = blockSelection.getSelectionMin();
                Vector3i max = blockSelection.getSelectionMax();
                for (Map.Entry<UUID, PrefabEditSession> entry : activeEditSessions.entrySet()) {
                    entry.getValue().markPrefabsDirtyInBounds(min, max);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        private ActionEntry historyAction(Ref<EntityStore> ref, @Nonnull ObjectArrayFIFOQueue<ActionEntry> from, @Nonnull ObjectArrayFIFOQueue<ActionEntry> to, ComponentAccessor<EntityStore> componentAccessor) {
            long stamp = this.undoLock.writeLock();
            try {
                if (from.isEmpty()) {
                    ActionEntry actionEntry = null;
                    return actionEntry;
                }
                ActionEntry builderAction = from.dequeueLast();
                to.enqueue(builderAction.restore(ref, this.player, componentAccessor.getExternalData().getWorld(), componentAccessor));
                while (to.size() > BuilderToolsPlugin.get().historyCount) {
                    to.dequeue();
                }
                ActionEntry actionEntry = builderAction;
                return actionEntry;
            }
            finally {
                this.undoLock.unlockWrite(stamp);
            }
        }

        public static class BlocksSampleData {
            public int mainBlock = 0;
            public int mainBlockCount = 0;
            public int mainBlockNotAir = 0;
            public int mainBlockNotAirCount = 0;
        }

        public static class SmoothSampleData {
            public float solidStrength = 0.0f;
            public int solidBlock = 0;
            public int solidBlockCount = 0;
            public int fillerBlock = 0;
            public int fillerBlockCount = 0;
        }
    }

    public static class PrefabPasteEventSystem
    extends WorldEventSystem<EntityStore, PrefabPasteEvent> {
        @Nonnull
        private final BuilderToolsPlugin plugin;

        protected PrefabPasteEventSystem(@Nonnull BuilderToolsPlugin plugin) {
            super(PrefabPasteEvent.class);
            this.plugin = plugin;
        }

        @Override
        public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PrefabPasteEvent event) {
            if (event.isPasteStart()) {
                this.plugin.pastedPrefabPathUUIDMap.put(event.getPrefabId(), new ConcurrentHashMap());
                this.plugin.pastedPrefabPathNameToUUIDMap.put(event.getPrefabId(), new ConcurrentHashMap());
            } else {
                this.plugin.pastedPrefabPathUUIDMap.remove(event.getPrefabId());
                this.plugin.pastedPrefabPathNameToUUIDMap.remove(event.getPrefabId());
            }
        }
    }

    public static class CachedAccessor
    extends AbstractCachedAccessor {
        private static final ThreadLocal<CachedAccessor> THREAD_LOCAL = ThreadLocal.withInitial(CachedAccessor::new);
        private static final int FLUID_COMPONENT = 0;
        private static final int PHYSICS_COMPONENT = 1;
        private static final int BLOCKS_COMPONENT = 2;

        public CachedAccessor() {
            super(3);
        }

        @Nonnull
        public static CachedAccessor of(ComponentAccessor<ChunkStore> accessor, int cx, int cy, int cz, int radius) {
            CachedAccessor cachedAccessor = THREAD_LOCAL.get();
            cachedAccessor.init(accessor, cx, cy, cz, radius);
            return cachedAccessor;
        }

        @Nullable
        public FluidSection getFluidSection(int cx, int cy, int cz) {
            return this.getComponentSection(cx, cy, cz, 0, FluidSection.getComponentType());
        }

        @Nullable
        public BlockPhysics getBlockPhysics(int cx, int cy, int cz) {
            return this.getComponentSection(cx, cy, cz, 1, BlockPhysics.getComponentType());
        }

        @Nullable
        public BlockSection getBlockSection(int cx, int cy, int cz) {
            return this.getComponentSection(cx, cy, cz, 2, BlockSection.getComponentType());
        }
    }

    private static final class QueuedTask {
        @Nonnull
        private final ThrowableTriConsumer<Ref<EntityStore>, BuilderState, ComponentAccessor<EntityStore>, ? extends Throwable> task;

        private QueuedTask(@Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderState, ComponentAccessor<EntityStore>, ? extends Throwable> biTask) {
            this.task = biTask;
        }

        void execute(@Nonnull Ref<EntityStore> ref, @Nonnull BuilderState state, @Nonnull ComponentAccessor<EntityStore> defaultComponentAccessor) throws Throwable {
            this.task.acceptNow(ref, state, defaultComponentAccessor);
        }
    }

    public static class ActionEntry {
        private final Action action;
        private final List<SelectionSnapshot<?>> snapshots;

        public ActionEntry(Action action, SelectionSnapshot<?> snapshots) {
            this(action, Collections.singletonList(snapshots));
        }

        public ActionEntry(Action action, List<SelectionSnapshot<?>> snapshots) {
            this.action = action;
            this.snapshots = snapshots;
        }

        public Action getAction() {
            return this.action;
        }

        @Nonnull
        public ActionEntry restore(Ref<EntityStore> ref, Player player, World world, ComponentAccessor<EntityStore> componentAccessor) {
            ObjectArrayList collector = Collections.emptyList();
            for (SelectionSnapshot<?> snapshot : this.snapshots) {
                Object nextSnapshot = snapshot.restore(ref, player, world, componentAccessor);
                if (nextSnapshot == null) continue;
                collector = collector.isEmpty() ? new ObjectArrayList() : collector;
                collector.add(nextSnapshot);
            }
            return new ActionEntry(this.action, collector);
        }
    }

    public static enum Action {
        EDIT,
        EDIT_SELECTION,
        EDIT_LINE,
        CUT_COPY,
        CUT_REMOVE,
        COPY,
        PASTE,
        CLEAR,
        ROTATE,
        FLIP,
        MOVE,
        STACK,
        SET,
        REPLACE,
        EXTRUDE,
        UPDATE_SELECTION,
        WALLS,
        HOLLOW;

    }
}

