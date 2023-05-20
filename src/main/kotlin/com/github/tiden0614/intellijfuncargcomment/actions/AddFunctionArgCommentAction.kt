package com.github.tiden0614.intellijfuncargcomment.actions

import com.github.tiden0614.intellijfuncargcomment.Utils
import com.github.tiden0614.intellijfuncargcomment.languages.LanguageSupport
import com.github.tiden0614.intellijfuncargcomment.languages.LanguageSupportFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiParameter
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class AddFunctionArgCommentAction(
  private val languageSupportFactory: LanguageSupport.Factory = LanguageSupportFactory()): AnAction() {

  override fun actionPerformed(actionEvent: AnActionEvent) {
    debug {"Entered action performed"}
    var methodCall = Utils.getMethodCallAtCaret(actionEvent) ?: return
    debug {"Found methodCall ${methodCall.text} with argList ${methodCall.argumentList.expressions}"}
    val methodDef = methodCall.resolveMethod()
    if (methodDef == null) {
      debug { "Cannot resolve a method def for method call ${methodCall.text}" }
      return
    }

    debug { "Found methodCall to ${methodDef.name} with argList ${methodCall.argumentList.expressions}" }
    val editor = actionEvent.getData(CommonDataKeys.EDITOR)!!
    val project = actionEvent.project!!

    val paramListCount = methodDef.parameterList.parametersCount
    assert(paramListCount > 0 && (paramListCount == methodCall.argumentList.expressionCount || methodDef.isVarArgs))

    val sb = StringBuilder("Replaced args for ${methodDef.name}: ")
    for (i in 0 until paramListCount) {
      val arg = methodCall.argumentList.expressions[i]
      val param = methodDef.parameterList.parameters[i]
      if (shouldAnnotate(arg, param)) {
        val newText = "/*${param.name}*/ ${arg.text}"
        WriteCommandAction.runWriteCommandAction(arg.project) {
          editor.document.replaceString(arg.startOffset, arg.endOffset, newText)
        }
        // Tests seem to have the psi tree out of sync with the document; manually committing here.
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        methodCall = Utils.getMethodCallAtCaret(actionEvent)!!
        sb.append("(${arg.text} -> $newText), ")
      }
    }
    debug { sb.toString() }
  }

  override fun update(actionEvent: AnActionEvent) {
    val language = actionEvent.getData(CommonDataKeys.PSI_FILE)?.language ?: return
    if (!languageSupportFactory.isLanguageSupported(language)) {
      debug {"Unsupported language ${language.id}"}
      return
    }

    val methodCall = Utils.getMethodCallAtCaret(actionEvent) ?: return
    if (methodCall.argumentList.expressionCount == 0) {
      debug {"Method call ${methodCall.text} has 0 arguments"}
      return
    }

    debug {"Action enabled for method call ${methodCall.text}"}
    actionEvent.presentation.isEnabledAndVisible = true
  }

  private fun shouldAnnotate(arg: PsiExpression, param: PsiParameter): Boolean {
    if (Utils.getPrevSiblingComment(arg) != null) {
      return false
    }

    if (param.isVarArgs) {
      return false
    }

    if (arg is PsiLiteralExpression) {
      return true
    }

    if (languageSupportFactory.getLanguageSupport(arg.language).isTextConstantExpression(arg.text)) {
      return true
    }

    return false
  }

  companion object {
    private val log = Logger.getInstance(Companion::class.java)

    private fun debug(messageSupplier: () -> String) {
      if (log.isDebugEnabled) {
        log.debug(messageSupplier())
      }
    }
  }
}