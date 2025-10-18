package example.swingjack;

public class Card {
    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public int baseValue() {
        switch (rank) {
            case "A": return 1;
            case "J":
            case "Q":
            case "K": return 10;
            default: return Integer.parseInt(rank);
        }
    }

    @Override
    public String toString() {
        return rank + suit;
    }

    // Метод для отримання шляху до картинки карти
    public String getImagePath() {
        // Rank
        String r;
        switch(rank) {
            case "A": r = "ace"; break;
            case "J": r = "jack"; break;
            case "Q": r = "queen"; break;
            case "K": r = "king"; break;
            default: r = rank; break;
        }

        // Suit
        String s;
        switch(suit) {
            case "♣": s = "clubs"; break;
            case "♦": s = "diamonds"; break;
            case "♥": s = "hearts"; break;
            case "♠": s = "spades"; break;
            default: s = "unknown"; break;
        }

        // Повертаємо шлях у ресурсах (classpath)
        return "/cards/" + r + "_of_" + s + ".png";
    }
}
