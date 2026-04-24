package org.bson;

final class NoOpFieldNameValidator implements FieldNameValidator {
   static final NoOpFieldNameValidator INSTANCE = new NoOpFieldNameValidator();

   private NoOpFieldNameValidator() {
   }

   @Override
   public boolean validate(String fieldName) {
      return true;
   }

   @Override
   public FieldNameValidator getValidatorForField(String fieldName) {
      return this;
   }
}
