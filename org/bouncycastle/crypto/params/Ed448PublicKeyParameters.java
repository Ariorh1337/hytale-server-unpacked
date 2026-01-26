/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.ec.rfc8032.Ed448;
import org.bouncycastle.util.io.Streams;

public final class Ed448PublicKeyParameters
extends AsymmetricKeyParameter {
    public static final int KEY_SIZE = 57;
    private final Ed448.PublicPoint publicPoint;

    public Ed448PublicKeyParameters(byte[] byArray) {
        this(Ed448PublicKeyParameters.validate(byArray), 0);
    }

    public Ed448PublicKeyParameters(byte[] byArray, int n) {
        super(false);
        this.publicPoint = Ed448PublicKeyParameters.parse(byArray, n);
    }

    public Ed448PublicKeyParameters(InputStream inputStream) throws IOException {
        super(false);
        byte[] byArray = new byte[57];
        if (57 != Streams.readFully(inputStream, byArray)) {
            throw new EOFException("EOF encountered in middle of Ed448 public key");
        }
        this.publicPoint = Ed448PublicKeyParameters.parse(byArray, 0);
    }

    public Ed448PublicKeyParameters(Ed448.PublicPoint publicPoint) {
        super(false);
        if (publicPoint == null) {
            throw new NullPointerException("'publicPoint' cannot be null");
        }
        this.publicPoint = publicPoint;
    }

    public void encode(byte[] byArray, int n) {
        Ed448.encodePublicPoint(this.publicPoint, byArray, n);
    }

    public byte[] getEncoded() {
        byte[] byArray = new byte[57];
        this.encode(byArray, 0);
        return byArray;
    }

    public boolean verify(int n, byte[] byArray, byte[] byArray2, int n2, int n3, byte[] byArray3, int n4) {
        switch (n) {
            case 0: {
                if (null == byArray) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (byArray.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                return Ed448.verify(byArray3, n4, this.publicPoint, byArray, byArray2, n2, n3);
            }
            case 1: {
                if (null == byArray) {
                    throw new NullPointerException("'ctx' cannot be null");
                }
                if (byArray.length > 255) {
                    throw new IllegalArgumentException("ctx");
                }
                if (64 != n3) {
                    throw new IllegalArgumentException("msgLen");
                }
                return Ed448.verifyPrehash(byArray3, n4, this.publicPoint, byArray, byArray2, n2);
            }
        }
        throw new IllegalArgumentException("algorithm");
    }

    private static Ed448.PublicPoint parse(byte[] byArray, int n) {
        Ed448.PublicPoint publicPoint = Ed448.validatePublicKeyPartialExport(byArray, n);
        if (publicPoint == null) {
            throw new IllegalArgumentException("invalid public key");
        }
        return publicPoint;
    }

    private static byte[] validate(byte[] byArray) {
        if (byArray.length != 57) {
            throw new IllegalArgumentException("'buf' must have length 57");
        }
        return byArray;
    }
}

