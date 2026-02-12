package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public interface Vector4Lc {
   long x();

   long y();

   long z();

   long w();

   LongBuffer get(LongBuffer var1);

   LongBuffer get(int var1, LongBuffer var2);

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   Vector4Lc getToAddress(long var1);

   Vector4L sub(Vector4Lc var1, Vector4L var2);

   Vector4L sub(Vector4ic var1, Vector4L var2);

   Vector4L sub(long var1, long var3, long var5, long var7, Vector4L var9);

   Vector4L add(Vector4Lc var1, Vector4L var2);

   Vector4L add(Vector4ic var1, Vector4L var2);

   Vector4L add(long var1, long var3, long var5, long var7, Vector4L var9);

   Vector4L mul(Vector4Lc var1, Vector4L var2);

   Vector4L mul(Vector4ic var1, Vector4L var2);

   Vector4L div(Vector4Lc var1, Vector4L var2);

   Vector4L div(Vector4ic var1, Vector4L var2);

   Vector4L mul(long var1, Vector4L var3);

   Vector4L div(float var1, Vector4L var2);

   Vector4L div(long var1, Vector4L var3);

   long lengthSquared();

   double length();

   double distance(Vector4Lc var1);

   double distance(Vector4ic var1);

   double distance(long var1, long var3, long var5, long var7);

   long gridDistance(Vector4Lc var1);

   long gridDistance(Vector4ic var1);

   long gridDistance(long var1, long var3, long var5, long var7);

   long distanceSquared(Vector4Lc var1);

   long distanceSquared(Vector4ic var1);

   long distanceSquared(long var1, long var3, long var5, long var7);

   long dot(Vector4Lc var1);

   long dot(Vector4ic var1);

   Vector4L negate(Vector4L var1);

   Vector4L min(Vector4Lc var1, Vector4L var2);

   Vector4L max(Vector4Lc var1, Vector4L var2);

   long get(int var1) throws IllegalArgumentException;

   int maxComponent();

   int minComponent();

   Vector4L absolute(Vector4L var1);

   boolean equals(long var1, long var3, long var5, long var7);
}
