/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.arguments.system;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractOptionalArg<Arg extends Argument<Arg, DataType>, DataType>
extends Argument<Arg, DataType> {
    @Nonnull
    private final Set<String> aliases = new HashSet<String>();
    @Nullable
    private String permission;
    private Set<AbstractOptionalArg<?, ?>> requiredIf;
    private Set<AbstractOptionalArg<?, ?>> requiredIfAbsent;
    private Set<AbstractOptionalArg<?, ?>> availableOnlyIfAll;
    private Set<AbstractOptionalArg<?, ?>> availableOnlyIfAllAbsent;

    AbstractOptionalArg(@Nonnull AbstractCommand commandRegisteredTo, @Nonnull String name, @Nonnull String description, @Nonnull ArgumentType<DataType> argumentType) {
        super(commandRegisteredTo, name, description, argumentType);
    }

    public final Arg addAliases(String ... newAliases) {
        if (this.getCommandRegisteredTo().hasBeenRegistered()) {
            throw new IllegalStateException("Cannot change aliases for an argument after a command has already been registered");
        }
        for (String newAlias : newAliases) {
            this.aliases.add(newAlias.toLowerCase());
        }
        return this.getThis();
    }

    public Arg requiredIf(@Nonnull AbstractOptionalArg<?, ?> dependent, AbstractOptionalArg<?, ?> ... otherDependents) {
        if (this.requiredIf == null) {
            this.requiredIf = new HashSet();
        }
        if (!this.addDependencyArg(this.requiredIf, this.requiredIfAbsent, dependent, otherDependents)) {
            throw new IllegalStateException("Cannot have one argument in both requiredIf and requiredIfAbsent. Argument: " + dependent.getName());
        }
        return this.getThis();
    }

    public Arg requiredIf(@Nonnull AbstractOptionalArg<?, ?> dependent) {
        return this.requiredIf(dependent, null);
    }

    public Arg requiredIfAbsent(@Nonnull AbstractOptionalArg<?, ?> dependent, AbstractOptionalArg<?, ?> ... otherDependents) {
        if (this.requiredIfAbsent == null) {
            this.requiredIfAbsent = new HashSet();
        }
        if (!this.addDependencyArg(this.requiredIfAbsent, this.requiredIf, dependent, otherDependents)) {
            throw new IllegalStateException("Cannot have one argument in both requiredIf and requiredIfAbsent. Argument: " + dependent.getName());
        }
        return this.getThis();
    }

    public Arg requiredIfAbsent(@Nonnull AbstractOptionalArg<?, ?> dependent) {
        return this.requiredIfAbsent(dependent, null);
    }

    public Arg availableOnlyIfAll(@Nonnull AbstractOptionalArg<?, ?> dependent, AbstractOptionalArg<?, ?> ... otherDependents) {
        if (this.availableOnlyIfAll == null) {
            this.availableOnlyIfAll = new HashSet();
        }
        if (!this.addDependencyArg(this.availableOnlyIfAll, this.availableOnlyIfAllAbsent, dependent, otherDependents)) {
            throw new IllegalStateException("Cannot have one argument in both availableIf and availableIfAbsent. Argument: " + dependent.getName());
        }
        return this.getThis();
    }

    public Arg availableOnlyIfAll(@Nonnull AbstractOptionalArg<?, ?> dependent) {
        return this.availableOnlyIfAll(dependent, null);
    }

    public Arg availableOnlyIfAllAbsent(@Nonnull AbstractOptionalArg<?, ?> dependent, AbstractOptionalArg<?, ?> ... otherDependents) {
        if (this.availableOnlyIfAllAbsent == null) {
            this.availableOnlyIfAllAbsent = new HashSet();
        }
        if (!this.addDependencyArg(this.availableOnlyIfAllAbsent, this.availableOnlyIfAll, dependent, otherDependents)) {
            throw new IllegalStateException("Cannot have one argument in both availableIf and availableIfAbsent. Argument: " + dependent.getName());
        }
        return this.getThis();
    }

    public Arg availableOnlyIfAllAbsent(@Nonnull AbstractOptionalArg<?, ?> dependent) {
        return this.availableOnlyIfAllAbsent(dependent, null);
    }

    private boolean addDependencyArg(@Nonnull Set<AbstractOptionalArg<?, ?>> set, @Nullable Set<AbstractOptionalArg<?, ?>> oppositeSet, AbstractOptionalArg<?, ?> dependent, AbstractOptionalArg<?, ?> ... otherDependents) {
        if (this.getCommandRegisteredTo().hasBeenRegistered()) {
            throw new IllegalStateException("Cannot change argument dependencies after command has completed registration");
        }
        if (oppositeSet != null && oppositeSet.contains(dependent)) {
            return false;
        }
        set.add(dependent);
        if (otherDependents != null) {
            if (oppositeSet != null) {
                for (AbstractOptionalArg<?, ?> otherDependent : otherDependents) {
                    if (!oppositeSet.contains(otherDependent)) continue;
                    return false;
                }
            }
            Collections.addAll(this.requiredIfAbsent, otherDependents);
        }
        return true;
    }

    public boolean verifyArgumentDependencies(@Nonnull CommandContext context, @Nonnull ParseResult parseResult) {
        boolean provided = context.provided(this);
        if (!provided) {
            if (this.requiredIf != null) {
                for (AbstractOptionalArg<?, ?> arg : this.requiredIf) {
                    if (!arg.provided(context)) continue;
                    parseResult.fail(Message.translation("server.commands.parsing.error.optionalArgRequiredIf").param("required", this.getName()).param("requirer", arg.getName()));
                    return false;
                }
            }
            if (this.requiredIfAbsent != null) {
                for (AbstractOptionalArg<?, ?> arg : this.requiredIfAbsent) {
                    if (arg.provided(context)) continue;
                    parseResult.fail(Message.translation("server.commands.parsing.error.optionalArgRequiredIf").param("required", this.getName()).param("requirer", arg.getName()));
                    return false;
                }
            }
            return true;
        }
        if (this.availableOnlyIfAll != null) {
            for (AbstractOptionalArg<?, ?> arg : this.availableOnlyIfAll) {
                if (arg.provided(context)) continue;
                parseResult.fail(Message.translation("server.commands.parsing.error.optionalArgAvailableIf").param("available", this.getName()).param("required", this.availableOnlyIfAll.stream().map(Argument::getName).collect(Collectors.joining(", "))));
                return false;
            }
        }
        if (this.availableOnlyIfAllAbsent != null) {
            for (AbstractOptionalArg<?, ?> arg : this.availableOnlyIfAllAbsent) {
                if (!arg.provided(context)) continue;
                parseResult.fail(Message.translation("server.commands.parsing.error.optionalArgAvailableIfAbsent").param("available", this.getName()).param("required", this.availableOnlyIfAllAbsent.stream().map(Argument::getName).collect(Collectors.joining(", "))));
                return false;
            }
        }
        return true;
    }

    @Nonnull
    public Arg setPermission(@Nonnull String permission) {
        if (this.getCommandRegisteredTo().hasBeenRegistered()) {
            throw new IllegalStateException("Cannot change permissions after a command has already been registered");
        }
        this.permission = permission;
        return this.getThis();
    }

    @Nonnull
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(this.aliases);
    }

    @Nullable
    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermission(@Nonnull CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    public static interface DefaultValueArgument<DataType> {
        public DataType getDefaultValue();
    }
}

