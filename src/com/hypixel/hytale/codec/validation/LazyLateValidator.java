package com.hypixel.hytale.codec.validation;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class LazyLateValidator<T> implements LateValidator<T> {
   @Nonnull
   private final Supplier<LateValidator<? super T>> supplier;
   private volatile LateValidator<? super T> validator;

   public LazyLateValidator(@Nonnull Supplier<LateValidator<? super T>> supplier) {
      this.supplier = supplier;
   }

   private LateValidator<? super T> get() {
      if (this.validator == null) {
         this.validator = this.supplier.get();
      }

      return this.validator;
   }

   @Override
   public void accept(T t, ValidationResults results) {
      this.get().accept(t, results);
   }

   @Override
   public void acceptLate(T t, ValidationResults results, ExtraInfo extraInfo) {
      this.get().acceptLate(t, results, extraInfo);
   }

   @Override
   public void updateSchema(SchemaContext context, Schema target) {
      this.get().updateSchema(context, target);
   }
}
