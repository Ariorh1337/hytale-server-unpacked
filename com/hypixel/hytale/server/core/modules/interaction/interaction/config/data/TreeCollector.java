/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.interaction.interaction.config.data;

import com.hypixel.hytale.function.function.TriFunction;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class TreeCollector<T>
implements Collector {
    private final TriFunction<CollectorTag, InteractionContext, Interaction, T> function;
    private Node<T> root;
    private Node<T> current;

    public TreeCollector(TriFunction<CollectorTag, InteractionContext, Interaction, T> function) {
        this.function = function;
    }

    public Node<T> getRoot() {
        return this.root;
    }

    @Override
    public void start() {
        this.root = new Node(null);
        this.current = this.root;
    }

    @Override
    public void into(@Nonnull InteractionContext context, Interaction interaction) {
        this.current.children = this.current.children != null ? Arrays.copyOf(this.current.children, this.current.children.length + 1) : new Node[1];
        Node<T> node = new Node<T>(this.current);
        this.current.children[this.current.children.length - 1] = node;
        this.current = node;
    }

    @Override
    public boolean collect(@Nonnull CollectorTag tag, @Nonnull InteractionContext context, @Nonnull Interaction interaction) {
        this.current.data = this.function.apply(tag, context, interaction);
        return false;
    }

    @Override
    public void outof() {
        this.current = this.current.parent;
    }

    @Override
    public void finished() {
    }

    public static class Node<T> {
        public static final Node[] EMPTY_ARRAY = new Node[0];
        private final Node<T> parent;
        private Node<T>[] children = EMPTY_ARRAY;
        private T data;

        Node(Node<T> parent) {
            this.parent = parent;
        }

        public Node<T> getParent() {
            return this.parent;
        }

        public Node<T>[] getChildren() {
            return this.children;
        }

        public T getData() {
            return this.data;
        }
    }
}

