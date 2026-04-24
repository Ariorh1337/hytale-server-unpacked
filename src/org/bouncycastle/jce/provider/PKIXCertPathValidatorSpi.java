package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.CertificateEncodingException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import org.bouncycastle.x509.ExtendedPKIXParameters;

public class PKIXCertPathValidatorSpi extends CertPathValidatorSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();
   private final boolean isForCRLCheck;

   public PKIXCertPathValidatorSpi() {
      this(false);
   }

   public PKIXCertPathValidatorSpi(boolean var1) {
      this.isForCRLCheck = var1;
   }

   @Override
   public CertPathValidatorResult engineValidate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException {
      PKIXExtendedParameters var3;
      if (var2 instanceof PKIXParameters) {
         PKIXExtendedParameters.Builder var4 = new PKIXExtendedParameters.Builder((PKIXParameters)var2);
         if (var2 instanceof ExtendedPKIXParameters) {
            ExtendedPKIXParameters var5 = (ExtendedPKIXParameters)var2;
            var4.setUseDeltasEnabled(var5.isUseDeltasEnabled());
            var4.setValidityModel(var5.getValidityModel());
         }

         var3 = var4.build();
      } else if (var2 instanceof PKIXExtendedBuilderParameters) {
         var3 = ((PKIXExtendedBuilderParameters)var2).getBaseParameters();
      } else {
         if (!(var2 instanceof PKIXExtendedParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be a " + PKIXParameters.class.getName() + " instance.");
         }

         var3 = (PKIXExtendedParameters)var2;
      }

      if (var3.getTrustAnchors() == null) {
         throw new InvalidAlgorithmParameterException("trustAnchors is null, this is not allowed for certification path validation.");
      }

      List var38 = var1.getCertificates();
      int var39 = var38.size();
      if (var38.isEmpty()) {
         throw new CertPathValidatorException("Certification path is empty.", null, var1, -1);
      }

      Date var6 = new Date();
      Date var7 = CertPathValidatorUtilities.getValidityDate(var3, var6);
      Set var8 = var3.getInitialPolicies();

      TrustAnchor var9;
      try {
         var9 = CertPathValidatorUtilities.findTrustAnchor((X509Certificate)var38.get(var38.size() - 1), var3.getTrustAnchors(), var3.getSigProvider());
         if (var9 == null) {
            throw new CertPathValidatorException("Trust anchor for certification path not found.", null, var1, -1);
         }

         checkCertificate(var9.getTrustedCert());
      } catch (AnnotatedException var36) {
         throw new CertPathValidatorException(var36.getMessage(), var36.getUnderlyingException(), var1, var38.size() - 1);
      }

      var3 = new PKIXExtendedParameters.Builder(var3).setTrustAnchor(var9).build();
      int var11 = 0;
      ArrayList[] var13 = new ArrayList[var39 + 1];

      for (int var14 = 0; var14 < var13.length; var14++) {
         var13[var14] = new ArrayList();
      }

      HashSet var41 = new HashSet();
      var41.add("2.5.29.32.0");
      PKIXPolicyNode var15 = new PKIXPolicyNode(new ArrayList(), 0, var41, null, new HashSet(), "2.5.29.32.0", false);
      var13[0].add(var15);
      PKIXNameConstraintValidator var16 = new PKIXNameConstraintValidator();
      HashSet var18 = new HashSet();
      int var17;
      if (var3.isExplicitPolicyRequired()) {
         var17 = 0;
      } else {
         var17 = var39 + 1;
      }

      int var19;
      if (var3.isAnyPolicyInhibited()) {
         var19 = 0;
      } else {
         var19 = var39 + 1;
      }

      int var20;
      if (var3.isPolicyMappingInhibited()) {
         var20 = 0;
      } else {
         var20 = var39 + 1;
      }

      X509Certificate var23 = var9.getTrustedCert();

      PublicKey var21;
      X500Name var22;
      try {
         if (var23 != null) {
            var22 = PrincipalUtils.getSubjectPrincipal(var23);
            var21 = var23.getPublicKey();
         } else {
            var22 = PrincipalUtils.getCA(var9);
            var21 = var9.getCAPublicKey();
         }
      } catch (RuntimeException var35) {
         throw new ExtCertPathValidatorException("Subject of trust anchor could not be (re)encoded.", var35, var1, -1);
      }

      Object var24 = null;

      try {
         var24 = CertPathValidatorUtilities.getAlgorithmIdentifier(var21);
      } catch (CertPathValidatorException var34) {
         throw new ExtCertPathValidatorException("Algorithm identifier of public key of trust anchor could not be read.", var34, var1, -1);
      }

      int var25 = var39;
      if (var3.getTargetConstraints() != null && !var3.getTargetConstraints().match((X509Certificate)var38.get(0))) {
         throw new ExtCertPathValidatorException("Target certificate in certification path does not match targetConstraints.", null, var1, 0);
      }

      List var26 = var3.getCertPathCheckers();
      Iterator var10 = var26.iterator();

      while (var10.hasNext()) {
         ((PKIXCertPathChecker)var10.next()).init(false);
      }

      ProvCrlRevocationChecker var27;
      if (var3.isRevocationEnabled()) {
         var27 = new ProvCrlRevocationChecker(this.helper);
      } else {
         var27 = null;
      }

      X509Certificate var28 = null;

      for (var11 = var38.size() - 1; var11 >= 0; var11--) {
         int var12 = var39 - var11;
         var28 = (X509Certificate)var38.get(var11);
         boolean var29 = var11 == var38.size() - 1;

         try {
            checkCertificate(var28);
         } catch (AnnotatedException var32) {
            throw new CertPathValidatorException(var32.getMessage(), var32.getUnderlyingException(), var1, var11);
         }

         RFC3280CertPathUtilities.processCertA(var1, var3, var7, var27, var11, var21, var29, var22, var23);
         RFC3280CertPathUtilities.processCertBC(var1, var11, var16, this.isForCRLCheck);
         PKIXPolicyNode var42 = RFC3280CertPathUtilities.processCertD(var1, var11, var18, var15, var13, var19, this.isForCRLCheck);
         var15 = RFC3280CertPathUtilities.processCertE(var1, var11, var42);
         RFC3280CertPathUtilities.processCertF(var1, var11, var15, var17);
         if (var12 != var39) {
            if (var28 != null && var28.getVersion() == 1) {
               if (var12 != 1 || !var28.equals(var9.getTrustedCert())) {
                  throw new CertPathValidatorException("Version 1 certificates can't be used as CA ones.", null, var1, var11);
               }
            } else {
               RFC3280CertPathUtilities.prepareNextCertA(var1, var11);
               var15 = RFC3280CertPathUtilities.prepareCertB(var1, var11, var13, var15, var20);
               RFC3280CertPathUtilities.prepareNextCertG(var1, var11, var16);
               var17 = RFC3280CertPathUtilities.prepareNextCertH1(var1, var11, var17);
               var20 = RFC3280CertPathUtilities.prepareNextCertH2(var1, var11, var20);
               var19 = RFC3280CertPathUtilities.prepareNextCertH3(var1, var11, var19);
               var17 = RFC3280CertPathUtilities.prepareNextCertI1(var1, var11, var17);
               var20 = RFC3280CertPathUtilities.prepareNextCertI2(var1, var11, var20);
               var19 = RFC3280CertPathUtilities.prepareNextCertJ(var1, var11, var19);
               RFC3280CertPathUtilities.prepareNextCertK(var1, var11);
               var25 = RFC3280CertPathUtilities.prepareNextCertL(var1, var11, var25);
               var25 = RFC3280CertPathUtilities.prepareNextCertM(var1, var11, var25);
               RFC3280CertPathUtilities.prepareNextCertN(var1, var11);
               HashSet var30 = var28.getCriticalExtensionOIDs();
               if (var30 != null) {
                  var30 = new HashSet(var30);
                  var30.remove(RFC3280CertPathUtilities.KEY_USAGE);
                  var30.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
                  var30.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
                  var30.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
                  var30.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
                  var30.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
                  var30.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
                  var30.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
                  var30.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
                  var30.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
               } else {
                  var30 = new HashSet();
               }

               RFC3280CertPathUtilities.prepareNextCertO(var1, var11, var30, var26);
               var23 = var28;
               var22 = PrincipalUtils.getSubjectPrincipal(var23);

               try {
                  var21 = CertPathValidatorUtilities.getNextWorkingKey(var1.getCertificates(), var11, this.helper);
               } catch (CertPathValidatorException var33) {
                  throw new CertPathValidatorException("Next working key could not be retrieved.", var33, var1, var11);
               }

               var24 = CertPathValidatorUtilities.getAlgorithmIdentifier(var21);
            }
         }
      }

      var17 = RFC3280CertPathUtilities.wrapupCertA(var17, var28);
      var17 = RFC3280CertPathUtilities.wrapupCertB(var1, var11 + 1, var17);
      HashSet var51 = var28.getCriticalExtensionOIDs();
      if (var51 != null) {
         var51 = new HashSet(var51);
         var51.remove(RFC3280CertPathUtilities.KEY_USAGE);
         var51.remove(RFC3280CertPathUtilities.CERTIFICATE_POLICIES);
         var51.remove(RFC3280CertPathUtilities.POLICY_MAPPINGS);
         var51.remove(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY);
         var51.remove(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT);
         var51.remove(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR);
         var51.remove(RFC3280CertPathUtilities.POLICY_CONSTRAINTS);
         var51.remove(RFC3280CertPathUtilities.BASIC_CONSTRAINTS);
         var51.remove(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME);
         var51.remove(RFC3280CertPathUtilities.NAME_CONSTRAINTS);
         var51.remove(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS);
         var51.remove(Extension.extendedKeyUsage.getId());
      } else {
         var51 = new HashSet();
      }

      RFC3280CertPathUtilities.wrapupCertF(var1, var11 + 1, var26, var51);
      PKIXPolicyNode var54 = RFC3280CertPathUtilities.wrapupCertG(var1, var3, var8, var11 + 1, var13, var15, var18);
      if (var17 <= 0 && var54 == null) {
         throw new CertPathValidatorException("Path processing failed on policy.", null, var1, var11);
      } else {
         return new PKIXCertPathValidatorResult(var9, var54, var28.getPublicKey());
      }
   }

   static void checkCertificate(X509Certificate var0) throws AnnotatedException {
      if (var0 instanceof BCX509Certificate) {
         RuntimeException var1 = null;

         try {
            if (null != ((BCX509Certificate)var0).getTBSCertificateNative()) {
               return;
            }
         } catch (RuntimeException var3) {
            var1 = var3;
         }

         throw new AnnotatedException("unable to process TBSCertificate", var1);
      } else {
         try {
            TBSCertificate.getInstance(var0.getTBSCertificate());
         } catch (CertificateEncodingException var4) {
            throw new AnnotatedException("unable to process TBSCertificate", var4);
         } catch (IllegalArgumentException var5) {
            throw new AnnotatedException(var5.getMessage());
         }
      }
   }
}
