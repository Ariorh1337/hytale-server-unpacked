package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLTaggedObject;

public class ContentInfo extends ASN1Object implements CMSObjectIdentifiers {
   private final ASN1ObjectIdentifier contentType;
   private final ASN1Encodable content;
   private final boolean isDefiniteLength;

   public static ContentInfo getInstance(Object var0) {
      if (var0 instanceof ContentInfo) {
         return (ContentInfo)var0;
      } else {
         return var0 != null ? new ContentInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ContentInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return new ContentInfo(ASN1Sequence.getInstance(var0, var1));
   }

   public static ContentInfo getTagged(ASN1TaggedObject var0, boolean var1) {
      return new ContentInfo(ASN1Sequence.getTagged(var0, var1));
   }

   private ContentInfo(ASN1Sequence var1) {
      int var2 = var1.size();
      if (var2 >= 1 && var2 <= 2) {
         this.contentType = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         ASN1Object var3 = null;
         if (var2 > 1) {
            ASN1TaggedObject var4 = ASN1TaggedObject.getContextInstance(var1.getObjectAt(1));
            if (!var4.isExplicit() || var4.getTagNo() != 0) {
               throw new IllegalArgumentException("Bad tag for 'content'");
            }

            var3 = var4.getExplicitBaseObject();
         }

         this.content = var3;
         this.isDefiniteLength = !(var1 instanceof BERSequence);
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }
   }

   public ContentInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      if (var1 == null) {
         throw new NullPointerException("'contentType' cannot be null");
      }

      this.contentType = var1;
      this.content = var2;
      if (var2 != null) {
         ASN1Primitive var3 = var2.toASN1Primitive();
         this.isDefiniteLength = var3 instanceof DEROctetString || var3 instanceof DLSequence || var3 instanceof DERSequence;
      } else {
         this.isDefiniteLength = true;
      }
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   public ASN1Encodable getContent() {
      return this.content;
   }

   public boolean isDefiniteLength() {
      return this.isDefiniteLength;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      if (this.isDefiniteLength) {
         return this.content == null ? new DLSequence(this.contentType) : new DLSequence(this.contentType, new DLTaggedObject(0, this.content));
      } else {
         return this.content == null ? new BERSequence(this.contentType) : new BERSequence(this.contentType, new BERTaggedObject(0, this.content));
      }
   }
}
