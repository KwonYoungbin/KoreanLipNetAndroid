package bykkmouse.lipnet;
import android.media.Image;
import android.os.Handler;
import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private UploadTask uploadTask;
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }


    }

    // 동영상선택
    private void doSelectMovie()
    {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        //EXTERNAL_CONTENT_URI
        i.setType("video/*");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try
        {
            startActivityForResult(i,0);
        } catch (android.content.ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /*
    public void openFolder()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + "Camera" + File.separator);
        //Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp"
        intent.setDataAndType(uri, "video/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }*/

    private void doRecordVideo()
    {
        Intent i = new Intent(getApplication(),CameraActivity.class);
        startActivity(i);
        /*Intent i = new Intent();
        i.setAction("ABC");
        startActivity(i);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            Uri uri = intent.getData();
            String path = getRealPathFromURI(getApplicationContext(), uri);
            String name = getFileName(uri);
            if(uploadTask != null) {
                uploadTask.cancel(true);
            }
            uploadTask = new UploadTask();
            uploadTask.execute(path, name, null);
        }
    }


    // 파일명 찾기
    private String getFileName(Uri uri)
    {
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }



    public void mOnClick(View v) {

        if(v.getId() == R.id.btnUpload) {
            doSelectMovie();
        }
        else if(v.getId() == R.id.btnDownload) {
        }
        else if(v.getId() == R.id.btnCamera) {
            doRecordVideo();

        }
    }








    public static String getRealPathFromURI(final Context context, final Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                } else {
                    String SDcardpath = getRemovableSDCardPath(context).split("/Android")[0];
                    return SDcardpath +"/"+ split[1];
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getRemovableSDCardPath(Context context) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null)
            return storages[1].toString();
        else
            return "";
    }


    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
