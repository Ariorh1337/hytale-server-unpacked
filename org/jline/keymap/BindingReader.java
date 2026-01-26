/*
 * Decompiled with CFR 0.152.
 */
package org.jline.keymap;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jline.keymap.KeyMap;
import org.jline.reader.EndOfFileException;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlockingReader;

public class BindingReader {
    protected final NonBlockingReader reader;
    protected final StringBuilder opBuffer = new StringBuilder();
    protected final Deque<Integer> pushBackChar = new ArrayDeque<Integer>();
    protected String lastBinding;

    public BindingReader(NonBlockingReader reader) {
        this.reader = reader;
    }

    public <T> T readBinding(KeyMap<T> keys) {
        return this.readBinding(keys, null, true);
    }

    public <T> T readBinding(KeyMap<T> keys, KeyMap<T> local) {
        return this.readBinding(keys, local, true);
    }

    public <T> T readBinding(KeyMap<T> keys, KeyMap<T> local, boolean block) {
        this.lastBinding = null;
        T o = null;
        int[] remaining = new int[1];
        boolean hasRead = false;
        while (true) {
            if (local != null) {
                o = local.getBound(this.opBuffer, remaining);
            }
            if (o == null && (local == null || remaining[0] >= 0)) {
                o = keys.getBound(this.opBuffer, remaining);
            }
            if (o != null) {
                if (remaining[0] >= 0) {
                    this.runMacro(this.opBuffer.substring(this.opBuffer.length() - remaining[0]));
                    this.opBuffer.setLength(this.opBuffer.length() - remaining[0]);
                } else {
                    long ambiguousTimeout = keys.getAmbiguousTimeout();
                    if (ambiguousTimeout > 0L && this.peekCharacter(ambiguousTimeout) != -2) {
                        o = null;
                    }
                }
                if (o != null) {
                    this.lastBinding = this.opBuffer.toString();
                    this.opBuffer.setLength(0);
                    return o;
                }
            } else if (remaining[0] > 0) {
                int cp = this.opBuffer.codePointAt(0);
                String rem = this.opBuffer.substring(Character.charCount(cp));
                this.lastBinding = this.opBuffer.substring(0, Character.charCount(cp));
                o = cp >= 128 ? keys.getUnicode() : keys.getNomatch();
                this.opBuffer.setLength(0);
                this.opBuffer.append(rem);
                if (o != null) {
                    return o;
                }
            }
            if (!block && hasRead) break;
            int c = this.readCharacter();
            if (c == -1) {
                return null;
            }
            this.opBuffer.appendCodePoint(c);
            hasRead = true;
        }
        return null;
    }

    public String readStringUntil(String sequence) {
        StringBuilder sb = new StringBuilder();
        if (!this.pushBackChar.isEmpty()) {
            this.pushBackChar.forEach(sb::appendCodePoint);
        }
        try {
            char[] buf = new char[64];
            while (true) {
                int idx;
                if ((idx = sb.indexOf(sequence, Math.max(0, sb.length() - buf.length - sequence.length()))) >= 0) {
                    String rem = sb.substring(idx + sequence.length());
                    this.runMacro(rem);
                    return sb.substring(0, idx);
                }
                int l = this.reader.readBuffered(buf);
                if (l < 0) {
                    throw new ClosedException();
                }
                sb.append(buf, 0, l);
            }
        }
        catch (ClosedException e) {
            throw new EndOfFileException(e);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    public int readCharacter() {
        if (!this.pushBackChar.isEmpty()) {
            return this.pushBackChar.pop();
        }
        try {
            int c = -2;
            int s = 0;
            while (c == -2) {
                c = this.reader.read(100L);
                if (c < 0 || !Character.isHighSurrogate((char)c)) continue;
                s = c;
                c = -2;
            }
            return s != 0 ? Character.toCodePoint((char)s, (char)c) : c;
        }
        catch (ClosedException e) {
            throw new EndOfFileException(e);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    public int readCharacterBuffered() {
        try {
            if (this.pushBackChar.isEmpty()) {
                char[] buf = new char[32];
                int l = this.reader.readBuffered(buf);
                if (l <= 0) {
                    return -1;
                }
                char s = '\u0000';
                int i = 0;
                while (i < l) {
                    char c;
                    if (Character.isHighSurrogate(c = buf[i++])) {
                        s = c;
                        if (i >= l) break;
                        c = buf[i++];
                        this.pushBackChar.addLast(Character.toCodePoint(s, c));
                        continue;
                    }
                    s = '\u0000';
                    this.pushBackChar.addLast(Integer.valueOf(c));
                }
                if (s != '\u0000') {
                    int c = this.reader.read();
                    if (c >= 0) {
                        this.pushBackChar.addLast(Character.toCodePoint(s, (char)c));
                    } else {
                        return -1;
                    }
                }
            }
            return this.pushBackChar.pop();
        }
        catch (ClosedException e) {
            throw new EndOfFileException(e);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    public int peekCharacter(long timeout) {
        if (!this.pushBackChar.isEmpty()) {
            return this.pushBackChar.peek();
        }
        try {
            return this.reader.peek(timeout);
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    public void runMacro(String macro) {
        macro.codePoints().forEachOrdered(this.pushBackChar::addLast);
    }

    public String getCurrentBuffer() {
        return this.opBuffer.toString();
    }

    public String getLastBinding() {
        return this.lastBinding;
    }
}

