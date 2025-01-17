package it.polimi.deib.newdem.adrenaline.model.game;

import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrack;
import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackData;
import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackImpl;
import it.polimi.deib.newdem.adrenaline.model.game.killtrack.KillTrackListener;
import it.polimi.deib.newdem.adrenaline.model.game.player.Player;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerColor;
import it.polimi.deib.newdem.adrenaline.model.game.player.PlayerImpl;
import it.polimi.deib.newdem.adrenaline.model.game.turn.FirstTurn;
import it.polimi.deib.newdem.adrenaline.model.game.turn.OrdinaryTurn;
import it.polimi.deib.newdem.adrenaline.model.game.turn.RoundRobin;
import it.polimi.deib.newdem.adrenaline.model.game.turn.Turn;
import it.polimi.deib.newdem.adrenaline.model.items.*;
import it.polimi.deib.newdem.adrenaline.model.map.*;
import it.polimi.deib.newdem.adrenaline.model.mgmt.User;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.deib.newdem.adrenaline.model.game.DamageBoardImpl.DEATH_SHOT_INDEX;
import static it.polimi.deib.newdem.adrenaline.model.game.DamageBoardImpl.OVERKILL_SHOT_INDEX;
import static java.lang.Integer.min;

public class GameImpl implements Game {

    private Map map;
    private RoundRobin<Turn> turnQueue;
    private KillTrack killTrack;
    private List<Player> players;
    private boolean isFrenzy; // BAD, enum or states
    private boolean isOver;
    private int turnTimeSeconds;
    protected Deck<PowerUpCard> powerUpDeck;
    protected Deck<WeaponCard> weaponDeck;
    protected Deck<DropInstance> dropDeck;
    private GameListener listener;
    private java.util.Map<PlayerColor, User> colorUserMap;
    private boolean init;
    private ListenerRegistry listenerRegistry;
    private int minPlayers;
    private int maxPlayers;

    public static final int MAX_PLAYERS_PER_GAME = 5;
    public static final String WEAPON_DECK_PATH = "cards/weapons.json";
    public static final String POWERUP_DECK_PATH = "cards/powerups.json";
    public static final String DROP_DECK_PATH = "cards/drops.json";

    /**
     * Builds a new game from the given {@code GameParameters}
     *
     * @param parameters to build the new game from
     */
    public GameImpl(GameParameters parameters) {
        map = parameters.getGameMap();
        killTrack = new KillTrackImpl(parameters.getKillTrackInitialLength());
        colorUserMap = parameters.getColorUserMap();
        players = new ArrayList<>(MAX_PLAYERS_PER_GAME);
        // ^ no players added
        turnQueue = new RoundRobin<>();
        isOver = false;
        turnTimeSeconds = parameters.getTurnTime();
        listenerRegistry = new ListenerRegistry();
        init = false;
        minPlayers = parameters.getMinPlayers();
        maxPlayers = parameters.getMaxPlayers();
    }

    private GameImpl(){}

    /**
     * Returns a reference to tis game's map
     *
     * @return reference to this game's map
     */
    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public void setGameListener(GameListener listener) {
        if (init) setAndNotifyListener(listener);
        else listenerRegistry.setGameListener(listener);
    }

    /**
     * Identifies a {@code Player} in this game from its {@code PlayerColor}
     *
     * @param color of the desired {@code Player}
     * @return player of the given color in this game.
     * Returns null if no player is present for the given color.
     */
    @Override
    public Player getPlayerFromColor(PlayerColor color) {
        for(Player p : players) {
            if(color == p.getColor()){
                return p;
            }
        }
        return null;
    }

    /**
     * Checks if this game's state is frenzy.
     *
     * @return is in frenzy
     */
    @Override
    public boolean isInFrenzy() {
        return isFrenzy;
    }

    /**
     * Checks if this game meets the criteria to go to the Frenzy stage.
     *
     * @return should this game go Frenzy
     */
    @Override
    public boolean shouldGoFrenzy() {
        return !isFrenzy &&
                killTrack.getTotalKills() >= killTrack.getTrackLength();
    }

    private void bindElementsListeners() {
        // attach new listeners
        killTrack.setListener(listenerRegistry.getKillTrackListener());
        setAndNotifyListener(listenerRegistry.getGameListener());
    }

    private void setAndNotifyListener(GameListener listener) {
        this.listener = listener;
    }

    private void createNewPlayers(){
        if(!players.isEmpty()) throw new IllegalStateException();
        boolean isFirst = true;

        for(java.util.Map.Entry<PlayerColor,User> e : colorUserMap.entrySet()) {
            Player newPlayer = new PlayerImpl(
                    e.getKey(),
                    this
            );

            if(isFirst){
                newPlayer.assignFirstPlayerCard();
                isFirst = false;
            }
            players.add(newPlayer);
        }
    }

