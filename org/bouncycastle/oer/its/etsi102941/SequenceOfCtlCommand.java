/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.CtlCommand;

public class SequenceOfCtlCommand
extends ASN1Object {
    private final List<CtlCommand> ctlCommands;

    public SequenceOfCtlCommand(List<CtlCommand> list) {
        this.ctlCommands = Collections.unmodifiableList(list);
    }

    private SequenceOfCtlCommand(ASN1Sequence aSN1Sequence) {
        ArrayList<CtlCommand> arrayList = new ArrayList<CtlCommand>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(CtlCommand.getInstance(iterator.next()));
        }
        this.ctlCommands = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfCtlCommand getInstance(Object object) {
        if (object instanceof SequenceOfCtlCommand) {
            return (SequenceOfCtlCommand)object;
        }
        if (object != null) {
            return new SequenceOfCtlCommand(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<CtlCommand> getCtlCommands() {
        return this.ctlCommands;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.ctlCommands.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<CtlCommand> items = new ArrayList<CtlCommand>();

        public Builder addHashId8(CtlCommand ... ctlCommandArray) {
            this.items.addAll(Arrays.asList(ctlCommandArray));
            return this;
        }

        public SequenceOfCtlCommand build() {
            return new SequenceOfCtlCommand(this.items);
        }
    }
}

