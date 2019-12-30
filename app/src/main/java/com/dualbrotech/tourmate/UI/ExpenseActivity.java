package com.dualbrotech.tourmate.UI;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dualbrotech.tourmate.Adapters.ExpenseAdapter;
import com.dualbrotech.tourmate.MapActivity;
import com.dualbrotech.tourmate.Models.Event;
import com.dualbrotech.tourmate.Models.EventImage;
import com.dualbrotech.tourmate.Models.Expense;
import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.others.URLs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpenseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Menu menu;
    FirebaseAuth auth;
    ArrayList<Expense> expenses = new ArrayList<>();

    SharedPreferences preferences;

    SharedPreferences.Editor editor;
    Intent getEvent;

    TextView tv_progressPercent, tv_eventBudget, tv_moneySpent, tv_moneyLeft, tv_balance, tv_updateDate;
    ProgressBar progressBar;
    ImageView iv_add_money;
    GridView expenseGridView;

    private Context context;
    private int year, month, day;
    private Calendar calendar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myref;
    Event event;

    ExpenseAdapter adapter;
    int eventBud=0;


    private boolean fabExpanded = false;
    private FloatingActionButton fabSettings;
    private LinearLayout favCamera;
    private LinearLayout favAddExpense;
    private LinearLayout favGallery;

    Uri file;
    StorageReference storageReference;

    ProgressDialog ringProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        checkLocationPermission();

        storageReference= FirebaseStorage.getInstance().getReference();

        fabSettings = (FloatingActionButton) this.findViewById(R.id.fabSetting);

        favCamera = (LinearLayout) this.findViewById(R.id.fabCamera);
        favAddExpense = (LinearLayout) this.findViewById(R.id.fabAddExpense);
        favGallery = (LinearLayout) this.findViewById(R.id.fabGallery);
        //layoutFabSettings = (LinearLayout) this.findViewById(R.id.layoutFabSettings);

        //When main Fab (Settings) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
        fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        favAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                showAddExpenseDialogue();
            }
        });

        favCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeSubMenusFab();
                takePicture();

            }
        });

        favGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,GalleryActivity.class);
                intent.putExtra("eventId",event.getNodeKey());
                startActivity(intent);
            }
        });


        //Only main FAB is visible in the beginning
        closeSubMenusFab();


        context = this;
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myref = firebaseDatabase.getReference("expenseInfo");
        myref.keepSynced(true);

        viewInitialization();

        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy");
        calendar.set(year,month,day);
        String finalDate = "Updated on "+sdf.format(calendar.getTime());
        tv_updateDate.setText(finalDate);

        getEvent = getIntent();

        event = (Event) getEvent.getSerializableExtra("event");
        eventBud = (int) Long.parseLong(event.getEventBudget());

        progressBar.setMax((int) Long.parseLong(event.getEventBudget()));

        auth = FirebaseAuth.getInstance();
        othersInitialization();

        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();

        adapter = new ExpenseAdapter(context,expenses);
        expenseGridView.setAdapter(adapter);

        expenseGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                PopupMenu popup = new PopupMenu(context, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.update_or_delete_menu, popup.getMenu());
                popup.setGravity(Gravity.RIGHT);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.edit_item:
                                updateExpense(i);
                                return true;
                            case R.id.delete_item:
                                deleteExpense(i);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;
            }
        });

        getDataFromDatabase();







    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Uploading Image ...", true);
                ringProgressDialog.setCancelable(false);
                //imageView.setImageURI(file);

                final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                StorageReference reference = storageReference.child("images/"+"JPG_"+timeStamp);
                reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri uri=taskSnapshot.getDownloadUrl();
                        String downloadUrl=uri.getEncodedPath();
                        DatabaseReference reference1 = firebaseDatabase.getReference("event_images");
                        EventImage image = new EventImage(event.getNodeKey(),downloadUrl,"JPG_"+timeStamp);
                        String key = reference1.push().getKey();
                        reference1.child(key).setValue(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    ringProgressDialog.dismiss();
                                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //Toast.makeText(context, ""+downloadUrl, Toast.LENGTH_SHORT).show();
                        Log.e("lol",downloadUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private void closeSubMenusFab(){
        favCamera.setVisibility(View.INVISIBLE);
        favAddExpense.setVisibility(View.INVISIBLE);
        favGallery.setVisibility(View.INVISIBLE);
        fabSettings.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        favCamera.setVisibility(View.VISIBLE);
        favAddExpense.setVisibility(View.VISIBLE);
        favGallery.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabSettings.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }

    private void deleteExpense(int position) {
        myref.child(expenses.get(position).getExpenseId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateExpense(final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.expense_dialogue, null);
        dialogBuilder.setView(dialogView);

        final EditText et_expense_title =  dialogView.findViewById(R.id.et_expense_title);
        final EditText et_expensetDate = dialogView.findViewById(R.id.et_expensetDate);
        final EditText et_expenseAmount = dialogView.findViewById(R.id.et_expenseAmount);
        Button btn_addExpense = dialogView.findViewById(R.id.btn_addExpense);

        et_expense_title.setText(expenses.get(position).getExpenseTitle());
        et_expensetDate.setText(expenses.get(position).getExpenseDate());
        et_expenseAmount.setText(expenses.get(position).getExpenseAmount());

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        et_expensetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy");
                        calendar.set(year,month,dayOfMonth);
                        String finalDate = sdf.format(calendar.getTime());
                        et_expensetDate.setText(finalDate);
                    }
                }, year, month, day
                );
                datePickerDialog.show();
            }
        });

        btn_addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expenseTitle = et_expense_title.getText().toString();
                String expenseDate = et_expensetDate.getText().toString();
                String expenseAmount = et_expenseAmount.getText().toString();

                DatabaseReference reference = firebaseDatabase.getReference("expenseInfo");

                String key = expenses.get(position).getExpenseId();
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Expense expense = new Expense(event.getNodeKey(),key,expenseTitle,expenseDate,expenseAmount);

                reference.child(key).setValue(expense).addOnCompleteListener(ExpenseActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,"Expense Updated",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context,"Failed to update Expense",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                getDataFromDatabase();
                alertDialog.dismiss();
            }
        });

    }

    private void viewInitialization() {
        tv_progressPercent = findViewById(R.id.tv_progress_percent);
        tv_eventBudget = findViewById(R.id.tv_event_budget);
        tv_moneySpent = findViewById(R.id.tv_balance_spent);
        tv_moneyLeft = findViewById(R.id.tv_balance_left);
        tv_balance = findViewById(R.id.tv_balance);
        tv_updateDate = findViewById(R.id.tv_update_date);
        expenseGridView = findViewById(R.id.expense_gridview);
        progressBar = findViewById(R.id.pb);
        iv_add_money = findViewById(R.id.iv_add_money);
    }

    private void othersInitialization() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_expense);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                showAddExpenseDialogue();
