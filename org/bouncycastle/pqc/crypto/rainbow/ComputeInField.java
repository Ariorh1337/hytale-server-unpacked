/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.GF2Field;

class ComputeInField {
    public short[] solveEquation(short[][] sArray, short[] sArray2) {
        if (sArray.length != sArray2.length) {
            return null;
        }
        try {
            int n;
            short[][] sArray3 = new short[sArray.length][sArray.length + 1];
            short[] sArray4 = new short[sArray.length];
            for (n = 0; n < sArray.length; ++n) {
                System.arraycopy(sArray[n], 0, sArray3[n], 0, sArray[0].length);
                sArray3[n][sArray2.length] = GF2Field.addElem(sArray2[n], sArray3[n][sArray2.length]);
            }
            this.gaussElim(sArray3);
            for (n = 0; n < sArray3.length; ++n) {
                sArray4[n] = sArray3[n][sArray2.length];
            }
            return sArray4;
        }
        catch (RuntimeException runtimeException) {
            return null;
        }
    }

    public short[][] inverse(short[][] sArray) {
        if (sArray.length != sArray[0].length) {
            throw new RuntimeException("The matrix is not invertible. Please choose another one!");
        }
        try {
            int n;
            int n2;
            short[][] sArray2 = new short[sArray.length][2 * sArray.length];
            for (n2 = 0; n2 < sArray.length; ++n2) {
                System.arraycopy(sArray[n2], 0, sArray2[n2], 0, sArray.length);
                for (n = sArray.length; n < 2 * sArray.length; ++n) {
                    sArray2[n2][n] = 0;
                }
                sArray2[n2][n2 + sArray2.length] = 1;
            }
            this.gaussElim(sArray2);
            short[][] sArray3 = new short[sArray2.length][sArray2.length];
            for (n2 = 0; n2 < sArray2.length; ++n2) {
                for (n = sArray2.length; n < 2 * sArray2.length; ++n) {
                    sArray3[n2][n - sArray2.length] = sArray2[n2][n];
                }
            }
            return sArray3;
        }
        catch (RuntimeException runtimeException) {
            return null;
        }
    }

    private void gaussElim(short[][] sArray) {
        for (int i = 0; i < sArray.length; ++i) {
            int n;
            int n2;
            for (n2 = i + 1; n2 < sArray.length; ++n2) {
                if (sArray[i][i] != 0) continue;
                for (n = i; n < sArray[0].length; ++n) {
                    sArray[i][n] = GF2Field.addElem(sArray[i][n], sArray[n2][n]);
                }
            }
            short s = GF2Field.invElem(sArray[i][i]);
            if (s == 0) {
                throw new RuntimeException("The matrix is not invertible");
            }
            sArray[i] = this.multVect(s, sArray[i]);
            for (n2 = 0; n2 < sArray.length; ++n2) {
                if (i == n2) continue;
                short s2 = sArray[n2][i];
                for (n = i; n < sArray[0].length; ++n) {
                    short s3 = GF2Field.multElem(sArray[i][n], s2);
                    sArray[n2][n] = GF2Field.addElem(sArray[n2][n], s3);
                }
            }
        }
    }

    public short[][] multiplyMatrix(short[][] sArray, short[][] sArray2) throws RuntimeException {
        if (sArray[0].length != sArray2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short s = 0;
        short[][] sArray3 = new short[sArray.length][sArray2[0].length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray2.length; ++j) {
                for (int k = 0; k < sArray2[0].length; ++k) {
                    s = GF2Field.multElem(sArray[i][j], sArray2[j][k]);
                    sArray3[i][k] = GF2Field.addElem(sArray3[i][k], s);
                }
            }
        }
        return sArray3;
    }

    public short[] multiplyMatrix(short[][] sArray, short[] sArray2) throws RuntimeException {
        if (sArray[0].length != sArray2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short s = 0;
        short[] sArray3 = new short[sArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray2.length; ++j) {
                s = GF2Field.multElem(sArray[i][j], sArray2[j]);
                sArray3[i] = GF2Field.addElem(sArray3[i], s);
            }
        }
        return sArray3;
    }

