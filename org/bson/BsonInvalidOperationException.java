/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BSONException;

public class BsonInvalidOperationException
extends BSONException {
    private static final long serialVersionUID = 7684248076818601418L;

    public BsonInvalidOperationException(String message) {
        super(message);
    }

    public BsonInvalidOperationException(String message, Throwable t) {
        super(message, t);
    }
}

