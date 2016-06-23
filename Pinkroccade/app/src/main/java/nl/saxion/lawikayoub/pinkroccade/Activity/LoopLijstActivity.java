package nl.saxion.lawikayoub.pinkroccade.Activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import nl.saxion.lawikayoub.pinkroccade.Model.ConnectionClass;
import nl.saxion.lawikayoub.pinkroccade.Model.LoopLijstItem;
import nl.saxion.lawikayoub.pinkroccade.View.LoopLijstAdapter;
import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.Model.Oe;
import nl.saxion.lawikayoub.pinkroccade.Model.Pakket;
import nl.saxion.lawikayoub.pinkroccade.R;

/**
 * Created by Lawik Ayoub on 17-Jun-16.
 * IMPORTANT NOTE: on the reportserver a looplijst is generated for the whole oe,
 * not for a specific user. In order to generate a looplijst for the current user only,
 * disable comment lines 309 and 366
 *
 * 309 line for future reference: "AND medewerker.id in('"+Model.getInstance().getMedewerker().getId()+"')"+
 * 366 line for future reference: "medewerker.id in('"+Model.getInstance().getMedewerker().getId()+"') AND"+
 */
public class LoopLijstActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    Spinner oeSpinner;
    Spinner pakketSpinner;
    ArrayAdapter oeSpinnerAdapter;
    ArrayAdapter pakketSpinnerAdapter;
    Calendar calendar;
    ListView loopLijstList;
    LoopLijstAdapter loopLijstAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looplijst);
        connectionClass = Model.getInstance().getConnectionClass(); //get the connectionclass

        //get all the views
        oeSpinner = (Spinner) findViewById(R.id.spinnerLooplijstOE);
        pakketSpinner = (Spinner) findViewById(R.id.spinerLooplijstPakket);
        loopLijstList = (ListView) findViewById(R.id.loopLijstListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLooplijst);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#004391"),
                android.graphics.PorterDuff.Mode.MULTIPLY); // set progressbar color

        Model.getInstance().getLoopLijst().clear(); //clear the looplijst arraylist

        //create and set the looplijst adapater
        loopLijstAdapter = new LoopLijstAdapter(this, -1, Model.getInstance().getLoopLijst());
        loopLijstList.setAdapter(loopLijstAdapter);

        //get all the available oe's for the current user using the GetOETask AsyncTask
        GetOETask getOE = new GetOETask();
        getOE.execute();

        //get the current date
        calendar = Calendar.getInstance();

        //execute the GetPakkettenTask AsyncTask when the user clicks on an OE
        oeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loopLijstList.setVisibility(View.GONE);
                GetPakkettenTask getPakketen = new GetPakkettenTask();
                getPakketen.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // execute the GetLoopLijstTask AsyncTask when the user clicks on a pakket
        pakketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetLoopLijstTask getLoopLijstTask = new GetLoopLijstTask();
                getLoopLijstTask.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    //AsyncTask class that retrieves the available oe's for the current user.
    private class GetOETask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess = false;
        String message = "";

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //make the progressbar visibile
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection connection = connectionClass.CONN();

                if (connection == null) { //check if the application can connect with the database
                    isSuccess = false; //if the connection fails end the task
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
            if (isSuccess) { //if everything went right create or update the oeSpinnerAdapter
                if(oeSpinnerAdapter==null) { // create a new oeSpinnerAdapter
                    oeSpinnerAdapter = new ArrayAdapter(LoopLijstActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, Model.getInstance().getMedewerker().getIEHS());
                    oeSpinner.setAdapter(oeSpinnerAdapter);
                }else { //update the oeSpinnerAdapter
                    oeSpinnerAdapter.notifyDataSetChanged();
                }

            } else { // if the connection failed let the user know and end the current activity
                Toast.makeText(LoopLijstActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //AsyncTask class that retrieves the pakketen for an oe
    private class GetPakkettenTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess = false;
        String message = "";
        String oe = oeSpinner.getSelectedItem().toString(); // get the current selected oe string
        Oe currentOe = Model.getInstance().getMedewerker().getCurrentOe(oe); // get the current selected oe object

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //make the progressbar visibile
        }

        @Override
        protected Void doInBackground(Void... params) {
            Connection connection = connectionClass.CONN();
            try {
                if (connection == null) { //check if the application can connect to the database
                    isSuccess = false; //if the connection fails end the task
                    message = "Verbinding met de database server mislukt, selecteer een oe om het " +
                            "opnieuw te proberen";

                } else { //if there is a successful connection retrieve the pakketten
                    //query that will select the pakketten from the database
                    String pakketQuery = "SELECT DISTINCT ID AS Pakket_id, DESCR AS " +
                            "Pakket_omschrijving, ID + COALESCE (' - ' + DESCR, '') AS " +
                            "Pakket_id_omschrijving FROM     CRSADMIN.TBRPR_BLOCK AS pakket WHERE" +
                            "  " +
                            "(I_EH IN ('" + oe + "')) ";

                    Statement statement = connection.createStatement(); //create a new statement
                    ResultSet rs = statement.executeQuery(pakketQuery); //create a new resultset filled with the results of the query
                    currentOe.getPakketten().clear(); //clear the arraylist with pakketten to avoid duplicates
                    while (rs.next()) { //add the pakketten to the arraylist
                        String pakket_id = rs.getString("Pakket_id");
                        currentOe.getPakketten().add(new Pakket(pakket_id));
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
            progressBar.setVisibility(View.GONE); //set the progressbar to gone
            if (isSuccess) { // create a new pakkerSpinnerAdapter
                pakketSpinnerAdapter = new ArrayAdapter(LoopLijstActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, currentOe.myPakketCodes());
                pakketSpinner.setAdapter(pakketSpinnerAdapter);
            } else { //if the connection failed let the user know and set the oe back to the first one in the list
                Toast.makeText(LoopLijstActivity.this, message, Toast.LENGTH_SHORT).show();
                oeSpinner.setSelection(0);


            }
        }
    }

    //AsyncTask that retrieves a looplijst based on the current date, the selected oe and the selected pakket
    private class GetLoopLijstTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccess = false;
        String oe = oeSpinner.getSelectedItem().toString(); //get the selected oe
        String pakket = pakketSpinner.getSelectedItem().toString(); //get the selected pakket
        String z = "";

        String date = "" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                + "-" + calendar.get(Calendar.DATE); //get the current date

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE); //set the progressbar to visibile
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connection connection = connectionClass.CONN();
                if (connection == null) { //check if the application can connect to the database
                    isSuccess = false;   // if the connection fails end the task
                    z = "Verbinding met de database server mislukt, selecteer een pakket om het "
                            + "opnieuw te proberen";
                } else { //if there is a successful connection retrieve the looplijst from the database

                    //query that select the looplijst(excluding reistijd)
                    String loopLijstQuery = "SELECT 'Indirect'             AS Soort, 'N' AS " +
                            "Legeregel_JN, 'N' AS Directeuren_JN, 'N' AS Indirecteuren_JN, 'N' AS" +
                            " " +
                            "Reistijd_JN, 'N' AS Opmerking_JN, 'N' AS Clientadres_JN, pakket" +
                            ".I_RPR_BLOCK AS Pakket_key, pakket.STRT AS Pakket_startdatum, pakket" +
                            ".I_EH AS OE_key, oe.CD AS OE_code, oe.OMSCH AS OE_omschrijving, " +
                            "pakket" +
                            ".ID AS Pakket_id, pakket.DESCR AS Pakket_omschrijving, pakket.I_M AS" +
                            " " +
                            "Medewerker_key, medewerker.ID AS Medewerker_id, medewerker.LONG_NM " +
                            "AS " +
                            "Medewerker_aanspreeknaam, pakket.STRT_MIN AS Pakket_starttijd, " +
                            "pakket" +
                            ".END_MAX AS Pakket_eindtijd, registratie.I_M_ST AS Registratie_key, " +
                            "registratie.STRT_FIXED AS Starttijd_fixed, registratie.STRT AS " +
                            "Startdatumtijd, registratie.TYDSDUUR AS Tijdsduur, 0 AS " +
                            "Directe_tijd, " +
                            "registratie.TYDSDUUR AS Indirecte_tijd, 0 AS Reis_tijd, CASE WHEN 1 " +
                            "= 0 " +
                            "THEN '' END AS Client_key, CASE WHEN 1 = 0 THEN '' END AS " +
                            "Client_geslacht, CASE WHEN 1 = 0 THEN Getdate () END AS " +
                            "Client_geboortedatum, CASE WHEN 1 = 0 THEN '' END AS " +
                            "Client_aanspreeknaam, CASE WHEN 1 = 0 THEN 0 END AS Client_nummer, " +
                            "CASE " +
                            "WHEN 1 = 0 THEN '' END AS Client_id, CASE WHEN 1 = 0 THEN '' END AS " +
                            "Client_BSN, CASE WHEN 1 = 0 THEN '' END AS Client_telefoon, CASE " +
                            "WHEN 1 " +
                            "= 0 THEN '' END AS Client_adres, CASE WHEN 1 = 0 THEN '' END AS " +
                            "Client_postcode, CASE WHEN 1 = 0 THEN '' END AS Client_woonplaats, " +
                            "CASE " +
                            "WHEN 1 = 0 THEN '' END AS Client_sleutelprotocol, registratie.MEMO " +
                            "AS " +
                            "MEMO, registratie.I_PR AS Product_key, product.CD AS Product_cd, " +
                            "product" +
                            ".OMSCH AS Product_omschrijving, product.SY_DATA AS " +
                            "Product_systeemwaarde" +
                            " FROM crsadmin. TBRPR_BLOCK pakket JOIN crsadmin.TBM_ST2 registratie" +
                            " ON " +
                            "pakket. I_RPR_BLOCK = registratie.I_RPR_BLOCK JOIN crsadmin.TBM_ATP " +
                            "product ON registratie. I_M_ATP = product.I_M_ATP LEFT JOIN crsadmin" +
                            ".TBEH oe ON pakket.I_EH = oe.I_EH LEFT JOIN crsadmin.TBM medewerker " +
                            "ON " +
                            "pakket.I_M = medewerker.I_M WHERE pakket. I_EH in " +
                            "('" + oe + "') AND pakket.STRT >= '" + date + "' AND " +
                            "pakket. STRT<dateadd (d, 1, '" + date + "') AND 'indirect' in" +
                            "('indirect') " +
                            "AND pakket.ID in ('" + pakket + "')" +
                            //disable the comment line below to generate looplijst for the current user only
                            //"AND medewerker.id in('"+Model.getInstance().getMedewerker().getId()+"')"+
                            "UNION ALL SELECT 'Direct' AS " +
                            "Soort, " +
                            "'N' AS " +
                            "Legeregel_JN, 'N' AS Directuren_JN, 'N' AS Indirecturen_JN, 'N' AS " +
                            "Reistijd_JN, 'N' AS Opmerking_JN, 'N' AS Clientadres_JN, pakket" +
                            ".I_RPR_BLOCK AS Pakket_key, pakket.STRT AS Pakket_startdatum, pakket" +
                            ".I_EH AS OE_key, oe.CD AS OE_code, oe.OMSCH AS OE_omschrijving, " +
                            "pakket" +
                            ".ID AS Pakket_id, pakket.DESCR AS Pakket_omschrijving, pakket.I_M AS" +
                            " " +
                            "Medewerker_key, medewerker.ID AS Medewerker_id, medewerker.LONG_NM " +
                            "AS " +
                            "Medewerker_aanspreeknaam, pakket.STRT_MIN AS Pakket_starttijd, " +
                            "pakket" +
                            ".END_MAX AS Pakket_eindtijd, registratie.I_RPR AS Registratie_key, " +
                            "registratie.STRT_FIXED AS Starttijd_fixed, registratie.STRT AS " +
                            "Startdatumtijd, registratie.TYDSDUUR AS Tijdsduur, registratie" +
                            ".TYDSDUUR " +
                            "AS Directe_tijd, 0 AS Indirecte_tijd, 0 AS Reis_tijd, registratie" +
                            ".I_C AS" +
                            " Client_key, client.GESLACHT AS Client_geslacht, client.GEB_DAT AS " +
                            "Client_geboortedatum, client.LONG_NM AS Client_aanspreeknaam, client" +
                            ".NR " +
                            "AS Client_nummer, client.ID AS Client_id, client.BSN AS Client_BSN, " +
                            "client.TEL1 AS Client_telefoon, COALESCE(Rtrim(Ltrim(clientadres" +
                            ".STRAAT)" +
                            "), '') + COALESCE(' ' + Rtrim(Ltrim (clientadres.HNR)), '') + " +
                            "COALESCE" +
                            "(Rtrim(Ltrim(clientadres.HNR_TOE)), '') + COALESCE(Rtrim(Ltrim" +
                            "(clientadres.HNR_SPC)), '') AS Client_adres, clientadres.PC AS " +
                            "Client_postcode, clientadres.WOONPL AS Client_woonplaats, CASE WHEN " +
                            "huishouden.KEY_PROC_IN = 'T' THEN 'J' ELSE 'N' END AS " +
                            "Client_sleutelprotocol, registratie.MEMO AS MEMO, registratie.I_PR " +
                            "AS " +
                            "Product_key, product.CD AS Product_cd, product.OMSCH AS " +
                            "Product_omschrijving, product.SY_DATA AS Product_systeemwaarde FROM " +
                            "crsadmin. TBRPR_BLOCK pakket JOIN crsadmin.TBRPR registratie ON " +
                            "pakket. " +
                            "I_RPR_BLOCK = registratie.I_RPR_BLOCK JOIN crsadmin.TBC client ON " +
                            "registratie. I_C = client.I_C JOIN crsadmin.TBPR product ON " +
                            "registratie" +
                            ".I_PR = product.I_PR LEFT JOIN crsadmin.TBEH oe ON pakket.I_EH = oe" +
                            ".I_EH" +
                            " LEFT JOIN crsadmin.TBM medewerker ON pakket.I_M = medewerker.I_M " +
                            "LEFT " +
                            "JOIN crsadmin.TBC_AD clientadres ON registratie.I_C = clientadres" +
                            ".I_C " +
                            "AND registratie. STRT >= clientadres.STRT AND(registratie.STRT < " +
                            "Dateadd" +
                            "(d, 1, clientadres.EIND) OR clientadres.EIND IS NULL) LEFT JOIN " +
                            "crsadmin" +
                            ".TBHHOUD huishouden ON clientadres.I_HHOUD = huishouden.I_HHOUD " +
                            "WHERE " +
                            "pakket.I_EH in ('" + oe + "') AND pakket.STRT >= " +
                            "'" + date + "' AND pakket. STRT<dateadd (d, 1, '" + date + "') AND " +
                            //disable the comment line below to generate looplijst for the current user only
                            //"medewerker.id in('"+Model.getInstance().getMedewerker().getId()+"') AND"+
                            "'direct1'" +
                            " in('direct1') AND pakket.ID in ('" + pakket + "')";

                    Statement statement = connection.createStatement(); //create a new statement
                    ResultSet rs = statement.executeQuery(loopLijstQuery); //create a new resultset filled with the results of the query
                    Model.getInstance().getLoopLijst().clear(); //clear the looplijst arraylist to avoid duplicates
                    while (rs.next()) { //create the looplijst items and add them to the looplijst arraylist
                        LoopLijstItem loopLijstItem = new LoopLijstItem(rs.getString
                                ("Startdatumtijd").substring(11, 16), rs.getInt("Tijdsduur"), rs.getString(("Client_aanspreeknaam")), rs.getString
                                ("Client_adres"), rs.getString("Client_postcode") + " " + rs.getString("Client_woonplaats"), rs.getString("Client_telefoon"),
                                rs.getString("Product_omschrijving"));

                        Model.getInstance().getLoopLijst().add(loopLijstItem);
                    }
                    isSuccess = true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE); //set the progressbar to gone
            if (isSuccess) {
                if (loopLijstList.getVisibility() != View.VISIBLE) {
                    loopLijstList.setVisibility(View.VISIBLE); // make the looplijst listview visible
                }
                loopLijstAdapter.notifyDataSetChanged(); //update loopLijstAdapter
                if (Model.getInstance().getLoopLijst().size() == 0) { // if there was no looplijst found let the user know
                    Toast.makeText(LoopLijstActivity.this, "Geen looplijst beschikbaar voor " +
                            "huidige datum en pakket combinatie.", Toast.LENGTH_SHORT).show();
                }
            } else { //if the connection failed let the user know and set the pakket back to the first one in the list
                Toast.makeText(LoopLijstActivity.this, z, Toast.LENGTH_SHORT).show();
                pakketSpinner.setSelection(0);
            }
        }

    }

}