    private void notifyPlayersIngress(){
        for(Player p : players) {
            listener.userDidEnterGame(
                    colorUserMap.get(p.getColor()),
                    p);
        }
    }

    private void setUpRoundRobin() {
        for (Player p : players) {
            turnQueue.enqueue(new FirstTurn(p));
        }
    }

    private void initPlayers() {
        for (Player p : players) {
            p.init();
        }
    }

    /**
     * Prepares this game for its first execution
     */
    public void init() {
        // TODO check that listener is not null
        // TODO add flavorful exception
        // TODO bind to gp min
        if(colorUserMap.entrySet().size() < minPlayers) throw new IllegalStateException();

        bindElementsListeners();

        // load cards
        importWeaponDeck();

        //load decks
        importPowerUpDeck(POWERUP_DECK_PATH);
        importDropDeck(DROP_DECK_PATH);

        // create Players
        createNewPlayers();

        // register players on listener
        notifyPlayersIngress();

        // prepare round robin
        setUpRoundRobin();

        // init players
        initPlayers();

        // fill tiles
        //TODO load non-empty decks from json
        refillTiles(); // no decks, breaks tests for now

        // set flags
        isFrenzy = false;
        init = true;

        listener.gameDidInit(this, generateGameData());
        // listenerThing.thingDidInit(thing, thing,generateThingData())
    }

    @Override
    public GameData generateGameData() {
        GameData gameData = new GameData();
        for (Player p : players) {
            User playerUser = getUserByPlayer(p);

            gameData.addUser(playerUser.getName(), p.getColor(), playerUser.isConnected());
        }

        gameData.setFinalized();
        return gameData;
    }

    @Override
    public KillTrackData generateKillTrackData() {
        return killTrack.generateKillTrackData();
    }

    @Override
    public GameResults generateResults() {
        GameResults results = new GameResults();

        for (Player p : players) {
            results.addPlayer(p.getColor(), p.getScore());
        }

        return results;
    }

    @Override
    public void concludeGame() {
        assignScoreFromKillTrack();
        listener.gameWillEnd(this, generateResults());
    }

    private void assignScoreFromKillTrack() {
        for(Player p : players) {
            p.addScore(killTrack.getScoreForPlayer(p));
        }
    }

    @Override
    public KillTrack getKillTrack() {
        return killTrack;
    }

    /**
     * Returns the next turn.
     *
     * @return
     */
    @Override
    public Turn getNextTurn() {
        Turn t = turnQueue.next();
        if(null == t) isOver = true;
        return t;
    }

    // rethink as singleton from AdrenalineGameController
    private void importWeaponDeck() {
        weaponDeck = WeaponDeck.createNewDeck();
    }

    private void importPowerUpDeck(String filePath) {
        PowerUpDeck factory;
        try {
            factory = PowerUpDeck.fromJson(filePath);
        }
        catch(InvalidJSONException e) {
            throw new IllegalStateException();
        }
        powerUpDeck = factory.createNewDeck();
    }

    private void importDropDeck(String filePath) {
        DropDeck factory;
        try {
            factory = DropDeck.fromJson(filePath);
        } catch (InvalidJSONException e) {
            throw new IllegalStateException();
        }
        dropDeck = factory.createNewDeck();
    }

    /**
     * Performs actions at the end of a turn.
     *
     * Among them, passing to frenzy if needed.
     *
     * @param turn to conclude
     */
    @Override
    public void concludeTurn(Turn turn) {
        // EOT powerups and reload are handled in
        // TurnBaseImpl::performClosingActions()

        // extra point for multiple kills
        for(Player p :players) {
            int killsCount = 0;
            for(Player q : players) {
                Player d = q.getDamager(DEATH_SHOT_INDEX);
                if(null != d && d.equals(p)) {
                    killsCount++;
                }
                if(killsCount >= 2) {
                    p.addScore(1);
                }
            }
        }

        // register kills
        registerKills();

        // refill tiles
        // both drops
        // and weapons
        refillTiles(); // needs non-empty drop deck

        // add new turn
        if(!isFrenzy) {
            turnQueue.enqueue(new OrdinaryTurn(turn.getActivePlayer()));
        }

        //go to frenzy if required
        if(shouldGoFrenzy()) {
            goFrenzy();
        }

        resetTurnDeathFlags();
    }

    private void resetTurnDeathFlags() {
        for(Player p : players) {
            p.resetTurnDeath();
        }
    }

