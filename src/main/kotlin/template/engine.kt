package template

import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

object Engine {

    private val engineCache: ConcurrentMap<List<String>, Map<String, ScriptEngine>> = ConcurrentHashMap()

    private val extensionMappings: Map<String, String> = mapOf(
        "java" to "java",
        "kotlin" to "kts"
    )

    public fun compileCode(snippets: List<Snippet>, compilerArgs: List<String>): List<Snippet> {
        val engine = getEngineCache(snippets, compilerArgs)
        // run each snipped and handle its result
        return snippets.mapIndexed { i, snip ->
            val result = try {
                engine.getOrElse(snip.lang) {
                    throw CompilationException(
//                      path = snippets.first,
                        snippet = snip,
                        underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
                        msg = colored(ANSI_RED, "ΛNK compilation failed [ snippets.first ]")
                    )
                }
                    .also { println("Going to eval ${snip.code}") }
                    .eval(snip.code)
            } catch (e: Exception) {
                // raise error and print to console
                if (snip.isFail) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    e.printStackTrace(pw)
                    return@mapIndexed snip.copy(result = sw.toString())
                } else {
                    println(colored(ANSI_RED, "[✗ snippets.first [${i + 1}]"))
                    throw CompilationException(
                        /*snippets.first, */snip, e, msg = "\n" + """
                    | File located at: snippets.first
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, e.localizedMessage)}
                    """.trimMargin()
                    )
                }
            }

            // handle results, ignore silent snippets
            if (snip.isSilent) snip
            else snip.copy(result = result?.let {
                when {
                    // replace entire snippet with result
                    snip.isReplace -> it.toString()
                    // simply append result
                    else -> "// ${it.toString().replace("\n", "\n// ")}"
                }
            })
        }
    }

    private fun getEngineCache(snippets: List<Snippet>, compilerArgs: List<String>): Map<String, ScriptEngine> {
        val cache = engineCache[compilerArgs]
        return if (cache == null) { // create a new engine
            val classLoader = URLClassLoader(compilerArgs.map { URL(it) }.toTypedArray())
            val seManager = ScriptEngineManager(classLoader)
            val langs = snippets.map(Snippet::lang).distinct()
            val engines = langs.toList().associateWith {
                seManager.getEngineByExtension(extensionMappings.getOrDefault(it, "kts"))
            }
            engineCache.putIfAbsent(compilerArgs, engines) ?: engines
        } else { // reset an engine. Non thread-safe
            cache.forEach { (_, engine) ->
                engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)
            }
            cache
        }
    }
}
