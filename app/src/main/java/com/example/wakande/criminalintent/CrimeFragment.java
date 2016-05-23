package com.example.wakande.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by wakande on 3/18/16.
 */
public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolveCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mSuspectPhoneButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private long mSuspectId;
    private Uri mTelURI;

    private static final String DATE_FORMAT = "cccc, LLL d, yyyy";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";


    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHONE = 2;
    private static final int REQUEST_PHOTO = 3;


    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }


    // onAttach(Activity) is called as well once the fragment is added to the fragment manager of the
//    host activity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        if (mCrime.getPhoneNumber() != null) {
            mTelURI = Uri.parse("tel:" + mCrime.getPhoneNumber());
        }
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }


    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mDateButton.setAllCaps(false);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String formattedDate = sdf.format(mCrime.getDate());
        updateDate(formattedDate);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolveCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolveCheckBox.setChecked(mCrime.isSolved());
        mSolveCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setType("text/plain")
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();
//                Intent reportIntent = new Intent(Intent.ACTION_SEND);
//                reportIntent.setType("text/plain");
//                reportIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                reportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                reportIntent = Intent.createChooser(reportIntent, getString(R.string.send_report));
                startActivity(reportIntent);
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }


        // initiate phone call

        mSuspectPhoneButton = (Button) v.findViewById(R.id.crime_report_phone);
        mSuspectPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mTelURI != null) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, mTelURI);
                    startActivity(phoneIntent);
                }
            }
        });

        if (mTelURI == null) {
            mSuspectPhoneButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        return v;
    }


    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate(mCrime.getDate().toString());
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts._ID

            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();
                String suspect = c.getString(0);
                mSuspectId = c.getLong(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_PHONE);
                } else {
                    prepareTelephoneURI();
                }


                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                mSuspectPhoneButton.setEnabled(true);
            } finally {
                c.close();
            }
        }else if(requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prepareTelephoneURI();
                }
            }
        }
    }


    private void updateDate(String text) {
        mDateButton.setText(text);
    }


    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getResources().getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void prepareTelephoneURI() {
        String[] idFields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phoneCursor = getActivity().getContentResolver() // ContactsContract.Data.CONTENT_URI works in place of ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                //ContactsContract.Contacts._ID is the same column as ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, idFields, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(mSuspectId)}, null);
        phoneCursor.moveToFirst();
        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        mCrime.setPhoneNumber(phoneNumber);
        mTelURI = Uri.parse("tel:" + phoneNumber);
        phoneCursor.close();
    }

    private void updatePhotoView(){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case (R.id.menu_item_delete_crime):
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return false;
        }
    }
}
