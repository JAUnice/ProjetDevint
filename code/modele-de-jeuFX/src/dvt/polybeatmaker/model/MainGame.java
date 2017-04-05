package dvt.polybeatmaker.model;

import dvt.devint.SceneDevint;
import dvt.jeu.simple.ControleDevint;
import dvt.jeu.simple.JeuDevint;
import dvt.jeu.simple.ModeleDevint;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;

/**
 * Jeu devint principal.
 */
public class MainGame extends JeuDevint {

    @Override
    public String titre() {
        return "PolybeatMaker";
    }

    @Override
    protected ModeleDevint initModel() {
        return new ModeleDevintTest();
    }

    @Override
    protected ControleDevint initControlAndScene() {
        ControleDevint controller = null;
        try {
            FXMLLoader loader = new FXMLLoader(new File("../ressources/fxml/mainGame.fxml").toURI().toURL());
            Parent root = loader.load();
            controller = loader.getController();
            SceneDevint sc = new SceneDevint(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return controller;
    }

}