package com.github.tiden0614.intellijfuncargcomment.languages

import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage

class LanguageSupportFactory: LanguageSupport.Factory {
  override fun isLanguageSupported(language: Language): Boolean {
    return SupportedLanguages.contains(language.id)
  }

  override fun getLanguageSupport(language: Language): LanguageSupport {
    if (!isLanguageSupported(language)) {
      throw Exception("Language ${language.displayName} isn't supported")
    }

    return SupportedLanguages[language.id]!!
  }

  companion object {
    private val SupportedLanguages = hashMapOf(Pair("JAVA", JavaLanguageSupport()))
  }
}
