/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.OpenChatWithCommand;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.MatchResult;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommandListPage
extends InteractiveCustomUIPage<CommandListPageEventData> {
    private static final Value<String> BUTTON_LABEL_STYLE = Value.ref("Pages/BasicTextButton.ui", "LabelStyle");
    private static final Value<String> BUTTON_LABEL_STYLE_SELECTED = Value.ref("Pages/BasicTextButton.ui", "SelectedLabelStyle");
    private final List<String> visibleCommands = new ObjectArrayList<String>();
    @Nonnull
    private String searchQuery = "";
    private String selectedCommand;
    private String selectedSubcommand;
    private Integer selectedVariantIndex;
    private final List<String> subcommandBreadcrumb = new ObjectArrayList<String>();
    @Nullable
    private final String initialCommand;

    public CommandListPage(@Nonnull PlayerRef playerRef) {
        this(playerRef, (String)null);
    }

    public CommandListPage(@Nonnull PlayerRef playerRef, @Nullable String initialCommand) {
        super(playerRef, CustomPageLifetime.CanDismiss, CommandListPageEventData.CODEC);
        this.initialCommand = initialCommand;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/CommandListPage.ui");
        eventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput", EventData.of("@SearchQuery", "#SearchInput.Value"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#BackButton", EventData.of("NavigateUp", "true"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SendToChatButton", EventData.of("SendToChat", "true"));
        this.buildCommandList(ref, commandBuilder, eventBuilder, store);
        String commandToSelect = this.visibleCommands.getFirst();
        if (this.initialCommand != null && this.visibleCommands.contains(this.initialCommand)) {
            commandToSelect = this.initialCommand;
        }
        this.selectCommand(ref, commandToSelect, commandBuilder, eventBuilder, store);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull CommandListPageEventData data) {
        if (data.searchQuery != null) {
            this.searchQuery = data.searchQuery.trim().toLowerCase();
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.buildCommandList(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else if (data.command != null) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.selectCommand(ref, data.command, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else if (data.navigateUp != null) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.navigateUp(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else if (data.subcommand != null) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.selectSubcommand(ref, data.subcommand, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
        } else if (data.variantIndex != null) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            try {
                int variantIdx = Integer.parseInt(data.variantIndex);
                this.selectVariant(ref, variantIdx, commandBuilder, eventBuilder, store);
                this.sendUpdate(commandBuilder, eventBuilder, false);
            }
            catch (NumberFormatException numberFormatException) {}
        } else if (data.sendToChat != null) {
            this.handleSendToChat(ref, store);
        }
    }

    private void handleSendToChat(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }
        String command = this.buildCurrentCommandString();
        playerComponent.getPageManager().setPage(ref, store, Page.None);
        this.playerRef.getPacketHandler().write((Packet)new OpenChatWithCommand(command));
    }

    @Nonnull
    private String buildCurrentCommandString() {
        StringBuilder sb = new StringBuilder("/");
        sb.append(this.selectedCommand);
        for (String part : this.subcommandBreadcrumb) {
            sb.append(" ").append(part);
        }
        AbstractCommand currentContext = CommandManager.get().getCommandRegistration().get(this.selectedCommand);
        if (currentContext != null) {
            for (String string : this.subcommandBreadcrumb) {
                Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
                if ((currentContext = subcommands.get(string)) != null) continue;
                break;
            }
            if (currentContext != null) {
                for (RequiredArg requiredArg : currentContext.getRequiredArguments()) {
                    sb.append(" <").append(requiredArg.getName()).append(">");
                }
            }
        }
        return sb.toString();
    }

    private void buildCommandList(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        commandBuilder.clear("#CommandList");
        Object2ObjectOpenHashMap<String, AbstractCommand> commands = new Object2ObjectOpenHashMap<String, AbstractCommand>(CommandManager.get().getCommandRegistration());
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        commands.values().removeIf(command -> !command.hasPermission(playerComponent));
        if (this.searchQuery.isEmpty()) {
            this.visibleCommands.clear();
            this.visibleCommands.addAll(commands.keySet());
            Collections.sort(this.visibleCommands);
        } else {
            ObjectArrayList<SearchResult> results = new ObjectArrayList<SearchResult>();
            for (Map.Entry entry : commands.entrySet()) {
                if (entry.getValue() == null) continue;
                results.add(new SearchResult((String)entry.getKey(), MatchResult.EXACT));
            }
            String[] terms = this.searchQuery.split(" ");
            for (int termIndex = 0; termIndex < terms.length; ++termIndex) {
                String term = terms[termIndex];
                for (int cmdIndex = results.size() - 1; cmdIndex >= 0; --cmdIndex) {
                    SearchResult result = (SearchResult)results.get(cmdIndex);
                    AbstractCommand command2 = (AbstractCommand)commands.get(result.name);
                    MatchResult match = command2 != null ? command2.matches(this.playerRef.getLanguage(), term, termIndex) : MatchResult.NONE;
                    if (match == MatchResult.NONE) {
                        results.remove(cmdIndex);
                        continue;
                    }
                    result.match = result.match.min(match);
                }
            }
            results.sort(SearchResult.COMPARATOR);
            this.visibleCommands.clear();
            for (int i = 0; i < results.size(); ++i) {
                this.visibleCommands.add(((SearchResult)results.get((int)i)).name);
            }
        }
        for (int i = 0; i < this.visibleCommands.size(); ++i) {
            String name = this.visibleCommands.get(i);
            commandBuilder.append("#CommandList", "Pages/BasicTextButton.ui");
            commandBuilder.set("#CommandList[" + i + "].TextSpans", Message.raw(name));
            eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CommandList[" + i + "]", EventData.of("Command", name));
            if (!name.equals(this.selectedCommand)) continue;
            commandBuilder.set("#CommandList[" + i + "].Style", BUTTON_LABEL_STYLE_SELECTED);
        }
    }

    private void selectCommand(@Nonnull Ref<EntityStore> ref, @Nonnull String commandName, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        AbstractCommand command = CommandManager.get().getCommandRegistration().get(commandName);
        if (command == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        commandBuilder.set("#CommandName.TextSpans", Message.raw(commandName));
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        commandBuilder.set("#CommandDescription.TextSpans", Message.translation(command.getDescription()));
        this.selectedSubcommand = null;
        this.selectedVariantIndex = null;
        this.subcommandBreadcrumb.clear();
        this.buildSubcommandTabs(command, playerComponent, commandBuilder, eventBuilder);
        this.displayCommandInfo(command, playerComponent, commandBuilder, eventBuilder);
        if (this.selectedCommand != null && this.visibleCommands.contains(this.selectedCommand)) {
            commandBuilder.set("#CommandList[" + this.visibleCommands.indexOf(this.selectedCommand) + "].Style", BUTTON_LABEL_STYLE);
        }
        commandBuilder.set("#CommandList[" + this.visibleCommands.indexOf(commandName) + "].Style", BUTTON_LABEL_STYLE_SELECTED);
        this.selectedCommand = commandName;
    }

    private void selectSubcommand(@Nonnull Ref<EntityStore> ref, @Nonnull String subcommandName, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        AbstractCommand currentContext = CommandManager.get().getCommandRegistration().get(this.selectedCommand);
        if (currentContext == null) {
            return;
        }
        for (String breadcrumbPart : this.subcommandBreadcrumb) {
            Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
            if ((currentContext = subcommands.get(breadcrumbPart)) != null) continue;
            return;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
        AbstractCommand subcommand = subcommands.get(subcommandName);
        if (subcommand == null) {
            return;
        }
        this.subcommandBreadcrumb.add(subcommandName);
        this.selectedSubcommand = subcommandName;
        this.selectedVariantIndex = null;
        this.updateTitleWithBreadcrumb(commandBuilder);
        commandBuilder.set("#CommandDescription.TextSpans", Message.translation(subcommand.getDescription()));
        this.buildSubcommandTabs(subcommand, playerComponent, commandBuilder, eventBuilder);
        commandBuilder.set("#BackButton.Visible", true);
        this.displayCommandInfo(subcommand, playerComponent, commandBuilder, eventBuilder);
    }

    private void selectVariant(@Nonnull Ref<EntityStore> ref, int variantIndex, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        AbstractCommand currentContext = CommandManager.get().getCommandRegistration().get(this.selectedCommand);
        if (currentContext == null) {
            return;
        }
        for (String breadcrumbPart : this.subcommandBreadcrumb) {
            Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
            if ((currentContext = subcommands.get(breadcrumbPart)) != null) continue;
            return;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        try {
            Field variantsField = AbstractCommand.class.getDeclaredField("variantCommands");
            variantsField.setAccessible(true);
            Int2ObjectMap variants = (Int2ObjectMap)variantsField.get(currentContext);
            AbstractCommand variant = (AbstractCommand)variants.get(variantIndex);
            if (variant == null || !variant.hasPermission(playerComponent)) {
                return;
            }
            this.selectedVariantIndex = variantIndex;
            this.updateTitleWithVariantSuffix(commandBuilder);
            commandBuilder.set("#VariantsSection.Visible", false);
            commandBuilder.set("#BackButton.Visible", true);
            commandBuilder.set("#CommandUsageLabel.TextSpans", this.getSimplifiedUsage(variant, playerComponent));
            this.buildParametersSection(variant, playerComponent, commandBuilder);
            this.buildArgumentTypesSection(variant, playerComponent, commandBuilder);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void navigateUp(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (this.selectedVariantIndex != null) {
            this.selectedVariantIndex = null;
            AbstractCommand currentContext = CommandManager.get().getCommandRegistration().get(this.selectedCommand);
            if (currentContext == null) {
                return;
            }
            for (String breadcrumbPart : this.subcommandBreadcrumb) {
                Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
                if ((currentContext = subcommands.get(breadcrumbPart)) != null) continue;
                return;
            }
            Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
            this.updateTitleWithBreadcrumb(commandBuilder);
            this.displayCommandInfo(currentContext, playerComponent, commandBuilder, eventBuilder);
            commandBuilder.set("#BackButton.Visible", !this.subcommandBreadcrumb.isEmpty());
            return;
        }
        if (this.subcommandBreadcrumb.isEmpty()) {
            return;
        }
        this.subcommandBreadcrumb.remove(this.subcommandBreadcrumb.size() - 1);
        AbstractCommand currentContext = CommandManager.get().getCommandRegistration().get(this.selectedCommand);
        if (currentContext == null) {
            return;
        }
        for (String breadcrumbPart : this.subcommandBreadcrumb) {
            Map<String, AbstractCommand> subcommands = currentContext.getSubCommands();
            if ((currentContext = subcommands.get(breadcrumbPart)) != null) continue;
            return;
        }
        Player playerComponent = componentAccessor.getComponent(ref, Player.getComponentType());
        this.selectedSubcommand = this.subcommandBreadcrumb.isEmpty() ? null : this.subcommandBreadcrumb.get(this.subcommandBreadcrumb.size() - 1);
        this.updateTitleWithBreadcrumb(commandBuilder);
        this.buildSubcommandTabs(currentContext, playerComponent, commandBuilder, eventBuilder);
        this.displayCommandInfo(currentContext, playerComponent, commandBuilder, eventBuilder);
    }

    private void buildSubcommandTabs(@Nonnull AbstractCommand command, @Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear("#SubcommandCards");
        Map<String, AbstractCommand> subcommands = command.getSubCommands();
        if (subcommands.isEmpty()) {
            commandBuilder.set("#SubcommandSection.Visible", false);
        } else {
            commandBuilder.set("#SubcommandSection.Visible", true);
            int cardIndex = 0;
            int rowIndex = 0;
            int cardsInCurrentRow = 0;
            for (Map.Entry<String, AbstractCommand> entry : subcommands.entrySet()) {
                AbstractCommand subcommand = entry.getValue();
                if (!subcommand.hasPermission(playerComponent)) continue;
                if (cardsInCurrentRow == 0) {
                    commandBuilder.appendInline("#SubcommandCards", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");
                }
                commandBuilder.append("#SubcommandCards[" + rowIndex + "]", "Pages/SubcommandCard.ui");
                commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #SubcommandName.TextSpans", Message.raw(entry.getKey()));
                commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #SubcommandUsage.TextSpans", this.getSimplifiedUsage(subcommand, playerComponent));
                commandBuilder.set("#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "] #SubcommandDescription.TextSpans", Message.translation(subcommand.getDescription()));
                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SubcommandCards[" + rowIndex + "][" + cardsInCurrentRow + "]", EventData.of("Subcommand", entry.getKey()));
                ++cardIndex;
                if (++cardsInCurrentRow < 3) continue;
                cardsInCurrentRow = 0;
                ++rowIndex;
            }
        }
        commandBuilder.set("#BackButton.Visible", !this.subcommandBreadcrumb.isEmpty());
    }

    private void updateTitleWithBreadcrumb(@Nonnull UICommandBuilder commandBuilder) {
        StringBuilder titleText = new StringBuilder(this.selectedCommand);
        for (String part : this.subcommandBreadcrumb) {
            titleText.append(" > ").append(part);
        }
        commandBuilder.set("#CommandName.TextSpans", Message.raw(titleText.toString()));
    }

    private void updateTitleWithVariantSuffix(@Nonnull UICommandBuilder commandBuilder) {
        StringBuilder titleText = new StringBuilder(this.selectedCommand);
        for (String part : this.subcommandBreadcrumb) {
            titleText.append(" > ").append(part);
        }
        titleText.append(" [Variant]");
        commandBuilder.set("#CommandName.TextSpans", Message.raw(titleText.toString()));
    }

    private void buildAliasesSection(@Nonnull AbstractCommand command, @Nonnull UICommandBuilder commandBuilder) {
        Set<String> aliases = command.getAliases();
        if (aliases == null || aliases.isEmpty()) {
            commandBuilder.set("#AliasesSection.Visible", false);
            return;
        }
        commandBuilder.set("#AliasesSection.Visible", true);
        commandBuilder.set("#AliasesList.TextSpans", Message.raw(String.join((CharSequence)", ", aliases)));
    }

    private void buildPermissionSection(@Nonnull AbstractCommand command, @Nonnull UICommandBuilder commandBuilder) {
        String permission = command.getPermission();
        if (permission == null || permission.isEmpty()) {
            commandBuilder.set("#PermissionSection.Visible", false);
            return;
        }
        commandBuilder.set("#PermissionSection.Visible", true);
        commandBuilder.set("#PermissionLabel.TextSpans", Message.raw(permission));
    }

    private void buildVariantsSection(@Nonnull AbstractCommand command, @Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        commandBuilder.clear("#VariantsList");
        try {
            Field variantsField = AbstractCommand.class.getDeclaredField("variantCommands");
            variantsField.setAccessible(true);
            Int2ObjectMap variants = (Int2ObjectMap)variantsField.get(command);
            if (variants.isEmpty()) {
                commandBuilder.set("#VariantsSection.Visible", false);
                return;
            }
            commandBuilder.set("#VariantsSection.Visible", true);
            int displayIndex = 0;
            for (Int2ObjectMap.Entry entry : variants.int2ObjectEntrySet()) {
                AbstractCommand variant = (AbstractCommand)entry.getValue();
                int variantIndex = entry.getIntKey();
                if (!variant.hasPermission(playerComponent)) continue;
                commandBuilder.append("#VariantsList", "Pages/VariantCard.ui");
                commandBuilder.set("#VariantsList[" + displayIndex + "] #VariantUsage.TextSpans", this.getSimplifiedUsage(variant, playerComponent));
                eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#VariantsList[" + displayIndex + "]", EventData.of("Variant", String.valueOf(variantIndex)));
                ++displayIndex;
            }
            if (displayIndex == 0) {
                commandBuilder.set("#VariantsSection.Visible", false);
            }
        }
        catch (Exception e) {
            commandBuilder.set("#VariantsSection.Visible", false);
        }
    }

    private void displayCommandInfo(@Nonnull AbstractCommand command, @Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder) {
        this.buildVariantsSection(command, playerComponent, commandBuilder, eventBuilder);
        this.buildAliasesSection(command, commandBuilder);
        this.buildPermissionSection(command, commandBuilder);
        commandBuilder.set("#CommandUsageLabel.TextSpans", this.getSimplifiedUsage(command, playerComponent));
        this.buildParametersSection(command, playerComponent, commandBuilder);
        this.buildArgumentTypesSection(command, playerComponent, commandBuilder);
    }

    private Message getSimplifiedUsage(@Nonnull AbstractCommand command, @Nonnull Player playerComponent) {
        Message message = Message.raw("/").insert(command.getFullyQualifiedName());
        try {
            Field requiredArgsField = AbstractCommand.class.getDeclaredField("requiredArguments");
            requiredArgsField.setAccessible(true);
            List requiredArgs = (List)requiredArgsField.get(command);
            for (RequiredArg arg : requiredArgs) {
                message.insert(" <").insert(Message.translation(arg.getName())).insert(">");
            }
        }
        catch (Exception requiredArgsField) {
            // empty catch block
        }
        try {
            Field optionalArgsField = AbstractCommand.class.getDeclaredField("optionalArguments");
            optionalArgsField.setAccessible(true);
            Map optionalArgs = (Map)optionalArgsField.get(command);
            if (!optionalArgs.isEmpty()) {
                message.insert(" [").insert(Message.translation("server.customUI.commandListPage.optionsIndicator")).insert("]");
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return message;
    }

    private void buildParametersSection(@Nonnull AbstractCommand command, @Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.clear("#RequiredArgumentsList");
        commandBuilder.clear("#OptionalArgumentsList");
        commandBuilder.clear("#DefaultArgumentsList");
        commandBuilder.clear("#FlagArgumentsList");
        boolean hasAnyParameters = false;
        try {
            Field requiredArgsField = AbstractCommand.class.getDeclaredField("requiredArguments");
            requiredArgsField.setAccessible(true);
            List requiredArgs = (List)requiredArgsField.get(command);
            if (!requiredArgs.isEmpty()) {
                hasAnyParameters = true;
                for (int i = 0; i < requiredArgs.size(); ++i) {
                    RequiredArg arg = (RequiredArg)requiredArgs.get(i);
                    commandBuilder.append("#RequiredArgumentsList", "Pages/ParameterItem.ui");
                    commandBuilder.set("#RequiredArgumentsList[" + i + "] #ParamName.TextSpans", Message.raw(arg.getName()));
                    commandBuilder.set("#RequiredArgumentsList[" + i + "] #ParamTag.TextSpans", Message.raw("[Required]"));
                    commandBuilder.set("#RequiredArgumentsList[" + i + "] #ParamType.TextSpans", Message.translation("server.customUI.commandListPage.paramType").param("type", arg.getArgumentType().getName()));
                    commandBuilder.set("#RequiredArgumentsList[" + i + "] #ParamDescription.TextSpans", arg.getDescription() != null ? Message.translation(arg.getDescription()) : Message.translation("server.customUI.commandListPage.noDescription"));
                }
            }
        }
        catch (Exception requiredArgsField) {
            // empty catch block
        }
        try {
            Field optionalArgsField = AbstractCommand.class.getDeclaredField("optionalArguments");
            optionalArgsField.setAccessible(true);
            Map optionalArgs = (Map)optionalArgsField.get(command);
            if (!optionalArgs.isEmpty()) {
                hasAnyParameters = true;
            }
            int optIndex = 0;
            int defIndex = 0;
            int flagIndex = 0;
            for (Map.Entry entry : optionalArgs.entrySet()) {
                FlagArg flagArg;
                Object arg = entry.getValue();
                if (arg instanceof OptionalArg) {
                    OptionalArg optArg = (OptionalArg)arg;
                    if (optArg.getPermission() != null && !playerComponent.hasPermission(optArg.getPermission())) continue;
                    commandBuilder.append("#OptionalArgumentsList", "Pages/ParameterItem.ui");
                    commandBuilder.set("#OptionalArgumentsList[" + optIndex + "] #ParamName.TextSpans", Message.raw("--" + optArg.getName() + " <" + optArg.getName() + ">"));
                    commandBuilder.set("#OptionalArgumentsList[" + optIndex + "] #ParamTag.TextSpans", Message.raw("[Optional]"));
                    commandBuilder.set("#OptionalArgumentsList[" + optIndex + "] #ParamType.TextSpans", Message.translation("server.customUI.commandListPage.paramType").param("type", optArg.getArgumentType().getName()));
                    commandBuilder.set("#OptionalArgumentsList[" + optIndex + "] #ParamDescription.TextSpans", optArg.getDescription() != null ? Message.translation(optArg.getDescription()) : Message.translation("server.customUI.commandListPage.noDescription"));
                    ++optIndex;
                    continue;
                }
                if (arg instanceof DefaultArg) {
                    DefaultArg defArg = (DefaultArg)arg;
                    if (defArg.getPermission() != null && !playerComponent.hasPermission(defArg.getPermission())) continue;
                    commandBuilder.append("#DefaultArgumentsList", "Pages/ParameterItem.ui");
                    commandBuilder.set("#DefaultArgumentsList[" + defIndex + "] #ParamName.TextSpans", Message.raw("--" + defArg.getName() + " <" + defArg.getName() + ">"));
                    commandBuilder.set("#DefaultArgumentsList[" + defIndex + "] #ParamTag.TextSpans", Message.raw("[Default]"));
                    commandBuilder.set("#DefaultArgumentsList[" + defIndex + "] #ParamType.TextSpans", Message.translation("server.customUI.commandListPage.paramTypeDefault").param("type", defArg.getArgumentType().getName()).param("default", defArg.getDefaultValueDescription()));
                    commandBuilder.set("#DefaultArgumentsList[" + defIndex + "] #ParamDescription.TextSpans", defArg.getDescription() != null ? Message.translation(defArg.getDescription()) : Message.translation("server.customUI.commandListPage.noDescription"));
                    ++defIndex;
                    continue;
                }
                if (!(arg instanceof FlagArg) || (flagArg = (FlagArg)arg).getPermission() != null && !playerComponent.hasPermission(flagArg.getPermission())) continue;
                commandBuilder.append("#FlagArgumentsList", "Pages/ParameterItem.ui");
                commandBuilder.set("#FlagArgumentsList[" + flagIndex + "] #ParamName.TextSpans", Message.raw("--" + flagArg.getName()));
                commandBuilder.set("#FlagArgumentsList[" + flagIndex + "] #ParamTag.TextSpans", Message.raw("[Flag]"));
                commandBuilder.set("#FlagArgumentsList[" + flagIndex + "] #ParamType.TextSpans", Message.translation("server.customUI.commandListPage.paramTypeFlag"));
                commandBuilder.set("#FlagArgumentsList[" + flagIndex + "] #ParamDescription.TextSpans", flagArg.getDescription() != null ? Message.translation(flagArg.getDescription()) : Message.translation("server.customUI.commandListPage.noDescription"));
                ++flagIndex;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        commandBuilder.set("#ParametersSection.Visible", hasAnyParameters);
    }

    private void buildArgumentTypesSection(@Nonnull AbstractCommand command, @Nonnull Player playerComponent, @Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.clear("#ArgumentTypesList");
        HashSet allArgumentTypes = new HashSet();
        try {
            Field requiredArgsField = AbstractCommand.class.getDeclaredField("requiredArguments");
            requiredArgsField.setAccessible(true);
            List requiredArgs = (List)requiredArgsField.get(command);
            for (RequiredArg arg : requiredArgs) {
                allArgumentTypes.add(arg.getArgumentType());
            }
            Field field = AbstractCommand.class.getDeclaredField("optionalArguments");
            field.setAccessible(true);
            Map optionalArgs = (Map)field.get(command);
            for (Object entry : optionalArgs.values()) {
                if (entry instanceof OptionalArg) {
                    allArgumentTypes.add(((OptionalArg)entry).getArgumentType());
                    continue;
                }
                if (!(entry instanceof DefaultArg)) continue;
                allArgumentTypes.add(((DefaultArg)entry).getArgumentType());
            }
        }
        catch (Exception requiredArgsField) {
            // empty catch block
        }
        if (allArgumentTypes.isEmpty()) {
            commandBuilder.set("#ArgumentTypesSection.Visible", false);
            return;
        }
        commandBuilder.set("#ArgumentTypesSection.Visible", true);
        int index = 0;
        for (ArgumentType argumentType : allArgumentTypes) {
            commandBuilder.append("#ArgumentTypesList", "Pages/ArgumentTypeItem.ui");
            commandBuilder.set("#ArgumentTypesList[" + index + "] #TypeName.TextSpans", argumentType.getName());
            commandBuilder.set("#ArgumentTypesList[" + index + "] #TypeDescription.TextSpans", argumentType.getArgumentUsage());
            CharSequence[] examples = argumentType.getExamples();
            if (examples != null && examples.length > 0) {
                commandBuilder.set("#ArgumentTypesList[" + index + "] #TypeExamples.TextSpans", Message.translation("server.customUI.commandListPage.examples").param("examples", String.join((CharSequence)"', '", examples)));
            } else {
                commandBuilder.set("#ArgumentTypesList[" + index + "] #TypeExamples.Visible", false);
            }
            ++index;
        }
    }

    public static class CommandListPageEventData {
        static final String KEY_COMMAND = "Command";
        static final String KEY_SUBCOMMAND = "Subcommand";
        static final String KEY_SEARCH_QUERY = "@SearchQuery";
        static final String KEY_NAVIGATE_UP = "NavigateUp";
        static final String KEY_VARIANT = "Variant";
        static final String KEY_SEND_TO_CHAT = "SendToChat";
        public static final BuilderCodec<CommandListPageEventData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CommandListPageEventData.class, CommandListPageEventData::new).addField(new KeyedCodec<String>("Command", Codec.STRING), (entry, s) -> {
            entry.command = s;
        }, entry -> entry.command)).addField(new KeyedCodec<String>("Subcommand", Codec.STRING), (entry, s) -> {
            entry.subcommand = s;
        }, entry -> entry.subcommand)).addField(new KeyedCodec<String>("@SearchQuery", Codec.STRING), (entry, s) -> {
            entry.searchQuery = s;
        }, entry -> entry.searchQuery)).addField(new KeyedCodec<String>("NavigateUp", Codec.STRING), (entry, s) -> {
            entry.navigateUp = s;
        }, entry -> entry.navigateUp)).addField(new KeyedCodec<String>("Variant", Codec.STRING), (entry, s) -> {
            entry.variantIndex = s;
        }, entry -> entry.variantIndex)).addField(new KeyedCodec<String>("SendToChat", Codec.STRING), (entry, s) -> {
            entry.sendToChat = s;
        }, entry -> entry.sendToChat)).build();
        private String command;
        private String subcommand;
        private String searchQuery;
        private String navigateUp;
        private String variantIndex;
        private String sendToChat;
    }

    private static class SearchResult {
        public static final Comparator<SearchResult> COMPARATOR = Comparator.comparing(o -> o.match);
        private final String name;
        private MatchResult match;

        public SearchResult(String name, MatchResult match) {
            this.name = name;
            this.match = match;
        }
    }
}

