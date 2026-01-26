/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.util.Arrays;

class HarakaSBase {
    protected long[][] haraka512_rc = new long[][]{{2652350495371256459L, -4767360454786055294L, -2778808723033108313L, -6138960262205972599L, 4944264682582508575L, 5312892415214084856L, 390034814247088728L, 2584105839607850161L}, {-2829930801980875922L, 9137660425067592590L, 7974068014816832049L, -4665944065725157058L, 2602240152241800734L, -1525694355931290902L, 8634660511727056099L, 1757945485816280992L}, {1181946526362588450L, -2765192619992380293L, 3395396416743122529L, -5116273100549372423L, -1285454309797503998L, -3363297609815171261L, -8360835858392998991L, -2371352336613968487L}, {-2500853454776756032L, 8465221333286591414L, 8817016078209461823L, 9067727467981428858L, 4244107674518258433L, -4347326460570889538L, 1711371409274742987L, 6486926172609168623L}, {1689001080716996467L, -491496126278250673L, 1273395568185090836L, 5805238412293617850L, -3441289770925384855L, 4592753210857527691L, 7062886034259989751L, -7974393977033172556L}, {-797818098819718290L, -41460260651793472L, 476036171179798187L, 7391697506481003962L, -855662275170689475L, -3489340839585811635L, -4891525734487956488L, 9110006695579921767L}, {-886938081943560790L, 4212830408327159617L, -3546674487567282635L, -1955379422127038289L, 3174578079917510314L, 5156046680874954380L, -318545805834821831L, -6176414008149462342L}, {2529785914229181047L, 2966313764524854080L, 6363694428402697361L, 8292109690175819701L, -8497546332135459587L, -3211108476154815616L, -5526938793786642321L, -4975969843627057770L}, {3357847021085574721L, -4764837212565187058L, -626391829400648692L, 2124133995575340009L, 7425858999829294301L, -3432032868905637771L, 1119301198758921294L, 1907812968586478892L}, {-8986524826712832802L, 3356175496741300052L, -5764600317639896362L, 4002747967109689317L, -8718925159733497197L, -1938063772587374661L, -8003749789895945835L, 7302960353763723932L}};
    protected int[][] haraka256_rc = new int[10][8];
    protected final byte[] buffer = new byte[64];
    protected int off = 0;

    protected HarakaSBase() {
    }

    protected void reset() {
        this.off = 0;
        Arrays.clear(this.buffer);
    }

    private void brRangeDec32Le(byte[] byArray, int[] nArray, int n) {
        for (int i = 0; i < nArray.length; ++i) {
            int n2 = n + (i << 2);
            nArray[i] = byArray[n2] & 0xFF | byArray[n2 + 1] << 8 & 0xFF00 | byArray[n2 + 2] << 16 & 0xFF0000 | byArray[n2 + 3] << 24;
        }
    }

    protected void interleaveConstant(long[] lArray, byte[] byArray, int n) {
        int[] nArray = new int[16];
        this.brRangeDec32Le(byArray, nArray, n);
        for (int i = 0; i < 4; ++i) {
            this.brAesCt64InterleaveIn(lArray, i, nArray, i << 2);
        }
        this.brAesCt64Ortho(lArray);
    }

    protected void interleaveConstant32(int[] nArray, byte[] byArray, int n) {
        for (int i = 0; i < 4; ++i) {
            nArray[i << 1] = this.brDec32Le(byArray, n + (i << 2));
            nArray[(i << 1) + 1] = this.brDec32Le(byArray, n + (i << 2) + 16);
        }
        this.brAesCtOrtho(nArray);
    }

    private int brDec32Le(byte[] byArray, int n) {
        return byArray[n] & 0xFF | byArray[n + 1] << 8 & 0xFF00 | byArray[n + 2] << 16 & 0xFF0000 | byArray[n + 3] << 24;
    }

