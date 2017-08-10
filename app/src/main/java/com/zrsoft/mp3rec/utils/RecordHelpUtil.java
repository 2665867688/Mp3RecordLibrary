package com.zrsoft.mp3rec.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class RecordHelpUtil {

    private static String TAG = RecordHelpUtil.class.getName();

    /**
     * SD卡是否正常
     * @return
     */
    public  static boolean isStorageAvailable() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            Log.v(TAG, "SD卡不可用");
            return false;
        }
        return true;
    }


    public static String getSDPath(){
        if(isStorageAvailable()){

            return Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        }else{
            return null;
        }
    }

    /**
     * 毫秒转时分秒
     * @param mis
     * @return
     */
    public static String misToTime(long mis){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(mis);
        return hms;
    }


    /**
     *
     * @param context:上下文环境
     * @param filePath:文件路径
     * @param fileName:文件名称
     * @param isNomedia：是否屏蔽相册读取
     * @param provider:android 7.0以后由于文件访问被列为不安全，生成uri时使用此provider对uri进行加密处理
     * @return
     */
    public static Uri getOutputMediaFileUri(Context context,String filePath,String fileName,boolean isNomedia,String provider) {
//        if (Build.VERSION.SDK_INT < 24) {
//            return Uri.fromFile(getOutputMediaFile(filePath,fileName,isNomedia));
//        } else {
//            return FileProvider.getUriForFile(context, provider, getOutputMediaFile(filePath,fileName,isNomedia));
//        }
        return Uri.fromFile(getOutputMediaFile(filePath,fileName,isNomedia));
    }

    /**
     *
     * @param filePath:文件路径
     * @param fileName:文件名
     * @param isNomedia:是否屏蔽相册读取
     * @return
     */
    public static File getOutputMediaFile(String filePath, String fileName, boolean isNomedia) {
        File mediaStorageDir = null;
        try {
            mediaStorageDir = new File(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // 在SD卡上创建文件夹需要权限：
                File file = null;
                if (isNomedia) {
                    file = new File(mediaStorageDir.getPath() + "/.nomedia");// 用来屏蔽相册读取
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }

    /**
     * android解决部分手机无法通过uri获取到相册的path
     * 通过uri 获取 文件路径
     *
     * @param imageUri
     * @return path
     */
    public static String getPath(Context context, Uri imageUri) {

        if (imageUri == null)
            return null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
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
                return getDataColumn(context, contentUri, null, null);

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
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);

        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;

    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
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

}
