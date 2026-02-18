/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.rocksdb.Cache;
import org.rocksdb.MemoryUsageType;
import org.rocksdb.RocksDB;

public class MemoryUtil {
    public static Map<MemoryUsageType, Long> getApproximateMemoryUsageByType(List<RocksDB> list, Set<Cache> set) {
        Object object;
        int n = list == null ? 0 : list.size();
        int n2 = set == null ? 0 : set.size();
        long[] lArray = new long[n];
        long[] lArray2 = new long[n2];
        if (n > 0) {
            object = list.listIterator();
            while (object.hasNext()) {
                lArray[object.nextIndex()] = ((RocksDB)object.next()).nativeHandle_;
            }
        }
        if (n2 > 0) {
            int n3 = 0;
            for (Cache object2 : set) {
                lArray2[n3] = object2.nativeHandle_;
                ++n3;
            }
        }
        object = MemoryUtil.getApproximateMemoryUsageByType(lArray, lArray2);
        HashMap hashMap = new HashMap();
        for (Map.Entry entry : object.entrySet()) {
            hashMap.put(MemoryUsageType.getMemoryUsageType((Byte)entry.getKey()), entry.getValue());
        }
        return hashMap;
    }

    private static native Map<Byte, Long> getApproximateMemoryUsageByType(long[] var0, long[] var1);
}

