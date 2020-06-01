package com.bokecc.room.ui.utils;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.bokecc.common.utils.Tools;
import com.bokecc.room.ui.listener.ImageUpdateListener;
import com.bokecc.room.ui.view.doc.CCDocView;
import com.bokecc.sskt.base.CCAtlasClient;
import com.bokecc.sskt.base.callback.CCAtlasCallBack;
import com.bokecc.sskt.base.common.network.OkhttpNet.OKHttpStatusListener;
import com.bokecc.sskt.base.common.network.OkhttpNet.OKHttpUtil;
import com.bokecc.sskt.base.common.network.OkhttpNet.ProgressListener;
import com.bokecc.sskt.base.common.util.CCInteractSDK;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.compress.CompressImage;
import com.jph.takephoto.compress.CompressImageImpl;
import com.jph.takephoto.model.TImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.bokecc.room.ui.config.Config.REQUEST_SYSTEM_PICTURE_UPDATE;

/**
 * @author swh
 * @Description java类作用
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";
    public static String checkImg(Uri imageUri) {
        if (imageUri == null) {
            Tools.showToast("图片加载失败");
            return null;
        }
        String imgPath = getImageAbsolutePath(imageUri);
        if (imgPath == null) {
            Tools.showToast("图片加载失败");
            return null;
        }
        return imgPath;
    }
    public static  String getImageAbsolutePath(Uri imageUri) {
        if ( DocumentsContract.isDocumentUri(CCInteractSDK.getInstance().getContext(), imageUri)) {
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
    public static String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = CCInteractSDK.getInstance().getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 进行图片压缩
     */



    public static void compressBitmap(String imgPath, final int type, final ImageUpdateListener mListener) {

        try {
           final int degree = readPictureDegree(imgPath);
            CompressConfig config = new CompressConfig.Builder()
                    .enableQualityCompress(false)
                    .setMaxSize(5 * 1024)
                    .create();
            ArrayList<TImage> images = new ArrayList<>();
            images.clear();
            File file = new File(imgPath);
            TImage image = TImage.of(file.getAbsolutePath(), TImage.FromType.OTHER);
            images.add(image);
            CompressImage compressImage = CompressImageImpl.of(CCInteractSDK.getInstance().getContext(), config, images, new CompressImage.CompressListener(){

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
                        /*if (type == REQUEST_SYSTEM_PICTURE_CHAT) {
//                    updatePic1(file);
                            mCCChatManager.updatePic1(file);
                        } else*/ if (type == REQUEST_SYSTEM_PICTURE_UPDATE) {
                            updateImage2Doc(file,mListener);
                        }
                    } catch (Exception e) {
                        Tools.handleException(TAG,e);
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
                    mListener.updateFailure("图片压缩失败,停止上传");
                }

            });
            compressImage.compress();
        } catch (Exception e) {
            mListener.updateFailure("发送图片失败，查看图片是否有问题");
        }
    }

    /**
     * 上传图片处理逻辑
     *
     * @param file
     * @param mListener
     */
    private static void updateImage2Doc(final File file,final ImageUpdateListener mListener) {
        CCAtlasClient.getInstance().getUpLoadImageUrl(file.getName(), file.length(), new CCAtlasCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, result);
                try {
                    OKHttpUtil.updateFileWithProgress(CCInteractSDK.getInstance().getContext(),
                            result, file, null, new OKHttpStatusListener() {
                                @Override
                                public void onFailed(int code, String errorMsg) {
                                    Tools.log(TAG, "onFailed: " + code + "-" + errorMsg);
                                    mListener.updateFailure(errorMsg);


                                }

                                @Override
                                public void onSuccessed(String result) {
                                    Tools.log(TAG,result);
                                    try {
                                        JSONObject object = new JSONObject(result);
                                        boolean res = object.getBoolean("success");
                                        if (res) {
                                            JSONObject docObject = object.getJSONObject("datas");
                                            String docId = docObject.getString("docId");
                                            mListener.updateSuccess(docId);

                                        } else {
                                            mListener.updateFailure("上传文档失败");

                                        }
                                    } catch (Exception e) {

                                        mListener.updateFailure("上传文档失败");
                                        Tools.handleException(TAG,e);
                                    }
                                }
                            }, new ProgressListener() {
                                @Override
                                public void onProgressChanged(long numBytes, long totalBytes) {
                                    mListener.updateLoading(numBytes*100/totalBytes);
                                }

                                @Override
                                public void onProgressStart(long totalBytes) {

                                }

                                @Override
                                public void onProgressFinish() {
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                Tools.log(TAG, "onFailed: " + "-" + errMsg);
                mListener.updateFailure(errMsg);


            }

        });
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度 获取从相册中选中图片的角度
     */
    public static int readPictureDegree(String path) throws IOException {
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
}
