/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.BoundedInputStream;
import io.netty.util.internal.CleanableDirectBuffer;
import io.netty.util.internal.Cleaner;
import io.netty.util.internal.CleanerJava24Linker;
import io.netty.util.internal.CleanerJava25;
import io.netty.util.internal.CleanerJava6;
import io.netty.util.internal.CleanerJava9;
import io.netty.util.internal.ConstantTimeUtils;
import io.netty.util.internal.DirectCleaner;
import io.netty.util.internal.LongAdderCounter;
import io.netty.util.internal.LongCounter;
import io.netty.util.internal.OutOfDirectMemoryError;
import io.netty.util.internal.PlatformDependent0;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.VarHandleFactory;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MpmcArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpmcAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscChunkedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded.MpscAtomicUnpaddedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdk.jfr.FlightRecorder;

public final class PlatformDependent {
    private static final InternalLogger logger;
    private static Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN;
    private static final boolean MAYBE_SUPER_USER;
    private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final boolean DIRECT_BUFFER_PREFERRED;
    private static final boolean EXPLICIT_NO_PREFER_DIRECT;
    private static final long MAX_DIRECT_MEMORY;
    private static final int MPSC_CHUNK_SIZE = 1024;
    private static final int MIN_MAX_MPSC_CAPACITY = 2048;
    private static final int MAX_ALLOWED_MPSC_CAPACITY = 0x40000000;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final File TMPDIR;
    private static final int BIT_MODE;
    private static final String NORMALIZED_ARCH;
    private static final String NORMALIZED_OS;
    private static final Set<String> LINUX_OS_CLASSIFIERS;
    private static final boolean IS_WINDOWS;
    private static final boolean IS_OSX;
    private static final boolean IS_J9_JVM;
    private static final boolean IS_IVKVM_DOT_NET;
    private static final int ADDRESS_SIZE;
    private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
    private static final AtomicLong DIRECT_MEMORY_COUNTER;
    private static final long DIRECT_MEMORY_LIMIT;
    private static final Cleaner CLEANER;
    private static final Cleaner DIRECT_CLEANER;
    private static final Cleaner LEGACY_CLEANER;
    private static final boolean HAS_ALLOCATE_UNINIT_ARRAY;
    private static final String LINUX_ID_PREFIX = "ID=";
    private static final String LINUX_ID_LIKE_PREFIX = "ID_LIKE=";
    public static final boolean BIG_ENDIAN_NATIVE_ORDER;
    private static final boolean JFR;
    private static final boolean VAR_HANDLE;
    private static final Cleaner NOOP;

