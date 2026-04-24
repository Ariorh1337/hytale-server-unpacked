package org.bouncycastle.asn1;

public class DERExternal extends ASN1External {
   public static DERExternal fromSequence(ASN1Sequence var0) {
      return new DERExternal(var0);
   }

   public static DERExternal fromVector(ASN1EncodableVector var0) {
      return fromSequence(DERFactory.createSequence(var0));
   }

   @Deprecated
   public DERExternal(ASN1EncodableVector var1) {
      this((ASN1Sequence)DERFactory.createSequence(var1));
   }

   /** @deprecated */
   public DERExternal(DERSequence var1) {
      super(var1);
   }

   public DERExternal(ASN1Sequence var1) {
      super(var1);
   }

   public DERExternal(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, DERTaggedObject var4) {
      super(var1, var2, var3, var4);
   }

   public DERExternal(ASN1ObjectIdentifier var1, ASN1Integer var2, ASN1Primitive var3, int var4, ASN1Primitive var5) {
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
         var1.add(this.dataValueDescriptor.toDERObject());
      }

      var1.add(new DERTaggedObject(0 == this.encoding, this.encoding, this.externalContent));
      return new DERSequence(var1);
   }

   @Override
   ASN1Primitive toDERObject() {
      return this;
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }
}
