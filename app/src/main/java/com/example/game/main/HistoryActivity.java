package com.example.game.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.game.R;
import com.example.game.utiles.DBHelper;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    ListView listhistory;
    DBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);

        listhistory = findViewById(R.id.list_history);
        listhistory.setOnItemLongClickListener(this);
        mDBHelper = new DBHelper(this);
        Cursor cursor = mDBHelper.query(this,"Score_tab");
        String[] from = {"_id", "Score", "Date"};
        int[] to = {R.id.tv_id, R.id.tv_score, R.id.tv_time};
        //使用SimpleCursorAdapter填充ListView
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.historylist_item, cursor, from, to, 1);
        listhistory.setAdapter(simpleCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //关于作者界面返回到主界面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        return true;
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示！")
                .setMessage("是否删除？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.delete((int) id, "Score_tab");
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create();
        builder.show();

        return false;
    }
}
