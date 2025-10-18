package example.swingjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();
    private int idx = 0;

    public Deck() {
        String[] ranks = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] suits = {"♠","♥","♦","♣"};
        for (String s : suits) {
            for (String r : ranks) {
                cards.add(new Card(r, s));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        idx = 0;
    }

    public Card draw() {
        if (idx >= cards.size()) shuffle();
        return cards.get(idx++);
    }
}
