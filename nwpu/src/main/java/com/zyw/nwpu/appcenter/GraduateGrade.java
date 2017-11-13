package com.zyw.nwpu.appcenter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;

import com.zyw.nwpu.R;
import com.zyw.nwpu.RobotActivity;
import com.zyw.nwpu.base.BaseActivity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

@ContentView(R.layout.activity_yjs__grade)
public class GraduateGrade extends BaseActivity implements OnClickListener {

    private SharedPreferences sharedPreferences;
    private EditText et_schoolnum;
    private EditText et_password;
    private String schoolnum;
    private String password;
    private static final String POSTURL = "http://222.24.192.175/npulife_api/yjs_score/yjs_score.php";
    private static String MAINBODYHTML;
    
	public static void startThis(Context cxt) {
		Intent intent = new Intent(cxt, GraduateGrade.class);
		cxt.startActivity(intent);
		((Activity) cxt).overridePendingTransition(R.anim.slide_in_right, R.anim.fade_outs);
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = this.getSharedPreferences("YJSScoreUserInfo", Context.MODE_WORLD_READABLE);
        super.onCreate(savedInstanceState);
    }

    public void initView() {
        findViewById(R.id.btn_undergraduategrade_login).setOnClickListener(this);
        et_schoolnum = (EditText) findViewById(R.id.et_yjsschoolnum_login);
        et_password = (EditText) findViewById(R.id.et_yjspassword_login);

        // 默认记住用户名
        et_schoolnum.setText(sharedPreferences.getString("userName", ""));
        et_password.setText(sharedPreferences.getString("password", ""));

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

        case R.id.btn_undergraduategrade_login:
            login();
            break;

        }
    }

    protected void login() {
        // TODO Auto-generated method stub
        final String schoolnum = et_schoolnum.getText().toString().trim();
        final String password = et_password.getText().toString().trim();

        Editor editor = sharedPreferences.edit();
        editor.putString("userName", schoolnum);
        editor.putString("password", password);
        editor.commit();

        new Thread(new Runnable() {

            @Override
            public void run() {

                DefaultHttpClient defaultclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(POSTURL);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", schoolnum));
                params.add(new BasicNameValuePair("password", password));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpResponse httpResponse = defaultclient.execute(httpPost);
                    // Log.i("my", String.valueOf(httpResponse.getStatusLine().getStatusCode()));

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        StringBuffer sb = new StringBuffer();
                        HttpEntity entity = httpResponse.getEntity();
                        MAINBODYHTML = EntityUtils.toString(entity);
                        // Log.i("my", MAINBODYHTML);
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

    protected void IsLoginSuccessful(String loginresult) {
        // TODO Auto-generated method stub
        Message msg = new Message();
        if (loginresult.contains("[]")) {
            Log.i("my", "密码或用户名错误");
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
            case 1:
                Toast.makeText(getApplicationContext(), "密码或用户名错误", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                show();
                // new AlertDialog.Builder(Grade.this).setTitle("提示")
                // .setMessage(MAINBODYHTML)
                // .setPositiveButton("确定", null).show();
                break;
            }
        }
    };

    protected void show() {
        // TODO Auto-generated method stub
        Context mContext = GraduateGrade.this;

        // 下面俩种方法都可以
        // //LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.gradeshow, null);
        setContentView(layout);

        TableLayout table = (TableLayout) findViewById(R.id.tablelayout);
        // 全部列自动填充空白
        table.setStretchAllColumns(true);
        try {
            // JSONArray jsonArray = new JSONArray(MAINBODYHTML);
            JSONObject jsonObject = new JSONObject(MAINBODYHTML);

            for (int i = 1; i < jsonObject.length() + 1; i++) {
                // "1":{
                // "class_code":" 126014",
                // "class_name":" ?003f?003f",
                // "type":"03f000d ",
                // "term":" 1",
                // "credit":" 2.0",
                // "score":" 80",
                // "get_score_time":" 15.10"
                // },
                System.out.println(jsonObject.length());
                String a = String.valueOf(i);
                String json = jsonObject.getString(a);
                System.out.println(json);
                // JSONArray jsonArray = new JSONArray(json);

                JSONObject item = new JSONObject(json);
                System.out.println(item.get("class_name"));
                String class_name = (String) item.get("class_name");
                String score = (String) item.get("score");

                for (int i1 = 0; i1 < 1; i1++) {
                    TableRow tablerow = new TableRow(GraduateGrade.this);
                    tablerow.setBackgroundColor(Color.rgb(222, 220, 210));
                    for (int j = 0; j < 2; j++) {

                        TextView testview = new TextView(GraduateGrade.this);
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
