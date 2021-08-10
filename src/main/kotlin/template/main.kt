package template

import java.net.URLClassLoader
import javax.script.ScriptEngineManager

object Reference

fun main() {
    val classLoader = Reference::class.java.classLoader
        .let { it as? URLClassLoader }
        ?.let {
            URLClassLoader(
                it.urLs.filter {
                    it.file.contains("/kotlin-script") ||
                            it.file.contains("/kotlin-stdlib") ||
                            it.file.contains("/kotlin-reflect") ||
                            it.file.contains("/kotlinx-coroutines") ||
                            it.file.contains("/kotlin-analysis-compiler")
                }.toTypedArray(),
                null
            )
        }
    println("classLoader: $classLoader")

    val saveClassLoader = Thread.currentThread().contextClassLoader
    Thread.currentThread().contextClassLoader = classLoader

    val manager = ScriptEngineManager(classLoader)
    val engine = manager.getEngineByExtension("kts")
    println("I got engine: $engine")

    engine.eval("println(\"HELLO\")")

    Thread.currentThread().contextClassLoader = saveClassLoader
}