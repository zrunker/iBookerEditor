package cc.ibooker.ibookereditor.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.AmapLocationPOIAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.utils.ConstantUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;

/**
 * 高德地图定位,设置中心点以及marker。
 * 检索周边POI，注意：不设置POI的类别，默认返回“餐饮服务”、“商务住宅”、“生活服务”这三种类别的POI。
 * Created by 邹峰立 on 2017/3/1 0001.
 */
public class AMapLocationPOIActivity extends BaseActivity implements AMapLocationListener, PoiSearch.OnPoiSearchListener {
    // 定位+地图
    private MapView mapView;
    private AMap aMap;
    // 声明AMapLocationClient对象
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double lat;
    private double lon;

    private PoiSearch.Query query = null;
    private String cityCode;// 城市编码
    private int currentPage = 0;// 查询页码
    private int pageCount = 20;// 每次查询多少条数据

    // 此次ListView实现加载更多功能
    private ListView mListView;
    private AmapLocationPOIAdapter adapter;
    private ArrayList<PoiItem> mDatas = new ArrayList<>();
    private int lastItem;// 记录istView可见项的最后一项
    private boolean isAbleLoad = true;// 是否允许加载更多
    private View footerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaplocation_poi);

        initView();

        mapView = findViewById(R.id.amap_location);
        mapView.onCreate(savedInstanceState);// 此处必须写
        // 初始化
        initLocation();
    }

    // 初始化控件
    private void initView() {
        ImageView backImg = findViewById(R.id.img_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mListView = findViewById(R.id.lv_poi);
        footerLayout = LayoutInflater.from(this).inflate(R.layout.layout_footer, mListView, false);
        // 滚动事件
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= adapter.getCount() && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 加载更多
                    loadMoreData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
        // 点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PoiItem poiItem = mDatas.get(position);

                ConstantUtil.sPointy = poiItem.getLatLonPoint().getLatitude();
                ConstantUtil.sPointx = poiItem.getLatLonPoint().getLongitude();
                ConstantUtil.sCurrentProv = poiItem.getProvinceName();
                ConstantUtil.sCurrentCity = poiItem.getCityName();
                ConstantUtil.sCurrentDistrict = poiItem.getAdName();
                ConstantUtil.sCurrentStreet = poiItem.getDirection();
                ConstantUtil.sCurrentStreetNum = poiItem.getPoiId();
                ConstantUtil.sCurrentCityCode = poiItem.getCityCode();
                ConstantUtil.sCurrentAdCode = poiItem.getAdCode();
                ConstantUtil.sCurrentAoiName = poiItem.getTitle();
                ConstantUtil.sCurrentAddress = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getDirection() + poiItem.getSnippet();

                // 关闭当前页面
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    // 自定义setAdapter
    private void setAdapter(ArrayList<PoiItem> list) {
        if (adapter == null) {
            adapter = new AmapLocationPOIAdapter(this, list);
            mListView.setAdapter(adapter);
        } else {
            adapter.reflashData(list);
        }
    }

    // 加载更多
    private void loadMoreData() {
        // 允许加载
        if (isAbleLoad && mDatas.size() >= pageCount) {
            if (mListView.getFooterViewsCount() <= 0)
                mListView.addFooterView(footerLayout);
            currentPage += pageCount;
            // 加载更多
            startPoiSearch();
        } else {
            // 不允许加载
            if (mListView.getFooterViewsCount() > 0)
                mListView.removeFooterView(footerLayout);
        }
    }

    // 初始化定位
    private void initLocation() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        // 高德地图 Android SDK 在地图初始化时显示指定的城市
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ConstantUtil.sPointy, ConstantUtil.sPointx), 17));
        UiSettings uiSettings = aMap.getUiSettings();
        // 隐藏高德地图默认的放大缩小控件
        uiSettings.setZoomControlsEnabled(false);
        // 开启定位
        startLocation();
    }

    // 初始化POI搜索
    private void startPoiSearch() {
        String keyWord = "";
        if (query == null)
            query = new PoiSearch.Query(keyWord, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(pageCount);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);//设置查询页码

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 1000));//设置周边搜索的中心点以及半径
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 开始查询
    }

    // 启动定位
    private void startLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    // 声明定位定位回调监听器
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                ConstantUtil.sPointy = amapLocation.getLatitude();//获取纬度
                ConstantUtil.sPointx = amapLocation.getLongitude();//获取经度
                ConstantUtil.sCurrentAddress = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                ConstantUtil.sCurrentCountry = amapLocation.getCountry();//国家信息
                ConstantUtil.sCurrentProv = amapLocation.getProvince();//省信息
                ConstantUtil.sCurrentCity = amapLocation.getCity();//城市信息
                ConstantUtil.sCurrentDistrict = amapLocation.getDistrict();//城区信息
                ConstantUtil.sCurrentStreet = amapLocation.getStreet();//街道信息
                ConstantUtil.sCurrentStreetNum = amapLocation.getStreetNum();//街道门牌号信息
                ConstantUtil.sCurrentCityCode = amapLocation.getCityCode();//城市编码
                ConstantUtil.sCurrentAdCode = amapLocation.getAdCode();//地区编码
                ConstantUtil.sCurrentAoiName = amapLocation.getAoiName();//获取当前定位点的AOI信息

                // 设置当前地图显示为当前位置
                cityCode = amapLocation.getAdCode();//地区编码
                lat = amapLocation.getLatitude();
                lon = amapLocation.getLongitude();
//                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 19));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lon));
                markerOptions.title("当前位置");
                markerOptions.visible(true);
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_location_marker));
                markerOptions.icon(bitmapDescriptor);
                aMap.addMarker(markerOptions);

                // 搜索POI
                startPoiSearch();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                ToastUtil.shortToast(AMapLocationPOIActivity.this, "定位失败：" + amapLocation.getErrorInfo());
            }
            // 关闭定位
            mLocationClient.stopLocation();
        }
    }

    // POI搜索结果
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        /*
         * 1）可以在回调中解析result，获取POI信息。
         * 2）result.getPois()可以获取到PoiItem列表，Poi详细信息可参考PoiItem类。
         * 3）若当前城市查询不到所需POI信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市。
         * 4）如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议。
         * 5）返回结果成功或者失败的响应码。1000为成功，其他为失败（详细信息参见网站开发指南-实用工具-错误码对照表）
         */
        if (1000 == i) {
            // 加载成功，处理数据
            ArrayList<PoiItem> list = poiResult.getPois();
            if (list.size() <= 0) {
                isAbleLoad = false;
            } else {
                mDatas.addAll(list);
                setAdapter(mDatas);
            }
            // 隐藏加载更多
            if (mListView.getFooterViewsCount() > 0)
                mListView.removeFooterView(footerLayout);
        } else {
            ToastUtil.shortToast(this, "搜索周边信息失败");
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();//停止定位
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            /*
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
