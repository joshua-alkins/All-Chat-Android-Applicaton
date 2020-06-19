package com.example.nerdlauncher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private PackageManager manager;
    private List<AppsActivity> apps;
    private GridView gridView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i= new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availActivities= manager.queryIntentActivities(i, 0);

        for (ResolveInfo re: availActivities)
        {
            AppsActivity app= new AppsActivity();
            app.label= re.activityInfo.packageName;
            app.name= re.loadLabel(manager);
            app.icon= re.loadIcon(manager);
            apps.add(app);
        }
    }

    private void loadListView()
    {
        gridView=  findViewById(R.id.grid);
        mediaPlayer= MediaPlayer.create(this, R.raw.ding);

        ArrayAdapter<AppsActivity> adapter= new ArrayAdapter<AppsActivity>(this, R.layout.item, apps)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item, null);
                }
                ImageView appIcon =  convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appName =  convertView.findViewById(R.id.name);
                appName.setText(apps.get(position).name);
                return convertView;
            }
        };
        gridView.setAdapter(adapter);
    }

    private void addClickListener(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= manager.getLaunchIntentForPackage(apps.get(i).label.toString());
                startActivity(intent);
                mediaPlayer.start();
            }
        });
    }
}
