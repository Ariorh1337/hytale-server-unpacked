/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.jcajce;

import java.security.Key;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;

public class JceGenericKey
extends GenericKey {
    private static Object getRepresentation(Key key) {
        byte[] byArray = key.getEncoded();
        if (byArray != null) {
            return byArray;
        }
        return key;
    }

    public JceGenericKey(AlgorithmIdentifier algorithmIdentifier, Key key) {
        super(algorithmIdentifier, JceGenericKey.getRepresentation(key));
    }
}

