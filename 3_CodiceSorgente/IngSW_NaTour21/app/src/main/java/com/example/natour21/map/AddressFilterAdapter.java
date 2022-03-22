package com.example.natour21.map;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class AddressFilterAdapter extends ArrayAdapter<String> {

    Filter addressFilter = new Filter(){


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            System.out.println("Filtering happening\n");
            System.out.println(suggestions);
            filterResults.values = suggestions;
            System.out.println(suggestions);
            filterResults.count = suggestions.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }

    };
    ArrayList<String> suggestions;
    public AddressFilterAdapter(Context context, int resource, List<String> strings){
        super(context, resource, strings);
        if(strings != null) suggestions = new ArrayList<String>(strings);
        else suggestions = new ArrayList<String>();
    }

    @Override
    public Filter getFilter(){
        return addressFilter;
    }

    public void setSuggestions(List<String> suggestions){
        if (suggestions != null)
            this.suggestions = new ArrayList<>(suggestions);
        else this.suggestions = new ArrayList<String>();
    }



}
