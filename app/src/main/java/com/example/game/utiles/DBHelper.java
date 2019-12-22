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

    public DBHelper(@Nullable Context context) {
        super(context, DBname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.history_DB = db;
        history_DB.execSQL("create table Score_tab (_id integer primary key ,Score integer,Date varchr(40),time not null default current_timestamp )");
        history_DB.execSQL("create table Ques_tab (_id integer primary key ,Ques varchar(40),Answer varchar(10) )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(ContentValues values, String TABname) {
        SQLiteDatabase database = getWritableDatabase();
        database.insert(TABname, null, values);
        database.close();
    }

    public Cursor query(Context context,String table) {
        //判断是否创建了表 没有则toast
        SQLiteDatabase database = getWritableDatabase();
        cursor = database.query(table, null, null,
                null, null, null, null);
//        cursor.close();
//        database.close();
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

}
