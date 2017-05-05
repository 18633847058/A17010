package com.yang.eric.a17010.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import com.mapbar.license.License;
import com.mapbar.mapdal.Auth;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.mapdal.NativeEnvParams;
import com.mapbar.mapdal.SdkAuth;
import com.mapbar.mapdal.WorldManager;
import com.yang.eric.a17010.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.yang.eric.a17010.utils.Constants.APP_NAME;

/**
 * Created by Yang on 2017/5/5.
 */

public class MapLicense {

    private Context context;

    public MapLicense(Context context) {
        this.context = context;
    }

    /**
     * 初始化license信息
     */
    public void initLicense(){
        //拷贝激活文件到应用目录下
        getActivityCodeFile();
        License.init(context, Constants.APP_PATH, APP_NAME, Constants.KEY, mHandler);
    }
    /**
     * 根据apk名称 获取asset里的apk文件
     * 注意：此处不需要每次都copy这个文件，检查如果有此文件，就不需要再copy
     *          但是如果更换了激活key，则需要删除之前的这个文件，然后再copy
     */
    public void getActivityCodeFile() {
        String filename = "activation_codes.dat";
        String path = Constants.APP_PATH + "/"+filename;
        AssetManager asset = context.getApplicationContext()
                .getAssets();
        try {
            File pathFile = new File(path);
            File pathFileParent = new File(pathFile.getParent());
            if(!pathFileParent.exists()){
                pathFileParent.mkdirs();
            }
            InputStream is = asset.open(filename+".png");
            FileOutputStream fos = new FileOutputStream(new File(path));
            final int fInt = 1024;
            byte[] buffer = new byte[fInt];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化引擎
     */
    private void NativeEnvironmentInit(String appName, String key) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        NativeEnvParams params = new NativeEnvParams(Constants.APP_PATH, appName,
                dm.densityDpi, key, new NativeEnv.AuthCallback() {
            @Override
            public void onDataAuthComplete(int errorCode) {
                String msg = null;
                switch(errorCode){
                    case Auth.Error.deviceIdReaderError:
                        msg="设备ID读取错误";
                        break;
                    case Auth.Error.expired:
                        msg="数据文件权限已经过期";
                        break;
                    case Auth.Error.licenseDeviceIdMismatch:
                        msg="授权文件与当前设备不匹配";
                        break;
                    case Auth.Error.licenseFormatError:
                        msg="授权文件格式错误";
                        break;
                    case Auth.Error.licenseIncompatible:
                        msg="授权文件存在且有效，但是不是针对当前应用程序产品的";
                        break;
                    case Auth.Error.licenseIoError:
                        msg="授权文件IO错误";
                        break;
                    case Auth.Error.licenseMissing:
                        msg="授权文件不存在";
                        break;
                    case Auth.Error.none:
                        msg="数据授权成功";
                        break;
                    case Auth.Error.noPermission:
                        msg="数据未授权";
                        break;
                    case Auth.Error.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSdkAuthComplete(int errorCode) {
                String msg=null;
                switch(errorCode){
                    case SdkAuth.ErrorCode.deviceIdReaderError:
                        msg="授权设备ID读取错误";
                        break;
                    case SdkAuth.ErrorCode.expired:
                        msg="授权KEY已经过期";
                        break;
                    case SdkAuth.ErrorCode.keyIsInvalid:
                        msg="授权KEY是无效值，已经被注销";
                        break;
                    case SdkAuth.ErrorCode.keyIsMismatch:
                        msg="授权KEY不匹配";
                        break;
                    case SdkAuth.ErrorCode.keyUpLimit:
                        msg="授权KEY到达激活上线";
                        break;
                    case SdkAuth.ErrorCode.licenseDeviceIdMismatch:
                        msg="设备码不匹配";
                        break;
                    case SdkAuth.ErrorCode.licenseFormatError:
                        msg="SDK授权文件格式错误";
                        break;
                    case SdkAuth.ErrorCode.licenseIoError:
                        msg="SDK授权文件读取错误";
                        break;
                    case SdkAuth.ErrorCode.licenseMissing:
                        msg="SDK授权文件没有准备好";
                        break;
                    case SdkAuth.ErrorCode.networkContentError:
                        msg="网络返回信息格式错误";
                        break;
                    case SdkAuth.ErrorCode.netWorkIsUnavailable:
                        msg="网络不可用，无法请求SDK验证";
                        break;
                    case SdkAuth.ErrorCode.none:
                        msg="SDK验证通过";
                        break;
                    case SdkAuth.ErrorCode.noPermission:
                        msg="模块没有权限";
                        break;
                    case SdkAuth.ErrorCode.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        params.sdkAuthOfflineOnly=true;
        NativeEnv.init(context.getApplicationContext(), params);
        WorldManager.getInstance().init();
    }

    /**
     * 初始化License模块的回调信息
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case License.Initialize.succ: {
                    //授权验证模块初始化成功
                    String code = License.getActivationCodeNew();
                    if(code==null){
                        //授权模式，判断是否激活成功
                        if (License.autoActivateNew()) {
                            //license激活成功，则进行sdk授权
                            NativeEnvironmentInit(APP_NAME, Constants.KEY);

                            Toast.makeText(context, "license activity success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "license activity fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
                case License.Initialize.deviceIdFailed:
                    //获取不到设备ID，一般来说是MAC地址
                    Toast.makeText(context, "deviceIdFailed", Toast.LENGTH_SHORT).show();
                    break;
                case License.Initialize.noAvailableDataPath:
                    //不存在有效的数据目录
                    Toast.makeText(context, "noAvailableDataPath", Toast.LENGTH_SHORT).show();
                    break;
                case License.Initialize.otherFailed:
                    //授权验证模块初始化失败
                    Toast.makeText(context, "otherFailed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            NativeEnvironmentInit(APP_NAME, Constants.KEY);
        }
    };
}
