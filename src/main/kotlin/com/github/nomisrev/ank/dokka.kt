package com.github.nomisrev.ank

import arrow.fx.coroutines.parTraverse
import java.net.URL
import org.jetbrains.dokka.model.DAnnotation
import org.jetbrains.dokka.model.DClass
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DInterface
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.DObject
import org.jetbrains.dokka.model.DPackage
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.SourceSetDependent
import org.jetbrains.dokka.model.doc.Author
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.CodeInline
import org.jetbrains.dokka.model.doc.Constructor
import org.jetbrains.dokka.model.doc.CustomDocTag
import org.jetbrains.dokka.model.doc.CustomTagWrapper
import org.jetbrains.dokka.model.doc.Deprecated
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.model.doc.Param
import org.jetbrains.dokka.model.doc.Property
import org.jetbrains.dokka.model.doc.Receiver
import org.jetbrains.dokka.model.doc.Return
import org.jetbrains.dokka.model.doc.Sample
import org.jetbrains.dokka.model.doc.See
import org.jetbrains.dokka.model.doc.Since
import org.jetbrains.dokka.model.doc.Suppress
import org.jetbrains.dokka.model.doc.TagWrapper
import org.jetbrains.dokka.model.doc.Throws
import org.jetbrains.dokka.model.doc.Version

fun DModule.classPath(): List<URL> =
        sourceSets.firstOrNull()?.classpath.orEmpty().map { it.toURI().toURL() }

/**
 * This methods gives you all info available for a detected `CodeBlock`.
 * It **does not** filter the list, but if you return a non-null value it will
 * update the existing value in the [DModule]
 */
suspend fun List<DPackage>.parTraverseCodeBlock(
    dModule: DModule,
    transform: suspend (module: DModule, `package`: DPackage, documentable: Documentable, node: DocumentationNode, wrapper: TagWrapper, CodeBlock) -> CodeBlock?
): List<DPackage> = parTraverse { `package` ->
    `package`.copy(
        properties = `package`.properties.map { property ->
            property.copy(documentation = property.documentation.process(dModule, `package`, property, transform))
        },
        functions = `package`.functions.map { function ->
            function.copy(documentation = function.documentation.process(dModule, `package`, function, transform))
        },
        classlikes = `package`.classlikes.map { it.process(dModule, `package`, transform) },
        typealiases = `package`.typealiases.map { typeAlias ->
            typeAlias.copy(documentation = typeAlias.documentation.process(dModule, `package`, typeAlias, transform))
        }
    )
}

private suspend fun DClasslike.process(
    module: DModule,
    `package`: DPackage,
    transform: suspend (module: DModule, `package`: DPackage, documentable: Documentable, node: DocumentationNode, wrapper: TagWrapper, CodeBlock) -> CodeBlock?
): DClasslike =
    when (this) {
        is DClass -> copy(documentation = documentation.process(module, `package`, this, transform))
        is DEnum -> copy(documentation = documentation.process(module, `package`, this, transform))
        is DInterface -> copy(documentation = documentation.process(module, `package`, this, transform))
        is DObject -> copy(documentation = documentation.process(module, `package`, this, transform))
        is DAnnotation -> copy(documentation = documentation.process(module, `package`, this, transform))
    }

private suspend fun SourceSetDependent<DocumentationNode>.process(
    module: DModule,
    `package`: DPackage,
    documentable: Documentable,
    transform: suspend (module: DModule, `package`: DPackage, documentable: Documentable, node: DocumentationNode, wrapper: TagWrapper, code: CodeBlock) -> CodeBlock?
): SourceSetDependent<DocumentationNode> =
    mapValues { (_, node) -> node.process(module, `package`, documentable, node, transform) }

private suspend fun DocumentationNode.process(
    module: DModule,
    `package`: DPackage,
    documentable: Documentable,
    node: DocumentationNode,
    transform: suspend (module: DModule, `package`: DPackage, documentable: Documentable, node: DocumentationNode, wrapper: TagWrapper, code: CodeBlock) -> CodeBlock?
): DocumentationNode =
    copy(children = children.map {
        when (it) {
            is See -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Param -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Throws -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Sample -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Property -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is CustomTagWrapper -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Description -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Author -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Version -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Since -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Return -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Receiver -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Constructor -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Deprecated -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
            is Suppress -> it.copy(root = it.root.process(module, `package`, documentable, node, it, transform))
        }
    })

private suspend fun DocTag.process(
    module: DModule,
    `package`: DPackage,
    documentable: Documentable,
    node: DocumentationNode,
    wrapper: TagWrapper,
    transform: suspend (module: DModule, `package`: DPackage, documentable: Documentable, node: DocumentationNode, wrapper: TagWrapper, code: CodeBlock) -> CodeBlock?
): DocTag =
    when (this) {
        is CodeBlock -> transform(module, `package`, documentable, node, wrapper, this) ?: this
        is CodeInline -> this
        is CustomDocTag -> copy(children = children.map {
            it.process(
                module,
                `package`,
                documentable,
                node,
                wrapper,
                transform
            )
        })
        else -> this
    }
