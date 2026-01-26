/*
 * Decompiled with CFR 0.152.
 */
package org.bson.types;

import java.io.Serializable;
import java.util.Date;

public final class BSONTimestamp
implements Comparable<BSONTimestamp>,
Serializable {
    private static final long serialVersionUID = -3268482672267936464L;
    private final int inc;
    private final Date time;

    public BSONTimestamp() {
        this.inc = 0;
        this.time = null;
    }

    public BSONTimestamp(int time, int increment) {
        this.time = new Date((long)time * 1000L);
        this.inc = increment;
    }

    public int getTime() {
        if (this.time == null) {
            return 0;
        }
        return (int)(this.time.getTime() / 1000L);
    }

    public int getInc() {
        return this.inc;
    }

    public String toString() {
        return "TS time:" + this.time + " inc:" + this.inc;
    }

    @Override
    public int compareTo(BSONTimestamp ts) {
        if (this.getTime() != ts.getTime()) {
            return this.getTime() - ts.getTime();
        }
        return this.getInc() - ts.getInc();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + this.inc;
        result = prime * result + this.getTime();
        return result;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BSONTimestamp) {
            BSONTimestamp t2 = (BSONTimestamp)obj;
            return this.getTime() == t2.getTime() && this.getInc() == t2.getInc();
        }
        return false;
    }
}

