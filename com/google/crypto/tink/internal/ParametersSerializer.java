/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.Serialization;
import java.security.GeneralSecurityException;

public abstract class ParametersSerializer<ParametersT extends Parameters, SerializationT extends Serialization> {
    private final Class<ParametersT> parametersClass;
    private final Class<SerializationT> serializationClass;

    private ParametersSerializer(Class<ParametersT> parametersClass, Class<SerializationT> serializationClass) {
        this.parametersClass = parametersClass;
        this.serializationClass = serializationClass;
    }

    public abstract SerializationT serializeParameters(ParametersT var1) throws GeneralSecurityException;

    public Class<ParametersT> getParametersClass() {
        return this.parametersClass;
    }

    public Class<SerializationT> getSerializationClass() {
        return this.serializationClass;
    }

    public static <ParametersT extends Parameters, SerializationT extends Serialization> ParametersSerializer<ParametersT, SerializationT> create(final ParametersSerializationFunction<ParametersT, SerializationT> function, Class<ParametersT> parametersClass, Class<SerializationT> serializationClass) {
        return new ParametersSerializer<ParametersT, SerializationT>(parametersClass, serializationClass){

            @Override
            public SerializationT serializeParameters(ParametersT parameters) throws GeneralSecurityException {
                return function.serializeParameters(parameters);
            }
        };
    }

    public static interface ParametersSerializationFunction<ParametersT extends Parameters, SerializationT extends Serialization> {
        public SerializationT serializeParameters(ParametersT var1) throws GeneralSecurityException;
    }
}

