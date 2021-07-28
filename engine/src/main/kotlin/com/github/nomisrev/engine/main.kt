package com.github.nomisrev.engine

import java.net.URL
import java.net.URLClassLoader
import javax.script.ScriptEngineManager

fun main() {
    val classLoader = URLClassLoader(emptyList<String>().map { URL(it) }.toTypedArray())
    val seManager = ScriptEngineManager(classLoader)
    val engines = requireNotNull(seManager.getEngineByExtension("kts")) { "getEngineByExtension" }
    engines.eval("println(\"Hello\")")
}