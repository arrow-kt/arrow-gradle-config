package com.github.nomisrev.ank

import java.util.*
import kotlinx.coroutines.runBlocking

/**
 * If a user writes a snippet that returns `TestResult`,
 * then we can collect all `TestResult` and create a test-report from it.
 *
 * Alternatively, we could perhaps automatically pick up tests if they're written under
 *
 * ```kotlin:ank:test
 *
 * And then we could **ignore** the output, but inject a `TestCollector` into the `script` of the user,
 * and collect it's output at the end `collector.result()`.
 *
 * This way the user can write multiple tests in a single snippet.
 */
data class TestResult(val result: Result<Any?>, val name: String)

class TestEnviroment {
    private val buffer: MutableList<TestResult> = Collections.synchronizedList(mutableListOf())

    fun test(name: String, f: suspend () -> Any?): Unit =
        runBlocking {
            buffer.add(TestResult(runCatching{ f() }, name))
        }

    fun closeAndReport(): String = """
      Ank Test Results:
      Passed: ${buffer.count { it.result.isSuccess }}/${buffer.size}
      Failed: ${buffer.count { it.result.isFailure }}/${buffer.size}
      ${
        buffer.filter { it.result.isFailure }
            .joinToString(separator = "\n", prefix = "  - ") {
                "${it.name} failed with ${it.result.exceptionOrNull()?.let { t -> t.message ?: t.stackTrace }}"
            }
    }""".trimIndent()

    fun insert(result: TestEnviroment): Boolean =
        buffer.addAll(result.buffer)
}
