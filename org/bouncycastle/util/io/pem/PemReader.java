/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io.pem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;

public class PemReader
extends BufferedReader {
    public static final String LAX_PEM_PARSING_SYSTEM_PROPERTY_NAME = "org.bouncycastle.pemreader.lax";
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";
    private static final Logger LOG = Logger.getLogger(PemReader.class.getName());

    public PemReader(Reader reader) {
        super(reader);
    }

    public PemObject readPemObject() throws IOException {
        int n;
        String string = this.readLine();
        while (string != null && !string.startsWith(BEGIN)) {
            string = this.readLine();
        }
        if (string != null && (n = (string = string.substring(BEGIN.length()).trim()).indexOf(45)) > 0 && string.endsWith("-----") && string.length() - n == 5) {
            String string2 = string.substring(0, n);
            return this.loadObject(string2);
        }
        return null;
    }

    private PemObject loadObject(String string) throws IOException {
        String string2;
        String string3 = END + string + "-----";
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<PemHeader> arrayList = new ArrayList<PemHeader>();
        while ((string2 = this.readLine()) != null) {
            String string4;
            int n = string2.indexOf(58);
            if (n >= 0) {
                string4 = string2.substring(0, n);
                String string5 = string2.substring(n + 1).trim();
                arrayList.add(new PemHeader(string4, string5));
                continue;
            }
            if (System.getProperty(LAX_PEM_PARSING_SYSTEM_PROPERTY_NAME, "false").equalsIgnoreCase("true")) {
                string4 = string2.trim();
                if (!string4.equals(string2) && LOG.isLoggable(Level.WARNING)) {
                    LOG.log(Level.WARNING, "PEM object contains whitespaces on -----END line", new Exception("trace"));
                }
                string2 = string4;
            }
            if (string2.indexOf(string3) == 0) break;
            stringBuffer.append(string2.trim());
        }
        if (string2 == null) {
            throw new IOException(string3 + " not found");
        }
        return new PemObject(string, arrayList, Base64.decode(stringBuffer.toString()));
    }
}

