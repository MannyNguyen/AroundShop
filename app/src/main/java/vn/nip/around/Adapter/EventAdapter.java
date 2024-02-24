package vn.nip.around.Adapter;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Bean.BeanBanner;
import vn.nip.around.Bean.BeanEvent;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Banner.BannerHomeFragment;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Common.RecyclerFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    public List<BeanEvent> list;
    RecyclerFragment fragment;


    public EventAdapter(FragmentActivity activity, RecyclerFragment fragment, RecyclerView recycler, List<BeanEvent> list) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position) {
        try {
            BeanEvent item = list.get(position);
            if (item != null) {
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getTime(), df);
                String time = DateTimeHelper.parseDateTime(dateTime);
                holder.time.setText(time);
                Object image = CmmFunc.getValueByKey(item, activity.getString(R.string.server_key_image));
                if (image != null) {
                    Picasso.with(activity).load(image + "").into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                        }
                    });
                }

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
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanEvent item = list.get(itemPosition);
            if (item != null) {
                switch (item.getType()) {
                    case 0:
                        break;
                    case 1:
                        FragmentHelper.addFragment(activity, R.id.home_content, NoticeFragment.newInstance(item.getId_content(), activity.getResources().getString(R.string.notice)));
                        break;
                    case 2:
                        FragmentHelper.addFragment(activity, R.id.home_content, ProductFragment.newInstance(item.getId_content(), ""));
                        break;
                    case 3:
                        new ActionGetBannerDetail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId_content());
                        break;
                }
            }
        } catch (Exception e) {
            Log.e("EventAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    class ActionGetBannerDetail extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = new JSONObject();
            int id = (int) params[0];
            try {

                String value = APIHelper.getBannerDetail(id);
                jsonObject = new JSONObject(value);
            } catch (Exception e) {

            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                BeanBanner beanBanner = (BeanBanner) CmmFunc.tryParseJson(jsonObject.getString("data"), BeanBanner.class);
                                Fragment fragment = BannerHomeFragment.newInstance(CmmFunc.tryParseObject(beanBanner));
                                FragmentHelper.addFragment(activity, R.id.home_content, fragment);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fragment.hideProgress();
            }
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView time;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            time = (TextView) view.findViewById(R.id.time);
        }


    }
}