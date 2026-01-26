/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.ui.browser;

import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.browser.FileListProvider;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record FileBrowserConfig(@Nonnull String listElementId, @Nullable String rootSelectorId, @Nullable String searchInputId, @Nullable String currentPathId, @Nonnull List<RootEntry> roots, @Nonnull Set<String> allowedExtensions, boolean enableRootSelector, boolean enableSearch, boolean enableDirectoryNav, boolean enableMultiSelect, int maxResults, @Nullable FileListProvider customProvider, boolean assetPackMode, @Nullable String assetPackSubPath, @Nullable Predicate<Path> terminalDirectoryPredicate) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String listElementId = "#FileList";
        private String rootSelectorId = "#RootSelector";
        private String searchInputId = "#SearchInput";
        private String currentPathId = null;
        private List<RootEntry> roots = List.of();
        private Set<String> allowedExtensions = Set.of();
        private boolean enableRootSelector = true;
        private boolean enableSearch = true;
        private boolean enableDirectoryNav = true;
        private boolean enableMultiSelect = false;
        private int maxResults = 50;
        private FileListProvider customProvider = null;
        private boolean assetPackMode = false;
        private String assetPackSubPath = null;
        private Predicate<Path> terminalDirectoryPredicate = null;

        public Builder listElementId(@Nonnull String listElementId) {
            this.listElementId = listElementId;
            return this;
        }

        public Builder rootSelectorId(@Nullable String rootSelectorId) {
            this.rootSelectorId = rootSelectorId;
            return this;
        }

        public Builder searchInputId(@Nullable String searchInputId) {
            this.searchInputId = searchInputId;
            return this;
        }

        public Builder currentPathId(@Nullable String currentPathId) {
            this.currentPathId = currentPathId;
            return this;
        }

        public Builder roots(@Nonnull List<RootEntry> roots) {
            this.roots = roots;
            return this;
        }

        public Builder allowedExtensions(String ... extensions) {
            this.allowedExtensions = Set.of(extensions);
            return this;
        }

        public Builder allowedExtensions(@Nonnull Set<String> extensions) {
            this.allowedExtensions = extensions;
            return this;
        }

        public Builder enableRootSelector(boolean enable) {
            this.enableRootSelector = enable;
            return this;
        }

        public Builder enableSearch(boolean enable) {
            this.enableSearch = enable;
            return this;
        }

        public Builder enableDirectoryNav(boolean enable) {
            this.enableDirectoryNav = enable;
            return this;
        }

        public Builder enableMultiSelect(boolean enable) {
            this.enableMultiSelect = enable;
            return this;
        }

        public Builder maxResults(int maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public Builder customProvider(@Nullable FileListProvider provider) {
            this.customProvider = provider;
            return this;
        }

        public Builder assetPackMode(boolean enable, @Nullable String subPath) {
            if (enable && subPath == null) {
                throw new IllegalArgumentException("assetPackSubPath cannot be null when assetPackMode is enabled");
            }
            this.assetPackMode = enable;
            this.assetPackSubPath = subPath;
            return this;
        }

        public Builder terminalDirectoryPredicate(@Nullable Predicate<Path> predicate) {
            this.terminalDirectoryPredicate = predicate;
            return this;
        }

        public FileBrowserConfig build() {
            return new FileBrowserConfig(this.listElementId, this.rootSelectorId, this.searchInputId, this.currentPathId, this.roots, this.allowedExtensions, this.enableRootSelector, this.enableSearch, this.enableDirectoryNav, this.enableMultiSelect, this.maxResults, this.customProvider, this.assetPackMode, this.assetPackSubPath, this.terminalDirectoryPredicate);
        }
    }

    public record RootEntry(@Nonnull LocalizableString displayName, @Nonnull Path path) {
        public RootEntry(@Nonnull String displayName, @Nonnull Path path) {
            this(LocalizableString.fromString(displayName), path);
        }
    }
}

