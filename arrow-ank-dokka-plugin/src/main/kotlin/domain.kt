package arrow.ank

import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.DPackage
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.model.doc.TagWrapper
import org.jetbrains.dokka.model.doc.Text

public const val AnkBlock: String = ":ank"
public const val AnkSilentBlock: String = ":ank:silent"
public const val AnkReplaceBlock: String = ":ank:replace"
public const val AnkOutFileBlock: String = ":ank:outFile"
public const val AnkPlayground: String = ":ank:playground"
public const val AnkFailBlock: String = ":ank:fail"
public const val AnkPlaygroundExtension: String = ":ank:playground:extension"

public val fenceRegexStart = "(.*)$AnkBlock.*".toRegex()

data class SnippetPath(
  val module: DModule,
  val `package`: DPackage,
  val documentable: Documentable,
  val node: DocumentationNode,
  val tagWrapper: TagWrapper,
  val codeBlock: CodeBlock
) {
  fun prettyPrint(): String =
    """
        Snippet in KDoc of ${documentable.name ?: "<anonymous>"} in ${`package`.packageName} failed.
    """.trimIndent()
}

public data class Snippet(
  val path: SnippetPath,
  val fence: String,
  val lang: String,
  val code: String,
  val result: String? = null
) {
  val isSilent: Boolean = fence.contains(AnkSilentBlock)
  val isReplace: Boolean = fence.contains(AnkReplaceBlock)
  val isFail: Boolean = fence.contains(AnkFailBlock)
  val isPlayground: Boolean = fence.contains(AnkPlayground)

  fun toCodeBlock(): CodeBlock =
    CodeBlock(code.lines().flatMap { listOf(Text(it), Br) }, mapOf("lang" to fence))
}

fun Snippet(
  module: DModule,
  `package`: DPackage,
  documentable: Documentable,
  node: DocumentationNode,
  wrapper: TagWrapper,
  code: CodeBlock
): Snippet? =
  code.params["lang"]?.let { fence ->
    fenceRegexStart.matchEntire(fence)?.let { match ->
      code.asStringOrNull()?.let { rawCode ->
        val lang = match.groupValues[1].trim()
        val path = SnippetPath(module, `package`, documentable, node, wrapper, code)
        Snippet(path, fence, lang, rawCode)
      }
    }
  }
