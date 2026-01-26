/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;
import org.bouncycastle.pqc.crypto.picnic.KMatrices;
import org.bouncycastle.pqc.crypto.picnic.KMatricesWithPointer;
import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

abstract class LowmcConstants {
    protected int[] linearMatrices;
    protected int[] roundConstants;
    protected int[] keyMatrices;
    protected KMatrices LMatrix;
    protected KMatrices KMatrix;
    protected KMatrices RConstants;
    protected int[] linearMatrices_full;
    protected int[] keyMatrices_full;
    protected int[] keyMatrices_inv;
    protected int[] linearMatrices_inv;
    protected int[] roundConstants_full;
    protected KMatrices LMatrix_full;
    protected KMatrices LMatrix_inv;
    protected KMatrices KMatrix_full;
    protected KMatrices KMatrix_inv;
    protected KMatrices RConstants_full;

    LowmcConstants() {
    }

    static int[] readArray(DataInputStream dataInputStream) throws IOException {
        int[] nArray = new int[dataInputStream.readInt()];
        for (int i = 0; i != nArray.length; ++i) {
            nArray[i] = dataInputStream.readInt();
        }
        return nArray;
    }

    static int[] ReadFromProperty(Properties properties, String string, int n) {
        String string2 = properties.getProperty(string);
        byte[] byArray = Hex.decode(LowmcConstants.removeCommas(string2));
        int[] nArray = new int[n];
        for (int i = 0; i < byArray.length / 4; ++i) {
            nArray[i] = Pack.littleEndianToInt(byArray, i * 4);
        }
        return nArray;
    }

    private static byte[] removeCommas(String string) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != string.length(); ++i) {
            if (string.charAt(i) == ',') continue;
            byteArrayOutputStream.write(string.charAt(i));
        }
        return byteArrayOutputStream.toByteArray();
    }

    private KMatricesWithPointer GET_MAT(KMatrices kMatrices, int n) {
        KMatricesWithPointer kMatricesWithPointer = new KMatricesWithPointer(kMatrices);
        kMatricesWithPointer.setMatrixPointer(n * kMatricesWithPointer.getSize());
        return kMatricesWithPointer;
    }

    protected KMatricesWithPointer LMatrix(PicnicEngine picnicEngine, int n) {
        if (picnicEngine.stateSizeBits == 128) {
            return this.GET_MAT(this.LMatrix, n);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return this.GET_MAT(this.LMatrix_full, n);
        }
        if (picnicEngine.stateSizeBits == 192) {
            if (picnicEngine.numRounds == 4) {
                return this.GET_MAT(this.LMatrix_full, n);
            }
            return this.GET_MAT(this.LMatrix, n);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return this.GET_MAT(this.LMatrix_full, n);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return this.GET_MAT(this.LMatrix, n);
        }
        return null;
    }

    protected KMatricesWithPointer LMatrixInv(PicnicEngine picnicEngine, int n) {
        if (picnicEngine.stateSizeBits == 129) {
            return this.GET_MAT(this.LMatrix_inv, n);
        }
        if (picnicEngine.stateSizeBits == 192 && picnicEngine.numRounds == 4) {
            return this.GET_MAT(this.LMatrix_inv, n);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return this.GET_MAT(this.LMatrix_inv, n);
        }
        return null;
    }

    protected KMatricesWithPointer KMatrix(PicnicEngine picnicEngine, int n) {
        if (picnicEngine.stateSizeBits == 128) {
            return this.GET_MAT(this.KMatrix, n);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return this.GET_MAT(this.KMatrix_full, n);
        }
        if (picnicEngine.stateSizeBits == 192) {
            if (picnicEngine.numRounds == 4) {
                return this.GET_MAT(this.KMatrix_full, n);
            }
            return this.GET_MAT(this.KMatrix, n);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return this.GET_MAT(this.KMatrix_full, n);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return this.GET_MAT(this.KMatrix, n);
        }
        return null;
    }

    protected KMatricesWithPointer KMatrixInv(PicnicEngine picnicEngine) {
        int n = 0;
        if (picnicEngine.stateSizeBits == 129) {
            return this.GET_MAT(this.KMatrix_inv, n);
        }
        if (picnicEngine.stateSizeBits == 192 && picnicEngine.numRounds == 4) {
            return this.GET_MAT(this.KMatrix_inv, n);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return this.GET_MAT(this.KMatrix_inv, n);
        }
        return null;
    }

    protected KMatricesWithPointer RConstant(PicnicEngine picnicEngine, int n) {
        if (picnicEngine.stateSizeBits == 128) {
            return this.GET_MAT(this.RConstants, n);
        }
        if (picnicEngine.stateSizeBits == 129) {
            return this.GET_MAT(this.RConstants_full, n);
        }
        if (picnicEngine.stateSizeBits == 192) {
            if (picnicEngine.numRounds == 4) {
                return this.GET_MAT(this.RConstants_full, n);
            }
            return this.GET_MAT(this.RConstants, n);
        }
        if (picnicEngine.stateSizeBits == 255) {
            return this.GET_MAT(this.RConstants_full, n);
        }
        if (picnicEngine.stateSizeBits == 256) {
            return this.GET_MAT(this.RConstants, n);
        }
        return null;
    }
}

