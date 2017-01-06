package xyz.devspace.md;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.io.InputStream;
import java.io.InputStreamReader;

public class MarkdownViewerActivity extends AppCompatActivity {

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
        if (data != null) {
            String renderedMarkdown = parseMarkdownFile(data);
            HtmlTextView htmlTextView = (HtmlTextView) findViewById(R.id.rendered_markdown);
            htmlTextView.setHtml(renderedMarkdown);
        } else {
            View view = findViewById(R.id.coordinator_markdown_viewer);
            Snackbar.make(view, "No markdown to view.", Snackbar.LENGTH_LONG).show();
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

    private String parseMarkdownFile(Uri uri) {
        String markdown = getStringFromFile(uri);
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(parser.parse(markdown));
    }

    private String getStringFromFile(Uri uri) {
        try {
            InputStream fileInputStream = getContentResolver().openInputStream(uri);
            if (fileInputStream != null) {
                String string = convertStreamToString(fileInputStream);
                fileInputStream.close();
                return string;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
