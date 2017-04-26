package dvt.polybeatmaker.controller;

import dvt.jeu.simple.ControleDevint;
import dvt.polybeatmaker.model.ConfigurationType;
import dvt.polybeatmaker.model.Instrument;
import dvt.polybeatmaker.model.PolybeatModel;
import dvt.polybeatmaker.model.Sequence;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static dvt.devint.ConstantesDevint.SYNTHESE_MAXIMALE;

/**
 * Controller for the main screen.
 */
public class MainController extends ControleDevint {

    public static String DEFAULT_BORDER = "#FFFFFF;";
    public static String CSS_SELECTED = "selectedbutton";
    public static String CSS_UNSELECTED = "unselectedbutton";

    private static String[] BORDER_COLOR = new String[]{"#FFFFFF;", "#e6e6ff;", "#4d004d;", "#ffe6ff;", "#4d3319;", "#f2e6d9;"};

    private int currentCSS = 0;
    private PolybeatModel model;
    private List<InstrumentController> childrenControllers;
    private AnchorPane root;
    private int buttonIndex = 1;
    private Button[] buttons;
    private List<Consumer<Void>> functions;

    @FXML private ProgressBar progressBar;
    @FXML private HBox mainBox;
    @FXML private Button load;
    @FXML private Button save;
    @FXML private Button quit;

    public void setPolybeatModel(PolybeatModel model) {
        this.model = model;
    }

    @Override
    protected void init() {
        try {
            childrenControllers = new ArrayList<>();
            for (Instrument instrument : Instrument.values()) {
                FXMLLoader loader = new FXMLLoader(new File("../ressources/fxml/instrument.fxml").toURI().toURL());
                mainBox.getChildren().add(loader.load());
                InstrumentController controller = loader.getController();
                childrenControllers.add(controller);
                controller.setInstrument(instrument);
                controller.setModel(model);
                model.getScheduler().setController(this);
                controller.init();
                buttons = new Button[]{quit, load, save};
                functions = Arrays.asList((x) -> quit(), (x) -> load(), (x) -> save());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void reset() {

    }

    public void updateProgressBar(int duration) {
        progressBar.setProgress(0);
        Timeline timeline = new Timeline();
        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), 1);
        KeyFrame keyFrame = new KeyFrame(new Duration(duration), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    @Override
    public void mapTouchToActions() {
        scene.mapKeyPressedToConsumer(KeyCode.F3, (x) -> swapBackgroundColor());
        scene.mapKeyPressedToConsumer(KeyCode.LEFT, (x) -> left());
        scene.mapKeyPressedToConsumer(KeyCode.RIGHT, (x) -> right());
        scene.mapKeyPressedToConsumer(KeyCode.ENTER, (x) -> confirm());
    }

    private void swapBackgroundColor() {
        if (currentCSS == 5) {
            currentCSS = 0;
        } else {
            currentCSS++;
        }
        InstrumentController.setHighlightColor(BORDER_COLOR[currentCSS]);
        for (InstrumentController controller : childrenControllers) {
            controller.updateHighlight();
        }
    }

    private void left() {
        if (buttonIndex == 0) {
            buttonIndex = 2;
        } else {
            buttonIndex--;
        }
        playCurrent();
        updateButtonStyles();
    }

    private void right() {
        if (buttonIndex == 2) {
            buttonIndex = 0;
        } else {
            buttonIndex++;
        }
        playCurrent();
        updateButtonStyles();
    }

    private void playCurrent() {
        scene.getSIVox().stop();
        scene.getSIVox().playText(buttons[buttonIndex].getText(), SYNTHESE_MAXIMALE);
    }

    private void updateButtonStyles() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].getStyleClass().clear();
            if (i == buttonIndex) {
                buttons[i].getStyleClass().add(CSS_SELECTED);
            } else {
                buttons[i].getStyleClass().add(CSS_UNSELECTED);
            }
        }
    }

    private void confirm() {
        functions.get(buttonIndex).accept(null);
    }

    private void quit() {
        Stage stage = (Stage) load.getScene().getWindow();
        stage.close();
    }

    private void load() {

    }

    private void save() {
        List<Instrument> currentInstruments = new ArrayList<>();
        List<Integer> currentActive = new ArrayList<>();
        for (InstrumentController controller : childrenControllers) {
            currentInstruments.add(controller.getInstrument());
            currentActive.add(controller.getActive());
        }
        Sequence sequence = new Sequence(currentInstruments, currentActive);
        try {
            FXMLLoader loader = new FXMLLoader(new File("../ressources/fxml/nameChooser.fxml").toURI().toURL());
            Stage stage = new Stage();
            stage.setScene(loader.load());
            NameChooserController controller = loader.getController();
            controller.load(sequence.toJSON(), ConfigurationType.SEQUENCE, "Entrez un nom pour la séquence actuelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
