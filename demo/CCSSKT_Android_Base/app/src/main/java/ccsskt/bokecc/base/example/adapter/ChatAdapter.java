package ccsskt.bokecc.base.example.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.bean.CCUser;
import com.bokecc.sskt.base.bean.ChatMsg;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.regex.Pattern;
import butterknife.BindView;
import butterknife.ButterKnife;

import ccsskt.bokecc.base.example.R;
import ccsskt.bokecc.base.example.base.BaseRecycleAdapter;
import ccsskt.bokecc.base.example.bean.ChatEntity;
import ccsskt.bokecc.base.example.bean.EmojiUtil;
import ccsskt.bokecc.base.example.util.DensityUtil;
import ccsskt.bokecc.base.example.view.ZoneLongPressTextView;

/**
 * 作者 ${CC视频}.<br/>
 * 聊天适配器
 */

public class ChatAdapter extends BaseRecycleAdapter<BaseRecycleAdapter.BaseViewHolder, ChatEntity> {

    private EventBus mEventBus;
    private CCAtlasClient mInteractSession;
    private int mRole;
    private Drawable mTeacherFlag;

    private PopupWindow mCopyPop;
    private FrameLayout mCopyView;
    private int[] mLocation = new int[2];
    private int mPopupWidth;
    private int mPopupHeight;
    private ClipboardManager mClipboardManager;

    private static final String NAME_B_CONTENT = "：";
    private static final String NAME_TEACHER_FLAG = "[t_f]";
    private static final String NAME_TEACHER_FLAG_DIV = " ";

