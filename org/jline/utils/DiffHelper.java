/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.util.LinkedList;
import java.util.List;
import org.jline.utils.AttributedString;

public class DiffHelper {
    public static List<Diff> diff(AttributedString text1, AttributedString text2) {
        int commonEnd;
        int commonStart;
        int l1 = text1.length();
        int l2 = text2.length();
        int n = Math.min(l1, l2);
        int startHiddenRange = -1;
        for (commonStart = 0; commonStart < n && text1.charAt(commonStart) == text2.charAt(commonStart) && text1.styleAt(commonStart).equals(text2.styleAt(commonStart)); ++commonStart) {
            if (text1.isHidden(commonStart)) {
                if (startHiddenRange >= 0) continue;
                startHiddenRange = commonStart;
                continue;
            }
            startHiddenRange = -1;
        }
        if (startHiddenRange >= 0 && (l1 > commonStart && text1.isHidden(commonStart) || l2 > commonStart && text2.isHidden(commonStart))) {
            commonStart = startHiddenRange;
        }
        startHiddenRange = -1;
        for (commonEnd = 0; commonEnd < n - commonStart && text1.charAt(l1 - commonEnd - 1) == text2.charAt(l2 - commonEnd - 1) && text1.styleAt(l1 - commonEnd - 1).equals(text2.styleAt(l2 - commonEnd - 1)); ++commonEnd) {
            if (text1.isHidden(l1 - commonEnd - 1)) {
                if (startHiddenRange >= 0) continue;
                startHiddenRange = commonEnd;
                continue;
            }
            startHiddenRange = -1;
        }
        if (startHiddenRange >= 0) {
            commonEnd = startHiddenRange;
        }
        LinkedList<Diff> diffs = new LinkedList<Diff>();
        if (commonStart > 0) {
            diffs.add(new Diff(Operation.EQUAL, text1.subSequence(0, commonStart)));
        }
        if (l2 > commonStart + commonEnd) {
            diffs.add(new Diff(Operation.INSERT, text2.subSequence(commonStart, l2 - commonEnd)));
        }
        if (l1 > commonStart + commonEnd) {
            diffs.add(new Diff(Operation.DELETE, text1.subSequence(commonStart, l1 - commonEnd)));
        }
        if (commonEnd > 0) {
            diffs.add(new Diff(Operation.EQUAL, text1.subSequence(l1 - commonEnd, l1)));
        }
        return diffs;
    }

    public static class Diff {
        public final Operation operation;
        public final AttributedString text;

        public Diff(Operation operation, AttributedString text) {
            this.operation = operation;
            this.text = text;
        }

        public String toString() {
            return "Diff(" + (Object)((Object)this.operation) + ",\"" + this.text + "\")";
        }
    }

    public static enum Operation {
        DELETE,
        INSERT,
        EQUAL;

    }
}

