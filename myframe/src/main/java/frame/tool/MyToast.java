package frame.tool;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myframe.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

/**
 * Author：LvQingYang
 * Date：2017/8/18
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class MyToast {
    private static Toast toast;
    private static AlertDialog dialog;
    //info toast
    public static void info(Context context ,String text, int duration){
        showToast(context, R.drawable.ic_info, text, duration);
    }

    public static void info(Context context ,int textId, int duration){
        showToast(context, R.drawable.ic_info,context.getString(textId),duration);
    }

    public static void info(Context context ,String text){
        showToast(context, R.drawable.ic_info, text, Toast.LENGTH_SHORT);
    }

    public static void info(Context context ,int textId){
        showToast(context, R.drawable.ic_info,context.getString(textId),Toast.LENGTH_SHORT);
    }

    //success
    public static void success(Context context ,String text, int duration){
        showToast(context, R.drawable.ic_success, text, duration);
    }

    public static void success(Context context ,int textId, int duration){
        showToast(context, R.drawable.ic_success, context.getString(textId),duration);
    }

    public static void success(Context context ,String text){
        showToast(context, R.drawable.ic_success, text, Toast.LENGTH_SHORT);
    }

    public static void success(Context context ,int textId){
        showToast(context, R.drawable.ic_success, context.getString(textId),Toast.LENGTH_SHORT);
    }

    //error
    public static void error(Context context ,String text, int duration){
        showToast(context, R.drawable.ic_error, text, duration);
    }

    public static void error(Context context ,int textId, int duration){
        showToast(context, R.drawable.ic_error, context.getString(textId),duration);
    }

    public static void error(Context context ,String text){
        showToast(context, R.drawable.ic_error, text, Toast.LENGTH_SHORT);
    }

    public static void error(Context context ,int textId){
        showToast(context, R.drawable.ic_error, context.getString(textId),Toast.LENGTH_SHORT);
    }

    //loading
    public static void loading(Context context ,String text){
        View view=LayoutInflater.from(context).inflate
                (R.layout.toast_loading,null);
        TextView tv=view.findViewById(R.id.tv);
        tv.setText(text);
        AVLoadingIndicatorView avl=view.findViewById(R.id.avl);
        avl.setIndicator(getIndicator(context));
        avl.show();

        dialog=new AlertDialog.Builder(context,R.style.CustomDialog)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.show();
    }


    public static void loading(Context context ,int textId){
        loading(context, context.getString(textId));
    }

    //warning
    public static void warning(Context context ,String text, int duration){
        showToast(context, R.drawable.ic_warning, text, duration);
    }

    public static void warning(Context context ,int textId, int duration){
        showToast(context, R.drawable.ic_warning, context.getString(textId),duration);
    }

    public static void warning(Context context ,String text){
        showToast(context, R.drawable.ic_warning, text, Toast.LENGTH_SHORT);
    }

    public static void warning(Context context ,int textId){
        showToast(context, R.drawable.ic_warning, context.getString(textId),Toast.LENGTH_SHORT);
    }

    public static void cancel(){
        if (toast != null) {
            toast.cancel();
        }
        if (dialog != null) {
            dialog.cancel();
        }
    }

    static void showToast(Context context, int iconId, String text, int duration){
        if (toast != null) {
            toast.cancel();
        }
        toast=Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER,0,0);

        View v= LayoutInflater.from(context).inflate
                (R.layout.toast,null);
        ImageView ivIcon=v.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(iconId);
        TextView tv=v.findViewById(R.id.tv);
        tv.setText(text);

        toast.setView(v);
        toast.show();
    }

    private static String getIndicator(Context context)
    {
        String[] arrayOfString = context.getResources().getStringArray(R.array.arr_indicator);
        int i = new Random().nextInt(arrayOfString.length);
        return arrayOfString[i];
    }
}
