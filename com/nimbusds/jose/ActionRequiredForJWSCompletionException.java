/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.CompletableJWSObjectSigning;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSignerOption;
import java.util.Objects;

public class ActionRequiredForJWSCompletionException
extends JOSEException {
    private final JWSSignerOption option;
    private final CompletableJWSObjectSigning completableSigning;

    public ActionRequiredForJWSCompletionException(String message, JWSSignerOption option, CompletableJWSObjectSigning completableSigning) {
        super(message);
        this.option = Objects.requireNonNull(option);
        this.completableSigning = Objects.requireNonNull(completableSigning);
    }

    public JWSSignerOption getTriggeringOption() {
        return this.option;
    }

    public CompletableJWSObjectSigning getCompletableJWSObjectSigning() {
        return this.completableSigning;
    }
}

