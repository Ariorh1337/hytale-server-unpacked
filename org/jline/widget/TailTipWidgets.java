/*
 * Decompiled with CFR 0.152.
 */
package org.jline.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.jline.builtins.Options;
import org.jline.console.ArgDesc;
import org.jline.console.CmdDesc;
import org.jline.console.CmdLine;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Buffer;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.jline.utils.Status;
import org.jline.utils.StyleResolver;
import org.jline.widget.Widgets;

public class TailTipWidgets
extends Widgets {
    private boolean enabled = false;
    private final CommandDescriptions cmdDescs;
    private TipType tipType;
    private int descriptionSize;
    private boolean descriptionEnabled = true;
    private boolean descriptionCache = false;
    private Object readerErrors;

    public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips) {
        this(reader, tailTips, 0, TipType.COMBINED);
    }

    public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, TipType tipType) {
        this(reader, tailTips, 0, tipType);
    }

    public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize) {
        this(reader, tailTips, descriptionSize, TipType.COMBINED);
    }

    public TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize, TipType tipType) {
        this(reader, tailTips, descriptionSize, tipType, null);
    }

    public TailTipWidgets(LineReader reader, Function<CmdLine, CmdDesc> descFun, int descriptionSize, TipType tipType) {
        this(reader, null, descriptionSize, tipType, descFun);
    }

    private TailTipWidgets(LineReader reader, Map<String, CmdDesc> tailTips, int descriptionSize, TipType tipType, Function<CmdLine, CmdDesc> descFun) {
        super(reader);
        if (this.existsWidget("_tailtip-accept-line")) {
            throw new IllegalStateException("TailTipWidgets already created!");
        }
        this.cmdDescs = tailTips != null ? new CommandDescriptions(tailTips) : new CommandDescriptions(descFun);
        this.descriptionSize = descriptionSize;
        this.tipType = tipType;
        this.addWidget("_tailtip-accept-line", this::tailtipAcceptLine);
        this.addWidget("_tailtip-self-insert", this::tailtipInsert);
        this.addWidget("_tailtip-backward-delete-char", this::tailtipBackwardDelete);
        this.addWidget("_tailtip-delete-char", this::tailtipDelete);
        this.addWidget("_tailtip-expand-or-complete", this::tailtipComplete);
        this.addWidget("_tailtip-redisplay", this::tailtipUpdateStatus);
        this.addWidget("_tailtip-kill-line", this::tailtipKillLine);
        this.addWidget("_tailtip-kill-whole-line", this::tailtipKillWholeLine);
        this.addWidget("tailtip-window", this::toggleWindow);
        this.addWidget("tailtip-toggle", this::toggleKeyBindings);
    }

    public void setTailTips(Map<String, CmdDesc> tailTips) {
        this.cmdDescs.setDescriptions(tailTips);
    }

    public void setDescriptionSize(int descriptionSize) {
        this.descriptionSize = descriptionSize;
        this.initDescription();
    }

    public int getDescriptionSize() {
        return this.descriptionSize;
    }

    public void setTipType(TipType type) {
        this.tipType = type;
        if (this.tipType == TipType.TAIL_TIP) {
            this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
        } else {
            this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
        }
    }

    public TipType getTipType() {
        return this.tipType;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void disable() {
        if (this.enabled) {
            this.toggleKeyBindings();
        }
    }

    public void enable() {
        if (!this.enabled) {
            this.toggleKeyBindings();
        }
    }

    public void setDescriptionCache(boolean cache) {
        this.descriptionCache = cache;
    }

    public boolean tailtipComplete() {
        if (this.doTailTip("expand-or-complete")) {
            if ("\t".equals(this.lastBinding())) {
                this.callWidget("backward-char");
                this.reader.runMacro(KeyMap.key(this.reader.getTerminal(), InfoCmp.Capability.key_right));
            }
            return true;
        }
        return false;
    }

    public boolean tailtipAcceptLine() {
        if (this.tipType != TipType.TAIL_TIP) {
            this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
        }
        this.clearDescription();
        this.setErrorPattern(null);
        this.setErrorIndex(-1);
        this.cmdDescs.clearTemporaryDescs();
        return this.clearTailTip("accept-line");
    }

    public boolean tailtipBackwardDelete() {
        return this.doTailTip(this.autopairEnabled() ? "_autopair-backward-delete-char" : "backward-delete-char");
    }

    private boolean clearTailTip(String widget) {
        this.clearTailTip();
        this.callWidget(widget);
        return true;
    }

    public boolean tailtipDelete() {
        this.clearTailTip();
        return this.doTailTip("delete-char");
    }

    public boolean tailtipKillLine() {
        this.clearTailTip();
        return this.doTailTip("kill-line");
    }

    public boolean tailtipKillWholeLine() {
        this.callWidget("kill-whole-line");
        return this.doTailTip("redisplay");
    }

    public boolean tailtipInsert() {
        return this.doTailTip(this.autopairEnabled() ? "_autopair-insert" : "self-insert");
    }

    public boolean tailtipUpdateStatus() {
        return this.doTailTip("redisplay");
    }

    private boolean doTailTip(String widget) {
        Buffer buffer = this.buffer();
        this.callWidget(widget);
        List<String> args = this.args();
        Pair cmdkey = this.cmdDescs.evaluateCommandLine(buffer.toString(), this.args(), buffer.cursor());
        CmdDesc cmdDesc = this.cmdDescs.getDescription((String)cmdkey.getU());
        if (cmdDesc == null) {
            this.setErrorPattern(null);
            this.setErrorIndex(-1);
            this.clearDescription();
            this.resetTailTip();
        } else if (cmdDesc.isValid()) {
            if (((Boolean)cmdkey.getV()).booleanValue()) {
                if (cmdDesc.isCommand() && buffer.length() == buffer.cursor()) {
                    this.doCommandTailTip(widget, cmdDesc, args);
                }
            } else {
                this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize));
                this.setErrorPattern(cmdDesc.getErrorPattern());
                this.setErrorIndex(cmdDesc.getErrorIndex());
            }
        }
        return true;
    }

    private void doCommandTailTip(String widget, CmdDesc cmdDesc, List<String> args) {
        int argnum = 0;
        String prevArg = "";
        for (String a : args) {
            if (!(a.startsWith("-") || prevArg.matches("-[a-zA-Z]") && cmdDesc.optionWithValue(prevArg))) {
                ++argnum;
            }
            prevArg = a;
        }
        String lastArg = "";
        prevArg = args.get(args.size() - 1);
        if (!this.prevChar().equals(" ") && args.size() > 1) {
            lastArg = args.get(args.size() - 1);
            prevArg = args.get(args.size() - 2);
        }
        int bpsize = argnum;
        boolean doTailTip = true;
        boolean noCompleters = false;
        if (widget.endsWith("backward-delete-char")) {
            this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
            noCompleters = true;
            if (!(lastArg.startsWith("-") || prevArg.matches("-[a-zA-Z]") && cmdDesc.optionWithValue(prevArg))) {
                --bpsize;
            }
            if (this.prevChar().equals(" ")) {
                ++bpsize;
            }
        } else if (!this.prevChar().equals(" ")) {
            doTailTip = false;
            this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize, cmdDesc.isSubcommand() ? lastArg : null));
        } else if (cmdDesc != null) {
            this.doDescription(this.compileMainDescription(cmdDesc, this.descriptionSize));
        }
        if (cmdDesc != null) {
            if (prevArg.startsWith("-") && !prevArg.contains("=") && !prevArg.matches("-[a-zA-Z][\\S]+") && cmdDesc.optionWithValue(prevArg)) {
                this.doDescription(this.compileOptionDescription(cmdDesc, prevArg, this.descriptionSize));
                this.setTipType(this.tipType);
            } else if (lastArg.matches("-[a-zA-Z][\\S]+") && cmdDesc.optionWithValue(lastArg.substring(0, 2))) {
                this.doDescription(this.compileOptionDescription(cmdDesc, lastArg.substring(0, 2), this.descriptionSize));
                this.setTipType(this.tipType);
            } else if (lastArg.startsWith("-")) {
                this.doDescription(this.compileOptionDescription(cmdDesc, lastArg, this.descriptionSize));
                if (!lastArg.contains("=")) {
                    this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
                    noCompleters = true;
                } else {
                    this.setTipType(this.tipType);
                }
            } else if (!widget.endsWith("backward-delete-char")) {
                this.setTipType(this.tipType);
            }
            if (bpsize > 0 && doTailTip) {
                List<ArgDesc> params = cmdDesc.getArgsDesc();
                if (!noCompleters) {
                    this.setSuggestionType(this.tipType == TipType.COMPLETER ? LineReader.SuggestionType.COMPLETER : LineReader.SuggestionType.TAIL_TIP);
                }
                if (bpsize - 1 < params.size()) {
                    if (!lastArg.startsWith("-")) {
                        List<AttributedString> d = !prevArg.startsWith("-") || !cmdDesc.optionWithValue(prevArg) ? params.get(bpsize - 1).getDescription() : this.compileOptionDescription(cmdDesc, prevArg, this.descriptionSize);
                        if (d == null || d.isEmpty()) {
                            d = this.compileMainDescription(cmdDesc, this.descriptionSize, cmdDesc.isSubcommand() ? lastArg : null);
                        }
                        this.doDescription(d);
                    }
                    StringBuilder tip = new StringBuilder();
                    for (int i = bpsize - 1; i < params.size(); ++i) {
                        tip.append(params.get(i).getName());
                        tip.append(" ");
                    }
                    this.setTailTip(tip.toString());
                } else if (!params.isEmpty() && params.get(params.size() - 1).getName().startsWith("[")) {
                    this.setTailTip(params.get(params.size() - 1).getName());
                    this.doDescription(params.get(params.size() - 1).getDescription());
                }
            } else if (doTailTip) {
                this.resetTailTip();
            }
        } else {
            this.clearDescription();
            this.resetTailTip();
        }
    }

    private void resetTailTip() {
        this.setTailTip("");
        if (this.tipType != TipType.TAIL_TIP) {
            this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
        }
    }

    private void doDescription(List<AttributedString> desc) {
        if (this.descriptionSize == 0 || !this.descriptionEnabled) {
            return;
        }
        List<AttributedString> list = desc;
        if (list.size() > this.descriptionSize) {
            AttributedStringBuilder asb = new AttributedStringBuilder();
            asb.append(list.get(this.descriptionSize - 1)).append("\u2026", new AttributedStyle(AttributedStyle.INVERSE));
            ArrayList<AttributedString> mod = new ArrayList<AttributedString>(list.subList(0, this.descriptionSize - 1));
            mod.add(asb.toAttributedString());
            list = mod;
        } else if (list.size() < this.descriptionSize) {
            ArrayList<AttributedString> mod = new ArrayList<AttributedString>(list);
            while (mod.size() != this.descriptionSize) {
                mod.add(new AttributedString(""));
            }
            list = mod;
        }
        this.setDescription(list);
    }

    public void initDescription() {
        Status.getStatus(this.reader.getTerminal()).setBorder(true);
        this.clearDescription();
    }

    @Override
    public void clearDescription() {
        this.doDescription(Collections.emptyList());
    }

    private boolean autopairEnabled() {
        Binding binding = this.getKeyMap().getBound("(");
        return binding instanceof Reference && ((Reference)binding).name().equals("_autopair-insert");
    }

    public boolean toggleWindow() {
        boolean bl = this.descriptionEnabled = !this.descriptionEnabled;
        if (this.descriptionEnabled) {
            this.initDescription();
        } else {
            this.destroyDescription();
        }
        this.callWidget("redraw-line");
        return true;
    }

    public boolean toggleKeyBindings() {
        if (this.enabled) {
            this.defaultBindings();
            this.destroyDescription();
            this.reader.setVariable("errors", this.readerErrors);
        } else {
            this.customBindings();
            if (this.descriptionEnabled) {
                this.initDescription();
            }
            this.readerErrors = this.reader.getVariable("errors");
            this.reader.setVariable("errors", 0);
        }
        try {
            this.callWidget("redraw-line");
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.enabled;
    }

    private boolean defaultBindings() {
        if (!this.enabled) {
            return false;
        }
        this.aliasWidget(".accept-line", "accept-line");
        this.aliasWidget(".backward-delete-char", "backward-delete-char");
        this.aliasWidget(".delete-char", "delete-char");
        this.aliasWidget(".expand-or-complete", "expand-or-complete");
        this.aliasWidget(".self-insert", "self-insert");
        this.aliasWidget(".redisplay", "redisplay");
        this.aliasWidget(".kill-line", "kill-line");
        this.aliasWidget(".kill-whole-line", "kill-whole-line");
        KeyMap<Binding> map = this.getKeyMap();
        map.bind((Binding)new Reference("insert-close-paren"), (CharSequence)")");
        this.setSuggestionType(LineReader.SuggestionType.NONE);
        if (this.autopairEnabled()) {
            this.callWidget("autopair-toggle");
            this.callWidget("autopair-toggle");
        }
        this.enabled = false;
        return true;
    }

    private void customBindings() {
        if (this.enabled) {
            return;
        }
        this.aliasWidget("_tailtip-accept-line", "accept-line");
        this.aliasWidget("_tailtip-backward-delete-char", "backward-delete-char");
        this.aliasWidget("_tailtip-delete-char", "delete-char");
        this.aliasWidget("_tailtip-expand-or-complete", "expand-or-complete");
        this.aliasWidget("_tailtip-self-insert", "self-insert");
        this.aliasWidget("_tailtip-redisplay", "redisplay");
        this.aliasWidget("_tailtip-kill-line", "kill-line");
        this.aliasWidget("_tailtip-kill-whole-line", "kill-whole-line");
        KeyMap<Binding> map = this.getKeyMap();
        map.bind((Binding)new Reference("_tailtip-self-insert"), (CharSequence)")");
        if (this.tipType != TipType.TAIL_TIP) {
            this.setSuggestionType(LineReader.SuggestionType.COMPLETER);
        } else {
            this.setSuggestionType(LineReader.SuggestionType.TAIL_TIP);
        }
        this.enabled = true;
    }

    private List<AttributedString> compileMainDescription(CmdDesc cmdDesc, int descriptionSize) {
        return this.compileMainDescription(cmdDesc, descriptionSize, null);
    }

    private List<AttributedString> compileMainDescription(CmdDesc cmdDesc, int descriptionSize, String lastArg) {
        if (descriptionSize == 0 || !this.descriptionEnabled) {
            return new ArrayList<AttributedString>();
        }
        ArrayList<AttributedString> out = new ArrayList<AttributedString>();
        List<AttributedString> mainDesc = cmdDesc.getMainDesc();
        if (mainDesc == null) {
            return out;
        }
        if (cmdDesc.isCommand() && cmdDesc.isValid() && !cmdDesc.isHighlighted()) {
            mainDesc = new ArrayList<AttributedString>();
            StyleResolver resolver = Options.HelpException.defaultStyle();
            for (AttributedString as : cmdDesc.getMainDesc()) {
                mainDesc.add(Options.HelpException.highlightSyntax(as.toString(), resolver));
            }
        }
        if (mainDesc.size() <= descriptionSize && lastArg == null) {
            out.addAll(mainDesc);
        } else {
            int tabs = 0;
            for (AttributedString as : mainDesc) {
                if (as.columnLength() < tabs) continue;
                tabs = as.columnLength() + 2;
            }
            int row = 0;
            int col = 0;
            ArrayList<AttributedString> descList = new ArrayList<AttributedString>();
            for (int i = 0; i < descriptionSize; ++i) {
                descList.add(new AttributedString(""));
            }
            for (AttributedString as : mainDesc) {
                if (lastArg != null && !as.toString().startsWith(lastArg)) continue;
                AttributedStringBuilder asb = new AttributedStringBuilder().tabs(tabs);
                if (col > 0) {
                    asb.append((AttributedString)descList.get(row));
                    asb.append("\t");
                }
                asb.append(as);
                descList.remove(row);
                descList.add(row, asb.toAttributedString());
                if (++row < descriptionSize) continue;
                row = 0;
                ++col;
            }
            out = new ArrayList(descList);
        }
        return out;
    }

    private List<AttributedString> compileOptionDescription(CmdDesc cmdDesc, String opt, int descriptionSize) {
        ArrayList<AttributedString> keyList;
        AttributedStringBuilder asb;
        if (descriptionSize == 0 || !this.descriptionEnabled) {
            return new ArrayList<AttributedString>();
        }
        ArrayList<AttributedString> out = new ArrayList<AttributedString>();
        TreeMap<String, List<AttributedString>> optsDesc = cmdDesc.getOptsDesc();
        StyleResolver resolver = Options.HelpException.defaultStyle();
        if (!opt.startsWith("-")) {
            return out;
        }
        int ind = opt.indexOf("=");
        if (ind > 0) {
            opt = opt.substring(0, ind);
        }
        ArrayList<String> matched = new ArrayList<String>();
        int tabs = 0;
        block0: for (String key : optsDesc.keySet()) {
            for (String k : key.split("\\s+")) {
                if (!k.trim().startsWith(opt)) continue;
                matched.add(key);
                if (key.length() < tabs) continue block0;
                tabs = key.length() + 2;
                continue block0;
            }
        }
        if (matched.size() == 1) {
            out.add(Options.HelpException.highlightSyntax((String)matched.get(0), resolver));
            for (AttributedString as : (List)optsDesc.get(matched.get(0))) {
                asb = new AttributedStringBuilder().tabs(8);
                asb.append("\t");
                asb.append(as);
                out.add(asb.toAttributedString());
            }
        } else if (matched.size() <= descriptionSize) {
            for (String key : matched) {
                asb = new AttributedStringBuilder().tabs(tabs);
                asb.append(Options.HelpException.highlightSyntax(key, resolver));
                asb.append("\t");
                asb.append(cmdDesc.optionDescription(key));
                out.add(asb.toAttributedString());
            }
        } else if (matched.size() <= 2 * descriptionSize) {
            int columnWidth;
            keyList = new ArrayList<AttributedString>();
            int row = 0;
            for (columnWidth = 2 * tabs; columnWidth < 50; columnWidth += tabs) {
            }
            for (String key : matched) {
                AttributedStringBuilder asb2 = new AttributedStringBuilder().tabs(tabs);
                if (row < descriptionSize) {
                    asb2.append(Options.HelpException.highlightSyntax(key, resolver));
                    asb2.append("\t");
                    asb2.append(cmdDesc.optionDescription(key));
                    if (asb2.columnLength() > columnWidth - 2) {
                        AttributedString trunc = asb2.columnSubSequence(0, columnWidth - 5);
                        asb2 = new AttributedStringBuilder().tabs(tabs);
                        asb2.append(trunc);
                        asb2.append("...", new AttributedStyle(AttributedStyle.INVERSE));
                        asb2.append("  ");
                    } else {
                        for (int i = asb2.columnLength(); i < columnWidth; ++i) {
                            asb2.append(" ");
                        }
                    }
                    keyList.add(asb2.toAttributedString().columnSubSequence(0, columnWidth));
                } else {
                    asb2.append((AttributedString)keyList.get(row - descriptionSize));
                    asb2.append(Options.HelpException.highlightSyntax(key, resolver));
                    asb2.append("\t");
                    asb2.append(cmdDesc.optionDescription(key));
                    keyList.remove(row - descriptionSize);
                    keyList.add(row - descriptionSize, asb2.toAttributedString());
                }
                ++row;
            }
            out = new ArrayList(keyList);
        } else {
            keyList = new ArrayList();
            for (int i = 0; i < descriptionSize; ++i) {
                keyList.add(new AttributedString(""));
            }
            int row = 0;
            for (String key : matched) {
                AttributedStringBuilder asb3 = new AttributedStringBuilder().tabs(tabs);
                asb3.append((AttributedString)keyList.get(row));
                asb3.append(Options.HelpException.highlightSyntax(key, resolver));
                asb3.append("\t");
                keyList.remove(row);
                keyList.add(row, asb3.toAttributedString());
                if (++row < descriptionSize) continue;
                row = 0;
            }
            out = new ArrayList(keyList);
        }
        return out;
    }

    public static enum TipType {
        TAIL_TIP,
        COMPLETER,
        COMBINED;

    }

    private class CommandDescriptions {
        Map<String, CmdDesc> descriptions = new HashMap<String, CmdDesc>();
        Map<String, CmdDesc> temporaryDescs = new HashMap<String, CmdDesc>();
        Map<String, CmdDesc> volatileDescs = new HashMap<String, CmdDesc>();
        Function<CmdLine, CmdDesc> descFun;

        public CommandDescriptions(Map<String, CmdDesc> descriptions) {
            this.descriptions = new HashMap<String, CmdDesc>(descriptions);
        }

        public CommandDescriptions(Function<CmdLine, CmdDesc> descFun) {
            this.descFun = descFun;
        }

        public void setDescriptions(Map<String, CmdDesc> descriptions) {
            this.descriptions = new HashMap<String, CmdDesc>(descriptions);
        }

        public Pair<String, Boolean> evaluateCommandLine(String line, int curPos) {
            return this.evaluateCommandLine(line, TailTipWidgets.this.args(), curPos);
        }

        public Pair<String, Boolean> evaluateCommandLine(String line, List<String> args) {
            return this.evaluateCommandLine(line, args, line.length());
        }

        private Pair<String, Boolean> evaluateCommandLine(String line, List<String> args, int curPos) {
            String cmd = null;
            CmdLine.DescriptionType descType = CmdLine.DescriptionType.METHOD;
            String head = line.substring(0, curPos);
            String tail = line.substring(curPos);
            if (TailTipWidgets.this.prevChar().equals(")")) {
                descType = CmdLine.DescriptionType.SYNTAX;
                cmd = head;
            } else {
                int i;
                if (line.length() == curPos) {
                    cmd = args != null && (args.size() > 1 || args.size() == 1 && line.endsWith(" ")) ? TailTipWidgets.this.parser().getCommand(args.get(0)) : null;
                    descType = CmdLine.DescriptionType.COMMAND;
                }
                int brackets = 0;
                for (i = head.length() - 1; i >= 0; --i) {
                    if (head.charAt(i) == ')') {
                        ++brackets;
                    } else if (head.charAt(i) == '(') {
                        --brackets;
                    }
                    if (brackets >= 0) continue;
                    descType = CmdLine.DescriptionType.METHOD;
                    cmd = head = head.substring(0, i);
                    break;
                }
                if (descType == CmdLine.DescriptionType.METHOD) {
                    brackets = 0;
                    for (i = 0; i < tail.length(); ++i) {
                        if (tail.charAt(i) == ')') {
                            ++brackets;
                        } else if (tail.charAt(i) == '(') {
                            --brackets;
                        }
                        if (brackets <= 0) continue;
                        tail = tail.substring(i + 1);
                        break;
                    }
                }
            }
            if (cmd != null && this.descFun != null && !this.descriptions.containsKey(cmd) && !this.temporaryDescs.containsKey(cmd)) {
                CmdDesc c = this.descFun.apply(new CmdLine(line, head, tail, args, descType));
                if (descType == CmdLine.DescriptionType.COMMAND) {
                    if (!TailTipWidgets.this.descriptionCache) {
                        this.volatileDescs.put(cmd, c);
                    } else if (c != null) {
                        this.descriptions.put(cmd, c);
                    } else {
                        this.temporaryDescs.put(cmd, null);
                    }
                } else {
                    this.temporaryDescs.put(cmd, c);
                }
            }
            return new Pair<String, Boolean>(cmd, descType == CmdLine.DescriptionType.COMMAND);
        }

        public CmdDesc getDescription(String command) {
            CmdDesc out = this.descriptions.containsKey(command) ? this.descriptions.get(command) : (this.temporaryDescs.containsKey(command) ? this.temporaryDescs.get(command) : this.volatileDescs.remove(command));
            return out;
        }

        public void clearTemporaryDescs() {
            this.temporaryDescs.clear();
        }
    }

    static class Pair<U, V> {
        final U u;
        final V v;

        public Pair(U u, V v) {
            this.u = u;
            this.v = v;
        }

        public U getU() {
            return this.u;
        }

        public V getV() {
            return this.v;
        }
    }
}

