/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.io.Serializable;
import java.util.Objects;

public class Status
implements Serializable {
    private static final long serialVersionUID = -3794191127754280439L;
    private final Code code;
    private final SubCode subCode;
    private final String state;

    public Status(Code code, SubCode subCode, String string) {
        this.code = code;
        this.subCode = subCode;
        this.state = string;
    }

    private Status(byte by, byte by2, String string) {
        this.code = Code.getCode(by);
        this.subCode = SubCode.getSubCode(by2);
        this.state = string;
    }

    public Code getCode() {
        return this.code;
    }

    public SubCode getSubCode() {
        return this.subCode;
    }

    public String getState() {
        return this.state;
    }

    public String getCodeString() {
        StringBuilder stringBuilder = new StringBuilder().append(this.code.name());
        if (this.subCode != null && this.subCode != SubCode.None) {
            stringBuilder.append("(").append(this.subCode.name()).append(")");
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Status status = (Status)object;
        return this.code == status.code && this.subCode == status.subCode && Objects.equals(this.state, status.state);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.code, this.subCode, this.state});
    }

    public static enum SubCode {
        None(0),
        MutexTimeout(1),
        LockTimeout(2),
        LockLimit(3),
        NoSpace(4),
        Deadlock(5),
        StaleFile(6),
        MemoryLimit(7),
        Undefined(127);

        private final byte value;

        private SubCode(byte by) {
            this.value = by;
        }

        public static SubCode getSubCode(byte by) {
            for (SubCode subCode : SubCode.values()) {
                if (subCode.value != by) continue;
                return subCode;
            }
            throw new IllegalArgumentException("Illegal value provided for SubCode (" + by + ").");
        }

        public byte getValue() {
            return this.value;
        }
    }

    public static enum Code {
        Ok(0),
        NotFound(1),
        Corruption(2),
        NotSupported(3),
        InvalidArgument(4),
        IOError(5),
        MergeInProgress(6),
        Incomplete(7),
        ShutdownInProgress(8),
        TimedOut(9),
        Aborted(10),
        Busy(11),
        Expired(12),
        TryAgain(13),
        Undefined(127);

        private final byte value;

        private Code(byte by) {
            this.value = by;
        }

        public static Code getCode(byte by) {
            for (Code code : Code.values()) {
                if (code.value != by) continue;
                return code;
            }
            throw new IllegalArgumentException("Illegal value provided for Code (" + by + ").");
        }

        public byte getValue() {
            return this.value;
        }
    }
}

