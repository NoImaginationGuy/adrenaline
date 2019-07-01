package it.polimi.deib.newdem.adrenaline.controller.effects;

import it.polimi.deib.newdem.adrenaline.TestingUtils;
import it.polimi.deib.newdem.adrenaline.controller.Config;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.PlayerSelector;
import it.polimi.deib.newdem.adrenaline.controller.effects.selection.TileSelector;
import it.polimi.deib.newdem.adrenaline.model.game.*;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoColor;
import it.polimi.deib.newdem.adrenaline.model.items.PowerUpCard;
import it.polimi.deib.newdem.adrenaline.model.map.Map;
import it.polimi.deib.newdem.adrenaline.model.map.Tile;
import it.polimi.deib.newdem.adrenaline.model.map.TilePosition;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;
import it.polimi.deib.newdem.adrenaline.view.server.NullVirtualGameView;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualDamageBoardView;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualGameView;
import it.polimi.deib.newdem.adrenaline.view.server.VirtualKillTrackView;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestNewtonEffect {

    EffectManager manager;
    NewtonEffect effect;
    Map map;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Game game;
    int counter;
    int effectsCounter;

    public class NewtonContext implements EffectContext {

        @Override
        public Player choosePlayer(MetaPlayer player, PlayerSelector selector, boolean forceChoice) throws UndoException {

            if(player == MetaPlayer.RED){
                return player2;
            }
            else if(player == MetaPlayer.BLUE){
                return player3;
            }else{
                return player4;
            }

        }

        @Override
        public Tile chooseTile(TileSelector selector, boolean forceChoice) throws UndoException {
            return map.getTile(new TilePosition(2,0));
        }

        @Override
        public Integer chooseFragment(List<Integer> choices) throws UndoException {
            Integer returnInt = counter;

            counter++;


            return returnInt;
        }

        @Override
        public PaymentReceipt choosePayment(PaymentInvoice price, int choice) throws UndoException {
            List<PowerUpCard> powerUpCards = new ArrayList<>();

            return null;


        }

        @Override
        public void damageDealtTrigger(Player attacker, Player victim) {

        }

        @Override
        public void damageTakenTrigger(Player attacker, Player victim) {

        }

        @Override
        public void applyGameChange(GameChange gameChange) {
            gameChange.update(game);
        }

        @Override
        public void revertGameChange(GameChange gameChange) {

        }

        @Override
        public Player getActor() {
            return player1;
        }

        @Override
        public Player getAttacker() {
            return player2;
        }

        @Override
        public Player getVictim() {
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
        EffectContext context;

        context = new NewtonContext();

        manager = new EffectManager(context);
        effect = new NewtonEffect();

        map =  Map.createMap(this.getClass().getClassLoader().getResource("Map0_0.json").getFile().replace("%20", " "));

        GameParameters gp = GameParameters.fromConfig(Config.getDefaultConfig());

        ColorUserPair colorUserPair1 = new ColorUserPair(PlayerColor.YELLOW, new User());
        ColorUserPair colorUserPair2 = new ColorUserPair(PlayerColor.GREEN, new User());
        ColorUserPair colorUserPair3 = new ColorUserPair(PlayerColor.GRAY, new User());
        ColorUserPair colorUserPair4 = new ColorUserPair(PlayerColor.MAGENTA, new User());
        List<ColorUserPair> listPairs = new ArrayList<>();
        listPairs.add(colorUserPair1);
        listPairs.add(colorUserPair2);
        listPairs.add(colorUserPair3);
        listPairs.add(colorUserPair4);


        gp.setColorUserOrder(listPairs);

        gp.setGameMap(map);
        TestingUtils.loadSingleton();

        game = new GameImpl(gp);

        VirtualGameView vgv = new NullVirtualGameView();
        game.setGameListener(vgv);
        game.setKillTrackListener(new VirtualKillTrackView(vgv)); //???

        game.init();

        player1 = game.getPlayerFromColor(PlayerColor.YELLOW);
        player2 = game.getPlayerFromColor(PlayerColor.GREEN);
        player3 = game.getPlayerFromColor(PlayerColor.GRAY);
        player4 = game.getPlayerFromColor(PlayerColor.MAGENTA);

        player1.getDamageBoard().setListener(new VirtualDamageBoardView(player1, vgv));
        player2.getDamageBoard().setListener(new VirtualDamageBoardView(player2, vgv));
        player3.getDamageBoard().setListener(new VirtualDamageBoardView(player3, vgv));
        player4.getDamageBoard().setListener(new VirtualDamageBoardView(player4, vgv));

        player1.getInventory().addAmmo(AmmoColor.RED, 1);
        player2.getInventory().addAmmo(AmmoColor.BLUE, 1);
        player3.getInventory().addAmmo(AmmoColor.BLUE, 1);
        player4.getInventory().addAmmo(AmmoColor.BLUE,1);


        TilePosition tilePosition1 = new TilePosition(0,0);
        TilePosition tilePosition2 = new TilePosition(0,0);
        TilePosition tilePosition3 = new TilePosition(1,0);
        TilePosition tilePosition4 = new TilePosition(2,0);

        Tile destination1 = map.getTile(tilePosition1);
        Tile destination2 = map.getTile(tilePosition2);
        Tile destination3 = map.getTile(tilePosition3);
        Tile destination4 = map.getTile(tilePosition4);


        map.movePlayer(player1, destination1);
        map.movePlayer(player2, destination2);
        map.movePlayer(player3, destination3);
        map.movePlayer(player4,destination4);

        counter = 0;
        effectsCounter = 0;
    }

    @Test
    public void apply() {

        try{
            manager.execute(effect);
        }catch (Exception e){
            fail();
        }

        assertEquals(new TilePosition(2,0), player2.getTile().getPosition());


    }
}