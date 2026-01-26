/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.util.HashMap;

public class Levenshtein {
    public static int distance(CharSequence lhs, CharSequence rhs) {
        return Levenshtein.distance(lhs, rhs, 1, 1, 1, 1);
    }

    public static int distance(CharSequence source, CharSequence target, int deleteCost, int insertCost, int replaceCost, int swapCost) {
        int matchDistance;
        int insertDistance;
        int deleteDistance;
        int i;
        if (2 * swapCost < insertCost + deleteCost) {
            throw new IllegalArgumentException("Unsupported cost assignment");
        }
        if (source.length() == 0) {
            return target.length() * insertCost;
        }
        if (target.length() == 0) {
            return source.length() * deleteCost;
        }
        int[][] table = new int[source.length()][target.length()];
        HashMap<Character, Integer> sourceIndexByCharacter = new HashMap<Character, Integer>();
        if (source.charAt(0) != target.charAt(0)) {
            table[0][0] = Math.min(replaceCost, deleteCost + insertCost);
        }
        sourceIndexByCharacter.put(Character.valueOf(source.charAt(0)), 0);
        for (i = 1; i < source.length(); ++i) {
            deleteDistance = table[i - 1][0] + deleteCost;
            insertDistance = (i + 1) * deleteCost + insertCost;
            matchDistance = i * deleteCost + (source.charAt(i) == target.charAt(0) ? 0 : replaceCost);
            table[i][0] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
        }
        for (int j = 1; j < target.length(); ++j) {
            deleteDistance = (j + 1) * insertCost + deleteCost;
            insertDistance = table[0][j - 1] + insertCost;
            matchDistance = j * insertCost + (source.charAt(0) == target.charAt(j) ? 0 : replaceCost);
            table[0][j] = Math.min(Math.min(deleteDistance, insertDistance), matchDistance);
        }
        for (i = 1; i < source.length(); ++i) {
            int maxSourceLetterMatchIndex = source.charAt(i) == target.charAt(0) ? 0 : -1;
            for (int j = 1; j < target.length(); ++j) {
                int swapDistance;
                Integer candidateSwapIndex = (Integer)sourceIndexByCharacter.get(Character.valueOf(target.charAt(j)));
                int jSwap = maxSourceLetterMatchIndex;
                int deleteDistance2 = table[i - 1][j] + deleteCost;
                int insertDistance2 = table[i][j - 1] + insertCost;
                int matchDistance2 = table[i - 1][j - 1];
                if (source.charAt(i) != target.charAt(j)) {
                    matchDistance2 += replaceCost;
                } else {
                    maxSourceLetterMatchIndex = j;
                }
                if (candidateSwapIndex != null && jSwap != -1) {
                    int iSwap = candidateSwapIndex;
                    int preSwapCost = iSwap == 0 && jSwap == 0 ? 0 : table[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
                    swapDistance = preSwapCost + (i - iSwap - 1) * deleteCost + (j - jSwap - 1) * insertCost + swapCost;
                } else {
                    swapDistance = Integer.MAX_VALUE;
                }
                table[i][j] = Math.min(Math.min(Math.min(deleteDistance2, insertDistance2), matchDistance2), swapDistance);
            }
            sourceIndexByCharacter.put(Character.valueOf(source.charAt(i)), i);
        }
        return table[source.length() - 1][target.length() - 1];
    }
}