    private static boolean initializeVarHandle() {
        boolean varHandleEnabled;
        Throwable varHandleFailure;
        if (UNSAFE_UNAVAILABILITY_CAUSE == null || PlatformDependent.javaVersion() < 9 || PlatformDependent0.isNativeImage()) {
            return false;
        }
        boolean varHandleAvailable = false;
        try {
            VarHandle.storeStoreFence();
            varHandleAvailable = VarHandleFactory.isSupported();
            varHandleFailure = VarHandleFactory.unavailableCause();
        }
        catch (Throwable t) {
            varHandleFailure = t;
        }
        if (varHandleFailure != null) {
            logger.debug("java.lang.invoke.VarHandle: unavailable, reason: {}", (Object)varHandleFailure.toString());
        } else {
            logger.debug("java.lang.invoke.VarHandle: available");
        }
        boolean bl = varHandleEnabled = varHandleAvailable && SystemPropertyUtil.getBoolean("io.netty.varHandle.enabled", varHandleAvailable);
        if (logger.isTraceEnabled() && varHandleFailure != null) {
            logger.debug("-Dio.netty.varHandle.enabled: {}", (Object)varHandleEnabled, (Object)varHandleFailure);
        } else if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.varHandle.enabled: {}", (Object)varHandleEnabled);
        }
        return varHandleEnabled;
    }

    static void addFilesystemOsClassifiers(Set<String> availableClassifiers) {
        if (PlatformDependent.processOsReleaseFile("/etc/os-release", availableClassifiers)) {
            return;
        }
        PlatformDependent.processOsReleaseFile("/usr/lib/os-release", availableClassifiers);
    }

    private static boolean processOsReleaseFile(String osReleaseFileName, Set<String> availableClassifiers) {
        Path file = Paths.get(osReleaseFileName, new String[0]);
        return AccessController.doPrivileged(() -> {
            block12: {
                try {
                    if (!Files.exists(file, new LinkOption[0])) break block12;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new BoundedInputStream(Files.newInputStream(file, new OpenOption[0])), StandardCharsets.UTF_8));){
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith(LINUX_ID_PREFIX)) {
                                String id = PlatformDependent.normalizeOsReleaseVariableValue(line.substring(LINUX_ID_PREFIX.length()));
                                PlatformDependent.addClassifier(availableClassifiers, id);
                                continue;
                            }
                            if (!line.startsWith(LINUX_ID_LIKE_PREFIX)) continue;
                            line = PlatformDependent.normalizeOsReleaseVariableValue(line.substring(LINUX_ID_LIKE_PREFIX.length()));
                            PlatformDependent.addClassifier(availableClassifiers, line.split(" "));
                        }
                    }
                    catch (SecurityException e) {
                        logger.debug("Unable to read {}", (Object)osReleaseFileName, (Object)e);
                    }
                    catch (IOException e) {
                        logger.debug("Error while reading content of {}", (Object)osReleaseFileName, (Object)e);
                    }
                    return true;
                }
                catch (SecurityException e) {
                    logger.debug("Unable to check if {} exists", (Object)osReleaseFileName, (Object)e);
                }
            }
            return false;
        });
    }

    static boolean addPropertyOsClassifiers(Set<String> availableClassifiers) {
        String osClassifiersPropertyName = "io.netty.osClassifiers";
        String osClassifiers = SystemPropertyUtil.get(osClassifiersPropertyName);
        if (osClassifiers == null) {
            return false;
        }
        if (osClassifiers.isEmpty()) {
            return true;
        }
        String[] classifiers = osClassifiers.split(",");
        if (classifiers.length == 0) {
            throw new IllegalArgumentException(osClassifiersPropertyName + " property is not empty, but contains no classifiers: " + osClassifiers);
        }
        if (classifiers.length > 2) {
            throw new IllegalArgumentException(osClassifiersPropertyName + " property contains more than 2 classifiers: " + osClassifiers);
        }
        for (String classifier : classifiers) {
            PlatformDependent.addClassifier(availableClassifiers, classifier);
        }
        return true;
    }

    public static long byteArrayBaseOffset() {
        return BYTE_ARRAY_BASE_OFFSET;
    }

    public static boolean hasDirectBufferNoCleanerConstructor() {
        return PlatformDependent0.hasDirectBufferNoCleanerConstructor();
    }

    public static byte[] allocateUninitializedArray(int size) {
        return HAS_ALLOCATE_UNINIT_ARRAY ? PlatformDependent0.allocateUninitializedArray(size) : new byte[size];
    }

    public static boolean isAndroid() {
        return PlatformDependent0.isAndroid();
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isOsx() {
        return IS_OSX;
    }

    public static boolean maybeSuperUser() {
        return MAYBE_SUPER_USER;
    }

    public static int javaVersion() {
        return PlatformDependent0.javaVersion();
    }

    public static boolean isVirtualThread(Thread thread) {
        return PlatformDependent0.isVirtualThread(thread);
    }

    public static boolean canEnableTcpNoDelayByDefault() {
        return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
    }

    public static boolean hasUnsafe() {
        return UNSAFE_UNAVAILABILITY_CAUSE == null;
    }

    public static Throwable getUnsafeUnavailabilityCause() {
        return UNSAFE_UNAVAILABILITY_CAUSE;
    }

    public static boolean isUnaligned() {
        return PlatformDependent0.isUnaligned();
    }

    public static boolean directBufferPreferred() {
        return DIRECT_BUFFER_PREFERRED;
    }

    public static boolean isExplicitNoPreferDirect() {
        return EXPLICIT_NO_PREFER_DIRECT;
    }

    public static boolean canReliabilyFreeDirectBuffers() {
        return CLEANER != NOOP;
    }

    public static long maxDirectMemory() {
        return DIRECT_MEMORY_LIMIT;
    }

    public static long usedDirectMemory() {
        return DIRECT_MEMORY_COUNTER != null ? DIRECT_MEMORY_COUNTER.get() : -1L;
    }

    public static File tmpdir() {
        return TMPDIR;
    }

    public static int bitMode() {
        return BIT_MODE;
    }

    public static int addressSize() {
        return ADDRESS_SIZE;
    }

    public static long allocateMemory(long size) {
        return PlatformDependent0.allocateMemory(size);
    }

    public static void freeMemory(long address) {
        PlatformDependent0.freeMemory(address);
    }

    public static long reallocateMemory(long address, long newSize) {
        return PlatformDependent0.reallocateMemory(address, newSize);
    }

    public static void throwException(Throwable t) {
        PlatformDependent0.throwException(t);
    }

    @Deprecated
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap();
    }

    @Deprecated
    public static LongCounter newLongCounter() {
        return new LongAdderCounter();
    }

    @Deprecated
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity) {
        return new ConcurrentHashMap(initialCapacity);
    }

    @Deprecated
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor) {
        return new ConcurrentHashMap(initialCapacity, loadFactor);
    }

    @Deprecated
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        return new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Deprecated
    public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> map) {
        return new ConcurrentHashMap<K, V>(map);
    }

    public static CleanableDirectBuffer allocateDirect(int capacity) {
        return CLEANER.allocate(capacity);
    }

    @Deprecated
    public static void freeDirectBuffer(ByteBuffer buffer) {
        LEGACY_CLEANER.freeDirectBuffer(buffer);
    }

    public static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.directBufferAddress(buffer);
    }

    public static ByteBuffer directBuffer(long memoryAddress, int size) {
        if (PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            return PlatformDependent0.newDirectBuffer(memoryAddress, size);
        }
        throw new UnsupportedOperationException("sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
    }

    public static boolean hasVarHandle() {
        return VAR_HANDLE;
    }

    public static VarHandle findVarHandleOfIntField(MethodHandles.Lookup lookup, Class<?> type, String fieldName) {
        if (VAR_HANDLE) {
            return VarHandleFactory.privateFindVarHandle(lookup, type, fieldName, Integer.TYPE);
        }
        return null;
    }

    public static VarHandle intBeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.intBeArrayView();
        }
        return null;
    }

    public static VarHandle intLeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.intLeArrayView();
        }
        return null;
    }

    public static VarHandle longBeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.longBeArrayView();
        }
        return null;
    }

    public static VarHandle longLeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.longLeArrayView();
        }
        return null;
    }

    public static VarHandle shortBeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.shortBeArrayView();
        }
        return null;
    }

    public static VarHandle shortLeArrayView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.shortLeArrayView();
        }
        return null;
    }

    public static VarHandle longBeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.longBeByteBufferView();
        }
        return null;
    }

    public static VarHandle longLeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.longLeByteBufferView();
        }
        return null;
    }

    public static VarHandle intBeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.intBeByteBufferView();
        }
        return null;
    }

    public static VarHandle intLeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.intLeByteBufferView();
        }
        return null;
    }

    public static VarHandle shortBeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.shortBeByteBufferView();
        }
        return null;
    }

    public static VarHandle shortLeByteBufferView() {
        if (VAR_HANDLE) {
            return VarHandleFactory.shortLeByteBufferView();
        }
        return null;
    }

    public static Object getObject(Object object, long fieldOffset) {
        return PlatformDependent0.getObject(object, fieldOffset);
    }

    public static int getVolatileInt(Object object, long fieldOffset) {
        return PlatformDependent0.getIntVolatile(object, fieldOffset);
    }

    public static int getInt(Object object, long fieldOffset) {
        return PlatformDependent0.getInt(object, fieldOffset);
    }

    public static void putOrderedInt(Object object, long fieldOffset, int value) {
        PlatformDependent0.putOrderedInt(object, fieldOffset, value);
    }

    public static int getAndAddInt(Object object, long fieldOffset, int delta) {
        return PlatformDependent0.getAndAddInt(object, fieldOffset, delta);
    }

    public static boolean compareAndSwapInt(Object object, long fieldOffset, int expected, int value) {
        return PlatformDependent0.compareAndSwapInt(object, fieldOffset, expected, value);
    }

    static void safeConstructPutInt(Object object, long fieldOffset, int value) {
        PlatformDependent0.safeConstructPutInt(object, fieldOffset, value);
    }

    public static byte getByte(long address) {
        return PlatformDependent0.getByte(address);
    }

    public static short getShort(long address) {
        return PlatformDependent0.getShort(address);
    }

    public static int getInt(long address) {
        return PlatformDependent0.getInt(address);
    }

    public static long getLong(long address) {
        return PlatformDependent0.getLong(address);
    }

    public static byte getByte(byte[] data, int index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getByte(data, index) : data[index];
    }

    public static byte getByte(byte[] data, long index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getByte(data, index) : data[PlatformDependent.toIntExact(index)];
    }

    public static short getShort(byte[] data, int index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getShort(data, index) : (short)data[index];
    }

    public static int getInt(byte[] data, int index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getInt(data, index) : data[index];
    }

    public static int getInt(int[] data, long index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getInt(data, index) : data[PlatformDependent.toIntExact(index)];
    }

    public static long getLong(byte[] data, int index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getLong(data, index) : (long)data[index];
    }

    public static long getLong(long[] data, long index) {
        return PlatformDependent.hasUnsafe() ? PlatformDependent0.getLong(data, index) : data[PlatformDependent.toIntExact(index)];
    }

    private static int toIntExact(long value) {
        return Math.toIntExact(value);
    }

    private static long getLongSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (long)bytes[offset] << 56 | ((long)bytes[offset + 1] & 0xFFL) << 48 | ((long)bytes[offset + 2] & 0xFFL) << 40 | ((long)bytes[offset + 3] & 0xFFL) << 32 | ((long)bytes[offset + 4] & 0xFFL) << 24 | ((long)bytes[offset + 5] & 0xFFL) << 16 | ((long)bytes[offset + 6] & 0xFFL) << 8 | (long)bytes[offset + 7] & 0xFFL;
        }
        return (long)bytes[offset] & 0xFFL | ((long)bytes[offset + 1] & 0xFFL) << 8 | ((long)bytes[offset + 2] & 0xFFL) << 16 | ((long)bytes[offset + 3] & 0xFFL) << 24 | ((long)bytes[offset + 4] & 0xFFL) << 32 | ((long)bytes[offset + 5] & 0xFFL) << 40 | ((long)bytes[offset + 6] & 0xFFL) << 48 | (long)bytes[offset + 7] << 56;
    }

    private static int getIntSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return bytes[offset] << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | bytes[offset + 3] & 0xFF;
        }
        return bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | bytes[offset + 3] << 24;
    }

    private static short getShortSafe(byte[] bytes, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return (short)(bytes[offset] << 8 | bytes[offset + 1] & 0xFF);
        }
        return (short)(bytes[offset] & 0xFF | bytes[offset + 1] << 8);
    }

    private static int hashCodeAsciiCompute(CharSequence value, int offset, int hash) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset + 4) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset);
        }
        return hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset) * 461845907 + PlatformDependent.hashCodeAsciiSanitizeInt(value, offset + 4);
    }

    private static int hashCodeAsciiSanitizeInt(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return value.charAt(offset + 3) & 0x1F | (value.charAt(offset + 2) & 0x1F) << 8 | (value.charAt(offset + 1) & 0x1F) << 16 | (value.charAt(offset) & 0x1F) << 24;
        }
        return (value.charAt(offset + 3) & 0x1F) << 24 | (value.charAt(offset + 2) & 0x1F) << 16 | (value.charAt(offset + 1) & 0x1F) << 8 | value.charAt(offset) & 0x1F;
    }

    private static int hashCodeAsciiSanitizeShort(CharSequence value, int offset) {
        if (BIG_ENDIAN_NATIVE_ORDER) {
            return value.charAt(offset + 1) & 0x1F | (value.charAt(offset) & 0x1F) << 8;
        }
        return (value.charAt(offset + 1) & 0x1F) << 8 | value.charAt(offset) & 0x1F;
    }

    private static int hashCodeAsciiSanitizeByte(char value) {
        return value & 0x1F;
    }

    public static void putByte(long address, byte value) {
        PlatformDependent0.putByte(address, value);
    }

    public static void putShort(long address, short value) {
        PlatformDependent0.putShort(address, value);
    }

    public static void putInt(long address, int value) {
        PlatformDependent0.putInt(address, value);
    }

    public static void putLong(long address, long value) {
        PlatformDependent0.putLong(address, value);
    }

    public static void putByte(byte[] data, int index, byte value) {
        PlatformDependent0.putByte(data, index, value);
    }

    public static void putByte(Object data, long offset, byte value) {
        PlatformDependent0.putByte(data, offset, value);
    }

    public static void putShort(byte[] data, int index, short value) {
        PlatformDependent0.putShort(data, index, value);
    }

    public static void putInt(byte[] data, int index, int value) {
        PlatformDependent0.putInt(data, index, value);
    }

    public static void putLong(byte[] data, int index, long value) {
        PlatformDependent0.putLong(data, index, value);
    }

    public static void putObject(Object o, long offset, Object x) {
        PlatformDependent0.putObject(o, offset, x);
    }

    public static long objectFieldOffset(Field field) {
        return PlatformDependent0.objectFieldOffset(field);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long length) {
        PlatformDependent0.copyMemory(srcAddr, dstAddr, length);
    }

    public static void copyMemory(byte[] src, int srcIndex, long dstAddr, long length) {
        PlatformDependent0.copyMemory(src, BYTE_ARRAY_BASE_OFFSET + (long)srcIndex, null, dstAddr, length);
    }

    public static void copyMemory(byte[] src, int srcIndex, byte[] dst, int dstIndex, long length) {
        PlatformDependent0.copyMemory(src, BYTE_ARRAY_BASE_OFFSET + (long)srcIndex, dst, BYTE_ARRAY_BASE_OFFSET + (long)dstIndex, length);
    }

    public static void copyMemory(long srcAddr, byte[] dst, int dstIndex, long length) {
        PlatformDependent0.copyMemory(null, srcAddr, dst, BYTE_ARRAY_BASE_OFFSET + (long)dstIndex, length);
    }

    public static void setMemory(byte[] dst, int dstIndex, long bytes, byte value) {
        PlatformDependent0.setMemory(dst, BYTE_ARRAY_BASE_OFFSET + (long)dstIndex, bytes, value);
    }

    public static void setMemory(long address, long bytes, byte value) {
        PlatformDependent0.setMemory(address, bytes, value);
    }

    public static ByteBuffer allocateDirectNoCleaner(int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        PlatformDependent.incrementMemoryCounter(capacity);
        try {
            return PlatformDependent0.allocateDirectNoCleaner(capacity);
        }
        catch (Throwable e) {
            PlatformDependent.decrementMemoryCounter(capacity);
            PlatformDependent.throwException(e);
            return null;
        }
    }

    public static CleanableDirectBuffer allocateDirectBufferNoCleaner(int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        return DIRECT_CLEANER.allocate(capacity);
    }

    public static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int len = capacity - buffer.capacity();
        PlatformDependent.incrementMemoryCounter(len);
        try {
            return PlatformDependent0.reallocateDirectNoCleaner(buffer, capacity);
        }
        catch (Throwable e) {
            PlatformDependent.decrementMemoryCounter(len);
            PlatformDependent.throwException(e);
            return null;
        }
    }

    public static CleanableDirectBuffer reallocateDirectBufferNoCleaner(CleanableDirectBuffer buffer, int capacity) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        return ((DirectCleaner)DIRECT_CLEANER).reallocate(buffer, capacity);
    }

    public static void freeDirectNoCleaner(ByteBuffer buffer) {
        assert (USE_DIRECT_BUFFER_NO_CLEANER);
        int capacity = buffer.capacity();
        PlatformDependent0.freeMemory(PlatformDependent0.directBufferAddress(buffer));
        PlatformDependent.decrementMemoryCounter(capacity);
    }

    public static boolean hasAlignDirectByteBuffer() {
        return PlatformDependent.hasUnsafe() || PlatformDependent0.hasAlignSliceMethod();
    }

    public static ByteBuffer alignDirectBuffer(ByteBuffer buffer, int alignment) {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("Cannot get aligned slice of non-direct byte buffer.");
        }
        if (PlatformDependent0.hasAlignSliceMethod()) {
            return PlatformDependent0.alignSlice(buffer, alignment);
        }
        if (PlatformDependent.hasUnsafe()) {
            long address = PlatformDependent.directBufferAddress(buffer);
            long aligned = PlatformDependent.align(address, alignment);
            buffer.position((int)(aligned - address));
            return buffer.slice();
        }
        throw new UnsupportedOperationException("Cannot align direct buffer. Needs either Unsafe or ByteBuffer.alignSlice method available.");
    }

    public static long align(long value, int alignment) {
        return Pow2.align(value, alignment);
    }

    public static ByteBuffer offsetSlice(ByteBuffer buffer, int index, int length) {
        if (PlatformDependent0.hasOffsetSliceMethod()) {
            return PlatformDependent0.offsetSlice(buffer, index, length);
        }
        return ((ByteBuffer)buffer.duplicate().clear().position(index).limit(index + length)).slice();
    }

    private static void incrementMemoryCounter(int capacity) {
        long newUsedMemory;
        if (DIRECT_MEMORY_COUNTER != null && (newUsedMemory = DIRECT_MEMORY_COUNTER.addAndGet(capacity)) > DIRECT_MEMORY_LIMIT) {
            DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
            throw new OutOfDirectMemoryError("failed to allocate " + capacity + " byte(s) of direct memory (used: " + (newUsedMemory - (long)capacity) + ", max: " + DIRECT_MEMORY_LIMIT + ')');
        }
    }

    private static void decrementMemoryCounter(int capacity) {
        if (DIRECT_MEMORY_COUNTER != null) {
            long usedMemory = DIRECT_MEMORY_COUNTER.addAndGet(-capacity);
            assert (usedMemory >= 0L);
        }
    }

    public static boolean useDirectBufferNoCleaner() {
        return USE_DIRECT_BUFFER_NO_CLEANER;
    }

    public static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        if (PlatformDependent.javaVersion() > 8 && (startPos2 | startPos1 | bytes1.length - length | bytes2.length - length) == 0) {
            return Arrays.equals(bytes1, bytes2);
        }
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? PlatformDependent.equalsSafe(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equals(bytes1, startPos1, bytes2, startPos2, length);
    }

    public static boolean isZero(byte[] bytes, int startPos, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? PlatformDependent.isZeroSafe(bytes, startPos, length) : PlatformDependent0.isZero(bytes, startPos, length);
    }

    public static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() ? ConstantTimeUtils.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length) : PlatformDependent0.equalsConstantTime(bytes1, startPos1, bytes2, startPos2, length);
    }

    public static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        return !PlatformDependent.hasUnsafe() || !PlatformDependent0.unalignedAccess() || BIG_ENDIAN_NATIVE_ORDER ? PlatformDependent.hashCodeAsciiSafe(bytes, startPos, length) : PlatformDependent0.hashCodeAscii(bytes, startPos, length);
    }

    public static int hashCodeAscii(CharSequence bytes) {
        int length = bytes.length();
        int remainingBytes = length & 7;
        int hash = -1028477387;
        if (length >= 32) {
            for (int i = length - 8; i >= remainingBytes; i -= 8) {
                hash = PlatformDependent.hashCodeAsciiCompute(bytes, i, hash);
            }
        } else if (length >= 8) {
            hash = PlatformDependent.hashCodeAsciiCompute(bytes, length - 8, hash);
            if (length >= 16) {
                hash = PlatformDependent.hashCodeAsciiCompute(bytes, length - 16, hash);
                if (length >= 24) {
                    hash = PlatformDependent.hashCodeAsciiCompute(bytes, length - 24, hash);
                }
            }
        }
        if (remainingBytes == 0) {
            return hash;
        }
        int offset = 0;
        if (remainingBytes != 2 & remainingBytes != 4 & remainingBytes != 6) {
            hash = hash * -862048943 + PlatformDependent.hashCodeAsciiSanitizeByte(bytes.charAt(0));
            offset = 1;
        }
        if (remainingBytes != 1 & remainingBytes != 4 & remainingBytes != 5) {
            hash = hash * (offset == 0 ? -862048943 : 461845907) + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.hashCodeAsciiSanitizeShort(bytes, offset));
            offset += 2;
        }
        if (remainingBytes >= 4) {
            return hash * (offset == 0 | offset == 3 ? -862048943 : 461845907) + PlatformDependent.hashCodeAsciiSanitizeInt(bytes, offset);
        }
        return hash;
    }

    public static <T> Queue<T> newMpscQueue() {
        return Mpsc.newMpscQueue();
    }

    public static <T> Queue<T> newMpscQueue(int maxCapacity) {
        return Mpsc.newMpscQueue(maxCapacity);
    }

    public static <T> Queue<T> newMpscQueue(int chunkSize, int maxCapacity) {
        return Mpsc.newChunkedMpscQueue(chunkSize, maxCapacity);
    }

    public static <T> Queue<T> newSpscQueue() {
        return PlatformDependent.hasUnsafe() ? new SpscLinkedQueue() : new SpscLinkedAtomicQueue();
    }

    public static <T> Queue<T> newFixedMpscQueue(int capacity) {
        return PlatformDependent.hasUnsafe() ? new MpscArrayQueue(capacity) : new MpscAtomicArrayQueue(capacity);
    }

    public static <T> Queue<T> newFixedMpscUnpaddedQueue(int capacity) {
        return PlatformDependent.hasUnsafe() ? new MpscUnpaddedArrayQueue(capacity) : new MpscAtomicUnpaddedArrayQueue(capacity);
    }

    public static <T> Queue<T> newFixedMpmcQueue(int capacity) {
        return PlatformDependent.hasUnsafe() ? new MpmcArrayQueue(capacity) : new MpmcAtomicArrayQueue(capacity);
    }

    public static ClassLoader getClassLoader(Class<?> clazz) {
        return PlatformDependent0.getClassLoader(clazz);
    }

    public static ClassLoader getContextClassLoader() {
        return PlatformDependent0.getContextClassLoader();
    }

    public static ClassLoader getSystemClassLoader() {
        return PlatformDependent0.getSystemClassLoader();
    }

    public static <C> Deque<C> newConcurrentDeque() {
        return new ConcurrentLinkedDeque();
    }

    @Deprecated
    public static Random threadLocalRandom() {
        return ThreadLocalRandom.current();
    }

    private static boolean isWindows0() {
        boolean windows = "windows".equals(NORMALIZED_OS);
        if (windows) {
            logger.debug("Platform: Windows");
        }
        return windows;
    }

    private static boolean isOsx0() {
        boolean osx = "osx".equals(NORMALIZED_OS);
        if (osx) {
            logger.debug("Platform: MacOS");
        }
        return osx;
    }

    private static boolean maybeSuperUser0() {
        String username = SystemPropertyUtil.get("user.name");
        if (PlatformDependent.isWindows()) {
            return "Administrator".equals(username);
        }
        return "root".equals(username) || "toor".equals(username);
    }

    private static Throwable unsafeUnavailabilityCause0() {
        if (PlatformDependent.isAndroid()) {
            logger.debug("sun.misc.Unsafe: unavailable (Android)");
            return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (Android)");
        }
        if (PlatformDependent.isIkvmDotNet()) {
            logger.debug("sun.misc.Unsafe: unavailable (IKVM.NET)");
            return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (IKVM.NET)");
        }
        Throwable cause = PlatformDependent0.getUnsafeUnavailabilityCause();
        if (cause != null) {
            return cause;
        }
        try {
            boolean hasUnsafe = PlatformDependent0.hasUnsafe();
            logger.debug("sun.misc.Unsafe: {}", (Object)(hasUnsafe ? "available" : "unavailable"));
            return null;
        }
        catch (Throwable t) {
            logger.trace("Could not determine if Unsafe is available", t);
            return new UnsupportedOperationException("Could not determine if Unsafe is available", t);
        }
    }

    public static boolean isJ9Jvm() {
        return IS_J9_JVM;
    }

    private static boolean isJ9Jvm0() {
        String vmName = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
        return vmName.startsWith("ibm j9") || vmName.startsWith("eclipse openj9");
    }

    public static boolean isIkvmDotNet() {
        return IS_IVKVM_DOT_NET;
    }

    private static boolean isIkvmDotNet0() {
        String vmName = SystemPropertyUtil.get("java.vm.name", "").toUpperCase(Locale.US);
        return vmName.equals("IKVM.NET");
    }

    private static Pattern getMaxDirectMemorySizeArgPattern() {
        Pattern pattern = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN;
        if (pattern == null) {
            MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = pattern = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
        }
        return pattern;
    }

    public static long estimateMaxDirectMemory() {
        long maxDirectMemory = PlatformDependent0.bitsMaxDirectMemory();
        if (maxDirectMemory > 0L) {
            return maxDirectMemory;
        }
        try {
            ClassLoader systemClassLoader = PlatformDependent.getSystemClassLoader();
            Class<?> mgmtFactoryClass = Class.forName("java.lang.management.ManagementFactory", true, systemClassLoader);
            Class<?> runtimeClass = Class.forName("java.lang.management.RuntimeMXBean", true, systemClassLoader);
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            MethodHandle getRuntime = lookup.findStatic(mgmtFactoryClass, "getRuntimeMXBean", MethodType.methodType(runtimeClass));
            MethodHandle getInputArguments = lookup.findVirtual(runtimeClass, "getInputArguments", MethodType.methodType(List.class));
            List vmArgs = getInputArguments.invoke(getRuntime.invoke());
            Pattern maxDirectMemorySizeArgPattern = PlatformDependent.getMaxDirectMemorySizeArgPattern();
            for (int i = vmArgs.size() - 1; i >= 0; --i) {
                Matcher m = maxDirectMemorySizeArgPattern.matcher((CharSequence)vmArgs.get(i));
                if (!m.matches()) continue;
                maxDirectMemory = Long.parseLong(m.group(1));
                switch (m.group(2).charAt(0)) {
                    case 'K': 
                    case 'k': {
                        maxDirectMemory *= 1024L;
                        break;
                    }
                    case 'M': 
                    case 'm': {
                        maxDirectMemory *= 0x100000L;
                        break;
                    }
                    case 'G': 
                    case 'g': {
                        maxDirectMemory *= 0x40000000L;
                        break;
                    }
                }
                break;
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (maxDirectMemory <= 0L) {
            maxDirectMemory = Runtime.getRuntime().maxMemory();
            logger.debug("maxDirectMemory: {} bytes (maybe)", (Object)maxDirectMemory);
        } else {
            logger.debug("maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        }
        return maxDirectMemory;
    }

    private static File tmpdir0() {
        File f;
        try {
            f = PlatformDependent.toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
            if (f != null) {
                logger.debug("-Dio.netty.tmpdir: {}", (Object)f);
                return f;
            }
            f = PlatformDependent.toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
            if (f != null) {
                logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", (Object)f);
                return f;
            }
            if (PlatformDependent.isWindows()) {
                f = PlatformDependent.toDirectory(System.getenv("TEMP"));
                if (f != null) {
                    logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", (Object)f);
                    return f;
                }
                String userprofile = System.getenv("USERPROFILE");
                if (userprofile != null) {
                    f = PlatformDependent.toDirectory(userprofile + "\\AppData\\Local\\Temp");
                    if (f != null) {
                        logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", (Object)f);
                        return f;
                    }
                    f = PlatformDependent.toDirectory(userprofile + "\\Local Settings\\Temp");
                    if (f != null) {
                        logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", (Object)f);
                        return f;
                    }
                }
            } else {
                f = PlatformDependent.toDirectory(System.getenv("TMPDIR"));
                if (f != null) {
                    logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", (Object)f);
                    return f;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        f = PlatformDependent.isWindows() ? new File("C:\\Windows\\Temp") : new File("/tmp");
        logger.warn("Failed to get the temporary directory; falling back to: {}", (Object)f);
        return f;
    }

    private static File toDirectory(String path) {
        if (path == null) {
            return null;
        }
        File f = new File(path);
        f.mkdirs();
        if (!f.isDirectory()) {
            return null;
        }
        try {
            return f.getAbsoluteFile();
        }
        catch (Exception ignored) {
            return f;
        }
    }

    private static int bitMode0() {
        int bitMode = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {}", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)bitMode);
            return bitMode;
        }
        bitMode = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)bitMode);
            return bitMode;
        }
        String arch = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
        if ("amd64".equals(arch) || "x86_64".equals(arch)) {
            bitMode = 64;
        } else if ("i386".equals(arch) || "i486".equals(arch) || "i586".equals(arch) || "i686".equals(arch)) {
            bitMode = 32;
        }
        if (bitMode > 0) {
            logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", (Object)bitMode, (Object)arch);
        }
        String vm = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
        Pattern bitPattern = Pattern.compile("([1-9][0-9]+)-?bit");
        Matcher m = bitPattern.matcher(vm);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 64;
    }

    private static int addressSize0() {
        if (!PlatformDependent.hasUnsafe()) {
            return -1;
        }
        return PlatformDependent0.addressSize();
    }

    private static long byteArrayBaseOffset0() {
        if (!PlatformDependent.hasUnsafe()) {
            return -1L;
        }
        return PlatformDependent0.byteArrayBaseOffset();
    }

    private static boolean equalsSafe(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int end = startPos1 + length;
        while (startPos1 < end) {
            if (bytes1[startPos1] != bytes2[startPos2]) {
                return false;
            }
            ++startPos1;
            ++startPos2;
        }
        return true;
    }

    private static boolean isZeroSafe(byte[] bytes, int startPos, int length) {
        int end = startPos + length;
        while (startPos < end) {
            if (bytes[startPos] != 0) {
                return false;
            }
            ++startPos;
        }
        return true;
    }

    static int hashCodeAsciiSafe(byte[] bytes, int startPos, int length) {
        int hash = -1028477387;
        int remainingBytes = length & 7;
        int end = startPos + remainingBytes;
        for (int i = startPos - 8 + length; i >= end; i -= 8) {
            hash = PlatformDependent0.hashCodeAsciiCompute(PlatformDependent.getLongSafe(bytes, i), hash);
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos + 1))) * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 3));
            }
            case 6: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 2));
            }
            case 5: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos + 1));
            }
            case 4: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getIntSafe(bytes, startPos));
            }
            case 3: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos + 1));
            }
            case 2: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(PlatformDependent.getShortSafe(bytes, startPos));
            }
            case 1: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(bytes[startPos]);
            }
        }
        return hash;
    }

    public static String normalizedArch() {
        return NORMALIZED_ARCH;
    }

    public static String normalizedOs() {
        return NORMALIZED_OS;
    }

    public static Set<String> normalizedLinuxClassifiers() {
        return LINUX_OS_CLASSIFIERS;
    }

    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        if (directory == null) {
            return Files.createTempFile(prefix, suffix, new FileAttribute[0]).toFile();
        }
        return Files.createTempFile(directory.toPath(), prefix, suffix, new FileAttribute[0]).toFile();
    }

    private static void addClassifier(Set<String> dest, String ... maybeClassifiers) {
        for (String id : maybeClassifiers) {
            if (!PlatformDependent.isAllowedClassifier(id)) continue;
            dest.add(id);
        }
    }

    private static boolean isAllowedClassifier(String classifier) {
        switch (classifier) {
            case "fedora": 
            case "suse": 
            case "arch": {
                return true;
            }
        }
        return false;
    }

    private static String normalizeOsReleaseVariableValue(String value) {
        String trimmed = value.trim();
        StringBuilder sb = new StringBuilder(trimmed.length());
        for (int i = 0; i < trimmed.length(); ++i) {
            char c = trimmed.charAt(i);
            if (c == '\"' || c == '\'') continue;
            sb.append(c);
        }
        return sb.toString();
    }

    private static String normalize(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); ++i) {
            char c = Character.toLowerCase(value.charAt(i));
            if ((c < 'a' || c > 'z') && (c < '0' || c > '9')) continue;
            sb.append(c);
        }
        return sb.toString();
    }

    private static String normalizeArch(String value) {
        switch (value = PlatformDependent.normalize(value)) {
            case "x8664": 
            case "amd64": 
            case "ia32e": 
            case "em64t": 
            case "x64": {
                return "x86_64";
            }
            case "x8632": 
            case "x86": 
            case "i386": 
            case "i486": 
            case "i586": 
            case "i686": 
            case "ia32": 
            case "x32": {
                return "x86_32";
            }
            case "ia64": 
            case "itanium64": {
                return "itanium_64";
            }
            case "sparc": 
            case "sparc32": {
                return "sparc_32";
            }
            case "sparcv9": 
            case "sparc64": {
                return "sparc_64";
            }
            case "arm": 
            case "arm32": {
                return "arm_32";
            }
            case "aarch64": {
                return "aarch_64";
            }
            case "riscv64": {
                return "riscv64";
            }
            case "ppc": 
            case "ppc32": {
                return "ppc_32";
            }
            case "ppc64": {
                return "ppc_64";
            }
            case "ppc64le": {
                return "ppcle_64";
            }
            case "s390": {
                return "s390_32";
            }
            case "s390x": {
                return "s390_64";
            }
            case "loongarch64": {
                return "loongarch_64";
            }
        }
        return "unknown";
    }

    private static String normalizeOs(String value) {
        if ((value = PlatformDependent.normalize(value)).startsWith("aix")) {
            return "aix";
        }
        if (value.startsWith("hpux")) {
            return "hpux";
        }
        if (value.startsWith("os400") && (value.length() <= 5 || !Character.isDigit(value.charAt(5)))) {
            return "os400";
        }
        if (value.startsWith("linux")) {
            return "linux";
        }
        if (value.startsWith("macosx") || value.startsWith("osx") || value.startsWith("darwin")) {
            return "osx";
        }
        if (value.startsWith("freebsd")) {
            return "freebsd";
        }
        if (value.startsWith("openbsd")) {
            return "openbsd";
        }
        if (value.startsWith("netbsd")) {
            return "netbsd";
        }
        if (value.startsWith("solaris") || value.startsWith("sunos")) {
            return "sunos";
        }
        if (value.startsWith("windows")) {
            return "windows";
        }
        return "unknown";
    }

    public static boolean isJfrEnabled() {
        return JFR;
    }

    private PlatformDependent() {
    }

    static {
        boolean jfrAvailable;
        LinkedHashSet<String> availableClassifiers;
        logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
        CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !PlatformDependent.isAndroid();
        UNSAFE_UNAVAILABILITY_CAUSE = PlatformDependent.unsafeUnavailabilityCause0();
        MAX_DIRECT_MEMORY = PlatformDependent.estimateMaxDirectMemory();
        BYTE_ARRAY_BASE_OFFSET = PlatformDependent.byteArrayBaseOffset0();
        TMPDIR = PlatformDependent.tmpdir0();
        BIT_MODE = PlatformDependent.bitMode0();
        NORMALIZED_ARCH = PlatformDependent.normalizeArch(SystemPropertyUtil.get("os.arch", ""));
        NORMALIZED_OS = PlatformDependent.normalizeOs(SystemPropertyUtil.get("os.name", ""));
        IS_WINDOWS = PlatformDependent.isWindows0();
        IS_OSX = PlatformDependent.isOsx0();
        IS_J9_JVM = PlatformDependent.isJ9Jvm0();
        IS_IVKVM_DOT_NET = PlatformDependent.isIkvmDotNet0();
        ADDRESS_SIZE = PlatformDependent.addressSize0();
        BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        NOOP = new Cleaner(){

            @Override
            public CleanableDirectBuffer allocate(final int capacity) {
                return new CleanableDirectBuffer(){
                    private final ByteBuffer byteBuffer;
                    {
                        this.byteBuffer = ByteBuffer.allocateDirect(capacity);
                    }

                    @Override
                    public ByteBuffer buffer() {
                        return this.byteBuffer;
                    }

                    @Override
                    public void clean() {
                    }
                };
            }

            @Override
            public void freeDirectBuffer(ByteBuffer buffer) {
            }
        };
        long maxDirectMemory = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1L);
        if (maxDirectMemory == 0L || !PlatformDependent.hasUnsafe() || !PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
            USE_DIRECT_BUFFER_NO_CLEANER = false;
            DIRECT_CLEANER = NOOP;
            DIRECT_MEMORY_COUNTER = null;
        } else {
            USE_DIRECT_BUFFER_NO_CLEANER = true;
            DIRECT_CLEANER = new DirectCleaner();
            DIRECT_MEMORY_COUNTER = maxDirectMemory < 0L ? ((maxDirectMemory = MAX_DIRECT_MEMORY) <= 0L ? null : new AtomicLong()) : new AtomicLong();
        }
        logger.debug("-Dio.netty.maxDirectMemory: {} bytes", (Object)maxDirectMemory);
        DIRECT_MEMORY_LIMIT = maxDirectMemory >= 1L ? maxDirectMemory : MAX_DIRECT_MEMORY;
        HAS_ALLOCATE_UNINIT_ARRAY = PlatformDependent.javaVersion() >= 9 && PlatformDependent0.hasAllocateArrayMethod();
        MAYBE_SUPER_USER = PlatformDependent.maybeSuperUser0();
        LEGACY_CLEANER = !PlatformDependent.isAndroid() ? (PlatformDependent.javaVersion() >= 9 ? (CleanerJava9.isSupported() ? new CleanerJava9() : (CleanerJava24Linker.isSupported() ? new CleanerJava24Linker() : (CleanerJava25.isSupported() ? new CleanerJava25() : NOOP))) : (CleanerJava6.isSupported() ? new CleanerJava6() : NOOP)) : NOOP;
        CLEANER = USE_DIRECT_BUFFER_NO_CLEANER ? DIRECT_CLEANER : LEGACY_CLEANER;
        EXPLICIT_NO_PREFER_DIRECT = SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
        boolean bl = DIRECT_BUFFER_PREFERRED = CLEANER != NOOP && !EXPLICIT_NO_PREFER_DIRECT;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noPreferDirect: {}", (Object)EXPLICIT_NO_PREFER_DIRECT);
        }
        if (CLEANER == NOOP && !PlatformDependent0.isExplicitNoUnsafe()) {
            logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system instability.");
        }
        if (!PlatformDependent.addPropertyOsClassifiers(availableClassifiers = new LinkedHashSet<String>())) {
            PlatformDependent.addFilesystemOsClassifiers(availableClassifiers);
        }
        LINUX_OS_CLASSIFIERS = Collections.unmodifiableSet(availableClassifiers);
        Throwable jfrFailure = null;
        try {
            jfrAvailable = FlightRecorder.isAvailable();
        }
        catch (Throwable t) {
            jfrFailure = t;
            jfrAvailable = false;
        }
        JFR = SystemPropertyUtil.getBoolean("io.netty.jfr.enabled", jfrAvailable);
        if (logger.isTraceEnabled() && jfrFailure != null) {
            logger.debug("-Dio.netty.jfr.enabled: {}", (Object)JFR, (Object)jfrFailure);
        } else if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.jfr.enabled: {}", (Object)JFR);
        }
        VAR_HANDLE = PlatformDependent.initializeVarHandle();
    }

    private static final class Mpsc {
        private static final boolean USE_MPSC_CHUNKED_ARRAY_QUEUE;

        private Mpsc() {
        }

        static <T> Queue<T> newMpscQueue(int maxCapacity) {
            int capacity = Math.max(Math.min(maxCapacity, 0x40000000), 2048);
            return Mpsc.newChunkedMpscQueue(1024, capacity);
        }

        static <T> Queue<T> newChunkedMpscQueue(int chunkSize, int capacity) {
            return USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscChunkedArrayQueue(chunkSize, capacity) : new MpscChunkedAtomicArrayQueue(chunkSize, capacity);
        }

        static <T> Queue<T> newMpscQueue() {
            return USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscUnboundedArrayQueue(1024) : new MpscUnboundedAtomicArrayQueue(1024);
        }

        static {
            Object unsafe = null;
            if (PlatformDependent.hasUnsafe()) {
                unsafe = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        return UnsafeAccess.UNSAFE;
                    }
                });
            }
            if (unsafe == null) {
                logger.debug("org.jctools-core.MpscChunkedArrayQueue: unavailable");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = false;
            } else {
                logger.debug("org.jctools-core.MpscChunkedArrayQueue: available");
                USE_MPSC_CHUNKED_ARRAY_QUEUE = true;
            }
        }
    }
}

