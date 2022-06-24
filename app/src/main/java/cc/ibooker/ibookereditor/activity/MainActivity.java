package cc.ibooker.ibookereditor.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.LocalOperDialogLvAdapter;
import cc.ibooker.ibookereditor.adapter.NotesAdapter;
import cc.ibooker.ibookereditor.adapter.SideMenuAdapter;
import cc.ibooker.ibookereditor.base.BaseActivity;
import cc.ibooker.ibookereditor.bean.LocalEntity;
import cc.ibooker.ibookereditor.bean.SideMenuItem;
import cc.ibooker.ibookereditor.dto.FileInfoBean;
import cc.ibooker.ibookereditor.event.LocalOperDialogEvent;
import cc.ibooker.ibookereditor.event.SaveNotesSuccessEvent;
import cc.ibooker.ibookereditor.sqlite.SQLiteDao;
import cc.ibooker.ibookereditor.sqlite.SQLiteDaoImpl;
import cc.ibooker.ibookereditor.utils.ActivityUtil;
import cc.ibooker.ibookereditor.utils.ClickUtil;
import cc.ibooker.ibookereditor.utils.DateUtil;
import cc.ibooker.ibookereditor.utils.FileUtil;
import cc.ibooker.ibookereditor.utils.ToastUtil;
import cc.ibooker.zdialoglib.DiyDialog;
import cc.ibooker.zdialoglib.TipDialog;
import cc.ibooker.zrecyclerviewlib.ZRecyclerView;
import cc.ibooker.zrecyclerviewlib.ZRvRefreshLayout;

import static cc.ibooker.ibookereditor.utils.ConstantUtil.PERMISSIONS_REQUEST_OPER_FILE;

/**
 * 书客编辑器开源项目
 *
 * @author 邹峰立
 */
