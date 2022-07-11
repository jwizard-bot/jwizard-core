package pl.miloszgilga;

import java.net.URI;
import java.net.URISyntaxException;


public class Utils {

    public static boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

}