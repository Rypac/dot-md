package com.braindump.md

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import org.sufficientlysecure.htmltextview.HtmlTextView

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)

        with(findViewById(R.id.toolbar) as Toolbar) {
            title = resources.getString(R.string.title_licenses)
            setSupportActionBar(this)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(findViewById(R.id.scrollable_html_text_view) as HtmlTextView) {
            setHtml(R.raw.licenses)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
