package ccsskt.bokecc.base.example;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import ccsskt.bokecc.base.example.base.TitleActivity;
import ccsskt.bokecc.base.example.base.TitleOptions;
import ccsskt.bokecc.base.example.util.ParseMsgUtil;
import ccsskt.bokecc.base.example.view.ClearEditLayout;
/**
 * 作者 ${CC视频}.<br/>
 */

public class LinkActivity extends TitleActivity<LinkActivity.LinkViewHolder> {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_link;
    }

    @Override
    protected LinkViewHolder getViewHolder(View contentView) {
        return new LinkViewHolder(contentView);
    }

    @Override
    protected void onBindViewHolder(LinkViewHolder holder) {

        TitleOptions.Builder builder = new TitleOptions.Builder();
        TitleOptions options = builder.leftStatus(TitleOptions.VISIBLE).
                leftResId(R.drawable.title_back).
                rightStatus(TitleOptions.GONE).
                titleStatus(TitleOptions.VISIBLE).title("复制课堂地址").
                onTitleClickListener(new TitleOptions.OnLeftClickListener() {
                    @Override
                    public void onLeft() {
                        finish();
                    }
                }).
                build();
        setTitleOptions(options);

        holder.mClearEditLayout.setHint(getResources().getString(R.string.link_url));
        holder.mClearEditLayout.setHintColor(Color.parseColor("#cccccc"));

    }

    final class LinkViewHolder extends TitleActivity.ViewHolder {

        @BindView(R.id.id_link_url)
        ClearEditLayout mClearEditLayout;

        public LinkViewHolder(View view) {
            super(view);
        }

        @OnClick(R.id.id_link_go)
        void go() {
            String url = "https://class.csslcloud.net/index/talker/?roomid=309AA109CC2718E09C33DC5901307461&userid=83F203DAC2468694";
            if (TextUtils.isEmpty(url)) {
                showToast("请输入链接");
                return;
            }
            parseUrl(url);
        }

        private void parseUrl(String url) {
            ParseMsgUtil.parseUrl(LinkActivity.this, url, new ParseMsgUtil.ParseCallBack() {
                @Override
                public void onStart() {
                    showProgress();
                }

                @Override
                public void onSuccess() {
                    dismissProgress();
                }

                @Override
                public void onFailure(String err) {
                    toastOnUiThread(err);
                    dismissProgress();
                }
            });
        }

    }

}
