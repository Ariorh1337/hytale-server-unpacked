/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.iana;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public interface IANAObjectIdentifiers {
    public static final ASN1ObjectIdentifier internet = new ASN1ObjectIdentifier("1.3.6.1");
    public static final ASN1ObjectIdentifier directory = internet.branch("1");
    public static final ASN1ObjectIdentifier mgmt = internet.branch("2");
    public static final ASN1ObjectIdentifier experimental = internet.branch("3");
    public static final ASN1ObjectIdentifier _private = internet.branch("4");
    public static final ASN1ObjectIdentifier security = internet.branch("5");
    public static final ASN1ObjectIdentifier SNMPv2 = internet.branch("6");
    public static final ASN1ObjectIdentifier mail = internet.branch("7");
    public static final ASN1ObjectIdentifier security_mechanisms = security.branch("5");
    public static final ASN1ObjectIdentifier security_nametypes = security.branch("6");
    public static final ASN1ObjectIdentifier pkix = security_mechanisms.branch("7");
    public static final ASN1ObjectIdentifier ipsec = security_mechanisms.branch("8");
    public static final ASN1ObjectIdentifier isakmpOakley = ipsec.branch("1");
    public static final ASN1ObjectIdentifier hmacMD5 = isakmpOakley.branch("1");
    public static final ASN1ObjectIdentifier hmacSHA1 = isakmpOakley.branch("2");
    public static final ASN1ObjectIdentifier hmacTIGER = isakmpOakley.branch("3");
    public static final ASN1ObjectIdentifier hmacRIPEMD160 = isakmpOakley.branch("4");
    public static final ASN1ObjectIdentifier id_alg = internet.branch("5.5.7.6");
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE128 = id_alg.branch("30");
    public static final ASN1ObjectIdentifier id_RSASSA_PSS_SHAKE256 = id_alg.branch("31");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake128 = id_alg.branch("32");
    public static final ASN1ObjectIdentifier id_ecdsa_with_shake256 = id_alg.branch("33");
    public static final ASN1ObjectIdentifier id_alg_unsigned = id_alg.branch("36");
    public static final ASN1ObjectIdentifier id_MLDSA44_RSA2048_PSS_SHA256 = id_alg.branch("37");
    public static final ASN1ObjectIdentifier id_MLDSA44_RSA2048_PKCS15_SHA256 = id_alg.branch("38");
    public static final ASN1ObjectIdentifier id_MLDSA44_Ed25519_SHA512 = id_alg.branch("39");
    public static final ASN1ObjectIdentifier id_MLDSA44_ECDSA_P256_SHA256 = id_alg.branch("40");
    public static final ASN1ObjectIdentifier id_MLDSA65_RSA3072_PSS_SHA512 = id_alg.branch("41");
    public static final ASN1ObjectIdentifier id_MLDSA65_RSA3072_PKCS15_SHA512 = id_alg.branch("42");
    public static final ASN1ObjectIdentifier id_MLDSA65_RSA4096_PSS_SHA512 = id_alg.branch("43");
    public static final ASN1ObjectIdentifier id_MLDSA65_RSA4096_PKCS15_SHA512 = id_alg.branch("44");
    public static final ASN1ObjectIdentifier id_MLDSA65_ECDSA_P256_SHA512 = id_alg.branch("45");
    public static final ASN1ObjectIdentifier id_MLDSA65_ECDSA_P384_SHA512 = id_alg.branch("46");
    public static final ASN1ObjectIdentifier id_MLDSA65_ECDSA_brainpoolP256r1_SHA512 = id_alg.branch("47");
    public static final ASN1ObjectIdentifier id_MLDSA65_Ed25519_SHA512 = id_alg.branch("48");
    public static final ASN1ObjectIdentifier id_MLDSA87_ECDSA_P384_SHA512 = id_alg.branch("49");
    public static final ASN1ObjectIdentifier id_MLDSA87_ECDSA_brainpoolP384r1_SHA512 = id_alg.branch("50");
    public static final ASN1ObjectIdentifier id_MLDSA87_Ed448_SHAKE256 = id_alg.branch("51");
    public static final ASN1ObjectIdentifier id_MLDSA87_RSA3072_PSS_SHA512 = id_alg.branch("52");
    public static final ASN1ObjectIdentifier id_MLDSA87_RSA4096_PSS_SHA512 = id_alg.branch("53");
    public static final ASN1ObjectIdentifier id_MLDSA87_ECDSA_P521_SHA512 = id_alg.branch("54");
}

