/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;

public class OERDecoder {
    public static ASN1Encodable decode(byte[] byArray, Element element) throws IOException {
        return OERDecoder.decode(new ByteArrayInputStream(byArray), element);
    }

    public static ASN1Encodable decode(InputStream inputStream, Element element) throws IOException {
        OERInputStream oERInputStream = new OERInputStream(inputStream);
        return oERInputStream.parse(element);
    }
}

