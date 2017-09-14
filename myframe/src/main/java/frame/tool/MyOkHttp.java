package frame.tool;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 *　　┏┓　　  ┏┓+ +
 *　┏┛┻━ ━ ━┛┻┓ + +
 *　┃　　　　　　  ┃
 *　┃　　　━　　    ┃ ++ + + +
 *     ████━████     ┃+
 *　┃　　　　　　  ┃ +
 *　┃　　　┻　　  ┃
 *　┃　　　　　　  ┃ + +
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃　　　　　　　　　　　
 *　　　┃　　　┃ + + + +
 *　　　┃　　　┃
 *　　　┃　　　┃ +  神兽保佑
 *　　　┃　　　┃    代码无bug！　
 *　　　┃　　　┃　　+　　　　　　　　　
 *　　　┃　 　　┗━━━┓ + +
 *　　　┃ 　　　　　　　┣┓
 *　　　┃ 　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　┃┫┫　┃┫┫
 *　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/15
 * Email：biloba12345@gamil.com
 * Info：OkHttp网络请求
 */



public class MyOkHttp {
    private static MyOkHttp mMyOkHttp;
    private OkHttpClient mClient;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private MyOkHttp(){
        mClient = new OkHttpClient();
    }

    public synchronized static MyOkHttp getInstance() {
        if ( mMyOkHttp== null) {
            mMyOkHttp = new MyOkHttp();
        }
        return mMyOkHttp;
    }

    public  String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }


    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }
}
