/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.protocol.BoolParamValue;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.DoubleParamValue;
import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.IntParamValue;
import com.hypixel.hytale.protocol.LongParamValue;
import com.hypixel.hytale.protocol.ParamValue;
import com.hypixel.hytale.protocol.StringParamValue;
import com.hypixel.hytale.protocol.packets.asseteditor.FailureReply;
import com.hypixel.hytale.protocol.packets.asseteditor.SuccessReply;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.lang.runtime.SwitchBootstraps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;

public class MessageUtil {
    private static final String[] ICU_PLURAL_KEYWORDS = new String[]{"zero", "one", "two", "few", "many", "other"};

    public static AttributedString toAnsiString(@Nonnull Message message) {
        AttributedStyle style = AttributedStyle.DEFAULT;
        String color = message.getColor();
        if (color != null) {
            style = MessageUtil.hexToStyle(color);
        }
        AttributedStringBuilder sb = new AttributedStringBuilder();
        sb.style(style).append(message.getAnsiMessage());
        List<Message> children = message.getChildren();
        for (Message child : children) {
            sb.append(MessageUtil.toAnsiString(child));
        }
        return sb.toAttributedString();
    }

    public static AttributedStyle hexToStyle(@Nonnull String str) {
        Color color = ColorParseUtil.parseColor(str);
        if (color == null) {
            return AttributedStyle.DEFAULT;
        }
        int colorId = Colors.roundRgbColor(color.red & 0xFF, color.green & 0xFF, color.blue & 0xFF, 256);
        return AttributedStyle.DEFAULT.foreground(colorId);
    }

