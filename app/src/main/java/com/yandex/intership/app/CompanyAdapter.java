package com.yandex.intership.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> implements Filterable {

    private static int viewHolderCount;
    private int companyItems;
    private Context context;
    private ArrayList<Company> companyList;
    private ArrayList<Company> copyOfCompanyList;
    public CompanyAdapter(Context context, int companyItems, ArrayList<Company> companyList){
        this.companyItems = companyList.size();
        this.context = context;
        this.companyList = companyList;
        this.copyOfCompanyList = new ArrayList<>(this.companyList);
        viewHolderCount = 0;
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        CompanyViewHolder viewHolder = new CompanyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
       holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.companyList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Toast.makeText(context, "---------------------------", Toast.LENGTH_LONG).show();
                List<Company> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(copyOfCompanyList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Company item : copyOfCompanyList) {
                        //if (item.getName().toLowerCase().substring(0, filterPattern.length()).equals(filterPattern)) {
                         if(item.getName().toLowerCase().contains(filterPattern)){
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                companyList.clear();
                companyList.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }

            ;
        };
    }

    public class CompanyViewHolder extends RecyclerView.ViewHolder {
        TextView symbol;
        TextView name;
        ToggleButton addToFavoriteView;
        TextView price;
        TextView change;
        Company company;
        ShapeableImageView logo;
        public CompanyViewHolder(View itemView) {
            super(itemView);

            symbol = itemView.findViewById(R.id.symbol);
            name = itemView.findViewById(R.id.name);
            addToFavoriteView = (ToggleButton) itemView.findViewById(R.id.addToFavorite);
            price = (TextView) itemView.findViewById(R.id.price);
            change = (TextView) itemView.findViewById(R.id.change);
            logo = (ShapeableImageView)itemView.findViewById(R.id.logo);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positionTappedIndex = getLayoutPosition();
                    Class companyPageActivity = CompanyPage.class;
                    Intent companyActivityIntent = new Intent(context, companyPageActivity);
                    companyActivityIntent.putExtra("position", String.valueOf(positionTappedIndex));
                    companyActivityIntent.putExtra("company", company);
                    context.startActivity(companyActivityIntent);
                }
            });
            this.addToFavoriteView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int positionTappedIndex = getLayoutPosition();
                    Company company = Company.getCompany(positionTappedIndex);
                    if (addToFavoriteView.isChecked()) {
                        company.addToFavorite();
                    } else if (!addToFavoriteView.isChecked()) {
                        company.removeFromFavorite();
                    }
                }
            });

        }

        public void bind(int companyIndex) {

            this.company = companyList.get(companyIndex);
            this.name.setText(this.company.getName());
            this.symbol.setText(this.company.getSymbol());
            this.price.setText(String.valueOf(this.company.getCurrentPrice()));
            this.change.setText(this.company.getPriceChangeField());
            if (company.getPriceChangeField().charAt(0) == '-')
                this.change.setTextColor(Color.RED);
            else this.change.setTextColor(Color.parseColor("#007F16"));
            int id = context.getResources().getIdentifier("com.yandex.intership.app:drawable/logo_" + this.company.getSymbol().toLowerCase(), null, null);
            this.logo.setImageResource(id);
            if (Company.getCompanyFavoriteList().contains(this.company))
                this.addToFavoriteView.setChecked(true);
            else {
                this.addToFavoriteView.setChecked(false);
            }

        }
    }
}