    private void registerKills() {
        for(Player p : players) {
            if(p.diedThisTurn()) {
                distributeScore(p); // assigns the due score to damagers
                scoreDamageboard(p);   // updates killtrack, removes from map, empties damageboard

                //TODO
                // remove player from map
                // setup respawn (if not implicit in turn)
            }
        }
    }

    /**
     * Assigns a new {@code User} to the {@code Player} of the given {@code PlayerColor}.
     *
     * @throws IllegalArgumentException if there is no player for the given {@code PlayerColor}
     * @param user to assign to the player of the given color
     * @param color of the user to whom assign the new user-
     */
    @Override
    public void setUserForColor(User user, PlayerColor color) {
        if(!colorUserMap.containsKey(color)) throw new IllegalArgumentException();
        colorUserMap.replace(color, user);
    }

    /**
     * Gets the {@code User} associated with this {@code Player}
     *
     * @param player to find the user associated with
     * @return user associated to the given {@code Player}
     */
    @Override
    public User getUserByPlayer(Player player) {
        return colorUserMap.getOrDefault(player.getColor(), null);
    }

    /**
     * Goes frenzy.
     *
     * This flips all players' boards where applicable
     * and prepares the last turn of all players
     * according to the game's criteria.
     *
     * Stops the generation of new turns.
     */
    /* friendly */ void goFrenzy() {
        if(isFrenzy) throw new IllegalStateException();
        List<Turn> turnList = turnQueue.getList();
        boolean precedeFirstPlayer = false;

       for(Turn t : turnList) {
           Player activePlayer = t.getActivePlayer();
           precedeFirstPlayer = precedeFirstPlayer || activePlayer.hasFirstPlayerCard();
           activePlayer.goFrenzy(precedeFirstPlayer);
       }

       isFrenzy = true;
    }


    private void distributeScore(Player p) {
        // score damage boards for dead player p
        for(Player q : this.players) {
            q.addScore(
                     p.getScoreForPlayer(q)
             );
        }
    }

    private void scoreDamageboard(Player deadPlayer) {
        deadPlayer.addSkull();
        Player killer = deadPlayer.getDamager(DEATH_SHOT_INDEX);
        // player mist not be null

        int killtrackMarkAmount = 1;
        Player overkiller = deadPlayer.getDamager(OVERKILL_SHOT_INDEX);
        if(null != overkiller) {
            killtrackMarkAmount++;
            int oldMarks = overkiller.getMarksFromPlayer(deadPlayer);
            overkiller.getDamageBoard().setMarksFromPlayer(min(oldMarks + 1, 3), deadPlayer);
        }

        killTrack.addKill(killer, killtrackMarkAmount);

        map.removePlayer(deadPlayer);
        deadPlayer.getDamageBoard().renewDamageBoard();
    }

    public boolean isOver() {
        // return isOver;
        return turnQueue.isEmpty();
    }

    @Override
    public int getTurnTime() {
        return turnTimeSeconds;
    }

    @Override
    public void setKillTrackListener(KillTrackListener listener) {
        if(!init) listenerRegistry.setKillTrackListener(listener);
        else killTrack.setListener(listener);
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public void declareOver() {
        isOver = true;
        // persistence?
    }

    public Deck<PowerUpCard> getPowerUpDeck() {
        return powerUpDeck;
    }

    public Deck<WeaponCard> getWeaponDeck() {return weaponDeck; }

    protected void refillTiles(){
        for(Tile t : map.getAllTiles()){
            if(t.hasSpawnPoint()) {
                refillSpawn(t);
            }
            else {
                // t has NOT spawnpoint
                // ==> t is ordinaryTile
                refillDrop(t);
            }
        }
    }

    private void refillSpawn(Tile t) {
        try {
        while (t.inspectWeaponSet().getWeapons().size() < 3) {
                t.addWeapon(weaponDeck.draw());
            }
        }
        catch (NoDrawableCardException e) {
        // this CAN happen, and that's ok.
        // We just ignore it and move on,
        // as designed in game.
        }
        catch (OutOfSlotsException |
                WeaponAlreadyPresentException |
                NotSpawnPointTileException e) {
            // this should NOT happen, so we report it
            throw new IllegalStateException(e);
        }

    }

    private void refillDrop(Tile t) {
        if(t.missingDrop()){
            try {
                t.addDrop(dropDeck.draw());
            }
            catch (NoDrawableCardException e) {
                // this CAN happen, and that's ok.
                // We just ignore it and move on,
                // as designed in game.
            }
            catch (DropAlreadyPresentException |
                    NotOrdinaryTileException e) {
                // this should NOT happen, so we report it
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public Deck<DropInstance> getDropDeck() {
        return dropDeck;
    }
}
