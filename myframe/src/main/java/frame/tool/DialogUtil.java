package frame.tool;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Author：LvQingYang
 * Date：2017/9/11
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class DialogUtil {

    public static AlertDialog getMesDialog(Context context,String title,String msg){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .create();
    }

    public static AlertDialog getMesDialog(Context context,int titleId,int msgId){
        return getMesDialog(context, context.getString(titleId),context.getString(msgId));
    }

    public static AlertDialog getMsgDialogWithBtn(Context context, String title, String msg,DialogInterface.OnClickListener posLis){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok,posLis)
                .setNegativeButton(android.R.string.cancel,null)
                .create();
    }

    public static AlertDialog getMsgDialogWithBtn(Context context, int titleId,int msgId,DialogInterface.OnClickListener posLis){
        return getMsgDialogWithBtn(context, context.getString(titleId),context.getString(msgId),posLis);
    }
}
