package nl.saxion.lawikayoub.pinkroccade.Model;

/**
 * Created by Lawik Ayoub on 18-Jun-16.
 */
public class Pakket {
    private String pakket_code;

    /**
     * constructor for the Pakket class
     * @param pakket_code the pakket code
     */
    public Pakket(String pakket_code) {
        this.pakket_code = pakket_code;
    }

    /**
     * method that gets the pakket code
     * @return the pakket code
     */
    public String getPakket_code() {
        return pakket_code;
    }
}
