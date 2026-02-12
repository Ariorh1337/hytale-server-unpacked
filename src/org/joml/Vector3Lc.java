package org.joml;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public interface Vector3Lc {
   long x();

   long y();

   long z();

   LongBuffer get(LongBuffer var1);

   LongBuffer get(int var1, LongBuffer var2);

   ByteBuffer get(ByteBuffer var1);

   ByteBuffer get(int var1, ByteBuffer var2);

   Vector3Lc getToAddress(long var1);

   Vector3L sub(Vector3Lc var1, Vector3L var2);

   Vector3L sub(long var1, long var3, long var5, Vector3L var7);

   Vector3L add(Vector3Lc var1, Vector3L var2);

   Vector3L add(long var1, long var3, long var5, Vector3L var7);

   Vector3L mul(long var1, Vector3L var3);

   Vector3L mul(Vector3Lc var1, Vector3L var2);

   Vector3L mul(long var1, long var3, long var5, Vector3L var7);

   Vector3L div(float var1, Vector3L var2);

   Vector3L div(long var1, Vector3L var3);

   long lengthSquared();

   double length();

   double distance(Vector3Lc var1);

   double distance(long var1, long var3, long var5);

   long gridDistance(Vector3Lc var1);

   long gridDistance(long var1, long var3, long var5);

   long distanceSquared(Vector3Lc var1);

   long distanceSquared(long var1, long var3, long var5);

   Vector3L negate(Vector3L var1);

   Vector3L min(Vector3Lc var1, Vector3L var2);

   Vector3L max(Vector3Lc var1, Vector3L var2);

   long get(int var1) throws IllegalArgumentException;

   int maxComponent();

   int minComponent();

   Vector3L absolute(Vector3L var1);

   boolean equals(long var1, long var3, long var5);
}
