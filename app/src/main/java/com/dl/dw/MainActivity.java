package com.dl.dw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.dl.dw.capture.ScreenCaptureService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dl.dw.databinding.ActivityMainBinding;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        checkPermission(this);

        TextView textView= new TextView(this);
        textView.setText("Hello World");
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(textView, params);


        binding.navView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                View view = getWindow().getDecorView();     // 获取DecorView
//                // 方式一:
//                view.setDrawingCacheEnabled(true);
//                view.buildDrawingCache();
//                Bitmap bitmap1 = view.getDrawingCache();
//                ImageUtils.save2Album(bitmap1, Bitmap.CompressFormat.PNG);
                takeScreenShot();
                textView.setText("Hello World222");
            }
        }, 4000);

    }

    public static boolean checkPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            activity.startActivityForResult(
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName())), 0);
            return false;
        }
        return true;
    }

    MediaProjectionManager mMediaProjectionManager=null;
    MediaProjection mMediaProjection=null;
    public void takeScreenShot() {
       // 系统截屏
        mMediaProjectionManager= (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), 1111);
    }

    ImageReader mImageReader;
    int mWidthPixels=ScreenUtils.getScreenWidth();
    int mHeightPixels= ScreenUtils.getScreenHeight();
    int mDensityDpi= ScreenUtils.getScreenDensityDpi();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1111 && resultCode == RESULT_OK) {
            //通过返回的结果获取MediaProjection对象执行后面的流程进行屏幕画面捕捉
            //必须在前台服务中才能运行
//            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
//            //创建用于接收投影的容器
//            mImageReader = ImageReader.newInstance(mWidthPixels, mHeightPixels, PixelFormat.RGBA_8888, 2);
//            //通过MediaProjection创建创建虚拟显示器对象，创建后物理屏幕画面会不断地投影到虚拟显示器VirtualDisplay上，输出到虚拟现实器创建时设定的输出Surface上。
//            VirtualDisplay mVirtualDisplay = mediaProjection.createVirtualDisplay("mediaprojection", mWidthPixels, mHeightPixels,
//                    mDensityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
//            //从容器中获取image
//            Image image = mImageReader.acquireLatestImage();
//            //获取bitmap
//            if (image != null) {
//                final Image.Plane[] planes = image.getPlanes();
//                if (planes.length > 0) {
//                    final ByteBuffer buffer = planes[0].getBuffer();
//                    int pixelStride = planes[0].getPixelStride();
//                    int rowStride = planes[0].getRowStride();
//                    int rowPadding = rowStride - pixelStride * mWidthPixels;
//                    Bitmap bitmap = Bitmap.createBitmap(mWidthPixels + rowPadding / pixelStride, mHeightPixels, Bitmap.Config.ARGB_8888);
//                    bitmap.copyPixelsFromBuffer(buffer);
//                    ImageUtils.save2Album(bitmap, Bitmap.CompressFormat.PNG);
//                    image.close();
//
//                }
//            }
//            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode,
//                    data);

            startService(ScreenCaptureService.getStartIntent(this, resultCode, data));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}