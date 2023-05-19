package com.github.tiden0614.intellijfuncargcomment

import com.github.tiden0614.intellijfuncargcomment.actions.AddFunctionArgCommentAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.intellij.lang.annotations.Language

class MyPluginTest : BasePlatformTestCase() {
  fun testMyAction() {
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
    myFixture.testAction(AddFunctionArgCommentAction())
    println(file.text)
  }
}
