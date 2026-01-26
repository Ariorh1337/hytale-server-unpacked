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
                                            StringParamValue s = (StringParamValue)paramValue2;
                                            yield s.value;
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
                                    StringParamValue s = (StringParamValue)paramValue2;
                                    yield s.value;
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
                            int otherStart;
                            int otherEnd;
                            int oneStart;
                            int oneEnd;
                            if (options == null) break;
                            String oneText = null;
                            String otherText = null;
                            int oneIdx = ((String)options).indexOf("one {");
                            int otherIdx = ((String)options).indexOf("other {");
                            if (oneIdx >= 0 && (oneEnd = MessageUtil.findMatchingBrace((String)options, (oneStart = oneIdx + "one {".length()) - 1)) > oneStart) {
                                oneText = ((String)options).substring(oneStart, oneEnd);
                            }
                            if (otherIdx >= 0 && (otherEnd = MessageUtil.findMatchingBrace((String)options, (otherStart = otherIdx + "other {".length()) - 1)) > otherStart) {
                                otherText = ((String)options).substring(otherStart, otherEnd);
                            }
                            String selected = (value = Integer.parseInt(replacement.toString())) == 1 && oneText != null ? oneText : (otherText != null ? otherText : (oneText != null ? oneText : ""));
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
}

