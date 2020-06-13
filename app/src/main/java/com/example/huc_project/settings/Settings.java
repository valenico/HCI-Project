package com.example.huc_project.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.huc_project.R;
import com.example.huc_project.Signup;

import java.util.ArrayList;

import static android.widget.ArrayAdapter.*;

public class Settings extends AppCompatActivity {

    private final ArrayList<String> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView lw = findViewById(R.id.list_of_settings);

        listItems.add("Invite Friends");
        listItems.add("General");
        listItems.add("Privacy & Security");
        listItems.add("Help & About");

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_list_item_1, listItems);
        adapter.setNotifyOnChange(true);
        lw.setAdapter(adapter);

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String che_cazzo_ho_premuto = ((TextView)view).getText().toString();

                if(che_cazzo_ho_premuto.equals("Invite Friends") && listItems.contains("Invite Friends by Email")){
                    listItems.remove("Invite Friends by Email");
                    listItems.remove("Invite Friends by SMS");
                    adapter.notifyDataSetChanged();
                } else if(che_cazzo_ho_premuto.equals("Invite Friends") && !listItems.contains("Invite Friends by Email")){
                    listItems.add(position+1,"Invite Friends by Email");
                    listItems.add(position+2,"Invite Friends by SMS");
                    adapter.notifyDataSetChanged();
                } else if(che_cazzo_ho_premuto.equals("General") && listItems.contains("Others")){
                    listItems.remove("Change Email");
                    listItems.remove("Change Username");
                    listItems.remove("Language");
                    listItems.remove("Modify/Delete Post");
                    listItems.remove("Others");
                    adapter.notifyDataSetChanged();
                } else if (che_cazzo_ho_premuto.equals("General") && !listItems.contains("Others")){
                    listItems.add(position+1,"Change Email");
                    listItems.add(position+2,"Change Username");
                    listItems.add(position+3,"Language");
                    listItems.add(position+4,"Modify/Delete Post");
                    listItems.add(position+5,"Others");
                    adapter.notifyDataSetChanged();
                }
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