/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyUse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyUseAndOpsConsistency {
    static final Map<KeyUse, Set<KeyOperation>> MAP;

    KeyUseAndOpsConsistency() {
    }

    static boolean areConsistent(KeyUse use, Set<KeyOperation> ops) {
        if (use == null || ops == null) {
            return true;
        }
        return !MAP.containsKey(use) || MAP.get(use).containsAll(ops);
    }

    static {
        HashMap<KeyUse, HashSet<KeyOperation>> map = new HashMap<KeyUse, HashSet<KeyOperation>>();
        map.put(KeyUse.SIGNATURE, new HashSet<KeyOperation>(Arrays.asList(KeyOperation.SIGN, KeyOperation.VERIFY)));
        map.put(KeyUse.ENCRYPTION, new HashSet<KeyOperation>(Arrays.asList(KeyOperation.ENCRYPT, KeyOperation.DECRYPT, KeyOperation.WRAP_KEY, KeyOperation.UNWRAP_KEY)));
        MAP = Collections.unmodifiableMap(map);
    }
}

