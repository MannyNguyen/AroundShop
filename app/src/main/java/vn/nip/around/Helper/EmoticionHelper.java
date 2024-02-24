package vn.nip.around.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Bean.BeanEmoticion;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

/**
 * Created by HOME on 11/28/2017.
 */

public class EmoticionHelper {
    public static SpannableString textToImage(Activity activity, String content) {
        SpannableString ss = new SpannableString(content);
        int starts = 0;
        int end = 0;
        String guess = "<:";
        int px = CmmFunc.convertDpToPx(activity, 60);
        for (int i = content.indexOf(guess); i >= 0; i = content.indexOf(guess, i + 1)) {
            starts = i;
            end = content.indexOf(">", i + 1);
            String key = content.substring(starts, end + 1);
            BeanEmoticion beanEmoticion = (BeanEmoticion) BeanEmoticion.getMap(activity).get(key);
            if (beanEmoticion != null) {
                if (beanEmoticion.getIdResource() > 0) {
                    //Drawable drawable = activity.getResources().getDrawable(beanEmoticion.getIdResource());
                    //drawable.setBounds(0, 0, px, px);
                    Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), beanEmoticion.getIdResource());
                    bitmap = CmmFunc.resizeBitmap(bitmap, px);
                    ImageSpan span = new ImageSpan(bitmap);
                    ss.setSpan(span, starts, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return ss;

    }
}
