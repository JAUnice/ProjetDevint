package dvt.polybeatmaker.model;


import dvt.polybeatmaker.controller.MainController;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler extends Timer {

    private static final int LOOPTIME = 9;

    private MainController controller;

    private boolean started;

    private List<Sound> sounds;

    public Scheduler() {
        super();
        started = false;
        sounds = new ArrayList<>();
    }

    public void addToQueue(Sound s) {
        if (!sounds.contains(s)) {
            sounds.add(s);
        }
    }

    public void removeFromQueue(Sound s) {
        sounds.remove(s);
    }

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void start() {
        started = true;
        schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        for (Sound s : sounds) {
                            s.play();
                        }
                        controller.updateProgressBar(LOOPTIME*1000);
                    }
                }, 0, LOOPTIME * 1000);
    }

    public boolean isStarted() {
        return started;
    }

}
