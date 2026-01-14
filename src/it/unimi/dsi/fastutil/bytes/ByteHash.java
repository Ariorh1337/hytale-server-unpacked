package it.unimi.dsi.fastutil.bytes;

public interface ByteHash {
   interface Strategy {
      int hashCode(byte var1);

      boolean equals(byte var1, byte var2);
   }
}
