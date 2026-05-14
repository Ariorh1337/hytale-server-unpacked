package org.rocksdb;

import java.util.ArrayList;
import java.util.List;

public class OptionString {
   private static final char kvPairSeparator = ';';
   private static final char kvSeparator = '=';
   private static final char complexValueBegin = '{';
   private static final char complexValueEnd = '}';
   private static final char wrappedValueBegin = '{';
   private static final char wrappedValueEnd = '}';
   private static final char arrayValueSeparator = ':';
   private static final char escapeChar = '\\';

   static class Entry {
      public final String key;
      public final OptionString.Value value;

      private Entry(String var1, OptionString.Value var2) {
         this.key = var1;
         this.value = var2;
      }

      @Override
      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   static class Parser {
      final String str;
      final StringBuilder sb;

      private Parser(String var1) {
         this.str = var1;
         this.sb = new StringBuilder(var1);
      }

      private void exception(String var1) {
         int var2 = this.str.length() - this.sb.length();
         int var3 = Math.min(var2, 64);
         int var4 = Math.min(64, this.str.length() - var2);
         String var5 = this.str.substring(var2 - var3, var2) + "__*HERE*__" + this.str.substring(var2, var2 + var4);
         throw new OptionString.Parser.Exception(var1 + " at [" + var5 + "]");
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

         char var1 = this.sb.charAt(0);
         this.sb.delete(0, 1);
         return var1;
      }

      private boolean hasNext() {
         return this.sb.length() > 0;
      }

      private boolean isChar(char var1) {
         return this.sb.length() > 0 && this.sb.charAt(0) == var1;
      }

      private boolean isKeyChar() {
         if (!this.hasNext()) {
            return false;
         }

         char var1 = this.first();
         return Character.isAlphabetic(var1) || Character.isDigit(var1) || "_".indexOf(var1) != -1;
      }

      private boolean isValueChar() {
         if (!this.hasNext()) {
            return false;
         }

         char var1 = this.first();
         return Character.isAlphabetic(var1) || Character.isDigit(var1) || "_-+.[]".indexOf(var1) != -1;
      }

      private String parseKey() {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.next());

         while (this.isKeyChar()) {
            var1.append(this.next());
         }

         return var1.toString();
      }

      private String parseSimpleValue() {
         if (this.isChar('{')) {
            this.next();
            String var2 = this.parseSimpleValue();
            if (!this.isChar('}')) {
               this.exception("Expected to end a wrapped value with }");
            }

            this.next();
            return var2;
         } else {
            StringBuilder var1 = new StringBuilder();

            while (this.isValueChar()) {
               var1.append(this.next());
            }

            return var1.toString();
         }
      }

      private List<String> parseList() {
         ArrayList var1 = new ArrayList(1);

         while (true) {
            var1.add(this.parseSimpleValue());
            if (this.isChar('\\')) {
               this.next();
            }

            if (!this.isChar(':')) {
               return var1;
            }

            this.next();
         }
      }

      private OptionString.Entry parseOption() {
         this.skipWhite();
         if (!this.isKeyChar()) {
            this.exception("No valid key character(s) for key in key=value ");
         }

         String var1 = this.parseKey();
         this.skipWhite();
         if (this.isChar('=')) {
            this.next();
         } else {
            this.exception("Expected = separating key and value");
         }

         this.skipWhite();
         OptionString.Value var2 = this.parseValue();
         return new OptionString.Entry(var1, var2);
      }

      private OptionString.Value parseValue() {
         this.skipWhite();
         if (this.isChar('{')) {
            this.next();
            this.skipWhite();
            OptionString.Value var2 = OptionString.Value.fromComplex(this.parseComplex());
            this.skipWhite();
            if (this.isChar('}')) {
               this.next();
               this.skipWhite();
            } else {
               this.exception("Expected } ending complex value");
            }

            return var2;
         } else if (this.isValueChar()) {
            return OptionString.Value.fromList(this.parseList());
         } else if (this.isChar(';')) {
            ArrayList var1 = new ArrayList();
            return OptionString.Value.fromList(var1);
         } else {
            this.exception("No valid value character(s) for value in key=value");
            return null;
         }
      }

      private List<OptionString.Entry> parseComplex() {
         ArrayList var1 = new ArrayList();
         this.skipWhite();
         if (this.hasNext()) {
            var1.add(this.parseOption());
            this.skipWhite();

            while (this.isChar(';')) {
               this.next();
               this.skipWhite();
               if (!this.isKeyChar()) {
                  break;
               }

               var1.add(this.parseOption());
               this.skipWhite();
            }
         }

         return var1;
      }

      public static List<OptionString.Entry> parse(String var0) {
         OptionString.Parser var1 = new OptionString.Parser(var0);
         List var2 = var1.parseComplex();
         if (var1.hasNext()) {
            var1.exception("Unexpected end of parsing ");
         }

         return var2;
      }

      static class Exception extends RuntimeException {
         private static final long serialVersionUID = 752283782841276408L;

         public Exception(String var1) {
            super(var1);
         }
      }
   }

   static class Value {
      final List<String> list;
      final List<OptionString.Entry> complex;

      public Value(List<String> var1, List<OptionString.Entry> var2) {
         this.list = var1;
         this.complex = var2;
      }

      public boolean isList() {
         return this.list != null && this.complex == null;
      }

      public static OptionString.Value fromList(List<String> var0) {
         return new OptionString.Value(var0, null);
      }

      public static OptionString.Value fromComplex(List<OptionString.Entry> var0) {
         return new OptionString.Value(null, var0);
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.isList()) {
            for (String var3 : this.list) {
               var1.append(var3).append(':');
            }

            if (var1.length() > 0) {
               var1.delete(var1.length() - 1, var1.length());
            }
         } else {
            var1.append('[');

            for (OptionString.Entry var5 : this.complex) {
               var1.append(var5.toString()).append(';');
            }

            var1.append(']');
         }

         return var1.toString();
      }
   }
}
