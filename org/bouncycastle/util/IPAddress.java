/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class IPAddress {
    public static boolean isValid(String string) {
        return IPAddress.isValidIPv4(string) || IPAddress.isValidIPv6(string);
    }

    public static boolean isValidWithNetMask(String string) {
        return IPAddress.isValidIPv4WithNetmask(string) || IPAddress.isValidIPv6WithNetmask(string);
    }

    public static boolean isValidIPv4(String string) {
        int n = string.length();
        if (n < 7 || n > 15) {
            return false;
        }
        int n2 = 0;
        for (int i = 0; i < 3; ++i) {
            int n3 = string.indexOf(46, n2);
            if (!IPAddress.isParseableIPv4Octet(string, n2, n3)) {
                return false;
            }
            n2 = n3 + 1;
        }
        return IPAddress.isParseableIPv4Octet(string, n2, n);
    }

    public static boolean isValidIPv4WithNetmask(String string) {
        int n = string.indexOf("/");
        if (n < 1) {
            return false;
        }
        String string2 = string.substring(0, n);
        String string3 = string.substring(n + 1);
        return IPAddress.isValidIPv4(string2) && (IPAddress.isValidIPv4(string3) || IPAddress.isParseableIPv4Mask(string3));
    }

    public static boolean isValidIPv6(String string) {
        int n;
        if (string.length() == 0) {
            return false;
        }
        char c = string.charAt(0);
        if (c != ':' && Character.digit(c, 16) < 0) {
            return false;
        }
        int n2 = 0;
        String string2 = string + ":";
        boolean bl = false;
        int n3 = 0;
        while (n3 < string2.length() && (n = string2.indexOf(58, n3)) >= n3) {
            if (n2 == 8) {
                return false;
            }
            if (n3 != n) {
                String string3 = string2.substring(n3, n);
                if (n == string2.length() - 1 && string3.indexOf(46) > 0) {
                    if (++n2 == 8) {
                        return false;
                    }
                    if (!IPAddress.isValidIPv4(string3)) {
                        return false;
                    }
                } else if (!IPAddress.isParseableIPv6Segment(string2, n3, n)) {
                    return false;
                }
            } else {
                if (n != 1 && n != string2.length() - 1 && bl) {
                    return false;
                }
                bl = true;
            }
            n3 = n + 1;
            ++n2;
        }
        return n2 == 8 || bl;
    }

    public static boolean isValidIPv6WithNetmask(String string) {
        int n = string.indexOf("/");
        if (n < 1) {
            return false;
        }
        String string2 = string.substring(0, n);
        String string3 = string.substring(n + 1);
        return IPAddress.isValidIPv6(string2) && (IPAddress.isValidIPv6(string3) || IPAddress.isParseableIPv6Mask(string3));
    }

    private static boolean isParseableIPv4Mask(String string) {
        return IPAddress.isParseable(string, 0, string.length(), 10, 2, false, 0, 32);
    }

    private static boolean isParseableIPv4Octet(String string, int n, int n2) {
        return IPAddress.isParseable(string, n, n2, 10, 3, true, 0, 255);
    }

    private static boolean isParseableIPv6Mask(String string) {
        return IPAddress.isParseable(string, 0, string.length(), 10, 3, false, 1, 128);
    }

    private static boolean isParseableIPv6Segment(String string, int n, int n2) {
        return IPAddress.isParseable(string, n, n2, 16, 4, true, 0, 65535);
    }

    private static boolean isParseable(String string, int n, int n2, int n3, int n4, boolean bl, int n5, int n6) {
        int n7 = n2 - n;
        if (n7 < 1 | n7 > n4) {
            return false;
        }
        boolean bl2 = n7 > 1 & !bl;
        if (bl2 && Character.digit(string.charAt(n), n3) <= 0) {
            return false;
        }
        int n8 = 0;
        while (n < n2) {
            char c;
            int n9;
            if ((n9 = Character.digit(c = string.charAt(n++), n3)) < 0) {
                return false;
            }
            n8 *= n3;
            n8 += n9;
        }
        return n8 >= n5 & n8 <= n6;
    }
}

