package com.example.game.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener, AdapterView.OnItemClickListener {
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
    int cursorlength;
    //使用cursorlength1暂存cursorlength的数据防止被置0
    int cursorlength1;
    int[] random_ques_array = new int[20];
    ListView mListView;
    //判断播放器是否在播放
    int Flag_isplaying;
    TextView play;
    TextView tv_sumques;
    EditText ed_quescount;
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
                Intent intent = new Intent(MainActivity.this, AddquesActivity.class);
                startActivity(intent);
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

        {
            play = findViewById(R.id.bt_player);
            tv_sumques = findViewById(R.id.tv_sumques);
            ed_quescount = findViewById(R.id.ed_quescount);
            mListView = findViewById(R.id.list_main);
            mListView.setOnItemLongClickListener(this);
            mListView.setOnItemClickListener(this);

            //设置标题
            this.setTitle("一起学习吧");
            mDBHelper = new DBHelper(this);
            //使用sharedpreferences判断是否已经加载过数据了
            //向sharedpreferences中存储数据
            SharedPreferences flag = getSharedPreferences("flag", MODE_PRIVATE);
            boolean iscreate = flag.getBoolean("iscreate", false);
            SharedPreferences.Editor edit = flag.edit();
            edit.putBoolean("iscreate", true);
            //完成事务提交
            edit.commit();
            //从sharedpreferences中提取数据
            if (iscreate == false) {
                //将原始题库存储到数据库
                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < ques_array.length; i++) {
                    singleques = ques_array[i];
                    singleanws = result_array[i];
                    contentValues.put("Ques", singleques);
                    contentValues.put("Answer", singleanws);
                    mDBHelper.insert(contentValues, "Ques_tab");
                }
            }
            //查询数据库的题目表 将数据输入到ListView
            reload();
        }
    }

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        getMenuInflater().inflate(R.menu.main, menu);
    //        return true;
    //    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void start(View view) {

        /*
        通过editview传入的参数判断是否为数字
         */
        if (isNumericZidai(ed_quescount.getText().toString())) {
            System.out.println("String.valueOf(ed_quescount.getText()):"+ed_quescount.getText());
            count_num = Integer.parseInt(String.valueOf(ed_quescount.getText()));
            if (count_num <= cursorlength1&&count_num>0) {
                mDialogshowclass = new Dialogshowclass(this, ques_array, result_array, count_num);

                Log.e("count_num=", String.valueOf(count_num));
                List list = new ArrayList();
                List set = mDialogshowclass.Random_Num(count_num, ques_array.length, list);
                ArrayList arrayList = new ArrayList<>(set);
                for (int i = 0; i < arrayList.size(); i++) {
                    random_ques_array[i] = (int) arrayList.get(i);
                }
                mDialogshowclass.Dialogshow(null, count, score, random_ques_array);
                count = 0;
                score = 0;
            } if (count_num>cursorlength1){
                Snackbar.make(play, "数量大于题库总数量！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }else {
                Snackbar.make(play, "测试数目不可小于1！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
        else {
            Snackbar.make(play, "请输入正确数量！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

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
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, final long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("提示！")
                .setMessage("是否删除？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDBHelper.delete((int) id, "Ques_tab");
                        reload();
                        Snackbar.make(view, "删除成功！", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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

    /*
    重新加载当前界面
     */
    public void reload() {
        Cursor cursor = mDBHelper.query(MainActivity.this, "Ques_tab");
        //获取cursor的长度
        if (cursor.moveToFirst()) {
            cursorlength++;
            while (cursor.moveToNext()) {
                cursorlength++;
            }
        }
        cursorlength1 = cursorlength;
        tv_sumques.setText("题库数量共" + cursorlength);
        //初始化题目数量
        cursorlength = 0;

        Log.e("cursorlength=", cursorlength + "");
        String[] from = {"_id", "Ques", "Answer"};
        int[] to = {R.id.item_num, R.id.item_text, R.id.item_answer};
        //使用SimpleAdapter填充ListView
        SimpleCursorAdapter simpleAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.mainlist_item, cursor, from, to, 1);
        simpleAdapter.notifyDataSetChanged();
        mListView.setAdapter(simpleAdapter);
        mDialogshowclass = new Dialogshowclass(MainActivity.this, ques_array, result_array, count_num);
    }

    //判断传入的字符串是否可以转换为数字
    public static boolean isNumericZidai(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        reload();

    }
}
