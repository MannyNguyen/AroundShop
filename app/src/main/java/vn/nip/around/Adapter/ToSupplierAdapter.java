package vn.nip.around.Adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.SupplierFragment;
import vn.nip.around.MainActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 22/8/2017.
 */

public class ToSupplierAdapter extends RecyclerView.Adapter<ToSupplierAdapter.MenuViewHolder> implements View.OnClickListener {


    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<File> list;
    SupplierFragment fragment;
    public static int currentPosition;

    public ToSupplierAdapter(FragmentActivity activity, SupplierFragment fragment, RecyclerView recycler, List<File> list) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }


    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_image, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {

            int width = (AppActivity.WINDOW_WIDTH - CmmFunc.convertDpToPx(activity, 32)) / 3;
            holder.container.setLayoutParams(new FrameLayout.LayoutParams(width, width));
            if (position < list.size() - 1) {
                holder.remove.setVisibility(View.VISIBLE);
                holder.image.setBackground(null);
                File item = list.get(position);
                Bitmap bitmap = BitmapFactory.decodeFile(item.getPath());
                holder.image.setImageBitmap(bitmap);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        currentPosition = position;
                        fragment.startActivityForResult(intent, 1999);
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(position);
                        //notifyItemRemoved(position);
                        //notifyItemRangeChanged(position, getItemCount());
                        notifyDataSetChanged();
                    }
                });
            } else {
                holder.remove.setVisibility(View.GONE);
                holder.image.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_add_image_supplier));
                holder.image.setBackground(fragment.getResources().getDrawable(R.drawable.border_gray_dash_400));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            if (list.size() > 3) {
                return;
            }

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        CmmVariable.READ_EXTERNAL_STORAGE);
                Toast.makeText(activity, "Please check permission Storage of App", Toast.LENGTH_SHORT).show();
                return;
            }

            final int position = recycler.getChildLayoutPosition(view);
            //File item = list.get(position);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    currentPosition = position;
                    intent.setType("image/*");
                    fragment.startActivityForResult(intent, 1999);
                }
            }).start();

        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView remove;
        private FrameLayout container;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            remove = (ImageView) view.findViewById(R.id.remove);
            container = (FrameLayout) view.findViewById(R.id.container);
        }
    }
}