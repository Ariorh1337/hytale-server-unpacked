/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.joml.Matrix3x2d;

public class Matrix3x2dStack
extends Matrix3x2d
implements Cloneable {
    private static final long serialVersionUID = 1L;
    private Matrix3x2d[] mats;
    private int curr;

    public Matrix3x2dStack(int stackSize) {
        if (stackSize < 1) {
            throw new IllegalArgumentException("stackSize must be >= 1");
        }
        this.mats = new Matrix3x2d[stackSize - 1];
        for (int i = 0; i < this.mats.length; ++i) {
            this.mats[i] = new Matrix3x2d();
        }
    }

    public Matrix3x2dStack() {
    }

    public Matrix3x2dStack clear() {
        this.curr = 0;
        this.identity();
        return this;
    }

    public Matrix3x2dStack pushMatrix() {
        if (this.curr == this.mats.length) {
            throw new IllegalStateException("max stack size of " + (this.curr + 1) + " reached");
        }
        this.mats[this.curr++].set(this);
        return this;
    }

    public Matrix3x2dStack popMatrix() {
        if (this.curr == 0) {
            throw new IllegalStateException("already at the bottom of the stack");
        }
        this.set(this.mats[--this.curr]);
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.curr;
        for (int i = 0; i < this.curr; ++i) {
            result = 31 * result + this.mats[i].hashCode();
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof Matrix3x2dStack) {
            Matrix3x2dStack other = (Matrix3x2dStack)obj;
            if (this.curr != other.curr) {
                return false;
            }
            for (int i = 0; i < this.curr; ++i) {
                if (this.mats[i].equals(other.mats[i])) continue;
                return false;
            }
        }
        return true;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(this.curr);
        for (int i = 0; i < this.curr; ++i) {
            out.writeObject(this.mats[i]);
        }
    }

    public void readExternal(ObjectInput in) throws IOException {
        super.readExternal(in);
        this.curr = in.readInt();
        this.mats = new Matrix3x2dStack[this.curr];
        for (int i = 0; i < this.curr; ++i) {
            Matrix3x2d m = new Matrix3x2d();
            m.readExternal(in);
            this.mats[i] = m;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        Matrix3x2dStack cloned = (Matrix3x2dStack)super.clone();
        Matrix3x2d[] clonedMats = new Matrix3x2d[this.mats.length];
        for (int i = 0; i < this.mats.length; ++i) {
            clonedMats[i] = (Matrix3x2d)this.mats[i].clone();
        }
        cloned.mats = clonedMats;
        return cloned;
    }
}

