package it.polimi.deib.newdem.adrenaline.common.model.map;

import it.polimi.deib.newdem.adrenaline.common.model.game.Player;

import java.util.List;

public class ConcreteRoom implements Room {
    // TODO figure out data structure

    // discussed on Discord
    private Map map;

    private List<Tile> tiles;

    public ConcreteRoom(Map map, List<Tile> tiles) {
        // TODO review
        this.map = map;
        this.tiles = tiles;
    }

    @Override
    public List<Tile> getTiles() {
        // TODO implement
        return tiles;
    }

    @Override
    public Map getMap() {
        // TODO implement
        return map;
    }

    @Override
    public Player getPlayers() {
        // TODO implement
        return null;
    }

}