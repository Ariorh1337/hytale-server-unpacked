/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class Duration
extends ASN1Object
implements ASN1Choice {
    public static final int microseconds = 0;
    public static final int milliseconds = 1;
    public static final int seconds = 2;
    public static final int minutes = 3;
    public static final int hours = 4;
    public static final int sixtyHours = 5;
    public static final int years = 6;
    private final int choice;
    private final UINT16 duration;

    public Duration(int n, UINT16 uINT16) {
        this.choice = n;
        this.duration = uINT16;
    }

    private Duration(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: {
                try {
                    this.duration = UINT16.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                    break;
                }
                catch (Exception exception) {
                    throw new IllegalStateException(exception.getMessage(), exception);
                }
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static Duration getInstance(Object object) {
        if (object instanceof Duration) {
            return (Duration)object;
        }
        if (object != null) {
            return new Duration(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public static Duration years(UINT16 uINT16) {
        return new Duration(6, uINT16);
    }

    public static Duration sixtyHours(UINT16 uINT16) {
        return new Duration(5, uINT16);
    }

    public static Duration hours(UINT16 uINT16) {
        return new Duration(4, uINT16);
    }

    public static Duration minutes(UINT16 uINT16) {
        return new Duration(3, uINT16);
    }

    public static Duration seconds(UINT16 uINT16) {
        return new Duration(2, uINT16);
    }

    public static Duration milliseconds(UINT16 uINT16) {
        return new Duration(1, uINT16);
    }

    public static Duration microseconds(UINT16 uINT16) {
        return new Duration(0, uINT16);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.duration);
    }

    public int getChoice() {
        return this.choice;
    }

    public UINT16 getDuration() {
        return this.duration;
    }

    public String toString() {
        switch (this.choice) {
            case 0: {
                return this.duration.value + "uS";
            }
            case 1: {
                return this.duration.value + "mS";
            }
            case 2: {
                return this.duration.value + " seconds";
            }
            case 3: {
                return this.duration.value + " minute";
            }
            case 4: {
                return this.duration.value + " hours";
            }
            case 5: {
                return this.duration.value + " sixty hours";
            }
            case 6: {
                return this.duration.value + " years";
            }
        }
        return this.duration.value + " unknown choice";
    }
}

