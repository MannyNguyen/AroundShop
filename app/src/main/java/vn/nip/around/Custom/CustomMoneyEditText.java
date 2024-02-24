package vn.nip.around.Custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by viminh on 3/20/2017.
 */

public class CustomMoneyEditText extends CustomEditText {

    private String current = "";

    public CustomMoneyEditText(Context context) {
        super(context);
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(s, start, lengthBefore, lengthAfter);
        try {
            if (!s.toString().equals(current)) {
                String cleanString = s.toString().replaceAll(",", "");
                for (int i = cleanString.length() - 3; i > 0; i -= 3) {
                    cleanString = new StringBuilder(cleanString).insert(i, ",").toString();
                }
                current = cleanString;
                this.setText(cleanString);
                this.setSelection(cleanString.length());

            }
        } catch (Exception e) {

        }
    }

    public long getValue() {
        try {
            String value = getText().toString().trim().replaceAll(",", "");
            long retValue = Long.valueOf(value);
            return retValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
