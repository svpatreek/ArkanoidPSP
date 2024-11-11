import java.util.Scanner;
import java.util.Random;

class Ball {
    int x, y;
    int dx, dy;
    boolean isMoving;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        this.dx = 0;
        this.dy = 0;
        this.isMoving = false;
    }

    public void startMoving() {
        Random random = new Random();
        dx = random.nextBoolean() ? 1 : -1;
        dy = -1;
        isMoving = true;
    }

    public void moveBall(int maxWidth, int maxHeight) {
        if (!isMoving) return;

        x += dx;
        y += dy;

        if (x <= 0 || x >= maxWidth - 1) {
            dx *= -1;
        }

        if (y <= 0) {
            dy *= -1;
        }
    }

    public void bounceIfHitPaddle(Paddle paddle) {
        if (y == paddle.y && x >= paddle.x && x <= paddle.x + paddle.width) {
            dy *= -1;
        }
    }

    public boolean hitBlock(Block block) {
        return y == block.y && x == block.x;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Block {
    int x, y;
    boolean isVisible;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.isVisible = true;
    }
}

class Paddle {
    int x, y;
    int width = 5;

    public Paddle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void movePaddle(int dx, int maxWidth) {
        x += dx;
        if (x < 0) x = 0;
        if (x + width > maxWidth) x = maxWidth - width;
    }
}

public class Main {
    private final int WIDTH = 20;
    private final int HEIGHT = 10;
    private final Ball ball;
    private final Paddle paddle;
    private final Block[] blocks;
    private boolean isGameOver;

    public Main() {
        paddle = new Paddle(WIDTH / 2 - 2, HEIGHT - 1);
        ball = new Ball(paddle.x + paddle.width / 2, paddle.y - 1);

        blocks = new Block[9];
        Random rand = new Random();

        int index = 0;
        while (index < blocks.length) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT / 2);

            if (y < paddle.y - 2 && !isBlockAtPosition(x, y)) {
                blocks[index++] = new Block(x, y);
            }
        }

        isGameOver = false;
    }

    private boolean isBlockAtPosition(int x, int y) {
        for (Block block : blocks) {
            if (block != null && block.x == x && block.y == y) {
                return true;
            }
        }
        return false;
    }

    private void displayGame() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (i == ball.y && j == ball.x) {
                    System.out.print("O");
                } else if (i == paddle.y && j >= paddle.x && j < paddle.x + paddle.width) {
                    System.out.print("=");
                } else {
                    boolean blockPrinted = false;
                    for (Block block : blocks) {
                        if (block != null && block.isVisible && block.x == j && block.y == i) {
                            System.out.print("#");
                            blockPrinted = true;
                            break;
                        }
                    }
                    if (!blockPrinted) {
                        System.out.print(".");
                    }
                }
            }
            System.out.println();
        }
    }

    private void updateGame() {
        ball.moveBall(WIDTH, HEIGHT);
        ball.bounceIfHitPaddle(paddle);

        for (Block block : blocks) {
            if (block != null && block.isVisible && ball.hitBlock(block)) {
                block.isVisible = false;
                ball.dy *= -1;
            }
        }

        isGameOver = true;
        for (Block block : blocks) {
            if (block != null && block.isVisible) {
                isGameOver = false;
                break;
            }
        }

        if (ball.y >= HEIGHT) {
            isGameOver = true;
        }
    }

    private void startGame() {
        Scanner scanner = new Scanner(System.in);
        boolean ballLaunched = false;

        while (!isGameOver) {
            displayGame();

            if (ballLaunched) {
                System.out.println("Perkelkite irklą (a - kairėn, d - dešinėn):");
            } else {
                System.out.println("Perkelkite irklą (a - kairėn, d - dešinėn, p - paleisti kamuoliuką):");
            }

            String input = scanner.nextLine();

            if (input.equals("a")) {
                paddle.movePaddle(-1, WIDTH);
                if (!ball.isMoving) {
                    ball.setPosition(paddle.x + paddle.width / 2, paddle.y - 1);
                }
            } else if (input.equals("d")) {
                paddle.movePaddle(1, WIDTH);
                if (!ball.isMoving) {
                    ball.setPosition(paddle.x + paddle.width / 2, paddle.y - 1);
                }
            } else if (input.equals("p") && !ballLaunched) {
                ball.startMoving();
                ballLaunched = true;
            }
            updateGame();
            System.out.println();
        }
        System.out.println("Žaidimas baigtas!");

        System.out.println("Pasirinkite: (1) Žaisti vėl, (2) Išeiti");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            Main newGame = new Main();
            newGame.startGame();
        } else {
            System.out.println("Ačiū už žaidimą!");
        }
    }

    public static void main(String[] args) {
        Main game = new Main();
        game.startGame();
    }
}