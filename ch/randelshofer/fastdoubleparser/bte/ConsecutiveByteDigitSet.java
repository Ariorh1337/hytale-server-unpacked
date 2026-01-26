/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;

final class ConsecutiveByteDigitSet
implements ByteDigitSet {
    private final byte zeroDigit;

    public ConsecutiveByteDigitSet(char zeroDigit) {
        if (zeroDigit > '\u007f') {
            throw new IllegalArgumentException("can not map to a single byte. zeroDigit=" + zeroDigit + "' 0x" + Integer.toHexString(zeroDigit));
        }
        this.zeroDigit = (byte)zeroDigit;
    }

    @Override
    public int toDigit(byte ch) {
        return (char)(ch - this.zeroDigit);
    }
}

