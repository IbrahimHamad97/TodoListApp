package com.example.todolistproject_java.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.todolistproject_java.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteList extends AppCompatActivity {
    ListView listView;
    ArrayList<HashMap<String, String>> todosList;
    public static final String MyPREFERENCES = "MyFavoritePrefs";
    SharedPreferences sharedPreferences;
    ArrayList<String> JSONObjectsStrings;

    private String userId;
    private String id;
    private String title;
    private String completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        listView = findViewById(R.id.favoriteList);
        todosList = new ArrayList<>();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        try {
            todosList = loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(FavoriteList.this, todosList,
                R.layout.list_item, new String[]{ "userId","id","title","completed"},
                new int[]{R.id.userId, R.id.id, R.id.title, R.id.completed});
        listView.setAdapter(adapter);
    }

    private ArrayList<HashMap<String, String>> loadData() throws JSONException {
        ArrayList<HashMap<String, String>> todosList = new ArrayList<>();
        userId = getIntent().getStringExtra("userId");
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        completed = getIntent().getStringExtra("completed");
        ArrayList<String> JSONObjectsStrings = new ArrayList<>();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONObject todo = new JSONObject();
        try {
            todo.put("userId", userId);
            todo.put("id", id);
            todo.put("title", title);
            todo.put("completed", completed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "loadData: " + todo.toString());
        editor.putString("todo", todo.toString());
        editor.commit();

        String jsonStr = sharedPreferences.getString("todo", null );
        JSONObjectsStrings.add(jsonStr);
        Log.d("TAG", "loadData: " + jsonStr);
        Log.d("TAG", "loadData: " + JSONObjectsStrings.toString());
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
        return todosList;

    }

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
}