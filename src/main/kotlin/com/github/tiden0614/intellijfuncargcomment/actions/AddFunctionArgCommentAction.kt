package com.github.tiden0614.intellijfuncargcomment.actions

import com.github.tiden0614.intellijfuncargcomment.Utils
import com.github.tiden0614.intellijfuncargcomment.languages.LanguageSupport
import com.github.tiden0614.intellijfuncargcomment.languages.LanguageSupportFactory
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiLiteralExpression
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset

class AddFunctionArgCommentAction(
  private val languageSupportFactory: LanguageSupport.Factory = LanguageSupportFactory()): AnAction() {

  override fun actionPerformed(actionEvent: AnActionEvent) {
    var methodCall = Utils.getMethodCallAtCaret(actionEvent) ?: return
    val methodDef = Utils.getMethodDef(methodCall, actionEvent) ?: return
    if (methodDef.isVarArgs) {
      return
    }
    val editor = actionEvent.getData(CommonDataKeys.EDITOR)!!
    val project = actionEvent.project!!

    val argListExprCount = methodCall.argumentList.expressionCount

    assert(argListExprCount > 0 && argListExprCount == methodDef.parameterList.parametersCount)

    for (i in 0 until argListExprCount) {
      val arg = methodCall.argumentList.expressions[i]
      if (shouldAnnotate(arg)) {
        val paramName = methodDef.parameterList.parameters[i].name
        val newText = "/*${paramName}*/ ${arg.text}"
        WriteCommandAction.runWriteCommandAction(arg.project) {
          editor.document.replaceString(arg.startOffset, arg.endOffset, newText)
        }
        // Tests seem to have the psi tree out of sync with the document; manually committing here.
        PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        methodCall = Utils.getMethodCallAtCaret(actionEvent)!!
      }
    }
  }

  override fun update(actionEvent: AnActionEvent) {
    val language = actionEvent.getData(CommonDataKeys.PSI_FILE)?.language ?: return
    if (!languageSupportFactory.isLanguageSupported(language)) {
      return
    }

    val methodCall = Utils.getMethodCallAtCaret(actionEvent) ?: return
    if (methodCall.argumentList.expressionCount == 0) {
      return
    }

    actionEvent.presentation.isEnabledAndVisible = true
  }

  private fun shouldAnnotate(arg: PsiExpression): Boolean {
    if (Utils.getPrevSiblingComment(arg) != null) {
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


}