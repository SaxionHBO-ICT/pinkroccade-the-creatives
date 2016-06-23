package nl.saxion.lawikayoub.pinkroccade.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.saxion.lawikayoub.pinkroccade.Model.LoopLijstItem;
import nl.saxion.lawikayoub.pinkroccade.Model.Model;
import nl.saxion.lawikayoub.pinkroccade.R;

/**
 * Created by Lawik Ayoub on 17-Jun-16.
 */
public class LoopLijstAdapter extends ArrayAdapter<LoopLijstItem> {
    public LoopLijstAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.looplijst_item,parent,false);
        }

        //get the current LoopLijstItem
        LoopLijstItem loopLijstItem = Model.getInstance().getCurrentLoopLijst(position);

        //get all the views
        TextView startTijd = (TextView) convertView.findViewById(R.id.startTijd);
        TextView duur = (TextView) convertView.findViewById(R.id.duur);
        TextView client = (TextView) convertView.findViewById(R.id.client);
        TextView straat = (TextView) convertView.findViewById(R.id.straat);
        TextView postcode = (TextView) convertView.findViewById(R.id.postcode);
        TextView tel = (TextView) convertView.findViewById(R.id.tel);
        TextView activiteit = (TextView) convertView.findViewById(R.id.activiteit);

        startTijd.setText(loopLijstItem.getStartTijd()); //set the starting time
        duur.setText(""+ loopLijstItem.getDuur()); //set the duration
        client.setText(loopLijstItem.getClient()); //set the client name
        straat.setText(loopLijstItem.getStraat()); //set the street address
        postcode.setText(loopLijstItem.getPostcode()); //set the postal code and city
        tel.setText("tel: " + loopLijstItem.getTel()); //set the telephone number
        activiteit.setText(loopLijstItem.getActiviteit()); //set the activity name

        return convertView;
    }
}
