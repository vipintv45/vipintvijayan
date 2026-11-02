package com.example.simpletvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {
    private final List<ResolveInfo> apps = new ArrayList<>();
    private PackageManager pm;
    private GridView grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        pm = getPackageManager();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 24, 36, 24);
        root.setBackgroundColor(Color.rgb(16, 20, 30));

        TextView title = new TextView(this);
        title.setText("Apps");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setPadding(8, 4, 8, 18);
        root.addView(title);

        grid = new GridView(this);
        grid.setNumColumns(5);
        grid.setHorizontalSpacing(18);
        grid.setVerticalSpacing(18);
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setSelector(android.R.drawable.list_selector_background);
        grid.setFocusable(true);
        grid.setFocusableInTouchMode(true);
        root.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
        loadApps();
        grid.setAdapter(new AppAdapter());
        if (!apps.isEmpty()) grid.requestFocus();
    }

    private void loadApps() {
        apps.clear();
        addApps(Intent.CATEGORY_LEANBACK_LAUNCHER);
        addApps(Intent.CATEGORY_LAUNCHER);
        Collections.sort(apps, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                return a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString());
            }
        });
    }

    private void addApps(String category) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(category);
        List<ResolveInfo> found = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : found) {
            String pkg = info.activityInfo.packageName;
            boolean exists = false;
            for (ResolveInfo old : apps) {
                if (old.activityInfo.packageName.equals(pkg)) { exists = true; break; }
            }
            if (!exists && !pkg.equals(getPackageName())) apps.add(info);
        }
    }

    private class AppAdapter extends BaseAdapter {
        @Override public int getCount() { return apps.size(); }
        @Override public Object getItem(int position) { return apps.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout tile = new LinearLayout(MainActivity.this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setPadding(8, 12, 8, 12);
            tile.setFocusable(true);
            tile.setFocusableInTouchMode(false);
            tile.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            ResolveInfo info = apps.get(position);
            ImageView icon = new ImageView(MainActivity.this);
            Drawable drawable = info.loadIcon(pm);
            icon.setImageDrawable(drawable);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(76, 76);
            tile.addView(icon, iconParams);

            TextView label = new TextView(MainActivity.this);
            label.setText(info.loadLabel(pm));
            label.setTextColor(Color.WHITE);
            label.setTextSize(14);
            label.setGravity(Gravity.CENTER);
            label.setMaxLines(2);
            label.setPadding(2, 10, 2, 0);
            tile.addView(label, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            tile.setOnClickListener(v -> {
                Intent launch = pm.getLaunchIntentForPackage(info.activityInfo.packageName);
                if (launch == null) {
                    launch = new Intent(Intent.ACTION_MAIN);
                    launch.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
                    launch.setPackage(info.activityInfo.packageName);
                }
                try { startActivity(launch); } catch (Exception ignored) { }
            });
            return tile;
        }
    }
}
