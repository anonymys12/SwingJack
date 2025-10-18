package example.swingjack;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CardPanel extends JPanel {
    private final List<Card> cards;
    private final boolean isDealerPanel;
    private boolean revealAll = false; // показати всі карти дилера наприкінці

    public CardPanel(List<Card> cards, boolean isDealerPanel) {
        this.cards = cards;
        this.isDealerPanel = isDealerPanel;
        setBackground(new Color(0, 100, 0));
    }

    public void revealDealerCards() {
        revealAll = true;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCards((Graphics2D) g);
    }

    private void drawCards(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cardW = 90, cardH = 130, gap = 30;
        int startX = 20;
        int y = (getHeight() - cardH) / 2;

        for (int i = 0; i < cards.size(); i++) {
            int x = startX + i * gap;
            if (isDealerPanel && !revealAll) drawBack(g2, x, y, cardW, cardH);
            else drawCard(g2, cards.get(i), x, y, cardW, cardH);
        }
    }

    private void drawBack(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(Color.BLUE.darker());
        g2.fillRoundRect(x, y, w, h, 15, 15);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, w, h, 15, 15);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2.drawString("?", x + w/2 - 7, y + h/2 + 8);
    }

    private void drawCard(Graphics2D g2, Card card, int x, int y, int w, int h) {
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, w, h, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, w, h, 15, 15);

        // rank + suit
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(card.getRank(), x + 10, y + 25);
        g2.drawString(card.getSuit(), x + 10, y + 50);

        // центр
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        String center = card.getRank();
        FontMetrics fm = g2.getFontMetrics();
        int cw = fm.stringWidth(center);
        g2.drawString(center, x + (w - cw)/2, y + h/2 + 12);
    }
}
