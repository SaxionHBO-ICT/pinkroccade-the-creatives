package nl.saxion.lawikayoub.pinkroccade.Model;

/**
 * Created by Lawik Ayoub on 17-Jun-16.
 */
public class LoopLijstItem {
    private String startTijd;
    private int duur;
    private String client;
    private String straat;
    private String postcode;
    private String tel;
    private String activiteit;

    /**
     * constructor for the LoopLijstItem class
     * @param startTijd the starting time
     * @param duur the duration of the activity
     * @param client the name of the client
     * @param straat the street address of the client including the number
     * @param postcode the postal code of the client including the city name
     * @param tel the telephone number of the client
     * @param activiteit the activity name
     */
    public LoopLijstItem(String startTijd, int duur, String client, String straat, String
            postcode, String tel, String activiteit) {
        this.startTijd = startTijd;
        this.duur = duur;
        this.client = client;
        this.straat = straat;
        this.postcode = postcode;
        this.tel = tel;
        this.activiteit = activiteit;
    }

    /**
     * method that gets the starting time
     * @return the starting time
     */
    public String getStartTijd() {
        return startTijd;
    }

    /**
     * method that gets the duration
     * @return the duration
     */
    public int getDuur() {
        return duur;
    }

    /**
     * method that gets the client name
     * @return the client name
     */
    public String getClient() {
        return client;
    }

    /**
     * method that gets the street address and number
     * @return the street address and number
     */
    public String getStraat() {
        return straat;
    }

    /**
     * method that gets the postal code and city
     * @return the postal code and city
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * method that gets the telephone number
     * @return the telephone number
     */
    public String getTel() {
        return tel;
    }

    /**
     * method that gets the activity name
     * @return the activity name
     */
    public String getActiviteit() {
        return activiteit;
    }
}
