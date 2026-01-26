/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;

public interface JOSEObjectTypeVerifier<C extends SecurityContext> {
    public void verify(JOSEObjectType var1, C var2) throws BadJOSEException;
}

