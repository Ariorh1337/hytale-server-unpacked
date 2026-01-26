/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.plugin.pending;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PendingLoadPlugin {
    @Nonnull
    private final PluginIdentifier identifier;
    @Nonnull
    private final PluginManifest manifest;
    @Nullable
    private final Path path;

    PendingLoadPlugin(@Nullable Path path, @Nonnull PluginManifest manifest) {
        this.path = path;
        this.identifier = new PluginIdentifier(manifest);
        this.manifest = manifest;
    }

    @Nonnull
    public PluginIdentifier getIdentifier() {
        return this.identifier;
    }

    @Nonnull
    public PluginManifest getManifest() {
        return this.manifest;
    }

    @Nullable
    public Path getPath() {
        return this.path;
    }

    public abstract PendingLoadPlugin createSubPendingLoadPlugin(PluginManifest var1);

    @Nullable
    public abstract PluginBase load();

    @Nonnull
    public List<PendingLoadPlugin> createSubPendingLoadPlugins() {
        List<PluginManifest> subPlugins = this.manifest.getSubPlugins();
        if (subPlugins.isEmpty()) {
            return Collections.emptyList();
        }
        ObjectArrayList<PendingLoadPlugin> plugins = new ObjectArrayList<PendingLoadPlugin>(subPlugins.size());
        for (PluginManifest subManifest : subPlugins) {
            subManifest.inherit(this.manifest);
            plugins.add(this.createSubPendingLoadPlugin(subManifest));
        }
        return plugins;
    }

    public boolean dependsOn(PluginIdentifier identifier) {
        return this.manifest.getDependencies().containsKey(identifier) || this.manifest.getOptionalDependencies().containsKey(identifier);
    }

    public abstract boolean isInServerClassPath();

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PendingLoadPlugin that = (PendingLoadPlugin)o;
        if (!this.identifier.equals(that.identifier)) {
            return false;
        }
        if (!this.manifest.equals(that.manifest)) {
            return false;
        }
        return Objects.equals(this.path, that.path);
    }

    public int hashCode() {
        int result = this.identifier.hashCode();
        result = 31 * result + this.manifest.hashCode();
        result = 31 * result + (this.path != null ? this.path.hashCode() : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "PendingLoadPlugin{identifier=" + String.valueOf(this.identifier) + ", manifest=" + String.valueOf(this.manifest) + ", path=" + String.valueOf(this.path) + "}";
    }

    @Nonnull
    public static List<PendingLoadPlugin> calculateLoadOrder(@Nonnull Map<PluginIdentifier, PendingLoadPlugin> pending) {
        HashMap<PluginIdentifier, EntryNode> nodes = new HashMap<PluginIdentifier, EntryNode>(pending.size());
        for (Map.Entry<PluginIdentifier, PendingLoadPlugin> entry : pending.entrySet()) {
            nodes.put(entry.getKey(), new EntryNode(entry.getValue()));
        }
        HashSet<PluginIdentifier> classpathPlugins = new HashSet<PluginIdentifier>();
        for (Map.Entry<PluginIdentifier, PendingLoadPlugin> entry : pending.entrySet()) {
            if (!entry.getValue().isInServerClassPath()) continue;
            classpathPlugins.add(entry.getKey());
        }
        HashMap<PluginIdentifier, Set> hashMap = new HashMap<PluginIdentifier, Set>();
        for (Object node : nodes.values()) {
            PluginManifest pluginManifest = ((EntryNode)node).plugin.manifest;
            for (PluginIdentifier pluginIdentifier : pluginManifest.getDependencies().keySet()) {
                if (nodes.containsKey(pluginIdentifier)) {
                    ((EntryNode)node).edge.add(pluginIdentifier);
                    continue;
                }
                hashMap.computeIfAbsent(((EntryNode)node).plugin.identifier, k -> new HashSet()).add(pluginIdentifier);
            }
            for (PluginIdentifier pluginIdentifier : pluginManifest.getOptionalDependencies().keySet()) {
                EntryNode dep = (EntryNode)nodes.get(pluginIdentifier);
                if (dep == null) continue;
                ((EntryNode)node).edge.add(pluginIdentifier);
            }
            if (((EntryNode)node).plugin.isInServerClassPath()) continue;
            ((EntryNode)node).edge.addAll(classpathPlugins);
        }
        HashMap<PluginIdentifier, Set> hashMap2 = new HashMap<PluginIdentifier, Set>();
        for (Map.Entry entry : pending.entrySet()) {
            PluginManifest manifest = ((PendingLoadPlugin)entry.getValue()).manifest;
            for (PluginIdentifier targetId : manifest.getLoadBefore().keySet()) {
                EntryNode targetNode = (EntryNode)nodes.get(targetId);
                if (targetNode != null) {
                    targetNode.edge.add((PluginIdentifier)entry.getKey());
                    continue;
                }
                hashMap2.computeIfAbsent((PluginIdentifier)entry.getKey(), k -> new HashSet()).add(targetId);
            }
        }
        if (!hashMap.isEmpty() || !hashMap2.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            if (!hashMap.isEmpty()) {
                sb.append("Missing required dependencies:\n");
                for (Map.Entry entry : hashMap.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(" requires: ").append(entry.getValue()).append("\n");
                }
            }
            if (!hashMap2.isEmpty()) {
                sb.append("Missing loadBefore targets:\n");
                for (Map.Entry entry : hashMap2.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(" loadBefore: ").append(entry.getValue()).append("\n");
                }
            }
            throw new IllegalArgumentException(sb.toString());
        }
        ObjectArrayList<PendingLoadPlugin> loadOrder = new ObjectArrayList<PendingLoadPlugin>(nodes.size());
        while (!nodes.isEmpty()) {
            boolean bl = false;
            Iterator iterator = nodes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                EntryNode node = (EntryNode)entry.getValue();
                if (!node.edge.isEmpty()) continue;
                bl = true;
                iterator.remove();
                loadOrder.add(node.plugin);
                PluginIdentifier identifier = (PluginIdentifier)entry.getKey();
                for (EntryNode otherNode : nodes.values()) {
                    otherNode.edge.remove(identifier);
                }
            }
            if (bl) continue;
            StringBuilder sb = new StringBuilder("Found cyclic dependency between plugins:\n");
            for (Map.Entry entry : nodes.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(" waiting on: ").append(((EntryNode)entry.getValue()).edge).append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
        return loadOrder;
    }

    private static final class EntryNode {
        private final Set<PluginIdentifier> edge = new HashSet<PluginIdentifier>();
        private final PendingLoadPlugin plugin;

        private EntryNode(PendingLoadPlugin plugin) {
            this.plugin = plugin;
        }

        @Nonnull
        public String toString() {
            return "EntryNode{plugin=" + String.valueOf(this.plugin) + ", dependencies=" + String.valueOf(this.edge) + "}";
        }
    }
}

