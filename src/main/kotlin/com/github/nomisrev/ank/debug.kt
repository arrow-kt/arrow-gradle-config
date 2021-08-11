package com.github.nomisrev.ank

import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.doc.CodeBlock

// Get all codeBlocks => write some dump version
private fun List<DModule>.allCodeBlocks(): List<CodeBlock> =
    flatMap { module ->
        module.packages.flatMap { `package` ->
            `package`.children.flatMap { documentable ->
                documentable.documentation.values.flatMap { node ->
                    node.children.flatMap { tagWrapper ->
                        tagWrapper.children.mapNotNull { docTag ->
                            (docTag as? CodeBlock)
                        }
                    }
                }
            }
        }
    }

private fun List<DModule>.allSnippets(): List<Snippet> =
    flatMap { module ->
        module.packages.flatMap { `package` ->
            `package`.children.flatMap { documentable ->
                documentable.documentation.values.flatMap { node ->
                    node.children.flatMap { wrapper ->
                        wrapper.children.mapNotNull { docTag ->
                            (docTag as? CodeBlock)?.let { code ->
                                code.params["lang"]?.let { fence ->
                                    fenceRegexStart.matchEntire(fence)?.let { match ->
                                        docTag.asStringOrNull()?.let { rawCode ->
                                            val lang = match.groupValues[1].trim()
                                            val path = SnippetPath(module, `package`, documentable, node, wrapper, code)
                                            Snippet(path, fence, lang, rawCode)
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
