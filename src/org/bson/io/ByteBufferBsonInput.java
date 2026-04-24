package org.bson.io;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import org.bson.BsonSerializationException;
import org.bson.ByteBuf;
import org.bson.types.ObjectId;

public class ByteBufferBsonInput implements BsonInput {
   private static final String[] ONE_BYTE_ASCII_STRINGS = new String[128];
   private byte[] scratchBuffer;
   private ByteBuf buffer;

   public ByteBufferBsonInput(ByteBuf buffer) {
      if (buffer == null) {
         throw new IllegalArgumentException("buffer can not be null");
      }

      this.buffer = buffer;
      buffer.order(ByteOrder.LITTLE_ENDIAN);
   }

   @Override
   public int getPosition() {
      this.ensureOpen();
      return this.buffer.position();
   }

   @Override
   public byte readByte() {
      this.ensureOpen();
      this.ensureAvailable(1);
      return this.buffer.get();
   }

   @Override
   public void readBytes(byte[] bytes) {
      this.ensureOpen();
      this.ensureAvailable(bytes.length);
      this.buffer.get(bytes);
   }

   @Override
   public void readBytes(byte[] bytes, int offset, int length) {
      this.ensureOpen();
      this.ensureAvailable(length);
      this.buffer.get(bytes, offset, length);
   }

   @Override
   public long readInt64() {
      this.ensureOpen();
      this.ensureAvailable(8);
      return this.buffer.getLong();
   }

   @Override
   public double readDouble() {
      this.ensureOpen();
      this.ensureAvailable(8);
      return this.buffer.getDouble();
   }

   @Override
   public int readInt32() {
      this.ensureOpen();
      this.ensureAvailable(4);
      return this.buffer.getInt();
   }

   @Override
   public ObjectId readObjectId() {
      this.ensureOpen();
      byte[] bytes = new byte[12];
      this.readBytes(bytes);
      return new ObjectId(bytes);
   }

   @Override
   public String readString() {
      this.ensureOpen();
      int size = this.readInt32();
      if (size <= 0) {
         throw new BsonSerializationException(String.format("While decoding a BSON string found a size that is not a positive number: %d", size));
      }

      this.ensureAvailable(size);
      return this.readString(size);
   }

   @Override
   public String readCString() {
      this.ensureOpen();
      int size = this.computeCStringLength(this.buffer.position());
      return this.readString(size);
   }

   private String readString(int bsonStringSize) {
      if (bsonStringSize == 2) {
         byte asciiByte = this.buffer.get();
         byte nullByte = this.buffer.get();
         if (nullByte != 0) {
            throw new BsonSerializationException("Found a BSON string that is not null-terminated");
         } else {
            return asciiByte < 0 ? StandardCharsets.UTF_8.newDecoder().replacement() : ONE_BYTE_ASCII_STRINGS[asciiByte];
         }
      } else if (this.buffer.isBackedByArray()) {
         int position = this.buffer.position();
         int arrayOffset = this.buffer.arrayOffset();
         int newPosition = position + bsonStringSize;
         this.buffer.position(newPosition);
         byte[] array = this.buffer.array();
         if (array[arrayOffset + newPosition - 1] != 0) {
            throw new BsonSerializationException("Found a BSON string that is not null-terminated");
         } else {
            return new String(array, arrayOffset + position, bsonStringSize - 1, StandardCharsets.UTF_8);
         }
      } else {
         if (this.scratchBuffer == null || bsonStringSize > this.scratchBuffer.length) {
            int scratchBufferSize = bsonStringSize + (bsonStringSize >>> 1);
            this.scratchBuffer = new byte[scratchBufferSize];
         }

         this.buffer.get(this.scratchBuffer, 0, bsonStringSize);
         if (this.scratchBuffer[bsonStringSize - 1] != 0) {
            throw new BsonSerializationException("BSON string not null-terminated");
         } else {
            return new String(this.scratchBuffer, 0, bsonStringSize - 1, StandardCharsets.UTF_8);
         }
      }
   }

   @Override
   public void skipCString() {
      this.ensureOpen();
      int pos = this.buffer.position();
      int length = this.computeCStringLength(pos);
      this.buffer.position(pos + length);
   }

   private int computeCStringLength(int prevPos) {
      int pos = prevPos;
      int limit = this.buffer.limit();
      int chunks = limit - pos >>> 3;

      for (int toPos = pos + (chunks << 3); pos < toPos; pos += 8) {
         long chunk = this.buffer.getLong(pos);
         long mask = chunk - 72340172838076673L;
         mask &= ~chunk;
         mask &= -9187201950435737472L;
         if (mask != 0L) {
            int offset = Long.numberOfTrailingZeros(mask) >>> 3;
            return pos - prevPos + offset + 1;
         }
      }

      while (pos < limit) {
         if (this.buffer.get(pos++) == 0) {
            return pos - prevPos;
         }
      }

      this.buffer.position(pos);
      throw new BsonSerializationException("Found a BSON string that is not null-terminated");
   }

   @Override
   public void skip(int numBytes) {
      this.ensureOpen();
      this.buffer.position(this.buffer.position() + numBytes);
   }

   @Override
   public BsonInputMark getMark(int readLimit) {
      return new BsonInputMark() {
         private final int mark = ByteBufferBsonInput.this.buffer.position();

         @Override
         public void reset() {
            ByteBufferBsonInput.this.ensureOpen();
            ByteBufferBsonInput.this.buffer.position(this.mark);
         }
      };
   }

   @Override
   public boolean hasRemaining() {
      this.ensureOpen();
      return this.buffer.hasRemaining();
   }

   @Override
   public void close() {
      this.buffer.release();
      this.buffer = null;
   }

   private void ensureOpen() {
      if (this.buffer == null) {
         throw new IllegalStateException("Stream is closed");
      }
   }

   private void ensureAvailable(int bytesNeeded) {
      if (this.buffer.remaining() < bytesNeeded) {
         throw new BsonSerializationException(
            String.format("While decoding a BSON document %d bytes were required, but only %d remain", bytesNeeded, this.buffer.remaining())
         );
      }
   }

   static {
      for (int b = 0; b < ONE_BYTE_ASCII_STRINGS.length; b++) {
         ONE_BYTE_ASCII_STRINGS[b] = String.valueOf((char)b);
      }
   }
}
