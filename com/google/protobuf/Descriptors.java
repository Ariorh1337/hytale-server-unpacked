/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.JavaFeaturesProto;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.TextFormat;
import com.google.protobuf.WireFormat;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

@CheckReturnValue
public final class Descriptors {
    private static final Logger logger = Logger.getLogger(Descriptors.class.getName());
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Descriptor[] EMPTY_DESCRIPTORS = new Descriptor[0];
    private static final FieldDescriptor[] EMPTY_FIELD_DESCRIPTORS = new FieldDescriptor[0];
    private static final EnumDescriptor[] EMPTY_ENUM_DESCRIPTORS = new EnumDescriptor[0];
    private static final ServiceDescriptor[] EMPTY_SERVICE_DESCRIPTORS = new ServiceDescriptor[0];
    private static final OneofDescriptor[] EMPTY_ONEOF_DESCRIPTORS = new OneofDescriptor[0];
    private static final ConcurrentHashMap<DescriptorProtos.FeatureSet, DescriptorProtos.FeatureSet> FEATURE_CACHE = new ConcurrentHashMap();
    private static volatile DescriptorProtos.FeatureSetDefaults javaEditionDefaults = null;

    static void setTestJavaEditionDefaults(DescriptorProtos.FeatureSetDefaults defaults) {
        javaEditionDefaults = defaults;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static DescriptorProtos.FeatureSetDefaults getJavaEditionDefaults() {
        Descriptor unused1 = DescriptorProtos.FeatureSetDefaults.getDescriptor();
        FileDescriptor unused2 = JavaFeaturesProto.getDescriptor();
        if (javaEditionDefaults != null) return javaEditionDefaults;
        Class<Descriptors> clazz = Descriptors.class;
        synchronized (Descriptors.class) {
            if (javaEditionDefaults != null) return javaEditionDefaults;
            try {
                ExtensionRegistry registry = ExtensionRegistry.newInstance();
                registry.add(JavaFeaturesProto.java_);
                Descriptors.setTestJavaEditionDefaults(DescriptorProtos.FeatureSetDefaults.parseFrom("\n'\u0018\u0084\u0007\"\u0003\u00ca>\u0000*\u001d\b\u0001\u0010\u0002\u0018\u0002 \u0003(\u00010\u00028\u0002@\u0001\u00ca>\n\b\u0001\u0010\u0001\u0018\u0000 \u0001(\u0003\n'\u0018\u00e7\u0007\"\u0003\u00ca>\u0000*\u001d\b\u0002\u0010\u0001\u0018\u0001 \u0002(\u00010\u00018\u0002@\u0001\u00ca>\n\b\u0000\u0010\u0001\u0018\u0000 \u0001(\u0003\n'\u0018\u00e8\u0007\"\u0013\b\u0001\u0010\u0001\u0018\u0001 \u0002(\u00010\u0001\u00ca>\u0004\b\u0000\u0010\u0001*\r8\u0002@\u0001\u00ca>\u0006\u0018\u0000 \u0001(\u0003\n'\u0018\u00e9\u0007\"\u001b\b\u0001\u0010\u0001\u0018\u0001 \u0002(\u00010\u00018\u0001@\u0002\u00ca>\b\b\u0000\u0010\u0001\u0018\u0000(\u0001*\u0005\u00ca>\u0002 \u0000 \u00e6\u0007(\u00e9\u0007".getBytes(Internal.ISO_8859_1), (ExtensionRegistryLite)registry));
            }
            catch (Exception e) {
                throw new AssertionError((Object)e);
            }
            return javaEditionDefaults;
        }
    }

    static DescriptorProtos.FeatureSet getEditionDefaults(DescriptorProtos.Edition edition) {
        DescriptorProtos.FeatureSetDefaults javaEditionDefaults = Descriptors.getJavaEditionDefaults();
        if (edition.getNumber() < javaEditionDefaults.getMinimumEdition().getNumber()) {
            throw new IllegalArgumentException("Edition " + edition + " is lower than the minimum supported edition " + javaEditionDefaults.getMinimumEdition() + "!");
        }
        if (edition.getNumber() > javaEditionDefaults.getMaximumEdition().getNumber()) {
            throw new IllegalArgumentException("Edition " + edition + " is greater than the maximum supported edition " + javaEditionDefaults.getMaximumEdition() + "!");
        }
        DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault found = null;
        for (DescriptorProtos.FeatureSetDefaults.FeatureSetEditionDefault editionDefault : javaEditionDefaults.getDefaultsList()) {
            if (editionDefault.getEdition().getNumber() > edition.getNumber()) break;
            found = editionDefault;
        }
        if (found == null) {
            throw new IllegalArgumentException("Edition " + edition + " does not have a valid default FeatureSet!");
        }
        return found.getFixedFeatures().toBuilder().mergeFrom(found.getOverridableFeatures()).build();
    }

    private static DescriptorProtos.FeatureSet internFeatures(DescriptorProtos.FeatureSet features) {
        DescriptorProtos.FeatureSet cached = FEATURE_CACHE.putIfAbsent(features, features);
        if (cached == null) {
            return features;
        }
        return cached;
    }

    private static String computeFullName(FileDescriptor file, Descriptor parent, String name) {
        if (parent != null) {
            return parent.getFullName() + '.' + name;
        }
        String packageName = file.getPackage();
        if (!packageName.isEmpty()) {
            return packageName + '.' + name;
        }
        return name;
    }

    private static <T> T binarySearch(T[] array, int size, ToIntFunction<T> getter, int number) {
        int left = 0;
        int right = size - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            T midValue = array[mid];
            int midValueNumber = getter.applyAsInt(midValue);
            if (number < midValueNumber) {
                right = mid - 1;
                continue;
            }
            if (number > midValueNumber) {
                left = mid + 1;
                continue;
            }
            return midValue;
        }
        return null;
    }

    public static final class FileDescriptor
    extends GenericDescriptor {
        private DescriptorProtos.FileDescriptorProto proto;
        private volatile DescriptorProtos.FileOptions options;
        private final Descriptor[] messageTypes;
        private final EnumDescriptor[] enumTypes;
        private final ServiceDescriptor[] services;
        private final FieldDescriptor[] extensions;
        private final FileDescriptor[] dependencies;
        private final FileDescriptor[] publicDependencies;
        private final FileDescriptorTables tables;
        private final boolean placeholder;
        private volatile boolean featuresResolved;

        @Override
        public DescriptorProtos.FileDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public FileDescriptor getFile() {
            return this;
        }

        @Override
        GenericDescriptor getParent() {
            return null;
        }

        public boolean isPlaceholder() {
            return this.placeholder;
        }

        @Override
        public String getFullName() {
            return this.proto.getName();
        }

        public String getPackage() {
            return this.proto.getPackage();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.FileOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.FileOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                FileDescriptor fileDescriptor = this;
                synchronized (fileDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public List<Descriptor> getMessageTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.messageTypes));
        }

        public int getMessageTypeCount() {
            return this.messageTypes.length;
        }

