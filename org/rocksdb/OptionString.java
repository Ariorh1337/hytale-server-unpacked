/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OptionString {
    private static final char kvPairSeparator = ';';
    private static final char kvSeparator = '=';
    private static final char complexValueBegin = '{';
    private static final char complexValueEnd = '}';
    private static final char wrappedValueBegin = '{';
    private static final char wrappedValueEnd = '}';
    private static final char arrayValueSeparator = ':';
    private static final char escapeChar = '\\';

    static class Parser {
        final String str;
        final StringBuilder sb;

        private Parser(String string) {
            this.str = string;
            this.sb = new StringBuilder(string);
        }

        private void exception(String string) {
            int n = this.str.length() - this.sb.length();
            int n2 = Math.min(n, 64);
            int n3 = Math.min(64, this.str.length() - n);
            String string2 = this.str.substring(n - n2, n) + "__*HERE*__" + this.str.substring(n, n + n3);
            throw new Exception(string + " at [" + string2 + "]");
        }

        private void skipWhite() {
            while (this.sb.length() > 0 && Character.isWhitespace(this.sb.charAt(0))) {
                this.sb.delete(0, 1);
            }
        }

        private char first() {
            if (this.sb.length() == 0) {
                this.exception("Unexpected end of input");
            }
            return this.sb.charAt(0);
        }

        private char next() {
            if (this.sb.length() == 0) {
                this.exception("Unexpected end of input");
            }
            char c = this.sb.charAt(0);
            this.sb.delete(0, 1);
            return c;
        }

        private boolean hasNext() {
            return this.sb.length() > 0;
        }

        private boolean isChar(char c) {
            return this.sb.length() > 0 && this.sb.charAt(0) == c;
        }

        private boolean isKeyChar() {
            if (!this.hasNext()) {
                return false;
            }
            char c = this.first();
            return Character.isAlphabetic(c) || Character.isDigit(c) || "_".indexOf(c) != -1;
        }

        private boolean isValueChar() {
            if (!this.hasNext()) {
                return false;
            }
            char c = this.first();
            return Character.isAlphabetic(c) || Character.isDigit(c) || "_-+.[]".indexOf(c) != -1;
        }

        private String parseKey() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.next());
            while (this.isKeyChar()) {
                stringBuilder.append(this.next());
            }
            return stringBuilder.toString();
        }

        private String parseSimpleValue() {
            if (this.isChar('{')) {
                this.next();
                String string = this.parseSimpleValue();
                if (!this.isChar('}')) {
                    this.exception("Expected to end a wrapped value with }");
                }
                this.next();
                return string;
            }
            StringBuilder stringBuilder = new StringBuilder();
            while (this.isValueChar()) {
                stringBuilder.append(this.next());
            }
            return stringBuilder.toString();
        }

        private List<String> parseList() {
            ArrayList<String> arrayList = new ArrayList<String>(1);
            while (true) {
                arrayList.add(this.parseSimpleValue());
                if (this.isChar('\\')) {
                    this.next();
                }
                if (!this.isChar(':')) break;
                this.next();
            }
            return arrayList;
        }

        private Entry parseOption() {
            this.skipWhite();
            if (!this.isKeyChar()) {
                this.exception("No valid key character(s) for key in key=value ");
            }
            String string = this.parseKey();
            this.skipWhite();
            if (this.isChar('=')) {
                this.next();
            } else {
                this.exception("Expected = separating key and value");
            }
            this.skipWhite();
            Value value = this.parseValue();
            return new Entry(string, value);
        }

        private Value parseValue() {
            this.skipWhite();
            if (this.isChar('{')) {
                this.next();
                this.skipWhite();
                Value value = Value.fromComplex(this.parseComplex());
                this.skipWhite();
                if (this.isChar('}')) {
                    this.next();
                    this.skipWhite();
                } else {
                    this.exception("Expected } ending complex value");
                }
                return value;
            }
            if (this.isValueChar()) {
                return Value.fromList(this.parseList());
            }
            if (this.isChar(';')) {
                ArrayList<String> arrayList = new ArrayList<String>();
                return Value.fromList(arrayList);
            }
            this.exception("No valid value character(s) for value in key=value");
            return null;
        }

        private List<Entry> parseComplex() {
            ArrayList<Entry> arrayList = new ArrayList<Entry>();
            this.skipWhite();
            if (this.hasNext()) {
                arrayList.add(this.parseOption());
                this.skipWhite();
                while (this.isChar(';')) {
                    this.next();
                    this.skipWhite();
                    if (!this.isKeyChar()) break;
                    arrayList.add(this.parseOption());
                    this.skipWhite();
                }
            }
            return arrayList;
        }

        public static List<Entry> parse(String string) {
            Objects.requireNonNull(string);
            Parser parser = new Parser(string);
            List<Entry> list = parser.parseComplex();
            if (parser.hasNext()) {
                parser.exception("Unexpected end of parsing ");
            }
            return list;
        }

        static class Exception
        extends RuntimeException {
            private static final long serialVersionUID = 752283782841276408L;

            public Exception(String string) {
                super(string);
            }
        }
    }

    static class Entry {
        public final String key;
        public final Value value;

        private Entry(String string, Value value) {
            this.key = string;
            this.value = value;
        }

        public String toString() {
            return "" + this.key + "=" + this.value;
        }
    }

    static class Value {
        final List<String> list;
        final List<Entry> complex;

        public Value(List<String> list, List<Entry> list2) {
            this.list = list;
            this.complex = list2;
        }

        public boolean isList() {
            return this.list != null && this.complex == null;
        }

        public static Value fromList(List<String> list) {
            return new Value(list, null);
        }

        public static Value fromComplex(List<Entry> list) {
            return new Value(null, list);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.isList()) {
                for (String string : this.list) {
                    stringBuilder.append(string).append(':');
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
                }
            } else {
                stringBuilder.append('[');
                for (Entry entry : this.complex) {
                    stringBuilder.append(entry.toString()).append(';');
                }
                stringBuilder.append(']');
            }
            return stringBuilder.toString();
        }
    }
}

