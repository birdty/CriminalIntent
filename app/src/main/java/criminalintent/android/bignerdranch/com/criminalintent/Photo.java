package criminalintent.android.bignerdranch.com.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

public class Photo
{
    private static final String JSON_FILENAME = "filename";

    private String filename;

    public Photo(String newFilename)
    {
        filename = newFilename;
    }

    public Photo(JSONObject json) throws JSONException {
        filename = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, filename);
        return json;
    }

    public String getFilename()
    {
        return filename;
    }
}
