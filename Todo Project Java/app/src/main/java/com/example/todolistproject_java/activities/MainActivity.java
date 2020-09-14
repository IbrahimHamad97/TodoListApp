package com.example.todolistproject_java.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.todolistproject_java.HttpHandler;
import com.example.todolistproject_java.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private final String CONNECTIVITY_STATUS = "Connectivity Status: ";
    private final int TODO_LIST_DATA_SIZE = 200;
    ListView listView;
    ArrayList<HashMap<String, String>> todosList;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    ArrayList<String> JSONObjectsStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        todosList = new ArrayList<>();

        //Saving Common Data.
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_APPEND);
        new GetTodos().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    handleListClicks(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Menu for the list and favorite list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.homeMenuItem:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.favMenuItem:
                startActivity(new Intent(this, FavoriteList.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    //When user clicks on a list item.
    private void handleListClicks(int position) throws JSONException {
        JSONObject jsonObj = new JSONObject(JSONObjectsStrings.get(position));

        Intent intent = new Intent(MainActivity.this, TodoDetails.class);
        intent.putExtra("position", position);
        intent.putExtra("userId", jsonObj.getString("userId"));
        intent.putExtra("id", jsonObj.getString("id"));
        intent.putExtra("title", jsonObj.getString("title"));
        intent.putExtra("completed", jsonObj.getString("completed"));
        startActivity(intent);
    }

    //Gets called on view creation to call the API and fill the list.
    private class GetTodos extends AsyncTask<Void, Void, Void> {

        /*Shows some kind of "loading" until data is retrieved
        Can be improved to be a spinner (will check it out later).*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Getting Information",
                    Toast.LENGTH_LONG).show();
        }
        //background thread for the data loading
        @Override
        protected Void doInBackground(Void... arg0) {
            JSONObjectsStrings = getJSONData();
            try {
                for (int i = 0; i < JSONObjectsStrings.size(); i++) {
                    JSONObject jsonObj = new JSONObject(JSONObjectsStrings.get(i));

                    // tmp hash map for single contact
                    HashMap<String, String> todoItem = new HashMap<>();

                    // adding each child node to HashMap key => value
                    todoItem.put("userId",jsonObj.getString("userId"));
                    todoItem.put("id", jsonObj.getString("id"));
                    todoItem.put("title", jsonObj.getString("title"));
                    todoItem.put("completed",jsonObj.getString("completed"));

                    // adding contact to contact list
                    todosList.add(todoItem);
                }

            } catch (final JSONException e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                Log.e(TAG, "" + exceptionAsString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        //Here it is calling the api
        private ArrayList<String> getJSONData() {
            HttpHandler sh = new HttpHandler();
            ArrayList<String> JSONObjectsStrings = new ArrayList<>();
            //SharedPreferences.Editor editor = sharedpreferences.edit();
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null;
            //If we are connected to the internet:
            if (isConnected){
                Log.d(CONNECTIVITY_STATUS,isConnected+"");
                for (int k =1; k <= TODO_LIST_DATA_SIZE; k++){
                    Log.d(CONNECTIVITY_STATUS, "do it: hiii" + k);
                    // Making a request to url and getting response for each object
                    String url = "https://jsonplaceholder.typicode.com/todos/"+k;
                    String jsonStr = sh.makeServiceCall(url);
                    JSONObjectsStrings.add(jsonStr);
                }
            }
            /*If the phone is offline, it reads data from the shared prefs which
            already has the data downloaded in. */
            else {
                Log.d(CONNECTIVITY_STATUS,isConnected+"");
                for (int k =0; k < TODO_LIST_DATA_SIZE; k++){
                    Log.d(CONNECTIVITY_STATUS, "doInBackground: hii");
                    String jsonStr = sharedpreferences.getString(""+k, null );
                    JSONObjectsStrings.add(jsonStr);
                }
            }
            return JSONObjectsStrings;
        }

        //Displays data after loading it.
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, todosList,
                    R.layout.list_item, new String[]{ "userId","id","title","completed"},
                    new int[]{R.id.userId, R.id.id, R.id.title, R.id.completed});
            listView.setAdapter(adapter);
        }
    }
}
