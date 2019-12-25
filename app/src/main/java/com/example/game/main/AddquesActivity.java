package com.example.game.main;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.game.R;
import com.example.game.utiles.DBHelper;

public class AddquesActivity extends AppCompatActivity {
Button btnadd;
EditText edtques;
EditText edtansw;
String singleques;
String singleanswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_addques);
        btnadd=findViewById(R.id.btn_add);
        edtques=findViewById(R.id.edt_addques);
        edtansw=findViewById(R.id.edt_addanswer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.second,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }

    public void add(View view) {
        ContentValues contentValues = new ContentValues();
        DBHelper dbHelper = new DBHelper(this);
        singleques= String.valueOf(edtques.getText());
        singleanswer= String.valueOf(edtansw.getText());
        if (singleanswer.equals("是")||singleanswer.equals("不是")){
            contentValues.put("Ques", singleques);
            contentValues.put("Answer", singleanswer);
            dbHelper.insert(contentValues, "Ques_tab");
            Snackbar.make(view, "添加成功！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
//            Toast.makeText(this,"答案取值只能为 是 或 不是！", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "答案取值只能为 是 或 不是！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }
}
