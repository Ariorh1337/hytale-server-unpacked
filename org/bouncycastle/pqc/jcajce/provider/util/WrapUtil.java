/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.InvalidKeyException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.pqc.jcajce.provider.util.KdfUtil;
import org.bouncycastle.util.Arrays;

public class WrapUtil {
    public static Wrapper getKeyWrapper(KTSParameterSpec kTSParameterSpec, byte[] byArray) throws InvalidKeyException {
        Wrapper wrapper = WrapUtil.getWrapper(kTSParameterSpec.getKeyAlgorithmName());
        AlgorithmIdentifier algorithmIdentifier = kTSParameterSpec.getKdfAlgorithm();
        if (algorithmIdentifier == null) {
            wrapper.init(true, new KeyParameter(Arrays.copyOfRange(byArray, 0, (kTSParameterSpec.getKeySize() + 7) / 8)));
        } else {
            wrapper.init(true, new KeyParameter(WrapUtil.makeKeyBytes(kTSParameterSpec, byArray)));
        }
        return wrapper;
    }

    public static Wrapper getKeyUnwrapper(KTSParameterSpec kTSParameterSpec, byte[] byArray) throws InvalidKeyException {
        Wrapper wrapper = WrapUtil.getWrapper(kTSParameterSpec.getKeyAlgorithmName());
        AlgorithmIdentifier algorithmIdentifier = kTSParameterSpec.getKdfAlgorithm();
        if (algorithmIdentifier == null) {
            wrapper.init(false, new KeyParameter(byArray, 0, (kTSParameterSpec.getKeySize() + 7) / 8));
        } else {
            wrapper.init(false, new KeyParameter(WrapUtil.makeKeyBytes(kTSParameterSpec, byArray)));
        }
        return wrapper;
    }

    public static Wrapper getWrapper(String string) {
        Wrapper wrapper;
        if (string.equalsIgnoreCase("AESWRAP") || string.equalsIgnoreCase("AES")) {
            wrapper = new RFC3394WrapEngine(AESEngine.newInstance());
        } else if (string.equalsIgnoreCase("ARIA")) {
            wrapper = new RFC3394WrapEngine(new ARIAEngine());
        } else if (string.equalsIgnoreCase("Camellia")) {
            wrapper = new RFC3394WrapEngine(new CamelliaEngine());
        } else if (string.equalsIgnoreCase("SEED")) {
            wrapper = new RFC3394WrapEngine(new SEEDEngine());
        } else if (string.equalsIgnoreCase("AES-KWP")) {
            wrapper = new RFC5649WrapEngine(new AESEngine());
        } else if (string.equalsIgnoreCase("Camellia-KWP")) {
            wrapper = new RFC5649WrapEngine(new CamelliaEngine());
        } else if (string.equalsIgnoreCase("ARIA-KWP")) {
            wrapper = new RFC5649WrapEngine(new ARIAEngine());
        } else {
            throw new UnsupportedOperationException("unknown key algorithm: " + string);
        }
        return wrapper;
    }

    public static byte[] trimSecret(String string, byte[] byArray) {
        if (string.equals("SEED")) {
            return Arrays.copyOfRange(byArray, 0, 16);
        }
        return byArray;
    }

    private static byte[] makeKeyBytes(KTSParameterSpec kTSParameterSpec, byte[] byArray) throws InvalidKeyException {
        try {
            return KdfUtil.makeKeyBytes(kTSParameterSpec.getKdfAlgorithm(), byArray, kTSParameterSpec.getOtherInfo(), kTSParameterSpec.getKeySize());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new InvalidKeyException(illegalArgumentException.getMessage());
        }
    }
}

