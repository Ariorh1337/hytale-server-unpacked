/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

public interface Buffer {
    public int cursor();

    public int atChar(int var1);

    public int length();

    public int currChar();

    public int prevChar();

    public int nextChar();

    public boolean cursor(int var1);

    public int move(int var1);

    public boolean up();

    public boolean down();

    public boolean moveXY(int var1, int var2);

    public boolean clear();

    public boolean currChar(int var1);

    public void write(int var1);

    public void write(int var1, boolean var2);

    public void write(CharSequence var1);

    public void write(CharSequence var1, boolean var2);

    public boolean backspace();

    public int backspace(int var1);

    public boolean delete();

    public int delete(int var1);

    public String substring(int var1);

    public String substring(int var1, int var2);

    public String upToCursor();

    public String toString();

    public Buffer copy();

    public void copyFrom(Buffer var1);

    public void zeroOut();
}

