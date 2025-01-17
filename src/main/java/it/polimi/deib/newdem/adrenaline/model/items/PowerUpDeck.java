package it.polimi.deib.newdem.adrenaline.model.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.deib.newdem.adrenaline.controller.effects.Effect;
import it.polimi.deib.newdem.adrenaline.controller.effects.EffectLoader;
import it.polimi.deib.newdem.adrenaline.utils.FileUtils;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class PowerUpDeck {

    private List<PowerUpCard> cards;

    private PowerUpDeck(List<PowerUpCard> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public Deck<PowerUpCard> createNewDeck() {
        return new Deck<>(cards);
    }



    /**
     * Method to load correctly the Trigger from Json field.
     * @param jsonTrigger Json field of the PowerUpTrigger.
     * @return the PowerUpTrigger as loaded from the json
     */
    private static PowerUpTrigger parseTrigger(String jsonTrigger) throws InvalidJSONException {
        switch (jsonTrigger.toLowerCase()) {
            case "discharge":
                return PowerUpTrigger.CALL;
            case "damagetaken":
                return PowerUpTrigger.DAMAGE_TAKEN;
            case "damagedealt":
                return PowerUpTrigger.DAMAGE_DEALT;
            case "call":
                return PowerUpTrigger.CALL;
            default:
                throw new InvalidJSONException("Invalid trigger.");
        }
    }

    /**
     * Method to load correctly the ammoColor from Json field.
     * @param jsonAmmoColor Json field of the ammoColor.
     * @return the Ammocolor as loaded from the json
     */
    private static AmmoColor parseAmmoColor(String jsonAmmoColor) throws InvalidJSONException {
        switch (jsonAmmoColor.toLowerCase()) {
            case "red":
                return AmmoColor.RED;
            case "yellow":
                return AmmoColor.YELLOW;
            case "blue":
                return AmmoColor.BLUE;
            default:
                throw new InvalidJSONException("Invalid ammo color.");
        }
    }

    /**
     * Method to load PowerUpDeck from json file in resources.
     * @param jsonFile the source file for the PowerUpDeck.
     * @return the PowerUpDeck loaded from the file.
     * @throws InvalidJSONException in case the file is wrong.
     */
    public static PowerUpDeck fromJson(String jsonFile) throws InvalidJSONException {
        List<PowerUpCard> cards = new ArrayList<>();

        try (Reader reader = FileUtils.getResourceReader(jsonFile)) {
            JsonObject deckJsonObject = new JsonParser().parse(reader).getAsJsonObject();

            JsonArray cardsJsonArray = deckJsonObject.get("cards").getAsJsonArray();

            for (JsonElement object : cardsJsonArray) {
                JsonObject cardObject = object.getAsJsonObject();

                int cardID = cardObject.get("id").getAsInt();

                String effectClassName = cardObject.get("effectClass").getAsString();
                Effect cardEffect = EffectLoader.fromClass(effectClassName);

                PowerUpTrigger trigger = parseTrigger(cardObject.get("trigger").getAsString());

                AmmoColor equivalentAmmo = parseAmmoColor(cardObject.get("equivAmmo").getAsString());

                PowerUpCard card = new PowerUpCardImpl(cardEffect, cardID, trigger, equivalentAmmo);
                cards.add(card);
            }

        } catch (Exception e) {
            throw new InvalidJSONException(e.getMessage());
        }

        return new PowerUpDeck(cards);
    }
}
