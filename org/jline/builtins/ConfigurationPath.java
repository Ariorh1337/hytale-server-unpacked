/*
 * Decompiled with CFR 0.152.
 */
package org.jline.builtins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class ConfigurationPath {
    private final Path appConfig;
    private final Path userConfig;

    public ConfigurationPath(Path appConfig, Path userConfig) {
        this.appConfig = appConfig;
        this.userConfig = userConfig;
    }

    public ConfigurationPath(String classpathResource, Path userConfig) {
        this.appConfig = null;
        this.userConfig = userConfig;
    }

    public Path getConfig(String name) {
        Path out = null;
        if (this.userConfig != null && Files.exists(this.userConfig.resolve(name), new LinkOption[0])) {
            out = this.userConfig.resolve(name);
        } else if (this.appConfig != null && Files.exists(this.appConfig.resolve(name), new LinkOption[0])) {
            out = this.appConfig.resolve(name);
        }
        return out;
    }

    public Path getUserConfig(String name) throws IOException {
        return this.getUserConfig(name, false);
    }

    public Path getUserConfig(String name, boolean create) throws IOException {
        Path out = null;
        if (this.userConfig != null) {
            if (!Files.exists(this.userConfig.resolve(name), new LinkOption[0]) && create) {
                Files.createFile(this.userConfig.resolve(name), new FileAttribute[0]);
            }
            if (Files.exists(this.userConfig.resolve(name), new LinkOption[0])) {
                out = this.userConfig.resolve(name);
            }
        }
        return out;
    }

    public static ConfigurationPath fromClasspath(String classpathResource) {
        return new ConfigurationPath(classpathResource, null);
    }
}

