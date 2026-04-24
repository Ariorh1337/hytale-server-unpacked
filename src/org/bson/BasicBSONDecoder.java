package org.bson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.bson.assertions.Assertions;
import org.bson.io.ByteBufferBsonInput;

public class BasicBSONDecoder implements BSONDecoder {
   private static volatile UuidRepresentation defaultUuidRepresentation = UuidRepresentation.JAVA_LEGACY;

   public static void setDefaultUuidRepresentation(UuidRepresentation uuidRepresentation) {
      defaultUuidRepresentation = Assertions.notNull("uuidRepresentation", uuidRepresentation);
   }

   public static UuidRepresentation getDefaultUuidRepresentation() {
      return defaultUuidRepresentation;
   }

   @Override
   public BSONObject readObject(byte[] bytes) {
      BSONCallback bsonCallback = new BasicBSONCallback();
      this.decode(bytes, bsonCallback);
      return (BSONObject)bsonCallback.get();
   }

   @Override
   public BSONObject readObject(InputStream in) throws IOException {
      return this.readObject(this.readFully(in));
   }

   @Override
   public int decode(byte[] bytes, BSONCallback callback) {
      BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(new ByteBufNIO(ByteBuffer.wrap(bytes))));

      int var5;
      try {
         BsonWriter writer = new BSONCallbackAdapter(new BsonWriterSettings(), callback);
         writer.pipe(reader);
         var5 = reader.getBsonInput().getPosition();
      } catch (Throwable var7) {
         try {
            reader.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      reader.close();
      return var5;
   }

   @Override
   public int decode(InputStream in, BSONCallback callback) throws IOException {
      return this.decode(this.readFully(in), callback);
   }

   private byte[] readFully(InputStream input) throws IOException {
      byte[] sizeBytes = new byte[4];
      Bits.readFully(input, sizeBytes);
      int size = Bits.readInt(sizeBytes);
      byte[] buffer = new byte[size];
      System.arraycopy(sizeBytes, 0, buffer, 0, 4);
      Bits.readFully(input, buffer, 4, size - 4);
      return buffer;
   }
}
