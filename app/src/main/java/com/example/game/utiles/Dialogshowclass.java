package com.example.game.utiles;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 弹窗显示
 */

public class Dialogshowclass {
//    int counter ;
    int which = 0;
    int score;
    //设置需要测试的题目适数量
    int counter_num ;
    //初始化倒计时控件
    TextView txv = null;
    String resultcode;
    String[] ques_array;
    String[] result_array;
    //随机数组

    private static Random ran=new Random();
    private AlertDialog firstDialog;

    //随机数集合
    ;
    //用户是否选择了
    boolean isSelected;
    Context context;
    DBHelper quesDB;
    private CountDownTimer myCountDownTimer;

    public Dialogshowclass(Context context, String[] ques_array, String[] result_array,int counter_num) {

        this.counter_num=counter_num;
        this.result_array = result_array;
        this.ques_array = ques_array;
        this.context = context;
        quesDB = new DBHelper(context);


    }

    public void Dialogshow(String which, int counter, int score, final int[] random_ques_array) {


        if (counter < counter_num + 1) {
            if (counter != 0 && which == result_array[random_ques_array[counter - 1]]) {
//                System.out.println("score="+score);
                score += (100/counter_num);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            TextView txv = new TextView(context);
            txv.setGravity(Gravity.CENTER);
            txv.setTextSize(40);
            builder.setTitle("快乐答题 gogogo");
            if (counter == counter_num) //判断是否达到最大题目数
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                //获得系统时间
                int month = calendar.get(Calendar.MONTH) + 1;
                //日
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                //小时
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                //分钟
                int minute = calendar.get(Calendar.MINUTE);
                String string = year + "年" + month + "月" + day + "日 " + hour + ":" + minute;
                builder.setMessage("你的得分为：" + score + "分");
                ContentValues contentValues = new ContentValues();
                contentValues.put("Score", score);
                contentValues.put("Date", string);
                quesDB.insert(contentValues, "Score_tab");
                //保存得分到Score_tab
            } else {// 未达到最大题目数 继续下一题
                builder.setMessage(ques_array[random_ques_array[counter]]);
            }
            builder.setView(txv);
            if (counter < counter_num) {
                builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resultcode = "是";
                        isSelected = true;
                    }
                });
                builder.setNegativeButton("不是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resultcode = "不是";
                        isSelected = true;
                    }
                });
            }
            isSelected = false;
            firstDialog = builder.create();
            firstDialog.show();
            counter++;
            countdown(txv, firstDialog);
            final int finalCounter = counter;
            final int finalScore = score;
            firstDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    firstDialog = null;
                    if (myCountDownTimer != null) {
                        // 停止countDownTimer
                        myCountDownTimer.cancel();
                        myCountDownTimer = null;
                    }
                    if (isSelected == true) {
                        Dialogshow(resultcode, finalCounter, finalScore,random_ques_array);//计算得分进入下一个问题展示页面
                        isSelected = false;
                    } else {
                        Dialogshow(null, finalCounter, finalScore,random_ques_array);
                    }
                }
            });
        }
//        randomset=null;
    }



    /**
     * 倒计时显示
     */
    public void countdown(TextView txv, AlertDialog Dialog) {
        final TextView txv_count = txv;
        txv_count.setTextColor(Color.rgb(255, 00, 00));
        final AlertDialog myDialog = Dialog;
        myCountDownTimer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txv_count.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                if (myDialog != null) {
                    myDialog.dismiss();
                }
            }
        };
        myCountDownTimer.start();
    }


    /**
     * 生成随机数
     */
    public static List Random_Num(int num_count, int num_range, List list) {
        while (list.size() < num_count) {
            int item = ran.nextInt(num_range);
            boolean addflag = true;
            for (int i = 0; i < list.size(); i++) {
                int itemnum = (int) list.get(i);
                if (item == itemnum) {
                    addflag = false;
                }
            }
            if (addflag) {
                list.add(item);
            }
        }
        return list;
    }

}
