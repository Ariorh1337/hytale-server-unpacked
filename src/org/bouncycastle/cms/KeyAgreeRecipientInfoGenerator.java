package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.GenericKey;

public abstract class KeyAgreeRecipientInfoGenerator implements RecipientInfoGenerator {
   private final ASN1ObjectIdentifier keyAgreementOID;
   private final ASN1ObjectIdentifier keyEncryptionOID;
   private final SubjectPublicKeyInfo originatorKeyInfo;

   protected KeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier var1, SubjectPublicKeyInfo var2, ASN1ObjectIdentifier var3) {
      this.originatorKeyInfo = var2;
      this.keyAgreementOID = var1;
      this.keyEncryptionOID = var3;
   }

   @Override
   public RecipientInfo generate(GenericKey var1) throws CMSException {
      OriginatorPublicKey var2 = this.createOriginatorPublicKey(this.originatorKeyInfo);
      OriginatorIdentifierOrKey var3 = new OriginatorIdentifierOrKey(var2);
      ASN1Object var4 = null;
      if (CMSUtils.isDES(this.keyEncryptionOID) || PKCSObjectIdentifiers.id_alg_CMSRC2wrap.equals(this.keyEncryptionOID)) {
         var4 = DERNull.INSTANCE;
      } else if (CMSUtils.isGOST(this.keyAgreementOID)) {
         var4 = new Gost2814789KeyWrapParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet);
      }

      AlgorithmIdentifier var5 = new AlgorithmIdentifier(this.keyEncryptionOID, var4);
      AlgorithmIdentifier var6 = new AlgorithmIdentifier(this.keyAgreementOID, var5);
      ASN1Sequence var7 = this.generateRecipientEncryptedKeys(var6, var5, var1);
      DEROctetString var8 = DEROctetString.fromContentsOptional(this.getUserKeyingMaterial(var6));
      return new RecipientInfo(new KeyAgreeRecipientInfo(var3, var8, var6, var7));
   }

   protected OriginatorPublicKey createOriginatorPublicKey(SubjectPublicKeyInfo var1) {
      return new OriginatorPublicKey(var1.getAlgorithm(), var1.getPublicKeyData());
   }

   protected abstract ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier var1, AlgorithmIdentifier var2, GenericKey var3) throws CMSException;

   protected abstract byte[] getUserKeyingMaterial(AlgorithmIdentifier var1) throws CMSException;
}
