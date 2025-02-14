package com.avagr.crypt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;

import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncodeActivity extends AppCompatActivity {

    String cryptType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        final String[] encOptions = new String[] {getString(R.string.encnone), getString(R.string.enccaesar), getString(R.string.encvig), getString(R.string.encaes), getString(R.string.encblow)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, encOptions);
        Spinner encSpinner = (Spinner) findViewById(R.id.enc_spinner);
        encSpinner.setAdapter(adapter);
        encSpinner.setPrompt(getString(R.string.chooseenc));
        final EditText keyinput = findViewById(R.id.keyText);
        final EditText input = findViewById(R.id.editText);
        final ImageButton infoButton = findViewById(R.id.infoButton);
        infoButton.setVisibility(View.GONE);
        encSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: cryptType = "None";
                            infoButton.setVisibility(View.GONE);
                            break;
                    case 1: cryptType = "Caesar";
                            infoButton.setVisibility(View.VISIBLE);
                            keyinput.setInputType(InputType.TYPE_CLASS_NUMBER);
                            keyinput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                            break;
                    case 2: cryptType = "Vigenere";
                            infoButton.setVisibility(View.VISIBLE);
                            keyinput.setInputType(InputType.TYPE_CLASS_TEXT);
                            keyinput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(35)});
                            break;
                    case 3: cryptType = "AES_128";
                            infoButton.setVisibility(View.VISIBLE);
                            keyinput.setInputType(InputType.TYPE_CLASS_TEXT);
                            keyinput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(16)});
                            break;
                    case 4: cryptType = "Blowfish";
                            infoButton.setVisibility(View.VISIBLE);
                            keyinput.setInputType(InputType.TYPE_CLASS_TEXT);
                            keyinput.setFilters(new InputFilter[] {new InputFilter.LengthFilter(35)});
                            break;
                    default: cryptType = "None";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                infoWindow(cryptType);
            }});
        Button startenc = findViewById(R.id.startenc);
        startenc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String inputtext = input.getText().toString();
                String keytext = keyinput.getText().toString();
                if (inputtext.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.notext, Toast.LENGTH_SHORT).show();
                } else {
                    switch (cryptType) {
                        case "None":
                            Toast.makeText(getApplicationContext(), R.string.nocipher, Toast.LENGTH_SHORT).show();
                            break;
                        case "Caesar":
                            Caesar caesar = new Caesar(inputtext, keytext);
                            EncBatch resbatchC = caesar.getCaesarText();
                            Intent intentC = new Intent(EncodeActivity.this, ResultActivity.class);
                            intentC.putExtra("Results", resbatchC);
                            startActivity(intentC);
                            break;

                        case "Vigenere":
                            keytext = keytext.replaceAll("[^\\w]", "").replaceAll("[0-9]", "");
                            Vigenere vigenere = new Vigenere(inputtext, keytext);
                            EncBatch resbatchV = vigenere.getVigText();
                            Intent intentV = new Intent(EncodeActivity.this, ResultActivity.class);
                            intentV.putExtra("Results", resbatchV);
                            startActivity(intentV);
                            break;

                        case "AES_128":
                            try {
                                if (0 < keytext.length() && keytext.length() < 16) {
                                    Toast.makeText(getApplicationContext(), R.string.wronglength, Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                if (keytext.equals("")) {
                                    keytext = getKeyString();
                                }
                                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                                SecretKey skey = new SecretKeySpec(keytext.getBytes(), "AES");
                                cipher.init(Cipher.ENCRYPT_MODE, skey);
                                byte[] encrypted = cipher.doFinal(inputtext.getBytes());
                                EncBatch resbatchAES = new EncBatch(new String(Base64.encodeBase64(encrypted)), keytext, cryptType);
                                Intent intentAES = new Intent(EncodeActivity.this, ResultActivity.class);
                                intentAES.putExtra("Results", resbatchAES);
                                startActivity(intentAES);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case "Blowfish":
                            try {
                                if (keytext.equals("")) {
                                    keytext = getKeyString();
                                }
                                Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
                                SecretKey skey = new SecretKeySpec(keytext.getBytes(), "Blowfish");
                                cipher.init(Cipher.ENCRYPT_MODE, skey);
                                byte[] encrypted = cipher.doFinal(inputtext.getBytes());
                                EncBatch resbatchBF = new EncBatch(new String(Base64.encodeBase64(encrypted)), keytext, cryptType);
                                Intent intentBF = new Intent(EncodeActivity.this, ResultActivity.class);
                                intentBF.putExtra("Results", resbatchBF);
                                startActivity(intentBF);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                    }
                }
            }
        });
    }


    public static String getKeyString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder key = new StringBuilder();
        Random random = new Random();
        while (key.length() < 16) {
            int index = (int) (random.nextFloat() * chars.length());
            key.append(chars.charAt(index));
        }
        String keyStr = key.toString();
        return keyStr;
    }

     public void infoWindow(String type) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.infowindow, null);
        String head = "";
        String body = "";
        String source = getString(R.string.source);
        String url = "";
        switch (type) {
            case "Caesar": head = getString(R.string.enccaesar);
                body = getString(R.string.caesarinfo);
                url = getString(R.string.caesarurl);
                break;
            case "Vigenere": head = getString(R.string.encvig);
                body = getString(R.string.viginfo);
                url = getString(R.string.vigurl);
                break;
            case "AES_128": head = getString(R.string.encaes);
                body = getString(R.string.aesinfo);
                url = getString(R.string.aesurl);
                break;
            case "Blowfish": head = getString(R.string.encblow);
                body = getString(R.string.bfinfo);
                url = getString(R.string.bfurl);
                break;

        }
        ((TextView)layout.findViewById(R.id.infoTitle)).setText(head);
        ((TextView)layout.findViewById(R.id.infoBody)).setText(body);
        ((TextView)layout.findViewById(R.id.linkView)).setText(source);
        DecodeActivity.addLink((TextView)layout.findViewById(R.id.linkView), source, url);
        float density = getResources().getDisplayMetrics().density;
        final PopupWindow iw = new PopupWindow(layout, (int)density*500, (int)density*700, true);
        iw.showAtLocation(layout, Gravity.CENTER, 0,0);
    }
}
