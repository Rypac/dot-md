package com.braindump.md;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MarkdownViewerActivity extends AppCompatActivity {

    private ParseMarkdownTask parseMarkdownTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null) {
            displayErrorAndExit(getResources().getString(R.string.error_no_markdown_received));
            return;
        }

        String fileName = fileName(data);
        if (fileName != null && actionBar != null) {
            actionBar.setTitle(fileName);
        }

        if (fileName != null && isMarkdownFile(fileName)) {
            parseMarkdownTask = new ParseMarkdownTask();
            parseMarkdownTask.execute(data);
        } else {
            displayErrorAndExit(getResources().getString(R.string.error_not_a_markdown_file));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parseMarkdownTask != null && !parseMarkdownTask.isCancelled()) {
            parseMarkdownTask.cancel(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    private String fileName(@NonNull Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }

        String result = null;
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private boolean isMarkdownFile(@NonNull String fileName) {
        String fileExtension = fileExtension(fileName);
        return fileExtension != null &&
            (fileExtension.equalsIgnoreCase("md") || fileExtension.equalsIgnoreCase("markdown"));
    }

    @Nullable
    private String fileExtension(@NonNull String fileName) {
        int index = fileName.lastIndexOf('.');
        return index > 0 ? fileName.substring(index + 1) : null;
    }

    private void displayErrorAndExit(@NonNull String error) {
        View view = findViewById(R.id.container_markdown_viewer);
        Snackbar
            .make(view, error, Snackbar.LENGTH_SHORT)
            .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    finish();
                }
            })
            .show();
    }

    private class ParseMarkdownTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(@NonNull Uri... params) {
            String markdown = null;
            try (InputStream inputStream = getContentResolver().openInputStream(params[0])) {
                if (inputStream != null) {
                    try (
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inputStreamReader)
                    ) {
                        Parser parser = Parser.builder().build();
                        HtmlRenderer renderer = HtmlRenderer.builder().build();
                        markdown = renderer.render(parser.parseReader(reader));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return markdown;
        }

        protected void onPostExecute(@Nullable String markdown) {
            if (markdown != null) {
                HtmlTextView htmlTextView = (HtmlTextView) findViewById(R.id.rendered_markdown);
                htmlTextView.setHtml(markdown);
            } else {
                displayErrorAndExit(getResources().getString(R.string.error_parsing_markdown_file));
            }
        }
    }
}
