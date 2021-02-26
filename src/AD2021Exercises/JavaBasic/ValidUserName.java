package AD2021Exercises.JavaBasic;

import java.util.Scanner;

/*
You are updating the username policy on your company's internal networking platform.
According to the policy, a username is considered valid if all the following constraints
are satisfied:

The username consists of 8 to 30 characters inclusive. If the username consists of less
than 8 or greater than 30 characters, then it is an invalid username.
The username can only contain alphanumeric characters and underscores (_).
Alphanumeric characters describe the character set consisting of lowercase characters [a-z],
uppercase characters [A-Z], and digits [0-9].
The first character of the username must be an alphabetic character, i.e., either
lowercase character [a-z] or uppercase character [A-Z].
 */
public class ValidUserName {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter user name: ");


        int num = sc.nextInt();
        if (num > 100 || num <= 0) {
            System.out.println("Wrong Number of User Names!");
        } else {
            sc.nextLine();
            for (int i = 0; i < num; i++) {

                String user_name = sc.nextLine();
                if (user_name.length() < 8 || user_name.length() > 30) {
                    outputInvalid();
                } else if (!user_name.matches("[a-zA-Z0-9_]+")) {
                    outputInvalid();
                } else if (!user_name.matches("^[a-zA-Z].*$")) {
                    outputInvalid();
                } else {
                    outputValid();
                }
            }
        }
        sc.close();
    }

    public static void outputInvalid() {
        System.out.println("Invalid");
    }

    public static void outputValid() {
        System.out.println("Valid");
    }
}
