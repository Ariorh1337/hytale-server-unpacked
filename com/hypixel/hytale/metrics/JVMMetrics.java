/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.metrics.InitStackThread;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.sun.management.OperatingSystemMXBean;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class JVMMetrics {
    @Nonnull
    public static final MetricsRegistry<ClassLoader> CLASS_LOADER_METRICS_REGISTRY = new MetricsRegistry();
    @Nonnull
    public static final MetricsRegistry<MemoryUsage> MEMORY_USAGE_METRICS_REGISTRY;
    @Nonnull
    public static final MetricsRegistry<GarbageCollectorMXBean> GARBAGE_COLLECTOR_METRICS_REGISTRY;
    @Nonnull
    public static final MetricsRegistry<MemoryPoolMXBean> MEMORY_POOL_METRICS_REGISTRY;
    @Nonnull
    public static final MetricsRegistry<Void> METRICS_REGISTRY;

    static {
        CLASS_LOADER_METRICS_REGISTRY.register("Name", ClassLoader::getName, Codec.STRING);
        CLASS_LOADER_METRICS_REGISTRY.register("Parent", ClassLoader::getParent, CLASS_LOADER_METRICS_REGISTRY);
        MEMORY_USAGE_METRICS_REGISTRY = new MetricsRegistry();
        MEMORY_USAGE_METRICS_REGISTRY.register("Init", MemoryUsage::getInit, Codec.LONG);
        MEMORY_USAGE_METRICS_REGISTRY.register("Used", MemoryUsage::getUsed, Codec.LONG);
        MEMORY_USAGE_METRICS_REGISTRY.register("Committed", MemoryUsage::getCommitted, Codec.LONG);
        MEMORY_USAGE_METRICS_REGISTRY.register("Max", MemoryUsage::getMax, Codec.LONG);
        GARBAGE_COLLECTOR_METRICS_REGISTRY = new MetricsRegistry();
        GARBAGE_COLLECTOR_METRICS_REGISTRY.register("Name", MemoryManagerMXBean::getName, Codec.STRING);
        GARBAGE_COLLECTOR_METRICS_REGISTRY.register("MemoryPoolNames", MemoryManagerMXBean::getMemoryPoolNames, Codec.STRING_ARRAY);
        GARBAGE_COLLECTOR_METRICS_REGISTRY.register("CollectionCount", GarbageCollectorMXBean::getCollectionCount, Codec.LONG);
        GARBAGE_COLLECTOR_METRICS_REGISTRY.register("CollectionTime", GarbageCollectorMXBean::getCollectionTime, Codec.LONG);
        MEMORY_POOL_METRICS_REGISTRY = new MetricsRegistry();
        MEMORY_POOL_METRICS_REGISTRY.register("Name", MemoryPoolMXBean::getName, Codec.STRING);
        MEMORY_POOL_METRICS_REGISTRY.register("Type", MemoryPoolMXBean::getType, new EnumCodec<MemoryType>(MemoryType.class));
        MEMORY_POOL_METRICS_REGISTRY.register("PeakUsage", MemoryPoolMXBean::getPeakUsage, MEMORY_USAGE_METRICS_REGISTRY);
        MEMORY_POOL_METRICS_REGISTRY.register("Usage", MemoryPoolMXBean::getUsage, MEMORY_USAGE_METRICS_REGISTRY);
        MEMORY_POOL_METRICS_REGISTRY.register("CollectionUsage", MemoryPoolMXBean::getCollectionUsage, MEMORY_USAGE_METRICS_REGISTRY);
        MetricsRegistry<MemoryPoolMXBean> usageThreshold = new MetricsRegistry<MemoryPoolMXBean>();
        usageThreshold.register("Threshold", MemoryPoolMXBean::getUsageThreshold, Codec.LONG);
        usageThreshold.register("ThresholdCount", MemoryPoolMXBean::getUsageThresholdCount, Codec.LONG);
        usageThreshold.register("ThresholdExceeded", MemoryPoolMXBean::isUsageThresholdExceeded, Codec.BOOLEAN);
        MEMORY_POOL_METRICS_REGISTRY.register("UsageThreshold", memoryPoolMXBean -> {
            if (!memoryPoolMXBean.isUsageThresholdSupported()) {
                return null;
            }
            return memoryPoolMXBean;
        }, usageThreshold);
        MetricsRegistry<MemoryPoolMXBean> collectionUsageThreshold = new MetricsRegistry<MemoryPoolMXBean>();
        collectionUsageThreshold.register("Threshold", MemoryPoolMXBean::getCollectionUsageThreshold, Codec.LONG);
        collectionUsageThreshold.register("ThresholdCount", MemoryPoolMXBean::getCollectionUsageThresholdCount, Codec.LONG);
        collectionUsageThreshold.register("ThresholdExceeded", MemoryPoolMXBean::isCollectionUsageThresholdExceeded, Codec.BOOLEAN);
        MEMORY_POOL_METRICS_REGISTRY.register("CollectionUsageThreshold", memoryPoolMXBean -> {
            if (!memoryPoolMXBean.isCollectionUsageThresholdSupported()) {
                return null;
            }
            return memoryPoolMXBean;
        }, collectionUsageThreshold);
        METRICS_REGISTRY = new MetricsRegistry();
        MetricsRegistry<java.lang.management.OperatingSystemMXBean> operatingSystem = new MetricsRegistry<java.lang.management.OperatingSystemMXBean>();
        METRICS_REGISTRY.register("PROCESSOR", unused -> System.getenv("PROCESSOR_IDENTIFIER"), Codec.STRING);
        METRICS_REGISTRY.register("PROCESSOR_ARCHITECTURE", unused -> System.getenv("PROCESSOR_ARCHITECTURE"), Codec.STRING);
        METRICS_REGISTRY.register("PROCESSOR_ARCHITEW6432", unused -> System.getenv("PROCESSOR_ARCHITEW6432"), Codec.STRING);
        operatingSystem.register("OSName", java.lang.management.OperatingSystemMXBean::getName, Codec.STRING);
        operatingSystem.register("OSArch", java.lang.management.OperatingSystemMXBean::getArch, Codec.STRING);
        operatingSystem.register("OSVersion", java.lang.management.OperatingSystemMXBean::getVersion, Codec.STRING);
        operatingSystem.register("AvailableProcessors", unused -> Runtime.getRuntime().availableProcessors(), Codec.INTEGER);
        operatingSystem.register("SystemLoadAverage", java.lang.management.OperatingSystemMXBean::getSystemLoadAverage, Codec.DOUBLE);
        if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean) {
            operatingSystem.register("CpuLoad", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getCpuLoad(), Codec.DOUBLE);
            operatingSystem.register("ProcessCpuLoad", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getProcessCpuLoad(), Codec.DOUBLE);
            operatingSystem.register("TotalMemorySize", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getTotalMemorySize(), Codec.LONG);
            operatingSystem.register("FreeMemorySize", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getFreeMemorySize(), Codec.LONG);
            operatingSystem.register("TotalSwapSpaceSize", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getTotalSwapSpaceSize(), Codec.LONG);
            operatingSystem.register("FreeSwapSpaceSize", operatingSystemMXBean -> ((OperatingSystemMXBean)operatingSystemMXBean).getFreeSwapSpaceSize(), Codec.LONG);
        }
        METRICS_REGISTRY.register("System", aVoid -> ManagementFactory.getOperatingSystemMXBean(), operatingSystem);
        MetricsRegistry<RuntimeMXBean> runtimeBean = new MetricsRegistry<RuntimeMXBean>();
        runtimeBean.register("StartTime", runtimeMXBean -> Instant.ofEpochMilli(runtimeMXBean.getStartTime()), Codec.INSTANT);
        runtimeBean.register("Uptime", runtimeMXBean -> Duration.ofMillis(runtimeMXBean.getUptime()), Codec.DURATION);
        runtimeBean.register("RuntimeName", RuntimeMXBean::getName, Codec.STRING);
        runtimeBean.register("SpecName", RuntimeMXBean::getSpecName, Codec.STRING);
        runtimeBean.register("SpecVendor", RuntimeMXBean::getSpecVendor, Codec.STRING);
        runtimeBean.register("SpecVersion", RuntimeMXBean::getSpecVersion, Codec.STRING);
        runtimeBean.register("ManagementSpecVersion", RuntimeMXBean::getManagementSpecVersion, Codec.STRING);
        runtimeBean.register("VMName", RuntimeMXBean::getVmName, Codec.STRING);
        runtimeBean.register("VMVendor", RuntimeMXBean::getVmVendor, Codec.STRING);
        runtimeBean.register("VMVersion", RuntimeMXBean::getVmVersion, Codec.STRING);
        runtimeBean.register("LibraryPath", RuntimeMXBean::getLibraryPath, Codec.STRING);
        try {
            ManagementFactory.getRuntimeMXBean().getBootClassPath();
            runtimeBean.register("BootClassPath", RuntimeMXBean::getBootClassPath, Codec.STRING);
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        runtimeBean.register("ClassPath", RuntimeMXBean::getClassPath, Codec.STRING);
        runtimeBean.register("InputArguments", runtimeMXBean -> (String[])runtimeMXBean.getInputArguments().toArray(String[]::new), Codec.STRING_ARRAY);
        runtimeBean.register("SystemProperties", RuntimeMXBean::getSystemProperties, new MapCodec<String, HashMap>(Codec.STRING, HashMap::new));
        METRICS_REGISTRY.register("Runtime", aVoid -> ManagementFactory.getRuntimeMXBean(), runtimeBean);
        MetricsRegistry<MemoryMXBean> memoryBean = new MetricsRegistry<MemoryMXBean>();
        memoryBean.register("ObjectPendingFinalizationCount", memoryMXBean -> memoryMXBean.getObjectPendingFinalizationCount(), Codec.INTEGER);
        memoryBean.register("HeapMemoryUsage", memoryMXBean -> memoryMXBean.getHeapMemoryUsage(), MEMORY_USAGE_METRICS_REGISTRY);
        memoryBean.register("NonHeapMemoryUsage", memoryMXBean -> memoryMXBean.getNonHeapMemoryUsage(), MEMORY_USAGE_METRICS_REGISTRY);
        METRICS_REGISTRY.register("Memory", aVoid -> ManagementFactory.getMemoryMXBean(), memoryBean);
        METRICS_REGISTRY.register("GarbageCollectors", memoryMXBean -> (GarbageCollectorMXBean[])ManagementFactory.getGarbageCollectorMXBeans().toArray(GarbageCollectorMXBean[]::new), new ArrayCodec<GarbageCollectorMXBean>(GARBAGE_COLLECTOR_METRICS_REGISTRY, GarbageCollectorMXBean[]::new));
        METRICS_REGISTRY.register("MemoryPools", memoryMXBean -> (MemoryPoolMXBean[])ManagementFactory.getMemoryPoolMXBeans().toArray(MemoryPoolMXBean[]::new), new ArrayCodec<MemoryPoolMXBean>(MEMORY_POOL_METRICS_REGISTRY, MemoryPoolMXBean[]::new));
        METRICS_REGISTRY.register("Threads", aVoid -> {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
            Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
            Long2ObjectOpenHashMap<Thread> threadIdMap = new Long2ObjectOpenHashMap<Thread>();
            for (Thread thread : stackTraces.keySet()) {
                threadIdMap.put(thread.getId(), thread);
            }
            ThreadMetricData[] data = new ThreadMetricData[threadInfos.length];
            for (int i = 0; i < threadInfos.length; ++i) {
                ThreadInfo threadInfo = threadInfos[i];
                data[i] = new ThreadMetricData(threadInfo, (Thread)threadIdMap.get(threadInfo.getThreadId()), threadMXBean);
            }
            return data;
        }, new ArrayCodec<ThreadMetricData>(ThreadMetricData.METRICS_REGISTRY, ThreadMetricData[]::new));
        METRICS_REGISTRY.register("SecurityManager", aVoid -> {
            SecurityManager securityManager = System.getSecurityManager();
            return securityManager == null ? null : securityManager.getClass().getName();
        }, Codec.STRING);
        MetricsRegistry<ClassLoadingMXBean> classLoading = new MetricsRegistry<ClassLoadingMXBean>();
        classLoading.register("LoadedClassCount", ClassLoadingMXBean::getLoadedClassCount, Codec.INTEGER);
        classLoading.register("UnloadedClassCount", ClassLoadingMXBean::getUnloadedClassCount, Codec.LONG);
        classLoading.register("TotalLoadedClassCount", ClassLoadingMXBean::getTotalLoadedClassCount, Codec.LONG);
        classLoading.register("SystemClassloader", unused -> ClassLoader.getSystemClassLoader(), CLASS_LOADER_METRICS_REGISTRY);
        classLoading.register("JVMMetricsClassloader", unused -> JVMMetrics.class.getClassLoader(), CLASS_LOADER_METRICS_REGISTRY);
        METRICS_REGISTRY.register("ClassLoading", aVoid -> ManagementFactory.getClassLoadingMXBean(), classLoading);
    }

    private static class ThreadMetricData {
        @Nonnull
        public static final MetricsRegistry<StackTraceElement> STACK_TRACE_ELEMENT_METRICS_REGISTRY = new MetricsRegistry();
        @Nonnull
        public static final MetricsRegistry<ThreadMetricData> METRICS_REGISTRY;
        private final ThreadInfo threadInfo;
        private final Thread thread;
        private final ThreadMXBean threadMXBean;

        public ThreadMetricData(ThreadInfo threadInfo, Thread thread, ThreadMXBean threadMXBean) {
            this.threadInfo = threadInfo;
            this.thread = thread;
            this.threadMXBean = threadMXBean;
        }

        static {
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("FileName", StackTraceElement::getFileName, Codec.STRING);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("LineNumber", StackTraceElement::getLineNumber, Codec.INTEGER);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("ModuleName", StackTraceElement::getModuleName, Codec.STRING);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("ModuleVersion", StackTraceElement::getModuleVersion, Codec.STRING);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("ClassLoaderName", StackTraceElement::getClassLoaderName, Codec.STRING);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("ClassName", StackTraceElement::getClassName, Codec.STRING);
            STACK_TRACE_ELEMENT_METRICS_REGISTRY.register("MethodName", StackTraceElement::getMethodName, Codec.STRING);
            METRICS_REGISTRY = new MetricsRegistry();
            METRICS_REGISTRY.register("Id", threadMetricData -> threadMetricData.threadInfo.getThreadId(), Codec.LONG);
            METRICS_REGISTRY.register("Name", threadMetricData -> threadMetricData.threadInfo.getThreadName(), Codec.STRING);
            METRICS_REGISTRY.register("State", threadMetricData -> threadMetricData.threadInfo.getThreadState(), new EnumCodec<Thread.State>(Thread.State.class));
            METRICS_REGISTRY.register("Priority", threadMetricData -> threadMetricData.threadInfo.getPriority(), Codec.INTEGER);
            METRICS_REGISTRY.register("Daemon", threadMetricData -> threadMetricData.threadInfo.isDaemon(), Codec.BOOLEAN);
            METRICS_REGISTRY.register("CPUTime", threadMetricData -> threadMetricData.threadMXBean.getThreadCpuTime(threadMetricData.threadInfo.getThreadId()), Codec.LONG);
            METRICS_REGISTRY.register("WaitedTime", threadMetricData -> threadMetricData.threadInfo.getWaitedTime(), Codec.LONG);
            METRICS_REGISTRY.register("WaitedCount", threadMetricData -> threadMetricData.threadInfo.getWaitedCount(), Codec.LONG);
            METRICS_REGISTRY.register("BlockedTime", threadMetricData -> threadMetricData.threadInfo.getBlockedTime(), Codec.LONG);
            METRICS_REGISTRY.register("BlockedCount", threadMetricData -> threadMetricData.threadInfo.getBlockedCount(), Codec.LONG);
            METRICS_REGISTRY.register("LockName", threadMetricData -> threadMetricData.threadInfo.getLockName(), Codec.STRING);
            METRICS_REGISTRY.register("LockOwnerId", threadMetricData -> threadMetricData.threadInfo.getLockOwnerId(), Codec.LONG);
            METRICS_REGISTRY.register("LockOwnerName", threadMetricData -> threadMetricData.threadInfo.getLockOwnerName(), Codec.STRING);
            METRICS_REGISTRY.register("StackTrace", threadMetricData -> threadMetricData.threadInfo.getStackTrace(), new ArrayCodec<StackTraceElement>(STACK_TRACE_ELEMENT_METRICS_REGISTRY, StackTraceElement[]::new));
            METRICS_REGISTRY.register("InitStackTrace", threadMetricData -> threadMetricData.thread instanceof InitStackThread ? ((InitStackThread)((Object)threadMetricData.thread)).getInitStack() : null, new ArrayCodec<StackTraceElement>(STACK_TRACE_ELEMENT_METRICS_REGISTRY, StackTraceElement[]::new));
            METRICS_REGISTRY.register("Interrupted", threadMetricData -> threadMetricData.thread != null ? Boolean.valueOf(threadMetricData.thread.isInterrupted()) : null, Codec.BOOLEAN);
            METRICS_REGISTRY.register("ThreadClass", threadMetricData -> threadMetricData.thread != null ? threadMetricData.thread.getClass().getName() : null, Codec.STRING);
            MetricsRegistry<ThreadGroup> threadGroup = new MetricsRegistry<ThreadGroup>();
            threadGroup.register("Name", ThreadGroup::getName, Codec.STRING);
            threadGroup.register("Parent", ThreadGroup::getParent, threadGroup);
            threadGroup.register("MaxPriority", ThreadGroup::getMaxPriority, Codec.INTEGER);
            threadGroup.register("Destroyed", ThreadGroup::isDestroyed, Codec.BOOLEAN);
            threadGroup.register("Daemon", ThreadGroup::isDaemon, Codec.BOOLEAN);
            threadGroup.register("ActiveCount", ThreadGroup::activeCount, Codec.INTEGER);
            threadGroup.register("ActiveGroupCount", ThreadGroup::activeGroupCount, Codec.INTEGER);
            METRICS_REGISTRY.register("ThreadGroup", threadMetricData -> threadMetricData.thread != null ? threadMetricData.thread.getThreadGroup() : null, threadGroup);
            METRICS_REGISTRY.register("UncaughtExceptionHandler", threadMetricData -> threadMetricData.thread != null ? threadMetricData.thread.getUncaughtExceptionHandler().getClass().getName() : null, Codec.STRING);
        }
    }
}

