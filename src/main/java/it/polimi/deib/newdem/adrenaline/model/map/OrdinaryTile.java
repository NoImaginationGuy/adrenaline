package it.polimi.deib.newdem.adrenaline.model.map;

import it.polimi.deib.newdem.adrenaline.model.items.DropInstance;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponCard;
import it.polimi.deib.newdem.adrenaline.model.items.WeaponSet;


public class OrdinaryTile extends ConcreteTile {

    private boolean missingDrop = true;
    private DropInstance dropInstance;

    /**Creates a new {@code OrdinaryTile} belonging to room and in position tilePosition.
     *
     * @param tilePosition the position the tile occupies.
     */
    public OrdinaryTile(TilePosition tilePosition){
        super(tilePosition);
    }

    /**Used to reveal if the tile is a Spawn Point Tile
     *
     * @return always false.
     */
    @Override
    public boolean hasSpawnPoint() {
        return false;
    }

    /**Used to observe the drop without grabbing it.
     *
     * @return The drop currently present of the tile, null if no drop is present.
     */
    @Override
    public DropInstance inspectDrop() {

        if(!missingDrop){
            return dropInstance.copyDrop();
        }
        else{
            return null;
        }
    }

    /**Used to observe the WeaponSet without grabbing it.
     *
     * @return empty set.
     */
    @Override
    public WeaponSet inspectWeaponSet() {
        WeaponSet set;
        set = new WeaponSet();
        return set;
    }

    /**Adds drop to the tile if it doesn't have one
     *
     * @param drop the drop to add.
     * @throws DropAlreadyPresentException if the tile already has a drop.
     */
    @Override
    public void addDrop(DropInstance drop) throws DropAlreadyPresentException {
        if(missingDrop){
            dropInstance = drop;
            missingDrop = false;

            if(getMap().getListener() != null){
                getMap().getListener().dropDidSpawn(this,drop);
            }

        }
        else{
            throw new DropAlreadyPresentException("Drop already present");
        }

    }

    /**Adds Weapon to the tile.
     *
     * @param weapon the weapon card of the weapon to add.
     * @throws NotSpawnPointTileException always since it is not a spawn point tile.
     */
    @Override
    public void addWeapon(WeaponCard weapon) throws NotSpawnPointTileException {
        throw new NotSpawnPointTileException("It is not possible to add Weapons to an Ordinary Tile");
    }

    /**Grabs drop from the tile if present then signals that the tile doesn't have one anymore.
     *
     * @return the drop present in the tile.
     */
    @Override
    public DropInstance grabDrop() {
        if(!missingDrop){
            missingDrop = true;
            if(getMap().getListener() != null){
                getMap().getListener().dropDidDespawn(this);
            }
            return dropInstance.copyDrop();
        }
        else{
            return null;
        }

    }

    /**Grabs weapon from the tile's weapon set.
     *
     * @param weaponCard the weapon to grab.
     * @return empty weapon set since this is an ordinary tile.
     */
    @Override
    public WeaponCard grabWeapon(WeaponCard weaponCard) throws NotSpawnPointTileException {
        throw new NotSpawnPointTileException("It is not possible to add Weapons to an Ordinary Tile");
    }

    /**Check if the tile lacks a drop.
     *
     * @return boolean value.
     */
    @Override
    public boolean missingDrop() {
        return missingDrop;
    }
}
