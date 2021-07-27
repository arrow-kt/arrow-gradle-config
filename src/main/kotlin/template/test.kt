package template

import java.net.URL
import java.net.URLClassLoader
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

fun main(): Unit {
    val classLoader = URLClassLoader(emptyList<String>().map { URL(it) }.toTypedArray())
    val seManager = ScriptEngineManager(classLoader)
    val engine = seManager.getEngineByExtension("kts")
    engine.eval("val x: Int = 1")
}