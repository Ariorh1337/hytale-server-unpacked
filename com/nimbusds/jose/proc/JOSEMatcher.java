/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSObject;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JOSEMatcher {
    private final Set<Class<? extends JOSEObject>> classes;
    private final Set<Algorithm> algs;
    private final Set<EncryptionMethod> encs;
    private final Set<URI> jkus;
    private final Set<String> kids;

    public JOSEMatcher(Set<Class<? extends JOSEObject>> classes, Set<Algorithm> algs, Set<EncryptionMethod> encs, Set<URI> jkus, Set<String> kids) {
        this.classes = classes;
        this.algs = algs;
        this.encs = encs;
        this.jkus = jkus;
        this.kids = kids;
    }

    public Set<Class<? extends JOSEObject>> getJOSEClasses() {
        return this.classes;
    }

    public Set<Algorithm> getAlgorithms() {
        return this.algs;
    }

    public Set<EncryptionMethod> getEncryptionMethods() {
        return this.encs;
    }

    public Set<URI> getJWKURLs() {
        return this.jkus;
    }

    public Set<String> getKeyIDs() {
        return this.kids;
    }

    public boolean matches(JOSEObject joseObject) {
        URI jku;
        if (this.classes != null) {
            boolean pass = false;
            for (Class<? extends JOSEObject> c : this.classes) {
                if (c == null || !c.isInstance(joseObject)) continue;
                pass = true;
                break;
            }
            if (!pass) {
                return false;
            }
        }
        if (this.algs != null && !this.algs.contains(joseObject.getHeader().getAlgorithm())) {
            return false;
        }
        if (this.encs != null) {
            if (!(joseObject instanceof JWEObject)) {
                return false;
            }
            JWEObject jweObject = (JWEObject)joseObject;
            if (!this.encs.contains(jweObject.getHeader().getEncryptionMethod())) {
                return false;
            }
        }
        if (this.jkus != null && !this.jkus.contains(jku = joseObject instanceof JWSObject ? ((JWSObject)joseObject).getHeader().getJWKURL() : (joseObject instanceof JWEObject ? ((JWEObject)joseObject).getHeader().getJWKURL() : null))) {
            return false;
        }
        if (this.kids != null) {
            String kid = joseObject instanceof JWSObject ? ((JWSObject)joseObject).getHeader().getKeyID() : (joseObject instanceof JWEObject ? ((JWEObject)joseObject).getHeader().getKeyID() : null);
            return this.kids.contains(kid);
        }
        return true;
    }

    public static class Builder {
        private Set<Class<? extends JOSEObject>> classes;
        private Set<Algorithm> algs;
        private Set<EncryptionMethod> encs;
        private Set<URI> jkus;
        private Set<String> kids;

        public Builder joseClass(Class<? extends JOSEObject> clazz) {
            this.classes = clazz == null ? null : new HashSet<Class<? extends JOSEObject>>(Collections.singletonList(clazz));
            return this;
        }

        public Builder joseClasses(Class<? extends JOSEObject> ... classes) {
            this.joseClasses(new HashSet<Class<? extends JOSEObject>>(Arrays.asList(classes)));
            return this;
        }

        public Builder joseClasses(Set<Class<? extends JOSEObject>> classes) {
            this.classes = classes;
            return this;
        }

        public Builder algorithm(Algorithm alg) {
            this.algs = alg == null ? null : new HashSet<Algorithm>(Collections.singletonList(alg));
            return this;
        }

        public Builder algorithms(Algorithm ... algs) {
            this.algorithms(new HashSet<Algorithm>(Arrays.asList(algs)));
            return this;
        }

        public Builder algorithms(Set<Algorithm> algs) {
            this.algs = algs;
            return this;
        }

        public Builder encryptionMethod(EncryptionMethod enc) {
            this.encs = enc == null ? null : new HashSet<EncryptionMethod>(Collections.singletonList(enc));
            return this;
        }

        public Builder encryptionMethods(EncryptionMethod ... encs) {
            this.encryptionMethods(new HashSet<EncryptionMethod>(Arrays.asList(encs)));
            return this;
        }

        public Builder encryptionMethods(Set<EncryptionMethod> encs) {
            this.encs = encs;
            return this;
        }

        public Builder jwkURL(URI jku) {
            this.jkus = jku == null ? null : new HashSet<URI>(Collections.singletonList(jku));
            return this;
        }

        public Builder jwkURLs(URI ... jkus) {
            this.jwkURLs(new HashSet<URI>(Arrays.asList(jkus)));
            return this;
        }

        public Builder jwkURLs(Set<URI> jkus) {
            this.jkus = jkus;
            return this;
        }

        public Builder keyID(String kid) {
            this.kids = kid == null ? null : new HashSet<String>(Collections.singletonList(kid));
            return this;
        }

        public Builder keyIDs(String ... ids) {
            this.keyIDs(new HashSet<String>(Arrays.asList(ids)));
            return this;
        }

        public Builder keyIDs(Set<String> kids) {
            this.kids = kids;
            return this;
        }

        public JOSEMatcher build() {
            return new JOSEMatcher(this.classes, this.algs, this.encs, this.jkus, this.kids);
        }
    }
}

