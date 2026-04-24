package org.bson;

import org.bson.assertions.Assertions;

public interface FieldNameValidator {
   boolean validate(String var1);

   default String getValidationErrorMessage(String fieldName) {
      Assertions.isTrue(fieldName + " is valid", !this.validate(fieldName));
      return String.format("Invalid BSON field name %s", fieldName);
   }

   FieldNameValidator getValidatorForField(String var1);

   default void start() {
   }

   default void end() {
   }
}
