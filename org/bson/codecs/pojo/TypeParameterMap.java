/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.pojo.Either;

final class TypeParameterMap {
    private final Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap;

    static Builder builder() {
        return new Builder();
    }

    Map<Integer, Either<Integer, TypeParameterMap>> getPropertyToClassParamIndexMap() {
        return this.propertyToClassParamIndexMap;
    }

    boolean hasTypeParameters() {
        return !this.propertyToClassParamIndexMap.isEmpty();
    }

    public String toString() {
        return "TypeParameterMap{fieldToClassParamIndexMap=" + this.propertyToClassParamIndexMap + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TypeParameterMap that = (TypeParameterMap)o;
        return this.getPropertyToClassParamIndexMap().equals(that.getPropertyToClassParamIndexMap());
    }

    public int hashCode() {
        return this.getPropertyToClassParamIndexMap().hashCode();
    }

    private TypeParameterMap(Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap) {
        this.propertyToClassParamIndexMap = Collections.unmodifiableMap(propertyToClassParamIndexMap);
    }

    static final class Builder {
        private final Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap = new HashMap<Integer, Either<Integer, TypeParameterMap>>();

        private Builder() {
        }

        Builder addIndex(int classTypeParameterIndex) {
            this.propertyToClassParamIndexMap.put(-1, Either.left(classTypeParameterIndex));
            return this;
        }

        Builder addIndex(int propertyTypeParameterIndex, int classTypeParameterIndex) {
            this.propertyToClassParamIndexMap.put(propertyTypeParameterIndex, Either.left(classTypeParameterIndex));
            return this;
        }

        Builder addIndex(int propertyTypeParameterIndex, TypeParameterMap typeParameterMap) {
            this.propertyToClassParamIndexMap.put(propertyTypeParameterIndex, Either.right(typeParameterMap));
            return this;
        }

        TypeParameterMap build() {
            if (this.propertyToClassParamIndexMap.size() > 1 && this.propertyToClassParamIndexMap.containsKey(-1)) {
                throw new IllegalStateException("You cannot have a generic field that also has type parameters.");
            }
            return new TypeParameterMap(this.propertyToClassParamIndexMap);
        }
    }
}

