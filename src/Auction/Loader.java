/**
 * Ryan Cooper
 * rycooper
 */
package Auction;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class Loader {
    public static <T> T loadFxmlFile(String filename) {
        FXMLLoader loader = new FXMLLoader(Auction.Loader.class.getResource(filename));

        T root = null;

        try {
            root = loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
