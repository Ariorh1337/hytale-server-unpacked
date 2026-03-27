package com.hypixel.hytale.common.plugin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface Mod {
   PluginManifest getManifest();

   default boolean isCoreMod() {
      return false;
   }

   @Nonnull
   static <T extends Mod> List<T> calculateLoadOrder(@Nonnull Map<PluginIdentifier, T> pending) throws ModLoadOrderException {
      return calculateLoadOrder(pending, pluginIdentifier -> false);
   }

   @Nonnull
   static <T extends Mod> List<T> calculateLoadOrder(@Nonnull Map<PluginIdentifier, T> pending, @Nonnull Predicate<PluginIdentifier> externalDepsValidator) throws ModLoadOrderException {
      return calculateLoadOrder(pending, externalDepsValidator, List.of());
   }

   @Nonnull
   static <T extends Mod> List<T> calculateLoadOrder(
      @Nonnull Map<PluginIdentifier, T> pending, @Nonnull Predicate<PluginIdentifier> externalDepsValidator, @Nonnull List<PluginIdentifier> manualOrder
   ) throws ModLoadOrderException {
      HashMap<PluginIdentifier, Mod.EntryNode<T>> nodes = new HashMap<>(pending.size());

      for (Entry<PluginIdentifier, T> entry : pending.entrySet()) {
         nodes.put(entry.getKey(), new Mod.EntryNode<>(entry.getValue()));
      }

      HashSet<PluginIdentifier> classpathPlugins = new HashSet<>();

      for (Entry<PluginIdentifier, T> entry : pending.entrySet()) {
         if (entry.getValue().isCoreMod() && "Hytale".equals(entry.getKey().getGroup())) {
            classpathPlugins.add(entry.getKey());
         }
      }

      HashMap<PluginIdentifier, Set<PluginIdentifier>> missingDependencies = new HashMap<>();

      for (Entry<PluginIdentifier, Mod.EntryNode<T>> node : nodes.entrySet()) {
         PluginManifest manifest = node.getValue().mod.getManifest();

         for (PluginIdentifier depId : manifest.getDependencies().keySet()) {
            if (nodes.containsKey(depId)) {
               node.getValue().edges.add(depId);
            } else if (!externalDepsValidator.test(depId)) {
               missingDependencies.computeIfAbsent(node.getKey(), k -> new HashSet<>()).add(depId);
            }
         }

         for (PluginIdentifier identifier : manifest.getOptionalDependencies().keySet()) {
            Mod.EntryNode<T> dep = nodes.get(identifier);
            if (dep != null) {
               node.getValue().edges.add(identifier);
            }
         }

         if (!classpathPlugins.contains(node.getKey())) {
            node.getValue().edges.addAll(classpathPlugins);
         }
      }

      for (Entry<PluginIdentifier, T> entry : pending.entrySet()) {
         PluginManifest manifest = entry.getValue().getManifest();

         for (PluginIdentifier targetId : manifest.getLoadBefore().keySet()) {
            Mod.EntryNode<T> targetNode = nodes.get(targetId);
            if (targetNode != null) {
               targetNode.edges.add(entry.getKey());
            }
         }
      }

      ArrayList<PluginIdentifier> filteredManualOrder = new ArrayList<>();
      HashSet<PluginIdentifier> seen = new HashSet<>();

      for (PluginIdentifier id : manualOrder) {
         if (nodes.containsKey(id) && seen.add(id)) {
            filteredManualOrder.add(id);
         }
      }

      for (int i = 0; i < filteredManualOrder.size() - 1; i++) {
         PluginIdentifier earlier = filteredManualOrder.get(i);
         PluginIdentifier later = filteredManualOrder.get(i + 1);
         Mod.EntryNode<T> laterNode = nodes.get(later);
         laterNode.edges.add(earlier);
         laterNode.manualOrderEdges.add(earlier);
      }

      if (!missingDependencies.isEmpty()) {
         StringBuilder sb = new StringBuilder("Missing required dependencies:\n");

         for (Entry<PluginIdentifier, Set<PluginIdentifier>> entry : missingDependencies.entrySet()) {
            sb.append("  ")
               .append(entry.getKey())
               .append(" requires: [")
               .append(String.join(", ", entry.getValue().stream().map(PluginIdentifier::toString).toList()))
               .append("]\n");
         }

         throw new ModLoadOrderException(ModLoadOrderException.Type.MISSING_DEPENDENCY, sb.toString());
      } else {
         ObjectArrayList<T> loadOrder = new ObjectArrayList<>(nodes.size());

         while (!nodes.isEmpty()) {
            ArrayList<PluginIdentifier> resolved = new ArrayList<>();

            for (Entry<PluginIdentifier, Mod.EntryNode<T>> entry : nodes.entrySet()) {
               if (entry.getValue().edges.isEmpty()) {
                  resolved.add(entry.getKey());
               }
            }

            resolved.sort(Comparator.comparing(PluginIdentifier::toString));
            if (resolved.isEmpty()) {
               boolean hasManualOrderConflict = false;

               for (Entry<PluginIdentifier, Mod.EntryNode<T>> entry : nodes.entrySet()) {
                  HashSet<PluginIdentifier> remaining = new HashSet<>(entry.getValue().manualOrderEdges);
                  remaining.retainAll(nodes.keySet());
                  if (!remaining.isEmpty()) {
                     hasManualOrderConflict = true;
                     break;
                  }
               }

               StringBuilder sb;
               ModLoadOrderException.Type errorType;
               if (hasManualOrderConflict) {
                  errorType = ModLoadOrderException.Type.MANUAL_ORDER_CONFLICT;
                  sb = new StringBuilder("Manual load order conflicts with plugin dependencies:\n");

                  for (Entry<PluginIdentifier, Mod.EntryNode<T>> entry : nodes.entrySet()) {
                     HashSet<PluginIdentifier> depEdges = new HashSet<>(entry.getValue().edges);
                     depEdges.removeAll(entry.getValue().manualOrderEdges);
                     HashSet<PluginIdentifier> manualEdges = new HashSet<>(entry.getValue().manualOrderEdges);
                     manualEdges.retainAll(nodes.keySet());
                     ArrayList<String> parts = new ArrayList<>();
                     if (!depEdges.isEmpty()) {
                        parts.add("[" + String.join(", ", depEdges.stream().map(PluginIdentifier::toString).toList()) + "] (dependencies)");
                     }

                     if (!manualEdges.isEmpty()) {
                        parts.add("[" + String.join(", ", manualEdges.stream().map(PluginIdentifier::toString).toList()) + "] (manual order)");
                     }

                     if (!parts.isEmpty()) {
                        sb.append("  ").append(entry.getKey()).append(" waiting on: ").append(String.join(" ", parts)).append("\n");
                     }
                  }
               } else {
                  errorType = ModLoadOrderException.Type.CYCLIC_DEPENDENCY;
                  sb = new StringBuilder("Found cyclic dependency between plugins:\n");

                  for (Entry<PluginIdentifier, Mod.EntryNode<T>> entry : nodes.entrySet()) {
                     sb.append("  ")
                        .append(entry.getKey())
                        .append(" waiting on: [")
                        .append(String.join(", ", entry.getValue().edges.stream().map(PluginIdentifier::toString).toList()))
                        .append("]\n");
                  }
               }

               throw new ModLoadOrderException(errorType, sb.toString());
            }

            for (PluginIdentifier id : resolved) {
               loadOrder.add(nodes.get(id).mod);
               nodes.remove(id);
            }

            for (PluginIdentifier id : resolved) {
               for (Mod.EntryNode<T> otherNode : nodes.values()) {
                  otherNode.edges.remove(id);
               }
            }
         }

         return loadOrder;
      }
   }

   final class EntryNode<T> {
      private final Set<PluginIdentifier> edges = new HashSet<>();
      private final Set<PluginIdentifier> manualOrderEdges = new HashSet<>();
      private final T mod;

      private EntryNode(T mod) {
         this.mod = mod;
      }

      @Nonnull
      @Override
      public String toString() {
         return "EntryNode{plugin=" + this.mod + ", dependencies=" + this.edges + "}";
      }
   }
}
