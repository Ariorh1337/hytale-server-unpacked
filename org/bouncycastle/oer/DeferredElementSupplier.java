/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.OERDefinition;

public class DeferredElementSupplier
implements ElementSupplier {
    private final OERDefinition.Builder src;
    private Element buildProduct;

    public DeferredElementSupplier(OERDefinition.Builder builder) {
        this.src = builder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element build() {
        DeferredElementSupplier deferredElementSupplier = this;
        synchronized (deferredElementSupplier) {
            if (this.buildProduct == null) {
                this.buildProduct = this.src.build();
            }
            return this.buildProduct;
        }
    }
}

