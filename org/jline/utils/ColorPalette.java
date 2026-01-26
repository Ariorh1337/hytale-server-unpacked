/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.jline.terminal.Terminal;
import org.jline.utils.Colors;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public class ColorPalette {
    public static final String XTERM_INITC = "\\E]4;%p1%d;rgb\\:%p2%{255}%*%{1000}%/%2.2X/%p3%{255}%*%{1000}%/%2.2X/%p4%{255}%*%{1000}%/%2.2X\\E\\\\";
    public static final ColorPalette DEFAULT = new ColorPalette();
    private final Terminal terminal;
    private String distanceName;
    private Colors.Distance distance;
    private boolean osc4;
    private int[] palette;

    public ColorPalette() {
        this.terminal = null;
        this.distanceName = null;
        this.palette = Colors.DEFAULT_COLORS_256;
    }

    public ColorPalette(Terminal terminal) throws IOException {
        this(terminal, null);
    }

    public ColorPalette(Terminal terminal, String distance) throws IOException {
        this.terminal = terminal;
        this.distanceName = distance;
        this.loadPalette(false);
    }

    public String getDistanceName() {
        return this.distanceName;
    }

    public void setDistance(String name) {
        this.distanceName = name;
    }

    public boolean canChange() {
        return this.terminal != null && this.terminal.getBooleanCapability(InfoCmp.Capability.can_change);
    }

    public boolean loadPalette() throws IOException {
        if (!this.osc4) {
            this.loadPalette(true);
        }
        return this.osc4;
    }

    protected void loadPalette(boolean doLoad) throws IOException {
        if (this.terminal != null) {
            int[] pal;
            int[] nArray = pal = doLoad ? ColorPalette.doLoad(this.terminal) : null;
            if (pal != null) {
                this.palette = pal;
                this.osc4 = true;
            } else {
                Integer cols = this.terminal.getNumericCapability(InfoCmp.Capability.max_colors);
                this.palette = cols != null ? (cols == Colors.DEFAULT_COLORS_88.length ? Colors.DEFAULT_COLORS_88 : Arrays.copyOf(Colors.DEFAULT_COLORS_256, Math.min(cols, 256))) : Arrays.copyOf(Colors.DEFAULT_COLORS_256, 256);
                this.osc4 = false;
            }
        } else {
            this.palette = Colors.DEFAULT_COLORS_256;
            this.osc4 = false;
        }
    }

    public int getLength() {
        return this.palette.length;
    }

    public int getColor(int index) {
        return this.palette[index];
    }

    public void setColor(int index, int color) {
        String initc;
        this.palette[index] = color;
        if (this.canChange() && ((initc = this.terminal.getStringCapability(InfoCmp.Capability.initialize_color)) != null || this.osc4)) {
            int r = (color >> 16 & 0xFF) * 1000 / 255 + 1;
            int g = (color >> 8 & 0xFF) * 1000 / 255 + 1;
            int b = (color & 0xFF) * 1000 / 255 + 1;
            if (initc == null) {
                initc = XTERM_INITC;
            }
            Curses.tputs(this.terminal.writer(), initc, index, r, g, b);
            this.terminal.writer().flush();
        }
    }

    public boolean isReal() {
        return this.osc4;
    }

    public int round(int r, int g, int b) {
        return Colors.roundColor((r << 16) + (g << 8) + b, this.palette, this.palette.length, this.getDist());
    }

    public int round(int col) {
        if (col >= this.palette.length) {
            col = Colors.roundColor(DEFAULT.getColor(col), this.palette, this.palette.length, this.getDist());
        }
        return col;
    }

    protected Colors.Distance getDist() {
        if (this.distance == null) {
            this.distance = Colors.getDistance(this.distanceName);
        }
        return this.distance;
    }

    private static int[] doLoad(Terminal terminal) throws IOException {
        PrintWriter writer = terminal.writer();
        NonBlockingReader reader = terminal.reader();
        int[] palette = new int[256];
        for (int i = 0; i < 16; ++i) {
            StringBuilder req = new StringBuilder(1024);
            req.append("\u001b]4");
            for (int j = 0; j < 16; ++j) {
                req.append(';').append(i * 16 + j).append(";?");
            }
            req.append("\u001b\\");
            writer.write(req.toString());
            writer.flush();
            boolean black = true;
            for (int j = 0; j < 16 && reader.peek(50L) >= 0; ++j) {
                int c;
                if (reader.read(10L) != 27 || reader.read(10L) != 93 || reader.read(10L) != 52 || reader.read(10L) != 59) {
                    return null;
                }
                int idx = 0;
                while ((c = reader.read(10L)) >= 48 && c <= 57) {
                    idx = idx * 10 + (c - 48);
                }
                if (c != 59) {
                    return null;
                }
                if (idx > 255) {
                    return null;
                }
                if (reader.read(10L) != 114 || reader.read(10L) != 103 || reader.read(10L) != 98 || reader.read(10L) != 58) {
                    return null;
                }
                StringBuilder sb = new StringBuilder(16);
                ArrayList<String> rgb = new ArrayList<String>();
                while (true) {
                    if ((c = reader.read(10L)) == 7) {
                        rgb.add(sb.toString());
                        break;
                    }
                    if (c == 27) {
                        c = reader.read(10L);
                        if (c == 92) {
                            rgb.add(sb.toString());
                            break;
                        }
                        return null;
                    }
                    if (c >= 48 && c <= 57 || c >= 65 && c <= 90 || c >= 97 && c <= 122) {
                        sb.append((char)c);
                        continue;
                    }
                    if (c != 47) continue;
                    rgb.add(sb.toString());
                    sb.setLength(0);
                }
                if (rgb.size() != 3) {
                    return null;
                }
                double r = (double)Integer.parseInt((String)rgb.get(0), 16) / ((double)(1 << 4 * ((String)rgb.get(0)).length()) - 1.0);
                double g = (double)Integer.parseInt((String)rgb.get(1), 16) / ((double)(1 << 4 * ((String)rgb.get(1)).length()) - 1.0);
                double b = (double)Integer.parseInt((String)rgb.get(2), 16) / ((double)(1 << 4 * ((String)rgb.get(2)).length()) - 1.0);
                palette[idx] = (int)((Math.round(r * 255.0) << 16) + (Math.round(g * 255.0) << 8) + Math.round(b * 255.0));
                black &= palette[idx] == 0;
            }
            if (black) break;
        }
        int max = 256;
        while (max > 0 && palette[--max] == 0) {
        }
        return Arrays.copyOfRange(palette, 0, max + 1);
    }

    public int getDefaultForeground() {
        if (this.terminal == null) {
            return -1;
        }
        return this.terminal.getDefaultForegroundColor();
    }

    public int getDefaultBackground() {
        if (this.terminal == null) {
            return -1;
        }
        return this.terminal.getDefaultBackgroundColor();
    }

    public String toString() {
        return "ColorPalette[length=" + this.getLength() + ", distance='" + this.getDist() + "']";
    }
}

