package it.polimi.deib.newdem.adrenaline.view.client.gui;

import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackData;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.view.KillTrackView;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class GUIKillTrackView implements KillTrackView {

    private GUIGameWindow window;

    private int currentSlot;

    public GUIKillTrackView(GUIGameWindow window) {
        this.window = window;

        this.currentSlot = 1;
    }

    @Override
    public void restoreView(KillTrackData data) {
        Platform.runLater(() -> {

            for (KillTrackData.KillData cell : data.getKills()) {
                Pane slot = (Pane) window.getScene().lookup("#killTrackSlot"+ currentSlot++);

                for (int i = 0; i < cell.getAmount(); i++) {
                    slot.getChildren().add(GUIGameWindowHelper.createDamageIcon(cell.getKiller()));
                }
            }
        });
    }

    @Override
    public void registerKill(PlayerColor pColor, int amount) {
        Platform.runLater(() -> {
            Pane slot = (Pane) window.getScene().lookup("#killTrackSlot"+ currentSlot++);

            for (int i = 0; i < amount; i++) {
                slot.getChildren().add(GUIGameWindowHelper.createDamageIcon(pColor));
            }
        });
    }

    @Override
    public void undoLastKill() {
        Platform.runLater(() -> {
            Pane slot = (Pane) window.getScene().lookup("#killTrackSlot"+ --currentSlot);

            slot.getChildren().clear();
        });
    }

}