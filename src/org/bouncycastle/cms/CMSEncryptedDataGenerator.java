package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EncryptedData;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEncryptedDataGenerator extends CMSEncryptedGenerator {
   private CMSEncryptedData doGenerate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();

      try {
         OutputStream var4 = var2.getOutputStream(var3);
         var1.write(var4);
         var4.close();
      } catch (IOException var9) {
         throw new CMSException("");
      }

      BEROctetString var10 = new BEROctetString(var3.toByteArray());
      EncryptedContentInfo var5 = CMSUtils.getEncryptedContentInfo(var1, var2, var10);
      ASN1Set var6 = CMSUtils.getAttrBERSet(this.unprotectedAttributeGenerator);
      EncryptedData var7 = new EncryptedData(var5, var6);
      ContentInfo var8 = new ContentInfo(CMSObjectIdentifiers.encryptedData, var7);
      return new CMSEncryptedData(var8);
   }

   public CMSEncryptedData generate(CMSTypedData var1, OutputEncryptor var2) throws CMSException {
      return this.doGenerate(var1, var2);
   }
}
