package com.example.game.utiles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


/**
 * @author glsite.com
 * @version $
 * @des
 * @updateAuthor $
 * @updateDes
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DBname = "history_db";
    private static final String TABname = "Score_tab";
    public SQLiteDatabase history_DB;

    static final int version = 1;
    private Cursor cursor;
    int update_id;
    int _idMAX;
    private SQLiteDatabase mDatabase;

    public DBHelper(@Nullable Context context) {
        super(context, DBname, null, version);
    }
//创建两张表  题库表和历史成绩表
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.history_DB = db;
        history_DB.execSQL("create table Score_tab (_id integer primary key ,Score integer,Date varchr(40),Time    DATETIME DEFAULT (  datetime( 'now', 'localtime' )   ) )");
        history_DB.execSQL("create table Ques_tab (_id integer primary key ,Ques varchar(40),Answer varchar(10) )");

    }
//当有版本更新时的操作
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
//插入数据
    public void insert(ContentValues values, String TABname) {
        SQLiteDatabase database = getWritableDatabase();
        database.insert(TABname, null, values);
        database.close();
    }
//查询数据
    public Cursor query(Context context,String table) {
        //判断是否创建了表 没有则toast
        mDatabase = getWritableDatabase();
        cursor = mDatabase.query(table, null, null,
                null, null, null, null);
//        cursor.close();
        return cursor;
    }

    public void delete(int id, String TABname) {
//        if (history_DB == null) {
            update_id=id;
            SQLiteDatabase database = getWritableDatabase();
            database.delete(TABname, "_id=?", new String[]{String.valueOf(id)});

            update();
            System.out.println("删除成功");

            database.close();
//        }
    }
    public void  update(){
        SQLiteDatabase database = getWritableDatabase();
        execute();

        database.execSQL("UPDATE Score_tab SET _id =(_id- 1) WHERE _id>= ? and _id<=?",new Object[]{update_id,_idMAX});
    }
    public void execute (){
        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor2 = database.query(TABname, new String[]{"_id"}, null, null, null, null, "_id desc  Limit 1 Offset 1;");
            if (cursor2.moveToFirst()) {
                 _idMAX = cursor2.getInt(cursor2.getColumnIndex("_id"))+1;
            }
    }
    public void close(){
        if (mDatabase!=null){
            mDatabase.close();
        }


    }

}
