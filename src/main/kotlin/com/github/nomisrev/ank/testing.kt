package com.github.nomisrev.ank

import arrow.core.Either
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
data class TestResult(val result: Either<Throwable, Any?>, val name: String)

operator fun String.invoke(f: suspend () -> Any?): TestResult =
    test(this, f)

fun test(name: String, f: suspend () -> Any?): TestResult =
    runBlocking {
        TestResult(Either.catch { f() }, name)
    }