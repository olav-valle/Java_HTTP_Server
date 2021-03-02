package AD2021Exercises.HTTPServer;


import java.util.*;

public class PokerSend {

    // Player card hands
    private static List<Integer> playerA;
    private static List<Integer> playerB;
    private static List<Integer> playerC;
    // Cards left in deck
    private static List<Integer> leftover;
    // Deck definition
    private static Map<Integer, String> cards;

    public PokerSend(){

        //1. Generate poker
        //1.1 define a map, key: ranking of card: value: card face.
        // principle: number is smaller, card is smaller
        cards = new HashMap<>();
        //1.2 define a list, to store number of all cards.
        List<Integer> list = new ArrayList<>();
        //1.3 buy poker
        // regular card
        String[] cardSuits = {"♠", "♥", "♣", "♦"};
        String[] cardNumbers = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};
        int cardRank = 0;
        //create all the cards
        for (String cardNumber : cardNumbers) {
            for (String cardSuit : cardSuits) {
                String card = cardSuit + cardNumber;
                //poker number and card are in the map
                cards.put(cardRank, card);
                //poker number is in the list
                list.add(cardRank);
                cardRank++;
            }
        }
        // jokers

        cards.put(cardRank, "Black Joker");
        list.add(cardRank++);
        cards.put(cardRank, "Red Joker");
        list.add(cardRank);

        // print all the cards
        System.out.println("All the cards: " + cards);
        System.out.println("All the numbers: " + list);
        System.out.println("-----------------------");
        //2. Shuffle the cards
        Collections.shuffle(list);
        System.out.println("Numbers after shuffle: " + list);

        System.out.println("-----------------------");
        //3. Distribute the cards
        //3.1 Define 4 sets, for 3 players and 1 for left over
        playerA = new ArrayList<>();
        playerB = new ArrayList<>();
        playerC = new ArrayList<>();
        leftover = new ArrayList<>();

        //3.2 Modulus Operation
        for (int i = 0; i < list.size(); i++) {
            Integer num = list.get(i);
            if (i >= list.size() - 3) {
                leftover.add(num);
            } else if (i % 3 == 0) {
                playerA.add(num);
            } else if (i % 3 == 1) {
                playerB.add(num);
            } else if (i % 3 == 2) {
                playerC.add(num);
            }

        }
        System.out.println("PlayerA num: " + playerA);
        System.out.println("PlayerB num: " + playerB);
        System.out.println("PlayerC num: " + playerC);
        System.out.println("Leftover: " + leftover);

        System.out.println("-----------------------");
        // 4. Look at cards
        System.out.println("Cards of Player A: " + printPoker(playerA, cards));
        System.out.println("Cards of Player B: " + printPoker(playerB, cards));
        System.out.println("Cards of Player C: " + printPoker(playerC, cards));
        System.out.println("Cards of Left Over: " + printPoker(leftover, cards));

    }

    public static void main(String player) {
       new PokerSend();
    }

    public String getPlayerhand(String playerName) {
        List<Integer> player;
        switch (playerName.toLowerCase().strip()) {
            case ("playera"):
                player = playerA;
                break;
            case ("playerb"):
                player = playerB;
                break;
            case ("playerc"):
                player = playerC;
                break;
            default:
                player = new ArrayList<>();
        }

        return printPoker(player, cards);
    }

    public static String getHand(){
        return null;
    }

    public static String printPoker(List<Integer> poker_nums, Map<Integer, String> poker_cards) {
        //1. sort number
        Collections.sort(poker_nums);
        //2. iterate list, get all the numbers
        StringBuilder sb = new StringBuilder();

        for (Integer poker_num : poker_nums) {
            String poker = poker_cards.get(poker_num);
            //4. concat string
            sb.append(poker + " ");
        }

        String str = sb.toString();

        //5. return string
        return str.trim();
    }
}
