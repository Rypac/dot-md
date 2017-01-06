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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import in.uncod.android.bypass.Bypass;

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
            try {
                String markdown = getStringFromFile(data);
                Bypass bypass = new Bypass();
                CharSequence string = bypass.markdownToSpannable(markdown);
                TextView text = (TextView) findViewById(R.id.rendered_markdown);
                text.setText(string);
            } catch (Exception e) {
                View view = findViewById(R.id.coordinator_markdown_viewer);
                Snackbar.make(view, "Unable to parse markdown.", Snackbar.LENGTH_LONG).show();
            }
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

    private String getStringFromFile(Uri uri) throws Exception {
        InputStream fileInputStream = getContentResolver().openInputStream(uri);
        if (fileInputStream != null) {
            String string = convertStreamToString(fileInputStream);
            fileInputStream.close();
            return string;
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
