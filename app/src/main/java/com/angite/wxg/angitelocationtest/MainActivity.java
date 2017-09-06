package com.angite.wxg.angitelocationtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

public class MainActivity extends AppCompatActivity {

    private TextView txtMenu, txtshow;
    private ImageView imgPic;
    private WebView webView;
    private ScrollView scroll;
    private Bitmap bitmap;
    private String detail = "";
    private boolean flag = false;
    private final static String PIC_URL = "http://ww2.sinaimg.cn/large/7a8aed7bgw1evshgr5z3oj20hs0qo0vq.jpg";
    private final static String HTML_URL = "http://angite.com/an/index.php/api/position/set_position";

    private LocationManager lm;
//    MapView mMapView=null;
    Button startButton;
    EditText deviceID;
    EditText useID;
    TextView showinfo;
    private LocationClient locationClient=null;
    private static final int UPDATE_TIME = 1000;
    private static int LOCATION_COUTNS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);


//        mMapView=(MapView)findViewById(R.id.bmapView);
        deviceID=(EditText)findViewById(R.id.DeviceID);
        useID=(EditText)findViewById(R.id.useID);
        showinfo=(TextView)findViewById(R.id.showinfo);
        startButton=(Button)findViewById(R.id.btn_start);
//        setViews();
        locationClient=new LocationClient(getApplicationContext());
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("wgs84");
        option.setProdName("angite.com");
        option.setOpenGps(true);
        option.setIsNeedAddress(true);
        option.setScanSpan(UPDATE_TIME);
        locationClient.setLocOption(option);
        BDLocationListener mylistener=new MyLocationListener();
        locationClient.registerLocationListener(mylistener);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationClient==null){
                    return;
                }
                if(locationClient.isStarted()){
                    startButton.setText("start");
                    locationClient.stop();
                    deviceID.setEnabled(true);
                    useID.setEnabled(true);
                }else{
                    if(deviceID.getText().toString().equals("")||useID.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "设备ID或者领用ID都不能为空", Toast.LENGTH_LONG).show();
                        return;
                    }
                    startButton.setText("stop");
                    locationClient.start();
                    deviceID.setEnabled(false);
                    useID.setEnabled(false);
                    Log.i("wxg","code===="+locationClient.requestLocation());
                }
            }
        });

    }
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation==null){
                Log.i("wxg","getLocation error");
            }
            final StringBuffer sb=new StringBuffer(256);
            sb.append("Time:");
            sb.append(bdLocation.getTime());
            sb.append("\nError code : ");
            sb.append(bdLocation.getLocType());
            sb.append("\nLatitude : ");
            sb.append(bdLocation.getLatitude());
            sb.append("\nLontitude : ");
            sb.append(bdLocation.getLongitude());
            CoordinateConverter converter=new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.COMMON);

            LatLng soureceLatlng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            converter.coord(soureceLatlng);
            sb.append("\nGPS转换成百度坐标:"+converter.convert().toString());
            sb.append("\nRadius : ");
            sb.append(bdLocation.getRadius());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nSpeed : ");
                sb.append(bdLocation.getSpeed());
                sb.append("\nSatellite : ");
                sb.append(bdLocation.getAltitude());
                sb.append("\nDirection : ");
                sb.append(bdLocation.getDirection());
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\nAddress : ");
                sb.append(bdLocation.getAddrStr());
                sb.append("\noperationers : ");
                sb.append(bdLocation.getOperators());    //获取运营商信息
            }
            sb.append("\ndescribe:"+bdLocation.getLocType());
            LOCATION_COUTNS ++;
            sb.append("\n检查位置更新次数：");
            sb.append(String.valueOf(LOCATION_COUTNS));

            Log.i("wxg","location="+sb.toString());
            String data="deviceid="+deviceID.getText().toString()+"&useid="+useID.getText().toString()+"&lat="+bdLocation.getLatitude()+"&lon="+bdLocation.getLongitude()+"&radius="+bdLocation.getRadius()+
                    "&get_time="+bdLocation.getTime()+"&loctype="+bdLocation.getLocType()+"&speed="+bdLocation.getSpeed()+"&altitude="+bdLocation.getAltitude()+
                    "&satellite_number="+bdLocation.getSatelliteNumber()+"&direction="+bdLocation.getDirection()+"&address="+bdLocation.getAddrStr();
            Log.i("wxg","data="+data);
            try {
                if(GetData.postLocation(HTML_URL,data))
                {
                    sb.append("\n提交服务成功！");
                }
                else{
                    sb.append("\n提交服务失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sb.append("\n提交服务出错了");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showinfo.setText(sb);
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


//    private void setViews() {
//        txtMenu = (TextView) findViewById(R.id.txtMenu);
//        txtshow = (TextView) findViewById(R.id.txtshow);
//        imgPic = (ImageView) findViewById(R.id.imgPic);
//        webView = (WebView) findViewById(R.id.webView);
//        scroll = (ScrollView) findViewById(R.id.scroll);
//        registerForContextMenu(txtMenu);
//    }

    // 定义一个隐藏所有控件的方法:
//    private void hideAllWidget() {
//        imgPic.setVisibility(View.GONE);
//        scroll.setVisibility(View.GONE);
//        webView.setVisibility(View.GONE);
//    }

//    @Override
//    // 重写上下文菜单的创建方法
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        MenuInflater inflator = new MenuInflater(this);
//        inflator.inflate(R.menu.menus, menu);
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
//
//    // 上下文菜单被点击是触发该方法
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.one:
//                new Thread() {
//                    public void run() {
//                        try {
//                            byte[] data = GetData.getImage(PIC_URL);
//                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        handler.sendEmptyMessage(0x001);
//                    }
//
//                    ;
//                }.start();
//                break;
//            case R.id.two:
//                new Thread() {
//                    public void run() {
//                        try {
////                            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
////                            Log.i("wxg","Location:"+lm.get)
//                            detail = GetData.getHtml(HTML_URL);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        handler.sendEmptyMessage(0x002);
//                    };
//                }.start();
//                break;
//            case R.id.three:
//                if (detail.equals("")) {
//                    Toast.makeText(MainActivity.this, "先请求HTML先嘛~", Toast.LENGTH_SHORT).show();
//                } else {
//                    handler.sendEmptyMessage(0x003);
//                }
//                break;
//        }
//        return true;
//    }
//

    //定义一个更新显示的方法
    private String updateShow(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("当前的位置信息：\n");
            sb.append("精度：" + location.getLongitude() + "\n");
            sb.append("纬度：" + location.getLatitude() + "\n");
            sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n");
            sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            return sb.toString();
        } else return "";
    }


    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ? true : false;
    }


    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        //mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        //mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        //mMapView.onPause();
    }

}
