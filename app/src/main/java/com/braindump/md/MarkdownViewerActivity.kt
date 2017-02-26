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
import android.webkit.WebView
import com.braindump.md.utils.MarkdownRenderer

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MarkdownViewerActivity : AppCompatActivity() {

    private var parseMarkdownTask: ParseMarkdownTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown_viewer)

        with(findViewById(R.id.toolbar) as Toolbar) {
            title = resources.getString(R.string.title_markdown_viewer)
            setSupportActionBar(this)
        }

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

        if (fileName?.contains(markdownExtension) == true) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                            markdown = MarkdownRenderer().render(reader)
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
                with(findViewById(R.id.scrollable_html_text_view) as WebView) {
                    loadDataWithBaseURL("about:blank", markdown, "text/html", "UTF-8", "about:blank")
                }
            } else {
                displayErrorAndExit(resources.getString(R.string.error_parsing_markdown_file))
            }
        }
    }

    companion object {
        private val markdownExtension = """^.*\.(?:md|markdown|mdown)(?:\.txt)?$""".toRegex(RegexOption.IGNORE_CASE)
    }
}
