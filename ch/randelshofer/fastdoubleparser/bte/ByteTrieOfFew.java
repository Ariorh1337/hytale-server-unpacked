/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteTrie;
import ch.randelshofer.fastdoubleparser.bte.ByteTrieNode;
import java.nio.charset.StandardCharsets;
import java.util.Set;

final class ByteTrieOfFew
implements ByteTrie {
    private ByteTrieNode root = new ByteTrieNode();

    public ByteTrieOfFew(Set<String> set) {
        for (String str : set) {
            if (str.isEmpty()) continue;
            this.add(str);
        }
    }

    private void add(String str) {
        ByteTrieNode node = this.root;
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < strBytes.length; ++i) {
            node = node.insert(strBytes[i]);
        }
        node.setEnd();
    }

    @Override
    public int match(byte[] str, int startIndex, int endIndex) {
        ByteTrieNode node = this.root;
        int longestMatch = startIndex;
        for (int i = startIndex; i < endIndex && (node = node.get(str[i])) != null; ++i) {
            longestMatch = node.isEnd() ? i + 1 : longestMatch;
        }
        return longestMatch - startIndex;
    }
}

