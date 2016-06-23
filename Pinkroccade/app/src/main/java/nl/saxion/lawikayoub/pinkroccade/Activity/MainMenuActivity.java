package nl.saxion.lawikayoub.pinkroccade.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.R;

/**
 * Created by Lawik Ayoub on 09-Jun-16.
 */
public class MainMenuActivity extends AppCompatActivity {
    TextView nameText;
    Button rapportagesButton;
    Button loopLijstButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //get all the views
        nameText = (TextView) findViewById(R.id.mainMenuNameText);
        rapportagesButton = (Button) findViewById(R.id.mainMenuRapportageButton);
        loopLijstButton = (Button) findViewById(R.id.mainMenuLoopLijstButton);

        nameText.setText(Model.getInstance().getMedewerker().getLONG_NAME()); //set the nameText to the name of the current user
        rapportagesButton.setOnClickListener(new RapportageButtonListener()); //set the OnclickListener for the rapportagesButton
        loopLijstButton.setOnClickListener(new LoopLijstButtonListener());  //set the OnclickListener for the LoopLijstButton


    }

    //ButtonListener that launches the RapportageInputActivity
    private class RapportageButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainMenuActivity.this,RapportageInputActivity.class);
            startActivity(intent);

        }
    }

    //ButtonListener that launches the LoopLijstActivity
    private class LoopLijstButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainMenuActivity.this,LoopLijstActivity.class);
            startActivity(intent);
        }
    }
}
