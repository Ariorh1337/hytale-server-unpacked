/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.Field;
import com.google.protobuf.FieldOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Option;
import com.google.protobuf.OptionOrBuilder;
import com.google.protobuf.SourceContext;
import com.google.protobuf.SourceContextOrBuilder;
import com.google.protobuf.Syntax;
import java.util.List;

public interface TypeOrBuilder
extends MessageOrBuilder {
    public String getName();

    public ByteString getNameBytes();

    public List<Field> getFieldsList();

    public Field getFields(int var1);

    public int getFieldsCount();

    public List<? extends FieldOrBuilder> getFieldsOrBuilderList();

    public FieldOrBuilder getFieldsOrBuilder(int var1);

    public List<String> getOneofsList();

    public int getOneofsCount();

    public String getOneofs(int var1);

    public ByteString getOneofsBytes(int var1);

    public List<Option> getOptionsList();

    public Option getOptions(int var1);

    public int getOptionsCount();

    public List<? extends OptionOrBuilder> getOptionsOrBuilderList();

    public OptionOrBuilder getOptionsOrBuilder(int var1);

    public boolean hasSourceContext();

    public SourceContext getSourceContext();

    public SourceContextOrBuilder getSourceContextOrBuilder();

    public int getSyntaxValue();

    public Syntax getSyntax();

    public String getEdition();

    public ByteString getEditionBytes();
}

