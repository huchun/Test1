package com.hucc.test1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by chunchun.hu on 2018/1/26.
 */

public class BaseActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 10000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // checkPermission();
    }

    /*
     *  读取外部存储器权限;
     *   写入外部存储器权限.
     */
    private void checkPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
        if (isAllGranted){
            return;
        }
        /**
         * 请求权限  一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
         */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions){
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_DENIED){
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE){
            boolean isAllGranted = true;
            //判断是否所有的权限都已经授予了
            for (int grant : grantResults){
                if (grant != PackageManager.PERMISSION_DENIED){
                    isAllGranted = false;
                    break;
                }
            }
        }
    }
}
