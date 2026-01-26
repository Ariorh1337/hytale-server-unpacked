/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import java.io.Reader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import org.bson.AbstractBsonReader;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonContextType;
import org.bson.BsonDbPointer;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReaderMark;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.internal.Base64;
import org.bson.json.DateTimeFormatter;
import org.bson.json.JsonParseException;
import org.bson.json.JsonScanner;
import org.bson.json.JsonToken;
import org.bson.json.JsonTokenType;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;

public class JsonReader
extends AbstractBsonReader {
    private final JsonScanner scanner;
    private JsonToken pushedToken;
    private Object currentValue;

    public JsonReader(String json) {
        this(new JsonScanner(json));
    }

    public JsonReader(Reader reader) {
        this(new JsonScanner(reader));
    }

    private JsonReader(JsonScanner scanner) {
        this.scanner = scanner;
        this.setContext(new Context(null, BsonContextType.TOP_LEVEL));
    }

    @Override
    protected BsonBinary doReadBinaryData() {
        return (BsonBinary)this.currentValue;
    }

    @Override
    protected byte doPeekBinarySubType() {
        return this.doReadBinaryData().getType();
    }

    @Override
    protected int doPeekBinarySize() {
        return this.doReadBinaryData().getData().length;
    }

    @Override
    protected boolean doReadBoolean() {
        return (Boolean)this.currentValue;
    }

    @Override
    public BsonType readBsonType() {
        JsonToken commaToken;
        if (this.isClosed()) {
            throw new IllegalStateException("This instance has been closed");
        }
        if (this.getState() == AbstractBsonReader.State.INITIAL || this.getState() == AbstractBsonReader.State.DONE || this.getState() == AbstractBsonReader.State.SCOPE_DOCUMENT) {
            this.setState(AbstractBsonReader.State.TYPE);
        }
        if (this.getState() != AbstractBsonReader.State.TYPE) {
            this.throwInvalidState("readBSONType", AbstractBsonReader.State.TYPE);
        }
        if (this.getContext().getContextType() == BsonContextType.DOCUMENT) {
            JsonToken nameToken = this.popToken();
            switch (nameToken.getType()) {
                case STRING: 
                case UNQUOTED_STRING: {
                    this.setCurrentName(nameToken.getValue(String.class));
                    break;
                }
                case END_OBJECT: {
                    this.setState(AbstractBsonReader.State.END_OF_DOCUMENT);
                    return BsonType.END_OF_DOCUMENT;
                }
                default: {
                    throw new JsonParseException("JSON reader was expecting a name but found '%s'.", nameToken.getValue());
                }
            }
            JsonToken colonToken = this.popToken();
            if (colonToken.getType() != JsonTokenType.COLON) {
                throw new JsonParseException("JSON reader was expecting ':' but found '%s'.", colonToken.getValue());
            }
        }
        JsonToken token = this.popToken();
        if (this.getContext().getContextType() == BsonContextType.ARRAY && token.getType() == JsonTokenType.END_ARRAY) {
            this.setState(AbstractBsonReader.State.END_OF_ARRAY);
            return BsonType.END_OF_DOCUMENT;
        }
        boolean noValueFound = false;
        switch (token.getType()) {
            case BEGIN_ARRAY: {
                this.setCurrentBsonType(BsonType.ARRAY);
                break;
            }
            case BEGIN_OBJECT: {
                this.visitExtendedJSON();
                break;
            }
            case DOUBLE: {
                this.setCurrentBsonType(BsonType.DOUBLE);
                this.currentValue = token.getValue();
                break;
            }
            case END_OF_FILE: {
                this.setCurrentBsonType(BsonType.END_OF_DOCUMENT);
                break;
            }
            case INT32: {
                this.setCurrentBsonType(BsonType.INT32);
                this.currentValue = token.getValue();
                break;
            }
            case INT64: {
                this.setCurrentBsonType(BsonType.INT64);
                this.currentValue = token.getValue();
                break;
            }
            case REGULAR_EXPRESSION: {
                this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                this.currentValue = token.getValue();
                break;
            }
            case STRING: {
                this.setCurrentBsonType(BsonType.STRING);
                this.currentValue = token.getValue();
                break;
            }
            case UNQUOTED_STRING: {
                String value = token.getValue(String.class);
                if ("false".equals(value) || "true".equals(value)) {
                    this.setCurrentBsonType(BsonType.BOOLEAN);
                    this.currentValue = Boolean.parseBoolean(value);
                    break;
                }
                if ("Infinity".equals(value)) {
                    this.setCurrentBsonType(BsonType.DOUBLE);
                    this.currentValue = Double.POSITIVE_INFINITY;
                    break;
                }
                if ("NaN".equals(value)) {
                    this.setCurrentBsonType(BsonType.DOUBLE);
                    this.currentValue = Double.NaN;
                    break;
                }
                if ("null".equals(value)) {
                    this.setCurrentBsonType(BsonType.NULL);
                    break;
                }
                if ("undefined".equals(value)) {
                    this.setCurrentBsonType(BsonType.UNDEFINED);
                    break;
                }
                if ("MinKey".equals(value)) {
                    this.visitEmptyConstructor();
                    this.setCurrentBsonType(BsonType.MIN_KEY);
                    this.currentValue = new MinKey();
                    break;
                }
                if ("MaxKey".equals(value)) {
                    this.visitEmptyConstructor();
                    this.setCurrentBsonType(BsonType.MAX_KEY);
                    this.currentValue = new MaxKey();
                    break;
                }
                if ("BinData".equals(value)) {
                    this.setCurrentBsonType(BsonType.BINARY);
                    this.currentValue = this.visitBinDataConstructor();
                    break;
                }
                if ("Date".equals(value)) {
                    this.currentValue = this.visitDateTimeConstructorWithOutNew();
                    this.setCurrentBsonType(BsonType.STRING);
                    break;
                }
                if ("HexData".equals(value)) {
                    this.setCurrentBsonType(BsonType.BINARY);
                    this.currentValue = this.visitHexDataConstructor();
                    break;
                }
                if ("ISODate".equals(value)) {
                    this.setCurrentBsonType(BsonType.DATE_TIME);
                    this.currentValue = this.visitISODateTimeConstructor();
                    break;
                }
                if ("NumberInt".equals(value)) {
                    this.setCurrentBsonType(BsonType.INT32);
                    this.currentValue = this.visitNumberIntConstructor();
                    break;
                }
                if ("NumberLong".equals(value)) {
                    this.setCurrentBsonType(BsonType.INT64);
                    this.currentValue = this.visitNumberLongConstructor();
                    break;
                }
                if ("NumberDecimal".equals(value)) {
                    this.setCurrentBsonType(BsonType.DECIMAL128);
                    this.currentValue = this.visitNumberDecimalConstructor();
                    break;
                }
                if ("ObjectId".equals(value)) {
                    this.setCurrentBsonType(BsonType.OBJECT_ID);
                    this.currentValue = this.visitObjectIdConstructor();
                    break;
                }
                if ("Timestamp".equals(value)) {
                    this.setCurrentBsonType(BsonType.TIMESTAMP);
                    this.currentValue = this.visitTimestampConstructor();
                    break;
                }
                if ("RegExp".equals(value)) {
                    this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                    this.currentValue = this.visitRegularExpressionConstructor();
                    break;
                }
                if ("DBPointer".equals(value)) {
                    this.setCurrentBsonType(BsonType.DB_POINTER);
                    this.currentValue = this.visitDBPointerConstructor();
                    break;
                }
                if ("UUID".equals(value)) {
                    this.setCurrentBsonType(BsonType.BINARY);
                    this.currentValue = this.visitUUIDConstructor();
                    break;
                }
                if ("new".equals(value)) {
                    this.visitNew();
                    break;
                }
                noValueFound = true;
                break;
            }
            default: {
                noValueFound = true;
            }
        }
        if (noValueFound) {
            throw new JsonParseException("JSON reader was expecting a value but found '%s'.", token.getValue());
        }
        if ((this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) && (commaToken = this.popToken()).getType() != JsonTokenType.COMMA) {
            this.pushToken(commaToken);
        }
        switch (this.getContext().getContextType()) {
            default: {
                this.setState(AbstractBsonReader.State.NAME);
                break;
            }
            case ARRAY: 
            case JAVASCRIPT_WITH_SCOPE: 
            case TOP_LEVEL: {
                this.setState(AbstractBsonReader.State.VALUE);
            }
        }
        return this.getCurrentBsonType();
    }

    @Override
    public Decimal128 doReadDecimal128() {
        return (Decimal128)this.currentValue;
    }

    @Override
    protected long doReadDateTime() {
        return (Long)this.currentValue;
    }

    @Override
    protected double doReadDouble() {
        return (Double)this.currentValue;
    }

    @Override
    protected void doReadEndArray() {
        JsonToken commaToken;
        this.setContext(this.getContext().getParentContext());
        if ((this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) && (commaToken = this.popToken()).getType() != JsonTokenType.COMMA) {
            this.pushToken(commaToken);
        }
    }

    @Override
    protected void doReadEndDocument() {
        JsonToken commaToken;
        this.setContext(this.getContext().getParentContext());
        if (this.getContext() != null && this.getContext().getContextType() == BsonContextType.SCOPE_DOCUMENT) {
            this.setContext(this.getContext().getParentContext());
            this.verifyToken(JsonTokenType.END_OBJECT);
        }
        if (this.getContext() == null) {
            throw new JsonParseException("Unexpected end of document.");
        }
        if ((this.getContext().getContextType() == BsonContextType.ARRAY || this.getContext().getContextType() == BsonContextType.DOCUMENT) && (commaToken = this.popToken()).getType() != JsonTokenType.COMMA) {
            this.pushToken(commaToken);
        }
    }

    @Override
    protected int doReadInt32() {
        return (Integer)this.currentValue;
    }

    @Override
    protected long doReadInt64() {
        return (Long)this.currentValue;
    }

    @Override
    protected String doReadJavaScript() {
        return (String)this.currentValue;
    }

    @Override
    protected String doReadJavaScriptWithScope() {
        return (String)this.currentValue;
    }

    @Override
    protected void doReadMaxKey() {
    }

    @Override
    protected void doReadMinKey() {
    }

    @Override
    protected void doReadNull() {
    }

    @Override
    protected ObjectId doReadObjectId() {
        return (ObjectId)this.currentValue;
    }

    @Override
    protected BsonRegularExpression doReadRegularExpression() {
        return (BsonRegularExpression)this.currentValue;
    }

    @Override
    protected BsonDbPointer doReadDBPointer() {
        return (BsonDbPointer)this.currentValue;
    }

    @Override
    protected void doReadStartArray() {
        this.setContext(new Context((AbstractBsonReader.Context)this.getContext(), BsonContextType.ARRAY));
    }

    @Override
    protected void doReadStartDocument() {
        this.setContext(new Context((AbstractBsonReader.Context)this.getContext(), BsonContextType.DOCUMENT));
    }

    @Override
    protected String doReadString() {
        return (String)this.currentValue;
    }

    @Override
    protected String doReadSymbol() {
        return (String)this.currentValue;
    }

    @Override
    protected BsonTimestamp doReadTimestamp() {
        return (BsonTimestamp)this.currentValue;
    }

    @Override
    protected void doReadUndefined() {
    }

    @Override
    protected void doSkipName() {
    }

    @Override
    protected void doSkipValue() {
        switch (this.getCurrentBsonType()) {
            case ARRAY: {
                this.readStartArray();
                while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    this.skipValue();
                }
                this.readEndArray();
                break;
            }
            case BINARY: {
                this.readBinaryData();
                break;
            }
            case BOOLEAN: {
                this.readBoolean();
                break;
            }
            case DATE_TIME: {
                this.readDateTime();
                break;
            }
            case DOCUMENT: {
                this.readStartDocument();
                while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    this.skipName();
                    this.skipValue();
                }
                this.readEndDocument();
                break;
            }
            case DOUBLE: {
                this.readDouble();
                break;
            }
            case INT32: {
                this.readInt32();
                break;
            }
            case INT64: {
                this.readInt64();
                break;
            }
            case DECIMAL128: {
                this.readDecimal128();
                break;
            }
            case JAVASCRIPT: {
                this.readJavaScript();
                break;
            }
            case JAVASCRIPT_WITH_SCOPE: {
                this.readJavaScriptWithScope();
                this.readStartDocument();
                while (this.readBsonType() != BsonType.END_OF_DOCUMENT) {
                    this.skipName();
                    this.skipValue();
                }
                this.readEndDocument();
                break;
            }
            case MAX_KEY: {
                this.readMaxKey();
                break;
            }
            case MIN_KEY: {
                this.readMinKey();
                break;
            }
            case NULL: {
                this.readNull();
                break;
            }
            case OBJECT_ID: {
                this.readObjectId();
                break;
            }
            case REGULAR_EXPRESSION: {
                this.readRegularExpression();
                break;
            }
            case STRING: {
                this.readString();
                break;
            }
            case SYMBOL: {
                this.readSymbol();
                break;
            }
            case TIMESTAMP: {
                this.readTimestamp();
                break;
            }
            case UNDEFINED: {
                this.readUndefined();
                break;
            }
        }
    }

    private JsonToken popToken() {
        if (this.pushedToken != null) {
            JsonToken token = this.pushedToken;
            this.pushedToken = null;
            return token;
        }
        return this.scanner.nextToken();
    }

    private void pushToken(JsonToken token) {
        if (this.pushedToken != null) {
            throw new BsonInvalidOperationException("There is already a pending token.");
        }
        this.pushedToken = token;
    }

    private void verifyToken(JsonTokenType expectedType) {
        JsonToken token = this.popToken();
        if (expectedType != token.getType()) {
            throw new JsonParseException("JSON reader expected token type '%s' but found '%s'.", new Object[]{expectedType, token.getValue()});
        }
    }

    private void verifyToken(JsonTokenType expectedType, Object expectedValue) {
        JsonToken token = this.popToken();
        if (expectedType != token.getType()) {
            throw new JsonParseException("JSON reader expected token type '%s' but found '%s'.", new Object[]{expectedType, token.getValue()});
        }
        if (!expectedValue.equals(token.getValue())) {
            throw new JsonParseException("JSON reader expected '%s' but found '%s'.", expectedValue, token.getValue());
        }
    }

    private void verifyString(String expected) {
        if (expected == null) {
            throw new IllegalArgumentException("Can't be null");
        }
        JsonToken token = this.popToken();
        JsonTokenType type = token.getType();
        if (type != JsonTokenType.STRING && type != JsonTokenType.UNQUOTED_STRING || !expected.equals(token.getValue())) {
            throw new JsonParseException("JSON reader expected '%s' but found '%s'.", expected, token.getValue());
        }
    }

    private void visitNew() {
        JsonToken typeToken = this.popToken();
        if (typeToken.getType() != JsonTokenType.UNQUOTED_STRING) {
            throw new JsonParseException("JSON reader expected a type name but found '%s'.", typeToken.getValue());
        }
        String value = typeToken.getValue(String.class);
        if ("MinKey".equals(value)) {
            this.visitEmptyConstructor();
            this.setCurrentBsonType(BsonType.MIN_KEY);
            this.currentValue = new MinKey();
        } else if ("MaxKey".equals(value)) {
            this.visitEmptyConstructor();
            this.setCurrentBsonType(BsonType.MAX_KEY);
            this.currentValue = new MaxKey();
        } else if ("BinData".equals(value)) {
            this.currentValue = this.visitBinDataConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
        } else if ("Date".equals(value)) {
            this.currentValue = this.visitDateTimeConstructor();
            this.setCurrentBsonType(BsonType.DATE_TIME);
        } else if ("HexData".equals(value)) {
            this.currentValue = this.visitHexDataConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
        } else if ("ISODate".equals(value)) {
            this.currentValue = this.visitISODateTimeConstructor();
            this.setCurrentBsonType(BsonType.DATE_TIME);
        } else if ("NumberInt".equals(value)) {
            this.currentValue = this.visitNumberIntConstructor();
            this.setCurrentBsonType(BsonType.INT32);
        } else if ("NumberLong".equals(value)) {
            this.currentValue = this.visitNumberLongConstructor();
            this.setCurrentBsonType(BsonType.INT64);
        } else if ("NumberDecimal".equals(value)) {
            this.currentValue = this.visitNumberDecimalConstructor();
            this.setCurrentBsonType(BsonType.DECIMAL128);
        } else if ("ObjectId".equals(value)) {
            this.currentValue = this.visitObjectIdConstructor();
            this.setCurrentBsonType(BsonType.OBJECT_ID);
        } else if ("RegExp".equals(value)) {
            this.currentValue = this.visitRegularExpressionConstructor();
            this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
        } else if ("DBPointer".equals(value)) {
            this.currentValue = this.visitDBPointerConstructor();
            this.setCurrentBsonType(BsonType.DB_POINTER);
        } else if ("UUID".equals(value)) {
            this.currentValue = this.visitUUIDConstructor();
            this.setCurrentBsonType(BsonType.BINARY);
        } else {
            throw new JsonParseException("JSON reader expected a type name but found '%s'.", value);
        }
    }

    private void visitExtendedJSON() {
        JsonToken nameToken = this.popToken();
        String value = nameToken.getValue(String.class);
        JsonTokenType type = nameToken.getType();
        if (type == JsonTokenType.STRING || type == JsonTokenType.UNQUOTED_STRING) {
            if ("$binary".equals(value) || "$type".equals(value)) {
                this.currentValue = this.visitBinDataExtendedJson(value);
                if (this.currentValue != null) {
                    this.setCurrentBsonType(BsonType.BINARY);
                    return;
                }
            }
            if ("$uuid".equals(value)) {
                this.currentValue = this.visitUuidExtendedJson();
                this.setCurrentBsonType(BsonType.BINARY);
                return;
            }
            if ("$regex".equals(value) || "$options".equals(value)) {
                this.currentValue = this.visitRegularExpressionExtendedJson(value);
                if (this.currentValue != null) {
                    this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                    return;
                }
            } else {
                if ("$code".equals(value)) {
                    this.visitJavaScriptExtendedJson();
                    return;
                }
                if ("$date".equals(value)) {
                    this.currentValue = this.visitDateTimeExtendedJson();
                    this.setCurrentBsonType(BsonType.DATE_TIME);
                    return;
                }
                if ("$maxKey".equals(value)) {
                    this.currentValue = this.visitMaxKeyExtendedJson();
                    this.setCurrentBsonType(BsonType.MAX_KEY);
                    return;
                }
                if ("$minKey".equals(value)) {
                    this.currentValue = this.visitMinKeyExtendedJson();
                    this.setCurrentBsonType(BsonType.MIN_KEY);
                    return;
                }
                if ("$oid".equals(value)) {
                    this.currentValue = this.visitObjectIdExtendedJson();
                    this.setCurrentBsonType(BsonType.OBJECT_ID);
                    return;
                }
                if ("$regularExpression".equals(value)) {
                    this.currentValue = this.visitNewRegularExpressionExtendedJson();
                    this.setCurrentBsonType(BsonType.REGULAR_EXPRESSION);
                    return;
                }
                if ("$symbol".equals(value)) {
                    this.currentValue = this.visitSymbolExtendedJson();
                    this.setCurrentBsonType(BsonType.SYMBOL);
                    return;
                }
                if ("$timestamp".equals(value)) {
                    this.currentValue = this.visitTimestampExtendedJson();
                    this.setCurrentBsonType(BsonType.TIMESTAMP);
                    return;
                }
                if ("$undefined".equals(value)) {
                    this.currentValue = this.visitUndefinedExtendedJson();
                    this.setCurrentBsonType(BsonType.UNDEFINED);
                    return;
                }
                if ("$numberLong".equals(value)) {
                    this.currentValue = this.visitNumberLongExtendedJson();
                    this.setCurrentBsonType(BsonType.INT64);
                    return;
                }
                if ("$numberInt".equals(value)) {
                    this.currentValue = this.visitNumberIntExtendedJson();
                    this.setCurrentBsonType(BsonType.INT32);
                    return;
                }
                if ("$numberDouble".equals(value)) {
                    this.currentValue = this.visitNumberDoubleExtendedJson();
                    this.setCurrentBsonType(BsonType.DOUBLE);
                    return;
                }
                if ("$numberDecimal".equals(value)) {
                    this.currentValue = this.visitNumberDecimalExtendedJson();
                    this.setCurrentBsonType(BsonType.DECIMAL128);
                    return;
                }
                if ("$dbPointer".equals(value)) {
                    this.currentValue = this.visitDbPointerExtendedJson();
                    this.setCurrentBsonType(BsonType.DB_POINTER);
                    return;
                }
            }
        }
        this.pushToken(nameToken);
        this.setCurrentBsonType(BsonType.DOCUMENT);
    }

    private void visitEmptyConstructor() {
        JsonToken nextToken = this.popToken();
        if (nextToken.getType() == JsonTokenType.LEFT_PAREN) {
            this.verifyToken(JsonTokenType.RIGHT_PAREN);
        } else {
            this.pushToken(nextToken);
        }
    }

    private BsonBinary visitBinDataConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken subTypeToken = this.popToken();
        if (subTypeToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected a binary subtype but found '%s'.", subTypeToken.getValue());
        }
        this.verifyToken(JsonTokenType.COMMA);
        JsonToken bytesToken = this.popToken();
        if (bytesToken.getType() != JsonTokenType.UNQUOTED_STRING && bytesToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected a string but found '%s'.", bytesToken.getValue());
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        byte[] bytes = Base64.decode(bytesToken.getValue(String.class));
        return new BsonBinary(subTypeToken.getValue(Integer.class).byteValue(), bytes);
    }

    private BsonBinary visitUUIDConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        String hexString = this.readStringFromExtendedJson().replace("-", "");
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return new BsonBinary(BsonBinarySubType.UUID_STANDARD, JsonReader.decodeHex(hexString));
    }

    private BsonRegularExpression visitRegularExpressionConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        String pattern = this.readStringFromExtendedJson();
        String options = "";
        JsonToken commaToken = this.popToken();
        if (commaToken.getType() == JsonTokenType.COMMA) {
            options = this.readStringFromExtendedJson();
        } else {
            this.pushToken(commaToken);
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return new BsonRegularExpression(pattern, options);
    }

    private ObjectId visitObjectIdConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        ObjectId objectId = new ObjectId(this.readStringFromExtendedJson());
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return objectId;
    }

    private BsonTimestamp visitTimestampConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken timeToken = this.popToken();
        if (timeToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected an integer but found '%s'.", timeToken.getValue());
        }
        int time = timeToken.getValue(Integer.class);
        this.verifyToken(JsonTokenType.COMMA);
        JsonToken incrementToken = this.popToken();
        if (incrementToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected an integer but found '%s'.", timeToken.getValue());
        }
        int increment = incrementToken.getValue(Integer.class);
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return new BsonTimestamp(time, increment);
    }

    private BsonDbPointer visitDBPointerConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        String namespace = this.readStringFromExtendedJson();
        this.verifyToken(JsonTokenType.COMMA);
        ObjectId id = new ObjectId(this.readStringFromExtendedJson());
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return new BsonDbPointer(namespace, id);
    }

    private int visitNumberIntConstructor() {
        int value;
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken valueToken = this.popToken();
        if (valueToken.getType() == JsonTokenType.INT32) {
            value = valueToken.getValue(Integer.class);
        } else if (valueToken.getType() == JsonTokenType.STRING) {
            value = Integer.parseInt(valueToken.getValue(String.class));
        } else {
            throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", valueToken.getValue());
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return value;
    }

    private long visitNumberLongConstructor() {
        long value;
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken valueToken = this.popToken();
        if (valueToken.getType() == JsonTokenType.INT32 || valueToken.getType() == JsonTokenType.INT64) {
            value = valueToken.getValue(Long.class);
        } else if (valueToken.getType() == JsonTokenType.STRING) {
            value = Long.parseLong(valueToken.getValue(String.class));
        } else {
            throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", valueToken.getValue());
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return value;
    }

    private Decimal128 visitNumberDecimalConstructor() {
        Decimal128 value;
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken valueToken = this.popToken();
        if (valueToken.getType() == JsonTokenType.INT32 || valueToken.getType() == JsonTokenType.INT64 || valueToken.getType() == JsonTokenType.DOUBLE) {
            value = valueToken.getValue(Decimal128.class);
        } else if (valueToken.getType() == JsonTokenType.STRING) {
            value = Decimal128.parse(valueToken.getValue(String.class));
        } else {
            throw new JsonParseException("JSON reader expected a number or a string but found '%s'.", valueToken.getValue());
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        return value;
    }

    private long visitISODateTimeConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken token = this.popToken();
        if (token.getType() == JsonTokenType.RIGHT_PAREN) {
            return new Date().getTime();
        }
        if (token.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected a string but found '%s'.", token.getValue());
        }
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        String dateTimeString = token.getValue(String.class);
        try {
            return DateTimeFormatter.parse(dateTimeString);
        }
        catch (DateTimeParseException e) {
            throw new JsonParseException("Failed to parse string as a date: " + dateTimeString, e);
        }
    }

    private BsonBinary visitHexDataConstructor() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken subTypeToken = this.popToken();
        if (subTypeToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected a binary subtype but found '%s'.", subTypeToken.getValue());
        }
        this.verifyToken(JsonTokenType.COMMA);
        String hex = this.readStringFromExtendedJson();
        this.verifyToken(JsonTokenType.RIGHT_PAREN);
        if ((hex.length() & 1) != 0) {
            hex = "0" + hex;
        }
        for (BsonBinarySubType subType : BsonBinarySubType.values()) {
            if (subType.getValue() != subTypeToken.getValue(Integer.class).intValue()) continue;
            return new BsonBinary(subType, JsonReader.decodeHex(hex));
        }
        return new BsonBinary(JsonReader.decodeHex(hex));
    }

    private long visitDateTimeConstructor() {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken token = this.popToken();
        if (token.getType() == JsonTokenType.RIGHT_PAREN) {
            return new Date().getTime();
        }
        if (token.getType() == JsonTokenType.STRING) {
            this.verifyToken(JsonTokenType.RIGHT_PAREN);
            String s = token.getValue(String.class);
            ParsePosition pos = new ParsePosition(0);
            Date dateTime = ((DateFormat)format).parse(s, pos);
            if (dateTime != null && pos.getIndex() == s.length()) {
                return dateTime.getTime();
            }
            throw new JsonParseException("JSON reader expected a date in 'EEE MMM dd yyyy HH:mm:ss z' format but found '%s'.", s);
        }
        if (token.getType() == JsonTokenType.INT32 || token.getType() == JsonTokenType.INT64) {
            int pos;
            long[] values;
            block8: {
                values = new long[7];
                pos = 0;
                do {
                    if (pos < values.length) {
                        values[pos++] = token.getValue(Long.class);
                    }
                    if ((token = this.popToken()).getType() == JsonTokenType.RIGHT_PAREN) break block8;
                    if (token.getType() == JsonTokenType.COMMA) continue;
                    throw new JsonParseException("JSON reader expected a ',' or a ')' but found '%s'.", token.getValue());
                } while ((token = this.popToken()).getType() == JsonTokenType.INT32 || token.getType() == JsonTokenType.INT64);
                throw new JsonParseException("JSON reader expected an integer but found '%s'.", token.getValue());
            }
            if (pos == 1) {
                return values[0];
            }
            if (pos < 3 || pos > 7) {
                throw new JsonParseException("JSON reader expected 1 or 3-7 integers but found %d.", pos);
            }
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.set(1, (int)values[0]);
            calendar.set(2, (int)values[1]);
            calendar.set(5, (int)values[2]);
            calendar.set(11, (int)values[3]);
            calendar.set(12, (int)values[4]);
            calendar.set(13, (int)values[5]);
            calendar.set(14, (int)values[6]);
            return calendar.getTimeInMillis();
        }
        throw new JsonParseException("JSON reader expected an integer or a string but found '%s'.", token.getValue());
    }

    private String visitDateTimeConstructorWithOutNew() {
        this.verifyToken(JsonTokenType.LEFT_PAREN);
        JsonToken token = this.popToken();
        if (token.getType() != JsonTokenType.RIGHT_PAREN) {
            while (token.getType() != JsonTokenType.END_OF_FILE && (token = this.popToken()).getType() != JsonTokenType.RIGHT_PAREN) {
            }
            if (token.getType() != JsonTokenType.RIGHT_PAREN) {
                throw new JsonParseException("JSON reader expected a ')' but found '%s'.", token.getValue());
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
        return df.format(new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BsonBinary visitBinDataExtendedJson(String firstKey) {
        Mark mark = new Mark();
        try {
            this.verifyToken(JsonTokenType.COLON);
            if (firstKey.equals("$binary")) {
                JsonToken nextToken = this.popToken();
                if (nextToken.getType() == JsonTokenType.BEGIN_OBJECT) {
                    byte type;
                    byte[] data;
                    JsonToken nameToken = this.popToken();
                    String firstNestedKey = nameToken.getValue(String.class);
                    if (firstNestedKey.equals("base64")) {
                        this.verifyToken(JsonTokenType.COLON);
                        data = Base64.decode(this.readStringFromExtendedJson());
                        this.verifyToken(JsonTokenType.COMMA);
                        this.verifyString("subType");
                        this.verifyToken(JsonTokenType.COLON);
                        type = this.readBinarySubtypeFromExtendedJson();
                    } else if (firstNestedKey.equals("subType")) {
                        this.verifyToken(JsonTokenType.COLON);
                        type = this.readBinarySubtypeFromExtendedJson();
                        this.verifyToken(JsonTokenType.COMMA);
                        this.verifyString("base64");
                        this.verifyToken(JsonTokenType.COLON);
                        data = Base64.decode(this.readStringFromExtendedJson());
                    } else {
                        throw new JsonParseException("Unexpected key for $binary: " + firstNestedKey);
                    }
                    this.verifyToken(JsonTokenType.END_OBJECT);
                    this.verifyToken(JsonTokenType.END_OBJECT);
                    BsonBinary bsonBinary = new BsonBinary(type, data);
                    return bsonBinary;
                }
                mark.reset();
                BsonBinary bsonBinary = this.visitLegacyBinaryExtendedJson(firstKey);
                return bsonBinary;
            }
            mark.reset();
            BsonBinary bsonBinary = this.visitLegacyBinaryExtendedJson(firstKey);
            return bsonBinary;
        }
        finally {
            mark.discard();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BsonBinary visitLegacyBinaryExtendedJson(String firstKey) {
        Mark mark = new Mark();
        try {
            byte type;
            byte[] data;
            this.verifyToken(JsonTokenType.COLON);
            if (firstKey.equals("$binary")) {
                data = Base64.decode(this.readStringFromExtendedJson());
                this.verifyToken(JsonTokenType.COMMA);
                this.verifyString("$type");
                this.verifyToken(JsonTokenType.COLON);
                type = this.readBinarySubtypeFromExtendedJson();
            } else {
                type = this.readBinarySubtypeFromExtendedJson();
                this.verifyToken(JsonTokenType.COMMA);
                this.verifyString("$binary");
                this.verifyToken(JsonTokenType.COLON);
                data = Base64.decode(this.readStringFromExtendedJson());
            }
            this.verifyToken(JsonTokenType.END_OBJECT);
            BsonBinary bsonBinary = new BsonBinary(type, data);
            return bsonBinary;
        }
        catch (JsonParseException e) {
            mark.reset();
            BsonBinary bsonBinary = null;
            return bsonBinary;
        }
        catch (NumberFormatException e) {
            mark.reset();
            BsonBinary bsonBinary = null;
            return bsonBinary;
        }
        finally {
            mark.discard();
        }
    }

    private byte readBinarySubtypeFromExtendedJson() {
        JsonToken subTypeToken = this.popToken();
        if (subTypeToken.getType() != JsonTokenType.STRING && subTypeToken.getType() != JsonTokenType.INT32) {
            throw new JsonParseException("JSON reader expected a string or number but found '%s'.", subTypeToken.getValue());
        }
        if (subTypeToken.getType() == JsonTokenType.STRING) {
            return (byte)Integer.parseInt(subTypeToken.getValue(String.class), 16);
        }
        return subTypeToken.getValue(Integer.class).byteValue();
    }

    private long visitDateTimeExtendedJson() {
        long value;
        this.verifyToken(JsonTokenType.COLON);
        JsonToken valueToken = this.popToken();
        if (valueToken.getType() == JsonTokenType.BEGIN_OBJECT) {
            JsonToken nameToken = this.popToken();
            String name = nameToken.getValue(String.class);
            if (!name.equals("$numberLong")) {
                throw new JsonParseException(String.format("JSON reader expected $numberLong within $date, but found %s", name));
            }
            value = this.visitNumberLongExtendedJson();
            this.verifyToken(JsonTokenType.END_OBJECT);
        } else {
            if (valueToken.getType() == JsonTokenType.INT32 || valueToken.getType() == JsonTokenType.INT64) {
                value = valueToken.getValue(Long.class);
            } else if (valueToken.getType() == JsonTokenType.STRING) {
                String dateTimeString = valueToken.getValue(String.class);
                try {
                    value = DateTimeFormatter.parse(dateTimeString);
                }
                catch (DateTimeParseException e) {
                    throw new JsonParseException("Failed to parse string as a date", e);
                }
            } else {
                throw new JsonParseException("JSON reader expected an integer or string but found '%s'.", valueToken.getValue());
            }
            this.verifyToken(JsonTokenType.END_OBJECT);
        }
        return value;
    }

    private MaxKey visitMaxKeyExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.INT32, 1);
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new MaxKey();
    }

    private MinKey visitMinKeyExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.INT32, 1);
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new MinKey();
    }

    private ObjectId visitObjectIdExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        ObjectId objectId = new ObjectId(this.readStringFromExtendedJson());
        this.verifyToken(JsonTokenType.END_OBJECT);
        return objectId;
    }

    private BsonRegularExpression visitNewRegularExpressionExtendedJson() {
        String pattern;
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.BEGIN_OBJECT);
        String options = "";
        String firstKey = this.readStringFromExtendedJson();
        if (firstKey.equals("pattern")) {
            this.verifyToken(JsonTokenType.COLON);
            pattern = this.readStringFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("options");
            this.verifyToken(JsonTokenType.COLON);
            options = this.readStringFromExtendedJson();
        } else if (firstKey.equals("options")) {
            this.verifyToken(JsonTokenType.COLON);
            options = this.readStringFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("pattern");
            this.verifyToken(JsonTokenType.COLON);
            pattern = this.readStringFromExtendedJson();
        } else {
            throw new JsonParseException("Expected 't' and 'i' fields in $timestamp document but found " + firstKey);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new BsonRegularExpression(pattern, options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BsonRegularExpression visitRegularExpressionExtendedJson(String firstKey) {
        Mark extendedJsonMark = new Mark();
        try {
            String pattern;
            this.verifyToken(JsonTokenType.COLON);
            String options = "";
            if (firstKey.equals("$regex")) {
                pattern = this.readStringFromExtendedJson();
                this.verifyToken(JsonTokenType.COMMA);
                this.verifyString("$options");
                this.verifyToken(JsonTokenType.COLON);
                options = this.readStringFromExtendedJson();
            } else {
                options = this.readStringFromExtendedJson();
                this.verifyToken(JsonTokenType.COMMA);
                this.verifyString("$regex");
                this.verifyToken(JsonTokenType.COLON);
                pattern = this.readStringFromExtendedJson();
            }
            this.verifyToken(JsonTokenType.END_OBJECT);
            BsonRegularExpression bsonRegularExpression = new BsonRegularExpression(pattern, options);
            return bsonRegularExpression;
        }
        catch (JsonParseException e) {
            extendedJsonMark.reset();
            BsonRegularExpression bsonRegularExpression = null;
            return bsonRegularExpression;
        }
        finally {
            extendedJsonMark.discard();
        }
    }

    private String readStringFromExtendedJson() {
        JsonToken patternToken = this.popToken();
        if (patternToken.getType() != JsonTokenType.STRING) {
            throw new JsonParseException("JSON reader expected a string but found '%s'.", patternToken.getValue());
        }
        return patternToken.getValue(String.class);
    }

    private String visitSymbolExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        String symbol = this.readStringFromExtendedJson();
        this.verifyToken(JsonTokenType.END_OBJECT);
        return symbol;
    }

    private BsonTimestamp visitTimestampExtendedJson() {
        int increment;
        int time;
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.BEGIN_OBJECT);
        String firstKey = this.readStringFromExtendedJson();
        if (firstKey.equals("t")) {
            this.verifyToken(JsonTokenType.COLON);
            time = this.readIntFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("i");
            this.verifyToken(JsonTokenType.COLON);
            increment = this.readIntFromExtendedJson();
        } else if (firstKey.equals("i")) {
            this.verifyToken(JsonTokenType.COLON);
            increment = this.readIntFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("t");
            this.verifyToken(JsonTokenType.COLON);
            time = this.readIntFromExtendedJson();
        } else {
            throw new JsonParseException("Expected 't' and 'i' fields in $timestamp document but found " + firstKey);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new BsonTimestamp(time, increment);
    }

    private int readIntFromExtendedJson() {
        int value;
        JsonToken nextToken = this.popToken();
        if (nextToken.getType() == JsonTokenType.INT32) {
            value = nextToken.getValue(Integer.class);
        } else if (nextToken.getType() == JsonTokenType.INT64) {
            value = nextToken.getValue(Long.class).intValue();
        } else {
            throw new JsonParseException("JSON reader expected an integer but found '%s'.", nextToken.getValue());
        }
        return value;
    }

    private BsonBinary visitUuidExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        String uuidString = this.readStringFromExtendedJson();
        this.verifyToken(JsonTokenType.END_OBJECT);
        try {
            return new BsonBinary(UUID.fromString(uuidString));
        }
        catch (IllegalArgumentException e) {
            throw new JsonParseException(e);
        }
    }

    private void visitJavaScriptExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        String code = this.readStringFromExtendedJson();
        JsonToken nextToken = this.popToken();
        switch (nextToken.getType()) {
            case COMMA: {
                this.verifyString("$scope");
                this.verifyToken(JsonTokenType.COLON);
                this.setState(AbstractBsonReader.State.VALUE);
                this.currentValue = code;
                this.setCurrentBsonType(BsonType.JAVASCRIPT_WITH_SCOPE);
                this.setContext(new Context((AbstractBsonReader.Context)this.getContext(), BsonContextType.SCOPE_DOCUMENT));
                break;
            }
            case END_OBJECT: {
                this.currentValue = code;
                this.setCurrentBsonType(BsonType.JAVASCRIPT);
                break;
            }
            default: {
                throw new JsonParseException("JSON reader expected ',' or '}' but found '%s'.", nextToken);
            }
        }
    }

    private BsonUndefined visitUndefinedExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        JsonToken valueToken = this.popToken();
        if (!valueToken.getValue(String.class).equals("true")) {
            throw new JsonParseException("JSON reader requires $undefined to have the value of true but found '%s'.", valueToken.getValue());
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new BsonUndefined();
    }

    private Long visitNumberLongExtendedJson() {
        Long value;
        this.verifyToken(JsonTokenType.COLON);
        String longAsString = this.readStringFromExtendedJson();
        try {
            value = Long.valueOf(longAsString);
        }
        catch (NumberFormatException e) {
            throw new JsonParseException(String.format("Exception converting value '%s' to type %s", longAsString, Long.class.getName()), e);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return value;
    }

    private Integer visitNumberIntExtendedJson() {
        Integer value;
        this.verifyToken(JsonTokenType.COLON);
        String intAsString = this.readStringFromExtendedJson();
        try {
            value = Integer.valueOf(intAsString);
        }
        catch (NumberFormatException e) {
            throw new JsonParseException(String.format("Exception converting value '%s' to type %s", intAsString, Integer.class.getName()), e);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return value;
    }

    private Double visitNumberDoubleExtendedJson() {
        Double value;
        this.verifyToken(JsonTokenType.COLON);
        String doubleAsString = this.readStringFromExtendedJson();
        try {
            value = Double.valueOf(doubleAsString);
        }
        catch (NumberFormatException e) {
            throw new JsonParseException(String.format("Exception converting value '%s' to type %s", doubleAsString, Double.class.getName()), e);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return value;
    }

    private Decimal128 visitNumberDecimalExtendedJson() {
        Decimal128 value;
        this.verifyToken(JsonTokenType.COLON);
        String decimal128AsString = this.readStringFromExtendedJson();
        try {
            value = Decimal128.parse(decimal128AsString);
        }
        catch (NumberFormatException e) {
            throw new JsonParseException(String.format("Exception converting value '%s' to type %s", decimal128AsString, Decimal128.class.getName()), e);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return value;
    }

    private BsonDbPointer visitDbPointerExtendedJson() {
        ObjectId oid;
        String ref;
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.BEGIN_OBJECT);
        String firstKey = this.readStringFromExtendedJson();
        if (firstKey.equals("$ref")) {
            this.verifyToken(JsonTokenType.COLON);
            ref = this.readStringFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$id");
            oid = this.readDbPointerIdFromExtendedJson();
            this.verifyToken(JsonTokenType.END_OBJECT);
        } else if (firstKey.equals("$id")) {
            oid = this.readDbPointerIdFromExtendedJson();
            this.verifyToken(JsonTokenType.COMMA);
            this.verifyString("$ref");
            this.verifyToken(JsonTokenType.COLON);
            ref = this.readStringFromExtendedJson();
        } else {
            throw new JsonParseException("Expected $ref and $id fields in $dbPointer document but found " + firstKey);
        }
        this.verifyToken(JsonTokenType.END_OBJECT);
        return new BsonDbPointer(ref, oid);
    }

    private ObjectId readDbPointerIdFromExtendedJson() {
        this.verifyToken(JsonTokenType.COLON);
        this.verifyToken(JsonTokenType.BEGIN_OBJECT);
        this.verifyToken(JsonTokenType.STRING, "$oid");
        ObjectId oid = this.visitObjectIdExtendedJson();
        return oid;
    }

    @Override
    public BsonReaderMark getMark() {
        return new Mark();
    }

    @Override
    protected Context getContext() {
        return (Context)super.getContext();
    }

    private static byte[] decodeHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("A hex string must contain an even number of characters: " + hex);
        }
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("A hex string can only contain the characters 0-9, A-F, a-f: " + hex);
            }
            out[i / 2] = (byte)(high * 16 + low);
        }
        return out;
    }

    protected class Context
    extends AbstractBsonReader.Context {
        protected Context(AbstractBsonReader.Context parentContext, BsonContextType contextType) {
            super(parentContext, contextType);
        }

        @Override
        protected Context getParentContext() {
            return (Context)super.getParentContext();
        }

        @Override
        protected BsonContextType getContextType() {
            return super.getContextType();
        }
    }

    protected class Mark
    extends AbstractBsonReader.Mark {
        private final JsonToken pushedToken;
        private final Object currentValue;
        private final int markPos;

        protected Mark() {
            this.pushedToken = JsonReader.this.pushedToken;
            this.currentValue = JsonReader.this.currentValue;
            this.markPos = JsonReader.this.scanner.mark();
        }

        @Override
        public void reset() {
            super.reset();
            JsonReader.this.pushedToken = this.pushedToken;
            JsonReader.this.currentValue = this.currentValue;
            JsonReader.this.scanner.reset(this.markPos);
            JsonReader.this.setContext(new Context(this.getParentContext(), this.getContextType()));
        }

        public void discard() {
            JsonReader.this.scanner.discard(this.markPos);
        }
    }
}

