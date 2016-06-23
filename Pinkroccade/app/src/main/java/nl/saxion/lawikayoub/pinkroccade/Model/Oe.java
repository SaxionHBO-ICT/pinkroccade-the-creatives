package nl.saxion.lawikayoub.pinkroccade.Model;

import java.util.ArrayList;

/**
 * Created by Lawik Ayoub on 18-Jun-16.
 */
public class Oe {
    private String I_EH;
    ArrayList<Pakket> pakketten;

    /**
     * constructor for the Oe class
     * @param I_EH the I_EH code of the oe
     */
    public Oe(String I_EH) {
        this.I_EH = I_EH;
        pakketten = new ArrayList<>();
    }

    /**
     * method that gets the I_EH code of the oe
     * @return the I_EH code
     */
    public String getI_EH() {
        return I_EH;
    }

    /**
     * method that gets all the available pakketten of the oe
     * @return ArrayList containing Pakket objects
     */
    public ArrayList<Pakket> getPakketten() {
        return pakketten;
    }

    /**
     * method that gets the pakket codes of this oe
     * @return ArrayList of pakket codes in String format
     */
    public ArrayList<String> myPakketCodes(){
        ArrayList<String> myPakketten = new ArrayList<>();
        for(Pakket pakket: pakketten){
            myPakketten.add(pakket.getPakket_code());

        }
        return myPakketten;
    }
}
