package arrow.ank

import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.Text

public const val ANSI_RESET: String = "\u001B[0m"
public const val ANSI_BLACK: String = "\u001B[30m"
public const val ANSI_RED: String = "\u001B[31m"
public const val ANSI_GREEN: String = "\u001B[32m"
public const val ANSI_YELLOW: String = "\u001B[33m"
public const val ANSI_BLUE: String = "\u001B[34m"
public const val ANSI_PURPLE: String = "\u001B[35m"
public const val ANSI_CYAN: String = "\u001B[36m"
public const val ANSI_WHITE: String = "\u001B[37m"

public fun colored(color: String, message: String): String = "$color$message$ANSI_RESET"

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

fun CodeBlock.asStringOrNull(): String? = buildString {
  children.forEach { tag ->
    when (tag) {
      is Text -> append(tag.body)
      Br -> append("\n")
      else -> return null
    }
  }
}
