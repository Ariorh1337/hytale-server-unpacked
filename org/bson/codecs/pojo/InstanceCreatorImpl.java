/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.CreatorExecutable;
import org.bson.codecs.pojo.InstanceCreator;
import org.bson.codecs.pojo.PropertyModel;

final class InstanceCreatorImpl<T>
implements InstanceCreator<T> {
    private final CreatorExecutable<T> creatorExecutable;
    private final Map<PropertyModel<?>, Object> cachedValues;
    private final Map<String, Integer> properties;
    private final Object[] params;
    private T newInstance;

    InstanceCreatorImpl(CreatorExecutable<T> creatorExecutable) {
        this.creatorExecutable = creatorExecutable;
        if (creatorExecutable.getProperties().isEmpty()) {
            this.cachedValues = null;
            this.properties = null;
            this.params = null;
            this.newInstance = creatorExecutable.getInstance();
        } else {
            this.cachedValues = new HashMap();
            this.properties = new HashMap<String, Integer>();
            for (int i = 0; i < creatorExecutable.getProperties().size(); ++i) {
                if (creatorExecutable.getIdPropertyIndex() != null && creatorExecutable.getIdPropertyIndex() == i) {
                    this.properties.put("_id", creatorExecutable.getIdPropertyIndex());
                    continue;
                }
                this.properties.put(creatorExecutable.getProperties().get(i).value(), i);
            }
            this.params = new Object[this.properties.size()];
        }
    }

    @Override
    public <S> void set(S value, PropertyModel<S> propertyModel) {
        if (this.newInstance != null) {
            propertyModel.getPropertyAccessor().set(this.newInstance, value);
        } else {
            if (!this.properties.isEmpty()) {
                Integer index;
                String propertyName = propertyModel.getWriteName();
                if (!this.properties.containsKey(propertyName)) {
                    propertyName = propertyModel.getName();
                }
                if ((index = this.properties.get(propertyName)) != null) {
                    this.params[index.intValue()] = value;
                }
                this.properties.remove(propertyName);
            }
            if (this.properties.isEmpty()) {
                this.constructInstanceAndProcessCachedValues();
            } else {
                this.cachedValues.put(propertyModel, value);
            }
        }
    }

    @Override
    public T getInstance() {
        if (this.newInstance == null) {
            try {
                for (Map.Entry<String, Integer> entry : this.properties.entrySet()) {
                    this.params[entry.getValue().intValue()] = null;
                }
                this.constructInstanceAndProcessCachedValues();
            }
            catch (CodecConfigurationException e) {
                throw new CodecConfigurationException(String.format("Could not construct new instance of: %s. Missing the following properties: %s", this.creatorExecutable.getType().getSimpleName(), this.properties.keySet()), e);
            }
        }
        return this.newInstance;
    }

    private void constructInstanceAndProcessCachedValues() {
        try {
            this.newInstance = this.creatorExecutable.getInstance(this.params);
        }
        catch (Exception e) {
            throw new CodecConfigurationException(e.getMessage(), e);
        }
        for (Map.Entry<PropertyModel<?>, Object> entry : this.cachedValues.entrySet()) {
            this.setPropertyValue(entry.getKey(), entry.getValue());
        }
    }

    private <S> void setPropertyValue(PropertyModel<S> propertyModel, Object value) {
        this.set(value, propertyModel);
    }
}

