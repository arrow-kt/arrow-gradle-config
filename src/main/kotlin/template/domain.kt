package template

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