    protected void haraka512Perm(byte[] byArray) {
        int n;
        int n2;
        int[] nArray = new int[16];
        long[] lArray = new long[8];
        this.brRangeDec32Le(this.buffer, nArray, 0);
        for (n2 = 0; n2 < 4; ++n2) {
            this.brAesCt64InterleaveIn(lArray, n2, nArray, n2 << 2);
        }
        this.brAesCt64Ortho(lArray);
        for (n2 = 0; n2 < 5; ++n2) {
            for (n = 0; n < 2; ++n) {
                this.brAesCt64BitsliceSbox(lArray);
                this.shiftRows(lArray);
                this.mixColumns(lArray);
                this.addRoundKey(lArray, this.haraka512_rc[(n2 << 1) + n]);
            }
            for (n = 0; n < 8; ++n) {
                long l = lArray[n];
                lArray[n] = (l & 0x1000100010001L) << 5 | (l & 0x2000200020002L) << 12 | (l & 0x4000400040004L) >>> 1 | (l & 0x8000800080008L) << 6 | (l & 0x20002000200020L) << 9 | (l & 0x40004000400040L) >>> 4 | (l & 0x80008000800080L) << 3 | (l & 0x2100210021002100L) >>> 5 | (l & 0x210021002100210L) << 2 | (l & 0x800080008000800L) << 4 | (l & 0x1000100010001000L) >>> 12 | (l & 0x4000400040004000L) >>> 10 | (l & 0x8400840084008400L) >>> 3;
            }
        }
        this.brAesCt64Ortho(lArray);
        for (n2 = 0; n2 < 4; ++n2) {
            this.brAesCt64InterleaveOut(nArray, lArray, n2);
        }
        for (n2 = 0; n2 < 16; ++n2) {
            for (n = 0; n < 4; ++n) {
                byArray[(n2 << 2) + n] = (byte)(nArray[n2] >>> (n << 3) & 0xFF);
            }
        }
    }

    protected void haraka256Perm(byte[] byArray) {
        int n;
        int[] nArray = new int[8];
        this.interleaveConstant32(nArray, this.buffer, 0);
        for (n = 0; n < 5; ++n) {
            int n2;
            for (n2 = 0; n2 < 2; ++n2) {
                HarakaSBase.brAesCtBitsliceSbox(nArray);
                this.shiftRows32(nArray);
                this.mixColumns32(nArray);
                this.addRoundKey32(nArray, this.haraka256_rc[(n << 1) + n2]);
            }
            for (n2 = 0; n2 < 8; ++n2) {
                int n3 = nArray[n2];
                nArray[n2] = n3 & 0x81818181 | (n3 & 0x2020202) << 1 | (n3 & 0x4040404) << 2 | (n3 & 0x8080808) << 3 | (n3 & 0x10101010) >>> 3 | (n3 & 0x20202020) >>> 2 | (n3 & 0x40404040) >>> 1;
            }
        }
        this.brAesCtOrtho(nArray);
        for (n = 0; n < 4; ++n) {
            this.brEnc32Le(byArray, nArray[n << 1], n << 2);
            this.brEnc32Le(byArray, nArray[(n << 1) + 1], (n << 2) + 16);
        }
    }

    private void brEnc32Le(byte[] byArray, int n, int n2) {
        for (int i = 0; i < 4; ++i) {
            byArray[n2 + i] = (byte)(n >> (i << 3));
        }
    }

    private void brAesCt64InterleaveIn(long[] lArray, int n, int[] nArray, int n2) {
        long l = (long)nArray[n2] & 0xFFFFFFFFL;
        long l2 = (long)nArray[n2 + 1] & 0xFFFFFFFFL;
        long l3 = (long)nArray[n2 + 2] & 0xFFFFFFFFL;
        long l4 = (long)nArray[n2 + 3] & 0xFFFFFFFFL;
        l |= l << 16;
        l2 |= l2 << 16;
        l3 |= l3 << 16;
        l4 |= l4 << 16;
        l &= 0xFFFF0000FFFFL;
        l2 &= 0xFFFF0000FFFFL;
        l3 &= 0xFFFF0000FFFFL;
        l4 &= 0xFFFF0000FFFFL;
        l |= l << 8;
        l2 |= l2 << 8;
        l3 |= l3 << 8;
        l4 |= l4 << 8;
        lArray[n] = (l &= 0xFF00FF00FF00FFL) | (l3 &= 0xFF00FF00FF00FFL) << 8;
        lArray[n + 4] = (l2 &= 0xFF00FF00FF00FFL) | (l4 &= 0xFF00FF00FF00FFL) << 8;
    }

