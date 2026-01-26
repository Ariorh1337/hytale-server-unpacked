/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;

final class ConventionDefaultsImpl
implements Convention {
    ConventionDefaultsImpl() {
    }

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        if (classModelBuilder.getDiscriminatorKey() == null) {
            classModelBuilder.discriminatorKey("_t");
        }
        if (classModelBuilder.getDiscriminator() == null && classModelBuilder.getType() != null) {
            classModelBuilder.discriminator(classModelBuilder.getType().getName());
        }
        for (PropertyModelBuilder<?> propertyModel : classModelBuilder.getPropertyModelBuilders()) {
            String propertyName;
            if (classModelBuilder.getIdPropertyName() != null || !(propertyName = propertyModel.getName()).equals("_id") && !propertyName.equals("id")) continue;
            classModelBuilder.idPropertyName(propertyName);
        }
    }
}

