package com.gea.econtact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements Filterable {

    private Context context;
    private LayoutInflater inflater;
    private List<ContactModel> data;
    private List<ContactModel> dataFilterd;

    // create constructor to innitilize context and data sent from MainActivity
    public ContactAdapter(Context context, List<ContactModel> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data = data;
        this.dataFilterd = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.contact_item, parent,false);

        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        // Get current position of item in recycler view to bind data and assign values from list

        final ContactModel contactModel = data.get(position);
        holder.name.setText(contactModel.getName());
        // convert byte[] to image
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(contactModel.getImage(),0, contactModel.getImage().length);
            holder.circleImageView.setImageBitmap(bmp);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        final String[] Options = {"Delete", "Edit"};
        holder.setOnClickListener(new ItemClickListener() {
            @Override
            public void onClick(final View view, final int position, boolean isLongClcik) {
                if (isLongClcik){
                    AlertDialog.Builder optionDialog;
                    optionDialog = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                    optionDialog.setItems(Options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0){
                                new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                                        .setTitle("Delete Contact")
                                        .setMessage("Are you sure you want to delete this contact?")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Continue with delete operation
                                                data.remove(position);
                                                new Database(context).deleteContact(contactModel.getId());

                                                Snackbar snackbar = Snackbar.make(view,"Contact Deleted", Snackbar.LENGTH_LONG);
                                                snackbar.setAction("UNDO", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        data.add(position, contactModel);
                                                        new Database(context).addContact(contactModel);
                                                        notifyDataSetChanged();
                                                }
                                                });
                                                snackbar.setActionTextColor(Color.YELLOW);
                                                snackbar.show();
                                                notifyDataSetChanged();

                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton("No", null).show();

                            }else if(which == 1){
                                //second option clicked, do this...
                                Intent intent = new Intent(context, EditContactActivity.class);
                                intent.putExtra("contact_id", contactModel.getId()); //Optional parameters
                                context.startActivity(intent);
                            }
                        }
                    });

                    optionDialog.show();

                }else{

                    Intent intent = new Intent(view.getContext(), ContactDetailsActivity.class);
                    intent.putExtra("contact_id", contactModel.getId()); //Optional parameters
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ContactModel getItem(int position){
        return  data.get(position);
    }
    public void removeItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(int position, ContactModel contactModel){
        data.add((position), contactModel);
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {

        return filter;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ContactModel> filterdList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0){
                filterdList.addAll(dataFilterd);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ContactModel contactModel: dataFilterd){
                    if (contactModel.getName().toLowerCase().contains(filterPattern)){
                        filterdList.add(contactModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data.clear();
            data.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
}

class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private ItemClickListener itemClickListener;
    public TextView name;
    public ImageView imageView;
    public CircleImageView circleImageView;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.nameTxt);
        circleImageView =  itemView.findViewById(R.id.contactImgView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    public void setOnClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;

    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}