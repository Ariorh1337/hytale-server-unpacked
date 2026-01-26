/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InlineMe;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Java8Compatibility;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.MessageReflection;
import com.google.protobuf.ProtobufToStringOutput;
import com.google.protobuf.TextFormatEscaper;
import com.google.protobuf.TextFormatParseInfoTree;
import com.google.protobuf.TextFormatParseLocation;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public final class TextFormat {
    private static final Logger logger = Logger.getLogger(TextFormat.class.getName());
    private static final String DEBUG_STRING_SILENT_MARKER = " \t ";
    private static final String ENABLE_INSERT_SILENT_MARKER_ENV_NAME = "SILENT_MARKER_INSERTION_ENABLED";
    private static final boolean ENABLE_INSERT_SILENT_MARKER = System.getenv().getOrDefault("SILENT_MARKER_INSERTION_ENABLED", "false").equals("true");
    private static final String REDACTED_MARKER = "[REDACTED]";
    private static final Parser PARSER = Parser.newBuilder().build();

    private TextFormat() {
    }

    @Deprecated
    public static String shortDebugString(MessageOrBuilder message) {
        return TextFormat.printer().emittingSingleLine(true).printToString(message, Printer.FieldReporterLevel.SHORT_DEBUG_STRING);
    }

    public static void printUnknownFieldValue(int tag, Object value, Appendable output) throws IOException {
        TextFormat.printUnknownFieldValue(tag, value, TextFormat.setSingleLineOutput(output, false), false);
    }

    private static void printUnknownFieldValue(int tag, Object value, TextGenerator generator, boolean redact) throws IOException {
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                generator.print(TextFormat.unsignedToString((Long)value));
                break;
            }
            case 5: {
                generator.print(String.format((Locale)null, "0x%08x", (Integer)value));
                break;
            }
            case 1: {
                generator.print(String.format((Locale)null, "0x%016x", (Long)value));
                break;
            }
            case 2: {
                try {
                    UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
                    generator.print("{");
                    generator.eol();
                    generator.indent();
                    Printer.printUnknownFields(message, generator, redact);
                    generator.outdent();
                    generator.print("}");
                }
                catch (InvalidProtocolBufferException e) {
                    generator.print("\"");
                    generator.print(TextFormat.escapeBytes((ByteString)value));
                    generator.print("\"");
                }
                break;
            }
            case 3: {
                Printer.printUnknownFields((UnknownFieldSet)value, generator, redact);
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad tag: " + tag);
            }
        }
    }

    public static Printer printer() {
        return Printer.DEFAULT_TEXT_FORMAT;
    }

    public static Printer debugFormatPrinter() {
        return Printer.DEFAULT_DEBUG_FORMAT;
    }

    public static Printer defaultFormatPrinter() {
        return Printer.DEFAULT_FORMAT;
    }

    @Deprecated
    @InlineMe(replacement="TextFormat.printer().print(message, output)", imports={"com.google.protobuf.TextFormat"})
    public static void print(MessageOrBuilder message, Appendable output) throws IOException {
        TextFormat.printer().print(message, output);
    }

    @Deprecated
    public static void printUnicode(MessageOrBuilder message, Appendable output) throws IOException {
        TextFormat.printer().escapingNonAscii(false).print(message, output, Printer.FieldReporterLevel.PRINT_UNICODE);
    }

    @Deprecated
    public static String printToString(MessageOrBuilder message) {
        return TextFormat.printer().printToString(message, Printer.FieldReporterLevel.TEXTFORMAT_PRINT_TO_STRING);
    }

    @Deprecated
    public static String printToUnicodeString(MessageOrBuilder message) {
        return TextFormat.printer().escapingNonAscii(false).printToString(message, Printer.FieldReporterLevel.PRINT_UNICODE);
    }

    @Deprecated
    @InlineMe(replacement="TextFormat.printer().printFieldValue(field, value, output)", imports={"com.google.protobuf.TextFormat"})
    public static void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
        TextFormat.printer().printFieldValue(field, value, output);
    }

    public static String unsignedToString(int value) {
        if (value >= 0) {
            return Integer.toString(value);
        }
        return Long.toString((long)value & 0xFFFFFFFFL);
    }

    public static String unsignedToString(long value) {
        if (value >= 0L) {
            return Long.toString(value);
        }
        return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
    }

    private static TextGenerator setSingleLineOutput(Appendable output, boolean singleLine) {
        return new TextGenerator(output, singleLine, null, Printer.FieldReporterLevel.TEXT_GENERATOR, false);
    }

    private static TextGenerator setSingleLineOutput(Appendable output, boolean singleLine, Descriptors.Descriptor rootMessageType, Printer.FieldReporterLevel fieldReporterLevel, boolean shouldEmitSilentMarker) {
        return new TextGenerator(output, singleLine, rootMessageType, fieldReporterLevel, shouldEmitSilentMarker);
    }

    public static Parser getParser() {
        return PARSER;
    }

    public static void merge(Readable input, Message.Builder builder) throws IOException {
        PARSER.merge(input, builder);
    }

    public static void merge(CharSequence input, Message.Builder builder) throws ParseException {
        PARSER.merge(input, builder);
    }

    public static <T extends Message> T parse(CharSequence input, Class<T> protoClass) throws ParseException {
        Message.Builder builder = ((Message)Internal.getDefaultInstance(protoClass)).newBuilderForType();
        TextFormat.merge(input, builder);
        Message output = builder.build();
        return (T)output;
    }

    public static void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
        PARSER.merge(input, extensionRegistry, builder);
    }

    public static void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
        PARSER.merge(input, extensionRegistry, builder);
    }

    public static <T extends Message> T parse(CharSequence input, ExtensionRegistry extensionRegistry, Class<T> protoClass) throws ParseException {
        Message.Builder builder = ((Message)Internal.getDefaultInstance(protoClass)).newBuilderForType();
        TextFormat.merge(input, extensionRegistry, builder);
        Message output = builder.build();
        return (T)output;
    }

    public static String escapeBytes(ByteString input) {
        return TextFormatEscaper.escapeBytes(input);
    }

    public static String escapeBytes(byte[] input) {
        return TextFormatEscaper.escapeBytes(input);
    }

    public static ByteString unescapeBytes(CharSequence charString) throws InvalidEscapeSequenceException {
        ByteString input = ByteString.copyFromUtf8(charString.toString());
        byte[] result = new byte[input.size()];
        int pos = 0;
        block16: for (int i = 0; i < input.size(); ++i) {
            byte c = input.byteAt(i);
            if (c == 92) {
                if (i + 1 < input.size()) {
                    int code;
                    if (TextFormat.isOctal(c = input.byteAt(++i))) {
                        code = TextFormat.digitValue(c);
                        if (i + 1 < input.size() && TextFormat.isOctal(input.byteAt(i + 1))) {
                            code = code * 8 + TextFormat.digitValue(input.byteAt(++i));
                        }
                        if (i + 1 < input.size() && TextFormat.isOctal(input.byteAt(i + 1))) {
                            code = code * 8 + TextFormat.digitValue(input.byteAt(++i));
                        }
                        result[pos++] = (byte)code;
                        continue;
                    }
                    switch (c) {
                        case 97: {
                            result[pos++] = 7;
                            continue block16;
                        }
                        case 98: {
                            result[pos++] = 8;
                            continue block16;
                        }
                        case 102: {
                            result[pos++] = 12;
                            continue block16;
                        }
                        case 110: {
                            result[pos++] = 10;
                            continue block16;
                        }
                        case 114: {
                            result[pos++] = 13;
                            continue block16;
                        }
                        case 116: {
                            result[pos++] = 9;
                            continue block16;
                        }
                        case 118: {
                            result[pos++] = 11;
                            continue block16;
                        }
                        case 92: {
                            result[pos++] = 92;
                            continue block16;
                        }
                        case 39: {
                            result[pos++] = 39;
                            continue block16;
                        }
                        case 34: {
                            result[pos++] = 34;
                            continue block16;
                        }
                        case 63: {
                            result[pos++] = 63;
                            continue block16;
                        }
                        case 120: {
                            code = 0;
                            if (i + 1 >= input.size() || !TextFormat.isHex(input.byteAt(i + 1))) {
                                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
                            }
                            code = TextFormat.digitValue(input.byteAt(++i));
                            if (i + 1 < input.size() && TextFormat.isHex(input.byteAt(i + 1))) {
                                code = code * 16 + TextFormat.digitValue(input.byteAt(++i));
                            }
                            result[pos++] = (byte)code;
                            continue block16;
                        }
                        case 117: {
                            if (++i + 3 < input.size() && TextFormat.isHex(input.byteAt(i)) && TextFormat.isHex(input.byteAt(i + 1)) && TextFormat.isHex(input.byteAt(i + 2)) && TextFormat.isHex(input.byteAt(i + 3))) {
                                char ch = (char)(TextFormat.digitValue(input.byteAt(i)) << 12 | TextFormat.digitValue(input.byteAt(i + 1)) << 8 | TextFormat.digitValue(input.byteAt(i + 2)) << 4 | TextFormat.digitValue(input.byteAt(i + 3)));
                                if (ch >= '\ud800' && ch <= '\udfff') {
                                    throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\u' refers to a surrogate");
                                }
                                byte[] chUtf8 = Character.toString(ch).getBytes(Internal.UTF_8);
                                System.arraycopy(chUtf8, 0, result, pos, chUtf8.length);
                                pos += chUtf8.length;
                                i += 3;
                                continue block16;
                            }
                            throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\u' with too few hex chars");
                        }
                        case 85: {
                            if (++i + 7 >= input.size()) {
                                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\U' with too few hex chars");
                            }
                            int codepoint = 0;
                            for (int offset = i; offset < i + 8; ++offset) {
                                byte b = input.byteAt(offset);
                                if (!TextFormat.isHex(b)) {
                                    throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\U' with too few hex chars");
                                }
                                codepoint = codepoint << 4 | TextFormat.digitValue(b);
                            }
                            if (!Character.isValidCodePoint(codepoint)) {
                                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\U" + input.substring(i, i + 8).toStringUtf8() + "' is not a valid code point value");
                            }
                            Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(codepoint);
                            if (unicodeBlock != null && (unicodeBlock.equals(Character.UnicodeBlock.LOW_SURROGATES) || unicodeBlock.equals(Character.UnicodeBlock.HIGH_SURROGATES) || unicodeBlock.equals(Character.UnicodeBlock.HIGH_PRIVATE_USE_SURROGATES))) {
                                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\U" + input.substring(i, i + 8).toStringUtf8() + "' refers to a surrogate code unit");
                            }
                            int[] codepoints = new int[]{codepoint};
                            byte[] chUtf8 = new String(codepoints, 0, 1).getBytes(Internal.UTF_8);
                            System.arraycopy(chUtf8, 0, result, pos, chUtf8.length);
                            pos += chUtf8.length;
                            i += 7;
                            continue block16;
                        }
                        default: {
                            throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\" + (char)c + '\'');
                        }
                    }
                }
                throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
            }
            result[pos++] = c;
        }
        return result.length == pos ? ByteString.wrap(result) : ByteString.copyFrom(result, 0, pos);
    }

    static String escapeText(String input) {
        return TextFormat.escapeBytes(ByteString.copyFromUtf8(input));
    }

    public static String escapeDoubleQuotesAndBackslashes(String input) {
        return TextFormatEscaper.escapeDoubleQuotesAndBackslashes(input);
    }

    static String unescapeText(String input) throws InvalidEscapeSequenceException {
        return TextFormat.unescapeBytes(input).toStringUtf8();
    }

    private static boolean isOctal(byte c) {
        return 48 <= c && c <= 55;
    }

    private static boolean isHex(byte c) {
        return 48 <= c && c <= 57 || 97 <= c && c <= 102 || 65 <= c && c <= 70;
    }

    private static int digitValue(byte c) {
        if (48 <= c && c <= 57) {
            return c - 48;
        }
        if (97 <= c && c <= 122) {
            return c - 97 + 10;
        }
        return c - 65 + 10;
    }

    static int parseInt32(String text) throws NumberFormatException {
        return (int)TextFormat.parseInteger(text, true, false);
    }

    static int parseUInt32(String text) throws NumberFormatException {
        return (int)TextFormat.parseInteger(text, false, false);
    }

    static long parseInt64(String text) throws NumberFormatException {
        return TextFormat.parseInteger(text, true, true);
    }

    static long parseUInt64(String text) throws NumberFormatException {
        return TextFormat.parseInteger(text, false, true);
    }

    private static long parseInteger(String text, boolean isSigned, boolean isLong) throws NumberFormatException {
        int pos = 0;
        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: " + text);
            }
            ++pos;
            negative = true;
        }
        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        } else if (text.startsWith("0", pos)) {
            radix = 8;
        }
        String numberText = text.substring(pos);
        long result = 0L;
        if (numberText.length() < 16) {
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }
            if (!isLong) {
                if (isSigned) {
                    if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                } else if (result >= 0x100000000L || result < 0L) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            }
        } else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                } else if (bigValue.bitLength() > 32) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            } else if (isSigned) {
                if (bigValue.bitLength() > 63) {
                    throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
                }
            } else if (bigValue.bitLength() > 64) {
                throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
            }
            result = bigValue.longValue();
        }
        return result;
    }

    static /* synthetic */ boolean access$400() {
        return ENABLE_INSERT_SILENT_MARKER;
    }

    private static final class TextGenerator {
        private final Appendable output;
        private final StringBuilder indent = new StringBuilder();
        private final boolean singleLineMode;
        private boolean shouldEmitSilentMarker;
        private boolean atStartOfLine = false;
        private final Printer.FieldReporterLevel fieldReporterLevel;
        private final Descriptors.Descriptor rootMessageType;

        private TextGenerator(Appendable output, boolean singleLineMode, Descriptors.Descriptor rootMessageType, Printer.FieldReporterLevel fieldReporterLevel, boolean shouldEmitSilentMarker) {
            this.output = output;
            this.singleLineMode = singleLineMode;
            this.rootMessageType = rootMessageType;
            this.fieldReporterLevel = fieldReporterLevel;
            this.shouldEmitSilentMarker = shouldEmitSilentMarker;
        }

        public void indent() {
            this.indent.append("  ");
        }

        public void outdent() {
            int length = this.indent.length();
            if (length == 0) {
                throw new IllegalArgumentException(" Outdent() without matching Indent().");
            }
            this.indent.setLength(length - 2);
        }

        public void print(CharSequence text) throws IOException {
            if (this.atStartOfLine) {
                this.atStartOfLine = false;
                this.output.append(this.singleLineMode ? " " : this.indent);
            }
            this.output.append(text);
        }

        public void eol() throws IOException {
            if (!this.singleLineMode) {
                this.output.append("\n");
            }
            this.atStartOfLine = true;
        }

        void maybePrintSilentMarker() throws IOException {
            if (this.shouldEmitSilentMarker) {
                this.output.append(TextFormat.DEBUG_STRING_SILENT_MARKER);
                this.shouldEmitSilentMarker = false;
            } else {
                this.output.append(" ");
            }
        }
    }

    public static final class Printer {
        private static final Printer DEFAULT_TEXT_FORMAT = new Printer(true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), false, false);
        private static final Printer DEFAULT_DEBUG_FORMAT = new Printer(true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), true, false);
        private static final Printer DEFAULT_FORMAT = new Printer(true, false, TypeRegistry.getEmptyTypeRegistry(), ExtensionRegistryLite.getEmptyRegistry(), false, false).setInsertSilentMarker(TextFormat.access$400());
        private final boolean escapeNonAscii;
        private final boolean useShortRepeatedPrimitives;
        private final TypeRegistry typeRegistry;
        private final ExtensionRegistryLite extensionRegistry;
        private final boolean enablingSafeDebugFormat;
        private final boolean singleLine;
        private boolean insertSilentMarker;
        private static final ThreadLocal<FieldReporterLevel> sensitiveFieldReportingLevel = new ThreadLocal<FieldReporterLevel>(){

            @Override
            protected FieldReporterLevel initialValue() {
                return FieldReporterLevel.ABSTRACT_TO_STRING;
            }
        };

        static Printer getOutputModePrinter() {
            if (ProtobufToStringOutput.isDefaultFormat()) {
                return TextFormat.defaultFormatPrinter();
            }
            if (ProtobufToStringOutput.shouldOutputDebugFormat()) {
                return TextFormat.debugFormatPrinter();
            }
            return TextFormat.printer();
        }

        @CanIgnoreReturnValue
        private Printer setInsertSilentMarker(boolean insertSilentMarker) {
            this.insertSilentMarker = insertSilentMarker;
            return this;
        }

        private Printer(boolean escapeNonAscii, boolean useShortRepeatedPrimitives, TypeRegistry typeRegistry, ExtensionRegistryLite extensionRegistry, boolean enablingSafeDebugFormat, boolean singleLine) {
            this.escapeNonAscii = escapeNonAscii;
            this.useShortRepeatedPrimitives = useShortRepeatedPrimitives;
            this.typeRegistry = typeRegistry;
            this.extensionRegistry = extensionRegistry;
            this.enablingSafeDebugFormat = enablingSafeDebugFormat;
            this.singleLine = singleLine;
            this.insertSilentMarker = false;
        }

        public Printer escapingNonAscii(boolean escapeNonAscii) {
            return new Printer(escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine);
        }

        public Printer usingTypeRegistry(TypeRegistry typeRegistry) {
            if (this.typeRegistry != TypeRegistry.getEmptyTypeRegistry()) {
                throw new IllegalArgumentException("Only one typeRegistry is allowed.");
            }
            return new Printer(this.escapeNonAscii, this.useShortRepeatedPrimitives, typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine);
        }

        public Printer usingExtensionRegistry(ExtensionRegistryLite extensionRegistry) {
            if (this.extensionRegistry != ExtensionRegistryLite.getEmptyRegistry()) {
                throw new IllegalArgumentException("Only one extensionRegistry is allowed.");
            }
            return new Printer(this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, extensionRegistry, this.enablingSafeDebugFormat, this.singleLine);
        }

        Printer enablingSafeDebugFormat(boolean enablingSafeDebugFormat) {
            return new Printer(this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, enablingSafeDebugFormat, this.singleLine);
        }

        public Printer usingShortRepeatedPrimitives(boolean useShortRepeatedPrimitives) {
            return new Printer(this.escapeNonAscii, useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, this.singleLine);
        }

        public Printer emittingSingleLine(boolean singleLine) {
            return new Printer(this.escapeNonAscii, this.useShortRepeatedPrimitives, this.typeRegistry, this.extensionRegistry, this.enablingSafeDebugFormat, singleLine);
        }

        void setSensitiveFieldReportingLevel(FieldReporterLevel level) {
            sensitiveFieldReportingLevel.set(level);
        }

        public void print(MessageOrBuilder message, Appendable output) throws IOException {
            this.print(message, output, FieldReporterLevel.PRINT);
        }

        void print(MessageOrBuilder message, Appendable output, FieldReporterLevel level) throws IOException {
            TextGenerator generator = TextFormat.setSingleLineOutput(output, this.singleLine, message.getDescriptorForType(), level, this.insertSilentMarker);
            this.print(message, generator);
        }

        public void print(UnknownFieldSet fields, Appendable output) throws IOException {
            Printer.printUnknownFields(fields, TextFormat.setSingleLineOutput(output, this.singleLine), this.enablingSafeDebugFormat);
        }

        private void print(MessageOrBuilder message, TextGenerator generator) throws IOException {
            if (message.getDescriptorForType().getFullName().equals("google.protobuf.Any") && this.printAny(message, generator)) {
                return;
            }
            this.printMessage(message, generator);
        }

        private void applyUnstablePrefix(Appendable output) {
            try {
                output.append("");
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private boolean printAny(MessageOrBuilder message, TextGenerator generator) throws IOException {
            Descriptors.Descriptor messageType = message.getDescriptorForType();
            Descriptors.FieldDescriptor typeUrlField = messageType.findFieldByNumber(1);
            Descriptors.FieldDescriptor valueField = messageType.findFieldByNumber(2);
            if (typeUrlField == null || typeUrlField.getType() != Descriptors.FieldDescriptor.Type.STRING || valueField == null || valueField.getType() != Descriptors.FieldDescriptor.Type.BYTES) {
                return false;
            }
            String typeUrl = (String)message.getField(typeUrlField);
            if (typeUrl.isEmpty()) {
                return false;
            }
            Object value = message.getField(valueField);
            DynamicMessage.Builder contentBuilder = null;
            try {
                Descriptors.Descriptor contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
                if (contentType == null) {
                    return false;
                }
                contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
                contentBuilder.mergeFrom((ByteString)value, this.extensionRegistry);
            }
            catch (InvalidProtocolBufferException e) {
                return false;
            }
            generator.print("[");
            generator.print(typeUrl);
            generator.print("]");
            generator.maybePrintSilentMarker();
            generator.print("{");
            generator.eol();
            generator.indent();
            this.print((MessageOrBuilder)contentBuilder, generator);
            generator.outdent();
            generator.print("}");
            generator.eol();
            return true;
        }

        public String printFieldToString(Descriptors.FieldDescriptor field, Object value) {
            try {
                StringBuilder text = new StringBuilder();
                if (this.enablingSafeDebugFormat) {
                    this.applyUnstablePrefix(text);
                }
                this.printField(field, value, text);
                return text.toString();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public void printField(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
            this.printField(field, value, TextFormat.setSingleLineOutput(output, this.singleLine));
        }

        private void printField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            if (field.isMapField()) {
                ArrayList<MapEntryAdapter> adapters = new ArrayList<MapEntryAdapter>();
                for (Object entry : (List)value) {
                    adapters.add(new MapEntryAdapter(entry, field));
                }
                Collections.sort(adapters);
                for (MapEntryAdapter adapter : adapters) {
                    this.printSingleField(field, adapter.getEntry(), generator);
                }
            } else if (field.isRepeated()) {
                if (this.useShortRepeatedPrimitives && field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    this.printShortRepeatedField(field, value, generator);
                } else {
                    for (Object element : (List)value) {
                        this.printSingleField(field, element, generator);
                    }
                }
            } else {
                this.printSingleField(field, value, generator);
            }
        }

        public void printFieldValue(Descriptors.FieldDescriptor field, Object value, Appendable output) throws IOException {
            this.printFieldValue(field, value, TextFormat.setSingleLineOutput(output, this.singleLine));
        }

        private void printFieldValue(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            if (this.shouldRedact(field, generator)) {
                generator.print(TextFormat.REDACTED_MARKER);
                if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    generator.eol();
                }
                return;
            }
            switch (field.getType()) {
                case INT32: 
                case SINT32: 
                case SFIXED32: {
                    generator.print(((Integer)value).toString());
                    break;
                }
                case INT64: 
                case SINT64: 
                case SFIXED64: {
                    generator.print(((Long)value).toString());
                    break;
                }
                case BOOL: {
                    generator.print(((Boolean)value).toString());
                    break;
                }
                case FLOAT: {
                    generator.print(((Float)value).toString());
                    break;
                }
                case DOUBLE: {
                    generator.print(((Double)value).toString());
                    break;
                }
                case UINT32: 
                case FIXED32: {
                    generator.print(TextFormat.unsignedToString((Integer)value));
                    break;
                }
                case UINT64: 
                case FIXED64: {
                    generator.print(TextFormat.unsignedToString((Long)value));
                    break;
                }
                case STRING: {
                    generator.print("\"");
                    generator.print(this.escapeNonAscii ? TextFormatEscaper.escapeText((String)value) : TextFormat.escapeDoubleQuotesAndBackslashes((String)value).replace("\n", "\\n"));
                    generator.print("\"");
                    break;
                }
                case BYTES: {
                    generator.print("\"");
                    if (value instanceof ByteString) {
                        generator.print(TextFormat.escapeBytes((ByteString)value));
                    } else {
                        generator.print(TextFormat.escapeBytes((byte[])value));
                    }
                    generator.print("\"");
                    break;
                }
                case ENUM: {
                    if (((Descriptors.EnumValueDescriptor)value).getIndex() == -1) {
                        generator.print(Integer.toString(((Descriptors.EnumValueDescriptor)value).getNumber()));
                        break;
                    }
                    generator.print(((Descriptors.EnumValueDescriptor)value).getName());
                    break;
                }
                case MESSAGE: 
                case GROUP: {
                    this.print((MessageOrBuilder)value, generator);
                }
            }
        }

        private boolean shouldRedact(Descriptors.FieldDescriptor field, TextGenerator generator) {
            Descriptors.FieldDescriptor.RedactionState state = field.getRedactionState();
            return this.enablingSafeDebugFormat && state.redact;
        }

        public String printToString(MessageOrBuilder message) {
            return this.printToString(message, FieldReporterLevel.PRINTER_PRINT_TO_STRING);
        }

        String printToString(MessageOrBuilder message, FieldReporterLevel level) {
            try {
                StringBuilder text = new StringBuilder();
                if (this.enablingSafeDebugFormat) {
                    this.applyUnstablePrefix(text);
                }
                this.print(message, text, level);
                return text.toString();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public String printToString(UnknownFieldSet fields) {
            try {
                StringBuilder text = new StringBuilder();
                if (this.enablingSafeDebugFormat) {
                    this.applyUnstablePrefix(text);
                }
                this.print(fields, (Appendable)text);
                return text.toString();
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Deprecated
        public String shortDebugString(MessageOrBuilder message) {
            return this.emittingSingleLine(true).printToString(message, FieldReporterLevel.SHORT_DEBUG_STRING);
        }

        @Deprecated
        @InlineMe(replacement="this.emittingSingleLine(true).printFieldToString(field, value)")
        public String shortDebugString(Descriptors.FieldDescriptor field, Object value) {
            return this.emittingSingleLine(true).printFieldToString(field, value);
        }

        @Deprecated
        @InlineMe(replacement="this.emittingSingleLine(true).printToString(fields)")
        public String shortDebugString(UnknownFieldSet fields) {
            return this.emittingSingleLine(true).printToString(fields);
        }

        private static void printUnknownFieldValue(int tag, Object value, TextGenerator generator, boolean redact) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    generator.print(redact ? String.format("UNKNOWN_VARINT %s", TextFormat.REDACTED_MARKER) : TextFormat.unsignedToString((Long)value));
                    break;
                }
                case 5: {
                    generator.print(redact ? String.format("UNKNOWN_FIXED32 %s", TextFormat.REDACTED_MARKER) : String.format((Locale)null, "0x%08x", (Integer)value));
                    break;
                }
                case 1: {
                    generator.print(redact ? String.format("UNKNOWN_FIXED64 %s", TextFormat.REDACTED_MARKER) : String.format((Locale)null, "0x%016x", (Long)value));
                    break;
                }
                case 2: {
                    try {
                        UnknownFieldSet message = UnknownFieldSet.parseFrom((ByteString)value);
                        generator.print("{");
                        generator.eol();
                        generator.indent();
                        Printer.printUnknownFields(message, generator, redact);
                        generator.outdent();
                        generator.print("}");
                    }
                    catch (InvalidProtocolBufferException e) {
                        if (redact) {
                            generator.print(String.format("UNKNOWN_STRING %s", TextFormat.REDACTED_MARKER));
                            break;
                        }
                        generator.print("\"");
                        generator.print(TextFormat.escapeBytes((ByteString)value));
                        generator.print("\"");
                    }
                    break;
                }
                case 3: {
                    Printer.printUnknownFields((UnknownFieldSet)value, generator, redact);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag: " + tag);
                }
            }
        }

        private void printMessage(MessageOrBuilder message, TextGenerator generator) throws IOException {
            for (Map.Entry<Descriptors.FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
                this.printField(field.getKey(), field.getValue(), generator);
            }
            Printer.printUnknownFields(message.getUnknownFields(), generator, this.enablingSafeDebugFormat);
        }

        private void printShortRepeatedField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            generator.print(field.getName());
            generator.print(": ");
            generator.print("[");
            String separator = "";
            for (Object element : (List)value) {
                generator.print(separator);
                this.printFieldValue(field, element, generator);
                separator = ", ";
            }
            generator.print("]");
            generator.eol();
        }

        private void printSingleField(Descriptors.FieldDescriptor field, Object value, TextGenerator generator) throws IOException {
            if (field.isExtension()) {
                generator.print("[");
                if (field.getContainingType().getOptions().getMessageSetWireFormat() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field.isOptional() && field.getExtensionScope() == field.getMessageType()) {
                    generator.print(field.getMessageType().getFullName());
                } else {
                    generator.print(field.getFullName());
                }
                generator.print("]");
            } else if (field.isGroupLike()) {
                generator.print(field.getMessageType().getName());
            } else {
                generator.print(field.getName());
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                generator.maybePrintSilentMarker();
                generator.print("{");
                generator.eol();
                generator.indent();
            } else {
                generator.print(":");
                generator.maybePrintSilentMarker();
            }
            this.printFieldValue(field, value, generator);
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                generator.outdent();
                generator.print("}");
            }
            generator.eol();
        }

        private static void printUnknownFields(UnknownFieldSet unknownFields, TextGenerator generator, boolean redact) throws IOException {
            if (unknownFields.isEmpty()) {
                return;
            }
            for (Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
                int number = entry.getKey();
                UnknownFieldSet.Field field = entry.getValue();
                Printer.printUnknownField(number, 0, field.getVarintList(), generator, redact);
                Printer.printUnknownField(number, 5, field.getFixed32List(), generator, redact);
                Printer.printUnknownField(number, 1, field.getFixed64List(), generator, redact);
                Printer.printUnknownField(number, 2, field.getLengthDelimitedList(), generator, redact);
                for (UnknownFieldSet value : field.getGroupList()) {
                    generator.print(entry.getKey().toString());
                    generator.maybePrintSilentMarker();
                    generator.print("{");
                    generator.eol();
                    generator.indent();
                    Printer.printUnknownFields(value, generator, redact);
                    generator.outdent();
                    generator.print("}");
                    generator.eol();
                }
            }
        }

        private static void printUnknownField(int number, int wireType, List<?> values, TextGenerator generator, boolean redact) throws IOException {
            for (Object value : values) {
                generator.print(String.valueOf(number));
                generator.print(":");
                generator.maybePrintSilentMarker();
                Printer.printUnknownFieldValue(wireType, value, generator, redact);
                generator.eol();
            }
        }

        static enum FieldReporterLevel {
            REPORT_ALL(0),
            TEXT_GENERATOR(1),
            PRINT(2),
            PRINTER_PRINT_TO_STRING(3),
            TEXTFORMAT_PRINT_TO_STRING(4),
            PRINT_UNICODE(5),
            SHORT_DEBUG_STRING(6),
            LEGACY_MULTILINE(7),
            LEGACY_SINGLE_LINE(8),
            DEBUG_MULTILINE(9),
            DEBUG_SINGLE_LINE(10),
            ABSTRACT_TO_STRING(11),
            ABSTRACT_BUILDER_TO_STRING(12),
            ABSTRACT_MUTABLE_TO_STRING(13),
            REPORT_NONE(14);

            private final int index;

            private FieldReporterLevel(int index) {
                this.index = index;
            }
        }

        static class MapEntryAdapter
        implements Comparable<MapEntryAdapter> {
            private Object entry;
            private Message messageEntry;
            private final Descriptors.FieldDescriptor keyField;

            MapEntryAdapter(Object entry, Descriptors.FieldDescriptor fieldDescriptor) {
                if (entry instanceof Message) {
                    this.messageEntry = (Message)entry;
                } else {
                    this.entry = entry;
                }
                this.keyField = fieldDescriptor.getMessageType().findFieldByName("key");
            }

            Object getKey() {
                if (this.messageEntry != null && this.keyField != null) {
                    return this.messageEntry.getField(this.keyField);
                }
                return null;
            }

            Object getEntry() {
                if (this.messageEntry != null) {
                    return this.messageEntry;
                }
                return this.entry;
            }

            @Override
            public int compareTo(MapEntryAdapter b) {
                Object aKey = this.getKey();
                Object bKey = b.getKey();
                if (aKey == null && bKey == null) {
                    return 0;
                }
                if (aKey == null) {
                    return -1;
                }
                if (bKey == null) {
                    return 1;
                }
                switch (this.keyField.getJavaType()) {
                    case BOOLEAN: {
                        return ((Boolean)aKey).compareTo((Boolean)bKey);
                    }
                    case LONG: {
                        return ((Long)aKey).compareTo((Long)bKey);
                    }
                    case INT: {
                        return ((Integer)aKey).compareTo((Integer)bKey);
                    }
                    case STRING: {
                        return ((String)aKey).compareTo((String)bKey);
                    }
                }
                return 0;
            }
        }
    }

    public static class Parser {
        private final TypeRegistry typeRegistry;
        private final boolean allowUnknownFields;
        private final boolean allowUnknownEnumValues;
        private final boolean allowUnknownExtensions;
        private final SingularOverwritePolicy singularOverwritePolicy;
        private TextFormatParseInfoTree.Builder parseInfoTreeBuilder;
        private final int recursionLimit;
        private static final int BUFFER_SIZE = 4096;

        private void detectSilentMarker(Tokenizer tokenizer, Descriptors.Descriptor immediateMessageType, String fieldName) {
        }

        private Parser(TypeRegistry typeRegistry, boolean allowUnknownFields, boolean allowUnknownEnumValues, boolean allowUnknownExtensions, SingularOverwritePolicy singularOverwritePolicy, TextFormatParseInfoTree.Builder parseInfoTreeBuilder, int recursionLimit) {
            this.typeRegistry = typeRegistry;
            this.allowUnknownFields = allowUnknownFields;
            this.allowUnknownEnumValues = allowUnknownEnumValues;
            this.allowUnknownExtensions = allowUnknownExtensions;
            this.singularOverwritePolicy = singularOverwritePolicy;
            this.parseInfoTreeBuilder = parseInfoTreeBuilder;
            this.recursionLimit = recursionLimit;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public void merge(Readable input, Message.Builder builder) throws IOException {
            this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
        }

        public void merge(CharSequence input, Message.Builder builder) throws ParseException {
            this.merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
        }

        public void merge(Readable input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws IOException {
            this.merge(Parser.toStringBuilder(input), extensionRegistry, builder);
        }

        private static StringBuilder toStringBuilder(Readable input) throws IOException {
            int n;
            StringBuilder text = new StringBuilder();
            CharBuffer buffer = CharBuffer.allocate(4096);
            while ((n = input.read(buffer)) != -1) {
                Java8Compatibility.flip(buffer);
                text.append(buffer, 0, n);
            }
            return text;
        }

        private void checkUnknownFields(List<UnknownField> unknownFields) throws ParseException {
            if (unknownFields.isEmpty()) {
                return;
            }
            StringBuilder msg = new StringBuilder("Input contains unknown fields and/or extensions:");
            for (UnknownField field : unknownFields) {
                msg.append('\n').append(field.message);
            }
            if (this.allowUnknownFields) {
                logger.warning(msg.toString());
                return;
            }
            int firstErrorIndex = 0;
            if (this.allowUnknownExtensions) {
                boolean allUnknownExtensions = true;
                for (UnknownField field : unknownFields) {
                    if (field.type == UnknownField.Type.FIELD) {
                        allUnknownExtensions = false;
                        break;
                    }
                    ++firstErrorIndex;
                }
                if (allUnknownExtensions) {
                    logger.warning(msg.toString());
                    return;
                }
            }
            String[] lineColumn = unknownFields.get((int)firstErrorIndex).message.split(":");
            throw new ParseException(Integer.parseInt(lineColumn[0]), Integer.parseInt(lineColumn[1]), msg.toString());
        }

        public void merge(CharSequence input, ExtensionRegistry extensionRegistry, Message.Builder builder) throws ParseException {
            Tokenizer tokenizer = new Tokenizer(input);
            MessageReflection.BuilderAdapter target = new MessageReflection.BuilderAdapter(builder);
            ArrayList<UnknownField> unknownFields = new ArrayList<UnknownField>();
            while (!tokenizer.atEnd()) {
                this.mergeField(tokenizer, extensionRegistry, target, unknownFields, this.recursionLimit);
            }
            this.checkUnknownFields(unknownFields);
        }

        private void mergeField(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, List<UnknownField> unknownFields, int recursionLimit) throws ParseException {
            this.mergeField(tokenizer, extensionRegistry, target, this.parseInfoTreeBuilder, unknownFields, recursionLimit);
        }

        private void mergeField(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields, int recursionLimit) throws ParseException {
            String name;
            Descriptors.FieldDescriptor field = null;
            int startLine = tokenizer.getLine();
            int startColumn = tokenizer.getColumn();
            Descriptors.Descriptor type = target.getDescriptorForType();
            ExtensionRegistry.ExtensionInfo extension = null;
            if ("google.protobuf.Any".equals(type.getFullName()) && tokenizer.tryConsume("[")) {
                if (recursionLimit < 1) {
                    throw tokenizer.parseException("Message is nested too deep");
                }
                this.mergeAnyFieldValue(tokenizer, extensionRegistry, target, parseTreeBuilder, unknownFields, type, recursionLimit - 1);
                return;
            }
            if (tokenizer.tryConsume("[")) {
                StringBuilder nameBuilder = new StringBuilder(tokenizer.consumeIdentifier());
                while (tokenizer.tryConsume(".")) {
                    nameBuilder.append('.');
                    nameBuilder.append(tokenizer.consumeIdentifier());
                }
                name = nameBuilder.toString();
                extension = target.findExtensionByName(extensionRegistry, name);
                if (extension == null) {
                    String message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + ".[" + name + "]";
                    unknownFields.add(new UnknownField(message, UnknownField.Type.EXTENSION));
                } else {
                    if (extension.descriptor.getContainingType() != type) {
                        throw tokenizer.parseExceptionPreviousToken("Extension \"" + name + "\" does not extend message type \"" + type.getFullName() + "\".");
                    }
                    field = extension.descriptor;
                }
                tokenizer.consume("]");
            } else {
                name = tokenizer.consumeIdentifier();
                field = type.findFieldByName(name);
                if (field == null) {
                    String lowerName = name.toLowerCase(Locale.US);
                    field = type.findFieldByName(lowerName);
                    if (field != null && !field.isGroupLike()) {
                        field = null;
                    }
                    if (field != null && !field.getMessageType().getName().equals(name)) {
                        field = null;
                    }
                }
                if (field == null) {
                    String message = tokenizer.getPreviousLine() + 1 + ":" + (tokenizer.getPreviousColumn() + 1) + ":\t" + type.getFullName() + "." + name;
                    unknownFields.add(new UnknownField(message, UnknownField.Type.FIELD));
                }
            }
            if (field == null) {
                this.detectSilentMarker(tokenizer, type, name);
                this.guessFieldTypeAndSkip(tokenizer, type, recursionLimit);
                return;
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                this.detectSilentMarker(tokenizer, type, field.getFullName());
                tokenizer.tryConsume(":");
                if (parseTreeBuilder != null) {
                    TextFormatParseInfoTree.Builder childParseTreeBuilder = parseTreeBuilder.getBuilderForSubMessageField(field);
                    this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, childParseTreeBuilder, unknownFields, recursionLimit);
                } else {
                    this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
                }
            } else {
                this.detectSilentMarker(tokenizer, type, field.getFullName());
                tokenizer.consume(":");
                this.consumeFieldValues(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
            }
            if (parseTreeBuilder != null) {
                parseTreeBuilder.setLocation(field, TextFormatParseLocation.create(startLine, startColumn));
            }
            if (!tokenizer.tryConsume(";")) {
                tokenizer.tryConsume(",");
            }
        }

        private String consumeFullTypeName(Tokenizer tokenizer) throws ParseException {
            if (!tokenizer.tryConsume("[")) {
                return tokenizer.consumeIdentifier();
            }
            String name = tokenizer.consumeIdentifier();
            while (tokenizer.tryConsume(".")) {
                name = name + "." + tokenizer.consumeIdentifier();
            }
            if (tokenizer.tryConsume("/")) {
                name = name + "/" + tokenizer.consumeIdentifier();
                while (tokenizer.tryConsume(".")) {
                    name = name + "." + tokenizer.consumeIdentifier();
                }
            }
            tokenizer.consume("]");
            return name;
        }

        private void consumeFieldValues(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields, int recursionLimit) throws ParseException {
            if (field.isRepeated() && tokenizer.tryConsume("[")) {
                if (!tokenizer.tryConsume("]")) {
                    while (true) {
                        this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
                        if (!tokenizer.tryConsume("]")) {
                            tokenizer.consume(",");
                            continue;
                        }
                        break;
                    }
                }
            } else {
                this.consumeFieldValue(tokenizer, extensionRegistry, target, field, extension, parseTreeBuilder, unknownFields, recursionLimit);
            }
        }

        private void consumeFieldValue(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, Descriptors.FieldDescriptor field, ExtensionRegistry.ExtensionInfo extension, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields, int recursionLimit) throws ParseException {
            if (this.singularOverwritePolicy == SingularOverwritePolicy.FORBID_SINGULAR_OVERWRITES && !field.isRepeated()) {
                if (target.hasField(field)) {
                    throw tokenizer.parseExceptionPreviousToken("Non-repeated field \"" + field.getFullName() + "\" cannot be overwritten.");
                }
                if (field.getContainingOneof() != null && target.hasOneof(field.getContainingOneof())) {
                    Descriptors.OneofDescriptor oneof = field.getContainingOneof();
                    throw tokenizer.parseExceptionPreviousToken("Field \"" + field.getFullName() + "\" is specified along with field \"" + target.getOneofFieldDescriptor(oneof).getFullName() + "\", another member of oneof \"" + oneof.getName() + "\".");
                }
            }
            Object value = null;
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                String endToken;
                if (recursionLimit < 1) {
                    throw tokenizer.parseException("Message is nested too deep");
                }
                if (tokenizer.tryConsume("<")) {
                    endToken = ">";
                } else {
                    tokenizer.consume("{");
                    endToken = "}";
                }
                Message defaultInstance = extension == null ? null : extension.defaultInstance;
                MessageReflection.MergeTarget subField = target.newMergeTargetForField(field, defaultInstance);
                while (!tokenizer.tryConsume(endToken)) {
                    if (tokenizer.atEnd()) {
                        throw tokenizer.parseException("Expected \"" + endToken + "\".");
                    }
                    this.mergeField(tokenizer, extensionRegistry, subField, parseTreeBuilder, unknownFields, recursionLimit - 1);
                }
                value = subField.finish();
            } else {
                switch (field.getType()) {
                    case INT32: 
                    case SINT32: 
                    case SFIXED32: {
                        value = tokenizer.consumeInt32();
                        break;
                    }
                    case INT64: 
                    case SINT64: 
                    case SFIXED64: {
                        value = tokenizer.consumeInt64();
                        break;
                    }
                    case UINT32: 
                    case FIXED32: {
                        value = tokenizer.consumeUInt32();
                        break;
                    }
                    case UINT64: 
                    case FIXED64: {
                        value = tokenizer.consumeUInt64();
                        break;
                    }
                    case FLOAT: {
                        value = Float.valueOf(tokenizer.consumeFloat());
                        break;
                    }
                    case DOUBLE: {
                        value = tokenizer.consumeDouble();
                        break;
                    }
                    case BOOL: {
                        value = tokenizer.consumeBoolean();
                        break;
                    }
                    case STRING: {
                        value = tokenizer.consumeString();
                        break;
                    }
                    case BYTES: {
                        value = tokenizer.consumeByteString();
                        break;
                    }
                    case ENUM: {
                        Descriptors.EnumDescriptor enumType = field.getEnumType();
                        if (tokenizer.lookingAtInteger()) {
                            int number = tokenizer.consumeInt32();
                            Object object = value = enumType.isClosed() ? enumType.findValueByNumber(number) : enumType.findValueByNumberCreatingIfUnknown(number);
                            if (value != null) break;
                            String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.';
                            if (this.allowUnknownEnumValues) {
                                logger.warning(unknownValueMsg);
                                return;
                            }
                            throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.');
                        }
                        String id = tokenizer.consumeIdentifier();
                        value = enumType.findValueByName(id);
                        if (value != null) break;
                        String unknownValueMsg = "Enum type \"" + enumType.getFullName() + "\" has no value named \"" + id + "\".";
                        if (this.allowUnknownEnumValues) {
                            logger.warning(unknownValueMsg);
                            return;
                        }
                        throw tokenizer.parseExceptionPreviousToken(unknownValueMsg);
                    }
                    case MESSAGE: 
                    case GROUP: {
                        throw new RuntimeException("Can't get here.");
                    }
                }
            }
            if (field.isRepeated()) {
                target.addRepeatedField(field, value);
            } else {
                target.setField(field, value);
            }
        }

        private void mergeAnyFieldValue(Tokenizer tokenizer, ExtensionRegistry extensionRegistry, MessageReflection.MergeTarget target, TextFormatParseInfoTree.Builder parseTreeBuilder, List<UnknownField> unknownFields, Descriptors.Descriptor anyDescriptor, int recursionLimit) throws ParseException {
            String anyEndToken;
            StringBuilder typeUrlBuilder;
            block8: {
                typeUrlBuilder = new StringBuilder();
                while (true) {
                    typeUrlBuilder.append(tokenizer.consumeIdentifier());
                    if (tokenizer.tryConsume("]")) break block8;
                    if (tokenizer.tryConsume("/")) {
                        typeUrlBuilder.append("/");
                        continue;
                    }
                    if (!tokenizer.tryConsume(".")) break;
                    typeUrlBuilder.append(".");
                }
                throw tokenizer.parseExceptionPreviousToken("Expected a valid type URL.");
            }
            this.detectSilentMarker(tokenizer, anyDescriptor, typeUrlBuilder.toString());
            tokenizer.tryConsume(":");
            if (tokenizer.tryConsume("<")) {
                anyEndToken = ">";
            } else {
                tokenizer.consume("{");
                anyEndToken = "}";
            }
            String typeUrl = typeUrlBuilder.toString();
            Descriptors.Descriptor contentType = null;
            try {
                contentType = this.typeRegistry.getDescriptorForTypeUrl(typeUrl);
            }
            catch (InvalidProtocolBufferException e) {
                throw tokenizer.parseException("Invalid valid type URL. Found: " + typeUrl);
            }
            if (contentType == null) {
                throw tokenizer.parseException("Unable to parse Any of type: " + typeUrl + ". Please make sure that the TypeRegistry contains the descriptors for the given types.");
            }
            DynamicMessage.Builder contentBuilder = DynamicMessage.getDefaultInstance(contentType).newBuilderForType();
            MessageReflection.BuilderAdapter contentTarget = new MessageReflection.BuilderAdapter(contentBuilder);
            while (!tokenizer.tryConsume(anyEndToken)) {
                this.mergeField(tokenizer, extensionRegistry, contentTarget, parseTreeBuilder, unknownFields, recursionLimit);
            }
            target.setField(anyDescriptor.findFieldByName("type_url"), typeUrlBuilder.toString());
            target.setField(anyDescriptor.findFieldByName("value"), contentBuilder.build().toByteString());
        }

        private void skipField(Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws ParseException {
            String name = this.consumeFullTypeName(tokenizer);
            this.detectSilentMarker(tokenizer, type, name);
            this.guessFieldTypeAndSkip(tokenizer, type, recursionLimit);
            if (!tokenizer.tryConsume(";")) {
                tokenizer.tryConsume(",");
            }
        }

        private void skipFieldMessage(Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws ParseException {
            String delimiter;
            if (tokenizer.tryConsume("<")) {
                delimiter = ">";
            } else {
                tokenizer.consume("{");
                delimiter = "}";
            }
            while (!tokenizer.lookingAt(">") && !tokenizer.lookingAt("}")) {
                this.skipField(tokenizer, type, recursionLimit);
            }
            tokenizer.consume(delimiter);
        }

        private void skipFieldValue(Tokenizer tokenizer) throws ParseException {
            if (!(tokenizer.tryConsumeByteString() || tokenizer.tryConsumeIdentifier() || tokenizer.tryConsumeInt64() || tokenizer.tryConsumeUInt64() || tokenizer.tryConsumeDouble() || tokenizer.tryConsumeFloat())) {
                throw tokenizer.parseException("Invalid field value: " + tokenizer.currentToken);
            }
        }

        private void guessFieldTypeAndSkip(Tokenizer tokenizer, Descriptors.Descriptor type, int recursionLimit) throws ParseException {
            boolean semicolonConsumed = tokenizer.tryConsume(":");
            if (tokenizer.lookingAt("[")) {
                this.skipFieldShortFormedRepeated(tokenizer, semicolonConsumed, type, recursionLimit);
            } else if (semicolonConsumed && !tokenizer.lookingAt("{") && !tokenizer.lookingAt("<")) {
                this.skipFieldValue(tokenizer);
            } else {
                if (recursionLimit < 1) {
                    throw tokenizer.parseException("Message is nested too deep");
                }
                this.skipFieldMessage(tokenizer, type, recursionLimit - 1);
            }
        }

        private void skipFieldShortFormedRepeated(Tokenizer tokenizer, boolean scalarAllowed, Descriptors.Descriptor type, int recursionLimit) throws ParseException {
            if (!tokenizer.tryConsume("[") || tokenizer.tryConsume("]")) {
                return;
            }
            while (true) {
                if (tokenizer.lookingAt("{") || tokenizer.lookingAt("<")) {
                    if (recursionLimit < 1) {
                        throw tokenizer.parseException("Message is nested too deep");
                    }
                    this.skipFieldMessage(tokenizer, type, recursionLimit - 1);
                } else if (scalarAllowed) {
                    this.skipFieldValue(tokenizer);
                } else {
                    throw tokenizer.parseException("Invalid repeated scalar field: missing \":\" before \"[\".");
                }
                if (tokenizer.tryConsume("]")) break;
                tokenizer.consume(",");
            }
        }

        public static enum SingularOverwritePolicy {
            ALLOW_SINGULAR_OVERWRITES,
            FORBID_SINGULAR_OVERWRITES;

        }

        public static class Builder {
            private boolean allowUnknownFields = false;
            private boolean allowUnknownEnumValues = false;
            private boolean allowUnknownExtensions = false;
            private SingularOverwritePolicy singularOverwritePolicy = SingularOverwritePolicy.ALLOW_SINGULAR_OVERWRITES;
            private TextFormatParseInfoTree.Builder parseInfoTreeBuilder = null;
            private TypeRegistry typeRegistry = TypeRegistry.getEmptyTypeRegistry();
            private int recursionLimit = 100;

            public Builder setTypeRegistry(TypeRegistry typeRegistry) {
                this.typeRegistry = typeRegistry;
                return this;
            }

            public Builder setAllowUnknownFields(boolean allowUnknownFields) {
                this.allowUnknownFields = allowUnknownFields;
                return this;
            }

            public Builder setAllowUnknownExtensions(boolean allowUnknownExtensions) {
                this.allowUnknownExtensions = allowUnknownExtensions;
                return this;
            }

            public Builder setSingularOverwritePolicy(SingularOverwritePolicy p) {
                this.singularOverwritePolicy = p;
                return this;
            }

            public Builder setParseInfoTreeBuilder(TextFormatParseInfoTree.Builder parseInfoTreeBuilder) {
                this.parseInfoTreeBuilder = parseInfoTreeBuilder;
                return this;
            }

            public Builder setRecursionLimit(int recursionLimit) {
                this.recursionLimit = recursionLimit;
                return this;
            }

            public Parser build() {
                return new Parser(this.typeRegistry, this.allowUnknownFields, this.allowUnknownEnumValues, this.allowUnknownExtensions, this.singularOverwritePolicy, this.parseInfoTreeBuilder, this.recursionLimit);
            }
        }

        static final class UnknownField {
            final String message;
            final Type type;

            UnknownField(String message, Type type) {
                this.message = message;
                this.type = type;
            }

            static enum Type {
                FIELD,
                EXTENSION;

            }
        }
    }

    public static class InvalidEscapeSequenceException
    extends IOException {
        private static final long serialVersionUID = -8164033650142593304L;

        InvalidEscapeSequenceException(String description) {
            super(description);
        }
    }

    @Deprecated
    public static class UnknownFieldParseException
    extends ParseException {
        private final String unknownField;

        public UnknownFieldParseException(String message) {
            this(-1, -1, "", message);
        }

        public UnknownFieldParseException(int line, int column, String unknownField, String message) {
            super(line, column, message);
            this.unknownField = unknownField;
        }

        public String getUnknownField() {
            return this.unknownField;
        }
    }

    public static class ParseException
    extends IOException {
        private static final long serialVersionUID = 3196188060225107702L;
        private final int line;
        private final int column;

        public ParseException(String message) {
            this(-1, -1, message);
        }

        public ParseException(int line, int column, String message) {
            super(Integer.toString(line) + ":" + column + ": " + message);
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return this.line;
        }

        public int getColumn() {
            return this.column;
        }
    }

    private static final class Tokenizer {
        private final CharSequence text;
        private String currentToken;
        private int pos = 0;
        private int line = 0;
        private int column = 0;
        private int lineInfoTrackingPos = 0;
        private int previousLine = 0;
        private int previousColumn = 0;
        private boolean containsSilentMarkerAfterCurrentToken = false;
        private boolean containsSilentMarkerAfterPrevToken = false;

        private Tokenizer(CharSequence text) {
            this.text = text;
            this.skipWhitespace();
            this.nextToken();
        }

        int getPreviousLine() {
            return this.previousLine;
        }

        int getPreviousColumn() {
            return this.previousColumn;
        }

        int getLine() {
            return this.line;
        }

        int getColumn() {
            return this.column;
        }

        boolean getContainsSilentMarkerAfterCurrentToken() {
            return this.containsSilentMarkerAfterCurrentToken;
        }

        boolean getContainsSilentMarkerAfterPrevToken() {
            return this.containsSilentMarkerAfterPrevToken;
        }

        boolean atEnd() {
            return this.currentToken.length() == 0;
        }

        void nextToken() {
            this.previousLine = this.line;
            this.previousColumn = this.column;
            while (this.lineInfoTrackingPos < this.pos) {
                if (this.text.charAt(this.lineInfoTrackingPos) == '\n') {
                    ++this.line;
                    this.column = 0;
                } else {
                    ++this.column;
                }
                ++this.lineInfoTrackingPos;
            }
            if (this.pos == this.text.length()) {
                this.currentToken = "";
            } else {
                this.currentToken = this.nextTokenInternal();
                this.skipWhitespace();
            }
        }

        private String nextTokenInternal() {
            int textLength = this.text.length();
            int startPos = this.pos;
            char startChar = this.text.charAt(startPos);
            int endPos = this.pos;
            if (Tokenizer.isAlphaUnder(startChar)) {
                char c;
                while (++endPos != textLength && (Tokenizer.isAlphaUnder(c = this.text.charAt(endPos)) || Tokenizer.isDigitPlusMinus(c))) {
                }
            } else if (Tokenizer.isDigitPlusMinus(startChar) || startChar == '.') {
                char c;
                if (startChar == '.') {
                    if (++endPos == textLength) {
                        return this.nextTokenSingleChar();
                    }
                    if (!Tokenizer.isDigitPlusMinus(this.text.charAt(endPos))) {
                        return this.nextTokenSingleChar();
                    }
                }
                while (++endPos != textLength && (Tokenizer.isDigitPlusMinus(c = this.text.charAt(endPos)) || Tokenizer.isAlphaUnder(c) || c == '.')) {
                }
            } else if (startChar == '\"' || startChar == '\'') {
                while (++endPos != textLength) {
                    char c = this.text.charAt(endPos);
                    if (c == startChar) {
                        ++endPos;
                    } else if (c != '\n' && (c != '\\' || ++endPos != textLength && this.text.charAt(endPos) != '\n')) continue;
                    break;
                }
            } else {
                return this.nextTokenSingleChar();
            }
            this.pos = endPos;
            return this.text.subSequence(startPos, endPos).toString();
        }

        private static boolean isAlphaUnder(char c) {
            return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || c == '_';
        }

        private static boolean isDigitPlusMinus(char c) {
            return '0' <= c && c <= '9' || c == '+' || c == '-';
        }

        private static boolean isWhitespace(char c) {
            return c == ' ' || c == '\f' || c == '\n' || c == '\r' || c == '\t';
        }

        private String nextTokenSingleChar() {
            char c = this.text.charAt(this.pos++);
            switch (c) {
                case ':': {
                    return ":";
                }
                case ',': {
                    return ",";
                }
                case '[': {
                    return "[";
                }
                case ']': {
                    return "]";
                }
                case '{': {
                    return "{";
                }
                case '}': {
                    return "}";
                }
                case '<': {
                    return "<";
                }
                case '>': {
                    return ">";
                }
            }
            return String.valueOf(c);
        }

        private void skipWhitespace() {
            int textLength = this.text.length();
            int startPos = this.pos;
            int endPos = this.pos - 1;
            while (++endPos != textLength) {
                char c = this.text.charAt(endPos);
                if (c == '#') {
                    while (++endPos != textLength && this.text.charAt(endPos) != '\n') {
                    }
                    if (endPos != textLength) continue;
                    break;
                }
                if (Tokenizer.isWhitespace(c)) continue;
            }
            this.pos = endPos;
        }

        boolean tryConsume(String token) {
            if (this.currentToken.equals(token)) {
                this.nextToken();
                return true;
            }
            return false;
        }

        void consume(String token) throws ParseException {
            if (!this.tryConsume(token)) {
                throw this.parseException("Expected \"" + token + "\".");
            }
        }

        boolean lookingAtInteger() {
            if (this.currentToken.length() == 0) {
                return false;
            }
            return Tokenizer.isDigitPlusMinus(this.currentToken.charAt(0));
        }

        boolean lookingAt(String text) {
            return this.currentToken.equals(text);
        }

        String consumeIdentifier() throws ParseException {
            for (int i = 0; i < this.currentToken.length(); ++i) {
                char c = this.currentToken.charAt(i);
                if (Tokenizer.isAlphaUnder(c) || '0' <= c && c <= '9' || c == '.') continue;
                throw this.parseException("Expected identifier. Found '" + this.currentToken + "'");
            }
            String result = this.currentToken;
            this.nextToken();
            return result;
        }

        boolean tryConsumeIdentifier() {
            try {
                this.consumeIdentifier();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        int consumeInt32() throws ParseException {
            try {
                int result = TextFormat.parseInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }

        int consumeUInt32() throws ParseException {
            try {
                int result = TextFormat.parseUInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }

        long consumeInt64() throws ParseException {
            try {
                long result = TextFormat.parseInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }

        boolean tryConsumeInt64() {
            try {
                this.consumeInt64();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        long consumeUInt64() throws ParseException {
            try {
                long result = TextFormat.parseUInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }

        public boolean tryConsumeUInt64() {
            try {
                this.consumeUInt64();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        public double consumeDouble() throws ParseException {
            switch (this.currentToken.toLowerCase(Locale.ROOT)) {
                case "-inf": 
                case "-infinity": {
                    this.nextToken();
                    return Double.NEGATIVE_INFINITY;
                }
                case "inf": 
                case "infinity": {
                    this.nextToken();
                    return Double.POSITIVE_INFINITY;
                }
                case "nan": {
                    this.nextToken();
                    return Double.NaN;
                }
            }
            try {
                double result = Double.parseDouble(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }

        public boolean tryConsumeDouble() {
            try {
                this.consumeDouble();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        public float consumeFloat() throws ParseException {
            switch (this.currentToken.toLowerCase(Locale.ROOT)) {
                case "-inf": 
                case "-inff": 
                case "-infinity": 
                case "-infinityf": {
                    this.nextToken();
                    return Float.NEGATIVE_INFINITY;
                }
                case "inf": 
                case "inff": 
                case "infinity": 
                case "infinityf": {
                    this.nextToken();
                    return Float.POSITIVE_INFINITY;
                }
                case "nan": 
                case "nanf": {
                    this.nextToken();
                    return Float.NaN;
                }
            }
            try {
                float result = Float.parseFloat(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }

        public boolean tryConsumeFloat() {
            try {
                this.consumeFloat();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        public boolean consumeBoolean() throws ParseException {
            if (this.currentToken.equals("true") || this.currentToken.equals("True") || this.currentToken.equals("t") || this.currentToken.equals("1")) {
                this.nextToken();
                return true;
            }
            if (this.currentToken.equals("false") || this.currentToken.equals("False") || this.currentToken.equals("f") || this.currentToken.equals("0")) {
                this.nextToken();
                return false;
            }
            throw this.parseException("Expected \"true\" or \"false\". Found \"" + this.currentToken + "\".");
        }

        public String consumeString() throws ParseException {
            return this.consumeByteString().toStringUtf8();
        }

        @CanIgnoreReturnValue
        ByteString consumeByteString() throws ParseException {
            ArrayList<ByteString> list = new ArrayList<ByteString>();
            this.consumeByteString(list);
            while (this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
                this.consumeByteString(list);
            }
            return ByteString.copyFrom(list);
        }

        boolean tryConsumeByteString() {
            try {
                this.consumeByteString();
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }

        private void consumeByteString(List<ByteString> list) throws ParseException {
            char quote;
            char c = quote = this.currentToken.length() > 0 ? this.currentToken.charAt(0) : (char)'\u0000';
            if (quote != '\"' && quote != '\'') {
                throw this.parseException("Expected string.");
            }
            if (this.currentToken.length() < 2 || this.currentToken.charAt(this.currentToken.length() - 1) != quote) {
                throw this.parseException("String missing ending quote.");
            }
            try {
                String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
                ByteString result = TextFormat.unescapeBytes(escaped);
                this.nextToken();
                list.add(result);
            }
            catch (InvalidEscapeSequenceException e) {
                throw this.parseException(e.getMessage());
            }
        }

        ParseException parseException(String description) {
            return new ParseException(this.line + 1, this.column + 1, description);
        }

        ParseException parseExceptionPreviousToken(String description) {
            return new ParseException(this.previousLine + 1, this.previousColumn + 1, description);
        }

        private ParseException integerParseException(NumberFormatException e) {
            return this.parseException("Couldn't parse integer: " + e.getMessage());
        }

        private ParseException floatParseException(NumberFormatException e) {
            return this.parseException("Couldn't parse number: " + e.getMessage());
        }
    }
}