    private static void brAesCtBitsliceSbox(int[] nArray) {
        int n = nArray[7];
        int n2 = nArray[6];
        int n3 = nArray[5];
        int n4 = nArray[4];
        int n5 = nArray[3];
        int n6 = nArray[2];
        int n7 = nArray[1];
        int n8 = nArray[0];
        int n9 = n4 ^ n6;
        int n10 = n ^ n7;
        int n11 = n ^ n4;
        int n12 = n ^ n6;
        int n13 = n2 ^ n3;
        int n14 = n13 ^ n8;
        int n15 = n14 ^ n4;
        int n16 = n10 ^ n9;
        int n17 = n14 ^ n;
        int n18 = n14 ^ n7;
        int n19 = n18 ^ n12;
        int n20 = n5 ^ n16;
        int n21 = n20 ^ n6;
        int n22 = n20 ^ n2;
        int n23 = n21 ^ n8;
        int n24 = n21 ^ n13;
        int n25 = n22 ^ n11;
        int n26 = n8 ^ n25;
        int n27 = n24 ^ n25;
        int n28 = n24 ^ n12;
        int n29 = n13 ^ n25;
        int n30 = n10 ^ n29;
        int n31 = n ^ n29;
        int n32 = n16 & n21;
        int n33 = n19 & n23;
        int n34 = n33 ^ n32;
        int n35 = n15 & n8;
        int n36 = n35 ^ n32;
        int n37 = n10 & n29;
        int n38 = n18 & n14;
        int n39 = n38 ^ n37;
        int n40 = n17 & n26;
        int n41 = n40 ^ n37;
        int n42 = n11 & n25;
        int n43 = n9 & n27;
        int n44 = n43 ^ n42;
        int n45 = n12 & n24;
        int n46 = n45 ^ n42;
        int n47 = n34 ^ n44;
        int n48 = n36 ^ n46;
        int n49 = n39 ^ n44;
        int n50 = n41 ^ n46;
        int n51 = n47 ^ n22;
        int n52 = n48 ^ n28;
        int n53 = n49 ^ n30;
        int n54 = n50 ^ n31;
        int n55 = n51 ^ n52;
        int n56 = n51 & n53;
        int n57 = n54 ^ n56;
        int n58 = n55 & n57;
        int n59 = n58 ^ n52;
        int n60 = n53 ^ n54;
        int n61 = n52 ^ n56;
        int n62 = n61 & n60;
        int n63 = n62 ^ n54;
        int n64 = n53 ^ n63;
        int n65 = n57 ^ n63;
        int n66 = n54 & n65;
        int n67 = n66 ^ n64;
        int n68 = n57 ^ n66;
        int n69 = n59 & n68;
        int n70 = n55 ^ n69;
        int n71 = n70 ^ n67;
        int n72 = n59 ^ n63;
        int n73 = n59 ^ n70;
        int n74 = n63 ^ n67;
        int n75 = n72 ^ n71;
        int n76 = n74 & n21;
        int n77 = n67 & n23;
        int n78 = n63 & n8;
        int n79 = n73 & n29;
        int n80 = n70 & n14;
        int n81 = n59 & n26;
        int n82 = n72 & n25;
        int n83 = n75 & n27;
        int n84 = n71 & n24;
        int n85 = n74 & n16;
        int n86 = n67 & n19;
        int n87 = n63 & n15;
        int n88 = n73 & n10;
        int n89 = n70 & n18;
        int n90 = n59 & n17;
        int n91 = n72 & n11;
        int n92 = n75 & n9;
        int n93 = n71 & n12;
        int n94 = n91 ^ n92;
        int n95 = n86 ^ n87;
        int n96 = n81 ^ n89;
        int n97 = n85 ^ n86;
        int n98 = n78 ^ n88;
        int n99 = n78 ^ n81;
        int n100 = n83 ^ n84;
        int n101 = n76 ^ n79;
        int n102 = n82 ^ n83;
        int n103 = n92 ^ n93;
        int n104 = n88 ^ n96;
        int n105 = n98 ^ n101;
        int n106 = n80 ^ n94;
        int n107 = n79 ^ n102;
        int n108 = n94 ^ n105;
        int n109 = n90 ^ n105;
        int n110 = n100 ^ n106;
        int n111 = n97 ^ n106;
        int n112 = n80 ^ n107;
        int n113 = n109 ^ n110;
        int n114 = n77 ^ n111;
        int n115 = n107 ^ n111;
        int n116 = n104 ^ ~n110;
        int n117 = n96 ^ ~n108;
        int n118 = n112 ^ n113;
        int n119 = n101 ^ n114;
        int n120 = n99 ^ n114;
        int n121 = n95 ^ n113;
        int n122 = n112 ^ ~n119;
        int n123 = n103 ^ ~n118;
        nArray[7] = n115;
        nArray[6] = n122;
        nArray[5] = n123;
        nArray[4] = n119;
        nArray[3] = n120;
        nArray[2] = n121;
        nArray[1] = n116;
        nArray[0] = n117;
    }

