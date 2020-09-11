package com.bokecc.cloudclass.demo.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bokecc.ccdocview.CCDocViewManager;
import com.bokecc.ccdocview.DocView;
import com.bokecc.ccdocview.DocWebView;
import com.bokecc.ccdocview.gestureview.GestureViewBinder;
import com.bokecc.cloudclass.demo.R;
import com.bokecc.cloudclass.demo.util.DensityUtil;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

public class DocDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private CCDocViewManager mCCDocViewManager;
    private DocView mDocView;
    private DocWebView mDocWebView;
    private RelativeLayout mDocContainer;
    private static final String TAG = "DocDemoActivity";
    private GestureViewBinder mGestureViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_demo);
        mDocView = findViewById(R.id.id_lecture_doc_display);
        mDocWebView = findViewById(R.id.id_lecture_docppt_display);
        mDocContainer = findViewById(R.id.id_lecture_doc_area);
        findViewById(R.id.restoreDoc).setOnClickListener(this);

        //1、初始化文档管理类
        mCCDocViewManager = CCDocViewManager.getInstance();
        //基础监听
        mCCDocViewManager.addInteractListeners();
        //2、设置画笔 noIntercept true可以用画笔，用户角色CCAtlasClient.PRESENTER(老师红色笔)、CCAtlasClient.TALKER（学生蓝色画笔）
        mDocView.setTouchInterceptor(false, CCAtlasClient.TALKER);
        //文档边缘默认颜色为白色
//        mDocWebView.setDocBackGroundColor("#B0E2FF");
        //3.设置文档展示界面
        mDocView.setDocWebViewSetVisibility(mDocWebView);
        mCCDocViewManager.setDocHistory(mDocView, mDocWebView);
        //4.设置白板的宽高
        ViewGroup.LayoutParams params = mDocContainer.getLayoutParams();
        int width = DensityUtil.getWidth(this);
        int height = (int) (width / 16.0 * 9);
        mDocView.setWhiteboard(width, height, true);
        params.height = height;
        mDocContainer.setLayoutParams(params);//父布局

        setGestureAction(true);

    }

    /**
     * 手势控制主方法
     *
     * @param supportGesture 是否支持手势
     */
    private void setGestureAction(boolean supportGesture) {
        mDocView.setGestureAction(supportGesture);
        if (mGestureViewBinder == null) {
            mGestureViewBinder = GestureViewBinder.bind(this, mDocContainer, mDocWebView);
            mGestureViewBinder.setFullGroup(true);
            //缩放背景颜色
            mDocContainer.setBackgroundColor(Color.WHITE);
        }
        mGestureViewBinder.setGestureEnable(supportGesture);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.restoreDoc) {//还原文档
            if (mGestureViewBinder != null)
                mGestureViewBinder.setBigBack();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCCDocViewManager.release();
    }
}
