package com.braindump.md

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        with(findViewById(R.id.toolbar) as Toolbar) {
            setSupportActionBar(this)
        }

        with(findViewById(R.id.fab) as FloatingActionButton) {
            setOnClickListener {
                startActivityForResult(
                    Intent(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setType("application/*"),
                    READ_REQUEST_CODE
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_licenses) {
            startActivity(Intent(this, LicensesActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startActivity(
                Intent(this, MarkdownViewerActivity::class.java).setData(resultData?.data)
            )
        }
    }

    companion object {
        private val READ_REQUEST_CODE = 15
    }
}
