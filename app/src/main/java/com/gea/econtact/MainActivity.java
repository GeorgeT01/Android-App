package com.gea.econtact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private FloatingActionButton AddContactBtn;
    private RecyclerView recyclerView;
    private Database database;
    private Context context;
    ContactAdapter contactAdapter;
    List<ContactModel> contactModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.contactRecyclerView);
        context = this;
        database = new Database(context);
        AddContactBtn = findViewById(R.id.AddContactBtn);

        AddContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                //intent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(intent);

            }
        });
        loadList();
        // Swipe to delete left and right
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final ContactModel deleteItem = ((ContactAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
                final int deleteIndex = viewHolder.getAdapterPosition();
                new AlertDialog.Builder(context, R.style.CustomAlertDialog)
                        .setTitle("Delete Contact")
                        .setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                contactAdapter.removeItem(viewHolder.getAdapterPosition());
                                new Database(context).deleteContact(deleteItem.getId());

                                final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Contact Deleted", Snackbar.LENGTH_LONG);
                                snackbar.setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //contactAdapter.restoreItem(deleteItem, viewHolder.getAdapterPosition());
                                        snackbar.dismiss();
                                        new Database(context).addContact(deleteItem);
                                        contactAdapter.restoreItem(deleteIndex, deleteItem);
                                        contactAdapter.notifyDataSetChanged();


                                    }
                                });
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        contactAdapter.notifyItemChanged(deleteIndex);
                    }
                })
                .setIcon(R.drawable.ic_warning_yellow)
                .show();


            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        //recyclerview divider
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    private void loadList(){
        contactModelList = database.readContacts();
        contactAdapter = new ContactAdapter(context, contactModelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchAction);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                contactAdapter.getFilter().filter(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
