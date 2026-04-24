package org.bson.internal.vector;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import org.bson.BinaryVector;
import org.bson.BsonInvalidOperationException;
import org.bson.Float32BinaryVector;
import org.bson.Int8BinaryVector;
import org.bson.PackedBitBinaryVector;
import org.bson.assertions.Assertions;

public final class BinaryVectorHelper {
   private static final ByteOrder STORED_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
   private static final String ERROR_MESSAGE_UNKNOWN_VECTOR_DATA_TYPE = "Unknown vector data type: ";
   private static final byte ZERO_PADDING = 0;
   private static final int METADATA_SIZE = 2;

   private BinaryVectorHelper() {
   }

   public static byte[] encodeVectorToBinary(BinaryVector vector) {
      BinaryVector.DataType dataType = vector.getDataType();
      switch (dataType) {
         case INT8:
            return encodeVector(dataType.getValue(), (byte)0, vector.asInt8Vector().getData());
         case PACKED_BIT:
            PackedBitBinaryVector packedBitVector = vector.asPackedBitVector();
            return encodeVector(dataType.getValue(), packedBitVector.getPadding(), packedBitVector.getData());
         case FLOAT32:
            return encodeVector(dataType.getValue(), vector.asFloat32Vector().getData());
         default:
            throw Assertions.fail("Unknown vector data type: " + dataType);
      }
   }

   public static BinaryVector decodeBinaryToVector(byte[] encodedVector) {
      isTrue("Vector encoded array length must be at least 2, but found: " + encodedVector.length, encodedVector.length >= 2);
      BinaryVector.DataType dataType = determineVectorDType(encodedVector[0]);
      byte padding = encodedVector[1];
      switch (dataType) {
         case INT8:
            return decodeInt8Vector(encodedVector, padding);
         case PACKED_BIT:
            return decodePackedBitVector(encodedVector, padding);
         case FLOAT32:
            return decodeFloat32Vector(encodedVector, padding);
         default:
            throw Assertions.fail("Unknown vector data type: " + dataType);
      }
   }

   private static Float32BinaryVector decodeFloat32Vector(byte[] encodedVector, byte padding) {
      isTrue("Padding must be 0 for FLOAT32 data type, but found: " + padding, padding == 0);
      return BinaryVector.floatVector(decodeLittleEndianFloats(encodedVector));
   }

   private static PackedBitBinaryVector decodePackedBitVector(byte[] encodedVector, byte padding) {
      byte[] packedBitVector = extractVectorData(encodedVector);
      isTrue("Padding must be 0 if vector is empty, but found: " + padding, padding == 0 || packedBitVector.length > 0);
      isTrue("Padding must be between 0 and 7 bits, but found: " + padding, padding >= 0 && padding <= 7);
      return BinaryVector.packedBitVector(packedBitVector, padding);
   }

   private static Int8BinaryVector decodeInt8Vector(byte[] encodedVector, byte padding) {
      isTrue("Padding must be 0 for INT8 data type, but found: " + padding, padding == 0);
      byte[] int8Vector = extractVectorData(encodedVector);
      return BinaryVector.int8Vector(int8Vector);
   }

   private static byte[] extractVectorData(byte[] encodedVector) {
      int vectorDataLength = encodedVector.length - 2;
      byte[] vectorData = new byte[vectorDataLength];
      System.arraycopy(encodedVector, 2, vectorData, 0, vectorDataLength);
      return vectorData;
   }

   private static byte[] encodeVector(byte dType, byte padding, byte[] vectorData) {
      byte[] bytes = new byte[vectorData.length + 2];
      bytes[0] = dType;
      bytes[1] = padding;
      System.arraycopy(vectorData, 0, bytes, 2, vectorData.length);
      return bytes;
   }

   private static byte[] encodeVector(byte dType, float[] vectorData) {
      byte[] bytes = new byte[vectorData.length * 4 + 2];
      bytes[0] = dType;
      bytes[1] = 0;
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      buffer.order(STORED_BYTE_ORDER);
      ((Buffer)buffer).position(2);
      FloatBuffer floatBuffer = buffer.asFloatBuffer();
      floatBuffer.put(vectorData);
      return bytes;
   }

   private static float[] decodeLittleEndianFloats(byte[] encodedVector) {
      isTrue("Byte array length must be a multiple of 4 for FLOAT32 data type, but found: " + encodedVector.length, (encodedVector.length - 2) % 4 == 0);
      int vectorSize = encodedVector.length - 2;
      int numFloats = vectorSize / 4;
      float[] floatArray = new float[numFloats];
      ByteBuffer buffer = ByteBuffer.wrap(encodedVector, 2, vectorSize);
      buffer.order(STORED_BYTE_ORDER);
      buffer.asFloatBuffer().get(floatArray);
      return floatArray;
   }

   public static BinaryVector.DataType determineVectorDType(byte dType) {
      BinaryVector.DataType[] values = BinaryVector.DataType.values();

      for (BinaryVector.DataType value : values) {
         if (value.getValue() == dType) {
            return value;
         }
      }

      throw new BsonInvalidOperationException("Unknown vector data type: " + dType);
   }

   private static void isTrue(String message, boolean condition) {
      if (!condition) {
         throw new BsonInvalidOperationException(message);
      }
   }
}