//
//
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tv_user_name);
        navUsername.setText("Hi "+auth.getCurrentUser().getDisplayName()+" !");
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void showAddExpenseDialogue() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.expense_dialogue, null);
        dialogBuilder.setView(dialogView);

        final EditText et_expense_title =  dialogView.findViewById(R.id.et_expense_title);
        final EditText et_expensetDate = dialogView.findViewById(R.id.et_expensetDate);
        final EditText et_expenseAmount = dialogView.findViewById(R.id.et_expenseAmount);
        Button btn_addExpense = dialogView.findViewById(R.id.btn_addExpense);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        et_expensetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd, MMM, yyyy");
                        calendar.set(year,month,dayOfMonth);
                        String finalDate = sdf.format(calendar.getTime());
                        et_expensetDate.setText(finalDate);
                    }
                }, year, month, day
                );
                datePickerDialog.show();
            }
        });

        btn_addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expenseTitle = et_expense_title.getText().toString();
                String expenseDate = et_expensetDate.getText().toString();
                String expenseAmount = et_expenseAmount.getText().toString();

                DatabaseReference reference = firebaseDatabase.getReference("expenseInfo");

                String key = reference.push().getKey();
                //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Expense expense = new Expense(event.getNodeKey(),key,expenseTitle,expenseDate,expenseAmount);

                reference.child(key).setValue(expense).addOnCompleteListener(ExpenseActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,"Expense Added",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(context,"Expense not Added",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                getDataFromDatabase();
                alertDialog.dismiss();
            }
        });

    }

    private void getDataFromDatabase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("expenseInfo").orderByChild("eventId").equalTo(event.getNodeKey());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenses.clear();
                for (DataSnapshot items : dataSnapshot.getChildren()){

                    Expense expense = items.getValue(Expense.class);

                    expenses.add(expense);
                    adapter.notifyDataSetChanged();

                    if (expenses.size()>=0){
                        //((TextView) findViewById(R.id.tv_dummyText)).setVisibility(View.GONE);


                        //Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(context, "No expense record.", Toast.LENGTH_LONG).show();
                    }
                }
                Log.e("size","Size: "+expenses.size());

               updateUiValues();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(context,"Lo",Toast.LENGTH_LONG).show();

            }
        });

    }

    private void updateUiValues() {
        int totalExpense = 0, moneyLeft = 0;
        for (int i=0; i<expenses.size(); i++){
            totalExpense +=  Integer.parseInt(expenses.get(i).getExpenseAmount());
        }
        moneyLeft = eventBud - totalExpense;

        int progressPercent = (100*totalExpense)/eventBud;
        if (progressPercent>80){
            LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
            Drawable progressDrawable = progressBarDrawable.getDrawable(1);
            progressDrawable.setColorFilter(ContextCompat.getColor(ExpenseActivity.this, R.color.red), PorterDuff.Mode.SRC_IN);
        }else {
            LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
            Drawable progressDrawable = progressBarDrawable.getDrawable(1);
            progressDrawable.setColorFilter(ContextCompat.getColor(ExpenseActivity.this, R.color.progress), PorterDuff.Mode.SRC_IN);
        }

        if (progressPercent<100){
            tv_progressPercent.setText(String.valueOf(progressPercent)+"%\nspent");
        }else tv_progressPercent.setText("100%\nspent");

        progressBar.setProgress(totalExpense);
        tv_moneyLeft.setText("TK- "+String.valueOf(moneyLeft));
        tv_moneySpent.setText("TK- "+String.valueOf(totalExpense));
        tv_eventBudget.setText("TK- "+String.valueOf(eventBud));
        tv_balance.setText("TK- "+String.valueOf(moneyLeft));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_event_creation).setVisible(false);
        menu.findItem(R.id.action_add_expense).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_expense) {
            showAddExpenseDialogue();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent intent = new Intent(context,MapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(context,EventsActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_weather) {
            Intent intent = new Intent(context,WeatherActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_nearby_places) {
            Intent intent = new Intent(context,NearbyPlacesActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sign_out) {
            auth.signOut();
            editor.clear();
            editor.putBoolean("signedIn",false);
            editor.commit();
            Toast.makeText(this,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,LoginActivity.class);
            finish();
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void moneyPopUp(View view) {
        PopupMenu popup = new PopupMenu(context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menu_add_money:
                        showAddMoneyDialogue();
                        return true;
                    case R.id.menu_lend_money:
                        showLendMoneyDialogue();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void showLendMoneyDialogue() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_money_dialogue, null);
        dialogBuilder.setView(dialogView);

        final EditText et_add_money_amount =  dialogView.findViewById(R.id.et_add_money_amount);

        Button btn_addExpense = dialogView.findViewById(R.id.btn_addMoney);
        btn_addExpense.setText("Lend Money");


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



        btn_addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int moneyAmount = Integer.parseInt(et_add_money_amount.getText().toString());
                eventBud-=moneyAmount;
                progressBar.setMax(eventBud);

                DatabaseReference reference = firebaseDatabase.getReference("eventInfo");
                reference.child(event.getNodeKey()).child("eventBudget").setValue(String.valueOf(eventBud))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //eventBud+=moneyAmount;
                                    Toast.makeText(context, "Money Lended", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                getDataFromDatabase();
                alertDialog.dismiss();
            }
        });
    }

    private void showAddMoneyDialogue() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.add_money_dialogue, null);
        dialogBuilder.setView(dialogView);

        final EditText et_add_money_amount =  dialogView.findViewById(R.id.et_add_money_amount);

        Button btn_addExpense = dialogView.findViewById(R.id.btn_addMoney);


        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



        btn_addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int moneyAmount = Integer.parseInt(et_add_money_amount.getText().toString());
                eventBud+=moneyAmount;
                progressBar.setMax(eventBud);

                DatabaseReference reference = firebaseDatabase.getReference("eventInfo");
                reference.child(event.getNodeKey()).child("eventBudget").setValue(String.valueOf(eventBud))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //eventBud+=moneyAmount;
                                    Toast.makeText(context, "Added money", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                getDataFromDatabase();
                alertDialog.dismiss();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,}, 101);
            return;

        }
    }

}
