package com.github.nomisrev.ank

import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.jetbrains.dokka.utilities.ServiceLocator.toFile

/**
 * Engine which can compile & evaluate code.
 * This [Engine] is optimised to split the
 */
object Engine {
    private val jss233Classpath: List<URL>

    init { // This is optional and experimental, but may save some memory.
        System.setProperty("kotlin.jsr223.experimental.resolve.dependencies.from.context.classloader", "true")

        val loader =  Engine.javaClass.classLoader
        jss233Classpath = loader.getResource("jsr223/list")
            ?.toFile()?.useLines { paths: Sequence<String> ->
                paths.mapNotNull { loader.getResource("jsr223/$it") }.toList()
            }.orEmpty()
    }

    // Maintains a cache with the classpath as KEY, and a set of ScriptEngines for that classpath (Java & Kotlin).
    private val engineCache: ConcurrentMap<List<String>, Map<String, ScriptEngine>> = ConcurrentHashMap()

    // Mappings between fence lang, and extension names
    private val extensionMappings: Map<String, String> = mapOf("java" to "java", "kotlin" to "kts")

    /**
     * Compiles a [List] of [Snippet]s for a given `classpath`.
     * The classpath should be formatted as `file://...` with the URI to the local jars.
     *
     * From Dokka we can simply use the `List<File>` classpath reference of a given Module,
     * otherwise you can programmatically access the ClassPath or load/pass it during build time.
     * Gradle knows the classpath, and can pass it as an argument to the program.
     */
    public fun compileCode(snippets: List<Snippet>, compilerArgs: List<String>): List<Snippet> {
        val classLoader = classLoader(compilerArgs)

        // We need to get the original contextClassLoader which Dokka uses to run, and store it
        // Then we need to set the contextClassloader so the engine can correctly define the compilation classpath
        // We do this **whilst** decoupling the classLoader for the ScriptEngine with the classLoader of Dokka.
        // This is because Dokka shadows some of the Kotlin compiler dependencies, having them mixed results in incorrect state
        val originalClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = classLoader

        val engineCache = getEngineCache(snippets, classLoader, compilerArgs)

        // run each snipped and handle its result
        return snippets.mapIndexed { i, snip ->
            val result = try {
                val engine = engineCache.getOrElse(snip.lang) {
                    throw CompilationException(
                        snippet = snip,
                        underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
                        msg = colored(ANSI_RED, "ΛNK compilation failed, no engine configured for `${snip.lang}`")
                    )
                }

                engine.eval(snip.code)
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
                        snip, e, msg = "\n" + """
                    | ${snip.path}
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, e.localizedMessage)}
                    """.trimMargin()
                    )
                }
            } finally {
                // When we're done, we reset back to the original classLoader
                Thread.currentThread().contextClassLoader = originalClassLoader
            }

            // handle results, ignore silent snippets
            if (snip.isSilent) snip
            else result?.let {
                when {
                    // replace entire snippet with result
                    snip.isReplace -> snip.copy(code = it.toString())
                    // simply append result
                    else -> snip.copy(result = "// ${it.toString().replace("\n", "\n// ")}")
                }
            } ?: snip
        }
    }

    // Gets the engine cache for a given classpath
    private fun getEngineCache(
        snippets: List<Snippet>,
        classLoader: URLClassLoader?,
        compilerArgs: List<String>
    ): Map<String, ScriptEngine> {
        val cache = engineCache[compilerArgs]
        return if (cache == null) { // create a new engine
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

    /*
     * Creates a **new** [URLClassLoader] **without** a parent.
     * This [URLClassLoader] has references to all the dependencies it needs to run [ScriptEngine],
     * and it holds all the user desired dependencies so we can evaluate code with user dependencies.
     */
    private fun classLoader(compilerArgs: List<String>): URLClassLoader? =
        Engine::class.java.classLoader
            .let { it as? URLClassLoader }
            ?.let {
                URLClassLoader(
                    (jss233Classpath + compilerArgs.map(::URL)).toTypedArray(),
                    null // Decouple from parent ClassLoader
                )
            }
}
