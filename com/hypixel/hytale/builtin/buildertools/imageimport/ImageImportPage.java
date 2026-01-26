/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.imageimport;

import com.hypixel.hytale.builtin.buildertools.BlockColorIndex;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
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
import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.browser.FileBrowserConfig;
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
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;

public class ImageImportPage
extends InteractiveCustomUIPage<PageData> {
    private static final int DEFAULT_MAX_SIZE = 128;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 512;
    private static final String PASTE_TOOL_ID = "EditorTool_Paste";
    private static final Path IMPORTS_DIR = Paths.get("imports", "images");
    @Nonnull
    private String imagePath = "";
    private int maxDimension = 128;
    @Nonnull
    private String orientationStr = "wall_xy";
    @Nonnull
    private Orientation orientation = Orientation.VERTICAL_XY;
    @Nonnull
    private String originStr = "bottom_center";
    @Nonnull
    private Origin origin = Origin.BOTTOM_CENTER;
    @Nullable
    private String statusMessage = null;
    private boolean isError = false;
    private boolean isProcessing = false;
    private boolean showBrowser = false;
    @Nonnull
    private final ServerFileBrowser browser;

    public ImageImportPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PageData.CODEC);
        FileBrowserConfig config = FileBrowserConfig.builder().listElementId("#BrowserPage #FileList").searchInputId("#BrowserPage #SearchInput").currentPathId("#BrowserPage #CurrentPath").roots(List.of(new FileBrowserConfig.RootEntry("Imports", IMPORTS_DIR))).allowedExtensions(".png", ".jpg", ".jpeg", ".gif", ".bmp").enableRootSelector(false).enableSearch(true).enableDirectoryNav(true).maxResults(50).build();
        try {
            Files.createDirectories(IMPORTS_DIR, new FileAttribute[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.browser = new ServerFileBrowser(config);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/ImageImportPage.ui");
        commandBuilder.set("#ImagePath #Input.Value", this.imagePath);
        commandBuilder.set("#MaxSizeInput #Input.Value", this.maxDimension);
        ArrayList<DropdownEntryInfo> orientationEntries = new ArrayList<DropdownEntryInfo>();
        orientationEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.imageImport.orientation.wall_xy"), "wall_xy"));
        orientationEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.imageImport.orientation.wall_xz"), "wall_xz"));
        orientationEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.imageImport.orientation.floor"), "floor"));
        commandBuilder.set("#OrientationInput #Input.Entries", orientationEntries);
        commandBuilder.set("#OrientationInput #Input.Value", this.orientationStr);
        ArrayList<DropdownEntryInfo> originEntries = new ArrayList<DropdownEntryInfo>();
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.bottom_front_left"), "bottom_front_left"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.bottom_center"), "bottom_center"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.center"), "center"));
        originEntries.add(new DropdownEntryInfo(LocalizableString.fromMessageId("server.customUI.origin.top_center"), "top_center"));
        commandBuilder.set("#OriginInput #Input.Entries", originEntries);
        commandBuilder.set("#OriginInput #Input.Value", this.originStr);
        this.updateStatus(commandBuilder);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ImagePath #Input", EventData.of("@ImagePath", "#ImagePath #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#MaxSizeInput #Input", EventData.of("@MaxSize", "#MaxSizeInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#OrientationInput #Input", EventData.of("@Orientation", "#OrientationInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#OriginInput #Input", EventData.of("@Origin", "#OriginInput #Input.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ImportButton", EventData.of("Import", "true"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ImagePath #BrowseButton", EventData.of("Browse", "true"));
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
        boolean needsUpdate = false;
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
            if (!this.browser.getSelectedItems().isEmpty()) {
                String selectedPath = this.browser.getSelectedItems().iterator().next();
                this.imagePath = this.browser.getRoot().resolve(selectedPath).toString();
            }
            this.showBrowser = false;
            this.rebuild();
            return;
        }
        if (this.showBrowser && (data.file != null || data.searchQuery != null || data.searchResult != null)) {
            Path resolvedPath;
            boolean handled = false;
            if (data.searchQuery != null) {
                this.browser.setSearchQuery(data.searchQuery.trim().toLowerCase());
                handled = true;
            }
            if (data.file != null) {
                String fileName = data.file;
                if ("..".equals(fileName)) {
                    this.browser.navigateUp();
                    handled = true;
                } else {
                    Path targetPath = this.browser.resolveFromCurrent(fileName);
                    if (targetPath != null && Files.isDirectory(targetPath, new LinkOption[0])) {
                        this.browser.navigateTo(Paths.get(fileName, new String[0]));
                        handled = true;
                    } else if (targetPath != null && Files.isRegularFile(targetPath, new LinkOption[0])) {
                        this.imagePath = targetPath.toString();
                        this.showBrowser = false;
                        this.rebuild();
                        return;
                    }
                }
            }
            if (data.searchResult != null && (resolvedPath = this.browser.resolveSecure(data.searchResult)) != null && Files.isRegularFile(resolvedPath, new LinkOption[0])) {
                this.imagePath = resolvedPath.toString();
                this.showBrowser = false;
                this.rebuild();
                return;
            }
            if (handled) {
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.browser.buildFileList(commandBuilder, eventBuilder);
                this.browser.buildCurrentPath(commandBuilder);
                this.sendUpdate(commandBuilder, eventBuilder, false);
                return;
            }
        }
        if (data.imagePath != null) {
            this.imagePath = StringUtil.stripQuotes(data.imagePath.trim());
            this.statusMessage = null;
            needsUpdate = true;
        }
        if (data.maxSize != null) {
            this.maxDimension = Math.max(1, Math.min(512, data.maxSize));
            needsUpdate = true;
        }
        if (data.orientation != null) {
            this.orientation = switch (this.orientationStr = data.orientation.trim().toLowerCase()) {
                case "wall_xz", "xz", "vertical_xz" -> Orientation.VERTICAL_XZ;
                case "floor", "horizontal", "horizontal_xz" -> Orientation.HORIZONTAL_XZ;
                default -> Orientation.VERTICAL_XY;
            };
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
        if (data.doImport != null && data.doImport.booleanValue() && !this.isProcessing) {
            this.performImport(ref, store);
            return;
        }
        if (needsUpdate) {
            this.sendUpdate();
        }
    }

    private void performImport(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        Path normalizedImports;
        Path normalizedPath;
        if (this.imagePath.isEmpty()) {
            this.setError("Please enter a path to an image file");
            return;
        }
        Path path = Paths.get(this.imagePath, new String[0]);
        if (!SingleplayerModule.isOwner(this.playerRef) && !(normalizedPath = path.toAbsolutePath().normalize()).startsWith(normalizedImports = IMPORTS_DIR.toAbsolutePath().normalize())) {
            this.setError("Files must be in the server's imports/images directory");
            return;
        }
        if (!Files.exists(path, new LinkOption[0])) {
            this.setError("File not found: " + this.imagePath);
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
        String finalPath = this.imagePath;
        int finalMaxSize = this.maxDimension;
        Orientation finalOrientation = this.orientation;
        Origin finalOrigin = this.origin;
        BuilderToolsPlugin.addToQueue(playerComponent, playerRefComponent, (r, builderState, componentAccessor) -> {
            try {
                int sizeY;
                int sizeX;
                BlockColorIndex colorIndex;
                BufferedImage image = null;
                try {
                    image = ImageIO.read(Paths.get(finalPath, new String[0]).toFile());
                }
                catch (Exception exception) {
                    // empty catch block
                }
                if (image == null) {
                    this.setError("Unable to read image file (unsupported format or corrupted). Try PNG format.");
                    return;
                }
                int width = image.getWidth();
                int height = image.getHeight();
                float scale = 1.0f;
                if (width > finalMaxSize || height > finalMaxSize) {
                    scale = (float)finalMaxSize / (float)Math.max(width, height);
                    width = Math.round((float)width * scale);
                    height = Math.round((float)height * scale);
                }
                if ((colorIndex = BuilderToolsPlugin.get().getBlockColorIndex()).isEmpty()) {
                    this.setError("Block color index not initialized");
                    return;
                }
                int sizeZ = switch (finalOrientation.ordinal()) {
                    case 0 -> {
                        sizeX = width;
                        sizeY = height;
                        yield 1;
                    }
                    case 1 -> {
                        sizeX = width;
                        sizeY = 1;
                        yield height;
                    }
                    case 2 -> {
                        sizeX = width;
                        sizeY = 1;
                        yield height;
                    }
                    default -> {
                        sizeX = width;
                        sizeY = height;
                        yield 1;
                    }
                };
                int offsetX = 0;
                int offsetY = 0;
                int offsetZ = 0;
                switch (finalOrigin.ordinal()) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        offsetX = -sizeX / 2;
                        offsetZ = -sizeZ / 2;
                        break;
                    }
                    case 2: {
                        offsetX = -sizeX / 2;
                        offsetY = -sizeY / 2;
                        offsetZ = -sizeZ / 2;
                        break;
                    }
                    case 3: {
                        offsetX = -sizeX / 2;
                        offsetY = -sizeY;
                        offsetZ = -sizeZ / 2;
                    }
                }
                BlockSelection selection = new BlockSelection(width * height, 0);
                selection.setPosition(0, 0, 0);
                int blockCount = 0;
                float finalScale = scale;
                for (int imgY = 0; imgY < height; ++imgY) {
                    for (int imgX = 0; imgX < width; ++imgX) {
                        int blockY;
                        int blockX;
                        int blue;
                        int green;
                        int red;
                        int blockId;
                        int srcY;
                        int srcX = Math.min((int)((float)imgX / finalScale), image.getWidth() - 1);
                        int rgba = image.getRGB(srcX, srcY = Math.min((int)((float)imgY / finalScale), image.getHeight() - 1));
                        int alpha = rgba >> 24 & 0xFF;
                        if (alpha < 128 || (blockId = colorIndex.findClosestBlock(red = rgba >> 16 & 0xFF, green = rgba >> 8 & 0xFF, blue = rgba & 0xFF)) <= 0) continue;
                        selection.addBlockAtLocalPos(blockX + offsetX, blockY + offsetY, (switch (finalOrientation.ordinal()) {
                            case 0 -> {
                                blockX = imgX;
                                blockY = height - 1 - imgY;
                                yield 0;
                            }
                            case 1 -> {
                                blockX = imgX;
                                blockY = 0;
                                yield height - 1 - imgY;
                            }
                            case 2 -> {
                                blockX = imgX;
                                blockY = 0;
                                yield imgY;
                            }
                            default -> {
                                blockX = imgX;
                                blockY = height - 1 - imgY;
                                yield 0;
                            }
                        }) + offsetZ, blockId, 0, 0, 0);
                        ++blockCount;
                    }
                }
                selection.setSelectionArea(new Vector3i(offsetX, offsetY, offsetZ), new Vector3i(sizeX - 1 + offsetX, sizeY - 1 + offsetY, sizeZ - 1 + offsetZ));
                builderState.setSelection(selection);
                builderState.sendSelectionToClient();
                this.statusMessage = String.format("Success! %d blocks copied to clipboard (%dx%dx%d)", blockCount, sizeX, sizeY, sizeZ);
                this.isProcessing = false;
                playerRefComponent.sendMessage(Message.translation("server.builderTools.imageImport.success").param("count", blockCount).param("width", sizeX).param("height", sizeY).param("depth", sizeZ));
                playerComponent.getPageManager().setPage((Ref<EntityStore>)r, store, Page.None);
                this.switchToPasteTool(playerComponent, playerRefComponent);
            }
            catch (Exception e) {
                ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log("Image import error");
                this.setError("Error: " + e.getMessage());
            }
        });
    }

    private void switchToPasteTool(@Nonnull Player playerComponent, @Nonnull PlayerRef playerRef) {
        ItemStack itemStack;
        short slot;
        Inventory inventory = playerComponent.getInventory();
        ItemContainer hotbar = inventory.getHotbar();
        ItemContainer storage = inventory.getStorage();
        ItemContainer tools = inventory.getTools();
        short hotbarSize = hotbar.getCapacity();
        for (short slot2 = 0; slot2 < hotbarSize; slot2 = (short)(slot2 + 1)) {
            ItemStack itemStack2 = hotbar.getItemStack(slot2);
            if (itemStack2 == null || itemStack2.isEmpty() || !PASTE_TOOL_ID.equals(itemStack2.getItemId())) continue;
            inventory.setActiveHotbarSlot((byte)slot2);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)slot2));
            return;
        }
        short emptySlot = -1;
        for (slot = 0; slot < hotbarSize; slot = (short)(slot + 1)) {
            itemStack = hotbar.getItemStack(slot);
            if (itemStack != null && !itemStack.isEmpty()) continue;
            emptySlot = slot;
            break;
        }
        if (emptySlot == -1) {
            return;
        }
        for (slot = 0; slot < storage.getCapacity(); slot = (short)(slot + 1)) {
            itemStack = storage.getItemStack(slot);
            if (itemStack == null || itemStack.isEmpty() || !PASTE_TOOL_ID.equals(itemStack.getItemId())) continue;
            storage.moveItemStackFromSlotToSlot(slot, 1, hotbar, emptySlot);
            inventory.setActiveHotbarSlot((byte)emptySlot);
            playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
            return;
        }
        ItemStack pasteToolStack = null;
        for (short slot3 = 0; slot3 < tools.getCapacity(); slot3 = (short)(slot3 + 1)) {
            ItemStack itemStack3 = tools.getItemStack(slot3);
            if (itemStack3 == null || itemStack3.isEmpty() || !PASTE_TOOL_ID.equals(itemStack3.getItemId())) continue;
            pasteToolStack = itemStack3;
            break;
        }
        if (pasteToolStack == null) {
            return;
        }
        hotbar.setItemStackForSlot(emptySlot, new ItemStack(pasteToolStack.getItemId()));
        inventory.setActiveHotbarSlot((byte)emptySlot);
        playerRef.getPacketHandler().writeNoCache(new SetActiveSlot(-1, (byte)emptySlot));
    }

    public static class PageData {
        static final String KEY_IMAGE_PATH = "@ImagePath";
        static final String KEY_MAX_SIZE = "@MaxSize";
        static final String KEY_ORIENTATION = "@Orientation";
        static final String KEY_ORIGIN = "@Origin";
        static final String KEY_IMPORT = "Import";
        static final String KEY_BROWSE = "Browse";
        static final String KEY_BROWSER_SELECT = "BrowserSelect";
        static final String KEY_BROWSER_CANCEL = "BrowserCancel";
        public static final BuilderCodec<PageData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PageData.class, PageData::new).addField(new KeyedCodec<String>("@ImagePath", Codec.STRING), (entry, s) -> {
            entry.imagePath = s;
        }, entry -> entry.imagePath)).addField(new KeyedCodec<Integer>("@MaxSize", Codec.INTEGER), (entry, i) -> {
            entry.maxSize = i;
        }, entry -> entry.maxSize)).addField(new KeyedCodec<String>("@Orientation", Codec.STRING), (entry, s) -> {
            entry.orientation = s;
        }, entry -> entry.orientation)).addField(new KeyedCodec<String>("@Origin", Codec.STRING), (entry, s) -> {
            entry.origin = s;
        }, entry -> entry.origin)).addField(new KeyedCodec<String>("Import", Codec.STRING), (entry, s) -> {
            entry.doImport = "true".equalsIgnoreCase((String)s);
        }, entry -> entry.doImport != null && entry.doImport != false ? "true" : null)).addField(new KeyedCodec<String>("Browse", Codec.STRING), (entry, s) -> {
            entry.browse = "true".equalsIgnoreCase((String)s);
        }, entry -> entry.browse != null && entry.browse != false ? "true" : null)).addField(new KeyedCodec<String>("BrowserSelect", Codec.STRING), (entry, s) -> {
            entry.browserSelect = "true".equalsIgnoreCase((String)s);
        }, entry -> entry.browserSelect != null && entry.browserSelect != false ? "true" : null)).addField(new KeyedCodec<String>("BrowserCancel", Codec.STRING), (entry, s) -> {
            entry.browserCancel = "true".equalsIgnoreCase((String)s);
        }, entry -> entry.browserCancel != null && entry.browserCancel != false ? "true" : null)).addField(new KeyedCodec<String>("File", Codec.STRING), (entry, s) -> {
            entry.file = s;
        }, entry -> entry.file)).addField(new KeyedCodec<String>("@SearchQuery", Codec.STRING), (entry, s) -> {
            entry.searchQuery = s;
        }, entry -> entry.searchQuery)).addField(new KeyedCodec<String>("SearchResult", Codec.STRING), (entry, s) -> {
            entry.searchResult = s;
        }, entry -> entry.searchResult)).build();
        @Nullable
        private String imagePath;
        @Nullable
        private Integer maxSize;
        @Nullable
        private String orientation;
        @Nullable
        private String origin;
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

    public static enum Orientation {
        VERTICAL_XY,
        VERTICAL_XZ,
        HORIZONTAL_XZ;

    }

    public static enum Origin {
        BOTTOM_FRONT_LEFT,
        BOTTOM_CENTER,
        CENTER,
        TOP_CENTER;

    }
}

