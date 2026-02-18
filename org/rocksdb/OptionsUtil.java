/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.List;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.ConfigOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.Env;
import org.rocksdb.RocksDBException;
import org.rocksdb.TableFormatConfig;

public class OptionsUtil {
    public static void loadLatestOptions(ConfigOptions configOptions, String string, DBOptions dBOptions, List<ColumnFamilyDescriptor> list) throws RocksDBException {
        OptionsUtil.loadLatestOptions(configOptions.nativeHandle_, string, dBOptions.nativeHandle_, list);
        OptionsUtil.loadTableFormatConfig(list);
    }

    public static void loadOptionsFromFile(ConfigOptions configOptions, String string, DBOptions dBOptions, List<ColumnFamilyDescriptor> list) throws RocksDBException {
        OptionsUtil.loadOptionsFromFile(configOptions.nativeHandle_, string, dBOptions.nativeHandle_, list);
        OptionsUtil.loadTableFormatConfig(list);
    }

    public static String getLatestOptionsFileName(String string, Env env) throws RocksDBException {
        return OptionsUtil.getLatestOptionsFileName(string, env.nativeHandle_);
    }

    private static void loadTableFormatConfig(List<ColumnFamilyDescriptor> list) {
        for (ColumnFamilyDescriptor columnFamilyDescriptor : list) {
            ColumnFamilyOptions columnFamilyOptions = columnFamilyDescriptor.getOptions();
            columnFamilyOptions.setFetchedTableFormatConfig(OptionsUtil.readTableFormatConfig(columnFamilyOptions.nativeHandle_));
        }
    }

    private OptionsUtil() {
    }

    private static native void loadLatestOptions(long var0, String var2, long var3, List<ColumnFamilyDescriptor> var5) throws RocksDBException;

    private static native void loadOptionsFromFile(long var0, String var2, long var3, List<ColumnFamilyDescriptor> var5) throws RocksDBException;

    private static native String getLatestOptionsFileName(String var0, long var1) throws RocksDBException;

    private static native TableFormatConfig readTableFormatConfig(long var0);
}

