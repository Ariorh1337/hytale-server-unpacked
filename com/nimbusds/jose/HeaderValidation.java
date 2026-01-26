/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.IllegalHeaderException;
import com.nimbusds.jose.UnprotectedHeader;

class HeaderValidation {
    static void ensureDisjoint(Header header, UnprotectedHeader unprotectedHeader) throws IllegalHeaderException {
        if (header == null || unprotectedHeader == null) {
            return;
        }
        for (String unprotectedParamName : unprotectedHeader.getIncludedParams()) {
            if (!header.getIncludedParams().contains(unprotectedParamName)) continue;
            throw new IllegalHeaderException("The parameters in the protected header and the unprotected header must be disjoint");
        }
    }

    private HeaderValidation() {
    }
}

