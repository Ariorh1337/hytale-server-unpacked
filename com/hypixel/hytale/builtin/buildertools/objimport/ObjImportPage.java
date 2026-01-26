/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.objimport;

import com.hypixel.hytale.builtin.buildertools.BlockColorIndex;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.buildertools.objimport.MeshVoxelizer;
import com.hypixel.hytale.builtin.buildertools.objimport.MtlParser;
import com.hypixel.hytale.builtin.buildertools.objimport.ObjParser;
import com.hypixel.hytale.builtin.buildertools.objimport.TextureSampler;
import com.hypixel.hytale.builtin.buildertools.utils.PasteToolUtil;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.StringUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserConfig;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserEventData;
import com.hypixel.hytale.server.core.ui.browser.ServerFileBrowser;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObjImportPage
extends InteractiveCustomUIPage<PageData> {
    private static final String DEFAULT_BLOCK = "Rock_Stone";
    private static final int DEFAULT_HEIGHT = 20;
    private static final int MIN_HEIGHT = 1;
    private static final int MAX_HEIGHT = 320;
    private static final float DEFAULT_SCALE = 1.0f;
    private static final float MIN_SCALE = 0.01f;
    private static final float MAX_SCALE = 100.0f;
    private static final String ASSET_PACK_SUB_PATH = "Server/Imports/Models";
    @Nonnull
    private String objPath = "";
    private int targetHeight = 20;
    private boolean useScaleMode = false;
    private float scale = 1.0f;
    @Nonnull
    private String blockPattern = "Rock_Stone";
    private boolean fillSolid = true;
    private boolean useMaterials = true;
    private boolean autoDetectTextures = false;
    @Nonnull
    private String originStr = "bottom_center";
    @Nonnull
    private Origin origin = Origin.BOTTOM_CENTER;
    @Nonnull
    private String rotationStr = "y_up";
    @Nonnull
    private MeshRotation rotation = MeshRotation.NONE;
    @Nullable
    private String statusMessage = null;
    private boolean isError = false;
    private boolean isProcessing = false;
    private boolean showBrowser = false;
    @Nonnull
    private final ServerFileBrowser browser;
    private static final String[] AUTO_DETECT_SUFFIXES = new String[]{"", "_dif", "_diffuse"};
    private static final String[] AUTO_DETECT_EXTENSIONS = new String[]{".png", ".jpg", ".jpeg"};

    public ObjImportPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PageData.CODEC);
        FileBrowserConfig config = FileBrowserConfig.builder().listElementId("#BrowserPage #FileList").searchInputId("#BrowserPage #SearchInput").currentPathId("#BrowserPage #CurrentPath").allowedExtensions(".obj").enableRootSelector(false).enableSearch(true).enableDirectoryNav(true).maxResults(50).assetPackMode(true, ASSET_PACK_SUB_PATH).build();
        this.browser = new ServerFileBrowser(config);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/ObjImportPage.ui");
        commandBuilder.set("#ObjPath #Input.Value", this.objPath);
        commandBuilder.set("#HeightInput #Input.Value", this.targetHeight);
        commandBuilder.set("#ScaleInput #Input.Value", this.scale);
        commandBuilder.set("#BlockPattern #Input.Value", this.blockPattern);
        commandBuilder.set("#FillModeCheckbox #CheckBox.Value", this.fillSolid);
        commandBuilder.set("#UseMaterialsCheckbox #CheckBox.Value", this.useMaterials);
        commandBuilder.set("#AutoDetectTexturesCheckbox #CheckBox.Value", this.autoDetectTextures);
        commandBuilder.set("#HeightInput.Visible", !this.useScaleMode);
        commandBuilder.set("#ScaleInput.Visible", this.useScaleMode);
        ArrayList<DropdownEntryInfo> sizeModeEntries = new ArrayList<DropdownEntryInfo>();
        sizeModeEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.objImport.sizeMode.height"), "height"));
        sizeModeEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.objImport.sizeMode.scale"), "scale"));
        commandBuilder.set("#SizeModeInput #Input.Entries", sizeModeEntries);
        commandBuilder.set("#SizeModeInput #Input.Value", this.useScaleMode ? "scale" : "height");
        ArrayList<DropdownEntryInfo> originEntries = new ArrayList<DropdownEntryInfo>();
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.bottom_front_left"), "bottom_front_left"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.bottom_center"), "bottom_center"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.center"), "center"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.top_center"), "top_center"));
        commandBuilder.set("#OriginInput #Input.Entries", originEntries);
        commandBuilder.set("#OriginInput #Input.Value", this.originStr);
        ArrayList<DropdownEntryInfo> axisEntries = new ArrayList<DropdownEntryInfo>();
        axisEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.objImport.axis.yUp"), "y_up"));
        axisEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.objImport.axis.zUp"), "z_up"));
        axisEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.objImport.axis.xUp"), "x_up"));
        commandBuilder.set("#RotationInput #Input.Entries", axisEntries);
        commandBuilder.set("#RotationInput #Input.Value", this.rotationStr);
        this.updateStatus(commandBuilder);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ObjPath #Input", EventData.of("@ObjPath", "#ObjPath #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#HeightInput #Input", EventData.of("@Height", "#HeightInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ScaleInput #Input", EventData.of("@Scale", "#ScaleInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SizeModeInput #Input", EventData.of("SizeMode", "#SizeModeInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BlockPattern #Input", EventData.of("@BlockPattern", "#BlockPattern #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#FillModeCheckbox #CheckBox", EventData.of("@FillSolid", "#FillModeCheckbox #CheckBox.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#UseMaterialsCheckbox #CheckBox", EventData.of("@UseMaterials", "#UseMaterialsCheckbox #CheckBox.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#AutoDetectTexturesCheckbox #CheckBox", EventData.of("@AutoDetectTextures", "#AutoDetectTexturesCheckbox #CheckBox.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#OriginInput #Input", EventData.of("@Origin", "#OriginInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#RotationInput #Input", EventData.of("@Rotation", "#RotationInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ImportButton", EventData.of("Import", "true"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ObjPath #BrowseButton", EventData.of("Browse", "true"));
        commandBuilder.set("#FormContainer.Visible", !this.showBrowser);
        commandBuilder.set("#BrowserPage.Visible", this.showBrowser);
        if (this.showBrowser) {
            this.buildBrowserPage(commandBuilder, eventBuilder);
        }
    }

    private void buildBrowserPage(@Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        this.browser.buildSearchInput(commandBuilder, eventBuilder);
        this.browser.buildCurrentPath(commandBuilder);
        this.browser.buildFileList(commandBuilder, eventBuilder);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #SelectButton", EventData.of("BrowserSelect", "true"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BrowserPage #CancelButton", EventData.of("BrowserCancel", "true"));
    }

    private void updateStatus(@Nonnull UICommandBuilder commandBuilder) {
        if (this.statusMessage != null) {
            commandBuilder.set("#StatusText.Text", this.statusMessage);
            commandBuilder.set("#StatusText.Visible", true);
            commandBuilder.set("#StatusText.Style.TextColor", this.isError ? "#e74c3c" : "#cfd8e3");
        } else {
            commandBuilder.set("#StatusText.Visible", false);
        }
    }

    private void setError(@Nonnull String message) {
        this.statusMessage = message;
        this.isError = true;
        this.isProcessing = false;
        this.rebuild();
    }

    private void setStatus(@Nonnull String message) {
        this.statusMessage = message;
        this.isError = false;
        this.rebuild();
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageData data) {
        if (data.browse != null && data.browse.booleanValue()) {
            this.showBrowser = true;
            this.rebuild();
            return;
        }
        if (data.browserCancel != null && data.browserCancel.booleanValue()) {
            this.showBrowser = false;
            this.rebuild();
            return;
        }
        if (data.browserSelect != null && data.browserSelect.booleanValue()) {
            this.showBrowser = false;
            this.rebuild();
            return;
        }
        if (this.showBrowser && this.handleBrowserEvent(data)) {
            return;
        }
        boolean needsUpdate = false;
        if (data.objPath != null) {
            this.objPath = StringUtil.stripQuotes(data.objPath.trim());
            this.statusMessage = null;
            needsUpdate = true;
        }
        if (data.height != null) {
            this.targetHeight = Math.max(1, Math.min(320, data.height));
            needsUpdate = true;
        }
        if (data.scale != null) {
            this.scale = Math.max(0.01f, Math.min(100.0f, data.scale.floatValue()));
            needsUpdate = true;
        }
        if (data.sizeMode != null) {
            this.useScaleMode = "scale".equalsIgnoreCase(data.sizeMode);
            this.rebuild();
            return;
        }
        if (data.blockPattern != null) {
            this.blockPattern = data.blockPattern.trim();
            needsUpdate = true;
        }
        if (data.fillSolid != null) {
            this.fillSolid = data.fillSolid;
            needsUpdate = true;
        }
        if (data.useMaterials != null) {
            this.useMaterials = data.useMaterials;
            needsUpdate = true;
        }
        if (data.autoDetectTextures != null) {
            this.autoDetectTextures = data.autoDetectTextures;
            needsUpdate = true;
        }
        if (data.origin != null) {
            this.origin = switch (this.originStr = data.origin.trim().toLowerCase()) {
                case "bottom_front_left" -> Origin.BOTTOM_FRONT_LEFT;
                case "center" -> Origin.CENTER;
                case "top_center" -> Origin.TOP_CENTER;
                default -> Origin.BOTTOM_CENTER;
            };
            needsUpdate = true;
        }
        if (data.rotation != null) {
            this.rotation = switch (this.rotationStr = data.rotation.trim().toLowerCase()) {
                case "z_up" -> MeshRotation.Z_UP_TO_Y_UP;
                case "x_up" -> MeshRotation.X_UP_TO_Y_UP;
                default -> MeshRotation.NONE;
            };
            needsUpdate = true;
        }
        if (data.doImport != null && data.doImport.booleanValue() && !this.isProcessing) {
            this.performImport(ref, store);
            return;
        }
        if (needsUpdate) {
            this.sendUpdate();
        }
    }

    private boolean handleBrowserEvent(@Nonnull PageData data) {
        Path resolvedPath;
        if (data.searchQuery != null) {
            this.browser.setSearchQuery(data.searchQuery.trim().toLowerCase());
            this.rebuildBrowser();
            return true;
        }
        if (data.file != null) {
            String fileName = data.file;
            if ("..".equals(fileName)) {
                this.browser.navigateUp();
                this.rebuildBrowser();
                return true;
            }
            if (this.browser.handleEvent(FileBrowserEventData.file(fileName))) {
                this.rebuildBrowser();
                return true;
            }
            Object virtualPath = this.browser.getAssetPackCurrentPath().isEmpty() ? fileName : this.browser.getAssetPackCurrentPath() + "/" + fileName;
            Path resolvedPath2 = this.browser.resolveAssetPackPath((String)virtualPath);
            if (resolvedPath2 != null && Files.isRegularFile(resolvedPath2, new LinkOption[0])) {
                this.objPath = resolvedPath2.toString();
                this.showBrowser = false;
                this.rebuild();
                return true;
            }
        }
        if (data.searchResult != null && (resolvedPath = this.browser.resolveAssetPackPath(data.searchResult)) != null && Files.isRegularFile(resolvedPath, new LinkOption[0])) {
            this.objPath = resolvedPath.toString();
            this.showBrowser = false;
            this.rebuild();
            return true;
        }
        return false;
    }

    private void rebuildBrowser() {
        UICommandBuilder commandBuilder = new UICommandBuilder();
        UIEventBuilder eventBuilder = new UIEventBuilder();
        this.browser.buildFileList(commandBuilder, eventBuilder);
        this.browser.buildCurrentPath(commandBuilder);
        this.sendUpdate(commandBuilder, eventBuilder, false);
    }

    @Nullable
    private List<WeightedBlock> parseBlockPattern(@Nonnull String pattern) {
        String[] parts;
        ArrayList<WeightedBlock> result = new ArrayList<WeightedBlock>();
        for (String part : parts = pattern.split(",")) {
            int blockId;
            if ((part = part.trim()).isEmpty()) continue;
            int weight = 100;
            String blockName = part;
            int pctIdx = part.indexOf(37);
            if (pctIdx > 0) {
                try {
                    weight = Integer.parseInt(part.substring(0, pctIdx).trim());
                    blockName = part.substring(pctIdx + 1).trim();
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            if ((blockId = BlockType.getAssetMap().getIndex(blockName)) == Integer.MIN_VALUE) {
                return null;
            }
            result.add(new WeightedBlock(blockId, weight));
        }
        return result.isEmpty() ? null : result;
    }

    private int selectRandomBlock(@Nonnull List<WeightedBlock> blocks, @Nonnull Random random) {
        if (blocks.isEmpty()) {
            throw new IllegalStateException("Cannot select from empty blocks list");
        }
        if (blocks.size() == 1) {
            return blocks.get((int)0).blockId;
        }
        int totalWeight = 0;
        for (WeightedBlock wb : blocks) {
            totalWeight += wb.weight;
        }
        if (totalWeight <= 0) {
            return blocks.get((int)0).blockId;
        }
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedBlock wb : blocks) {
            if (roll >= (cumulative += wb.weight)) continue;
            return wb.blockId;
        }
        return blocks.get((int)0).blockId;
    }

    private void performImport(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        if (this.objPath.isEmpty()) {
            this.setError("Please enter a path to an OBJ file");
            return;
        }
        Path path = Paths.get(this.objPath, new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            this.setError("File not found: " + this.objPath);
            return;
        }
        if (!this.objPath.toLowerCase().endsWith(".obj")) {
            this.setError("File must be a .obj file");
            return;
        }
        List<WeightedBlock> blocks = this.parseBlockPattern(this.blockPattern);
        if (blocks == null) {
            this.setError("Invalid block pattern: " + this.blockPattern);
            return;
        }
        this.isProcessing = true;
        this.setStatus("Processing...");
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerComponent == null || playerRefComponent == null) {
            this.setError("Player not found");
            return;
        }
        int finalHeight = this.targetHeight;
        boolean finalUseScaleMode = this.useScaleMode;
        float finalScale = this.scale;
        String finalPath = this.objPath;
        boolean finalFillSolid = this.fillSolid;
        boolean finalUseMaterials = this.useMaterials;
        boolean finalAutoDetectTextures = this.autoDetectTextures;
        Origin finalOrigin = this.origin;
        MeshRotation finalRotation = this.rotation;
        List<WeightedBlock> finalBlocks = blocks;
        BuilderToolsPlugin.addToQueue(playerComponent, playerRefComponent, (r, builderState, componentAccessor) -> {
            try {
                int computedHeight;
                Path objFilePath = Paths.get(finalPath, new String[0]);
                ObjParser.ObjMesh mesh = ObjParser.parse(objFilePath);
                switch (finalRotation.ordinal()) {
                    case 1: {
                        mesh.transformZUpToYUp();
                        break;
                    }
                    case 2: {
                        mesh.transformXUpToYUp();
                        break;
                    }
                }
                if (finalUseScaleMode) {
                    float[] bounds = mesh.getBounds();
                    float meshHeight = bounds[4] - bounds[1];
                    computedHeight = Math.max(1, (int)Math.ceil(meshHeight * finalScale));
                } else {
                    computedHeight = finalHeight;
                }
                if (finalBlocks.isEmpty()) {
                    this.setError("No blocks available for import");
                    return;
                }
                BlockColorIndex colorIndex = BuilderToolsPlugin.get().getBlockColorIndex();
                HashMap<String, BufferedImage> materialTextures = new HashMap<String, BufferedImage>();
                HashMap<String, Integer> materialToBlockId = new HashMap<String, Integer>();
                int defaultBlockId = ((WeightedBlock)finalBlocks.get((int)0)).blockId;
                if (finalUseMaterials && mesh.mtlLib() != null) {
                    this.loadMaterialData(objFilePath, mesh, colorIndex, materialTextures, materialToBlockId, finalAutoDetectTextures);
                    if (!materialToBlockId.isEmpty()) {
                        defaultBlockId = (Integer)materialToBlockId.values().iterator().next();
                    }
                }
                boolean hasUvTextures = mesh.hasUvCoordinates() && !materialTextures.isEmpty();
                boolean preserveOrigin = finalOrigin == Origin.BOTTOM_FRONT_LEFT;
                MeshVoxelizer.VoxelResult result = hasUvTextures ? MeshVoxelizer.voxelize(mesh, computedHeight, finalFillSolid, materialTextures, materialToBlockId, colorIndex, defaultBlockId, preserveOrigin) : MeshVoxelizer.voxelize(mesh, computedHeight, finalFillSolid, null, materialToBlockId, colorIndex, defaultBlockId, preserveOrigin);
                TextureSampler.clearCache();
                int offsetX = 0;
                int offsetY = 0;
                int offsetZ = 0;
                switch (finalOrigin.ordinal()) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        offsetX = -result.sizeX() / 2;
                        offsetZ = -result.sizeZ() / 2;
                        break;
                    }
                    case 2: {
                        offsetX = -result.sizeX() / 2;
                        offsetY = -result.sizeY() / 2;
                        offsetZ = -result.sizeZ() / 2;
                        break;
                    }
                    case 3: {
                        offsetX = -result.sizeX() / 2;
                        offsetY = -result.sizeY();
                        offsetZ = -result.sizeZ() / 2;
                    }
                }
                BlockSelection selection = new BlockSelection(result.countSolid(), 0);
                selection.setPosition(0, 0, 0);
                Random random = new Random();
                boolean hasMaterialBlockIds = result.blockIds() != null;
                for (int x = 0; x < result.sizeX(); ++x) {
                    for (int y = 0; y < result.sizeY(); ++y) {
                        for (int z = 0; z < result.sizeZ(); ++z) {
                            int blockId;
                            if (!result.voxels()[x][y][z]) continue;
                            if (hasMaterialBlockIds) {
                                blockId = result.getBlockId(x, y, z);
                                if (blockId == 0) {
                                    blockId = this.selectRandomBlock(finalBlocks, random);
                                }
                            } else {
                                blockId = this.selectRandomBlock(finalBlocks, random);
                            }
                            selection.addBlockAtLocalPos(x + offsetX, y + offsetY, z + offsetZ, blockId, 0, 0, 0);
                        }
                    }
                }
                selection.setSelectionArea(new Vector3i(offsetX, offsetY, offsetZ), new Vector3i(result.sizeX() - 1 + offsetX, result.sizeY() - 1 + offsetY, result.sizeZ() - 1 + offsetZ));
                builderState.setSelection(selection);
                builderState.sendSelectionToClient();
                int blockCount = result.countSolid();
                String textureInfo = hasUvTextures ? " (UV textured)" : "";
                this.statusMessage = String.format("Success! %d blocks copied to clipboard (%dx%dx%d)%s", blockCount, result.sizeX(), result.sizeY(), result.sizeZ(), textureInfo);
                this.isProcessing = false;
                playerRefComponent.sendMessage(Message.translation("server.builderTools.objImport.success").param("count", blockCount).param("width", result.sizeX()).param("height", result.sizeY()).param("depth", result.sizeZ()));
                playerComponent.getPageManager().setPage((Ref<EntityStore>)r, store, Page.None);
                PasteToolUtil.switchToPasteTool(playerComponent, playerRefComponent);
            }
            catch (ObjParser.ObjParseException e) {
                BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("OBJ parse error: %s", e.getMessage());
                this.setError("Parse error: " + e.getMessage());
            }
            catch (IOException e) {
                ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("OBJ import IO error");
                this.setError("IO error: " + e.getMessage());
            }
            catch (Exception e) {
                ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("OBJ import error");
                this.setError("Error: " + e.getMessage());
            }
        });
    }

    private void loadMaterialData(@Nonnull Path objPath, @Nonnull ObjParser.ObjMesh mesh, @Nonnull BlockColorIndex colorIndex, @Nonnull Map<String, BufferedImage> materialTextures, @Nonnull Map<String, Integer> materialToBlockId, boolean autoDetectTextures) throws IOException {
        if (mesh.mtlLib() == null) {
            return;
        }
        Path mtlPath = objPath.getParent().resolve(mesh.mtlLib());
        if (!Files.exists(mtlPath, new LinkOption[0])) {
            return;
        }
        Map<String, MtlParser.MtlMaterial> materials = MtlParser.parse(mtlPath);
        Path textureDir = mtlPath.getParent();
        for (Map.Entry<String, MtlParser.MtlMaterial> entry : materials.entrySet()) {
            int blockId;
            Path resolvedPath;
            BufferedImage texture;
            String materialName = entry.getKey();
            MtlParser.MtlMaterial material = entry.getValue();
            String texturePath = material.diffuseTexturePath();
            if (texturePath == null && autoDetectTextures) {
                texturePath = ObjImportPage.findMatchingTexture(textureDir, materialName);
            }
            if (texturePath != null && (texture = TextureSampler.loadTexture(resolvedPath = textureDir.resolve(texturePath))) != null) {
                int blockId2;
                materialTextures.put(materialName, texture);
                int[] avgColor = TextureSampler.getAverageColor(resolvedPath);
                if (avgColor == null || (blockId2 = colorIndex.findClosestBlock(avgColor[0], avgColor[1], avgColor[2])) <= 0) continue;
                materialToBlockId.put(materialName, blockId2);
                continue;
            }
            int[] rgb = material.getDiffuseColorRGB();
            if (rgb == null || (blockId = colorIndex.findClosestBlock(rgb[0], rgb[1], rgb[2])) <= 0) continue;
            materialToBlockId.put(materialName, blockId);
        }
    }

    @Nullable
    private static String findMatchingTexture(@Nonnull Path directory, @Nonnull String materialName) {
        for (String suffix : AUTO_DETECT_SUFFIXES) {
            for (String ext : AUTO_DETECT_EXTENSIONS) {
                String filename = materialName + suffix + ext;
                if (!Files.exists(directory.resolve(filename), new LinkOption[0])) continue;
                return filename;
            }
        }
        return null;
    }

    public static class PageData {
        static final String KEY_OBJ_PATH = "@ObjPath";
        static final String KEY_HEIGHT = "@Height";
        static final String KEY_SCALE = "@Scale";
        static final String KEY_SIZE_MODE = "SizeMode";
        static final String KEY_BLOCK_PATTERN = "@BlockPattern";
        static final String KEY_FILL_SOLID = "@FillSolid";
        static final String KEY_USE_MATERIALS = "@UseMaterials";
        static final String KEY_AUTO_DETECT_TEXTURES = "@AutoDetectTextures";
        static final String KEY_ORIGIN = "@Origin";
        static final String KEY_ROTATION = "@Rotation";
        static final String KEY_IMPORT = "Import";
        static final String KEY_BROWSE = "Browse";
        static final String KEY_BROWSER_SELECT = "BrowserSelect";
        static final String KEY_BROWSER_CANCEL = "BrowserCancel";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).addField(new KeyedCodec<String>("@ObjPath", Codec.STRING), (e, s) -> {
            e.objPath = s;
        }, e -> e.objPath)).addField(new KeyedCodec<Integer>("@Height", Codec.INTEGER), (e, i) -> {
            e.height = i;
        }, e -> e.height)).addField(new KeyedCodec<Float>("@Scale", Codec.FLOAT), (e, f) -> {
            e.scale = f;
        }, e -> e.scale)).addField(new KeyedCodec<String>("SizeMode", Codec.STRING), (e, s) -> {
            e.sizeMode = s;
        }, e -> e.sizeMode)).addField(new KeyedCodec<String>("@BlockPattern", Codec.STRING), (e, s) -> {
            e.blockPattern = s;
        }, e -> e.blockPattern)).addField(new KeyedCodec<Boolean>("@FillSolid", Codec.BOOLEAN), (e, b) -> {
            e.fillSolid = b;
        }, e -> e.fillSolid)).addField(new KeyedCodec<Boolean>("@UseMaterials", Codec.BOOLEAN), (e, b) -> {
            e.useMaterials = b;
        }, e -> e.useMaterials)).addField(new KeyedCodec<Boolean>("@AutoDetectTextures", Codec.BOOLEAN), (e, b) -> {
            e.autoDetectTextures = b;
        }, e -> e.autoDetectTextures)).addField(new KeyedCodec<String>("@Origin", Codec.STRING), (e, s) -> {
            e.origin = s;
        }, e -> e.origin)).addField(new KeyedCodec<String>("@Rotation", Codec.STRING), (e, s) -> {
            e.rotation = s;
        }, e -> e.rotation)).addField(new KeyedCodec<String>("Import", Codec.STRING), (e, s) -> {
            e.doImport = "true".equalsIgnoreCase((String)s);
        }, e -> e.doImport != null && e.doImport != false ? "true" : null)).addField(new KeyedCodec<String>("Browse", Codec.STRING), (e, s) -> {
            e.browse = "true".equalsIgnoreCase((String)s);
        }, e -> e.browse != null && e.browse != false ? "true" : null)).addField(new KeyedCodec<String>("BrowserSelect", Codec.STRING), (e, s) -> {
            e.browserSelect = "true".equalsIgnoreCase((String)s);
        }, e -> e.browserSelect != null && e.browserSelect != false ? "true" : null)).addField(new KeyedCodec<String>("BrowserCancel", Codec.STRING), (e, s) -> {
            e.browserCancel = "true".equalsIgnoreCase((String)s);
        }, e -> e.browserCancel != null && e.browserCancel != false ? "true" : null)).addField(new KeyedCodec<String>("File", Codec.STRING), (e, s) -> {
            e.file = s;
        }, e -> e.file)).addField(new KeyedCodec<String>("@SearchQuery", Codec.STRING), (e, s) -> {
            e.searchQuery = s;
        }, e -> e.searchQuery)).addField(new KeyedCodec<String>("SearchResult", Codec.STRING), (e, s) -> {
            e.searchResult = s;
        }, e -> e.searchResult)).build();
        @Nullable
        private String objPath;
        @Nullable
        private Integer height;
        @Nullable
        private Float scale;
        @Nullable
        private String sizeMode;
        @Nullable
        private String blockPattern;
        @Nullable
        private Boolean fillSolid;
        @Nullable
        private Boolean useMaterials;
        @Nullable
        private Boolean autoDetectTextures;
        @Nullable
        private String origin;
        @Nullable
        private String rotation;
        @Nullable
        private Boolean doImport;
        @Nullable
        private Boolean browse;
        @Nullable
        private Boolean browserSelect;
        @Nullable
        private Boolean browserCancel;
        @Nullable
        private String file;
        @Nullable
        private String searchQuery;
        @Nullable
        private String searchResult;
    }

    public static enum Origin {
        BOTTOM_FRONT_LEFT,
        BOTTOM_CENTER,
        CENTER,
        TOP_CENTER;

    }

    public static enum MeshRotation {
        NONE,
        Z_UP_TO_Y_UP,
        X_UP_TO_Y_UP;

    }

    private record WeightedBlock(int blockId, int weight) {
    }
}

