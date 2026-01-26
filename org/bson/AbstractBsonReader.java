/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.util.Arrays;
import org.bson.BSONException;
import org.bson.BsonBinary;
import org.bson.BsonContextType;
import org.bson.BsonDbPointer;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonReaderMark;
import org.bson.BsonRegularExpression;
import org.bson.BsonSerializationException;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.StringUtils;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public abstract class AbstractBsonReader
implements BsonReader {
    private State state = State.INITIAL;
    private Context context;
    private BsonType currentBsonType;
    private String currentName;
    private boolean closed;

    protected AbstractBsonReader() {
    }

    @Override
    public BsonType getCurrentBsonType() {
        return this.currentBsonType;
    }

    @Override
    public String getCurrentName() {
        if (this.state != State.VALUE) {
            this.throwInvalidState("getCurrentName", State.VALUE);
        }
        return this.currentName;
    }

    protected void setCurrentBsonType(BsonType newType) {
        this.currentBsonType = newType;
    }

    public State getState() {
        return this.state;
    }

    protected void setState(State newState) {
        this.state = newState;
    }

    protected void setCurrentName(String newName) {
        this.currentName = newName;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    protected boolean isClosed() {
        return this.closed;
    }

    protected abstract BsonBinary doReadBinaryData();

    protected abstract byte doPeekBinarySubType();

    protected abstract int doPeekBinarySize();

    protected abstract boolean doReadBoolean();

    protected abstract long doReadDateTime();

    protected abstract double doReadDouble();

    protected abstract void doReadEndArray();

    protected abstract void doReadEndDocument();

    protected abstract int doReadInt32();

    protected abstract long doReadInt64();

    protected abstract Decimal128 doReadDecimal128();

    protected abstract String doReadJavaScript();

    protected abstract String doReadJavaScriptWithScope();

    protected abstract void doReadMaxKey();

    protected abstract void doReadMinKey();

    protected abstract void doReadNull();

    protected abstract ObjectId doReadObjectId();

    protected abstract BsonRegularExpression doReadRegularExpression();

    protected abstract BsonDbPointer doReadDBPointer();

    protected abstract void doReadStartArray();

    protected abstract void doReadStartDocument();

    protected abstract String doReadString();

    protected abstract String doReadSymbol();

    protected abstract BsonTimestamp doReadTimestamp();

    protected abstract void doReadUndefined();

    protected abstract void doSkipName();

    protected abstract void doSkipValue();

    @Override
    public BsonBinary readBinaryData() {
        this.checkPreconditions("readBinaryData", BsonType.BINARY);
        this.setState(this.getNextState());
        return this.doReadBinaryData();
    }

    @Override
    public byte peekBinarySubType() {
        this.checkPreconditions("readBinaryData", BsonType.BINARY);
        return this.doPeekBinarySubType();
    }

    @Override
    public int peekBinarySize() {
        this.checkPreconditions("readBinaryData", BsonType.BINARY);
        return this.doPeekBinarySize();
    }

    @Override
    public boolean readBoolean() {
        this.checkPreconditions("readBoolean", BsonType.BOOLEAN);
        this.setState(this.getNextState());
        return this.doReadBoolean();
    }

    @Override
    public abstract BsonType readBsonType();

    @Override
    public long readDateTime() {
        this.checkPreconditions("readDateTime", BsonType.DATE_TIME);
        this.setState(this.getNextState());
        return this.doReadDateTime();
    }

    @Override
    public double readDouble() {
        this.checkPreconditions("readDouble", BsonType.DOUBLE);
        this.setState(this.getNextState());
        return this.doReadDouble();
    }

    @Override
    public void readEndArray() {
        if (this.isClosed()) {
            throw new IllegalStateException("BSONBinaryWriter");
        }
        if (this.getContext().getContextType() != BsonContextType.ARRAY) {
            this.throwInvalidContextType("readEndArray", this.getContext().getContextType(), BsonContextType.ARRAY);
        }
        if (this.getState() == State.TYPE) {
            this.readBsonType();
        }
        if (this.getState() != State.END_OF_ARRAY) {
            this.throwInvalidState("ReadEndArray", State.END_OF_ARRAY);
        }
        this.doReadEndArray();
        this.setStateOnEnd();
    }

    @Override
    public void readEndDocument() {
        if (this.isClosed()) {
            throw new IllegalStateException("BSONBinaryWriter");
        }
        if (this.getContext().getContextType() != BsonContextType.DOCUMENT && this.getContext().getContextType() != BsonContextType.SCOPE_DOCUMENT) {
            this.throwInvalidContextType("readEndDocument", this.getContext().getContextType(), BsonContextType.DOCUMENT, BsonContextType.SCOPE_DOCUMENT);
        }
        if (this.getState() == State.TYPE) {
            this.readBsonType();
        }
        if (this.getState() != State.END_OF_DOCUMENT) {
            this.throwInvalidState("readEndDocument", State.END_OF_DOCUMENT);
        }
        this.doReadEndDocument();
        this.setStateOnEnd();
    }

    @Override
    public int readInt32() {
        this.checkPreconditions("readInt32", BsonType.INT32);
        this.setState(this.getNextState());
        return this.doReadInt32();
    }

    @Override
    public long readInt64() {
        this.checkPreconditions("readInt64", BsonType.INT64);
        this.setState(this.getNextState());
        return this.doReadInt64();
    }

    @Override
    public Decimal128 readDecimal128() {
        this.checkPreconditions("readDecimal", BsonType.DECIMAL128);
        this.setState(this.getNextState());
        return this.doReadDecimal128();
    }

    @Override
    public String readJavaScript() {
        this.checkPreconditions("readJavaScript", BsonType.JAVASCRIPT);
        this.setState(this.getNextState());
        return this.doReadJavaScript();
    }

    @Override
    public String readJavaScriptWithScope() {
        this.checkPreconditions("readJavaScriptWithScope", BsonType.JAVASCRIPT_WITH_SCOPE);
        this.setState(State.SCOPE_DOCUMENT);
        return this.doReadJavaScriptWithScope();
    }

    @Override
    public void readMaxKey() {
        this.checkPreconditions("readMaxKey", BsonType.MAX_KEY);
        this.setState(this.getNextState());
        this.doReadMaxKey();
    }

    @Override
    public void readMinKey() {
        this.checkPreconditions("readMinKey", BsonType.MIN_KEY);
        this.setState(this.getNextState());
        this.doReadMinKey();
    }

    @Override
    public void readNull() {
        this.checkPreconditions("readNull", BsonType.NULL);
        this.setState(this.getNextState());
        this.doReadNull();
    }

    @Override
    public ObjectId readObjectId() {
        this.checkPreconditions("readObjectId", BsonType.OBJECT_ID);
        this.setState(this.getNextState());
        return this.doReadObjectId();
    }

    @Override
    public BsonRegularExpression readRegularExpression() {
        this.checkPreconditions("readRegularExpression", BsonType.REGULAR_EXPRESSION);
        this.setState(this.getNextState());
        return this.doReadRegularExpression();
    }

    @Override
    public BsonDbPointer readDBPointer() {
        this.checkPreconditions("readDBPointer", BsonType.DB_POINTER);
        this.setState(this.getNextState());
        return this.doReadDBPointer();
    }

    @Override
    public void readStartArray() {
        this.checkPreconditions("readStartArray", BsonType.ARRAY);
        this.doReadStartArray();
        this.setState(State.TYPE);
    }

    @Override
    public void readStartDocument() {
        this.checkPreconditions("readStartDocument", BsonType.DOCUMENT);
        this.doReadStartDocument();
        this.setState(State.TYPE);
    }

    @Override
    public String readString() {
        this.checkPreconditions("readString", BsonType.STRING);
        this.setState(this.getNextState());
        return this.doReadString();
    }

    @Override
    public String readSymbol() {
        this.checkPreconditions("readSymbol", BsonType.SYMBOL);
        this.setState(this.getNextState());
        return this.doReadSymbol();
    }

    @Override
    public BsonTimestamp readTimestamp() {
        this.checkPreconditions("readTimestamp", BsonType.TIMESTAMP);
        this.setState(this.getNextState());
        return this.doReadTimestamp();
    }

    @Override
    public void readUndefined() {
        this.checkPreconditions("readUndefined", BsonType.UNDEFINED);
        this.setState(this.getNextState());
        this.doReadUndefined();
    }

    @Override
    public void skipName() {
        if (this.isClosed()) {
            throw new IllegalStateException("This instance has been closed");
        }
        if (this.getState() != State.NAME) {
            this.throwInvalidState("skipName", State.NAME);
        }
        this.setState(State.VALUE);
        this.doSkipName();
    }

    @Override
    public void skipValue() {
        if (this.isClosed()) {
            throw new IllegalStateException("BSONBinaryWriter");
        }
        if (this.getState() != State.VALUE) {
            this.throwInvalidState("skipValue", State.VALUE);
        }
        this.doSkipValue();
        this.setState(State.TYPE);
    }

    @Override
    public BsonBinary readBinaryData(String name) {
        this.verifyName(name);
        return this.readBinaryData();
    }

    @Override
    public boolean readBoolean(String name) {
        this.verifyName(name);
        return this.readBoolean();
    }

    @Override
    public long readDateTime(String name) {
        this.verifyName(name);
        return this.readDateTime();
    }

    @Override
    public double readDouble(String name) {
        this.verifyName(name);
        return this.readDouble();
    }

    @Override
    public int readInt32(String name) {
        this.verifyName(name);
        return this.readInt32();
    }

    @Override
    public long readInt64(String name) {
        this.verifyName(name);
        return this.readInt64();
    }

    @Override
    public Decimal128 readDecimal128(String name) {
        this.verifyName(name);
        return this.readDecimal128();
    }

    @Override
    public String readJavaScript(String name) {
        this.verifyName(name);
        return this.readJavaScript();
    }

    @Override
    public String readJavaScriptWithScope(String name) {
        this.verifyName(name);
        return this.readJavaScriptWithScope();
    }

    @Override
    public void readMaxKey(String name) {
        this.verifyName(name);
        this.readMaxKey();
    }

    @Override
    public void readMinKey(String name) {
        this.verifyName(name);
        this.readMinKey();
    }

    @Override
    public String readName() {
        if (this.state == State.TYPE) {
            this.readBsonType();
        }
        if (this.state != State.NAME) {
            this.throwInvalidState("readName", State.NAME);
        }
        this.state = State.VALUE;
        return this.currentName;
    }

    @Override
    public void readName(String name) {
        this.verifyName(name);
    }

    @Override
    public void readNull(String name) {
        this.verifyName(name);
        this.readNull();
    }

    @Override
    public ObjectId readObjectId(String name) {
        this.verifyName(name);
        return this.readObjectId();
    }

    @Override
    public BsonRegularExpression readRegularExpression(String name) {
        this.verifyName(name);
        return this.readRegularExpression();
    }

    @Override
    public BsonDbPointer readDBPointer(String name) {
        this.verifyName(name);
        return this.readDBPointer();
    }

    @Override
    public String readString(String name) {
        this.verifyName(name);
        return this.readString();
    }

    @Override
    public String readSymbol(String name) {
        this.verifyName(name);
        return this.readSymbol();
    }

    @Override
    public BsonTimestamp readTimestamp(String name) {
        this.verifyName(name);
        return this.readTimestamp();
    }

    @Override
    public void readUndefined(String name) {
        this.verifyName(name);
        this.readUndefined();
    }

    protected void throwInvalidContextType(String methodName, BsonContextType actualContextType, BsonContextType ... validContextTypes) {
        String validContextTypesString = StringUtils.join(" or ", Arrays.asList(validContextTypes));
        String message = String.format("%s can only be called when ContextType is %s, not when ContextType is %s.", new Object[]{methodName, validContextTypesString, actualContextType});
        throw new BsonInvalidOperationException(message);
    }

    protected void throwInvalidState(String methodName, State ... validStates) {
        String validStatesString = StringUtils.join(" or ", Arrays.asList(validStates));
        String message = String.format("%s can only be called when State is %s, not when State is %s.", new Object[]{methodName, validStatesString, this.state});
        throw new BsonInvalidOperationException(message);
    }

    protected void verifyBSONType(String methodName, BsonType requiredBsonType) {
        if (this.state == State.INITIAL || this.state == State.SCOPE_DOCUMENT || this.state == State.TYPE) {
            this.readBsonType();
        }
        if (this.state == State.NAME) {
            this.skipName();
        }
        if (this.state != State.VALUE) {
            this.throwInvalidState(methodName, State.VALUE);
        }
        if (this.currentBsonType != requiredBsonType) {
            throw new BsonInvalidOperationException(String.format("%s can only be called when CurrentBSONType is %s, not when CurrentBSONType is %s.", new Object[]{methodName, requiredBsonType, this.currentBsonType}));
        }
    }

    protected void verifyName(String expectedName) {
        this.readBsonType();
        String actualName = this.readName();
        if (!actualName.equals(expectedName)) {
            throw new BsonSerializationException(String.format("Expected element name to be '%s', not '%s'.", expectedName, actualName));
        }
    }

    protected void checkPreconditions(String methodName, BsonType type) {
        if (this.isClosed()) {
            throw new IllegalStateException("BsonWriter is closed");
        }
        this.verifyBSONType(methodName, type);
    }

    protected Context getContext() {
        return this.context;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected State getNextState() {
        switch (this.context.getContextType()) {
            case ARRAY: 
            case DOCUMENT: 
            case SCOPE_DOCUMENT: {
                return State.TYPE;
            }
            case TOP_LEVEL: {
                return State.DONE;
            }
        }
        throw new BSONException(String.format("Unexpected ContextType %s.", new Object[]{this.context.getContextType()}));
    }

    private void setStateOnEnd() {
        switch (this.getContext().getContextType()) {
            case ARRAY: 
            case DOCUMENT: {
                this.setState(State.TYPE);
                break;
            }
            case TOP_LEVEL: {
                this.setState(State.DONE);
                break;
            }
            default: {
                throw new BSONException(String.format("Unexpected ContextType %s.", new Object[]{this.getContext().getContextType()}));
            }
        }
    }

    public static enum State {
        INITIAL,
        TYPE,
        NAME,
        VALUE,
        SCOPE_DOCUMENT,
        END_OF_DOCUMENT,
        END_OF_ARRAY,
        DONE,
        CLOSED;

    }

    protected abstract class Context {
        private final Context parentContext;
        private final BsonContextType contextType;

        protected Context(Context parentContext, BsonContextType contextType) {
            this.parentContext = parentContext;
            this.contextType = contextType;
        }

        protected Context getParentContext() {
            return this.parentContext;
        }

        protected BsonContextType getContextType() {
            return this.contextType;
        }
    }

    protected class Mark
    implements BsonReaderMark {
        private final State state;
        private final Context parentContext;
        private final BsonContextType contextType;
        private final BsonType currentBsonType;
        private final String currentName;

        protected Context getParentContext() {
            return this.parentContext;
        }

        protected BsonContextType getContextType() {
            return this.contextType;
        }

        protected Mark() {
            this.state = AbstractBsonReader.this.state;
            this.parentContext = AbstractBsonReader.this.context.parentContext;
            this.contextType = AbstractBsonReader.this.context.contextType;
            this.currentBsonType = AbstractBsonReader.this.currentBsonType;
            this.currentName = AbstractBsonReader.this.currentName;
        }

        @Override
        public void reset() {
            AbstractBsonReader.this.state = this.state;
            AbstractBsonReader.this.currentBsonType = this.currentBsonType;
            AbstractBsonReader.this.currentName = this.currentName;
        }
    }
}

