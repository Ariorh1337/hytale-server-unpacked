/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.util.Date;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Duration;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ITSValidityPeriod {
    private final long startDate;
    private final UINT16 duration;
    private final Unit timeUnit;

    public static Builder from(Date date) {
        return new Builder(date);
    }

    public ITSValidityPeriod(ValidityPeriod validityPeriod) {
        this.startDate = validityPeriod.getStart().getValue().longValue();
        Duration duration = validityPeriod.getDuration();
        this.duration = duration.getDuration();
        this.timeUnit = Unit.values()[duration.getChoice()];
    }

    ITSValidityPeriod(long l, UINT16 uINT16, Unit unit) {
        this.startDate = l;
        this.duration = uINT16;
        this.timeUnit = unit;
    }

    public Date getStartDate() {
        return new Date(this.startDate);
    }

    public ValidityPeriod toASN1Structure() {
        return ValidityPeriod.builder().setStart(new Time32(this.startDate / 1000L)).setDuration(new Duration(this.timeUnit.unitTag, this.duration)).createValidityPeriod();
    }

    public static class Builder {
        private final long startDate;

        Builder(Date date) {
            this.startDate = date.getTime();
        }

        public ITSValidityPeriod plusYears(int n) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf(n), Unit.years);
        }

        public ITSValidityPeriod plusSixtyHours(int n) {
            return new ITSValidityPeriod(this.startDate, UINT16.valueOf(n), Unit.sixtyHours);
        }
    }

    public static enum Unit {
        microseconds(0),
        milliseconds(1),
        seconds(2),
        minutes(3),
        hours(4),
        sixtyHours(5),
        years(6);

        private final int unitTag;

        private Unit(int n2) {
            this.unitTag = n2;
        }
    }
}

