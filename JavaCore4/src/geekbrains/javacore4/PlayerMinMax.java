package geekbrains.javacore4;

public class PlayerMinMax implements Player {

    // score: -1 поражение, 0 ничья, 1 победа
    private record Move(int x, int y, int score) {}

    @Override
    public void makeMove(Game game) {
        Move m = bestMove(game);
        game.move(m.x, m.y);
    }

    private Move bestMove(Game game) {
        Move m = null;
        for (int x = 1; x <= game.getMaxX(); ++x) {
            for (int y = 1; y <= game.getMaxY(); ++y) {
                if (!game.move(x, y))
                    continue;
                int score;
                if (game.isDraw())
                    score = 0;
                else if (game.isGameOver())
                    score = 1; // был наш ход, мы выиграли
                else {
                    Move m2 = bestMove(game); // лучший ответ оппонента
                    score = -m2.score;
                }
                game.undoMove(x, y);
                if (m == null || score > m.score) {
                    m = new Move(x, y, score);
                    if (score == 1)
                        return m;
                }
            }
        }
        return m;
    }
}
