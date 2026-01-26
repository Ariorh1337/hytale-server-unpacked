/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.util.Exceptions;

public abstract class BaseDeterministicOrRandomSignature
extends SignatureSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final AlgorithmParameterSpec originalSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
    protected AlgorithmParameters engineParams;
    protected ContextParameterSpec paramSpec;
    protected AsymmetricKeyParameter keyParams;
    protected boolean isInitState = true;

    protected BaseDeterministicOrRandomSignature(String string) {
    }

    @Override
    protected final void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        this.verifyInit(publicKey);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        this.reInit();
    }

    protected abstract void verifyInit(PublicKey var1) throws InvalidKeyException;

    @Override
    protected final void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        this.signInit(privateKey, null);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        this.reInit();
    }

    @Override
    protected final void engineInitSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException {
        this.signInit(privateKey, secureRandom);
        this.paramSpec = ContextParameterSpec.EMPTY_CONTEXT_SPEC;
        this.isInitState = true;
        this.reInit();
    }

    protected abstract void signInit(PrivateKey var1, SecureRandom var2) throws InvalidKeyException;

    @Override
    protected final void engineUpdate(byte by) throws SignatureException {
        this.isInitState = false;
        this.updateEngine(by);
    }

    protected abstract void updateEngine(byte var1) throws SignatureException;

    @Override
    protected final void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        this.isInitState = false;
        this.updateEngine(byArray, n, n2);
    }

    protected abstract void updateEngine(byte[] var1, int var2, int var3) throws SignatureException;

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec == null) {
            if (this.originalSpec != null) {
                algorithmParameterSpec = this.originalSpec;
            } else {
                return;
            }
        }
        if (!this.isInitState) {
            throw new ProviderException("cannot call setParameter in the middle of update");
        }
        if (algorithmParameterSpec instanceof ContextParameterSpec) {
            this.paramSpec = (ContextParameterSpec)algorithmParameterSpec;
            this.reInit();
        } else {
            byte[] byArray = SpecUtil.getContextFrom(algorithmParameterSpec);
            if (byArray != null) {
                this.paramSpec = new ContextParameterSpec(byArray);
                this.reInit();
            } else {
                throw new InvalidAlgorithmParameterException("unknown AlgorithmParameterSpec in signature");
            }
        }
    }

    private void reInit() {
        CipherParameters cipherParameters = this.keyParams;
        if (this.keyParams.isPrivate()) {
            if (this.appRandom != null) {
                cipherParameters = new ParametersWithRandom(cipherParameters, this.appRandom);
            }
            if (this.paramSpec != null) {
                cipherParameters = new ParametersWithContext(cipherParameters, this.paramSpec.getContext());
            }
            this.reInitialize(true, cipherParameters);
        } else {
            if (this.paramSpec != null) {
                cipherParameters = new ParametersWithContext(cipherParameters, this.paramSpec.getContext());
            }
            this.reInitialize(false, cipherParameters);
        }
    }

    protected abstract void reInitialize(boolean var1, CipherParameters var2);

    @Override
    protected final AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null && this.paramSpec != ContextParameterSpec.EMPTY_CONTEXT_SPEC) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
                this.engineParams.init(this.paramSpec);
            }
            catch (Exception exception) {
                throw Exceptions.illegalStateException(exception.toString(), exception);
            }
        }
        return this.engineParams;
    }

    @Override
    protected final void engineSetParameter(String string, Object object) {
        throw new UnsupportedOperationException("SetParameter unsupported");
    }

    @Override
    protected final Object engineGetParameter(String string) {
        throw new UnsupportedOperationException("GetParameter unsupported");
    }
}

