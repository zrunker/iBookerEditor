package cc.ibooker.ibookereditor.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import cc.ibooker.ibookereditor.R;
import cc.ibooker.ibookereditor.adapter.SideMenuAdapter;
import cc.ibooker.ibookereditor.bean.SideMenuItem;

/**
 * 书客编辑器
 */
public class MainActivity extends AppCompatActivity {
    private ArrayList<SideMenuItem> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 浮动按钮
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        init();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        LinearLayout navView = findViewById(R.id.nav_view);
        ListView listView = navView.findViewById(R.id.id_layout_side_nav_bar_menu);
        initData();
        listView.setAdapter(new SideMenuAdapter(this, mDatas));
    }

    // 初始化数据
    private void initData() {
        if (mDatas == null)
            mDatas = new ArrayList<>();
        mDatas.clear();
        mDatas.add(new SideMenuItem(0, getString(R.string.article), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_location, getString(R.string.local), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_recommend, getString(R.string.recommend), true));
        mDatas.add(new SideMenuItem(R.drawable.icon_question, getString(R.string.grammar_reference), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_set, getString(R.string.set), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_feedback, getString(R.string.feedback), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_star, getString(R.string.score), false));
        mDatas.add(new SideMenuItem(R.drawable.icon_about, getString(R.string.about), false));
    }
}
