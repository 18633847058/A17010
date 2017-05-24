package com.yang.eric.a17010.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.location.BDLocation;
import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.beans.Location;
import com.yang.eric.a17010.contract.MainContract;
import com.yang.eric.a17010.greendao.gen.LocationDao;
import com.yang.eric.a17010.protocol.AuthMsg;
import com.yang.eric.a17010.protocol.LocationMsg;
import com.yang.eric.a17010.protocol.QueryMsg;
import com.yang.eric.a17010.protocol.Response;
import com.yang.eric.a17010.utils.Constants;
import com.yang.eric.a17010.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/4/21.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private SharedPreferences sharedPreferences;

    private LocationDao locationDao;
    private Location l;
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<LocationMsg> locationMsgs = new ArrayList<>();
    private int i;

    private String username;
    private long uuid;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        this.sharedPreferences = MapsApplication.getInstance()
                .getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        this.locationDao = MapsApplication.getInstance().getDaoSession().getLocationDao();
    }

    @Override
    public void sendAuth() {
        username = sharedPreferences.getString(Constants.USERNAME, "");
        uuid = sharedPreferences.getLong(Constants.CHECK_COED, 0);
        if (!username.isEmpty() && uuid != 0) {
            AuthMsg authMsg = new AuthMsg();
            authMsg.setUsername(username);
            authMsg.setUUID(uuid);
            authMsg.setBDID(123456);
            MapsApplication.getClient().getSocketClient().sendData(authMsg.encode());
        }
    }

    @Override
    public void sendLocation(BDLocation location) {
        l = new Location();
        l.setLatitude(((int) (location.getLatitude() * 36000)));
        l.setLongitude((int) (location.getLongitude() * 36000));
        l.setAltitude((int) location.getAltitude());
        l.setDirection(((int) location.getDirection()));
        l.setSpeed((int) location.getSpeed());
        l.setType(0);
        l.setUpload(2);
        if (i++ == 0) {
            l.setUpload(0);
            if (MapsApplication.isConnected){
                locations.add(l);
                LocationMsg locationMsg = new LocationMsg();
                MapsApplication.getClient().getSocketClient().sendData(
                        locationMsg.encode(locations, ((byte) 0)));
                locationMsgs.add(locationMsg);
                locations.clear();
            } else {
                locationDao.insert(l);
            }
        } else {
            if (i == 20){
                i = 0;
            }
            locationDao.insert(l);
        }
    }

    @Override
    public void reSend() {
        List<Location> loc = locationDao.queryBuilder().where(LocationDao.Properties.Upload.eq(0)).list();
        for (int i = 0; i < loc.size(); i++) {
            locations.add(loc.get(i));
            if (locations.size() == 10 || i == loc.size() - 1) {
                LocationMsg locationMsg = new LocationMsg();
                MapsApplication.getClient().getSocketClient().sendData(
                        locationMsg.encode(locations, ((byte) 1)));
                locationMsgs.add(locationMsg);
                locations.clear();
            }
        }
    }

    @Override
    public void request() {
        long address  =  sharedPreferences.getLong(Constants.ADDRESS, 0);
        QueryMsg queryMsg = new QueryMsg();
        queryMsg.setDataType((byte) 7);
        queryMsg.setUUID(address);
    }

    @Override
    public void receive(byte[] message) {
        switch (message[0]) {
            case 0x00:
                Response r = new Response();
                if (r.decode(message)) {
                    if (r.getResponseType() == 0x02) {
                        if (r.getResult() == 0x00) {
                            view.showMessage("鉴权成功!", false);
                            reSend();
                        } else if (r.getResult() == 0x01){
                            view.showMessage("鉴权码错误,请重新登录!", true);
                            wipeData();
                            disconnect();
                            view.go2Login();
                        }
                    }else if (r.getResponseType() == 0x03) {
                        if (r.getResult() == 0x00) {
                            LogUtils.e("locationMsg","发送成功!");
                            for (int j = 0; j < locationMsgs.size(); j++) {
                                LocationMsg msg = locationMsgs.get(j);
                                if (msg.getNumber() == r.getNumber()) {
                                    if (msg.getDataType() == 0x00) {
                                        for (Location l :
                                                msg.getLocations()) {
                                            l.setUpload(1);
                                            locationDao.insert(l);
                                        }
                                        locationMsgs.remove(j);
                                    } else if (msg.getDataType() == 0x01){
                                        for (Location l :
                                                msg.getLocations()) {
                                            l.setUpload(1);
                                            locationDao.update(l);
                                        }
                                        locationMsgs.remove(j);
                                    }
                                    break;
                                }
                            }
                        } else if (r.getResult() == 0x01){
                            LogUtils.e("locationMsg","发送失败!");
                            for (int j = 0; j < locationMsgs.size(); j++) {
                                LocationMsg msg = locationMsgs.get(j);
                                if (msg.getNumber() == r.getNumber()) {
                                    if (msg.getDataType() == 0x00) {
                                        for (Location l :
                                                msg.getLocations()) {
                                            l.setUpload(0);
                                            locationDao.insert(l);
                                        }
                                        locationMsgs.remove(j);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    MapsApplication.getClient().disconnect();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void wipeData() {
        sharedPreferences.edit().putString(Constants.USERNAME, "").apply();
        sharedPreferences.edit().putLong(Constants.CHECK_COED, 0).apply();
        sharedPreferences.edit().putBoolean(Constants.ISLOGIN, false).apply();
    }

    @Override
    public void disconnect() {
        view.removeListener();
        MapsApplication.getClient().disconnect();
    }

    @Override
    public void start() {

    }
}
