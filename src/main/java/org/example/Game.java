package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Game {
    private static final char dotHuman = 'X';
    private static final char dotAI = 'O';
    private static final char dotEmpty = '-';
    private static final char dotReserve = '+';
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static char[][] FIELD = new char[3][3];


    public void initialize()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                FIELD[i][j] = dotEmpty;
            }
        }
    }

    public void printField()
    {
        System.out.println("\n--------------------");
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                System.out.print(" | " + FIELD[i][j] + " | ");
            }
            System.out.println("\n--------------------");
        }
    }


    public void turnHuman()
    {
            try {
                int x, y;
                do {
                    System.out.println("Введите координаты X и Y (от 1 до 3) через пробел. Для сохранения игры и выхода введите '9 9'");
                    x = SCANNER.nextInt() - 1;
                    y = SCANNER.nextInt() - 1;
                    if (isGameSaved(x, y))
                    {
                        saveTheGame();
                        System.exit(1);
                    }
                }
                while (!isCellValid(x, y) || !isCellEmpty(x, y));
                {
                    FIELD[x][y] = dotHuman;
                }
            } catch (InputMismatchException e) {
                System.out.println("Неправильный ввод.");
            }
    }



    public boolean isGameSaved(int x, int y)
    {
        return x == 8 && y == 8;
    }


    public void turnAI()
    {
        int x, y;
        do {
            x = RANDOM.nextInt(3);
            y = RANDOM.nextInt(3);
        } while (!isCellEmpty(x, y));
        {
            FIELD[x][y] = dotAI;
        }

    }


    private boolean isCellEmpty(int x, int y) {
        return FIELD[x][y] == dotEmpty;
    }

    public boolean isCellValid(int x, int y)
    {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    public boolean haveWon(char symbol, String msg)
    {
        if (checkDraw())
        {
            System.out.println("Ничья!");
            return true;
        }

        for (char[] chars : FIELD) {
            if (chars[0] == symbol && chars[1] == symbol && chars[2] == symbol) {
                System.out.println(msg);
                return true;
            }
        }

        for (int col = 0; col < FIELD.length; col++)
        {
            if (FIELD[0][col] == symbol && FIELD[1][col] == symbol && FIELD[2][col] == symbol)
            {
                System.out.println(msg);
                return true;
            }
        }

        if (FIELD[0][0] == FIELD[1][1] && FIELD[2][2] == FIELD[0][0] && FIELD[0][0] == symbol)
            return true;

        return FIELD[0][2] == FIELD[1][1] && FIELD[2][0] == FIELD[0][2] && FIELD[0][2] == symbol;
    }

    public boolean checkDraw()
    {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (isCellEmpty(i, j)) return false;
            }
        }
        return true;
    }

    public void run()
    {
        while (true)
        {
            System.out.println("""
                Хотите ли вы начать новую игру или попытаться найти сохранненую?
                1. Новая игра
                2. Попытаться загрузить игру.""");

            String input = SCANNER.next();
            if (Objects.equals(input, "2"))
            {
                try {
                    int[] arr = decodeSave("save.out");
                    System.out.println(Arrays.toString(arr));
                    loadGame(arr);
                    printField();
                    break;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            if (Objects.equals(input, "1"))
            {
                initialize();
                printField();
                break;
            }
            else
            {
                System.out.println("Неверный ввод.");
            }
        }


        while (true) {
            turnHuman();
            printField();
            if (haveWon(dotHuman, "You have won!")) break;

            turnAI();
            printField();
            if (haveWon(dotAI, "You lost!")) break;
        }
    }

    public void codeSave(int[] arr) throws IOException {
        FileOutputStream fos = new FileOutputStream("save.out");
        for (int b = 0; b < 3; b++) { // write to 3 bytes
        byte wr = 0;
        for (int v = 0; v < 3; v++) { // write by 3 values in each
            wr += (byte) (arr[3 * b + v] << (v * 2));
            }
            try {
                fos.write(wr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        fos.flush();
        fos.close();

    }

    public int[] decodeSave(String fileName) throws IOException {
        int[] arr = new int[9];
        FileInputStream fis = new FileInputStream(fileName);
        int b;
        int i = 0;
        while ((b = fis.read()) != -1) {
        for (int v = 0; v < 3; ++v) { // 3 values of four possible
            arr[i++] = b >> (v * 2) & 0x3;
            }
        }
        fis.close();
        System.out.println(Arrays.toString(arr));
        return arr;
    }

    private void loadGame(int[] arr) {
        int j = 0;
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    while (j < arr.length) {

                        if (arr[j] == 1) {
                            FIELD[x][y] = 'X';
                            j++;
                            break;
                        }

                        if (arr[j] == 2) {
                            FIELD[x][y] = 'O';
                            j++;
                            break;
                        }

                        if (arr[j] == 0) {
                            FIELD[x][y] = '-';
                            j++;
                            break;
                        }

                        if (arr[j] == 3) {
                            FIELD[x][y] = '+';
                            j++;
                            break;
                        }
                    }
                }
        }
    }


    public void saveTheGame()
    {

        int[] arr = new int[9];
        int i = 0;
        for (int x = 0; x < 3; x++)
        {
            for (int y = 0; y < 3; y++)
            {
                if (FIELD[x][y] == 'X') {
                    arr[i] = 1;
                }

                if (FIELD[x][y] == 'O') {
                    arr[i] = 2;
                }

                if (FIELD[x][y] == '-') {
                    arr[i] = 0;
                }

                if (FIELD[x][y] == '+') {
                    arr[i] = 3;
                }
                i++;
            }
        }
        try {
            codeSave(arr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
