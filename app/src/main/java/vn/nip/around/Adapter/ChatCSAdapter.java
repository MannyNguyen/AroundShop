package vn.nip.around.Adapter;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartfoxserver.v2.entities.data.SFSDataType;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import vn.nip.around.Bean.BeanCSChat;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Fragment.Common.ImageFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.EmoticionHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.R;

public class ChatCSAdapter extends RecyclerView.Adapter<ChatCSAdapter.ChatViewHolder> implements View.OnClickListener {

    final int MY_TEXT = 100;
    final int MY_IMAGE = 101;

    final int OTHER_TEXT = 200;
    final int OTHER_IMAGE = 201;

    final int OTHER_PRODUCT = 301;
    Fragment fragment;
    RecyclerView recycler;
    List<BeanCSChat> list;

    public ChatCSAdapter(Fragment fragment, RecyclerView recycler, List<BeanCSChat> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case MY_TEXT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_message_text_me, parent, false);
                break;

            case MY_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_message_image_me, parent, false);
                break;

            case OTHER_TEXT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_message_text_other, parent, false);
                break;
            case OTHER_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_message_image_other, parent, false);
                break;

            case OTHER_PRODUCT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_message_product_other, parent, false);
                break;

        }

        itemView.setOnClickListener(this);
        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        try {
            final BeanCSChat item = list.get(position);
            if (item != null) {

                switch (item.getChat().getInt("type")) {
                    case BeanCSChat.IMAGE:
                        if (item.getChat().get("content").getTypeId().equals(SFSDataType.BYTE_ARRAY)) {
                            final Bitmap bmp = CmmFunc.getBitmapFromByteArray(item.getChat().getByteArray("content"));
                            holder.image.setImageBitmap(bmp);
                            holder.image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewImage(bmp);
                                }
                            });
                        } else if (item.getChat().get("content").getTypeId().equals(SFSDataType.UTF_STRING)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bitmap = CmmFunc.getBitmapFromURL(item.getChat().getUtfString("content"));
                                    bitmap = CmmFunc.resizeBitmap(bitmap, CmmVariable.IMAGE_CHAT_SIZE);
                                    final Bitmap finalBitmap = bitmap;
                                    fragment.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            holder.image.setImageBitmap(finalBitmap);
                                        }
                                    });
                                }
                            }).start();
                            holder.image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    viewImage(item.getChat().getUtfString("content"));
                                }
                            });
                        }

                        break;
                    case BeanCSChat.TEXT:
                        holder.message.setVisibility(View.GONE);
                        final String text = item.getChat().getUtfString("content") + "";
                        if (text.indexOf("<:") != -1) {
                            final SpannableString spannableString = EmoticionHelper.textToImage(fragment.getActivity(), text);
                            holder.message.setText(spannableString);
                        } else {
                            holder.message.setText(text);
                        }
                        holder.message.setVisibility(View.VISIBLE);

                        break;

                    case BeanCSChat.PRODUCT:
                        try {
                            final SFSObject content = (SFSObject) item.getChat().getSFSObject("content");
                            Picasso.with(fragment.getContext()).load(content.getUtfString("product_image")).transform(new CircleTransform()).centerInside().resize(CmmVariable.IMAGE_CHAT_SIZE, CmmVariable.IMAGE_CHAT_SIZE).into(holder.image);
                            holder.name.setText(content.getUtfString("product_name"));
                            holder.price.setText(CmmFunc.formatMoney(content.getInt("product_price")));
                            holder.oldPrice.setVisibility(View.GONE);
                            if (content.getInt("product_old_price") != 0) {
                                holder.oldPrice.setVisibility(View.VISIBLE);
                                holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.oldPrice.setText(CmmFunc.formatMoney(content.getInt("product_old_price")));
                            }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProductFragment.newInstance(content.getInt("product_id"), StringUtils.EMPTY));
                                }
                            });

                            holder.buy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, content.getInt("product_id"));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }


                if (!item.getSender_avatar().equals(StringUtils.EMPTY)) {
                    Picasso.with(fragment.getContext()).load(item.getSender_avatar()).transform(new CircleTransform()).into(holder.avatar);
                }


                DateTime dateTime = DateTime.parse(item.getTime(), DateTimeFormat.forPattern(BeanCSChat.FORMAT));
                holder.time.setText(dateTime.toString("hh:mm a dd/MM/yyyy"));
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "onBindViewHolder", e.getMessage());
        }
    }

    class ActionAddCart extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                String response = APIHelper.addCart(productID, 1);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    return;
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, CartFragment.newInstance());

                }
            } catch (Exception e) {

            }
        }
    }

    private void viewImage(final Object object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (object instanceof String) {
                    bitmap = CmmFunc.getBitmapFromURL(object + "");
                } else if (object instanceof Bitmap) {
                    bitmap = (Bitmap) object;
                }
                ImageFragment imageFragment = ImageFragment.newInstance();
                imageFragment.bitmap = bitmap;
                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, imageFragment);
            }
        }).start();
    }


    @Override
    public int getItemViewType(int position) {
        BeanCSChat bean = list.get(position);
        //My self
        if (SmartFoxHelper.getInstance().isConnected() == false || SmartFoxHelper.getInstance().getMySelf().getName().equals(bean.getSender_username())) {
            if (bean.getChat().getInt("type") == BeanCSChat.TEXT) {
                return MY_TEXT;
            } else if (bean.getChat().getInt("type") == BeanCSChat.IMAGE) {
                return MY_IMAGE;
            }
        }
        //other
        if (bean.getChat().getInt("type") == BeanCSChat.TEXT) {
            return OTHER_TEXT;
        } else if (bean.getChat().getInt("type") == BeanCSChat.IMAGE) {
            return OTHER_IMAGE;
        }

        if (bean.getChat().getInt("type") == BeanCSChat.PRODUCT) {
            return OTHER_PRODUCT;
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {

    }


    public class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        ImageView image;
        TextView message;
        TextView time;

        Button buy;
        TextView name;
        TextView price;
        TextView oldPrice;


        public ChatViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            image = (ImageView) itemView.findViewById(R.id.image);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);

            buy = (Button) itemView.findViewById(R.id.buy_now);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            oldPrice = (TextView) itemView.findViewById(R.id.old_price);
        }
    }
}
