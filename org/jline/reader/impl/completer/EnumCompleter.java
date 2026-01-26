/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl.completer;

import java.util.Objects;
import org.jline.reader.Candidate;
import org.jline.reader.impl.completer.StringsCompleter;

public class EnumCompleter
extends StringsCompleter {
    public EnumCompleter(Class<? extends Enum<?>> source) {
        Objects.requireNonNull(source);
        for (Enum<?> n : source.getEnumConstants()) {
            this.candidates.add(new Candidate(n.name().toLowerCase()));
        }
    }
}

