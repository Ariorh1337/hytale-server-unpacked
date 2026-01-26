/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.ec.rfc8032.Ed25519;
import org.bouncycastle.util.io.Streams;

public final class Ed25519PublicKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 32;
    private final Ed25519.PublicPoint publicPoint;

    public Ed25519PublicKeyParameters(byte[] byArray) {
        this(Ed25519PublicKeyParameters.validate(byArray), 0);
    }

    public Ed25519PublicKeyParameters(byte[] byArray, int n) {
        super(false);
        this.publicPoint = Ed25519PublicKeyParameters.parse(byArray, n);
    }

    public Ed25519PublicKeyParameters(InputStream inputStream) throws IOException {
        super(false);
        byte[] byArray = new byte[32];
        if (32 != Streams.readFully(inputStream, byArray)) {
            throw new EOFException("EOF encountered in middle of Ed25519 public key");
        }
        this.publicPoint = Ed25519PublicKeyParameters.parse(byArray, 0);
    }

    public Ed25519PublicKeyParameters(Ed25519.PublicPoint publicPoint) {
        super(false);
        if (publicPoint == null) {
            throw new NullPointerException("'publicPoint' cannot be null");
        }
        this.publicPoint = publicPoint;
    }

    public void encode(byte[] byArray, int n) {
        Ed25519.encodePublicPoint(this.publicPoint, byArray, n);
    }

    public byte[] getEncoded() {
        byte[] byArray = new byte[32];
        this.encode(byArray, 0);
        return byArray;
    }

    public boolean verify(int n, byte[] byArray, byte[] byArray2, int n2, int n3, byte[] byArray3, int n4) {
        switch (n) {
            case 0: {
                if (null != byArray) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed25519.verify(byArray3, n4, this.publicPoint, byArray2, n2, n3);
            }
            case 1: {
                if (null == byArray) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (byArray.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed25519.verify(byArray3, n4, this.publicPoint, byArray, byArray2, n2, n3);
            }
            case 2: {
                if (null == byArray) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (byArray.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                if (64 != n3) {
                    throw new IllegalArgumentException("msgLen");
                }
                return Ed25519.verifyPrehash(byArray3, n4, this.publicPoint, byArray, byArray2, n2);
            }
        }
        throw new IllegalArgumentException("algorithm");
    }

    private static Ed25519.PublicPoint parse(byte[] byArray, int n) {
        Ed25519.PublicPoint publicPoint = Ed25519.validatePublicKeyPartialExport(byArray, n);
        if (publicPoint == null) {
            throw new IllegalArgumentException("invalid public key");
        }
        return publicPoint;
    }

    private static byte[] validate(byte[] byArray) {
        if (byArray.length != 32) {
            throw new IllegalArgumentException("'buf' must have length 32");
        }
        return byArray;
    }
}

