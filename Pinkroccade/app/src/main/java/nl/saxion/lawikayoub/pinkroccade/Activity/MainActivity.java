package nl.saxion.lawikayoub.pinkroccade.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.ProgressBar;

import android.widget.Toast;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import nl.saxion.lawikayoub.pinkroccade.Model.ConnectionClass;
import nl.saxion.lawikayoub.pinkroccade.Model.Medewerker;
import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.R;


public class MainActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    EditText editTextUserName, editTextPassword;
    Button loginButton;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionClass = Model.getInstance().getConnectionClass(); // get the connection class

        //get all the views
        editTextUserName = (EditText) findViewById(R.id.editUserName);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLogin);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#004391")
                ,android.graphics.PorterDuff.Mode.MULTIPLY); //set the progressbar color

        //execute the DoLoginTask AsyncTask when the user clicks on the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLoginTask doLoginTask = new DoLoginTask();
                doLoginTask.execute("");

            }
        });


    }

    //AsyncTask that authenticates the user
    private class DoLoginTask extends AsyncTask<String, String, String> {
        String message = "";
        Boolean isSuccess = false;

        String userid = editTextUserName.getText().toString(); //get the user id
        String password = editTextPassword.getText().toString(); //get the password(not checked in the current version)


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //set the progressbar to visible
        }

        @Override
        protected void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE); //set the progressbar to gone
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show(); //let the user know if the login was succesful or not

            if (isSuccess) { //if the login was successful open the main menu
                Intent i = new Intent(MainActivity.this, MainMenuActivity.class);
                startActivity(i);
                finish();
            }

        }

        @Override
        protected String doInBackground(String... params) {
                try {
                    Connection con = connectionClass.CONN();
                    if (con == null) { //check if the application can connect with the database
                        message = "Verbinding met de database server mislukt"; //if the connection fails end the task
                    } else { //if the connection is succesful retrieve user data from the database
                        String query = "select * from CRSADMIN.TBM where ID='" + userid + "'"; //query that selects the user
                        Statement statement = con.createStatement(); //create a new statement
                        ResultSet rs = statement.executeQuery(query); //create a new resultset filled with the results of the query

                        if (rs.next()) { //if the resultset contains data create a new user object containing the retrieved data
                            message = "Login succesvol";
                            isSuccess = true;
                            Model.getInstance().setMedewerker(new Medewerker(rs.getString("ID"),
                                    rs.getString("LONG_NM")));
                        } else { //if the resultset contains no data end the task
                            message = "Ongeldige login gegevens";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    message = "Exceptions";
                }
            return message;
        }
    }
}