    private void shiftRows32(int[] nArray) {
        for (int i = 0; i < 8; ++i) {
            int n = nArray[i];
            nArray[i] = n & 0xFF | (n & 0xFC00) >>> 2 | (n & 0x300) << 6 | (n & 0xF00000) >>> 4 | (n & 0xF0000) << 4 | (n & 0xC0000000) >>> 6 | (n & 0x3F000000) << 2;
        }
    }

    private void mixColumns32(int[] nArray) {
        int n = nArray[0];
        int n2 = nArray[1];
        int n3 = nArray[2];
        int n4 = nArray[3];
        int n5 = nArray[4];
        int n6 = nArray[5];
        int n7 = nArray[6];
        int n8 = nArray[7];
        int n9 = n >>> 8 | n << 24;
        int n10 = n2 >>> 8 | n2 << 24;
        int n11 = n3 >>> 8 | n3 << 24;
        int n12 = n4 >>> 8 | n4 << 24;
        int n13 = n5 >>> 8 | n5 << 24;
        int n14 = n6 >>> 8 | n6 << 24;
        int n15 = n7 >>> 8 | n7 << 24;
        int n16 = n8 >>> 8 | n8 << 24;
        nArray[0] = n8 ^ n16 ^ n9 ^ this.rotr16(n ^ n9);
        nArray[1] = n ^ n9 ^ n8 ^ n16 ^ n10 ^ this.rotr16(n2 ^ n10);
        nArray[2] = n2 ^ n10 ^ n11 ^ this.rotr16(n3 ^ n11);
        nArray[3] = n3 ^ n11 ^ n8 ^ n16 ^ n12 ^ this.rotr16(n4 ^ n12);
        nArray[4] = n4 ^ n12 ^ n8 ^ n16 ^ n13 ^ this.rotr16(n5 ^ n13);
        nArray[5] = n5 ^ n13 ^ n14 ^ this.rotr16(n6 ^ n14);
        nArray[6] = n6 ^ n14 ^ n15 ^ this.rotr16(n7 ^ n15);
        nArray[7] = n7 ^ n15 ^ n16 ^ this.rotr16(n8 ^ n16);
    }

    private void addRoundKey32(int[] nArray, int[] nArray2) {
        nArray[0] = nArray[0] ^ nArray2[0];
        nArray[1] = nArray[1] ^ nArray2[1];
        nArray[2] = nArray[2] ^ nArray2[2];
        nArray[3] = nArray[3] ^ nArray2[3];
        nArray[4] = nArray[4] ^ nArray2[4];
        nArray[5] = nArray[5] ^ nArray2[5];
        nArray[6] = nArray[6] ^ nArray2[6];
        nArray[7] = nArray[7] ^ nArray2[7];
    }

    private int rotr16(int n) {
        return n << 16 | n >>> 16;
    }

