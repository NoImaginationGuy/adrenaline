package it.polimi.deib.newdem.adrenaline.model.map;

import com.google.gson.Gson;
import it.polimi.deib.newdem.adrenaline.model.items.AmmoColor;
import it.polimi.deib.newdem.adrenaline.utils.FileUtils;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapBuilder {

    private SpawnPointTileDict spawnPointTileDict;
    private int[][][] roomListInt;
    private int[][][] adjacencyList;
    private final List<Room> rooms;
    private Tile[][] matrixMap;
    private String mapID;


    /**Creates a new {@code MapBuilder} instance taking a json encoded version of the map as input.
     * 
     * @param mapJsonData json encoded version of the map as input.
     */
    public MapBuilder(String mapJsonData){

        try (Reader reader = FileUtils.getResourceReader(mapJsonData)) {

            Gson gson = new Gson();
            JsonData jsonData = gson.fromJson(reader,JsonData.class );

            this.adjacencyList = jsonData.getAdjacencyList();
            this.roomListInt = jsonData.getRoomListInt();
            this.spawnPointTileDict = jsonData.getSpawnPointTileDict();
            this.mapID = jsonData.getMapID();


        } catch (Exception e) {
            //TODO
        }

        this.matrixMap = new Tile[99][99];

        this.matrixMap = buildMatrixMap();

        this.rooms = buildRooms();

    }

    /**Method used to create the  first input to the Map's constructor: matrixMap.
     *
     * @return matrixMap an array of arrays of tiles.
     */
    private Tile[][] buildMatrixMap(){

        for(int[][] adjacencyCouple : adjacencyList){


            int[] tileInt1 = adjacencyCouple[0];
            int[] tileInt2 = adjacencyCouple[1];

            if(matrixMap[tileInt1[0]][tileInt1[1]] == null){
                if(spawnPointColorTileInt(tileInt1) != null){
                    matrixMap[tileInt1[0]][tileInt1[1]] = new SpawnPointTile(new TilePosition(tileInt1[0], tileInt1[1]),spawnPointColorTileInt(tileInt1));

                }
                else{
                    matrixMap[tileInt1[0]][tileInt1[1]] = new OrdinaryTile(new TilePosition(tileInt1[0], tileInt1[1]));
                }
            }

            if(matrixMap[tileInt2[0]][tileInt2[1]] == null){
                if(spawnPointColorTileInt(tileInt2) != null){
                    matrixMap[tileInt2[0]][tileInt2[1]] = new SpawnPointTile(new TilePosition(tileInt2[0], tileInt2[1]),spawnPointColorTileInt(tileInt2));

                }
                else{
                    matrixMap[tileInt2[0]][tileInt2[1]] = new OrdinaryTile(new TilePosition(tileInt2[0], tileInt2[1]));
                }
            }

            if(!matrixMap[tileInt2[0]][tileInt2[1]].getAdjacentTiles().contains(matrixMap[tileInt1[0]][tileInt1[1]])) {
                matrixMap[tileInt2[0]][tileInt2[1]].addAdjacentTiles(matrixMap[tileInt1[0]][tileInt1[1]]);
            }
            if (!matrixMap[tileInt1[0]][tileInt1[1]].getAdjacentTiles().contains(matrixMap[tileInt2[0]][tileInt2[1]])){
                matrixMap[tileInt1[0]][tileInt1[1]].addAdjacentTiles(matrixMap[tileInt2[0]][tileInt2[1]]);
            }

        }

        return matrixMap;
    }
    /**Used to bind rooms and tiles together in buildMatrixMap.
     *
     */
    private List<Room> buildRooms() {
        List<Room> tempRooms = new ArrayList<Room>();

        for (int[][] roomInt : roomListInt) {
            Room room = new ConcreteRoom();
            tempRooms.add(room);
            for (int[] intPos : roomInt) {
                matrixMap[intPos[0]][intPos[1]].setRoom(room);
                room.addTiles(matrixMap[intPos[0]][intPos[1]]);
            }
        }

        return tempRooms;
    }

    /**
     * Inner class used to load the data of the SpawnPointTile
     * @param tileInt
     * @return
     */
    private AmmoColor spawnPointColorTileInt(int[] tileInt){
            AmmoColor result;

            int[] red = spawnPointTileDict.getRed();
            int[] blue = spawnPointTileDict.getBlue();
            int[] yellow = spawnPointTileDict.getYellow();


            if(Arrays.equals(red,tileInt)){
                result = AmmoColor.RED;
            }else if(Arrays.equals(blue,tileInt)){
                result = AmmoColor.BLUE;
            }else if(Arrays.equals(yellow,tileInt)){
                result = AmmoColor.YELLOW;
            }else{
                result = null;
            }


            return result;
    }

    /**
     * Method used in the static method of map to create a map.
     * @return
     */
    public Map buildMap(){
        Map map = new ConcreteMap(matrixMap,rooms, mapID);
        map.bindRooms();

        return map;
    }
}

