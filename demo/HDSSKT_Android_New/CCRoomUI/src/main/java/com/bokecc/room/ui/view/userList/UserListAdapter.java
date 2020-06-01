package com.bokecc.room.ui.view.userList;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bokecc.room.ui.R;
import com.bokecc.room.ui.model.RoomUser;
import com.bokecc.room.ui.view.adapter.BaseRecycleAdapter;

/**
 * 用户列表适配器
 * @author wy
 */
public class UserListAdapter extends BaseRecycleAdapter<BaseRecycleAdapter.BaseViewHolder, RoomUser>
{

    public UserListAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemView(int viewType) {
        return 0;
    }

    @Override
    public BaseViewHolder getViewHolder(View itemView, int viewType) {
        return new UserListAdapterHolder(itemView);
    }


    class UserListAdapterHolder extends BaseRecycleAdapter.BaseViewHolder {

        TextView mName;
        ImageView mContent;

        UserListAdapterHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.id_chat_img_name);
            mContent = itemView.findViewById(R.id.id_chat_img_content);
        }

    }

}