    private void brAesCt64Ortho(long[] lArray) {
        this.Swapn(lArray, 1, 0, 1);
        this.Swapn(lArray, 1, 2, 3);
        this.Swapn(lArray, 1, 4, 5);
        this.Swapn(lArray, 1, 6, 7);
        this.Swapn(lArray, 2, 0, 2);
        this.Swapn(lArray, 2, 1, 3);
        this.Swapn(lArray, 2, 4, 6);
        this.Swapn(lArray, 2, 5, 7);
        this.Swapn(lArray, 4, 0, 4);
        this.Swapn(lArray, 4, 1, 5);
        this.Swapn(lArray, 4, 2, 6);
        this.Swapn(lArray, 4, 3, 7);
    }

    private void brAesCtOrtho(int[] nArray) {
        this.Swapn32(nArray, 1, 0, 1);
        this.Swapn32(nArray, 1, 2, 3);
        this.Swapn32(nArray, 1, 4, 5);
        this.Swapn32(nArray, 1, 6, 7);
        this.Swapn32(nArray, 2, 0, 2);
        this.Swapn32(nArray, 2, 1, 3);
        this.Swapn32(nArray, 2, 4, 6);
        this.Swapn32(nArray, 2, 5, 7);
        this.Swapn32(nArray, 4, 0, 4);
        this.Swapn32(nArray, 4, 1, 5);
        this.Swapn32(nArray, 4, 2, 6);
        this.Swapn32(nArray, 4, 3, 7);
    }

    private void Swapn32(int[] nArray, int n, int n2, int n3) {
        int n4 = 0;
        int n5 = 0;
        switch (n) {
            case 1: {
                n4 = 0x55555555;
                n5 = -1431655766;
                break;
            }
            case 2: {
                n4 = 0x33333333;
                n5 = -858993460;
                break;
            }
            case 4: {
                n4 = 0xF0F0F0F;
                n5 = -252645136;
            }
        }
        int n6 = nArray[n2];
        int n7 = nArray[n3];
        nArray[n2] = n6 & n4 | (n7 & n4) << n;
        nArray[n3] = (n6 & n5) >>> n | n7 & n5;
    }

    private void Swapn(long[] lArray, int n, int n2, int n3) {
        long l = 0L;
        long l2 = 0L;
        switch (n) {
            case 1: {
                l = 0x5555555555555555L;
                l2 = -6148914691236517206L;
                break;
            }
            case 2: {
                l = 0x3333333333333333L;
                l2 = -3689348814741910324L;
                break;
            }
            case 4: {
                l = 0xF0F0F0F0F0F0F0FL;
                l2 = -1085102592571150096L;
                break;
            }
            default: {
                return;
            }
        }
        long l3 = lArray[n2];
        long l4 = lArray[n3];
        lArray[n2] = l3 & l | (l4 & l) << n;
        lArray[n3] = (l3 & l2) >>> n | l4 & l2;
    }

