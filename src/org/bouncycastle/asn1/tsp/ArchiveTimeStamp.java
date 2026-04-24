package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ArchiveTimeStamp extends ASN1Object {
   private final AlgorithmIdentifier digestAlgorithm;
   private final Attributes attributes;
   private final ASN1Sequence reducedHashTree;
   private final ContentInfo timeStamp;

   public static ArchiveTimeStamp getInstance(Object var0) {
      if (var0 instanceof ArchiveTimeStamp) {
         return (ArchiveTimeStamp)var0;
      } else {
         return var0 != null ? new ArchiveTimeStamp(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static ArchiveTimeStamp getInstance(ASN1TaggedObject var0, boolean var1) {
      return new ArchiveTimeStamp(ASN1Sequence.getInstance(var0, var1));
   }

   public static ArchiveTimeStamp getTagged(ASN1TaggedObject var0, boolean var1) {
      return new ArchiveTimeStamp(ASN1Sequence.getTagged(var0, var1));
   }

   public ArchiveTimeStamp(AlgorithmIdentifier var1, PartialHashtree[] var2, ContentInfo var3) {
      this(var1, null, var2, var3);
   }

   public ArchiveTimeStamp(ContentInfo var1) {
      this(null, null, null, var1);
   }

   public ArchiveTimeStamp(AlgorithmIdentifier var1, Attributes var2, PartialHashtree[] var3, ContentInfo var4) {
      if (var4 == null) {
         throw new NullPointerException("'timeStamp' cannot be null");
      }

      this.digestAlgorithm = var1;
      this.attributes = var2;
      this.reducedHashTree = DERSequence.fromElementsOptional(var3);
      this.timeStamp = var4;
   }

   private ArchiveTimeStamp(ASN1Sequence var1) {
      int var2 = var1.size();
      int var3 = 0;
      if (var2 >= 1 && var2 <= 4) {
         AlgorithmIdentifier var4 = null;
         if (var3 < var2) {
            ASN1TaggedObject var5 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 0);
            if (var5 != null) {
               var3++;
               var4 = AlgorithmIdentifier.getTagged(var5, false);
            }
         }

         this.digestAlgorithm = var4;
         Attributes var9 = null;
         if (var3 < var2) {
            ASN1TaggedObject var6 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 1);
            if (var6 != null) {
               var3++;
               var9 = Attributes.getTagged(var6, false);
            }
         }

         this.attributes = var9;
         ASN1Sequence var10 = null;
         if (var3 < var2) {
            ASN1TaggedObject var7 = ASN1TaggedObject.getContextOptional(var1.getObjectAt(var3), 2);
            if (var7 != null) {
               var3++;
               var10 = ASN1Sequence.getInstance(var7, false);
            }
         }

         this.reducedHashTree = var10;
         this.timeStamp = ContentInfo.getInstance(var1.getObjectAt(var3++));
         if (var3 != var2) {
            throw new IllegalArgumentException("Unexpected elements in sequence");
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var2);
      }
   }

   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return this.digestAlgorithm != null ? this.digestAlgorithm : this.getTimeStampInfo().getMessageImprint().getHashAlgorithm();
   }

   public byte[] getTimeStampDigestValue() {
      return this.getTimeStampInfo().getMessageImprint().getHashedMessage();
   }

   private TSTInfo getTimeStampInfo() {
      if (!CMSObjectIdentifiers.signedData.equals(this.timeStamp.getContentType())) {
         throw new IllegalStateException("cannot identify algorithm identifier for digest");
      }

      SignedData var1 = SignedData.getInstance(this.timeStamp.getContent());
      ContentInfo var2 = var1.getEncapContentInfo();
      if (!PKCSObjectIdentifiers.id_ct_TSTInfo.equals(var2.getContentType())) {
         throw new IllegalStateException("cannot parse time stamp");
      }

      ASN1OctetString var3 = ASN1OctetString.getInstance(var2.getContent());
      return TSTInfo.getInstance(var3.getOctets());
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public PartialHashtree getHashTreeLeaf() {
      return this.reducedHashTree == null ? null : PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(0));
   }

   public PartialHashtree[] getReducedHashTree() {
      if (this.reducedHashTree == null) {
         return null;
      }

      PartialHashtree[] var1 = new PartialHashtree[this.reducedHashTree.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(var2));
      }

      return var1;
   }

   public ContentInfo getTimeStamp() {
      return this.timeStamp;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      if (this.digestAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 0, this.digestAlgorithm));
      }

      if (this.attributes != null) {
         var1.add(new DERTaggedObject(false, 1, this.attributes));
      }

      if (this.reducedHashTree != null) {
         var1.add(new DERTaggedObject(false, 2, this.reducedHashTree));
      }

      var1.add(this.timeStamp);
      return new DERSequence(var1);
   }
}
