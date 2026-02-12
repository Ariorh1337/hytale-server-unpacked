package org.rocksdb;

import java.nio.ByteBuffer;

public class ByteBufferGetStatus {
   public final Status status;
   public final int requiredSize;
   public final ByteBuffer value;

   ByteBufferGetStatus(Status var1, int var2, ByteBuffer var3) {
      this.status = var1;
      this.requiredSize = var2;
      this.value = var3;
   }

   ByteBufferGetStatus(Status var1) {
      this.status = var1;
      this.requiredSize = 0;
      this.value = null;
   }
}
