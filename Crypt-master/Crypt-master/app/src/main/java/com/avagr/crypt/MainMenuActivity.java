package com.avagr.crypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button toenc = findViewById(R.id.toenc);
        toenc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EncodeActivity.class);
                startActivity(intent);
            }
        });

        Button todec =findViewById(R.id.todec);
        todec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentdc = new Intent(getApplicationContext(), DecodeActivity.class);
                startActivity(intentdc);
            }
        });
    }

    public void updateList() {
        final DatabaseHelper dbhelper = new DatabaseHelper(this);
        final ArrayList<HashMap<String, String>> entryList = dbhelper.getEntries();
        ListView lv = (ListView) findViewById(R.id.dbview);
        ListAdapter adapter = new SimpleAdapter(MainMenuActivity.this, entryList, R.layout.listrow, new String[]{"title", "enctext", "cipher", "time"},
                new int[]{R.id.viewtitle, R.id.viewenctext, R.id.viewcip, R.id.viewtime});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.entrywindow, null);
                final int tpos = position;
                ((TextView)layout.findViewById(R.id.entrytitle)).setText(entryList.get(position).get("title"));
                ((TextView)layout.findViewById(R.id.entrytext)).setText(entryList.get(position).get("enctext"));
                ((TextView)layout.findViewById(R.id.entrykey)).setText("Key: " + entryList.get(position).get("keytext"));
                ((TextView)layout.findViewById(R.id.entrytime)).setText(entryList.get(position).get("time"));
                ((TextView)layout.findViewById(R.id.entrycip)).setText("Cipher: " + entryList.get(position).get("cipher"));
                float density = getResources().getDisplayMetrics().density;
                final PopupWindow iw = new PopupWindow(layout, (int)density*520, (int)density*800, true);
                iw.showAtLocation(layout, Gravity.CENTER, 0,0);

                ImageButton delete = (ImageButton)layout.findViewById(R.id.deletebutton);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this, R.style.CustomDialog);
                        builder.setTitle(getString(R.string.confdel));
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbhelper.DeleteEntry(entryList.get(tpos).get("title"));
                                iw.dismiss();
                                updateList();
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

                Button encrypt = layout.findViewById(R.id.entryenc);
                encrypt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cryptType = entryList.get(tpos).get("cipher");
                        String keytext = entryList.get(tpos).get("keytext");
                        String inputtext = entryList.get(tpos).get("enctext");
                        switch (cryptType) {
                            case "Caesar":
                                Caesar caesar = new Caesar(inputtext, keytext);
                                EncBatch resbatchC = caesar.getCaesarText();
                                Intent intentC = new Intent(MainMenuActivity.this, ResultActivity.class);
                                intentC.putExtra("Results", resbatchC);
                                startActivity(intentC);
                                break;

                            case "Vigenere":
                                keytext = keytext.replaceAll("[^\\w]", "").replaceAll("[0-9]", "");
                                Vigenere vigenere = new Vigenere(inputtext, keytext);
                                EncBatch resbatchV = vigenere.getVigText();
                                Intent intentV = new Intent(MainMenuActivity.this, ResultActivity.class);
                                intentV.putExtra("Results", resbatchV);
                                startActivity(intentV);
                                break;

                            case "AES_128":
                                try {
                                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                    SecretKey skey = new SecretKeySpec(keytext.getBytes(), "AES");
                                    cipher.init(Cipher.ENCRYPT_MODE, skey);
                                    byte[] encrypted = cipher.doFinal(inputtext.getBytes());
                                    EncBatch resbatchAES = new EncBatch(new String(Base64.encodeBase64(encrypted)), keytext, cryptType);
                                    Intent intentAES = new Intent(MainMenuActivity.this, ResultActivity.class);
                                    intentAES.putExtra("Results", resbatchAES);
                                    startActivity(intentAES);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case "Blowfish":
                                try {
                                    Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
                                    SecretKey skey = new SecretKeySpec(keytext.getBytes(), "Blowfish");
                                    cipher.init(Cipher.ENCRYPT_MODE, skey);
                                    byte[] encrypted = cipher.doFinal(inputtext.getBytes());
                                    EncBatch resbatchBF = new EncBatch(new String(Base64.encodeBase64(encrypted)), keytext, cryptType);
                                    Intent intentBF = new Intent(MainMenuActivity.this, ResultActivity.class);
                                    intentBF.putExtra("Results", resbatchBF);
                                    startActivity(intentBF);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
                Button decrypt = layout.findViewById(R.id.entrydec);
                decrypt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cryptType = entryList.get(tpos).get("cipher");
                        String keytext = entryList.get(tpos).get("keytext");
                        String inputtext = entryList.get(tpos).get("enctext");
                        switch (cryptType) {
                            case "None":
                                Toast.makeText(getApplicationContext(), R.string.nocipher, Toast.LENGTH_SHORT).show();
                                break;
                            case "Caesar":
                                Caesar caesar = new Caesar(inputtext, keytext);
                                EncBatch resbatchC = caesar.getCaesarDec();
                                Intent intentC = new Intent(MainMenuActivity.this, ResultActivity.class);
                                intentC.putExtra("Results", resbatchC);
                                startActivity(intentC);
                                break;

                            case "Vigenere":
                                keytext = keytext.replaceAll("[^\\w]", "").replaceAll("[0-9]", "");
                                Vigenere vigenere = new Vigenere(inputtext, keytext);
                                EncBatch resbatchV = vigenere.getVigDec();
                                Intent intentV = new Intent(MainMenuActivity.this, ResultActivity.class);
                                intentV.putExtra("Results", resbatchV);
                                startActivity(intentV);
                                break;

                            case "AES_128":
                                try {
                                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                    SecretKey skey = new SecretKeySpec(keytext.getBytes(), "AES");
                                    cipher.init(Cipher.DECRYPT_MODE, skey);
                                    byte[] decrypted = cipher.doFinal(Base64.decodeBase64(inputtext.getBytes()));
                                    EncBatch resbatchAES = new EncBatch(new String(decrypted, "UTF-8"), keytext, cryptType);
                                    Intent intentAES = new Intent(MainMenuActivity.this, ResultActivity.class);
                                    intentAES.putExtra("Results", resbatchAES);
                                    startActivity(intentAES);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;

                            case "Blowfish":
                                try {
                                    Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
                                    SecretKey skey = new SecretKeySpec(keytext.getBytes(), "Blowfish");
                                    cipher.init(Cipher.DECRYPT_MODE, skey);
                                    byte[] decrypted = cipher.doFinal(Base64.decodeBase64(inputtext.getBytes()));
                                    EncBatch resbatchBF = new EncBatch(new String(decrypted, "UTF-8"), keytext, cryptType);
                                    Intent intentBF = new Intent(MainMenuActivity.this, ResultActivity.class);
                                    intentBF.putExtra("Results", resbatchBF);
                                    startActivity(intentBF);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

}
