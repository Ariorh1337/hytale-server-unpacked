/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class NumberFormatSymbols {
    private final Set<Character> decimalSeparator;
    private final Set<Character> groupingSeparator;
    private final Set<String> exponentSeparator;
    private final Set<Character> minusSign;
    private final Set<Character> plusSign;
    private final Set<String> infinity;
    private final Set<String> nan;
    private final List<Character> digits;

    public NumberFormatSymbols(Set<Character> decimalSeparator, Set<Character> groupingSeparator, Set<String> exponentSeparator, Set<Character> minusSign, Set<Character> plusSign, Set<String> infinity, Set<String> nan, List<Character> digits) {
        if (Objects.requireNonNull(digits, "digits").size() != 10) {
            throw new IllegalArgumentException("digits list must have size 10");
        }
        this.decimalSeparator = new LinkedHashSet<Character>((Collection)Objects.requireNonNull(decimalSeparator, "decimalSeparator"));
        this.groupingSeparator = new LinkedHashSet<Character>((Collection)Objects.requireNonNull(groupingSeparator, "groupingSeparator"));
        this.exponentSeparator = new LinkedHashSet<String>((Collection)Objects.requireNonNull(exponentSeparator, "exponentSeparator"));
        this.minusSign = new LinkedHashSet<Character>((Collection)Objects.requireNonNull(minusSign, "minusSign"));
        this.plusSign = new LinkedHashSet<Character>((Collection)Objects.requireNonNull(plusSign, "plusSign"));
        this.infinity = new LinkedHashSet<String>((Collection)Objects.requireNonNull(infinity, "infinity"));
        this.nan = new LinkedHashSet<String>((Collection)Objects.requireNonNull(nan, "nan"));
        this.digits = new ArrayList<Character>(digits);
    }

    public NumberFormatSymbols(String decimalSeparators, String groupingSeparators, Collection<String> exponentSeparators, String minusSigns, String plusSigns, Collection<String> infinity, Collection<String> nan, String digits) {
        this(NumberFormatSymbols.toSet(decimalSeparators), NumberFormatSymbols.toSet(groupingSeparators), new LinkedHashSet<String>(exponentSeparators), NumberFormatSymbols.toSet(minusSigns), NumberFormatSymbols.toSet(plusSigns), new LinkedHashSet<String>(infinity), new LinkedHashSet<String>(nan), NumberFormatSymbols.toList(NumberFormatSymbols.expandDigits(digits)));
    }

    private static String expandDigits(String digits) {
        if (digits.length() == 10) {
            return digits;
        }
        if (digits.length() != 1) {
            throw new IllegalArgumentException("digits must have length 1 or 10, digits=\"" + digits + "\"");
        }
        StringBuilder buf = new StringBuilder(10);
        char zeroChar = digits.charAt(0);
        for (int i = 0; i < 10; ++i) {
            buf.append((char)(zeroChar + i));
        }
        return buf.toString();
    }

    public static NumberFormatSymbols fromDecimalFormatSymbols(DecimalFormatSymbols symbols) {
        ArrayList<Character> digits = new ArrayList<Character>(10);
        char zeroDigit = symbols.getZeroDigit();
        for (int i = 0; i < 10; ++i) {
            digits.add(Character.valueOf((char)(zeroDigit + i)));
        }
        return new NumberFormatSymbols(Collections.singleton(Character.valueOf(symbols.getDecimalSeparator())), Collections.singleton(Character.valueOf(symbols.getGroupingSeparator())), Collections.singleton(symbols.getExponentSeparator()), Collections.singleton(Character.valueOf(symbols.getMinusSign())), Collections.emptySet(), Collections.singleton(symbols.getInfinity()), Collections.singleton(symbols.getNaN()), digits);
    }

    public static NumberFormatSymbols fromDefault() {
        return new NumberFormatSymbols(Collections.singleton(Character.valueOf('.')), Collections.emptySet(), new HashSet<String>(Arrays.asList("e", "E")), Collections.singleton(Character.valueOf('-')), Collections.singleton(Character.valueOf('+')), Collections.singleton("Infinity"), Collections.singleton("NaN"), Arrays.asList(Character.valueOf('0'), Character.valueOf('1'), Character.valueOf('2'), Character.valueOf('3'), Character.valueOf('4'), Character.valueOf('5'), Character.valueOf('6'), Character.valueOf('7'), Character.valueOf('8'), Character.valueOf('9')));
    }

    private static List<Character> toList(String chars) {
        ArrayList<Character> set = new ArrayList<Character>(10);
        for (char ch : chars.toCharArray()) {
            set.add(Character.valueOf(ch));
        }
        return set;
    }

    private static Set<Character> toSet(String chars) {
        LinkedHashSet<Character> set = new LinkedHashSet<Character>(chars.length() * 2);
        for (char ch : chars.toCharArray()) {
            set.add(Character.valueOf(ch));
        }
        return set;
    }

    public Set<Character> decimalSeparator() {
        return this.decimalSeparator;
    }

    public List<Character> digits() {
        return this.digits;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        NumberFormatSymbols that = (NumberFormatSymbols)obj;
        return Objects.equals(this.decimalSeparator, that.decimalSeparator) && Objects.equals(this.groupingSeparator, that.groupingSeparator) && Objects.equals(this.exponentSeparator, that.exponentSeparator) && Objects.equals(this.minusSign, that.minusSign) && Objects.equals(this.plusSign, that.plusSign) && Objects.equals(this.infinity, that.infinity) && Objects.equals(this.nan, that.nan) && Objects.equals(this.digits, that.digits);
    }

    public Set<String> exponentSeparator() {
        return this.exponentSeparator;
    }

    public Set<Character> groupingSeparator() {
        return this.groupingSeparator;
    }

    public int hashCode() {
        return Objects.hash(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits);
    }

    public Set<String> infinity() {
        return this.infinity;
    }

    public Set<Character> minusSign() {
        return this.minusSign;
    }

    public Set<String> nan() {
        return this.nan;
    }

    public Set<Character> plusSign() {
        return this.plusSign;
    }

    public String toString() {
        return "NumberFormatSymbols[decimalSeparator=" + this.decimalSeparator + ", groupingSeparator=" + this.groupingSeparator + ", exponentSeparator=" + this.exponentSeparator + ", minusSign=" + this.minusSign + ", plusSign=" + this.plusSign + ", infinity=" + this.infinity + ", nan=" + this.nan + ", digits=" + this.digits + ']';
    }

    public NumberFormatSymbols withDecimalSeparator(Set<Character> newValue) {
        return new NumberFormatSymbols(newValue, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits);
    }

    public NumberFormatSymbols withDigits(List<Character> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, newValue);
    }

    public NumberFormatSymbols withExponentSeparator(Set<String> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, newValue, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits);
    }

    public NumberFormatSymbols withGroupingSeparator(Set<Character> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, newValue, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, this.nan, this.digits);
    }

    public NumberFormatSymbols withInfinity(Set<String> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, newValue, this.nan, this.digits);
    }

    public NumberFormatSymbols withMinusSign(Set<Character> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, newValue, this.plusSign, this.infinity, this.nan, this.digits);
    }

    public NumberFormatSymbols withNaN(Set<String> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, this.plusSign, this.infinity, newValue, this.digits);
    }

    public NumberFormatSymbols withPlusSign(Set<Character> newValue) {
        return new NumberFormatSymbols(this.decimalSeparator, this.groupingSeparator, this.exponentSeparator, this.minusSign, newValue, this.infinity, this.nan, this.digits);
    }
}

