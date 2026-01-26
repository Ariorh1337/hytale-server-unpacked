/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.JavaFeaturesProto;
import com.google.protobuf.Message;

public final class GeneratorNames {
    private GeneratorNames() {
    }

    public static String getFileJavaPackage(DescriptorProtos.FileDescriptorProtoOrBuilder file) {
        return GeneratorNames.getProto2ApiDefaultJavaPackage(file.getOptions(), file.getPackage());
    }

    public static String getFileJavaPackage(Descriptors.FileDescriptor file) {
        return GeneratorNames.getProto2ApiDefaultJavaPackage(file.getOptions(), file.getPackage());
    }

    static String getDefaultJavaPackage(DescriptorProtos.FileOptions fileOptions, String filePackage) {
        if (fileOptions.hasJavaPackage()) {
            return fileOptions.getJavaPackage();
        }
        return filePackage;
    }

    static String joinPackage(String a, String b) {
        if (a.isEmpty()) {
            return b;
        }
        if (b.isEmpty()) {
            return a;
        }
        return a + '.' + b;
    }

    static String getProto2ApiDefaultJavaPackage(DescriptorProtos.FileOptions fileOptions, String filePackage) {
        return GeneratorNames.getDefaultJavaPackage(fileOptions, filePackage);
    }

    public static String getFileClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file) {
        return GeneratorNames.getFileClassNameImpl(file, GeneratorNames.getResolvedFileFeatures(JavaFeaturesProto.java_, file));
    }

    public static String getFileClassName(Descriptors.FileDescriptor file) {
        return GeneratorNames.getFileClassNameImpl(file.toProto(), file.getFeatures().getExtension(JavaFeaturesProto.java_));
    }

    private static String getFileClassNameImpl(DescriptorProtos.FileDescriptorProtoOrBuilder file, JavaFeaturesProto.JavaFeatures resolvedFeatures) {
        if (file.getOptions().hasJavaOuterClassname()) {
            return file.getOptions().getJavaOuterClassname();
        }
        String className = GeneratorNames.getDefaultFileClassName(file, resolvedFeatures.getUseOldOuterClassnameDefault());
        if (resolvedFeatures.getUseOldOuterClassnameDefault() && GeneratorNames.hasConflictingClassName(file, className)) {
            return className + "OuterClass";
        }
        return className;
    }

    static <T extends Message> T getResolvedFileFeatures(GeneratedMessage.GeneratedExtension<DescriptorProtos.FeatureSet, T> ext, DescriptorProtos.FileDescriptorProtoOrBuilder file) {
        DescriptorProtos.Edition edition = file.getSyntax().equals("proto3") ? DescriptorProtos.Edition.EDITION_PROTO3 : (!file.hasEdition() ? DescriptorProtos.Edition.EDITION_PROTO2 : file.getEdition());
        DescriptorProtos.FeatureSet features = file.getOptions().getFeatures();
        if (features.getUnknownFields().hasField(ext.getNumber())) {
            ExtensionRegistry registry = ExtensionRegistry.newInstance();
            registry.add(ext);
            try {
                features = ((DescriptorProtos.FeatureSet.Builder)DescriptorProtos.FeatureSet.newBuilder().mergeFrom(features.getUnknownFields().toByteString(), (ExtensionRegistryLite)registry)).build();
            }
            catch (InvalidProtocolBufferException e) {
                throw new IllegalArgumentException("Failed to parse features", e);
            }
        }
        return (T)((Message)Descriptors.getEditionDefaults(edition).getExtension(ext)).toBuilder().mergeFrom((Message)features.getExtension(ext)).build();
    }

    static String getDefaultFileClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file, boolean useOldOuterClassnameDefault) {
        String name = file.getName();
        name = name.substring(name.lastIndexOf(47) + 1);
        name = GeneratorNames.underscoresToCamelCase(GeneratorNames.stripProto(name));
        return useOldOuterClassnameDefault ? name : name + "Proto";
    }

    private static String stripProto(String filename) {
        if (filename.endsWith(".protodevel")) {
            return filename.substring(0, filename.length() - ".protodevel".length());
        }
        if (filename.endsWith(".proto")) {
            return filename.substring(0, filename.length() - ".proto".length());
        }
        return filename;
    }

    private static boolean hasConflictingClassName(DescriptorProtos.FileDescriptorProtoOrBuilder file, String name) {
        for (DescriptorProtos.EnumDescriptorProto enumDesc : file.getEnumTypeList()) {
            if (!name.equals(enumDesc.getName())) continue;
            return true;
        }
        for (DescriptorProtos.ServiceDescriptorProto serviceDesc : file.getServiceList()) {
            if (!name.equals(serviceDesc.getName())) continue;
            return true;
        }
        for (DescriptorProtos.DescriptorProto messageDesc : file.getMessageTypeList()) {
            if (!GeneratorNames.hasConflictingClassName(messageDesc, name)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasConflictingClassName(DescriptorProtos.DescriptorProto messageDesc, String name) {
        if (name.equals(messageDesc.getName())) {
            return true;
        }
        for (DescriptorProtos.EnumDescriptorProto enumDesc : messageDesc.getEnumTypeList()) {
            if (!name.equals(enumDesc.getName())) continue;
            return true;
        }
        for (DescriptorProtos.DescriptorProto nestedMessageDesc : messageDesc.getNestedTypeList()) {
            if (!GeneratorNames.hasConflictingClassName(nestedMessageDesc, name)) continue;
            return true;
        }
        return false;
    }

    static String underscoresToCamelCase(String input, boolean capitalizeNextLetter) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if ('a' <= ch && ch <= 'z') {
                if (capitalizeNextLetter) {
                    result.append((char)(ch + -32));
                } else {
                    result.append(ch);
                }
                capitalizeNextLetter = false;
                continue;
            }
            if ('A' <= ch && ch <= 'Z') {
                if (i == 0 && !capitalizeNextLetter) {
                    result.append((char)(ch + 32));
                } else {
                    result.append(ch);
                }
                capitalizeNextLetter = false;
                continue;
            }
            if ('0' <= ch && ch <= '9') {
                result.append(ch);
                capitalizeNextLetter = true;
                continue;
            }
            capitalizeNextLetter = true;
        }
        return result.toString();
    }

    static String underscoresToCamelCase(String input) {
        return GeneratorNames.underscoresToCamelCase(input, true);
    }

    public static String getBytecodeClassName(Descriptors.Descriptor message) {
        return GeneratorNames.getClassFullName(GeneratorNames.getClassNameWithoutPackage(message), message.getFile(), !GeneratorNames.getNestInFileClass(message));
    }

    public static String getBytecodeClassName(Descriptors.EnumDescriptor enm) {
        return GeneratorNames.getClassFullName(GeneratorNames.getClassNameWithoutPackage(enm), enm.getFile(), !GeneratorNames.getNestInFileClass(enm));
    }

    static String getBytecodeClassName(Descriptors.ServiceDescriptor service) {
        String suffix = "";
        boolean isOwnFile = !GeneratorNames.getNestInFileClass(service);
        return GeneratorNames.getClassFullName(GeneratorNames.getClassNameWithoutPackage(service), service.getFile(), isOwnFile) + suffix;
    }

    static String getQualifiedFromBytecodeClassName(String bytecodeClassName) {
        return bytecodeClassName.replace('$', '.');
    }

    public static String getQualifiedClassName(Descriptors.Descriptor message) {
        return GeneratorNames.getQualifiedFromBytecodeClassName(GeneratorNames.getBytecodeClassName(message));
    }

    public static String getQualifiedClassName(Descriptors.EnumDescriptor enm) {
        return GeneratorNames.getQualifiedFromBytecodeClassName(GeneratorNames.getBytecodeClassName(enm));
    }

    public static String getQualifiedClassName(Descriptors.ServiceDescriptor service) {
        return GeneratorNames.getQualifiedFromBytecodeClassName(GeneratorNames.getBytecodeClassName(service));
    }

    private static String getClassFullName(String nameWithoutPackage, Descriptors.FileDescriptor file, boolean isOwnFile) {
        StringBuilder result = new StringBuilder();
        if (isOwnFile) {
            result.append(GeneratorNames.getFileJavaPackage(file.toProto()));
            if (result.length() > 0) {
                result.append(".");
            }
        } else {
            result.append(GeneratorNames.joinPackage(GeneratorNames.getFileJavaPackage(file.toProto()), GeneratorNames.getFileClassName(file)));
            if (result.length() > 0) {
                result.append("$");
            }
        }
        result.append(nameWithoutPackage.replace('.', '$'));
        return result.toString();
    }

    private static boolean getNestInFileClass(Descriptors.FileDescriptor file, JavaFeaturesProto.JavaFeatures resolvedFeatures) {
        switch (resolvedFeatures.getNestInFileClass()) {
            case YES: {
                return true;
            }
            case NO: {
                return false;
            }
            case LEGACY: {
                return !file.getOptions().getJavaMultipleFiles();
            }
        }
        throw new IllegalArgumentException("Java features are not resolved");
    }

    public static boolean getNestInFileClass(Descriptors.Descriptor descriptor) {
        return GeneratorNames.getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
    }

    public static boolean getNestInFileClass(Descriptors.EnumDescriptor descriptor) {
        return GeneratorNames.getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
    }

    private static boolean getNestInFileClass(Descriptors.ServiceDescriptor descriptor) {
        return GeneratorNames.getNestInFileClass(descriptor.getFile(), descriptor.getFeatures().getExtension(JavaFeaturesProto.java_));
    }

    static String stripPackageName(String fullName, Descriptors.FileDescriptor file) {
        if (file.getPackage().isEmpty()) {
            return fullName;
        }
        return fullName.substring(file.getPackage().length() + 1);
    }

    static String getClassNameWithoutPackage(Descriptors.Descriptor message) {
        return GeneratorNames.stripPackageName(message.getFullName(), message.getFile());
    }

    static String getClassNameWithoutPackage(Descriptors.EnumDescriptor enm) {
        Descriptors.Descriptor containingType = enm.getContainingType();
        if (containingType == null) {
            return enm.getName();
        }
        return GeneratorNames.joinPackage(GeneratorNames.getClassNameWithoutPackage(containingType), enm.getName());
    }

    static String getClassNameWithoutPackage(Descriptors.ServiceDescriptor service) {
        return GeneratorNames.stripPackageName(service.getFullName(), service.getFile());
    }
}

