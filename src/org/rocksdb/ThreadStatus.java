package org.rocksdb;

import java.util.Map;

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

   private ThreadStatus(long var1, byte var3, String var4, String var5, byte var6, long var7, byte var9, long[] var10, byte var11) {
      this.threadId = var1;
      this.threadType = ThreadType.fromValue(var3);
      this.dbName = var4;
      this.cfName = var5;
      this.operationType = OperationType.fromValue(var6);
      this.operationElapsedTime = var7;
      this.operationStage = OperationStage.fromValue(var9);
      this.operationProperties = var10;
      this.stateType = StateType.fromValue(var11);
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

   public static String getThreadTypeName(ThreadType var0) {
      return getThreadTypeName(var0.getValue());
   }

   public static String getOperationName(OperationType var0) {
      return getOperationName(var0.getValue());
   }

   public static String microsToString(long var0) {
      return microsToStringNative(var0);
   }

   public static String getOperationStageName(OperationStage var0) {
      return getOperationStageName(var0.getValue());
   }

   public static String getOperationPropertyName(OperationType var0, int var1) {
      return getOperationPropertyName(var0.getValue(), var1);
   }

   public static Map<String, Long> interpretOperationProperties(OperationType var0, long[] var1) {
      return interpretOperationProperties(var0.getValue(), var1);
   }

   public static String getStateName(StateType var0) {
      return getStateName(var0.getValue());
   }

   private static native String getThreadTypeName(byte var0);

   private static native String getOperationName(byte var0);

   private static native String microsToStringNative(long var0);

   private static native String getOperationStageName(byte var0);

   private static native String getOperationPropertyName(byte var0, int var1);

   private static native Map<String, Long> interpretOperationProperties(byte var0, long[] var1);

   private static native String getStateName(byte var0);
}
