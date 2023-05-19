package com.github.tiden0614.intellijfuncargcomment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.*
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

    fun getMethodDef(methodCall: PsiMethodCallExpression, actionEvent: AnActionEvent): PsiMethod? {
      val methodDefCandidates = getResolveHelper(actionEvent).getReferencedMethodCandidates(methodCall, false)
      if (methodDefCandidates.size != 1) {
        return null
      }
      return methodDefCandidates[0].element as PsiMethod
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

    private fun getResolveHelper(actionEvent: AnActionEvent) =
      PsiResolveHelper.SERVICE.getInstance(actionEvent.project!!)
  }
}