/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.KeysetManager;
import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.KeysetWriter;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.PrivateKey;
import com.google.crypto.tink.Registry;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.TinkProtoParametersFormat;
import com.google.crypto.tink.Util;
import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.config.GlobalTinkFlags;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MonitoringClient;
import com.google.crypto.tink.internal.MutableKeyCreationRegistry;
import com.google.crypto.tink.internal.MutableMonitoringRegistry;
import com.google.crypto.tink.internal.MutableParametersRegistry;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.crypto.tink.tinkkey.KeyHandle;
import com.google.crypto.tink.tinkkey.TinkKey;
import com.google.crypto.tink.tinkkey.internal.InternalKeyHandle;
import com.google.crypto.tink.tinkkey.internal.ProtoKey;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.InlineMe;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class KeysetHandle
implements KeysetHandleInterface {
    private final List<Entry> entries;
    private final MonitoringAnnotations annotations;
    @Nullable
    private final KeysetHandle unmonitoredHandle;

    private static KeyStatus parseStatusWithDisabledFallback(KeyStatusType in) {
        switch (in) {
            case ENABLED: {
                return KeyStatus.ENABLED;
            }
            case DESTROYED: {
                return KeyStatus.DESTROYED;
            }
        }
        return KeyStatus.DISABLED;
    }

    private static boolean isValidKeyStatusType(KeyStatusType in) {
        switch (in) {
            case ENABLED: 
            case DESTROYED: 
            case DISABLED: {
                return true;
            }
        }
        return false;
    }

    private static KeyStatusType serializeStatus(KeyStatus in) {
        if (KeyStatus.ENABLED.equals(in)) {
            return KeyStatusType.ENABLED;
        }
        if (KeyStatus.DISABLED.equals(in)) {
            return KeyStatusType.DISABLED;
        }
        if (KeyStatus.DESTROYED.equals(in)) {
            return KeyStatusType.DESTROYED;
        }
        throw new IllegalStateException("Unknown key status");
    }

    private static List<Entry> getEntriesFromKeyset(Keyset keyset) throws GeneralSecurityException {
        ArrayList<Entry> result = new ArrayList<Entry>(keyset.getKeyCount());
        for (Keyset.Key protoKey : keyset.getKeyList()) {
            boolean keyParsingFailed;
            Key key;
            int id = protoKey.getKeyId();
            try {
                key = KeysetHandle.toKey(protoKey);
                keyParsingFailed = false;
            }
            catch (GeneralSecurityException e) {
                if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
                    throw e;
                }
                key = new LegacyProtoKey(KeysetHandle.toProtoKeySerialization(protoKey), InsecureSecretKeyAccess.get());
                keyParsingFailed = true;
            }
            if (GlobalTinkFlags.validateKeysetsOnParsing.getValue() && !KeysetHandle.isValidKeyStatusType(protoKey.getStatus())) {
                throw new GeneralSecurityException("Parsing of a single key failed (wrong status) and Tink is configured via validateKeysetsOnParsing to reject such keysets.");
            }
            result.add(new Entry(key, protoKey.getStatus(), id, id == keyset.getPrimaryKeyId(), keyParsingFailed, Entry.NO_LOGGING));
        }
        return Collections.unmodifiableList(result);
    }

    private Entry entryByIndex(int i) {
        Entry entry = this.entries.get(i);
        if (!KeysetHandle.isValidKeyStatusType(entry.keyStatusType)) {
            throw new IllegalStateException("Keyset-Entry at position " + i + " has wrong status");
        }
        if (entry.keyParsingFailed) {
            throw new IllegalStateException("Keyset-Entry at position " + i + " didn't parse correctly");
        }
        return this.entries.get(i);
    }

    public static Builder.Entry importKey(Key key) {
        Builder.Entry importedEntry = new Builder.Entry(key);
        Integer requirement = key.getIdRequirementOrNull();
        if (requirement != null) {
            importedEntry.withFixedId(requirement);
        }
        return importedEntry;
    }

    public static Builder.Entry generateEntryFromParametersName(String parametersName) throws GeneralSecurityException {
        Parameters parameters = MutableParametersRegistry.globalInstance().get(parametersName);
        return new Builder.Entry(parameters);
    }

    public static Builder.Entry generateEntryFromParameters(Parameters parameters) {
        return new Builder.Entry(parameters);
    }

    private KeysetHandle getUnmonitoredHandle() {
        return this.unmonitoredHandle == null ? this : this.unmonitoredHandle;
    }

    private static void validateNoDuplicateIds(List<Entry> entries) throws GeneralSecurityException {
        HashSet<Integer> idsSoFar = new HashSet<Integer>();
        boolean foundPrimary = false;
        for (Entry e : entries) {
            if (idsSoFar.contains(e.getId())) {
                throw new GeneralSecurityException("KeyID " + e.getId() + " is duplicated in the keyset, and Tink is configured to reject such keysets with the flag validateKeysetsOnParsing.");
            }
            idsSoFar.add(e.getId());
            if (!e.isPrimary()) continue;
            foundPrimary = true;
        }
        if (!foundPrimary) {
            throw new GeneralSecurityException("Primary key id not found in keyset, and Tink is configured to reject such keysets with the flag validateKeysetsOnParsing.");
        }
    }

    private KeysetHandle(List<Entry> entries, MonitoringAnnotations annotations) throws GeneralSecurityException {
        this.entries = entries;
        this.annotations = annotations;
        if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
            KeysetHandle.validateNoDuplicateIds(entries);
        }
        this.unmonitoredHandle = null;
    }

    private KeysetHandle(List<Entry> entries, MonitoringAnnotations annotations, KeysetHandle unmonitoredHandle) {
        this.entries = entries;
        this.annotations = annotations;
        this.unmonitoredHandle = unmonitoredHandle;
    }

    private static KeysetHandle addMonitoringIfNeeded(KeysetHandle unmonitoredHandle) {
        MonitoringAnnotations annotations = unmonitoredHandle.annotations;
        if (annotations.isEmpty()) {
            return unmonitoredHandle;
        }
        Entry.EntryConsumer keyExportLogger = entryToLog -> {
            MonitoringClient client = MutableMonitoringRegistry.globalInstance().getMonitoringClient();
            client.createLogger(unmonitoredHandle, annotations, "keyset_handle", "get_key").logKeyExport(entryToLog.getId());
        };
        ArrayList<Entry> monitoredEntries = new ArrayList<Entry>(unmonitoredHandle.entries.size());
        for (Entry e : unmonitoredHandle.entries) {
            monitoredEntries.add(new Entry(e.key, e.keyStatusType, e.id, e.isPrimary, e.keyParsingFailed, keyExportLogger));
        }
        return new KeysetHandle(monitoredEntries, annotations, unmonitoredHandle);
    }

    static final KeysetHandle fromKeyset(Keyset keyset) throws GeneralSecurityException {
        KeysetHandle.assertEnoughKeyMaterial(keyset);
        List<Entry> entries = KeysetHandle.getEntriesFromKeyset(keyset);
        return new KeysetHandle(entries, MonitoringAnnotations.EMPTY);
    }

    static final KeysetHandle fromKeysetAndAnnotations(Keyset keyset, MonitoringAnnotations annotations) throws GeneralSecurityException {
        KeysetHandle.assertEnoughKeyMaterial(keyset);
        List<Entry> entries = KeysetHandle.getEntriesFromKeyset(keyset);
        return KeysetHandle.addMonitoringIfNeeded(new KeysetHandle(entries, annotations));
    }

    Keyset getKeyset() {
        try {
            Keyset.Builder builder = Keyset.newBuilder();
            for (Entry entry : this.entries) {
                Keyset.Key protoKey = KeysetHandle.createKeysetKey(entry.getKey(), entry.keyStatusType, entry.getId());
                builder.addKey(protoKey);
                if (!entry.isPrimary()) continue;
                builder.setPrimaryKeyId(entry.getId());
            }
            return builder.build();
        }
        catch (GeneralSecurityException e) {
            throw new TinkBugException(e);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(KeysetHandle handle) {
        Builder builder = new Builder();
        for (int i = 0; i < handle.size(); ++i) {
            Entry entry;
            try {
                entry = handle.getAt(i);
            }
            catch (IllegalStateException e) {
                builder.setErrorToThrow(new GeneralSecurityException("Keyset-Entry in original keyset at position " + i + " has wrong status or key parsing failed", e));
                break;
            }
            Builder.Entry builderEntry = KeysetHandle.importKey(entry.getKey()).withFixedId(entry.getId());
            builderEntry.setStatus(entry.getStatus());
            if (entry.isPrimary()) {
                builderEntry.makePrimary();
            }
            builder.addEntry(builderEntry);
        }
        return builder;
    }

    @Override
    public Entry getPrimary() {
        for (Entry entry : this.entries) {
            if (entry == null || !entry.isPrimary()) continue;
            if (entry.getStatus() != KeyStatus.ENABLED) {
                throw new IllegalStateException("Keyset has primary which isn't enabled");
            }
            return entry;
        }
        throw new IllegalStateException("Keyset has no valid primary");
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public Entry getAt(int i) {
        if (i < 0 || i >= this.size()) {
            throw new IndexOutOfBoundsException("Invalid index " + i + " for keyset of size " + this.size());
        }
        return this.entryByIndex(i);
    }

    @Deprecated
    public List<KeyHandle> getKeys() {
        ArrayList<InternalKeyHandle> result = new ArrayList<InternalKeyHandle>();
        Keyset keyset = this.getKeyset();
        for (Keyset.Key key : keyset.getKeyList()) {
            KeyData keyData = key.getKeyData();
            result.add(new InternalKeyHandle((TinkKey)new ProtoKey(keyData, com.google.crypto.tink.KeyTemplate.fromProto(key.getOutputPrefixType())), key.getStatus(), key.getKeyId()));
        }
        return Collections.unmodifiableList(result);
    }

    @Deprecated
    public KeysetInfo getKeysetInfo() {
        Keyset keyset = this.getKeyset();
        return Util.getKeysetInfo(keyset);
    }

    public static final KeysetHandle generateNew(Parameters parameters) throws GeneralSecurityException {
        return KeysetHandle.newBuilder().addEntry(KeysetHandle.generateEntryFromParameters(parameters).withRandomId().makePrimary()).build();
    }

    @Deprecated
    public static final KeysetHandle generateNew(KeyTemplate keyTemplate) throws GeneralSecurityException {
        return KeysetHandle.generateNew(TinkProtoParametersFormat.parse(keyTemplate.toByteArray()));
    }

    public static final KeysetHandle generateNew(com.google.crypto.tink.KeyTemplate keyTemplate) throws GeneralSecurityException {
        return KeysetHandle.generateNew(keyTemplate.toParameters());
    }

    @Deprecated
    public static final KeysetHandle createFromKey(KeyHandle keyHandle, KeyAccess access) throws GeneralSecurityException {
        KeysetManager km = KeysetManager.withEmptyKeyset().add(keyHandle);
        km.setPrimary(km.getKeysetHandle().getKeysetInfo().getKeyInfo(0).getKeyId());
        return km.getKeysetHandle();
    }

    @Deprecated
    public static final KeysetHandle read(KeysetReader reader, Aead masterKey) throws GeneralSecurityException, IOException {
        return KeysetHandle.readWithAssociatedData(reader, masterKey, new byte[0]);
    }

    @Deprecated
    public static final KeysetHandle readWithAssociatedData(KeysetReader reader, Aead masterKey, byte[] associatedData) throws GeneralSecurityException, IOException {
        EncryptedKeyset encryptedKeyset = reader.readEncrypted();
        KeysetHandle.assertEnoughEncryptedKeyMaterial(encryptedKeyset);
        return KeysetHandle.fromKeyset(KeysetHandle.decrypt(encryptedKeyset, masterKey, associatedData));
    }

    @Deprecated
    public static final KeysetHandle readNoSecret(KeysetReader reader) throws GeneralSecurityException, IOException {
        byte[] serializedKeyset;
        try {
            serializedKeyset = reader.read().toByteArray();
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("invalid keyset");
        }
        return KeysetHandle.readNoSecret(serializedKeyset);
    }

    @Deprecated
    public static final KeysetHandle readNoSecret(byte[] serialized) throws GeneralSecurityException {
        try {
            Keyset keyset = Keyset.parseFrom(serialized, ExtensionRegistryLite.getEmptyRegistry());
            KeysetHandle.assertNoSecretKeyMaterial(keyset);
            return KeysetHandle.fromKeyset(keyset);
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("invalid keyset");
        }
    }

    @Deprecated
    public void write(KeysetWriter keysetWriter, Aead masterKey) throws GeneralSecurityException, IOException {
        this.writeWithAssociatedData(keysetWriter, masterKey, new byte[0]);
    }

    @Deprecated
    public void writeWithAssociatedData(KeysetWriter keysetWriter, Aead masterKey, byte[] associatedData) throws GeneralSecurityException, IOException {
        Keyset keyset = this.getKeyset();
        EncryptedKeyset encryptedKeyset = KeysetHandle.encrypt(keyset, masterKey, associatedData);
        keysetWriter.write(encryptedKeyset);
    }

    @Deprecated
    public void writeNoSecret(KeysetWriter writer) throws GeneralSecurityException, IOException {
        Keyset keyset = this.getKeyset();
        KeysetHandle.assertNoSecretKeyMaterial(keyset);
        writer.write(keyset);
    }

    private static EncryptedKeyset encrypt(Keyset keyset, Aead masterKey, byte[] associatedData) throws GeneralSecurityException {
        byte[] encryptedKeyset = masterKey.encrypt(keyset.toByteArray(), associatedData);
        return EncryptedKeyset.newBuilder().setEncryptedKeyset(ByteString.copyFrom(encryptedKeyset)).setKeysetInfo(Util.getKeysetInfo(keyset)).build();
    }

    private static Keyset decrypt(EncryptedKeyset encryptedKeyset, Aead masterKey, byte[] associatedData) throws GeneralSecurityException {
        try {
            Keyset keyset = Keyset.parseFrom(masterKey.decrypt(encryptedKeyset.getEncryptedKeyset().toByteArray(), associatedData), ExtensionRegistryLite.getEmptyRegistry());
            KeysetHandle.assertEnoughKeyMaterial(keyset);
            return keyset;
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("invalid keyset, corrupted key material");
        }
    }

    public KeysetHandle getPublicKeysetHandle() throws GeneralSecurityException {
        Keyset keyset = this.getKeyset();
        ArrayList<Entry> publicEntries = new ArrayList<Entry>(this.entries.size());
        int i = 0;
        for (Entry entry : this.entries) {
            Entry publicEntry;
            if (entry.getKey() instanceof PrivateKey) {
                Key publicKey = ((PrivateKey)((Object)entry.getKey())).getPublicKey();
                publicEntry = new Entry(publicKey, entry.keyStatusType, entry.getId(), entry.isPrimary(), false, Entry.NO_LOGGING);
                KeysetHandle.validateKeyId(publicKey, entry.getId());
            } else {
                boolean keyParsingFailed;
                Key publicKey;
                Keyset.Key protoKey = keyset.getKey(i);
                KeyData keyData = KeysetHandle.getPublicKeyDataFromRegistry(protoKey.getKeyData());
                Keyset.Key publicProtoKey = protoKey.toBuilder().setKeyData(keyData).build();
                try {
                    publicKey = KeysetHandle.toKey(publicProtoKey);
                    keyParsingFailed = false;
                }
                catch (GeneralSecurityException e) {
                    if (GlobalTinkFlags.validateKeysetsOnParsing.getValue()) {
                        throw e;
                    }
                    publicKey = new LegacyProtoKey(KeysetHandle.toProtoKeySerialization(publicProtoKey), InsecureSecretKeyAccess.get());
                    keyParsingFailed = true;
                }
                int id = publicProtoKey.getKeyId();
                publicEntry = new Entry(publicKey, entry.keyStatusType, id, id == keyset.getPrimaryKeyId(), keyParsingFailed, Entry.NO_LOGGING);
            }
            publicEntries.add(publicEntry);
            ++i;
        }
        return KeysetHandle.addMonitoringIfNeeded(new KeysetHandle(publicEntries, this.annotations));
    }

    private static KeyData getPublicKeyDataFromRegistry(KeyData privateKeyData) throws GeneralSecurityException {
        if (privateKeyData.getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) {
            throw new GeneralSecurityException("The keyset contains a non-private key");
        }
        KeyData publicKeyData = Registry.getPublicKeyData(privateKeyData.getTypeUrl(), privateKeyData.getValue());
        return publicKeyData;
    }

    public String toString() {
        return this.getKeysetInfo().toString();
    }

    private static void assertNoSecretKeyMaterial(Keyset keyset) throws GeneralSecurityException {
        for (Keyset.Key key : keyset.getKeyList()) {
            if (key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.UNKNOWN_KEYMATERIAL && key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.SYMMETRIC && key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) continue;
            throw new GeneralSecurityException(String.format("keyset contains key material of type %s for type url %s", key.getKeyData().getKeyMaterialType().name(), key.getKeyData().getTypeUrl()));
        }
    }

    private static void assertEnoughKeyMaterial(Keyset keyset) throws GeneralSecurityException {
        if (keyset == null || keyset.getKeyCount() <= 0) {
            throw new GeneralSecurityException("empty keyset");
        }
    }

    private static void assertEnoughEncryptedKeyMaterial(EncryptedKeyset keyset) throws GeneralSecurityException {
        if (keyset == null || keyset.getEncryptedKeyset().size() == 0) {
            throw new GeneralSecurityException("empty keyset");
        }
    }

    private <P> P getPrimitiveInternal(InternalConfiguration config, Class<P> classObject) throws GeneralSecurityException {
        Keyset keyset = this.getUnmonitoredHandle().getKeyset();
        Util.validateKeyset(keyset);
        for (int i = 0; i < this.size(); ++i) {
            if (!this.entries.get(i).keyParsingFailed && KeysetHandle.isValidKeyStatusType(this.entries.get(i).keyStatusType)) continue;
            Keyset.Key protoKey = keyset.getKey(i);
            throw new GeneralSecurityException("Key parsing of key with index " + i + " and type_url " + protoKey.getKeyData().getTypeUrl() + " failed, unable to get primitive");
        }
        return config.wrap(this.getUnmonitoredHandle(), this.annotations, classObject);
    }

    public <P> P getPrimitive(Configuration configuration, Class<P> targetClassObject) throws GeneralSecurityException {
        if (!(configuration instanceof InternalConfiguration)) {
            throw new GeneralSecurityException("Currently only subclasses of InternalConfiguration are accepted");
        }
        InternalConfiguration internalConfig = (InternalConfiguration)configuration;
        return this.getPrimitiveInternal(internalConfig, targetClassObject);
    }

    @Deprecated
    @InlineMe(replacement="this.getPrimitive(RegistryConfiguration.get(), targetClassObject)", imports={"com.google.crypto.tink.RegistryConfiguration"})
    public <P> P getPrimitive(Class<P> targetClassObject) throws GeneralSecurityException {
        return this.getPrimitive(RegistryConfiguration.get(), targetClassObject);
    }

    @Deprecated
    public KeyHandle primaryKey() throws GeneralSecurityException {
        Keyset keyset = this.getKeyset();
        int primaryKeyId = keyset.getPrimaryKeyId();
        for (Keyset.Key key : keyset.getKeyList()) {
            if (key.getKeyId() != primaryKeyId) continue;
            return new InternalKeyHandle((TinkKey)new ProtoKey(key.getKeyData(), com.google.crypto.tink.KeyTemplate.fromProto(key.getOutputPrefixType())), key.getStatus(), key.getKeyId());
        }
        throw new GeneralSecurityException("No primary key found in keyset.");
    }

    public boolean equalsKeyset(KeysetHandle other) {
        if (this.size() != other.size()) {
            return false;
        }
        boolean primaryFound = false;
        for (int i = 0; i < this.size(); ++i) {
            Entry thisEntry = this.entries.get(i);
            Entry otherEntry = other.entries.get(i);
            if (thisEntry.keyParsingFailed) {
                return false;
            }
            if (otherEntry.keyParsingFailed) {
                return false;
            }
            if (!KeysetHandle.isValidKeyStatusType(thisEntry.keyStatusType)) {
                return false;
            }
            if (!KeysetHandle.isValidKeyStatusType(otherEntry.keyStatusType)) {
                return false;
            }
            if (!thisEntry.equalsEntry(otherEntry)) {
                return false;
            }
            primaryFound |= thisEntry.isPrimary;
        }
        return primaryFound;
    }

    private static ProtoKeySerialization toProtoKeySerialization(Keyset.Key protoKey) throws GeneralSecurityException {
        int id = protoKey.getKeyId();
        Integer idRequirement = protoKey.getOutputPrefixType() == OutputPrefixType.RAW ? null : Integer.valueOf(id);
        return ProtoKeySerialization.create(protoKey.getKeyData().getTypeUrl(), protoKey.getKeyData().getValue(), protoKey.getKeyData().getKeyMaterialType(), protoKey.getOutputPrefixType(), idRequirement);
    }

    private static Key toKey(Keyset.Key protoKey) throws GeneralSecurityException {
        ProtoKeySerialization protoKeySerialization = KeysetHandle.toProtoKeySerialization(protoKey);
        return MutableSerializationRegistry.globalInstance().parseKeyWithLegacyFallback(protoKeySerialization, InsecureSecretKeyAccess.get());
    }

    private static Keyset.Key toKeysetKey(int id, KeyStatusType status, ProtoKeySerialization protoKeySerialization) {
        return Keyset.Key.newBuilder().setKeyData(KeyData.newBuilder().setTypeUrl(protoKeySerialization.getTypeUrl()).setValue(protoKeySerialization.getValue()).setKeyMaterialType(protoKeySerialization.getKeyMaterialType())).setStatus(status).setKeyId(id).setOutputPrefixType(protoKeySerialization.getOutputPrefixType()).build();
    }

    private static void validateKeyId(Key key, int id) throws GeneralSecurityException {
        Integer idRequirement = key.getIdRequirementOrNull();
        if (idRequirement != null && idRequirement != id) {
            throw new GeneralSecurityException("Wrong ID set for key with ID requirement");
        }
    }

    private static Keyset.Key createKeysetKey(Key key, KeyStatusType keyStatus, int id) throws GeneralSecurityException {
        ProtoKeySerialization serializedKey = MutableSerializationRegistry.globalInstance().serializeKey(key, ProtoKeySerialization.class, InsecureSecretKeyAccess.get());
        KeysetHandle.validateKeyId(key, id);
        return KeysetHandle.toKeysetKey(id, keyStatus, serializedKey);
    }

    @Immutable
    public static final class Entry
    implements KeysetHandleInterface.Entry {
        private static final EntryConsumer NO_LOGGING = e -> {};
        private final Key key;
        private final KeyStatusType keyStatusType;
        private final KeyStatus keyStatus;
        private final int id;
        private final boolean isPrimary;
        private final boolean keyParsingFailed;
        private final EntryConsumer keyExportLogger;

        private Entry(Key key, KeyStatusType keyStatusType, int id, boolean isPrimary, boolean keyParsingFailed, EntryConsumer keyExportLogger) {
            this.key = key;
            this.keyStatusType = keyStatusType;
            this.keyStatus = KeysetHandle.parseStatusWithDisabledFallback(keyStatusType);
            this.id = id;
            this.isPrimary = isPrimary;
            this.keyParsingFailed = keyParsingFailed;
            this.keyExportLogger = keyExportLogger;
        }

        @Override
        public Key getKey() {
            this.keyExportLogger.accept(this);
            return this.key;
        }

        @Override
        public KeyStatus getStatus() {
            return this.keyStatus;
        }

        @Override
        public int getId() {
            return this.id;
        }

        @Override
        public boolean isPrimary() {
            return this.isPrimary;
        }

        private boolean equalsEntry(Entry other) {
            if (other.isPrimary != this.isPrimary) {
                return false;
            }
            if (!other.keyStatusType.equals(this.keyStatusType)) {
                return false;
            }
            if (other.id != this.id) {
                return false;
            }
            return other.key.equalsKey(this.key);
        }

        @Immutable
        private static interface EntryConsumer {
            public void accept(Entry var1);
        }
    }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<Entry>();
        @Nullable
        private GeneralSecurityException errorToThrow = null;
        private MonitoringAnnotations annotations = MonitoringAnnotations.EMPTY;
        private boolean buildCalled = false;

        private void clearPrimary() {
            for (Entry entry : this.entries) {
                entry.isPrimary = false;
            }
        }

        @CanIgnoreReturnValue
        public Builder addEntry(Entry entry) {
            if (entry.builder != null) {
                throw new IllegalStateException("Entry has already been added to a KeysetHandle.Builder");
            }
            if (entry.isPrimary) {
                this.clearPrimary();
            }
            entry.builder = this;
            this.entries.add(entry);
            return this;
        }

        @CanIgnoreReturnValue
        @Alpha
        public Builder setMonitoringAnnotations(MonitoringAnnotations annotations) {
            this.annotations = annotations;
            return this;
        }

        public int size() {
            return this.entries.size();
        }

        public Entry getAt(int i) {
            return this.entries.get(i);
        }

        @Deprecated
        @CanIgnoreReturnValue
        public Entry removeAt(int i) {
            return this.entries.remove(i);
        }

        @CanIgnoreReturnValue
        public Builder deleteAt(int i) {
            this.entries.remove(i);
            return this;
        }

        private static void checkIdAssignments(List<Entry> entries) throws GeneralSecurityException {
            for (int i = 0; i < entries.size() - 1; ++i) {
                if (entries.get(i).strategy != KeyIdStrategy.RANDOM_ID || entries.get(i + 1).strategy == KeyIdStrategy.RANDOM_ID) continue;
                throw new GeneralSecurityException("Entries with 'withRandomId()' may only be followed by other entries with 'withRandomId()'.");
            }
        }

        private void setErrorToThrow(GeneralSecurityException errorToThrow) {
            this.errorToThrow = errorToThrow;
        }

        private static int randomIdNotInSet(Set<Integer> ids) {
            int id = 0;
            while (id == 0 || ids.contains(id)) {
                id = com.google.crypto.tink.internal.Util.randKeyId();
            }
            return id;
        }

        private static int getNextIdFromBuilderEntry(Entry builderEntry, Set<Integer> idsSoFar) throws GeneralSecurityException {
            int id = 0;
            if (builderEntry.strategy == null) {
                throw new GeneralSecurityException("No ID was set (with withFixedId or withRandomId)");
            }
            id = builderEntry.strategy == KeyIdStrategy.RANDOM_ID ? Builder.randomIdNotInSet(idsSoFar) : builderEntry.strategy.getFixedId();
            return id;
        }

        public KeysetHandle build() throws GeneralSecurityException {
            if (this.errorToThrow != null) {
                throw new GeneralSecurityException("Cannot build keyset due to error in original", this.errorToThrow);
            }
            if (this.buildCalled) {
                throw new GeneralSecurityException("KeysetHandle.Builder#build must only be called once");
            }
            this.buildCalled = true;
            ArrayList<com.google.crypto.tink.KeysetHandle$Entry> handleEntries = new ArrayList<com.google.crypto.tink.KeysetHandle$Entry>(this.entries.size());
            Integer primaryId = null;
            Builder.checkIdAssignments(this.entries);
            HashSet<Integer> idsSoFar = new HashSet<Integer>();
            for (Entry builderEntry : this.entries) {
                com.google.crypto.tink.KeysetHandle$Entry handleEntry;
                if (builderEntry.keyStatus == null) {
                    throw new GeneralSecurityException("Key Status not set.");
                }
                int id = Builder.getNextIdFromBuilderEntry(builderEntry, idsSoFar);
                if (idsSoFar.contains(id)) {
                    throw new GeneralSecurityException("Id " + id + " is used twice in the keyset");
                }
                idsSoFar.add(id);
                if (builderEntry.key != null) {
                    KeysetHandle.validateKeyId(builderEntry.key, id);
                    handleEntry = new com.google.crypto.tink.KeysetHandle$Entry(builderEntry.key, KeysetHandle.serializeStatus(builderEntry.keyStatus), id, builderEntry.isPrimary, false, com.google.crypto.tink.KeysetHandle$Entry.NO_LOGGING);
                } else {
                    Integer idRequirement = builderEntry.parameters.hasIdRequirement() ? Integer.valueOf(id) : null;
                    Key key = MutableKeyCreationRegistry.globalInstance().createKey(builderEntry.parameters, idRequirement);
                    handleEntry = new com.google.crypto.tink.KeysetHandle$Entry(key, KeysetHandle.serializeStatus(builderEntry.keyStatus), id, builderEntry.isPrimary, false, com.google.crypto.tink.KeysetHandle$Entry.NO_LOGGING);
                }
                if (builderEntry.isPrimary) {
                    if (primaryId != null) {
                        throw new GeneralSecurityException("Two primaries were set");
                    }
                    primaryId = id;
                    if (builderEntry.keyStatus != KeyStatus.ENABLED) {
                        throw new GeneralSecurityException("Primary key is not enabled");
                    }
                }
                handleEntries.add(handleEntry);
            }
            if (primaryId == null) {
                throw new GeneralSecurityException("No primary was set");
            }
            KeysetHandle unmonitoredKeyset = new KeysetHandle(handleEntries, this.annotations);
            return KeysetHandle.addMonitoringIfNeeded(unmonitoredKeyset);
        }

        public static final class Entry {
            private boolean isPrimary;
            private KeyStatus keyStatus = KeyStatus.ENABLED;
            @Nullable
            private final Key key;
            @Nullable
            private final Parameters parameters;
            private KeyIdStrategy strategy = null;
            @Nullable
            private Builder builder = null;

            private Entry(Key key) {
                this.key = key;
                this.parameters = null;
            }

            private Entry(Parameters parameters) {
                this.key = null;
                this.parameters = parameters;
            }

            @CanIgnoreReturnValue
            public Entry makePrimary() {
                if (this.builder != null) {
                    this.builder.clearPrimary();
                }
                this.isPrimary = true;
                return this;
            }

            public boolean isPrimary() {
                return this.isPrimary;
            }

            @CanIgnoreReturnValue
            public Entry setStatus(KeyStatus status) {
                this.keyStatus = status;
                return this;
            }

            public KeyStatus getStatus() {
                return this.keyStatus;
            }

            @CanIgnoreReturnValue
            public Entry withFixedId(int id) {
                this.strategy = KeyIdStrategy.fixedId(id);
                return this;
            }

            @CanIgnoreReturnValue
            public Entry withRandomId() {
                this.strategy = KeyIdStrategy.randomId();
                return this;
            }
        }

        private static class KeyIdStrategy {
            private static final KeyIdStrategy RANDOM_ID = new KeyIdStrategy();
            private final int fixedId;

            private KeyIdStrategy() {
                this.fixedId = 0;
            }

            private KeyIdStrategy(int id) {
                this.fixedId = id;
            }

            private static KeyIdStrategy randomId() {
                return RANDOM_ID;
            }

            private static KeyIdStrategy fixedId(int id) {
                return new KeyIdStrategy(id);
            }

            private int getFixedId() {
                return this.fixedId;
            }
        }
    }
}

