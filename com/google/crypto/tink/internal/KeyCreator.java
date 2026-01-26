/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public interface KeyCreator<ParametersT extends Parameters> {
    public Key createKey(ParametersT var1, @Nullable Integer var2) throws GeneralSecurityException;
}

