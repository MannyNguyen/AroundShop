package vn.nip.around.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Custom.RoundedTransformation;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.InfoBookFragment;
import vn.nip.around.Fragment.SendGift.ProfileSendGiftFragment;
import vn.nip.around.Fragment.SendGift.SendGiftFragment;
import vn.nip.around.Fragment.SendGift.TypeSendGiftFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class SendGiftAdapter extends RecyclerView.Adapter<SendGiftAdapter.MenuViewHolder> {
    View itemView;
    Fragment fragment;
    SendGiftFragment sendGiftFragment;
    RecyclerView recycler;
    public List<BeanSendGift> list;


    public SendGiftAdapter(Fragment fragment, RecyclerView recycler, List<BeanSendGift> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
        if (fragment instanceof SendGiftFragment) {
            sendGiftFragment = (SendGiftFragment) fragment;
        }
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_send_gift, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {
            holder.invite.setVisibility(View.GONE);
            holder.send.setVisibility(View.GONE);
            holder.icAround.setVisibility(View.INVISIBLE);
            final BeanSendGift item = list.get(position);
            if (item != null) {
                holder.name.setText(item.getFullname());
                if (item.isInstall()) {
                    holder.send.setVisibility(View.VISIBLE);
                    holder.icAround.setVisibility(View.VISIBLE);
                    holder.send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TypeSendGiftFragment.newInstance(CmmFunc.tryParseObject(item)).show(fragment.getActivity().getSupportFragmentManager(), TypeSendGiftFragment.class.getName());
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProfileSendGiftFragment.newInstance(CmmFunc.tryParseObject(item)));
                        }
                    });
                } else {
                    holder.invite.setVisibility(View.VISIBLE);
                    holder.invite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //new ActionCheckGift().execute(item.getPhone());
                            Uri uri = Uri.parse("smsto:" + item.getPhone());
                            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                            it.putExtra("sms_body", sendGiftFragment.message);
                            sendGiftFragment.getActivity().startActivity(it);

//                            SmsManager sms = SmsManager.getDefault();
//                            sms.sendTextMessage(item.getPhone(), null, sendGiftFragment.message, null, null);
//                            Toast.makeText(fragment.getActivity(), "Sent.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (!item.getAvatar().equals(StringUtils.EMPTY)) {
                    Picasso.with(fragment.getContext()).load(item.getAvatar()).transform(new CircleTransform()).into(holder.avatar);
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

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView avatar;
        ImageView icAround;
        Button invite;
        ImageButton send;


        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            icAround = (ImageView) view.findViewById(R.id.ic_around);
            invite = (Button) view.findViewById(R.id.invite);
            send = (ImageButton) view.findViewById(R.id.send);
        }
    }
}