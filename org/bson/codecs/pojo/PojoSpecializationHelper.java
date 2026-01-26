/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.codecs.pojo.Either;
import org.bson.codecs.pojo.TypeData;
import org.bson.codecs.pojo.TypeParameterMap;

final class PojoSpecializationHelper {
    static <V> TypeData<V> specializeTypeData(TypeData<V> typeData, List<TypeData<?>> typeParameters, TypeParameterMap typeParameterMap) {
        if (!typeParameterMap.hasTypeParameters() || typeParameters.isEmpty()) {
            return typeData;
        }
        Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap = typeParameterMap.getPropertyToClassParamIndexMap();
        Either<Integer, TypeParameterMap> classTypeParamRepresentsWholeField = propertyToClassParamIndexMap.get(-1);
        if (classTypeParamRepresentsWholeField != null) {
            Integer index = classTypeParamRepresentsWholeField.map(i -> i, e -> {
                throw new IllegalStateException("Invalid state, the whole class cannot be represented by a subtype.");
            });
            return typeParameters.get(index);
        }
        return PojoSpecializationHelper.getTypeData(typeData, typeParameters, propertyToClassParamIndexMap);
    }

    private static <V> TypeData<V> getTypeData(TypeData<V> typeData, List<TypeData<?>> specializedTypeParameters, Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap) {
        ArrayList subTypeParameters = new ArrayList(typeData.getTypeParameters());
        for (int i = 0; i < typeData.getTypeParameters().size(); ++i) {
            subTypeParameters.set(i, PojoSpecializationHelper.getTypeData((TypeData)subTypeParameters.get(i), specializedTypeParameters, propertyToClassParamIndexMap, i));
        }
        return TypeData.builder(typeData.getType()).addTypeParameters(subTypeParameters).build();
    }

    private static TypeData<?> getTypeData(TypeData<?> typeData, List<TypeData<?>> specializedTypeParameters, Map<Integer, Either<Integer, TypeParameterMap>> propertyToClassParamIndexMap, int index) {
        if (!propertyToClassParamIndexMap.containsKey(index)) {
            return typeData;
        }
        return propertyToClassParamIndexMap.get(index).map(l -> {
            if (typeData.getTypeParameters().isEmpty()) {
                return (TypeData)specializedTypeParameters.get((int)l);
            }
            TypeData.Builder builder = TypeData.builder(typeData.getType());
            ArrayList typeParameters = new ArrayList(typeData.getTypeParameters());
            typeParameters.set(index, (TypeData)specializedTypeParameters.get((int)l));
            builder.addTypeParameters(typeParameters);
            return builder.build();
        }, r -> PojoSpecializationHelper.getTypeData(typeData, specializedTypeParameters, r.getPropertyToClassParamIndexMap()));
    }

    private PojoSpecializationHelper() {
    }
}

