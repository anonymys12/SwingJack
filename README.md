# SwingJack

SwingJack — це простий **Blackjack** на Java з графічним інтерфейсом Swing.  
Гравець грає проти комп’ютерного дилера. Карти анімовані, дилерські карти спочатку приховані.

## Особливості

- Анімація карт гравця і дилера.
- Дилерські карти спочатку закриті (“рубашка”).
- Плавні переходи карт при роздачі.
- Стильні градієнтні кнопки (`Hit`, `Stand`, `New Game`).
- Підтримка правил Blackjack (Dealer стоїть на 17+).

## Структура проекту

SwingJack/
├── src/
│ └── example/swingjack/
│ ├── BlackjackGUI.java
│ ├── Card.java
│ ├── CardPanel.java
│ └── Deck.java
├── resources/
│ └── cards/ ← PNG-картки (2_of_clubs.png, ace_of_hearts.png і т.д.)
└── README.md

## Запуск

1. Склонуй репозиторій:

```bash
git clone https://github.com/<твій-нік>/SwingJack.git
cd SwingJack
#Скомпілюй і запусти через командний рядок:

javac -d out src/example/swingjack/*.java
java -cp out example.swingjack.BlackjackGUI
javac -d out src/example/swingjack/*.java
java -cp out example.swingjack.BlackjackGUI

##  Як додавати карти

Картинки зберігати у resources/cards/.

Імена карт у форматі: 2_of_clubs.png, ace_of_hearts.png, jack_of_spades.png і т.д.

Клас Card автоматично підбирає картинку по масті і рангу.
                                                                    <img width="1920" height="1014" alt="image" src="https://github.com/user-attachments/assets/314c8db6-fd3f-4ee2-884a-d89f31f3215d" />

