package nl.saxion.lawikayoub.pinkroccade.Model;

import java.util.ArrayList;

/**
 * Created by Lawik Ayoub on 01-Jun-16.
 */
public class Medewerker {
    private String id;
    private String LONG_NAME;
    private ArrayList<Oe> oes;


    /**
     * constructor for the Medewerker class
     * @param id the medewerker id
     * @param LONG_NAME the name of the medewerker
     */
    public Medewerker(String id, String LONG_NAME){
        this.id = id;
        this.LONG_NAME = LONG_NAME;
        oes= new ArrayList<>();
    }

    /**
     * method that gets the id of the medewerker
     * @return the id of the medewerker
     */
    public String getId() {
        return id;
    }

    /**
     * method that gets the name of the medewerker
     * @return
     */
    public String getLONG_NAME() {
        return LONG_NAME;
    }

    /**
     * method that gets all the I_EH codes from the available oe's of the medewerker
     * @return ArrayList of I_EH codes
     */
    public ArrayList<String> getIEHS() {
        ArrayList<String> myI_EHS = new ArrayList<>();
        for (Oe oe:oes ){
            myI_EHS.add(oe.getI_EH());
        }
        return myI_EHS;
    }

    /**
     * method that gets all the available oe's of the medewerker
     * @return ArrayList of oe's
     */
    public ArrayList<Oe> getOes() {
        return oes;
    }

    /**
     * method that gets an oe object of which the I_EH code equals the parameter
     * @param code the I_EH code
     * @return the oe object of which the I_EH code equals the parameter
     */
    public Oe getCurrentOe(String code){
        for(Oe oe: oes){
            if (code.equals(oe.getI_EH())){
                return oe;
            }
        }
        return null;
    }
}
