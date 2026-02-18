/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Map;
import org.rocksdb.WalProcessingOption;
import org.rocksdb.WriteBatch;

public interface WalFilter {
    public void columnFamilyLogNumberMap(Map<Integer, Long> var1, Map<String, Integer> var2);

    public LogRecordFoundResult logRecordFound(long var1, String var3, WriteBatch var4, WriteBatch var5);

    public String name();

    public static class LogRecordFoundResult {
        public static LogRecordFoundResult CONTINUE_UNCHANGED = new LogRecordFoundResult(WalProcessingOption.CONTINUE_PROCESSING, false);
        final WalProcessingOption walProcessingOption;
        final boolean batchChanged;

        public LogRecordFoundResult(WalProcessingOption walProcessingOption, boolean bl) {
            this.walProcessingOption = walProcessingOption;
            this.batchChanged = bl;
        }
    }
}

