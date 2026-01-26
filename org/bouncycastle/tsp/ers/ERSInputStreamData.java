/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.tsp.ers.ExpUtil;
import org.bouncycastle.util.io.Streams;

public class ERSInputStreamData
extends ERSCachingData {
    private final File contentFile;
    private final byte[] contentBytes;

    public ERSInputStreamData(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("directory not allowed");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file + " not found");
        }
        this.contentBytes = null;
        this.contentFile = file;
    }

    public ERSInputStreamData(InputStream inputStream) {
        try {
            this.contentBytes = Streams.readAll(inputStream);
        }
        catch (IOException iOException) {
            throw ExpUtil.createIllegalState("unable to open content: " + iOException.getMessage(), iOException);
        }
        this.contentFile = null;
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] byArray) {
        byte[] byArray2;
        if (this.contentBytes != null) {
            byArray2 = ERSUtil.calculateDigest(digestCalculator, this.contentBytes);
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.contentFile);
                byArray2 = ERSUtil.calculateDigest(digestCalculator, fileInputStream);
                ((InputStream)fileInputStream).close();
            }
            catch (IOException iOException) {
                throw ExpUtil.createIllegalState("unable to open content: " + iOException.getMessage(), iOException);
            }
        }
        if (byArray != null) {
            return ERSUtil.concatPreviousHashes(digestCalculator, byArray, byArray2);
        }
        return byArray2;
    }
}

