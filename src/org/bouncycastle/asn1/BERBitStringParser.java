package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

public class BERBitStringParser implements ASN1BitStringParser {
   private final ASN1StreamParser parser;
   private ConstructedBitStream _bitStream;

   BERBitStringParser(ASN1StreamParser var1) {
      this.parser = var1;
   }

   @Override
   public InputStream getOctetStream() throws IOException {
      this._bitStream = new ConstructedBitStream(this.parser, true);
      return this._bitStream;
   }

   @Override
   public InputStream getBitStream() throws IOException {
      this._bitStream = new ConstructedBitStream(this.parser, false);
      return this._bitStream;
   }

   @Override
   public int getPadBits() {
      return this._bitStream.getPadBits();
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return parse(this.parser);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("IOException converting stream to byte array: " + var2.getMessage(), var2);
      }
   }

   static BERBitString parse(ASN1StreamParser var0) throws IOException {
      ConstructedBitStream var1 = new ConstructedBitStream(var0, false);
      byte[] var2 = Streams.readAll(var1);
      int var3 = var1.getPadBits();
      return new BERBitString(var2, var3);
   }
}
