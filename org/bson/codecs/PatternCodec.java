/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.BsonReader;
import org.bson.BsonRegularExpression;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class PatternCodec
implements Codec<Pattern> {
    private static final int GLOBAL_FLAG = 256;

    @Override
    public void encode(BsonWriter writer, Pattern value, EncoderContext encoderContext) {
        writer.writeRegularExpression(new BsonRegularExpression(value.pattern(), PatternCodec.getOptionsAsString(value)));
    }

    @Override
    public Pattern decode(BsonReader reader, DecoderContext decoderContext) {
        BsonRegularExpression regularExpression = reader.readRegularExpression();
        return Pattern.compile(regularExpression.getPattern(), PatternCodec.getOptionsAsInt(regularExpression));
    }

    @Override
    public Class<Pattern> getEncoderClass() {
        return Pattern.class;
    }

    private static String getOptionsAsString(Pattern pattern) {
        int flags = pattern.flags();
        StringBuilder buf = new StringBuilder();
        for (RegexFlag flag : RegexFlag.values()) {
            if ((pattern.flags() & flag.javaFlag) <= 0) continue;
            buf.append(flag.flagChar);
            flags -= flag.javaFlag;
        }
        if (flags > 0) {
            throw new IllegalArgumentException("some flags could not be recognized.");
        }
        return buf.toString();
    }

    private static int getOptionsAsInt(BsonRegularExpression regularExpression) {
        int optionsInt = 0;
        String optionsString = regularExpression.getOptions();
        if (optionsString == null || optionsString.length() == 0) {
            return optionsInt;
        }
        optionsString = optionsString.toLowerCase();
        for (int i = 0; i < optionsString.length(); ++i) {
            RegexFlag flag = RegexFlag.getByCharacter(optionsString.charAt(i));
            if (flag != null) {
                optionsInt |= flag.javaFlag;
                if (flag.unsupported == null) continue;
                continue;
            }
            throw new IllegalArgumentException("unrecognized flag [" + optionsString.charAt(i) + "] " + optionsString.charAt(i));
        }
        return optionsInt;
    }

    private static enum RegexFlag {
        CANON_EQ(128, 'c', "Pattern.CANON_EQ"),
        UNIX_LINES(1, 'd', "Pattern.UNIX_LINES"),
        GLOBAL(256, 'g', null),
        CASE_INSENSITIVE(2, 'i', null),
        MULTILINE(8, 'm', null),
        DOTALL(32, 's', "Pattern.DOTALL"),
        LITERAL(16, 't', "Pattern.LITERAL"),
        UNICODE_CASE(64, 'u', "Pattern.UNICODE_CASE"),
        COMMENTS(4, 'x', null);

        private static final Map<Character, RegexFlag> BY_CHARACTER;
        private final int javaFlag;
        private final char flagChar;
        private final String unsupported;

        public static RegexFlag getByCharacter(char ch) {
            return BY_CHARACTER.get(Character.valueOf(ch));
        }

        private RegexFlag(int f, char ch, String u) {
            this.javaFlag = f;
            this.flagChar = ch;
            this.unsupported = u;
        }

        static {
            BY_CHARACTER = new HashMap<Character, RegexFlag>();
            for (RegexFlag flag : RegexFlag.values()) {
                BY_CHARACTER.put(Character.valueOf(flag.flagChar), flag);
            }
        }
    }
}

