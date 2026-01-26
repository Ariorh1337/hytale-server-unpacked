/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import javax.security.auth.Destroyable;

public interface SecretWithEncapsulation
extends Destroyable {
    public byte[] getSecret();

    public byte[] getEncapsulation();
}

