package nl.saxion.lawikayoub.pinkroccade.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import nl.saxion.lawikayoub.pinkroccade.View.ArcProgress;
import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.R;

/**
 * Created by Lawik Ayoub on 10-Jun-16.
 */
public class ShowRapportageActivity extends AppCompatActivity {
    TextView periodeText;
    ArcProgress productiviteitProgress;
    ArcProgress gezichtenProgress;
    TextView medewerkerNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapportage);

        //get all the views
        periodeText = (TextView) findViewById(R.id.rapportagePeriode);
        productiviteitProgress = (ArcProgress) findViewById(R.id.productiviteitProgress);
        gezichtenProgress = (ArcProgress) findViewById(R.id.gezichtenProgress);
        medewerkerNaam = (TextView) findViewById(R.id.rapportageMedewerkerNaam);

        periodeText.setText(Model.getInstance().getRapportage().getPeriode()); //set the periodeText
        productiviteitProgress.setProgress(Model.getInstance().getRapportage().getProductiviteit()); //set the productiviteit progress
        gezichtenProgress.setProgress(Model.getInstance().getRapportage().getGezichtenPerClient()); //set the gezichten per client progress
        medewerkerNaam.setText(Model.getInstance().getMedewerker().getLONG_NAME()); //set the medewerkerNaam to the name of the user


    }
}
