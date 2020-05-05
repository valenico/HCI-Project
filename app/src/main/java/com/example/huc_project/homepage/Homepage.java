package com.example.huc_project.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.huc_project.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

public class Homepage extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> rowsArrayList = new ArrayList<>();

    boolean guest_mode = false;
    boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Intent i = getIntent();
        String guest = i.getStringExtra("guest");
        if(guest != null && guest == "true"){
            guest_mode = true;
        }

        populateData();
        setUpRecyclerView();
        setUpCircularMenu();
        initScrollListener();

    }

    private void populateData() {
        int i = 0;
        while (i < 10) {
            rowsArrayList.add("Item " + i);
            i++;
        }
    }

    private void setUpCircularMenu(){
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView settingsItem = new ImageView(this);
        settingsItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
        SubActionButton button1 = itemBuilder.setContentView(settingsItem).build();

        ImageView chatItem = new ImageView(this);
        chatItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));
        SubActionButton button2 = itemBuilder.setContentView(chatItem).build();

        ImageView profItem = new ImageView(this);
        profItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
        SubActionButton button3 = itemBuilder.setContentView(profItem).build();

        ImageView addItem = new ImageView(this);
        addItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        SubActionButton button4 = itemBuilder.setContentView(addItem).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .addSubActionView(button4).attachTo(actionButton).build();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
                        //bottom of list!
                        //loadMore();
                        //isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        rowsArrayList.add(null);
        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rowsArrayList.remove(rowsArrayList.size() - 1);
                int scrollPosition = rowsArrayList.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    rowsArrayList.add("Item " + currentSize);
                    currentSize++;
                }

                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();

        MenuItem addItem = menu.findItem(R.id.add_icon);
        Button addView = (Button) addItem.getActionView();

        MenuItem postsItem = menu.findItem(R.id.post_icon);
        Button postsView = (Button) postsItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataGettingActivity.class);
                startActivity(intent);
            }
        });

        postsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateNewPostActivity.class);
                startActivity(intent);
            }
        });
        return true;
    }
}
