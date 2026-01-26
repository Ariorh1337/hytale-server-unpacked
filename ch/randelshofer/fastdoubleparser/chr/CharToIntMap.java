/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import java.util.Collection;

final class CharToIntMap
implements CharDigitSet,
CharSet {
    private Node[] table;

    public CharToIntMap(Collection<Character> chars) {
        this(chars.size());
        int i = 0;
        for (char ch : chars) {
            this.put(ch, i++);
        }
    }

    @Override
    public boolean containsKey(char key) {
        return this.getOrDefault(key, -1) >= 0;
    }

    @Override
    public int toDigit(char ch) {
        return this.getOrDefault(ch, 10);
    }

    public CharToIntMap(int maxSize) {
        int n = (-1 >>> Integer.numberOfLeadingZeros(maxSize * 2)) + 1;
        this.table = new Node[n];
    }

    public void put(char key, int value) {
        int index = this.getIndex(key);
        Node found = this.table[index];
        if (found == null) {
            this.table[index] = new Node(key, value);
        } else {
            while (found.next != null && found.key != key) {
                found = found.next;
            }
            if (found.key == key) {
                found.value = value;
            } else {
                found.next = new Node(key, value);
            }
        }
    }

    private int getIndex(char key) {
        return key & this.table.length - 1;
    }

    public int getOrDefault(char key, int defaultValue) {
        int index = this.getIndex(key);
        Node found = this.table[index];
        while (found != null) {
            if (found.key == key) {
                return found.value;
            }
            found = found.next;
        }
        return defaultValue;
    }

    private static class Node {
        char key;
        int value;
        Node next;

        public Node(char key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}

