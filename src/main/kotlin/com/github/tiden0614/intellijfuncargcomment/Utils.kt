package com.github.tiden0614.intellijfuncargcomment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.findParentInFile

class Utils {
  companion object {
    fun getPrevSiblingComment(element: PsiElement): PsiComment? {
      var sibling = element.prevSibling
      while (sibling != null) {
        if (sibling is PsiWhiteSpace) {
          sibling = sibling.prevSibling
          continue
        }

        return if (sibling is PsiComment) {
          sibling
        } else {
          null
        }
      }

      return null
    }

    fun getMethodCallAtCaret(actionEvent: AnActionEvent): PsiMethodCallExpression? {
      val editor = actionEvent.getData(CommonDataKeys.EDITOR) ?: return null
      val caretModel = editor.caretModel
      val offset = caretModel.offset
      val psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE) ?: return null
      val element = psiFile.findElementAt(offset) ?: return null

      if (element is PsiIdentifier) {
        val maybeMethodCall = element.findParentInFile { it is PsiMethodCallExpression }
        if (maybeMethodCall != null) {
          return maybeMethodCall as PsiMethodCallExpression
        }
      }

      return null
    }
  }
}