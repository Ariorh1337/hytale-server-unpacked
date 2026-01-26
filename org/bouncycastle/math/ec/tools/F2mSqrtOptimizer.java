/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Strings;

public class F2mSqrtOptimizer {
    public static void main(String[] stringArray) {
        TreeSet treeSet = new TreeSet(F2mSqrtOptimizer.enumToList(ECNamedCurveTable.getNames()));
        treeSet.addAll(F2mSqrtOptimizer.enumToList(CustomNamedCurves.getNames()));
        for (String string : treeSet) {
            ECCurve eCCurve;
            X9ECParametersHolder x9ECParametersHolder = CustomNamedCurves.getByNameLazy(string);
            if (x9ECParametersHolder == null) {
                x9ECParametersHolder = ECNamedCurveTable.getByNameLazy(string);
            }
            if (x9ECParametersHolder == null || !ECAlgorithms.isF2mCurve(eCCurve = x9ECParametersHolder.getCurve())) continue;
            System.out.print(string + ":");
            F2mSqrtOptimizer.implPrintRootZ(eCCurve);
        }
    }

    public static void printRootZ(ECCurve eCCurve) {
        if (!ECAlgorithms.isF2mCurve(eCCurve)) {
            throw new IllegalArgumentException("Sqrt optimization only defined over characteristic-2 fields");
        }
        F2mSqrtOptimizer.implPrintRootZ(eCCurve);
    }

    private static void implPrintRootZ(ECCurve eCCurve) {
        ECFieldElement eCFieldElement = eCCurve.fromBigInteger(BigInteger.valueOf(2L));
        ECFieldElement eCFieldElement2 = eCFieldElement.sqrt();
        System.out.println(Strings.toUpperCase(eCFieldElement2.toBigInteger().toString(16)));
        if (!eCFieldElement2.square().equals(eCFieldElement)) {
            throw new IllegalStateException("Optimized-sqrt sanity check failed");
        }
    }

    private static List enumToList(Enumeration enumeration) {
        ArrayList arrayList = new ArrayList();
        while (enumeration.hasMoreElements()) {
            arrayList.add(enumeration.nextElement());
        }
        return arrayList;
    }
}

