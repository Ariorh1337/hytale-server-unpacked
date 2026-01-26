/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.bouncycastle.pqc.crypto.picnic.KMatrices;
import org.bouncycastle.pqc.crypto.picnic.LowmcConstants;
import org.bouncycastle.util.Exceptions;

public class LowmcConstantsL1
extends LowmcConstants {
    LowmcConstantsL1() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(LowmcConstants.class.getResourceAsStream("lowmcL1.bin.properties")));
            this.linearMatrices = LowmcConstantsL1.readArray(dataInputStream);
            this.roundConstants = LowmcConstantsL1.readArray(dataInputStream);
            this.keyMatrices = LowmcConstantsL1.readArray(dataInputStream);
            this.linearMatrices_full = LowmcConstantsL1.readArray(dataInputStream);
            this.keyMatrices_full = LowmcConstantsL1.readArray(dataInputStream);
            this.keyMatrices_inv = LowmcConstantsL1.readArray(dataInputStream);
            this.linearMatrices_inv = LowmcConstantsL1.readArray(dataInputStream);
            this.roundConstants_full = LowmcConstantsL1.readArray(dataInputStream);
        }
        catch (IOException iOException) {
            throw Exceptions.illegalStateException("unable to load Picnic properties: " + iOException.getMessage(), iOException);
        }
        this.LMatrix = new KMatrices(20, 128, 4, this.linearMatrices);
        this.KMatrix = new KMatrices(21, 128, 4, this.keyMatrices);
        this.RConstants = new KMatrices(0, 1, 4, this.roundConstants);
        this.LMatrix_full = new KMatrices(4, 129, 5, this.linearMatrices_full);
        this.LMatrix_inv = new KMatrices(4, 129, 5, this.linearMatrices_inv);
        this.KMatrix_full = new KMatrices(5, 129, 5, this.keyMatrices_full);
        this.KMatrix_inv = new KMatrices(1, 129, 5, this.keyMatrices_inv);
        this.RConstants_full = new KMatrices(4, 1, 5, this.roundConstants_full);
    }
}

