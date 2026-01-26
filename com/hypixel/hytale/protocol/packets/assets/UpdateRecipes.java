/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.protocol.packets.assets;

import com.hypixel.hytale.protocol.CraftingRecipe;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.UpdateType;
import com.hypixel.hytale.protocol.io.PacketIO;
import com.hypixel.hytale.protocol.io.ProtocolException;
import com.hypixel.hytale.protocol.io.ValidationResult;
import com.hypixel.hytale.protocol.io.VarInt;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateRecipes
implements Packet {
    public static final int PACKET_ID = 60;
    public static final boolean IS_COMPRESSED = true;
    public static final int NULLABLE_BIT_FIELD_SIZE = 1;
    public static final int FIXED_BLOCK_SIZE = 2;
    public static final int VARIABLE_FIELD_COUNT = 2;
    public static final int VARIABLE_BLOCK_START = 10;
    public static final int MAX_SIZE = 0x64000000;
    @Nonnull
    public UpdateType type = UpdateType.Init;
    @Nullable
    public Map<String, CraftingRecipe> recipes;
    @Nullable
    public String[] removedRecipes;

    @Override
    public int getId() {
        return 60;
    }

    public UpdateRecipes() {
    }

    public UpdateRecipes(@Nonnull UpdateType type, @Nullable Map<String, CraftingRecipe> recipes, @Nullable String[] removedRecipes) {
        this.type = type;
        this.recipes = recipes;
        this.removedRecipes = removedRecipes;
    }

    public UpdateRecipes(@Nonnull UpdateRecipes other) {
        this.type = other.type;
        this.recipes = other.recipes;
        this.removedRecipes = other.removedRecipes;
    }

    @Nonnull
    public static UpdateRecipes deserialize(@Nonnull ByteBuf buf, int offset) {
        int i;
        int varIntLen;
        UpdateRecipes obj = new UpdateRecipes();
        byte nullBits = buf.getByte(offset);
        obj.type = UpdateType.fromValue(buf.getByte(offset + 1));
        if ((nullBits & 1) != 0) {
            int varPos0 = offset + 10 + buf.getIntLE(offset + 2);
            int recipesCount = VarInt.peek(buf, varPos0);
            if (recipesCount < 0) {
                throw ProtocolException.negativeLength("Recipes", recipesCount);
            }
            if (recipesCount > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Recipes", recipesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos0);
            obj.recipes = new HashMap<String, CraftingRecipe>(recipesCount);
            int dictPos = varPos0 + varIntLen;
            for (i = 0; i < recipesCount; ++i) {
                int keyLen = VarInt.peek(buf, dictPos);
                if (keyLen < 0) {
                    throw ProtocolException.negativeLength("key", keyLen);
                }
                if (keyLen > 4096000) {
                    throw ProtocolException.stringTooLong("key", keyLen, 4096000);
                }
                int keyVarLen = VarInt.length(buf, dictPos);
                String key = PacketIO.readVarString(buf, dictPos);
                CraftingRecipe val = CraftingRecipe.deserialize(buf, dictPos += keyVarLen + keyLen);
                dictPos += CraftingRecipe.computeBytesConsumed(buf, dictPos);
                if (obj.recipes.put(key, val) == null) continue;
                throw ProtocolException.duplicateKey("recipes", key);
            }
        }
        if ((nullBits & 2) != 0) {
            int varPos1 = offset + 10 + buf.getIntLE(offset + 6);
            int removedRecipesCount = VarInt.peek(buf, varPos1);
            if (removedRecipesCount < 0) {
                throw ProtocolException.negativeLength("RemovedRecipes", removedRecipesCount);
            }
            if (removedRecipesCount > 4096000) {
                throw ProtocolException.arrayTooLong("RemovedRecipes", removedRecipesCount, 4096000);
            }
            varIntLen = VarInt.length(buf, varPos1);
            if ((long)(varPos1 + varIntLen) + (long)removedRecipesCount * 1L > (long)buf.readableBytes()) {
                throw ProtocolException.bufferTooSmall("RemovedRecipes", varPos1 + varIntLen + removedRecipesCount * 1, buf.readableBytes());
            }
            obj.removedRecipes = new String[removedRecipesCount];
            int elemPos = varPos1 + varIntLen;
            for (i = 0; i < removedRecipesCount; ++i) {
                int strLen = VarInt.peek(buf, elemPos);
                if (strLen < 0) {
                    throw ProtocolException.negativeLength("removedRecipes[" + i + "]", strLen);
                }
                if (strLen > 4096000) {
                    throw ProtocolException.stringTooLong("removedRecipes[" + i + "]", strLen, 4096000);
                }
                int strVarLen = VarInt.length(buf, elemPos);
                obj.removedRecipes[i] = PacketIO.readVarString(buf, elemPos);
                elemPos += strVarLen + strLen;
            }
        }
        return obj;
    }

    public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
        int sl;
        int i;
        byte nullBits = buf.getByte(offset);
        int maxEnd = 10;
        if ((nullBits & 1) != 0) {
            int fieldOffset0 = buf.getIntLE(offset + 2);
            int pos0 = offset + 10 + fieldOffset0;
            int dictLen = VarInt.peek(buf, pos0);
            pos0 += VarInt.length(buf, pos0);
            for (i = 0; i < dictLen; ++i) {
                sl = VarInt.peek(buf, pos0);
                pos0 += VarInt.length(buf, pos0) + sl;
                pos0 += CraftingRecipe.computeBytesConsumed(buf, pos0);
            }
            if (pos0 - offset > maxEnd) {
                maxEnd = pos0 - offset;
            }
        }
        if ((nullBits & 2) != 0) {
            int fieldOffset1 = buf.getIntLE(offset + 6);
            int pos1 = offset + 10 + fieldOffset1;
            int arrLen = VarInt.peek(buf, pos1);
            pos1 += VarInt.length(buf, pos1);
            for (i = 0; i < arrLen; ++i) {
                sl = VarInt.peek(buf, pos1);
                pos1 += VarInt.length(buf, pos1) + sl;
            }
            if (pos1 - offset > maxEnd) {
                maxEnd = pos1 - offset;
            }
        }
        return maxEnd;
    }

    @Override
    public void serialize(@Nonnull ByteBuf buf) {
        int startPos = buf.writerIndex();
        byte nullBits = 0;
        if (this.recipes != null) {
            nullBits = (byte)(nullBits | 1);
        }
        if (this.removedRecipes != null) {
            nullBits = (byte)(nullBits | 2);
        }
        buf.writeByte(nullBits);
        buf.writeByte(this.type.getValue());
        int recipesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int removedRecipesOffsetSlot = buf.writerIndex();
        buf.writeIntLE(0);
        int varBlockStart = buf.writerIndex();
        if (this.recipes != null) {
            buf.setIntLE(recipesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.recipes.size() > 4096000) {
                throw ProtocolException.dictionaryTooLarge("Recipes", this.recipes.size(), 4096000);
            }
            VarInt.write(buf, this.recipes.size());
            for (Map.Entry<String, CraftingRecipe> e : this.recipes.entrySet()) {
                PacketIO.writeVarString(buf, e.getKey(), 4096000);
                e.getValue().serialize(buf);
            }
        } else {
            buf.setIntLE(recipesOffsetSlot, -1);
        }
        if (this.removedRecipes != null) {
            buf.setIntLE(removedRecipesOffsetSlot, buf.writerIndex() - varBlockStart);
            if (this.removedRecipes.length > 4096000) {
                throw ProtocolException.arrayTooLong("RemovedRecipes", this.removedRecipes.length, 4096000);
            }
            VarInt.write(buf, this.removedRecipes.length);
            for (String item : this.removedRecipes) {
                PacketIO.writeVarString(buf, item, 4096000);
            }
        } else {
            buf.setIntLE(removedRecipesOffsetSlot, -1);
        }
    }

    @Override
    public int computeSize() {
        int size = 10;
        if (this.recipes != null) {
            int recipesSize = 0;
            for (Map.Entry entry : this.recipes.entrySet()) {
                recipesSize += PacketIO.stringSize((String)entry.getKey()) + ((CraftingRecipe)entry.getValue()).computeSize();
            }
            size += VarInt.size(this.recipes.size()) + recipesSize;
        }
        if (this.removedRecipes != null) {
            int removedRecipesSize = 0;
            for (String elem : this.removedRecipes) {
                removedRecipesSize += PacketIO.stringSize(elem);
            }
            size += VarInt.size(this.removedRecipes.length) + removedRecipesSize;
        }
        return size;
    }

    public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
        int i;
        int pos;
        if (buffer.readableBytes() - offset < 10) {
            return ValidationResult.error("Buffer too small: expected at least 10 bytes");
        }
        byte nullBits = buffer.getByte(offset);
        if ((nullBits & 1) != 0) {
            int recipesOffset = buffer.getIntLE(offset + 2);
            if (recipesOffset < 0) {
                return ValidationResult.error("Invalid offset for Recipes");
            }
            pos = offset + 10 + recipesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for Recipes");
            }
            int recipesCount = VarInt.peek(buffer, pos);
            if (recipesCount < 0) {
                return ValidationResult.error("Invalid dictionary count for Recipes");
            }
            if (recipesCount > 4096000) {
                return ValidationResult.error("Recipes exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < recipesCount; ++i) {
                int keyLen = VarInt.peek(buffer, pos);
                if (keyLen < 0) {
                    return ValidationResult.error("Invalid string length for key");
                }
                if (keyLen > 4096000) {
                    return ValidationResult.error("key exceeds max length 4096000");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += keyLen) > buffer.writerIndex()) {
                    return ValidationResult.error("Buffer overflow reading key");
                }
                pos += CraftingRecipe.computeBytesConsumed(buffer, pos);
            }
        }
        if ((nullBits & 2) != 0) {
            int removedRecipesOffset = buffer.getIntLE(offset + 6);
            if (removedRecipesOffset < 0) {
                return ValidationResult.error("Invalid offset for RemovedRecipes");
            }
            pos = offset + 10 + removedRecipesOffset;
            if (pos >= buffer.writerIndex()) {
                return ValidationResult.error("Offset out of bounds for RemovedRecipes");
            }
            int removedRecipesCount = VarInt.peek(buffer, pos);
            if (removedRecipesCount < 0) {
                return ValidationResult.error("Invalid array count for RemovedRecipes");
            }
            if (removedRecipesCount > 4096000) {
                return ValidationResult.error("RemovedRecipes exceeds max length 4096000");
            }
            pos += VarInt.length(buffer, pos);
            for (i = 0; i < removedRecipesCount; ++i) {
                int strLen = VarInt.peek(buffer, pos);
                if (strLen < 0) {
                    return ValidationResult.error("Invalid string length in RemovedRecipes");
                }
                pos += VarInt.length(buffer, pos);
                if ((pos += strLen) <= buffer.writerIndex()) continue;
                return ValidationResult.error("Buffer overflow reading string in RemovedRecipes");
            }
        }
        return ValidationResult.OK;
    }

    public UpdateRecipes clone() {
        UpdateRecipes copy = new UpdateRecipes();
        copy.type = this.type;
        if (this.recipes != null) {
            HashMap<String, CraftingRecipe> m = new HashMap<String, CraftingRecipe>();
            for (Map.Entry<String, CraftingRecipe> e : this.recipes.entrySet()) {
                m.put(e.getKey(), e.getValue().clone());
            }
            copy.recipes = m;
        }
        copy.removedRecipes = this.removedRecipes != null ? Arrays.copyOf(this.removedRecipes, this.removedRecipes.length) : null;
        return copy;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UpdateRecipes)) {
            return false;
        }
        UpdateRecipes other = (UpdateRecipes)obj;
        return Objects.equals((Object)this.type, (Object)other.type) && Objects.equals(this.recipes, other.recipes) && Arrays.equals(this.removedRecipes, other.removedRecipes);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode((Object)this.type);
        result = 31 * result + Objects.hashCode(this.recipes);
        result = 31 * result + Arrays.hashCode(this.removedRecipes);
        return result;
    }
}

