package Agent;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;

public class AgentGUI extends Application{
    private Agent agent;
    private BorderPane chooseAuction;
    private BorderPane placeBid;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Agent");

        Image mainStreet = new Image("file:Resources\\CartoonMainStreet.jpg");
        BackgroundFill imageSet = new BackgroundFill(new
                ImagePattern(mainStreet), CornerRadii.EMPTY, Insets.EMPTY);
        chooseAuction = new BorderPane();
        chooseAuction.setBackground(new Background(imageSet));

        Image frame = new Image("file:Resources\\GoldPictureFrame.jpg");
        BackgroundFill pictureFrame=new BackgroundFill(new ImagePattern(frame),
                CornerRadii.EMPTY, Insets.EMPTY);
        placeBid = new BorderPane();
        placeBid.setBackground(new Background(pictureFrame));

        Scene scene = new Scene(chooseAuction, 612, 403);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
