package arrow.ank

import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import org.junit.Test


class MyAwesomePluginTest : BaseAbstractTest() {
  private val configuration = dokkaConfiguration {
    sourceSets { sourceSet { sourceRoots = listOf("src/main/kotlin") } }
  }

  @Test
  fun `fails in general inlining functions with lambdas`() {
    testInline(
      """
            |/src/main/kotlin/sample/testing.kt
            |package sample
            |/**
               * has KDoc
               * line 2
               * line 3
               *
               * ```kotlin:ank
               * import arrow.core.*
               *
               * 1.right().flatMap {
               *    if (it == 0) "noop".left() else it.right()
               * }
               * ```
               */
            |data class TestingIsEasy(val reason: String)
            """.trimIndent(),
      configuration
    ) {
      documentablesTransformationStage =
        { module ->
          val testedPackage = module.packages.find { it.name.startsWith("sample") }
          val testedClass = testedPackage?.classlikes?.find { it.name == "TestingIsEasy" }

          requireNotNull(testedPackage)
          requireNotNull(testedClass)
        }
    }
  }

}
