/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.ui;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.ValueCodec;

public class Anchor {
    public static final BuilderCodec<Anchor> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Anchor.class, Anchor::new).addField(new KeyedCodec<Integer>("Left", ValueCodec.INTEGER), (p, t) -> {
        p.left = t;
    }, p -> p.left)).addField(new KeyedCodec<Integer>("Right", ValueCodec.INTEGER), (p, t) -> {
        p.right = t;
    }, p -> p.right)).addField(new KeyedCodec<Integer>("Top", ValueCodec.INTEGER), (p, t) -> {
        p.top = t;
    }, p -> p.top)).addField(new KeyedCodec<Integer>("Bottom", ValueCodec.INTEGER), (p, t) -> {
        p.bottom = t;
    }, p -> p.bottom)).addField(new KeyedCodec<Integer>("Height", ValueCodec.INTEGER), (p, t) -> {
        p.height = t;
    }, p -> p.height)).addField(new KeyedCodec<Integer>("Full", ValueCodec.INTEGER), (p, t) -> {
        p.full = t;
    }, p -> p.full)).addField(new KeyedCodec<Integer>("Horizontal", ValueCodec.INTEGER), (p, t) -> {
        p.horizontal = t;
    }, p -> p.horizontal)).addField(new KeyedCodec<Integer>("Vertical", ValueCodec.INTEGER), (p, t) -> {
        p.vertical = t;
    }, p -> p.vertical)).addField(new KeyedCodec<Integer>("Width", ValueCodec.INTEGER), (p, t) -> {
        p.width = t;
    }, p -> p.width)).addField(new KeyedCodec<Integer>("MinWidth", ValueCodec.INTEGER), (p, t) -> {
        p.minWidth = t;
    }, p -> p.minWidth)).addField(new KeyedCodec<Integer>("MaxWidth", ValueCodec.INTEGER), (p, t) -> {
        p.maxWidth = t;
    }, p -> p.maxWidth)).build();
    private Value<Integer> left;
    private Value<Integer> right;
    private Value<Integer> top;
    private Value<Integer> bottom;
    private Value<Integer> height;
    private Value<Integer> full;
    private Value<Integer> horizontal;
    private Value<Integer> vertical;
    private Value<Integer> width;
    private Value<Integer> minWidth;
    private Value<Integer> maxWidth;

    public void setLeft(Value<Integer> left) {
        this.left = left;
    }

    public void setRight(Value<Integer> right) {
        this.right = right;
    }

    public void setTop(Value<Integer> top) {
        this.top = top;
    }

    public void setBottom(Value<Integer> bottom) {
        this.bottom = bottom;
    }

    public void setHeight(Value<Integer> height) {
        this.height = height;
    }

    public void setFull(Value<Integer> full) {
        this.full = full;
    }

    public void setHorizontal(Value<Integer> horizontal) {
        this.horizontal = horizontal;
    }

    public void setVertical(Value<Integer> vertical) {
        this.vertical = vertical;
    }

    public void setWidth(Value<Integer> width) {
        this.width = width;
    }

    public void setMinWidth(Value<Integer> minWidth) {
        this.minWidth = minWidth;
    }

    public void setMaxWidth(Value<Integer> maxWidth) {
        this.maxWidth = maxWidth;
    }
}

