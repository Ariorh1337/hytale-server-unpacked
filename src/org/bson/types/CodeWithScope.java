package org.bson.types;

import java.util.Objects;
import org.bson.Document;

public class CodeWithScope extends Code {
   private final Document scope;
   private static final long serialVersionUID = -6284832275113680002L;

   public CodeWithScope(String code, Document scope) {
      super(code);
      this.scope = scope;
   }

   public Document getScope() {
      return this.scope;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o == null || this.getClass() != o.getClass()) {
         return false;
      }

      if (!super.equals(o)) {
         return false;
      }

      CodeWithScope that = (CodeWithScope)o;
      return Objects.equals(this.scope, that.scope);
   }

   @Override
   public int hashCode() {
      return this.getCode().hashCode() ^ this.scope.hashCode();
   }
}