    public short multiplyMatrix_quad(short[][] sArray, short[] sArray2) throws RuntimeException {
        if (sArray.length != sArray[0].length || sArray[0].length != sArray2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short s = 0;
        short[] sArray3 = new short[sArray.length];
        short s2 = 0;
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray2.length; ++j) {
                s = GF2Field.multElem(sArray[i][j], sArray2[j]);
                sArray3[i] = GF2Field.addElem(sArray3[i], s);
            }
            s = GF2Field.multElem(sArray3[i], sArray2[i]);
            s2 = GF2Field.addElem(s2, s);
        }
        return s2;
    }

    public short[] addVect(short[] sArray, short[] sArray2) {
        if (sArray.length != sArray2.length) {
            throw new RuntimeException("Addition is not possible! vector1.length: " + sArray.length + " vector2.length: " + sArray2.length);
        }
        short[] sArray3 = new short[sArray.length];
        for (int i = 0; i < sArray3.length; ++i) {
            sArray3[i] = GF2Field.addElem(sArray[i], sArray2[i]);
        }
        return sArray3;
    }

    public short[][] multVects(short[] sArray, short[] sArray2) {
        if (sArray.length != sArray2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        short[][] sArray3 = new short[sArray.length][sArray2.length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray2.length; ++j) {
                sArray3[i][j] = GF2Field.multElem(sArray[i], sArray2[j]);
            }
        }
        return sArray3;
    }

    public short[] multVect(short s, short[] sArray) {
        short[] sArray2 = new short[sArray.length];
        for (int i = 0; i < sArray2.length; ++i) {
            sArray2[i] = GF2Field.multElem(s, sArray[i]);
        }
        return sArray2;
    }

    public short[][] multMatrix(short s, short[][] sArray) {
        short[][] sArray2 = new short[sArray.length][sArray[0].length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray[0].length; ++j) {
                sArray2[i][j] = GF2Field.multElem(s, sArray[i][j]);
            }
        }
        return sArray2;
    }

    public short[][] addMatrix(short[][] sArray, short[][] sArray2) {
        if (sArray.length != sArray2.length || sArray[0].length != sArray2[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        short[][] sArray3 = new short[sArray.length][sArray[0].length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray[0].length; ++j) {
                sArray3[i][j] = GF2Field.addElem(sArray[i][j], sArray2[i][j]);
            }
        }
        return sArray3;
    }

    public short[][] addMatrixTranspose(short[][] sArray) {
        if (sArray.length != sArray[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        return this.addMatrix(sArray, this.transpose(sArray));
    }

    public short[][] transpose(short[][] sArray) {
        short[][] sArray2 = new short[sArray[0].length][sArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray[0].length; ++j) {
                sArray2[j][i] = sArray[i][j];
            }
        }
        return sArray2;
    }

    public short[][] to_UT(short[][] sArray) {
        if (sArray.length != sArray[0].length) {
            throw new RuntimeException("Computation to upper triangular matrix is not possible!");
        }
        short[][] sArray2 = new short[sArray.length][sArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            sArray2[i][i] = sArray[i][i];
            for (int j = i + 1; j < sArray[0].length; ++j) {
                sArray2[i][j] = GF2Field.addElem(sArray[i][j], sArray[j][i]);
            }
        }
        return sArray2;
    }

    public short[][][] obfuscate_l1_polys(short[][] sArray, short[][][] sArray2, short[][][] sArray3) {
        if (sArray2[0].length != sArray3[0].length || sArray2[0][0].length != sArray3[0][0].length || sArray2.length != sArray[0].length || sArray3.length != sArray.length) {
            throw new RuntimeException("Multiplication not possible!");
        }
        short[][][] sArray4 = new short[sArray3.length][sArray3[0].length][sArray3[0][0].length];
        for (int i = 0; i < sArray2[0].length; ++i) {
            for (int j = 0; j < sArray2[0][0].length; ++j) {
                for (int k = 0; k < sArray.length; ++k) {
                    for (int i2 = 0; i2 < sArray[0].length; ++i2) {
                        short s = GF2Field.multElem(sArray[k][i2], sArray2[i2][i][j]);
                        sArray4[k][i][j] = GF2Field.addElem(sArray4[k][i][j], s);
                    }
                    sArray4[k][i][j] = GF2Field.addElem(sArray3[k][i][j], sArray4[k][i][j]);
                }
            }
        }
        return sArray4;
    }
}

