package arrow.ank

import org.jetbrains.dokka.base.testApi.testRunner.BaseAbstractTest
import org.junit.Test

class MyAwesomePluginTest : BaseAbstractTest() {
  private val configuration = dokkaConfiguration {
    sourceSets { sourceSet { sourceRoots = listOf("src/main/kotlin") } }
  }

  @Test
  fun `my awesome plugin should find packages and classes`() {
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
               * test("This is a test") {
               *   require(true == true)
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
