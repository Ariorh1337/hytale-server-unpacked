/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.JsonObjectReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class JsonObjectDeserializer {
    private final ArrayList<Token> tokens = new ArrayList();

    @Nullable
    public Object deserialize(@NotNull JsonObjectReader reader) throws IOException {
        this.parse(reader);
        Token root = this.getCurrentToken();
        if (root != null) {
            return root.getValue();
        }
        return null;
    }

    private void parse(@NotNull JsonObjectReader reader) throws IOException {
        boolean done = false;
        switch (reader.peek()) {
            case BEGIN_ARRAY: {
                reader.beginArray();
                this.pushCurrentToken(new TokenArray());
                break;
            }
            case END_ARRAY: {
                reader.endArray();
                done = this.handleArrayOrMapEnd();
                break;
            }
            case BEGIN_OBJECT: {
                reader.beginObject();
                this.pushCurrentToken(new TokenMap());
                break;
            }
            case END_OBJECT: {
                reader.endObject();
                done = this.handleArrayOrMapEnd();
                break;
            }
            case NAME: {
                this.pushCurrentToken(new TokenName(reader.nextName()));
                break;
            }
            case STRING: {
                done = this.handlePrimitive(() -> reader.nextString());
                break;
            }
            case NUMBER: {
                done = this.handlePrimitive(() -> this.nextNumber(reader));
                break;
            }
            case BOOLEAN: {
                done = this.handlePrimitive(() -> reader.nextBoolean());
                break;
            }
            case NULL: {
                reader.nextNull();
                done = this.handlePrimitive(() -> null);
                break;
            }
            case END_DOCUMENT: {
                done = true;
            }
        }
        if (!done) {
            this.parse(reader);
        }
    }

    private boolean handleArrayOrMapEnd() {
        if (this.hasOneToken()) {
            return true;
        }
        Token arrayOrMapToken = this.getCurrentToken();
        this.popCurrentToken();
        if (this.getCurrentToken() instanceof TokenName) {
            TokenName tokenName = (TokenName)this.getCurrentToken();
            this.popCurrentToken();
            TokenMap tokenMap = (TokenMap)this.getCurrentToken();
            if (tokenName != null && arrayOrMapToken != null && tokenMap != null) {
                tokenMap.value.put(tokenName.value, arrayOrMapToken.getValue());
            }
        } else if (this.getCurrentToken() instanceof TokenArray) {
            TokenArray tokenArray = (TokenArray)this.getCurrentToken();
            if (arrayOrMapToken != null && tokenArray != null) {
                tokenArray.value.add(arrayOrMapToken.getValue());
            }
        }
        return false;
    }

    private boolean handlePrimitive(NextValue callback) throws IOException {
        Object primitive = callback.nextValue();
        if (this.getCurrentToken() == null && primitive != null) {
            this.pushCurrentToken(new TokenPrimitive(primitive));
            return true;
        }
        if (this.getCurrentToken() instanceof TokenName) {
            TokenName tokenNameNumber = (TokenName)this.getCurrentToken();
            this.popCurrentToken();
            TokenMap tokenMapNumber = (TokenMap)this.getCurrentToken();
            tokenMapNumber.value.put(tokenNameNumber.value, primitive);
        } else if (this.getCurrentToken() instanceof TokenArray) {
            TokenArray tokenArrayNumber = (TokenArray)this.getCurrentToken();
            tokenArrayNumber.value.add(primitive);
        }
        return false;
    }

    private Object nextNumber(JsonObjectReader reader) throws IOException {
        try {
            return reader.nextInt();
        }
        catch (Exception exception) {
            try {
                return reader.nextDouble();
            }
            catch (Exception exception2) {
                return reader.nextLong();
            }
        }
    }

    @Nullable
    private Token getCurrentToken() {
        if (this.tokens.isEmpty()) {
            return null;
        }
        return this.tokens.get(this.tokens.size() - 1);
    }

    private void pushCurrentToken(Token token) {
        this.tokens.add(token);
    }

    private void popCurrentToken() {
        if (this.tokens.isEmpty()) {
            return;
        }
        this.tokens.remove(this.tokens.size() - 1);
    }

    private boolean hasOneToken() {
        return this.tokens.size() == 1;
    }

    private static interface Token {
        @NotNull
        public Object getValue();
    }

    private static final class TokenArray
    implements Token {
        final ArrayList<Object> value = new ArrayList();

        private TokenArray() {
        }

        @Override
        @NotNull
        public Object getValue() {
            return this.value;
        }
    }

    private static final class TokenMap
    implements Token {
        final HashMap<String, Object> value = new HashMap();

        private TokenMap() {
        }

        @Override
        @NotNull
        public Object getValue() {
            return this.value;
        }
    }

    private static final class TokenName
    implements Token {
        final String value;

        TokenName(@NotNull String value) {
            this.value = value;
        }

        @Override
        @NotNull
        public Object getValue() {
            return this.value;
        }
    }

    private static interface NextValue {
        @Nullable
        public Object nextValue() throws IOException;
    }

    private static final class TokenPrimitive
    implements Token {
        final Object value;

        TokenPrimitive(@NotNull Object value) {
            this.value = value;
        }

        @Override
        @NotNull
        public Object getValue() {
            return this.value;
        }
    }
}