    private void brAesCt64BitsliceSbox(long[] lArray) {
        long l = lArray[7];
        long l2 = lArray[6];
        long l3 = lArray[5];
        long l4 = lArray[4];
        long l5 = lArray[3];
        long l6 = lArray[2];
        long l7 = lArray[1];
        long l8 = lArray[0];
        long l9 = l4 ^ l6;
        long l10 = l ^ l7;
        long l11 = l ^ l4;
        long l12 = l ^ l6;
        long l13 = l2 ^ l3;
        long l14 = l13 ^ l8;
        long l15 = l14 ^ l4;
        long l16 = l10 ^ l9;
        long l17 = l14 ^ l;
        long l18 = l14 ^ l7;
        long l19 = l18 ^ l12;
        long l20 = l5 ^ l16;
        long l21 = l20 ^ l6;
        long l22 = l20 ^ l2;
        long l23 = l21 ^ l8;
        long l24 = l21 ^ l13;
        long l25 = l22 ^ l11;
        long l26 = l8 ^ l25;
        long l27 = l24 ^ l25;
        long l28 = l24 ^ l12;
        long l29 = l13 ^ l25;
        long l30 = l10 ^ l29;
        long l31 = l ^ l29;
        long l32 = l16 & l21;
        long l33 = l19 & l23;
        long l34 = l33 ^ l32;
        long l35 = l15 & l8;
        long l36 = l35 ^ l32;
        long l37 = l10 & l29;
        long l38 = l18 & l14;
        long l39 = l38 ^ l37;
        long l40 = l17 & l26;
        long l41 = l40 ^ l37;
        long l42 = l11 & l25;
        long l43 = l9 & l27;
        long l44 = l43 ^ l42;
        long l45 = l12 & l24;
        long l46 = l45 ^ l42;
        long l47 = l34 ^ l44;
        long l48 = l36 ^ l46;
        long l49 = l39 ^ l44;
        long l50 = l41 ^ l46;
        long l51 = l47 ^ l22;
        long l52 = l48 ^ l28;
        long l53 = l49 ^ l30;
        long l54 = l50 ^ l31;
        long l55 = l51 ^ l52;
        long l56 = l51 & l53;
        long l57 = l54 ^ l56;
        long l58 = l55 & l57;
        long l59 = l58 ^ l52;
        long l60 = l53 ^ l54;
        long l61 = l52 ^ l56;
        long l62 = l61 & l60;
        long l63 = l62 ^ l54;
        long l64 = l53 ^ l63;
        long l65 = l57 ^ l63;
        long l66 = l54 & l65;
        long l67 = l66 ^ l64;
        long l68 = l57 ^ l66;
        long l69 = l59 & l68;
        long l70 = l55 ^ l69;
        long l71 = l70 ^ l67;
        long l72 = l59 ^ l63;
        long l73 = l59 ^ l70;
        long l74 = l63 ^ l67;
        long l75 = l72 ^ l71;
        long l76 = l74 & l21;
        long l77 = l67 & l23;
        long l78 = l63 & l8;
        long l79 = l73 & l29;
        long l80 = l70 & l14;
        long l81 = l59 & l26;
        long l82 = l72 & l25;
        long l83 = l75 & l27;
        long l84 = l71 & l24;
        long l85 = l74 & l16;
        long l86 = l67 & l19;
        long l87 = l63 & l15;
        long l88 = l73 & l10;
        long l89 = l70 & l18;
        long l90 = l59 & l17;
        long l91 = l72 & l11;
        long l92 = l75 & l9;
        long l93 = l71 & l12;
        long l94 = l91 ^ l92;
        long l95 = l86 ^ l87;
        long l96 = l81 ^ l89;
        long l97 = l85 ^ l86;
        long l98 = l78 ^ l88;
        long l99 = l78 ^ l81;
        long l100 = l83 ^ l84;
        long l101 = l76 ^ l79;
        long l102 = l82 ^ l83;
        long l103 = l92 ^ l93;
        long l104 = l88 ^ l96;
        long l105 = l98 ^ l101;
        long l106 = l80 ^ l94;
        long l107 = l79 ^ l102;
        long l108 = l94 ^ l105;
        long l109 = l90 ^ l105;
        long l110 = l100 ^ l106;
        long l111 = l97 ^ l106;
        long l112 = l80 ^ l107;
        long l113 = l109 ^ l110;
        long l114 = l77 ^ l111;
        long l115 = l107 ^ l111;
        long l116 = l104 ^ (l110 ^ 0xFFFFFFFFFFFFFFFFL);
        long l117 = l96 ^ (l108 ^ 0xFFFFFFFFFFFFFFFFL);
        long l118 = l112 ^ l113;
        long l119 = l101 ^ l114;
        long l120 = l99 ^ l114;
        long l121 = l95 ^ l113;
        long l122 = l112 ^ (l119 ^ 0xFFFFFFFFFFFFFFFFL);
        long l123 = l103 ^ (l118 ^ 0xFFFFFFFFFFFFFFFFL);
        lArray[7] = l115;
        lArray[6] = l122;
        lArray[5] = l123;
        lArray[4] = l119;
        lArray[3] = l120;
        lArray[2] = l121;
        lArray[1] = l116;
        lArray[0] = l117;
    }

