package com.github.tiden0614.intellijfuncargcomment.languages

import com.intellij.lang.Language

interface LanguageSupport {
  fun isTextConstantExpression(text: String): Boolean

  interface Factory {
    fun isLanguageSupported(language: Language): Boolean

    fun getLanguageSupport(language: Language): LanguageSupport
  }
}