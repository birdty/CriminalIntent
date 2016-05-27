package criminalintent.android.bignerdranch.com.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment
{
    public static final String TAG = "CrimeFragment";
    public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    public static final String DIALOG_DATE = "date";
    private static final String DIALOG_IMAGE = "image";

    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_PHOTO = 1;
    public static final int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private ImageButton photoButton;
    private ImageView photoView;
    private Button suspectButton;
    private Callbacks callbacks;

    public interface Callbacks
    {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        callbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        callbacks = null;
    }

    public CrimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);

        crime = CrimeLab.get(getActivity()).getCrime(crimeId);

        setHasOptionsMenu(true);
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v =  inflater.inflate(R.layout.fragment_crime, container, false);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
        {
            if ( NavUtils.getParentActivityName(getActivity()) != null )
            {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        titleField = (EditText)v.findViewById(R.id.crime_title);

        titleField.setText(crime.getTitle());

        titleField.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                            crime.setTitle(s.toString());
                            callbacks.onCrimeUpdated(crime);
                            getActivity().setTitle(crime.getTitle());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }
        );

        dateButton = (Button)v.findViewById(R.id.crime_date);

        updateDate();

        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                FragmentManager fm = getActivity().getSupportFragmentManager();

                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                dialog.show(fm, DIALOG_DATE);
            }
        });

        solvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);

        solvedCheckBox.setChecked(crime.isSolved());

        solvedCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        crime.setSolved(isChecked);
                        callbacks.onCrimeUpdated(crime);
                    }
                }
        );

        photoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);

        photoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        photoView = (ImageView)v.findViewById(R.id.crime_imageView);

        photoView.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v)
            {
                Photo p = crime.getPhoto();

                if ( p == null )
                {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();

                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();

                DialogFragment d = ImageFragment.newInstance(path);

                d.show(fm, DIALOG_IMAGE);
            }
        });

        Button reportButton = (Button)v.findViewById(R.id.crime_reportButton);

        reportButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        suspectButton = (Button)v.findViewById(R.id.crime_suspectButton);

        suspectButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v )
            {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if ( crime.getSuspect() != null )
        {
            suspectButton.setText(crime.getSuspect());
        }

        PackageManager pm = getActivity().getPackageManager();

        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);

        if ( ! hasACamera )
        {
            photoButton.setEnabled(false);
        }

        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId)
    {
        Bundle args = new Bundle();

        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();

        fragment.setArguments(args);

        return fragment;
    }

    public void returnResult()
    {
        this.getActivity().setResult(Activity.RESULT_OK, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( resultCode != Activity.RESULT_OK ) return;

        if ( requestCode == REQUEST_DATE ) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            crime.setDate(date);

            updateDate();
        }
        else if ( requestCode == REQUEST_PHOTO )
        {
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);

            if ( filename != null )
            {
                Photo p = new Photo(filename);

                crime.setPhoto(p);

                callbacks.onCrimeUpdated(crime);

                showPhoto();
            }
        }
        else if ( requestCode == REQUEST_CONTACT )
        {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            if ( c.getCount() == 0 )
            {
                c.close();
                return;
            }

            c.moveToFirst();

            String suspect = c.getString(0);
            crime.setSuspect(suspect);
            callbacks.onCrimeUpdated(crime);
            suspectButton.setText(suspect);
            c.close();
        }
    }

    private void updateDate()
    {
        dateButton.setText(crime.getDate().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch ( item.getItemId() )
        {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) == null)
                {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    private void showPhoto()
    {
        Photo p = crime.getPhoto();

        BitmapDrawable b = null;

        if ( p != null )
        {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();

            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }

        photoView.setImageDrawable(b);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop()
    {
        super.onStop();

        PictureUtils.cleanImageView(photoView);
    }

    private String getCrimeReport()
    {
        String solvedString = null;

        if ( crime.isSolved() )
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";

        String dateString = DateFormat.format(dateFormat, crime.getDate()).toString();

        String suspect = crime.getSuspect();

        if ( suspect == null )
        {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
