/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.JavaFloatBitsFromByteArray;
import ch.randelshofer.fastdoubleparser.JavaFloatBitsFromCharArray;
import ch.randelshofer.fastdoubleparser.JavaFloatBitsFromCharSequence;

public final class JavaFloatParser {
    private static final JavaFloatBitsFromByteArray BYTE_ARRAY_PARSER = new JavaFloatBitsFromByteArray();
    private static final JavaFloatBitsFromCharArray CHAR_ARRAY_PARSER = new JavaFloatBitsFromCharArray();
    private static final JavaFloatBitsFromCharSequence CHAR_SEQUENCE_PARSER = new JavaFloatBitsFromCharSequence();

    private JavaFloatParser() {
    }

    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return JavaFloatParser.parseFloat(str, 0, str.length());
    }

    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_SEQUENCE_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Float.intBitsToFloat((int)bitPattern);
    }

    public static float parseFloat(byte[] str) throws NumberFormatException {
        return JavaFloatParser.parseFloat(str, 0, str.length);
    }

    public static float parseFloat(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = BYTE_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Float.intBitsToFloat((int)bitPattern);
    }

    public static float parseFloat(char[] str) throws NumberFormatException {
        return JavaFloatParser.parseFloat(str, 0, str.length);
    }

    public static float parseFloat(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        if (bitPattern == 9221120237041090561L) {
            throw new NumberFormatException("illegal syntax");
        }
        return Float.intBitsToFloat((int)bitPattern);
    }
}

