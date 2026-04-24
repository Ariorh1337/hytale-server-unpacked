package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BERGenerator extends ASN1Generator {
   private boolean _tagged = false;
   private boolean _isExplicit;
   private int _tagNo;

   protected BERGenerator(OutputStream var1) {
      super(var1);
   }

   protected BERGenerator(OutputStream var1, int var2, boolean var3) {
      super(var1);
      this._tagged = true;
      this._isExplicit = var3;
      this._tagNo = var2;
   }

   @Override
   public OutputStream getRawOutputStream() {
      return this._out;
   }

   private void writeHdr(int var1) throws IOException {
      this._out.write(var1);
      this._out.write(128);
   }

   protected void writeBERHeader(int var1) throws IOException {
      if (!this._tagged) {
         this.writeHdr(var1);
      } else if (this._isExplicit) {
         this.writeHdr(this._tagNo | 128 | 32);
         this.writeHdr(var1);
      } else {
         this.writeHdr(inheritConstructedFlag(this._tagNo | 128, var1));
      }
   }

   protected void writeBEREnd() throws IOException {
      this._out.write(0);
      this._out.write(0);
      if (this._tagged && this._isExplicit) {
         this._out.write(0);
         this._out.write(0);
      }
   }
}
