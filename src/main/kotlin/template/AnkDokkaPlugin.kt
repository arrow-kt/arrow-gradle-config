package template

//import com.github.nomisrev.engine.Engine
//import com.github.nomisrev.engine.Snippet
//import com.github.nomisrev.engine.fenceRegexStart
import java.net.URLClassLoader
import javax.script.ScriptEngineManager
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

class AnkDokkaPlugin : DokkaPlugin() {
    val dokkaBasePlugin by lazy { plugin<DokkaBase>() }
    val ank by extending {
        dokkaBasePlugin.preMergeDocumentableTransformer providing ::AnkCompiler
    }

    init { // This is optional and experimental, but may save some memory.
        System.setProperty("kotlin.jsr223.experimental.resolve.dependencies.from.context.classloader", "true")
    }
}

class AnkCompiler(val ctx: DokkaContext) : PreMergeDocumentableTransformer {

    // Could we optimise with `suspend` and running in parallel?
    override fun invoke(modules: List<DModule>): List<DModule> =
        modules.also {
            val classLoader = classLoader()

            // We need to get the original contextClassLoader which Dokka uses to run, and store it
            // Then we need to set the contextClassloader so the engine can correctly define the compilation classpath
            // We do this **whilst** decoupling the classLoader for the ScriptEngine with the classLoader of Dokka.
            // This is because Dokka shadows some of the Kotlin compiler dependencies, having them mixed results in incorrect state
            val originalClassLoader = Thread.currentThread().contextClassLoader
            Thread.currentThread().contextClassLoader = classLoader

            ScriptEngineManager(classLoader)
                .getEngineByExtension("kts")
                .eval("println(\"HELLO\")")

            // When we're done, we reset back to the original classLoader
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
}

/**
 * Creates a **new** [URLClassLoader] **without** a parent.
 * This [URLClassLoader] has references to all the depedencies it needs to run [ScriptEngine],
 * and it holds all the user desired dependencies so we can evaluate code with user dependencies.
 */
fun classLoader(): URLClassLoader? =
    Reference::class.java.classLoader
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

private fun CodeBlock.asStringOrNull(): String? =
    buildString {
        children.forEach { tag ->
            when (tag) {
                is Text -> append(tag.body)
                Br -> append("\n")
                else -> return null
            }
        }
    }

private fun List<DModule>.allSnippets(): List<Snippet> =
    flatMap { module ->
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
    val isFail: Boolean = fence.contains(AnkFailBlock)
    val isPlayground: Boolean = fence.contains(AnkPlayground)
}