    public ChatAdapter(Context context, int role) {
        super(context);
        mEventBus = EventBus.getDefault();
        mInteractSession = CCAtlasClient.getInstance();
        mRole = role;
        mTeacherFlag = mContext.getResources().getDrawable(R.drawable.chat_teacher_flag);
        mTeacherFlag.setBounds(0, 0, mTeacherFlag.getIntrinsicWidth(), mTeacherFlag.getIntrinsicHeight());
        View popupView = LayoutInflater.from(context).inflate(R.layout.copy_popup_layout, null);
        mCopyView = (FrameLayout) popupView.findViewById(R.id.id_copy_layout);
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mCopyPop = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mCopyPop.setBackgroundDrawable(new BitmapDrawable());
        mPopupWidth = popupView.getMeasuredWidth();
        mPopupHeight = popupView.getMeasuredHeight();
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onBindViewHolder(final BaseRecycleAdapter.BaseViewHolder holder, int position) {
        final ChatEntity chatEntity = mDatas.get(position);
        if (holder instanceof ChatViewHolder) {
            String msg;
            int offset;
            // FIXME: 兼容我们直播客户端 请根据需求自己处理
            mUrlIndices.clear();
            final String realData = transformMsg(chatEntity.getMsg());
            if (chatEntity.getUserRole() == CCAtlasClient.PRESENTER || chatEntity.getUserRole()
                    == CCAtlasClient.ASSISTANT) {
                msg = NAME_TEACHER_FLAG + NAME_TEACHER_FLAG_DIV + chatEntity.getUserName() + NAME_B_CONTENT + realData;
                offset = (NAME_TEACHER_FLAG + NAME_TEACHER_FLAG_DIV + chatEntity.getUserName() + NAME_B_CONTENT).length();
            } else {
                msg = chatEntity.getUserName() + NAME_B_CONTENT + realData;
                offset = (chatEntity.getUserName() + NAME_B_CONTENT).length();
            }
            SpannableString ss = new SpannableString(msg);
            if (chatEntity.getUserRole() == CCAtlasClient.PRESENTER ||chatEntity.getUserRole() ==
                    CCAtlasClient.ASSISTANT) {
                ImageSpan imageSpan = new ImageSpan(mTeacherFlag, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(imageSpan, 0, NAME_TEACHER_FLAG.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            int userNameColor;
            if (chatEntity.isSelf()) {
                userNameColor = Color.parseColor("#FFFC6551");
            } else {
                userNameColor = Color.parseColor("#FFF27C19");
            }
            ss.setSpan(new ForegroundColorSpan(userNameColor),
                    0, offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")),
                    offset,
                    msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (mRole == CCAtlasClient.PRESENTER  || mRole == CCAtlasClient.ASSISTANT && !chatEntity.isSelf()) {
                ss.setSpan(new NameClickSpan(position), 0, offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
//            Pattern pattern = Pattern.compile("((http|https)://|(www))(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");
//            Matcher matcher = pattern.matcher(chatEntity.getMsg());
//            while (matcher.find()) {
//                ss.setSpan(new UrlClickSpan(matcher.group()), offset + matcher.start(), offset + matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
            for (UrlIndex urlIndex :
                    mUrlIndices) {
                ss.setSpan(new UrlClickSpan(urlIndex.url), offset + urlIndex.start, offset + urlIndex.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ((ChatViewHolder) holder).mContent.setMovementMethod(LinkMovementClickMethod.getInstance());
            ((ChatViewHolder) holder).mContent.setText(EmojiUtil.parseFaceMsg(mContext, ss, offset));
            ((ChatViewHolder) holder).mContent.setLongPressZone(offset, ss.length());
            ((ChatViewHolder) holder).mContent.setOnZoneLongPressedListener(new ZoneLongPressTextView.OnZoneLongPressedListener() {
                @Override
                public void onLongClick(View v) {
                    mCopyPop.setOnDismissListener(new LongPressedDismissListener(((ChatViewHolder) holder).mLongpressFlagLayout));
                    mCopyView.setOnClickListener(new CopyOnCLickListener(realData));
                    v.getLocationOnScreen(mLocation);
                    mCopyPop.showAtLocation(v, Gravity.NO_GRAVITY, (mLocation[0] + v.getWidth() / 2) - mPopupWidth / 2,
                            mLocation[1] - mPopupHeight - DensityUtil.dp2px(mContext, 5));
                    ((ChatViewHolder) holder).mLongpressFlagLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {
            if (chatEntity.isSelf()) {
                ((ChatImageViewHolder) holder).mName.setTextColor(Color.parseColor("#FFFC6551"));
            } else {
                ((ChatImageViewHolder) holder).mName.setTextColor(Color.parseColor("#FFF27C19"));
            }
            String msg;
            if (chatEntity.getUserRole() == CCAtlasClient.PRESENTER || chatEntity.getUserRole() == CCAtlasClient.ASSISTANT ) {
                msg = NAME_TEACHER_FLAG + NAME_TEACHER_FLAG_DIV + chatEntity.getUserName();
            } else {
                msg = chatEntity.getUserName();
            }
            SpannableString ss = new SpannableString(msg);
            if (chatEntity.getUserRole() == CCAtlasClient.PRESENTER || chatEntity.getUserRole() == CCAtlasClient.ASSISTANT) {
                ImageSpan imageSpan = new ImageSpan(mTeacherFlag, ImageSpan.ALIGN_BOTTOM);
                ss.setSpan(imageSpan, 0, NAME_TEACHER_FLAG.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            if (mRole == CCAtlasClient.PRESENTER || mRole == CCAtlasClient.ASSISTANT && !chatEntity.isSelf()) {
                ss.setSpan(new NameClickSpan(position), 0, chatEntity.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ((ChatImageViewHolder) holder).mName.setMovementMethod(LinkMovementMethod.getInstance());
            ((ChatImageViewHolder) holder).mName.setText(ss);
            Glide.with(mContext).load(chatEntity.getMsg()).asBitmap().thumbnail(0.3f).
                    into(((ChatImageViewHolder) holder).mContent);
            ((ChatImageViewHolder) holder).mContent.setOnClickListener(new ImageClickListener(position));
        }
    }

//    Pattern pattern = Pattern.compile("(([hH][tT]{2}[pP]|[hH][tT]{2}[pP][sS])://|(www))(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");

    private ArrayList<UrlIndex> mUrlIndices = new ArrayList<>();

    Pattern pattern = Pattern.compile("(([hH][tT]{2}[pP]|[hH][tT]{2}[pP][sS]|[fF][tT][pP])://|)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");

    private String transformMsg(String msg) {
        // FIXME: 2017/7/26 兼容我们直播客户端，进行字符串处理
        String datas[] = msg.split(" ");
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        for (String data :
                datas) {
            if (data.startsWith("[uri_") && data.endsWith("]")) {
                String url = data.substring("[uri_".length(), data.length() - 1);
                if (pattern.matcher(url).find()) {
                    sb.append(url);
                    UrlIndex urlIndex = new UrlIndex();
                    urlIndex.url = url;
                    urlIndex.start = offset;
                    urlIndex.end = urlIndex.start + url.length();
                    mUrlIndices.add(urlIndex);
                    offset = url.length();
                } else {
                    sb.append(url);
                    offset = url.length();
//                    sb.append(data); // 原内容
//                    offset = data.length();
                }
            } else {
                sb.append(data);
                offset = data.length();
            }
        }
        return sb.toString();
    }

    @Override
    public int getItemViewType(int position) {
        return getDatas().get(position).getType();
    }

    @Override
    public int getItemView(int viewType) {
        if (viewType == ChatMsg.TYPE_TXT) {
            return R.layout.chat_item_layout;
        } else {
            return R.layout.chat_img_layout;
        }
    }

    @Override
    public BaseRecycleAdapter.BaseViewHolder getViewHolder(View itemView, int viewType) {
        if (viewType == ChatMsg.TYPE_TXT) {
            return new ChatViewHolder(itemView);
        } else {
            return new ChatImageViewHolder(itemView);
        }
    }

    private final class CopyOnCLickListener implements View.OnClickListener {

        private String mText;

        CopyOnCLickListener(String text) {
            mText = text;
        }

        @Override
        public void onClick(View v) {
            //将文本数据复制到剪贴板
            mClipboardManager.setText(mText);
            mCopyPop.dismiss();
        }
    }

    private final class LongPressedDismissListener implements PopupWindow.OnDismissListener {

        private View mView;

        LongPressedDismissListener(View view) {
            mView = view;
        }

        @Override
        public void onDismiss() {
            mView.setVisibility(View.INVISIBLE);
        }
    }

    private final class ImageClickListener implements View.OnClickListener {

        private int mPosition;

        ImageClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            String imgPath = getDatas().get(mPosition).getMsg();
//            mEventBus.post(new MyEBEvent(Config.CHAT_IMG, imgPath));
        }

    }

    // 解决长按和点击的冲突
    private static final class LinkMovementClickMethod extends LinkMovementMethod{

        private long lastClickTime;

        private static final long CLICK_DELAY = ViewConfiguration.getLongPressTimeout();

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y); // 得到这个点在垂直方向上的行数
                int off = layout.getOffsetForHorizontal(line, x); // 得到触摸点在某一行水平方向上的偏移量

                ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        if(System.currentTimeMillis() - lastClickTime < CLICK_DELAY){
                            link[0].onClick(widget);
                        }
                    } else {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                        lastClickTime = System.currentTimeMillis();
                    }

                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }
            return super.onTouchEvent(widget, buffer, event);
        }

        public static LinkMovementClickMethod getInstance(){
            if(null == sInstance){
                sInstance = new LinkMovementClickMethod();
            }
            return sInstance;
        }

        private static LinkMovementClickMethod sInstance;

    }

    private final class UrlClickSpan extends ClickableSpan {

        private String mUrl;

        UrlClickSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
//            if (!mUrl.startsWith("http") && !mUrl.startsWith("https")) {
//                mUrl = "http://" + mUrl;
//            }
            String datas[] = mUrl.split(":");
            String protocol = datas[0];
            String path = mUrl.substring(protocol.length());
            String uriStr = protocol.toLowerCase() + path;
            Log.e("tag", "onClick: " + uriStr);
            Uri uri = Uri.parse(uriStr);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(it);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // TODO: 2017/7/7  是否需要下划线 颜色控制
            ds.setColor(Color.parseColor("#ffffff"));
            ds.setUnderlineText(true); //去掉下划线
        }

    }

    private final class NameClickSpan extends ClickableSpan {

        private int mPosition;

        NameClickSpan(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View widget) {
            String userid = getDatas().get(mPosition).getUserId();
            if (mInteractSession.getUserList() != null && mInteractSession.getUserList().size() > 0) {
                for (CCUser user :
                        mInteractSession.getUserList()) {
                    if (userid.equals(user.getUserId())) {
                        mEventBus.post(user);
                        return;
                    }
                }
            }
            // 如果是旁听角色
            if (getDatas().get(mPosition).getUserRole() == CCAtlasClient.AUDITOR) {
                CCUser user = new CCUser();
                user.setUserName(getDatas().get(mPosition).getUserName());
                user.setUserId(userid);
                user.setUserRole(getDatas().get(mPosition).getUserRole());
                mEventBus.post(user);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false); //去掉下划线
        }

    }

    final class ChatViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_chat_item_content)
        ZoneLongPressTextView mContent;
        @BindView(R.id.id_chat_item_longpress_flag)
        FrameLayout mLongpressFlagLayout;

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final class ChatImageViewHolder extends BaseRecycleAdapter.BaseViewHolder {

        @BindView(R.id.id_chat_img_name)
        TextView mName;
        @BindView(R.id.id_chat_img_content)
        ImageView mContent;

        ChatImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private final class UrlIndex {
        String url;
        int start;
        int end;
    }

}
