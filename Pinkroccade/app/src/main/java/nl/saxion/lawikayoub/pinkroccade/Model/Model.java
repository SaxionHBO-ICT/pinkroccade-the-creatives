package nl.saxion.lawikayoub.pinkroccade.Model;

import java.util.ArrayList;

/**
 * Created by Lawik Ayoub on 08-Jun-16.
 */
public class Model {
    private static Model ourInstance = new Model();
    private Rapportage rapportage;
    private Medewerker medewerker;
    private ArrayList<LoopLijstItem> LoopLijst;
    private ConnectionClass connectionClass;


    /**
     * method that gets the instance of the model class
     * @return the instance of the model class
     */
    public static Model getInstance() {
        return ourInstance;
    }

    /**
     * constructor for the model class
     */
    private Model() {
        LoopLijst = new ArrayList<>();
        connectionClass = new ConnectionClass();

    }

    /**
     * method that gets the current medewerker
     * @return the current medewerker
     */
    public Medewerker getMedewerker() {
        return medewerker;
    }

    /**
     * method that sets the current medewerker
     * @param medewerker a medewerker object
     */
    public void setMedewerker(Medewerker medewerker) {
        this.medewerker = medewerker;
    }

    /**
     * method that gets the current rapportage
     * @return the current rapportage
     */
    public Rapportage getRapportage() {
        return rapportage;
    }

    /**
     * method that sets the current rapportage
     * @param rapportage a rapportage object
     */
    public void setRapportage(Rapportage rapportage) {
        this.rapportage = rapportage;
    }

    /**
     * method that gets the current looplijst
     * @return ArrayList containing LoopLijstItem objects
     */
    public ArrayList<LoopLijstItem> getLoopLijst() {
        return LoopLijst;
    }

    /**
     * method that gets a single LoopLijstItem object
     * from the ArrayList at the position specified in the parameter
     * @param position the position of the LoopLijstItem
     * @return the LoopLijstItem object at the position of the parameter
     */
    public LoopLijstItem getCurrentLoopLijst(int position){
        return LoopLijst.get(position);
    }

    /**
     * method that gets the connection class
     * @return the ConnectionClass object
     */
    public ConnectionClass getConnectionClass() {
        return connectionClass;
    }
}
