package com.google.protobuf;

@CheckReturnValue
final class ExtensionSchemas {
   private static final ExtensionSchema<?> LITE_SCHEMA = new ExtensionSchemaLite();
   private static final ExtensionSchema<?> FULL_SCHEMA = loadSchemaForFullRuntime();

   private static ExtensionSchema<?> loadSchemaForFullRuntime() {
      if (Android.assumeLiteRuntime) {
         return null;
      }

      try {
         Class<?> clazz = Class.forName("com.google.protobuf.ExtensionSchemaFull");
         return (ExtensionSchema<?>)clazz.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
         return null;
      }
   }

   static ExtensionSchema<?> lite() {
      return LITE_SCHEMA;
   }

   static ExtensionSchema<?> full() {
      if (FULL_SCHEMA == null) {
         throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
      } else {
         return FULL_SCHEMA;
      }
   }

   private ExtensionSchemas() {
   }
}
