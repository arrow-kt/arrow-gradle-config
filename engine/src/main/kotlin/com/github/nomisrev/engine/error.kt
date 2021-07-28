package com.github.nomisrev.engine

// TODO Update this to use Dokka's error reporting system
public data class CompilationException(
//    val path: Path,
    val snippet: Snippet,
    val underlying: Throwable,
    val msg: String
) : NoStackTrace(msg) {
    override fun toString(): String = msg
}

public data class AnkFailedException(val msg: String) : NoStackTrace(msg) {
    override fun toString(): String = msg
}

public abstract class NoStackTrace(msg: String) : Throwable(msg, null, false, false)