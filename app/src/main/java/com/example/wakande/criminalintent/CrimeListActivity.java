package com.example.wakande.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by wakande on 3/22/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
