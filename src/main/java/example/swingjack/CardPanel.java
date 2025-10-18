package example.swingjack;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CardPanel extends JPanel {
    private final List<Card> cards;
    private final boolean isDealerPanel;
    private boolean revealAll = false;

    private final List<AnimatedCard> animCards = new ArrayList<>();

    public CardPanel(List<Card> cards, boolean isDealerPanel) {
        this.cards = cards;
        this.isDealerPanel = isDealerPanel;
        setBackground(new Color(0, 100, 0));
    }

    public void revealDealerCards() {
        revealAll = true;
        repaint();
    }

    public void hideDealerCards() {
        revealAll = false;
        repaint();
    }

    // Анімація нової карти
    public void animateCard(Card card, int targetIndex) {
        int cardW = 90, cardH = 130, gap = 30;
        int startX = getWidth() / 2 - cardW / 2;
        int startY = isDealerPanel ? 20 : getHeight() - cardH - 20;
        int targetX = 20 + targetIndex * gap;
        int targetY = (getHeight() - cardH) / 2;

        AnimatedCard anim = new AnimatedCard(card, startX, startY, targetX, targetY);
        animCards.add(anim);

        final boolean[] finished = {false}; // final масив для лямбди

        Timer timer = new Timer(15, null);
        timer.addActionListener(e -> {
            if (!finished[0] && anim.moveStep()) {
                animCards.remove(anim);
                finished[0] = true;   // змінюємо елемент final масиву
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCards((Graphics2D) g);
    }

    private void drawCards(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cardW = 90, cardH = 130, gap = 30;

        // Статичні карти
        for (int i = 0; i < cards.size(); i++) {
            int finalI = i;
            boolean isAnimating = animCards.stream().anyMatch(a -> a.card == cards.get(finalI));
            if (isAnimating) continue;

            int x = 20 + i * gap;
            int y = (getHeight() - cardH) / 2;

            if (isDealerPanel && !revealAll) drawBack(g2, x, y, cardW, cardH);
            else drawCard(g2, cards.get(i), x, y, cardW, cardH);
        }

        // Анімовані карти
        for (AnimatedCard anim : animCards) {
            if (isDealerPanel && !revealAll) drawBack(g2, anim.x, anim.y, cardW, cardH);
            else drawCard(g2, anim.card, anim.x, anim.y, cardW, cardH);
        }
    }

    private void drawBack(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(20, 20, 120));
        g2.fillRoundRect(x, y, w, h, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, w, h, 15, 15);

        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.drawString("★", x + w / 2 - 7, y + h / 2 + 7);
    }

    private void drawCard(Graphics2D g2, Card card, int x, int y, int w, int h) {
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, w, h, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, w, h, 15, 15);

        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(card.getRank(), x + 10, y + 25);
        g2.drawString(card.getSuit(), x + 10, y + 50);

        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        String center = card.getRank();
        FontMetrics fm = g2.getFontMetrics();
        int cw = fm.stringWidth(center);
        g2.drawString(center, x + (w - cw) / 2, y + h / 2 + 12);
    }

    // Клас для анімації карти
    private static class AnimatedCard {
        final Card card;
        int x, y;
        final int targetX, targetY;

        AnimatedCard(Card card, int startX, int startY, int targetX, int targetY) {
            this.card = card;
            this.x = startX;
            this.y = startY;
            this.targetX = targetX;
            this.targetY = targetY;
        }

        boolean moveStep() {
            int dx = (targetX - x) / 5;
            int dy = (targetY - y) / 5;
            if (Math.abs(dx) < 1) dx = targetX - x;
            if (Math.abs(dy) < 1) dy = targetY - y;
            x += dx;
            y += dy;
            return x == targetX && y == targetY;
        }
    }
}
