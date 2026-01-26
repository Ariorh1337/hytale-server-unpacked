/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;

public class AESWrapPadEngine
extends RFC5649WrapEngine {
    public AESWrapPadEngine() {
        super(AESEngine.newInstance());
    }
}

