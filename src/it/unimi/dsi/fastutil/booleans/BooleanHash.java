package it.unimi.dsi.fastutil.booleans;

public interface BooleanHash {
   interface Strategy {
      int hashCode(boolean var1);

      boolean equals(boolean var1, boolean var2);
   }
}
