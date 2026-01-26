/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;

public class StyleResolver {
    private static final Logger log = Logger.getLogger(StyleResolver.class.getName());
    private final Function<String, String> source;

    public StyleResolver(Function<String, String> source) {
        this.source = Objects.requireNonNull(source);
    }

    private static Integer colorRgb(String name) {
        if ((name = name.toLowerCase(Locale.US)).charAt(0) == 'x' || name.charAt(0) == '#') {
            try {
                return Integer.parseInt(name.substring(1), 16);
            }
            catch (NumberFormatException e) {
                log.warning("Invalid hexadecimal color: " + name);
                return null;
            }
        }
        Integer color = StyleResolver.color(name);
        if (color != null && color != -1) {
            color = Colors.DEFAULT_COLORS_256[color];
        }
        return color;
    }

    private static Integer color(String name) {
        int flags = 0;
        if (name.equals("default")) {
            return -1;
        }
        if (name.charAt(0) == '!') {
            name = name.substring(1);
            flags = 8;
        } else if (name.startsWith("bright-")) {
            name = name.substring(7);
            flags = 8;
        } else if (name.charAt(0) == '~') {
            name = name.substring(1);
            try {
                return Colors.rgbColor(name);
            }
            catch (IllegalArgumentException e) {
                log.warning("Invalid style-color name: " + name);
                return null;
            }
        }
        switch (name) {
            case "black": 
            case "k": {
                return flags + 0;
            }
            case "red": 
            case "r": {
                return flags + 1;
            }
            case "green": 
            case "g": {
                return flags + 2;
            }
            case "yellow": 
            case "y": {
                return flags + 3;
            }
            case "blue": 
            case "b": {
                return flags + 4;
            }
            case "magenta": 
            case "m": {
                return flags + 5;
            }
            case "cyan": 
            case "c": {
                return flags + 6;
            }
            case "white": 
            case "w": {
                return flags + 7;
            }
        }
        return null;
    }

    public AttributedStyle resolve(String spec) {
        int i;
        Objects.requireNonNull(spec);
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Resolve: " + spec);
        }
        if ((i = spec.indexOf(":-")) != -1) {
            String[] parts = spec.split(":-");
            return this.resolve(parts[0].trim(), parts[1].trim());
        }
        return this.apply(AttributedStyle.DEFAULT, spec);
    }

    public AttributedStyle resolve(String spec, String defaultSpec) {
        AttributedStyle style;
        Objects.requireNonNull(spec);
        if (log.isLoggable(Level.FINEST)) {
            log.finest(String.format("Resolve: %s; default: %s", spec, defaultSpec));
        }
        if ((style = this.apply(AttributedStyle.DEFAULT, spec)) == AttributedStyle.DEFAULT && defaultSpec != null) {
            style = this.apply(style, defaultSpec);
        }
        return style;
    }

    private AttributedStyle apply(AttributedStyle style, String spec) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Apply: " + spec);
        }
        for (String item : spec.split(",")) {
            if ((item = item.trim()).isEmpty()) continue;
            style = item.startsWith(".") ? this.applyReference(style, item) : (item.contains(":") ? this.applyColor(style, item) : (item.matches("[0-9]+(;[0-9]+)*") ? this.applyAnsi(style, item) : this.applyNamed(style, item)));
        }
        return style;
    }

    private AttributedStyle applyAnsi(AttributedStyle style, String spec) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Apply-ansi: " + spec);
        }
        return new AttributedStringBuilder().style(style).ansiAppend("\u001b[" + spec + "m").style();
    }

    private AttributedStyle applyReference(AttributedStyle style, String spec) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Apply-reference: " + spec);
        }
        if (spec.length() == 1) {
            log.warning("Invalid style-reference; missing discriminator: " + spec);
        } else {
            String name = spec.substring(1);
            String resolvedSpec = this.source.apply(name);
            if (resolvedSpec != null) {
                return this.apply(style, resolvedSpec);
            }
        }
        return style;
    }

    private AttributedStyle applyNamed(AttributedStyle style, String name) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Apply-named: " + name);
        }
        switch (name.toLowerCase(Locale.US)) {
            case "default": {
                return AttributedStyle.DEFAULT;
            }
            case "bold": {
                return style.bold();
            }
            case "faint": {
                return style.faint();
            }
            case "italic": {
                return style.italic();
            }
            case "underline": {
                return style.underline();
            }
            case "blink": {
                return style.blink();
            }
            case "inverse": {
                return style.inverse();
            }
            case "inverse-neg": 
            case "inverseneg": {
                return style.inverseNeg();
            }
            case "conceal": {
                return style.conceal();
            }
            case "crossed-out": 
            case "crossedout": {
                return style.crossedOut();
            }
            case "hidden": {
                return style.hidden();
            }
        }
        log.warning("Unknown style: " + name);
        return style;
    }

    private AttributedStyle applyColor(AttributedStyle style, String spec) {
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Apply-color: " + spec);
        }
        String[] parts = spec.split(":", 2);
        String colorMode = parts[0].trim();
        String colorName = parts[1].trim();
        switch (colorMode.toLowerCase(Locale.US)) {
            case "foreground": 
            case "fg": 
            case "f": {
                Integer color = StyleResolver.color(colorName);
                if (color == null) {
                    log.warning("Invalid color-name: " + colorName);
                    break;
                }
                return color >= 0 ? style.foreground(color) : style.foregroundDefault();
            }
            case "background": 
            case "bg": 
            case "b": {
                Integer color = StyleResolver.color(colorName);
                if (color == null) {
                    log.warning("Invalid color-name: " + colorName);
                    break;
                }
                return color >= 0 ? style.background(color) : style.backgroundDefault();
            }
            case "foreground-rgb": 
            case "fg-rgb": 
            case "f-rgb": {
                Integer color = StyleResolver.colorRgb(colorName);
                if (color == null) {
                    log.warning("Invalid color-name: " + colorName);
                    break;
                }
                return color >= 0 ? style.foregroundRgb(color) : style.foregroundDefault();
            }
            case "background-rgb": 
            case "bg-rgb": 
            case "b-rgb": {
                Integer color = StyleResolver.colorRgb(colorName);
                if (color == null) {
                    log.warning("Invalid color-name: " + colorName);
                    break;
                }
                return color >= 0 ? style.backgroundRgb(color) : style.backgroundDefault();
            }
            default: {
                log.warning("Invalid color-mode: " + colorMode);
            }
        }
        return style;
    }
}

