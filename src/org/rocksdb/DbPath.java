package org.rocksdb;

import java.nio.file.Path;

public class DbPath {
   final Path path;
   final long targetSize;

   public DbPath(Path var1, long var2) {
      this.path = var1;
      this.targetSize = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (var1 != null && this.getClass() == var1.getClass()) {
         DbPath var2 = (DbPath)var1;
         if (this.targetSize != var2.targetSize) {
            return false;
         } else {
            return this.path != null ? this.path.equals(var2.path) : var2.path == null;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.path != null ? this.path.hashCode() : 0;
      return 31 * var1 + (int)(this.targetSize ^ this.targetSize >>> 32);
   }
}
