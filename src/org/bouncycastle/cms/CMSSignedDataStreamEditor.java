package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1StreamParser;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfoParser;
import org.bouncycastle.asn1.cms.SignedDataParser;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class CMSSignedDataStreamEditor {
   private static final CMSSignedHelper HELPER = CMSSignedHelper.INSTANCE;
   private static final DefaultDigestAlgorithmIdentifierFinder dgstAlgFinder = new DefaultDigestAlgorithmIdentifierFinder();

   public static OutputStream addDigestAlgorithm(
      OutputStream var0, InputStream var1, AlgorithmIdentifier var2, DigestAlgorithmIdentifierFinder var3, DigestCalculatorProvider var4
   ) throws IOException, CMSException {
      ContentInfoParser var5 = new ContentInfoParser((ASN1SequenceParser)new ASN1StreamParser(var1).readObject());
      SignedDataParser var6 = SignedDataParser.getInstance(var5.getContent(16));
      BERSequenceGenerator var7 = new BERSequenceGenerator(var0);
      var7.addObject(CMSObjectIdentifiers.signedData);
      BERSequenceGenerator var8 = new BERSequenceGenerator(var7.getRawOutputStream(), 0, true);
      var8.addObject(var6.getVersion());
      ASN1EncodableVector var9 = new ASN1EncodableVector();
      LinkedHashMap var10 = new LinkedHashMap();

      try {
         Iterator var11 = ((DLSet)var6.getDigestAlgorithms().toASN1Primitive()).iterator();

         while (var11.hasNext()) {
            AlgorithmIdentifier var12 = AlgorithmIdentifier.getInstance(var11.next());
            var9.add(HELPER.fixDigestAlgID(var12, var3));
            var10.put(var12, var4.get(var12));
         }

         if (!var10.containsKey(var2)) {
            var9.add(HELPER.fixDigestAlgID(var2, var3));
            var10.put(var2, var4.get(var2));
         }
      } catch (OperatorCreationException var13) {
         throw new CMSException("unable to find digest algorithm");
      }

      var8.addObject(new DERSet(var9));
      CMSSignedDataParser.writeEncapContentInfoToGenerator(var6, var8);
      CMSSignedDataParser.writeSetToGeneratorTagged(var8, var6.getCertificates(), 0);
      CMSSignedDataParser.writeSetToGeneratorTagged(var8, var6.getCrls(), 1);
      var8.addObject(var6.getSignerInfos());
      var8.close();
      var7.close();
      return var0;
   }

   public static OutputStream addDigestAlgorithm(OutputStream var0, InputStream var1, AlgorithmIdentifier var2, DigestCalculatorProvider var3) throws IOException, CMSException {
      return addDigestAlgorithm(var0, var1, var2, dgstAlgFinder, var3);
   }
}
