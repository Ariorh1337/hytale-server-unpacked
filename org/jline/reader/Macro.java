/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import org.jline.reader.Binding;

public class Macro
implements Binding {
    private final String sequence;

    public Macro(String sequence) {
        this.sequence = sequence;
    }

    public String getSequence() {
        return this.sequence;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Macro macro = (Macro)o;
        return this.sequence.equals(macro.sequence);
    }

    public int hashCode() {
        return this.sequence.hashCode();
    }

    public String toString() {
        return "Macro[" + this.sequence + ']';
    }
}

