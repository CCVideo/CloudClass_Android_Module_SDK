package com.bokecc.room.ui.view.chat;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bokecc.room.ui.model.ChatEntity;
import com.bokecc.sskt.base.common.util.SoftKeyboardUtil;
import com.bumptech.glide.Glide;
import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.R;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.TImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.bokecc.sskt.base.CCAtlasClient.TALKER;

/**
 * 聊天控件
 * @author wy
 */
public class ChatView extends FrameLayout {

    private final String TAG = "ChatView";

    private Button btn_bg;

    private RelativeLayout room_chat_layout;
    private ImageButton room_chat_open_img;
    private EditText room_chat_input;
    private Button room_chat_send;
    /**聊天信息列表*/
    private RecyclerView chatRecyclerView;
    private ChatAdapter mChatAdapter;
    private ArrayList<ChatEntity> mChatEntities;
    private boolean isStateIDLE = true;
    private boolean isScroll = true;

    /**监听*/
    private ChatViewListener listener;

    /***/
    private Activity activity;

    /**大屏图片*/
    private ImageView room_chat_img;

    public ChatView(Context context) {
        super(context);
        initView(context);
    }

    public ChatView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView(context);
    }

    public ChatView(Context context, AttributeSet attrs,int deStyleAttr){
        super(context,attrs,deStyleAttr);
        initView(context);
    }

    /**
     * 初始化视图
     * @param context
     */
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_chat_layout,this,true);

        btn_bg = findViewById(R.id.room_chat_bg_btn);
        btn_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoftKeyBoardUtil.hideKeyboard(room_chat_input);
            }
        });

        room_chat_layout = findViewById(R.id.room_chat_layout);

        room_chat_open_img = findViewById(R.id.room_chat_open_img);
        room_chat_open_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSystemAlbum();
            }
        });
        room_chat_input = findViewById(R.id.room_chat_input);
        room_chat_send = findViewById(R.id.room_chat_send);
        room_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChat();
            }
        });

        //隐藏当前视图
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoftKeyBoardUtil.hideKeyboard(room_chat_input);
            }
        });

        chatRecyclerView = findViewById(R.id.room_chat_list);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mChatAdapter = new ChatAdapter(context, TALKER, new ChatAdapterListener() {
            @Override
            public void clickImage(String path) {
                showChatImg(path);
            }
        });
        mChatEntities = new ArrayList<>();
        mChatAdapter.bindDatas(mChatEntities);
        chatRecyclerView.setAdapter(mChatAdapter);
