package com.braindump.md

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.sufficientlysecure.htmltextview.HtmlTextView

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MarkdownViewerActivity : AppCompatActivity() {

    private var parseMarkdownTask: ParseMarkdownTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_viewer)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.data
        if (data == null) {
            displayErrorAndExit(resources.getString(R.string.error_no_markdown_received))
            return
        }

        val fileName = fileName(data)?.apply {
            actionBar?.title = this
        }

        if (fileName?.contains(markdownExtension) ?: false) {
            parseMarkdownTask = ParseMarkdownTask().apply {
                execute(data)
            }
        } else {
            displayErrorAndExit(resources.getString(R.string.error_not_a_markdown_file))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parseMarkdownTask?.cancel(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayErrorAndExit(error: String) {
        Snackbar
            .make(findViewById(R.id.container_markdown_viewer), error, Snackbar.LENGTH_SHORT)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    finish()
                }
            })
            .show()
    }

    private fun fileName(uri: Uri): String? =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)) else null
        }

    private inner class ParseMarkdownTask : AsyncTask<Uri, Void, String?>() {
        override fun doInBackground(vararg params: Uri): String? {
            var markdown: String? = null
            try {
                contentResolver.openInputStream(params[0])?.use { inputStream ->
                    InputStreamReader(inputStream).use { inputStreamReader ->
                        BufferedReader(inputStreamReader).use { reader ->
                            val parser = Parser.builder().build()
                            val renderer = HtmlRenderer.builder().build()
                            markdown = renderer.render(parser.parseReader(reader))
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return markdown
        }

        override fun onPostExecute(markdown: String?) {
            if (markdown != null) {
                val htmlTextView = findViewById(R.id.rendered_markdown) as HtmlTextView
                htmlTextView.setHtml(markdown)
            } else {
                displayErrorAndExit(resources.getString(R.string.error_parsing_markdown_file))
            }
        }
    }

    companion object {
        private val markdownExtension = """^.*\.(?:md|markdown|mdown)(?:\.txt)?$""".toRegex(RegexOption.IGNORE_CASE)
    }
}
