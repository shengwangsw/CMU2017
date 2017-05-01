package pt.ulisboa.tecnico.meic.cmu.locmess;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Akilino on 16/03/2017.
 */

public class NewPost extends AppCompatActivity implements PropertiesAdapter.ItemClickCallback{

    private ArrayList<Property> properties;
    private Toolbar toolbar;
    private EditText fromDateEditText, fromTimeEditText, toDateEditText, toTimeEditText;
    private Calendar calendar;
    private Spinner locationSpinner;
    private PropertiesAdapter adapter;
    private Button addButton;
    private String helper,key,value = null;
    private Property property;
    RecyclerView recyclerView;
    String[] keys;
    ArrayList<String> keysList;
    List<String> spinnerArray = new ArrayList<>();
    LocationActivity locationActivity = new LocationActivity();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.new_post);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        populateSpinner();

        calendar = Calendar.getInstance();

        fromDateEditText = (EditText) findViewById(R.id.fromDateEditText);
        fromTimeEditText = (EditText) findViewById(R.id.fromTimeEditText);
        toDateEditText = (EditText) findViewById(R.id.toDateEditText);
        toTimeEditText = (EditText) findViewById(R.id.toTimeEditText);

        addButton = (Button) findViewById(R.id.addButton);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProperties);
        setupRecyclerView();

        keysList = new ArrayList<>();
        keys = getResources().getStringArray(R.array.keys);

        for(int i = 0; i < keys.length; i++)
            keysList.add(keys[i]);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewPost.this,date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                helper = "fromDate";
            }
        });

        toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewPost.this,date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                helper = "toDate";
            }
        });

        fromTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NewPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10){
                            fromTimeEditText.setText("0" + hourOfDay + ":" + minute);
                        }else{
                            fromTimeEditText.setText(hourOfDay + ":" + minute);
                        }
                    }
                },hour,minute,true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        });

        toTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog;
                timePickerDialog = new TimePickerDialog(NewPost.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10){
                            toTimeEditText.setText("0" + hourOfDay + ":" + minute);
                        }else{
                            toTimeEditText.setText(hourOfDay + ":" + minute);
                        }
                    }
                },hour,minute,true);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();

            }
        });

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            updateLabel();
        }
    };

    public void populateSpinner(){

        ArrayList<Location> locationList = locationActivity.populateView();
        int size = locationActivity.populateView().size();

        for(int i = 0; i < size; i++){
            spinnerArray.add(locationList.get(i).locationName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

    }

    public void setupRecyclerView(){
        properties = new ArrayList<>();
        adapter = new PropertiesAdapter(this,properties);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItemClickCallback(this);
    }

    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());

                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }

    private void moveItem(int oldPos, int newPos){
        Property property = properties.get(oldPos);
        properties.remove(oldPos);
        properties.add(newPos,property);
        adapter.notifyItemMoved(oldPos,newPos);
    }

    private void deleteItem(final int position){
        properties.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_create){
            Toast.makeText(this, "Post successfuly created!", Toast.LENGTH_SHORT).show();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLabel(){
        String myFormat = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        if(helper.equals("fromDate")){
            fromDateEditText.setText(simpleDateFormat.format(calendar.getTime()));
        }else if(helper.equals("toDate")){
            toDateEditText.setText(simpleDateFormat.format(calendar.getTime()));
        }

    }

    public static ArrayList<String> populateList(int numPosts){
        ArrayList<String> list = new ArrayList<>();

        for(int i = 1; i <= numPosts;i++){
            list.add("Key" + i + ": Value" + i);
        }

        return list;
    }

    public void openDialogAddProperty(View view){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_edit_property, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        final AutoCompleteTextView keyValue = (AutoCompleteTextView) promptsView
                .findViewById(R.id.autoCompleteTextView1);

        final ArrayAdapter<String> completeTextViewAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,keysList);
        keyValue.setAdapter(completeTextViewAdapter);

        final EditText valueValue = (EditText) promptsView
                .findViewById(R.id.valueEditText);

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Add Property")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                key = keyValue.getText().toString();
                                value = valueValue.getText().toString();

                                property = new Property(key, value);

                                if(!keysList.contains(key)){
                                    Toast.makeText(NewPost.this, "bool: " + (!Arrays.asList(keysList).contains(key)), Toast.LENGTH_SHORT).show();
                                    keysList.add(key);
                                    completeTextViewAdapter.notifyDataSetChanged();
                                }

                                Toast.makeText(NewPost.this, "value: " + key + ":" + value, Toast.LENGTH_SHORT).show();
                                properties.add(0,property);
                                adapter.notifyItemInserted(0);
                                //adapter.notifyItemInserted(properties.size());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    public void openDialogEditProperty(final int position){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_edit_property, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        final AutoCompleteTextView keyValue = (AutoCompleteTextView) promptsView
                .findViewById(R.id.autoCompleteTextView1);

        final ArrayAdapter<String> completeTextViewAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,keysList);
        keyValue.setAdapter(completeTextViewAdapter);

        final EditText valueValue = (EditText) promptsView
                .findViewById(R.id.valueEditText);

        keyValue.setText(properties.get(position).getKey());
        valueValue.setText(properties.get(position).getValue());

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Edit Property")
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                key = keyValue.getText().toString();
                                value = valueValue.getText().toString();

                                property = new Property(key, value);

                                if(!keysList.contains(key)){
                                    keysList.add(key);
                                    completeTextViewAdapter.notifyDataSetChanged();
                                }

                                Toast.makeText(NewPost.this, "value: " + key + ":" + value, Toast.LENGTH_SHORT).show();

                                properties.add(position,property);
                                properties.remove(position+1);
                                adapter.notifyDataSetChanged();

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

    public void openDialogViewProperty(final int position){
        LayoutInflater li = LayoutInflater.from(NewPost.this);
        final View promptsView = li.inflate(R.layout.dialog_view_property, null);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                NewPost.this);

        alertDialogBuilder.setView(promptsView);

        TextView keyValue = (TextView) promptsView
                .findViewById(R.id.textViewDialogViewKey);


        keyValue.setText(properties.get(position).getText());

        alertDialogBuilder
                .setCancelable(false)
                .setTitle("Property")
                .setPositiveButton("Edit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                openDialogEditProperty(position);
                            }
                        })
                .setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                properties.remove(position);
                                adapter.notifyItemRemoved(position);
                            }
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.whiteListRadioButton:
                if (checked)
                    Toast.makeText(this, "whitelist", Toast.LENGTH_SHORT).show();
                    break;
            case R.id.blackListRadioButton:
                if (checked)
                    Toast.makeText(this, "blacklist", Toast.LENGTH_SHORT).show();
                    break;
            case R.id.decentralizedRadioButton:
                if(checked)
                    Toast.makeText(this, "decentralized", Toast.LENGTH_SHORT).show();
                    break;
            case R.id.centralizedModeRadioButton:
                if(checked)
                    Toast.makeText(this, "centralized", Toast.LENGTH_SHORT).show();
                    break;
        }
    }

    @Override
    public void onItemClick(int p) {
        Log.d("newPost", "onItemClick: it clicked");
        openDialogViewProperty(p);
    }
}
