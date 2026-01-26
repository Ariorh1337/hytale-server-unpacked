/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.PKIXRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.util.Iterable;

public class RecipientInformationStore
implements Iterable<RecipientInformation> {
    private final List all;
    private final Map table = new HashMap();

    public RecipientInformationStore(RecipientInformation recipientInformation) {
        this.all = new ArrayList(1);
        this.all.add(recipientInformation);
        RecipientId recipientId = recipientInformation.getRID();
        this.table.put(recipientId, this.all);
    }

    public RecipientInformationStore(Collection<RecipientInformation> collection) {
        for (RecipientInformation recipientInformation : collection) {
            RecipientId recipientId = recipientInformation.getRID();
            ArrayList<RecipientInformation> arrayList = (ArrayList<RecipientInformation>)this.table.get(recipientId);
            if (arrayList == null) {
                arrayList = new ArrayList<RecipientInformation>(1);
                this.table.put(recipientId, arrayList);
            }
            arrayList.add(recipientInformation);
        }
        this.all = new ArrayList<RecipientInformation>(collection);
    }

    public RecipientInformation get(RecipientId recipientId) {
        Collection<RecipientInformation> collection = this.getRecipients(recipientId);
        return collection.size() == 0 ? null : collection.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<RecipientInformation> getRecipients() {
        return new ArrayList<RecipientInformation>(this.all);
    }

    public Collection<RecipientInformation> getRecipients(RecipientId recipientId) {
        Cloneable cloneable;
        if (recipientId instanceof PKIXRecipientId) {
            cloneable = (PKIXRecipientId)recipientId;
            X500Name x500Name = ((PKIXRecipientId)cloneable).getIssuer();
            byte[] byArray = ((PKIXRecipientId)cloneable).getSubjectKeyIdentifier();
            if (x500Name != null && byArray != null) {
                ArrayList arrayList;
                ArrayList<RecipientInformation> arrayList2 = new ArrayList<RecipientInformation>();
                ArrayList arrayList3 = (ArrayList)this.table.get(new PKIXRecipientId(((RecipientId)cloneable).getType(), x500Name, ((PKIXRecipientId)cloneable).getSerialNumber(), null));
                if (arrayList3 != null) {
                    arrayList2.addAll(arrayList3);
                }
                if ((arrayList = (ArrayList)this.table.get(new PKIXRecipientId(((RecipientId)cloneable).getType(), null, null, byArray))) != null) {
                    arrayList2.addAll(arrayList);
                }
                return arrayList2;
            }
        }
        return (cloneable = (ArrayList)this.table.get(recipientId)) == null ? new ArrayList<RecipientInformation>() : new ArrayList(cloneable);
    }

    @Override
    public Iterator<RecipientInformation> iterator() {
        return this.getRecipients().iterator();
    }
}

