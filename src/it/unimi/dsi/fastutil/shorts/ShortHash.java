package it.unimi.dsi.fastutil.shorts;

public interface ShortHash {
   interface Strategy {
      int hashCode(short var1);

      boolean equals(short var1, short var2);
   }
}
