/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.shape;

import com.hypixel.hytale.math.vector.Vector2d;
import java.awt.Graphics2D;

public class ViewUtil {
    public static final int INSIDE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 4;
    public static final int TOP = 8;

    private ViewUtil() {
        throw new UnsupportedOperationException();
    }

    private static int computeOutCode(double x, double y) {
        int code = 0;
        if (x < -1.0) {
            code |= 1;
        } else if (x > 1.0) {
            code |= 2;
        }
        if (y < -1.0) {
            code |= 4;
        } else if (y > 1.0) {
            code |= 8;
        }
        return code;
    }

    private static void CohenSutherlandLineClipAndDraw(double x0, double y0, double x1, double y1, Graphics2D graphics2D, double width, double height) {
        int outcode0 = ViewUtil.computeOutCode(x0, y0);
        int outcode1 = ViewUtil.computeOutCode(x1, y1);
        boolean accept = false;
        while (true) {
            double y;
            double x;
            int outcodeOut;
            if ((outcode0 | outcode1) == 0) {
                accept = true;
                break;
            }
            if ((outcode0 & outcode1) != 0) break;
            int n = outcodeOut = outcode0 != 0 ? outcode0 : outcode1;
            if ((outcodeOut & 8) != 0) {
                x = x0 + (x1 - x0) * (1.0 - y0) / (y1 - y0);
                y = 1.0;
            } else if ((outcodeOut & 4) != 0) {
                x = x0 + (x1 - x0) * (-1.0 - y0) / (y1 - y0);
                y = -1.0;
            } else if ((outcodeOut & 2) != 0) {
                y = y0 + (y1 - y0) * (1.0 - x0) / (x1 - x0);
                x = 1.0;
            } else if ((outcodeOut & 1) != 0) {
                y = y0 + (y1 - y0) * (-1.0 - x0) / (x1 - x0);
                x = -1.0;
            } else {
                x = 0.0;
                y = 0.0;
            }
            if (outcodeOut == outcode0) {
                x0 = x;
                y0 = y;
                outcode0 = ViewUtil.computeOutCode(x0, y0);
                continue;
            }
            x1 = x;
            y1 = y;
            outcode1 = ViewUtil.computeOutCode(x1, y1);
        }
        if (accept) {
            Vector2d start = new Vector2d(x0, y0);
            Vector2d vector2d = new Vector2d(x1, y1);
        }
    }
}

