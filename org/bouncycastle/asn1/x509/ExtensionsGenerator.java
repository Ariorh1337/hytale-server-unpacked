/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class ExtensionsGenerator {
    private Hashtable extensions = new Hashtable();
    private Vector extOrdering = new Vector();
    private static final Set dupsAllowed;

    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws IOException {
        Extension extension = (Extension)this.extensions.get(aSN1ObjectIdentifier);
        if (extension != null) {
            this.implAddExtensionDup(extension, bl, aSN1Encodable.toASN1Primitive().getEncoded("DER"));
        } else {
            this.implAddExtension(new Extension(aSN1ObjectIdentifier, bl, (ASN1OctetString)new DEROctetString(aSN1Encodable)));
        }
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) {
        Extension extension = (Extension)this.extensions.get(aSN1ObjectIdentifier);
        if (extension != null) {
            this.implAddExtensionDup(extension, bl, byArray);
        } else {
            this.implAddExtension(new Extension(aSN1ObjectIdentifier, bl, byArray));
        }
    }

    public void addExtension(Extension extension) {
        if (this.hasExtension(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " already added");
        }
        this.implAddExtension(extension);
    }

    public void addExtension(Extensions extensions) {
        this.addExtensions(extensions);
    }

    public void addExtensions(Extensions extensions) {
        ASN1ObjectIdentifier[] aSN1ObjectIdentifierArray = extensions.getExtensionOIDs();
        for (int i = 0; i != aSN1ObjectIdentifierArray.length; ++i) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = aSN1ObjectIdentifierArray[i];
            Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
            this.addExtension(ASN1ObjectIdentifier.getInstance(aSN1ObjectIdentifier), extension.isCritical(), extension.getExtnValue().getOctets());
        }
    }

    public void replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws IOException {
        this.replaceExtension(new Extension(aSN1ObjectIdentifier, bl, (ASN1OctetString)new DEROctetString(aSN1Encodable)));
    }

    public void replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) {
        this.replaceExtension(new Extension(aSN1ObjectIdentifier, bl, byArray));
    }

    public void replaceExtension(Extension extension) {
        if (!this.hasExtension(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " not present");
        }
        this.extensions.put(extension.getExtnId(), extension);
    }

    public void removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (!this.hasExtension(aSN1ObjectIdentifier)) {
            throw new IllegalArgumentException("extension " + aSN1ObjectIdentifier + " not present");
        }
        this.extOrdering.removeElement(aSN1ObjectIdentifier);
        this.extensions.remove(aSN1ObjectIdentifier);
    }

    public boolean hasExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.extensions.containsKey(aSN1ObjectIdentifier);
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (Extension)this.extensions.get(aSN1ObjectIdentifier);
    }

    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }

    public Extensions generate() {
        Extension[] extensionArray = new Extension[this.extOrdering.size()];
        for (int i = 0; i != this.extOrdering.size(); ++i) {
            extensionArray[i] = (Extension)this.extensions.get(this.extOrdering.elementAt(i));
        }
        return new Extensions(extensionArray);
    }

    private void implAddExtension(Extension extension) {
        this.extOrdering.addElement(extension.getExtnId());
        this.extensions.put(extension.getExtnId(), extension);
    }

    private void implAddExtensionDup(Extension extension, boolean bl, byte[] byArray) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = extension.getExtnId();
        if (!dupsAllowed.contains(aSN1ObjectIdentifier)) {
            throw new IllegalArgumentException("extension " + aSN1ObjectIdentifier + " already added");
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(DEROctetString.getInstance(extension.getExtnValue()).getOctets());
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(byArray);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(aSN1Sequence.size() + aSN1Sequence2.size());
        Enumeration enumeration = aSN1Sequence.getObjects();
        while (enumeration.hasMoreElements()) {
            aSN1EncodableVector.add((ASN1Encodable)enumeration.nextElement());
        }
        enumeration = aSN1Sequence2.getObjects();
        while (enumeration.hasMoreElements()) {
            aSN1EncodableVector.add((ASN1Encodable)enumeration.nextElement());
        }
        try {
            this.extensions.put(aSN1ObjectIdentifier, new Extension(aSN1ObjectIdentifier, bl, (ASN1OctetString)new DEROctetString(new DERSequence(aSN1EncodableVector))));
        }
        catch (IOException iOException) {
            throw new ASN1ParsingException(iOException.getMessage(), iOException);
        }
    }

    static {
        HashSet<ASN1ObjectIdentifier> hashSet = new HashSet<ASN1ObjectIdentifier>();
        hashSet.add(Extension.subjectAlternativeName);
        hashSet.add(Extension.issuerAlternativeName);
        hashSet.add(Extension.subjectDirectoryAttributes);
        hashSet.add(Extension.certificateIssuer);
        dupsAllowed = Collections.unmodifiableSet(hashSet);
    }
}

