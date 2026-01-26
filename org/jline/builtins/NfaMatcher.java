/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NfaMatcher<T> {
    private final String regexp;
    private final BiFunction<T, String, Boolean> matcher;
    private volatile State start;

    public NfaMatcher(String regexp, BiFunction<T, String, Boolean> matcher) {
        this.regexp = regexp;
        this.matcher = matcher;
    }

    public void compile() {
        if (this.start == null) {
            this.start = NfaMatcher.toNfa(NfaMatcher.toPostFix(this.regexp));
        }
    }

    public boolean match(List<T> args) {
        HashSet<State> clist = new HashSet<State>();
        this.compile();
        this.addState(clist, this.start);
        for (T arg : args) {
            HashSet nlist = new HashSet();
            clist.stream().filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c)).filter(s -> this.matcher.apply(arg, s.c)).forEach(s -> this.addState(nlist, s.out));
            clist = nlist;
        }
        return clist.stream().anyMatch(s -> Objects.equals("++MATCH++", s.c));
    }

    public Set<String> matchPartial(List<T> args) {
        HashSet<State> clist = new HashSet<State>();
        this.compile();
        this.addState(clist, this.start);
        for (T arg : args) {
            HashSet nlist = new HashSet();
            clist.stream().filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c)).filter(s -> this.matcher.apply(arg, s.c)).forEach(s -> this.addState(nlist, s.out));
            clist = nlist;
        }
        return clist.stream().filter(s -> !Objects.equals("++MATCH++", s.c) && !Objects.equals("++SPLIT++", s.c)).map(s -> s.c).collect(Collectors.toSet());
    }

    void addState(Set<State> l, State s) {
        if (s != null && l.add(s) && Objects.equals("++SPLIT++", s.c)) {
            this.addState(l, s.out);
            this.addState(l, s.out1);
        }
    }

    static State toNfa(List<String> postfix) {
        Frag e;
        ArrayDeque<Frag> stack = new ArrayDeque<Frag>();
        Iterator<String> iterator = postfix.iterator();
        block14: while (iterator.hasNext()) {
            State s;
            String p;
            switch (p = iterator.next()) {
                case ".": {
                    Frag e2 = (Frag)stack.pollLast();
                    Frag e1 = (Frag)stack.pollLast();
                    e1.patch(e2.start);
                    stack.offerLast(new Frag(e1.start, e2.out));
                    continue block14;
                }
                case "|": {
                    Frag e2 = (Frag)stack.pollLast();
                    Frag e1 = (Frag)stack.pollLast();
                    s = new State("++SPLIT++", e1.start, e2.start);
                    stack.offerLast(new Frag(s, e1.out, e2.out));
                    continue block14;
                }
                case "?": {
                    e = (Frag)stack.pollLast();
                    s = new State("++SPLIT++", e.start, null);
                    stack.offerLast(new Frag(s, e.out, s::setOut1));
                    continue block14;
                }
                case "*": {
                    e = (Frag)stack.pollLast();
                    s = new State("++SPLIT++", e.start, null);
                    e.patch(s);
                    stack.offerLast(new Frag(s, s::setOut1));
                    continue block14;
                }
                case "+": {
                    e = (Frag)stack.pollLast();
                    s = new State("++SPLIT++", e.start, null);
                    e.patch(s);
                    stack.offerLast(new Frag(e.start, s::setOut1));
                    continue block14;
                }
            }
            s = new State(p, null, null);
            stack.offerLast(new Frag(s, s::setOut));
        }
        e = (Frag)stack.pollLast();
        if (!stack.isEmpty()) {
            throw new IllegalStateException("Wrong postfix expression, " + stack.size() + " elements remaining");
        }
        e.patch(new State("++MATCH++", null, null));
        return e.start;
    }

    static List<String> toPostFix(String regexp) {
        ArrayList<String> postfix = new ArrayList<String>();
        int s = -1;
        int natom = 0;
        int nalt = 0;
        ArrayDeque<Integer> natoms = new ArrayDeque<Integer>();
        ArrayDeque<Integer> nalts = new ArrayDeque<Integer>();
        block6: for (int i = 0; i < regexp.length(); ++i) {
            char c = regexp.charAt(i);
            if (Character.isJavaIdentifierPart(c)) {
                if (s >= 0) continue;
                s = i;
                continue;
            }
            if (s >= 0) {
                if (natom > 1) {
                    --natom;
                    postfix.add(".");
                }
                postfix.add(regexp.substring(s, i));
                ++natom;
                s = -1;
            }
            if (Character.isWhitespace(c)) continue;
            switch (c) {
                case '(': {
                    if (natom > 1) {
                        --natom;
                        postfix.add(".");
                    }
                    nalts.offerLast(nalt);
                    natoms.offerLast(natom);
                    nalt = 0;
                    natom = 0;
                    continue block6;
                }
                case '|': {
                    if (natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                    }
                    while (--natom > 0) {
                        postfix.add(".");
                    }
                    ++nalt;
                    continue block6;
                }
                case ')': {
                    if (nalts.isEmpty() || natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                    }
                    while (--natom > 0) {
                        postfix.add(".");
                    }
                    while (nalt > 0) {
                        postfix.add("|");
                        --nalt;
                    }
                    nalt = (Integer)nalts.pollLast();
                    natom = (Integer)natoms.pollLast();
                    ++natom;
                    continue block6;
                }
                case '*': 
                case '+': 
                case '?': {
                    if (natom == 0) {
                        throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                    }
                    postfix.add(String.valueOf(c));
                    continue block6;
                }
                default: {
                    throw new IllegalStateException("unexpected '" + c + "' at pos " + i);
                }
            }
        }
        if (s >= 0) {
            if (natom > 1) {
                --natom;
                postfix.add(".");
            }
            postfix.add(regexp.substring(s));
            ++natom;
        }
        while (--natom > 0) {
            postfix.add(".");
        }
        while (nalt > 0) {
            postfix.add("|");
            --nalt;
        }
        return postfix;
    }

    static class State {
        static final String Match = "++MATCH++";
        static final String Split = "++SPLIT++";
        final String c;
        State out;
        State out1;

        public State(String c, State out, State out1) {
            this.c = c;
            this.out = out;
            this.out1 = out1;
        }

        public void setOut(State out) {
            this.out = out;
        }

        public void setOut1(State out1) {
            this.out1 = out1;
        }
    }

    private static class Frag {
        final State start;
        final List<Consumer<State>> out = new ArrayList<Consumer<State>>();

        public Frag(State start, Collection<Consumer<State>> l) {
            this.start = start;
            this.out.addAll(l);
        }

        public Frag(State start, Collection<Consumer<State>> l1, Collection<Consumer<State>> l2) {
            this.start = start;
            this.out.addAll(l1);
            this.out.addAll(l2);
        }

        public Frag(State start, Consumer<State> c) {
            this.start = start;
            this.out.add(c);
        }

        public Frag(State start, Collection<Consumer<State>> l, Consumer<State> c) {
            this.start = start;
            this.out.addAll(l);
            this.out.add(c);
        }

        public void patch(State s) {
            this.out.forEach(c -> c.accept(s));
        }
    }
}

