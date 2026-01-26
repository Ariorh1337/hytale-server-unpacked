/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.ConventionAnnotationImpl;
import org.bson.codecs.pojo.ConventionDefaultsImpl;
import org.bson.codecs.pojo.ConventionObjectIdGeneratorsImpl;
import org.bson.codecs.pojo.ConventionSetPrivateFieldImpl;
import org.bson.codecs.pojo.ConventionUseGettersAsSettersImpl;

public final class Conventions {
    public static final Convention CLASS_AND_PROPERTY_CONVENTION = new ConventionDefaultsImpl();
    public static final Convention ANNOTATION_CONVENTION = new ConventionAnnotationImpl();
    public static final Convention SET_PRIVATE_FIELDS_CONVENTION = new ConventionSetPrivateFieldImpl();
    public static final Convention USE_GETTERS_FOR_SETTERS = new ConventionUseGettersAsSettersImpl();
    public static final Convention OBJECT_ID_GENERATORS = new ConventionObjectIdGeneratorsImpl();
    public static final List<Convention> DEFAULT_CONVENTIONS = Collections.unmodifiableList(Arrays.asList(CLASS_AND_PROPERTY_CONVENTION, ANNOTATION_CONVENTION, OBJECT_ID_GENERATORS));
    public static final List<Convention> NO_CONVENTIONS = Collections.emptyList();

    private Conventions() {
    }
}

