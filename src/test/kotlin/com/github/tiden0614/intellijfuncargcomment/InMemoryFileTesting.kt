package com.github.tiden0614.intellijfuncargcomment

import com.github.tiden0614.intellijfuncargcomment.actions.AddFunctionArgCommentAction
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.findParentInFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class InMemoryFileTesting : BasePlatformTestCase() {
  fun testSimpleMethodAnnotation() {
    @Language("JAVA")
    val javaText = """
      public class Test {
        public int myMethod(int a, int b, String c, List<String> d, List<String> e) {
          return 0;
        }
        
        public static void main(String[] args) {
          Test test = new Test();
          test.my<caret>Method(0, Integer.MAX_VALUE, "2", null, new ArrayList<>());
        }
      }
    """.trimIndent()

    val file = myFixture.addFileToProject("Test.java", javaText)
    myFixture.configureFromExistingVirtualFile(file.virtualFile)
    val methodCall = file
      .findElementAt(myFixture.editor.caretModel.offset)!!
      .findParentInFile { it is PsiMethodCallExpression } as PsiMethodCallExpression
    assertEquals(5, methodCall.argumentList.expressionCount)

    myFixture.testAction(AddFunctionArgCommentAction())

    assertMethodCallArgComment(methodCall, 0, "/*a*/")
    assertMethodCallArgComment(methodCall, 1, "/*b*/")
    assertMethodCallArgComment(methodCall, 2, "/*c*/")
    assertMethodCallArgComment(methodCall, 3, "/*d*/")
    assertMethodCallArgComment(methodCall, 4, null)
  }

  fun testNotReplacingExistingComment() {
    @Language("JAVA")
    val javaText = """
      public class Test {
        public int myMethod(int a, int b) {
          return 0;
        }
        
        public static void main(String[] args) {
          Test test = new Test();
          test.my<caret>Method(0, /* existingComment */ 0);
        }
      }
    """.trimIndent()

    val file = myFixture.addFileToProject("Test.java", javaText)
    myFixture.configureFromExistingVirtualFile(file.virtualFile)
    val methodCall = file
      .findElementAt(myFixture.editor.caretModel.offset)!!
      .findParentInFile { it is PsiMethodCallExpression } as PsiMethodCallExpression
    assertEquals(2, methodCall.argumentList.expressionCount)

    myFixture.testAction(AddFunctionArgCommentAction())

    assertMethodCallArgComment(methodCall, 0, "/*a*/")
    assertMethodCallArgComment(methodCall, 1, "/* existingComment */")
  }

  fun testNotTouchingVarArgs() {
    @Language("JAVA")
    val javaText = """
      public class Test {
        public int myMethod(int a, Integer... b) {
          return 0;
        }
        
        public static void main(String[] args) {
          Test test = new Test();
          test.my<caret>Method(0, 1, 2, 3, 4);
        }
      }
    """.trimIndent()

    val file = myFixture.addFileToProject("Test.java", javaText)
    myFixture.configureFromExistingVirtualFile(file.virtualFile)
    val methodCall = file
      .findElementAt(myFixture.editor.caretModel.offset)!!
      .findParentInFile { it is PsiMethodCallExpression } as PsiMethodCallExpression
    assertEquals(5, methodCall.argumentList.expressionCount)

    myFixture.testAction(AddFunctionArgCommentAction())

    assertMethodCallArgComment(methodCall, 0, "/*a*/")
    for (i in 1 .. 4) {
      assertMethodCallArgComment(methodCall, i, null)
    }
  }

  fun testEmptyVarArg() {
    @Language("JAVA")
    val javaText = """
      public class Test {
        public int myMethod(int a, Integer... b) {
          return 0;
        }
        
        public static void main(String[] args) {
          Test test = new Test();
          test.my<caret>Method(0);
        }
      }
    """.trimIndent()

    val file = myFixture.addFileToProject("Test.java", javaText)
    myFixture.configureFromExistingVirtualFile(file.virtualFile)
    val methodCall = file
      .findElementAt(myFixture.editor.caretModel.offset)!!
      .findParentInFile { it is PsiMethodCallExpression } as PsiMethodCallExpression
    assertEquals(1, methodCall.argumentList.expressionCount)

    myFixture.testAction(AddFunctionArgCommentAction())

    assertMethodCallArgComment(methodCall, 0, "/*a*/")
  }

  private fun assertMethodCallArgComment(methodCall: PsiMethodCallExpression, i: Int, expected: String?): Unit {
    val comment = findArgCommentFor(methodCall, i)
    assertEquals(expected, comment)
  }

  private fun findArgCommentFor(methodCall: PsiMethodCallExpression, i: Int): String? {
    return Utils.getPrevSiblingComment(methodCall.argumentList.expressions[i])?.text
  }
}
