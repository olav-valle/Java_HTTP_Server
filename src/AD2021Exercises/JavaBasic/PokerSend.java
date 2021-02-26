package AD2021Exercises.JavaBasic;



import java.util.*;

public class PokerSend {
    public static void main(String[] args) {
        //1. Generate poker
        //1.1 define a map, key: number of cards: value: card.
        // principle: number is smaller, card is smaller
        Map<Integer, String> pokers = new HashMap<>();
        //1.2 define a list, to store number of all cards.
        List<Integer> list = new ArrayList<>();
        //1.3 buy poker
        // regular card
        String[] pokersuits = {"♠", "♥", "♣", "♦"};
        String[] pokernumbers = {"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};
        int poker_num = 0;
        //create all the cards
        for (String pokernumber : pokernumbers) {
            for (String pokersuit : pokersuits) {
                String pokercards = pokersuit + pokernumber;
                //poker number and card are in the map
                pokers.put(poker_num, pokercards);
                //poker number is in the list
                list.add(poker_num);
                poker_num++;
            }
        }
        // jokers

        pokers.put(poker_num, "Black Joker");
        list.add(poker_num++);
        pokers.put(poker_num, "Red Joker");
        list.add(poker_num);

        // print all the cards
        System.out.println("All the cards: " + pokers);
        System.out.println("All the numbers: " + list);
        System.out.println("-----------------------");
        //2. Shuffle the cards
        Collections.shuffle(list);
        System.out.println("Numbers after shuffle: " + list);

        System.out.println("-----------------------");
        //3. Distribute the cards
        //3.1 Define 4 sets, for 3 players and 1 for left over
        List<Integer> playerA = new ArrayList<>();
        List<Integer> playerB = new ArrayList<>();
        List<Integer> playerC = new ArrayList<>();
        List<Integer> leftover = new ArrayList<>();

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
        System.out.println("Cards of Player A: " + printPoker(playerA, pokers));
        System.out.println("Cards of Player B: " + printPoker(playerB, pokers));
        System.out.println("Cards of Player C: " + printPoker(playerC, pokers));
        System.out.println("Cards of Left Over: " + printPoker(leftover, pokers));

    }

    public static String printPoker(List<Integer> poker_nums, Map<Integer, String> poker_cards) {
        //1. sort number
        Collections.sort(poker_nums);
        //2. iterate list, get all the numbers
        StringBuilder sb = new StringBuilder()  ;

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
