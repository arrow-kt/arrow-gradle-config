package com.github.nomisrev.ank

import java.util.concurrent.atomic.AtomicReference

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
typealias TestResult = Pair<String, Result<Any?>>

class TestEnviroment {
    private val buffer: AtomicReference<List<TestResult>> = AtomicReference(emptyList())

    fun testReport(): String? = buffer.get()
        ?.takeIf { it.isNotEmpty() }
        ?.run {
            """
        |${colored(ANSI_PURPLE, "Ank Test Results:")}
        |${passed()}${failed()}
        """.trimMargin()
        }

    fun List<TestResult>.passed(): String {
        val successes = filter { it.second.isSuccess }
        return if (successes.isNotEmpty()) colored(ANSI_GREEN, "Passed: ${successes.size}/${size}")
        else ""
    }

    fun List<TestResult>.failed(): String {
        val failures = filter { it.second.isFailure }
        return if (failures.isNotEmpty()) {
            val failures = filter { it.second.isFailure }
            val failuresOut = failures.joinToString(separator = "\n", prefix = "  - ") {
                "${it.first} failed with ${it.second.exceptionOrNull()?.let { t -> t.message ?: t.stackTrace }}"
            }

            colored(ANSI_RED, """
             |Failed: ${failures.size}/${size}
             |$failuresOut
            """.trimMargin())
        } else ""
    }

    fun insert(result: List<TestResult>): List<TestResult> =
        buffer.updateAndGet { it + result }
}

