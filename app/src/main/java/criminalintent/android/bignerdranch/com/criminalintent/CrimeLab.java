package criminalintent.android.bignerdranch.com.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

public class CrimeLab
{
    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab crimeLab;

    private Context appContext;

    private ArrayList<Crime> crimes;

    private CriminalIntentJSONSerializer serializer;

    private CrimeLab(Context newContext)
    {
        appContext = newContext;

        serializer = new CriminalIntentJSONSerializer(appContext, FILENAME);

        try
        {
            crimes = serializer.loadCrimes();
        }
        catch (Exception e )
        {
            crimes = new ArrayList<Crime>();
        }
    }

    public boolean saveCrimes()
    {
        try
        {
            serializer.saveCrimes(crimes);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public ArrayList<Crime> getCrimes()
    {
        return crimes;
    }

    public Crime getCrime(UUID id)
    {
        for(Crime c : crimes)
        {
            if (c.getId().equals(id))
            {
                return c;
            }
        }

        return null;
    }

    public static CrimeLab get(Context c)
    {
        if ( crimeLab == null )
        {
            crimeLab = new CrimeLab(c.getApplicationContext());
        }

        return crimeLab;
    }

    public void addCrime(Crime c)
    {
        crimes.add(c);
    }

    public void deleteCrime(Crime c)
    {
        crimes.remove(c);
    }
}
