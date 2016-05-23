//package com.example.wakande.criminalintent;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.app.Fragment;
//
//import java.util.UUID;
//
//public class CrimeActivity extends SingleFragmentActivity {
//
//    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
//    private static final String EXTRA_CRIME_POSITION = "com.bignerdranch.android.criminalintent.position";
//
//    @Override
//    protected Fragment createFragment() {
//        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
//        setPositionResult();
//        return CrimeFragment.newInstance(crimeId);
//    }
//
//    public static Intent newIntent(Context packageContext, UUID crimeId, int position){
//        Intent intent = new Intent(packageContext, CrimeActivity.class);
//        intent.putExtra(EXTRA_CRIME_ID, crimeId);
//        intent.putExtra(EXTRA_CRIME_POSITION, position);
//        return intent;
//    }
//
//    public static int getPositionResult(Intent data){
//        return data.getIntExtra(EXTRA_CRIME_POSITION, 0);
//    }
//
//    private void setPositionResult(){
//        Intent data = new Intent();
//        data.putExtra(EXTRA_CRIME_POSITION,getIntent().getIntExtra(EXTRA_CRIME_POSITION,0) );
//        setResult(RESULT_OK, data);
//    }
//
//
//
//}
