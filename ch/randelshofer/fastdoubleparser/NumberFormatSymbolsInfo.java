/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.FormatCharSet;
import java.util.Collection;
import java.util.Set;

class NumberFormatSymbolsInfo {
    NumberFormatSymbolsInfo() {
    }

    static boolean isAscii(NumberFormatSymbols symbols) {
        return NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.decimalSeparator()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.groupingSeparator()) && NumberFormatSymbolsInfo.isAsciiStringCollection(symbols.exponentSeparator()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.minusSign()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.plusSign()) && NumberFormatSymbolsInfo.isAsciiStringCollection(symbols.infinity()) && NumberFormatSymbolsInfo.isAsciiStringCollection(symbols.nan()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.digits());
    }

    static boolean isMostlyAscii(NumberFormatSymbols symbols) {
        return NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.decimalSeparator()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.groupingSeparator()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.minusSign()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.plusSign()) && NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.digits());
    }

    static boolean isDigitsTokensAscii(NumberFormatSymbols symbols) {
        return NumberFormatSymbolsInfo.isAsciiCharCollection(symbols.digits());
    }

    static boolean isAsciiStringCollection(Collection<String> collection) {
        for (String str : collection) {
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (ch <= '\u007f') continue;
                return false;
            }
        }
        return true;
    }

    static boolean isAsciiCharCollection(Collection<Character> collection) {
        for (char ch : collection) {
            if (ch <= '\u007f') continue;
            return false;
        }
        return true;
    }

    static boolean containsFormatChars(NumberFormatSymbols symbols) {
        FormatCharSet formatCharSet = new FormatCharSet();
        return NumberFormatSymbolsInfo.containsChars(symbols.decimalSeparator(), (CharSet)formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.groupingSeparator(), (CharSet)formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.exponentSeparator(), formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.minusSign(), (CharSet)formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.plusSign(), (CharSet)formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.infinity(), formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.nan(), formatCharSet) || NumberFormatSymbolsInfo.containsChars(symbols.digits(), (CharSet)formatCharSet);
    }

    private static boolean containsChars(Set<String> strings, FormatCharSet set) {
        for (String str : strings) {
            int n = str.length();
            for (int i = 0; i < n; ++i) {
                if (!set.containsKey(str.charAt(i))) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean containsChars(Collection<Character> characters, CharSet set) {
        for (char ch : characters) {
            if (!set.containsKey(ch)) continue;
            return true;
        }
        return false;
    }
}

