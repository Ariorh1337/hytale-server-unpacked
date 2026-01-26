/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.time.commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.commands.worldconfig.WorldConfigPauseTimeCommand;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import javax.annotation.Nonnull;

public class TimeCommand
extends AbstractWorldCommand {
    public TimeCommand() {
        super("time", "server.commands.time.get.desc");
        this.setPermissionGroup(GameMode.Creative);
        this.addAliases("daytime");
        this.addUsageVariant(new SetTimeHourCommand());
        for (TimeOfDay value : TimeOfDay.values()) {
            this.addSubCommand(new SetTimePeriodCommand(value));
        }
        this.addSubCommand(new TimeSetSubCommand());
        this.addSubCommand(new TimePauseCommand());
        this.addSubCommand(new TimeDilationCommand());
    }

    @Override
    public void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        LocalDateTime gameDateTime = worldTimeResource.getGameDateTime();
        Message pausedMessage = Message.translation(world.getWorldConfig().isGameTimePaused() ? "server.commands.time.paused" : "server.commands.time.unpaused");
        Message message = Message.translation("server.commands.time.info").param("worldName", world.getName()).param("timePaused", pausedMessage);
        context.sendMessage(message.param("time", worldTimeResource.getGameTime().toString()).param("dayOfWeek", FormatUtil.addNumberSuffix(gameDateTime.get(ChronoField.DAY_OF_WEEK))).param("weekOfMonth", FormatUtil.addNumberSuffix(gameDateTime.get(ChronoField.ALIGNED_WEEK_OF_MONTH))).param("weekOfYear", FormatUtil.addNumberSuffix(gameDateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR))).param("dayOfYear", FormatUtil.addNumberSuffix(gameDateTime.getDayOfYear())).param("moonPhase", FormatUtil.addNumberSuffix(worldTimeResource.getMoonPhase() + 1)));
    }

    private static class SetTimeHourCommand
    extends AbstractWorldCommand {
        @Nonnull
        private final RequiredArg<Float> timeArg = (RequiredArg)this.withRequiredArg("time", "server.commands.time.set.timeArg.desc", ArgTypes.FLOAT).addValidator(Validators.range(Float.valueOf(0.0f), Float.valueOf(WorldTimeResource.HOURS_PER_DAY)));

        public SetTimeHourCommand() {
            super("server.commands.time.set.desc");
            this.setPermissionGroup(null);
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
            float time = ((Float)this.timeArg.get(context)).floatValue();
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            worldTimeResource.setDayTime(time / (float)WorldTimeResource.HOURS_PER_DAY, world, store);
            context.sendMessage(Message.translation("server.commands.time.set").param("worldName", world.getName()).param("time", worldTimeResource.getGameTime().toString()));
        }
    }

    public static enum TimeOfDay {
        Dawn(hoursOfDaylight -> ((float)WorldTimeResource.HOURS_PER_DAY - hoursOfDaylight) / 2.0f, "day", "morning"),
        Midday(hoursOfDaylight -> (float)WorldTimeResource.HOURS_PER_DAY / 2.0f, "noon"),
        Dusk(hoursOfDaylight -> ((float)WorldTimeResource.HOURS_PER_DAY - hoursOfDaylight) / 2.0f + hoursOfDaylight, "night"),
        Midnight(hoursOfDaylight -> 0.0f, new String[0]);

        @Nonnull
        private final Float2FloatFunction periodFunc;
        private final String[] aliases;

        private TimeOfDay(Float2FloatFunction periodFunc, String ... aliases) {
            this.periodFunc = periodFunc;
            this.aliases = aliases;
        }
    }

    private static class SetTimePeriodCommand
    extends AbstractWorldCommand {
        @Nonnull
        private final TimeOfDay timeOfDay;

        public SetTimePeriodCommand(@Nonnull TimeOfDay timeOfDay) {
            super(timeOfDay.name(), "server.commands.time.period." + timeOfDay.name().toLowerCase() + ".desc");
            this.setPermissionGroup(null);
            this.timeOfDay = timeOfDay;
            this.addAliases(timeOfDay.aliases);
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
            float daylightHours = (float)WorldTimeResource.HOURS_PER_DAY * 0.6f;
            float periodTime = Math.max(0.0f, ((Float)this.timeOfDay.periodFunc.apply(Float.valueOf(daylightHours))).floatValue());
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            worldTimeResource.setDayTime(periodTime / (float)WorldTimeResource.HOURS_PER_DAY, world, store);
            context.sendMessage(Message.translation("server.commands.time.set").param("worldName", world.getName()).param("time", String.format("%s (%s)", worldTimeResource.getGameTime().toString(), this.timeOfDay.name())));
        }
    }

    private static class TimeSetSubCommand
    extends AbstractCommandCollection {
        public TimeSetSubCommand() {
            super("set", "server.commands.time.set.desc");
            this.setPermissionGroup(null);
            this.addUsageVariant(new SetTimeHourCommand());
            for (TimeOfDay value : TimeOfDay.values()) {
                this.addSubCommand(new SetTimePeriodCommand(value));
            }
        }
    }

    private static class TimePauseCommand
    extends AbstractWorldCommand {
        public TimePauseCommand() {
            super("pause", "server.commands.pausetime.desc");
            this.setPermissionGroup(null);
            this.addAliases("stop");
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
            WorldConfigPauseTimeCommand.pauseTime(context.sender(), world, store);
        }
    }

    private static class TimeDilationCommand
    extends AbstractWorldCommand {
        private static final float TIME_DILATION_MIN = 0.01f;
        private static final float TIME_DILATION_MAX = 4.0f;
        @Nonnull
        private final RequiredArg<Float> timeDilationArg = (RequiredArg)((RequiredArg)this.withRequiredArg("timeDilation", "server.commands.time.dilation.timeDilation.desc", ArgTypes.FLOAT).addValidator(Validators.greaterThan(Float.valueOf(0.01f)))).addValidator(Validators.max(Float.valueOf(4.0f)));

        public TimeDilationCommand() {
            super("dilation", "server.commands.time.dilation.desc");
            this.setPermissionGroup(null);
        }

        @Override
        protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
            float timeDilation = ((Float)this.timeDilationArg.get(context)).floatValue();
            World.setTimeDilation(timeDilation, store);
            context.sendMessage(Message.translation("server.commands.time.dilation.set.success").param("timeDilation", timeDilation));
        }
    }
}

