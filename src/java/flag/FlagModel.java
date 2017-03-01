package flag;

/*
 * @author Yat Rehani
 * 
 * This file is the Model component of the MVC, and it models the business
 * logic for the web application.  In this case, the business logic involves
 * making a request to cia.gov and then screen scraping the HTML that is
 * returned in order to fabricate a flag image URL and description.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FlagModel {

    private String flagCode; // The search string of the desired country
    private String flagImageURL; // The URL of the flag image
    private String flagDescription; // flag Description
    private String flagCountryName; // flag Country Name

    /**
     * Arguments.
     *
     * @param searchTag The tag of the flag to be searched for.
     */
    public void doFlagSearch(String searchTag) 
            throws UnsupportedEncodingException  {
        flagCode = searchTag;
        /*
         * URL encode the searchTag, e.g. to encode spaces as %20
         *
         * There is no reason that UTF-8 would be unsupported.  It is the
         * standard encoding today.  So if it is not supported, we have
         * big problems, so don't catch the exception.
         */
        searchTag = URLEncoder.encode(searchTag, "UTF-8");
 
        String response = "";

        // Create URLs for the pages to be screen scraped
        flagImageURL =
                "https://www.cia.gov/library/publications/the-world-factbook/graphics/flags/large/"
                + searchTag
                +"-lgflag.gif";
        
        String flagDescriptionURL =
                "https://www.cia.gov/library/publications/the-world-factbook/geos/"
                + searchTag
                +".html";
        
        response = fetch(flagDescriptionURL);

        //System.out.println(response);
        
        /*Code to scrape description*/
        
        int cutLeft = response.indexOf("\"flag_description_text\">");
        // If not found, then no such photo is available.
        if (cutLeft == -1) {
            flagDescription = "The description is currently unavailable";
        } else {
        // Skip past this string. 
        cutLeft += "\"flag_description_text\">".length();

        // Look for the close parenthesis
        int cutRight = response.indexOf("</span>", cutLeft);

        // Now snip out the part from positions cutLeft to cutRight
        flagDescription = response.substring(cutLeft, cutRight);
        }
        //System.out.println("flagDescription= " + flagDescription);
        
        /*Code to scrape description*/
        
        int cutLeft1 = response.indexOf("../geos/"+searchTag+".html\">");
        // If not found, then no country is available.
        if (cutLeft1 == -1) {
            flagCountryName = searchTag;
        } else {
        // Skip past this string. 
        cutLeft1 += ("../geos/"+searchTag+".html\">").length();

        // Look for the close parenthesis
        int cutRight1 = response.indexOf("<", cutLeft1);

        // Now snip out the part from positions cutLeft to cutRight
        flagCountryName = response.substring(cutLeft1, cutRight1);
        }
    }
 
    public String getFlagImageUrl() {
        return (flagImageURL);
    }
    
    public String getFlagDescription() {
        return (flagDescription);
    }
    
    public String getFlagCountryName() {
        return (flagCountryName);
    }

    /*
     * Make an HTTP request to a given URL
     * 
     * @param urlString The URL of the request
     * @return A string of the response from the HTTP GET.  This is identical
     * to what would be returned from using curl on the command line.
     */
    private String fetch(String urlString) {
        String response = "";
        try {
            URL url = new URL(urlString);
            /*
             * Create an HttpURLConnection.  This is useful for setting headers
             * and for getting the path of the resource that is returned (which 
             * may be different than the URL above if redirected).
             * HttpsURLConnection (with an "s") can be used if required by the site.
             */
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            // Read each line of "in" until done, adding each to "response"
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
            // Do something reasonable.  This is left for students to do.
        }
        return response;
    }
}
