package com.example.huc_project.homepage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huc_project.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<PostRow> itemsList;
    public List<PostRow> itemsListFull;
    private OnItemListener onItemListener;

    public RecyclerViewAdapter() {}

    public RecyclerViewAdapter(List<PostRow> itemList, OnItemListener onItemListener) {
        this.itemsList = itemList;
        this.onItemListener = onItemListener;
        itemsListFull = new ArrayList<>(itemList);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_row, parent, false);
            return new ItemViewHolder(view, onItemListener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {

            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {

        return itemsList == null ? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

        RelativeLayout tvItem;

        OnItemListener onItemListener;

        public ItemViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.relative);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.itemRow_progressBar);
        }

    }

    public interface OnItemListener{
        void onItemClick(int position);
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        PostRow item = itemsList.get(position);
        String title = item.post.title;
        String desc = item.post.postdesc;
        StorageReference img_ref = item.img_ref;


        TextView tv1 = (TextView) viewHolder.tvItem.getChildAt(1);
        TextView tv2 = (TextView) viewHolder.tvItem.getChildAt(2);
        ImageView view = (ImageView) viewHolder.tvItem.getChildAt(0);

        if(title.length()>30){
            String longtitle = title.trim().substring(0, Math.min(title.length(), 30)) + " ... ";
            tv1.setText(longtitle.trim());
        } else {
            tv1.setText(title.trim());
        }

        if(desc.length()>70){
            String long_desc = desc.trim().substring(0, Math.min(desc.length(), 70)) + " ... ";
            tv2.setText(long_desc.trim());
        } else {
            tv2.setText(desc.trim());
        }
        if(img_ref!=null ) Homepage.glideTask(item.glide, img_ref, view);
        else {
            //default image
            view.setImageResource(R.drawable.ic_no_image);
        }
        int i = 0;
        LinearLayout tag_cards = (LinearLayout) viewHolder.tvItem.getChildAt(4);
        ArrayList<String> mytags = item.post.getCategories();
        try {
            for(i=0; i < mytags.size(); i++) ((TextView) tag_cards.getChildAt(i)).setText(switchText(mytags.get(i)));
            for(; i < 3; i++) ((TextView) tag_cards.getChildAt(i)).setVisibility(View.INVISIBLE);
        } catch(Exception e) {
            for(; i < 3; i++) ((TextView) tag_cards.getChildAt(i)).setVisibility(View.INVISIBLE);
        }

        TextView pos = (TextView) ((LinearLayout) viewHolder.tvItem.getChildAt(3)).getChildAt(1);
        if(item.getPost().getCountry()!= null && !item.post.country.trim().equals("") && item.getPost().getCity()!= null && !item.post.city.trim().equals("")){
            pos.setText(item.post.country+ ", "+item.post.city);
        }
        else if(item.getPost().getCountry()!= null && !item.post.country.trim().equals("")){
            pos.setText(item.post.country);
        }
        else if(item.getPost().getCity()!= null && !item.post.city.trim().equals("")){
            pos.setText(item.post.city);
        }
        else{
            pos.setVisibility(View.INVISIBLE);
            ((LinearLayout) viewHolder.tvItem.getChildAt(3)).getChildAt(0).setVisibility(View.INVISIBLE);
        }
    }

    private String switchText(String tag){

        String res = "#";

        switch (tag) {
            case "nature": return res+"Nature";
            case "science": return res+"Science&IT";
            case "food": return res+"Food";
            case "fashion": return res+"Fashion";
            case "sport": return res+"Sport";
            case "movies": return res+"Movies";
            default: return res+"Music";
        }

    }



    @Override
    public Filter getFilter() {
        return filtered_results;
    }

    private Filter filtered_results = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PostRow> filteredList = new ArrayList<>();
            List<PostRow> filteredListCategories = new ArrayList<>();
            List<PostRow> filteredListRole = new ArrayList<>();
            List<PostRow> filteredListPackage = new ArrayList<>();
            List<PostRow> filteredListCountries = new ArrayList<>();
            List<PostRow> filteredListCities = new ArrayList<>();
            List<PostRow> filteredListFilters = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(itemsListFull);
            }
            else if (constraint.toString().contains("/-/-/")) {
                String[] words = constraint.toString().split("/-/-/", -1);


                String[] categories={"niente","science", "nature", "sport", "fashion", "food", "movie", "music", "sponsorship", "sponsor", "is_package", "country"};

                Boolean isitfiltered=false;
                Boolean roleisasked=false;
                Boolean categoryisasked=false;
                Boolean packageisasked=false;
                Boolean countryisasked=false;
                Boolean cityisasked=false;

                String onetry="1";
                onetry= onetry.toLowerCase().trim();

                for (int i = 1; i < words.length ; i++) {
                    String w = words[i];
                    w = w.toLowerCase().trim();
                    if (w.equals(onetry)) {
                        isitfiltered=true;
                    }
                }

                //check if there are filters on CATEGORIES (1-7)
                for (int i = 1; i < 8 ; i++) {
                    String w = words[i];
                    w = w.toLowerCase().trim();
                    if (w.equals(onetry)) {
                        isitfiltered=true;
                        categoryisasked=true;
                    }
                }

                //check if there are filters on SPONSOR/SPONSORSHIP (8-9)
                if (words[8].equals(onetry) || words[9].equals((onetry))) {
                    isitfiltered=true;
                    roleisasked=true;

                }

                //check if there are filters on PACKAGE (10)
                if (words[10].equals(onetry)) {
                    isitfiltered=true;
                    packageisasked=true;
                    for (PostRow s: itemsListFull) {
                        if (s.post.isPackage) {
                            if (!filteredListPackage.contains(s)) {
                                filteredListPackage.add(s);
                            }
                        }
                    }

                }


                String filterPatternCountry = words[11].toString().toLowerCase().trim();
                if (!(filterPatternCountry.equals(("")))) {
                    Log.e("provacountry", "CIAO NON DOVREI ESSERE QUI PORCO DIO");
                    isitfiltered=true;
                    countryisasked=true;
                    Log.e("tagg", "OK RICONOSCE CHE NON è VUOTA");
                    Log.e("insidefilter", filterPatternCountry);
                    for (PostRow s: itemsListFull) {
                        Log.e("countryinpost", filterPatternCountry);
                        Log.e("countryinpost", s.post.country);
                        if (s.post.country.toLowerCase().contains(filterPatternCountry)) {
                            if (!filteredListCountries.contains(s)) {
                                filteredListCountries.add(s);
                            }
                        }
                    }
                    if (!packageisasked) {
                        filteredListPackage=filteredListCountries;
                        packageisasked=true;
                    }
                    else {
                        filteredListPackage.retainAll((filteredListCountries));
                    }
                }


                //check if there are filters on CITY (12) and join the post obtained with that of package
                String filterPatternCity = words[12].toString().toLowerCase().trim();
                if (!(filterPatternCity.equals(("")))) {
                    isitfiltered=true;
                    cityisasked=true;
                    for (PostRow s: itemsListFull) {
                        if (s.post.city.toLowerCase().contains(filterPatternCity)) {
                            if (!filteredListCities.contains(s)) {
                                filteredListCities.add(s);
                            }
                        }
                    }
                    if (!packageisasked) {
                        filteredListPackage=filteredListCities;
                        packageisasked=true;
                    }
                    else {
                        filteredListPackage.retainAll((filteredListCities));
                    }
                }


                for (int i = 1; i < 10 ; i++) {
                    String w = words[i];
                    w = w.toLowerCase().trim();
                    categories[i]=categories[i].toLowerCase().trim();
                    if (w.equals(onetry)) {
                        for (PostRow s : itemsListFull) {
                            if (s.post.categories.contains(categories[i])) {
                                if (!filteredListCategories.contains(s)) {
                                    filteredListCategories.add(s);
                                }
                            }
                            else if (s.post.role.equals(categories[i])) {
                                if (!filteredListRole.contains(s)) {
                                    filteredListRole.add(s);
                                }
                            }

                        }

                    }

                }


                //HERE I DO THE INTERSECTION BETWEEN ALL THE FILTERS EXCLUDING THE SEARCH BAR FILTER (I LET THE ELSE IF JUST FOR BETTER READING)
                if (isitfiltered) {
                    if (categoryisasked) {
                        filteredListFilters=filteredListCategories;
                        if (roleisasked) {
                            filteredListFilters.retainAll(filteredListRole);
                            if (packageisasked) {
                                filteredListFilters.retainAll(filteredListPackage);
                            }
                            else if (!packageisasked) {
                                //Do nothing
                            }
                        }
                        else if (!roleisasked){
                            if (packageisasked) {
                                filteredListFilters.retainAll(filteredListPackage);
                            }
                            else if (!packageisasked) {
                                //do nothing
                            }
                        }
                    }
                    else if (!categoryisasked) {
                        if (roleisasked) {
                            filteredListFilters=filteredListRole;
                            if (packageisasked) {
                                filteredListFilters.retainAll(filteredListPackage);
                            }
                            else if (!packageisasked) {
                                //do nothing
                            }
                        }
                        else if (!roleisasked){
                            if (packageisasked) {
                                filteredListFilters=filteredListPackage;
                            }
                            else if (!packageisasked) {
                                //THIS CONDITION SHOULD NEVER BE MET
                                Log.e("tagg", "NON DOVRESTI ESSERE QUI PERCHè è TUTTO FALSO E QUINDI isitfiltered dovrebbe essere false");

                            }
                        }

                    }
                }


                //HERE I DO THE FILTER WITH THE SEARCH BAR

                String filterPattern = words[0].toString().toLowerCase().trim();
                if (filterPattern.equals("")) {
                    if (isitfiltered) {
                        filteredList = filteredListFilters;
                    }
                    else {
                        filteredList.addAll(itemsListFull);
                    }
                }
                else {
                    if (isitfiltered) {
                        for (PostRow s : itemsListFull) {
                            if (s.post.title.toLowerCase().contains(filterPattern) || s.post.postdesc.toLowerCase().contains(filterPattern) || s.post.categories.contains(filterPattern)) {
                                filteredList.add(s);
                            }
                        }

                        filteredList.retainAll(filteredListFilters);
                    }
                    else {
                        for (PostRow s : itemsListFull) {
                            if (s.post.title.toLowerCase().contains(filterPattern) || s.post.postdesc.toLowerCase().contains(filterPattern) || s.post.categories.contains(filterPattern)) {
                                filteredList.add(s);
                            }
                        }
                    }
                     //FUNZIONA PER I CASI IN CUI LA STRINGA IN RICERCA è VUOTA?
                }


            }




            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PostRow s : itemsListFull){
                    if(s.post.title.toLowerCase().contains(filterPattern) || s.post.postdesc.toLowerCase().contains(filterPattern) || s.post.categories.contains(filterPattern) ){
                        filteredList.add(s);
                    }
                }
            }


            FilterResults res = new FilterResults();
            res.values = filteredList;
            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((Collection<? extends PostRow>) results.values);
            notifyDataSetChanged();
        }
    };

}
