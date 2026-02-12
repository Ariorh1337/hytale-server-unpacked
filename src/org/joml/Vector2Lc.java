package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public interface Vector2Lc {
   long x();

   long y();

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   LongBuffer get(LongBuffer var1);

   LongBuffer get(int var1, LongBuffer var2);

   Vector2Lc getToAddress(long var1);

   Vector2L sub(Vector2Lc var1, Vector2L var2);

   Vector2L sub(long var1, long var3, Vector2L var5);

   long lengthSquared();

   double length();

   double distance(Vector2Lc var1);

   double distance(long var1, long var3);

   long distanceSquared(Vector2Lc var1);

   long distanceSquared(long var1, long var3);

   long gridDistance(Vector2Lc var1);

   long gridDistance(long var1, long var3);

   Vector2L add(Vector2Lc var1, Vector2L var2);

   Vector2L add(long var1, long var3, Vector2L var5);

   Vector2L mul(long var1, Vector2L var3);

   Vector2L mul(Vector2Lc var1, Vector2L var2);

   Vector2L mul(long var1, long var3, Vector2L var5);

   Vector2L div(float var1, Vector2L var2);

   Vector2L div(long var1, Vector2L var3);

   Vector2L negate(Vector2L var1);

   Vector2L min(Vector2Lc var1, Vector2L var2);

   Vector2L max(Vector2Lc var1, Vector2L var2);

   long maxComponent();

   long minComponent();

   Vector2L absolute(Vector2L var1);

   long get(int var1) throws IllegalArgumentException;

   boolean equals(long var1, long var3);
}
