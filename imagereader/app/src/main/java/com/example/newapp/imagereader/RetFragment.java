package com.example.newapp.imagereader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int IMG_LOAD_REQUEST = 1330;
    private static final int MAX_FACES = 3;
    public Bitmap image;
    private static final String TAG = "MyActivity";
    private FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];
    private RectF rects[] = new RectF[MAX_FACES];
    public static final String IMGS = "com.example.ImageReader.IMAGE";
    public static final String EXTRA_MESSAGE = "com.example.ImageReader.MESSAGE";
    private OnFragmentInteractionListener mListener;

    public RetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RetFragment newInstance(String param1, String param2) {
        RetFragment fragment = new RetFragment();
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
    public void onStart() {
        super.onStart();
        Button button = (Button) getView().findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();
            }
        });

        Button button3 = (Button) getView().findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callrest();
            }
        });

        Button button2 = (Button) getView().findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browse();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ret, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRetFragmentInteraction(uri);
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

    public void opencamera(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CAMERA_PIC_REQUEST);
            }
        }
        else
        {
            takepicture();
        }

    }

    public void browse() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMG_LOAD_REQUEST);
            }
        }
        else
        {
            openfol();
        }
    }

    public void takepicture()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    public void openfol()
    {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMG_LOAD_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK & data != null) {
            if (requestCode == CAMERA_PIC_REQUEST) {
                image = (Bitmap) data.getExtras().get("data");
                ImageView imageview1 = (ImageView) getView().findViewById(R.id.imageView2); //sets imageview as the bitmap
                imageview1.setImageBitmap(image);
            }
            else if (requestCode == IMG_LOAD_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                ImageView imageView2 = (ImageView) getView().findViewById(R.id.imageView2);
                imageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                image = BitmapFactory.decodeFile(picturePath);
            }
        }
        else
        {
            Log.v(TAG, "Photo not taken or Image not selected!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PIC_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera request granted
                takepicture();
            } else {
                Log.v(TAG, "Permission not granted!");
                //Camera request denied
            }
        }

        else if(requestCode == IMG_LOAD_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera request granted
                openfol();
            } else {
                Log.v(TAG, "Permission not granted!");
                //Camera request denied
            }
        }
    }


    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    public void callrest()
    {
        int ctr = 0;
        Arrays.fill(faceList, null);
        ImageView imageview = (ImageView) getView().findViewById(R.id.imageView2);
        //Bitmap tmpBmp = image.copy(Bitmap.Config.RGB_565, true);
        Bitmap btm = ((BitmapDrawable)imageview.getDrawable()).getBitmap();
        Bitmap tmpBmp = btm.copy(Bitmap.Config.RGB_565, true);
        FaceDetector faceDet = new FaceDetector(tmpBmp.getWidth(), tmpBmp.getHeight(), MAX_FACES);
        faceDet.findFaces(tmpBmp, faceList);
        for (int i=0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face ["+face+"]");
            if (face != null) {
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                PointF pf = new PointF();
                face.getMidPoint(pf);
                Log.d("FaceDet", "t Eyes distance ["+face.eyesDistance()+"] - Face midpoint ["+pf+"]");
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance();
                r.right = pf.x + face.eyesDistance();
                r.top = pf.y - face.eyesDistance();
                r.bottom = pf.y + face.eyesDistance();
                rects[i] = r;
                ctr += 1;
            }
        }


        Log.v(TAG,Integer.toString(ctr));
        if(ctr > 0) {
            Bitmap rini = Bitmap.createBitmap(btm, (int) rects[0].left - 2, (int) rects[0].top + 2, (int) (rects[0].right - rects[0].left + 5), (int) (rects[0].bottom - rects[0].top + 8));
            Bitmap resultBmp = Bitmap.createScaledBitmap(rini, 48, 48, true);
            imageview.setImageBitmap(resultBmp);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ctr; i++) {
                Bitmap rfir = Bitmap.createBitmap(btm, (int) rects[i].left - 2, (int) rects[i].top + 2, (int) (rects[i].right - rects[i].left + 5), (int) (rects[i].bottom - rects[i].top + 8));
                Bitmap sBmp = Bitmap.createScaledBitmap(rfir, 48, 48, true);
                sb.append("-");
                sb.append(encodeToBase64(sBmp, Bitmap.CompressFormat.JPEG, 100));
            }
                sb.delete(0,1);
            final String imgs = sb.toString();
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String url = "http://10.0.2.2:3000/iprd";
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("imgr", imgs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("onResponse", response.toString());
                        Intent intent = new Intent(getActivity(), ImageActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, response.toString());
                        intent.putExtra(IMGS, imgs);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("onErrorResponse", error.toString());
                    }
                }) {
                };
                queue.add(jsonObjectRequest);



        }

        else
        {
            Log.v(TAG,"No face found, Please take another photo!");
        }
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
        void onRetFragmentInteraction(Uri uri);
    }
}
