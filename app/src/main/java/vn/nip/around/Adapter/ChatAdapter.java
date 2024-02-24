package vn.nip.around.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.nip.around.Bean.BeanChat;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.ImageFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> implements View.OnClickListener {
    Activity activity;
    RecyclerView recycler;
    List<BeanChat> list;

    public ChatAdapter(Activity activity, RecyclerView recycler, List<BeanChat> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_me, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_other, parent, false);
        }
        itemView.setOnClickListener(this);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, int position) {
        try {
            final BeanChat item = list.get(position);
            BeanChat previous = null;
            if (position > 0) {
                previous = list.get(position - 1);
                if (previous != null) {
                    if (previous.getType() == item.getType()) {
                        holder.thumb.setVisibility(View.INVISIBLE);
                        holder.marginTop.setVisibility(View.GONE);
                    } else {
                        holder.thumb.setVisibility(View.VISIBLE);
                        holder.marginTop.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.thumb.setVisibility(View.VISIBLE);
                    holder.marginTop.setVisibility(View.VISIBLE);
                }
            }
            if (item != null) {
                if (item.getMessage().equals("TEXT_TYPE")) {
                    holder.image.setVisibility(View.GONE);
                    holder.containerImage.setVisibility(View.GONE);
                    holder.message.setVisibility(View.VISIBLE);

                    holder.message.setText(item.getChat_description());
                } else if (item.getMessage().equals("IMAGE_TYPE")) {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.containerImage.setVisibility(View.VISIBLE);
                    holder.message.setVisibility(View.GONE);
                    holder.image.setImageBitmap(item.getBitmap());
                    holder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ImageFragment imageFragment = ImageFragment.newInstance();
                            imageFragment.bitmap = item.getBitmap();
                            FragmentHelper.addFragment((FragmentActivity) activity, R.id.home_content, imageFragment);
                        }
                    });
                } else {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.containerImage.setVisibility(View.VISIBLE);
                    holder.message.setVisibility(View.GONE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = CmmFunc.getBitmapFromURL(item.getUrlImage());
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.image.setImageBitmap(bitmap);
                                    holder.image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ImageFragment imageFragment = ImageFragment.newInstance();
                                            imageFragment.bitmap = bitmap;
                                            FragmentHelper.addFragment((FragmentActivity) activity, R.id.home_content, imageFragment);
                                        }
                                    });
                                }
                            });
                        }
                    }).start();
                }
                if ((previous == null || item.getType() == 0 && previous.getType() != item.getType())) {

                    if (CmmVariable.avatarUser == null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonObject = new ActionGetProfile().execute().get();
                                    CmmVariable.avatarUser = CmmFunc.getBitmapFromURL(jsonObject.getString("avatar"));
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.thumb.setImageBitmap(CmmVariable.avatarUser);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        holder.thumb.setImageBitmap(CmmVariable.avatarUser);
                    }

                    holder.thumb.setVisibility(View.VISIBLE);
                }
                if (previous == null || item.getType() == 1 && previous.getType() != item.getType()) {

                    if (CmmVariable.avatarShipper == null) {

                    }
                    holder.thumb.setImageBitmap(CmmVariable.avatarShipper);
                    holder.thumb.setVisibility(View.VISIBLE);
                }
            }


        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "onBindViewHolder", e.getMessage());
        }
    }


    @Override
    public int getItemViewType(int position) {

        BeanChat bean = list.get(position);
        //0 / 1
        if (bean != null) {
            return bean.getType();
        } else {
            return 2;
        }


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {

    }

    //region Actions
    class ActionGetProfile extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_profile", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetProfile", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        ImageView image;
        ImageView thumb;
        CardView containerImage;
        LinearLayout container;
        FrameLayout marginTop;

        public ChatViewHolder(View itemView) {
            super(itemView);
            containerImage = (CardView) itemView.findViewById(R.id.container_image);
            message = (TextView) itemView.findViewById(R.id.message);
            image = (ImageView) itemView.findViewById(R.id.image);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            container = (LinearLayout) itemView.findViewById(R.id.container);
            marginTop = (FrameLayout) itemView.findViewById(R.id.margin_top);
        }
    }
}
