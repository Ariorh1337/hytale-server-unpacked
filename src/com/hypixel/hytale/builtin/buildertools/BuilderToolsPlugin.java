package com.hypixel.hytale.builtin.buildertools;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
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
import com.hypixel.hytale.builtin.buildertools.commands.LayerCommand;
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
import com.hypixel.hytale.builtin.buildertools.commands.ShrinkCommand;
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
import com.hypixel.hytale.builtin.buildertools.prefabeditor.commands.PrefabEditCommand;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaveContributor;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.PrefabSaveSupport;
import com.hypixel.hytale.builtin.buildertools.prefabeditor.saving.SupportMode;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.BrushConfig;
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
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ClearRotationOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.DeleteOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.EchoOnceOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.EchoOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.ErodeOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.FluidFixOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.HeightmapLayerOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.KernelErosionOperation;
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
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.sequential.transforms.RotateOperation;
import com.hypixel.hytale.builtin.buildertools.scriptedbrushes.operations.system.BrushOperation;
import com.hypixel.hytale.builtin.buildertools.snapshot.BlockSelectionSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.ClipboardBoundsSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.ClipboardContentsSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityAddSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityFreezeSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityRemoveSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntityScaleSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntitySettingsSnapshot;
import com.hypixel.hytale.builtin.buildertools.snapshot.EntitySnapshot;
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
import com.hypixel.hytale.component.AddReason;
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
import com.hypixel.hytale.event.EventBus;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.function.predicate.TriIntObjPredicate;
import com.hypixel.hytale.math.Axis;
import com.hypixel.hytale.math.block.BlockCubeUtil;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.iterator.LineIterator;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3iUtil;
import com.hypixel.hytale.math.vector.VectorBoxUtil;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricResults;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.DrawType;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.buildertools.BrushOrigin;
import com.hypixel.hytale.protocol.packets.buildertools.BrushShape;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolArgUpdate;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolsEnabledTools;
import com.hypixel.hytale.protocol.packets.interface_.BlockChange;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.EditorBlocksChange;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
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
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.permissions.GroupPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.permissions.PlayerGroupEvent;
import com.hypixel.hytale.server.core.event.events.permissions.PlayerPermissionChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.InventoryUtils;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.modules.prefabspawner.PrefabSpawnerBlock;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.permissions.HytalePermissions;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
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
import com.hypixel.hytale.server.core.prefab.selection.standard.RotateBlockMode;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
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
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class BuilderToolsPlugin extends JavaPlugin implements SelectionProvider, MetricProvider {
   public static final String EDITOR_BLOCK = "Editor_Block";
   public static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
   public static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";
   protected static final float SPHERE_SIZE = 1.0F;
   static final int MAX_CLIPBOARD_BLOCK_COUNT = 4000000;
   static final double CLIPBOARD_PRE_LIMIT_FACTOR = 1.65;
   private static final FeedbackConsumer FEEDBACK_CONSUMER = BuilderToolsPlugin::sendFeedback;
   private static final MetricsRegistry<BuilderToolsPlugin> PLUGIN_METRICS_REGISTRY = new MetricsRegistry<BuilderToolsPlugin>()
      .register(
         "BuilderStates",
         plugin -> plugin.builderStates.values().toArray(BuilderToolsPlugin.BuilderState[]::new),
         new ArrayCodec<>(BuilderToolsPlugin.BuilderState.STATE_METRICS_REGISTRY, BuilderToolsPlugin.BuilderState[]::new)
      );
   private static final long RETAIN_BUILDER_STATE_TIMESTAMP = Long.MAX_VALUE;
   private static final long MIN_CLEANUP_INTERVAL_NANOS = TimeUnit.MINUTES.toNanos(1L);
   private final Map<UUID, BuilderToolsPlugin.BuilderState> builderStates = new ConcurrentHashMap<>();
   private PrefabEditSessionManager prefabEditSessionManager;
   private final BlockColorIndex blockColorIndex = new BlockColorIndex();
   private static BuilderToolsPlugin instance;
   private int historyCount;
   private long toolExpireTimeNanos;
   @Nullable
   private ScheduledFuture<?> cleanupTask;
   private ComponentType<EntityStore, BuilderToolsUserData> userDataComponentType;
   private ComponentType<EntityStore, PrefabAnchor> prefabAnchorComponentType;
   private final Int2ObjectConcurrentHashMap<ConcurrentHashMap<UUID, UUID>> pastedPrefabPathUUIDMap = new Int2ObjectConcurrentHashMap<>();
   private final Int2ObjectConcurrentHashMap<ConcurrentHashMap<String, UUID>> pastedPrefabPathNameToUUIDMap = new Int2ObjectConcurrentHashMap<>();
   private final List<PrefabSaveContributor> prefabSaveContributors = new ArrayList<>();
   private final List<PrefabSaveContributor> clipboardContributors = new ArrayList<>();
   @Nullable
   private volatile BiConsumer<PlayerRef, Store<EntityStore>> selectionBoundsUpdatedCallback;
   @Nullable
   private volatile BiConsumer<PlayerRef, Store<EntityStore>> builderToolModeDeactivatedCallback;
   @Nullable
   private volatile BiConsumer<PlayerRef, Store<EntityStore>> selectionClearedCallback;
   private static final float SMOOTHING_KERNEL_TOTAL = 27.0F;
   private static final int[] SMOOTHING_KERNEL = new int[]{1, 2, 1, 2, 3, 2, 1, 2, 1, 2, 3, 2, 3, 4, 3, 2, 3, 2, 1, 2, 1, 2, 3, 2, 1, 2, 1};
   private static final String[] KNOWN_SHAPE_SUFFIXES = new String[]{
      "Pillar_Base",
      "Pillar_Middle",
      "Roof_Flat",
      "Roof_Steep",
      "Roof_Shallow",
      "Roof_Corner",
      "Roof_Hollow",
      "Stalactite_Small",
      "Stalactite_Large",
      "Smooth",
      "Beam",
      "Half",
      "Stairs",
      "Wall",
      "Fence",
      "Corner",
      "Roof",
      "Decorative",
      "Ornate"
   };
   private final Config<BuilderToolsPlugin.BuilderToolsConfig> config = this.withConfig("BuilderToolsModule", BuilderToolsPlugin.BuilderToolsConfig.CODEC);
   private static final Message MESSAGE_PACK_NOT_FOUND = Message.translation("server.commands.editprefab.save.pack.notFound");
   private static final Message MESSAGE_PACK_IMMUTABLE = Message.translation("server.commands.editprefab.save.pack.immutable");
   private ResourceType<EntityStore, PrefabEditSession> prefabEditSessionResourceType;

   @Nonnull
   public static List<BuilderToolsPlugin.ColorGradientMaterial> parseGradientMaterials(@Nullable String materialsStr) {
      ArrayList<BuilderToolsPlugin.ColorGradientMaterial> result = new ArrayList<>();
      if (materialsStr != null && !materialsStr.isEmpty()) {
         String[] entries = materialsStr.split(",");

         for (String entry : entries) {
            int percentIdx = entry.indexOf("%");
            if (percentIdx >= 0) {
               try {
                  float weight = Float.parseFloat(entry.substring(0, percentIdx));
                  String materialName = entry.substring(percentIdx + 1);
                  BlockPattern pattern = BlockPattern.parse(materialName);
                  if (pattern != null) {
                     result.add(new BuilderToolsPlugin.ColorGradientMaterial(pattern, weight));
                  }
               } catch (NumberFormatException var11) {
               }
            }
         }

         return result;
      } else {
         return result;
      }
   }

   public BuilderToolsPlugin(@Nonnull JavaPluginInit init) {
      super(init);
      instance = this;
      this.getLogger().setLevel(Level.FINE);
   }

   public static BuilderToolsPlugin get() {
      return instance;
   }

   public void registerPrefabSaveContributor(@Nonnull PrefabSaveContributor contributor) {
      this.prefabSaveContributors.add(contributor);
   }

   @Nonnull
   public List<PrefabSaveContributor> getPrefabSaveContributors() {
      return this.prefabSaveContributors;
   }

   public void registerClipboardContributor(@Nonnull PrefabSaveContributor contributor) {
      this.clipboardContributors.add(contributor);
   }

   @Nonnull
   public List<PrefabSaveContributor> getClipboardContributors() {
      return this.clipboardContributors;
   }

   public void setSelectionBoundsUpdatedCallback(@Nullable BiConsumer<PlayerRef, Store<EntityStore>> callback) {
      this.selectionBoundsUpdatedCallback = callback;
   }

   public void setBuilderToolModeDeactivatedCallback(@Nullable BiConsumer<PlayerRef, Store<EntityStore>> callback) {
      this.builderToolModeDeactivatedCallback = callback;
   }

   public void setSelectionClearedCallback(@Nullable BiConsumer<PlayerRef, Store<EntityStore>> callback) {
      this.selectionClearedCallback = callback;
   }

   @Nullable
   public BiConsumer<PlayerRef, Store<EntityStore>> getSelectionBoundsUpdatedCallback() {
      return this.selectionBoundsUpdatedCallback;
   }

   @Nullable
   public BiConsumer<PlayerRef, Store<EntityStore>> getBuilderToolModeDeactivatedCallback() {
      return this.builderToolModeDeactivatedCallback;
   }

   @Nullable
   public BiConsumer<PlayerRef, Store<EntityStore>> getSelectionClearedCallback() {
      return this.selectionClearedCallback;
   }

   @Nonnull
   public BlockColorIndex getBlockColorIndex() {
      return this.blockColorIndex;
   }

   private static String getBlockBaseMaterial(String blockName) {
      for (String suffix : KNOWN_SHAPE_SUFFIXES) {
         if (blockName.endsWith("_" + suffix)) {
            return blockName.substring(0, blockName.length() - suffix.length() - 1);
         }
      }

      return blockName;
   }

   private static String getBlockShape(String blockName) {
      for (String suffix : KNOWN_SHAPE_SUFFIXES) {
         if (blockName.endsWith("_" + suffix)) {
            return suffix;
         }
      }

      return "";
   }

   private static boolean passesFilter(String filterMode, int blockId, int targetBlockId) {
      if (!"All".equals(filterMode) && blockId != targetBlockId) {
         BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
         BlockType targetType = BlockType.getAssetMap().getAsset(targetBlockId);
         if (blockType != null && targetType != null) {
            return switch (filterMode) {
               case "SameMaterial" -> getBlockBaseMaterial(blockType.getId()).equals(getBlockBaseMaterial(targetType.getId()));
               case "SameShape" -> getBlockShape(blockType.getId()).equals(getBlockShape(targetType.getId()));
               default -> true;
            };
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private static boolean shouldShowNotification(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
      PlayerSettings settings = componentAccessor.getComponent(ref, PlayerSettings.getComponentType());
      return settings != null && settings.creativeSettings().showBuilderToolsNotifications();
   }

   public static void invalidateWorldMapForSelection(@Nonnull BlockSelection selection, @Nonnull World world) {
      invalidateWorldMapForBounds(selection.getSelectionMin(), selection.getSelectionMax(), world);
   }

   static void invalidateWorldMapForBounds(@Nonnull Vector3i min, @Nonnull Vector3i max, @Nonnull World world) {
      LongSet affectedChunks = new LongOpenHashSet();
      int minChunkX = min.x >> 5;
      int maxChunkX = max.x >> 5;
      int minChunkZ = min.z >> 5;
      int maxChunkZ = max.z >> 5;

      for (int cx = minChunkX; cx <= maxChunkX; cx++) {
         for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
            affectedChunks.add(ChunkUtil.indexChunk(cx, cz));
         }
      }

      world.getWorldMapManager().clearImagesInChunks(affectedChunks);

      for (Player worldPlayer : world.getPlayers()) {
         worldPlayer.getWorldMapTracker().clearChunks(affectedChunks);
      }
   }

   @Nonnull
   public static BuilderToolsPlugin.BuilderState getState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
      return instance.getBuilderState(player, playerRef);
   }

   public static <T extends Throwable> void addToQueue(
      @Nonnull Player player,
      @Nonnull PlayerRef playerRef,
      @Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderToolsPlugin.BuilderState, ComponentAccessor<EntityStore>, T> task
   ) {
      getState(player, playerRef).addToQueue(task);
   }

   @Nullable
   public static AssetPack resolveTargetPack(@Nonnull String explicitPackName, @Nonnull Player playerComponent, @Nonnull CommandContext context) {
      return resolveTargetPack(explicitPackName, null, playerComponent, context);
   }

   @Nullable
   public static AssetPack resolveTargetPack(
      @Nonnull String explicitPackName, @Nullable Path prefabPath, @Nonnull Player playerComponent, @Nonnull CommandContext context
   ) {
      AssetModule assetModule = AssetModule.get();
      if (!explicitPackName.isEmpty()) {
         AssetPack pack = assetModule.getAssetPack(explicitPackName);
         if (pack == null) {
            context.sendMessage(MESSAGE_PACK_NOT_FOUND.param("name", explicitPackName));
            return null;
         } else if (pack.isImmutable()) {
            context.sendMessage(MESSAGE_PACK_IMMUTABLE.param("name", explicitPackName));
            return null;
         } else {
            return pack;
         }
      } else {
         if (prefabPath != null) {
            AssetPack sourcePack = PrefabStore.get().findAssetPackForPrefabPath(prefabPath);
            if (sourcePack != null) {
               if (!sourcePack.isImmutable()) {
                  return sourcePack;
               }

               context.sendMessage(Message.translation("server.commands.editprefab.save.noPack"));
               return null;
            }
         }

         String lastPack = BuilderToolsUserData.get(playerComponent).getLastSavePack();
         if (lastPack != null) {
            AssetPack pack = assetModule.getAssetPack(lastPack);
            if (pack != null && !pack.isImmutable()) {
               return pack;
            }
         }

         AssetPack basePack = assetModule.getBaseAssetPack();
         if (!basePack.isImmutable()) {
            return basePack;
         }

         context.sendMessage(Message.translation("server.commands.editprefab.save.noPack"));
         return null;
      }
   }

   @Override
   protected void setup() {
      CommandRegistry commandRegistry = this.getCommandRegistry();
      EventRegistry eventRegistry = this.getEventRegistry();
      ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
      ServerManager.get().registerSubPacketHandlers(BuilderToolsPacketHandler::new);
      eventRegistry.register(PlayerConnectEvent.class, this::onPlayerConnect);
      eventRegistry.register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
      eventRegistry.registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
      eventRegistry.registerGlobal(
         AddWorldEvent.class, event -> event.getWorld().getWorldMapManager().addMarkerProvider("prefabs", PrefabMarkerProvider.INSTANCE)
      );
      EventBus eventBus = HytaleServer.get().getEventBus();
      eventBus.register(PlayerGroupEvent.Added.class, event -> this.resendEnabledTools(event.getPlayerUuid()));
      eventBus.register(PlayerGroupEvent.Removed.class, event -> this.resendEnabledTools(event.getPlayerUuid()));
      eventBus.register(PlayerPermissionChangeEvent.PermissionsAdded.class, event -> this.resendEnabledTools(event.getPlayerUuid()));
      eventBus.register(PlayerPermissionChangeEvent.PermissionsRemoved.class, event -> this.resendEnabledTools(event.getPlayerUuid()));
      eventBus.register(GroupPermissionChangeEvent.Added.class, this::onGroupPermissionsChanged);
      eventBus.register(GroupPermissionChangeEvent.Removed.class, this::onGroupPermissionsChanged);
      entityStoreRegistry.registerSystem(new BuilderToolsPlugin.PrefabPasteEventSystem(this));
      entityStoreRegistry.registerSystem(new PrefabDirtySystems.BlockBreakDirtySystem());
      entityStoreRegistry.registerSystem(new PrefabDirtySystems.BlockPlaceDirtySystem());
      this.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, event -> {
         ScriptedBrushAsset.invalidateBrushToItemCache();
         this.resendEnabledToolsToAll();
      });
      this.getEventRegistry().register(RemovedAssetsEvent.class, Item.class, event -> {
         ScriptedBrushAsset.invalidateBrushToItemCache();
         this.resendEnabledToolsToAll();
      });
      this.prefabAnchorComponentType = entityStoreRegistry.registerComponent(PrefabAnchor.class, "PrefabAnchor", PrefabAnchor.CODEC);
      Interaction.CODEC.register("PrefabSelectionInteraction", PrefabSelectionInteraction.class, PrefabSelectionInteraction.CODEC);
      Interaction.CODEC.register("PickupItem", PickupItemInteraction.class, PickupItemInteraction.CODEC);
      Interaction.getAssetStore().loadAssets("Hytale:Hytale", List.of(new PickupItemInteraction("*PickupItem")));
      RootInteraction.getAssetStore().loadAssets("Hytale:Hytale", List.of(PickupItemInteraction.DEFAULT_ROOT));
      this.prefabEditSessionManager = new PrefabEditSessionManager(this);
      this.prefabEditSessionResourceType = entityStoreRegistry.registerResource(PrefabEditSession.class, "PrefabEditSession", PrefabEditSession.CODEC);
      AssetRegistry.register(
         ((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(
                        PrefabEditorCreationSettings.class, new DefaultAssetMap()
                     )
                     .setPath("PrefabEditorCreationSettings"))
                  .setKeyFunction(PrefabEditorCreationSettings::getId))
               .setCodec(PrefabEditorCreationSettings.CODEC))
            .build()
      );
      AssetRegistry.register(
         ((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(
                        ScriptedBrushAsset.class, new DefaultAssetMap()
                     )
                     .setPath("ScriptedBrushes"))
                  .setKeyFunction(ScriptedBrushAsset::getId))
               .setCodec(ScriptedBrushAsset.CODEC))
            .build()
      );
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
      commandRegistry.registerCommand(new ShrinkCommand());
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
      commandRegistry.registerCommand(new LayerCommand());
      OpenCustomUIInteraction.registerBlockEntityCustomPage(
         this,
         PrefabSpawnerBlock.PrefabSpawnerSettingsPage.class,
         "PrefabSpawner",
         (playerRef, blockRef) -> {
            Store<ChunkStore> store = blockRef.getStore();
            BlockModule.BlockStateInfo info = store.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            PrefabSpawnerBlock state = store.getComponent(blockRef, PrefabSpawnerBlock.getComponentType());
            return info != null && state != null
               ? new PrefabSpawnerBlock.PrefabSpawnerSettingsPage(playerRef, info, state, CustomPageLifetime.CanDismissOrCloseThroughInteraction)
               : null;
         }
      );
      SelectionManager.setSelectionProvider(this);
      ToolArg.CODEC.register("Bool", BoolArg.class, BoolArg.CODEC);
      ToolArg.CODEC.register("String", StringArg.class, StringArg.CODEC);
      ToolArg.CODEC.register("Int", IntArg.class, IntArg.CODEC);
      ToolArg.CODEC.register("Float", FloatArg.class, FloatArg.CODEC);
      ToolArg.CODEC.register("Block", BlockArg.class, BlockArg.CODEC);
      ToolArg.CODEC.register("Mask", MaskArg.class, MaskArg.CODEC);
      ToolArg.CODEC.register("BrushShape", BrushShapeArg.class, BrushShapeArg.CODEC);
      ToolArg.CODEC.register("BrushOrigin", BrushOriginArg.class, BrushOriginArg.CODEC);
      ToolArg.CODEC.register("Option", OptionArg.class, OptionArg.CODEC);
      this.registerBrushOperations();
      this.userDataComponentType = entityStoreRegistry.registerComponent(BuilderToolsUserData.class, "BuilderTools", BuilderToolsUserData.CODEC);
      entityStoreRegistry.registerSystem(new BuilderToolsSystems.EnsureBuilderTools());
      entityStoreRegistry.registerSystem(new BuilderToolsUserDataSystem());
   }

   private void registerBrushOperations() {
      BrushOperation.OPERATION_CODEC.register("dimensions", DimensionsOperation.class, DimensionsOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("randomdimensions", RandomizeDimensionsOperation.class, RandomizeDimensionsOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("runcommand", RunCommandOperation.class, RunCommandOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("historymask", HistoryMaskOperation.class, HistoryMaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("mask", MaskOperation.class, MaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("clearoperationmask", ClearOperationMaskOperation.class, ClearOperationMaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("usebrushmask", UseBrushMaskOperation.class, UseBrushMaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("useoperationmask", UseOperationMaskOperation.class, UseOperationMaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("appendmask", AppendMaskOperation.class, AppendMaskOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("appendmaskfromtoolarg", AppendMaskFromToolArgOperation.class, AppendMaskFromToolArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("ignorebrushsettings", IgnoreExistingBrushDataOperation.class, IgnoreExistingBrushDataOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("debug", DebugBrushOperation.class, DebugBrushOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loop", LoopOperation.class, LoopOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loadloop", LoadLoopFromToolArgOperation.class, LoadLoopFromToolArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("looprandom", LoopRandomOperation.class, LoopRandomOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loopcircle", CircleOffsetAndLoopOperation.class, CircleOffsetAndLoopOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loopcirclefromarg", CircleOffsetFromArgOperation.class, CircleOffsetFromArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("savebrushconfig", SaveBrushConfigOperation.class, SaveBrushConfigOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loadbrushconfig", LoadBrushConfigOperation.class, LoadBrushConfigOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("saveindex", SaveIndexOperation.class, SaveIndexOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loadoperationsfromasset", LoadOperationsFromAssetOperation.class, LoadOperationsFromAssetOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("jump", JumpToIndexOperation.class, JumpToIndexOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("exit", ExitOperation.class, ExitOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumprandom", JumpToRandomIndex.class, JumpToRandomIndex.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumpifequal", JumpIfStringMatchOperation.class, JumpIfStringMatchOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumpifclicktype", JumpIfClickType.class, JumpIfClickType.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumpifcompare", JumpIfCompareOperation.class, JumpIfCompareOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumpifblocktype", JumpIfBlockTypeOperation.class, JumpIfBlockTypeOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("jumpiftoolarg", JumpIfToolArgOperation.class, JumpIfToolArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("pattern", BlockPatternOperation.class, BlockPatternOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loadmaterial", LoadMaterialFromToolArgOperation.class, LoadMaterialFromToolArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("loadint", LoadIntFromToolArgOperation.class, LoadIntFromToolArgOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("lift", LiftOperation.class, LiftOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("density", SetDensity.class, SetDensity.CODEC);
      BrushOperation.OPERATION_CODEC.register("set", SetOperation.class, SetOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("smooth", SmoothOperation.class, SmoothOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("kernelsmooth", KernelErosionOperation.class, KernelErosionOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("fluidfix", FluidFixOperation.class, FluidFixOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("shape", ShapeOperation.class, ShapeOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("rotation", RotateOperation.class, RotateOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("clearrotation", ClearRotationOperation.class, ClearRotationOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("offset", OffsetOperation.class, OffsetOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("layer", LayerOperation.class, LayerOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("heightmaplayer", HeightmapLayerOperation.class, HeightmapLayerOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("melt", MeltOperation.class, MeltOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("material", MaterialOperation.class, MaterialOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("delete", DeleteOperation.class, DeleteOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("disableonhold", DisableHoldInteractionOperation.class, DisableHoldInteractionOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("randomoffset", RandomOffsetOperation.class, RandomOffsetOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("erode", ErodeOperation.class, ErodeOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("persistentdata", PersistentDataOperation.class, PersistentDataOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("pasteprefab", PastePrefabOperation.class, PastePrefabOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("echo", EchoOperation.class, EchoOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("echoonce", EchoOnceOperation.class, EchoOnceOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("replace", ReplaceOperation.class, ReplaceOperation.CODEC);
      BrushOperation.OPERATION_CODEC.register("breakpoint", BreakpointOperation.class, BreakpointOperation.CODEC);
   }

   public ResourceType<EntityStore, PrefabEditSession> getPrefabEditSessionResourceType() {
      return this.prefabEditSessionResourceType;
   }

   @Override
   protected void start() {
      BuilderToolsPlugin.BuilderToolsConfig config = this.config.get();
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
      Iterator<Entry<UUID, BuilderToolsPlugin.BuilderState>> iterator = this.builderStates.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<UUID, BuilderToolsPlugin.BuilderState> entry = iterator.next();
         BuilderToolsPlugin.BuilderState state = entry.getValue();
         if (state.timestamp < expire) {
            iterator.remove();
            this.getLogger().at(Level.FINE).log("[%s] Expired and removed builder tool", state.getDisplayName());
         }
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

   private void onPlayerReady(@Nonnull PlayerReadyEvent event) {
      Ref<EntityStore> ref = event.getPlayerRef();
      if (ref.isValid()) {
         Store<EntityStore> store = ref.getStore();
         UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
         if (uuidComponent != null) {
            BuilderToolsPlugin.BuilderState state = this.builderStates.get(uuidComponent.getUuid());
            if (state != null && state.getSelection() != null) {
               state.sendSelectionToClient();
            }

            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef != null) {
               this.sendEnabledTools(playerRef);
            }
         }
      }
   }

   private void onGroupPermissionsChanged(@Nonnull GroupPermissionChangeEvent event) {
      String groupName = event.getGroupName();
      PermissionsModule permissionsModule = PermissionsModule.get();

      for (PlayerRef playerRef : Universe.get().getPlayers()) {
         if (permissionsModule.getGroupsForUser(playerRef.getUuid()).contains(groupName)) {
            this.sendEnabledTools(playerRef);
         }
      }
   }

   private void resendEnabledTools(@Nonnull UUID uuid) {
      PlayerRef playerRef = Universe.get().getPlayer(uuid);
      if (playerRef != null) {
         this.sendEnabledTools(playerRef);
      }
   }

   private void resendEnabledToolsToAll() {
      Universe universe = Universe.get();
      if (universe != null) {
         for (PlayerRef playerRef : universe.getPlayers()) {
            this.sendEnabledTools(playerRef);
         }
      }
   }

   private void sendEnabledTools(@Nonnull PlayerRef playerRef) {
      PermissionsModule permissionsModule = PermissionsModule.get();
      UUID uuid = playerRef.getUuid();
      boolean hasEditor = permissionsModule.hasPermission(uuid, HytalePermissions.BUILDER_TOOLS_EDITOR);
      ArrayList<String> enabledItemIds = new ArrayList<>();

      for (Entry<String, Item> entry : Item.getAssetMap().getAssetMap().entrySet()) {
         String itemId = entry.getKey();
         BuilderTool builderTool = entry.getValue().getBuilderTool();
         if (builderTool != null && builderTool.isSurvivalAllowed() && itemId != null && !enabledItemIds.contains(itemId)) {
            String toolId = builderTool.getId();
            if (hasEditor || toolId != null && permissionsModule.hasPermission(uuid, HytalePermissions.toolPermission(toolId))) {
               enabledItemIds.add(itemId);
            }
         }
      }

      BuilderToolsEnabledTools packet = new BuilderToolsEnabledTools();
      packet.toolIds = enabledItemIds.toArray(new String[0]);
      playerRef.getPacketHandler().write(packet);
   }

   public void onToolArgUpdate(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef,
      @Nonnull BuilderToolArgUpdate packet,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      ItemContainer section = InventoryUtils.getSectionById(ref, packet.section, componentAccessor);
      if (section == null) {
         MessageUtil.sendFailureReply(
            playerRef,
            packet.token,
            Message.translation("server.builderTools.invalidTool").param("item", Message.translation("builderTools.invalidTool.unknown.section"))
         );
      } else {
         ItemStack itemStack = section.getItemStack((short)packet.slot);
         if (itemStack == null) {
            MessageUtil.sendFailureReply(playerRef, packet.token, Message.translation("server.builderTools.invalidTool").param("item", "Empty"));
         } else {
            Item item = itemStack.getItem();
            BuilderTool builderToolData = item.getBuilderTool();
            if (builderToolData == null) {
               MessageUtil.sendFailureReply(
                  playerRef, packet.token, Message.translation("server.builderTools.invalidTool").param("item", itemStack.getDisplayName())
               );
            } else {
               try {
                  ItemStack updatedItemStack = builderToolData.updateArgMetadata(itemStack, packet.id, packet.value);
                  section.setItemStackForSlot((short)packet.slot, updatedItemStack);
                  MessageUtil.sendSuccessReply(playerRef, packet.token);
               } catch (ToolArgException e) {
                  MessageUtil.sendFailureReply(playerRef, packet.token, e.getTranslationMessage());
               } catch (IllegalArgumentException e) {
                  MessageUtil.sendFailureReply(
                     playerRef, packet.token, Message.translation("server.builderTools.toolArgParseError").param("arg", packet.id).param("value", packet.value)
                  );
               }
            }
         }
      }
   }

   @Nonnull
   public BuilderToolsPlugin.BuilderState getBuilderState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
      return this.builderStates.computeIfAbsent(playerRef.getUuid(), k -> new BuilderToolsPlugin.BuilderState(player, playerRef));
   }

   @Nullable
   public BuilderToolsPlugin.BuilderState clearBuilderState(UUID uuid) {
      BuilderToolsPlugin.BuilderState state = this.builderStates.remove(uuid);
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
         return (BuilderToolsPlugin.BuilderState)state;
      });
   }

   private void releaseBuilderState(@Nonnull UUID uuid) {
      if (this.toolExpireTimeNanos <= 0L) {
         this.clearBuilderState(uuid);
      } else {
         this.builderStates.compute(uuid, (id, state) -> {
            if (state == null) {
               return null;
            }

            state.release();
            this.getLogger().at(Level.FINE).log("[%s] Marked builder tool for removal", state.getDisplayName());
            return (BuilderToolsPlugin.BuilderState)state;
         });
      }
   }

   public ComponentType<EntityStore, BuilderToolsUserData> getUserDataComponentType() {
      return this.userDataComponentType;
   }

   public static void sendFeedback(
      @Nonnull Message message,
      @Nullable CommandSender feedback,
      @Nonnull NotificationStyle notificationStyle,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (feedback instanceof PlayerRef playerRefComponent) {
         if (!shouldShowNotification(playerRefComponent.getReference(), componentAccessor)) {
            return;
         }

         NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), message, notificationStyle);
      } else if (feedback != null) {
         feedback.sendMessage(message);
      }
   }

   public static void sendFeedback(@Nonnull String key, int total, CommandSender feedback, ComponentAccessor<EntityStore> componentAccessor) {
      if (feedback instanceof PlayerRef playerRefComponent) {
         if (!shouldShowNotification(playerRefComponent.getReference(), componentAccessor)) {
            return;
         }

         NotificationUtil.sendNotification(
            playerRefComponent.getPacketHandler(),
            Message.translation("server.builderTools.blocksEdited").param("key", key),
            Message.raw(String.valueOf(total)),
            NotificationStyle.Success
         );
      } else if (feedback != null) {
         feedback.sendMessage(Message.translation("server.builderTools.blocksEdited").param("key", key));
      }
   }

   public static void sendFeedback(@Nonnull String key, int total, int num, CommandSender feedback, ComponentAccessor<EntityStore> componentAccessor) {
      if (num % 100000 == 0) {
         if (feedback instanceof PlayerRef playerRefComponent) {
            if (!shouldShowNotification(playerRefComponent.getReference(), componentAccessor)) {
               return;
            }

            NotificationUtil.sendNotification(
               playerRefComponent.getPacketHandler(),
               Message.translation("server.builderTools.doneEditing").param("key", key),
               Message.translation("server.builderTools.blocksChanged").param("total", total),
               NotificationStyle.Success
            );
         } else if (feedback != null) {
            feedback.sendMessage(
               Message.translation("server.builderTools.editingStatus")
                  .param("key", key)
                  .param("percent", MathUtil.round((double)num / total * 100.0, 2))
                  .param("count", num)
                  .param("total", total)
            );
         }
      }
   }

   @Override
   public <T extends Throwable> void computeSelectionCopy(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull Player player,
      @Nonnull ThrowableConsumer<BlockSelection, T> task,
      @Nonnull ComponentAccessor<EntityStore> componentAccessor
   ) {
      if (this.isEnabled()) {
         PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
         assert playerRefComponent != null;
         this.getBuilderState(player, playerRefComponent).computeSelectionCopy(task);
      }
   }

   @Nonnull
   @Override
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
   public static Holder<ChunkStore> createBlockComponent(
      WorldChunk chunk, int x, int y, int z, int newId, int oldId, @Nullable Holder<ChunkStore> oldHolder, boolean copy
   ) {
      if (newId == 0) {
         return null;
      }

      BlockType type = BlockType.getAssetMap().getAsset(newId);
      return type.getBlockEntity() != null ? type.getBlockEntity().clone() : null;
   }

   public static void forEachCopyableInSelection(
      @Nonnull World world, int minX, int minY, int minZ, int width, int height, int depth, @Nonnull Consumer<Ref<EntityStore>> action
   ) {
      int encompassingWidth = width + 1;
      int encompassingHeight = height + 1;
      int encompassingDepth = depth + 1;
      if (world.isInThread()) {
         internalForEachCopyableInSelection(world, minX, minY, minZ, encompassingWidth, encompassingHeight, encompassingDepth, action);
      } else {
         CompletableFuture.runAsync(
               () -> internalForEachCopyableInSelection(world, minX, minY, minZ, encompassingWidth, encompassingHeight, encompassingDepth, action), world
            )
            .join();
      }
   }

   private static void internalForEachCopyableInSelection(
      @Nonnull World world,
      int minX,
      int minY,
      int minZ,
      int encompassingWidth,
      int encompassingHeight,
      int encompassingDepth,
      @Nonnull Consumer<Ref<EntityStore>> action
   ) {
      world.getEntityStore()
         .getStore()
         .forEachChunk(Archetype.of(PrefabCopyableComponent.getComponentType(), TransformComponent.getComponentType()), (archetypeChunk, commandBuffer) -> {
            int size = archetypeChunk.size();

            for (int index = 0; index < size; index++) {
               Vector3d vector = archetypeChunk.getComponent(index, TransformComponent.getComponentType()).getPosition();
               Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
               if (VectorBoxUtil.isInside(minX, minY, minZ, 0.0, 0.0, 0.0, encompassingWidth, encompassingHeight, encompassingDepth, vector)) {
                  action.accept(ref);
               }
            }
         });
   }

   private static int getNonEmptyNeighbourBlock(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
      int blockId;
      if ((blockId = accessor.getBlock(x, y, z + 1)) > 0) {
         return blockId;
      } else if ((blockId = accessor.getBlock(x, y, z - 1)) > 0) {
         return blockId;
      } else if ((blockId = accessor.getBlock(x, y + 1, z)) > 0) {
         return blockId;
      } else if ((blockId = accessor.getBlock(x, y - 1, z)) > 0) {
         return blockId;
      } else if ((blockId = accessor.getBlock(x - 1, y, z)) > 0) {
         return blockId;
      } else {
         return (blockId = accessor.getBlock(x + 1, y, z)) > 0 ? blockId : 0;
      }
   }

   @Nonnull
   public UUID getNewPathIdOnPrefabPasted(@Nullable UUID id, String name, int prefabId) {
      ConcurrentHashMap<UUID, UUID> prefabIdMap = this.pastedPrefabPathUUIDMap.get(prefabId);
      if (prefabIdMap == null) {
         prefabIdMap = new ConcurrentHashMap<>();
         this.pastedPrefabPathUUIDMap.put(prefabId, prefabIdMap);
      }

      if (id != null) {
         return prefabIdMap.computeIfAbsent(id, k -> UUID.randomUUID());
      }

      ConcurrentHashMap<String, UUID> prefabNameMap = this.pastedPrefabPathNameToUUIDMap.get(prefabId);
      if (prefabNameMap == null) {
         prefabNameMap = new ConcurrentHashMap<>();
         this.pastedPrefabPathNameToUUIDMap.put(prefabId, prefabNameMap);
      }

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

   public enum Action implements UndoAction {
      EDIT("server.builderTools.action.edit"),
      EDIT_SELECTION("server.builderTools.action.editSelection"),
      EDIT_LINE("server.builderTools.action.editLine"),
      CUT_COPY("server.builderTools.action.cutCopy", false),
      CUT_REMOVE("server.builderTools.action.cutRemove"),
      COPY("server.builderTools.action.copy", false),
      PASTE("server.builderTools.action.paste"),
      CLEAR("server.builderTools.action.clear"),
      ROTATE("server.builderTools.action.rotate"),
      FLIP("server.builderTools.action.flip"),
      MOVE("server.builderTools.action.move"),
      STACK("server.builderTools.action.stack"),
      SET("server.builderTools.action.set"),
      REPLACE("server.builderTools.action.replace"),
      EXTRUDE("server.builderTools.action.extrude"),
      UPDATE_SELECTION("server.builderTools.action.updateSelection", false),
      WALLS("server.builderTools.action.walls"),
      HOLLOW("server.builderTools.action.hollow"),
      LAYER("server.builderTools.action.layer"),
      ENTITY_TRANSFORM("server.builderTools.action.entityTransform"),
      ENTITY_SCALE("server.builderTools.action.entityScale"),
      ENTITY_CLONE("server.builderTools.action.entityClone"),
      ENTITY_REMOVE("server.builderTools.action.entityRemove"),
      ENTITY_FREEZE("server.builderTools.action.entityFreeze"),
      ENTITY_SETTINGS("server.builderTools.action.entitySettings"),
      TRIGGER_VOLUME("server.builderTools.action.triggerVolume", false);

      private final String translationKey;
      private final boolean marksPrefabDirty;

      Action(String translationKey) {
         this(translationKey, true);
      }

      Action(String translationKey, boolean marksPrefabDirty) {
         this.translationKey = translationKey;
         this.marksPrefabDirty = marksPrefabDirty;
      }

      @Nonnull
      @Override
      public String id() {
         return "builtin:" + this.name().toLowerCase();
      }

      @Nonnull
      @Override
      public Message toMessage() {
         return Message.translation(this.translationKey);
      }

      @Override
      public boolean marksPrefabDirty() {
         return this.marksPrefabDirty;
      }
   }

   public static class ActionEntry {
      private final UndoAction action;
      private final List<SelectionSnapshot<?>> snapshots;
      private boolean entityNotFound;
      private int cumulativeRotXBefore;
      private int cumulativeRotYBefore;
      private int cumulativeRotZBefore;

      public ActionEntry(UndoAction action, SelectionSnapshot<?> snapshots) {
         this(action, Collections.singletonList(snapshots));
      }

      public ActionEntry(UndoAction action, List<SelectionSnapshot<?>> snapshots) {
         this.action = action;
         this.snapshots = snapshots;
      }

      public UndoAction getAction() {
         return this.action;
      }

      @Nonnull
      public List<SelectionSnapshot<?>> getSnapshots() {
         return this.snapshots;
      }

      public boolean isEntityNotFound() {
         return this.entityNotFound;
      }

      public void setCumulativeRotBefore(int x, int y, int z) {
         this.cumulativeRotXBefore = x;
         this.cumulativeRotYBefore = y;
         this.cumulativeRotZBefore = z;
      }

      public int getCumulativeRotXBefore() {
         return this.cumulativeRotXBefore;
      }

      public int getCumulativeRotYBefore() {
         return this.cumulativeRotYBefore;
      }

      public int getCumulativeRotZBefore() {
         return this.cumulativeRotZBefore;
      }

      @Nonnull
      public BuilderToolsPlugin.ActionEntry restore(Ref<EntityStore> ref, PlayerRef playerRef, World world, ComponentAccessor<EntityStore> componentAccessor) {
         List<SelectionSnapshot<?>> collector = Collections.emptyList();
         List<Ref<EntityStore>> recreatedEntityRefs = null;
         boolean handledViaLastTransformRefs = false;
         if (this.action instanceof BuilderToolsPlugin.Action builtinAction && builtinAction == BuilderToolsPlugin.Action.ROTATE) {
            PrototypePlayerBuilderToolSettings protoSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
            List<Ref<EntityStore>> currentRefs = protoSettings.getLastTransformEntityRefs();
            if (currentRefs != null) {
               handledViaLastTransformRefs = true;
               Store<EntityStore> entityStore = world.getEntityStore().getStore();

               for (Ref<EntityStore> currentRef : currentRefs) {
                  if (currentRef != null && currentRef.isValid()) {
                     collector = collector.isEmpty() ? new ObjectArrayList<>() : collector;
                     collector.add(new EntityRemoveSnapshot(currentRef));
                     entityStore.removeEntity(currentRef, RemoveReason.UNLOAD);
                  }
               }

               protoSettings.setLastTransformEntityRefs(null);
            }
         }

         boolean entityNotFound = false;

         for (SelectionSnapshot<?> snapshot : this.snapshots) {
            if (!handledViaLastTransformRefs || !(snapshot instanceof EntityAddSnapshot)) {
               SelectionSnapshot<?> nextSnapshot = snapshot.restore(ref, playerRef, world, componentAccessor);
               if (nextSnapshot != null) {
                  collector = collector.isEmpty() ? new ObjectArrayList<>() : collector;
                  collector.add(nextSnapshot);
                  if (nextSnapshot instanceof EntityAddSnapshot entityAddSnapshot) {
                     if (recreatedEntityRefs == null) {
                        recreatedEntityRefs = new ReferenceArrayList<>();
                     }

                     recreatedEntityRefs.add(entityAddSnapshot.getEntityRef());
                  }
               } else if (snapshot instanceof EntitySnapshot) {
                  entityNotFound = true;
               }
            }
         }

         if (this.action instanceof BuilderToolsPlugin.Action builtinAction2
            && (builtinAction2 == BuilderToolsPlugin.Action.ROTATE || builtinAction2 == BuilderToolsPlugin.Action.CUT_REMOVE)
            && recreatedEntityRefs != null
            && !recreatedEntityRefs.isEmpty()) {
            PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
            prototypeSettings.setLastTransformEntityRefs(recreatedEntityRefs);
         }

         BuilderToolsPlugin.ActionEntry result = new BuilderToolsPlugin.ActionEntry(this.action, collector);
         result.entityNotFound = entityNotFound;
         return result;
      }
   }

   public static class BuilderState {
      private static final MetricsRegistry<BuilderToolsPlugin.BuilderState> STATE_METRICS_REGISTRY = new MetricsRegistry<BuilderToolsPlugin.BuilderState>()
         .register("Uuid", state -> state.playerRef.getUuid(), Codec.UUID_STRING)
         .register("Username", BuilderToolsPlugin.BuilderState::getDisplayName, Codec.STRING)
         .register("ActivePrefabPath", BuilderToolsPlugin.BuilderState::getActivePrefabPath, Codec.UUID_STRING)
         .register("Selection", BuilderToolsPlugin.BuilderState::getSelection, BlockSelection.METRICS_REGISTRY)
         .register("TaskFuture", state -> Objects.toString(state.getTaskFuture()), Codec.STRING)
         .register("TaskCount", BuilderToolsPlugin.BuilderState::getTaskCount, Codec.INTEGER)
         .register("UndoCount", BuilderToolsPlugin.BuilderState::getUndoCount, Codec.INTEGER)
         .register("RedoCount", BuilderToolsPlugin.BuilderState::getRedoCount, Codec.INTEGER);
      @Deprecated(forRemoval = true)
      private Player player;
      private PlayerRef playerRef;
      @Nonnull
      private final BuilderToolsUserData userData;
      private final StampedLock undoLock = new StampedLock();
      private final ObjectArrayFIFOQueue<BuilderToolsPlugin.ActionEntry> undo = new ObjectArrayFIFOQueue<>();
      private final ObjectArrayFIFOQueue<BuilderToolsPlugin.ActionEntry> redo = new ObjectArrayFIFOQueue<>();
      private final StampedLock taskLock = new StampedLock();
      private final ObjectArrayFIFOQueue<BuilderToolsPlugin.QueuedTask> tasks = new ObjectArrayFIFOQueue<>();
      @Nullable
      private volatile CompletableFuture<Void> taskFuture;
      private volatile long timestamp = Long.MAX_VALUE;
      private BlockSelection selection;
      @Nullable
      private Vector3i rawPos1;
      @Nullable
      private Vector3i rawPos2;
      private boolean skipNextPreviewRebuild;
      private int cumulativeRotX;
      private int cumulativeRotY;
      private int cumulativeRotZ;
      @Nullable
      private BlockSelection preRotationSnapshot;
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
      @Nullable
      private BlockSelection pendingUndoSnapshot;
      private List<EntityAddSnapshot> pendingEntitySnapshots = new ArrayList<>();
      private List<EntityTransformSnapshot> pendingEntityTransformSnapshots = new ArrayList<>();
      private int executionCountInGroup;
      private static final double SHADING_MAX_LOS_RANGE = 48.0;
      private static final double SHADING_AMBIENT = 0.2;
      private static final int[] SOBEL_SMOOTH = new int[]{1, 4, 6, 4, 1};
      private static final int[] SOBEL_DERIV = new int[]{-1, -2, 0, 2, 1};
      @Nullable
      private EntityTransformSnapshot pendingEntityTransformSession;
      @Nullable
      private EntityScaleSnapshot pendingEntityScaleSession;
      @Nullable
      private Ref<EntityStore> pendingEntityScaleRef;

      private BuilderState(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
         this.player = player;
         this.playerRef = playerRef;
         this.userData = BuilderToolsUserData.get(player);
      }

      private void release() {
         this.timestamp = System.nanoTime();
      }

      private void retain(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
         long stamp = this.taskLock.writeLock();

         try {
            this.player = player;
            this.playerRef = playerRef;
            this.timestamp = Long.MAX_VALUE;
            if (this.selection != null) {
               this.sendArea();
            }
         } finally {
            this.taskLock.unlockWrite(stamp);
         }
      }

      public void syncRawPositions() {
         if (this.selection != null && this.selection.hasSelectionBounds()) {
            this.rawPos1 = this.selection.getSelectionMin();
            this.rawPos2 = this.selection.getSelectionMax();
         } else {
            this.rawPos1 = null;
            this.rawPos2 = null;
         }
      }

      public <T extends Throwable> void addToQueue(
         @Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderToolsPlugin.BuilderState, ComponentAccessor<EntityStore>, T> task
      ) {
         long stamp = this.taskLock.writeLock();

         try {
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("[%s] Add task with ComponentAccessor to queue %s: %s, %s", this.getDisplayName(), task, this.taskFuture, this.tasks);
            this.tasks.enqueue(new BuilderToolsPlugin.QueuedTask(task));
            if (this.taskFuture == null || this.taskFuture.isDone()) {
               this.taskFuture = CompletableFutureUtil._catch(CompletableFuture.runAsync(this::runTask, this.player.getWorld()));
            }
         } finally {
            this.taskLock.unlockWrite(stamp);
         }
      }

      public <T extends Throwable> void computeSelectionCopy(@Nonnull ThrowableConsumer<BlockSelection, T> task) {
         this.addToQueue(
            (r, b, componentAccessor) -> {
               long start = System.nanoTime();
               if (this.selection == null) {
                  this.selection = new BlockSelection();
               }

               BlockSelection oldSelection = this.selection;
               this.pushHistory(BuilderToolsPlugin.Action.COPY, BlockSelectionSnapshot.copyOf(this.selection));
               this.selection = new BlockSelection();
               this.preRotationSnapshot = null;
               this.selection.setPosition(oldSelection.getX(), oldSelection.getY(), oldSelection.getZ());
               this.selection.setSelectionArea(oldSelection.getSelectionMin(), oldSelection.getSelectionMax());
               task.accept(this.selection);
               this.syncRawPositions();
               long diff = System.nanoTime() - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log(
                     "Took: %dns (%dms) to execute computeSelectionCopy for %s which copied %d blocks",
                     diff,
                     TimeUnit.NANOSECONDS.toMillis(diff),
                     task,
                     this.selection.getBlockCount()
                  );
               this.sendUpdate();
            }
         );
      }

      public void runTask() {
         Ref<EntityStore> ref = this.playerRef.getReference();
         if (ref != null && ref.isValid()) {
            Store<EntityStore> store = ref.getStore();

            while (true) {
               long stamp = this.taskLock.readLock();

               try {
                  if (this.tasks.isEmpty()) {
                     break;
                  }
               } finally {
                  this.taskLock.unlockRead(stamp);
               }

               try {
                  long stamp2 = this.taskLock.writeLock();

                  BuilderToolsPlugin.QueuedTask task;
                  try {
                     task = this.tasks.dequeue();
                     BuilderToolsPlugin.get()
                        .getLogger()
                        .at(Level.FINE)
                        .log("[%s] Run task from queue: %s, %s, %s", this.getDisplayName(), task, this.taskFuture, this.tasks);
                  } finally {
                     this.taskLock.unlockWrite(stamp2);
                  }

                  task.execute(ref, this, store);
               } catch (Throwable e) {
                  BuilderToolsPlugin.get().getLogger().at(Level.SEVERE).withCause(e).log("Failed to execute builder tools task for: %s", this.getDisplayName());
               }
            }

            this.taskFuture = null;
         } else {
            this.taskFuture = null;
         }
      }

      public int getTaskCount() {
         long stamp = this.taskLock.readLock();

         try {
            return this.tasks.size();
         } finally {
            this.taskLock.unlockRead(stamp);
         }
      }

      public int getUndoCount() {
         long stamp = this.taskLock.readLock();

         try {
            return this.undo.size();
         } finally {
            this.taskLock.unlockRead(stamp);
         }
      }

      public int getRedoCount() {
         long stamp = this.taskLock.readLock();

         try {
            return this.redo.size();
         } finally {
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

      @Nullable
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
         this.preRotationSnapshot = null;
      }

      public void setSkipNextPreviewRebuild(boolean skip) {
         this.skipNextPreviewRebuild = skip;
      }

      public void sendSelectionToClient() {
         this.sendUpdate();
      }

      private void sendErrorFeedback(@Nonnull Ref<EntityStore> ref, @Nonnull Message message, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         this.sendFeedback(ref, message, "CREATE_ERROR", NotificationStyle.Warning, componentAccessor);
      }

      private void sendFeedback(
         @Nonnull Ref<EntityStore> ref, @Nonnull Message message, @Nullable String sound, @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         this.sendFeedback(message, componentAccessor);
         if (sound != null) {
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex(sound), SoundCategory.UI, componentAccessor);
         }
      }

      private void sendFeedback(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull Message message,
         @Nullable String sound,
         @Nonnull NotificationStyle notificationStyle,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         this.sendFeedback(message, notificationStyle, componentAccessor);
         if (sound != null) {
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex(sound), SoundCategory.UI, componentAccessor);
         }
      }

      private void sendFeedback(@Nonnull Message message, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         BuilderToolsPlugin.sendFeedback(message, this.playerRef, NotificationStyle.Default, componentAccessor);
      }

      private void sendFeedback(
         @Nonnull Message message, @Nonnull NotificationStyle notificationStyle, @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         BuilderToolsPlugin.sendFeedback(message, this.playerRef, notificationStyle, componentAccessor);
      }

      private void sendFeedback(@Nonnull String key, int total, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         BuilderToolsPlugin.sendFeedback(key, total, this.playerRef, componentAccessor);
      }

      private void sendFeedback(@Nonnull String key, int total, int num, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         BuilderToolsPlugin.sendFeedback(key, total, num, this.playerRef, componentAccessor);
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
         World world = componentAccessor.getExternalData().getWorld();
         UUIDComponent uuidComponent = componentAccessor.getComponent(ref, UUIDComponent.getComponentType());
         assert uuidComponent != null;
         long start = System.nanoTime();

         ToolOperation toolOperation;
         try {
            toolOperation = ToolOperation.fromPacket(ref, this.player, this.playerRef, packet, componentAccessor);
         } catch (Exception e) {
            this.playerRef.sendMessage(Message.translation("server.builderTools.interaction.toolParseError").param("error", e.getMessage()));
            return 0;
         }

         PrototypePlayerBuilderToolSettings protoSettings = ToolOperation.PROTOTYPE_TOOL_SETTINGS.get(uuidComponent.getUuid());
         if (protoSettings != null && toolOperation instanceof PaintOperation) {
            BuilderTool builderTool = BuilderTool.getActiveBuilderTool(ref, componentAccessor);
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
                     CommandManager.get().handleCommand(this.playerRef, brushConfigId).thenAccept(unused -> {
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
               ItemStack itemInHand = InventoryComponent.getItemInHand(componentAccessor, ref);
               if (itemInHand != null && itemInHand.getItemId().equals(protoSettings.getPrototypeItemId())) {
                  toolOperation.executeAsBrushConfig(protoSettings, packet, componentAccessor);
                  return 0;
               }
            }
         }

         Vector3i currentPosition = toolOperation.getPosition();
         Vector3i lastPosition = protoSettings != null && packet.isHoldDownInteraction ? protoSettings.getLastBrushPosition() : null;
         List<Vector3i> positionsToExecute = ToolOperation.calculateInterpolatedPositions(
            lastPosition, currentPosition, toolOperation.getBrushWidth(), toolOperation.getBrushHeight(), toolOperation.getBrushSpacing()
         );
         if (positionsToExecute.isEmpty()) {
            return 0;
         }

         for (Vector3i position : positionsToExecute) {
            toolOperation.executeAt(position.x(), position.y(), position.z(), componentAccessor);
         }

         if (protoSettings != null) {
            protoSettings.setLastBrushPosition(positionsToExecute.get(positionsToExecute.size() - 1));
         }

         EditOperation edit = toolOperation.getEditOperation();
         BlockSelection before = edit.getBefore();
         BlockSelection after = edit.getAfter();
         int undoGroupSize = packet.undoGroupSize > 0 ? packet.undoGroupSize : 10;
         this.handleBrushUndoGrouping(before, edit.getSpawnedEntityRefs(), edit.getMovedEntitySnapshots(), undoGroupSize, packet.isHoldDownInteraction);
         after.placeNoReturn("Use Builder Tool ?/?", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - start;
         int size = after.getBlockCount() + after.getFluidCount() + after.getTintCount();
         int interpolatedCount = positionsToExecute.size();
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute edit of %d blocks (%d positions)", diff, TimeUnit.NANOSECONDS.toMillis(diff), size, interpolatedCount);
         if (size > 0 && protoSettings != null && BuilderToolsPlugin.shouldShowNotification(ref, componentAccessor) && toolOperation.showEditNotification()) {
            this.sendFeedback("Edit", size, componentAccessor);
         }

         return size;
      }

      public void placeBrushConfig(
         @Nonnull Ref<EntityStore> ref,
         long startTime,
         @Nonnull BrushConfigEditStore brushConfigEditStore,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         PlayerRef playerRefComponent = componentAccessor.getComponent(ref, PlayerRef.getComponentType());
         assert playerRefComponent != null;
         World world = componentAccessor.getExternalData().getWorld();
         BlockSelection after = brushConfigEditStore.getAfter();
         BlockSelection before = brushConfigEditStore.getBefore();
         PrototypePlayerBuilderToolSettings prototypePlayerBuilderToolSettings = ToolOperation.PROTOTYPE_TOOL_SETTINGS.get(playerRefComponent.getUuid());
         BrushConfig brushConfig = brushConfigEditStore.getBrushConfig();
         int undoGroupSize = prototypePlayerBuilderToolSettings != null ? prototypePlayerBuilderToolSettings.getUndoGroupSize() : 10;
         boolean isHoldDown = brushConfig != null && brushConfig.isHoldDownInteraction();
         this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
         after.placeNoReturn("Use Builder Tool ?/?", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - startTime;
         int size = after.getBlockCount() + after.getFluidCount() + after.getTintCount();
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute edit of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
         if (size > 0 && BuilderToolsPlugin.shouldShowNotification(playerRefComponent.getReference(), componentAccessor)) {
            this.sendFeedback("Edit", size, componentAccessor);
         }
      }

      public void flood(
         @Nonnull EditOperation editOperation, int x, int y, int z, int shapeWidth, int shapeHeight, @Nonnull BlockPattern pattern, int targetBlockId
      ) {
         int halfWidth = shapeWidth / 2;
         int halfHeight = shapeHeight / 2;
         Vector3i min = new Vector3i(x - halfWidth, y - halfHeight, z - halfWidth);
         Vector3i max = new Vector3i(x + halfWidth, y + halfHeight, z + halfWidth);
         OverridableChunkAccessor accessor = editOperation.getAccessor();
         LongOpenHashSet checkedPositions = new LongOpenHashSet();
         LongArrayList floodPositions = new LongArrayList();
         floodPositions.push(BlockUtil.pack(x, y, z));

         do {
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

            long west = BlockUtil.pack(px - 1, py, pz);
            if (this.isFloodPossible(accessor, west, min, max, blockId, targetBlockId) && !checkedPositions.contains(west)) {
               floodPositions.push(west);
            }

            long top = BlockUtil.pack(px, py + 1, pz);
            if (this.isFloodPossible(accessor, top, min, max, blockId, targetBlockId) && !checkedPositions.contains(top)) {
               floodPositions.push(top);
            }

            long bottom = BlockUtil.pack(px, py - 1, pz);
            if (this.isFloodPossible(accessor, bottom, min, max, blockId, targetBlockId) && !checkedPositions.contains(bottom)) {
               floodPositions.push(bottom);
            }

            long north = BlockUtil.pack(px, py, pz + 1);
            if (this.isFloodPossible(accessor, north, min, max, blockId, targetBlockId) && !checkedPositions.contains(north)) {
               floodPositions.push(north);
            }

            long south = BlockUtil.pack(px, py, pz - 1);
            if (this.isFloodPossible(accessor, south, min, max, blockId, targetBlockId) && !checkedPositions.contains(south)) {
               floodPositions.push(south);
            }

            if (this.isFloodPossible(accessor, packedPosition, min, max, blockId, targetBlockId)) {
               editOperation.setBlock(px, py, pz, blockId);
            }
         } while (!floodPositions.isEmpty());
      }

      private boolean isFloodPossible(
         @Nonnull ChunkAccessor accessor, long blockPosition, @Nonnull Vector3i min, @Nonnull Vector3i max, int blockId, int targetBlockId
      ) {
         int x = BlockUtil.unpackX(blockPosition);
         int y = BlockUtil.unpackY(blockPosition);
         int z = BlockUtil.unpackZ(blockPosition);
         if (x >= min.x() && y >= min.y() && z >= min.z() && x <= max.x() && y <= max.y() && z <= max.z()) {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            BlockType blockType = assetMap.getAsset(accessor.getBlock(x, y, z));
            return accessor.getBlock(x, y, z) == targetBlockId || blockType.getDrawType() != DrawType.Cube && blockType.getDrawType() != DrawType.CubeWithModel;
         } else {
            return false;
         }
      }

      public boolean isAsideAir(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
         return accessor.getBlock(x + 1, y, z) <= 0
            || accessor.getBlock(x - 1, y, z) <= 0
            || accessor.getBlock(x, y + 1, z) <= 0
            || accessor.getBlock(x, y - 1, z) <= 0
            || accessor.getBlock(x, y, z + 1) <= 0
            || accessor.getBlock(x, y, z - 1) <= 0;
      }

      public boolean isAsideBlock(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
         return accessor.getBlock(x, y, z) <= 0
            && (
               accessor.getBlock(x + 1, y, z) > 0
                  || accessor.getBlock(x - 1, y, z) > 0
                  || accessor.getBlock(x, y + 1, z) > 0
                  || accessor.getBlock(x, y - 1, z) > 0
                  || accessor.getBlock(x, y, z + 1) > 0
                  || accessor.getBlock(x, y, z - 1) > 0
            );
      }

      @Nonnull
      public BuilderToolsPlugin.BuilderState.BlocksSampleData getBlocksSampleData(@Nonnull ChunkAccessor accessor, int x, int y, int z, int radius) {
         BuilderToolsPlugin.BuilderState.BlocksSampleData data = new BuilderToolsPlugin.BuilderState.BlocksSampleData();
         Int2IntMap blockCounts = new Int2IntOpenHashMap();

         for (int ix = x - radius; ix <= x + radius; ix++) {
            for (int iz = z - radius; iz <= z + radius; iz++) {
               for (int iy = y - radius; iy <= y + radius; iy++) {
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
            if (count > data.mainBlockNotAirCount && block != 0) {
               data.mainBlockNotAir = block;
               data.mainBlockNotAirCount = count;
            }
         }

         return data;
      }

      @Nonnull
      public BuilderToolsPlugin.BuilderState.SmoothSampleData getBlocksSmoothData(@Nonnull ChunkAccessor accessor, int x, int y, int z) {
         BuilderToolsPlugin.BuilderState.SmoothSampleData data = new BuilderToolsPlugin.BuilderState.SmoothSampleData();
         Int2IntMap blockCounts = new Int2IntOpenHashMap();
         int kernelIndex = 0;

         for (int ix = x - 1; ix <= x + 1; ix++) {
            for (int iy = y - 1; iy <= y + 1; iy++) {
               for (int iz = z - 1; iz <= z + 1; iz++) {
                  int currentBlock = accessor.getBlock(ix, iy, iz);
                  blockCounts.put(currentBlock, blockCounts.getOrDefault(currentBlock, 0) + BuilderToolsPlugin.SMOOTHING_KERNEL[kernelIndex++]);
               }
            }
         }

         float solidCount = 0.0F;
         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();

         for (Int2IntMap.Entry pair : Int2IntMaps.fastIterable(blockCounts)) {
            int block = pair.getIntKey();
            int count = pair.getIntValue();
            BlockType blockType = assetMap.getAsset(block);
            if (blockType.getMaterial() == BlockMaterial.Solid) {
               solidCount += count;
               if (count > data.solidBlockCount) {
                  data.solidBlock = block;
                  data.solidBlockCount = count;
               }
            } else if (count > data.fillerBlockCount) {
               data.fillerBlock = block;
               data.fillerBlockCount = count;
            }
         }

         data.solidStrength = solidCount / 27.0F;
         return data;
      }

      public void editLine(
         int x1,
         int y1,
         int z1,
         int x2,
         int y2,
         int z2,
         BlockPattern material,
         int lineWidth,
         int lineHeight,
         int wallThickness,
         BrushShape shape,
         BrushOrigin origin,
         int spacing,
         int density,
         @Nullable BlockMask mask,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         World world = componentAccessor.getExternalData().getWorld();
         long start = System.nanoTime();
         float halfWidth = lineWidth / 2.0F;
         float halfHeight = lineHeight / 2.0F;
         int iHalfWidth = MathUtil.fastCeil(halfWidth);
         int iHalfHeight = MathUtil.fastCeil(halfHeight);
         int maxRadius = Math.max(iHalfWidth, iHalfHeight);
         Vector3i min = new Vector3i(Math.min(x1, x2) - maxRadius, Math.min(y1, y2) - maxRadius, Math.min(z1, z2) - maxRadius);
         Vector3i max = new Vector3i(Math.max(x1, x2) + maxRadius, Math.max(y1, y2) + maxRadius, Math.max(z1, z2) + maxRadius);
         BlockSelection before = new BlockSelection();
         before.setPosition(x1, y1, z1);
         before.setSelectionArea(min, max);
         this.pushHistory(BuilderToolsPlugin.Action.EDIT_LINE, new BlockSelectionSnapshot(before));
         BlockSelection after = new BlockSelection(before);
         int originOffset = 0;
         if (origin == BrushOrigin.Bottom) {
            originOffset = iHalfHeight + 1;
         } else if (origin == BrushOrigin.Top) {
            originOffset = -iHalfHeight;
         }

         float innerHalfWidth = Math.max(0.0F, halfWidth - wallThickness);
         float innerHalfHeight = Math.max(0.0F, halfHeight - wallThickness);
         Predicate<Vector3i> isInShape = this.createShapePredicate(shape, halfWidth, halfHeight, innerHalfWidth, innerHalfHeight, wallThickness > 0);
         int lineDistX = x2 - x1;
         int lineDistZ = z2 - z1;
         int halfLineDistX = lineDistX / 2;
         int halfLineDistZ = lineDistZ / 2;
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(
            world, x1 + halfLineDistX, z1 + halfLineDistZ, Math.max(Math.abs(lineDistX), Math.abs(lineDistZ)) + maxRadius + 1
         );
         Vector3i rel = new Vector3i();
         LineIterator line = new LineIterator(x1, y1, z1, x2, y2, z2);
         int stepCount = 0;

         while (line.hasNext()) {
            Vector3i coord = line.next();
            if (stepCount % spacing != 0) {
               stepCount++;
            } else {
               stepCount++;

               for (int sx = -iHalfWidth; sx <= iHalfWidth; sx++) {
                  for (int sz = iHalfWidth; sz >= -iHalfWidth; sz--) {
                     int blockX = coord.x() + sx;
                     int blockZ = coord.z() + sz;
                     WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(blockX, blockZ));

                     for (int sy = -iHalfHeight; sy <= iHalfHeight; sy++) {
                        rel.set(sx, sy, sz);
                        if (isInShape.test(rel)) {
                           int blockY = coord.y() + sy + originOffset;
                           int currentBlockId = chunk.getBlock(blockX, blockY, blockZ);
                           int currentFluidId = chunk.getFluidId(blockX, blockY, blockZ);
                           if ((mask == null || !mask.isExcluded(accessor, blockX, blockY, blockZ, min, max, currentBlockId, currentFluidId))
                              && this.random.nextInt(100) < density) {
                              int blockId = material.nextBlock(this.random);
                              before.addBlockAtWorldPos(
                                 blockX,
                                 blockY,
                                 blockZ,
                                 currentBlockId,
                                 chunk.getRotationIndex(blockX, blockY, blockZ),
                                 chunk.getFiller(blockX, blockY, blockZ),
                                 chunk.getSupportValue(blockX, blockY, blockZ),
                                 chunk.getBlockComponentHolder(blockX, blockY, blockZ)
                              );
                              after.addBlockAtWorldPos(blockX, blockY, blockZ, blockId, 0, 0, 0);
                           }
                        }
                     }
                  }
               }
            }
         }

         after.placeNoReturn("Edit 1/1", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - start;
         int size = after.getBlockCount();
         double length = new Vector3i(x1, y1, z1).distance(x2, y2, z2);
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute editLine of %d blocks with length %s", diff, TimeUnit.NANOSECONDS.toMillis(diff), size, length);
         this.sendFeedback(Message.translation("server.builderTools.drawLineOf").param("count", Math.round(length)), componentAccessor);
      }

      private Predicate<Vector3i> createShapePredicate(
         BrushShape shape, float halfWidth, float halfHeight, float innerHalfWidth, float innerHalfHeight, boolean hollow
      ) {
         float hw = halfWidth + 0.41F;
         float hh = halfHeight + 0.41F;
         float ihw = innerHalfWidth + 0.41F;
         float ihh = innerHalfHeight + 0.41F;

         return switch (shape) {
            case Cube -> coord -> {
               double ax = Math.abs(coord.x());
               double ay = Math.abs(coord.y());
               double az = Math.abs(coord.z());
               boolean inOuter = ax <= hw && ay <= hh && az <= hw;
               if (!hollow) {
                  return inOuter;
               }

               boolean inInner = ax < ihw && ay < ihh && az < ihw;
               return inOuter && !inInner;
            };
            case Sphere -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double outerDist = sx * sx / (hw * hw) + sy * sy / (hh * hh) + sz * sz / (hw * hw);
               boolean inOuter = outerDist <= 1.0;
               if (!hollow) {
                  return inOuter;
               }

               double innerDist = sx * sx / (ihw * ihw) + sy * sy / (ihh * ihh) + sz * sz / (ihw * ihw);
               boolean inInner = ihw > 0.0F && ihh > 0.0F && innerDist <= 1.0;
               return inOuter && !inInner;
            };
            case Cylinder -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double outerRadialDist = (sx * sx + sz * sz) / (hw * hw);
               boolean inOuterRadius = outerRadialDist <= 1.0 && Math.abs(sy) <= hh;
               if (!hollow) {
                  return inOuterRadius;
               }

               double innerRadialDist = (sx * sx + sz * sz) / (ihw * ihw);
               boolean inInnerRadius = ihw > 0.0F && innerRadialDist <= 1.0 && Math.abs(sy) < ihh;
               return inOuterRadius && !inInnerRadius;
            };
            case Cone -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double normalizedY = (sy + hh) / (2.0F * hh);
               if (!(normalizedY < 0.0) && !(normalizedY > 1.0)) {
                  double currentRadius = hw * (1.0 - normalizedY);
                  double radialDist = Math.sqrt(sx * sx + sz * sz);
                  boolean inOuter = radialDist <= currentRadius;
                  if (!hollow) {
                     return inOuter;
                  }

                  double innerRadius = Math.max(0.0, currentRadius - (hw - ihw));
                  boolean inInner = radialDist < innerRadius;
                  return inOuter && !inInner;
               } else {
                  return false;
               }
            };
            case InvertedCone -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double normalizedY = (sy + hh) / (2.0F * hh);
               if (!(normalizedY < 0.0) && !(normalizedY > 1.0)) {
                  double currentRadius = hw * normalizedY;
                  double radialDist = Math.sqrt(sx * sx + sz * sz);
                  boolean inOuter = radialDist <= currentRadius;
                  if (!hollow) {
                     return inOuter;
                  }

                  double innerRadius = Math.max(0.0, currentRadius - (hw - ihw));
                  boolean inInner = radialDist < innerRadius;
                  return inOuter && !inInner;
               } else {
                  return false;
               }
            };
            case Pyramid -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double normalizedY = (sy + hh) / (2.0F * hh);
               if (!(normalizedY < 0.0) && !(normalizedY > 1.0)) {
                  double currentHalfSize = hw * (1.0 - normalizedY);
                  boolean inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
                  if (!hollow) {
                     return inOuter;
                  }

                  double innerHalfSize = Math.max(0.0, currentHalfSize - (hw - ihw));
                  boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
                  return inOuter && !inInner;
               } else {
                  return false;
               }
            };
            case InvertedPyramid -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double normalizedY = (sy + hh) / (2.0F * hh);
               if (!(normalizedY < 0.0) && !(normalizedY > 1.0)) {
                  double currentHalfSize = hw * normalizedY;
                  boolean inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
                  if (!hollow) {
                     return inOuter;
                  }

                  double innerHalfSize = Math.max(0.0, currentHalfSize - (hw - ihw));
                  boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
                  return inOuter && !inInner;
               } else {
                  return false;
               }
            };
            case Dome -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               if (sy < 0.0) {
                  return false;
               }

               double outerDist = sx * sx / (hw * hw) + sy * sy / (hh * hh) + sz * sz / (hw * hw);
               boolean inOuter = outerDist <= 1.0;
               if (!hollow) {
                  return inOuter;
               }

               double innerDist = sx * sx / (ihw * ihw) + sy * sy / (ihh * ihh) + sz * sz / (ihw * ihw);
               boolean inInner = ihw > 0.0F && ihh > 0.0F && innerDist <= 1.0;
               return inOuter && !inInner;
            };
            case InvertedDome -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               if (sy > 0.0) {
                  return false;
               }

               double outerDist = sx * sx / (hw * hw) + sy * sy / (hh * hh) + sz * sz / (hw * hw);
               boolean inOuter = outerDist <= 1.0;
               if (!hollow) {
                  return inOuter;
               }

               double innerDist = sx * sx / (ihw * ihw) + sy * sy / (ihh * ihh) + sz * sz / (ihw * ihw);
               boolean inInner = ihw > 0.0F && ihh > 0.0F && innerDist <= 1.0;
               return inOuter && !inInner;
            };
            case Diamond -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double normalizedY = Math.abs(sy) / hh;
               if (normalizedY > 1.0) {
                  return false;
               }

               double currentHalfSize = hw * (1.0 - normalizedY);
               boolean inOuter = Math.abs(sx) <= currentHalfSize && Math.abs(sz) <= currentHalfSize;
               if (!hollow) {
                  return inOuter;
               }

               double innerHalfSize = Math.max(0.0, currentHalfSize - (hw - ihw));
               boolean inInner = Math.abs(sx) < innerHalfSize && Math.abs(sz) < innerHalfSize;
               return inOuter && !inInner;
            };
            case Torus -> coord -> {
               double sx = coord.x();
               double sy = coord.y();
               double sz = coord.z();
               double minorRadius = Math.max(1.0F, hh / 2.0F);
               double majorRadius = Math.max(1.0, hw - minorRadius);
               double minorRadiusAdjusted = minorRadius + 0.41F;
               double distFromCenter = Math.sqrt(sx * sx + sz * sz);
               double distFromRing = distFromCenter - majorRadius;
               double distFromTube = Math.sqrt(distFromRing * distFromRing + sy * sy);
               boolean inOuter = distFromTube <= minorRadiusAdjusted;
               if (!hollow) {
                  return inOuter;
               }

               double innerMinorRadius = Math.max(0.0, minorRadiusAdjusted - (ihw > 0.0F ? hw - ihw : 0.0F));
               boolean inInner = innerMinorRadius > 0.0 && distFromTube < innerMinorRadius;
               return inOuter && !inInner;
            };
         };
      }

      private static boolean isBlockSolidCover(LocalCachedChunkAccessor accessor, int x, int y, int z) {
         int blockId = accessor.getBlock(x, y, z);
         if (blockId <= 0) {
            return false;
         }

         BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
         return blockType != null && blockType.getMaterial() == BlockMaterial.Solid;
      }

      private int[] getFaceHalfExtents(int normalX, int normalY, int normalZ, int halfWidth, int halfHeight) {
         if (Math.abs(normalX) == 1) {
            return new int[]{0, halfHeight, halfWidth};
         } else {
            return Math.abs(normalY) == 1 ? new int[]{halfWidth, 0, halfHeight} : new int[]{halfWidth, halfHeight, 0};
         }
      }

      private LongOpenHashSet findConnectedSurfaceBlocks(
         @Nonnull LocalCachedChunkAccessor accessor,
         int x,
         int y,
         int z,
         int normalX,
         int normalY,
         int normalZ,
         int halfWidth,
         int halfHeight,
         @Nonnull String filterMode,
         @Nonnull String strategy
      ) {
         int[] halfExtents = this.getFaceHalfExtents(normalX, normalY, normalZ, halfWidth, halfHeight);
         int xHalf = halfExtents[0];
         int yHalf = halfExtents[1];
         int zHalf = halfExtents[2];
         int sxMin = x - xHalf;
         int sxMax = x + xHalf;
         int syMin = y - yHalf;
         int syMax = y + yHalf;
         int szMin = z - zHalf;
         int szMax = z + zHalf;
         if (Math.abs(normalX) == 1) {
            sxMin = x;
            sxMax = x;
         } else if (Math.abs(normalY) == 1) {
            syMin = y;
            syMax = y;
         } else {
            szMin = z;
            szMax = z;
         }

         LongOpenHashSet connected = new LongOpenHashSet();
         LongArrayList queue = new LongArrayList();
         LongOpenHashSet visited = new LongOpenHashSet();
         long startPacked = BlockUtil.pack(x, y, z);
         if ("Default".equals(strategy) && isBlockSolidCover(accessor, x + normalX, y + normalY, z + normalZ)) {
            return connected;
         }

         queue.push(startPacked);
         visited.add(startPacked);
         int[][] faceNeighbors;
         if (Math.abs(normalX) == 1) {
            faceNeighbors = new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
         } else if (Math.abs(normalY) == 1) {
            faceNeighbors = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
         } else {
            faceNeighbors = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}};
         }

         int targetBlockId = accessor.getBlock(x, y, z);

         while (!queue.isEmpty()) {
            long packed = queue.popLong();
            int px = BlockUtil.unpackX(packed);
            int py = BlockUtil.unpackY(packed);
            int pz = BlockUtil.unpackZ(packed);
            int blockId = accessor.getBlock(px, py, pz);
            if (blockId > 0 && BuilderToolsPlugin.passesFilter(filterMode, blockId, targetBlockId)) {
               WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(px, pz));
               int fillerValue = chunk.getFiller(px, py, pz);
               if (fillerValue == 0) {
                  boolean include = true;
                  if ("FullBlocks".equals(filterMode) || "NotFullBlocks".equals(filterMode)) {
                     BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
                     boolean isFull = blockType != null && blockType.getMaterial() == BlockMaterial.Solid;
                     include = "FullBlocks".equals(filterMode) ? isFull : !isFull;
                  }

                  if (include) {
                     connected.add(packed);
                  }
               }

               for (int[] offset : faceNeighbors) {
                  int nx = px + offset[0];
                  int ny = py + offset[1];
                  int nz = pz + offset[2];
                  if (nx >= sxMin
                     && nx <= sxMax
                     && ny >= syMin
                     && ny <= syMax
                     && nz >= szMin
                     && nz <= szMax
                     && (!"Default".equals(strategy) || !isBlockSolidCover(accessor, nx + normalX, ny + normalY, nz + normalZ))) {
                     long neighborPacked = BlockUtil.pack(nx, ny, nz);
                     if (!visited.contains(neighborPacked)) {
                        visited.add(neighborPacked);
                        queue.push(neighborPacked);
                     }
                  }
               }
            }
         }

         return connected;
      }

      public void extendOrShrinkFace(
         int x,
         int y,
         int z,
         int normalX,
         int normalY,
         int normalZ,
         int depth,
         int extrudeWidth,
         int extrudeLength,
         boolean shrink,
         @Nonnull BlockPattern pattern,
         @Nonnull String filterMode,
         @Nonnull String strategy,
         int undoGroupSize,
         boolean isHoldDown,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         World world = componentAccessor.getExternalData().getWorld();
         long start = System.nanoTime();
         int halfWidth = extrudeWidth / 2;
         int halfHeight = extrudeLength / 2;
         int maxHalf = Math.max(halfWidth, halfHeight);
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, x, z, maxHalf + depth);
         int testBlock = accessor.getBlock(x, y, z);
         if (testBlock > 0) {
            LongOpenHashSet connected = this.findConnectedSurfaceBlocks(
               accessor, x, y, z, normalX, normalY, normalZ, halfWidth, halfHeight, filterMode, strategy
            );
            if (!connected.isEmpty()) {
               int totalBlocks = connected.size() * depth;
               BlockSelection before = new BlockSelection(totalBlocks, 0);
               before.setPosition(shrink ? x : x + normalX, shrink ? y : y + normalY, shrink ? z : z + normalZ);
               BlockSelection after = new BlockSelection(totalBlocks, 0);
               after.copyPropertiesFrom(before);
               int dirSign = shrink ? -1 : 1;
               int startOffset = shrink ? 0 : 1;

               for (long packed : connected) {
                  int sx = BlockUtil.unpackX(packed);
                  int sy = BlockUtil.unpackY(packed);
                  int sz = BlockUtil.unpackZ(packed);
                  WorldChunk sourceChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(sx, sz));
                  int sourceBlock = shrink ? 0 : sourceChunk.getBlock(sx, sy, sz);
                  int sourceRotation = shrink ? 0 : sourceChunk.getRotationIndex(sx, sy, sz);
                  int sourceFiller = shrink ? 0 : sourceChunk.getFiller(sx, sy, sz);
                  int sourceSupport = shrink ? 0 : sourceChunk.getSupportValue(sx, sy, sz);

                  for (int i = 0; i < depth; i++) {
                     int ex = sx + normalX * dirSign * (i + startOffset);
                     int ey = sy + normalY * dirSign * (i + startOffset);
                     int ez = sz + normalZ * dirSign * (i + startOffset);
                     WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(ex, ez));
                     int currentBlock = chunk.getBlock(ex, ey, ez);
                     int currentFiller = chunk.getFiller(ex, ey, ez);
                     if (shrink && currentBlock <= 0
                        || shrink && currentFiller != 0
                        || shrink && !BuilderToolsPlugin.passesFilter(filterMode, currentBlock, testBlock)) {
                        break;
                     }

                     Material material = null;
                     if (pattern != null && !pattern.isEmpty()) {
                        material = Material.fromPattern(pattern, this.random);
                     }

                     if (material == null || material.equals(Material.EMPTY)) {
                        before.addBlockAtWorldPos(
                           ex,
                           ey,
                           ez,
                           currentBlock,
                           chunk.getRotationIndex(ex, ey, ez),
                           chunk.getFiller(ex, ey, ez),
                           chunk.getSupportValue(ex, ey, ez),
                           chunk.getBlockComponentHolder(ex, ey, ez)
                        );
                        after.addBlockAtWorldPos(ex, ey, ez, sourceBlock, sourceRotation, sourceFiller, sourceSupport);
                     } else if (material.isFluid()) {
                        int currentFluidId = chunk.getFluidId(ex, ey, ez);
                        if (currentFluidId == 0) {
                           byte currentFluidLevel = chunk.getFluidLevel(ex, ey, ez);
                           before.addFluidAtWorldPos(ex, ey, ez, currentFluidId, currentFluidLevel);
                           after.addFluidAtWorldPos(ex, ey, ez, material.getFluidId(), material.getFluidLevel());
                        }
                     } else {
                        int newBlockId = material.getBlockId();
                        int newRotation = material.getRotation();
                        Holder<ChunkStore> holder = chunk.getBlockComponentHolder(ex, ey, ez);
                        Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, ex, ey, ez, newBlockId, currentBlock, holder, false);
                        int supportValue = chunk.getSupportValue(ex, ey, ez);
                        int filler = chunk.getFiller(ex, ey, ez);
                        int rotation = chunk.getRotationIndex(ex, ey, ez);
                        before.addBlockAtWorldPos(ex, ey, ez, currentBlock, rotation, filler, supportValue, holder);
                        after.addBlockAtWorldPos(ex, ey, ez, newBlockId, newRotation, 0, 0, newHolder);
                     }
                  }
               }

               String label = shrink ? "Shrink" : "Extrude";
               this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
               after.placeNoReturn(label, this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to %s %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), label.toLowerCase(), after.getBlockCount());
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      public void fillVolume(
         int x,
         int y,
         int z,
         int normalX,
         int normalY,
         int normalZ,
         int fillDepth,
         int fillWidth,
         int fillHeight,
         @Nonnull BlockPattern pattern,
         int undoGroupSize,
         boolean isHoldDown,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (!pattern.isEmpty()) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            int halfWidth = fillWidth / 2;
            int halfHeight = fillHeight / 2;
            int maxHalf = Math.max(halfWidth, halfHeight);
            int[] halfExtents = this.getFaceHalfExtents(normalX, normalY, normalZ, halfWidth, halfHeight);
            int xHalf = halfExtents[0];
            int yHalf = halfExtents[1];
            int zHalf = halfExtents[2];
            int startX = x + normalX;
            int startY = y + normalY;
            int startZ = z + normalZ;
            int sxMin = startX - xHalf;
            int sxMax = startX + xHalf;
            int syMin = startY - yHalf;
            int syMax = startY + yHalf;
            int szMin = startZ - zHalf;
            int szMax = startZ + zHalf;
            if (Math.abs(normalX) == 1) {
               sxMin = normalX > 0 ? startX : startX - fillDepth + 1;
               sxMax = normalX > 0 ? startX + fillDepth - 1 : startX;
            } else if (Math.abs(normalY) == 1) {
               syMin = normalY > 0 ? startY : startY - fillDepth + 1;
               syMax = normalY > 0 ? startY + fillDepth - 1 : startY;
            } else {
               szMin = normalZ > 0 ? startZ : startZ - fillDepth + 1;
               szMax = normalZ > 0 ? startZ + fillDepth - 1 : startZ;
            }

            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, x, z, maxHalf + fillDepth);
            if (accessor.getBlock(startX, startY, startZ) <= 0) {
               LongOpenHashSet connected = new LongOpenHashSet();
               LongArrayList queue = new LongArrayList();
               LongOpenHashSet visited = new LongOpenHashSet();
               long startPacked = BlockUtil.pack(startX, startY, startZ);
               queue.push(startPacked);
               visited.add(startPacked);
               int[][] neighbors = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};

               while (!queue.isEmpty()) {
                  long packed = queue.popLong();
                  int px = BlockUtil.unpackX(packed);
                  int py = BlockUtil.unpackY(packed);
                  int pz = BlockUtil.unpackZ(packed);
                  if (accessor.getBlock(px, py, pz) <= 0) {
                     connected.add(packed);

                     for (int[] offset : neighbors) {
                        int nx = px + offset[0];
                        int ny = py + offset[1];
                        int nz = pz + offset[2];
                        if (nx >= sxMin && nx <= sxMax && ny >= syMin && ny <= syMax && nz >= szMin && nz <= szMax) {
                           long neighborPacked = BlockUtil.pack(nx, ny, nz);
                           if (!visited.contains(neighborPacked)) {
                              visited.add(neighborPacked);
                              queue.push(neighborPacked);
                           }
                        }
                     }
                  }
               }

               int totalBlocks = connected.size() * fillDepth;
               BlockSelection before = new BlockSelection(totalBlocks, 0);
               before.setPosition(x, y, z);
               BlockSelection after = new BlockSelection(totalBlocks, 0);
               after.copyPropertiesFrom(before);

               for (long packed : connected) {
                  int bx = BlockUtil.unpackX(packed);
                  int by = BlockUtil.unpackY(packed);
                  int bz = BlockUtil.unpackZ(packed);
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(bx, bz));
                  int currentBlock = chunk.getBlock(bx, by, bz);
                  if (currentBlock <= 0) {
                     Material material = Material.fromPattern(pattern, this.random);
                     if (material.isFluid()) {
                        int currentFluidId = chunk.getFluidId(bx, by, bz);
                        if (currentFluidId == 0) {
                           byte currentFluidLevel = chunk.getFluidLevel(bx, by, bz);
                           before.addFluidAtWorldPos(bx, by, bz, currentFluidId, currentFluidLevel);
                           after.addFluidAtWorldPos(bx, by, bz, material.getFluidId(), material.getFluidLevel());
                        }
                     } else {
                        int newBlockId = material.getBlockId();
                        int newRotation = material.getRotation();
                        Holder<ChunkStore> holder = chunk.getBlockComponentHolder(bx, by, bz);
                        Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, bx, by, bz, newBlockId, currentBlock, holder, false);
                        int supportValue = chunk.getSupportValue(bx, by, bz);
                        int filler = chunk.getFiller(bx, by, bz);
                        int rotation = chunk.getRotationIndex(bx, by, bz);
                        before.addBlockAtWorldPos(bx, by, bz, currentBlock, rotation, filler, supportValue, holder);
                        after.addBlockAtWorldPos(bx, by, bz, newBlockId, newRotation, 0, 0, newHolder);
                     }
                  }
               }

               this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
               after.placeNoReturn("Fill", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to fill %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      private static boolean isFullCubeBlock(int blockId, int filler) {
         if (filler != 0) {
            return false;
         }

         BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
         return blockType != null && blockType.getHitboxTypeIndex() == 0 && blockType.getCustomModel() == null;
      }

      public void applyRecoloring(
         int targetX,
         int targetY,
         int targetZ,
         int width,
         int height,
         @Nonnull BlockPattern replacementPattern,
         boolean smartMatch,
         @Nonnull BrushShape brushShape,
         int undoGroupSize,
         boolean isHoldDown,
         @Nonnull BlockColorIndex blockColorIndex,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         World world = componentAccessor.getExternalData().getWorld();
         int halfWidth = width / 2;
         int halfHeight = height / 2;
         int maxHalf = Math.max(halfWidth, halfHeight);
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, targetX, targetZ, maxHalf);
         int targetBlockId = accessor.getBlock(targetX, targetY, targetZ);
         if (targetBlockId > 0) {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            String targetBaseMaterial = null;
            HashMap<String, Integer> variantCache = null;
            if (smartMatch) {
               BlockType targetType = assetMap.getAsset(targetBlockId);
               if (targetType == null) {
                  return;
               }

               variantCache = new HashMap<>();
               String targetKey = Objects.requireNonNullElse(targetType.getDefaultStateKey(), targetType.getId());
               targetBaseMaterial = BuilderToolsPlugin.getBlockBaseMaterial(targetKey);
            }

            int xHalf = halfWidth;
            int yHalf = halfHeight;
            int zHalf = halfWidth;
            Predicate<Vector3i> inShape = this.createShapePredicate(brushShape, halfWidth, halfHeight, 0.0F, 0.0F, false);
            Vector3i rel = new Vector3i();
            int totalBlocks = (xHalf * 2 + 1) * (yHalf * 2 + 1) * (zHalf * 2 + 1);
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(targetX, targetY, targetZ);
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);

            for (int x = -xHalf; x <= xHalf; x++) {
               for (int y = -yHalf; y <= yHalf; y++) {
                  for (int z = -zHalf; z <= zHalf; z++) {
                     int wx = targetX + x;
                     int wy = targetY + y;
                     int wz = targetZ + z;
                     if (wy >= 0 && wy < 320) {
                        rel.set(x, y, z);
                        if (inShape.test(rel)) {
                           WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(wx, wz));
                           int blockId = chunk.getBlock(wx, wy, wz);
                           BlockType currentType = assetMap.getAsset(blockId);
                           if (currentType != null) {
                              String currentKey = Objects.requireNonNullElse(currentType.getDefaultStateKey(), currentType.getId());
                              if (smartMatch
                                 ? BuilderToolsPlugin.getBlockBaseMaterial(currentKey).equals(targetBaseMaterial) && chunk.getFiller(wx, wy, wz) == 0
                                 : blockId == targetBlockId) {
                                 Material material = Material.fromPattern(replacementPattern, this.random);
                                 int newBlockId = material.getBlockId();
                                 int newRotation = material.getRotation();
                                 if (smartMatch) {
                                    BlockType inputType = assetMap.getAsset(newBlockId);
                                    String inputName = Objects.requireNonNullElse(inputType.getDefaultStateKey(), inputType.getId());
                                    String shape = BuilderToolsPlugin.getBlockShape(currentKey);
                                    if (!shape.isEmpty()) {
                                       int variantId = variantCache.computeIfAbsent(
                                          inputName + "|" + shape,
                                          k -> {
                                             int exact = assetMap.getIndex(inputName + "_" + shape);
                                             return exact != Integer.MIN_VALUE && !blockColorIndex.isExcludedQuality(exact)
                                                ? exact
                                                : blockColorIndex.findClosestVariant(material.getBlockId(), shape);
                                          }
                                       );
                                       if (variantId != Integer.MIN_VALUE && variantId >= 0) {
                                          newBlockId = variantId;
                                       }
                                    }

                                    newRotation = chunk.getRotationIndex(wx, wy, wz);
                                 }

                                 Holder<ChunkStore> holder = chunk.getBlockComponentHolder(wx, wy, wz);
                                 Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, wx, wy, wz, newBlockId, blockId, holder, false);
                                 int supportValue = chunk.getSupportValue(wx, wy, wz);
                                 int filler = chunk.getFiller(wx, wy, wz);
                                 int rotation = chunk.getRotationIndex(wx, wy, wz);
                                 BlockType currentBase = assetMap.getAsset(currentKey);
                                 String stateName = currentBase != null ? currentBase.getStateForBlock(currentType.getId()) : null;
                                 if (stateName != null && !stateName.equals("default")) {
                                    BlockType newBase = assetMap.getAsset(newBlockId);
                                    String statedKey = newBase != null ? newBase.getBlockKeyForState(stateName) : null;
                                    if (statedKey != null) {
                                       int statedId = assetMap.getIndex(statedKey);
                                       if (statedId != Integer.MIN_VALUE) {
                                          newBlockId = statedId;
                                       }
                                    }
                                 }

                                 before.addBlockAtWorldPos(wx, wy, wz, blockId, rotation, filler, supportValue, holder);
                                 after.addBlockAtWorldPos(wx, wy, wz, newBlockId, newRotation, 0, 0, newHolder);
                                 if (smartMatch) {
                                    this.replaceMultiBlockStructure(wx, wy, wz, blockId, newBlockId, newRotation, accessor, before, after);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
            after.placeNoReturn("Color", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            this.sendUpdate();
            this.sendArea();
         }
      }

      private static float edgeBlendFactor(@Nonnull BrushShape brushShape, int x, int y, int z, int halfWidth, int halfHeight, float edgeBlend) {
         if (edgeBlend <= 0.0F) {
            return 1.0F;
         }

         double rx = halfWidth + 0.5;
         double ry = halfHeight + 0.5;

         double d = switch (brushShape) {
            case Cube, Pyramid, InvertedPyramid, Diamond -> Math.max(Math.abs(x) / rx, Math.max(Math.abs(y) / ry, Math.abs(z) / rx));
            default -> Math.sqrt(x * x / (rx * rx) + y * y / (ry * ry) + z * z / (rx * rx));
         };
         double start = 1.0 - edgeBlend;
         return d <= start ? 1.0F : (float)Math.max(0.0, (1.0 - d) / edgeBlend);
      }

      public void applyGradient(
         int targetX,
         int targetY,
         int targetZ,
         int width,
         int height,
         int startX,
         int startY,
         int startZ,
         int endX,
         int endY,
         int endZ,
         @Nonnull List<BuilderToolsPlugin.ColorGradientMaterial> materials,
         @Nonnull String shape,
         float blendBand,
         @Nonnull BrushShape brushShape,
         boolean invert,
         int undoGroupSize,
         boolean isHoldDown,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (materials.size() >= 2) {
            World world = componentAccessor.getExternalData().getWorld();
            if ("Linear".equals(shape)) {
               this.applyLinearGradient(
                  targetX,
                  targetY,
                  targetZ,
                  width,
                  height,
                  startX,
                  startY,
                  startZ,
                  endX,
                  endY,
                  endZ,
                  materials,
                  blendBand,
                  brushShape,
                  invert,
                  undoGroupSize,
                  isHoldDown,
                  world,
                  componentAccessor
               );
            } else {
               this.applyShapedGradient(
                  targetX,
                  targetY,
                  targetZ,
                  width,
                  height,
                  startX,
                  startY,
                  startZ,
                  endX,
                  endY,
                  endZ,
                  materials,
                  shape,
                  blendBand,
                  brushShape,
                  invert,
                  undoGroupSize,
                  isHoldDown,
                  world,
                  componentAccessor
               );
            }
         }
      }

      private void applyLinearGradient(
         int targetX,
         int targetY,
         int targetZ,
         int width,
         int height,
         int startX,
         int startY,
         int startZ,
         int endX,
         int endY,
         int endZ,
         @Nonnull List<BuilderToolsPlugin.ColorGradientMaterial> materials,
         float blendBand,
         @Nonnull BrushShape brushShape,
         boolean invert,
         int undoGroupSize,
         boolean isHoldDown,
         @Nonnull World world,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         double lineLength = Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY) + (endZ - startZ) * (endZ - startZ));
         if (!(lineLength < 1.0)) {
            double normX = (endX - startX) / lineLength;
            double normY = (endY - startY) / lineLength;
            double normZ = (endZ - startZ) / lineLength;
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            int xHalf = halfWidth;
            int yHalf = halfHeight;
            int zHalf = halfWidth;
            Predicate<Vector3i> inShape = this.createShapePredicate(brushShape, halfWidth, halfHeight, 0.0F, 0.0F, false);
            Vector3i rel = new Vector3i();
            int totalBlocks = (xHalf * 2 + 1) * (yHalf * 2 + 1) * (zHalf * 2 + 1);
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, targetX, targetZ, Math.max(xHalf, zHalf) + 2);
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(targetX, targetY, targetZ);
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);

            for (int x = -xHalf; x <= xHalf; x++) {
               for (int y = -yHalf; y <= yHalf; y++) {
                  for (int z = -zHalf; z <= zHalf; z++) {
                     int wx = targetX + x;
                     int wy = targetY + y;
                     int wz = targetZ + z;
                     if (wy >= 0 && wy < 320) {
                        rel.set(x, y, z);
                        if (inShape.test(rel)) {
                           WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(wx, wz));
                           int blockId = chunk.getBlock(wx, wy, wz);
                           if (blockId > 0 && isFullCubeBlock(blockId, chunk.getFiller(wx, wy, wz))) {
                              double dx = wx - startX;
                              double dy = wy - startY;
                              double dz = wz - startZ;
                              double projection = (dx * normX + dy * normY + dz * normZ) / lineLength;
                              float t = (float)Math.max(0.0, Math.min(1.0, projection));
                              if (invert) {
                                 t = 1.0F - t;
                              }

                              BlockPattern pattern = this.getGradientMaterialAtPosition(materials, t, blendBand);
                              if (pattern != null) {
                                 this.applyGradientBlock(accessor, before, after, wx, wy, wz, blockId, pattern);
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
            after.placeNoReturn("Gradient", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            this.sendUpdate();
            this.sendArea();
         }
      }

      private void applyShapedGradient(
         int targetX,
         int targetY,
         int targetZ,
         int width,
         int height,
         int startX,
         int startY,
         int startZ,
         int endX,
         int endY,
         int endZ,
         @Nonnull List<BuilderToolsPlugin.ColorGradientMaterial> materials,
         @Nonnull String shape,
         float blendBand,
         @Nonnull BrushShape brushShape,
         boolean invert,
         int undoGroupSize,
         boolean isHoldDown,
         @Nonnull World world,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         double lineLength = Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY) + (endZ - startZ) * (endZ - startZ));
         if (!(lineLength < 1.0)) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            int xHalf = halfWidth;
            int yHalf = halfHeight;
            int zHalf = halfWidth;
            Predicate<Vector3i> inShape = this.createShapePredicate(brushShape, halfWidth, halfHeight, 0.0F, 0.0F, false);
            Vector3i rel = new Vector3i();
            int totalBlocks = (xHalf * 2 + 1) * (yHalf * 2 + 1) * (zHalf * 2 + 1);
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, targetX, targetZ, Math.max(xHalf, zHalf) + 2);
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(targetX, targetY, targetZ);
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);

            for (int x = -xHalf; x <= xHalf; x++) {
               for (int y = -yHalf; y <= yHalf; y++) {
                  for (int z = -zHalf; z <= zHalf; z++) {
                     int wx = targetX + x;
                     int wy = targetY + y;
                     int wz = targetZ + z;
                     if (wy >= 0 && wy < 320) {
                        rel.set(x, y, z);
                        if (inShape.test(rel)) {
                           WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(wx, wz));
                           int blockId = chunk.getBlock(wx, wy, wz);
                           if (blockId > 0 && isFullCubeBlock(blockId, chunk.getFiller(wx, wy, wz))) {
                              double dx = wx - startX;
                              double dy = wy - startY;
                              double dz = wz - startZ;
                              double adx = Math.abs(dx);
                              double ady = Math.abs(dy);
                              double adz = Math.abs(dz);

                              double dist = switch (shape) {
                                 case "Cube" -> Math.max(adx, Math.max(ady, adz));
                                 case "Diamond" -> adx + ady + adz;
                                 case "Cylinder" -> Math.max(Math.sqrt(dx * dx + dz * dz), ady);
                                 default -> Math.sqrt(dx * dx + dy * dy + dz * dz);
                              };
                              float t = (float)Math.min(1.0, dist / lineLength);
                              if (invert) {
                                 t = 1.0F - t;
                              }

                              BlockPattern pattern = this.getGradientMaterialAtPosition(materials, t, blendBand);
                              if (pattern != null) {
                                 this.applyGradientBlock(accessor, before, after, wx, wy, wz, blockId, pattern);
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
            after.placeNoReturn("Gradient", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            this.sendUpdate();
            this.sendArea();
         }
      }

      private void applyGradientBlock(
         @Nonnull LocalCachedChunkAccessor accessor,
         @Nonnull BlockSelection before,
         @Nonnull BlockSelection after,
         int x,
         int y,
         int z,
         int currentBlockId,
         @Nonnull BlockPattern pattern
      ) {
         WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
         Material material = Material.fromPattern(pattern, this.random);
         int newBlockId = material.getBlockId();
         int newRotation = material.getRotation();
         Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
         Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, newBlockId, currentBlockId, holder, false);
         int supportValue = chunk.getSupportValue(x, y, z);
         int filler = chunk.getFiller(x, y, z);
         int rotation = chunk.getRotationIndex(x, y, z);
         before.addBlockAtWorldPos(x, y, z, currentBlockId, rotation, filler, supportValue, holder);
         after.addBlockAtWorldPos(x, y, z, newBlockId, newRotation, 0, 0, newHolder);
      }

      @Nullable
      private BlockPattern getGradientMaterialAtPosition(@Nonnull List<BuilderToolsPlugin.ColorGradientMaterial> materials, float t, float blendBand) {
         float upper = 0.0F;
         int seg = materials.size() - 1;

         for (int i = 0; i < materials.size(); i++) {
            upper += materials.get(i).weight / 100.0F;
            if (t <= upper) {
               seg = i;
               break;
            }
         }

         if (blendBand <= 0.0F) {
            return materials.get(seg).pattern;
         }

         float lower = upper - materials.get(seg).weight / 100.0F;
         if (seg < materials.size() - 1 && upper - t < blendBand) {
            float nextWidth = materials.get(seg + 1).weight / 100.0F;
            float band = Math.min(blendBand, Math.min(upper - lower, nextWidth));
            if (upper - t < band) {
               float p = 0.5F * (1.0F - (upper - t) / band);
               if (this.random.nextFloat() < p) {
                  return materials.get(seg + 1).pattern;
               }
            }
         } else if (seg > 0 && t - lower < blendBand) {
            float prevWidth = materials.get(seg - 1).weight / 100.0F;
            float band = Math.min(blendBand, Math.min(upper - lower, prevWidth));
            if (t - lower < band) {
               float p = 0.5F * (1.0F - (t - lower) / band);
               if (this.random.nextFloat() < p) {
                  return materials.get(seg - 1).pattern;
               }
            }
         }

         return materials.get(seg).pattern;
      }

      public void applyShading(
         int targetX,
         int targetY,
         int targetZ,
         float playerX,
         float playerY,
         float playerZ,
         int width,
         int height,
         boolean lighten,
         boolean autoMode,
         boolean falloffFromTarget,
         @Nonnull BuilderToolsPlugin.ShadingLight lightSource,
         @Nullable int[] customRamp,
         float intensity,
         @Nonnull BrushShape brushShape,
         boolean invert,
         float edgeBlend,
         int undoGroupSize,
         boolean isHoldDown,
         @Nonnull BlockColorIndex blockColorIndex,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         World world = componentAccessor.getExternalData().getWorld();
         int halfWidth = width / 2;
         int halfHeight = height / 2;
         int maxHalf = Math.max(halfWidth, halfHeight);
         boolean falloffFromBrush = lightSource != BuilderToolsPlugin.ShadingLight.PLAYER || falloffFromTarget;
         double falloffRefX = falloffFromBrush ? targetX + 0.5 : playerX;
         double falloffRefY = falloffFromBrush ? targetY + 0.5 : playerY;
         double falloffRefZ = falloffFromBrush ? targetZ + 0.5 : playerZ;
         double playerDx = playerX - targetX;
         double playerDz = playerZ - targetZ;
         int losReach = (int)Math.ceil(Math.min(48.0, Math.sqrt(playerDx * playerDx + playerDz * playerDz)));
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, targetX, targetZ, maxHalf + losReach);
         int totalBlocks = (width + 1) * (height + 1) * (width + 1);
         BlockSelection before = new BlockSelection(totalBlocks, 0);
         before.setPosition(targetX, targetY, targetZ);
         BlockSelection after = new BlockSelection(totalBlocks, 0);
         after.copyPropertiesFrom(before);
         Predicate<Vector3i> inShape = this.createShapePredicate(brushShape, halfWidth, halfHeight, 0.0F, 0.0F, false);
         Vector3i rel = new Vector3i();

         for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
               for (int z = -halfWidth; z <= halfWidth; z++) {
                  int wx = targetX + x;
                  int wy = targetY + y;
                  int wz = targetZ + z;
                  if (wy >= 0 && wy < 320) {
                     rel.set(x, y, z);
                     if (inShape.test(rel)) {
                        WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(wx, wz));
                        int blockId = chunk.getBlock(wx, wy, wz);
                        if (blockId > 0 && isFullCubeBlock(blockId, chunk.getFiller(wx, wy, wz))) {
                           float edgeFactor = edgeBlendFactor(brushShape, x, y, z, halfWidth, halfHeight, edgeBlend);
                           if (!(edgeFactor < 1.0F) || !(this.random.nextFloat() > edgeFactor)) {
                              float exposure = computeBlockExposure(
                                 accessor, wx, wy, wz, playerX, playerY, playerZ, falloffRefX, falloffRefY, falloffRefZ, lightSource
                              );
                              int newBlockId;
                              if (customRamp != null) {
                                 float scaled = 0.5F + (exposure - 0.5F) * intensity;
                                 int idx = Math.round((1.0F - scaled) * (customRamp.length - 1));
                                 idx = Math.max(0, Math.min(customRamp.length - 1, idx));
                                 if (invert) {
                                    idx = customRamp.length - 1 - idx;
                                 }

                                 newBlockId = customRamp[idx];
                              } else if (autoMode) {
                                 float signed = (exposure - 0.5F) * 2.0F * intensity;
                                 signed = Math.max(-1.0F, Math.min(1.0F, signed));
                                 newBlockId = signed >= 0.0F
                                    ? blockColorIndex.findLighterVariant(blockId, signed)
                                    : blockColorIndex.findDarkerVariant(blockId, -signed);
                              } else if (lighten) {
                                 newBlockId = blockColorIndex.findLighterVariant(blockId, Math.min(1.0F, exposure * intensity));
                              } else {
                                 newBlockId = blockColorIndex.findDarkerVariant(blockId, Math.min(1.0F, exposure * intensity));
                              }

                              if (newBlockId != blockId) {
                                 int newRotation = chunk.getRotationIndex(wx, wy, wz);
                                 Holder<ChunkStore> holder = chunk.getBlockComponentHolder(wx, wy, wz);
                                 Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, wx, wy, wz, newBlockId, blockId, holder, false);
                                 int supportValue = chunk.getSupportValue(wx, wy, wz);
                                 int filler = chunk.getFiller(wx, wy, wz);
                                 before.addBlockAtWorldPos(wx, wy, wz, blockId, newRotation, filler, supportValue, holder);
                                 after.addBlockAtWorldPos(wx, wy, wz, newBlockId, newRotation, 0, 0, newHolder);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         this.handleBrushUndoGrouping(before, Collections.emptyList(), Collections.emptyList(), undoGroupSize, isHoldDown);
         after.placeNoReturn("Shading", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         this.sendUpdate();
         this.sendArea();
      }

      private static float computeBlockExposure(
         @Nonnull LocalCachedChunkAccessor accessor,
         int bx,
         int by,
         int bz,
         float playerX,
         float playerY,
         float playerZ,
         double falloffRefX,
         double falloffRefY,
         double falloffRefZ,
         @Nonnull BuilderToolsPlugin.ShadingLight lightSource
      ) {
         double[] normal = estimateSurfaceNormalSobel(accessor, bx, by, bz);
         if (normal == null) {
            return 0.0F;
         }

         if (lightSource == BuilderToolsPlugin.ShadingLight.ANGLE) {
            return (float)((normal[1] + 1.0) / 2.0);
         }

         double ox = bx + 0.5;
         double oy = by + 0.5;
         double oz = bz + 0.5;
         double lightX;
         double lightY;
         double lightZ;
         double lightDist;
         if (lightSource == BuilderToolsPlugin.ShadingLight.SUN) {
            lightX = ox;
            lightY = oy + 48.0;
            lightZ = oz;
            lightDist = 48.0;
         } else {
            lightX = playerX;
            lightY = playerY;
            lightZ = playerZ;
            lightDist = Math.sqrt((playerX - ox) * (playerX - ox) + (playerY - oy) * (playerY - oy) + (playerZ - oz) * (playerZ - oz));
            if (lightDist < 1.0E-6) {
               return 1.0F;
            }
         }

         double lx = (lightX - ox) / lightDist;
         double ly = (lightY - oy) / lightDist;
         double lz = (lightZ - oz) / lightDist;
         double diffuse = Math.max(0.0, normal[0] * lx + normal[1] * ly + normal[2] * lz);
         double frx = falloffRefX - ox;
         double fry = falloffRefY - oy;
         double frz = falloffRefZ - oz;
         double falloffDist = Math.sqrt(frx * frx + fry * fry + frz * frz);
         double visibility = !(falloffDist > 48.0) && countLineOfSightOccluders(accessor, bx, by, bz, lightX, lightY, lightZ, lightDist) <= 0 ? 1.0 : 0.0;
         double falloff = Math.max(0.0, 1.0 - falloffDist / 48.0);
         double direct = diffuse * visibility * falloff;
         double ambient = 0.2 * (0.5 + 0.5 * normal[1]);
         return (float)Math.min(1.0, ambient + 0.8 * direct);
      }

      @Nullable
      private static double[] estimateSurfaceNormalSobel(@Nonnull LocalCachedChunkAccessor accessor, int x, int y, int z) {
         double gx = 0.0;
         double gy = 0.0;
         double gz = 0.0;
         int emptyCount = 0;

         for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -2; k <= 2; k++) {
                  if (!isBlockSolidCover(accessor, x + i, y + j, z + k)) {
                     if (Math.abs(i) <= 1 && Math.abs(j) <= 1 && Math.abs(k) <= 1) {
                        emptyCount++;
                     }
                  } else {
                     int si = i + 2;
                     int sj = j + 2;
                     int sk = k + 2;
                     gx += SOBEL_DERIV[si] * SOBEL_SMOOTH[sj] * SOBEL_SMOOTH[sk];
                     gy += SOBEL_SMOOTH[si] * SOBEL_DERIV[sj] * SOBEL_SMOOTH[sk];
                     gz += SOBEL_SMOOTH[si] * SOBEL_SMOOTH[sj] * SOBEL_DERIV[sk];
                  }
               }
            }
         }

         if (emptyCount == 0) {
            return null;
         }

         double nx = -gx;
         double ny = -gy;
         double nz = -gz;
         double len = Math.sqrt(nx * nx + ny * ny + nz * nz);
         return len < 1.0E-6 ? new double[]{0.0, 1.0, 0.0} : new double[]{nx / len, ny / len, nz / len};
      }

      private static int countLineOfSightOccluders(
         @Nonnull LocalCachedChunkAccessor accessor, int bx, int by, int bz, double px, double py, double pz, double dist
      ) {
         if (dist < 1.0E-6) {
            return 0;
         }

         double ox = bx + 0.5;
         double oy = by + 0.5;
         double oz = bz + 0.5;
         double dirX = (px - ox) / dist;
         double dirY = (py - oy) / dist;
         double dirZ = (pz - oz) / dist;
         int cx = bx;
         int cy = by;
         int cz = bz;
         int stepX = dirX > 0.0 ? 1 : (dirX < 0.0 ? -1 : 0);
         int stepY = dirY > 0.0 ? 1 : (dirY < 0.0 ? -1 : 0);
         int stepZ = dirZ > 0.0 ? 1 : (dirZ < 0.0 ? -1 : 0);
         double tMaxX = stepX != 0 ? voxelBoundaryT(ox, dirX, stepX) : Double.MAX_VALUE;
         double tMaxY = stepY != 0 ? voxelBoundaryT(oy, dirY, stepY) : Double.MAX_VALUE;
         double tMaxZ = stepZ != 0 ? voxelBoundaryT(oz, dirZ, stepZ) : Double.MAX_VALUE;
         double tDeltaX = stepX != 0 ? 1.0 / Math.abs(dirX) : Double.MAX_VALUE;
         double tDeltaY = stepY != 0 ? 1.0 / Math.abs(dirY) : Double.MAX_VALUE;
         double tDeltaZ = stepZ != 0 ? 1.0 / Math.abs(dirZ) : Double.MAX_VALUE;
         double reach = Math.min(dist, 48.0);
         int occluders = 0;
         double t = 0.0;

         while (t < reach) {
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
               cx += stepX;
               t = tMaxX;
               tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
               cy += stepY;
               t = tMaxY;
               tMaxY += tDeltaY;
            } else {
               cz += stepZ;
               t = tMaxZ;
               tMaxZ += tDeltaZ;
            }

            if (t >= reach) {
               break;
            }

            if (cy >= 0 && cy < 320 && isBlockSolidCover(accessor, cx, cy, cz)) {
               occluders++;
            }
         }

         return occluders;
      }

      private static double voxelBoundaryT(double origin, double dir, int step) {
         double next = step > 0 ? Math.floor(origin) + 1.0 : Math.floor(origin);
         return (next - origin) / dir;
      }

      public void update(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
         if (this.selection == null) {
            this.selection = new BlockSelection();
         }

         this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
         this.selection.setSelectionArea(new Vector3i(xMin, yMin, zMin), new Vector3i(xMax, yMax, zMax));
         this.syncRawPositions();
      }

      public void tint(@Nonnull Ref<EntityStore> ref, int color, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            World world = componentAccessor.getExternalData().getWorld();
            int count = 0;
            int minX = this.selection.getSelectionMin().x();
            int minZ = this.selection.getSelectionMin().z();
            int maxX = this.selection.getSelectionMax().x();
            int maxZ = this.selection.getSelectionMax().z();
            BlockSelection place = new BlockSelection();
            place.setPosition(minX, 0, minZ);

            for (int x = minX; x < maxX; x++) {
               for (int z = minZ; z < maxZ; z++) {
                  place.addTintAtWorldPos(x, z, color);
                  count++;
               }
            }

            BlockSelection before = place.place(null, world);
            this.pushHistory(BuilderToolsPlugin.Action.EDIT, new BlockSelectionSnapshot(before));
            this.sendFeedback(Message.translation("server.builderTools.setColumnsTint").param("count", count), componentAccessor);
         }
      }

      public void environment(@Nonnull Ref<EntityStore> ref, int environmentId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            World world = componentAccessor.getExternalData().getWorld();
            LongSet dirtyChunks = new LongOpenHashSet();
            int count = 0;

            for (int x = this.selection.getSelectionMin().x(); x < this.selection.getSelectionMax().x(); x++) {
               for (int z = this.selection.getSelectionMin().z(); z < this.selection.getSelectionMax().z(); z++) {
                  WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                  dirtyChunks.add(chunk.getIndex());

                  for (int y = this.selection.getSelectionMin().y(); y < this.selection.getSelectionMax().y(); y++) {
                     chunk.getBlockChunk().setEnvironment(x, y, z, environmentId);
                     count++;
                  }
               }
            }

            dirtyChunks.forEach(value -> world.getNotificationHandler().updateChunk(value));
            this.sendFeedback(Message.translation("server.builderTools.setEnvironment").param("count", count), componentAccessor);
         }
      }

      public int copyOrCut(
         @Nonnull Ref<EntityStore> ref,
         int xMin,
         int yMin,
         int zMin,
         int xMax,
         int yMax,
         int zMax,
         int settings,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) throws PrefabCopyException {
         return this.copyOrCut(ref, xMin, yMin, zMin, xMax, yMax, zMax, settings, null, null, componentAccessor);
      }

      public int copyOrCut(
         @Nonnull Ref<EntityStore> ref,
         int xMin,
         int yMin,
         int zMin,
         int xMax,
         int yMax,
         int zMax,
         int settings,
         @Nullable Vector3i playerAnchor,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) throws PrefabCopyException {
         return this.copyOrCut(ref, xMin, yMin, zMin, xMax, yMax, zMax, settings, playerAnchor, null, componentAccessor);
      }

      public int copyOrCut(
         @Nonnull Ref<EntityStore> ref,
         int xMin,
         int yMin,
         int zMin,
         int xMax,
         int yMax,
         int zMax,
         int settings,
         @Nullable Vector3i playerAnchor,
         @Nullable Set<Ref<EntityStore>> skipEntityRemoveSnapshotFor,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) throws PrefabCopyException {
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
         boolean entities = (settings & 16) != 0;
         boolean keepAnchors = (settings & 64) != 0;
         int width = xMax - xMin;
         int height = yMax - yMin;
         int depth = zMax - zMin;
         long selectionVolume = (long)(width + 1) * (depth + 1) * (Math.abs(height) + 1);
         if (selectionVolume > 6600000L && BuilderToolsPlugin.shouldShowNotification(ref, componentAccessor)) {
            NotificationUtil.sendNotification(
               this.playerRef.getPacketHandler(),
               Message.translation("server.builderTools.copycut.tooLarge"),
               Message.translation("server.builderTools.copycut.tooLarge.detail").param("overCount", selectionVolume - 4000000L),
               NotificationStyle.Warning
            );
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_ERROR"), SoundCategory.UI, componentAccessor);
            return 0;
         }

         int halfWidth = width / 2;
         int halfDepth = depth / 2;
         if (cut) {
            before = new BlockSelection();
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            after = new BlockSelection(before);
            snapshots = new ObjectArrayList<>();
            this.pushHistory(BuilderToolsPlugin.Action.CUT_COPY, ClipboardContentsSnapshot.copyOf(this.selection));
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.COPY, ClipboardContentsSnapshot.copyOf(this.selection));
         }

         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
         int editorBlock = assetMap.getIndex("Editor_Block");
         if (editorBlock == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! Editor_Block");
         }

         int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
         if (editorBlockPrefabAir == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! Editor_Empty");
         }

         int editorBlockPrefabAnchor = assetMap.getIndex("Editor_Anchor");
         if (editorBlockPrefabAnchor == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unknown key! Editor_Anchor");
         }

         Set<Vector3i> anchors = new HashSet<>();
         Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
         Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
         this.selection = new BlockSelection();
         this.preRotationSnapshot = null;
         this.selection.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
         this.selection.setSelectionArea(min, max);
         this.syncRawPositions();
         int count = 0;
         int counter = 0;
         int top = Math.max(yMin, yMax);
         int bottom = Math.min(yMin, yMax);
         int totalBlocks = (width + 1) * (depth + 1) * (top - bottom + 1);

         for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
               WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
               Store<ChunkStore> store = chunk.getReference().getStore();
               ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
               int lastSection = -1;
               BlockPhysics blockPhysics = null;

               for (int y = top; y >= bottom; y--) {
                  int block = chunk.getBlock(x, y, z);
                  int fluid = chunk.getFluidId(x, y, z);
                  if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                     lastSection = ChunkUtil.chunkCoordinate(y);
                     Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                     if (section != null) {
                        blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
                     } else {
                        blockPhysics = null;
                     }
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
                           count++;
                        } else if (id == editorBlockPrefabAir) {
                           this.selection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                           count++;
                        }
                     }
                  } else if (blocks && (block != 0 || fluid != 0 || empty) && block != editorBlock) {
                     this.selection.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                     count++;
                  }

                  counter++;
                  this.sendFeedback(cut ? "Gather 1/2" : "Gather 1/1", totalBlocks, counter, componentAccessor);
               }
            }
         }

         if (count > 4000000) {
            this.selection = new BlockSelection();
            this.preRotationSnapshot = null;
            NotificationUtil.sendNotification(
               this.playerRef.getPacketHandler(),
               Message.translation("server.builderTools.copycut.tooLarge"),
               Message.translation("server.builderTools.copycut.tooLarge.detail").param("overCount", count - 4000000),
               NotificationStyle.Warning
            );
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_ERROR"), SoundCategory.UI, componentAccessor);
            return 0;
         }

         if (anchors.size() > 1 && playerAnchor == null) {
            StringBuilder sb = new StringBuilder("Anchors: ");
            boolean first = true;

            for (Vector3i anchor : anchors) {
               if (!first) {
                  sb.append(", ");
               }

               first = false;
               sb.append('[').append(anchor.x()).append(", ").append(anchor.y()).append(", ").append(anchor.z()).append(']');
            }

            throw new PrefabCopyException("Prefab has multiple anchor blocks!\n" + sb);
         } else {
            if (playerAnchor != null) {
               this.selection.setAnchorAtWorldPos(playerAnchor.x(), playerAnchor.y(), playerAnchor.z());
            }

            if (entities) {
               List<SelectionSnapshot<?>> snapshotsList = snapshots;
               Store<EntityStore> store = world.getEntityStore().getStore();
               ReferenceArrayList<Ref<EntityStore>> entitiesToRemove = cut ? new ReferenceArrayList<>() : null;
               Set<Ref<EntityStore>> skipSet = skipEntityRemoveSnapshotFor;
               BuilderToolsPlugin.forEachCopyableInSelection(world, xMin, yMin, zMin, width, height, depth, e -> {
                  Holder<EntityStore> holder = store.copyEntity(e);
                  this.selection.addEntityFromWorld(holder);
                  if (cut) {
                     boolean shouldSkip = skipSet != null && skipSet.contains(e);
                     if (!shouldSkip) {
                        snapshotsList.add(new EntityRemoveSnapshot(e));
                        entitiesToRemove.add(e);
                     }
                  }
               });
               if (cut && entitiesToRemove != null) {
                  for (Ref<EntityStore> e : entitiesToRemove) {
                     store.removeEntity(e, RemoveReason.UNLOAD);
                  }
               }
            }

            int cbMinX = Math.min(xMin, xMax);
            int cbMinY = Math.min(yMin, yMax);
            int cbMinZ = Math.min(zMin, zMax);
            int cbMaxX = Math.max(xMin, xMax);
            int cbMaxY = Math.max(yMin, yMax);
            int cbMaxZ = Math.max(zMin, zMax);

            for (PrefabSaveContributor contributor : BuilderToolsPlugin.get().getClipboardContributors()) {
               contributor.contribute(this.selection, world, new Vector3i(cbMinX, cbMinY, cbMinZ), new Vector3i(cbMaxX, cbMaxY, cbMaxZ));
            }

            if (cut) {
               snapshots.add(new BlockSelectionSnapshot(before));
               this.pushHistory(BuilderToolsPlugin.Action.CUT_REMOVE, snapshots);
            }

            if (after != null) {
               after.placeNoReturn("Cut 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            }

            long end = System.nanoTime();
            long diff = end - start;
            int size = count;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute copy of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
            if (cut) {
               this.sendUpdate();
            } else {
               this.playerRef.getPacketHandler().write(Objects.requireNonNullElseGet(this.selection, BlockSelection::new).toPacketWithSelection());
            }

            int entityCount = entities ? this.selection.getEntityCount() : 0;
            String translationKey;
            if (cut) {
               translationKey = entityCount > 0 ? "server.builderTools.cutWithEntities" : "server.builderTools.cut";
            } else {
               translationKey = entityCount > 0 ? "server.builderTools.copiedWithEntities" : "server.builderTools.copied";
            }

            this.sendFeedback(
               ref,
               Message.translation(translationKey).param("blockCount", size).param("entityCount", entityCount),
               cut ? "SFX_CREATE_CUT" : "SFX_CREATE_COPY",
               componentAccessor
            );
            return count;
         }
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
         this.pushHistory(BuilderToolsPlugin.Action.CLEAR, new BlockSelectionSnapshot(before));
         BlockSelection after = new BlockSelection(before);
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
         int top = Math.max(yMin, yMax);
         int bottom = Math.min(yMin, yMax);
         int height = top - bottom;
         int totalBlocks = (width + 1) * (depth + 1) * (height + 1);
         int counter = 0;

         for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
               WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
               Store<ChunkStore> store = chunk.getReference().getStore();
               ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
               int lastSection = -1;
               BlockPhysics blockPhysics = null;

               for (int y = top; y >= bottom; y--) {
                  int block = chunk.getBlock(x, y, z);
                  int fluid = chunk.getFluidId(x, y, z);
                  if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                     lastSection = ChunkUtil.chunkCoordinate(y);
                     Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                     if (section != null) {
                        blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
                     } else {
                        blockPhysics = null;
                     }
                  }

                  if (block != 0 || fluid != 0) {
                     before.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                     after.addEmptyAtWorldPos(x, y, z);
                  }

                  this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
               }
            }
         }

         after.placeNoReturn("Clear 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - start;
         int size = after.getBlockCount();
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute clear of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
         this.sendFeedback("Clear", size, componentAccessor);
         return size;
      }

      private static Vector3d rotateByEulerMatrix(@Nonnull Vector3dc v, @Nonnull RotationTuple t) {
         Vector3d r = new Vector3d(v);
         t.roll().rotateZ(r, r);
         t.pitch().rotateX(r, r);
         t.yaw().rotateY(r, r);
         return r;
      }

      public static RotationTuple transformRotation(RotationTuple prevRot, Quaterniond rotation) {
         Vector3d forwardVec = new Vector3d(1.0, 0.0, 0.0);
         Vector3d upVec = new Vector3d(0.0, 1.0, 0.0);
         forwardVec = rotateByEulerMatrix(forwardVec, prevRot);
         upVec = rotateByEulerMatrix(upVec, prevRot);
         Vector3d fwd = rotation.transform(new Vector3d(forwardVec.x, forwardVec.y, forwardVec.z));
         Vector3d up = rotation.transform(new Vector3d(upVec.x, upVec.y, upVec.z));
         Vector3d newForward = new Vector3d(fwd.x, fwd.y, fwd.z);
         Vector3d newUp = new Vector3d(up.x, up.y, up.z);
         double bestScore = Float.MIN_VALUE;
         RotationTuple bestRot = prevRot;

         for (RotationTuple rot : RotationTuple.VALUES) {
            Vector3d rotForward = rotateByEulerMatrix(new Vector3d(1.0, 0.0, 0.0), rot);
            Vector3d rotUp = rotateByEulerMatrix(new Vector3d(0.0, 1.0, 0.0), rot);
            double score = rotForward.dot(newForward) + rotUp.dot(newUp);
            if (score > bestScore) {
               bestScore = score;
               bestRot = rot;
            }
         }

         return bestRot;
      }

      public void transformThenPasteClipboard(
         @Nonnull BlockChange[] blockChanges,
         @Nullable PrototypePlayerBuilderToolSettings.FluidChange[] fluidChanges,
         @Nullable PrototypePlayerBuilderToolSettings.EntityChange[] entityChanges,
         @Nullable Holder<ChunkStore>[] blockHolders,
         @Nonnull Quaterniond rotation,
         @Nonnull Vector3i translationOffset,
         @Nonnull Rotation3f rotationOrigin,
         @Nonnull Vector3i initialPastePoint,
         boolean keepEmptyBlocks,
         @Nonnull PrototypePlayerBuilderToolSettings prototypeSettings,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         World world = componentAccessor.getExternalData().getWorld();
         long start = System.nanoTime();
         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
         int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
         int yOffsetOutOfGround = 0;

         for (BlockChange blockChange : blockChanges) {
            if (blockChange.y < 0 && Math.abs(blockChange.y) > yOffsetOutOfGround) {
               yOffsetOutOfGround = Math.abs(blockChange.y);
            }
         }

         int centerX = translationOffset.x + (int)rotationOrigin.x;
         int centerY = translationOffset.y + (int)rotationOrigin.y;
         int centerZ = translationOffset.z + (int)rotationOrigin.z;
         BlockSelection before = new BlockSelection();
         before.setPosition(centerX, centerY, centerZ);
         BlockSelection after = new BlockSelection(before);
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, centerX, centerZ, 50);
         int minX = Integer.MAX_VALUE;
         int minY = Integer.MAX_VALUE;
         int minZ = Integer.MAX_VALUE;
         int maxX = Integer.MIN_VALUE;
         int maxY = Integer.MIN_VALUE;
         int maxZ = Integer.MIN_VALUE;

         record RotatedBlock(Vector3i location, int blockId, int newRotation, Holder<ChunkStore> holder, BlockType blockType, BlockBoundingBoxes hitbox) {
         }

         ObjectArrayList<RotatedBlock> rotatedBlocks = new ObjectArrayList<>(blockChanges.length);
         LongOpenHashSet basePositions = new LongOpenHashSet(blockChanges.length);
         Vector3d mutableVec = new Vector3d();

         for (int i = 0; i < blockChanges.length; i++) {
            BlockChange blockChange = blockChanges[i];
            mutableVec.set(
               blockChange.x - rotationOrigin.x + initialPastePoint.x + 0.5,
               blockChange.y - rotationOrigin.y + initialPastePoint.y + 0.5 + yOffsetOutOfGround,
               blockChange.z - rotationOrigin.z + initialPastePoint.z + 0.5
            );
            rotation.transform(mutableVec);
            mutableVec.add(translationOffset.x, translationOffset.y, translationOffset.z);
            Vector3i rotatedLocation = new Vector3i(
               (int)Math.floor(mutableVec.x + 0.1 + rotationOrigin.x - 0.5),
               (int)Math.floor(mutableVec.y + 0.1 + rotationOrigin.y - 0.5),
               (int)Math.floor(mutableVec.z + 0.1 + rotationOrigin.z - 0.5)
            );
            minX = Math.min(minX, rotatedLocation.x);
            minY = Math.min(minY, rotatedLocation.y);
            minZ = Math.min(minZ, rotatedLocation.z);
            maxX = Math.max(maxX, rotatedLocation.x);
            maxY = Math.max(maxY, rotatedLocation.y);
            maxZ = Math.max(maxZ, rotatedLocation.z);
            int newRotation = transformRotation(RotationTuple.get(blockChange.rotation), rotation).index();
            int blockIdToPlace = blockChange.block;
            if (blockChange.block == editorBlockPrefabAir && !keepEmptyBlocks) {
               blockIdToPlace = 0;
            }

            BlockType blockType = assetMap.getAsset(blockIdToPlace);
            if (blockType != null) {
               BlockBoundingBoxes hitbox = BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
               if (hitbox != null) {
                  Holder<ChunkStore> holder;
                  if (blockHolders != null && i < blockHolders.length && blockHolders[i] != null) {
                     holder = blockHolders[i].clone();
                  } else if (blockType.getBlockEntity() != null) {
                     holder = blockType.getBlockEntity().clone();
                  } else {
                     holder = null;
                  }

                  rotatedBlocks.add(new RotatedBlock(rotatedLocation, blockIdToPlace, newRotation, holder, blockType, hitbox));
                  basePositions.add(BlockUtil.pack(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z));
               }
            }
         }

         for (RotatedBlock rb : rotatedBlocks) {
            Vector3i rotatedLocation = rb.location();
            int blockIdToPlace = rb.blockId();
            int newRotation = rb.newRotation();
            Holder<ChunkStore> holder = rb.holder();
            WorldChunk currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(rotatedLocation.x, rotatedLocation.z));
            int blockIdInRotatedLocation = currentChunk.getBlock(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
            int filler = currentChunk.getFiller(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
            int blockRotation = currentChunk.getRotationIndex(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
            before.addBlockAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, blockIdInRotatedLocation, blockRotation, filler, 0, holder);
            int originalFluidId = currentChunk.getFluidId(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
            byte originalFluidLevel = currentChunk.getFluidLevel(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z);
            before.addFluidAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, originalFluidId, originalFluidLevel);
            if (rb.hitbox().protrudesUnitBox()) {
               FillerBlockUtil.forEachFillerBlock(
                  rb.hitbox().get(newRotation),
                  (x, y, z) -> {
                     if (x != 0 || y != 0 || z != 0) {
                        int fx = rotatedLocation.x + x;
                        int fy = rotatedLocation.y + y;
                        int fz = rotatedLocation.z + z;
                        if (!before.hasBlockAtWorldPos(fx, fy, fz)) {
                           WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fx, fz));
                           before.addBlockAtWorldPos(
                              fx,
                              fy,
                              fz,
                              fillerChunk.getBlock(fx, fy, fz),
                              fillerChunk.getRotationIndex(fx, fy, fz),
                              fillerChunk.getFiller(fx, fy, fz),
                              0,
                              fillerChunk.getBlockComponentHolder(fx, fy, fz)
                           );
                        }
                     }
                  }
               );
               FillerBlockUtil.forEachFillerBlock(rb.hitbox().get(newRotation), (x, y, z) -> {
                  int fx = rotatedLocation.x + x;
                  int fy = rotatedLocation.y + y;
                  int fz = rotatedLocation.z + z;
                  if (x == 0 && y == 0 && z == 0 || !basePositions.contains(BlockUtil.pack(fx, fy, fz))) {
                     boolean isBase = x == 0 && y == 0 && z == 0;
                     after.addBlockAtWorldPos(fx, fy, fz, blockIdToPlace, newRotation, FillerBlockUtil.pack(x, y, z), 0, isBase ? holder : null);
                  }
               });
            } else {
               after.addBlockAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, blockIdToPlace, newRotation, 0, 0, holder);
            }
         }

         int finalYOffsetOutOfGround = yOffsetOutOfGround;
         if (fluidChanges != null) {
            for (PrototypePlayerBuilderToolSettings.FluidChange fluidChange : fluidChanges) {
               mutableVec.set(
                  fluidChange.x() - rotationOrigin.x + initialPastePoint.x + 0.5,
                  fluidChange.y() - rotationOrigin.y + initialPastePoint.y + 0.5 + finalYOffsetOutOfGround,
                  fluidChange.z() - rotationOrigin.z + initialPastePoint.z + 0.5
               );
               rotation.transform(mutableVec);
               mutableVec.add(translationOffset.x, translationOffset.y, translationOffset.z);
               Vector3i rotatedLocation = new Vector3i(
                  (int)Math.floor(mutableVec.x + 0.1 + rotationOrigin.x - 0.5),
                  (int)Math.floor(mutableVec.y + 0.1 + rotationOrigin.y - 0.5),
                  (int)Math.floor(mutableVec.z + 0.1 + rotationOrigin.z - 0.5)
               );
               after.addFluidAtWorldPos(rotatedLocation.x, rotatedLocation.y, rotatedLocation.z, fluidChange.fluidId(), fluidChange.fluidLevel());
            }
         }

         List<Ref<EntityStore>> previousEntityRefs = prototypeSettings.getLastTransformEntityRefs();
         List<EntityRemoveSnapshot> previousEntitySnapshots = new ArrayList<>();
         if (previousEntityRefs != null) {
            Store<EntityStore> entityStore = world.getEntityStore().getStore();

            for (Ref<EntityStore> ref : previousEntityRefs) {
               if (ref != null && ref.isValid()) {
                  previousEntitySnapshots.add(new EntityRemoveSnapshot(ref));
                  entityStore.removeEntity(ref, RemoveReason.UNLOAD);
               }
            }
         }

         List<Ref<EntityStore>> addedEntityRefs = new ReferenceArrayList<>();
         if (entityChanges != null && entityChanges.length > 0) {
            Vector3d mutableEntityPos = new Vector3d();

            for (PrototypePlayerBuilderToolSettings.EntityChange entityChange : entityChanges) {
               boolean isBlockEntity = entityChange.entityHolder().getComponent(BlockEntity.getComponentType()) != null;
               double blockCenterOffset = isBlockEntity ? 0.5 : 0.0;
               mutableEntityPos.set(
                  entityChange.x() + initialPastePoint.x - rotationOrigin.x,
                  entityChange.y() + blockCenterOffset + initialPastePoint.y - rotationOrigin.y + finalYOffsetOutOfGround,
                  entityChange.z() + initialPastePoint.z - rotationOrigin.z
               );
               rotation.transform(mutableEntityPos);
               mutableEntityPos.add(translationOffset.x, translationOffset.y, translationOffset.z);
               double newX = mutableEntityPos.x + rotationOrigin.x;
               double newY = mutableEntityPos.y + rotationOrigin.y - blockCenterOffset;
               double newZ = mutableEntityPos.z + rotationOrigin.z;
               Holder<EntityStore> clonedHolder = entityChange.entityHolder().clone();
               TransformComponent transformComponent = clonedHolder.getComponent(TransformComponent.getComponentType());
               if (transformComponent != null && transformComponent.getPosition() != null) {
                  transformComponent.getPosition().set(newX, newY, newZ);
                  Rotation3f entityRotation = transformComponent.getRotation();
                  if (entityRotation != null) {
                     entityRotation.premul(rotation);
                  }
               }

               HeadRotation headRotation = clonedHolder.getComponent(HeadRotation.getComponentType());
               if (headRotation != null && headRotation.getRotation() != null) {
                  headRotation.getRotation().premul(rotation);
               }

               clonedHolder.putComponent(UUIDComponent.getComponentType(), new UUIDComponent(UUID.randomUUID()));
               if (clonedHolder.getComponent(EntityTrackerSystems.Visible.getComponentType()) != null) {
                  clonedHolder.removeComponent(EntityTrackerSystems.Visible.getComponentType());
               }

               if (clonedHolder.getComponent(NetworkId.getComponentType()) != null) {
                  clonedHolder.removeComponent(NetworkId.getComponentType());
               }

               Ref<EntityStore> entityRef = componentAccessor.addEntity(clonedHolder, AddReason.LOAD);
               if (entityRef != null) {
                  addedEntityRefs.add(entityRef);
               }
            }
         }

         if (minX != Integer.MAX_VALUE) {
            before.setSelectionArea(new Vector3i(minX, minY, minZ), new Vector3i(maxX, maxY, maxZ));
         }

         prototypeSettings.setLastTransformEntityRefs(new ArrayList<>(addedEntityRefs));
         List<SelectionSnapshot<?>> snapshots = new ObjectArrayList<>(addedEntityRefs.size() + previousEntitySnapshots.size() + 1);

         for (Ref<EntityStore> entityRef : addedEntityRefs) {
            snapshots.add(new EntityAddSnapshot(entityRef));
         }

         for (EntityRemoveSnapshot snapshot : previousEntitySnapshots) {
            snapshots.add(snapshot);
         }

         snapshots.add(new BlockSelectionSnapshot(before));
         this.pushHistory(BuilderToolsPlugin.Action.ROTATE, snapshots);
         after.placeNoReturn("Transform 1/1", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - start;
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
         this.sendUpdate();
         this.sendArea();
      }

      public void transformSelectionPoints(@Nonnull Quaterniond rotation, @Nonnull Vector3i translationOffset, @Nonnull Rotation3f rotationOrigin) {
         Vector3i newMin = this.transformBlockLocation(this.selection.getSelectionMin(), rotation, translationOffset, rotationOrigin);
         Vector3i newMax = this.transformBlockLocation(this.selection.getSelectionMax(), rotation, translationOffset, rotationOrigin);
         this.selection.setSelectionArea(Vector3iUtil.min(newMin, newMax), Vector3iUtil.max(newMin, newMax));
         this.syncRawPositions();
         this.sendUpdate();
         this.sendArea();
      }

      @Nonnull
      public Vector3i transformBlockLocation(
         @Nonnull Vector3i blockLocation, @Nonnull Quaterniond rotation, @Nonnull Vector3i translationOffset, @Nonnull Rotation3f rotationOrigin
      ) {
         Vector3d relative = new Vector3d(
            blockLocation.x - rotationOrigin.x + 0.5, blockLocation.y - rotationOrigin.y + 0.5, blockLocation.z - rotationOrigin.z + 0.5
         );
         rotation.transform(relative);
         relative.add(translationOffset.x, translationOffset.y, translationOffset.z);
         return new Vector3i(
            (int)Math.floor(relative.x + rotationOrigin.x - 0.5 + 0.1),
            (int)Math.floor(relative.y + rotationOrigin.y - 0.5 + 0.1),
            (int)Math.floor(relative.z + rotationOrigin.z - 0.5 + 0.1)
         );
      }

      public void layer(
         int x,
         int y,
         int z,
         @Nonnull List<Pair<Integer, String>> layers,
         int depth,
         Vector3ic direction,
         WorldChunk chunk,
         BlockSelection before,
         BlockSelection after
      ) {
         int xModifier = direction.x() == 1 ? -1 : (direction.x() == -1 ? 1 : 0);
         int yModifier = direction.y() == 1 ? -1 : (direction.y() == -1 ? 1 : 0);
         int zModifier = direction.z() == 1 ? -1 : (direction.z() == -1 ? 1 : 0);

         for (int i = 0; i < depth; i++) {
            if (chunk.getBlock(x + i * xModifier + xModifier, y + i * yModifier + yModifier, z + i * zModifier + zModifier) <= 0
               && this.attemptSetLayer(x, y, z, i, layers, chunk, before, after)) {
               return;
            }
         }
      }

      public void layer(@Nonnull List<Pair<Integer, String>> layers, Vector3ic direction, ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            int maxDepth = 0;

            for (Pair<Integer, String> layer : layers) {
               maxDepth += layer.left();
            }

            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(BuilderToolsPlugin.Action.LAYER, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int currentBlock = chunk.getBlock(x, y, z);
                     int currentFluid = chunk.getFluidId(x, y, z);
                     if (currentBlock > 0 && (this.globalMask == null || !this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlock, currentFluid))) {
                        this.layer(x, y, z, layers, maxDepth, direction, chunk, before, after);
                     }
                  }
               }
            }

            after.placeNoReturn("Finished layer", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute layer", (long)diff, (long)TimeUnit.NANOSECONDS.toMillis(diff));
            this.sendUpdate();
            this.sendArea();
         }
      }

      private boolean attemptSetLayer(
         int x, int y, int z, int depth, List<Pair<Integer, String>> layers, WorldChunk chunk, BlockSelection before, BlockSelection after
      ) {
         int currentDepth = 0;

         for (Pair<Integer, String> layer : layers) {
            currentDepth += layer.left();
            if (depth < currentDepth) {
               int currentBlock = chunk.getBlock(x, y, z);
               int currentBlockFiller = chunk.getFiller(x, y, z);
               Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
               int rotation = chunk.getRotationIndex(x, y, z);
               int supportValue = chunk.getSupportValue(x, y, z);
               BlockPattern pattern = BlockPattern.parse(layer.right());
               int materialId = pattern.nextBlock(this.random);
               Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, materialId, currentBlock, holder, true);
               before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, currentBlockFiller, supportValue, holder);
               after.addBlockAtWorldPos(x, y, z, materialId, rotation, 0, 0, newHolder);
               return true;
            }
         }

         return false;
      }

      public int paste(@Nonnull Ref<EntityStore> ref, int x, int y, int z, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         return this.paste(ref, x, y, z, false, false, componentAccessor);
      }

      public int paste(@Nonnull Ref<EntityStore> ref, int x, int y, int z, boolean technicalPaste, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         return this.paste(ref, x, y, z, technicalPaste, false, componentAccessor);
      }

      public int paste(
         @Nonnull Ref<EntityStore> ref,
         int x,
         int y,
         int z,
         boolean technicalPaste,
         boolean skipAirBlocks,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
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
            } else {
               selectionToPlace = this.convertEditorEmptyToAir(this.selection);
            }

            selectionToPlace.setPosition(x, y, z);
            int prefabId = PrefabUtil.getNextPrefabId();
            selectionToPlace.setPrefabId(prefabId);
            if (!BuilderToolsPlugin.onPasteStart(prefabId, componentAccessor)) {
               this.sendErrorFeedback(ref, Message.translation("server.builderTools.pasteCancelledByEvent"), componentAccessor);
               return 0;
            }

            int entityCount = selectionToPlace.getEntityCount();
            List<SelectionSnapshot<?>> snapshots = new ObjectArrayList<>(entityCount + 1);
            Consumer<Ref<EntityStore>> collector = BlockSelection.DEFAULT_ENTITY_CONSUMER;
            if (entityCount > 0) {
               collector = e -> snapshots.add(new EntityAddSnapshot(e));
            }

            BlockSelection before = selectionToPlace.place(this.playerRef, world, Vector3iUtil.ZERO, this.globalMask, collector, skipAirBlocks);
            before.setSelectionArea(pasteMin, pasteMax);
            snapshots.add(new BlockSelectionSnapshot(before));
            this.pushHistory(BuilderToolsPlugin.Action.PASTE, snapshots);
            BuilderToolsPlugin.invalidateWorldMapForBounds(pasteMin, pasteMax, world);
            BuilderToolsPlugin.get().onPasteEnd(prefabId, componentAccessor);
            selectionToPlace.setPrefabId(-1);
            selectionToPlace.setPosition(0, 0, 0);
            long end = System.nanoTime();
            long diff = end - start;
            int size = selectionToPlace.getBlockCount();
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute paste of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), size);
            this.sendFeedback(Message.translation("server.builderTools.pastedBlocks").param("count", size), componentAccessor);
            return size;
         } else {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
            return 0;
         }
      }

      private BlockSelection convertEmptyBlocksToEditorEmpty(@Nonnull BlockSelection original) {
         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
         int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
         if (editorBlockPrefabAir == Integer.MIN_VALUE) {
            return original;
         }

         BlockSelection converted = new BlockSelection(original.getBlockCount(), original.getEntityCount());
         converted.setPosition(original.getX(), original.getY(), original.getZ());
         converted.setAnchor(original.getAnchorX(), original.getAnchorY(), original.getAnchorZ());
         converted.setSelectionArea(original.getSelectionMin(), original.getSelectionMax());
         LongOpenHashSet fluidPositions = new LongOpenHashSet();
         original.forEachFluid((x, y, z, fluidId, fluidLevel) -> {
            if (fluidId != 0) {
               fluidPositions.add(BlockUtil.packUnchecked(x, y, z));
            }
         });
         original.forEachBlock((x, y, z, block) -> {
            int blockId = block.blockId();
            if (blockId == 0 && !fluidPositions.contains(BlockUtil.packUnchecked(x, y, z))) {
               blockId = editorBlockPrefabAir;
            }

            converted.addBlockAtLocalPos(x, y, z, blockId, block.rotation(), block.filler(), block.supportValue(), block.holder());
         });
         original.forEachFluid((x, y, z, fluidId, fluidLevel) -> converted.addFluidAtLocalPos(x, y, z, fluidId, fluidLevel));
         original.forEachEntity(holder -> converted.addEntityHolderRaw(holder.clone()));
         return converted;
      }

      private BlockSelection convertEditorEmptyToAir(@Nonnull BlockSelection original) {
         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
         int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
         if (editorBlockPrefabAir == Integer.MIN_VALUE) {
            return original;
         }

         BlockSelection converted = new BlockSelection(original.getBlockCount(), original.getEntityCount());
         converted.setPosition(original.getX(), original.getY(), original.getZ());
         converted.setAnchor(original.getAnchorX(), original.getAnchorY(), original.getAnchorZ());
         converted.setSelectionArea(original.getSelectionMin(), original.getSelectionMax());
         original.forEachBlock((x, y, z, block) -> {
            int blockId = block.blockId() == editorBlockPrefabAir ? 0 : block.blockId();
            converted.addBlockAtLocalPos(x, y, z, blockId, block.rotation(), block.filler(), block.supportValue(), block.holder());
         });
         original.forEachFluid((x, y, z, fluidId, fluidLevel) -> converted.addFluidAtLocalPos(x, y, z, fluidId, fluidLevel));
         original.forEachEntity(holder -> converted.addEntityHolderRaw(holder.clone()));
         return converted;
      }

      public void rotate(@Nonnull Ref<EntityStore> ref, @Nonnull Axis axis, int angle, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         this.rotate(ref, axis, angle, RotateBlockMode.ALL, componentAccessor);
      }

      public void rotate(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull Axis axis,
         int angle,
         @Nonnull RotateBlockMode rotateBlockMode,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection != null) {
            long start = System.nanoTime();
            if (this.preRotationSnapshot == null) {
               this.preRotationSnapshot = this.selection.cloneSelection();
            }

            this.pushHistory(BuilderToolsPlugin.Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
            this.selection = this.selection.rotate(axis, angle, rotateBlockMode);
            switch (axis) {
               case X:
                  this.cumulativeRotX = ((this.cumulativeRotX + angle) % 360 + 360) % 360;
                  break;
               case Y:
                  this.cumulativeRotY = ((this.cumulativeRotY + angle) % 360 + 360) % 360;
                  break;
               case Z:
                  this.cumulativeRotZ = ((this.cumulativeRotZ + angle) % 360 + 360) % 360;
            }

            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute rotate of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
            this.sendUpdate();
            this.sendFeedback(
               Message.translation("server.builderTools.clipboardRotatedBy").param("angle", angle).param("axis", axis.toString()), componentAccessor
            );
         } else {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
         }
      }

      public void resetClipboardRotation(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.preRotationSnapshot == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noRotationToReset"), componentAccessor);
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
            this.selection = this.preRotationSnapshot;
            this.preRotationSnapshot = null;
            this.cumulativeRotX = this.cumulativeRotY = this.cumulativeRotZ = 0;
            this.sendUpdate();
            this.sendFeedback(Message.translation("server.builderTools.clipboardRotationReset"), componentAccessor);
         }
      }

      public void rotateArbitrary(@Nonnull Ref<EntityStore> ref, float yaw, float pitch, float roll, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection != null) {
            long start = System.nanoTime();
            if (this.preRotationSnapshot == null) {
               this.preRotationSnapshot = this.selection.cloneSelection();
            }

            this.pushHistory(BuilderToolsPlugin.Action.ROTATE, ClipboardContentsSnapshot.copyOf(this.selection));
            int entitiesBefore = this.selection.getEntityCount();
            this.selection = this.selection.rotateArbitrary(yaw, pitch, roll);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log(
                  "Took: %dns (%dms) to execute arbitrary rotate of %d blocks, %d -> %d entities",
                  diff,
                  TimeUnit.NANOSECONDS.toMillis(diff),
                  this.selection.getBlockCount(),
                  entitiesBefore,
                  this.selection.getEntityCount()
               );
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
            if (this.preRotationSnapshot == null) {
               this.preRotationSnapshot = this.selection.cloneSelection();
            }

            this.pushHistory(BuilderToolsPlugin.Action.FLIP, ClipboardContentsSnapshot.copyOf(this.selection));
            this.selection = this.selection.flip(axis);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute flip of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
            this.sendUpdate();
            this.sendFeedback(Message.translation("server.builderTools.clipboardFlipped").param("axis", axis.toString()), componentAccessor);
         } else {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
         }
      }

      public void applyRandomizeTransforms(
         @Nonnull Ref<EntityStore> ref,
         int deltaX,
         int deltaY,
         int deltaZ,
         boolean flipX,
         boolean flipY,
         boolean flipZ,
         @Nonnull RotateBlockMode rotateBlockMode,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionClipboardEmpty"), componentAccessor);
         } else {
            if (this.preRotationSnapshot == null) {
               this.preRotationSnapshot = this.selection.cloneSelection();
            }

            if (deltaX != 0) {
               this.selection = this.selection.rotate(Axis.X, deltaX, rotateBlockMode);
            }

            if (deltaY != 0) {
               this.selection = this.selection.rotate(Axis.Y, deltaY, rotateBlockMode);
            }

            if (deltaZ != 0) {
               this.selection = this.selection.rotate(Axis.Z, deltaZ, rotateBlockMode);
            }

            if (flipX) {
               this.selection = this.selection.flip(Axis.X);
            }

            if (flipY) {
               this.selection = this.selection.flip(Axis.Y);
            }

            if (flipZ) {
               this.selection = this.selection.flip(Axis.Z);
            }

            if (deltaX != 0) {
               this.cumulativeRotX = ((this.cumulativeRotX + deltaX) % 360 + 360) % 360;
            }

            if (deltaY != 0) {
               this.cumulativeRotY = ((this.cumulativeRotY + deltaY) % 360 + 360) % 360;
            }

            if (deltaZ != 0) {
               this.cumulativeRotZ = ((this.cumulativeRotZ + deltaZ) % 360 + 360) % 360;
            }

            this.sendUpdate();
            this.sendFeedback(Message.translation("server.builderTools.clipboardRandomized"), componentAccessor);
         }
      }

      public void hollow(
         @Nonnull Ref<EntityStore> ref,
         final int blockId,
         int thickness,
         boolean setTop,
         boolean setBottom,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            final Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            final BlockSelection before = new BlockSelection();
            before.setPosition(min.x, min.y, min.z);
            before.setSelectionArea(min, max);
            final BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            final LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(
               world, max.x + 1 - min.x, max.z + 1 - min.z, Math.max(max.x + 1 - min.x, Math.max(max.y + 1 - min.y, max.z + 1 - min.z))
            );
            BlockCubeUtil.forEachBlock(
               min,
               max,
               thickness,
               !setTop,
               !setBottom,
               true,
               null,
               new TriIntObjPredicate<Void>() {
                  private int previousX = Integer.MIN_VALUE;
                  private int previousZ = Integer.MIN_VALUE;
                  @Nullable
                  private WorldChunk currentChunk;

                  public boolean test(int x, int y, int z, Void unused) {
                     if (this.previousX != x || this.previousZ != z) {
                        this.previousX = x;
                        this.previousZ = z;
                        this.currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                     }

                     int currentBlockId = this.currentChunk.getBlock(x, y, z);
                     int currentFluidId = this.currentChunk.getFluidId(x, y, z);
                     if (BuilderState.this.globalMask != null
                        && BuilderState.this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlockId, currentFluidId)) {
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
               }
            );
            this.pushHistory(BuilderToolsPlugin.Action.HOLLOW, new BlockSelectionSnapshot(before));
            after.placeNoReturn("Hollow 1/1", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
         }
      }

      public void walls(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull final BlockPattern pattern,
         int thickness,
         boolean cappedTop,
         boolean cappedBottom,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (!pattern.isEmpty()) {
            if (this.selection == null) {
               this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
            } else if (!this.selection.hasSelectionBounds()) {
               this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
            } else {
               World world = componentAccessor.getExternalData().getWorld();
               long start = System.nanoTime();
               final Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               final Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               final BlockSelection before = new BlockSelection();
               before.setPosition(min.x, min.y, min.z);
               before.setSelectionArea(min, max);
               final BlockSelection after = new BlockSelection(before);
               final LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(
                  world, max.x + 1 - min.x, max.z + 1 - min.z, Math.max(max.x + 1 - min.x, Math.max(max.y + 1 - min.y, max.z + 1 - min.z))
               );
               BlockCubeUtil.forEachBlock(
                  min,
                  max,
                  thickness,
                  cappedTop,
                  cappedBottom,
                  false,
                  null,
                  new TriIntObjPredicate<Void>() {
                     private int previousX = Integer.MIN_VALUE;
                     private int previousZ = Integer.MIN_VALUE;
                     @Nullable
                     private WorldChunk currentChunk;

                     public boolean test(int x, int y, int z, Void unused) {
                        if (this.previousX != x || this.previousZ != z) {
                           this.previousX = x;
                           this.previousZ = z;
                           this.currentChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                        }

                        int currentBlockId = this.currentChunk.getBlock(x, y, z);
                        int currentFluidId = this.currentChunk.getFluidId(x, y, z);
                        if (BuilderState.this.globalMask != null
                           && BuilderState.this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlockId, currentFluidId)) {
                           return true;
                        }

                        Material material = Material.fromPattern(pattern, BuilderState.this.random);
                        if (material.isFluid()) {
                           byte currentFluidLevel = this.currentChunk.getFluidLevel(x, y, z);
                           if (currentBlockId != 0) {
                              Holder<ChunkStore> holder = this.currentChunk.getBlockComponentHolder(x, y, z);
                              int rotation = this.currentChunk.getRotationIndex(x, y, z);
                              int supportValue = this.currentChunk.getSupportValue(x, y, z);
                              int filler = this.currentChunk.getFiller(x, y, z);
                              before.addBlockAtWorldPos(x, y, z, currentBlockId, rotation, filler, supportValue, holder);
                              after.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                              BuilderState.this.clearFillerBlocksIfNeeded(x, y, z, currentBlockId, rotation, accessor, before, after);
                           }

                           before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                           after.addFluidAtWorldPos(x, y, z, material.getFluidId(), material.getFluidLevel());
                        } else {
                           int newBlockId = material.getBlockId();
                           int newRotation = material.getRotation();
                           Holder<ChunkStore> holder = this.currentChunk.getBlockComponentHolder(x, y, z);
                           Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(
                              this.currentChunk, x, y, z, newBlockId, currentBlockId, holder, false
                           );
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
                  }
               );
               this.pushHistory(BuilderToolsPlugin.Action.WALLS, new BlockSelectionSnapshot(before));
               after.placeNoReturn("Walls 1/1", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to execute walls of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      public void set(@Nonnull BlockPattern pattern, ComponentAccessor<EntityStore> componentAccessor) {
         if (!pattern.isEmpty()) {
            if (this.selection == null) {
               this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
            } else if (!this.selection.hasSelectionBounds()) {
               this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
            } else {
               long start = System.nanoTime();
               Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               int xMin = min.x();
               int xMax = max.x();
               int yMin = min.y();
               int yMax = max.y();
               int zMin = min.z();
               int zMax = max.z();
               int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
               int width = xMax - xMin;
               int depth = zMax - zMin;
               int halfWidth = width / 2;
               int halfDepth = depth / 2;
               BlockSelection before = new BlockSelection(totalBlocks, 0);
               before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
               before.setSelectionArea(min, max);
               this.pushHistory(BuilderToolsPlugin.Action.SET, new BlockSelectionSnapshot(before));
               BlockSelection after = new BlockSelection(totalBlocks, 0);
               after.copyPropertiesFrom(before);
               World world = componentAccessor.getExternalData().getWorld();
               LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
               int counter = 0;

               for (int x = xMin; x <= xMax; x++) {
                  for (int z = zMin; z <= zMax; z++) {
                     WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                     for (int y = yMax; y >= yMin; y--) {
                        int currentBlock = chunk.getBlock(x, y, z);
                        int currentFluid = chunk.getFluidId(x, y, z);
                        if (this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlock, currentFluid)) {
                           counter++;
                        } else {
                           Material material = Material.fromPattern(pattern, this.random);
                           if (material.isFluid()) {
                              byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                              if (currentBlock != 0) {
                                 Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                                 int rotation = chunk.getRotationIndex(x, y, z);
                                 int supportValue = chunk.getSupportValue(x, y, z);
                                 int filler = chunk.getFiller(x, y, z);
                                 before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, filler, supportValue, holder);
                                 after.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                 this.clearFillerBlocksIfNeeded(x, y, z, currentBlock, rotation, accessor, before, after);
                              }

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
               }

               after.placeNoReturn("Set 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to execute set of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      public void submerge(@Nonnull BlockPattern pattern, ComponentAccessor<EntityStore> componentAccessor) {
         if (!pattern.isEmpty()) {
            if (this.selection == null) {
               this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
            } else if (!this.selection.hasSelectionBounds()) {
               this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
            } else {
               long start = System.nanoTime();
               Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               int xMin = min.x();
               int xMax = max.x();
               int yMin = min.y();
               int yMax = max.y();
               int zMin = min.z();
               int zMax = max.z();
               int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
               int width = xMax - xMin;
               int depth = zMax - zMin;
               int halfWidth = width / 2;
               int halfDepth = depth / 2;
               BlockSelection before = new BlockSelection(totalBlocks, 0);
               before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
               before.setSelectionArea(min, max);
               this.pushHistory(BuilderToolsPlugin.Action.SET, new BlockSelectionSnapshot(before));
               BlockSelection after = new BlockSelection(totalBlocks, 0);
               after.copyPropertiesFrom(before);
               World world = componentAccessor.getExternalData().getWorld();
               LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
               int counter = 0;

               for (int x = xMin; x <= xMax; x++) {
                  for (int z = zMin; z <= zMax; z++) {
                     WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                     for (int y = yMax; y >= yMin; y--) {
                        int currentBlock = chunk.getBlock(x, y, z);
                        int currentFluid = chunk.getFluidId(x, y, z);
                        if (this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlock, currentFluid)) {
                           counter++;
                        } else {
                           Material material = Material.fromPattern(pattern, this.random);
                           if (material.isFluid()) {
                              byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                              before.addFluidAtWorldPos(x, y, z, currentFluid, currentFluidLevel);
                              after.addFluidAtWorldPos(x, y, z, material.getFluidId(), material.getFluidLevel());
                           }

                           this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                        }
                     }
                  }
               }

               after.placeNoReturn("Submerge 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to execute submerge of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      public void unsubmerge(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            BlockSelection before = new BlockSelection(totalBlocks, 0);
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(BuilderToolsPlugin.Action.SET, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int counter = 0;

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int currentBlock = chunk.getBlock(x, y, z);
                     int currentFluid = chunk.getFluidId(x, y, z);
                     if (this.globalMask != null && this.globalMask.isExcluded(accessor, x, y, z, min, max, currentBlock, currentFluid)) {
                        counter++;
                     } else {
                        if (currentFluid != 0) {
                           byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                           before.addFluidAtWorldPos(x, y, z, currentFluid, currentFluidLevel);
                           after.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                        }

                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                     }
                  }
               }
            }

            after.placeNoReturn("Unsubmerge 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute unsubmerge of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
            this.sendUpdate();
            this.sendArea();
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_SELECTION_FILL"), SoundCategory.SFX, componentAccessor);
         }
      }

      public void fill(@Nonnull BlockPattern pattern, ComponentAccessor<EntityStore> componentAccessor) {
         if (!pattern.isEmpty()) {
            if (this.selection == null) {
               this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
            } else if (!this.selection.hasSelectionBounds()) {
               this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
            } else {
               long start = System.nanoTime();
               Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               int xMin = min.x();
               int xMax = max.x();
               int yMin = min.y();
               int yMax = max.y();
               int zMin = min.z();
               int zMax = max.z();
               int totalBlocks = (xMax - xMin + 1) * (zMax - zMin + 1) * (yMax - yMin + 1);
               int width = xMax - xMin;
               int depth = zMax - zMin;
               int halfWidth = width / 2;
               int halfDepth = depth / 2;
               BlockSelection before = new BlockSelection(totalBlocks, 0);
               before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
               before.setSelectionArea(min, max);
               this.pushHistory(BuilderToolsPlugin.Action.EDIT, new BlockSelectionSnapshot(before));
               BlockSelection after = new BlockSelection(totalBlocks, 0);
               after.copyPropertiesFrom(before);
               World world = componentAccessor.getExternalData().getWorld();
               LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
               int counter = 0;

               for (int x = xMin; x <= xMax; x++) {
                  for (int z = zMin; z <= zMax; z++) {
                     WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                     for (int y = yMax; y >= yMin; y--) {
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

               after.placeNoReturn("Fill 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
               BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
               long end = System.nanoTime();
               long diff = end - start;
               BuilderToolsPlugin.get()
                  .getLogger()
                  .at(Level.FINE)
                  .log("Took: %dns (%dms) to execute fill of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), counter);
               this.sendUpdate();
               this.sendArea();
            }
         }
      }

      public void replace(
         @Nonnull Ref<EntityStore> ref, @Nonnull Material from, @Nonnull Material to, @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendFeedback(Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendFeedback(Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(BuilderToolsPlugin.Action.REPLACE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
            int counter = 0;

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int currentFiller = chunk.getFiller(x, y, z);
                     if (currentFiller != 0) {
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                     } else {
                        boolean shouldReplace = false;
                        if (from.isFluid()) {
                           int currentFluidId = chunk.getFluidId(x, y, z);
                           shouldReplace = currentFluidId == from.getFluidId();
                        } else {
                           int currentBlock = chunk.getBlock(x, y, z);
                           shouldReplace = currentBlock == from.getBlockId();
                        }

                        if (shouldReplace) {
                           int currentBlock = chunk.getBlock(x, y, z);
                           int currentFluidId = chunk.getFluidId(x, y, z);
                           byte currentFluidLevel = chunk.getFluidLevel(x, y, z);
                           if (to.isFluid()) {
                              if (currentBlock != 0) {
                                 Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                                 int rotation = chunk.getRotationIndex(x, y, z);
                                 int supportValue = chunk.getSupportValue(x, y, z);
                                 before.addBlockAtWorldPos(x, y, z, currentBlock, rotation, currentFiller, supportValue, holder);
                                 after.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                 this.clearFillerBlocksIfNeeded(x, y, z, currentBlock, rotation, accessor, before, after);
                              }

                              before.addFluidAtWorldPos(x, y, z, currentFluidId, currentFluidLevel);
                              after.addFluidAtWorldPos(x, y, z, to.getFluidId(), to.getFluidLevel());
                           } else if (to.isEmpty()) {
                              Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                              int rotation = chunk.getRotationIndex(x, y, z);
                              int supportValue = chunk.getSupportValue(x, y, z);
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

                              Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                              Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(
                                 chunk, x, y, z, to.getBlockId(), currentBlock, holder, true
                              );
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
            }

            after.placeNoReturn("Replace 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute replace", (long)diff, (long)TimeUnit.NANOSECONDS.toMillis(diff));
            this.sendUpdate();
            this.sendArea();
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_SELECTION_FILL"), SoundCategory.SFX, componentAccessor);
         }
      }

      private void clearFillerBlocksIfNeeded(
         int baseX, int baseY, int baseZ, int oldBlockId, int rotationIndex, LocalCachedChunkAccessor accessor, BlockSelection before, BlockSelection after
      ) {
         this.replaceMultiBlockStructure(baseX, baseY, baseZ, oldBlockId, 0, rotationIndex, accessor, before, after);
      }

      private void replaceMultiBlockStructure(
         int baseX,
         int baseY,
         int baseZ,
         int oldBlockId,
         int newBlockId,
         int rotationIndex,
         LocalCachedChunkAccessor accessor,
         BlockSelection before,
         BlockSelection after
      ) {
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
            FillerBlockUtil.forEachFillerBlock(
               oldHitbox.get(rotationIndex),
               (fx, fy, fz) -> {
                  if (fx != 0 || fy != 0 || fz != 0) {
                     int fillerX = baseX + fx;
                     int fillerY = baseY + fy;
                     int fillerZ = baseZ + fz;
                     WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                     int fillerBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                     int fillerFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                     if (fillerFiller != 0) {
                        Holder<ChunkStore> fillerHolder = fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ);
                        before.addBlockAtWorldPos(
                           fillerX,
                           fillerY,
                           fillerZ,
                           fillerBlock,
                           rotationIndex,
                           fillerFiller,
                           fillerChunk.getSupportValue(fillerX, fillerY, fillerZ),
                           fillerHolder
                        );
                        boolean willBeFilledByNewStructure = finalNewHitbox != null
                           && finalNewHitbox.protrudesUnitBox()
                           && finalNewHitbox.get(rotationIndex).getBoundingBox().containsBlock(fx, fy, fz);
                        if (!willBeFilledByNewStructure) {
                           after.addBlockAtWorldPos(fillerX, fillerY, fillerZ, 0, 0, 0, 0);
                        }
                     }
                  }
               }
            );
         }

         if (newHitbox != null && newHitbox.protrudesUnitBox()) {
            FillerBlockUtil.forEachFillerBlock(
               newHitbox.get(rotationIndex),
               (fx, fy, fz) -> {
                  if (fx != 0 || fy != 0 || fz != 0) {
                     int fillerX = baseX + fx;
                     int fillerY = baseY + fy;
                     int fillerZ = baseZ + fz;
                     WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                     int existingBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                     int existingFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                     if (existingFiller == 0 && !before.hasBlockAtWorldPos(fillerX, fillerY, fillerZ)) {
                        Holder<ChunkStore> fillerHolder = fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ);
                        before.addBlockAtWorldPos(
                           fillerX,
                           fillerY,
                           fillerZ,
                           existingBlock,
                           rotationIndex,
                           existingFiller,
                           fillerChunk.getSupportValue(fillerX, fillerY, fillerZ),
                           fillerHolder
                        );
                     }

                     int newFiller = FillerBlockUtil.pack(fx, fy, fz);
                     after.addBlockAtWorldPos(fillerX, fillerY, fillerZ, newBlockId, rotationIndex, newFiller, 0);
                  }
               }
            );
         }
      }

      public void replace(
         @Nonnull Ref<EntityStore> ref,
         @Nullable BlockMask fromMask,
         @Nonnull BlockPattern toPattern,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            BlockSelection before = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            before.setSelectionArea(min, max);
            this.pushHistory(BuilderToolsPlugin.Action.REPLACE, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(before);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
            int counter = 0;

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int filler = chunk.getFiller(x, y, z);
                     if (filler != 0) {
                        this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                     } else {
                        int block = chunk.getBlock(x, y, z);
                        if (block >= 0 && block != 1) {
                           boolean shouldReplace;
                           if (fromMask == null) {
                              shouldReplace = true;
                           } else {
                              int fluidId = chunk.getFluidId(x, y, z);
                              shouldReplace = !fromMask.isExcluded(accessor, x, y, z, min, max, block, fluidId);
                           }

                           if (shouldReplace) {
                              Holder<ChunkStore> holder = chunk.getBlockComponentHolder(x, y, z);
                              Material material = Material.fromPattern(toPattern, this.random);
                              int newBlockId = material.getBlockId();
                              int newRotation = material.hasRotation() ? material.getRotation() : chunk.getRotationIndex(x, y, z);
                              Holder<ChunkStore> newHolder = BuilderToolsPlugin.createBlockComponent(chunk, x, y, z, newBlockId, block, holder, true);
                              int rotationIndex = chunk.getRotationIndex(x, y, z);
                              before.addBlockAtWorldPos(
                                 x, y, z, block, rotationIndex, filler, chunk.getSupportValue(x, y, z), chunk.getBlockComponentHolder(x, y, z)
                              );
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
                        } else {
                           this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                        }
                     }
                  }
               }
            }

            after.placeNoReturn("Replace 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute replace of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
            SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("CREATE_SELECTION_FILL"), SoundCategory.SFX, componentAccessor);
         }
      }

      public int replace(@Nonnull Ref<EntityStore> ref, @Nonnull Int2IntFunction function, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
            return 0;
         }

         if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
            return 0;
         }

         long start = System.nanoTime();
         Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
         Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
         int xMin = min.x();
         int xMax = max.x();
         int yMin = min.y();
         int yMax = max.y();
         int zMin = min.z();
         int zMax = max.z();
         BlockSelection before = new BlockSelection();
         int width = xMax - xMin;
         int depth = zMax - zMin;
         int halfWidth = width / 2;
         int halfDepth = depth / 2;
         before.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
         before.setSelectionArea(min, max);
         this.pushHistory(BuilderToolsPlugin.Action.REPLACE, new BlockSelectionSnapshot(before));
         BlockSelection after = new BlockSelection(before);
         World world = componentAccessor.getExternalData().getWorld();
         LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
         int totalBlocks = (width + 1) * (depth + 1) * (yMax - yMin + 1);
         int counter = 0;

         for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
               WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

               for (int y = yMax; y >= yMin; y--) {
                  int filler = chunk.getFiller(x, y, z);
                  if (filler != 0) {
                     this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                  } else {
                     int block = chunk.getBlock(x, y, z);
                     int replace = function.applyAsInt(block);
                     if (block != replace) {
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
         }

         after.placeNoReturn("Replace 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
         BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
         long end = System.nanoTime();
         long diff = end - start;
         int replacedCount = after.getBlockCount();
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute replace of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), replacedCount);
         this.sendUpdate();
         this.sendArea();
         return replacedCount;
      }

      public void move(
         @Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, boolean empty, boolean entities, @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
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

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int block = chunk.getBlock(x, y, z);
                     int fluidId = chunk.getFluidId(x, y, z);
                     byte fluidLevel = chunk.getFluidLevel(x, y, z);
                     if ((block != 0 || fluidId != 0 || empty)
                        && (this.globalMask == null || !this.globalMask.isExcluded(accessor, x, y, z, min, max, block, fluidId))) {
                        int filler = chunk.getFiller(x, y, z);
                        int rotationIndex = chunk.getRotationIndex(x, y, z);
                        selected.addBlockAtWorldPos(
                           x, y, z, block, rotationIndex, filler, chunk.getSupportValue(x, y, z), chunk.getBlockComponentHolder(x, y, z)
                        );
                        selected.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                        cleared.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                        cleared.addFluidAtWorldPos(x, y, z, 0, (byte)0);
                        if (filler == 0 && block != 0) {
                           BlockType blockType = blockTypeAssetMap.getAsset(block);
                           if (blockType != null) {
                              BlockBoundingBoxes hitbox = hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex());
                              if (hitbox != null && hitbox.protrudesUnitBox()) {
                                 int baseX = x;
                                 int baseY = y;
                                 int baseZ = z;
                                 FillerBlockUtil.forEachFillerBlock(
                                    hitbox.get(rotationIndex),
                                    (fx, fy, fz) -> {
                                       if (fx != 0 || fy != 0 || fz != 0) {
                                          int fillerX = baseX + fx;
                                          int fillerY = baseY + fy;
                                          int fillerZ = baseZ + fz;
                                          if (fillerX < xMin || fillerX > xMax || fillerY < yMin || fillerY > yMax || fillerZ < zMin || fillerZ > zMax) {
                                             WorldChunk fillerChunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(fillerX, fillerZ));
                                             int fillerBlock = fillerChunk.getBlock(fillerX, fillerY, fillerZ);
                                             int fillerFiller = fillerChunk.getFiller(fillerX, fillerY, fillerZ);
                                             if (fillerFiller != 0) {
                                                int fillerRotation = fillerChunk.getRotationIndex(fillerX, fillerY, fillerZ);
                                                selected.addBlockAtWorldPos(
                                                   fillerX,
                                                   fillerY,
                                                   fillerZ,
                                                   fillerBlock,
                                                   fillerRotation,
                                                   fillerFiller,
                                                   fillerChunk.getSupportValue(fillerX, fillerY, fillerZ),
                                                   fillerChunk.getBlockComponentHolder(fillerX, fillerY, fillerZ)
                                                );
                                                cleared.addBlockAtWorldPos(fillerX, fillerY, fillerZ, 0, 0, 0, 0);
                                             }
                                          }
                                       }
                                    }
                                 );
                              }
                           }
                        }
                     }
                  }
               }
            }

            BlockSelection beforeCleared = cleared.place(this.playerRef, world);
            selected.setPosition(xPos + direction.x(), yPos + direction.y(), zPos + direction.z());
            BlockSelection beforePlace = selected.place(this.playerRef, world);
            List<SelectionSnapshot<?>> snapshots = new ObjectArrayList<>();
            if (entities) {
               for (Ref<EntityStore> targetEntityRef : TargetUtil.getAllEntitiesInBox(
                  Vector3iUtil.toVector3d(min), Vector3iUtil.toVector3d(max), componentAccessor
               )) {
                  snapshots.add(new EntityTransformSnapshot(targetEntityRef, componentAccessor));
                  TransformComponent transformComponent = componentAccessor.getComponent(targetEntityRef, TransformComponent.getComponentType());
                  if (transformComponent != null) {
                     transformComponent.getPosition().add(direction.x, direction.y, direction.z);
                  }
               }
            }

            beforePlace.add(beforeCleared);
            ClipboardBoundsSnapshot clipboardSnapshot = new ClipboardBoundsSnapshot(min, max);
            Vector3i destMin = new Vector3i(min).add(direction);
            Vector3i destMax = new Vector3i(max).add(direction);
            beforePlace.setSelectionArea(Vector3iUtil.min(min, destMin), Vector3iUtil.max(max, destMax));
            snapshots.add(new BlockSelectionSnapshot(beforePlace));
            snapshots.add(clipboardSnapshot);
            this.pushHistory(BuilderToolsPlugin.Action.MOVE, snapshots);
            BuilderToolsPlugin.invalidateWorldMapForSelection(cleared, world);
            BuilderToolsPlugin.invalidateWorldMapForSelection(selected, world);
            this.selection.setSelectionArea(new Vector3i(min).add(direction), new Vector3i(max).add(direction));
            this.syncRawPositions();
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute move of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), cleared.getBlockCount());
            this.sendUpdate();
            this.sendArea();
            this.sendFeedback(
               Message.translation("server.builderTools.selectionMovedBy").param("x", direction.x()).param("y", direction.y()).param("z", direction.z()),
               componentAccessor
            );
         }
      }

      public void shift(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            this.selection.setSelectionArea(this.selection.getSelectionMin().add(direction), this.selection.getSelectionMax().add(direction));
            this.syncRawPositions();
            this.sendArea();
            this.sendFeedback(
               Message.translation("server.builderTools.selectionShiftedBy").param("x", direction.x()).param("y", direction.y()).param("z", direction.z()),
               componentAccessor
            );
         }
      }

      public void pos1(@Nonnull Vector3i pos1, ComponentAccessor<EntityStore> componentAccessor) {
         this.rawPos1 = new Vector3i(pos1);
         if (this.selection == null) {
            this.selection = new BlockSelection();
         }

         if (this.selection.hasSelectionBounds()) {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            if (this.rawPos2 == null) {
               this.rawPos2 = this.selection.getSelectionMax();
            }
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
         }

         Vector3i otherCorner = this.rawPos2 != null ? this.rawPos2 : pos1;
         this.selection.setSelectionArea(pos1, otherCorner);
         this.sendArea();
         this.sendFeedback(
            Message.translation("server.builderTools.setPosTo").param("num", 1).param("x", pos1.x()).param("y", pos1.y()).param("z", pos1.z()),
            componentAccessor
         );
      }

      public void pos2(@Nonnull Vector3i pos2, ComponentAccessor<EntityStore> componentAccessor) {
         this.rawPos2 = new Vector3i(pos2);
         if (this.selection == null) {
            this.selection = new BlockSelection();
         }

         if (this.selection.hasSelectionBounds()) {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            if (this.rawPos1 == null) {
               this.rawPos1 = this.selection.getSelectionMin();
            }
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
         }

         Vector3i otherCorner = this.rawPos1 != null ? this.rawPos1 : pos2;
         this.selection.setSelectionArea(otherCorner, pos2);
         this.sendArea();
         this.sendFeedback(
            Message.translation("server.builderTools.setPosTo").param("num", 2).param("x", pos2.x()).param("y", pos2.y()).param("z", pos2.z()),
            componentAccessor
         );
      }

      public void select(@Nonnull Vector3i pos1, @Nonnull Vector3i pos2, @Nullable String reason, ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection != null && !this.selection.getSelectionMax().equals(Vector3iUtil.ZERO)) {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            this.selection.setSelectionArea(pos1, pos2);
            this.syncRawPositions();
            this.sendArea();
         } else {
            if (this.selection == null) {
               this.selection = new BlockSelection();
            }

            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, ClipboardBoundsSnapshot.EMPTY);
            this.selection.setSelectionArea(pos1, pos2);
            this.syncRawPositions();
            this.sendArea();
         }

         if (reason != null) {
            this.sendFeedback(
               Message.translation(reason)
                  .param("x1", pos1.x())
                  .param("y1", pos1.y())
                  .param("z1", pos1.z())
                  .param("x2", pos2.x())
                  .param("y2", pos2.y())
                  .param("z2", pos2.z()),
               componentAccessor
            );
         } else {
            this.sendFeedback(
               Message.translation("server.builderTools.selected")
                  .param("x1", pos1.x())
                  .param("y1", pos1.y())
                  .param("z1", pos1.z())
                  .param("x2", pos2.x())
                  .param("y2", pos2.y())
                  .param("z2", pos2.z()),
               componentAccessor
            );
         }
      }

      public void deselect(ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection != null && this.selection.hasSelectionBounds()) {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            this.selection.setSelectionArea(Vector3iUtil.ZERO, Vector3iUtil.ZERO);
            this.rawPos1 = null;
            this.rawPos2 = null;
            EditorBlocksChange packet = new EditorBlocksChange();
            packet.selection = null;
            this.playerRef.getPacketHandler().write(packet);
            this.sendFeedback(Message.translation("server.builderTools.deselected"), componentAccessor);
            BiConsumer<PlayerRef, Store<EntityStore>> clearedCb = BuilderToolsPlugin.get().getSelectionClearedCallback();
            if (clearedCb != null) {
               World world = componentAccessor.getExternalData().getWorld();
               if (world != null) {
                  clearedCb.accept(this.playerRef, world.getEntityStore().getStore());
               }
            }
         } else {
            this.sendFeedback(Message.translation("server.builderTools.noSelectionToDeselect"), componentAccessor);
         }
      }

      public void stack(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull Vector3i direction,
         int count,
         boolean empty,
         int spacing,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            BlockSelection selected = new BlockSelection();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            selected.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
            World world = componentAccessor.getExternalData().getWorld();
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = yMax; y >= yMin; y--) {
                     int block = chunk.getBlock(x, y, z);
                     int fluidId = chunk.getFluidId(x, y, z);
                     byte fluidLevel = chunk.getFluidLevel(x, y, z);
                     if ((block != 0 || fluidId != 0 || empty)
                        && (this.globalMask == null || !this.globalMask.isExcluded(accessor, x, y, z, min, max, block, fluidId))) {
                        selected.addBlockAtWorldPos(
                           x,
                           y,
                           z,
                           block,
                           chunk.getRotationIndex(x, y, z),
                           chunk.getFiller(x, y, z),
                           chunk.getSupportValue(x, y, z),
                           chunk.getBlockComponentHolder(x, y, z)
                        );
                        selected.addFluidAtWorldPos(x, y, z, fluidId, fluidLevel);
                     }
                  }
               }
            }

            BlockSelection before = new BlockSelection();
            before.setAnchor(selected.getAnchorX(), selected.getAnchorY(), selected.getAnchorZ());
            before.setPosition(selected.getX(), selected.getY(), selected.getZ());
            Vector3i size = max.sub(min).add(1, 1, 1);

            for (int i = 1; i <= count; i++) {
               selected.setPosition(
                  before.getX() + (size.x() + spacing) * direction.x() * i,
                  before.getY() + (size.y() + spacing) * direction.y() * i,
                  before.getZ() + (size.z() + spacing) * direction.z() * i
               );
               before.add(selected.place(this.playerRef, world));
            }

            Vector3i stackOffset = new Vector3i(
               (size.x() + spacing) * direction.x() * count, (size.y() + spacing) * direction.y() * count, (size.z() + spacing) * direction.z() * count
            );
            Vector3i totalMin = Vector3iUtil.min(min, min.add(stackOffset));
            Vector3i totalMax = Vector3iUtil.max(max, max.add(stackOffset));
            before.setSelectionArea(totalMin, totalMax);
            this.pushHistory(BuilderToolsPlugin.Action.STACK, new BlockSelectionSnapshot(before));
            BuilderToolsPlugin.invalidateWorldMapForSelection(before, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute stack of %d blocks %d times", diff, TimeUnit.NANOSECONDS.toMillis(diff), selected.getBlockCount(), count);
            this.sendUpdate();
            this.sendArea();
            this.sendFeedback(
               Message.translation("server.builderTools.selectionStacked")
                  .param("count", count)
                  .param("x", direction.x())
                  .param("y", direction.y())
                  .param("z", direction.z()),
               componentAccessor
            );
         }
      }

      public void expand(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            if (direction.x() < 0) {
               min = min.add(direction.x(), 0, 0);
            } else if (direction.x() > 0) {
               max = max.add(direction.x(), 0, 0);
            }

            if (direction.y() < 0) {
               min = min.add(0, direction.y(), 0);
            } else if (direction.y() > 0) {
               max = max.add(0, direction.y(), 0);
            }

            if (direction.z() < 0) {
               min = min.add(0, 0, direction.z());
            } else if (direction.z() > 0) {
               max = max.add(0, 0, direction.z());
            }

            this.selection.setSelectionArea(min, max);
            this.syncRawPositions();
            this.sendArea();
            this.sendFeedback(
               Message.translation("server.builderTools.selectionExpanded").param("x", direction.x()).param("y", direction.y()).param("z", direction.z()),
               componentAccessor
            );
         }
      }

      public void contract(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i direction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            if (direction.x() > 0) {
               min = min.add(direction.x(), 0, 0);
            } else if (direction.x() < 0) {
               max = max.add(direction.x(), 0, 0);
            }

            if (direction.y() > 0) {
               min = min.add(0, direction.y(), 0);
            } else if (direction.y() < 0) {
               max = max.add(0, direction.y(), 0);
            }

            if (direction.z() > 0) {
               min = min.add(0, 0, direction.z());
            } else if (direction.z() < 0) {
               max = max.add(0, 0, direction.z());
            }

            this.selection.setSelectionArea(min, max);
            this.syncRawPositions();
            this.sendArea();
            this.sendFeedback(
               ref,
               Message.translation("server.builderTools.selectionContracted").param("x", direction.x()).param("y", direction.y()).param("z", direction.z()),
               direction.length() > 0.0 ? "CREATE_SCALE_INCREASE" : "CREATE_SCALE_DECREASE",
               componentAccessor
            );
         }
      }

      public void shrink(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            World world = componentAccessor.getExternalData().getWorld();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
            int width = xMax - xMin;
            int depth = zMax - zMin;
            int halfWidth = width / 2;
            int halfDepth = depth / 2;
            LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
            int editorBlockPrefabAir = BlockType.getAssetMap().getIndex("Editor_Empty");
            int newXMin = Integer.MAX_VALUE;
            int newYMin = Integer.MAX_VALUE;
            int newZMin = Integer.MAX_VALUE;
            int newXMax = Integer.MIN_VALUE;
            int newYMax = Integer.MIN_VALUE;
            int newZMax = Integer.MIN_VALUE;
            int top = Math.max(yMin, yMax);
            int bottom = Math.min(yMin, yMax);
            int totalBlocks = (width + 1) * (depth + 1) * (top - bottom + 1);
            int counter = 0;

            for (int x = xMin; x <= xMax; x++) {
               for (int z = zMin; z <= zMax; z++) {
                  WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));

                  for (int y = bottom; y <= top; y++) {
                     int block = chunk.getBlock(x, y, z);
                     int fluid = chunk.getFluidId(x, y, z);
                     if (block != 0 && block != editorBlockPrefabAir || fluid != 0) {
                        newXMin = Math.min(newXMin, x);
                        newYMin = Math.min(newYMin, y);
                        newZMin = Math.min(newZMin, z);
                        newXMax = Math.max(newXMax, x);
                        newYMax = Math.max(newYMax, y);
                        newZMax = Math.max(newZMax, z);
                     }

                     this.sendFeedback("Shrink", totalBlocks, ++counter, componentAccessor);
                  }
               }
            }

            if (newXMin == Integer.MAX_VALUE) {
               this.sendFeedback(Message.translation("server.builderTools.selectionShrinkEmpty"), componentAccessor);
            } else {
               Vector3i newMin = new Vector3i(newXMin, newYMin, newZMin);
               Vector3i newMax = new Vector3i(newXMax, newYMax, newZMax);
               if (newMin.equals(min) && newMax.equals(max)) {
                  this.sendFeedback(Message.translation("server.builderTools.selectionShrinkUnchanged"), componentAccessor);
               } else {
                  int oldBlockCount = (xMax - xMin + 1) * (yMax - yMin + 1) * (zMax - zMin + 1);
                  int newBlockCount = (newXMax - newXMin + 1) * (newYMax - newYMin + 1) * (newZMax - newZMin + 1);
                  this.pushHistory(BuilderToolsPlugin.Action.UPDATE_SELECTION, new ClipboardBoundsSnapshot(this.selection));
                  this.selection.setSelectionArea(newMin, newMax);
                  this.syncRawPositions();
                  this.sendArea();
                  this.sendFeedback(
                     Message.translation("server.builderTools.selectionShrunk").param("oldCount", oldBlockCount).param("newCount", newBlockCount),
                     componentAccessor
                  );
               }
            }
         }
      }

      public void repairFillers(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else if (!this.selection.hasSelectionBounds()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         } else {
            long start = System.nanoTime();
            Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            int xMin = min.x();
            int xMax = max.x();
            int yMin = min.y();
            int yMax = max.y();
            int zMin = min.z();
            int zMax = max.z();
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
            this.pushHistory(BuilderToolsPlugin.Action.SET, new BlockSelectionSnapshot(before));
            BlockSelection after = new BlockSelection(totalBlocks, 0);
            after.copyPropertiesFrom(before);
            World world = componentAccessor.getExternalData().getWorld();
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            BuilderToolsPlugin.CachedAccessor cachedAccessor = BuilderToolsPlugin.CachedAccessor.of(
               chunkStore,
               ChunkUtil.chunkCoordinate(xMin + halfWidth),
               ChunkUtil.chunkCoordinate(yMin + halfHeight),
               ChunkUtil.chunkCoordinate(zMin + halfDepth),
               Math.max(Math.max(width, depth), height)
            );
            BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
            IndexedLookupTableAssetMap<String, BlockBoundingBoxes> blockHitboxMap = BlockBoundingBoxes.getAssetMap();
            int counter = 0;

            for (int x = xMin; x <= xMax; x++) {
               int cx = ChunkUtil.chunkCoordinate(x);

               for (int z = zMin; z <= zMax; z++) {
                  int cz = ChunkUtil.chunkCoordinate(z);
                  Ref<ChunkStore> chunkRef = cachedAccessor.getChunk(cx, cz);
                  WorldChunk wc = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());

                  for (int y = yMax; y >= yMin; y--) {
                     int cy = ChunkUtil.chunkCoordinate(y);
                     BlockSection chunk = cachedAccessor.getBlockSection(cx, cy, cz);
                     if (chunk != null) {
                        int block = chunk.get(x, y, z);
                        BlockType blockType = blockTypeMap.getAsset(block);
                        if (blockType != null) {
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
                              FillerBlockUtil.forEachFillerBlock(
                                 hitbox.get(chunk.getRotationIndex(x, y, z)),
                                 (x1, y1, z1) -> before.copyFromAtWorld(finalX + x1, finalY + y1, finalZ + z1, wc, physics)
                              );
                           }

                           this.sendFeedback("Gather 1/2", totalBlocks, ++counter, componentAccessor);
                        }
                     }
                  }
               }
            }

            after.tryFixFiller(false);
            after.placeNoReturn("Set 2/2", this.playerRef, BuilderToolsPlugin.FEEDBACK_CONSUMER, world, componentAccessor);
            BuilderToolsPlugin.invalidateWorldMapForSelection(after, world);
            long end = System.nanoTime();
            long diff = end - start;
            BuilderToolsPlugin.get()
               .getLogger()
               .at(Level.FINE)
               .log("Took: %dns (%dms) to execute repair of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), after.getBlockCount());
            this.sendUpdate();
            this.sendArea();
         }
      }

      @Nonnull
      public List<BuilderToolsPlugin.ActionEntry> undo(@Nonnull Ref<EntityStore> ref, int count, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         this.commitPendingUndoGroup();
         this.commitPendingEntitySessions();
         long start = System.nanoTime();
         BlockSelection before = this.selection;
         List<BuilderToolsPlugin.ActionEntry> list = new ObjectArrayList<>();

         for (int i = 0; i < count; i++) {
            BuilderToolsPlugin.ActionEntry action = this.historyAction(ref, this.undo, this.redo, componentAccessor);
            if (action == null) {
               break;
            }

            list.add(action);
         }

         if (before != this.selection) {
            this.sendUpdate();
            this.sendArea();
         }

         long end = System.nanoTime();
         long diff = end - start;
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute undo of %d actions", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
         if (list.isEmpty()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.nothingToUndo"), componentAccessor);
         } else {
            int i = 0;

            for (BuilderToolsPlugin.ActionEntry pair : list) {
               if (pair.isEntityNotFound()) {
                  this.sendErrorFeedback(ref, Message.translation("server.builderTools.undoEntityNotFound"), componentAccessor);
               } else {
                  this.sendFeedback(
                     ref,
                     Message.translation("server.builderTools.undoStatus").param("index", ++i).param("action", pair.getAction().toMessage()),
                     "CREATE_UNDO",
                     componentAccessor
                  );
               }
            }
         }

         return list;
      }

      @Nonnull
      public List<BuilderToolsPlugin.ActionEntry> redo(@Nonnull Ref<EntityStore> ref, int count, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         this.commitPendingEntitySessions();
         long start = System.nanoTime();
         BlockSelection before = this.selection;
         List<BuilderToolsPlugin.ActionEntry> list = new ObjectArrayList<>();

         for (int i = 0; i < count; i++) {
            BuilderToolsPlugin.ActionEntry action = this.historyAction(ref, this.redo, this.undo, componentAccessor);
            if (action == null) {
               break;
            }

            list.add(action);
         }

         if (before != this.selection) {
            this.sendUpdate();
            this.sendArea();
         }

         long end = System.nanoTime();
         long diff = end - start;
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute redo of %d actions", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
         if (list.isEmpty()) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.nothingToRedo"), componentAccessor);
         } else {
            int i = 0;

            for (BuilderToolsPlugin.ActionEntry pair : list) {
               if (pair.isEntityNotFound()) {
                  this.sendErrorFeedback(ref, Message.translation("server.builderTools.redoEntityNotFound"), componentAccessor);
               } else {
                  this.sendFeedback(
                     ref,
                     Message.translation("server.builderTools.redoStatus").param("index", ++i).param("action", pair.getAction().toMessage()),
                     "CREATE_REDO",
                     componentAccessor
                  );
               }
            }
         }

         return list;
      }

      public void save(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull String name,
         boolean relativize,
         boolean overwrite,
         @Nonnull SupportMode supportMode,
         @Nullable AssetPack targetPack,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection == null) {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelection"), componentAccessor);
         } else {
            long start = System.nanoTime();
            name = name.trim();
            if (name.isBlank()) {
               this.sendErrorFeedback(ref, Message.translation("server.builderTools.prefabSave.nameRequired"), componentAccessor);
            } else if (name.contains("..")) {
               this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
            } else {
               if (!name.endsWith(".prefab.json")) {
                  name = name + ".prefab.json";
               }

               PrefabStore prefabStore = PrefabStore.get();
               Path basePath = prefabStore.getPrefabsPathForPack(targetPack);
               if (!PathUtil.isChildOf(basePath, basePath.resolve(name)) && !SingleplayerModule.isOwner(this.playerRef)) {
                  this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
               } else {
                  try {
                     BlockSelection postClone = relativize ? this.selection.relativize() : this.selection.cloneSelection();
                     PrefabSaveSupport.apply(postClone, supportMode);
                     if (targetPack != null) {
                        prefabStore.savePrefabToPack(targetPack, name, postClone, overwrite);
                     } else {
                        prefabStore.saveServerPrefab(name, postClone, overwrite);
                     }

                     this.sendUpdate();
                     String savedKey = targetPack != null ? "server.builderTools.savedSelectionToPrefab.pack" : "server.builderTools.savedSelectionToPrefab";
                     Message savedMsg = Message.translation(savedKey).param("name", name);
                     if (targetPack != null) {
                        savedMsg = savedMsg.param("pack", targetPack.getName());
                     }

                     this.sendFeedback(savedMsg, componentAccessor);
                  } catch (PrefabSaveException e) {
                     switch (e.getType()) {
                        case ERROR:
                           BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e).log("Exception saving prefab %s", name);
                           this.sendFeedback(
                              Message.translation("server.builderTools.errorSavingPrefab").param("name", name).param("message", e.getCause().getMessage()),
                              componentAccessor
                           );
                           break;
                        case ALREADY_EXISTS:
                           BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", name);
                           this.sendFeedback(Message.translation("server.builderTools.prefabAlreadyExists"), componentAccessor);
                     }
                  }

                  long end = System.nanoTime();
                  long diff = end - start;
                  BuilderToolsPlugin.get()
                     .getLogger()
                     .at(Level.FINE)
                     .log("Took: %dns (%dms) to execute save of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
               }
            }
         }
      }

      public void saveFromSelection(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull String name,
         boolean relativize,
         boolean overwrite,
         boolean includeEntities,
         boolean includeEmpty,
         @Nullable Vector3i playerAnchor,
         @Nonnull SupportMode supportMode,
         @Nullable AssetPack targetPack,
         @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (this.selection != null
            && (!this.selection.getSelectionMin().equals(Vector3iUtil.ZERO) || !this.selection.getSelectionMax().equals(Vector3iUtil.ZERO))) {
            World world = componentAccessor.getExternalData().getWorld();
            long start = System.nanoTime();
            name = name.trim();
            if (name.isBlank()) {
               this.sendErrorFeedback(ref, Message.translation("server.builderTools.prefabSave.nameRequired"), componentAccessor);
            } else if (name.contains("..")) {
               this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
            } else {
               if (!name.endsWith(".prefab.json")) {
                  name = name + ".prefab.json";
               }

               PrefabStore prefabStore = PrefabStore.get();
               Path basePath = prefabStore.getPrefabsPathForPack(targetPack);
               if (!PathUtil.isChildOf(basePath, basePath.resolve(name)) && !SingleplayerModule.isOwner(this.playerRef)) {
                  this.sendFeedback(Message.translation("server.builderTools.attemptedToSaveOutsidePrefabsDir"), componentAccessor);
               } else {
                  Vector3i min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
                  Vector3i max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
                  int xMin = min.x();
                  int yMin = min.y();
                  int zMin = min.z();
                  int xMax = max.x();
                  int yMax = max.y();
                  int zMax = max.z();
                  int width = xMax - xMin;
                  int height = yMax - yMin;
                  int depth = zMax - zMin;
                  int halfWidth = width / 2;
                  int halfDepth = depth / 2;
                  LocalCachedChunkAccessor accessor = LocalCachedChunkAccessor.atWorldCoords(world, xMin + halfWidth, zMin + halfDepth, Math.max(width, depth));
                  BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
                  int editorBlock = assetMap.getIndex("Editor_Block");
                  int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
                  int editorBlockPrefabAnchor = assetMap.getIndex("Editor_Anchor");
                  BlockSelection tempSelection = new BlockSelection();
                  tempSelection.setPosition(xMin + halfWidth, yMin, zMin + halfDepth);
                  tempSelection.setSelectionArea(min, max);
                  int count = 0;
                  int top = Math.max(yMin, yMax);
                  int bottom = Math.min(yMin, yMax);

                  for (int x = xMin; x <= xMax; x++) {
                     for (int z = zMin; z <= zMax; z++) {
                        WorldChunk chunk = accessor.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
                        Store<ChunkStore> store = chunk.getReference().getStore();
                        ChunkColumn chunkColumn = store.getComponent(chunk.getReference(), ChunkColumn.getComponentType());
                        int lastSection = -1;
                        BlockPhysics blockPhysics = null;

                        for (int y = top; y >= bottom; y--) {
                           int block = chunk.getBlock(x, y, z);
                           int fluid = chunk.getFluidId(x, y, z);
                           if (lastSection != ChunkUtil.chunkCoordinate(y)) {
                              lastSection = ChunkUtil.chunkCoordinate(y);
                              Ref<ChunkStore> section = chunkColumn.getSection(lastSection);
                              if (section != null) {
                                 blockPhysics = store.getComponent(section, BlockPhysics.getComponentType());
                              } else {
                                 blockPhysics = null;
                              }
                           }

                           if (block == editorBlockPrefabAnchor && playerAnchor == null) {
                              tempSelection.setAnchorAtWorldPos(x, y, z);
                              int id = BuilderToolsPlugin.getNonEmptyNeighbourBlock(accessor, x, y, z);
                              if (id > 0 && id != editorBlockPrefabAir) {
                                 tempSelection.addBlockAtWorldPos(x, y, z, id, 0, 0, 0);
                                 count++;
                              } else if (id == editorBlockPrefabAir) {
                                 tempSelection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                                 count++;
                              }
                           } else if ((block != 0 || fluid != 0 || includeEmpty) && block != editorBlock) {
                              if (block == editorBlockPrefabAir) {
                                 tempSelection.addBlockAtWorldPos(x, y, z, 0, 0, 0, 0);
                              } else {
                                 tempSelection.copyFromAtWorld(x, y, z, chunk, blockPhysics);
                              }

                              count++;
                           }
                        }
                     }
                  }

                  if (playerAnchor != null) {
                     tempSelection.setAnchorAtWorldPos(playerAnchor.x(), playerAnchor.y(), playerAnchor.z());
                  }

                  if (includeEntities) {
                     Store<EntityStore> entityStore = world.getEntityStore().getStore();
                     BuilderToolsPlugin.forEachCopyableInSelection(world, xMin, yMin, zMin, width, height, depth, e -> {
                        Holder<EntityStore> holder = entityStore.copyEntity(e);
                        tempSelection.addEntityFromWorld(holder);
                     });
                  }

                  for (PrefabSaveContributor contributor : BuilderToolsPlugin.get().getPrefabSaveContributors()) {
                     contributor.contribute(tempSelection, world, new Vector3i(xMin, yMin, zMin), new Vector3i(xMax, yMax, zMax));
                  }

                  try {
                     BlockSelection postClone = relativize ? tempSelection.relativize() : tempSelection.cloneSelection();
                     PrefabSaveSupport.apply(postClone, supportMode);
                     if (targetPack != null) {
                        prefabStore.savePrefabToPack(targetPack, name, postClone, overwrite);
                     } else {
                        prefabStore.saveServerPrefab(name, postClone, overwrite);
                     }

                     String savedKey = targetPack != null ? "server.builderTools.savedSelectionToPrefab.pack" : "server.builderTools.savedSelectionToPrefab";
                     Message savedMsg = Message.translation(savedKey).param("name", name);
                     if (targetPack != null) {
                        savedMsg = savedMsg.param("pack", targetPack.getName());
                     }

                     this.sendFeedback(savedMsg, componentAccessor);
                  } catch (PrefabSaveException e) {
                     switch (e.getType()) {
                        case ERROR:
                           BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e).log("Exception saving prefab %s", name);
                           this.sendFeedback(
                              Message.translation("server.builderTools.errorSavingPrefab").param("name", name).param("message", e.getCause().getMessage()),
                              componentAccessor
                           );
                           break;
                        case ALREADY_EXISTS:
                           BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", name);
                           this.sendFeedback(Message.translation("server.builderTools.prefabAlreadyExists"), componentAccessor);
                     }
                  }

                  long end = System.nanoTime();
                  long diff = end - start;
                  BuilderToolsPlugin.get()
                     .getLogger()
                     .at(Level.FINE)
                     .log("Took: %dns (%dms) to execute saveFromSelection of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), count);
               }
            }
         } else {
            this.sendErrorFeedback(ref, Message.translation("server.builderTools.noSelectionBounds"), componentAccessor);
         }
      }

      public void load(@Nonnull String name, @Nonnull BlockSelection serverPrefab, ComponentAccessor<EntityStore> componentAccessor) {
         long start = System.nanoTime();

         try {
            Vector3ic min = Vector3iUtil.ZERO;
            Vector3ic max = Vector3iUtil.ZERO;
            if (this.selection != null) {
               Objects.requireNonNull(this.selection.getSelectionMin(), "min is null");
               Objects.requireNonNull(this.selection.getSelectionMax(), "max is null");
               min = Vector3iUtil.min(this.selection.getSelectionMin(), this.selection.getSelectionMax());
               max = Vector3iUtil.max(this.selection.getSelectionMin(), this.selection.getSelectionMax());
            }

            this.pushHistory(
               BuilderToolsPlugin.Action.COPY, ClipboardContentsSnapshot.copyOf(Objects.requireNonNullElseGet(this.selection, BlockSelection::new))
            );
            this.setSelection(serverPrefab.cloneSelection());
            this.selection.setSelectionArea(min, max);
            this.cumulativeRotX = this.cumulativeRotY = this.cumulativeRotZ = 0;
            this.syncRawPositions();
            this.sendUpdate();
            this.sendFeedback(Message.translation("server.general.loadedPrefab").param("name", name), componentAccessor);
         } catch (PrefabLoadException e) {
            switch (e.getType()) {
               case ERROR:
                  BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e).log("Exception loading prefab %s", name);
                  this.sendFeedback(
                     Message.translation("server.builderTools.errorSavingPrefab").param("name", name).param("message", e.getCause().getMessage()),
                     componentAccessor
                  );
                  break;
               case NOT_FOUND:
                  BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab doesn't exist %s", name);
                  this.sendFeedback(Message.translation("server.builderTools.prefabDoesNotExist").param("name", name), componentAccessor);
            }
         }

         long end = System.nanoTime();
         long diff = end - start;
         BuilderToolsPlugin.get()
            .getLogger()
            .at(Level.FINE)
            .log("Took: %dns (%dms) to execute load of %d blocks", diff, TimeUnit.NANOSECONDS.toMillis(diff), this.selection.getBlockCount());
      }

      public void clearHistory(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         long stamp = this.undoLock.writeLock();

         try {
            this.undo.clear();
            this.redo.clear();
         } finally {
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
         EditorBlocksChange packet = Objects.requireNonNullElseGet(this.selection, BlockSelection::new).toPacket();
         packet.skipPreviewRebuild = this.skipNextPreviewRebuild;
         this.skipNextPreviewRebuild = false;
         packet.cumulativeRotX = this.cumulativeRotX;
         packet.cumulativeRotY = this.cumulativeRotY;
         packet.cumulativeRotZ = this.cumulativeRotZ;
         this.playerRef.getPacketHandler().write(packet);
      }

      public void sendArea() {
         if (this.selection != null) {
            this.playerRef.getPacketHandler().write(this.selection.toSelectionPacket());
         } else {
            EditorBlocksChange packet = new EditorBlocksChange();
            packet.selection = null;
            this.playerRef.getPacketHandler().write(packet);
         }
      }

      public void pushHistory(@Nonnull UndoAction action, @Nonnull SelectionSnapshot<?> snapshot) {
         this.pushHistory(action, Collections.singletonList(snapshot));
      }

      public void pushHistory(@Nonnull UndoAction action, @Nonnull List<SelectionSnapshot<?>> snapshots) {
         if (action != BuilderToolsPlugin.Action.UPDATE_SELECTION || this.getUserData().isRecordingSelectionHistory()) {
            long stamp = this.undoLock.writeLock();

            try {
               BuilderToolsPlugin.ActionEntry entry = new BuilderToolsPlugin.ActionEntry(action, snapshots);
               entry.setCumulativeRotBefore(this.cumulativeRotX, this.cumulativeRotY, this.cumulativeRotZ);
               this.undo.enqueue(entry);
               this.redo.clear();

               while (this.undo.size() > BuilderToolsPlugin.get().historyCount) {
                  this.undo.dequeue();
               }
            } finally {
               this.undoLock.unlockWrite(stamp);
            }

            if (action.marksPrefabDirty()) {
               this.markPrefabsDirtyFromSnapshots(snapshots);
            }
         }
      }

      private void handleBrushUndoGrouping(
         @Nonnull BlockSelection before,
         @Nonnull List<Ref<EntityStore>> spawnedRefs,
         @Nonnull List<EntityTransformSnapshot> movedSnapshots,
         int undoGroupSize,
         boolean isHoldDown
      ) {
         if (!isHoldDown) {
            this.commitPendingUndoGroup();
         }

         if (before.getBlockCount() != 0 || before.getFluidCount() != 0 || before.getEntityCount() != 0 || before.getTintCount() != 0) {
            if (this.pendingUndoSnapshot == null) {
               this.pendingUndoSnapshot = before;
            } else {
               this.mergeBeforeSnapshotPreservingOriginal(before);
            }

            this.executionCountInGroup++;

            for (Ref<EntityStore> ref : spawnedRefs) {
               this.pendingEntitySnapshots.add(new EntityAddSnapshot(ref));
            }

            this.pendingEntityTransformSnapshots.addAll(movedSnapshots);
            if (this.executionCountInGroup >= undoGroupSize) {
               this.commitPendingUndoGroup();
            }
         }
      }

      private void mergeBeforeSnapshotPreservingOriginal(@Nonnull BlockSelection newBefore) {
         newBefore.forEachBlock(
            (x, y, z, block) -> {
               int worldX = x + newBefore.getX();
               int worldY = y + newBefore.getY();
               int worldZ = z + newBefore.getZ();
               if (!this.pendingUndoSnapshot.hasBlockAtWorldPos(worldX, worldY, worldZ)) {
                  this.pendingUndoSnapshot
                     .addBlockAtWorldPos(
                        worldX,
                        worldY,
                        worldZ,
                        block.blockId(),
                        block.rotation(),
                        block.filler(),
                        block.supportValue(),
                        block.holder() != null ? block.holder().clone() : null
                     );
               }
            }
         );
         newBefore.forEachFluid((x, y, z, fluidId, fluidLevel) -> {
            int worldX = x + newBefore.getX();
            int worldY = y + newBefore.getY();
            int worldZ = z + newBefore.getZ();
            if (this.pendingUndoSnapshot.getFluidAtWorldPos(worldX, worldY, worldZ) < 0) {
               this.pendingUndoSnapshot.addFluidAtWorldPos(worldX, worldY, worldZ, fluidId, fluidLevel);
            }
         });
         newBefore.forEachTint((x, z, color) -> {
            int worldX = x + newBefore.getX();
            int worldZ = z + newBefore.getZ();
            if (!this.pendingUndoSnapshot.hasTintAtWorldPos(worldX, worldZ)) {
               this.pendingUndoSnapshot.addTintAtWorldPos(worldX, worldZ, color);
            }
         });
         newBefore.forEachEntity(entity -> this.pendingUndoSnapshot.addEntityHolderRaw(entity));
      }

      private void commitPendingUndoGroup() {
         if (this.pendingUndoSnapshot != null && this.executionCountInGroup > 0) {
            List<SelectionSnapshot<?>> snapshots = new ArrayList<>();
            snapshots.add(new BlockSelectionSnapshot(this.pendingUndoSnapshot));
            snapshots.addAll(this.pendingEntitySnapshots);
            snapshots.addAll(this.pendingEntityTransformSnapshots);
            this.pushHistory(BuilderToolsPlugin.Action.EDIT, snapshots);
            this.pendingUndoSnapshot = null;
            this.pendingEntitySnapshots.clear();
            this.pendingEntityTransformSnapshots.clear();
            this.executionCountInGroup = 0;
         }
      }

      public void handleEntityTransform(
         @Nonnull Ref<EntityStore> targetRef, boolean hasTransformData, boolean isSessionEnd, @Nonnull ComponentAccessor<EntityStore> componentAccessor
      ) {
         if (hasTransformData && this.pendingEntityTransformSession == null) {
            this.pendingEntityTransformSession = new EntityTransformSnapshot(targetRef, componentAccessor);
         }

         if (isSessionEnd) {
            this.commitPendingEntitySessions();
         }
      }

      public void commitPendingEntityTransformSession() {
         if (this.pendingEntityTransformSession != null) {
            this.pushHistory(BuilderToolsPlugin.Action.ENTITY_TRANSFORM, this.pendingEntityTransformSession);
            this.pendingEntityTransformSession = null;
         }
      }

      public void pushEntityCloneHistory(@Nonnull Ref<EntityStore> clonedEntityRef) {
         this.pushHistory(BuilderToolsPlugin.Action.ENTITY_CLONE, new EntityAddSnapshot(clonedEntityRef));
      }

      public void pushEntityRemoveHistory(@Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.pendingEntityTransformSession != null) {
            this.pendingEntityTransformSession.applyTransform(componentAccessor);
            this.pendingEntityTransformSession = null;
         }

         this.pendingEntityScaleSession = null;
         this.pendingEntityScaleRef = null;
         EntityRemoveSnapshot snapshot = new EntityRemoveSnapshot(targetRef);
         this.pushHistory(BuilderToolsPlugin.Action.ENTITY_REMOVE, snapshot);
      }

      public void pushEntityFreezeHistory(@Nonnull EntityFreezeSnapshot snapshot) {
         this.commitPendingEntitySessions();
         this.pushHistory(BuilderToolsPlugin.Action.ENTITY_FREEZE, snapshot);
      }

      public void pushEntitySettingsHistory(@Nonnull EntitySettingsSnapshot snapshot) {
         this.commitPendingEntitySessions();
         this.pushHistory(BuilderToolsPlugin.Action.ENTITY_SETTINGS, snapshot);
      }

      public void pushTriggerVolumeHistory(@Nonnull SelectionSnapshot<?> snapshot) {
         this.commitPendingEntitySessions();
         this.pushHistory(BuilderToolsPlugin.Action.TRIGGER_VOLUME, snapshot);
      }

      public void pushTriggerVolumeHistory(@Nonnull List<SelectionSnapshot<?>> snapshots) {
         this.commitPendingEntitySessions();
         this.pushHistory(BuilderToolsPlugin.Action.TRIGGER_VOLUME, snapshots);
      }

      public void handleEntityScale(@Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         if (this.pendingEntityScaleRef == null || !targetRef.equals(this.pendingEntityScaleRef)) {
            this.commitPendingEntityScaleSession();
            this.pendingEntityScaleRef = targetRef;
            this.pendingEntityScaleSession = new EntityScaleSnapshot(targetRef, componentAccessor);
         }
      }

      private void commitPendingEntityScaleSession() {
         if (this.pendingEntityScaleSession != null) {
            this.pushHistory(BuilderToolsPlugin.Action.ENTITY_SCALE, this.pendingEntityScaleSession);
            this.pendingEntityScaleSession = null;
            this.pendingEntityScaleRef = null;
         }
      }

      private void commitPendingEntitySessions() {
         this.commitPendingEntityTransformSession();
         this.commitPendingEntityScaleSession();
      }

      private void markPrefabsDirtyFromSnapshots(@Nonnull List<SelectionSnapshot<?>> snapshots) {
         PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
         Map<UUID, PrefabEditSession> activeEditSessions = prefabEditSessionManager.getActiveEditSessions();
         if (!activeEditSessions.isEmpty()) {
            for (SelectionSnapshot<?> snapshot : snapshots) {
               if (snapshot instanceof BlockSelectionSnapshot blockSnapshot) {
                  BlockSelection blockSelection = blockSnapshot.getBlockSelection();
                  Vector3i min = blockSelection.getSelectionMin();
                  Vector3i max = blockSelection.getSelectionMax();

                  for (Entry<UUID, PrefabEditSession> entry : activeEditSessions.entrySet()) {
                     entry.getValue().markPrefabsDirtyInBounds(min, max);
                  }
               }
            }
         }
      }

      @Nullable
      private BuilderToolsPlugin.ActionEntry historyAction(
         Ref<EntityStore> ref,
         @Nonnull ObjectArrayFIFOQueue<BuilderToolsPlugin.ActionEntry> from,
         @Nonnull ObjectArrayFIFOQueue<BuilderToolsPlugin.ActionEntry> to,
         ComponentAccessor<EntityStore> componentAccessor
      ) {
         long stamp = this.undoLock.writeLock();

         try {
            if (from.isEmpty()) {
               return null;
            }

            BuilderToolsPlugin.ActionEntry builderAction = from.dequeueLast();
            BuilderToolsPlugin.ActionEntry restoredAction = builderAction.restore(
               ref, this.playerRef, componentAccessor.getExternalData().getWorld(), componentAccessor
            );
            if (builderAction.getAction() == BuilderToolsPlugin.Action.ROTATE) {
               int savedRotX = this.cumulativeRotX;
               int savedRotY = this.cumulativeRotY;
               int savedRotZ = this.cumulativeRotZ;
               this.cumulativeRotX = builderAction.getCumulativeRotXBefore();
               this.cumulativeRotY = builderAction.getCumulativeRotYBefore();
               this.cumulativeRotZ = builderAction.getCumulativeRotZBefore();
               restoredAction.setCumulativeRotBefore(savedRotX, savedRotY, savedRotZ);
            }

            to.enqueue(restoredAction);

            while (to.size() > BuilderToolsPlugin.get().historyCount) {
               to.dequeue();
            }

            propagateEntityRefUpdates(builderAction, restoredAction, from);
            return builderAction;
         } finally {
            this.undoLock.unlockWrite(stamp);
         }
      }

      private static void propagateEntityRefUpdates(
         @Nonnull BuilderToolsPlugin.ActionEntry original,
         @Nonnull BuilderToolsPlugin.ActionEntry restored,
         @Nonnull ObjectArrayFIFOQueue<BuilderToolsPlugin.ActionEntry> remaining
      ) {
         List<SelectionSnapshot<?>> originalSnapshots = original.getSnapshots();
         List<SelectionSnapshot<?>> restoredSnapshots = restored.getSnapshots();
         List<Ref<EntityStore>> oldRefs = null;
         List<Ref<EntityStore>> newRefs = null;

         for (int i = 0; i < originalSnapshots.size() && i < restoredSnapshots.size(); i++) {
            if (originalSnapshots.get(i) instanceof EntityRemoveSnapshot removeSnapshot && restoredSnapshots.get(i) instanceof EntityAddSnapshot addSnapshot) {
               Ref<EntityStore> oldRef = removeSnapshot.getOriginalRef();
               Ref<EntityStore> newRef = addSnapshot.getEntityRef();
               if (oldRef != newRef) {
                  if (oldRefs == null) {
                     oldRefs = new ObjectArrayList<>();
                     newRefs = new ObjectArrayList<>();
                  }

                  oldRefs.add(oldRef);
                  newRefs.add(newRef);
               }
            }
         }

         if (oldRefs != null) {
            List<BuilderToolsPlugin.ActionEntry> temp = new ObjectArrayList<>(remaining.size());

            while (!remaining.isEmpty()) {
               temp.add(remaining.dequeue());
            }

            for (BuilderToolsPlugin.ActionEntry entry : temp) {
               for (SelectionSnapshot<?> snapshot : entry.getSnapshots()) {
                  if (snapshot instanceof EntitySnapshot<?> entitySnapshot) {
                     for (int i = 0; i < oldRefs.size(); i++) {
                        entitySnapshot.updateEntityRef(oldRefs.get(i), newRefs.get(i));
                     }
                  }
               }

               remaining.enqueue(entry);
            }
         }
      }

      public static class BlocksSampleData {
         public int mainBlock = 0;
         public int mainBlockCount = 0;
         public int mainBlockNotAir = 0;
         public int mainBlockNotAirCount = 0;
      }

      public static class SmoothSampleData {
         public float solidStrength = 0.0F;
         public int solidBlock = 0;
         public int solidBlockCount = 0;
         public int fillerBlock = 0;
         public int fillerBlockCount = 0;
      }
   }

   public static class BuilderToolsConfig {
      public static final BuilderCodec<BuilderToolsPlugin.BuilderToolsConfig> CODEC = BuilderCodec.builder(
            BuilderToolsPlugin.BuilderToolsConfig.class, BuilderToolsPlugin.BuilderToolsConfig::new
         )
         .append(new KeyedCodec<>("HistoryCount", Codec.INTEGER), (o, i) -> o.historyCount = i, o -> o.historyCount)
         .documentation("The number of builder tool edit operations to keep in the undo/redo history")
         .add()
         .<Long>append(new KeyedCodec<>("ToolExpireTime", Codec.LONG), (o, l) -> o.toolExpireTime = l, o -> o.toolExpireTime)
         .documentation(
            "The minimum time (in seconds) that a user's builder tool data will be persisted for after they disconnect from the server. If set to zero the player's data is removed immediately on disconnect"
         )
         .addValidator(Validators.greaterThanOrEqual(0L))
         .add()
         .build();
      private int historyCount = 50;
      private long toolExpireTime = 3600L;
   }

   public static class CachedAccessor extends AbstractCachedAccessor {
      private static final ThreadLocal<BuilderToolsPlugin.CachedAccessor> THREAD_LOCAL = ThreadLocal.withInitial(BuilderToolsPlugin.CachedAccessor::new);
      private static final int FLUID_COMPONENT = 0;
      private static final int PHYSICS_COMPONENT = 1;
      private static final int BLOCKS_COMPONENT = 2;

      public CachedAccessor() {
         super(3);
      }

      @Nonnull
      public static BuilderToolsPlugin.CachedAccessor of(ComponentAccessor<ChunkStore> accessor, int cx, int cy, int cz, int radius) {
         BuilderToolsPlugin.CachedAccessor cachedAccessor = THREAD_LOCAL.get();
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

   public record ColorGradientMaterial(@Nonnull BlockPattern pattern, float weight) {
   }

   public static class PrefabPasteEventSystem extends WorldEventSystem<EntityStore, PrefabPasteEvent> {
      @Nonnull
      private final BuilderToolsPlugin plugin;

      protected PrefabPasteEventSystem(@Nonnull BuilderToolsPlugin plugin) {
         super(PrefabPasteEvent.class);
         this.plugin = plugin;
      }

      public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PrefabPasteEvent event) {
         if (event.isPasteStart()) {
            this.plugin.pastedPrefabPathUUIDMap.put(event.getPrefabId(), new ConcurrentHashMap<>());
            this.plugin.pastedPrefabPathNameToUUIDMap.put(event.getPrefabId(), new ConcurrentHashMap<>());
         } else {
            this.plugin.pastedPrefabPathUUIDMap.remove(event.getPrefabId());
            this.plugin.pastedPrefabPathNameToUUIDMap.remove(event.getPrefabId());
         }
      }
   }

   private static final class QueuedTask {
      @Nonnull
      private final ThrowableTriConsumer<Ref<EntityStore>, BuilderToolsPlugin.BuilderState, ComponentAccessor<EntityStore>, ? extends Throwable> task;

      private QueuedTask(
         @Nonnull ThrowableTriConsumer<Ref<EntityStore>, BuilderToolsPlugin.BuilderState, ComponentAccessor<EntityStore>, ? extends Throwable> biTask
      ) {
         this.task = biTask;
      }

      void execute(
         @Nonnull Ref<EntityStore> ref, @Nonnull BuilderToolsPlugin.BuilderState state, @Nonnull ComponentAccessor<EntityStore> defaultComponentAccessor
      ) throws Throwable {
         this.task.acceptNow(ref, state, defaultComponentAccessor);
      }
   }

   public enum ShadingLight {
      PLAYER,
      SUN,
      ANGLE;
   }
}
