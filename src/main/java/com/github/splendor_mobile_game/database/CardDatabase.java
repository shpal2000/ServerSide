package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.enums.CardTier;
import com.github.splendor_mobile_game.game.enums.TokenType;
import com.github.splendor_mobile_game.game.model.Card;

import java.util.ArrayList;

public class CardDatabase {



    public static ArrayList<Card> getAllCards(CardTier cardTier) {
        ArrayList<Card> array = new ArrayList<>();
        InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
        ArrayList<Card> allCards = inMemoryDatabase.getAllCards();
        for (Card card : allCards) {
            if (card.getCardTier() == cardTier) {
                array.add(card);
            }
        }
        return array;
    }



}
