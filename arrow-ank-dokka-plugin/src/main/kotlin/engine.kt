package arrow.ank

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.jetbrains.kotlin.utils.ifEmpty

/** Engine which can compile & evaluate code. This [Engine] is optimised to split the */
object Engine {

  private val jss233Classpath: List<URL>

  init { // This is optional and experimental, but may save some memory.
    System.setProperty(
      "kotlin.jsr223.experimental.resolve.dependencies.from.context.classloader",
      "true"
    )

    val loader = Engine.javaClass.classLoader
    jss233Classpath =
      loader
        .getResourceAsStream("jsr223/list")
        ?.bufferedReader()
        ?.useLines { paths: Sequence<String> ->
          paths
            .mapNotNull {
              val jar =
                File.createTempFile(it.removeSuffix(".jar"), ".jar").also(File::deleteOnExit)
              loader.getResourceAsStream("jsr223/$it").use { input ->
                Files.copy(input, jar.toPath(), REPLACE_EXISTING)
              }
              jar.toURI().toURL()
            }
            .toList()
        }
        .orEmpty()
        .ifEmpty { throw RuntimeException("JS223 Classpath not found. Incorrect build.") }
  }

  private val enviroment = TestEnviroment()
  fun testReport(): String? = enviroment.testReport()

  // We need to communicate between two different classspaths, so we don't share types
  // Use String text to communicate beteween processes
  private val seperator = "@@@@@@@@@@@@@@@@@@@@@@@"

  fun String.decode(): List<TestResult> =
    split(",").flatMap {
      val (state, name, res) =
        it.split(seperator).takeIf { it.size == 3 } ?: return@flatMap emptyList()
      when (state) {
        "SUCCESS" -> Pair(name, Result.success(res))
        else -> Pair(name, Result.failure(AssertionError(res)))
      }.let(::listOf)
    }

  /**
   * Compiles a [List] of [Snippet]s for a given `classpath`. The classpath should be formatted as
   * `file://...` with the URI to the local jars.
   *
   * From Dokka we can simply use the `List<File>` classpath reference of a given Module, otherwise
   * you can programmatically access the ClassPath or load/pass it during build time. Gradle knows
   * the classpath, and can pass it as an argument to the program.
   */
  fun engine(classpath: List<URL>): Resource<ScriptEngine> =
    (resource {
        val classLoader = classLoader(classpath)

        // We need to get the original contextClassLoader which Dokka uses to run, and store it
        // Then we need to set the contextClassloader so the engine can correctly define the
        // compilation classpath
        // We do this **whilst** decoupling the classLoader for the ScriptEngine with the
        // classLoader of Dokka.
        // This is because Dokka shadows some of the Kotlin compiler dependencies, having them mixed
        // results in incorrect state
        val originalClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = classLoader
        val manager = ScriptEngineManager(classLoader)
        val engine =
          manager.getEngineByExtension("kts").apply {
            setBindings(createBindings(), ScriptContext.ENGINE_SCOPE)
            eval(testPrelude) // Setup testing prelude
          }
        Pair(engine, originalClassLoader)
      } release
        { (engine, originalClassLoader) ->
          val result = engine.eval("enviroment.encode()") // Get test enviroment results
          if (result is String) enviroment.insert(result.decode())
          // When we're done, we reset back to the original classLoader
          Thread.currentThread().contextClassLoader = originalClassLoader
        })
      .map { (engine, _) -> engine }

  /*
   * Creates a **new** [URLClassLoader] **without** a parent.
   * This [URLClassLoader] has references to all the dependencies it needs to run [ScriptEngine],
   * and it holds all the user desired dependencies so we can evaluate code with user dependencies.
   */
  private fun classLoader(compilerArgs: List<URL>): URLClassLoader? =
    URLClassLoader(
      (jss233Classpath + compilerArgs).toTypedArray(),
      getParentClassLoader() // Decouple from parent ClassLoader
    )

  private fun getParentClassLoader(): ClassLoader? {
    val version = System.getProperty("java.specification.version")?.toDoubleOrNull()
    return if (version != null && version > 1.8) {
      try {
        ClassLoader::class.java.getMethod("getPlatformClassLoader").invoke(null) as? ClassLoader
      } catch (e: Exception) {
        println(e)
        null
      }
    } else {
      null
    }
  }

  private val testPrelude: String =
    """
      | import kotlinx.coroutines.runBlocking
      | 
      | val enviroment = ArrayList<Pair<String, Result<Any?>>>()
      | 
      | fun test(name: String, f: suspend () -> Any?) =
      |     runBlocking<Unit> {
      |         val result = Pair(name, runCatching { f() })
      |         enviroment.add(result)
      |     }
      |     
      | val seperator = "$seperator"
      | 
      | fun List<Pair<String, Result<Any?>>>.encode(): String =
      |     joinToString(separator = ",") { (name, result) ->
      |         result.fold(
      |             { "SUCCESS${'$'}seperator${'$'}name${'$'}seperator${'$'}it" },
      |             { "FAILURE${'$'}seperator${'$'}name${'$'}seperator${'$'}{it.message}" })
      |     }
    """.trimMargin()
}

/** Evaluates a Snippet and returns it as [Snippet] */
public fun ScriptEngine.eval(snip: Snippet): Snippet {
  val result =
    try {
      eval(snip.code)
    } catch (e: Exception) {
      if (snip.isFail) {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        snip.copy(result = sw.toString())
      } else {
        throw CompilationException(
          snip,
          e,
          msg =
            "\n" +
              """
                    | ${snip.path.prettyPrint()}
                    |
                    |```
                    |${snip.code}
                    |```
                    |${colored(ANSI_RED, e.localizedMessage)}
                    """.trimMargin()
        )
      }
    }

  return when {
    result == null || snip.isSilent || snip.isFail -> snip
    snip.isReplace -> snip.copy(code = result.toString())
    else -> snip.copy(code = snip.code + "\n" + "// ${result.toString().replace("\n", "\n// ")}")
  }
}
