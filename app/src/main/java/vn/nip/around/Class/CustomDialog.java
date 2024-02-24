package vn.nip.around.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Fragment.Common.RateFragment;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.Interface.ICallbackValue;
import vn.nip.around.R;

import com.squareup.picasso.Picasso;

/**
 * Created by viminh on 10/5/2016.
 */

public class CustomDialog {

    public static void DialogOTP(Activity activity, String phone, final ICallbackValue okCallback, final ICallback resendCallBack) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_otp, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            final EditText otp = (EditText) dialogView.findViewById(R.id.otp);
            final TextView phoneView = (TextView) dialogView.findViewById(R.id.phone);
            phoneView.setText(phone);
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialogView.findViewById(R.id.resend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    CmmFunc.hideKeyboard(GlobalClass.getActivity());
//                    if (resendCallBack != null) {
//                        resendCallBack.excute();
//                    }

                }
            });
            dialogView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CmmFunc.hideKeyboard(GlobalClass.getActivity());
                    if (otp.getText().toString().equals("")) {
                        otp.setError(GlobalClass.getActivity().getString(R.string.required));
                        return;
                    }
                    if (okCallback != null) {
                        dialog.dismiss();
                        okCallback.excute(otp.getText().toString());
                    }

                }
            });

        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }


    public static void Dialog2Button(final Activity activity, final String titleText, final String messageText, final String titleOk, final String titleCancle, final ICallback okCallback, final ICallback cancleCallback) {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_confirm, null);

            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);

            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            final Button ok = (Button) dialogView.findViewById(R.id.ok);
            final Button cancel = (Button) dialogView.findViewById(R.id.cancel);
            cancel.setText(titleCancle);
            ok.setText(titleOk);
            final AlertDialog dialog = builder.create();
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);


            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.5f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);

            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (okCallback != null) {
                        okCallback.excute();
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    if (cancleCallback != null) {
                        cancleCallback.excute();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void showMessage(final Activity activity, String titleText, String messageText) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_message, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);
            title.setText(titleText);
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            View ok =  dialogView.findViewById(R.id.ok);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    public static void showMessage(Activity activity, String titleText, String messageText, final ICallback callback) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_message, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);
            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            CardView ok = (CardView) dialogView.findViewById(R.id.ok);
            final AlertDialog dialog = builder.create();
            dialog.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.excute();
                    }

                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }

    }

    public static AlertDialog dialogFinish;

    public static void showFinish(Activity activity, String titleText, String messageText, final ICallback callback) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_message, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            TextView title = (TextView) dialogView.findViewById(R.id.title);
            title.setText(titleText);
            if (titleText.equals("")) {
                title.setVisibility(View.GONE);
            }
            TextView message = (TextView) dialogView.findViewById(R.id.message);
            message.setText(messageText);
            CardView ok = (CardView) dialogView.findViewById(R.id.ok);
            final AlertDialog dialogFinish = builder.create();
            dialogFinish.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogFinish.dismiss();
                    if (callback != null) {
                        callback.excute();
                    }

                }
            });
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }

    }

    public static void popupShipFee(Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_shipping_fee, null);
            ImageView imageView = (ImageView) dialogView.findViewById(R.id.image);
            if (StorageHelper.getLanguage().equals("vi")) {
                Picasso.with(activity).load(CmmVariable.popupPriceVN).into(imageView);
            } else {
                Picasso.with(activity).load(CmmVariable.popupPriceEN).into(imageView);
            }
            builder.setView(dialogView);
            builder.setCancelable(true);
            final AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }

    public static void popupServiceFee(Activity activity, int type) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_shipping_fee, null);
            ImageView imageView = (ImageView) dialogView.findViewById(R.id.image);
            switch (type) {
                case 0:
                    if (StorageHelper.getLanguage().equals("vi")) {
                        Picasso.with(activity).load(CmmVariable.popupPickupServiceFeeVN).into(imageView);
                    } else {
                        Picasso.with(activity).load(CmmVariable.popupPickupServiceFeeEN).into(imageView);
                    }
                    break;
                case 1:
                    if (StorageHelper.getLanguage().equals("vi")) {
                        Picasso.with(activity).load(CmmVariable.popupGiftingServiceFeeVN).into(imageView);
                    } else {
                        Picasso.with(activity).load(CmmVariable.popupGiftingServiceFeeEN).into(imageView);
                    }
                    break;
            }

            builder.setView(dialogView);
            builder.setCancelable(true);
            final AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            Log.e("CustomDialog", "ViMT - DialogOTP: " + e.getMessage());
        }
    }
}
