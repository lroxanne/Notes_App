package com.example.thenotesapp;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class GalleryUtils {
    static String FOLDER_NAME = "TemplatesEngine";
    static String FOLDER_NAME_PUBLIC = Environment.DIRECTORY_MOVIES + "/" + FOLDER_NAME;
    public static void saveToGallery(String privatePath, String fileName, String publicPath, Context context) {
        Log.e("VideoPlayback", "File Paths: Private=" + privatePath + ", FileName=" + fileName + ", Public=" + publicPath);

        boolean isAvailble = new File(FOLDER_NAME_PUBLIC).exists();
        if (!isAvailble)
            new File(FOLDER_NAME_PUBLIC).mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveVideoToGalleryUsingMediaStore(privatePath, fileName, context);
        } else {
            saveVideoToGalleryForOld(privatePath, fileName, publicPath, context);
        }
    }

    private static void saveVideoToGalleryForOld(String privatePath, String fileName, String publicPath, Context context) {
        try {
            File sourceFile = new File(privatePath + "/" + fileName);
            File destinationFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+"/"+FOLDER_NAME,
                   fileName
            );
            FileInputStream sourceStream = new FileInputStream(sourceFile);
            FileOutputStream destinationStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = sourceStream.read(buffer)) > 0) {
                destinationStream.write(buffer, 0, length);
            }
            sourceStream.close();
            destinationStream.close();
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{destinationFile.getAbsolutePath()},
                    null,
                    null
            );
            sourceFile.delete();
        } catch (Exception e) {
            handleSaveError(e);
        }
    }


    private static void saveVideoToGalleryUsingMediaStore(String privatePath, String fileName, Context context) {
        try {
            File sourceFile = new File(privatePath + "/" + fileName);
            File destinationFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+"/"+FOLDER_NAME,
                    fileName
            );
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/*");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, FOLDER_NAME_PUBLIC);
            Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                FileInputStream sourceStream = new FileInputStream(privatePath + "/" + fileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = sourceStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                sourceStream.close();
                assert outputStream != null;
                outputStream.close();
            }
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{destinationFile.getAbsolutePath()},
                    null,
                    null
            );
            sourceFile.delete();
        } catch (Exception e) {
            handleSaveError(e);
        }
    }

    private static void savePdfToPublicUsingMediaStore(String privatePath, String fileName, Context context) {
        try {
            File sourceFile = new File(privatePath + "/" + fileName);
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/" + FOLDER_NAME);

            Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                FileInputStream sourceStream = new FileInputStream(sourceFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = sourceStream.read(buffer)) > 0) {
                    assert outputStream != null;
                    outputStream.write(buffer, 0, length);
                }
                sourceStream.close();
                assert outputStream != null;
                outputStream.close();
            }
            sourceFile.delete();
        } catch (Exception e) {
            handleSaveError(e);
        }
    }


    private static void handleSaveError(Exception e) {
        e.printStackTrace();
    }
}

