/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.PlainObject;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import java.text.ParseException;

public interface JOSEProcessor<C extends SecurityContext> {
    public Payload process(String var1, C var2) throws ParseException, BadJOSEException, JOSEException;

    public Payload process(JOSEObject var1, C var2) throws BadJOSEException, JOSEException;

    public Payload process(PlainObject var1, C var2) throws BadJOSEException, JOSEException;

    public Payload process(JWSObject var1, C var2) throws BadJOSEException, JOSEException;

    public Payload process(JWEObject var1, C var2) throws BadJOSEException, JOSEException;
}