public class MainActivity extends BaseActivity implements View.OnClickListener,
        ZRvRefreshLayout.OnRvRefreshListener {
    private DrawerLayout drawer;
    private ListView sideListview;
    private SideMenuAdapter sideMenuAdapter;
    private ArrayList<SideMenuItem> mSideMenuDatas;

    // 内容区
    private ZRvRefreshLayout zrvr;
    private ZRecyclerView zrv;
    private NotesAdapter notesAdapter;
    private ArrayList<LocalEntity> localEntities = new ArrayList<>();

    // 网络状态、数据加载状态
    private LinearLayout stateLayout;
    private ImageView stateImg;
    private TextView stateTv;

    // 权限组
    private final String[] needPermissions = new String[]{
            // SDK在Android 6.0+需要进行运行检测的权限如下：
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private SQLiteDao sqLiteDao;

    private DiyDialog localOperDialog, detailDialog;
    private TipDialog delDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 设置DrawerLayout
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // 申请权限
        if (!hasPermission(needPermissions)) {
            requestPermission(PERMISSIONS_REQUEST_OPER_FILE, needPermissions);
        }

        // 初始化
        init();

        // 初始化侧边栏数据
        initData();
        setSideAdapter();

        // 加载数据
        onRefresh();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawer.closeDrawer(GravityCompat.START, true);
        closeLocalOperDialog();
        closeDelDialog();
        closeDetailDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(SaveNotesSuccessEvent.class);
        EventBus.getDefault().removeStickyEvent(LocalOperDialogEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        zrvr.setRefreshing(false);
    }

    // 保存本地笔记成功事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeSaveNotesSuccessEvent(SaveNotesSuccessEvent event) {
        // 保存相关数据到数据中
        if (sqLiteDao == null) {
            sqLiteDao = new SQLiteDaoImpl(this);
        }
        sqLiteDao.updateLocalFileById(event.getFileInfoBean(), event.get_id());

        // 刷新界面
        zrvr.executeRefresh();

        EventBus.getDefault().removeStickyEvent(event);
    }

    // 展示本地笔记相关操作事件
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void executeLocalOperDialogEvent(LocalOperDialogEvent event) {
        int position = event.getPosition();
        LocalEntity localEntity = event.getLocalEntity();
        showLocalOperDialog(localEntity, position);
        EventBus.getDefault().removeStickyEvent(event);
    }

    // 返回按钮监听
    @Override
    public void onBackPressed() {
        if (drawer == null)
            drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START, true);
        } else {
            super.onBackPressed();
        }
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick()) return;
        if (v.getId() == R.id.ibtn_edit) {// 编辑
            Intent intent_edit = new Intent(this, EditNotesActivity.class);
            startActivity(intent_edit);
        }
    }

    // 初始化方法
    private void init() {
        ImageButton editImgBtn = findViewById(R.id.ibtn_edit);
        editImgBtn.setOnClickListener(this);

        zrvr = findViewById(R.id.zrvr);
        zrvr.setOnRvRefreshListener(this);
        zrv = zrvr.zRv;

        // 侧边栏相关信息
        LinearLayout headerLayout = findViewById(R.id.layout_side_nav_bar_header);
        headerLayout.setOnClickListener(this);
        sideListview = findViewById(R.id.listview);
        sideListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtil.isFastClick()) return;
                switch (position) {
                    case 1:// 笔记
                        drawer.closeDrawer(GravityCompat.START, true);
                        onRefresh();
                        break;
                    case 2:// 语法参考
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentGrammer = new Intent(MainActivity.this, WebActivity.class);
                        intentGrammer.putExtra("webUrl", "http://ibooker.cc/article/1/detail");
                        startActivity(intentGrammer);
                        break;
                    case 3:// 设置
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentSet = new Intent(MainActivity.this, SetActivity.class);
                        startActivity(intentSet);
                        break;
                    case 4:// 反馈
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentFeedback = new Intent(MainActivity.this, FeedbackActivity.class);
                        startActivity(intentFeedback);
                        break;
                    case 5:// 评分
                        drawer.closeDrawer(GravityCompat.START, true);
//                        String mAddress = "market://details?id=" + AppUtil.getVersion(MainActivity.this);
//                        Intent marketIntent = new Intent("android.intent.action.VIEW");
//                        marketIntent.setData(Uri.parse(mAddress));
//                        startActivity(marketIntent);

                        String mAddress = "market://details?id=" + getPackageName();
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse(mAddress));
                        if (marketIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(marketIntent);
                        } else {
                            // 要调起的应用不存在时的处理
                            ToastUtil.shortToast(MainActivity.this, "没有可以打开的应用市场！");
                        }
                        break;
                    case 6:// 关于
                        drawer.closeDrawer(GravityCompat.START, true);
                        Intent intentAbout = new Intent(MainActivity.this, WebActivity.class);
                        intentAbout.putExtra("webUrl", "http://ibooker.cc/article/182/detail");
                        startActivity(intentAbout);
                        break;
                }
            }
        });

        // 状态信息
        stateLayout = findViewById(R.id.layout_state);
        stateImg = stateLayout.findViewById(R.id.img_state);
        stateTv = stateLayout.findViewById(R.id.tv_state_tip);
        TextView reloadTv = stateLayout.findViewById(R.id.tv_reload);
        reloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick()) return;
                updateStateLayout(false, -1, null);
                onRefresh();
            }
        });
        updateStateLayout(false, -1, null);

        // 初始化sqLiteDao
        if (sqLiteDao == null) {
            sqLiteDao = new SQLiteDaoImpl(this);
        }
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        getLocalNotesList();
    }

    // 更新状态布局
    private void updateStateLayout(boolean isShow, int state, String stateTip) {
        if (isShow) {
            stateLayout.setVisibility(View.VISIBLE);
            switch (state) {
                case 1:// 无网络
                    stateImg.setImageResource(R.drawable.img_load_error);
                    stateTv.setText(getResources().getString(R.string.nonet_tip));
                    break;
                case 2:// 异常
                    stateImg.setImageResource(R.drawable.img_load_error);
                    stateTv.setText(stateTip);
                    break;
                case 3:// 失败
                    stateImg.setImageResource(R.drawable.img_load_failed);
                    stateTv.setText(stateTip);
                    break;
                case 4:// 数据为空
                    stateImg.setImageResource(R.drawable.img_load_empty);
                    stateTv.setText(getResources().getString(R.string.no_data));
                    break;
                case 0:// 成功
                    stateImg.setImageResource(R.drawable.img_load_success);
                    stateTv.setText(stateTip);
                    break;
            }
        } else {
            stateLayout.setVisibility(View.GONE);
        }
    }

    // 刷新本地笔记列表
    private void setNotesAdapter() {
        if (notesAdapter == null) {
            notesAdapter = new NotesAdapter(this, localEntities);
            zrv.setAdapter(notesAdapter);
        } else {
            notesAdapter.refreshData(localEntities);
        }
    }

    // 初始化数据
    private void initData() {
        if (mSideMenuDatas == null) {
            mSideMenuDatas = new ArrayList<>();
        }
        mSideMenuDatas.clear();
        mSideMenuDatas.add(new SideMenuItem(0, getString(R.string.util), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_notes, getString(R.string.notes), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_qa, getString(R.string.grammar_reference), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_set, getString(R.string.set), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_feedback, getString(R.string.feedback), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_star, getString(R.string.score), false));
        mSideMenuDatas.add(new SideMenuItem(R.drawable.icon_about, getString(R.string.about), false));
    }

    // 自定义setSideAdapter
    private void setSideAdapter() {
        if (sideMenuAdapter == null) {
            sideMenuAdapter = new SideMenuAdapter(this, mSideMenuDatas);
            sideListview.setAdapter(sideMenuAdapter);
        } else {
            sideMenuAdapter.reflashData(mSideMenuDatas);
        }
    }

    /**
     * 获取本地笔记相关信息
     */
    private void getLocalNotesList() {
        // 清空原有数据
        if (localEntities.size() > 0)
            localEntities.clear();
        // 先从数据库中获取
        if (sqLiteDao == null)
            sqLiteDao = new SQLiteDaoImpl(this);
        ArrayList<FileInfoBean> localFileList1 = sqLiteDao.selectLocalFilesByTime();
        // 再从本地文件中获取
        ArrayList<FileInfoBean> localFileList2 = FileUtil.getFileInfos(FileUtil.LOCALFILE_PATH);
        // 重置数据
        ArrayList<FileInfoBean> fileInfoBeans = new ArrayList<>(localFileList1);
        if (fileInfoBeans.size() <= 0) {
            fileInfoBeans.addAll(localFileList2);
        }
        // 合并数据
        if (localFileList2.size() > 0 && localFileList1.size() > 0) {
            for (FileInfoBean data2 : localFileList2) {// 遍历本地
                boolean isAdd = true;
                for (FileInfoBean data1 : localFileList1) {// 遍历数据库
                    // 通过地址判断 是否未同一个文件
                    if (TextUtils.isEmpty(data2.getFilePath())
                            || data2.getFilePath().equals(data1.getFilePath())) {
                        isAdd = false;
                        break;
                    }
                }
                // 防止文件重复添加
                if (isAdd) {
                    String filePath = data2.getFilePath();
                    for (FileInfoBean data3 : fileInfoBeans) {
                        if (!filePath.equals(data3.getFilePath())) {
                            // 插入数据库
                            int _id = sqLiteDao.insertLocalFile2(data2);
                            data2.setId(_id);
                            fileInfoBeans.add(data2);
                            break;
                        }
                    }
                }

            }
        }

        // 转换数据
        localEntities = localFileListToEntities(fileInfoBeans);

        // 刷新界面
        zrvr.setRefreshing(false);
        if (localEntities.size() <= 0) {
            updateStateLayout(true, 4, null);
        } else {
            updateStateLayout(false, -1, null);
            setNotesAdapter();
        }
    }

    /**
     * 将ArrayList<FileInfoBean>转化为ArrayList<LocalEntity>
     *
     * @param list 待转化数据
     */
    private ArrayList<LocalEntity> localFileListToEntities(ArrayList<FileInfoBean> list) {
        ArrayList<LocalEntity> localList = new ArrayList<>();
        for (FileInfoBean fileInfoBean : list) {
            LocalEntity data = new LocalEntity();
            int _id = fileInfoBean.getId();
            File file = new File(fileInfoBean.getFilePath());
            if (file.exists() && file.isFile()) {// 文件存在
                data.setFile(file);
                data.setaFormatSize(FileUtil.formatFileSize(FileUtil.getFileSize(file)));
                data.setaId(_id);
                data.setaFilePath(fileInfoBean.getFilePath());
                data.setaTitle(fileInfoBean.getFileName());
                data.setaTime(fileInfoBean.getFileCreateTime());
                data.setaFormatTime(DateUtil.getFormatTimeStampToDateTime(fileInfoBean.getFileCreateTime()));
                localList.add(data);
            } else {// 文件不存在 - 删除数据库中数据
                sqLiteDao.deleteLocalFileById(_id);
            }
        }
        return localList;
    }

    /**
     * 展示本地操作Dialog
     */
    private ListView listView;

    private void showLocalOperDialog(final LocalEntity localEntity, final int position) {
        if (localOperDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_local_oper, null);
            listView = view.findViewById(R.id.listview);
            LocalOperDialogLvAdapter localOperDialogLvAdapter = new LocalOperDialogLvAdapter(this);
            listView.setAdapter(localOperDialogLvAdapter);
            localOperDialog = new DiyDialog(this, view);
            localOperDialog.setDiyDialogWidth(70);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if (ClickUtil.isFastClick()) return;
                closeLocalOperDialog();
                if (i == 0) {// 详情
                    showDetailDialog(localEntity);
                } else if (i == 1) {// 分享
                    shareFile(MainActivity.this, new File(localEntity.getaFilePath()), localEntity.getaTitle());
                } else if (i == 2) {// 删除
                    showDelDialog(localEntity.getaFilePath(), position);
                }
            }
        });
        localOperDialog.showDiyDialog();
    }

    /**
     * 关闭本地操作Dialog
     */
    private void closeLocalOperDialog() {
        if (localOperDialog != null) {
            localOperDialog.closeDiyDialog();
        }
    }

    /**
     * 展示删除Dialog
     */
    private void showDelDialog(final String filePath, final int position) {
        if (delDialog == null)
            delDialog = new TipDialog(this);
        delDialog.setEnsureColor("#FE7517")
                .setOnTipEnsureListener(new TipDialog.OnTipEnsureListener() {
                    @Override
                    public void onEnsure() {
                        if (ClickUtil.isFastClick()) return;
                        if (FileUtil.deleteDirs(filePath)) {
                            // 移除数据
                            if (position >= 0 && position < localEntities.size()) {
                                localEntities.remove(position);
                            }
                            // 刷新列表
                            notesAdapter.refreshData(localEntities);
                        } else {
                            ToastUtil.shortToast(MainActivity.this, "删除文件失败！");
                        }
                    }
                });
        delDialog.showTipDialog();
    }

    /**
     * 关闭删除Dialog
     */
    private void closeDelDialog() {
        if (delDialog != null) {
            delDialog.closeTipDialog();
        }
    }

    /**
     * 展示详情Dialog
     */
    private TextView adNameTv, adTimeTv, adSizeTv, adPathTv;

    private void showDetailDialog(LocalEntity localEntity) {
        if (detailDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_adetail_dialog, null);
            adNameTv = view.findViewById(R.id.tv_name);
            adTimeTv = view.findViewById(R.id.tv_time);
            adSizeTv = view.findViewById(R.id.tv_size);
            adPathTv = view.findViewById(R.id.tv_path);
            detailDialog = new DiyDialog(this, view);
            detailDialog.setDiyDialogWidth(75);
        }
        if (localEntity != null) {
            if (localEntity.getFile() != null) {
                adNameTv.setText(localEntity.getFile().getName());
            }
            adTimeTv.setText(localEntity.getaFormatTime());
            adSizeTv.setText(localEntity.getaFormatSize());
            adPathTv.setText(localEntity.getaFilePath());
        }
        detailDialog.showDiyDialog();
    }

    /**
     * 关闭详情Dialog
     */
    private void closeDetailDialog() {
        if (detailDialog != null) {
            detailDialog.closeDiyDialog();
        }
    }

    /**
     * 分享文件
     */
    private void shareFile(Context context, File file, String Kdescription) {
        if (file != null && file.exists() && file.isFile()) {
//            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra("subject", Kdescription);
            intent.putExtra("body", ""); // 正文
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "cc.ibooker.ibookereditor.fileProvider", file);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                uri = Uri.fromFile(file);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            }

            intent.putExtra(Intent.EXTRA_STREAM, uri); // 添加附件，附件为file对象
            if (uri.toString().endsWith(".gz")) {
                intent.setType("application/x-gzip"); // 如果是gz使用gzip的mime
            } else if (uri.toString().endsWith(".txt")) {
                intent.setType("text/plain"); // 纯文本则用text/plain的mime
            } else {
                intent.setType("application/octet-stream"); // 其他的均使用流当做二进制数据来发送
            }
            context.startActivity(intent);// 调用系统的mail客户端进行发送
        }
    }

    @Override
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        super.doRequestPermissionsResult(requestCode, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_OPER_FILE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onRefresh();
        }
    }

    // 退出应用
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN) {
            drawer.closeDrawer(GravityCompat.START, true);
            if (System.currentTimeMillis() - exitTime > 5000) {
                exitTime = System.currentTimeMillis();
                ToastUtil.shortToast(getApplicationContext(), "再按一次退出程序");
            } else {
                ActivityUtil.getInstance().exitSystem();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
