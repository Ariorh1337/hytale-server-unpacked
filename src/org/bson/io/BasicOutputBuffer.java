package org.bson.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bson.ByteBuf;
import org.bson.ByteBufNIO;
import org.bson.types.ObjectId;

public class BasicOutputBuffer extends OutputBuffer {
   private ByteBuffer buffer;

   public BasicOutputBuffer() {
      this(1024);
   }

   public BasicOutputBuffer(int initialSize) {
      this.buffer = ByteBuffer.allocate(initialSize).order(ByteOrder.LITTLE_ENDIAN);
   }

   public byte[] getInternalBuffer() {
      return this.buffer.array();
   }

   @Override
   public void write(byte[] b) {
      this.writeBytes(b, 0, b.length);
   }

   @Override
   public byte[] toByteArray() {
      this.ensureOpen();
      return Arrays.copyOf(this.buffer.array(), this.buffer.position());
   }

   @Override
   public void writeInt32(int value) {
      this.ensureOpen();
      this.ensure(4);
      this.buffer.putInt(value);
   }

   @Override
   public void writeInt32(int position, int value) {
      this.ensureOpen();
      this.checkPosition(position, 4);
      this.buffer.putInt(position, value);
   }

   @Override
   public void writeInt64(long value) {
      this.ensureOpen();
      this.ensure(8);
      this.buffer.putLong(value);
   }

   @Override
   public void writeObjectId(ObjectId value) {
      this.ensureOpen();
      this.ensure(12);
      value.putToByteBuffer(this.buffer);
   }

   @Override
   public void writeBytes(byte[] bytes, int offset, int length) {
      this.ensureOpen();
      this.ensure(length);
      this.buffer.put(bytes, offset, length);
   }

   @Override
   public void writeByte(int value) {
      this.ensureOpen();
      this.ensure(1);
      this.buffer.put((byte)(0xFF & value));
   }

   @Override
   protected void write(int absolutePosition, int value) {
      this.ensureOpen();
      this.checkPosition(absolutePosition, 1);
      this.buffer.put(absolutePosition, (byte)(0xFF & value));
   }

   @Override
   public int getPosition() {
      this.ensureOpen();
      return this.buffer.position();
   }

   @Override
   public int getSize() {
      this.ensureOpen();
      return this.buffer.position();
   }

   @Override
   public int pipe(OutputStream out) throws IOException {
      this.ensureOpen();
      out.write(this.buffer.array(), 0, this.buffer.position());
      return this.buffer.position();
   }

   @Override
   public void truncateToPosition(int newPosition) {
      this.ensureOpen();
      if (newPosition <= this.buffer.position() && newPosition >= 0) {
         ((Buffer)this.buffer).position(newPosition);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public List<ByteBuf> getByteBuffers() {
      this.ensureOpen();
      ByteBuffer flipped = ByteBuffer.wrap(this.buffer.array(), 0, this.buffer.position());
      return Collections.singletonList(new ByteBufNIO(flipped));
   }

   @Override
   public void close() {
      this.buffer = null;
   }

   private void ensureOpen() {
      if (this.buffer == null) {
         throw new IllegalStateException("The output is closed");
      }
   }

   private void ensure(int more) {
      int length = this.buffer.position();
      int need = length + more;
      if (need > this.buffer.capacity()) {
         int newSize = length * 2;
         if (newSize < need) {
            newSize = need + 128;
         }

         ByteBuffer tmp = ByteBuffer.allocate(newSize).order(ByteOrder.LITTLE_ENDIAN);
         tmp.put(this.buffer.array(), 0, length);
         this.buffer = tmp;
      }
   }

   private void checkPosition(int absolutePosition, int bytesToWrite) {
      if (absolutePosition < 0) {
         throw new IllegalArgumentException(String.format("position must be >= 0 but was %d", absolutePosition));
      }

      if (absolutePosition > this.buffer.position() - bytesToWrite) {
         throw new IllegalArgumentException(String.format("position must be <= %d but was %d", this.buffer.position() - bytesToWrite, absolutePosition));
      }
   }
}
