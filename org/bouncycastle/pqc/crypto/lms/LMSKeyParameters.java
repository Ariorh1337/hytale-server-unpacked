/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import java.io.IOException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Encodable;

public abstract class LMSKeyParameters
extends AsymmetricKeyParameter
implements Encodable {
    protected LMSKeyParameters(boolean bl) {
        super(bl);
    }

    @Override
    public abstract byte[] getEncoded() throws IOException;
}