        public Descriptor getMessageType(int index) {
            return this.messageTypes[index];
        }

        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
        }

        public int getEnumTypeCount() {
            return this.enumTypes.length;
        }

        public EnumDescriptor getEnumType(int index) {
            return this.enumTypes[index];
        }

        public List<ServiceDescriptor> getServices() {
            return Collections.unmodifiableList(Arrays.asList(this.services));
        }

        public int getServiceCount() {
            return this.services.length;
        }

        public ServiceDescriptor getService(int index) {
            return this.services[index];
        }

        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList(Arrays.asList(this.extensions));
        }

        public int getExtensionCount() {
            return this.extensions.length;
        }

        public FieldDescriptor getExtension(int index) {
            return this.extensions[index];
        }

        public List<FileDescriptor> getDependencies() {
            return Collections.unmodifiableList(Arrays.asList(this.dependencies));
        }

        public List<FileDescriptor> getPublicDependencies() {
            return Collections.unmodifiableList(Arrays.asList(this.publicDependencies));
        }

        DescriptorProtos.Edition getEdition() {
            switch (this.proto.getSyntax()) {
                case "editions": {
                    return this.proto.getEdition();
                }
                case "proto3": {
                    return DescriptorProtos.Edition.EDITION_PROTO3;
                }
            }
            return DescriptorProtos.Edition.EDITION_PROTO2;
        }

        public void copyHeadingTo(DescriptorProtos.FileDescriptorProto.Builder protoBuilder) {
            protoBuilder.setName(this.getName()).setSyntax(this.proto.getSyntax());
            if (!this.getPackage().isEmpty()) {
                protoBuilder.setPackage(this.getPackage());
            }
            if (this.proto.getSyntax().equals("editions")) {
                protoBuilder.setEdition(this.proto.getEdition());
            }
            if (this.proto.hasOptions() && !this.proto.getOptions().equals(DescriptorProtos.FileOptions.getDefaultInstance())) {
                protoBuilder.setOptions(this.proto.getOptions());
            }
        }

        public Descriptor findMessageTypeByName(String name) {
            GenericDescriptor result;
            if (name.indexOf(46) != -1) {
                return null;
            }
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
                name = packageName + '.' + name;
            }
            if ((result = this.tables.findSymbol(name)) instanceof Descriptor && result.getFile() == this) {
                return (Descriptor)result;
            }
            return null;
        }

        public EnumDescriptor findEnumTypeByName(String name) {
            GenericDescriptor result;
            if (name.indexOf(46) != -1) {
                return null;
            }
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
                name = packageName + '.' + name;
            }
            if ((result = this.tables.findSymbol(name)) instanceof EnumDescriptor && result.getFile() == this) {
                return (EnumDescriptor)result;
            }
            return null;
        }

        public ServiceDescriptor findServiceByName(String name) {
            GenericDescriptor result;
            if (name.indexOf(46) != -1) {
                return null;
            }
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
                name = packageName + '.' + name;
            }
            if ((result = this.tables.findSymbol(name)) instanceof ServiceDescriptor && result.getFile() == this) {
                return (ServiceDescriptor)result;
            }
            return null;
        }

        public FieldDescriptor findExtensionByName(String name) {
            GenericDescriptor result;
            if (name.indexOf(46) != -1) {
                return null;
            }
            String packageName = this.getPackage();
            if (!packageName.isEmpty()) {
                name = packageName + '.' + name;
            }
            if ((result = this.tables.findSymbol(name)) instanceof FieldDescriptor && result.getFile() == this) {
                return (FieldDescriptor)result;
            }
            return null;
        }

        public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies) throws DescriptorValidationException {
            return FileDescriptor.buildFrom(proto, dependencies, false);
        }

        public static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, boolean allowUnknownDependencies) throws DescriptorValidationException {
            return FileDescriptor.buildFrom(proto, dependencies, allowUnknownDependencies, false);
        }

        private static FileDescriptor buildFrom(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, boolean allowUnknownDependencies, boolean allowUnresolvedFeatures) throws DescriptorValidationException {
            FileDescriptorTables tables = new FileDescriptorTables(dependencies, allowUnknownDependencies);
            FileDescriptor result = new FileDescriptor(proto, dependencies, tables, allowUnknownDependencies);
            result.crossLink();
            if (!allowUnresolvedFeatures) {
                result.resolveAllFeaturesInternal();
            }
            return result;
        }

        private static byte[] latin1Cat(String[] strings) {
            if (strings.length == 1) {
                return strings[0].getBytes(Internal.ISO_8859_1);
            }
            StringBuilder descriptorData = new StringBuilder();
            for (String part : strings) {
                descriptorData.append(part);
            }
            return descriptorData.toString().getBytes(Internal.ISO_8859_1);
        }

        private static FileDescriptor[] findDescriptors(Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
            ArrayList<FileDescriptor> descriptors = new ArrayList<FileDescriptor>();
            for (int i = 0; i < dependencyClassNames.length; ++i) {
                try {
                    Class<?> clazz = descriptorOuterClass.getClassLoader().loadClass(dependencyClassNames[i]);
                    descriptors.add((FileDescriptor)clazz.getField("descriptor").get(null));
                    continue;
                }
                catch (Exception e) {
                    logger.warning("Descriptors for \"" + dependencyFileNames[i] + "\" can not be found.");
                }
            }
            return descriptors.toArray(new FileDescriptor[0]);
        }

        @Deprecated
        public static void internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies, InternalDescriptorAssigner descriptorAssigner) {
            FileDescriptor result;
            DescriptorProtos.FileDescriptorProto proto;
            byte[] descriptorBytes = FileDescriptor.latin1Cat(descriptorDataParts);
            try {
                proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
            }
            catch (InvalidProtocolBufferException e) {
                throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
            }
            try {
                result = FileDescriptor.buildFrom(proto, dependencies, true);
            }
            catch (DescriptorValidationException e) {
                throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", e);
            }
            ExtensionRegistry registry = descriptorAssigner.assignDescriptors(result);
            if (registry != null) {
                throw new RuntimeException("assignDescriptors must return null");
            }
        }

        public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, FileDescriptor[] dependencies) {
            DescriptorProtos.FileDescriptorProto proto;
            byte[] descriptorBytes = FileDescriptor.latin1Cat(descriptorDataParts);
            try {
                proto = DescriptorProtos.FileDescriptorProto.parseFrom(descriptorBytes);
            }
            catch (InvalidProtocolBufferException e) {
                throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
            }
            try {
                return FileDescriptor.buildFrom(proto, dependencies, true, true);
            }
            catch (DescriptorValidationException e) {
                throw new IllegalArgumentException("Invalid embedded descriptor for \"" + proto.getName() + "\".", e);
            }
        }

        public static FileDescriptor internalBuildGeneratedFileFrom(String[] descriptorDataParts, Class<?> descriptorOuterClass, String[] dependencyClassNames, String[] dependencyFileNames) {
            FileDescriptor[] dependencies = FileDescriptor.findDescriptors(descriptorOuterClass, dependencyClassNames, dependencyFileNames);
            return FileDescriptor.internalBuildGeneratedFileFrom(descriptorDataParts, dependencies);
        }

        public static void internalUpdateFileDescriptor(FileDescriptor descriptor, ExtensionRegistry registry) {
            ByteString bytes = descriptor.proto.toByteString();
            try {
                DescriptorProtos.FileDescriptorProto proto = DescriptorProtos.FileDescriptorProto.parseFrom(bytes, (ExtensionRegistryLite)registry);
                descriptor.setProto(proto);
            }
            catch (InvalidProtocolBufferException e) {
                throw new IllegalArgumentException("Failed to parse protocol buffer descriptor for generated code.", e);
            }
        }

        private FileDescriptor(DescriptorProtos.FileDescriptorProto proto, FileDescriptor[] dependencies, FileDescriptorTables tables, boolean allowUnknownDependencies) throws DescriptorValidationException {
            int i;
            this.tables = tables;
            this.proto = proto;
            this.dependencies = (FileDescriptor[])dependencies.clone();
            this.featuresResolved = false;
            HashMap<String, FileDescriptor> nameToFileMap = new HashMap<String, FileDescriptor>();
            for (FileDescriptor file : dependencies) {
                nameToFileMap.put(file.getName(), file);
            }
            ArrayList<FileDescriptor> publicDependencies = new ArrayList<FileDescriptor>();
            for (i = 0; i < proto.getPublicDependencyCount(); ++i) {
                int index = proto.getPublicDependency(i);
                if (index < 0 || index >= proto.getDependencyCount()) {
                    throw new DescriptorValidationException(this, "Invalid public dependency index.");
                }
                String name = proto.getDependency(index);
                FileDescriptor file = (FileDescriptor)nameToFileMap.get(name);
                if (file == null) {
                    if (allowUnknownDependencies) continue;
                    throw new DescriptorValidationException(this, "Invalid public dependency: " + name);
                }
                publicDependencies.add(file);
            }
            this.publicDependencies = new FileDescriptor[publicDependencies.size()];
            publicDependencies.toArray(this.publicDependencies);
            this.placeholder = false;
            tables.addPackage(this.getPackage(), this);
            this.messageTypes = proto.getMessageTypeCount() > 0 ? new Descriptor[proto.getMessageTypeCount()] : EMPTY_DESCRIPTORS;
            for (i = 0; i < proto.getMessageTypeCount(); ++i) {
                this.messageTypes[i] = new Descriptor(proto.getMessageType(i), this, null, i);
            }
            this.enumTypes = proto.getEnumTypeCount() > 0 ? new EnumDescriptor[proto.getEnumTypeCount()] : EMPTY_ENUM_DESCRIPTORS;
            for (i = 0; i < proto.getEnumTypeCount(); ++i) {
                this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), this, null, i);
            }
            this.services = proto.getServiceCount() > 0 ? new ServiceDescriptor[proto.getServiceCount()] : EMPTY_SERVICE_DESCRIPTORS;
            for (i = 0; i < proto.getServiceCount(); ++i) {
                this.services[i] = new ServiceDescriptor(proto.getService(i), this, i);
            }
            this.extensions = proto.getExtensionCount() > 0 ? new FieldDescriptor[proto.getExtensionCount()] : EMPTY_FIELD_DESCRIPTORS;
            for (i = 0; i < proto.getExtensionCount(); ++i) {
                this.extensions[i] = new FieldDescriptor(proto.getExtension(i), this, null, i, true);
            }
        }

        FileDescriptor(String packageName, Descriptor message) throws DescriptorValidationException {
            this.tables = new FileDescriptorTables(new FileDescriptor[0], true);
            this.proto = DescriptorProtos.FileDescriptorProto.newBuilder().setName(message.getFullName() + ".placeholder.proto").setPackage(packageName).addMessageType(message.toProto()).build();
            this.dependencies = new FileDescriptor[0];
            this.publicDependencies = new FileDescriptor[0];
            this.featuresResolved = false;
            this.messageTypes = new Descriptor[]{message};
            this.enumTypes = EMPTY_ENUM_DESCRIPTORS;
            this.services = EMPTY_SERVICE_DESCRIPTORS;
            this.extensions = EMPTY_FIELD_DESCRIPTORS;
            this.placeholder = true;
            this.tables.addPackage(packageName, this);
            this.tables.addSymbol(message);
        }

        public void resolveAllFeaturesImmutable() {
            try {
                this.resolveAllFeaturesInternal();
            }
            catch (DescriptorValidationException e) {
                throw new IllegalArgumentException("Invalid features for \"" + this.proto.getName() + "\".", e);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void resolveAllFeaturesInternal() throws DescriptorValidationException {
            if (this.featuresResolved) {
                return;
            }
            FileDescriptor fileDescriptor = this;
            synchronized (fileDescriptor) {
                if (this.featuresResolved) {
                    return;
                }
                this.resolveFeatures(this.proto.getOptions().getFeatures());
                for (Descriptor descriptor : this.messageTypes) {
                    descriptor.resolveAllFeatures();
                }
                for (GenericDescriptor genericDescriptor : this.enumTypes) {
                    ((EnumDescriptor)genericDescriptor).resolveAllFeatures();
                }
                for (GenericDescriptor genericDescriptor : this.services) {
                    ((ServiceDescriptor)genericDescriptor).resolveAllFeatures();
                }
                for (GenericDescriptor genericDescriptor : this.extensions) {
                    ((FieldDescriptor)genericDescriptor).resolveAllFeatures();
                }
                this.featuresResolved = true;
            }
        }

        @Override
        DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
            if (this.getEdition().getNumber() >= DescriptorProtos.Edition.EDITION_2023.getNumber()) {
                return DescriptorProtos.FeatureSet.getDefaultInstance();
            }
            DescriptorProtos.FeatureSet.Builder features = null;
            if (this.getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 && this.proto.getOptions().getJavaStringCheckUtf8()) {
                features = DescriptorProtos.FeatureSet.newBuilder();
                features.setExtension(JavaFeaturesProto.java_, JavaFeaturesProto.JavaFeatures.newBuilder().setUtf8Validation(JavaFeaturesProto.JavaFeatures.Utf8Validation.VERIFY).build());
            }
            return features != null ? features.build() : DescriptorProtos.FeatureSet.getDefaultInstance();
        }

        private void crossLink() throws DescriptorValidationException {
            for (Descriptor descriptor : this.messageTypes) {
                descriptor.crossLink();
            }
            for (GenericDescriptor genericDescriptor : this.services) {
                ((ServiceDescriptor)genericDescriptor).crossLink();
            }
            for (GenericDescriptor genericDescriptor : this.extensions) {
                ((FieldDescriptor)genericDescriptor).crossLink();
            }
        }

        private synchronized void setProto(DescriptorProtos.FileDescriptorProto proto) {
            this.proto = proto;
            this.options = null;
            try {
                int i;
                this.resolveFeatures(proto.getOptions().getFeatures());
                for (i = 0; i < this.messageTypes.length; ++i) {
                    this.messageTypes[i].setProto(proto.getMessageType(i));
                }
                for (i = 0; i < this.enumTypes.length; ++i) {
                    this.enumTypes[i].setProto(proto.getEnumType(i));
                }
                for (i = 0; i < this.services.length; ++i) {
                    this.services[i].setProto(proto.getService(i));
                }
                for (i = 0; i < this.extensions.length; ++i) {
                    this.extensions[i].setProto(proto.getExtension(i));
                }
            }
            catch (DescriptorValidationException e) {
                throw new IllegalArgumentException("Invalid features for \"" + proto.getName() + "\".", e);
            }
        }

        @Deprecated
        public static interface InternalDescriptorAssigner {
            public ExtensionRegistry assignDescriptors(FileDescriptor var1);
        }
    }

    public static final class Descriptor
    extends GenericDescriptor {
        private final int index;
        private DescriptorProtos.DescriptorProto proto;
        private volatile DescriptorProtos.MessageOptions options;
        private final String fullName;
        private final GenericDescriptor parent;
        private final Descriptor[] nestedTypes;
        private final EnumDescriptor[] enumTypes;
        private final FieldDescriptor[] fields;
        private final FieldDescriptor[] fieldsSortedByNumber;
        private final FieldDescriptor[] extensions;
        private final OneofDescriptor[] oneofs;
        private final int realOneofCount;
        private final int[] extensionRangeLowerBounds;
        private final int[] extensionRangeUpperBounds;
        private final boolean placeholder;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.DescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        @Override
        public FileDescriptor getFile() {
            return this.parent.getFile();
        }

        @Override
        GenericDescriptor getParent() {
            return this.parent;
        }

        public boolean isPlaceholder() {
            return this.placeholder;
        }

        public Descriptor getContainingType() {
            if (this.parent instanceof Descriptor) {
                return (Descriptor)this.parent;
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.MessageOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.MessageOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                Descriptor descriptor = this;
                synchronized (descriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public List<FieldDescriptor> getFields() {
            return Collections.unmodifiableList(Arrays.asList(this.fields));
        }

        public int getFieldCount() {
            return this.fields.length;
        }

        public FieldDescriptor getField(int index) {
            return this.fields[index];
        }

        public List<OneofDescriptor> getOneofs() {
            return Collections.unmodifiableList(Arrays.asList(this.oneofs));
        }

        public int getOneofCount() {
            return this.oneofs.length;
        }

        public OneofDescriptor getOneof(int index) {
            return this.oneofs[index];
        }

        public List<OneofDescriptor> getRealOneofs() {
            return Collections.unmodifiableList(Arrays.asList(this.oneofs).subList(0, this.realOneofCount));
        }

        public int getRealOneofCount() {
            return this.realOneofCount;
        }

        public OneofDescriptor getRealOneof(int index) {
            if (index >= this.realOneofCount) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return this.oneofs[index];
        }

        public List<FieldDescriptor> getExtensions() {
            return Collections.unmodifiableList(Arrays.asList(this.extensions));
        }

        public int getExtensionCount() {
            return this.extensions.length;
        }

        public FieldDescriptor getExtension(int index) {
            return this.extensions[index];
        }

        public List<Descriptor> getNestedTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.nestedTypes));
        }

        public int getNestedTypeCount() {
            return this.nestedTypes.length;
        }

        public Descriptor getNestedType(int index) {
            return this.nestedTypes[index];
        }

        public List<EnumDescriptor> getEnumTypes() {
            return Collections.unmodifiableList(Arrays.asList(this.enumTypes));
        }

        public int getEnumTypeCount() {
            return this.enumTypes.length;
        }

        public EnumDescriptor getEnumType(int index) {
            return this.enumTypes[index];
        }

        public boolean isExtensionNumber(int number) {
            int index = Arrays.binarySearch(this.extensionRangeLowerBounds, number);
            if (index < 0) {
                index = ~index - 1;
            }
            return index >= 0 && number < this.extensionRangeUpperBounds[index];
        }

        public boolean isReservedNumber(int number) {
            for (DescriptorProtos.DescriptorProto.ReservedRange range : this.proto.getReservedRangeList()) {
                if (range.getStart() > number || number >= range.getEnd()) continue;
                return true;
            }
            return false;
        }

        public boolean isReservedName(String name) {
            Internal.checkNotNull(name);
            for (String reservedName : this.proto.getReservedNameList()) {
                if (!reservedName.equals(name)) continue;
                return true;
            }
            return false;
        }

        public boolean isExtendable() {
            return !this.proto.getExtensionRangeList().isEmpty();
        }

        public FieldDescriptor findFieldByName(String name) {
            GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
            if (result instanceof FieldDescriptor) {
                return (FieldDescriptor)result;
            }
            return null;
        }

        public FieldDescriptor findFieldByNumber(int number) {
            return (FieldDescriptor)Descriptors.binarySearch(this.fieldsSortedByNumber, this.fieldsSortedByNumber.length, FieldDescriptor.NUMBER_GETTER, number);
        }

        public Descriptor findNestedTypeByName(String name) {
            GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
            if (result instanceof Descriptor) {
                return (Descriptor)result;
            }
            return null;
        }

        public EnumDescriptor findEnumTypeByName(String name) {
            GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
            if (result instanceof EnumDescriptor) {
                return (EnumDescriptor)result;
            }
            return null;
        }

        Descriptor(String fullname) throws DescriptorValidationException {
            String name = fullname;
            String packageName = "";
            int pos = fullname.lastIndexOf(46);
            if (pos != -1) {
                name = fullname.substring(pos + 1);
                packageName = fullname.substring(0, pos);
            }
            this.index = 0;
            this.proto = DescriptorProtos.DescriptorProto.newBuilder().setName(name).addExtensionRange(DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder().setStart(1).setEnd(0x20000000).build()).build();
            this.fullName = fullname;
            this.nestedTypes = EMPTY_DESCRIPTORS;
            this.enumTypes = EMPTY_ENUM_DESCRIPTORS;
            this.fields = EMPTY_FIELD_DESCRIPTORS;
            this.fieldsSortedByNumber = EMPTY_FIELD_DESCRIPTORS;
            this.extensions = EMPTY_FIELD_DESCRIPTORS;
            this.oneofs = EMPTY_ONEOF_DESCRIPTORS;
            this.realOneofCount = 0;
            this.parent = new FileDescriptor(packageName, this);
            this.extensionRangeLowerBounds = new int[]{1};
            this.extensionRangeUpperBounds = new int[]{0x20000000};
            this.placeholder = true;
        }

        /*
         * WARNING - void declaration
         */
        private Descriptor(DescriptorProtos.DescriptorProto proto, FileDescriptor file, Descriptor parent, int index) throws DescriptorValidationException {
            int i;
            this.parent = parent == null ? file : parent;
            this.index = index;
            this.proto = proto;
            this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
            this.oneofs = proto.getOneofDeclCount() > 0 ? new OneofDescriptor[proto.getOneofDeclCount()] : EMPTY_ONEOF_DESCRIPTORS;
            for (i = 0; i < proto.getOneofDeclCount(); ++i) {
                this.oneofs[i] = new OneofDescriptor(proto.getOneofDecl(i), this, i);
            }
            this.nestedTypes = proto.getNestedTypeCount() > 0 ? new Descriptor[proto.getNestedTypeCount()] : EMPTY_DESCRIPTORS;
            for (i = 0; i < proto.getNestedTypeCount(); ++i) {
                this.nestedTypes[i] = new Descriptor(proto.getNestedType(i), file, this, i);
            }
            this.enumTypes = proto.getEnumTypeCount() > 0 ? new EnumDescriptor[proto.getEnumTypeCount()] : EMPTY_ENUM_DESCRIPTORS;
            for (i = 0; i < proto.getEnumTypeCount(); ++i) {
                this.enumTypes[i] = new EnumDescriptor(proto.getEnumType(i), file, this, i);
            }
            this.fields = proto.getFieldCount() > 0 ? new FieldDescriptor[proto.getFieldCount()] : EMPTY_FIELD_DESCRIPTORS;
            for (i = 0; i < proto.getFieldCount(); ++i) {
                this.fields[i] = new FieldDescriptor(proto.getField(i), file, this, i, false);
            }
            this.fieldsSortedByNumber = proto.getFieldCount() > 0 ? (FieldDescriptor[])this.fields.clone() : EMPTY_FIELD_DESCRIPTORS;
            this.extensions = proto.getExtensionCount() > 0 ? new FieldDescriptor[proto.getExtensionCount()] : EMPTY_FIELD_DESCRIPTORS;
            for (i = 0; i < proto.getExtensionCount(); ++i) {
                this.extensions[i] = new FieldDescriptor(proto.getExtension(i), file, this, i, true);
            }
            for (i = 0; i < proto.getOneofDeclCount(); ++i) {
                OneofDescriptor.access$2802(this.oneofs[i], new FieldDescriptor[this.oneofs[i].getFieldCount()]);
                this.oneofs[i].fieldCount = 0;
            }
            for (i = 0; i < proto.getFieldCount(); ++i) {
                OneofDescriptor oneofDescriptor = this.fields[i].getContainingOneof();
                if (oneofDescriptor == null) continue;
                ((OneofDescriptor)oneofDescriptor).fields[((OneofDescriptor)oneofDescriptor).fieldCount++] = this.fields[i];
            }
            int syntheticOneofCount = 0;
            for (OneofDescriptor oneof : this.oneofs) {
                if (oneof.isSynthetic()) {
                    ++syntheticOneofCount;
                    continue;
                }
                if (syntheticOneofCount <= 0) continue;
                throw new DescriptorValidationException((GenericDescriptor)this, "Synthetic oneofs must come last.");
            }
            this.realOneofCount = this.oneofs.length - syntheticOneofCount;
            this.placeholder = false;
            file.tables.addSymbol(this);
            if (proto.getExtensionRangeCount() > 0) {
                this.extensionRangeLowerBounds = new int[proto.getExtensionRangeCount()];
                this.extensionRangeUpperBounds = new int[proto.getExtensionRangeCount()];
                boolean bl = false;
                for (DescriptorProtos.DescriptorProto.ExtensionRange range : proto.getExtensionRangeList()) {
                    void var6_9;
                    this.extensionRangeLowerBounds[var6_9] = range.getStart();
                    this.extensionRangeUpperBounds[var6_9] = range.getEnd();
                    ++var6_9;
                }
                Arrays.sort(this.extensionRangeLowerBounds);
                Arrays.sort(this.extensionRangeUpperBounds);
            } else {
                this.extensionRangeLowerBounds = EMPTY_INT_ARRAY;
                this.extensionRangeUpperBounds = EMPTY_INT_ARRAY;
            }
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
            for (Descriptor descriptor : this.nestedTypes) {
                descriptor.resolveAllFeatures();
            }
            for (GenericDescriptor genericDescriptor : this.enumTypes) {
                ((EnumDescriptor)genericDescriptor).resolveAllFeatures();
            }
            for (GenericDescriptor genericDescriptor : this.oneofs) {
                ((OneofDescriptor)genericDescriptor).resolveAllFeatures();
            }
            for (GenericDescriptor genericDescriptor : this.fields) {
                ((FieldDescriptor)genericDescriptor).resolveAllFeatures();
            }
            for (GenericDescriptor genericDescriptor : this.extensions) {
                ((FieldDescriptor)genericDescriptor).resolveAllFeatures();
            }
        }

        private void crossLink() throws DescriptorValidationException {
            for (Descriptor descriptor : this.nestedTypes) {
                descriptor.crossLink();
            }
            for (GenericDescriptor genericDescriptor : this.fields) {
                ((FieldDescriptor)genericDescriptor).crossLink();
            }
            Arrays.sort(this.fieldsSortedByNumber);
            this.validateNoDuplicateFieldNumbers();
            for (GenericDescriptor genericDescriptor : this.extensions) {
                ((FieldDescriptor)genericDescriptor).crossLink();
            }
        }

        private void validateNoDuplicateFieldNumbers() throws DescriptorValidationException {
            int i = 0;
            while (i + 1 < this.fieldsSortedByNumber.length) {
                FieldDescriptor old = this.fieldsSortedByNumber[i];
                FieldDescriptor field = this.fieldsSortedByNumber[i + 1];
                if (old.getNumber() == field.getNumber()) {
                    throw new DescriptorValidationException((GenericDescriptor)field, "Field number " + field.getNumber() + " has already been used in \"" + field.getContainingType().getFullName() + "\" by field \"" + old.getName() + "\".");
                }
                ++i;
            }
        }

        private void setProto(DescriptorProtos.DescriptorProto proto) throws DescriptorValidationException {
            int i;
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
            for (i = 0; i < this.nestedTypes.length; ++i) {
                this.nestedTypes[i].setProto(proto.getNestedType(i));
            }
            for (i = 0; i < this.oneofs.length; ++i) {
                this.oneofs[i].setProto(proto.getOneofDecl(i));
            }
            for (i = 0; i < this.enumTypes.length; ++i) {
                this.enumTypes[i].setProto(proto.getEnumType(i));
            }
            for (i = 0; i < this.fields.length; ++i) {
                this.fields[i].setProto(proto.getField(i));
            }
            for (i = 0; i < this.extensions.length; ++i) {
                this.extensions[i].setProto(proto.getExtension(i));
            }
        }
    }

    public static final class OneofDescriptor
    extends GenericDescriptor {
        private final int index;
        private DescriptorProtos.OneofDescriptorProto proto;
        private volatile DescriptorProtos.OneofOptions options;
        private final String fullName;
        private final Descriptor containingType;
        private int fieldCount;
        private FieldDescriptor[] fields;

        public int getIndex() {
            return this.index;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public FileDescriptor getFile() {
            return this.containingType.getFile();
        }

        @Override
        GenericDescriptor getParent() {
            return this.containingType;
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        public Descriptor getContainingType() {
            return this.containingType;
        }

        public int getFieldCount() {
            return this.fieldCount;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.OneofOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.OneofOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                OneofDescriptor oneofDescriptor = this;
                synchronized (oneofDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public List<FieldDescriptor> getFields() {
            return Collections.unmodifiableList(Arrays.asList(this.fields));
        }

        public FieldDescriptor getField(int index) {
            return this.fields[index];
        }

        @Override
        public DescriptorProtos.OneofDescriptorProto toProto() {
            return this.proto;
        }

        boolean isSynthetic() {
            return this.fields.length == 1 && this.fields[0].isProto3Optional;
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
        }

        private void setProto(DescriptorProtos.OneofDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
        }

        private OneofDescriptor(DescriptorProtos.OneofDescriptorProto proto, Descriptor parent, int index) {
            this.proto = proto;
            this.fullName = Descriptors.computeFullName(null, parent, proto.getName());
            this.index = index;
            this.containingType = parent;
            this.fieldCount = 0;
        }

        static /* synthetic */ FieldDescriptor[] access$2802(OneofDescriptor x0, FieldDescriptor[] x1) {
            x0.fields = x1;
            return x1;
        }
    }

    public static final class FieldDescriptor
    extends GenericDescriptor
    implements Comparable<FieldDescriptor>,
    FieldSet.FieldDescriptorLite<FieldDescriptor> {
        private static final ToIntFunction<FieldDescriptor> NUMBER_GETTER = FieldDescriptor::getNumber;
        private static final WireFormat.FieldType[] table = WireFormat.FieldType.values();
        private final int index;
        private DescriptorProtos.FieldDescriptorProto proto;
        private volatile DescriptorProtos.FieldOptions options;
        private final String fullName;
        private String jsonName;
        private final GenericDescriptor parent;
        private final Descriptor extensionScope;
        private final boolean isProto3Optional;
        private volatile RedactionState redactionState;
        private Type type;
        private Descriptor containingType;
        private OneofDescriptor containingOneof;
        private GenericDescriptor typeDescriptor;
        private Object defaultValue;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.FieldDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public int getNumber() {
            return this.proto.getNumber();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        public String getJsonName() {
            String result = this.jsonName;
            if (result != null) {
                return result;
            }
            if (this.proto.hasJsonName()) {
                this.jsonName = this.proto.getJsonName();
                return this.jsonName;
            }
            this.jsonName = FieldDescriptor.fieldNameToJsonName(this.proto.getName());
            return this.jsonName;
        }

        public JavaType getJavaType() {
            return this.getType().getJavaType();
        }

        @Override
        public WireFormat.JavaType getLiteJavaType() {
            return this.getLiteType().getJavaType();
        }

        @Override
        public FileDescriptor getFile() {
            return this.parent.getFile();
        }

        @Override
        GenericDescriptor getParent() {
            return this.parent;
        }

        public Type getType() {
            if (!(this.type != Type.MESSAGE || this.typeDescriptor != null && ((Descriptor)this.typeDescriptor).toProto().getOptions().getMapEntry() || this.containingType != null && this.containingType.toProto().getOptions().getMapEntry() || this.features == null || this.getFeatures().getMessageEncoding() != DescriptorProtos.FeatureSet.MessageEncoding.DELIMITED)) {
                return Type.GROUP;
            }
            return this.type;
        }

        @Override
        public WireFormat.FieldType getLiteType() {
            return table[this.getType().ordinal()];
        }

        public boolean needsUtf8Check() {
            if (this.getType() != Type.STRING) {
                return false;
            }
            if (this.getContainingType().toProto().getOptions().getMapEntry()) {
                return true;
            }
            if (this.getFeatures().getExtension(JavaFeaturesProto.java_).getUtf8Validation().equals(JavaFeaturesProto.JavaFeatures.Utf8Validation.VERIFY)) {
                return true;
            }
            return this.getFeatures().getUtf8Validation().equals(DescriptorProtos.FeatureSet.Utf8Validation.VERIFY);
        }

        public boolean isMapField() {
            return this.getType() == Type.MESSAGE && this.isRepeated() && this.getMessageType().toProto().getOptions().getMapEntry();
        }

        public boolean isRequired() {
            return this.getFeatures().getFieldPresence() == DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED;
        }

        @Deprecated
        public boolean isOptional() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL && this.getFeatures().getFieldPresence() != DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED;
        }

        @Override
        public boolean isRepeated() {
            return this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
        }

        @Override
        public boolean isPacked() {
            if (!this.isPackable()) {
                return false;
            }
            return this.getFeatures().getRepeatedFieldEncoding().equals(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.PACKED);
        }

        public boolean isPackable() {
            return this.isRepeated() && this.getLiteType().isPackable();
        }

        public boolean hasDefaultValue() {
            return this.proto.hasDefaultValue();
        }

        public Object getDefaultValue() {
            if (this.getJavaType() == JavaType.MESSAGE) {
                throw new UnsupportedOperationException("FieldDescriptor.getDefaultValue() called on an embedded message field.");
            }
            return this.defaultValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.FieldOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.FieldOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                FieldDescriptor fieldDescriptor = this;
                synchronized (fieldDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public boolean isExtension() {
            return this.proto.hasExtendee();
        }

        public Descriptor getContainingType() {
            return this.containingType;
        }

        public OneofDescriptor getContainingOneof() {
            return this.containingOneof;
        }

        public OneofDescriptor getRealContainingOneof() {
            return this.containingOneof != null && !this.containingOneof.isSynthetic() ? this.containingOneof : null;
        }

        boolean hasOptionalKeyword() {
            return this.isProto3Optional || this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 && !this.isRequired() && !this.isRepeated() && this.getContainingOneof() == null;
        }

        public boolean hasPresence() {
            if (this.isRepeated()) {
                return false;
            }
            return this.isProto3Optional || this.getType() == Type.MESSAGE || this.getType() == Type.GROUP || this.isExtension() || this.getContainingOneof() != null || this.getFeatures().getFieldPresence() != DescriptorProtos.FeatureSet.FieldPresence.IMPLICIT;
        }

        boolean isGroupLike() {
            if (this.getType() != Type.GROUP) {
                return false;
            }
            if (!this.getMessageType().getName().toLowerCase().equals(this.getName())) {
                return false;
            }
            if (this.getMessageType().getFile() != this.getFile()) {
                return false;
            }
            return this.isExtension() ? this.getMessageType().getContainingType() == this.getExtensionScope() : this.getMessageType().getContainingType() == this.getContainingType();
        }

        public Descriptor getExtensionScope() {
            if (!this.isExtension()) {
                throw new UnsupportedOperationException(String.format("This field is not an extension. (%s)", this.fullName));
            }
            return this.extensionScope;
        }

        public Descriptor getMessageType() {
            if (this.getJavaType() != JavaType.MESSAGE) {
                throw new UnsupportedOperationException(String.format("This field is not of message type. (%s)", this.fullName));
            }
            return (Descriptor)this.typeDescriptor;
        }

        public EnumDescriptor getEnumType() {
            if (this.getJavaType() != JavaType.ENUM) {
                throw new UnsupportedOperationException(String.format("This field is not of enum type. (%s)", this.fullName));
            }
            return (EnumDescriptor)this.typeDescriptor;
        }

        public boolean legacyEnumFieldTreatedAsClosed() {
            if (this.getFile().getDependencies().isEmpty()) {
                return this.getType() == Type.ENUM && this.getEnumType().isClosed();
            }
            return this.getType() == Type.ENUM && (this.getFeatures().getExtension(JavaFeaturesProto.java_).getLegacyClosedEnum() || this.getEnumType().isClosed());
        }

        @Override
        public int compareTo(FieldDescriptor other) {
            if (other.containingType != this.containingType) {
                throw new IllegalArgumentException("FieldDescriptors can only be compared to other FieldDescriptors for fields of the same message type.");
            }
            return this.getNumber() - other.getNumber();
        }

        public String toString() {
            return this.getFullName();
        }

        private static String fieldNameToJsonName(String name) {
            int length = name.length();
            StringBuilder result = new StringBuilder(length);
            boolean isNextUpperCase = false;
            for (int i = 0; i < length; ++i) {
                char ch = name.charAt(i);
                if (ch == '_') {
                    isNextUpperCase = true;
                    continue;
                }
                if (isNextUpperCase) {
                    if ('a' <= ch && ch <= 'z') {
                        ch = (char)(ch - 97 + 65);
                    }
                    result.append(ch);
                    isNextUpperCase = false;
                    continue;
                }
                result.append(ch);
            }
            return result.toString();
        }

        private FieldDescriptor(DescriptorProtos.FieldDescriptorProto proto, FileDescriptor file, Descriptor parent, int index, boolean isExtension) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
            if (proto.hasType()) {
                this.type = Type.valueOf(proto.getType());
            }
            this.isProto3Optional = proto.getProto3Optional();
            if (this.getNumber() <= 0) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Field numbers must be positive integers.");
            }
            if (isExtension) {
                if (!proto.hasExtendee()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.extendee not set for extension field.");
                }
                this.containingType = null;
                if (parent != null) {
                    this.extensionScope = parent;
                    this.parent = parent;
                } else {
                    this.extensionScope = null;
                    this.parent = Internal.checkNotNull(file);
                }
                if (proto.hasOneofIndex()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.oneof_index set for extension field.");
                }
                this.containingOneof = null;
            } else {
                if (proto.hasExtendee()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.extendee set for non-extension field.");
                }
                this.containingType = parent;
                if (proto.hasOneofIndex()) {
                    if (proto.getOneofIndex() < 0 || proto.getOneofIndex() >= parent.toProto().getOneofDeclCount()) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "FieldDescriptorProto.oneof_index is out of range for type " + parent.getName());
                    }
                    this.containingOneof = parent.getOneofs().get(proto.getOneofIndex());
                    this.containingOneof.fieldCount++;
                    this.parent = Internal.checkNotNull(this.containingOneof);
                } else {
                    this.containingOneof = null;
                    this.parent = Internal.checkNotNull(parent);
                }
                this.extensionScope = null;
            }
            file.tables.addSymbol(this);
        }

        private static RedactionState isOptionSensitive(FieldDescriptor field, Object value) {
            block10: {
                block9: {
                    if (field.getType() != Type.ENUM) break block9;
                    if (field.isRepeated()) {
                        for (EnumValueDescriptor v : (List)value) {
                            if (!v.getOptions().getDebugRedact()) continue;
                            return RedactionState.of(true, false);
                        }
                    } else if (((EnumValueDescriptor)value).getOptions().getDebugRedact()) {
                        return RedactionState.of(true, false);
                    }
                    break block10;
                }
                if (field.getJavaType() != JavaType.MESSAGE) break block10;
                if (field.isRepeated()) {
                    for (Message m : (List)value) {
                        for (Map.Entry<FieldDescriptor, Object> entry : m.getAllFields().entrySet()) {
                            RedactionState state = FieldDescriptor.isOptionSensitive(entry.getKey(), entry.getValue());
                            if (!state.redact) continue;
                            return state;
                        }
                    }
                } else {
                    for (Map.Entry<FieldDescriptor, Object> entry : ((Message)value).getAllFields().entrySet()) {
                        RedactionState state = FieldDescriptor.isOptionSensitive(entry.getKey(), entry.getValue());
                        if (!state.redact) continue;
                        return state;
                    }
                }
            }
            return RedactionState.of(false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        RedactionState getRedactionState() {
            RedactionState state = this.redactionState;
            if (state == null) {
                FieldDescriptor fieldDescriptor = this;
                synchronized (fieldDescriptor) {
                    state = this.redactionState;
                    if (state == null) {
                        DescriptorProtos.FieldOptions options = this.getOptions();
                        state = RedactionState.of(options.getDebugRedact());
                        for (Map.Entry<FieldDescriptor, Object> entry : options.getAllFields().entrySet()) {
                            state = RedactionState.combine(state, FieldDescriptor.isOptionSensitive(entry.getKey(), entry.getValue()));
                            if (!state.redact) continue;
                            break;
                        }
                        this.redactionState = state;
                    }
                }
            }
            return state;
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
        }

        @Override
        DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
            if (this.getFile().getEdition().getNumber() >= DescriptorProtos.Edition.EDITION_2023.getNumber()) {
                return DescriptorProtos.FeatureSet.getDefaultInstance();
            }
            DescriptorProtos.FeatureSet.Builder features = null;
            if (this.proto.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REQUIRED) {
                features = DescriptorProtos.FeatureSet.newBuilder();
                features.setFieldPresence(DescriptorProtos.FeatureSet.FieldPresence.LEGACY_REQUIRED);
            }
            if (this.proto.getType() == DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP) {
                if (features == null) {
                    features = DescriptorProtos.FeatureSet.newBuilder();
                }
                features.setMessageEncoding(DescriptorProtos.FeatureSet.MessageEncoding.DELIMITED);
            }
            if (this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 && this.proto.getOptions().getPacked()) {
                if (features == null) {
                    features = DescriptorProtos.FeatureSet.newBuilder();
                }
                features.setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.PACKED);
            }
            if (this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO3 && this.proto.getOptions().hasPacked() && !this.proto.getOptions().getPacked()) {
                if (features == null) {
                    features = DescriptorProtos.FeatureSet.newBuilder();
                }
                features.setRepeatedFieldEncoding(DescriptorProtos.FeatureSet.RepeatedFieldEncoding.EXPANDED);
            }
            return features != null ? features.build() : DescriptorProtos.FeatureSet.getDefaultInstance();
        }

        @Override
        void validateFeatures() throws DescriptorValidationException {
            if (this.containingType != null && this.containingType.toProto().getOptions().getMessageSetWireFormat() && this.isExtension() && (this.isRequired() || this.isRepeated() || this.getType() != Type.MESSAGE)) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Extensions of MessageSets may not be required or repeated messages.");
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private void crossLink() throws DescriptorValidationException {
            if (this.proto.hasExtendee()) {
                GenericDescriptor extendee = this.getFile().tables.lookupSymbol(this.proto.getExtendee(), this, FileDescriptorTables.SearchFilter.TYPES_ONLY);
                if (!(extendee instanceof Descriptor)) {
                    throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getExtendee() + "\" is not a message type.");
                }
                this.containingType = (Descriptor)extendee;
                if (!this.getContainingType().isExtensionNumber(this.getNumber())) {
                    throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.getContainingType().getFullName() + "\" does not declare " + this.getNumber() + " as an extension number.");
                }
            }
            if (this.proto.hasTypeName()) {
                GenericDescriptor typeDescriptor = this.getFile().tables.lookupSymbol(this.proto.getTypeName(), this, FileDescriptorTables.SearchFilter.TYPES_ONLY);
                if (!this.proto.hasType()) {
                    if (typeDescriptor instanceof Descriptor) {
                        this.type = Type.MESSAGE;
                    } else {
                        if (!(typeDescriptor instanceof EnumDescriptor)) throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not a type.");
                        this.type = Type.ENUM;
                    }
                }
                if (this.type.getJavaType() == JavaType.MESSAGE) {
                    if (!(typeDescriptor instanceof Descriptor)) {
                        throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not a message type.");
                    }
                    this.typeDescriptor = typeDescriptor;
                    if (this.proto.hasDefaultValue()) {
                        throw new DescriptorValidationException((GenericDescriptor)this, "Messages can't have default values.");
                    }
                } else {
                    if (this.type.getJavaType() != JavaType.ENUM) throw new DescriptorValidationException((GenericDescriptor)this, "Field with primitive type has type_name.");
                    if (!(typeDescriptor instanceof EnumDescriptor)) {
                        throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getTypeName() + "\" is not an enum type.");
                    }
                    this.typeDescriptor = typeDescriptor;
                }
            } else if (this.type.getJavaType() == JavaType.MESSAGE || this.type.getJavaType() == JavaType.ENUM) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Field with message or enum type missing type_name.");
            }
            if (this.proto.getOptions().getPacked() && !this.isPackable()) {
                throw new DescriptorValidationException((GenericDescriptor)this, "[packed = true] can only be specified for repeated primitive fields.");
            }
            if (this.proto.hasDefaultValue()) {
                if (this.isRepeated()) {
                    throw new DescriptorValidationException((GenericDescriptor)this, "Repeated fields cannot have default values.");
                }
                try {
                    switch (this.type.ordinal()) {
                        case 4: 
                        case 14: 
                        case 16: {
                            this.defaultValue = TextFormat.parseInt32(this.proto.getDefaultValue());
                            return;
                        }
                        case 6: 
                        case 12: {
                            this.defaultValue = TextFormat.parseUInt32(this.proto.getDefaultValue());
                            return;
                        }
                        case 2: 
                        case 15: 
                        case 17: {
                            this.defaultValue = TextFormat.parseInt64(this.proto.getDefaultValue());
                            return;
                        }
                        case 3: 
                        case 5: {
                            this.defaultValue = TextFormat.parseUInt64(this.proto.getDefaultValue());
                            return;
                        }
                        case 1: {
                            if (this.proto.getDefaultValue().equals("inf")) {
                                this.defaultValue = Float.valueOf(Float.POSITIVE_INFINITY);
                                return;
                            }
                            if (this.proto.getDefaultValue().equals("-inf")) {
                                this.defaultValue = Float.valueOf(Float.NEGATIVE_INFINITY);
                                return;
                            }
                            if (this.proto.getDefaultValue().equals("nan")) {
                                this.defaultValue = Float.valueOf(Float.NaN);
                                return;
                            }
                            this.defaultValue = Float.valueOf(this.proto.getDefaultValue());
                            return;
                        }
                        case 0: {
                            if (this.proto.getDefaultValue().equals("inf")) {
                                this.defaultValue = Double.POSITIVE_INFINITY;
                                return;
                            }
                            if (this.proto.getDefaultValue().equals("-inf")) {
                                this.defaultValue = Double.NEGATIVE_INFINITY;
                                return;
                            }
                            if (this.proto.getDefaultValue().equals("nan")) {
                                this.defaultValue = Double.NaN;
                                return;
                            }
                            this.defaultValue = Double.valueOf(this.proto.getDefaultValue());
                            return;
                        }
                        case 7: {
                            this.defaultValue = Boolean.valueOf(this.proto.getDefaultValue());
                            return;
                        }
                        case 8: {
                            this.defaultValue = this.proto.getDefaultValue();
                            return;
                        }
                        case 11: {
                            try {
                                this.defaultValue = TextFormat.unescapeBytes(this.proto.getDefaultValue());
                                return;
                            }
                            catch (TextFormat.InvalidEscapeSequenceException e) {
                                throw new DescriptorValidationException(this, "Couldn't parse default value: " + e.getMessage(), e);
                            }
                        }
                        case 13: {
                            this.defaultValue = this.getEnumType().findValueByName(this.proto.getDefaultValue());
                            if (this.defaultValue != null) return;
                            throw new DescriptorValidationException((GenericDescriptor)this, "Unknown enum default value: \"" + this.proto.getDefaultValue() + '\"');
                        }
                        case 9: 
                        case 10: {
                            throw new DescriptorValidationException((GenericDescriptor)this, "Message type had default value.");
                        }
                    }
                    return;
                }
                catch (NumberFormatException e) {
                    throw new DescriptorValidationException(this, "Could not parse default value: \"" + this.proto.getDefaultValue() + '\"', e);
                }
            } else if (this.isRepeated()) {
                this.defaultValue = Collections.emptyList();
                return;
            } else {
                switch (this.type.getJavaType().ordinal()) {
                    case 7: {
                        this.defaultValue = this.getEnumType().getValue(0);
                        return;
                    }
                    case 8: {
                        this.defaultValue = null;
                        return;
                    }
                    default: {
                        this.defaultValue = this.type.getJavaType().defaultDefault;
                    }
                }
            }
        }

        private void setProto(DescriptorProtos.FieldDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
        }

        @Override
        public boolean internalMessageIsImmutable(Object message) {
            return message instanceof MessageLite;
        }

        @Override
        public void internalMergeFrom(Object to, Object from) {
            ((Message.Builder)to).mergeFrom((Message)from);
        }

        static {
            if (Type.types.length != DescriptorProtos.FieldDescriptorProto.Type.values().length) {
                throw new RuntimeException("descriptor.proto has a new declared type but Descriptors.java wasn't updated.");
            }
        }

        public static enum Type {
            DOUBLE(JavaType.DOUBLE),
            FLOAT(JavaType.FLOAT),
            INT64(JavaType.LONG),
            UINT64(JavaType.LONG),
            INT32(JavaType.INT),
            FIXED64(JavaType.LONG),
            FIXED32(JavaType.INT),
            BOOL(JavaType.BOOLEAN),
            STRING(JavaType.STRING),
            GROUP(JavaType.MESSAGE),
            MESSAGE(JavaType.MESSAGE),
            BYTES(JavaType.BYTE_STRING),
            UINT32(JavaType.INT),
            ENUM(JavaType.ENUM),
            SFIXED32(JavaType.INT),
            SFIXED64(JavaType.LONG),
            SINT32(JavaType.INT),
            SINT64(JavaType.LONG);

            private static final Type[] types;
            private final JavaType javaType;

            private Type(JavaType javaType) {
                this.javaType = javaType;
            }

            public DescriptorProtos.FieldDescriptorProto.Type toProto() {
                return DescriptorProtos.FieldDescriptorProto.Type.forNumber(this.ordinal() + 1);
            }

            public JavaType getJavaType() {
                return this.javaType;
            }

            public static Type valueOf(DescriptorProtos.FieldDescriptorProto.Type type) {
                return types[type.getNumber() - 1];
            }

            static {
                types = Type.values();
            }
        }

        public static enum JavaType {
            INT(0),
            LONG(0L),
            FLOAT(Float.valueOf(0.0f)),
            DOUBLE(0.0),
            BOOLEAN(false),
            STRING(""),
            BYTE_STRING(ByteString.EMPTY),
            ENUM(null),
            MESSAGE(null);

            private final Object defaultDefault;

            private JavaType(Object defaultDefault) {
                this.defaultDefault = defaultDefault;
            }
        }

        static final class RedactionState {
            private static final RedactionState FALSE_FALSE = new RedactionState(false, false);
            private static final RedactionState FALSE_TRUE = new RedactionState(false, true);
            private static final RedactionState TRUE_FALSE = new RedactionState(true, false);
            private static final RedactionState TRUE_TRUE = new RedactionState(true, true);
            final boolean redact;
            final boolean report;

            private RedactionState(boolean redact, boolean report) {
                this.redact = redact;
                this.report = report;
            }

            private static RedactionState of(boolean redact) {
                return RedactionState.of(redact, false);
            }

            private static RedactionState of(boolean redact, boolean report) {
                if (redact) {
                    return report ? TRUE_TRUE : TRUE_FALSE;
                }
                return report ? FALSE_TRUE : FALSE_FALSE;
            }

            private static RedactionState combine(RedactionState lhs, RedactionState rhs) {
                return RedactionState.of(lhs.redact || rhs.redact, rhs.report);
            }
        }
    }

    public static final class ServiceDescriptor
    extends GenericDescriptor {
        private final int index;
        private DescriptorProtos.ServiceDescriptorProto proto;
        private volatile DescriptorProtos.ServiceOptions options;
        private final String fullName;
        private final FileDescriptor file;
        private MethodDescriptor[] methods;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.ServiceDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        @Override
        public FileDescriptor getFile() {
            return this.file;
        }

        @Override
        GenericDescriptor getParent() {
            return this.file;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.ServiceOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.ServiceOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                ServiceDescriptor serviceDescriptor = this;
                synchronized (serviceDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public List<MethodDescriptor> getMethods() {
            return Collections.unmodifiableList(Arrays.asList(this.methods));
        }

        public int getMethodCount() {
            return this.methods.length;
        }

        public MethodDescriptor getMethod(int index) {
            return this.methods[index];
        }

        public MethodDescriptor findMethodByName(String name) {
            GenericDescriptor result = this.file.tables.findSymbol(this.fullName + '.' + name);
            if (result instanceof MethodDescriptor) {
                return (MethodDescriptor)result;
            }
            return null;
        }

        private ServiceDescriptor(DescriptorProtos.ServiceDescriptorProto proto, FileDescriptor file, int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.fullName = Descriptors.computeFullName(file, null, proto.getName());
            this.file = file;
            this.methods = new MethodDescriptor[proto.getMethodCount()];
            for (int i = 0; i < proto.getMethodCount(); ++i) {
                this.methods[i] = new MethodDescriptor(proto.getMethod(i), this, i);
            }
            file.tables.addSymbol(this);
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
            for (MethodDescriptor method : this.methods) {
                method.resolveAllFeatures();
            }
        }

        private void crossLink() throws DescriptorValidationException {
            for (MethodDescriptor method : this.methods) {
                method.crossLink();
            }
        }

        private void setProto(DescriptorProtos.ServiceDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
            for (int i = 0; i < this.methods.length; ++i) {
                this.methods[i].setProto(proto.getMethod(i));
            }
        }
    }

    public static final class EnumDescriptor
    extends GenericDescriptor
    implements Internal.EnumLiteMap<EnumValueDescriptor> {
        private final int index;
        private DescriptorProtos.EnumDescriptorProto proto;
        private volatile DescriptorProtos.EnumOptions options;
        private final String fullName;
        private final GenericDescriptor parent;
        private final EnumValueDescriptor[] values;
        private final EnumValueDescriptor[] valuesSortedByNumber;
        private final int distinctNumbers;
        private Map<Integer, WeakReference<EnumValueDescriptor>> unknownValues = null;
        private ReferenceQueue<EnumValueDescriptor> cleanupQueue = null;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.EnumDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        @Override
        public FileDescriptor getFile() {
            return this.parent.getFile();
        }

        @Override
        GenericDescriptor getParent() {
            return this.parent;
        }

        public boolean isPlaceholder() {
            return false;
        }

        public boolean isClosed() {
            return this.getFeatures().getEnumType() == DescriptorProtos.FeatureSet.EnumType.CLOSED;
        }

        public Descriptor getContainingType() {
            if (this.parent instanceof Descriptor) {
                return (Descriptor)this.parent;
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.EnumOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.EnumOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                EnumDescriptor enumDescriptor = this;
                synchronized (enumDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        public List<EnumValueDescriptor> getValues() {
            return Collections.unmodifiableList(Arrays.asList(this.values));
        }

        public int getValueCount() {
            return this.values.length;
        }

        public EnumValueDescriptor getValue(int index) {
            return this.values[index];
        }

        public boolean isReservedNumber(int number) {
            for (DescriptorProtos.EnumDescriptorProto.EnumReservedRange range : this.proto.getReservedRangeList()) {
                if (range.getStart() > number || number > range.getEnd()) continue;
                return true;
            }
            return false;
        }

        public boolean isReservedName(String name) {
            Internal.checkNotNull(name);
            for (String reservedName : this.proto.getReservedNameList()) {
                if (!reservedName.equals(name)) continue;
                return true;
            }
            return false;
        }

        public EnumValueDescriptor findValueByName(String name) {
            GenericDescriptor result = this.getFile().tables.findSymbol(this.fullName + '.' + name);
            if (result instanceof EnumValueDescriptor) {
                return (EnumValueDescriptor)result;
            }
            return null;
        }

        @Override
        public EnumValueDescriptor findValueByNumber(int number) {
            return (EnumValueDescriptor)Descriptors.binarySearch(this.valuesSortedByNumber, this.distinctNumbers, EnumValueDescriptor.NUMBER_GETTER, number);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public EnumValueDescriptor findValueByNumberCreatingIfUnknown(int number) {
            EnumValueDescriptor result = this.findValueByNumber(number);
            if (result != null) {
                return result;
            }
            EnumDescriptor enumDescriptor = this;
            synchronized (enumDescriptor) {
                if (this.cleanupQueue == null) {
                    this.cleanupQueue = new ReferenceQueue();
                    this.unknownValues = new HashMap<Integer, WeakReference<EnumValueDescriptor>>();
                } else {
                    UnknownEnumValueReference toClean;
                    while ((toClean = (UnknownEnumValueReference)this.cleanupQueue.poll()) != null) {
                        this.unknownValues.remove(toClean.number);
                    }
                }
                WeakReference<EnumValueDescriptor> reference = this.unknownValues.get(number);
                EnumValueDescriptor enumValueDescriptor = result = reference == null ? null : (EnumValueDescriptor)reference.get();
                if (result == null) {
                    result = new EnumValueDescriptor(this, number);
                    this.unknownValues.put(number, new UnknownEnumValueReference(number, result));
                }
            }
            return result;
        }

        int getUnknownEnumValueDescriptorCount() {
            return this.unknownValues.size();
        }

        private EnumDescriptor(DescriptorProtos.EnumDescriptorProto proto, FileDescriptor file, Descriptor parent, int index) throws DescriptorValidationException {
            this.parent = parent == null ? file : parent;
            this.index = index;
            this.proto = proto;
            this.fullName = Descriptors.computeFullName(file, parent, proto.getName());
            if (proto.getValueCount() == 0) {
                throw new DescriptorValidationException((GenericDescriptor)this, "Enums must contain at least one value.");
            }
            this.values = new EnumValueDescriptor[proto.getValueCount()];
            for (int i = 0; i < proto.getValueCount(); ++i) {
                this.values[i] = new EnumValueDescriptor(proto.getValue(i), this, i);
            }
            this.valuesSortedByNumber = (EnumValueDescriptor[])this.values.clone();
            Arrays.sort(this.valuesSortedByNumber, EnumValueDescriptor.BY_NUMBER);
            int j = 0;
            for (int i = 1; i < proto.getValueCount(); ++i) {
                EnumValueDescriptor oldValue = this.valuesSortedByNumber[j];
                EnumValueDescriptor newValue = this.valuesSortedByNumber[i];
                if (oldValue.getNumber() == newValue.getNumber()) continue;
                this.valuesSortedByNumber[++j] = newValue;
            }
            this.distinctNumbers = j + 1;
            Arrays.fill(this.valuesSortedByNumber, this.distinctNumbers, proto.getValueCount(), null);
            file.tables.addSymbol(this);
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
            for (EnumValueDescriptor value : this.values) {
                value.resolveAllFeatures();
            }
        }

        private void setProto(DescriptorProtos.EnumDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
            for (int i = 0; i < this.values.length; ++i) {
                this.values[i].setProto(proto.getValue(i));
            }
        }

        private static class UnknownEnumValueReference
        extends WeakReference<EnumValueDescriptor> {
            private final int number;

            private UnknownEnumValueReference(int number, EnumValueDescriptor descriptor) {
                super(descriptor);
                this.number = number;
            }
        }
    }

    private static final class FileDescriptorTables {
        private final Set<FileDescriptor> dependencies;
        private final boolean allowUnknownDependencies;
        private final Map<String, GenericDescriptor> descriptorsByName = new HashMap<String, GenericDescriptor>();

        FileDescriptorTables(FileDescriptor[] dependencies, boolean allowUnknownDependencies) {
            this.dependencies = Collections.newSetFromMap(new IdentityHashMap(dependencies.length));
            this.allowUnknownDependencies = allowUnknownDependencies;
            for (FileDescriptor dependency : dependencies) {
                this.dependencies.add(dependency);
                this.importPublicDependencies(dependency);
            }
            for (FileDescriptor dependency : this.dependencies) {
                try {
                    this.addPackage(dependency.getPackage(), dependency);
                }
                catch (DescriptorValidationException e) {
                    throw new AssertionError((Object)e);
                }
            }
        }

        private void importPublicDependencies(FileDescriptor file) {
            for (FileDescriptor dependency : file.getPublicDependencies()) {
                if (!this.dependencies.add(dependency)) continue;
                this.importPublicDependencies(dependency);
            }
        }

        GenericDescriptor findSymbol(String fullName) {
            return this.findSymbol(fullName, SearchFilter.ALL_SYMBOLS);
        }

        GenericDescriptor findSymbol(String fullName, SearchFilter filter) {
            GenericDescriptor result = this.descriptorsByName.get(fullName);
            if (result != null && (filter == SearchFilter.ALL_SYMBOLS || filter == SearchFilter.TYPES_ONLY && this.isType(result) || filter == SearchFilter.AGGREGATES_ONLY && this.isAggregate(result))) {
                return result;
            }
            for (FileDescriptor dependency : this.dependencies) {
                result = ((FileDescriptor)dependency).tables.descriptorsByName.get(fullName);
                if (result == null || filter != SearchFilter.ALL_SYMBOLS && (filter != SearchFilter.TYPES_ONLY || !this.isType(result)) && (filter != SearchFilter.AGGREGATES_ONLY || !this.isAggregate(result))) continue;
                return result;
            }
            return null;
        }

        boolean isType(GenericDescriptor descriptor) {
            return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor;
        }

        boolean isAggregate(GenericDescriptor descriptor) {
            return descriptor instanceof Descriptor || descriptor instanceof EnumDescriptor || descriptor instanceof PackageDescriptor || descriptor instanceof ServiceDescriptor;
        }

        GenericDescriptor lookupSymbol(String name, GenericDescriptor relativeTo, SearchFilter filter) throws DescriptorValidationException {
            GenericDescriptor result;
            String fullname;
            if (name.startsWith(".")) {
                fullname = name.substring(1);
                result = this.findSymbol(fullname, filter);
            } else {
                int firstPartLength = name.indexOf(46);
                String firstPart = firstPartLength == -1 ? name : name.substring(0, firstPartLength);
                StringBuilder scopeToTry = new StringBuilder(relativeTo.getFullName());
                while (true) {
                    int dotpos;
                    if ((dotpos = scopeToTry.lastIndexOf(".")) == -1) {
                        fullname = name;
                        result = this.findSymbol(name, filter);
                        break;
                    }
                    scopeToTry.setLength(dotpos + 1);
                    scopeToTry.append(firstPart);
                    result = this.findSymbol(scopeToTry.toString(), SearchFilter.AGGREGATES_ONLY);
                    if (result != null) {
                        if (firstPartLength != -1) {
                            scopeToTry.setLength(dotpos + 1);
                            scopeToTry.append(name);
                            result = this.findSymbol(scopeToTry.toString(), filter);
                        }
                        fullname = scopeToTry.toString();
                        break;
                    }
                    scopeToTry.setLength(dotpos);
                }
            }
            if (result == null) {
                if (this.allowUnknownDependencies && filter == SearchFilter.TYPES_ONLY) {
                    logger.warning("The descriptor for message type \"" + name + "\" cannot be found and a placeholder is created for it");
                    result = new Descriptor(fullname);
                    this.dependencies.add(result.getFile());
                    return result;
                }
                throw new DescriptorValidationException(relativeTo, '\"' + name + "\" is not defined.");
            }
            return result;
        }

        void addSymbol(GenericDescriptor descriptor) throws DescriptorValidationException {
            FileDescriptorTables.validateSymbolName(descriptor);
            String fullName = descriptor.getFullName();
            GenericDescriptor old = this.descriptorsByName.put(fullName, descriptor);
            if (old != null) {
                this.descriptorsByName.put(fullName, old);
                if (descriptor.getFile() == old.getFile()) {
                    int dotpos = fullName.lastIndexOf(46);
                    if (dotpos == -1) {
                        throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined.");
                    }
                    throw new DescriptorValidationException(descriptor, '\"' + fullName.substring(dotpos + 1) + "\" is already defined in \"" + fullName.substring(0, dotpos) + "\".");
                }
                throw new DescriptorValidationException(descriptor, '\"' + fullName + "\" is already defined in file \"" + old.getFile().getName() + "\".");
            }
        }

        void addPackage(String fullName, FileDescriptor file) throws DescriptorValidationException {
            String name;
            int dotpos = fullName.lastIndexOf(46);
            if (dotpos == -1) {
                name = fullName;
            } else {
                this.addPackage(fullName.substring(0, dotpos), file);
                name = fullName.substring(dotpos + 1);
            }
            GenericDescriptor old = this.descriptorsByName.put(fullName, new PackageDescriptor(name, fullName, file));
            if (old != null) {
                this.descriptorsByName.put(fullName, old);
                if (!(old instanceof PackageDescriptor)) {
                    throw new DescriptorValidationException(file, '\"' + name + "\" is already defined (as something other than a package) in file \"" + old.getFile().getName() + "\".");
                }
            }
        }

        static void validateSymbolName(GenericDescriptor descriptor) throws DescriptorValidationException {
            String name = descriptor.getName();
            if (name.length() == 0) {
                throw new DescriptorValidationException(descriptor, "Missing name.");
            }
            for (int i = 0; i < name.length(); ++i) {
                char c = name.charAt(i);
                if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_' || '0' <= c && c <= '9' && i > 0) continue;
                throw new DescriptorValidationException(descriptor, '\"' + name + "\" is not a valid identifier.");
            }
        }

        static enum SearchFilter {
            TYPES_ONLY,
            AGGREGATES_ONLY,
            ALL_SYMBOLS;

        }

        private static final class PackageDescriptor
        extends GenericDescriptor {
            private final String name;
            private final String fullName;
            private final FileDescriptor file;

            @Override
            public Message toProto() {
                return this.file.toProto();
            }

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public String getFullName() {
                return this.fullName;
            }

            @Override
            GenericDescriptor getParent() {
                return this.file;
            }

            @Override
            public FileDescriptor getFile() {
                return this.file;
            }

            PackageDescriptor(String name, String fullName, FileDescriptor file) {
                this.file = file;
                this.fullName = fullName;
                this.name = name;
            }
        }
    }

    public static class DescriptorValidationException
    extends Exception {
        private static final long serialVersionUID = 5750205775490483148L;
        private final String name;
        private final Message proto;
        private final String description;

        public String getProblemSymbolName() {
            return this.name;
        }

        public Message getProblemProto() {
            return this.proto;
        }

        public String getDescription() {
            return this.description;
        }

        private DescriptorValidationException(GenericDescriptor problemDescriptor, String description) {
            super(problemDescriptor.getFullName() + ": " + description);
            this.name = problemDescriptor.getFullName();
            this.proto = problemDescriptor.toProto();
            this.description = description;
        }

        private DescriptorValidationException(GenericDescriptor problemDescriptor, String description, Throwable cause) {
            this(problemDescriptor, description);
            this.initCause(cause);
        }

        private DescriptorValidationException(FileDescriptor problemDescriptor, String description) {
            super(problemDescriptor.getName() + ": " + description);
            this.name = problemDescriptor.getName();
            this.proto = problemDescriptor.toProto();
            this.description = description;
        }
    }

    public static abstract class GenericDescriptor {
        volatile DescriptorProtos.FeatureSet features;

        private GenericDescriptor() {
        }

        public abstract Message toProto();

        public abstract String getName();

        public abstract String getFullName();

        public abstract FileDescriptor getFile();

        abstract GenericDescriptor getParent();

        void resolveFeatures(DescriptorProtos.FeatureSet unresolvedFeatures) throws DescriptorValidationException {
            DescriptorProtos.FeatureSet.Builder features;
            boolean hasPossibleUnknownJavaFeature;
            GenericDescriptor parent = this.getParent();
            DescriptorProtos.FeatureSet inferredLegacyFeatures = null;
            if (parent != null && unresolvedFeatures.equals(DescriptorProtos.FeatureSet.getDefaultInstance()) && (inferredLegacyFeatures = this.inferLegacyProtoFeatures()).equals(DescriptorProtos.FeatureSet.getDefaultInstance())) {
                this.features = parent.features;
                this.validateFeatures();
                return;
            }
            boolean hasPossibleCustomJavaFeature = false;
            for (FieldDescriptor f : unresolvedFeatures.getExtensionFields().keySet()) {
                if (f.getNumber() != JavaFeaturesProto.java_.getNumber() || f == JavaFeaturesProto.java_.getDescriptor()) continue;
                hasPossibleCustomJavaFeature = true;
                break;
            }
            boolean bl = hasPossibleUnknownJavaFeature = !unresolvedFeatures.getUnknownFields().isEmpty() && unresolvedFeatures.getUnknownFields().hasField(JavaFeaturesProto.java_.getNumber());
            if (hasPossibleCustomJavaFeature || hasPossibleUnknownJavaFeature) {
                ExtensionRegistry registry = ExtensionRegistry.newInstance();
                registry.add(JavaFeaturesProto.java_);
                ByteString bytes = unresolvedFeatures.toByteString();
                try {
                    unresolvedFeatures = DescriptorProtos.FeatureSet.parseFrom(bytes, (ExtensionRegistryLite)registry);
                }
                catch (InvalidProtocolBufferException e) {
                    throw new DescriptorValidationException(this, "Failed to parse features with Java feature extension registry.", e);
                }
            }
            if (parent == null) {
                DescriptorProtos.Edition edition = this.getFile().getEdition();
                features = Descriptors.getEditionDefaults(edition).toBuilder();
            } else {
                features = parent.features.toBuilder();
            }
            if (inferredLegacyFeatures == null) {
                inferredLegacyFeatures = this.inferLegacyProtoFeatures();
            }
            features.mergeFrom(inferredLegacyFeatures);
            features.mergeFrom(unresolvedFeatures);
            this.features = Descriptors.internFeatures(features.build());
            this.validateFeatures();
        }

        DescriptorProtos.FeatureSet inferLegacyProtoFeatures() {
            return DescriptorProtos.FeatureSet.getDefaultInstance();
        }

        void validateFeatures() throws DescriptorValidationException {
        }

        DescriptorProtos.FeatureSet getFeatures() {
            if (this.features == null && (this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO2 || this.getFile().getEdition() == DescriptorProtos.Edition.EDITION_PROTO3)) {
                this.getFile().resolveAllFeaturesImmutable();
            }
            if (this.features == null) {
                throw new NullPointerException(String.format("Features not yet loaded for %s.", this.getFullName()));
            }
            return this.features;
        }
    }

    public static final class MethodDescriptor
    extends GenericDescriptor {
        private final int index;
        private DescriptorProtos.MethodDescriptorProto proto;
        private volatile DescriptorProtos.MethodOptions options;
        private final String fullName;
        private final ServiceDescriptor service;
        private Descriptor inputType;
        private Descriptor outputType;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.MethodDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        @Override
        public FileDescriptor getFile() {
            return this.service.file;
        }

        @Override
        GenericDescriptor getParent() {
            return this.service;
        }

        public ServiceDescriptor getService() {
            return this.service;
        }

        public Descriptor getInputType() {
            return this.inputType;
        }

        public Descriptor getOutputType() {
            return this.outputType;
        }

        public boolean isClientStreaming() {
            return this.proto.getClientStreaming();
        }

        public boolean isServerStreaming() {
            return this.proto.getServerStreaming();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.MethodOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.MethodOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                MethodDescriptor methodDescriptor = this;
                synchronized (methodDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        private MethodDescriptor(DescriptorProtos.MethodDescriptorProto proto, ServiceDescriptor parent, int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.service = parent;
            this.fullName = parent.getFullName() + '.' + proto.getName();
            this.service.file.tables.addSymbol(this);
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
        }

        private void crossLink() throws DescriptorValidationException {
            GenericDescriptor input = this.getFile().tables.lookupSymbol(this.proto.getInputType(), this, FileDescriptorTables.SearchFilter.TYPES_ONLY);
            if (!(input instanceof Descriptor)) {
                throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getInputType() + "\" is not a message type.");
            }
            this.inputType = (Descriptor)input;
            GenericDescriptor output = this.getFile().tables.lookupSymbol(this.proto.getOutputType(), this, FileDescriptorTables.SearchFilter.TYPES_ONLY);
            if (!(output instanceof Descriptor)) {
                throw new DescriptorValidationException((GenericDescriptor)this, '\"' + this.proto.getOutputType() + "\" is not a message type.");
            }
            this.outputType = (Descriptor)output;
        }

        private void setProto(DescriptorProtos.MethodDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
        }
    }

    public static final class EnumValueDescriptor
    extends GenericDescriptor
    implements Internal.EnumLite {
        static final Comparator<EnumValueDescriptor> BY_NUMBER = new Comparator<EnumValueDescriptor>(){

            @Override
            public int compare(EnumValueDescriptor o1, EnumValueDescriptor o2) {
                return Integer.compare(o1.getNumber(), o2.getNumber());
            }
        };
        static final ToIntFunction<EnumValueDescriptor> NUMBER_GETTER = EnumValueDescriptor::getNumber;
        private final int index;
        private DescriptorProtos.EnumValueDescriptorProto proto;
        private volatile DescriptorProtos.EnumValueOptions options;
        private final String fullName;
        private final EnumDescriptor type;

        public int getIndex() {
            return this.index;
        }

        @Override
        public DescriptorProtos.EnumValueDescriptorProto toProto() {
            return this.proto;
        }

        @Override
        public String getName() {
            return this.proto.getName();
        }

        @Override
        public int getNumber() {
            return this.proto.getNumber();
        }

        public String toString() {
            return this.proto.getName();
        }

        @Override
        public String getFullName() {
            return this.fullName;
        }

        @Override
        public FileDescriptor getFile() {
            return this.type.getFile();
        }

        @Override
        GenericDescriptor getParent() {
            return this.type;
        }

        public EnumDescriptor getType() {
            return this.type;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public DescriptorProtos.EnumValueOptions getOptions() {
            if (this.options == null) {
                DescriptorProtos.EnumValueOptions strippedOptions = this.proto.getOptions();
                if (strippedOptions.hasFeatures()) {
                    strippedOptions = strippedOptions.toBuilder().clearFeatures().build();
                }
                EnumValueDescriptor enumValueDescriptor = this;
                synchronized (enumValueDescriptor) {
                    if (this.options == null) {
                        this.options = strippedOptions;
                    }
                }
            }
            return this.options;
        }

        private EnumValueDescriptor(DescriptorProtos.EnumValueDescriptorProto proto, EnumDescriptor parent, int index) throws DescriptorValidationException {
            this.index = index;
            this.proto = proto;
            this.type = parent;
            this.fullName = parent.getFullName() + '.' + proto.getName();
            this.type.getFile().tables.addSymbol(this);
        }

        private EnumValueDescriptor(EnumDescriptor parent, Integer number) {
            String name = "UNKNOWN_ENUM_VALUE_" + parent.getName() + "_" + number;
            DescriptorProtos.EnumValueDescriptorProto proto = DescriptorProtos.EnumValueDescriptorProto.newBuilder().setName(name).setNumber(number).build();
            this.index = -1;
            this.proto = proto;
            this.type = parent;
            this.fullName = parent.getFullName() + '.' + proto.getName();
        }

        private void resolveAllFeatures() throws DescriptorValidationException {
            this.resolveFeatures(this.proto.getOptions().getFeatures());
        }

        private void setProto(DescriptorProtos.EnumValueDescriptorProto proto) throws DescriptorValidationException {
            this.proto = proto;
            this.options = null;
            this.resolveFeatures(proto.getOptions().getFeatures());
        }
    }
}