    private void shiftRows(long[] lArray) {
        for (int i = 0; i < lArray.length; ++i) {
            long l = lArray[i];
            lArray[i] = l & 0xFFFFL | (l & 0xFFF00000L) >>> 4 | (l & 0xF0000L) << 12 | (l & 0xFF0000000000L) >>> 8 | (l & 0xFF00000000L) << 8 | (l & 0xF000000000000000L) >>> 12 | (l & 0xFFF000000000000L) << 4;
        }
    }

    private void mixColumns(long[] lArray) {
        long l = lArray[0];
        long l2 = lArray[1];
        long l3 = lArray[2];
        long l4 = lArray[3];
        long l5 = lArray[4];
        long l6 = lArray[5];
        long l7 = lArray[6];
        long l8 = lArray[7];
        long l9 = l >>> 16 | l << 48;
        long l10 = l2 >>> 16 | l2 << 48;
        long l11 = l3 >>> 16 | l3 << 48;
        long l12 = l4 >>> 16 | l4 << 48;
        long l13 = l5 >>> 16 | l5 << 48;
        long l14 = l6 >>> 16 | l6 << 48;
        long l15 = l7 >>> 16 | l7 << 48;
        long l16 = l8 >>> 16 | l8 << 48;
        lArray[0] = l8 ^ l16 ^ l9 ^ this.rotr32(l ^ l9);
        lArray[1] = l ^ l9 ^ l8 ^ l16 ^ l10 ^ this.rotr32(l2 ^ l10);
        lArray[2] = l2 ^ l10 ^ l11 ^ this.rotr32(l3 ^ l11);
        lArray[3] = l3 ^ l11 ^ l8 ^ l16 ^ l12 ^ this.rotr32(l4 ^ l12);
        lArray[4] = l4 ^ l12 ^ l8 ^ l16 ^ l13 ^ this.rotr32(l5 ^ l13);
        lArray[5] = l5 ^ l13 ^ l14 ^ this.rotr32(l6 ^ l14);
        lArray[6] = l6 ^ l14 ^ l15 ^ this.rotr32(l7 ^ l15);
        lArray[7] = l7 ^ l15 ^ l16 ^ this.rotr32(l8 ^ l16);
    }

    private long rotr32(long l) {
        return l << 32 | l >>> 32;
    }

    private void addRoundKey(long[] lArray, long[] lArray2) {
        lArray[0] = lArray[0] ^ lArray2[0];
        lArray[1] = lArray[1] ^ lArray2[1];
        lArray[2] = lArray[2] ^ lArray2[2];
        lArray[3] = lArray[3] ^ lArray2[3];
        lArray[4] = lArray[4] ^ lArray2[4];
        lArray[5] = lArray[5] ^ lArray2[5];
        lArray[6] = lArray[6] ^ lArray2[6];
        lArray[7] = lArray[7] ^ lArray2[7];
    }

    private void brAesCt64InterleaveOut(int[] nArray, long[] lArray, int n) {
        long l = lArray[n] & 0xFF00FF00FF00FFL;
        long l2 = lArray[n + 4] & 0xFF00FF00FF00FFL;
        long l3 = lArray[n] >>> 8 & 0xFF00FF00FF00FFL;
        long l4 = lArray[n + 4] >>> 8 & 0xFF00FF00FF00FFL;
        l |= l >>> 8;
        l2 |= l2 >>> 8;
        l3 |= l3 >>> 8;
        l4 |= l4 >>> 8;
        nArray[n <<= 2] = (int)((l &= 0xFFFF0000FFFFL) | l >>> 16);
        nArray[n + 1] = (int)((l2 &= 0xFFFF0000FFFFL) | l2 >>> 16);
        nArray[n + 2] = (int)((l3 &= 0xFFFF0000FFFFL) | l3 >>> 16);
        nArray[n + 3] = (int)((l4 &= 0xFFFF0000FFFFL) | l4 >>> 16);
    }

    protected static void xor(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3, int n4) {
        for (int i = 0; i < n4; ++i) {
            byArray3[n3 + i] = (byte)(byArray[n + i] ^ byArray2[n2 + i]);
        }
    }
}

