package org.bson.types;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.bson.assertions.Assertions;

public final class ObjectId implements Comparable<ObjectId>, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int OBJECT_ID_LENGTH = 12;
   private static final int LOW_ORDER_THREE_BYTES = 16777215;
   private static final long RANDOM_VALUE;
   private static final AtomicInteger NEXT_COUNTER;
   private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   private final int timestamp;
   private final long nonce;

   public static ObjectId get() {
      return new ObjectId();
   }

   public static ObjectId getSmallestWithDate(Date date) {
      return new ObjectId(dateToTimestampSeconds(date), 0L);
   }

   public static boolean isValid(String hexString) {
      if (hexString == null) {
         throw new IllegalArgumentException();
      }

      int len = hexString.length();
      if (len != 24) {
         return false;
      }

      for (int i = 0; i < len; i++) {
         char c = hexString.charAt(i);
         if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
            return false;
         }
      }

      return true;
   }

   public ObjectId() {
      this(new Date());
   }

   public ObjectId(Date date) {
      this(dateToTimestampSeconds(date), RANDOM_VALUE | NEXT_COUNTER.getAndIncrement() & 16777215);
   }

   public ObjectId(Date date, int counter) {
      this(dateToTimestampSeconds(date), getNonceFromUntrustedCounter(counter));
   }

   public ObjectId(int timestamp, int counter) {
      this(timestamp, getNonceFromUntrustedCounter(counter));
   }

   private ObjectId(int timestamp, long nonce) {
      this.timestamp = timestamp;
      this.nonce = nonce;
   }

   private static long getNonceFromUntrustedCounter(int counter) {
      if ((counter & 0xFF000000) != 0) {
         throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
      } else {
         return RANDOM_VALUE | counter;
      }
   }

   public ObjectId(String hexString) {
      this(parseHexString(hexString));
   }

   public ObjectId(byte[] bytes) {
      this(ByteBuffer.wrap(Assertions.isTrueArgument("bytes has length of 12", bytes, Assertions.notNull("bytes", bytes).length == 12)));
   }

   public ObjectId(ByteBuffer buffer) {
      Assertions.notNull("buffer", buffer);
      Assertions.isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= 12);
      ByteOrder originalOrder = buffer.order();

      try {
         buffer.order(ByteOrder.BIG_ENDIAN);
         this.timestamp = buffer.getInt();
         this.nonce = buffer.getLong();
      } finally {
         buffer.order(originalOrder);
      }
   }

   public byte[] toByteArray() {
      return ByteBuffer.allocate(12).putInt(this.timestamp).putLong(this.nonce).array();
   }

   public void putToByteBuffer(ByteBuffer buffer) {
      Assertions.notNull("buffer", buffer);
      Assertions.isTrueArgument("buffer.remaining() >=12", buffer.remaining() >= 12);
      ByteOrder originalOrder = buffer.order();

      try {
         buffer.order(ByteOrder.BIG_ENDIAN);
         buffer.putInt(this.timestamp);
         buffer.putLong(this.nonce);
      } finally {
         buffer.order(originalOrder);
      }
   }

   public int getTimestamp() {
      return this.timestamp;
   }

   public Date getDate() {
      return new Date((this.timestamp & 4294967295L) * 1000L);
   }

   public String toHexString() {
      char[] chars = new char[24];
      int i = 0;

      for (byte b : this.toByteArray()) {
         chars[i++] = HEX_CHARS[b >> 4 & 15];
         chars[i++] = HEX_CHARS[b & 15];
      }

      return new String(chars);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ObjectId other = (ObjectId)o;
         return this.timestamp != other.timestamp ? false : this.nonce == other.nonce;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.timestamp + Long.hashCode(this.nonce);
   }

   public int compareTo(ObjectId other) {
      int cmp = Integer.compareUnsigned(this.timestamp, other.timestamp);
      return cmp != 0 ? cmp : Long.compareUnsigned(this.nonce, other.nonce);
   }

   @Override
   public String toString() {
      return this.toHexString();
   }

   private Object writeReplace() {
      return new ObjectId.SerializationProxy(this);
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   private static byte[] parseHexString(String s) {
      Assertions.notNull("hexString", s);
      Assertions.isTrueArgument("hexString has 24 characters", s.length() == 24);
      byte[] b = new byte[12];

      for (int i = 0; i < b.length; i++) {
         int pos = i << 1;
         char c1 = s.charAt(pos);
         char c2 = s.charAt(pos + 1);
         b[i] = (byte)((hexCharToInt(c1) << 4) + hexCharToInt(c2));
      }

      return b;
   }

   private static int hexCharToInt(char c) {
      if (c >= '0' && c <= '9') {
         return c - 48;
      } else if (c >= 'a' && c <= 'f') {
         return c - 87;
      } else if (c >= 'A' && c <= 'F') {
         return c - 55;
      } else {
         throw new IllegalArgumentException("invalid hexadecimal character: [" + c + "]");
      }
   }

   private static int dateToTimestampSeconds(Date time) {
      return (int)(time.getTime() / 1000L);
   }

   static {
      try {
         SecureRandom secureRandom = new SecureRandom();
         RANDOM_VALUE = secureRandom.nextLong() & -16777216L;
         NEXT_COUNTER = new AtomicInteger(secureRandom.nextInt());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private static class SerializationProxy implements Serializable {
      private static final long serialVersionUID = 1L;
      private final byte[] bytes;

      SerializationProxy(ObjectId objectId) {
         this.bytes = objectId.toByteArray();
      }

      private Object readResolve() {
         return new ObjectId(this.bytes);
      }
   }
}
