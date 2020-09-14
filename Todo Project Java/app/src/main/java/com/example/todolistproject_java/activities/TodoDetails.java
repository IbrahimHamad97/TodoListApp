package com.example.todolistproject_java.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.todolistproject_java.R;

public class TodoDetails extends AppCompatActivity {
    private TextView userIDTv;
    private TextView IDTv;
    private TextView titleTv;
    private TextView completedTv;
    private Button addBtn;
    private Button backBtn;

    private String userId;
    private String id;
    private String title;
    private String completed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        userIDTv = findViewById(R.id.userIDTv);
        IDTv = findViewById(R.id.IDTv);
        titleTv = findViewById(R.id.titleTv);
        completedTv = findViewById(R.id.completedTv);
        addBtn = findViewById(R.id.addBtn);
        backBtn = findViewById(R.id.goBackhome);

        userId = getIntent().getStringExtra("userId");
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        completed = getIntent().getStringExtra("completed");

        userIDTv.setText(userId);
        IDTv.setText(id);
        titleTv.setText(title);
        completedTv.setText(completed);

        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TodoDetails.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TodoDetails.this, FavoriteList.class);
                intent.putExtra("userId", userId);
                intent.putExtra("id", id);
                intent.putExtra("title", title);
                intent.putExtra("completed", completed);
                startActivity(intent);
            }
        });

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