package com.example.newapp.imagereader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onResFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getActivity().getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String imlist = intent.getStringExtra(MainActivity.IMGS);
        ImageView imageview1 = (ImageView) getView().findViewById(R.id.imageView4);
        ImageView imageview2 = (ImageView) getView().findViewById(R.id.imageView5);
        ImageView imageview3 = (ImageView) getView().findViewById(R.id.imageView3);
        String[] parts = imlist.split("-");
        byte[] dc1 = Base64.decode(parts[0], Base64.DEFAULT);
        Bitmap img1 = BitmapFactory.decodeByteArray(dc1, 0, dc1.length);
        imageview1.setImageBitmap(img1);
        TextView tx1 = (TextView)getView().findViewById(R.id.textView4);
        TextView tx2 = (TextView)getView().findViewById(R.id.textView6);
        TextView tx3 = (TextView)getView().findViewById(R.id.textView5);
        tx2.setText("");
        tx3.setText("");
        try{
            JSONObject js = new JSONObject(message);
            tx1.setText(js.getString("pic1"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if(parts.length == 2)
        {
            byte[] dc2 = Base64.decode(parts[0], Base64.DEFAULT);
            Bitmap img2 = BitmapFactory.decodeByteArray(dc2, 0, dc2.length);
            imageview2.setImageBitmap(img2);
            imageview3.setImageResource(android.R.color.transparent);
            try{
                JSONObject js = new JSONObject(message);
                tx2.setText(js.getString("pic2"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(parts.length == 3)
        {
            byte[] dc2 = Base64.decode(parts[0], Base64.DEFAULT);
            Bitmap img2 = BitmapFactory.decodeByteArray(dc2, 0, dc2.length);
            imageview2.setImageBitmap(img2);
            byte[] dc3 = Base64.decode(parts[0], Base64.DEFAULT);
            Bitmap img3 = BitmapFactory.decodeByteArray(dc3, 0, dc3.length);
            imageview3.setImageBitmap(img3);
            try{
                JSONObject js = new JSONObject(message);
                tx2.setText(js.getString("pic2"));
                tx3.setText(js.getString("pic3"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else
        {
            imageview2.setImageResource(android.R.color.transparent);
            imageview3.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onResFragmentInteraction(Uri uri);
    }
}
