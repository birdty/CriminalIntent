package criminalintent.android.bignerdranch.com.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {

    private ViewPager viewPager;
    private ArrayList<Crime> crimes;

    public void onCrimeUpdated(Crime crime)
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPager = new ViewPager(this);

        viewPager.setId(R.id.viewPager);

        this.setContentView(viewPager);

        crimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = this.getSupportFragmentManager();

        viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = crimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return crimes.size();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}

            public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {}

            public void onPageSelected(int pos)
            {
                Crime crime = crimes.get(pos);
                if ( crime.getTitle() != null )
                {
                    setTitle(crime.getTitle());
                }
            }
        });

        UUID crimeId = (UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);

        for(int i = 0; i < crimes.size(); i++)
        {
            if ( crimes.get(i).getId().equals(crimeId) )
            {
                viewPager.setCurrentItem(i);
                break;
            }
        }

    }

}
