package org.bouncycastle.asn1;

public class DLExternal extends ASN1External {
   public static DLExternal fromSequence(ASN1Sequence var0) {
      return new DLExternal(var0);
   }

   public static DLExternal fromVector(ASN1EncodableVector var0) {
      return fromSequence(DLFactory.createSequence(var0));
   }

   @Deprecated
   public DLExternal(ASN1EncodableVector var1) {
      this((ASN1Sequence)DLFactory.createSequence(var1));
   }

   /** @deprecated */
   public DLExternal(DLSequence var1) {
      super(var1);
   }

   private DLExternal(ASN1Sequence var1) {
      super(var1);
   }

   public DLExternal(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, DERTaggedObject var4) {
      super(var1, var2, var3, var4);
   }

   public DLExternal(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, int var4, ASN1Primitive var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Override
   ASN1Sequence buildSequence() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      if (this.directReference != null) {
         var1.add(this.directReference);
      }

      if (this.indirectReference != null) {
         var1.add(this.indirectReference);
      }

      if (this.dataValueDescriptor != null) {
         var1.add(this.dataValueDescriptor.toDLObject());
      }

      var1.add(new DLTaggedObject(0 == this.encoding, this.encoding, this.externalContent));
      return new DLSequence(var1);
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }
}
