package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class CMSSignedDataStreamGenerator extends CMSSignedGenerator {
   private int _bufferSize;
   private String encoding = "BER";

   public CMSSignedDataStreamGenerator() {
   }

   public CMSSignedDataStreamGenerator(DigestAlgorithmIdentifierFinder var1) {
      super(var1);
   }

   public void setBufferSize(int var1) {
      this._bufferSize = var1;
   }

   public void setEncoding(String var1) {
      if (!"BER".equals(var1) && !"DL".equals(var1) && !"DER".equals(var1)) {
         throw new IllegalArgumentException("encoding must be one of BER, DER, or DL");
      }

      this.encoding = var1;
   }

   public OutputStream open(OutputStream var1) throws IOException {
      return this.open(var1, false);
   }

   public OutputStream open(OutputStream var1, boolean var2) throws IOException {
      return this.open(CMSObjectIdentifiers.data, var1, var2);
   }

   public OutputStream open(OutputStream var1, boolean var2, OutputStream var3) throws IOException {
      return this.open(CMSObjectIdentifiers.data, var1, var2, var3);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, boolean var3) throws IOException {
      return this.open(var1, var2, var3, null);
   }

   public OutputStream open(ASN1ObjectIdentifier var1, OutputStream var2, boolean var3, OutputStream var4) throws IOException {
      HashSet var5 = new HashSet();
      var5.addAll(this.extraDigestAlgorithms);

      for (SignerInformation var7 : this._signers) {
         CMSUtils.addDigestAlgs(var5, var7, this.digestAlgIdFinder);
      }

      for (SignerInfoGenerator var15 : this.signerGens) {
         var5.add(CMSSignedHelper.INSTANCE.fixDigestAlgID(var15.getDigestAlgorithm(), this.digestAlgIdFinder));
      }

      if ("BER".equals(this.encoding)) {
         BERSequenceGenerator var14 = new BERSequenceGenerator(var2);
         var14.addObject(CMSObjectIdentifiers.signedData);
         BERSequenceGenerator var17 = new BERSequenceGenerator(var14.getRawOutputStream(), 0, true);
         var17.addObject(this.calculateVersion(var1));
         var17.addObject(CMSUtils.convertToDlSet(var5));
         BERSequenceGenerator var18 = new BERSequenceGenerator(var17.getRawOutputStream());
         var18.addObject(var1);
         OutputStream var19 = var3 ? CMSUtils.createBEROctetOutputStream(var18.getRawOutputStream(), 0, true, this._bufferSize) : null;
         OutputStream var20 = CMSUtils.getSafeTeeOutputStream(var4, var19);
         OutputStream var21 = CMSUtils.attachSignersToOutputStream(this.signerGens, var20);
         return new CMSSignedDataStreamGenerator.CmsSignedDataOutputStream(var21, var1, var14, var17, var18);
      } else {
         ASN1EncodableVector var13 = new ASN1EncodableVector();
         var13.add(CMSObjectIdentifiers.signedData);
         ASN1EncodableVector var16 = new ASN1EncodableVector();
         var16.add(this.calculateVersion(var1));
         var16.add(CMSUtils.convertToDlSet(var5));
         ASN1EncodableVector var8 = new ASN1EncodableVector();
         var8.add(var1);
         ByteArrayOutputStream var9 = var3 ? new ByteArrayOutputStream() : null;
         OutputStream var10 = CMSUtils.getSafeTeeOutputStream(var4, var9);
         OutputStream var11 = CMSUtils.attachSignersToOutputStream(this.signerGens, var10);
         return new CMSSignedDataStreamGenerator.CmsDLSignedDataOutputStream(var11, var1, var16, var8, var9, var2);
      }
   }

   public List<AlgorithmIdentifier> getDigestAlgorithms() {
      ArrayList var1 = new ArrayList();

      for (SignerInformation var3 : this._signers) {
         AlgorithmIdentifier var4 = CMSSignedHelper.INSTANCE.fixDigestAlgID(var3.getDigestAlgorithmID(), this.digestAlgIdFinder);
         var1.add(var4);
      }

      for (SignerInfoGenerator var6 : this.signerGens) {
         var1.add(var6.getDigestAlgorithm());
      }

      return var1;
   }

   private ASN1Integer calculateVersion(ASN1ObjectIdentifier var1) {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      if (this.certs != null) {
         for (Object var7 : this.certs) {
            if (var7 instanceof ASN1TaggedObject) {
               ASN1TaggedObject var8 = (ASN1TaggedObject)var7;
               if (var8.getTagNo() == 1) {
                  var4 = true;
               } else if (var8.getTagNo() == 2) {
                  var5 = true;
               } else if (var8.getTagNo() == 3) {
                  var2 = true;
               }
            }
         }
      }

      if (var2) {
         return ASN1Integer.FIVE;
      }

      if (this.crls != null) {
         for (Object var10 : this.crls) {
            if (var10 instanceof ASN1TaggedObject) {
               var3 = true;
            }
         }
      }

      if (var3) {
         return ASN1Integer.FIVE;
      } else if (var5) {
         return ASN1Integer.FOUR;
      } else if (var4) {
         return ASN1Integer.THREE;
      } else if (checkForVersion3(this._signers, this.signerGens)) {
         return ASN1Integer.THREE;
      } else {
         return !CMSObjectIdentifiers.data.equals(var1) ? ASN1Integer.THREE : ASN1Integer.ONE;
      }
   }

   private static boolean checkForVersion3(List var0, List var1) {
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         SignerInfo var3 = ((SignerInformation)var2.next()).toASN1Structure();
         if (var3.getVersion().hasValue(3)) {
            return true;
         }
      }

      for (SignerInfoGenerator var5 : var1) {
         if (var5.getGeneratedVersion() == 3) {
            return true;
         }
      }

      return false;
   }

   private class CmsDLSignedDataOutputStream extends OutputStream {
      private OutputStream _out;
      private ASN1ObjectIdentifier _contentOID;
      private ASN1EncodableVector _sigGen;
      private ASN1EncodableVector _eiGen;
      private ByteArrayOutputStream _ecStream;
      private OutputStream _output;

      public CmsDLSignedDataOutputStream(
         OutputStream nullx,
         ASN1ObjectIdentifier nullxx,
         ASN1EncodableVector nullxxx,
         ASN1EncodableVector nullxxxx,
         ByteArrayOutputStream nullxxxxx,
         OutputStream nullxxxxxx
      ) {
         this._out = nullx;
         this._contentOID = nullxx;
         this._sigGen = nullxxx;
         this._eiGen = nullxxxx;
         this._ecStream = nullxxxxx;
         this._output = nullxxxxxx;
      }

      @Override
      public void write(int var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this._out.write(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void close() throws IOException {
         this._out.close();
         if (this._ecStream != null) {
            this._eiGen.add(new DERTaggedObject(true, 0, new DEROctetString(this._ecStream.toByteArray())));
         }

         CMSSignedDataStreamGenerator.this.digests.clear();
         this._sigGen.add(new DLSequence(this._eiGen));
         boolean var1 = "DER".equals(CMSSignedDataStreamGenerator.this.encoding);
         if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
            ASN1Set var2 = var1
               ? CMSUtils.createDerSetFromList(CMSSignedDataStreamGenerator.this.certs)
               : CMSUtils.createDlSetFromList(CMSSignedDataStreamGenerator.this.certs);
            this._sigGen.add(new DERTaggedObject(false, 0, var2));
         }

         if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
            ASN1Set var7 = var1
               ? CMSUtils.createDerSetFromList(CMSSignedDataStreamGenerator.this.crls)
               : CMSUtils.createDlSetFromList(CMSSignedDataStreamGenerator.this.crls);
            this._sigGen.add(new DERTaggedObject(false, 1, var7));
         }

         ASN1EncodableVector var8 = new ASN1EncodableVector();

         for (SignerInfoGenerator var4 : CMSSignedDataStreamGenerator.this.signerGens) {
            try {
               var8.add(var4.generate(this._contentOID));
               byte[] var5 = var4.getCalculatedDigest();
               CMSSignedDataStreamGenerator.this.digests.put(var4.getDigestAlgorithm().getAlgorithm().getId(), var5);
            } catch (CMSException var6) {
               throw new CMSStreamException("exception generating signers: " + var6.getMessage(), var6);
            }
         }

         for (SignerInformation var11 : CMSSignedDataStreamGenerator.this._signers) {
            var8.add(var11.toASN1Structure());
         }

         this._sigGen.add(var1 ? new DERSet(var8) : new DLSet(var8));
         ContentInfo var10 = new ContentInfo(CMSObjectIdentifiers.signedData, new DLSequence(this._sigGen));
         this._output.write(var10.getEncoded(CMSSignedDataStreamGenerator.this.encoding));
      }
   }

   private class CmsSignedDataOutputStream extends OutputStream {
      private OutputStream _out;
      private ASN1ObjectIdentifier _contentOID;
      private BERSequenceGenerator _sGen;
      private BERSequenceGenerator _sigGen;
      private BERSequenceGenerator _eiGen;

      public CmsSignedDataOutputStream(
         OutputStream nullx, ASN1ObjectIdentifier nullxx, BERSequenceGenerator nullxxx, BERSequenceGenerator nullxxxx, BERSequenceGenerator nullxxxxx
      ) {
         this._out = nullx;
         this._contentOID = nullxx;
         this._sGen = nullxxx;
         this._sigGen = nullxxxx;
         this._eiGen = nullxxxxx;
      }

      @Override
      public void write(int var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this._out.write(var1, var2, var3);
      }

      @Override
      public void write(byte[] var1) throws IOException {
         this._out.write(var1);
      }

      @Override
      public void close() throws IOException {
         this._out.close();
         this._eiGen.close();
         CMSSignedDataStreamGenerator.this.digests.clear();
         if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
            ASN1Set var1 = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs);
            this._sigGen.addObject(new BERTaggedObject(false, 0, var1));
         }

         if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
            ASN1Set var6 = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls);
            this._sigGen.addObject(new BERTaggedObject(false, 1, var6));
         }

         ASN1EncodableVector var7 = new ASN1EncodableVector();

         for (SignerInfoGenerator var3 : CMSSignedDataStreamGenerator.this.signerGens) {
            try {
               var7.add(var3.generate(this._contentOID));
               byte[] var4 = var3.getCalculatedDigest();
               CMSSignedDataStreamGenerator.this.digests.put(var3.getDigestAlgorithm().getAlgorithm().getId(), var4);
            } catch (CMSException var5) {
               throw new CMSStreamException("exception generating signers: " + var5.getMessage(), var5);
            }
         }

         for (SignerInformation var9 : CMSSignedDataStreamGenerator.this._signers) {
            var7.add(var9.toASN1Structure());
         }

         this._sigGen.addObject(new DLSet(var7));
         this._sigGen.close();
         this._sGen.close();
      }
   }
}
