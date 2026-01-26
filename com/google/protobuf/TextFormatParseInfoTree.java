/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.TextFormatParseLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextFormatParseInfoTree {
    private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField;
    Map<Descriptors.FieldDescriptor, List<TextFormatParseInfoTree>> subtreesFromField;

    private TextFormatParseInfoTree(Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField, Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField) {
        HashMap<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locs = new HashMap<Descriptors.FieldDescriptor, List<TextFormatParseLocation>>();
        for (Map.Entry<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> kv : locationsFromField.entrySet()) {
            locs.put(kv.getKey(), Collections.unmodifiableList(kv.getValue()));
        }
        this.locationsFromField = Collections.unmodifiableMap(locs);
        HashMap subs = new HashMap();
        for (Map.Entry<Descriptors.FieldDescriptor, List<Builder>> kv : subtreeBuildersFromField.entrySet()) {
            ArrayList<TextFormatParseInfoTree> submessagesOfField = new ArrayList<TextFormatParseInfoTree>();
            for (Builder subBuilder : kv.getValue()) {
                submessagesOfField.add(subBuilder.build());
            }
            subs.put(kv.getKey(), Collections.unmodifiableList(submessagesOfField));
        }
        this.subtreesFromField = Collections.unmodifiableMap(subs);
    }

    public List<TextFormatParseLocation> getLocations(Descriptors.FieldDescriptor fieldDescriptor) {
        List<TextFormatParseLocation> result = this.locationsFromField.get(fieldDescriptor);
        return result == null ? Collections.emptyList() : result;
    }

    public TextFormatParseLocation getLocation(Descriptors.FieldDescriptor fieldDescriptor, int index) {
        return TextFormatParseInfoTree.getFromList(this.getLocations(fieldDescriptor), index, fieldDescriptor);
    }

    public List<TextFormatParseInfoTree> getNestedTrees(Descriptors.FieldDescriptor fieldDescriptor) {
        List<TextFormatParseInfoTree> result = this.subtreesFromField.get(fieldDescriptor);
        return result == null ? Collections.emptyList() : result;
    }

    public TextFormatParseInfoTree getNestedTree(Descriptors.FieldDescriptor fieldDescriptor, int index) {
        return TextFormatParseInfoTree.getFromList(this.getNestedTrees(fieldDescriptor), index, fieldDescriptor);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static <T> T getFromList(List<T> list, int index, Descriptors.FieldDescriptor fieldDescriptor) {
        if (index >= list.size() || index < 0) {
            throw new IllegalArgumentException(String.format("Illegal index field: %s, index %d", fieldDescriptor == null ? "<null>" : fieldDescriptor.getName(), index));
        }
        return list.get(index);
    }

    public static class Builder {
        private Map<Descriptors.FieldDescriptor, List<TextFormatParseLocation>> locationsFromField = new HashMap<Descriptors.FieldDescriptor, List<TextFormatParseLocation>>();
        private Map<Descriptors.FieldDescriptor, List<Builder>> subtreeBuildersFromField = new HashMap<Descriptors.FieldDescriptor, List<Builder>>();

        private Builder() {
        }

        public Builder setLocation(Descriptors.FieldDescriptor fieldDescriptor, TextFormatParseLocation location) {
            List<TextFormatParseLocation> fieldLocations = this.locationsFromField.get(fieldDescriptor);
            if (fieldLocations == null) {
                fieldLocations = new ArrayList<TextFormatParseLocation>();
                this.locationsFromField.put(fieldDescriptor, fieldLocations);
            }
            fieldLocations.add(location);
            return this;
        }

        public Builder getBuilderForSubMessageField(Descriptors.FieldDescriptor fieldDescriptor) {
            List<Builder> submessageBuilders = this.subtreeBuildersFromField.get(fieldDescriptor);
            if (submessageBuilders == null) {
                submessageBuilders = new ArrayList<Builder>();
                this.subtreeBuildersFromField.put(fieldDescriptor, submessageBuilders);
            }
            Builder subtreeBuilder = new Builder();
            submessageBuilders.add(subtreeBuilder);
            return subtreeBuilder;
        }

        public TextFormatParseInfoTree build() {
            return new TextFormatParseInfoTree(this.locationsFromField, this.subtreeBuildersFromField);
        }
    }
}

