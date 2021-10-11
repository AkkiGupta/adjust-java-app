package com.adjust.task.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adjust.task.state_machine.OnPostDone;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class PostSecondsJson extends AsyncTask<String, Void, String> {

    Context myContext;
    OnPostDone mListener;
    int responseCode;
    int mValToPost;

    public PostSecondsJson(Context context, int val, OnPostDone listener) {
        myContext = context;
        mValToPost = val;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("seconds", mValToPost);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = jObj.toString();
        OutputStream out = null;

        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            out = new BufferedOutputStream(urlConnection.getOutputStream());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_CREATED) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (urlConnection != null) // Make sure the connection is not null.
                urlConnection.disconnect();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        boolean success = false;
        int seconds_val = -1;
        int id = -1;
        if (responseCode == HttpsURLConnection.HTTP_CREATED) {
            success = true;
            try {
                s = s.trim();
                s = s.replace("}\": \"\"","")
                        .replace("\"{\\","")
                        .replace("\\", "");
                JSONObject jObj = new JSONObject(s);
                seconds_val = jObj.getInt("seconds");
                id = jObj.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mListener.requestDone(success, s, seconds_val, id);
    }
}