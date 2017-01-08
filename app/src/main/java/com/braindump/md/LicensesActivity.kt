package com.braindump.md

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewManager
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.titleResource
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.nestedScrollView
import org.sufficientlysecure.htmltextview.HtmlTextView

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coordinatorLayout {
            fitsSystemWindows = true
            appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
                toolbar {
                    id = ID_TOOLBAR
                    titleResource = R.string.title_licenses
                    popupTheme = R.style.AppTheme_PopupOverlay
                }.lparams(width = matchParent, height = wrapContent) {
                    scrollFlags = SCROLL_FLAG_SCROLL.or(SCROLL_FLAG_ENTER_ALWAYS)
                }
            }.lparams(width = matchParent, height = wrapContent)
            nestedScrollView {
                htmlTextView {
                    id = ID_LICENSES
                    verticalPadding = dimen(R.dimen.activity_vertical_margin)
                    horizontalPadding = dimen(R.dimen.activity_horizontal_margin)
                }
            }.lparams(width = matchParent, height = matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }

        setSupportActionBar(find(ID_TOOLBAR))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        with(find<HtmlTextView>(ID_LICENSES)) {
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

    companion object {
        private val ID_TOOLBAR = 1000
        private val ID_LICENSES = 1001
    }

    inline fun ViewManager.htmlTextView(theme: Int = 0, init: HtmlTextView.() -> Unit) =
        ankoView(::HtmlTextView, theme, init)
}
