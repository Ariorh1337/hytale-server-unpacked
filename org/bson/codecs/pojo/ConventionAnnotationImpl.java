/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonType;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.CreatorExecutable;
import org.bson.codecs.pojo.InstanceCreatorFactoryImpl;
import org.bson.codecs.pojo.PojoBuilderHelper;
import org.bson.codecs.pojo.PropertyMetadata;
import org.bson.codecs.pojo.PropertyModelBuilder;
import org.bson.codecs.pojo.TypeData;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;

final class ConventionAnnotationImpl
implements Convention {
    ConventionAnnotationImpl() {
    }

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (Annotation annotation : classModelBuilder.getAnnotations()) {
            this.processClassAnnotation(classModelBuilder, annotation);
        }
        for (PropertyModelBuilder propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            this.processPropertyAnnotations(classModelBuilder, propertyModelBuilder);
        }
        this.processCreatorAnnotation(classModelBuilder);
        this.cleanPropertyBuilders(classModelBuilder);
    }

    private void processClassAnnotation(ClassModelBuilder<?> classModelBuilder, Annotation annotation) {
        if (annotation instanceof BsonDiscriminator) {
            String name;
            BsonDiscriminator discriminator = (BsonDiscriminator)annotation;
            String key = discriminator.key();
            if (!key.equals("")) {
                classModelBuilder.discriminatorKey(key);
            }
            if (!(name = discriminator.value()).equals("")) {
                classModelBuilder.discriminator(name);
            }
            classModelBuilder.enableDiscriminator(true);
        }
    }

    private void processPropertyAnnotations(ClassModelBuilder<?> classModelBuilder, PropertyModelBuilder<?> propertyModelBuilder) {
        BsonProperty bsonProperty;
        for (Annotation annotation : propertyModelBuilder.getReadAnnotations()) {
            if (annotation instanceof BsonProperty) {
                bsonProperty = (BsonProperty)annotation;
                if (!"".equals(bsonProperty.value())) {
                    propertyModelBuilder.readName(bsonProperty.value());
                }
                propertyModelBuilder.discriminatorEnabled(bsonProperty.useDiscriminator());
                if (!propertyModelBuilder.getName().equals(classModelBuilder.getIdPropertyName())) continue;
                classModelBuilder.idPropertyName(null);
                continue;
            }
            if (annotation instanceof BsonId) {
                classModelBuilder.idPropertyName(propertyModelBuilder.getName());
                continue;
            }
            if (annotation instanceof BsonIgnore) {
                propertyModelBuilder.readName(null);
                continue;
            }
            if (!(annotation instanceof BsonRepresentation)) continue;
            BsonRepresentation bsonRepresentation = (BsonRepresentation)annotation;
            BsonType bsonRep = bsonRepresentation.value();
            propertyModelBuilder.bsonRepresentation(bsonRep);
        }
        for (Annotation annotation : propertyModelBuilder.getWriteAnnotations()) {
            if (annotation instanceof BsonProperty) {
                bsonProperty = (BsonProperty)annotation;
                if ("".equals(bsonProperty.value())) continue;
                propertyModelBuilder.writeName(bsonProperty.value());
                continue;
            }
            if (!(annotation instanceof BsonIgnore)) continue;
            propertyModelBuilder.writeName(null);
        }
    }

    private <T> void processCreatorAnnotation(ClassModelBuilder<T> classModelBuilder) {
        Class<T> clazz = classModelBuilder.getType();
        CreatorExecutable<T> creatorExecutable = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!Modifier.isPublic(constructor.getModifiers()) || constructor.isSynthetic()) continue;
            for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                if (!annotation.annotationType().equals(BsonCreator.class)) continue;
                if (creatorExecutable != null) {
                    throw new CodecConfigurationException("Found multiple constructors annotated with @BsonCreator");
                }
                creatorExecutable = new CreatorExecutable<T>(clazz, constructor);
            }
        }
        boolean foundStaticBsonCreatorMethod = false;
        for (Class<T> bsonCreatorClass = clazz; bsonCreatorClass != null && !foundStaticBsonCreatorMethod; bsonCreatorClass = bsonCreatorClass.getSuperclass()) {
            for (Method method : bsonCreatorClass.getDeclaredMethods()) {
                if (!Modifier.isStatic(method.getModifiers()) || method.isSynthetic() || method.isBridge()) continue;
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (!annotation.annotationType().equals(BsonCreator.class)) continue;
                    if (creatorExecutable != null) {
                        throw new CodecConfigurationException("Found multiple constructors / methods annotated with @BsonCreator");
                    }
                    if (!bsonCreatorClass.isAssignableFrom(method.getReturnType())) {
                        throw new CodecConfigurationException(String.format("Invalid method annotated with @BsonCreator. Returns '%s', expected %s", method.getReturnType(), bsonCreatorClass));
                    }
                    creatorExecutable = new CreatorExecutable<T>(clazz, method);
                    foundStaticBsonCreatorMethod = true;
                }
            }
        }
        if (creatorExecutable != null) {
            List<BsonProperty> properties = creatorExecutable.getProperties();
            List<Class<?>> parameterTypes = creatorExecutable.getParameterTypes();
            List<Type> parameterGenericTypes = creatorExecutable.getParameterGenericTypes();
            if (properties.size() != parameterTypes.size()) {
                throw creatorExecutable.getError(clazz, "All parameters in the @BsonCreator method / constructor must be annotated with a @BsonProperty.");
            }
            for (int i = 0; i < properties.size(); ++i) {
                boolean isIdProperty = creatorExecutable.getIdPropertyIndex() != null && creatorExecutable.getIdPropertyIndex().equals(i);
                Class<?> parameterType = parameterTypes.get(i);
                Type genericType = parameterGenericTypes.get(i);
                PropertyModelBuilder<?> propertyModelBuilder = null;
                if (isIdProperty) {
                    propertyModelBuilder = classModelBuilder.getProperty(classModelBuilder.getIdPropertyName());
                } else {
                    BsonProperty bsonProperty = properties.get(i);
                    for (PropertyModelBuilder<?> builder : classModelBuilder.getPropertyModelBuilders()) {
                        if (bsonProperty.value().equals(builder.getWriteName())) {
                            propertyModelBuilder = builder;
                            break;
                        }
                        if (!bsonProperty.value().equals(builder.getReadName())) continue;
                        propertyModelBuilder = builder;
                    }
                    if (propertyModelBuilder == null) {
                        propertyModelBuilder = classModelBuilder.getProperty(bsonProperty.value());
                    }
                    if (propertyModelBuilder == null) {
                        propertyModelBuilder = this.addCreatorPropertyToClassModelBuilder(classModelBuilder, bsonProperty.value(), parameterType);
                    } else {
                        if (!bsonProperty.value().equals(propertyModelBuilder.getName())) {
                            propertyModelBuilder.writeName(bsonProperty.value());
                        }
                        ConventionAnnotationImpl.tryToExpandToGenericType(parameterType, propertyModelBuilder, genericType);
                    }
                }
                if (propertyModelBuilder.getTypeData().isAssignableFrom(parameterType)) continue;
                throw creatorExecutable.getError(clazz, String.format("Invalid Property type for '%s'. Expected %s, found %s.", propertyModelBuilder.getWriteName(), propertyModelBuilder.getTypeData().getType(), parameterType));
            }
            classModelBuilder.instanceCreatorFactory(new InstanceCreatorFactoryImpl(creatorExecutable));
        }
    }

    private static <T> void tryToExpandToGenericType(Class<?> parameterType, PropertyModelBuilder<T> propertyModelBuilder, Type genericType) {
        if (parameterType.isAssignableFrom(propertyModelBuilder.getTypeData().getType())) {
            propertyModelBuilder.typeData(TypeData.newInstance(genericType, parameterType));
        }
    }

    private <T, S> PropertyModelBuilder<S> addCreatorPropertyToClassModelBuilder(ClassModelBuilder<T> classModelBuilder, String name, Class<S> clazz) {
        PropertyModelBuilder<S> propertyModelBuilder = PojoBuilderHelper.createPropertyModelBuilder(new PropertyMetadata<S>(name, classModelBuilder.getType().getSimpleName(), TypeData.builder(clazz).build())).readName(null).writeName(name);
        classModelBuilder.addProperty(propertyModelBuilder);
        return propertyModelBuilder;
    }

    private void cleanPropertyBuilders(ClassModelBuilder<?> classModelBuilder) {
        ArrayList<String> propertiesToRemove = new ArrayList<String>();
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            if (propertyModelBuilder.isReadable() || propertyModelBuilder.isWritable()) continue;
            propertiesToRemove.add(propertyModelBuilder.getName());
        }
        for (String propertyName : propertiesToRemove) {
            classModelBuilder.removeProperty(propertyName);
        }
    }
}

