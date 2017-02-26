package com.braindump.md.utils

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.Reader

class MarkdownRenderer {

    private val renderer = HtmlRenderer.builder().build()
    private val parser = Parser
        .builder()
        .extensions(listOf(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            AutolinkExtension.create(),
            InsExtension.create()
        ))
        .build()

    fun render(markdown: Reader): String =
        injectCss(renderer.render(parser.parseReader(markdown)))

    fun render(markdown: String): String =
        injectCss(renderer.render(parser.parse(markdown)))

    private fun injectCss(markdown: String): String {
        val css = """
            html {
                box-sizing: border-box;
            }
            body {
                margin: 0 auto;
                max-width: 46em;
                line-height: 1.5em;
                padding: 1em;
                color: #333;
            }
            pre {
                padding: 1em;
                white-space: pre;
                overflow-x: auto;
                background-color: #f7f7f7;
            }
        """
        return "<head><style type='text/css'>$css</style></head>$markdown"
    }
}
