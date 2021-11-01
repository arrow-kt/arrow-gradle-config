package com.github.nomisrev.ank

// TODO Update this to use Dokka's error reporting system
public data class CompilationException(
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
