package com.avagr.crypt;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        final EncBatch resbatch = (EncBatch) intent.getExtras().getSerializable("Results");
        TextView resview = findViewById(R.id.resultView);
        resview.setText(resbatch.getText());
        resview.setMovementMethod(new ScrollingMovementMethod());
        TextView keyview = findViewById(R.id.keyView);
        keyview.setText(resbatch.getKey());
        ImageButton share = findViewById(R.id.shareButton);
        ImageButton save = findViewById(R.id.saveButton);
        registerForContextMenu(findViewById(R.id.resultView));
        final DatabaseHelper dbhelper = new DatabaseHelper(this);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, resbatch.getText());
                startActivity(Intent.createChooser(i,"Share the result via"));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this, R.style.CustomDialog);
                builder.setTitle(getString(R.string.namedb));
                final EditText input = new EditText(ResultActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues cv = new ContentValues();
                        String dbtitle = input.getText().toString();
                        cv.put("title", dbtitle);
                        cv.put("keytext", resbatch.getKey());
                        cv.put("enctext", resbatch.getText());
                        cv.put("cipher", resbatch.getCip());
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dformat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                        cv.put("time", dformat.format(calendar.getTime()));
                        SQLiteDatabase db = dbhelper.getWritableDatabase();
                        db.insert("encres", null, cv);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
    }
}
