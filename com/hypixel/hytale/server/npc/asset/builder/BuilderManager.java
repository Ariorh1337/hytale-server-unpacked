/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.map.CaseInsensitiveHashStrategy;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.monitor.AssetMonitor;
import com.hypixel.hytale.server.core.asset.monitor.AssetMonitorHandler;
import com.hypixel.hytale.server.core.asset.monitor.EventKind;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.server.npc.AllNPCsLoadedEvent;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptor;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import com.hypixel.hytale.server.npc.asset.builder.BuilderParameters;
import com.hypixel.hytale.server.npc.asset.builder.BuilderValidationHelper;
import com.hypixel.hytale.server.npc.asset.builder.FeatureEvaluatorHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionContextHelper;
import com.hypixel.hytale.server.npc.asset.builder.InstructionType;
import com.hypixel.hytale.server.npc.asset.builder.InternalReferenceResolver;
import com.hypixel.hytale.server.npc.asset.builder.SpawnableWithModelBuilder;
import com.hypixel.hytale.server.npc.asset.builder.StateMappingHelper;
import com.hypixel.hytale.server.npc.asset.builder.providerevaluators.ProviderEvaluatorTypeRegistry;
import com.hypixel.hytale.server.npc.asset.builder.validators.ValidatorTypeRegistry;
import com.hypixel.hytale.server.npc.decisionmaker.core.Evaluator;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import com.hypixel.hytale.server.npc.util.expression.StdLib;
import com.hypixel.hytale.server.npc.validators.NPCLoadTimeValidationHelper;
import com.hypixel.hytale.server.spawning.LoadedNPCEvent;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Period;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuilderManager {
    public static final String CONTENT_KEY = "Content";
    private static final String CLASS_KEY = "Class";
    private static final String TEST_TYPE_KEY = "TestType";
    private static final String FAIL_REASON_KEY = "FailReason";
    private static final String PLAYER_GROUP_TAG = "$player";
    private static final String SELF_GROUP_TAG = "$self";
    private static int playerGroupID;
    private static int selfGroupID;
    private final Int2ObjectConcurrentHashMap<BuilderInfo> builderCache = new Int2ObjectConcurrentHashMap();
    private final String elementTypeName = "NPC";
    private final String defaultFileType = NPCPlugin.FACTORY_CLASS_ROLE;
    private boolean autoReload;
    private final Map<Class<?>, BuilderFactory<?>> factoryMap = new HashMap();
    private final Map<String, Class<?>> categoryNames = new HashMap();
    @Nonnull
    private final Object2IntMap<String> nameToIndexMap;
    private final AtomicInteger nextIndex = new AtomicInteger();
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock();
    private boolean setup;
    @Nullable
    public static BuilderManager SCHEMA_BUILDER_MANAGER;

    public BuilderManager() {
        this.nameToIndexMap = new Object2IntOpenCustomHashMap<String>(CaseInsensitiveHashStrategy.getInstance());
        this.nameToIndexMap.defaultReturnValue(Integer.MIN_VALUE);
        playerGroupID = this.getOrCreateIndex(PLAYER_GROUP_TAG);
        selfGroupID = this.getOrCreateIndex(SELF_GROUP_TAG);
    }

    public <T> void registerFactory(@Nonnull BuilderFactory<T> factory) {
        if (factory == null) {
            throw new IllegalArgumentException();
        }
        Class<T> clazz = factory.getCategory();
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        if (this.factoryMap.containsKey(clazz)) {
            throw new IllegalArgumentException(factory.getClass().getSimpleName());
        }
        this.factoryMap.put(clazz, factory);
    }

    public void addCategory(String name, Class<?> clazz) {
        this.categoryNames.put(name, clazz);
    }

    public String getCategoryName(@Nonnull Class<?> factoryClass) {
        for (Map.Entry<String, Class<?>> stringClassEntry : this.categoryNames.entrySet()) {
            if (stringClassEntry.getValue() != factoryClass) continue;
            return stringClassEntry.getKey();
        }
        return factoryClass.getSimpleName();
    }

    public int getIndex(@Nullable String name) {
        if (name == null || name.isEmpty()) {
            return Integer.MIN_VALUE;
        }
        this.indexLock.readLock().lock();
        try {
            int n = this.nameToIndexMap.getInt(name);
            return n;
        }
        finally {
            this.indexLock.readLock().unlock();
        }
    }

    public void setAutoReload(boolean autoReload) {
        this.autoReload = autoReload;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public String lookupName(int index) {
        if (index < 0) {
            return null;
        }
        BuilderInfo info = this.builderCache.get(index);
        if (info != null) {
            return info.getKeyName();
        }
        this.indexLock.readLock().lock();
        try {
            ObjectIterator<Object2IntMap.Entry<String>> iterator = Object2IntMaps.fastIterator(this.nameToIndexMap);
            while (iterator.hasNext()) {
                Object2IntMap.Entry entry = (Object2IntMap.Entry)iterator.next();
                if (entry.getIntValue() != index) continue;
                String string = (String)entry.getKey();
                return string;
            }
        }
        finally {
            this.indexLock.readLock().unlock();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getOrCreateIndex(String name) {
        this.indexLock.writeLock().lock();
        try {
            int index = this.nameToIndexMap.getInt(name);
            if (index >= 0) {
                int n = index;
                return n;
            }
            index = this.nextIndex.getAndIncrement();
            this.nameToIndexMap.put(name, index);
            int n = index;
            return n;
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }

    @Nullable
    public BuilderInfo tryGetBuilderInfo(int builderIndex) {
        return builderIndex < 0 ? null : this.builderCache.get(builderIndex);
    }

    public void unloadBuilders(AssetPack pack) {
        Path path = pack.getRoot().resolve(NPCPlugin.ROLE_ASSETS_PATH);
        AssetMonitor assetMonitor = AssetModule.get().getAssetMonitor();
        if (assetMonitor != null) {
            assetMonitor.removeMonitorDirectoryFiles(path, pack);
        }
        if (!Files.isDirectory(path, new LinkOption[0])) {
            return;
        }
        try {
            Files.walkFileTree(path, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                @Nonnull
                public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                    if (BuilderManager.isJsonFile(file) && !BuilderManager.isIgnoredFile(file)) {
                        String builderName = BuilderManager.builderNameFromPath(file);
                        BuilderManager.this.removeBuilder(builderName);
                        NPCPlugin.get().getLogger().at(Level.INFO).log("Deleted %s builder %s", (Object)"NPC", (Object)builderName);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public boolean loadBuilders(@Nonnull AssetPack pack, final boolean includeTests) {
        Path path = pack.getRoot().resolve(NPCPlugin.ROLE_ASSETS_PATH);
        boolean valid = true;
        NPCPlugin.get().getLogger().at(Level.INFO).log("Starting to load NPC builders!");
        final Object2IntOpenHashMap typeCounter = new Object2IntOpenHashMap();
        try {
            AssetMonitor assetMonitor = AssetModule.get().getAssetMonitor();
            if (assetMonitor != null && !pack.isImmutable() && Files.isDirectory(path, new LinkOption[0])) {
                assetMonitor.removeMonitorDirectoryFiles(path, pack);
                assetMonitor.monitorDirectoryFiles(path, new BuilderAssetMonitorHandler(pack, includeTests));
            }
            final ObjectArrayList<String> errors = new ObjectArrayList<String>();
            if (Files.isDirectory(path, new LinkOption[0])) {
                Files.walkFileTree(path, FileUtil.DEFAULT_WALK_TREE_OPTIONS_SET, Integer.MAX_VALUE, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(this){
                    final /* synthetic */ BuilderManager this$0;
                    {
                        this.this$0 = this$0;
                    }

                    @Override
                    @Nonnull
                    public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) {
                        if (BuilderManager.isJsonFile(file) && !BuilderManager.isIgnoredFile(file)) {
                            this.this$0.loadFile(file, errors, typeCounter, includeTests, false);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            Int2ObjectOpenHashMap<BuilderInfo> loadedBuilders = new Int2ObjectOpenHashMap<BuilderInfo>();
            for (BuilderInfo builderInfo : this.builderCache.values()) {
                try {
                    if (this.validateBuilder(builderInfo)) {
                        loadedBuilders.put(builderInfo.getIndex(), builderInfo);
                        continue;
                    }
                    valid = false;
                }
                catch (IllegalArgumentException | IllegalStateException e) {
                    valid = false;
                    errors.add(String.format("%s: %s", builderInfo.getKeyName(), e.getMessage()));
                }
            }
            this.setup = true;
            this.validateAllLoadedBuilders(loadedBuilders, false, errors);
            if (!errors.isEmpty()) {
                valid = false;
                for (String error : errors) {
                    NPCPlugin.get().getLogger().at(Level.SEVERE).log("FAIL: " + error);
                }
            }
            errors.clear();
            this.onAllBuildersLoaded(loadedBuilders);
        }
        catch (IOException e) {
            throw new SkipSentryException(new RuntimeException(e));
        }
        StringBuilder output = new StringBuilder();
        output.append("Loaded ").append(this.builderCache.size()).append(" ").append("NPC").append(" configurations");
        for (Object2IntMap.Entry entry : typeCounter.object2IntEntrySet()) {
            output.append(", ").append((String)entry.getKey()).append(": ").append(entry.getIntValue());
        }
        NPCPlugin.get().getLogger().at(Level.INFO).log(output.toString());
        return valid;
    }

    private void finishLoadingBuilders(@Nonnull Int2ObjectOpenHashMap<BuilderInfo> loadedBuilders, @Nonnull List<String> errors) {
        this.onAllBuildersLoaded(loadedBuilders);
        this.validateAllLoadedBuilders(loadedBuilders, true, errors);
        if (!errors.isEmpty()) {
            for (String error : errors) {
                NPCPlugin.get().getLogger().at(Level.SEVERE).log(error);
            }
        }
        errors.clear();
    }

    public void assetEditorLoadFile(@Nonnull Path fileName) {
        HashSet<String> failedBuilderTexts = new HashSet<String>();
        ObjectArrayList<String> errors = new ObjectArrayList<String>();
        Int2ObjectOpenHashMap<BuilderInfo> loadedBuilders = new Int2ObjectOpenHashMap<BuilderInfo>();
        HashSet<String> loadedBuilderNames = new HashSet<String>();
        try {
            int builderIndex = this.loadFile(fileName, errors, null, true, true);
            if (builderIndex < 0) {
                return;
            }
            String name = BuilderManager.builderNameFromPath(fileName);
            NPCPlugin.get().getLogger().at(Level.INFO).log("Reloaded NPC builder " + name);
            loadedBuilderNames.add(name);
            for (BuilderInfo builderInfo : this.builderCache.values()) {
                if (!this.isDependant(builderInfo.getBuilder(), builderInfo.getIndex(), builderIndex)) continue;
                builderInfo.setNeedsValidation();
            }
            if (this.autoReload) {
                this.reloadDependants(builderIndex);
            }
            BuilderInfo builder = this.builderCache.get(builderIndex);
            BuilderManager.onBuilderReloaded(builder);
            loadedBuilders.put(builderIndex, builder);
        }
        catch (Throwable e) {
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Failed to reload %s config %s: %s", "NPC", fileName, e.getMessage());
            failedBuilderTexts.add(BuilderManager.builderNameFromPath(fileName) + ": " + e.getMessage());
        }
        BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.reloadAssets").param("class", "NPC"), loadedBuilderNames);
        BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.loadFailed").param("class", "NPC"), failedBuilderTexts);
        this.finishLoadingBuilders(loadedBuilders, errors);
    }

    public void assetEditorRemoveFile(@Nonnull Path filePath) {
        String builderName = BuilderManager.builderNameFromPath(filePath);
        this.removeBuilder(builderName);
        NPCPlugin.get().getLogger().at(Level.INFO).log("Deleted %s builder %s", (Object)"NPC", (Object)builderName);
        BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.removedAssets").param("class", "NPC"), Set.of(builderName));
        ObjectArrayList<String> errors = new ObjectArrayList<String>();
        this.finishLoadingBuilders(new Int2ObjectOpenHashMap<BuilderInfo>(), errors);
    }

    public int loadFile(@Nonnull Path fileName, boolean reloading, @Nonnull List<String> errors) {
        return this.loadFile(fileName, errors, null, false, reloading);
    }

    public int loadFile(@Nonnull Path fileName, @Nonnull List<String> errors, @Nullable Object2IntMap<String> typeCounter, boolean includeTests, boolean reloading) {
        Builder builder;
        Class<?> category;
        JsonObject data;
        int errorCount = errors.size();
        try (BufferedReader fileReader = Files.newBufferedReader(fileName);
             JsonReader reader = new JsonReader(fileReader);){
            data = JsonParser.parseReader(reader).getAsJsonObject();
        }
        catch (Exception e) {
            errors.add(String.valueOf(fileName) + ": Failed to load NPC builder: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
        String categoryName = this.defaultFileType;
        JsonObject content = data;
        TestType testType = null;
        JsonElement testTypeElement = data.get(TEST_TYPE_KEY);
        if (testTypeElement != null) {
            try {
                testType = Enum.valueOf(TestType.class, testTypeElement.getAsString().toUpperCase());
            }
            catch (Exception e) {
                errors.add(String.valueOf(fileName) + ": " + e.getMessage());
            }
            if (!includeTests) {
                return Integer.MIN_VALUE;
            }
        }
        String keyName = BuilderManager.builderNameFromPath(fileName);
        String componentInterface = null;
        StateMappingHelper stateHelper = new StateMappingHelper();
        JsonElement classData = data.get(CLASS_KEY);
        if (classData != null) {
            categoryName = classData.getAsString();
            stateHelper.readComponentDefaultLocalState(data);
            JsonElement interfaceData = data.get("Interface");
            if (interfaceData != null) {
                componentInterface = interfaceData.getAsString();
            }
        }
        if ((category = this.categoryNames.get(categoryName)) == null) {
            errors.add(String.valueOf(fileName) + ": Failed to load NPC builder, unknown class " + categoryName);
            return Integer.MIN_VALUE;
        }
        if (typeCounter != null) {
            JsonElement type = data.get("Type");
            String typeString = testType == null ? (type != null ? type.getAsString() : categoryName) : "Test";
            typeCounter.mergeInt(typeString, 1, Integer::sum);
        }
        BuilderFactory factory = this.getFactory(category);
        try {
            builder = factory.createBuilder(content);
        }
        catch (Exception e) {
            errors.add(String.valueOf(fileName) + ": " + e.getMessage());
            return Integer.MIN_VALUE;
        }
        String fileNameString = fileName.toString();
        this.checkIfDeprecated(builder, factory, content, fileNameString, categoryName);
        builder.setLabel(categoryName + "|" + factory.getKeyName(content));
        builder.ignoreAttribute(TEST_TYPE_KEY);
        if (testType == TestType.FAILING) {
            builder.ignoreAttribute(FAIL_REASON_KEY);
        }
        builder.ignoreAttribute("Parameters");
        BuilderParameters builderParameters = new BuilderParameters(StdLib.getInstance(), fileNameString, componentInterface);
        try {
            builderParameters.readJSON(data, stateHelper);
        }
        catch (Exception e) {
            errors.add(fileNameString + ": Failed to load NPC builder, 'Parameters' section invalid: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
        if (classData != null) {
            builder.ignoreAttribute(CLASS_KEY);
            builder.ignoreAttribute("Interface");
            builder.ignoreAttribute("DefaultState");
            builder.ignoreAttribute("ResetOnStateChange");
        }
        builderParameters.addParametersToScope();
        InternalReferenceResolver internalReferenceResolver = new InternalReferenceResolver();
        AssetExtraInfo.Data extraInfoData = new AssetExtraInfo.Data(null, keyName, null);
        AssetExtraInfo extraInfo = new AssetExtraInfo(extraInfoData);
        ObjectArrayList evaluators = new ObjectArrayList();
        BuilderValidationHelper validationHelper = new BuilderValidationHelper(fileNameString, new FeatureEvaluatorHelper(builder.canRequireFeature()), internalReferenceResolver, stateHelper, new InstructionContextHelper(InstructionType.Component), extraInfo, evaluators, errors);
        try {
            builder.readConfig(null, content, this, builderParameters, validationHelper);
        }
        catch (Exception e) {
            errors.add(fileNameString + ": Failed to load NPC: " + e.getMessage());
            return Integer.MIN_VALUE;
        }
        internalReferenceResolver.validateInternalReferences(fileNameString, errors);
        extraInfoData.loadContainedAssets(reloading);
        for (Evaluator evaluator : evaluators) {
            evaluator.initialise();
        }
        internalReferenceResolver.optimise();
        builderParameters.disposeCompileContext();
        stateHelper.validate(fileNameString, errors);
        stateHelper.optimise();
        BuilderInfo entry = this.tryGetBuilderInfo(this.getIndex(keyName));
        if (entry != null && entry.getPath() != null) {
            try {
                if (!Files.isSameFile(fileName, entry.getPath())) {
                    NPCPlugin.get().getLogger().at(Level.WARNING).log("Replacing asset '%s' of file '%s' with other file '%s'", keyName, entry.getPath(), fileName);
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (testType == TestType.FAILING) {
            JsonElement jsonElement = data.get(FAIL_REASON_KEY);
            if (jsonElement == null) {
                errors.add(String.valueOf(fileName) + ": Missing fail reason!");
                return Integer.MIN_VALUE;
            }
            if (errors.size() == errorCount) {
                errors.add(String.valueOf(fileName) + ": Should have failed validation: " + jsonElement.getAsString());
                return Integer.MIN_VALUE;
            }
            if (errors.size() - errorCount > 1) {
                errors.add(String.valueOf(fileName) + ": Should have failed validation: " + jsonElement.getAsString() + ", but additional errors were also detected.");
                return Integer.MIN_VALUE;
            }
            String error = errors.removeLast();
            if (!error.contains(jsonElement.getAsString())) {
                errors.add(String.valueOf(fileName) + ": Should have failed validation: " + jsonElement.getAsString() + ", but was instead: " + error);
                return Integer.MIN_VALUE;
            }
            if (NPCPlugin.get().isLogFailingTestErrors()) {
                NPCPlugin.get().getLogger().at(Level.WARNING).log("Expected test failure: " + error);
            }
            return Integer.MIN_VALUE;
        }
        return errors.size() > errorCount ? Integer.MIN_VALUE : this.cacheBuilder(keyName, builder, fileName);
    }

    public boolean validateBuilder(@Nonnull BuilderInfo builderInfo) {
        if (builderInfo.isValidated()) {
            return builderInfo.isValid();
        }
        if (!builderInfo.canBeValidated()) {
            return false;
        }
        Builder<?> builder = builderInfo.getBuilder();
        if (builder.getDependencies().isEmpty() && !builder.hasDynamicDependencies()) {
            return builderInfo.setValidated(true);
        }
        return this.validateBuilder(builderInfo, new IntOpenHashSet(), new IntArrayList());
    }

    @Nonnull
    public <T> BuilderFactory<T> getFactory(@Nonnull Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("No factory class supplied!");
        }
        BuilderFactory<?> factory = this.factoryMap.get(clazz);
        if (factory == null) {
            throw new NullPointerException(String.format("Factory for type '%s' is not registered!", clazz.getSimpleName()));
        }
        if (factory.getCategory() != clazz) {
            throw new IllegalArgumentException(String.format("Factory class mismatch! Expected %s, was %s", clazz.getSimpleName(), factory.getCategory().getSimpleName()));
        }
        return factory;
    }

    @Nonnull
    public BuilderInfo getCachedBuilderInfo(int index, @Nonnull Class<?> classType) {
        if (index < 0) {
            throw new SkipSentryException(new IllegalArgumentException("Builder asset can't have negative index " + index));
        }
        BuilderInfo builderInfo = this.tryGetCachedBuilderInfo(index, classType);
        if (builderInfo == null) {
            throw new SkipSentryException(new IllegalArgumentException(String.format("Asset '%s' (%s) is not available", this.lookupName(index), index)));
        }
        return builderInfo;
    }

    @Nullable
    public <T> Builder<T> tryGetCachedValidBuilder(int index, @Nonnull Class<?> classType) {
        BuilderInfo builderInfo = this.tryGetCachedBuilderInfo(index, classType);
        return builderInfo != null && builderInfo.isValid() ? builderInfo.getBuilder() : null;
    }

    public <T> Builder<T> getCachedBuilder(int index, @Nonnull Class<?> classType) {
        BuilderInfo builderInfo = this.getCachedBuilderInfo(index, classType);
        return builderInfo.getBuilder();
    }

    public boolean isEmpty() {
        return this.builderCache.isEmpty();
    }

    @Nonnull
    public Int2ObjectMap<BuilderInfo> getAllBuilders() {
        Int2ObjectOpenHashMap<BuilderInfo> builders = new Int2ObjectOpenHashMap<BuilderInfo>();
        for (BuilderInfo builder : this.builderCache.values()) {
            builders.put(builder.getIndex(), builder);
        }
        return builders;
    }

    public <T extends Collection<?>> T collectMatchingBuilders(T collection, @Nonnull Predicate<BuilderInfo> filter, @Nonnull BiConsumer<BuilderInfo, T> consumer) {
        for (BuilderInfo builderInfo : this.builderCache.values()) {
            if (!filter.test(builderInfo)) continue;
            consumer.accept(builderInfo, (BuilderInfo)((Object)collection));
        }
        return collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Object2IntMap<String> getNameToIndexMap() {
        Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<String>();
        if (!this.setup) {
            return Object2IntMaps.unmodifiable(map);
        }
        this.indexLock.readLock().lock();
        try {
            ObjectIterator<Object2IntMap.Entry<String>> iterator = Object2IntMaps.fastIterator(this.nameToIndexMap);
            while (iterator.hasNext()) {
                Object2IntMap.Entry next = (Object2IntMap.Entry)iterator.next();
                map.put((String)next.getKey(), next.getIntValue());
            }
        }
        finally {
            this.indexLock.readLock().unlock();
        }
        return Object2IntMaps.unmodifiable(map);
    }

    @Nullable
    public <T> BuilderInfo findMatchingBuilder(@Nonnull BiPredicate<BuilderInfo, T> filter, T t) {
        for (BuilderInfo builderInfo : this.builderCache.values()) {
            if (!filter.test(builderInfo, (BuilderInfo)t)) continue;
            return builderInfo;
        }
        return null;
    }

    @Nullable
    public BuilderInfo getBuilderInfo(Builder<?> builder) {
        return this.findMatchingBuilder((builderInfo, b) -> builderInfo.getBuilder() == b, builder);
    }

    public List<String> getTemplateNames() {
        return this.collectMatchingBuilders(new ObjectArrayList(), builderInfo -> true, (builderInfo, strings) -> strings.add(builderInfo.getKeyName()));
    }

    public void forceValidation(int builderIndex) {
        BuilderInfo builderInfo = this.tryGetBuilderInfo(builderIndex);
        if (builderInfo == null) {
            return;
        }
        IntSet dependencies = this.computeAllDependencies(builderInfo.getBuilder(), builderInfo.getIndex());
        builderInfo.setForceValidation();
        IntIterator i = dependencies.iterator();
        while (i.hasNext()) {
            builderInfo = this.tryGetBuilderInfo(i.nextInt());
            if (builderInfo == null) continue;
            builderInfo.setForceValidation();
        }
    }

    public void checkIfDeprecated(@Nonnull Builder<?> builder, @Nonnull BuilderFactory<?> builderFactory, @Nonnull JsonElement element, String fileName, String context) {
        if (!builder.isDeprecated()) {
            return;
        }
        NPCPlugin.get().getLogger().at(Level.WARNING).log("Builder %s of type %s is deprecated and should be replaced in %s: %s", builderFactory.getKeyName(element), this.getCategoryName(builderFactory.getCategory()), context, fileName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nonnull
    public Schema generateSchema(@Nonnull SchemaContext context) {
        try {
            SCHEMA_BUILDER_MANAGER = this;
            BuilderFactory<?> roleFactory = this.factoryMap.get(Role.class);
            Schema schema = roleFactory.toSchema(context, true);
            ObjectSchema check = new ObjectSchema();
            check.setRequired(CLASS_KEY, "Type");
            StringSchema keys = new StringSchema();
            keys.setEnum((String[])this.categoryNames.keySet().toArray(String[]::new));
            check.setProperties(Map.of(CLASS_KEY, keys));
            check.setProperties(Map.of("Type", StringSchema.constant("Component")));
            Schema dynamicComponent = new Schema();
            dynamicComponent.setIf(check);
            Schema[] subSchemas = new Schema[this.categoryNames.size()];
            int index = 0;
            for (Map.Entry<String, Class<?>> cats : this.categoryNames.entrySet()) {
                BuilderFactory factory = this.getFactory(cats.getValue());
                Schema s = factory.toSchema(context, true);
                Schema cond = new Schema();
                ObjectSchema classCheck = new ObjectSchema();
                classCheck.setProperties(Map.of(CLASS_KEY, StringSchema.constant(cats.getKey())));
                cond.setIf(classCheck);
                cond.setThen(s);
                cond.setElse(false);
                subSchemas[index++] = cond;
            }
            dynamicComponent.setThen(Schema.anyOf(subSchemas));
            dynamicComponent.setElse(false);
            schema.getThen().setAnyOf(ArrayUtil.append(schema.getThen().getAnyOf(), dynamicComponent));
            Schema schema2 = schema;
            return schema2;
        }
        finally {
            SCHEMA_BUILDER_MANAGER = null;
        }
    }

    @Nonnull
    public List<BuilderDescriptor> generateDescriptors() {
        ObjectArrayList<BuilderDescriptor> builderDescriptors = new ObjectArrayList<BuilderDescriptor>();
        for (BuilderFactory<?> builderFactory : this.factoryMap.values()) {
            String categoryName = this.getCategoryName(builderFactory.getCategory());
            Builder<?> defaultBuilder = builderFactory.tryCreateDefaultBuilder();
            if (defaultBuilder != null) {
                try {
                    builderDescriptors.add(defaultBuilder.getDescriptor(categoryName, categoryName, this));
                }
                catch (IllegalStateException | NullPointerException e) {
                    NPCPlugin.get().getLogger().at(Level.SEVERE).log("Failed to build descriptor for %s %s: %s", categoryName, categoryName, e.getMessage());
                }
            }
            for (String builderName : builderFactory.getBuilderNames()) {
                String name;
                Builder<?> builder = builderFactory.createBuilder(builderName);
                Objects.requireNonNull(builder, "Unable to create builder for descriptor generation");
                String string = name = builderName == null || builderName.isEmpty() ? categoryName : builderName;
                if (name.equals("Component")) continue;
                try {
                    builderDescriptors.add(builder.getDescriptor(name, categoryName, this));
                }
                catch (IllegalStateException | NullPointerException e) {
                    NPCPlugin.get().getLogger().at(Level.SEVERE).log("Failed to build descriptor for %s %s: %s", categoryName, name, e.getMessage());
                }
            }
        }
        return builderDescriptors;
    }

    public static void saveDescriptors(List<BuilderDescriptor> builderDescriptors, @Nonnull Path fileName) {
        try (BufferedWriter fileWriter = Files.newBufferedWriter(fileName, new OpenOption[0]);){
            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
            gsonBuilder.registerTypeAdapter((Type)((Object)Duration.class), (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()));
            gsonBuilder.registerTypeAdapter((Type)((Object)Period.class), (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()));
            ValidatorTypeRegistry.registerTypes(gsonBuilder);
            ProviderEvaluatorTypeRegistry.registerTypes(gsonBuilder);
            Gson gson = gsonBuilder.create();
            gson.toJson(builderDescriptors, (Appendable)fileWriter);
        }
        catch (IOException e) {
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Failed to write builder descriptors to %s", fileName);
        }
    }

    @Nullable
    public Builder<Role> tryGetCachedValidRole(int builderIndex) {
        return this.tryGetCachedValidBuilder(builderIndex, Role.class);
    }

    public void validateAllLoadedBuilders(@Nonnull Int2ObjectMap<BuilderInfo> loadedBuilders, boolean validateDependents, @Nonnull List<String> errors) {
        NPCPlugin.get().getLogger().at(Level.INFO).log("Validating loaded NPC configurations...");
        BuilderManager.validateAllSpawnableNPCs(loadedBuilders, errors);
        if (validateDependents) {
            Int2ObjectOpenHashMap<BuilderInfo> dependents = new Int2ObjectOpenHashMap<BuilderInfo>();
            loadedBuilders.forEach((index, builderInfo) -> {
                for (BuilderInfo info : this.builderCache.values()) {
                    boolean isDependent;
                    int builderIndex = info.getIndex();
                    Builder<?> builder = info.getBuilder();
                    try {
                        isDependent = this.isDependant(builder, builderIndex, (int)index);
                    }
                    catch (SkipSentryException | IllegalArgumentException | IllegalStateException e) {
                        NPCPlugin.get().getLogger().at(Level.WARNING).log("Could not check if builder %s was dependent: %s", (Object)this.lookupName(info.getIndex()), (Object)e.getMessage());
                        continue;
                    }
                    if (!builder.isSpawnable() || !isDependent) continue;
                    dependents.put(builderIndex, info);
                }
            });
            BuilderManager.validateAllSpawnableNPCs(dependents, errors);
        }
        NPCPlugin.get().getLogger().at(Level.INFO).log("Validation complete.");
    }

    public void onAllBuildersLoaded(@Nonnull Int2ObjectMap<BuilderInfo> loadedBuilders) {
        if (!loadedBuilders.isEmpty()) {
            IEventDispatcher<AllNPCsLoadedEvent, AllNPCsLoadedEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(AllNPCsLoadedEvent.class);
            if (dispatcher.hasListener()) {
                dispatcher.dispatch(new AllNPCsLoadedEvent(this.getAllBuilders(), loadedBuilders));
            }
            this.getAllBuilders().forEach((index, builderInfo) -> {
                if (builderInfo.needsValidation()) {
                    NPCPlugin.get().testAndValidateRole((BuilderInfo)builderInfo);
                }
            });
        }
    }

    public static void onBuilderReloaded(@Nonnull BuilderInfo builderInfo) {
        builderInfo.getBuilder().clearDynamicDependencies();
        NPCPlugin.reloadNPCsWithRole(builderInfo.getIndex());
    }

    public static int getPlayerGroupID() {
        return playerGroupID;
    }

    public static int getSelfGroupID() {
        return selfGroupID;
    }

    protected static void onBuilderAdded(@Nonnull BuilderInfo builderInfo) {
        IEventDispatcher<LoadedNPCEvent, LoadedNPCEvent> dispatcher;
        if (builderInfo.getBuilder().isSpawnable() && (dispatcher = HytaleServer.get().getEventBus().dispatchFor(LoadedNPCEvent.class)).hasListener()) {
            dispatcher.dispatch(new LoadedNPCEvent(builderInfo));
        }
    }

    protected boolean isDependant(@Nonnull Builder<?> builder, int builderIndex, int dependencyIndex) {
        if (builderIndex == dependencyIndex) {
            return true;
        }
        return this.computeAllDependencies(builder, builderIndex).contains(dependencyIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int cacheBuilder(String name, Builder<?> builder, Path path) {
        int index;
        this.indexLock.writeLock().lock();
        try {
            index = this.nameToIndexMap.getInt(name);
            if (index >= 0) {
                this.removeBuilder(index);
            } else {
                index = this.nextIndex.getAndIncrement();
                this.nameToIndexMap.put(name, index);
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
        BuilderInfo builderInfo = new BuilderInfo(index, name, builder, path);
        this.builderCache.put(index, builderInfo);
        BuilderManager.onBuilderAdded(builderInfo);
        return index;
    }

    private void removeBuilder(int index) {
        BuilderInfo builder = this.builderCache.remove(index);
        if (builder != null) {
            builder.setRemoved();
        }
    }

    private void removeBuilder(String name) {
        int index = this.getIndex(name);
        if (index >= 0) {
            this.removeBuilder(index);
        }
    }

    @Nullable
    private Builder<?> tryGetCachedBuilder(int index) {
        BuilderInfo entry = this.tryGetBuilderInfo(index);
        return entry == null ? null : entry.getBuilder();
    }

    @Nullable
    private BuilderInfo tryGetCachedBuilderInfo(int index, @Nonnull Class<?> classType) {
        BuilderInfo entry = this.tryGetBuilderInfo(index);
        if (entry == null) {
            return null;
        }
        Builder<?> cachedBuilder = entry.getBuilder();
        if (cachedBuilder.category() != classType) {
            throw new IllegalArgumentException(String.format("Asset '%s'(%s) is different type. Is '%s' but should be '%s'", this.lookupName(index), index, cachedBuilder.category().getName(), classType.getName()));
        }
        return entry;
    }

    private static void validateAllSpawnableNPCs(@Nonnull Int2ObjectMap<BuilderInfo> builders, @Nonnull List<String> errors) {
        builders.forEach((index, builderInfo) -> {
            Builder<?> builderInstance;
            String modelName;
            Builder<?> builder = builderInfo.getBuilder();
            if (!builder.isSpawnable() || !(builder instanceof SpawnableWithModelBuilder)) {
                return;
            }
            SpawnableWithModelBuilder spawnableBuilder = (SpawnableWithModelBuilder)builder;
            ExecutionContext context = new ExecutionContext(builder.getBuilderParameters().createScope());
            String fileName = builderInfo.getPath().toString();
            try {
                modelName = spawnableBuilder.getSpawnModelName(context, spawnableBuilder.createModifierScope(context));
            }
            catch (SkipSentryException | IllegalStateException e) {
                errors.add(String.format("%s: %s", fileName, e.getMessage()));
                builderInfo.setValidated(false);
                return;
            }
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelName);
            if (modelAsset == null) {
                errors.add(String.format("%s: Model %s does not exist.", fileName, modelName));
                builderInfo.setValidated(false);
                return;
            }
            Model model = Model.createScaledModel(modelAsset, modelAsset.getMaxScale());
            NPCLoadTimeValidationHelper validationHelper = new NPCLoadTimeValidationHelper(fileName, model, !(builderInstance = builderInfo.getBuilder()).isSpawnable());
            if (!(builderInstance.validate(fileName, validationHelper, context, context.getScope(), errors) && validationHelper.validateMotionControllers(errors) && validationHelper.getValueStoreValidator().validate(errors))) {
                builderInfo.setValidated(false);
            }
        });
    }

    private static void sendReloadNotification(Message message, @Nonnull Set<String> builders) {
        if (builders.isEmpty()) {
            return;
        }
        NotificationUtil.sendNotificationToUniverse(message, Message.raw(builders.toString()), NotificationStyle.Warning);
    }

    private static boolean isIgnoredFile(@Nonnull Path path) {
        return !path.getFileName().toString().isEmpty() && path.getFileName().toString().charAt(0) == '!';
    }

    private static boolean isJsonFile(@Nonnull Path path) {
        return Files.isRegularFile(path, new LinkOption[0]) && path.toString().endsWith(".json");
    }

    private static boolean isJsonFileName(@Nonnull Path path, EventKind eventKind) {
        return path.toString().endsWith(".json");
    }

    @Nonnull
    private static String builderNameFromPath(@Nonnull Path path) {
        int endIndex;
        String fileName = path.getFileName().toString();
        if (fileName.startsWith("NPCRole-")) {
            fileName = fileName.split("-")[1];
        }
        return (endIndex = fileName.lastIndexOf(46)) >= 0 ? fileName.substring(0, endIndex) : fileName;
    }

    @Nonnull
    private String buildPathString(@Nonnull IntArrayList path, int index) {
        if (path.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append(" (Path: ");
        IntListIterator i = path.iterator();
        while (i.hasNext()) {
            result.append(this.lookupName(i.nextInt())).append(" -> ");
        }
        result.append(this.lookupName(index)).append(')');
        return result.toString();
    }

    private boolean validateBuilder(@Nonnull BuilderInfo builderInfo, @Nonnull IntSet validatedDependencies, @Nonnull IntArrayList path) {
        IntSet dependencies;
        int index = builderInfo.getIndex();
        if (path.contains(index)) {
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Builder '%s' validation failed: Cyclic reference detected for builder '%s'%s", this.lookupName(path.getInt(0)), this.lookupName(index), this.buildPathString(path, index));
            return builderInfo.setValidated(false);
        }
        path.add(index);
        try {
            dependencies = this.computeAllDependencies(builderInfo.getBuilder(), builderInfo.getIndex());
        }
        catch (SkipSentryException | IllegalArgumentException | IllegalStateException e) {
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Builder '%s' validation failed: %s", (Object)this.lookupName(path.getInt(0)), (Object)e.getMessage());
            return builderInfo.setValidated(false);
        }
        boolean valid = true;
        IntIterator i = dependencies.iterator();
        while (i.hasNext()) {
            int dependency = i.nextInt();
            if (path.contains(dependency)) {
                NPCPlugin.get().getLogger().at(Level.SEVERE).log("Builder '%s' validation failed: Cyclic reference detected for builder '%s'%s", this.lookupName(path.getInt(0)), this.lookupName(dependency), this.buildPathString(path, index));
                return builderInfo.setValidated(false);
            }
            if (!validatedDependencies.add(dependency)) continue;
            BuilderInfo childBuilder = this.builderCache.get(dependency);
            if (childBuilder == null) {
                NPCPlugin.get().getLogger().at(Level.SEVERE).log("Builder '%s' validation failed: Reference to unknown builder '%s'%s", this.lookupName(path.getInt(0)), this.lookupName(dependency), this.buildPathString(path, dependency));
                valid = false;
                continue;
            }
            if (!childBuilder.isValidated()) {
                valid = this.validateBuilder(childBuilder, validatedDependencies, path);
                continue;
            }
            if (childBuilder.isValid()) continue;
            NPCPlugin.get().getLogger().at(Level.SEVERE).log("Builder '%s' validation failed: Reference to invalid builder '%s'%s", this.lookupName(path.getInt(0)), childBuilder.getKeyName(), this.buildPathString(path, dependency));
            valid = false;
        }
        path.removeInt(path.size() - 1);
        return builderInfo.setValidated(valid);
    }

    @Nonnull
    private IntSet computeAllDependencies(@Nonnull Builder<?> builder, int builderIndex) {
        return this.computeAllDependencies(builder, builderIndex, new IntOpenHashSet(), new IntArrayList());
    }

    @Nonnull
    private IntSet computeAllDependencies(@Nonnull Builder<?> builder, int builderIndex, @Nonnull IntSet dependencies, @Nonnull IntArrayList path) {
        if (path.contains(builderIndex)) {
            throw new SkipSentryException(new IllegalArgumentException("Cyclic reference detected for builder: " + this.lookupName(builderIndex)));
        }
        path.add(builderIndex);
        this.iterateDependencies(builder.getDependencies().iterator(), dependencies, path);
        if (builder.hasDynamicDependencies()) {
            this.iterateDependencies(builder.getDynamicDependencies().iterator(), dependencies, path);
        }
        path.removeInt(path.size() - 1);
        return dependencies;
    }

    private void iterateDependencies(@Nonnull IntIterator iterator, @Nonnull IntSet dependencies, @Nonnull IntArrayList path) {
        while (iterator.hasNext()) {
            int dependency = iterator.nextInt();
            if (dependencies.contains(dependency)) continue;
            dependencies.add(dependency);
            Builder<?> child = this.tryGetCachedBuilder(dependency);
            if (child == null) {
                throw new SkipSentryException(new IllegalStateException("Reference to unknown builder: " + this.lookupName(dependency)));
            }
            this.computeAllDependencies(child, dependency, dependencies, path);
        }
    }

    private void reloadDependants(int dependency) {
        for (BuilderInfo builderInfo : this.builderCache.values()) {
            int index = builderInfo.getIndex();
            String keyName = builderInfo.getKeyName();
            Builder<?> builder = builderInfo.getBuilder();
            try {
                if (!builder.isSpawnable() || !this.isDependant(builder, index, dependency)) continue;
                NPCPlugin.get().getLogger().at(Level.INFO).log("Reloading entities of type '%s' because dependency '%s' changed", (Object)keyName, (Object)this.lookupName(dependency));
                BuilderManager.onBuilderReloaded(builderInfo);
            }
            catch (Throwable e) {
                NPCPlugin.get().getLogger().at(Level.INFO).log("Failed to reload entities of type '%s': %s", (Object)keyName, (Object)e.getMessage());
            }
        }
    }

    private class BuilderAssetMonitorHandler
    implements AssetMonitorHandler {
        private final AssetPack pack;
        private final boolean includeTests;

        public BuilderAssetMonitorHandler(AssetPack pack, boolean includeTests) {
            this.pack = pack;
            this.includeTests = includeTests;
        }

        @Override
        public Object getKey() {
            return this.pack;
        }

        @Override
        public boolean test(Path path, EventKind eventKind) {
            return BuilderManager.isJsonFileName(path, eventKind);
        }

        @Override
        public void accept(Map<Path, EventKind> map) {
            Int2ObjectOpenHashMap<BuilderInfo> loadedBuilders = new Int2ObjectOpenHashMap<BuilderInfo>();
            HashSet<String> loadedBuilderNames = new HashSet<String>();
            HashSet<String> failedBuilderTexts = new HashSet<String>();
            HashSet<String> deletedBuilderNames = new HashSet<String>();
            ObjectArrayList<String> errors = new ObjectArrayList<String>();
            for (Map.Entry<Path, EventKind> entry : map.entrySet()) {
                Path path = entry.getKey();
                EventKind eventKind = entry.getValue();
                if (eventKind == EventKind.ENTRY_CREATE || eventKind == EventKind.ENTRY_MODIFY) {
                    if (!Files.isRegularFile(path, new LinkOption[0]) || BuilderManager.isIgnoredFile(path)) continue;
                    try {
                        int builderIndex = BuilderManager.this.loadFile(path, errors, null, this.includeTests, true);
                        if (builderIndex < 0) continue;
                        String name = BuilderManager.builderNameFromPath(path);
                        NPCPlugin.get().getLogger().at(Level.INFO).log("Reloaded NPC builder " + name);
                        loadedBuilderNames.add(name);
                        for (BuilderInfo builderInfo : BuilderManager.this.builderCache.values()) {
                            try {
                                if (!BuilderManager.this.isDependant(builderInfo.getBuilder(), builderInfo.getIndex(), builderIndex)) continue;
                                builderInfo.setNeedsValidation();
                            }
                            catch (IllegalArgumentException | IllegalStateException e) {
                                NPCPlugin.get().getLogger().at(Level.WARNING).log("Could not check if builder %s was dependent: %s", (Object)BuilderManager.this.lookupName(builderInfo.getIndex()), (Object)e.getMessage());
                            }
                        }
                        if (BuilderManager.this.autoReload) {
                            BuilderManager.this.reloadDependants(builderIndex);
                        }
                        BuilderInfo builder = BuilderManager.this.builderCache.get(builderIndex);
                        BuilderManager.onBuilderReloaded(builder);
                        loadedBuilders.put(builderIndex, builder);
                    }
                    catch (Throwable e) {
                        NPCPlugin.get().getLogger().at(Level.SEVERE).log("Failed to reload %s config %s: %s", "NPC", path, e.getMessage());
                        failedBuilderTexts.add(BuilderManager.builderNameFromPath(path) + ": " + e.getMessage());
                    }
                    continue;
                }
                if (eventKind != EventKind.ENTRY_DELETE) continue;
                String builderName = BuilderManager.builderNameFromPath(path);
                BuilderManager.this.removeBuilder(builderName);
                NPCPlugin.get().getLogger().at(Level.INFO).log("Deleted %s builder %s", (Object)"NPC", (Object)builderName);
                deletedBuilderNames.add(builderName);
            }
            BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.reloadAssets").param("class", "NPC"), loadedBuilderNames);
            BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.loadFailed").param("class", "NPC"), failedBuilderTexts);
            BuilderManager.sendReloadNotification(Message.translation("server.general.assetstore.removedAssets").param("class", "NPC"), deletedBuilderNames);
            BuilderManager.this.finishLoadingBuilders(loadedBuilders, errors);
        }
    }

    private static enum TestType {
        NORMAL,
        FAILING;

    }
}

