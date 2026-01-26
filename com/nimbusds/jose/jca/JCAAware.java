/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jca;

import com.nimbusds.jose.jca.JCAContext;

public interface JCAAware<T extends JCAContext> {
    public T getJCAContext();
}

