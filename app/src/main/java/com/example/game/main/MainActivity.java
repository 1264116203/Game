package com.example.game.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, View.OnClickListener {
    //准备题目资源
    static String[] ques_array = {
            "android虚拟设备的缩写是AVD",
            "Service中不能执行耗时操作",
            "android 中Intent的作用的是实现应用程序间的数据共享",
            "在android中使用Menu时可能需要重写的方法是：onCreateOptionsMenu()，onOptionsItemSelected()",
            "退出Activity的方法是：System.exit()",
            "Toast没有焦点",
            "Toast只能持续一段时间",
            "query方法，是ContentProvider对象的方法",
            "对于一个已经存在的SharedPreferences对象setting,想向其中存入一个字符串\"person\",setting应该先调用edit()",
            "刘老师真帅！",
            "SharedPreference用于本地存储大量数据",
            "string.xml用来存储全局字符串",
            "调用onDestory()表示Activity即将被销毁",
            "Intent只能用来跳转activity",
            "继承、多态、封装是JAVA的灵魂",
            "onCreateView()是用来 加载Fragment布局并绑定布局文件的",
            "new Thread()可以创建子线程",
            "Thread.sleep(10)表示该线程休眠10秒",
            "MediaPlayer可以打开.mp3 .mp4 .png等格式文件",
            "static关键字用来定义常量"
    };
    //准备答案数组
    String[] result_array = {"是", "不是", "不是", "是", "是", "是", "是", "不是", "是", "是","不是","是","是","不是","是","是","是","不是","不是","不是"};
    //统计成绩
    int score;
    //当前测试题目数量
    int count = 0;
    //测试题目总数量
    int count_num;
    //查询题库返回的cursor的条数
    int cursorlength;
    //使用cursorlength1暂存cursorlength的数据防止被置0
    int cursorlength1;
    //随机产生的随机数的数组
    int[] random_ques_array = new int[20];
    ListView mListView;
    //判断播放器是否在播放
    int Flag_isplaying;
    //播放按钮
    Button play;
    //主界面上方的题库数目提示文本
    TextView tv_sumques;
    //主界面上方的测试题目数量输入文本
    EditText ed_quescount;

    Dialogshowclass mDialogshowclass;
    //将初始题库暂存 准备转存到数据库
    String singleques;
    //将初始题库的答案暂存 准备转存到数据库
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
                //使用startActivityForResult()进行跳转 来进行数据回调
                startActivityForResult(intent,1);
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
        /**
         * 初始化数据
         */


        {
            play = findViewById(R.id.bt_player);
            tv_sumques = findViewById(R.id.tv_sumques);
            ed_quescount = findViewById(R.id.ed_quescount);
            mListView = findViewById(R.id.list_main);
            mListView.setOnItemLongClickListener(this);


            //设置标题
            this.setTitle("一起学习吧");
            mDBHelper = new DBHelper(this);
           /*
            使用sharedpreferences判断是否已经加载过数据了  防止重复原始题库重复加载进数据库
            向sharedpreferences中存储数据
            */

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


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
        /*
        点击开始测试
        */
    public void start(View view) {
        /*
        通过editview传入的参数判断是否为数字
         */
        if (isNumericZidai(ed_quescount.getText().toString())) {
            System.out.println("String.valueOf(ed_quescount.getText()):" + ed_quescount.getText());
            //捕获NumberFormatException异常但是不做处理  交由下方代码处理
            try {
                count_num = Integer.parseInt(String.valueOf(ed_quescount.getText()));
            } catch (NumberFormatException e) {
            }
            //判断输入数值知否在题库范围内
            if (count_num <= cursorlength1 && count_num > 0) {
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
            } else {
                Snackbar.make(play, "请输入正确的数量！", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            //            else {
            //                Snackbar.make(play, "测试数目不可小于1！", Snackbar.LENGTH_LONG)
            //                        .setAction("Action", null).show();
            //            }
        } else {
            Snackbar.make(play, "请输入数字！", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }
/*
点击进入历史成绩界面
 */
    public void score(View view) {
        Intent it_score = new Intent(this, HistoryActivity.class);
        startActivity(it_score);
    }
/*
点击播放音乐
 */
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
/*
长按listviewitem删除题库
 */
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
                        Snackbar.make(mListView, "删除成功！", Snackbar.LENGTH_LONG)
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
//单击listviewitem刷新当前界面
    @Override
    public void onClick(View v) {
        Intent it_addques = new Intent(this, AddquesActivity.class);
        startActivity(it_addques);
    }

    /*
   重新查询数据库并加载当前界面
     */
    public void reload() {
        Cursor cursor = mDBHelper.query(MainActivity.this, "Ques_tab");
        //释放资源

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
        mDBHelper.close();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //根据使用startActivityForResult的resultcode判断是否为AddquesActivity.java返回的  并刷新当前界面
      if (requestCode==1){
          reload();
      }
    }
}
