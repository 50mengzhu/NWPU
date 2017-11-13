package com.zyw.nwpu.appcenter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xutils.view.annotation.ContentView;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zyw.nwpu.R;
import com.zyw.nwpu.base.BaseActivity;
import com.zyw.nwpulib.utils.CommonUtil;

@ContentView(R.layout.activity_grade)
public class UndergraduateGrade extends BaseActivity implements OnClickListener {

    protected static String VERIFATIONURL = null;
    protected static final String LOGINURL = "http://222.24.192.175/npulife_api/score/score_form.php";
    protected static final String POSTURL = "http://222.24.192.175/npulife_api/score/score_get.php";
    protected static String GetLt = null;
    protected static String JSESSIONID;
    protected String LT;
    protected Bitmap bmVerifation;
    protected String MAINBODYHTML;
    protected String cookie;
    protected String PHPSESSID;
    private EditText et_schoolnum, etVerifation;
    private EditText et_password;
    private ImageView ivVerifation;
    static CookieStore cookieStore = null;
    private SharedPreferences sharedPreferences;
    
	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, UndergraduateGrade.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = this.getSharedPreferences("ScoreUserInfo", Context.MODE_WORLD_READABLE);

        super.onCreate(savedInstanceState);

        DoGetVerifation();
        // 研究生
        // MAINBODYHTML = YJSscore.get();
        // show();
    }

    public void initView() {
        findViewById(R.id.btn_login_login).setOnClickListener(this);
        et_schoolnum = (EditText) findViewById(R.id.et_schoolnum_login);
        et_password = (EditText) findViewById(R.id.et_password_login);
        ivVerifation = (ImageView) findViewById(R.id.iv_verifation);
        etVerifation = (EditText) findViewById(R.id.etVerifation);

        // 默认记住用户名
        et_schoolnum.setText(sharedPreferences.getString("userName", ""));
        et_password.setText(sharedPreferences.getString("password", ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // DoGetVerifation();
    }

    /**
     * 取得验证码
     */
    private void DoGetVerifation() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i("my", "begin");
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet get = new HttpGet(LOGINURL);
                HttpResponse response = null;
                try {
                    response = httpclient.execute(get);
                    setCookieStore(response);
                    // if (cookie.equals(null)) {
                    // setCookieStore(response);
                    // }
                    StringBuffer sb = new StringBuffer();
                    HttpEntity entity = response.getEntity();
                    GetLt = EntityUtils.toString(entity);
                    // Log.i("my",GetLt);
                    // IsLoginSuccessful(MAINBODYHTML);
                    Document doc = Jsoup.parse(GetLt);
                    // 查找第一个a元素
                    Element lt = doc.select("a").first();
                    LT = lt.text();
                    Log.i("my", LT);
                } catch (ClientProtocolException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                VERIFATIONURL = "http://222.24.192.175/npulife_api/score/verifycode/verifyCode_" + JSESSIONID + ".jpg";
                HttpGet httpget = new HttpGet(VERIFATIONURL);
                httpget.addHeader("PHPSESSID", JSESSIONID);
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse httpResponse = client.execute(httpget);
                    Log.i("my", VERIFATIONURL);
                    byte[] bytes = new byte[1024];
                    bytes = EntityUtils.toByteArray(httpResponse.getEntity());
                    bmVerifation = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bmVerifation == null)
                    Toast.makeText(getApplicationContext(), "获取验证码失败请检查网络设置", Toast.LENGTH_SHORT).show();
                Message msg = new Message();
                msg.arg1 = 10;
                handler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * @param httpResponse
     *            储存cookie
     */
    public static void setCookieStore(HttpResponse httpResponse) {
        System.out.println("----setCookieStore");
        cookieStore = new BasicCookieStore();
        // JSESSIONID
        String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
        System.out.println(setCookie);
        JSESSIONID = setCookie.substring("PHPSESSID=".length(), setCookie.indexOf(";"));
        System.out.println("JSESSIONID:" + JSESSIONID);
        // 新建一个Cookie
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", JSESSIONID);
        cookie.setVersion(0);
        cookie.setDomain("127.0.0.1");
        cookie.setPath("/CwlProClient");
        // cookie.setAttribute(ClientCookie.VERSION_ATTR, "0");
        // cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "127.0.0.1");
        // cookie.setAttribute(ClientCookie.PORT_ATTR, "8080");
        // cookie.setAttribute(ClientCookie.PATH_ATTR, "/CwlProWeb");
        cookieStore.addCookie(cookie);
    }

    public void onClick(View v) {
        switch (v.getId()) {

        case R.id.btn_login_login:
            login();
            break;

        }
    }

    // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    private void login() {
        final String schoolnum = et_schoolnum.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String verifation = etVerifation.getText().toString().trim();

        Editor editor = sharedPreferences.edit();
        editor.putString("userName", schoolnum);
        editor.putString("password", password);
        editor.commit();

        // if (TextUtils.isEmpty(schoolnum)) {
        // CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
        // "请输入学号");
        // return;
        // }

        // if (schoolnum.length() != 10) {
        // CommonUtil.showShortToast(getApplicationContext(), "请输入10位学号");
        // return;
        // }

        // if (TextUtils.isEmpty(password)) {
        // CommonUtil.ToastUtils.showShortToast(getApplicationContext(),
        // "请输入密码");
        // return;
        // }

        if (TextUtils.isEmpty(verifation)) {
            CommonUtil.ToastUtils.showShortToast(getApplicationContext(), "请输入验证码");
            return;
        }
        DoLogin(schoolnum, password, verifation);
    }

    private void DoLogin(final String user, final String password, final String verifation) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                DefaultHttpClient defaultclient = new DefaultHttpClient();
                defaultclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
                HttpPost httpPost = new HttpPost(POSTURL);
                PHPSESSID = "PHPSESSID=" + JSESSIONID;
                httpPost.addHeader("Cookie", PHPSESSID);
                // Log.i("my",VERIFATIONURL);
                // Log.i("my", verifation);

                // 设置post参数
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", user));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("validatecode", verifation));
                params.add(new BasicNameValuePair("lt", LT));
                // Log.i("my", LT);
                params.add(new BasicNameValuePair("subLogin", "提交"));

                // 获得个人主界面的HTML
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpResponse httpResponse = defaultclient.execute(httpPost);
                    // Log.i("my", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        HttpEntity entity = httpResponse.getEntity();
                        MAINBODYHTML = EntityUtils.toString(entity);
                        // Log.i("my",MAINBODYHTML);
                        IsLoginSuccessful(MAINBODYHTML);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void IsLoginSuccessful(String loginresult) {
        // Document doc = Jsoup.parse(loginresult);
        // Elements elements = doc.select("span");
        // String highTemperature = elements.get(0).text();
        // System.out.println(highTemperature);

        Message msg = new Message();
        if (loginresult.contains("验证码")) {
            // 刷新验证码
            DoGetVerifation();
            // Log.i("my", "验证码错误");
            msg.arg1 = 0;
            handler.sendMessage(msg);
        } else if (loginresult.contains("密码")) {
            DoGetVerifation();
            // Log.i("my", "密码或用户名错误");
            msg.arg1 = 1;
            handler.sendMessage(msg);
        } else {
            msg.arg1 = 2;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
            case 0:
                Toast.makeText(getApplicationContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getApplicationContext(), "密码或用户名错误", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                show();
                // new AlertDialog.Builder(Grade.this).setTitle("提示")
                // .setMessage(MAINBODYHTML)
                // .setPositiveButton("确定", null).show();
                break;
            // 显示验证码图片
            case 10:
                ivVerifation.setImageBitmap(bmVerifation);
                break;
            }
        }
    };

    public void show() {
        Context mContext = UndergraduateGrade.this;

        // 下面俩种方法都可以
        // //LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.gradeshow, null);
        setContentView(layout);

        TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
        // 全部列自动填充空白
        table.setStretchAllColumns(true);

        try {
            JSONArray jsonArray = new JSONArray(MAINBODYHTML);

            for (int i = 0; i < jsonArray.length(); i++) {
                // "class_code":" U08M11076",
                // "class_name":" 半导体物理",
                // "class_count":" 2",
                // "finish_count":" 2",
                // "score":" 77",
                // "is_pass":" 是"
                org.json.JSONObject item = jsonArray.getJSONObject(i);
                System.out.println(item.get("class_name"));
                String class_name = (String) item.get("class_name");
                String score = (String) item.get("score");

                for (int i1 = 0; i1 < 1; i1++) {
                    TableRow tablerow = new TableRow(UndergraduateGrade.this);
                    tablerow.setBackgroundColor(Color.rgb(222, 220, 210));
                    for (int j = 0; j < 2; j++) {

                        TextView testview = new TextView(UndergraduateGrade.this);
                        testview.setBackgroundResource(R.drawable.gardeshowtableshape);
                        if (j == 0) {
                            testview.setText(class_name);
                        } else {
                            testview.setText(score);

                        }
                        // testview.setTextSize(20);

                        // 设置TextView的宽度为N个字符的宽度。
                        testview.setEms(7);
                        // 设置文本是否包含顶部和底部额外空白，默认为true。
                        testview.setIncludeFontPadding(false);
                        tablerow.addView(testview);
                    }
                    table.addView(tablerow, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
                            LayoutParams.WRAP_CONTENT));
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
