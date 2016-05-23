package com.example.wakande.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wakande.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by wakande on 4/11/16.
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimebase.db";

    public CrimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                        "_id integer PRIMARY KEY AUTOINCREMENT," +
                        CrimeTable.Cols.UUID    + ", " +
                        CrimeTable.Cols.TITLE   + ", " +
                        CrimeTable.Cols.DATE    + ", " +
                        CrimeTable.Cols.SOLVED  + ", " +
                        CrimeTable.Cols.SUSPECT + ", " +
                        CrimeTable.Cols.PHONE_NUMBER
                        + ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
