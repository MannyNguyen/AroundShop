package vn.nip.around.Fragment.Common;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

import static java.lang.Math.abs;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends BaseFragment implements View.OnClickListener {

    public Bitmap bitmap;
    private float xCoOrdinate, yCoOrdinate;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance() {

        Bundle args = new Bundle();

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_image, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (bitmap != null) {
            final ImageView image = (ImageView) view.findViewById(R.id.image);
            final View backGround = view.findViewById(R.id.bg);
            view.findViewById(R.id.save).setOnClickListener(ImageFragment.this);
            final int windowwidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            final int windowheight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
            image.setImageBitmap(bitmap);

            image.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            //xCoOrdinate = image.getX() - event.getRawX();
                            yCoOrdinate = image.getY() - event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            image.animate().y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                            float alpha = (windowheight / 2) - event.getRawY();
                            if (alpha > 0) {
                                alpha = event.getRawY() - (windowheight / 2);
                            }
                            alpha = (alpha / (windowheight / 2)) * (-1);
                            backGround.setAlpha(0.8f - alpha);
                            Log.d("YYY", event.getRawY() + "");

                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getRawY() < windowheight / 4) {
                                image.animate().y(0 - image.getHeight()).setDuration(200).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        FragmentHelper.pop(getActivity());
                                    }
                                }).start();
                            } else if (event.getRawY() > windowheight - windowheight / 4) {
                                image.animate().y(windowheight + image.getHeight()).setDuration(200).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        FragmentHelper.pop(getActivity());
                                    }
                                }).start();
                            } else {
                                image.animate().y(windowheight / 2 - image.getHeight() / 2).setDuration(200).start();
                                backGround.setAlpha(0.9f);
                            }

                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            });

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                downloadImage();
                break;
        }
    }

    private void downloadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    String name = System.currentTimeMillis() + ".png";
                    //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    final File file = new File("/sdcard/Download/" + name);
                    FileOutputStream fos = new FileOutputStream("/sdcard/Download/" + name);
                    fos.write(byteArray);
                    fos.close();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getContext(), "Download success", Toast.LENGTH_SHORT).show();
                                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Download fail. Please check permission", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {

                }
            }
        }).start();
    }
}
