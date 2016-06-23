package nl.saxion.lawikayoub.pinkroccade.Model;

/**
 * Created by Lawik Ayoub on 08-Jun-16.
 */
public class Rapportage {
    private String beginDatum;
    private String eindDatum;
    private String oe;
    private int productiviteit;
    private int gezichtenPerClient;
    private String periode;

    /**
     * Constructor for the Rapportage class
     * @param beginDatum the start date of the rapportage
     * @param eindDatum the end date of the rapportage
     * @param periode the date range of the start date and end date
     * @param oe the oe of the rapportage
     * @param productiviteit the amount of productivity the medewerker has in this rapportage
     * @param gezichtenPerClient the average amount of people that helps a client in the specified oe in this rapportage
     */
    public Rapportage(String beginDatum, String eindDatum,String periode, String oe, int productiviteit, int gezichtenPerClient) {
        this.beginDatum = beginDatum;
        this.eindDatum = eindDatum;
        this.periode = periode;
        this.oe = oe;
        this.productiviteit = productiviteit;
        this.gezichtenPerClient = gezichtenPerClient;
    }

    /**
     * method that gets the productivity
     * @return the productivity
     */
    public int getProductiviteit() {
        return productiviteit;
    }

    /**
     * method that gets the average amount of people that helps a client
     * @return the average amount of people that helps a client
     */
    public int getGezichtenPerClient() {
        return gezichtenPerClient;
    }


    /**
     * method that gets the date range of the rapportage
     * @return the date range of the rapportage
     */
    public String getPeriode() {
        return periode;
    }

}
