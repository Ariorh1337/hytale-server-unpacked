/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import com.hypixel.hytale.common.util.SystemUtil;
import com.hypixel.hytale.function.supplier.SupplierUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class HardwareUtil {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final int PROCESS_TIMEOUT_SECONDS = 2;
    private static final Pattern UUID_PATTERN = Pattern.compile("([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})");
    private static final Supplier<UUID> WINDOWS = SupplierUtil.cache(() -> {
        UUID uuid;
        String output = HardwareUtil.runCommand("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Cryptography", "/v", "MachineGuid");
        if (output != null) {
            for (String line : output.split("\r?\n")) {
                Matcher matcher;
                if (!line.contains("MachineGuid") || !(matcher = UUID_PATTERN.matcher(line)).find()) continue;
                return UUID.fromString(matcher.group(1));
            }
        }
        if ((output = HardwareUtil.runCommand("powershell", "-NoProfile", "-Command", "(Get-CimInstance -Class Win32_ComputerSystemProduct).UUID")) != null && (uuid = HardwareUtil.parseUuidFromOutput(output)) != null) {
            return uuid;
        }
        output = HardwareUtil.runCommand("wmic", "csproduct", "get", "UUID");
        if (output != null && (uuid = HardwareUtil.parseUuidFromOutput(output)) != null) {
            return uuid;
        }
        throw new RuntimeException("Failed to get hardware UUID for Windows - registry, PowerShell, and wmic all failed");
    });
    private static final Supplier<UUID> MAC = SupplierUtil.cache(() -> {
        Matcher matcher;
        String output = HardwareUtil.runCommand("/usr/sbin/ioreg", "-rd1", "-c", "IOPlatformExpertDevice");
        if (output != null) {
            for (String line : output.split("\r?\n")) {
                if (!line.contains("IOPlatformUUID") || !(matcher = UUID_PATTERN.matcher(line)).find()) continue;
                return UUID.fromString(matcher.group(1));
            }
        }
        if ((output = HardwareUtil.runCommand("/usr/sbin/system_profiler", "SPHardwareDataType")) != null) {
            for (String line : output.split("\r?\n")) {
                if (!line.contains("Hardware UUID") || !(matcher = UUID_PATTERN.matcher(line)).find()) continue;
                return UUID.fromString(matcher.group(1));
            }
        }
        throw new RuntimeException("Failed to get hardware UUID for macOS - ioreg and system_profiler both failed");
    });
    private static final Supplier<UUID> LINUX = SupplierUtil.cache(() -> {
        UUID uuid;
        UUID machineId = HardwareUtil.readMachineIdFile(Path.of("/etc/machine-id", new String[0]));
        if (machineId != null) {
            return machineId;
        }
        machineId = HardwareUtil.readMachineIdFile(Path.of("/var/lib/dbus/machine-id", new String[0]));
        if (machineId != null) {
            return machineId;
        }
        try {
            String content;
            Path path = Path.of("/sys/class/dmi/id/product_uuid", new String[0]);
            if (Files.isReadable(path) && !(content = Files.readString(path, StandardCharsets.UTF_8).trim()).isEmpty()) {
                return UUID.fromString(content);
            }
        }
        catch (Exception path) {
            // empty catch block
        }
        String output = HardwareUtil.runCommand("dmidecode", "-s", "system-uuid");
        if (output != null && (uuid = HardwareUtil.parseUuidFromOutput(output)) != null) {
            return uuid;
        }
        throw new RuntimeException("Failed to get hardware UUID for Linux - all methods failed");
    });

    @Nullable
    private static String runCommand(String ... command) {
        try {
            Process process = new ProcessBuilder(command).start();
            if (process.waitFor(2L, TimeUnit.SECONDS)) {
                return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            }
            process.destroyForcibly();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    @Nullable
    private static UUID parseUuidFromOutput(String output) {
        Matcher matcher = UUID_PATTERN.matcher(output);
        if (matcher.find()) {
            return UUID.fromString(matcher.group(1));
        }
        return null;
    }

    @Nullable
    private static UUID readMachineIdFile(Path path) {
        try {
            if (!Files.isReadable(path)) {
                return null;
            }
            String content = Files.readString(path, StandardCharsets.UTF_8).trim();
            if (content.isEmpty() || content.length() != 32) {
                return null;
            }
            return UUID.fromString(content.substring(0, 8) + "-" + content.substring(8, 12) + "-" + content.substring(12, 16) + "-" + content.substring(16, 20) + "-" + content.substring(20, 32));
        }
        catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static UUID getUUID() {
        try {
            return switch (SystemUtil.TYPE) {
                default -> throw new MatchException(null, null);
                case SystemUtil.SystemType.WINDOWS -> WINDOWS.get();
                case SystemUtil.SystemType.LINUX -> LINUX.get();
                case SystemUtil.SystemType.MACOS -> MAC.get();
                case SystemUtil.SystemType.OTHER -> throw new RuntimeException("Unknown OS!");
            };
        }
        catch (Exception e) {
            ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to get Hardware UUID");
            return null;
        }
    }
}

