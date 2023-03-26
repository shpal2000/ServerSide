package com.github.splendor_mobile_game.database;

import com.github.splendor_mobile_game.game.model.Room;
import com.github.splendor_mobile_game.game.model.User;

import java.util.ArrayList;
import java.util.UUID;

public class InMemoryDatabase implements Database {

    private ArrayList<User> allUsers = new ArrayList<>();
    private ArrayList<Room> allRooms = new ArrayList<>();


    public InMemoryDatabase() {

    }



    @Override
    public User getUser(UUID uuid) {
        for(User user : allUsers) {
            if (user.getUuid().equals(uuid)) return user;
        }
        return null;
    }

    @Override
    public void addUser(User user) {
        this.allUsers.add(user);
    }

    @Override
    public Room getRoom(UUID uuid) {
        for(Room room : allRooms) {
            if (room.getUuid().equals(uuid)) return room;
        }
        return null;
    }

    @Override
    public Room getRoom(String enterCode) {
        for(Room room : allRooms) {
            if (room.getEnterCode().equals(enterCode)) return room;
        }
        return null;
    }

    @Override
    public void addRoom(Room room) {
        this.allRooms.add(room);
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return allUsers;
    }

    @Override
    public ArrayList<Room> getAllRooms() {
        return allRooms;
    }

    private ArrayList<Card> allCards = new ArrayList<>();

    public InMemoryDatabase() {
        loadCardsFromCsv();
    }

    private void loadCardsFromCsv() {
        String csvFile = "cards.csv";
        String line = "";
        String csvSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
                CardTier cardTier = CardTier.valueOf(data[0]);
                int points = Integer.parseInt(data[1]);
                int emeraldCost = Integer.parseInt(data[2]);
                int sapphireCost = Integer.parseInt(data[3]);
                int rubyCost = Integer.parseInt(data[4]);
                int diamondCost = Integer.parseInt(data[5]);
                int onyxCost = Integer.parseInt(data[6]);
                TokenType additionalToken = TokenType.valueOf(data[7]);
                Card card = new Card(cardTier, points, emeraldCost, sapphireCost, rubyCost, diamondCost, onyxCost);
                if (additionalToken != null) {
                    card.setAdditionalToken(additionalToken);
                }
                allCards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
