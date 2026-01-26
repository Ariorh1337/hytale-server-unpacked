/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.validation;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.exception.CodecValidationException;
import com.hypixel.hytale.logger.HytaleLogger;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValidationResults {
    protected final ExtraInfo extraInfo;
    @Nullable
    protected List<ValidatorResultsHolder> validatorExceptions;
    @Nullable
    protected List<ValidationResult> results;

    public ValidationResults(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    public ExtraInfo getExtraInfo() {
        return this.extraInfo;
    }

    public void fail(String reason) {
        this.add(ValidationResult.fail(reason));
    }

    public void warn(String reason) {
        this.add(ValidationResult.warn(reason));
    }

    public void add(ValidationResult result) {
        if (this.results == null) {
            this.results = new ObjectArrayList<ValidationResult>();
        }
        this.results.add(result);
    }

    public void _processValidationResults() {
        if (this.results == null || this.results.isEmpty()) {
            return;
        }
        for (ValidationResult validationResult : this.results) {
            Result result = validationResult.result;
            if (result != Result.WARNING && result != Result.FAIL) continue;
            if (this.validatorExceptions == null) {
                this.validatorExceptions = new ObjectArrayList<ValidatorResultsHolder>();
            }
            this.validatorExceptions.add(new ValidatorResultsHolder(this.extraInfo.peekKey(), this.extraInfo.peekLine(), this.extraInfo.peekColumn(), new ObjectArrayList<ValidationResult>(this.results)));
            break;
        }
        this.results.clear();
    }

    public void logOrThrowValidatorExceptions(@Nonnull HytaleLogger logger) {
        this.logOrThrowValidatorExceptions(logger, "Failed to validate asset!\n");
    }

    public void logOrThrowValidatorExceptions(@Nonnull HytaleLogger logger, @Nonnull String msg) {
        if (this.validatorExceptions == null || this.validatorExceptions.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder(msg);
        this.extraInfo.appendDetailsTo(sb);
        boolean failed = false;
        for (ValidatorResultsHolder holder : this.validatorExceptions) {
            if (holder.key != null && !holder.key.isEmpty()) {
                sb.append("Key: ").append(holder.key).append("\n");
            }
            sb.append("Results:\n");
            for (ValidationResult result : holder.results) {
                failed |= result.appendResult(sb);
            }
        }
        if (failed) {
            throw new CodecValidationException(sb.toString());
        }
        logger.at(Level.WARNING).log(sb.toString());
        this.validatorExceptions.clear();
    }

    public boolean hasFailed() {
        if (this.results == null) {
            return false;
        }
        for (ValidationResult res : this.results) {
            if (res.result() != Result.FAIL) continue;
            return true;
        }
        return false;
    }

    @Nullable
    public List<ValidationResult> getResults() {
        if (this.results == null) {
            return null;
        }
        return this.results;
    }

    public void setResults(@Nullable List<ValidationResult> results) {
        this.results = results;
    }

    @Nonnull
    public String toString() {
        return "ValidationResults{results=" + String.valueOf(this.results) + "}";
    }

    public record ValidationResult(Result result, String reason) {
        public boolean appendResult(@Nonnull StringBuilder sb) {
            sb.append("\t").append((Object)this.result).append(": ").append(this.reason).append("\n");
            return this.result == Result.FAIL;
        }

        @Nonnull
        public static ValidationResult fail(String reason) {
            return new ValidationResult(Result.FAIL, reason);
        }

        @Nonnull
        public static ValidationResult warn(String reason) {
            return new ValidationResult(Result.WARNING, reason);
        }
    }

    public static enum Result {
        SUCCESS,
        WARNING,
        FAIL;

    }

    protected record ValidatorResultsHolder(String key, int line, int column, List<ValidationResult> results) {
    }
}

