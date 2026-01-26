/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.math.BigDecimal;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.types.Decimal128;

final class NumberCodecHelper {
    static int decodeInt(BsonReader reader) {
        int intValue;
        BsonType bsonType = reader.getCurrentBsonType();
        switch (bsonType) {
            case INT32: {
                intValue = reader.readInt32();
                break;
            }
            case INT64: {
                long longValue = reader.readInt64();
                intValue = (int)longValue;
                if (longValue == (long)intValue) break;
                throw NumberCodecHelper.invalidConversion(Integer.class, longValue);
            }
            case DOUBLE: {
                double doubleValue = reader.readDouble();
                intValue = (int)doubleValue;
                if (doubleValue == (double)intValue) break;
                throw NumberCodecHelper.invalidConversion(Integer.class, doubleValue);
            }
            case DECIMAL128: {
                Decimal128 decimal128 = reader.readDecimal128();
                intValue = decimal128.intValue();
                if (decimal128.equals(new Decimal128(intValue))) break;
                throw NumberCodecHelper.invalidConversion(Integer.class, decimal128);
            }
            default: {
                throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", new Object[]{bsonType}));
            }
        }
        return intValue;
    }

    static long decodeLong(BsonReader reader) {
        long longValue;
        BsonType bsonType = reader.getCurrentBsonType();
        switch (bsonType) {
            case INT32: {
                longValue = reader.readInt32();
                break;
            }
            case INT64: {
                longValue = reader.readInt64();
                break;
            }
            case DOUBLE: {
                double doubleValue = reader.readDouble();
                longValue = (long)doubleValue;
                if (doubleValue == (double)longValue) break;
                throw NumberCodecHelper.invalidConversion(Long.class, doubleValue);
            }
            case DECIMAL128: {
                Decimal128 decimal128 = reader.readDecimal128();
                longValue = decimal128.longValue();
                if (decimal128.equals(new Decimal128(longValue))) break;
                throw NumberCodecHelper.invalidConversion(Long.class, decimal128);
            }
            default: {
                throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", new Object[]{bsonType}));
            }
        }
        return longValue;
    }

    static double decodeDouble(BsonReader reader) {
        double doubleValue;
        BsonType bsonType = reader.getCurrentBsonType();
        switch (bsonType) {
            case INT32: {
                doubleValue = reader.readInt32();
                break;
            }
            case INT64: {
                long longValue = reader.readInt64();
                doubleValue = longValue;
                if (longValue == (long)doubleValue) break;
                throw NumberCodecHelper.invalidConversion(Double.class, longValue);
            }
            case DOUBLE: {
                doubleValue = reader.readDouble();
                break;
            }
            case DECIMAL128: {
                Decimal128 decimal128 = reader.readDecimal128();
                try {
                    doubleValue = decimal128.doubleValue();
                    if (!decimal128.equals(new Decimal128(new BigDecimal(doubleValue)))) {
                        throw NumberCodecHelper.invalidConversion(Double.class, decimal128);
                    }
                    break;
                }
                catch (NumberFormatException e) {
                    throw NumberCodecHelper.invalidConversion(Double.class, decimal128);
                }
            }
            default: {
                throw new BsonInvalidOperationException(String.format("Invalid numeric type, found: %s", new Object[]{bsonType}));
            }
        }
        return doubleValue;
    }

    private static <T extends Number> BsonInvalidOperationException invalidConversion(Class<T> clazz, Number value) {
        return new BsonInvalidOperationException(String.format("Could not convert `%s` to a %s without losing precision", value, clazz));
    }

    private NumberCodecHelper() {
    }
}

