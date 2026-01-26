/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

public class BcDefaultDigestProvider
implements BcDigestProvider {
    private static final Map lookup = BcDefaultDigestProvider.createTable();
    public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

    private static Map createTable() {
        HashMap<ASN1ObjectIdentifier, BcDigestProvider> hashMap = new HashMap<ASN1ObjectIdentifier, BcDigestProvider>();
        hashMap.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA256Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(224);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(256);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(384);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(512);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_shake128, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHAKEDigest(128);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_shake256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHAKEDigest(256);
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_shake128_len, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new AdjustedXof(new SHAKEDigest(128), ASN1Integer.getInstance(algorithmIdentifier.getParameters()).intValueExact());
            }
        });
        hashMap.put(NISTObjectIdentifiers.id_shake256_len, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new AdjustedXof(new SHAKEDigest(256), ASN1Integer.getInstance(algorithmIdentifier.getParameters()).intValueExact());
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md5, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD5Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md4, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD4Digest();
            }
        });
        hashMap.put(PKCSObjectIdentifiers.md2, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD2Digest();
            }
        });
        hashMap.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411Digest();
            }
        });
        hashMap.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411_2012_256Digest();
            }
        });
        hashMap.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411_2012_512Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD128Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD160Digest();
            }
        });
        hashMap.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD256Digest();
            }
        });
        hashMap.put(GMObjectIdentifiers.sm3, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SM3Digest();
            }
        });
        hashMap.put(MiscObjectIdentifiers.blake3_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new Blake3Digest(256);
            }
        });
        return Collections.unmodifiableMap(hashMap);
    }

    private BcDefaultDigestProvider() {
    }

    @Override
    public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        BcDigestProvider bcDigestProvider = (BcDigestProvider)lookup.get(algorithmIdentifier.getAlgorithm());
        if (bcDigestProvider == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return bcDigestProvider.get(algorithmIdentifier);
    }

    private static class AdjustedXof
    implements Xof {
        private final Xof xof;
        private final int length;

        AdjustedXof(Xof xof, int n) {
            this.xof = xof;
            this.length = n;
        }

        @Override
        public String getAlgorithmName() {
            return this.xof.getAlgorithmName() + "-" + this.length;
        }

        @Override
        public int getDigestSize() {
            return (this.length + 7) / 8;
        }

        @Override
        public void update(byte by) {
            this.xof.update(by);
        }

        @Override
        public void update(byte[] byArray, int n, int n2) {
            this.xof.update(byArray, n, n2);
        }

        @Override
        public int doFinal(byte[] byArray, int n) {
            return this.doFinal(byArray, n, this.getDigestSize());
        }

        @Override
        public void reset() {
            this.xof.reset();
        }

        @Override
        public int getByteLength() {
            return this.xof.getByteLength();
        }

        @Override
        public int doFinal(byte[] byArray, int n, int n2) {
            return this.xof.doFinal(byArray, n, n2);
        }

        @Override
        public int doOutput(byte[] byArray, int n, int n2) {
            return this.xof.doOutput(byArray, n, n2);
        }
    }
}

