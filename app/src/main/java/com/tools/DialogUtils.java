package com.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Administrator on 2019/2/26/026.
 */

public class DialogUtils {

    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack, String message, DialogInterface.OnKeyListener keyListener, View view ) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.alertDialogFunction(dialog);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }

        builder.show();
        return builder;
    }


    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack, final AlertDialogNegativeCallBack negative, String message, DialogInterface.OnKeyListener keyListener, View view ) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.alertDialogFunction(dialog);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                negative.alertDialogFunction();
                dialog.dismiss();
            }
        });
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }

        builder.show();
        return builder;
    }


    public static AlertDialog.Builder showAlertDialog(Context context, final AlertDialogCallBack callBack, final AlertDialogNegativeCallBack negative, String message, DialogInterface.OnKeyListener keyListener, View view , boolean isOk, boolean isCancel) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info).setView(view);
        builder.setMessage(message);
        if(isOk) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(callBack!=null)
                    callBack.alertDialogFunction(dialog);
                }
            });
        }
        if(isCancel) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(negative!=null)
                    negative.alertDialogFunction();
                    // dialog.dismiss();
                }
            });
        }
        if(keyListener!=null){
            builder.setOnKeyListener(keyListener);
        }
            builder.show();
        return builder;
    }

    /**
     * 检查Activity是否已关闭
     */
    public static Boolean checkActivityIsRun(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isFinishing() || !activity.isDestroyed();
        }
        return !activity.isFinishing();
    }
}
