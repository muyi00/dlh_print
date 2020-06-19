package com.dlh.open.test;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtils {
    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private static Toast sToast;
    private static int sGravity = -1;
    private static int sXOffset = -1;
    private static int sYOffset = -1;
    private static int sBgColor = COLOR_DEFAULT;
    private static int sBgResource = -1;
    private static int sMsgColor = COLOR_DEFAULT;

    private static Application sApplication;

    public static void init(@NonNull final Context context) {
        ToastUtils.sApplication = (Application) context.getApplicationContext();
    }

    public static Application getApp() {
        if (sApplication != null) {
            return sApplication;
        }
        throw new NullPointerException("你应该先初始化 com.dlh.module_base.utils.ToastUtils.init");
    }

    private ToastUtils() {
        throw new UnsupportedOperationException("请不要实例化我哦...");
    }


    /**
     * 设置吐司位置
     *
     * @param gravity gravity
     * @param xOffset x轴偏移像素
     * @param yOffset y轴偏移像素
     */
    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景颜色
     */
    public static void setBgColor(@ColorInt final int backgroundColor) {
        sBgColor = backgroundColor;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource 背景资源
     */
    public static void setBgResource(@DrawableRes final int bgResource) {
        sBgResource = bgResource;
    }

    /**
     * 设置消息颜色
     *
     * @param msgColor 消息颜色
     */
    public static void setMsgColor(@ColorInt final int msgColor) {
        sMsgColor = msgColor;
    }

    /**
     * 显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(@NonNull final CharSequence text) {
        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示短时吐司
     *
     * @param resId 文本资源id
     */
    public static void showShort(@StringRes final int resId) {
        show(resId, Toast.LENGTH_SHORT);
    }

//    /**
//     * 显示短时吐司
//     *
//     * @param resId The resource id for text.
//     * @param args  The args.
//     */
//    public static void showShort(@StringRes final int resId, final Object... args) {
//        if (args != null && args.length == 0) {
//            show(resId, Toast.LENGTH_SHORT);
//        } else {
//            show(resId, Toast.LENGTH_SHORT, args);
//        }
//    }
//
//    /**
//     * 显示短时吐司
//     *
//     * @param format The format.
//     * @param args   The args.
//     */
//    public static void showShort(final String format, final Object... args) {
//        if (args != null && args.length == 0) {
//            show(format, Toast.LENGTH_SHORT);
//        } else {
//            show(format, Toast.LENGTH_SHORT, args);
//        }
//    }

    /**
     * 显示长时吐司
     *
     * @param text The text.
     */
    public static void showLong(@NonNull final CharSequence text) {
        show(text, Toast.LENGTH_LONG);
    }

//    /**
//     * 显示长时吐司
//     *
//     * @param resId The resource id for text.
//     */
//    public static void showLong(@StringRes final int resId) {
//        show(resId, Toast.LENGTH_LONG);
//    }
//
//    /**
//     * 显示长时吐司
//     *
//     * @param resId The resource id for text.
//     * @param args  The args.
//     */
//    public static void showLong(@StringRes final int resId, final Object... args) {
//        if (args != null && args.length == 0) {
//            show(resId, Toast.LENGTH_SHORT);
//        } else {
//            show(resId, Toast.LENGTH_LONG, args);
//        }
//    }
//
//    /**
//     * 显示长时吐司
//     *
//     * @param format The format.
//     * @param args   The args.
//     */
//    public static void showLong(final String format, final Object... args) {
//        if (args != null && args.length == 0) {
//            show(format, Toast.LENGTH_SHORT);
//        } else {
//            show(format, Toast.LENGTH_LONG, args);
//        }
//    }

    /**
     * 显示短时自定义吐司
     */
    public static View showCustomShort(@LayoutRes final int layoutId) {
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_SHORT);
        return view;
    }

    /**
     * 显示长时自定义吐司
     */
    public static View showCustomLong(@LayoutRes final int layoutId) {
        final View view = getView(layoutId);
        show(view, Toast.LENGTH_LONG);
        return view;
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }

    private static void show(@StringRes final int resId, final int duration) {
        show(getApp().getResources().getText(resId).toString(), duration);
    }

    private static void show(@StringRes final int resId, final int duration, final Object... args) {
        show(String.format(getApp().getResources().getString(resId), args), duration);
    }

    private static void show(final String format, final int duration, final Object... args) {
        show(String.format(format, args), duration);
    }

    private static void show(final CharSequence text, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = Toast.makeText(getApp(), text, duration);
                TextView tvMessage = sToast.getView().findViewById(android.R.id.message);
                int msgColor = tvMessage.getCurrentTextColor();
                //it solve the font of toast
                TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance);
                if (sMsgColor != COLOR_DEFAULT) {
                    tvMessage.setTextColor(sMsgColor);
                } else {
                    tvMessage.setTextColor(msgColor);
                }
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    sToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg(tvMessage);
                sToast.show();
            }
        });
    }

    private static void show(final View view, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = new Toast(getApp());
                sToast.setView(view);
                sToast.setDuration(duration);
                if (sGravity != -1 || sXOffset != -1 || sYOffset != -1) {
                    sToast.setGravity(sGravity, sXOffset, sYOffset);
                }
                setBg();
                sToast.show();
            }
        });
    }

    private static void setBg() {
        View toastView = sToast.getView();
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource);
        } else if (sBgColor != COLOR_DEFAULT) {
            Drawable background = toastView.getBackground();
            if (background != null) {
                background.setColorFilter(
                        new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN)
                );
            } else {
                ViewCompat.setBackground(toastView, new ColorDrawable(sBgColor));
            }
        }
    }

    private static void setBg(final TextView tvMsg) {
        View toastView = sToast.getView();
        if (sBgResource != -1) {
            toastView.setBackgroundResource(sBgResource);
            tvMsg.setBackgroundColor(Color.TRANSPARENT);
        } else if (sBgColor != COLOR_DEFAULT) {
            Drawable tvBg = toastView.getBackground();
            Drawable msgBg = tvMsg.getBackground();
            if (tvBg != null && msgBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
                tvMsg.setBackgroundColor(Color.TRANSPARENT);
            } else if (tvBg != null) {
                tvBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else if (msgBg != null) {
                msgBg.setColorFilter(new PorterDuffColorFilter(sBgColor, PorterDuff.Mode.SRC_IN));
            } else {
                toastView.setBackgroundColor(sBgColor);
            }
        }
    }

    private static View getView(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) getApp().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate != null ? inflate.inflate(layoutId, null) : null;
    }

    public static void temporaryShow(final int gravity, final int xOffset, final int yOffset, final float textSize, final CharSequence text, final int duration) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                cancel();
                sToast = Toast.makeText(getApp(), text, duration);
                //设置位置
                sToast.setGravity(gravity, xOffset, yOffset);
                //设置信息
                TextView tvMessage = sToast.getView().findViewById(android.R.id.message);
                int msgColor = tvMessage.getCurrentTextColor();
                TextViewCompat.setTextAppearance(tvMessage, android.R.style.TextAppearance);
                tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
                if (sMsgColor != COLOR_DEFAULT) {
                    tvMessage.setTextColor(sMsgColor);
                } else {
                    tvMessage.setTextColor(msgColor);
                }

                setBg(tvMessage);
                sToast.show();
            }
        });
    }

    public static void temporaryShowShort(final int gravity, final float textSize, final CharSequence text) {
        ToastUtils.temporaryShow(gravity, 0, 0, textSize, text, Toast.LENGTH_SHORT);
    }

    public static void temporaryShowLong(final int gravity, final float textSize, final CharSequence text) {
        ToastUtils.temporaryShow(gravity, 0, 0, textSize, text, Toast.LENGTH_LONG);
    }
}
