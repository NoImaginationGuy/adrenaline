package it.polimi.deib.newdem.adrenaline.controller;

import it.polimi.deib.newdem.adrenaline.model.game.*;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.turn.NullTurnDataSource;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.game.turn.TurnDataSource;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualGameView;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualKillTrackView;

import java.util.*;
import java.util.List;

public class AdrenalineGameController implements GameController {

    private Game game;

    private LobbyController lobbyController;

    private VirtualGameView vgv;

    protected java.util.Map<Player, TurnDataSource> playerDataSourceMap;

    public AdrenalineGameController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }

    public Player getPlayer(User user){
        //TODO
        return null;
    }

    /**
     * Sets up a new game, not restored from disk.
     *
     * @param users soon to be players of the new game
     */
    @Override
    public void setupGame(List<User> users) {
        //TODO notifica listener
        GameParameters gp = GameParameters.fromConfig(lobbyController.getConfig());
        if(users.isEmpty() || users.size() > lobbyController.getConfig().getMaxPlayers()) {
            throw new IllegalArgumentException();
        }

        String s = this.getClass().getClassLoader().getResource("maps/Map0_0.json").getFile().replace("%20", " ");
        Map myMap = Map.createMap( s );

        List<ColorUserPair> listCup = generateColorUserOrder(users);

        gp.setGameMap(myMap);
        gp.setColorUserOrder(listCup);
        Config config = lobbyController.getConfig();
        gp.setMinPlayers(config.getMinPlayers());
        gp.setMaxPlayers(config.getMaxPlayers());

        game = new GameImpl(gp);
        // bind map listener?

        vgv = new VirtualGameView(lobbyController.getConnectionReceiver());
        game.setGameListener(vgv);
        game.setKillTrackListener(new VirtualKillTrackView(vgv)); //???

        game.init(); // (VirtualGameView)

        buildTurnDataSources(game);
    }

    @Override
    public void recoverGame() {

    }

    private List<ColorUserPair> generateColorUserOrder(List<User> users){
        List<ColorUserPair> listCup = new ArrayList<>(lobbyController.getConfig().getMaxPlayers());

        List<PlayerColor> colors = Arrays.asList(PlayerColor.values());
        Collections.shuffle(colors);
        Collections.shuffle(users);

        for(int i = 0; i < users.size(); i++) {
            listCup.add(new ColorUserPair(
                    colors.get(i),
                    users.get(i)
            ));
        }
        return listCup;
    }

    @Override
    public void runGame() {
        while (!game.isOver()) {
            Turn turn = game.getNextTurn();
            turn.bindDataSource(playerDataSourceMap.get(turn.getActivePlayer()));
            if(!turn.getActivePlayer().isConnected()) {
                game.concludeTurn(turn);
                if(!isAnyPlayerConnected())
                {
                    game.declareOver();
                    lobbyController.endGame();
                }
                continue;
            }

            TimedExecutor te = new TimedExecutor(turn::execute);
            try {
                te.execute(game.getTurnTime());
            }
            catch (TimeoutException e) {
                // revert?
            } catch (AbortedException e) {
                // nothing to do here?
                // player disconnected during their turn
            }
        }
        lobbyController.endGame(); // /snap/
    }

    @Override
    public void userDidDisconnect(User user) {
        // TODO
    }

    @Override
    public void userDidReconnect(User user) {
        // TODO
    }

    protected void buildTurnDataSources(Game game){
        playerDataSourceMap = new HashMap<>();
        for(PlayerColor c : PlayerColor.values()){
            Player p = game.getPlayerFromColor(c);
            if(null != p){
                playerDataSourceMap.put(p, new NullTurnDataSource());
            }
        }
    }

    private boolean isAnyPlayerConnected(){
        for(Player p : game.getPlayers())
        {
            if(p.isConnected()) {
                return true;
            }
        }
        return false;
    }

}
