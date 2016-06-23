package nl.saxion.lawikayoub.pinkroccade.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.saxion.lawikayoub.pinkroccade.Model.ConnectionClass;
import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.Model.Oe;
import nl.saxion.lawikayoub.pinkroccade.R;
import nl.saxion.lawikayoub.pinkroccade.Model.Rapportage;

/**
 * Created by Lawik Ayoub on 09-Jun-16.
 */
public class RapportageInputActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    EditText beginDag;
    EditText beginMaand;
    EditText beginJaar;
    EditText eindDag;
    EditText eindMaand;
    EditText eindJaar;
    Button rapportageOpvraagButton;
    Spinner spinner;
    ArrayAdapter spinnerAdapter;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_rapportage);

        connectionClass = Model.getInstance().getConnectionClass(); //get the connection class

        //get all the views
        spinner = (Spinner) findViewById(R.id.oeSpinner);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRapportage);
        beginDag = (EditText) findViewById(R.id.inputRapportageBeginDag);
        beginMaand = (EditText) findViewById(R.id.inputRapportageBeginMaand);
        beginJaar = (EditText) findViewById(R.id.inputRapportageBeginJaar);
        eindDag = (EditText) findViewById(R.id.inputRapportageEindDag);
        eindMaand = (EditText) findViewById(R.id.inputRapportageEindMaand);
        eindJaar = (EditText) findViewById(R.id.inputRapportageEindJaar);
        rapportageOpvraagButton = (Button) findViewById(R.id.inputRapportageButton);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#004391"),
                android.graphics.PorterDuff.Mode.MULTIPLY); //set the progressbar color

        //get all the available oe's for the current user using the GetOETask AsyncTask
        GetOETask getOETask = new GetOETask();
        getOETask.execute();

        //OnClickListener for the rapportageOpvraagButton
        rapportageOpvraagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the start date in a string format yyyy-MM-dd
                String beginDatum = beginJaar.getText().toString() + "-" + beginMaand.getText()
                        .toString() + "-" + beginDag.getText().toString();

                //get the end date in a string format yyyy-MM-dd
                String eindDatum = eindJaar.getText().toString() + "-" + eindMaand.getText()
                        .toString() +
                        "-" + eindDag.getText().toString();

                //first check if the both dates are valid by checking the length of the year and  using the isValidDate method
                if (beginJaar.getText().toString().length() < 4 || eindJaar.getText().length() <
                        4 || !isValidDate(beginDatum) || !isValidDate(eindDatum)) {
                    Toast.makeText(RapportageInputActivity.this, "Ongeldige datum invoer", Toast
                            .LENGTH_SHORT).show();

                    //then check if the start date isnt bigger than the end date using the compareDates method
                } else if (!compareDates(beginDatum, eindDatum)) {
                    Toast.makeText(RapportageInputActivity.this, "Begin datum is groter dan " +
                            "of " + "gelijk aan eind datum", Toast.LENGTH_SHORT).show();

                    /*if both dates are valid and the start date isn't bigger than the end date retrieve the rapportages
                    using the GetRapportageTask AsyncTask*/
                } else {
                    GetRapportageTask getRapportageTask = new GetRapportageTask();
                    getRapportageTask.execute();
                }
            }

        });


    }


    private class GetRapportageTask extends AsyncTask<Void, Void, String> {
        boolean isSucess = false;
        String message = "";

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //make the progressbar visible
        }

        //get the start date in a string format yyyy-MM-dd
        String qBeginDatum = beginJaar.getText().toString() + "-" + beginMaand.getText().toString
                () + "-" + beginDag.getText().toString();

        //get the end date in a string format yyyy-MM-dd
        String qEindDatum = eindJaar.getText().toString() + "-" + eindMaand.getText().toString()
                + "-" + eindDag.getText().toString();

        String qOe = spinner.getSelectedItem().toString(); //get the current selected oe

        //set the periode to start date t/m end date
        String periode = "Periode van " + beginDag.getText().toString() + "-" + beginMaand
                .getText().toString() + "-" + beginJaar.getText().toString() + " t/m " + eindDag
                .getText().toString() + "-" + eindMaand.getText().toString() + "-" + eindJaar
                .getText().toString();

        Integer gezichtenPerClient = 0;
        Integer productiviteit = 0;



        @Override
        protected String doInBackground(Void... params) {
            try {
                Connection con = connectionClass.CONN();
                if (con == null) { //check if the application can connect with the database
                    isSucess = false; //if the connection fails end the task
                    message = "Verbinding met de database server mislukt";
                } else { // if there is a succesful connection retrieve the rapportages from the database

                    //query that selects the gezichten per client
                    String gezichtenPerClientQuery = "SELECT I_EH, COUNT(DISTINCT client) AS " +
                            "aantal_clienten, SUM(aantal_handen) AS aantal_handen, SUM" +
                            "(aantal_handen)" +
                            " * 1. / COUNT(DISTINCT client) AS gem_aantal_handen FROM     (SELECT" +
                            " " +
                            "I_EH, I_C AS client, COUNT(DISTINCT I_M) AS aantal_handen FROM      " +
                            "CRSADMIN.TBRPR AS rpr WHERE   (I_EH = '" + qOe + "') AND (STRT " +
                            "BETWEEN " +
                            "'" + qBeginDatum + "' AND '" + qEindDatum + "'  ) GROUP BY I_EH, " +
                            "I_C) AS" +
                            " c GROUP BY I_EH";

                    //query that gets the productiviteit
                    String productiviteitQuery = "with uren as ( SELECT I_EH, i_m, tydsduur as " +
                            "direct, 0 as indirect FROM crsadmin.TBRPR AS direct WHERE i_m IS NOT" +
                            " " +
                            "NULL AND direct.STRT BETWEEN '" + qBeginDatum + "' AND '" +
                            qEindDatum +
                            "' UNION ALL SELECT I_EH, i_m, CASE WHEN roostercode.SY_DATA = " +
                            "'10200' " +
                            "THEN indirect.tydsduur ELSE 0 END as direct, CASE WHEN roostercode" +
                            ".SY_DATA = '10210' THEN indirect.tydsduur ELSE 0 END as indirect " +
                            "FROM " +
                            "crsadmin.TBM_ST2 indirect INNER JOIN crsadmin.TBM_ATP roostercode ON" +
                            " " +
                            "indirect.i_m_atp=roostercode.i_m_atp WHERE indirect.STRT BETWEEN '" +
                            qBeginDatum + "' AND '" + qEindDatum + "' AND roostercode.SY_DATA IN " +
                            "('10200', '10210')) SELECT TOP 1000 medewerker.LONG_NM as " +
                            "medewerkernaam, sum(uren.direct)*100./(sum(uren.direct)+sum(uren" +
                            ".indirect)) as productiviteit, max(totaal.prod) as OEprod, uren" +
                            ".I_EH, " +
                            "uren.i_m, count(*) FROM crsadmin.TBM medewerker LEFT JOIN uren on " +
                            "medewerker.i_m = uren.i_m LEFT JOIN (select I_EH, sum(uren.direct)" +
                            "*100./" +
                            "(sum(uren.direct)+sum(uren.indirect)) as prod from uren Where I_EH =" +
                            " '" + qOe + "' GROUP BY I_EH) totaal ON uren.I_EH = totaal.I_EH " +
                            "WHERE " +
                            "medewerker.Id = '" + Model.getInstance().getMedewerker().getId() + "'" +
                            " " +
                            "GROUP BY medewerker.LONG_NM, uren.I_EH, uren.i_m";

                    Statement statement = con.createStatement(); //create a new statement

                    //create a new resultset filled with the results of the productiviteit query
                    ResultSet rs = statement.executeQuery(productiviteitQuery);
                    if (rs.next()) { //round the productiviteit
                        productiviteit = (int) Math.round(rs.getDouble("productiviteit"));

                    }
                    //create a new resultset filled the results of the gezicht per client query
                    ResultSet rs2 = statement.executeQuery(gezichtenPerClientQuery);
                    if (rs2.next()) { //round the gezichtenPerClient
                        gezichtenPerClient = (int) Math.round(rs2.getDouble("gem_aantal_handen"));
                    }
                    isSucess = true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE); //make the progressbar gone
            if (isSucess) { //if everything went right add the new rapportage to the model class and start the ShowRapportageActivity
                Model.getInstance().setRapportage(new Rapportage(qBeginDatum, qEindDatum,
                        periode, qOe, productiviteit, gezichtenPerClient));

                Intent intent = new Intent(RapportageInputActivity.this, ShowRapportageActivity
                        .class);
                startActivity(intent);

            } else { //if the connection failed let the user know
                Toast.makeText(RapportageInputActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //AsyncTask class that retrieves the available oe's for the current user.
    private class GetOETask extends AsyncTask<Void, Void, Void> {
        String message = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //make the progressbar visible
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection connection = connectionClass.CONN();
                if (connection == null) { //check if the application can connect with the database
                    isSuccess = false; // if the connection fails end the task
                    message = "Verbinding met de database server mislukt";
                } else { // if there is a succesful connection retrieve the oe's from the database

                    // query that will select the oe's from the database
                    String getOEQuery = "SELECT DISTINCT I_EH FROM CRSADMIN.TBRPR INNER JOIN " +
                            "CRSADMIN" +
                            ".TBM ON CRSADMIN.TBM.I_M = CRSADMIN.TBRPR.I_M WHERE CRSADMIN.TBM.ID " +
                            "= '" + Model.getInstance().getMedewerker().getId() + "'";


                    Statement statement = connection.createStatement(); //create a new statement
                    ResultSet rs = statement.executeQuery(getOEQuery); // create a new resultset filled with the results of the query
                    Model.getInstance().getMedewerker().getOes().clear(); //clear the arraylist with oe's to avoid duplicates
                    while (rs.next()) { //add the oe's to the arraylist
                        String I_EH = rs.getString("I_EH");
                        Model.getInstance().getMedewerker().getOes().add(new Oe(I_EH));
                    }
                    isSuccess = true;
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE); //make the progressbar gone
            if (isSuccess) { //if everything went right create or update the spinnerAdapter
                if(spinnerAdapter==null){ // create a new spinnerAdapter
                spinnerAdapter = new ArrayAdapter(RapportageInputActivity.this, R.layout
                        .spinner_textview, Model.getInstance().getMedewerker().getIEHS());
                spinner.setAdapter(spinnerAdapter);
                }else {
                    spinnerAdapter.notifyDataSetChanged(); //update the spinnerAdapter
                }
            } else { // if the connection failed let the user know and end the current activity
                Toast.makeText(RapportageInputActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * method that checks if a date is valid
     * @param datum the date that needs to be checked
     * @return true if the date is valid, false if it isn't
     */
    private static boolean isValidDate(String datum) {
        try {
            int jaar = Integer.parseInt(datum.substring(0, datum.indexOf('-'))); //get the year of the date
            if (jaar < 1975) { //check if the year isn't smaller than 1975 to avoid low inputs like 0003
                return false;
            } else { //if the date can be parsed return true, otherwise return false
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                dateFormat.parse(datum);
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * method that checks if one date is bigger than or equal the other
     * @param beginDatum the start date
     * @param eindDatum the end date
     * @return true if the start date isn't bigger than or equal to end date, otherwise return false
     */
    private static boolean compareDates(String beginDatum, String eindDatum) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date begin;
        Date eind;
        try {
            begin = simpleDateFormat.parse(beginDatum); //parse the start date
            eind = simpleDateFormat.parse(eindDatum); //parse the end the date
            int comparison = begin.compareTo(eind); //compare the start date with the end date 0 is equal, >1 is bigger, <1 is smaller
            if (comparison <0) { // if int comparison <0 return true
                return true;
            } else { // if int comparison == 0 or >0 return false
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }


}
