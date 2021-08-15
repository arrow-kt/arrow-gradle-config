package com.github.nomisrev.ank

import arrow.fx.coroutines.parTraverse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

class AnkDokkaPlugin : DokkaPlugin() {
    val dokkaBasePlugin by lazy { plugin<DokkaBase>() }
    val ank by extending {
        dokkaBasePlugin.preMergeDocumentableTransformer providing ::AnkCompiler
    }
}

/**
 * => Properly log errors through DokkaLogger
 */
private class AnkCompiler(private val ctx: DokkaContext) : PreMergeDocumentableTransformer {

    override fun invoke(modules: List<DModule>): List<DModule> = runBlocking(Dispatchers.Default) {
        ctx.logger.warn(colored(ANSI_PURPLE, "Λnk Dokka Plugin is running"))

        // Shall we process all packages in parallel??
        modules.parTraverse { module ->
            val classpath = module.sourceSets.firstOrNull()?.classpath.orEmpty()
                .map { it.toURI().toURL().toString() }

            Engine.createEngine(classpath).use { engine ->
                val packages =
                    module.packages.parTraverseCodeBlock(module) { module, `package`, documentable, node, wrapper, codeBlock ->
                        Snippet(module, `package`, documentable, node, wrapper, codeBlock)?.let {
                            Engine.compileCode(engine, it)
                        }?.toCodeBlock()
                    }

                module.copy(packages = packages)
            }

        }.also { Engine.printAndCloseTestEnivorment().let(::println) }
    }
}
