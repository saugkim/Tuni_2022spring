package org.tuni.torchmodel;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link ISFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ISFragment extends Fragment {

    View view;
    ImageView imageView;
    Button saveButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Bitmap mParam1;
    //private Bitmap mParam2;

    public ISFragment() {
        // Required empty public constructor
    }
    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ISFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ISFragment newInstance(Bitmap bitmap) {
        ISFragment fragment = new ISFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, bitmap);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_is, container, false);
        saveButton = view.findViewById(R.id.saveButtonIS);
        imageView = view.findViewById(R.id.imageViewIS);
        imageView.setImageBitmap(mParam1);
        saveButton.setOnClickListener(view -> {
            SaveImage.SAVE(getActivity(), mParam1);
        });
        return view;
    }
}