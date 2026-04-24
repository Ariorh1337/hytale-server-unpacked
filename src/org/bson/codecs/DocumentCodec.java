package org.bson.codecs;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class DocumentCodec implements CollectibleCodec<Document>, OverridableUuidRepresentationCodec<Document> {
   private static final String ID_FIELD_NAME = "_id";
   private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(
      Arrays.asList(
         new ValueCodecProvider(),
         new CollectionCodecProvider(),
         new IterableCodecProvider(),
         new BsonValueCodecProvider(),
         new DocumentCodecProvider(),
         new MapCodecProvider()
      )
   );
   private static final BsonTypeCodecMap DEFAULT_BSON_TYPE_CODEC_MAP = new BsonTypeCodecMap(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, DEFAULT_REGISTRY);
   private static final IdGenerator DEFAULT_ID_GENERATOR = new ObjectIdGenerator();
   private final BsonTypeCodecMap bsonTypeCodecMap;
   private final CodecRegistry registry;
   private final IdGenerator idGenerator;
   private final Transformer valueTransformer;
   private final UuidRepresentation uuidRepresentation;

   public DocumentCodec() {
      this(DEFAULT_REGISTRY, DEFAULT_BSON_TYPE_CODEC_MAP, null);
   }

   public DocumentCodec(CodecRegistry registry) {
      this(registry, BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public DocumentCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap) {
      this(registry, bsonTypeClassMap, null);
   }

   public DocumentCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer);
   }

   private DocumentCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer) {
      this(registry, bsonTypeCodecMap, DEFAULT_ID_GENERATOR, valueTransformer, UuidRepresentation.UNSPECIFIED);
   }

   private DocumentCodec(
      CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, IdGenerator idGenerator, Transformer valueTransformer, UuidRepresentation uuidRepresentation
   ) {
      this.registry = Assertions.notNull("registry", registry);
      this.bsonTypeCodecMap = bsonTypeCodecMap;
      this.idGenerator = idGenerator;
      this.valueTransformer = valueTransformer != null ? valueTransformer : value -> value;
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public Codec<Document> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return this.uuidRepresentation.equals(uuidRepresentation)
         ? this
         : new DocumentCodec(this.registry, this.bsonTypeCodecMap, this.idGenerator, this.valueTransformer, uuidRepresentation);
   }

   public boolean documentHasId(Document document) {
      return document.containsKey("_id");
   }

   public BsonValue getDocumentId(Document document) {
      if (!this.documentHasId(document)) {
         throw new IllegalStateException("The document does not contain an _id");
      }

      Object id = document.get("_id");
      if (id instanceof BsonValue) {
         return (BsonValue)id;
      }

      BsonDocument idHoldingDocument = new BsonDocument();
      BsonWriter writer = new BsonDocumentWriter(idHoldingDocument);
      writer.writeStartDocument();
      writer.writeName("_id");
      this.writeValue(writer, EncoderContext.builder().build(), id);
      writer.writeEndDocument();
      return idHoldingDocument.get("_id");
   }

   public Document generateIdIfAbsentFromDocument(Document document) {
      if (!this.documentHasId(document)) {
         document.put("_id", this.idGenerator.generate());
      }

      return document;
   }

   public void encode(BsonWriter writer, Document document, EncoderContext encoderContext) {
      writer.writeStartDocument();
      this.beforeFields(writer, encoderContext, document);

      for (Entry<String, Object> entry : document.entrySet()) {
         if (!this.skipField(encoderContext, entry.getKey())) {
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
         }
      }

      writer.writeEndDocument();
   }

   public Document decode(BsonReader reader, DecoderContext decoderContext) {
      Document document = new Document();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         String fieldName = reader.readName();
         document.put(
            fieldName,
            ContainerCodecHelper.readValue(reader, decoderContext, this.bsonTypeCodecMap, this.uuidRepresentation, this.registry, this.valueTransformer)
         );
      }

      reader.readEndDocument();
      return document;
   }

   @Override
   public Class<Document> getEncoderClass() {
      return Document.class;
   }

   private void beforeFields(BsonWriter bsonWriter, EncoderContext encoderContext, Map<String, Object> document) {
      if (encoderContext.isEncodingCollectibleDocument() && document.containsKey("_id")) {
         bsonWriter.writeName("_id");
         this.writeValue(bsonWriter, encoderContext, document.get("_id"));
      }
   }

   private boolean skipField(EncoderContext encoderContext, String key) {
      return encoderContext.isEncodingCollectibleDocument() && key.equals("_id");
   }

   private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
      if (value == null) {
         writer.writeNull();
      } else {
         Codec codec = this.registry.get(value.getClass());
         encoderContext.encodeWithChildContext(codec, writer, value);
      }
   }
}
