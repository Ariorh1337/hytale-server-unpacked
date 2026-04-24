package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthEnvelopedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.operator.OutputAEADEncryptor;

public class CMSAuthEnvelopedDataGenerator extends CMSAuthEnvelopedGenerator {
   private CMSAuthEnvelopedData doGenerate(CMSTypedData var1, OutputAEADEncryptor var2) throws CMSException {
      ASN1EncodableVector var3 = CMSUtils.getRecipentInfos(var2.getKey(), this.recipientInfoGenerators);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();

      ASN1Set var5;
      try {
         OutputStream var6 = var2.getOutputStream(var4);
         if (CMSAlgorithm.ChaCha20Poly1305.equals(var2.getAlgorithmIdentifier().getAlgorithm())) {
            var5 = CMSUtils.processAuthAttrSet(this.authAttrsGenerator, var2);
            var1.write(var6);
         } else {
            var1.write(var6);
            var5 = CMSUtils.processAuthAttrSet(this.authAttrsGenerator, var2);
         }

         var6.close();
      } catch (IOException var12) {
         throw new CMSException("unable to process authenticated content: " + var12.getMessage(), var12);
      }

      BEROctetString var13 = new BEROctetString(var4.toByteArray());
      DEROctetString var7 = new DEROctetString(var2.getMAC());
      EncryptedContentInfo var8 = CMSUtils.getEncryptedContentInfo(var1, var2, var13);
      ASN1Set var9 = CMSUtils.getAttrDLSet(this.unauthAttrsGenerator);
      AuthEnvelopedData var10 = new AuthEnvelopedData(this.originatorInfo, new DERSet(var3), var8, var5, var7, var9);
      ContentInfo var11 = new ContentInfo(CMSObjectIdentifiers.authEnvelopedData, var10);
      return new CMSAuthEnvelopedData(var11);
   }

   public CMSAuthEnvelopedData generate(CMSTypedData var1, OutputAEADEncryptor var2) throws CMSException {
      return this.doGenerate(var1, var2);
   }
}
