/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Map;
import org.rocksdb.OperationStage;
import org.rocksdb.OperationType;
import org.rocksdb.StateType;
import org.rocksdb.ThreadType;

public class ThreadStatus {
    private final long threadId;
    private final ThreadType threadType;
    private final String dbName;
    private final String cfName;
    private final OperationType operationType;
    private final long operationElapsedTime;
    private final OperationStage operationStage;
    private final long[] operationProperties;
    private final StateType stateType;

    private ThreadStatus(long l, byte by, String string, String string2, byte by2, long l2, byte by3, long[] lArray, byte by4) {
        this.threadId = l;
        this.threadType = ThreadType.fromValue(by);
        this.dbName = string;
        this.cfName = string2;
        this.operationType = OperationType.fromValue(by2);
        this.operationElapsedTime = l2;
        this.operationStage = OperationStage.fromValue(by3);
        this.operationProperties = lArray;
        this.stateType = StateType.fromValue(by4);
    }

    public long getThreadId() {
        return this.threadId;
    }

    public ThreadType getThreadType() {
        return this.threadType;
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getCfName() {
        return this.cfName;
    }

    public OperationType getOperationType() {
        return this.operationType;
    }

    public long getOperationElapsedTime() {
        return this.operationElapsedTime;
    }

    public OperationStage getOperationStage() {
        return this.operationStage;
    }

    public long[] getOperationProperties() {
        return this.operationProperties;
    }

    public StateType getStateType() {
        return this.stateType;
    }

    public static String getThreadTypeName(ThreadType threadType) {
        return ThreadStatus.getThreadTypeName(threadType.getValue());
    }

    public static String getOperationName(OperationType operationType) {
        return ThreadStatus.getOperationName(operationType.getValue());
    }

    public static String microsToString(long l) {
        return ThreadStatus.microsToStringNative(l);
    }

    public static String getOperationStageName(OperationStage operationStage) {
        return ThreadStatus.getOperationStageName(operationStage.getValue());
    }

    public static String getOperationPropertyName(OperationType operationType, int n) {
        return ThreadStatus.getOperationPropertyName(operationType.getValue(), n);
    }

    public static Map<String, Long> interpretOperationProperties(OperationType operationType, long[] lArray) {
        return ThreadStatus.interpretOperationProperties(operationType.getValue(), lArray);
    }

    public static String getStateName(StateType stateType) {
        return ThreadStatus.getStateName(stateType.getValue());
    }

    private static native String getThreadTypeName(byte var0);

    private static native String getOperationName(byte var0);

    private static native String microsToStringNative(long var0);

    private static native String getOperationStageName(byte var0);

    private static native String getOperationPropertyName(byte var0, int var1);

    private static native Map<String, Long> interpretOperationProperties(byte var0, long[] var1);

    private static native String getStateName(byte var0);
}

