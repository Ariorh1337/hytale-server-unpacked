/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.bson.assertions.Assertions;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.CreatorExecutable;
import org.bson.codecs.pojo.InstanceCreatorFactoryImpl;
import org.bson.codecs.pojo.PojoSpecializationHelper;
import org.bson.codecs.pojo.PropertyAccessorImpl;
import org.bson.codecs.pojo.PropertyMetadata;
import org.bson.codecs.pojo.PropertyModel;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.PropertyModelSerializationImpl;
import org.bson.codecs.pojo.PropertyReflectionUtils;
import org.bson.codecs.pojo.TypeData;
import org.bson.codecs.pojo.TypeParameterMap;

final class PojoBuilderHelper {
    /*
     * WARNING - void declaration
     */
    static <T> void configureClassModelBuilder(ClassModelBuilder<T> classModelBuilder, Class<T> clazz) {
        classModelBuilder.type(Assertions.notNull("clazz", clazz));
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        TreeSet<String> propertyNames = new TreeSet<String>();
        HashMap<String, TypeParameterMap> propertyTypeParameterMap = new HashMap<String, TypeParameterMap>();
        Class<T> currentClass = clazz;
        String declaringClassName = clazz.getSimpleName();
        TypeData<T> parentClassTypeData = null;
        HashMap propertyNameMap = new HashMap();
        while (!currentClass.isEnum() && currentClass.getSuperclass() != null) {
            void var13_17;
            PropertyMetadata<?> propertyMetadata;
            annotations.addAll(Arrays.asList(currentClass.getDeclaredAnnotations()));
            ArrayList genericTypeNames = new ArrayList();
            for (AnnotatedElement annotatedElement : currentClass.getTypeParameters()) {
                genericTypeNames.add(annotatedElement.getName());
            }
            PropertyReflectionUtils.PropertyMethods propertyMethods = PropertyReflectionUtils.getPropertyMethods(currentClass);
            for (Method method : propertyMethods.getSetterMethods()) {
                String string = PropertyReflectionUtils.toPropertyName(method);
                propertyNames.add(string);
                propertyMetadata = PojoBuilderHelper.getOrCreateMethodPropertyMetadata(string, declaringClassName, propertyNameMap, TypeData.newInstance(method), propertyTypeParameterMap, parentClassTypeData, genericTypeNames, PojoBuilderHelper.getGenericType(method));
                if (propertyMetadata.getSetter() != null) continue;
                propertyMetadata.setSetter(method);
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    propertyMetadata.addWriteAnnotation(annotation);
                }
            }
            for (Method method : propertyMethods.getGetterMethods()) {
                String string = PropertyReflectionUtils.toPropertyName(method);
                propertyNames.add(string);
                propertyMetadata = (PropertyMetadata<?>)propertyNameMap.get(string);
                if (propertyMetadata != null && propertyMetadata.getGetter() != null || (propertyMetadata = PojoBuilderHelper.getOrCreateMethodPropertyMetadata(string, declaringClassName, propertyNameMap, TypeData.newInstance(method), propertyTypeParameterMap, parentClassTypeData, genericTypeNames, PojoBuilderHelper.getGenericType(method))).getGetter() != null) continue;
                propertyMetadata.setGetter(method);
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    propertyMetadata.addReadAnnotation(annotation);
                }
            }
            Field[] fieldArray = currentClass.getDeclaredFields();
            int n = fieldArray.length;
            boolean bl = false;
            while (var13_17 < n) {
                Field field = fieldArray[var13_17];
                propertyNames.add(field.getName());
                PropertyMetadata<?> propertyMetadata2 = PojoBuilderHelper.getOrCreateFieldPropertyMetadata(field.getName(), declaringClassName, propertyNameMap, TypeData.newInstance(field), propertyTypeParameterMap, parentClassTypeData, genericTypeNames, field.getGenericType());
                if (propertyMetadata2 != null && propertyMetadata2.getField() == null) {
                    propertyMetadata2.field(field);
                    for (Annotation annotation : field.getDeclaredAnnotations()) {
                        propertyMetadata2.addReadAnnotation(annotation);
                        propertyMetadata2.addWriteAnnotation(annotation);
                    }
                }
                ++var13_17;
            }
            parentClassTypeData = TypeData.newInstance(currentClass.getGenericSuperclass(), currentClass);
            currentClass = currentClass.getSuperclass();
        }
        if (currentClass.isInterface()) {
            annotations.addAll(Arrays.asList(currentClass.getDeclaredAnnotations()));
        }
        for (String propertyName : propertyNames) {
            PropertyMetadata propertyMetadata = (PropertyMetadata)propertyNameMap.get(propertyName);
            if (!propertyMetadata.isSerializable() && !propertyMetadata.isDeserializable()) continue;
            classModelBuilder.addProperty(PojoBuilderHelper.createPropertyModelBuilder(propertyMetadata));
        }
        Collections.reverse(annotations);
        classModelBuilder.annotations(annotations);
        classModelBuilder.propertyNameToTypeParameterMap(propertyTypeParameterMap);
        AnnotatedElement noArgsConstructor = null;
        for (AnnotatedElement annotatedElement : clazz.getDeclaredConstructors()) {
            if (((Constructor)annotatedElement).getParameterTypes().length != 0 || !Modifier.isPublic(((Constructor)annotatedElement).getModifiers()) && !Modifier.isProtected(((Constructor)annotatedElement).getModifiers())) continue;
            noArgsConstructor = annotatedElement;
            ((Constructor)noArgsConstructor).setAccessible(true);
        }
        classModelBuilder.instanceCreatorFactory(new InstanceCreatorFactoryImpl<T>(new CreatorExecutable<T>(clazz, noArgsConstructor)));
    }

    private static <T, S> PropertyMetadata<T> getOrCreateMethodPropertyMetadata(String propertyName, String declaringClassName, Map<String, PropertyMetadata<?>> propertyNameMap, TypeData<T> typeData, Map<String, TypeParameterMap> propertyTypeParameterMap, TypeData<S> parentClassTypeData, List<String> genericTypeNames, Type genericType) {
        PropertyMetadata<T> propertyMetadata = PojoBuilderHelper.getOrCreatePropertyMetadata(propertyName, declaringClassName, propertyNameMap, typeData);
        if (!PojoBuilderHelper.isAssignableClass(propertyMetadata.getTypeData().getType(), typeData.getType())) {
            propertyMetadata.setError(String.format("Property '%s' in %s, has differing data types: %s and %s.", propertyName, declaringClassName, propertyMetadata.getTypeData(), typeData));
        }
        PojoBuilderHelper.cachePropertyTypeData(propertyMetadata, propertyTypeParameterMap, parentClassTypeData, genericTypeNames, genericType);
        return propertyMetadata;
    }

    private static boolean isAssignableClass(Class<?> propertyTypeClass, Class<?> typeDataClass) {
        Assertions.notNull("propertyTypeClass", propertyTypeClass);
        Assertions.notNull("typeDataClass", typeDataClass);
        return propertyTypeClass.isAssignableFrom(typeDataClass) || typeDataClass.isAssignableFrom(propertyTypeClass);
    }

    private static <T, S> PropertyMetadata<T> getOrCreateFieldPropertyMetadata(String propertyName, String declaringClassName, Map<String, PropertyMetadata<?>> propertyNameMap, TypeData<T> typeData, Map<String, TypeParameterMap> propertyTypeParameterMap, TypeData<S> parentClassTypeData, List<String> genericTypeNames, Type genericType) {
        PropertyMetadata<T> propertyMetadata = PojoBuilderHelper.getOrCreatePropertyMetadata(propertyName, declaringClassName, propertyNameMap, typeData);
        if (!propertyMetadata.getTypeData().getType().isAssignableFrom(typeData.getType())) {
            return null;
        }
        PojoBuilderHelper.cachePropertyTypeData(propertyMetadata, propertyTypeParameterMap, parentClassTypeData, genericTypeNames, genericType);
        return propertyMetadata;
    }

    private static <T> PropertyMetadata<T> getOrCreatePropertyMetadata(String propertyName, String declaringClassName, Map<String, PropertyMetadata<?>> propertyNameMap, TypeData<T> typeData) {
        PropertyMetadata<Object> propertyMetadata = propertyNameMap.get(propertyName);
        if (propertyMetadata == null) {
            propertyMetadata = new PropertyMetadata<T>(propertyName, declaringClassName, typeData);
            propertyNameMap.put(propertyName, propertyMetadata);
        }
        return propertyMetadata;
    }

    private static <T, S> void cachePropertyTypeData(PropertyMetadata<T> propertyMetadata, Map<String, TypeParameterMap> propertyTypeParameterMap, TypeData<S> parentClassTypeData, List<String> genericTypeNames, Type genericType) {
        TypeParameterMap typeParameterMap = PojoBuilderHelper.getTypeParameterMap(genericTypeNames, genericType);
        propertyTypeParameterMap.put(propertyMetadata.getName(), typeParameterMap);
        propertyMetadata.typeParameterInfo(typeParameterMap, parentClassTypeData);
    }

    private static Type getGenericType(Method method) {
        return PropertyReflectionUtils.isGetter(method) ? method.getGenericReturnType() : method.getGenericParameterTypes()[0];
    }

    static <T> PropertyModelBuilder<T> createPropertyModelBuilder(PropertyMetadata<T> propertyMetadata) {
        PropertyModelBuilder propertyModelBuilder = PropertyModel.builder().propertyName(propertyMetadata.getName()).readName(propertyMetadata.getName()).writeName(propertyMetadata.getName()).typeData(propertyMetadata.getTypeData()).readAnnotations(propertyMetadata.getReadAnnotations()).writeAnnotations(propertyMetadata.getWriteAnnotations()).propertySerialization(new PropertyModelSerializationImpl()).propertyAccessor(new PropertyAccessorImpl<T>(propertyMetadata)).setError(propertyMetadata.getError());
        if (propertyMetadata.getTypeParameters() != null) {
            propertyModelBuilder.typeData(PojoSpecializationHelper.specializeTypeData(propertyModelBuilder.getTypeData(), propertyMetadata.getTypeParameters(), propertyMetadata.getTypeParameterMap()));
        }
        return propertyModelBuilder;
    }

    private static TypeParameterMap getTypeParameterMap(List<String> genericTypeNames, Type propertyType) {
        int classParamIndex = genericTypeNames.indexOf(propertyType.toString());
        TypeParameterMap.Builder builder = TypeParameterMap.builder();
        if (classParamIndex != -1) {
            builder.addIndex(classParamIndex);
        } else if (propertyType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)propertyType;
            for (int i = 0; i < pt.getActualTypeArguments().length; ++i) {
                classParamIndex = genericTypeNames.indexOf(pt.getActualTypeArguments()[i].toString());
                if (classParamIndex != -1) {
                    builder.addIndex(i, classParamIndex);
                    continue;
                }
                builder.addIndex(i, PojoBuilderHelper.getTypeParameterMap(genericTypeNames, pt.getActualTypeArguments()[i]));
            }
        }
        return builder.build();
    }

    static <V> V stateNotNull(String property, V value) {
        if (value == null) {
            throw new IllegalStateException(String.format("%s cannot be null", property));
        }
        return value;
    }

    private PojoBuilderHelper() {
    }
}

