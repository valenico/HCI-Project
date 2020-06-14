package com.example.huc_project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.huc_project.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Settings extends AppCompatActivity implements CustomAdapter.OnItemListener {

    private final List<String> listItems = new ArrayList<>();
    private RecyclerView recyclerView;
    CustomAdapter adapter;
    private int cases = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listItems.add("Invite Friends");
        listItems.add("General");
        listItems.add("Privacy & Security");
        listItems.add("Help & About");

        setUpRecyclerView();
        initScrollListener();

    }

    private void setUpRecyclerView(){
        recyclerView = findViewById(R.id.list_of_settings);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new CustomAdapter(listItems, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

        ArrayList<String> mylist = new ArrayList<>(listItems);
        String clicked = listItems.get(position);

        if(clicked.equals("General") && !mylist.contains("Change Email")){
            listItems.add(position+1,"Change Email");
            listItems.add(position+2,"Change Username");
            listItems.add(position+3,"Language");
            listItems.add(position+4,"Modify or Delete Post");
            listItems.add(position+5,"Others");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("General")){
            listItems.remove("Change Email");
            listItems.remove("Change Username");
            listItems.remove("Language");
            listItems.remove("Modify or Delete Post");
            listItems.remove("Others");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Help & About") && !mylist.contains("Report a Problem")){
            listItems.add(position+1,"Report a Problem");
            listItems.add(position+2,"Send a Feedback");
            listItems.add(position+3,"Terms of Use");
            listItems.add(position+4,"Data Policy");
            listItems.add(position+5,"About the App");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Help & About")){
            listItems.remove("Report a Problem");
            listItems.remove("Send a Feedback");
            listItems.remove("Terms of Use");
            listItems.remove("Data Policy");
            listItems.remove("About the App");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Privacy & Security") && !mylist.contains("Change Password")){
            listItems.add(position+1,"Change Password");
            listItems.add(position+2,"Account Privacy");
            listItems.add(position+3,"Blocked Accounts");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Privacy & Security")) {
            listItems.remove("Change Password");
            listItems.remove("Account Privacy");
            listItems.remove("Blocked Accounts");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Invite Friends") && !mylist.contains("Invite Friends by Email")){
            listItems.add(position+1,"Invite Friends by Email");
            listItems.add(position+2,"Invite Friends by SMS");
            listItems.add(position+3,"Invite Friends by ...");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Invite Friends")){
            listItems.remove("Invite Friends by Email");
            listItems.remove("Invite Friends by SMS");
            listItems.remove("Invite Friends by ...");
            adapter.notifyDataSetChanged();
        }
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }



}