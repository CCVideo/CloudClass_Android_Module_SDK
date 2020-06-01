package com.bokecc.room.ui.view.userList;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.bokecc.common.dialog.CustomDialog;
import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.RoomUser;
import com.bokecc.room.ui.view.menu.MenuTopView;
import com.bokecc.room.ui.view.widget.RecycleViewDivider;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户列表视图
 *
 * @author wy
 */
public class UserListDialog extends CustomDialog implements View.OnClickListener {

    private MenuTopView.MenuTopListener mListener;
    private RecyclerView recyclerView;

    private RoomUserAdapter userAdapter;

    private List<RoomUser> mRoomUsers = new ArrayList<>();


    private Context context;
    /**
     * 标题
     */
    private TextView title;

    public UserListDialog(Context context, MenuTopView.MenuTopListener listener) {
        super(context);
        this.context = context;
        this.mListener = listener;
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        if (!isShowing()) show();

        CCAtlasClient.getInstance().getRewardHistory(new CCAtlasCallBack<ArrayList<CCUser>>() {
            @Override
            public void onSuccess(ArrayList<CCUser> users) {
                transformUser(users);
                title.setText(users.size()+"个成员");
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errCode, String errMsg) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.view_user_list_layout);

        setCanceledOnTouchOutside(false);
        super.onCreateMatchParent(savedInstanceState);

        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return false;
            }
        });

        initView();
    }

    /**
     * 初始化视图和适配器
     */
    private void initView() {
        title = findViewById(R.id.id_list_title);
        findViewById(R.id.id_list_back).setOnClickListener(this);

        userAdapter = new RoomUserAdapter(context, CCAtlasClient.getInstance().getRole(), 0,mListener);
        userAdapter.bindDatas(mRoomUsers);

        recyclerView = findViewById(R.id.user_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecycleViewDivider(context,
                LinearLayoutManager.HORIZONTAL, 1, Color.parseColor("#E8E8E8"),
                0, 0, RecycleViewDivider.TYPE_BOTTOM));
        recyclerView.setAdapter(userAdapter);

        findViewById(R.id.id_list_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 更新数据
     *
     * @param users
     */
    public void updateData(ArrayList<CCUser> users) {
        transformUser(users);
        title.setText(users.size()+"个成员");
        userAdapter.notifyDataSetChanged();
        if (!isShowing()) show();
    }

    /**
     * 转换用户
     *
     * @param users
     */
    private void transformUser(ArrayList<CCUser> users) {
        if (users == null || users.size() <= 0) {
            return;
        }
        mRoomUsers.clear();

        ArrayList<RoomUser> compareUsers = new ArrayList<>();
        int index = 0;
        for (CCUser user : users) {
//            if (mUserPopup.isShowing()) { // 如果弹出框显示，更新当前选中的用户状态
//                if (mCurUser != null && user.getUserId().equals(mCurUser.getUserId())) {
//                    updateOrShowUserPopup(user);
//                }
//            }
            RoomUser roomUser = new RoomUser();
            roomUser.setUser(user);
            if (user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_IN_MAI ||
                    user.getLianmaiStatus() == CCAtlasClient.LIANMAI_STATUS_UP_MAI) {
                compareUsers.add(roomUser);
            } else {
                if (user.getUserRole() == CCAtlasClient.PRESENTER) {
                    mRoomUsers.add(0, roomUser);
                    index++;
                } /*else if (user.getLianmaiStatus() == 3) { // 这段代码实现将连麦中的学生位置定位在老师的下面
                    mRoomUsers.add(index, roomUser);
                    index++;
                }*/ else {
                    mRoomUsers.add(roomUser);
                }
            }
        }
        Collections.sort(compareUsers, new RoomUserComparator());
        int queueIndex = 1;
        for (RoomUser queueUser :
                compareUsers) {
            queueUser.setMaiIndex(queueIndex++);
        }
        mRoomUsers.addAll(index, compareUsers);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_list_back) {
            dismiss();
        }
    }


}
