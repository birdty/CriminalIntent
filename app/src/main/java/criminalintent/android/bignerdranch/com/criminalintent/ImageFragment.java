package criminalintent.android.bignerdranch.com.criminalintent;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends DialogFragment
{
    public static final String EXTRA_IMAGE_PATH = "com.bignerdranch.android.criminalintent.image_path";

    private ImageView imageView;

    public static ImageFragment newInstance(String imagePath)
    {
        Bundle args = new Bundle();

        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment fragment = new ImageFragment();

        fragment.setArguments(args);

        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        imageView = new ImageView(getActivity());
        String path = (String)getArguments().getSerializable(EXTRA_IMAGE_PATH);

        BitmapDrawable image = PictureUtils.getScaledDrawable(getActivity(), path);

        imageView.setImageDrawable(image);

        return imageView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        PictureUtils.cleanImageView(imageView);
    }
}
