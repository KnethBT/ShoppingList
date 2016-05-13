package org.projects.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

//import com.flurry.android.FlurryAgen;


public class MainActivity extends AppCompatActivity
{
    //Firebase
    Firebase mRef = new Firebase("https://amber-fire-9651.firebaseIO.com/ShoppingList");
    FirebaseListAdapter<Product> adapter;

    ListView listView;
    EditText txtProduct;
    EditText txtAmountOfProduct;

    Product lastDeletedProduct;

    //ArrayList<Product> bag = new ArrayList<Product>();

    //Flurry
    public static String FlurryKey = "M2BPW8RFPFCQYBRCM4K8";

    public FirebaseListAdapter<Product> getMyAdapter()
    {
        return  adapter;
    }

   public Product getItem(int index)
   {
       return getMyAdapter().getItem(index);
   }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Flurry:
      //  New FlurryAgent.Builder().withLogEnabled(false).build(this, FlurryKey);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);

        adapter = new FirebaseListAdapter<Product>(
                this,
                Product.class,
                android.R.layout.simple_list_item_checked,
                mRef)
        {
            @Override
            protected void populateView(View view, Product product, int i)
            {
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(product.toString());
            }
        };
        //setting the adapter on the listview
        listView.setAdapter(adapter);

        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        txtProduct = (EditText) findViewById(R.id.txtProduct);
        txtAmountOfProduct = (EditText) findViewById(R.id.txtNumberOfProduct);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtAmountOfProduct.getText().toString().length() > 0 && txtProduct.getText().length() > 0 ) {
                    //Firebase:
                    Product p = new Product(txtProduct.getText().toString(), Integer.parseInt(txtAmountOfProduct.getText().toString()));
                    mRef.push().setValue(p);
                    //FlurryAgent.logEvent("Item Added");

                    //The next line is needed in order to say to the ListView
                    //that the data has changed - we have added stuff now!
                    getMyAdapter().notifyDataSetChanged();
                }
                else
                {
                    //Giv brugeren feedback på hvad de mangler at udfylde!
                }
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getCheckedItemPosition() > -1) {

                    String testText = listView.getItemAtPosition(listView.getCheckedItemPosition()).toString().split(" : ")[1];
                    int testNr = Integer.parseInt(listView.getItemAtPosition(listView.getCheckedItemPosition()).toString().split(" : ")[0]);
                    lastDeletedProduct = new Product(testText,testNr);
                    getMyAdapter().getRef(listView.getCheckedItemPosition()).setValue(null);
                   // FlurryAgent.logEvent("Item Deleted");

                    //Fjern selection (ellers vil den valgte plads, blive ved med at være slected!)
                    listView.clearChoices();

                    final View parent = findViewById(R.id.layout);

                    Snackbar snackbar = Snackbar
                            .make(parent, "Product deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //This code will ONLY be executed in case that
                                    //the user has hit the UNDO button
                                    mRef.push().setValue(lastDeletedProduct);

                                    Snackbar snackbar = Snackbar.make(parent, "Product restored!", Snackbar.LENGTH_SHORT);

                                    //Show the user we have restored the name - but here
                                    //on this snackbar there is NO UNDO - so not SetAction method is called
                                    snackbar.show();

                                    getMyAdapter().notifyDataSetChanged();
                                }
                            });
                    snackbar.show();

                    getMyAdapter().notifyDataSetChanged();
                }
            }
        });


        //Load Selected
        if (savedInstanceState!=null)
        {
            listView.setSelection(savedInstanceState.getInt("selectedPos"));
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_editSeleceted)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText inputProduct = new EditText(this);
            final EditText inputAmount = new EditText(this);

            //Input fields:
            inputProduct.setInputType(InputType.TYPE_CLASS_TEXT);
            inputAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputProduct.setText(listView.getItemAtPosition(listView.getCheckedItemPosition()).toString().split(" : ")[1]);
            inputAmount.setText(listView.getItemAtPosition(listView.getCheckedItemPosition()).toString().split(" : ")[0]);

            //Tilføjer mine textfields til min layout:
            layout.addView(inputProduct);
            layout.addView(inputAmount);

            builder.setView(layout);
            //Buttons:
            builder.setPositiveButton("Edit",  new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (inputProduct.getText().length() > 0 && inputAmount.getText().length() > 0 )
                    {

                        getMyAdapter().getRef(listView.getCheckedItemPosition()).setValue
                        (
                                new Product
                                        (
                                            inputProduct.getText().toString(),
                                            Integer.parseInt(inputAmount.getText().toString())
                                        )
                        );

                    }
                }
            });
            builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                   dialog.cancel();
                }
            });
            builder.show();
        }
        else if (id == R.id.action_clearList)
        {
            //Dialog box, der spørg brugen om han er sikker på om han vil clear listen.
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("Confirm clearing list");
            alertDialog.setMessage("Are you sure you want to clear the list?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public  void onClick(DialogInterface dialog, int which)
                {
                    mRef.setValue(null);
                    getMyAdapter().notifyDataSetChanged();

                   // FlurryAgent.logEvent("ShoppingList Cleared");

                    //Giv brugeren feedback!:
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Cleard!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                public  void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                    //Giv brugeren feedback!:
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Canceled", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            alertDialog.show();
        }
        else if (id == R.id.action_shareButton)
        {
            String shoppingList = lstStringConverter();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(intent.EXTRA_TEXT, shoppingList);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected  void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedPos", listView.getCheckedItemPosition());
    }


    public String lstStringConverter()
    {
        SharedPreferences sPrefs = getSharedPreferences("settingPrefs", MODE_PRIVATE);
        String name = sPrefs.getString("name", "YourName");

        String shoppingList = name + "ShoppingList:\n";
        for (int i = 0; i < adapter.getCount(); i++)
        {
            Product product = adapter.getItem(i);
            shoppingList += product.getQuantity() + " : " + product.name + "\n";
        }

        return shoppingList;
    }
}
