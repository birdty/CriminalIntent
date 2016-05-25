package criminalintent.android.bignerdranch.com.criminalintent;

import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    protected Fragment createFragment()
    {
        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);


        return CrimeFragment.newInstance(crimeId);
    }
}
