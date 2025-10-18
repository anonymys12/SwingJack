package example.swingjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BlackjackGUI extends JFrame {
    private Deck deck;
    private final List<Card> playerCards = new ArrayList<>();
    private final List<Card> dealerCards = new ArrayList<>();

    private final JButton hitBtn = createButton("Hit", new Color(220, 20, 60));
    private final JButton standBtn = createButton("Stand", new Color(30, 144, 255));
    private final JButton newBtn = createButton("New Game", new Color(34, 139, 34));

    private final CardPanel playerPanel = new CardPanel(playerCards, false);
    private final CardPanel dealerPanel = new CardPanel(dealerCards, true);

    private final JLabel statusLabel = new JLabel("Welcome to SwingJack");

    public BlackjackGUI() {
        super("SwingJack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        center.add(dealerPanel);
        center.add(playerPanel);
        add(center, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.add(hitBtn);
        controls.add(standBtn);
        controls.add(newBtn);
        controls.setBackground(new Color(0, 70, 0));
        add(controls, BorderLayout.SOUTH);

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(50, 50, 50));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.NORTH);

        hitBtn.addActionListener(e -> onHit());
        standBtn.addActionListener(e -> onStand());
        newBtn.addActionListener(e -> startGame());

        startGame();
    }

    private JButton createButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            private boolean pressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color top = pressed ? baseColor.darker() : baseColor.brighter();
                Color bottom = pressed ? baseColor.darker().darker() : baseColor.darker();

                GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(baseColor.darker().darker());
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }

            @Override
            public boolean isContentAreaFilled() { return false; }

            @Override
            public boolean isFocusPainted() { return false; }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                super.processMouseEvent(e);
                if (e.getID() == MouseEvent.MOUSE_PRESSED) pressed = true;
                if (e.getID() == MouseEvent.MOUSE_RELEASED) pressed = false;
                repaint();
            }
        };

        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        return btn;
    }

    private void startGame() {
        deck = new Deck();
        deck.shuffle();
        playerCards.clear();
        dealerCards.clear();

        dealerPanel.hideDealerCards();

        final int[] index = {0};

        // Порядок роздачі
        final Card[] dealOrder = {deck.draw(), deck.draw(), deck.draw(), deck.draw()};

        Timer timer = new Timer(400, null);
        timer.addActionListener(e -> {
            int i = index[0]++;
            if (i >= dealOrder.length) {
                ((Timer)e.getSource()).stop();
                statusLabel.setText("Your move");
                hitBtn.setEnabled(true);
                standBtn.setEnabled(true);
                repaintPanels();
                return;
            }

            Card c = dealOrder[i];
            if (i % 2 == 0) {
                playerCards.add(c);
                playerPanel.animateCard(c, playerCards.size() - 1);
            } else {
                dealerCards.add(c);
                dealerPanel.animateCard(c, dealerCards.size() - 1);
            }
        });
        timer.start();

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
    }

    private void onHit() {
        Card c = deck.draw();
        playerCards.add(c);
        playerPanel.animateCard(c, playerCards.size() - 1);

        int val = calculateBest(playerCards);
        if (val > 21) {
            statusLabel.setText("You busted: " + val);
            endRound();
        } else if (val == 21) {
            statusLabel.setText("You have 21!");
            endRound();
        } else {
            statusLabel.setText("Your sum: " + val);
        }
    }

    private void onStand() {
        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);

        dealerPanel.revealDealerCards();

        Timer dealerTimer = new Timer(400, null);
        dealerTimer.addActionListener(e -> {
            int val = calculateBest(dealerCards);
            if (val < 17) {
                Card c = deck.draw();
                dealerCards.add(c);
                dealerPanel.animateCard(c, dealerCards.size() - 1);
            } else {
                ((Timer)e.getSource()).stop();
                endRound();
            }
        });
        dealerTimer.start();
    }

    private void endRound() {
        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        dealerPanel.revealDealerCards();

        int player = calculateBest(playerCards);
        int dealer = calculateBest(dealerCards);

        String result;
        if (player > 21) result = "You busted. Dealer wins (" + dealer + ")";
        else if (dealer > 21) result = "Dealer busted. You win (" + player + ")";
        else if (player > dealer) result = "You win: " + player + " vs " + dealer;
        else if (player < dealer) result = "Dealer wins: " + dealer + " vs " + player;
        else result = "Push: both " + player;

        statusLabel.setText(result);
        repaintPanels();
    }

    private void repaintPanels() {
        dealerPanel.repaint();
        playerPanel.repaint();
    }

    private int calculateBest(List<Card> cards) {
        int sum = 0;
        int aces = 0;
        for (Card c : cards) {
            sum += c.baseValue();
            if ("A".equals(c.getRank())) aces++;
        }
        for (int i = 0; i < aces; i++) if (sum + 10 <= 21) sum += 10;
        return sum;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlackjackGUI gui = new BlackjackGUI();
            gui.setVisible(true);
        });
    }
}
