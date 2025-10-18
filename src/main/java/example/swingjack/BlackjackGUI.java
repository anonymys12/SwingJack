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

    private final JButton hitBtn = createButton("Hit", new Color(220,20,60));
    private final JButton standBtn = createButton("Stand", new Color(30,144,255));
    private final JButton newBtn = createButton("New Game", new Color(34,139,34));

    private final CardPanel playerPanel = new CardPanel(playerCards, false);
    private final CardPanel dealerPanel = new CardPanel(dealerCards, true);

    private final JLabel statusLabel = new JLabel("Welcome to SwingJack");

    public BlackjackGUI() {
        super("SwingJack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        // Панелі гравця і дилера
        JPanel center = new JPanel(new GridLayout(2,1,0,10));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        center.add(dealerPanel);
        center.add(playerPanel);
        add(center, BorderLayout.CENTER);

        // Панель кнопок
        JPanel controls = new JPanel();
        controls.add(hitBtn);
        controls.add(standBtn);
        controls.add(newBtn);
        controls.setBackground(new Color(0, 70, 0));
        add(controls, BorderLayout.SOUTH);

        // Статус гри
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(50,50,50));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        add(statusLabel, BorderLayout.NORTH);

        hitBtn.addActionListener(e -> onHit());
        standBtn.addActionListener(e -> onStand());
        newBtn.addActionListener(e -> startGame());

        startGame();
    }

    // ----------------------------------------
    // Кнопки з градієнтом і натисканням
    // ----------------------------------------
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
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

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
                if (e.getID() == MouseEvent.MOUSE_PRESSED) pressed = true;
                else if (e.getID() == MouseEvent.MOUSE_RELEASED || e.getID() == MouseEvent.MOUSE_EXITED) pressed = false;
                repaint();
                super.processMouseEvent(e);
            }
        };

        btn.setPreferredSize(new Dimension(140, 50));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
        });

        return btn;
    }

    // ----------------------------------------
    // Початок гри з анімацією
    // ----------------------------------------
    private void startGame() {
        deck = new Deck();
        deck.shuffle();
        playerCards.clear();
        dealerCards.clear();

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        statusLabel.setText("Dealing...");

        Card[] dealOrder = new Card[4];
        for (int i = 0; i < 4; i++) dealOrder[i] = deck.draw();

        Timer timer = new Timer(400, null);
        final int[] index = {0};
        timer.addActionListener(e -> {
            if (index[0] == 0 || index[0] == 2) playerCards.add(dealOrder[index[0]]);
            else dealerCards.add(dealOrder[index[0]]);
            playerPanel.repaint();
            dealerPanel.repaint();
            index[0]++;
            if (index[0] >= dealOrder.length) {
                ((Timer)e.getSource()).stop();
                hitBtn.setEnabled(true);
                standBtn.setEnabled(true);
                int val = calculateBest(playerCards);
                statusLabel.setText(val == 21 ? "Blackjack!" : "Your move");
            }
        });
        timer.start();
    }

    // ----------------------------------------
    // Дії гравця
    // ----------------------------------------
    private void onHit() {
        playerCards.add(deck.draw());
        playerPanel.repaint();
        int val = calculateBest(playerCards);
        if (val > 21) {
            statusLabel.setText("You busted: " + val);
            dealerPanel.revealDealerCards();
            hitBtn.setEnabled(false);
            standBtn.setEnabled(false);
            return;
        } else if (val == 21) {
            statusLabel.setText("You have 21!");
            onStand();
            return;
        } else {
            statusLabel.setText("Your sum: " + val);
        }
    }

    private void onStand() {
        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);

        // Дилер тягне карти автоматично
        while (calculateBest(dealerCards) < 17) {
            dealerCards.add(deck.draw());
        }

        dealerPanel.revealDealerCards(); // відкриваємо карти дилера

        int dealerVal = calculateBest(dealerCards);
        int playerVal = calculateBest(playerCards);

        // Перевірка перебору
        String result;
        if (playerVal > 21) result = "You busted. Dealer wins (" + dealerVal + ")";
        else if (dealerVal > 21) result = "Dealer busted. You win (" + playerVal + ")";
        else if (playerVal > dealerVal) result = "You win: " + playerVal + " vs " + dealerVal;
        else if (playerVal < dealerVal) result = "Dealer wins: " + dealerVal + " vs " + playerVal;
        else result = "Push: both " + playerVal;

        statusLabel.setText(result);
        playerPanel.repaint();
        dealerPanel.repaint();
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
