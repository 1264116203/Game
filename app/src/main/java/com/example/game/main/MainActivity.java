package com.example.game.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.game.R;
import com.example.game.utiles.DBHelper;
import com.example.game.utiles.Dialogshowclass;
import com.example.game.utiles.MusicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener{
    //准备题目资源
    static String[] ques_array = {
            "android虚拟设备的缩写是AVD",
            "Service中不能执行耗时操作",
            "android 中Intent的作用的是实现应用程序间的数据共享",
            "在android中使用Menu时可能需要重写的方法是：onCreateOptionsMenu()，onOptionsItemSelected()",
            "退出Activity错误的方法是：System.exit()",
            "刘伟荣老师真帅！",
            "Toast没有焦点",
            "Toast只能持续一段时间",
            "query方法，是ContentProvider对象的方法",
            "对于一个已经存在的SharedPreferences对象setting,想向其中存入一个字符串\"person\",setting应该先调用edit()",
    };
    //准备答案数组
    String[] result_array = {"是", "不是", "不是", "是", "不是", "是", "是", "是", "不是", "是"};
    int score;
    int count = 0;
    int count_num = 1;
    int[] random_ques_array = new int[20];
    ListView mListView;
    //判断播放器是否在播放
    int Flag_isplaying;
    TextView play;
    Dialogshowclass mDialogshowclass;
    String singleques;
    String singleanws;
    DBHelper mDBHelper;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        play = findViewById(R.id.bt_player);
        mListView = findViewById(R.id.list_main);
        mListView.setOnItemLongClickListener(this);
        //设置标题
        this.setTitle("一起学习吧");
        //将原始题库存储到数据库
        mDBHelper = new DBHelper(this);
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < ques_array.length; i++) {
            singleques = ques_array[i];
            singleanws = result_array[i];
            contentValues.put("Ques", singleques);
            contentValues.put("Answer", singleanws);
            mDBHelper.insert(contentValues, "Ques_tab");
        }

        //查询数据库的题目表 将数据输入到ListView

        Cursor cursor = mDBHelper.query(this, "Ques_tab");
        String[] from = {"_id", "Ques", "Answer"};
        int[] to = {R.id.item_num, R.id.item_text, R.id.item_answer};
        //使用SimpleAdapter填充ListView
        SimpleCursorAdapter simpleAdapter = new SimpleCursorAdapter(this, R.layout.mainlist_item, cursor, from, to, 1);
        mListView.setAdapter(simpleAdapter);
        mDialogshowclass = new Dialogshowclass(this, ques_array, result_array, count_num);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void start(View view) {
        List list = new ArrayList();
        List set = mDialogshowclass.Random_Num(count_num, ques_array.length, list);
        ArrayList arrayList = new ArrayList<>(set);
        for (int i = 0; i < arrayList.size(); i++) {
            random_ques_array[i] = (int) arrayList.get(i);
        }

        mDialogshowclass.Dialogshow(null, count, score, random_ques_array);
        count = 0;
        score = 0;
    }

    public void score(View view) {
        Intent it_score = new Intent(this, HistoryActivity.class);
        startActivity(it_score);
    }

    public void music(View view) {
        if (Flag_isplaying == 0) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
            Flag_isplaying = 1;
            play.setText("停止");

        } else {
            Intent intent = new Intent(this, MusicService.class);
            stopService(intent);
            Flag_isplaying = 0;
            play.setText("播放");

        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

        mDialogshowclass = new Dialogshowclass(this, ques_array, result_array, count_num);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示！")
                .setMessage("是否删除？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.delete((int) id, "Ques_tab");

                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        Intent it_addques = new Intent(this, AddquesActivity.class);
        startActivity(it_addques);
    }
}
