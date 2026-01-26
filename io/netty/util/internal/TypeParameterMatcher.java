/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ReflectionUtil;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeParameterMatcher {
    private static final TypeParameterMatcher NOOP = new TypeParameterMatcher(){

        @Override
        public boolean match(Object msg) {
            return true;
        }
    };

    public static TypeParameterMatcher get(Class<?> parameterType) {
        Map<Class<?>, TypeParameterMatcher> getCache = InternalThreadLocalMap.get().typeParameterMatcherGetCache();
        TypeParameterMatcher matcher = getCache.get(parameterType);
        if (matcher == null) {
            matcher = parameterType == Object.class ? NOOP : new ReflectiveMatcher(parameterType);
            getCache.put(parameterType, matcher);
        }
        return matcher;
    }

    public static TypeParameterMatcher find(Object object, Class<?> parametrizedSuperclass, String typeParamName) {
        TypeParameterMatcher matcher;
        Class<?> thisClass;
        Map<Class<?>, Map<String, TypeParameterMatcher>> findCache = InternalThreadLocalMap.get().typeParameterMatcherFindCache();
        Map<String, TypeParameterMatcher> map = findCache.get(thisClass = object.getClass());
        if (map == null) {
            map = new HashMap<String, TypeParameterMatcher>();
            findCache.put(thisClass, map);
        }
        if ((matcher = map.get(typeParamName)) == null) {
            matcher = TypeParameterMatcher.get(ReflectionUtil.resolveTypeParameter(object, parametrizedSuperclass, typeParamName));
            map.put(typeParamName, matcher);
        }
        return matcher;
    }

    public abstract boolean match(Object var1);

    TypeParameterMatcher() {
    }

    private static final class ReflectiveMatcher
    extends TypeParameterMatcher {
        private final Class<?> type;

        ReflectiveMatcher(Class<?> type) {
            this.type = type;
        }

        @Override
        public boolean match(Object msg) {
            return this.type.isInstance(msg);
        }
    }
}

