/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.valuestore;

import com.hypixel.hytale.server.npc.asset.builder.BuilderContext;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class ValueStoreValidator {
    private final EnumMap<ValueStore.Type, HashMap<String, List<ValueUsage>>> usages = new EnumMap(ValueStore.Type.class);

    public void registerValueUsage(@Nonnull ValueUsage usage) {
        if (usage.useType == UseType.READ) {
            return;
        }
        HashMap usagesOfType = this.usages.computeIfAbsent(usage.valueType, k -> new HashMap());
        List usagesByParameter = usagesOfType.computeIfAbsent(usage.name, k -> new ObjectArrayList());
        usagesByParameter.add(usage);
    }

    public boolean validate(@Nonnull List<String> errors) {
        boolean result = true;
        for (ValueStore.Type type : ValueStore.Type.VALUES) {
            result &= this.validateType(type, errors);
        }
        return result;
    }

    private boolean validateType(@Nonnull ValueStore.Type type, @Nonnull List<String> errors) {
        HashMap<String, List<ValueUsage>> usagesOfType = this.usages.get(type);
        if (usagesOfType == null) {
            return true;
        }
        boolean result = true;
        ObjectArrayList<ValueUsage> writes = new ObjectArrayList<ValueUsage>();
        ObjectArrayList<ValueUsage> exclusiveWrites = new ObjectArrayList<ValueUsage>();
        for (Map.Entry<String, List<ValueUsage>> usagesByParameter : usagesOfType.entrySet()) {
            for (ValueUsage usage : usagesByParameter.getValue()) {
                writes.add(usage);
                if (usage.useType != UseType.EXCLUSIVE_WRITE) continue;
                exclusiveWrites.add(usage);
            }
            if (writes.size() > 1 && !exclusiveWrites.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("The core components [ ");
                for (ValueUsage writer : exclusiveWrites) {
                    sb.append(writer.context.getLabel()).append(" ");
                }
                sb.append("] require an exclusive write of the ").append(type.get()).append(" parameter '").append(usagesByParameter.getKey()).append("' but it is written to by [ ");
                for (ValueUsage writer : writes) {
                    sb.append(writer.context.getLabel()).append(" ");
                }
                sb.append("]");
                errors.add(sb.toString());
                result = false;
            }
            writes.clear();
            exclusiveWrites.clear();
        }
        return result;
    }

    public static class ValueUsage {
        protected final String name;
        protected final ValueStore.Type valueType;
        protected final UseType useType;
        protected final BuilderContext context;

        public ValueUsage(String name, ValueStore.Type valueType, UseType useType, BuilderContext context) {
            this.name = name;
            this.valueType = valueType;
            this.useType = useType;
            this.context = context;
        }
    }

    public static enum UseType implements Supplier<String>
    {
        READ("Reads the value"),
        WRITE("Writes the value"),
        EXCLUSIVE_WRITE("Has exclusive write ownership of the value");

        private final String description;

        private UseType(String description) {
            this.description = description;
        }

        @Override
        public String get() {
            return this.description;
        }
    }
}

