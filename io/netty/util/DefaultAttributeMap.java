/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap
implements AttributeMap {
    private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, DefaultAttribute[]> ATTRIBUTES_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, DefaultAttribute[].class, "attributes");
    private static final DefaultAttribute[] EMPTY_ATTRIBUTES = new DefaultAttribute[0];
    private volatile DefaultAttribute[] attributes = EMPTY_ATTRIBUTES;

    private static int searchAttributeByKey(DefaultAttribute[] sortedAttributes, AttributeKey<?> key) {
        int low = 0;
        int high = sortedAttributes.length - 1;
        while (low <= high) {
            boolean searchRight;
            int mid = low + high >>> 1;
            DefaultAttribute midVal = sortedAttributes[mid];
            AttributeKey midValKey = midVal.key;
            if (midValKey == key) {
                return mid;
            }
            int midValKeyId = midValKey.id();
            int keyId = key.id();
            assert (midValKeyId != keyId);
            boolean bl = searchRight = midValKeyId < keyId;
            if (searchRight) {
                low = mid + 1;
                continue;
            }
            high = mid - 1;
        }
        return -(low + 1);
    }

    private static void orderedCopyOnInsert(DefaultAttribute[] sortedSrc, int srcLength, DefaultAttribute[] copy, DefaultAttribute toInsert) {
        int i;
        int id = toInsert.key.id();
        for (i = srcLength - 1; i >= 0; --i) {
            DefaultAttribute attribute = sortedSrc[i];
            assert (attribute.key.id() != id);
            if (attribute.key.id() < id) break;
            copy[i + 1] = sortedSrc[i];
        }
        copy[i + 1] = toInsert;
        int toCopy = i + 1;
        if (toCopy > 0) {
            System.arraycopy(sortedSrc, 0, copy, 0, toCopy);
        }
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        DefaultAttribute[] newAttributes;
        DefaultAttribute[] attributes;
        ObjectUtil.checkNotNull(key, "key");
        DefaultAttribute<T> newAttribute = null;
        do {
            int index;
            if ((index = DefaultAttributeMap.searchAttributeByKey(attributes = this.attributes, key)) >= 0) {
                DefaultAttribute attribute = attributes[index];
                assert (attribute.key() == key);
                if (!attribute.isRemoved()) {
                    return attribute;
                }
                if (newAttribute == null) {
                    newAttribute = new DefaultAttribute<T>(this, key);
                }
                int count = attributes.length;
                newAttributes = Arrays.copyOf(attributes, count);
                newAttributes[index] = newAttribute;
                continue;
            }
            if (newAttribute == null) {
                newAttribute = new DefaultAttribute<T>(this, key);
            }
            int count = attributes.length;
            newAttributes = new DefaultAttribute[count + 1];
            DefaultAttributeMap.orderedCopyOnInsert(attributes, count, newAttributes, newAttribute);
        } while (!ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes));
        return newAttribute;
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        ObjectUtil.checkNotNull(key, "key");
        return DefaultAttributeMap.searchAttributeByKey(this.attributes, key) >= 0;
    }

    private <T> void removeAttributeIfMatch(AttributeKey<T> key, DefaultAttribute<T> value) {
        DefaultAttribute[] newAttributes;
        DefaultAttribute[] attributes;
        do {
            int index;
            if ((index = DefaultAttributeMap.searchAttributeByKey(attributes = this.attributes, key)) < 0) {
                return;
            }
            DefaultAttribute attribute = attributes[index];
            assert (attribute.key() == key);
            if (attribute != value) {
                return;
            }
            int count = attributes.length;
            int newCount = count - 1;
            newAttributes = newCount == 0 ? EMPTY_ATTRIBUTES : new DefaultAttribute[newCount];
            System.arraycopy(attributes, 0, newAttributes, 0, index);
            int remaining = count - index - 1;
            if (remaining <= 0) continue;
            System.arraycopy(attributes, index + 1, newAttributes, index, remaining);
        } while (!ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes));
    }

    private static final class DefaultAttribute<T>
    extends AtomicReference<T>
    implements Attribute<T> {
        private static final AtomicReferenceFieldUpdater<DefaultAttribute, DefaultAttributeMap> MAP_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultAttribute.class, DefaultAttributeMap.class, "attributeMap");
        private static final long serialVersionUID = -2661411462200283011L;
        private volatile DefaultAttributeMap attributeMap;
        private final AttributeKey<T> key;

        DefaultAttribute(DefaultAttributeMap attributeMap, AttributeKey<T> key) {
            this.attributeMap = attributeMap;
            this.key = key;
        }

        @Override
        public AttributeKey<T> key() {
            return this.key;
        }

        private boolean isRemoved() {
            return this.attributeMap == null;
        }

        @Override
        public T setIfAbsent(T value) {
            while (!this.compareAndSet(null, value)) {
                Object old = this.get();
                if (old == null) continue;
                return (T)old;
            }
            return null;
        }

        @Override
        public T getAndRemove() {
            DefaultAttributeMap attributeMap = this.attributeMap;
            boolean removed = attributeMap != null && MAP_UPDATER.compareAndSet(this, attributeMap, null);
            T oldValue = this.getAndSet(null);
            if (removed) {
                attributeMap.removeAttributeIfMatch(this.key, this);
            }
            return oldValue;
        }

        @Override
        public void remove() {
            DefaultAttributeMap attributeMap = this.attributeMap;
            boolean removed = attributeMap != null && MAP_UPDATER.compareAndSet(this, attributeMap, null);
            this.set(null);
            if (removed) {
                attributeMap.removeAttributeIfMatch(this.key, this);
            }
        }
    }
}

