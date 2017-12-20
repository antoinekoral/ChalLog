package fr.challenge.challog;

import android.graphics.Color;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RU extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ru);
    }

    public void bipAuthorise(View view) throws IOException, JSONException {

        RelativeLayout monLayout = (RelativeLayout) findViewById(R.id.gestionRU);
        monLayout.setBackgroundColor(Color.GREEN);

        final TextView text = (TextView) findViewById(R.id.textViewRU);
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
