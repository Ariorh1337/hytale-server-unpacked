package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKIXNameConstraintValidator implements NameConstraintValidator {
   private Set excludedSubtreesDN = new HashSet();
   private Set excludedSubtreesDNS = new HashSet();
   private Set excludedSubtreesEmail = new HashSet();
   private Set excludedSubtreesURI = new HashSet();
   private Set excludedSubtreesIP = new HashSet();
   private Set excludedSubtreesOtherName = new HashSet();
   private Set permittedSubtreesDN;
   private Set permittedSubtreesDNS;
   private Set permittedSubtreesEmail;
   private Set permittedSubtreesURI;
   private Set permittedSubtreesIP;
   private Set permittedSubtreesOtherName;

   @Override
   public void checkPermitted(GeneralName var1) throws NameConstraintValidatorException {
      ASN1Encodable var2 = var1.getName();
      switch (var1.getTagNo()) {
         case 0:
            checkPermittedOtherName(this.permittedSubtreesOtherName, OtherName.getInstance(var2));
            break;
         case 1:
            this.checkPermittedEmail(extractNameAsString(var2));
            break;
         case 2:
            checkPermittedDNS(this.permittedSubtreesDNS, extractNameAsString(var2));
         case 3:
         case 5:
         default:
            break;
         case 4:
            this.checkPermittedDN(X500Name.getInstance(var2));
            break;
         case 6:
            checkPermittedURI(this.permittedSubtreesURI, extractNameAsString(var2));
            break;
         case 7:
            checkPermittedIP(this.permittedSubtreesIP, ASN1OctetString.getInstance(var2).getOctets());
      }
   }

   @Override
   public void checkExcluded(GeneralName var1) throws NameConstraintValidatorException {
      ASN1Encodable var2 = var1.getName();
      switch (var1.getTagNo()) {
         case 0:
            checkExcludedOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(var2));
            break;
         case 1:
            this.checkExcludedEmail(extractNameAsString(var2));
            break;
         case 2:
            checkExcludedDNS(this.excludedSubtreesDNS, extractNameAsString(var2));
         case 3:
         case 5:
         default:
            break;
         case 4:
            this.checkExcludedDN(X500Name.getInstance(var2));
            break;
         case 6:
            checkExcludedURI(this.excludedSubtreesURI, extractNameAsString(var2));
            break;
         case 7:
            checkExcludedIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(var2).getOctets());
      }
   }

   @Override
   public void intersectPermittedSubtree(GeneralSubtree var1) {
      this.intersectPermittedSubtree(new GeneralSubtree[]{var1});
   }

   @Override
   public void intersectPermittedSubtree(GeneralSubtree[] var1) {
      HashMap var2 = new HashMap();

      for (int var3 = 0; var3 != var1.length; var3++) {
         GeneralSubtree var4 = var1[var3];
         Integer var5 = Integers.valueOf(var4.getBase().getTagNo());
         Set var6 = (Set)var2.get(var5);
         if (var6 == null) {
            var6 = new HashSet();
            var2.put(var5, var6);
         }

         var6.add(var4);
      }

      for (Entry var8 : var2.entrySet()) {
         int var9 = (Integer)var8.getKey();
         Set var10 = (Set)var8.getValue();
         switch (var9) {
            case 0:
               this.permittedSubtreesOtherName = intersectOtherName(this.permittedSubtreesOtherName, var10);
               break;
            case 1:
               this.permittedSubtreesEmail = intersectEmail(this.permittedSubtreesEmail, var10);
               break;
            case 2:
               this.permittedSubtreesDNS = intersectDNS(this.permittedSubtreesDNS, var10);
               break;
            case 3:
            case 5:
            default:
               throw new IllegalStateException("Unknown tag encountered: " + var9);
            case 4:
               this.permittedSubtreesDN = intersectDN(this.permittedSubtreesDN, var10);
               break;
            case 6:
               this.permittedSubtreesURI = intersectURI(this.permittedSubtreesURI, var10);
               break;
            case 7:
               this.permittedSubtreesIP = intersectIP(this.permittedSubtreesIP, var10);
         }
      }
   }

   @Override
   public void intersectEmptyPermittedSubtree(int var1) {
      switch (var1) {
         case 0:
            this.permittedSubtreesOtherName = new HashSet();
            break;
         case 1:
            this.permittedSubtreesEmail = new HashSet();
            break;
         case 2:
            this.permittedSubtreesDNS = new HashSet();
            break;
         case 3:
         case 5:
         default:
            throw new IllegalStateException("Unknown tag encountered: " + var1);
         case 4:
            this.permittedSubtreesDN = new HashSet();
            break;
         case 6:
            this.permittedSubtreesURI = new HashSet();
            break;
         case 7:
            this.permittedSubtreesIP = new HashSet();
      }
   }

   @Override
   public void addExcludedSubtree(GeneralSubtree var1) {
      GeneralName var2 = var1.getBase();
      ASN1Encodable var3 = var2.getName();
      switch (var2.getTagNo()) {
         case 0:
            this.excludedSubtreesOtherName = unionOtherName(this.excludedSubtreesOtherName, OtherName.getInstance(var3));
            break;
         case 1:
            this.excludedSubtreesEmail = unionEmail(this.excludedSubtreesEmail, extractNameAsString(var3));
            break;
         case 2:
            this.excludedSubtreesDNS = unionDNS(this.excludedSubtreesDNS, extractNameAsString(var3));
            break;
         case 3:
         case 5:
         default:
            throw new IllegalStateException("Unknown tag encountered: " + var2.getTagNo());
         case 4:
            this.excludedSubtreesDN = unionDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(var3));
            break;
         case 6:
            this.excludedSubtreesURI = unionURI(this.excludedSubtreesURI, extractNameAsString(var3));
            break;
         case 7:
            this.excludedSubtreesIP = unionIP(this.excludedSubtreesIP, ASN1OctetString.getInstance(var3).getOctets());
      }
   }

   @Override
   public int hashCode() {
      return hashCollection(this.excludedSubtreesDN)
         + hashCollection(this.excludedSubtreesDNS)
         + hashCollection(this.excludedSubtreesEmail)
         + hashCollection(this.excludedSubtreesIP)
         + hashCollection(this.excludedSubtreesURI)
         + hashCollection(this.excludedSubtreesOtherName)
         + hashCollection(this.permittedSubtreesDN)
         + hashCollection(this.permittedSubtreesDNS)
         + hashCollection(this.permittedSubtreesEmail)
         + hashCollection(this.permittedSubtreesIP)
         + hashCollection(this.permittedSubtreesURI)
         + hashCollection(this.permittedSubtreesOtherName);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PKIXNameConstraintValidator)) {
         return false;
      }

      PKIXNameConstraintValidator var2 = (PKIXNameConstraintValidator)var1;
      return collectionsAreEqual(var2.excludedSubtreesDN, this.excludedSubtreesDN)
         && collectionsAreEqual(var2.excludedSubtreesDNS, this.excludedSubtreesDNS)
         && collectionsAreEqual(var2.excludedSubtreesEmail, this.excludedSubtreesEmail)
         && collectionsAreEqual(var2.excludedSubtreesIP, this.excludedSubtreesIP)
         && collectionsAreEqual(var2.excludedSubtreesURI, this.excludedSubtreesURI)
         && collectionsAreEqual(var2.excludedSubtreesOtherName, this.excludedSubtreesOtherName)
         && collectionsAreEqual(var2.permittedSubtreesDN, this.permittedSubtreesDN)
         && collectionsAreEqual(var2.permittedSubtreesDNS, this.permittedSubtreesDNS)
         && collectionsAreEqual(var2.permittedSubtreesEmail, this.permittedSubtreesEmail)
         && collectionsAreEqual(var2.permittedSubtreesIP, this.permittedSubtreesIP)
         && collectionsAreEqual(var2.permittedSubtreesURI, this.permittedSubtreesURI)
         && collectionsAreEqual(var2.permittedSubtreesOtherName, this.permittedSubtreesOtherName);
   }

   public void checkPermittedDN(X500Name var1) throws NameConstraintValidatorException {
      checkPermittedDN(this.permittedSubtreesDN, ASN1Sequence.getInstance(var1));
   }

   public void checkExcludedDN(X500Name var1) throws NameConstraintValidatorException {
      checkExcludedDN(this.excludedSubtreesDN, ASN1Sequence.getInstance(var1));
   }

   public void checkPermittedEmail(String var1) throws NameConstraintValidatorException {
      checkPermittedEmail(this.permittedSubtreesEmail, var1);
   }

   public void checkExcludedEmail(String var1) throws NameConstraintValidatorException {
      checkExcludedEmail(this.excludedSubtreesEmail, var1);
   }

   private static boolean withinDNSubtree(ASN1Sequence var0, ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= var0.size()) {
         int var2 = 0;
         RDN var3 = RDN.getInstance(var1.getObjectAt(0));

         for (int var4 = 0; var4 < var0.size(); var4++) {
            var2 = var4;
            RDN var5 = RDN.getInstance(var0.getObjectAt(var4));
            if (IETFUtils.rDNAreEqual(var3, var5)) {
               break;
            }
         }

         if (var1.size() > var0.size() - var2) {
            return false;
         }

         for (int var7 = 0; var7 < var1.size(); var7++) {
            RDN var8 = RDN.getInstance(var1.getObjectAt(var7));
            RDN var6 = RDN.getInstance(var0.getObjectAt(var2 + var7));
            if (var8.size() != var6.size()) {
               return false;
            }

            if (!var8.getFirst().getType().equals(var6.getFirst().getType())) {
               return false;
            }

            if (var8.size() == 1 && var8.getFirst().getType().equals(RFC4519Style.serialNumber)) {
               if (!var6.getFirst().getValue().toString().startsWith(var8.getFirst().getValue().toString())) {
                  return false;
               }
            } else if (!IETFUtils.rDNAreEqual(var8, var6)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static void checkPermittedDN(Set var0, ASN1Sequence var1) throws NameConstraintValidatorException {
      if (var0 != null && (!var0.isEmpty() || var1.size() != 0) && !isDNConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("Subject distinguished name is not from a permitted subtree");
      }
   }

   private static void checkExcludedDN(Set var0, ASN1Sequence var1) throws NameConstraintValidatorException {
      if (isDNConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("Subject distinguished name is from an excluded subtree");
      }
   }

   private static boolean isDNConstrained(Set var0, ASN1Sequence var1) {
      for (ASN1Sequence var3 : var0) {
         if (withinDNSubtree(var1, var3)) {
            return true;
         }
      }

      return false;
   }

   private static Set intersectDN(Set var0, Set var1) {
      HashSet var2 = new HashSet();

      for (GeneralSubtree var4 : var1) {
         ASN1Sequence var5 = ASN1Sequence.getInstance(var4.getBase().getName());
         if (var0 != null) {
            for (ASN1Sequence var7 : var0) {
               if (withinDNSubtree(var5, var7)) {
                  var2.add(var5);
               } else if (withinDNSubtree(var7, var5)) {
                  var2.add(var7);
               }
            }
         } else if (var5 != null) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private static Set unionDN(Set var0, ASN1Sequence var1) {
      if (var0.isEmpty()) {
         if (var1 != null) {
            var0.add(var1);
         }

         return var0;
      } else {
         HashSet var2 = new HashSet();
         Iterator var3 = var0.iterator();

         while (var3.hasNext()) {
            ASN1Sequence var4 = ASN1Sequence.getInstance(var3.next());
            if (withinDNSubtree(var1, var4)) {
               var2.add(var4);
            } else if (withinDNSubtree(var4, var1)) {
               var2.add(var1);
            } else {
               var2.add(var4);
               var2.add(var1);
            }
         }

         return var2;
      }
   }

   private static Set intersectOtherName(Set var0, Set var1) {
      HashSet var2 = new HashSet();

      for (GeneralSubtree var4 : var1) {
         OtherName var5 = OtherName.getInstance(var4.getBase().getName());
         if (var5 != null) {
            if (var0 == null) {
               var2.add(var5);
            } else {
               Iterator var6 = var0.iterator();

               while (var6.hasNext()) {
                  OtherName var7 = OtherName.getInstance(var6.next());
                  intersectOtherName(var5, var7, var2);
               }
            }
         }
      }

      return var2;
   }

   private static void intersectOtherName(OtherName var0, OtherName var1, Set var2) {
      if (var0.equals(var1)) {
         var2.add(var0);
      }
   }

   private static Set unionOtherName(Set var0, OtherName var1) {
      HashSet var2 = var0 != null ? new HashSet(var0) : new HashSet();
      var2.add(var1);
      return var2;
   }

   private static Set intersectEmail(Set var0, Set var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         String var4 = extractNameAsString((GeneralSubtree)var3.next());
         if (var0 == null) {
            var2.add(var4);
         } else {
            for (String var6 : var0) {
               intersectEmail(var4, var6, var2);
            }
         }
      }

      return var2;
   }

   private static Set unionEmail(Set var0, String var1) {
      if (var0.isEmpty()) {
         var0.add(var1);
         return var0;
      }

      HashSet var2 = new HashSet();

      for (String var4 : var0) {
         unionEmail(var4, var1, var2);
      }

      return var2;
   }

   private static Set intersectIP(Set var0, Set var1) {
      HashSet var2 = new HashSet();

      for (GeneralSubtree var4 : var1) {
         byte[] var5 = ASN1OctetString.getInstance(var4.getBase().getName()).getOctets();
         if (var0 == null) {
            var2.add(var5);
         } else {
            for (byte[] var7 : var0) {
               byte[] var8 = intersectIPRange(var7, var5);
               if (var8 != null) {
                  var2.add(var8);
               }
            }
         }
      }

      return var2;
   }

   private static Set unionIP(Set var0, byte[] var1) {
      if (var0.isEmpty()) {
         if (var1 != null) {
            var0.add(var1);
         }

         return var0;
      } else {
         HashSet var2 = new HashSet();

         for (byte[] var4 : var0) {
            var2.addAll(unionIPRange(var4, var1));
         }

         return var2;
      }
   }

   private static Set unionIPRange(byte[] var0, byte[] var1) {
      HashSet var2 = new HashSet();
      if (Arrays.areEqual(var0, var1)) {
         var2.add(var0);
      } else {
         var2.add(var0);
         var2.add(var1);
      }

      return var2;
   }

   private static byte[] intersectIPRange(byte[] var0, byte[] var1) {
      if (var0.length != var1.length) {
         return null;
      }

      byte[][] var2 = extractIPsAndSubnetMasks(var0, var1);
      byte[] var3 = var2[0];
      byte[] var4 = var2[1];
      byte[] var5 = var2[2];
      byte[] var6 = var2[3];
      byte[][] var7 = minMaxIPs(var3, var4, var5, var6);
      byte[] var8 = var7[0];
      byte[] var9 = var7[1];
      byte[] var10 = var7[2];
      byte[] var11 = var7[3];
      byte[] var12 = min(var9, var11);
      byte[] var13 = max(var8, var10);
      if (compareTo(var13, var12) == 1) {
         return null;
      }

      byte[] var14 = or(var8, var10);
      byte[] var15 = or(var4, var6);
      return ipWithSubnetMask(var14, var15);
   }

   private static byte[] ipWithSubnetMask(byte[] var0, byte[] var1) {
      return Arrays.concatenate(var0, var1);
   }

   private static byte[][] extractIPsAndSubnetMasks(byte[] var0, byte[] var1) {
      int var2 = var0.length / 2;
      byte[] var3 = new byte[var2];
      byte[] var4 = new byte[var2];
      System.arraycopy(var0, 0, var3, 0, var2);
      System.arraycopy(var0, var2, var4, 0, var2);
      byte[] var5 = new byte[var2];
      byte[] var6 = new byte[var2];
      System.arraycopy(var1, 0, var5, 0, var2);
      System.arraycopy(var1, var2, var6, 0, var2);
      return new byte[][]{var3, var4, var5, var6};
   }

   private static byte[][] minMaxIPs(byte[] var0, byte[] var1, byte[] var2, byte[] var3) {
      int var4 = var0.length;
      byte[] var5 = new byte[var4];
      byte[] var6 = new byte[var4];
      byte[] var7 = new byte[var4];
      byte[] var8 = new byte[var4];

      for (int var9 = 0; var9 < var4; var9++) {
         var5[var9] = (byte)(var0[var9] & var1[var9]);
         var6[var9] = (byte)(var0[var9] & var1[var9] | ~var1[var9]);
         var7[var9] = (byte)(var2[var9] & var3[var9]);
         var8[var9] = (byte)(var2[var9] & var3[var9] | ~var3[var9]);
      }

      return new byte[][]{var5, var6, var7, var8};
   }

   private static void checkPermittedEmail(Set var0, String var1) throws NameConstraintValidatorException {
      if (var0 != null && (var1.length() != 0 || var0.size() != 0) && !isEmailConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("Subject email address is not from a permitted subtree.");
      }
   }

   private static void checkExcludedEmail(Set var0, String var1) throws NameConstraintValidatorException {
      if (isEmailConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("Email address is from an excluded subtree.");
      }
   }

   private static void checkPermittedOtherName(Set var0, OtherName var1) throws NameConstraintValidatorException {
      if (var0 != null && !isOtherNameConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("Subject OtherName is not from a permitted subtree.");
      }
   }

   private static void checkExcludedOtherName(Set var0, OtherName var1) throws NameConstraintValidatorException {
      if (isOtherNameConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("OtherName is from an excluded subtree.");
      }
   }

   private static void checkPermittedIP(Set var0, byte[] var1) throws NameConstraintValidatorException {
      if (var0 != null && (var1.length != 0 || var0.size() != 0) && !isIPConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("IP is not from a permitted subtree.");
      }
   }

   private static void checkExcludedIP(Set var0, byte[] var1) throws NameConstraintValidatorException {
      if (isIPConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("IP is from an excluded subtree.");
      }
   }

   private static boolean isIPConstrained(Set var0, byte[] var1) {
      for (byte[] var3 : var0) {
         if (isIPConstrained(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isIPConstrained(byte[] var0, byte[] var1) {
      int var2 = var1.length;
      if (var2 != var0.length / 2) {
         return false;
      }

      byte[] var3 = new byte[var2];
      System.arraycopy(var0, var2, var3, 0, var2);
      byte[] var4 = new byte[var2];
      byte[] var5 = new byte[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         var4[var6] = (byte)(var0[var6] & var3[var6]);
         var5[var6] = (byte)(var1[var6] & var3[var6]);
      }

      return Arrays.areEqual(var4, var5);
   }

   private static boolean isOtherNameConstrained(Set var0, OtherName var1) {
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         OtherName var3 = OtherName.getInstance(var2.next());
         if (isOtherNameConstrained(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isOtherNameConstrained(OtherName var0, OtherName var1) {
      return var0.equals(var1);
   }

   private static boolean isEmailConstrained(Set var0, String var1) {
      for (String var3 : var0) {
         if (isEmailConstrained(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isEmailConstrained(String var0, String var1) {
      int var2 = var0.indexOf(64);
      if (var2 > 0) {
         return var1.equalsIgnoreCase(var0);
      } else {
         String var3 = var1.substring(var1.indexOf(64) + 1);
         if (var2 == 0) {
            return var3.equalsIgnoreCase(var0.substring(1));
         } else {
            return var0.startsWith(".") ? withinDomain(var3, var0) : var3.equalsIgnoreCase(var0);
         }
      }
   }

   private static boolean withinDomain(String var0, String var1) {
      if (var1.startsWith(".")) {
         var1 = var1.substring(1);
      }

      String[] var2 = Strings.split(var1, '.');
      String[] var3 = Strings.split(var0, '.');
      if (var3.length <= var2.length) {
         return false;
      }

      int var4 = var3.length - var2.length;
      if (var3[var4 - 1].equals("")) {
         return false;
      }

      for (int var5 = 0; var5 < var2.length; var5++) {
         if (!var2[var5].equalsIgnoreCase(var3[var4 + var5])) {
            return false;
         }
      }

      return true;
   }

   private static void checkExcludedDNS(Set var0, String var1) throws NameConstraintValidatorException {
      if (isDNSConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("DNS is from an excluded subtree.");
      }
   }

   private static void checkPermittedDNS(Set var0, String var1) throws NameConstraintValidatorException {
      if (var0 != null && (var1.length() != 0 || var0.size() != 0) && !isDNSConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("DNS is not from a permitted subtree.");
      }
   }

   private static boolean isDNSConstrained(Set var0, String var1) {
      for (String var3 : var0) {
         if (isDNSConstrained(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isDNSConstrained(String var0, String var1) {
      return var1.equalsIgnoreCase(var0) || withinDomain(var1, var0);
   }

   private static void unionEmail(String var0, String var1, Set var2) {
      if (var0.indexOf(64) != -1) {
         String var3 = var0.substring(var0.indexOf(64) + 1);
         if (var1.indexOf(64) != -1) {
            if (var0.equalsIgnoreCase(var1)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (withinDomain(var3, var1)) {
               var2.add(var1);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var3.equalsIgnoreCase(var1)) {
            var2.add(var1);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var0.startsWith(".")) {
         if (var1.indexOf(64) != -1) {
            String var4 = var1.substring(var1.indexOf(64) + 1);
            if (withinDomain(var4, var0)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (isDNSConstrained(var1, var0)) {
               var2.add(var1);
            } else if (withinDomain(var1, var0)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (withinDomain(var1, var0)) {
            var2.add(var0);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var1.indexOf(64) != -1) {
         String var5 = var1.substring(var1.indexOf(64) + 1);
         if (var5.equalsIgnoreCase(var0)) {
            var2.add(var0);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (withinDomain(var0, var1)) {
            var2.add(var1);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var0.equalsIgnoreCase(var1)) {
         var2.add(var0);
      } else {
         var2.add(var0);
         var2.add(var1);
      }
   }

   private static void unionURI(String var0, String var1, Set var2) {
      if (var0.indexOf(64) != -1) {
         String var3 = var0.substring(var0.indexOf(64) + 1);
         if (var1.indexOf(64) != -1) {
            if (var0.equalsIgnoreCase(var1)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (withinDomain(var3, var1)) {
               var2.add(var1);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var3.equalsIgnoreCase(var1)) {
            var2.add(var1);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var0.startsWith(".")) {
         if (var1.indexOf(64) != -1) {
            String var4 = var1.substring(var1.indexOf(64) + 1);
            if (withinDomain(var4, var0)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (isDNSConstrained(var1, var0)) {
               var2.add(var1);
            } else if (withinDomain(var1, var0)) {
               var2.add(var0);
            } else {
               var2.add(var0);
               var2.add(var1);
            }
         } else if (withinDomain(var1, var0)) {
            var2.add(var0);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var1.indexOf(64) != -1) {
         String var5 = var1.substring(var1.indexOf(64) + 1);
         if (var5.equalsIgnoreCase(var0)) {
            var2.add(var0);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (withinDomain(var0, var1)) {
            var2.add(var1);
         } else {
            var2.add(var0);
            var2.add(var1);
         }
      } else if (var0.equalsIgnoreCase(var1)) {
         var2.add(var0);
      } else {
         var2.add(var0);
         var2.add(var1);
      }
   }

   private static Set intersectDNS(Set var0, Set var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         String var4 = extractNameAsString((GeneralSubtree)var3.next());
         if (var0 == null) {
            var2.add(var4);
         } else {
            for (String var6 : var0) {
               if (isDNSConstrained(var4, var6)) {
                  var2.add(var6);
               } else if (withinDomain(var4, var6)) {
                  var2.add(var4);
               }
            }
         }
      }

      return var2;
   }

   private static Set unionDNS(Set var0, String var1) {
      if (var0.isEmpty()) {
         var0.add(var1);
         return var0;
      }

      HashSet var2 = new HashSet();

      for (String var4 : var0) {
         if (isDNSConstrained(var1, var4)) {
            var2.add(var1);
         } else if (withinDomain(var1, var4)) {
            var2.add(var4);
         } else {
            var2.add(var4);
            var2.add(var1);
         }
      }

      return var2;
   }

   private static void intersectEmail(String var0, String var1, Set var2) {
      if (var0.indexOf(64) != -1) {
         String var3 = var0.substring(var0.indexOf(64) + 1);
         if (var1.indexOf(64) != -1) {
            if (var0.equalsIgnoreCase(var1)) {
               var2.add(var0);
            }
         } else if (var1.startsWith(".")) {
            if (withinDomain(var3, var1)) {
               var2.add(var0);
            }
         } else if (var3.equalsIgnoreCase(var1)) {
            var2.add(var0);
         }
      } else if (var0.startsWith(".")) {
         if (var1.indexOf(64) != -1) {
            String var4 = var1.substring(var1.indexOf(64) + 1);
            if (withinDomain(var4, var0)) {
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (isDNSConstrained(var1, var0)) {
               var2.add(var0);
            } else if (withinDomain(var1, var0)) {
               var2.add(var1);
            }
         } else if (withinDomain(var1, var0)) {
            var2.add(var1);
         }
      } else if (var1.indexOf(64) != -1) {
         String var5 = var1.substring(var1.indexOf(64) + 1);
         if (var5.equalsIgnoreCase(var0)) {
            var2.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (withinDomain(var0, var1)) {
            var2.add(var0);
         }
      } else if (var0.equalsIgnoreCase(var1)) {
         var2.add(var0);
      }
   }

   private static void checkExcludedURI(Set var0, String var1) throws NameConstraintValidatorException {
      if (isURIConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("URI is from an excluded subtree.");
      }
   }

   private static Set intersectURI(Set var0, Set var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         String var4 = extractNameAsString((GeneralSubtree)var3.next());
         if (var0 == null) {
            var2.add(var4);
         } else {
            for (String var6 : var0) {
               intersectURI(var6, var4, var2);
            }
         }
      }

      return var2;
   }

   private static Set unionURI(Set var0, String var1) {
      if (var0.isEmpty()) {
         var0.add(var1);
         return var0;
      }

      HashSet var2 = new HashSet();

      for (String var4 : var0) {
         unionURI(var4, var1, var2);
      }

      return var2;
   }

   private static void intersectURI(String var0, String var1, Set var2) {
      if (var0.indexOf(64) != -1) {
         String var3 = var0.substring(var0.indexOf(64) + 1);
         if (var1.indexOf(64) != -1) {
            if (var0.equalsIgnoreCase(var1)) {
               var2.add(var0);
            }
         } else if (var1.startsWith(".")) {
            if (withinDomain(var3, var1)) {
               var2.add(var0);
            }
         } else if (var3.equalsIgnoreCase(var1)) {
            var2.add(var0);
         }
      } else if (var0.startsWith(".")) {
         if (var1.indexOf(64) != -1) {
            String var4 = var1.substring(var1.indexOf(64) + 1);
            if (withinDomain(var4, var0)) {
               var2.add(var1);
            }
         } else if (var1.startsWith(".")) {
            if (isDNSConstrained(var1, var0)) {
               var2.add(var0);
            } else if (withinDomain(var1, var0)) {
               var2.add(var1);
            }
         } else if (withinDomain(var1, var0)) {
            var2.add(var1);
         }
      } else if (var1.indexOf(64) != -1) {
         String var5 = var1.substring(var1.indexOf(64) + 1);
         if (var5.equalsIgnoreCase(var0)) {
            var2.add(var1);
         }
      } else if (var1.startsWith(".")) {
         if (withinDomain(var0, var1)) {
            var2.add(var0);
         }
      } else if (var0.equalsIgnoreCase(var1)) {
         var2.add(var0);
      }
   }

   private static void checkPermittedURI(Set var0, String var1) throws NameConstraintValidatorException {
      if (var0 != null && (var1.length() != 0 || var0.size() != 0) && !isURIConstrained(var0, var1)) {
         throw new NameConstraintValidatorException("URI is not from a permitted subtree.");
      }
   }

   private static boolean isURIConstrained(Set var0, String var1) {
      for (String var3 : var0) {
         if (isURIConstrained(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isURIConstrained(String var0, String var1) {
      String var2 = extractHostFromURL(var1);
      return var0.startsWith(".") ? withinDomain(var2, var0) : var2.equalsIgnoreCase(var0);
   }

   private static String extractHostFromURL(String var0) {
      String var1 = var0.substring(var0.indexOf(58) + 1);
      int var2 = var1.indexOf("//");
      if (var2 != -1) {
         var1 = var1.substring(var2 + 2);
      }

      int var3 = var1.lastIndexOf(58);
      if (var3 != -1) {
         var1 = var1.substring(0, var3);
      }

      var1 = var1.substring(var1.indexOf(58) + 1);
      var1 = var1.substring(var1.indexOf(64) + 1);
      int var4 = var1.indexOf(47);
      if (var4 != -1) {
         var1 = var1.substring(0, var4);
      }

      return var1;
   }

   private static byte[] max(byte[] var0, byte[] var1) {
      return compareTo(var0, var1) > 0 ? var0 : var1;
   }

   private static byte[] min(byte[] var0, byte[] var1) {
      return compareTo(var0, var1) < 0 ? var0 : var1;
   }

   private static int compareTo(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         int var3 = var0[var2] & 255;
         int var4 = var1[var2] & 255;
         if (var3 < var4) {
            return -1;
         }

         if (var3 > var4) {
            return 1;
         }
      }

      return 0;
   }

   private static byte[] or(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length];

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2[var3] = (byte)(var0[var3] | var1[var3]);
      }

      return var2;
   }

   private static int hashCollection(Collection var0) {
      if (var0 == null) {
         return 0;
      }

      int var1 = 0;

      for (Object var3 : var0) {
         if (var3 instanceof byte[]) {
            var1 += Arrays.hashCode((byte[])var3);
         } else {
            var1 += var3.hashCode();
         }
      }

      return var1;
   }

   private static boolean collectionsAreEqual(Collection var0, Collection var1) {
      if (var0 == var1) {
         return true;
      }

      if (var0 != null && var1 != null) {
         if (var0.size() != var1.size()) {
            return false;
         }

         for (Object var3 : var0) {
            Iterator var4 = var1.iterator();
            boolean var5 = false;

            while (var4.hasNext()) {
               Object var6 = var4.next();
               if (equals(var3, var6)) {
                  var5 = true;
                  break;
               }
            }

            if (!var5) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean equals(Object var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 == null || var1 == null) {
         return false;
      } else {
         return var0 instanceof byte[] && var1 instanceof byte[] ? Arrays.areEqual((byte[])var0, (byte[])var1) : var0.equals(var1);
      }
   }

   private static String stringifyIP(byte[] var0) {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < var0.length / 2; var2++) {
         if (var1.length() > 0) {
            var1.append(".");
         }

         var1.append(Integer.toString(var0[var2] & 255));
      }

      var1.append("/");
      boolean var4 = true;

      for (int var3 = var0.length / 2; var3 < var0.length; var3++) {
         if (var4) {
            var4 = false;
         } else {
            var1.append(".");
         }

         var1.append(Integer.toString(var0[var3] & 255));
      }

      return var1.toString();
   }

   private static String stringifyIPCollection(Set var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("[");

      for (Iterator var2 = var0.iterator(); var2.hasNext(); var1.append(stringifyIP((byte[])var2.next()))) {
         if (var1.length() > 1) {
            var1.append(",");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   private static String stringifyOtherNameCollection(Set var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append("[");
      Iterator var2 = var0.iterator();

      while (var2.hasNext()) {
         if (var1.length() > 1) {
            var1.append(",");
         }

         OtherName var3 = OtherName.getInstance(var2.next());
         var1.append(var3.getTypeID().getId());
         var1.append(":");

         try {
            var1.append(Hex.toHexString(var3.getValue().toASN1Primitive().getEncoded()));
         } catch (IOException var5) {
            var1.append(var5.toString());
         }
      }

      var1.append("]");
      return var1.toString();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      addLine(var1, "permitted:");
      if (this.permittedSubtreesDN != null) {
         addLine(var1, "DN:");
         addLine(var1, this.permittedSubtreesDN.toString());
      }

      if (this.permittedSubtreesDNS != null) {
         addLine(var1, "DNS:");
         addLine(var1, this.permittedSubtreesDNS.toString());
      }

      if (this.permittedSubtreesEmail != null) {
         addLine(var1, "Email:");
         addLine(var1, this.permittedSubtreesEmail.toString());
      }

      if (this.permittedSubtreesURI != null) {
         addLine(var1, "URI:");
         addLine(var1, this.permittedSubtreesURI.toString());
      }

      if (this.permittedSubtreesIP != null) {
         addLine(var1, "IP:");
         addLine(var1, stringifyIPCollection(this.permittedSubtreesIP));
      }

      if (this.permittedSubtreesOtherName != null) {
         addLine(var1, "OtherName:");
         addLine(var1, stringifyOtherNameCollection(this.permittedSubtreesOtherName));
      }

      addLine(var1, "excluded:");
      if (!this.excludedSubtreesDN.isEmpty()) {
         addLine(var1, "DN:");
         addLine(var1, this.excludedSubtreesDN.toString());
      }

      if (!this.excludedSubtreesDNS.isEmpty()) {
         addLine(var1, "DNS:");
         addLine(var1, this.excludedSubtreesDNS.toString());
      }

      if (!this.excludedSubtreesEmail.isEmpty()) {
         addLine(var1, "Email:");
         addLine(var1, this.excludedSubtreesEmail.toString());
      }

      if (!this.excludedSubtreesURI.isEmpty()) {
         addLine(var1, "URI:");
         addLine(var1, this.excludedSubtreesURI.toString());
      }

      if (!this.excludedSubtreesIP.isEmpty()) {
         addLine(var1, "IP:");
         addLine(var1, stringifyIPCollection(this.excludedSubtreesIP));
      }

      if (!this.excludedSubtreesOtherName.isEmpty()) {
         addLine(var1, "OtherName:");
         addLine(var1, stringifyOtherNameCollection(this.excludedSubtreesOtherName));
      }

      return var1.toString();
   }

   private static void addLine(StringBuilder var0, String var1) {
      var0.append(var1).append(Strings.lineSeparator());
   }

   private static String extractNameAsString(GeneralSubtree var0) {
      return extractNameAsString(var0.getBase().getName());
   }

   private static String extractNameAsString(ASN1Encodable var0) {
      return ASN1IA5String.getInstance(var0).getString();
   }
}
