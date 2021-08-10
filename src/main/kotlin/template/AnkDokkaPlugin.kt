package template

//import com.github.nomisrev.engine.Engine
//import com.github.nomisrev.engine.Snippet
//import com.github.nomisrev.engine.fenceRegexStart
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.script.ScriptEngineManager
import kotlin.script.experimental.jvm.util.classPathFromTypicalResourceUrls
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

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

class AnkDokkaPlugin : DokkaPlugin() {
    val dokkaBasePlugin by lazy { plugin<DokkaBase>() }
    val ank by extending {
        dokkaBasePlugin.preMergeDocumentableTransformer providing ::AnkCompiler
    }

    init {
        System.setProperty("kotlin.jsr223.experimental.resolve.dependencies.from.context.classloader", "true")
    }
}

class AnkCompiler(val ctx: DokkaContext) : PreMergeDocumentableTransformer {

    // Could we optimise with `suspend` and running in parallel?
    override fun invoke(modules: List<DModule>): List<DModule> =
        modules.also {
            val classLoader = classLoader().also { println("classLoader: $it") }

            val saveClassLoader = Thread.currentThread().contextClassLoader
            Thread.currentThread().contextClassLoader = classLoader
            println("Old classloader: $saveClassLoader, Thread.currentThread().contextClassLoader = ${Thread.currentThread().contextClassLoader}")

            val manager = ScriptEngineManager(classLoader)
            val engine = manager.getEngineByExtension("kts")

            engine.eval("println(\"HELLO\")")

            Thread.currentThread().contextClassLoader = saveClassLoader

//            val classpath = listOf(
//                File("/Users/simonvergauwen/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib-jdk8/1.5.21/6b3de2a43405a65502728047db37a98a0c7e72f0/kotlin-stdlib-jdk8-1.5.21.jar").toURI()
//                    .toString()
//            ).map { URL(it) }.toTypedArray()
//
//            val classLoader = AnkCompiler::class.java.classLoader
//                .let { it as? URLClassLoader }
//                ?.let {
//                    URLClassLoader(
//                        it.urLs.filter {
//                            it.file.contains("/kotlin-script") ||
//                                    it.file.contains("/kotlin-stdlib") ||
//                                    it.file.contains("/kotlin-reflect") ||
//                                    it.file.contains("/kotlinx-coroutines") ||
//                                    it.file.contains("/kotlin-analysis-compiler")
//                        }.toTypedArray(),
//                        null
//                    )
//                }
//
//            val classLoader2 = AnkCompiler::class.java.classLoader
//                .classPathFromTypicalResourceUrls()
//                .let { files ->
//                    URLClassLoader(
//                        files.filter {
//                            it.absolutePath.contains("/kotlin-script") ||
//                                    it.absolutePath.contains("/kotlin-stdlib") ||
//                                    it.absolutePath.contains("/kotlin-reflect") ||
//                                    it.absolutePath.contains("/kotlinx-coroutines") ||
//                                    it.absolutePath.contains("/kotlin-analysis-compiler")
//                        }.map { it.toURI().toURL() }
//                            .toList()
//                            .toTypedArray(),
//                        null
//                    )
//                }
//
//            println("classLoader: $classLoader, classLoader2: $classLoader2")
//            Thread.currentThread().contextClassLoader = classLoader
//            val manager = ScriptEngineManager(classLoader2)
//            val engine = manager.getEngineByExtension("kts")
//            println("I got engine: $engine")
//
//            engine.eval("println(\"HELLO\")")

//            println("I got factory: ${engine.factory}")
//            engine.eval("println(\"HELLO\")")
//            Engine.compileCode(modules.allSnippets(), emptyList())
        }
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