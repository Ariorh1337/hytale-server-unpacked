/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.JsonDoubleBitsFromByteArray;
import ch.randelshofer.fastdoubleparser.JsonDoubleBitsFromCharArray;
import ch.randelshofer.fastdoubleparser.JsonDoubleBitsFromCharSequence;

public final class JsonDoubleParser {
    private static final JsonDoubleBitsFromByteArray BYTE_ARRAY_PARSER = new JsonDoubleBitsFromByteArray();
    private static final JsonDoubleBitsFromCharArray CHAR_ARRAY_PARSER = new JsonDoubleBitsFromCharArray();
    private static final JsonDoubleBitsFromCharSequence CHAR_SEQUENCE_PARSER = new JsonDoubleBitsFromCharSequence();

    private JsonDoubleParser() {
    }

    public static double parseDouble(CharSequence str) throws NumberFormatException {
        return JsonDoubleParser.parseDouble(str, 0, str.length());
    }

    public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_SEQUENCE_PARSER.parseNumber(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Double.longBitsToDouble(bitPattern);
    }

    public static double parseDouble(byte[] str) throws NumberFormatException {
        return JsonDoubleParser.parseDouble(str, 0, str.length);
    }

    public static double parseDouble(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = BYTE_ARRAY_PARSER.parseNumber(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Double.longBitsToDouble(bitPattern);
    }

    public static double parseDouble(char[] str) throws NumberFormatException {
        return JsonDoubleParser.parseDouble(str, 0, str.length);
    }

    public static double parseDouble(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseNumber(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Double.longBitsToDouble(bitPattern);
    }
}

