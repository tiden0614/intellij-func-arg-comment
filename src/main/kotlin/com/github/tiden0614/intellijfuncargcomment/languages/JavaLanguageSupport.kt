package com.github.tiden0614.intellijfuncargcomment.languages

class JavaLanguageSupport: LanguageSupport {
  override fun isTextConstantExpression(text: String): Boolean {
    return ConstantExpressions.contains(text.trim())
  }

  companion object {
    private val ConstantExpressions = hashSetOf(
      "Integer.MAX_VALUE",
      "Integer.MIN_VALUE",
      "Long.MAX_VALUE",
      "Long.MIN_VALUE",
      "Collections.emptyList()",
      "Collections.emptySet()",
      "Collections.emptyMap()",)
  }
}
