/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.file.Path;

public class DbPath {
    final Path path;
    final long targetSize;

    public DbPath(Path path, long l) {
        this.path = path;
        this.targetSize = l;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        DbPath dbPath = (DbPath)object;
        if (this.targetSize != dbPath.targetSize) {
            return false;
        }
        return this.path != null ? this.path.equals(dbPath.path) : dbPath.path == null;
    }

    public int hashCode() {
        int n = this.path != null ? this.path.hashCode() : 0;
        n = 31 * n + (int)(this.targetSize ^ this.targetSize >>> 32);
        return n;
    }
}

