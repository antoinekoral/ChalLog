package fr.challenge.challog;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.nfc.tech.NdefFormatable.get;

public class RU extends AppCompatActivity {

    public static final String TAG = RU.class.getSimpleName();

    public static final String ERROR_DETECTED = "No NFC tag detected!";
    public static final String WRITE_SUCCESS = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR = "Error during writing, is the NFC tag close enough to your device?";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag detectedTag;
    Context context;
    Button btnWrite;
    private NfcAdapter mNfcAdapter;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ru);

        context = this;

        text = (TextView) findViewById(R.id.textViewRU);
        //btnWrite = (Button) findViewById(R.id.button);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected,tagDetected,ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if(mNfcAdapter!= null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter!= null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        detectedTag = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);

        Log.d(TAG, "onNewIntent: " + intent.getAction());

        if (detectedTag != null) {
            Toast.makeText(this, getString(R.string.message_tag_detected), Toast.LENGTH_SHORT).show();
            byte[] list = detectedTag.getId();
            String message = "";
            for (int i=0;i<list.length;i++) {
                message += list[i] + ";";
            }
            Log.d(TAG,"Réponse : " + message);
            text.setText(message);
            //readFromTag(getIntent());
        }
    }

    public void readFromTag(Intent intent){

        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Parcelable[] rawMessages =

                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {

                NdefMessage[] messages = new NdefMessage[rawMessages.length];

                for (int i = 0; i < rawMessages.length; i++) {

                    messages[i] = (NdefMessage) rawMessages[i];

                }

                text.setText(messages.toString());

            } else {
                Toast.makeText(this, "DID'NT WORK.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "DID'NT WORK.", Toast.LENGTH_SHORT).show();
        }
    }

    public void bipAuthorise(View view) throws IOException, JSONException {

        RelativeLayout monLayout = (RelativeLayout) findViewById(R.id.gestionRU);
        monLayout.setBackgroundColor(Color.GREEN);

        //String prenom = "Prenom";
        //String nom = "Nom";
        //text.setText(prenom +" "+ nom);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url ="http://challenge-2016.eclair.ec-lyon.fr/appli2017/api/users/";

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response

                        try {

                            JSONArray array = response.getJSONArray("data");
                            String rep = "";

                            for(int i = 0; i<array.length(); i++) {
                                JSONObject rec = array.getJSONObject(i);
                                rep += rec.getString("nom") + "-";
                            }

                            text.setText(rep);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("Error.Response", error);
                        text.setText("This didn't work!");
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);

    }

    public void bipNonAuthorise(View view) {

        RelativeLayout monLayout = (RelativeLayout) findViewById(R.id.gestionRU);
        monLayout.setBackgroundColor(Color.RED);

        Button button = (Button) findViewById(R.id.button2);
        button.setVisibility(View.VISIBLE);

        final TextView text = (TextView) findViewById(R.id.textViewRU);
        text.setText("");

    }

    public void forcerPassage(View view) throws IOException, JSONException {

        Button button = (Button) findViewById(R.id.button2);
        button.setVisibility(View.INVISIBLE);

        bipAuthorise(view);

    }
}
