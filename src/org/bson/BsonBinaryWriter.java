package org.bson;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import org.bson.assertions.Assertions;
import org.bson.io.BsonInput;
import org.bson.io.BsonOutput;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class BsonBinaryWriter extends AbstractBsonWriter {
   private final BsonBinaryWriterSettings binaryWriterSettings;
   private final BsonOutput bsonOutput;
   private final Deque<Integer> maxDocumentSizeStack = new ArrayDeque<>();
   private static final int ARRAY_INDEXES_CACHE_SIZE = 1000;
   private static final byte[] ARRAY_INDEXES_BUFFER;
   private static final int[] ARRAY_INDEXES_OFFSETS = new int[1000];
   private static final int[] ARRAY_INDEXES_LENGTHS = new int[1000];
   private BsonBinaryWriter.Mark mark;

   public BsonBinaryWriter(BsonOutput bsonOutput, FieldNameValidator validator) {
      this(new BsonWriterSettings(), new BsonBinaryWriterSettings(), bsonOutput, validator);
   }

   public BsonBinaryWriter(BsonOutput bsonOutput) {
      this(new BsonWriterSettings(), new BsonBinaryWriterSettings(), bsonOutput);
   }

   public BsonBinaryWriter(BsonWriterSettings settings, BsonBinaryWriterSettings binaryWriterSettings, BsonOutput bsonOutput) {
      this(settings, binaryWriterSettings, bsonOutput, NoOpFieldNameValidator.INSTANCE);
   }

   public BsonBinaryWriter(BsonWriterSettings settings, BsonBinaryWriterSettings binaryWriterSettings, BsonOutput bsonOutput, FieldNameValidator validator) {
      super(settings, validator);
      this.binaryWriterSettings = binaryWriterSettings;
      this.bsonOutput = bsonOutput;
      this.maxDocumentSizeStack.push(binaryWriterSettings.getMaxDocumentSize());
   }

   @Override
   public void close() {
      super.close();
   }

   public BsonOutput getBsonOutput() {
      return this.bsonOutput;
   }

   public BsonBinaryWriterSettings getBinaryWriterSettings() {
      return this.binaryWriterSettings;
   }

   protected BsonBinaryWriter.Context getContext() {
      return (BsonBinaryWriter.Context)super.getContext();
   }

   @Override
   protected void doWriteStartDocument() {
      if (this.getState() == AbstractBsonWriter.State.VALUE) {
         this.bsonOutput.writeByte(BsonType.DOCUMENT.getValue());
         this.writeCurrentName();
      }

      this.setContext(new BsonBinaryWriter.Context(this.getContext(), BsonContextType.DOCUMENT, this.bsonOutput.getPosition()));
      this.bsonOutput.writeInt32(0);
   }

   @Override
   protected void doWriteEndDocument() {
      this.bsonOutput.writeByte(0);
      this.backpatchSize();
      this.setContext(this.getContext().getParentContext());
      if (this.getContext() != null && this.getContext().getContextType() == BsonContextType.JAVASCRIPT_WITH_SCOPE) {
         this.backpatchSize();
         this.setContext(this.getContext().getParentContext());
      }
   }

   @Override
   protected void doWriteStartArray() {
      this.bsonOutput.writeByte(BsonType.ARRAY.getValue());
      this.writeCurrentName();
      this.setContext(new BsonBinaryWriter.Context(this.getContext(), BsonContextType.ARRAY, this.bsonOutput.getPosition()));
      this.bsonOutput.writeInt32(0);
   }

   @Override
   protected void doWriteEndArray() {
      this.bsonOutput.writeByte(0);
      this.backpatchSize();
      this.setContext(this.getContext().getParentContext());
   }

   @Override
   protected void doWriteBinaryData(BsonBinary value) {
      this.bsonOutput.writeByte(BsonType.BINARY.getValue());
      this.writeCurrentName();
      int totalLen = value.getData().length;
      if (value.getType() == BsonBinarySubType.OLD_BINARY.getValue()) {
         totalLen += 4;
      }

      this.bsonOutput.writeInt32(totalLen);
      this.bsonOutput.writeByte(value.getType());
      if (value.getType() == BsonBinarySubType.OLD_BINARY.getValue()) {
         this.bsonOutput.writeInt32(totalLen - 4);
      }

      this.bsonOutput.writeBytes(value.getData());
   }

   @Override
   public void doWriteBoolean(boolean value) {
      this.bsonOutput.writeByte(BsonType.BOOLEAN.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeByte(value ? 1 : 0);
   }

   @Override
   protected void doWriteDateTime(long value) {
      this.bsonOutput.writeByte(BsonType.DATE_TIME.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeInt64(value);
   }

   @Override
   protected void doWriteDBPointer(BsonDbPointer value) {
      this.bsonOutput.writeByte(BsonType.DB_POINTER.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeString(value.getNamespace());
      this.bsonOutput.writeBytes(value.getId().toByteArray());
   }

   @Override
   protected void doWriteDouble(double value) {
      this.bsonOutput.writeByte(BsonType.DOUBLE.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeDouble(value);
   }

   @Override
   protected void doWriteInt32(int value) {
      this.bsonOutput.writeByte(BsonType.INT32.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeInt32(value);
   }

   @Override
   protected void doWriteInt64(long value) {
      this.bsonOutput.writeByte(BsonType.INT64.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeInt64(value);
   }

   @Override
   protected void doWriteDecimal128(Decimal128 value) {
      this.bsonOutput.writeByte(BsonType.DECIMAL128.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeInt64(value.getLow());
      this.bsonOutput.writeInt64(value.getHigh());
   }

   @Override
   protected void doWriteJavaScript(String value) {
      this.bsonOutput.writeByte(BsonType.JAVASCRIPT.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeString(value);
   }

   @Override
   protected void doWriteJavaScriptWithScope(String value) {
      this.bsonOutput.writeByte(BsonType.JAVASCRIPT_WITH_SCOPE.getValue());
      this.writeCurrentName();
      this.setContext(new BsonBinaryWriter.Context(this.getContext(), BsonContextType.JAVASCRIPT_WITH_SCOPE, this.bsonOutput.getPosition()));
      this.bsonOutput.writeInt32(0);
      this.bsonOutput.writeString(value);
   }

   @Override
   protected void doWriteMaxKey() {
      this.bsonOutput.writeByte(BsonType.MAX_KEY.getValue());
      this.writeCurrentName();
   }

   @Override
   protected void doWriteMinKey() {
      this.bsonOutput.writeByte(BsonType.MIN_KEY.getValue());
      this.writeCurrentName();
   }

   @Override
   public void doWriteNull() {
      this.bsonOutput.writeByte(BsonType.NULL.getValue());
      this.writeCurrentName();
   }

   @Override
   public void doWriteObjectId(ObjectId value) {
      this.bsonOutput.writeByte(BsonType.OBJECT_ID.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeObjectId(value);
   }

   @Override
   public void doWriteRegularExpression(BsonRegularExpression value) {
      this.bsonOutput.writeByte(BsonType.REGULAR_EXPRESSION.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeCString(value.getPattern());
      this.bsonOutput.writeCString(value.getOptions());
   }

   @Override
   public void doWriteString(String value) {
      this.bsonOutput.writeByte(BsonType.STRING.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeString(value);
   }

   @Override
   public void doWriteSymbol(String value) {
      this.bsonOutput.writeByte(BsonType.SYMBOL.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeString(value);
   }

   @Override
   public void doWriteTimestamp(BsonTimestamp value) {
      this.bsonOutput.writeByte(BsonType.TIMESTAMP.getValue());
      this.writeCurrentName();
      this.bsonOutput.writeInt64(value.getValue());
   }

   @Override
   public void doWriteUndefined() {
      this.bsonOutput.writeByte(BsonType.UNDEFINED.getValue());
      this.writeCurrentName();
   }

   @Override
   public void pipe(BsonReader reader) {
      Assertions.notNull("reader", reader);
      this.pipeDocument(reader, null);
   }

   @Override
   public void pipe(BsonReader reader, List<BsonElement> extraElements) {
      Assertions.notNull("reader", reader);
      Assertions.notNull("extraElements", extraElements);
      this.pipeDocument(reader, extraElements);
   }

   private void pipeDocument(BsonReader reader, List<BsonElement> extraElements) {
      if (reader instanceof BsonBinaryReader) {
         BsonBinaryReader binaryReader = (BsonBinaryReader)reader;
         if (this.getState() == AbstractBsonWriter.State.VALUE) {
            this.bsonOutput.writeByte(BsonType.DOCUMENT.getValue());
            this.writeCurrentName();
         }

         BsonInput bsonInput = binaryReader.getBsonInput();
         int size = bsonInput.readInt32();
         if (size < 5) {
            throw new BsonSerializationException("Document size must be at least 5");
         }

         int pipedDocumentStartPosition = this.bsonOutput.getPosition();
         this.bsonOutput.writeInt32(size);
         byte[] bytes = new byte[size - 4];
         bsonInput.readBytes(bytes);
         this.bsonOutput.writeBytes(bytes);
         binaryReader.setState(AbstractBsonReader.State.TYPE);
         if (extraElements != null) {
            this.bsonOutput.truncateToPosition(this.bsonOutput.getPosition() - 1);
            this.setContext(new BsonBinaryWriter.Context(this.getContext(), BsonContextType.DOCUMENT, pipedDocumentStartPosition));
            this.setState(AbstractBsonWriter.State.NAME);
            this.pipeExtraElements(extraElements);
            this.bsonOutput.writeByte(0);
            this.bsonOutput.writeInt32(pipedDocumentStartPosition, this.bsonOutput.getPosition() - pipedDocumentStartPosition);
            this.setContext(this.getContext().getParentContext());
         }

         if (this.getContext() == null) {
            this.setState(AbstractBsonWriter.State.DONE);
         } else {
            if (this.getContext().getContextType() == BsonContextType.JAVASCRIPT_WITH_SCOPE) {
               this.backpatchSize();
               this.setContext(this.getContext().getParentContext());
            }

            this.setState(this.getNextState());
         }

         this.validateSize(this.bsonOutput.getPosition() - pipedDocumentStartPosition);
      } else if (extraElements != null) {
         super.pipe(reader, extraElements);
      } else {
         super.pipe(reader);
      }
   }

   public void pushMaxDocumentSize(int maxDocumentSize) {
      this.maxDocumentSizeStack.push(maxDocumentSize);
   }

   public void popMaxDocumentSize() {
      this.maxDocumentSizeStack.pop();
   }

   public void mark() {
      this.mark = new BsonBinaryWriter.Mark();
   }

   public void reset() {
      if (this.mark == null) {
         throw new IllegalStateException("Can not reset without first marking");
      }

      this.mark.reset();
      this.mark = null;
   }

   private void writeCurrentName() {
      if (this.getContext().getContextType() == BsonContextType.ARRAY) {
         int index = this.getContext().index++;
         if (index >= 1000) {
            this.bsonOutput.writeCString(Integer.toString(index));
         } else {
            this.bsonOutput.writeBytes(ARRAY_INDEXES_BUFFER, ARRAY_INDEXES_OFFSETS[index], ARRAY_INDEXES_LENGTHS[index]);
         }
      } else {
         this.bsonOutput.writeCString(this.getName());
      }
   }

   private void backpatchSize() {
      int size = this.bsonOutput.getPosition() - this.getContext().startPosition;
      this.validateSize(size);
      this.bsonOutput.writeInt32(this.bsonOutput.getPosition() - size, size);
   }

   private void validateSize(int size) {
      if (size > this.maxDocumentSizeStack.peek()) {
         throw new BsonMaximumSizeExceededException(String.format("Document size of %d is larger than maximum of %d.", size, this.maxDocumentSizeStack.peek()));
      }
   }

   static {
      int totalSize = 0;

      for (int i = 0; i < 1000; i++) {
         totalSize += (int)(Math.log10(Math.max(i, 1)) + 1.0 + 1.0);
      }

      ARRAY_INDEXES_BUFFER = new byte[totalSize];
      int offset = 0;

      for (int i = 0; i < 1000; i++) {
         String string = Integer.toString(i);
         int length = string.length();

         for (int j = 0; j < length; j++) {
            ARRAY_INDEXES_BUFFER[offset++] = (byte)string.charAt(j);
         }

         ARRAY_INDEXES_BUFFER[offset++] = 0;
         ARRAY_INDEXES_OFFSETS[i] = offset - (length + 1);
         ARRAY_INDEXES_LENGTHS[i] = length + 1;
      }
   }

   protected class Context extends AbstractBsonWriter.Context {
      private final int startPosition;
      private int index;

      public Context(BsonBinaryWriter.Context parentContext, BsonContextType contextType, int startPosition) {
         super(parentContext, contextType);
         this.startPosition = startPosition;
      }

      public Context(BsonBinaryWriter.Context from) {
         super(from);
         this.startPosition = from.startPosition;
         this.index = from.index;
      }

      public BsonBinaryWriter.Context getParentContext() {
         return (BsonBinaryWriter.Context)super.getParentContext();
      }

      public BsonBinaryWriter.Context copy() {
         return BsonBinaryWriter.this.new Context(this);
      }
   }

   protected class Mark extends AbstractBsonWriter.Mark {
      private final int position = BsonBinaryWriter.this.bsonOutput.getPosition();

      @Override
      protected void reset() {
         super.reset();
         BsonBinaryWriter.this.bsonOutput.truncateToPosition(BsonBinaryWriter.this.mark.position);
      }
   }
}
