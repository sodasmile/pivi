import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {

    class FolderHolder {
        File folder;
    }

    @Override
    public void start(Stage stage) {
        File selectedFolder = chooseFolder(stage);
        final FolderHolder folderHolder = new FolderHolder();
        folderHolder.folder = selectedFolder;

        stage.setTitle("Title");
        BorderPane root = new BorderPane();

        BorderPane gridpane = new BorderPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setBackground(new Background(new BackgroundFill(Paint.valueOf("BLACK"), CornerRadii.EMPTY, null)));

        final ImageView iv2 = new ImageView();
        final Image image2 = new Image("http://mediafiles.allaboutsymbian.com/808/zeiss/g.jpg", true);
        iv2.setImage(image2);
        iv2.setFitWidth(1680);
        iv2.setPreserveRatio(true);

        HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(iv2);

        gridpane.setCenter(iv2);

        root.setCenter(gridpane);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                File pictureDir = folderHolder.folder;
                File[] imageFiles = pictureDir.listFiles((dir, name) -> {
                    return name.toLowerCase().endsWith(".jpg");
                });
                if (imageFiles.length != 0) {
                    int imgNumber = new Random(System.currentTimeMillis()).nextInt(imageFiles.length);
                    File imageFile = imageFiles[imgNumber];
                    Platform.runLater(() -> {
                        try {
                            iv2.setImage(new Image(new FileInputStream(imageFile)));
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }).start();

        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed((x) -> {
            KeyCode code = x.getCode();
            switch (code) {
            case F:
                stage.setFullScreen(!stage.isFullScreen());
                break;
            case D:
                folderHolder.folder = chooseFolder(stage);
            }
        });
        //stage.setFullScreen(true);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                iv2.setFitWidth(newSceneWidth.doubleValue() - 10);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    private File chooseFolder(Stage stage) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Velg katalogen bildene ligger i");
        File selectedFolder = fileChooser.showDialog(stage);
        if (selectedFolder == null) {
            String userHome = System.getProperty("user.home");
            selectedFolder = new File(userHome + File.separatorChar + "Pictures" + File.separatorChar + "Photo Booth");
        }
        return selectedFolder;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
