package org.michiganhackers.photoassassin;

import java.util.List;

public class Game {
    private List<Integer> participantIds;
    private Integer lengthOfGame;
    private Integer gameId;
    private String gameName;
    private Integer maxNumberParticipants;
    private Integer numberParticipants;

    public List<Integer> getParticipantIds() {
        return participantIds;
    }

    public Integer getLengthOfGame() {
        return lengthOfGame;
    }

    public Game(List<Integer> participantIds, Integer lengthOfGame, Integer gameId, String gameName, Integer maxNumberParticipants, Integer numberParticipants) {
        this.participantIds = participantIds;
        this.lengthOfGame = lengthOfGame;
        this.gameId = gameId;
        this.gameName = gameName;
        this.maxNumberParticipants = maxNumberParticipants;
        this.numberParticipants = numberParticipants;
    }

    public Integer getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public Integer getMaxNumberParticipants() {
        return maxNumberParticipants;
    }

    public Integer getNumberParticipants() {
        return numberParticipants;
    }

    public String getCurrentGameCapacity() {
        final String SLASH = " / ";
        return getNumberParticipants().toString() + SLASH + getMaxNumberParticipants().toString();
    }

}