//        mChatList.addItemDecoration(new RecycleViewDivider(this,
//                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 2), Color.TRANSPARENT,
//                0, 0, RecycleViewDivider.TYPE_BETWEEN));
//        canScrollHorizontally();//能否横向滚动
//        canScrollVertically();//能否纵向滚动
//        scrollToPosition(int position);//滚动到指定位置
//
//        findViewByPosition(int position);//获取指定位置的Item View
//        findFirstCompletelyVisibleItemPosition();//获取第一个完全可见的Item位置
//        findFirstVisibleItemPosition();//获取第一个可见Item的位置
//        findLastCompletelyVisibleItemPosition();//获取最后一个完全可见的Item位置
//        findLastVisibleItemPosition();//获取最后一个可见Item的位置
        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isStateIDLE = true;
                    if (!recyclerView.canScrollVertically(1)) {
                        Log.i(TAG, "onScrollStateChanged: bottom");
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        Log.i(TAG, "onScrollStateChanged: top");
                    }
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition == recyclerView.getLayoutManager().getItemCount() - 1) {
                        Log.i(TAG, "onScrollStateChanged: last visible");
                        if (!isScroll) {
                            isScroll = true;
                        }
                    }
                } else {
                    isStateIDLE = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { // 手指向上滑 内容显示下面的
                    Log.i(TAG, "onScrolled: down");
                } else { // 手指向下滑 内容显示上面的
                    if (!isStateIDLE && isScroll) {
                        isScroll = false;
                    }
                    Log.i(TAG, "onScrolled: up");
                }
            }
        });

        //全屏图控件
        room_chat_img = findViewById(R.id.room_chat_img);
        room_chat_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBigPicture();
            }
        });
    }

    public void hideBigPicture() {
        room_chat_img.setVisibility(View.GONE);
        room_chat_img.setImageBitmap(null);
    }

    public boolean showBigPicture(){
        return room_chat_img!=null&&room_chat_img.getVisibility()==VISIBLE;
    }
    /**
     * 设置监听
     * @param listener
     */
    public void setListener(Activity activity,ChatViewListener listener){
        this.activity = activity;
        this.listener = listener;
        onSoftInputChange(activity);
    }

    // 软键盘监听
    private SoftKeyboardUtil mSoftKeyBoardUtil;

    /**
     * 打开聊天
     */
    public void openChat(){
        mSoftKeyBoardUtil.showKeyboard(room_chat_input);
    }

    /**
     * 发送聊天信息
     */
    private void sendChat(){
        try {
            String content = room_chat_input.getText().toString();
            if (TextUtils.isEmpty(content) || content.trim().isEmpty()) {
                Tools.showToast("禁止发送空消息");
                return;
            }
            listener.sendMsg(content);
            room_chat_input.setText("");
            mSoftKeyBoardUtil.hideKeyboard(room_chat_input);
//            content = transformMsg(content);
        } catch (Exception e) {
            Tools.showToast("发送失败："+e.getMessage());
        }
    }

    /**
     * 初始化键盘监听
     * @param context
     */
    private void onSoftInputChange(Context context) {
        mSoftKeyBoardUtil = new SoftKeyboardUtil(context);
        mSoftKeyBoardUtil.observeSoftKeyboard(activity, new SoftKeyboardUtil.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean isShow, int changeHeight) {
                if (isShow) {
                    room_chat_layout.setVisibility(View.VISIBLE);
                    btn_bg.setVisibility(View.VISIBLE);
                } else {
                    room_chat_layout.setVisibility(View.GONE);
                    btn_bg.setVisibility(View.GONE);
                }
            }
        });
    }



    public final int REQUEST_SYSTEM_PICTURE = 0;


    /**
     * 添加图片
     */
    private void openSystemAlbum(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, REQUEST_SYSTEM_PICTURE);
    }

    /**
     * 处理系统选择器返回的图片
     * @param data
     */
    public void handlePic(final Intent data) {
        try {
            if (data == null) {
                Tools.showToast("图片加载失败");
                return;
            }
            // 获得图片的uri
            Uri imageUri = data.getData();
            if (imageUri == null) {
                Tools.showToast("图片加载失败");
                return;
            }
            //获取图片的路径
            String imgPath = getImageAbsolutePath(imageUri);
            if (!TextUtils.isEmpty(imgPath)) {
                compressBitmap(imgPath, readPictureDegree(imgPath));
            } else {
                Tools.showToast("图片加载失败");
            }
        } catch (IOException e) {
            Tools.showToast("图片加载失败");
        }
    }

    /**
     * 获取图片路径
     * @param imageUri
     * @return
     */
    private String getImageAbsolutePath(Uri imageUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(activity, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * ？？？
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = activity.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度 获取从相册中选中图片的角度
     */
    private int readPictureDegree(String path) throws IOException {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    //    private ArrayList<TImage> images = new ArrayList<>();
    private CompressConfig config = new CompressConfig.Builder()
            .enableQualityCompress(false)
            .setMaxSize(5 * 1024)
            .create();

    /**
     * 压缩图片
     * @param imgPath
     * @param degree
     */
    private void compressBitmap(String imgPath, int degree) {
        try {
            ArrayList<TImage> images = new ArrayList<>();
            File file = new File(imgPath);
            TImage image = TImage.of(file.getAbsolutePath(), TImage.FromType.OTHER);
            images.add(image);
            CompressImage compressImage = CompressImageImpl.of(activity, config, images, new MyCompressListener(degree));
            compressImage.compress();
        } catch (Exception e) {
            Tools.showToast("发送图片失败，查看图片是否有问题");
        }
    }

    /**
     * 压缩回调
     */
    private class MyCompressListener implements CompressImage.CompressListener {

        private int degree;

        MyCompressListener(int degree) {
            this.degree = degree;
        }

        @Override
        public void onCompressSuccess(ArrayList<TImage> images) {
            //图片压缩成功
            BufferedOutputStream bos = null;
            try {
                File file = new File(images.get(0).getCompressPath());
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (degree != 0 && bitmap != null) {
                    Matrix m = new Matrix();
                    m.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                    Bitmap temp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    file.deleteOnExit();
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    temp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bitmap.recycle();
                }
                listener.sendPic(file);
            } catch (Exception e) {
            } finally {
                images.clear();
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        @Override
        public void onCompressFailed(ArrayList<TImage> images, String msg) {
            //图片压缩失败
            Tools.showToast("图片压缩失败,停止上传");
        }
    }

    /**
     * 更新聊天列表
     * @param chatEntity
     */
    public void updateChatList(final ChatEntity chatEntity) {

        mChatEntities.add(chatEntity);
        if(!mChatEntities.isEmpty()){
            if (chatRecyclerView.getVisibility()!=VISIBLE) {
                chatRecyclerView.setVisibility(VISIBLE);
            }
        }
        mChatAdapter.notifyItemInserted(mChatEntities.size() - 1);
        if (!isScroll) {
            chatRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);// 进行定位
        }
    }

    /**
     * 显示全屏图片
     * @param path
     */
    public void showChatImg(String path){
        Glide.with(activity).load(path).fitCenter().into(room_chat_img);
        room_chat_img.setVisibility(View.VISIBLE);
    }

    /**
     *  设置聊天 View 的背景
     */
    public void setChatViewNoneBg(boolean isNone) {
        chatRecyclerView.setBackgroundResource((isNone||mChatEntities.isEmpty())?R.color.chat_bg:R.drawable.shape_chat_bg);
    }

}
