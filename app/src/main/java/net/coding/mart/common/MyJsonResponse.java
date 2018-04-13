package net.coding.mart.common;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.loopj.android.http.JsonHttpResponseHandler;

import net.coding.mart.common.widget.SingleToast;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chenchao on 15/10/11.
 */
public abstract class MyJsonResponse extends JsonHttpResponseHandler {

    private long startTime;


    public void onMySuccess(JSONObject response) {
    }


    @Override
    public void onStart() {
        super.onStart();
        startTime = System.currentTimeMillis();
    }

    public void onMyFailure(JSONObject response) {
        if (response == null) {
            SingleToast.showMiddleToast(mActivity, NetworkImpl.ERROR_MSG_CONNECT_FAIL);
        } else {
            int code = response.optInt("code", NetworkImpl.NETWORK_CONNECT_FAIL);
            SingleToast.showErrorMsg(mActivity, code, response);
        }
    }

    private Activity mActivity;

    public MyJsonResponse(Fragment fragment) {
        this(fragment.getActivity());
    }

    public MyJsonResponse(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        int code = response.optInt("code", -1);
        if (code == 0) {
            onMySuccess(response);
        } else {
            onMyFailure(response);
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
//        Log.e("MyJsonResponse", "onFinish API execute time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    // 没有网络的情况下，会调用这个回调函数，并且 errorResponse 为 null
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        if (errorResponse == null) {
            errorResponse = sNetworkError;
        }
        onMyFailure(errorResponse);
    }

    // 服务器异常的时候会调用，这个时候返回的是 responseString，一般是 html 代e
    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        onMyFailure(sServiceError);
    }

    static JSONObject sNetworkError;
    static JSONObject sServiceError;

    static {
        try {
            String connectFailString = String.format("{\"code\":%d,\"msg\":{\"error\":\"%s\"}}",
                    NetworkImpl.NETWORK_CONNECT_FAIL, NetworkImpl.ERROR_MSG_CONNECT_FAIL);
            sNetworkError = new JSONObject(connectFailString);


            String serviceFailString = String.format("{\"code\":%d,\"msg\":{\"error\":\"%s\"}}",
                    NetworkImpl.NETWORK_ERROR_SERVICE, NetworkImpl.ERROR_MSG_SERVICE_ERROR);
            sServiceError = new JSONObject(serviceFailString);

        } catch (Exception e) {
            Global.errorLog(e);
        }
    }
}