    @Deprecated
    public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token) {
        MessageUtil.sendSuccessReply(playerRef, token, null);
    }

    @Deprecated
    public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token, @Nullable Message message) {
        FormattedMessage msg = message != null ? message.getFormattedMessage() : null;
        playerRef.getPacketHandler().writeNoCache(new SuccessReply(token, msg));
    }

    @Deprecated
    public static void sendFailureReply(@Nonnull PlayerRef playerRef, int token, @Nonnull Message message) {
        FormattedMessage msg = message != null ? message.getFormattedMessage() : null;
        playerRef.getPacketHandler().writeNoCache(new FailureReply(token, msg));
    }

    @Nonnull
    public static String formatText(String text, @Nullable Map<String, ParamValue> params, @Nullable Map<String, FormattedMessage> messageParams) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (params == null && messageParams == null) {
            return text;
        }
        int len = text.length();
        StringBuilder sb = new StringBuilder(text.length());
        int lastWritePos = 0;
        for (int i = 0; i < len; ++i) {
            char ch = text.charAt(i);
            if (ch == '{') {
                FormattedMessage replacementMessage;
                int optionsStart;
                int os;
                int ol;
                int formatEndExclusive;
                int formatStart;
                int fs;
                int fl;
                int contentStart;
                int c1;
                if (i + 1 < len && text.charAt(i + 1) == '{') {
                    if (i > lastWritePos) {
                        sb.append(text, lastWritePos, i);
                    }
                    sb.append('{');
                    lastWritePos = ++i + 1;
                    continue;
                }
                int end = MessageUtil.findMatchingBrace(text, i);
                if (end < 0) continue;
                if (i > lastWritePos) {
                    sb.append(text, lastWritePos, i);
                }
                int c2 = (c1 = text.indexOf(44, contentStart = i + 1, end)) >= 0 ? text.indexOf(44, c1 + 1, end) : -1;
                int nameStart = contentStart;
                int nameEndExclusive = c1 >= 0 && c1 < end ? c1 : end;
                int ns = MessageUtil.trimStart(text, nameStart, nameEndExclusive - 1);
                int nl = MessageUtil.trimEnd(text, ns, nameEndExclusive - 1);
                String key = nl > 0 ? text.substring(ns, ns + nl) : "";
                Object format = null;
                if (c1 >= 0 && c1 < end && (fl = MessageUtil.trimEnd(text, fs = MessageUtil.trimStart(text, formatStart = c1 + 1, (formatEndExclusive = c2 >= 0 ? c2 : end) - 1), formatEndExclusive - 1)) > 0) {
                    format = text.substring(fs, fs + fl);
                }
                Object options = null;
                if (c2 >= 0 && c2 < end && (ol = MessageUtil.trimEnd(text, os = MessageUtil.trimStart(text, optionsStart = c2 + 1, end - 1), end - 1)) > 0) {
                    options = text.substring(os, os + ol);
                }
                ParamValue replacement = params != null ? params.get(key) : null;
                FormattedMessage formattedMessage = replacementMessage = messageParams != null ? messageParams.get(key) : null;
                if (replacementMessage != null) {
                    if (replacementMessage.rawText != null) {
                        sb.append(replacementMessage.rawText);
                    } else if (replacementMessage.messageId != null) {
                        String message = I18nModule.get().getMessage("en-US", replacementMessage.messageId);
                        if (message != null) {
                            sb.append(MessageUtil.formatText(message, replacementMessage.params, replacementMessage.messageParams));
                        } else {
                            sb.append(replacementMessage.messageId);
                        }
                    }
                } else if (replacement != null) {
                    StringParamValue s;
                    String formattedReplacement = "";
                    ParamValue paramValue = format;
                    int n = 0;
                    block0 : switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{"upper", "lower", "number", "plural"}, paramValue, n)) {
                        case 0: {
                            if (!(replacement instanceof StringParamValue)) break;
                            s = (StringParamValue)replacement;
                            formattedReplacement = s.value.toUpperCase();
                            break;
                        }
                        case 1: {
                            if (!(replacement instanceof StringParamValue)) break;
                            s = (StringParamValue)replacement;
                            formattedReplacement = s.value.toLowerCase();
                            break;
                        }
                        case 2: {
                            LongParamValue l;
                            IntParamValue iv;
                            DoubleParamValue d;
                            BoolParamValue b;
                            StringParamValue s2;
                            ParamValue paramValue2;
                            int n2;
                            s = options;
                            int n3 = 0;
                            switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{"integer", "decimal"}, (Object)s, n3)) {
                                case 0: {
                                    Objects.requireNonNull(replacement);
                                    n2 = 0;
                                    formattedReplacement = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{StringParamValue.class, BoolParamValue.class, DoubleParamValue.class, IntParamValue.class, LongParamValue.class}, (Object)paramValue2, n2)) {
                                        case 0 -> {
                                            s2 = (StringParamValue)paramValue2;
                                            yield s2.value;
                                        }
                                        case 1 -> {
                                            b = (BoolParamValue)paramValue2;
                                            if (b.value) {
                                                yield "1";
                                            }
                                            yield "0";
                                        }
                                        case 2 -> {
                                            d = (DoubleParamValue)paramValue2;
                                            yield Integer.toString((int)d.value);
                                        }
                                        case 3 -> {
                                            iv = (IntParamValue)paramValue2;
                                            yield Integer.toString(iv.value);
                                        }
                                        case 4 -> {
                                            l = (LongParamValue)paramValue2;
                                            yield Long.toString(l.value);
                                        }
                                        default -> "";
                                    };
                                    break block0;
                                }
                            }
                            Objects.requireNonNull(replacement);
                            n2 = 0;
                            formattedReplacement = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{StringParamValue.class, BoolParamValue.class, DoubleParamValue.class, IntParamValue.class, LongParamValue.class}, (Object)paramValue2, n2)) {
                                case 0 -> {
                                    s2 = (StringParamValue)paramValue2;
                                    yield s2.value;
                                }
                                case 1 -> {
                                    b = (BoolParamValue)paramValue2;
                                    if (b.value) {
                                        yield "1";
                                    }
                                    yield "0";
                                }
                                case 2 -> {
                                    d = (DoubleParamValue)paramValue2;
                                    yield Double.toString((int)d.value);
                                }
                                case 3 -> {
                                    iv = (IntParamValue)paramValue2;
                                    yield Integer.toString(iv.value);
                                }
                                case 4 -> {
                                    l = (LongParamValue)paramValue2;
                                    yield Long.toString(l.value);
                                }
                                default -> "";
                            };
                            break;
                        }
                        case 3: {
                            int value;
                            String category;
                            if (options == null) break;
                            Map<String, String> pluralTexts = MessageUtil.parsePluralOptions(options);
                            String selected = pluralTexts.containsKey(category = MessageUtil.getPluralCategory(value = Integer.parseInt(replacement.toString()), "en-US")) ? pluralTexts.get(category) : (pluralTexts.containsKey("other") ? pluralTexts.get("other") : (pluralTexts.isEmpty() ? "" : pluralTexts.values().iterator().next()));
                            formattedReplacement = MessageUtil.formatText(selected, params, messageParams);
                            break;
                        }
                    }
                    if (format == null) {
                        ParamValue paramValue3 = replacement;
                        Objects.requireNonNull(paramValue3);
                        paramValue = paramValue3;
                        n = 0;
                        formattedReplacement = switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{StringParamValue.class, BoolParamValue.class, DoubleParamValue.class, IntParamValue.class, LongParamValue.class}, (Object)paramValue, n)) {
                            case 0 -> {
                                s = (StringParamValue)paramValue;
                                yield s.value;
                            }
                            case 1 -> {
                                BoolParamValue b = (BoolParamValue)paramValue;
                                yield Boolean.toString(b.value);
                            }
                            case 2 -> {
                                DoubleParamValue d = (DoubleParamValue)paramValue;
                                yield Double.toString(d.value);
                            }
                            case 3 -> {
                                IntParamValue iv = (IntParamValue)paramValue;
                                yield Integer.toString(iv.value);
                            }
                            case 4 -> {
                                LongParamValue l = (LongParamValue)paramValue;
                                yield Long.toString(l.value);
                            }
                            default -> "";
                        };
                    }
                    sb.append(formattedReplacement);
                } else {
                    sb.append(text, i, end);
                }
                i = end;
                lastWritePos = end + 1;
                continue;
            }
            if (ch != '}' || i + 1 >= len || text.charAt(i + 1) != '}') continue;
            if (i > lastWritePos) {
                sb.append(text, lastWritePos, i);
            }
            sb.append('}');
            lastWritePos = ++i + 1;
        }
        if (lastWritePos < len) {
            sb.append(text, lastWritePos, len);
        }
        return sb.toString();
    }

    private static int findMatchingBrace(@Nonnull String text, int start) {
        int depth = 0;
        int len = text.length();
        for (int i = start; i < len; ++i) {
            if (text.charAt(i) == '{') {
                ++depth;
                continue;
            }
            if (text.charAt(i) != '}' || --depth != 0) continue;
            return i;
        }
        return -1;
    }

    private static int trimStart(@Nonnull String text, int start, int end) {
        int i;
        for (i = start; i <= end && Character.isWhitespace(text.charAt(i)); ++i) {
        }
        return i;
    }

    private static int trimEnd(@Nonnull String text, int start, int end) {
        int i = start;
        while (end >= i && Character.isWhitespace(text.charAt(i))) {
            --end;
        }
        return end >= i ? end - i + 1 : 0;
    }

    @Nonnull
    private static Map<String, String> parsePluralOptions(@Nonnull String options) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String keyword : ICU_PLURAL_KEYWORDS) {
            int braceStart;
            int end;
            String searchPattern = keyword + " {";
            int idx = options.indexOf(searchPattern);
            if (idx < 0 || (end = MessageUtil.findMatchingBrace(options, braceStart = idx + keyword.length() + 1)) <= braceStart + 1) continue;
            result.put(keyword, options.substring(braceStart + 1, end));
        }
        return result;
    }

    @Nonnull
    private static String getPluralCategory(int n, @Nonnull String locale) {
        String lang;
        return switch (lang = locale.contains("-") ? locale.substring(0, locale.indexOf(45)) : locale) {
            case "en" -> MessageUtil.getEnglishPluralCategory(n);
            case "fr" -> MessageUtil.getFrenchPluralCategory(n);
            case "de" -> MessageUtil.getGermanPluralCategory(n);
            case "pt" -> {
                if (locale.equals("pt-BR") || locale.equals("pt_BR")) {
                    yield MessageUtil.getPortugueseBrazilianPluralCategory(n);
                }
                yield MessageUtil.getPortuguesePluralCategory(n);
            }
            case "ru" -> MessageUtil.getRussianPluralCategory(n);
            case "es" -> MessageUtil.getSpanishPluralCategory(n);
            case "pl" -> MessageUtil.getPolishPluralCategory(n);
            case "tr" -> MessageUtil.getTurkishPluralCategory(n);
            case "uk" -> MessageUtil.getUkrainianPluralCategory(n);
            case "it" -> MessageUtil.getItalianPluralCategory(n);
            case "nl" -> MessageUtil.getDutchPluralCategory(n);
            case "da" -> MessageUtil.getDanishPluralCategory(n);
            case "fi" -> MessageUtil.getFinnishPluralCategory(n);
            case "no", "nb", "nn" -> MessageUtil.getNorwegianPluralCategory(n);
            case "zh" -> MessageUtil.getChinesePluralCategory(n);
            case "ja" -> MessageUtil.getJapanesePluralCategory(n);
            case "ko" -> MessageUtil.getKoreanPluralCategory(n);
            default -> MessageUtil.getEnglishPluralCategory(n);
        };
    }

    @Nonnull
    private static String getEnglishPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getFrenchPluralCategory(int n) {
        return n == 0 || n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getGermanPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getPortuguesePluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getPortugueseBrazilianPluralCategory(int n) {
        return n == 0 || n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getRussianPluralCategory(int n) {
        int absN = Math.abs(n);
        int mod10 = absN % 10;
        int mod100 = absN % 100;
        if (mod10 == 1 && mod100 != 11) {
            return "one";
        }
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
            return "few";
        }
        if (mod10 == 0 || mod10 >= 5 && mod10 <= 9 || mod100 >= 11 && mod100 <= 14) {
            return "many";
        }
        return "other";
    }

    @Nonnull
    private static String getSpanishPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getPolishPluralCategory(int n) {
        int absN = Math.abs(n);
        int mod10 = absN % 10;
        int mod100 = absN % 100;
        if (n == 1) {
            return "one";
        }
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
            return "few";
        }
        if (mod10 == 0 || mod10 == 1 || mod10 >= 5 && mod10 <= 9 || mod100 >= 12 && mod100 <= 14) {
            return "many";
        }
        return "other";
    }

    @Nonnull
    private static String getTurkishPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getUkrainianPluralCategory(int n) {
        int absN = Math.abs(n);
        int mod10 = absN % 10;
        int mod100 = absN % 100;
        if (mod10 == 1 && mod100 != 11) {
            return "one";
        }
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
            return "few";
        }
        if (mod10 == 0 || mod10 >= 5 && mod10 <= 9 || mod100 >= 11 && mod100 <= 14) {
            return "many";
        }
        return "other";
    }

    @Nonnull
    private static String getItalianPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getDutchPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getDanishPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getFinnishPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getNorwegianPluralCategory(int n) {
        return n == 1 ? "one" : "other";
    }

    @Nonnull
    private static String getChinesePluralCategory(int n) {
        return "other";
    }

    @Nonnull
    private static String getJapanesePluralCategory(int n) {
        return "other";
    }

    @Nonnull
    private static String getKoreanPluralCategory(int n) {
        return "other";
    }
}

