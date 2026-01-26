/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

final class Utf8Decoder {
    private Utf8Decoder() {
    }

    static Result decode(byte[] bytes, int offset, int length) {
        char[] chars = new char[length];
        boolean invalid = false;
        int charIndex = 0;
        int limit = offset + length;
        int i = offset;
        block7: while (i < limit) {
            byte b = bytes[i];
            int opcode = Integer.numberOfLeadingZeros(~b << 24);
            if (i + opcode > limit) {
                throw new NumberFormatException("UTF-8 code point is incomplete");
            }
            switch (opcode) {
                case 0: {
                    chars[charIndex++] = (char)b;
                    ++i;
                    continue block7;
                }
                case 1: {
                    invalid = true;
                    i = limit;
                    continue block7;
                }
                case 2: {
                    byte c1 = bytes[i + 1];
                    int value = (b & 0x1F) << 6 | c1 & 0x3F;
                    invalid |= value < 128 | (c1 & 0xC0) != 128;
                    chars[charIndex++] = (char)value;
                    i += 2;
                    continue block7;
                }
                case 3: {
                    byte c1 = bytes[i + 1];
                    byte c2 = bytes[i + 2];
                    int value = (b & 0xF) << 12 | (c1 & 0x3F) << 6 | c2 & 0x3F;
                    invalid |= value < 2048 | (c1 & c2 & 0xC0) != 128;
                    chars[charIndex++] = (char)value;
                    i += 3;
                    continue block7;
                }
                case 4: {
                    byte c1 = bytes[i + 1];
                    byte c2 = bytes[i + 2];
                    byte c3 = bytes[i + 2];
                    int value = (b & 7) << 18 | (c1 & 0x3F) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F;
                    chars[charIndex++] = (char)(0xD800 | value - 65536 >>> 10 & 0x3FF);
                    chars[charIndex++] = (char)(0xDC00 | value - 65536 & 0x3FF);
                    invalid |= value < 65536 | (c1 & c2 & c3 & 0xC0) != 128;
                    i += 4;
                    continue block7;
                }
            }
            invalid = true;
            i = limit;
        }
        if (invalid) {
            throw new NumberFormatException("invalid UTF-8 encoding");
        }
        return new Result(chars, charIndex);
    }

    static final class Result {
        private final char[] chars;
        private final int length;

        Result(char[] chars, int length) {
            this.chars = chars;
            this.length = length;
        }

        public char[] chars() {
            return this.chars;
        }

        public int length() {
            return this.length;
        }
    }
}

