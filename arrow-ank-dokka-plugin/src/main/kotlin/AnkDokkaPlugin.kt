package arrow.ank

import arrow.fx.coroutines.parTraverse
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

class AnkDokkaPlugin : DokkaPlugin() {
  val dokkaBasePlugin by lazy { plugin<DokkaBase>() }
  val ank by extending { dokkaBasePlugin.preMergeDocumentableTransformer providing ::AnkCompiler }
}

/** => Properly log errors through DokkaLogger */
private class AnkCompiler(private val ctx: DokkaContext) : PreMergeDocumentableTransformer {

  override fun invoke(modules: List<DModule>): List<DModule> =
    runBlocking(Dispatchers.Default) {
      ctx.logger.warn(colored(ANSI_PURPLE, "Λnk Dokka Plugin is running"))

      modules
        .parTraverse { module ->
          val packages =
            module.packages.parTraverseCodeBlock(module) {
              module,
              `package`,
              documentable,
              node,
              wrapper,
              codeBlock ->
              Engine.engine(ankClasspathUrls()).use { engine ->
                Snippet(module, `package`, documentable, node, wrapper, codeBlock)
                  ?.let { engine.eval(it) }
                  ?.toCodeBlock()
              }
            }

          module.copy(packages = packages)
        }
        .also { Engine.testReport()?.let(::println) }
    }

  private fun ankClasspathUrls(): List<URL> =
    ctx.configuration.pluginsClasspath.map { it.toURI().toURL() } +
      ctx.configuration.sourceSets.flatMap { it.classpath.map { it.toURI().toURL() } }.distinct()
}
