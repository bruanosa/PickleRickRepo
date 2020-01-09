package com.example.picklerickgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE = "high_score.db";
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "high_score_table";
    private static final String _ID = "_id";
    private static final String COL2 = "initials";
    private static final String COL3 = "high_score";

    public DataBaseHelper(Context context) {
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT NOT NULL, " + COL3 + " INTEGER NOT NULL)";
        db.execSQL(createTable);
        addData("AAA", 16, db);
        addData("BBB", 8, db);
        addData("CCC", 4, db);
        addData("DDD", 2, db);
        addData("FFF", 1, db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addData(String initials, int highScore, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, initials);
        Log.d(TAG, "addData: Adding " + initials + " to " + TABLE_NAME);
        contentValues.put(COL3, highScore);
        Log.d(TAG, "addData: Adding " + highScore + " to " + TABLE_NAME);
        db.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<HighScoreInfo> fetch(SQLiteDatabase db) {
        Cursor data = getData(db);
        ArrayList<HighScoreInfo> temp = new ArrayList<>();
        HighScoreInfo initsAndScores;
        try {
            if(data.moveToFirst()) {
                while(!data.isAfterLast()) {
                    int id = data.getInt(data.getColumnIndex(_ID));
                    String initials = data.getString(data.getColumnIndex(COL2));
                    int score = data.getInt(data.getColumnIndex(COL3));
                    initsAndScores = new HighScoreInfo(initials, score);
                    initsAndScores.setId(id);
                    temp.add(initsAndScores);
                    data.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public Cursor getData(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getHighScoreFields(String initials, SQLiteDatabase db) {
        String query1 = "SELECT " + _ID + ", " + COL2 + ", " + COL3 + " FROM " + TABLE_NAME + " WHERE " + COL2 + " = '" + initials + "'";
        Cursor data = db.rawQuery(query1, null);
        return data;
    }

    public void updateInitials(String newInitials, int id, String oldInitials, SQLiteDatabase db) {
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newInitials + "' WHERE " + _ID + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldInitials + "'";
        db.execSQL(query);
    }

    public void updateHighScore(int newHighScore, int id, int oldHighScore, SQLiteDatabase db) {
        String query = "UPDATE " + TABLE_NAME + " SET " + COL3 +
                " = '" + newHighScore + "' WHERE " + _ID + " = '" + id + "'" +
                " AND " + COL3 + " = '" + oldHighScore + "'";
        db.execSQL(query);
    }

    public void deleteInitials(int id, String initials, SQLiteDatabase db) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + _ID + " = '" + id + "'" +
                " AND " + COL2 + " = '" + initials + "'";
        db.execSQL(query);
    }

    public void deleteHighScore(int id, int highScore, SQLiteDatabase db) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + _ID + " = '" + id + "'" +
                " AND " + COL3 + " = '" + highScore + "'";
        db.execSQL(query);
    }

}
