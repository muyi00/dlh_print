package com.dlh.open.test;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class AsyncTaskService extends AsyncTask<String, Integer, Integer> {


    private static final String TAG = "AsyncTaskService";

    private final AsyncCallBack _back;

    private LoadingDialog _loadingDialog;
    private String _maskContent = null;
    private final Context _context;
    private final boolean flag;
    private DialogInterface.OnDismissListener listener;


    public AsyncTaskService(Context context, boolean flag, AsyncCallBack back) {

        super();
        this.flag = flag;
        _context = context;
        _back = back;
    }

    public AsyncTaskService(Context context, AsyncCallBack back) {
        this(context, true, back);
    }

    public AsyncTaskService(AsyncCallBack back) {
        this(null, back);
    }

    @Override
    protected Integer doInBackground(String... params) {
        // TODO Auto-generated method stub
        if (_back != null) {
            int tt = 0;
            try {
                tt = _back.asyncProcess();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return tt;
        }
        return 0;
    }

    @Override
    public void onPreExecute() {
        if (_maskContent != null) {
            _loadingDialog = new LoadingDialog(_context).show(_maskContent);
            _loadingDialog.setCancelable(flag);
            _loadingDialog.setOnDismissListener(listener);
        }
    }

    @Override
    protected void onPostExecute(Integer Result) {
        if (_back != null) {
            _back.postUI(Result);
        }

        try {
            if ((_loadingDialog != null) && this._loadingDialog.isShowing()) {
                _loadingDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
            Log.e(TAG, "关闭弹出框异常");
        } finally {
            _loadingDialog = null;
        }
    }

    public AsyncTaskService setMaskContent(String maskContent) {
        if (_loadingDialog != null && _loadingDialog.isShowing()) {
            if (!TextUtils.isEmpty(maskContent)) {
                _loadingDialog.setContent(maskContent);
            }
        } else {
            _maskContent = maskContent;
        }
        return this;
    }

    public AsyncTaskService executeTask() {
        this.execute();
        return this;
    }

    public interface AsyncCallBack {
        int asyncProcess() throws InterruptedException;

        void postUI(int rsult);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        if (listener != null && _loadingDialog != null) {
            _loadingDialog.setOnDismissListener(listener);
        }
    }
}