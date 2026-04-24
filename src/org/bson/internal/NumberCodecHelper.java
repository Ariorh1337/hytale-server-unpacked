package org.bson.internal;

import java.math.BigDecimal;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.types.Decimal128;

public final class NumberCodecHelper {
   public static byte decodeByte(BsonReader reader) {
      int value = decodeInt(reader);
      if (value >= -128 && value <= 127) {
         return (byte)value;
      } else {
         throw new BsonInvalidOperationException(String.format("%s can not be converted into a Byte.", value));
      }
   }

   public static short decodeShort(BsonReader reader) {
      int value = decodeInt(reader);
      if (value >= -32768 && value <= 32767) {
         return (short)value;
      } else {
         throw new BsonInvalidOperationException(String.format("%s can not be converted into a Short.", value));
      }
   }

   public static int decodeInt(BsonReader reader) {
      BsonType bsonType = reader.getCurrentBsonType();
      int intValue;
      switch (bsonType) {
         case INT32:
            intValue = reader.readInt32();
            break;
         case INT64:
            long longValue = reader.readInt64();
            intValue = (int)longValue;
            if (longValue != intValue) {
               throw invalidConversion(Integer.class, longValue);
            }
            break;
         case DOUBLE:
            double doubleValue = reader.readDouble();
            intValue = (int)doubleValue;
            if (doubleValue != intValue) {
               throw invalidConversion(Integer.class, doubleValue);
            }
            break;
         case DECIMAL128:
            Decimal128 decimal128 = reader.readDecimal128();
            intValue = decimal128.intValue();
            if (!decimal128.equals(new Decimal128(intValue))) {
               throw invalidConversion(Integer.class, decimal128);
            }
            break;
         default:
            throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", bsonType));
      }

      return intValue;
   }

   public static long decodeLong(BsonReader reader) {
      BsonType bsonType = reader.getCurrentBsonType();
      long longValue;
      switch (bsonType) {
         case INT32:
            longValue = reader.readInt32();
            break;
         case INT64:
            longValue = reader.readInt64();
            break;
         case DOUBLE:
            double doubleValue = reader.readDouble();
            longValue = (long)doubleValue;
            if (doubleValue != longValue) {
               throw invalidConversion(Long.class, doubleValue);
            }
            break;
         case DECIMAL128:
            Decimal128 decimal128 = reader.readDecimal128();
            longValue = decimal128.longValue();
            if (!decimal128.equals(new Decimal128(longValue))) {
               throw invalidConversion(Long.class, decimal128);
            }
            break;
         default:
            throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", bsonType));
      }

      return longValue;
   }

   public static float decodeFloat(BsonReader reader) {
      double value = decodeDouble(reader);
      if (!(value < -Float.MAX_VALUE) && !(value > Float.MAX_VALUE)) {
         return (float)value;
      } else {
         throw new BsonInvalidOperationException(String.format("%s can not be converted into a Float.", value));
      }
   }

   public static double decodeDouble(BsonReader reader) {
      BsonType bsonType = reader.getCurrentBsonType();
      double doubleValue;
      switch (bsonType) {
         case INT32:
            doubleValue = reader.readInt32();
            break;
         case INT64:
            long longValue = reader.readInt64();
            doubleValue = longValue;
            if (longValue != (long)doubleValue) {
               throw invalidConversion(Double.class, longValue);
            }
            break;
         case DOUBLE:
            doubleValue = reader.readDouble();
            break;
         case DECIMAL128:
            Decimal128 decimal128 = reader.readDecimal128();

            try {
               doubleValue = decimal128.doubleValue();
               if (!decimal128.equals(new Decimal128(new BigDecimal(doubleValue)))) {
                  throw invalidConversion(Double.class, decimal128);
               }
               break;
            } catch (NumberFormatException e) {
               throw invalidConversion(Double.class, decimal128);
            }
         default:
            throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", bsonType));
      }

      return doubleValue;
   }

   private static <T extends Number> BsonInvalidOperationException invalidConversion(Class<T> clazz, Number value) {
      return new BsonInvalidOperationException(String.format("Could not convert `%s` to a %s without losing precision", value, clazz));
   }

   private NumberCodecHelper() {
   }
}
