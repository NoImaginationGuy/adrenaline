package it.polimi.deib.newdem.adrenaline.controller.effects;

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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestChainsawEffect {

    Map map;
    Player player1;
    Player player2;
    Player player3;
    EffectManager manager;
    EffectManager managerNoPayment;
    Game game;
    

    public class ChainsawContext implements EffectContext {

        @Override
        public Player choosePlayer(MetaPlayer player, PlayerSelector selector, boolean forceChoice) throws UndoException {

            if(player == MetaPlayer.RED){
                return player2;
            }
            else{
                return player3;
            }

        }

        @Override
        public Tile chooseTile(TileSelector selector, boolean forceChoice) throws UndoException {
            return map.getTile(new TilePosition(1,0));
        }

        @Override
        public Integer chooseFragment(List<Integer> choices) throws UndoException {
            return null;
        }

        @Override
        public PaymentReceipt choosePayment(PaymentInvoice price, Integer choice) throws UndoException {
            List<PowerUpCard> powerUpCards = new ArrayList<>();

            return new PaymentReceipt(1,1,1, powerUpCards);
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
            return null;
        }

        @Override
        public Player getVictim() {
            return null;
        }
    }

    public class ChainsawContextNoPayment implements EffectContext {

        @Override
        public Player choosePlayer(MetaPlayer player, PlayerSelector selector, boolean forceChoice) throws UndoException {

            if(player == MetaPlayer.RED){
                return player2;
            }
            else{
                return player3;
            }

        }

        @Override
        public Tile chooseTile(TileSelector selector, boolean forceChoice) throws UndoException {
            return map.getTile(new TilePosition(1,0));
        }

        @Override
        public Integer chooseFragment(List<Integer> choices) throws UndoException {
            return null;
        }

        @Override
        public PaymentReceipt choosePayment(PaymentInvoice price, Integer choice) throws UndoException {
            return null;
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
            return null;
        }

        @Override
        public Player getVictim() {
            return null;
        }
    }

    @Before
    public void setUp() throws Exception {
        map =  Map.createMap(this.getClass().getClassLoader().getResource("JsonData.json").getFile().replace("%20", " "));

        GameParameters gp = GameParameters.fromConfig(Config.getDefaultConfig());

        ColorUserPair colorUserPair1 = new ColorUserPair(PlayerColor.YELLOW, new User());
        ColorUserPair colorUserPair2 = new ColorUserPair(PlayerColor.GREEN, new User());
        ColorUserPair colorUserPair3 = new ColorUserPair(PlayerColor.GRAY, new User());
        List<ColorUserPair> listPairs = new ArrayList<>();
        listPairs.add(colorUserPair1);
        listPairs.add(colorUserPair2);
        listPairs.add(colorUserPair3);

        gp.setColorUserOrder(listPairs);

        gp.setGameMap(map);

        game = new GameImpl(gp);

        game.init();

        player1 = game.getPlayerFromColor(PlayerColor.YELLOW);
        player2 = game.getPlayerFromColor(PlayerColor.GREEN);
        player3 = game.getPlayerFromColor(PlayerColor.GRAY);

        player1.getInventory().addAmmo(AmmoColor.YELLOW, 1);
        player1.getInventory().addAmmo(AmmoColor.BLUE, 1);
        player1.getInventory().addAmmo(AmmoColor.RED, 1);
        player2.getInventory().addAmmo(AmmoColor.BLUE, 1);
        player3.getInventory().addAmmo(AmmoColor.BLUE, 1);


        TilePosition tilePosition = new TilePosition(0,0);

        TilePosition tilePosition3 = new TilePosition(1,0);

        Tile destination = map.getTile(tilePosition);
        Tile destination3 = map.getTile(tilePosition3);


        map.movePlayer(player1, destination);
        map.movePlayer(player2, destination);
        map.movePlayer(player3, destination3);



        manager = new EffectManager(new ChainsawContext());
    }

    @Test
    public void testApplyPayment() {
        ChainsawEffect effect = new ChainsawEffect();
        try{
            manager.execute(effect);
        }catch (Exception e){
            fail();
        }

        assertEquals(0,player1.getInventory().getBlue());
        assertEquals(3,player2.getDamageFromPlayer(player1));

    }

    @Test
    public void testApplyNoPayment(){
        ChainsawEffect effect = new ChainsawEffect();

        managerNoPayment = new EffectManager(new ChainsawContextNoPayment());
        try{
            managerNoPayment.execute(effect);
        }catch (Exception e){
            fail();
        }

        assertEquals(1,player1.getInventory().getBlue());
        // FIXME
//        assertEquals(1,player2.getDamageFromPlayer(player1));
    }
}