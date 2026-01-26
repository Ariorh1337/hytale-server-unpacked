/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SingleKeyJWSKeySelector<C extends SecurityContext>
implements JWSKeySelector<C> {
    private final List<Key> singletonKeyList;
    private final JWSAlgorithm expectedJWSAlg;

    public SingleKeyJWSKeySelector(JWSAlgorithm expectedJWSAlg, Key key) {
        this.singletonKeyList = Collections.singletonList(Objects.requireNonNull(key));
        this.expectedJWSAlg = Objects.requireNonNull(expectedJWSAlg);
    }

    @Override
    public List<? extends Key> selectJWSKeys(JWSHeader header, C context) {
        if (!this.expectedJWSAlg.equals(header.getAlgorithm())) {
            return Collections.emptyList();
        }
        return this.singletonKeyList;
    }
}

