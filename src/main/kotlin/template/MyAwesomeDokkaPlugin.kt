package template

import java.io.PrintWriter
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.Extension
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

class MyAwesomeDokkaPlugin : DokkaPlugin() {
    val dokkaBasePlugin by lazy { plugin<DokkaBase>() }
    val extension by extending {
        dokkaBasePlugin.preMergeDocumentableTransformer providing  { Checker() }
    }
}

class Checker() : PreMergeDocumentableTransformer {

    // Could we optimise with `suspend` and running in parallel?
    override fun invoke(modules: List<DModule>): List<DModule> =
        modules.also {
            val allSnippets = modules.flatMap { module ->
                module.packages.flatMap { `package` ->
                    `package`.children.flatMap { documentable ->
                        documentable.documentation.values.flatMap { node ->
                            node.children.flatMap { tagWrapper ->
                                tagWrapper.children.mapNotNull { docTag ->
                                    (docTag as? CodeBlock)?.let { code ->
                                        code.params["lang"]?.let { fence ->
                                            fenceRegexStart.matchEntire(fence)?.let { match ->
                                                docTag.asStringOrNull()?.let { rawCode ->
                                                    val lang = match.groupValues[1].trim()
                                                    Snippet(fence, lang, rawCode, null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            compileCode(allSnippets, emptyList())

            println(allSnippets)
        }
}

fun CodeBlock.asStringOrNull(): String? =
    buildString {
        children.forEach { tag ->
            when (tag) {
                is Text -> append(tag.body)
                Br -> append("\n")
                else -> return null
            }
        }
    }

public val fenceRegexStart = "(.*):ank.*".toRegex()

public const val AnkBlock: String = ":ank"
public const val AnkSilentBlock: String = ":ank:silent"
public const val AnkReplaceBlock: String = ":ank:replace"
public const val AnkOutFileBlock: String = ":ank:outFile"
public const val AnkPlayground: String = ":ank:playground"
public const val AnkFailBlock: String = ":ank:fail"
public const val AnkPlaygroundExtension: String = ":ank:playground:extension"

public data class Snippet(
    val fence: String,
    val lang: String,
    val code: String,
    val result: String?
) {
    val isSilent: Boolean = fence.contains(AnkSilentBlock)
    val isReplace: Boolean = fence.contains(AnkReplaceBlock)
//    val isOutFile: Boolean = fence.contains(AnkOutFileBlock)
    val isFail: Boolean = fence.contains(AnkFailBlock)
    val isPlayground: Boolean = fence.contains(AnkPlayground)
    val isPlaygroundExtension: Boolean = fence.contains(AnkPlaygroundExtension)
}


private val engineCache: ConcurrentMap<List<String>, Map<String, ScriptEngine>> = ConcurrentHashMap()

public val extensionMappings: Map<String, String> = mapOf(
    "java" to "java",
    "kotlin" to "kts"
)

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

public fun compileCode(
    snippets: List<Snippet>,
    compilerArgs: List<String>
): List<Snippet> {
    val engine = getEngineCache(snippets, compilerArgs)
    // run each snipped and handle its result
    return snippets.mapIndexed { i, snip ->
        val result = try {
            if (snip.isPlaygroundExtension) ""
            else engine.getOrElse(snip.lang) {
                throw CompilationException(
//                    path = snippets.first,
                    snippet = snip,
                    underlying = IllegalStateException("No engine configured for `${snip.lang}`"),
                    msg = colored(ANSI_RED, "ΛNK compilation failed [ snippets.first ]")
                )
            }.eval(snip.code)
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
        else {
            val resultString: String? = result?.let {
                when {
                    // replace entire snippet with result
                    snip.isReplace -> it.toString()
                    snip.isPlaygroundExtension -> it.toString()
                    // write result to a new file
//                    snip.isOutFile -> {
//                        val fileName = snip.fence.lines()[0].substringAfter("(").substringBefore(")")
//                        val dir = snippets.first.parent
//                        Files.write(dir.resolve(fileName), result.toString().toByteArray())
//                        ""
//                    }
                    // simply append result
                    else -> "// ${it.toString().replace("\n", "\n// ")}"
                }
            }
            snip.copy(result = resultString)
        }
    }
}

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

public const val ANSI_RESET: String = "\u001B[0m"
public const val ANSI_BLACK: String = "\u001B[30m"
public const val ANSI_RED: String = "\u001B[31m"
public const val ANSI_GREEN: String = "\u001B[32m"
public const val ANSI_YELLOW: String = "\u001B[33m"
public const val ANSI_BLUE: String = "\u001B[34m"
public const val ANSI_PURPLE: String = "\u001B[35m"
public const val ANSI_CYAN: String = "\u001B[36m"
public const val ANSI_WHITE: String = "\u001B[37m"

public fun colored(color: String, message: String): String =
    "$color$message$ANSI_RESET"

public val AnkHeader: String =
    """
            |      :::     ::::    ::: :::    :::
            |    :+: :+:   :+:+:   :+: :+:   :+:
            |   +:+   +:+  :+:+:+  +:+ +:+  +:+
            |  +#+     ++: +#+ +:+ +#+ +#++:++
            |  +#+     +#+ +#+  +#+#+# +#+  +#+
            |  #+#     #+# #+#   #+#+# #+#   #+#
            |  ###     ### ###    #### ###    ###
            """.trimMargin()