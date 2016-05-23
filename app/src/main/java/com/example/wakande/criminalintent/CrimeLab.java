package com.example.wakande.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.wakande.criminalintent.database.CrimeBaseHelper;
import com.example.wakande.criminalintent.database.CrimeCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.wakande.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Created by wakande on 3/22/16.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {

        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    // converts crimes to content values for insertion into the database
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.PHONE_NUMBER, crime.getPhoneNumber());

        return values;
    }
 // queries the database and returns a cursor that can be walked for the crime objects that it contains
    // as it is waled, cursor wrapper # getcrime is used to transform data to crimes
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFIlename());
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime c) {
        String uuidString = c.getId().toString();
        ContentValues values = getContentValues(c);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public boolean deleteCrime(Crime c) {
        String uuidString = c.getId().toString();

        int deleted = mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
        return deleted == 1;

    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

}
