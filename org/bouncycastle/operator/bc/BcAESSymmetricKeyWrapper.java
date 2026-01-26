/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.operator.bc.AESUtil;
import org.bouncycastle.operator.bc.BcSymmetricKeyWrapper;

public class BcAESSymmetricKeyWrapper
extends BcSymmetricKeyWrapper {
    public BcAESSymmetricKeyWrapper(KeyParameter keyParameter) {
        super(AESUtil.determineKeyEncAlg(keyParameter), new AESWrapEngine(), keyParameter);
    }
}

