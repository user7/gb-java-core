package geekbrains.javacore4;

class PlayerMinMax implements Player {
    @Override
    public void makeMove(Game game) {
        minimax(true, game.getCurrentPlayer(), game);
    }

    record Move(int x, int y) {}

    private int minimax(boolean needMakeMove, int player, Game game) {
        if (game.isGameOver())
            return game.isDraw() ? 0 : game.getWinnerPlayer() == player ? 1 : -1;

        Move[] lossDrawWin = new Move[3]; // храним найденные ходы, 0 - проигрышный, 1 - ничейный, 2 - победный
        boolean weGo = player == game.getCurrentPlayer();
        for (int x = 1; x <= game.getMaxX(); ++x) {
            for (int y = 1; y <= game.getMaxY(); ++y) {
                if (!game.move(x, y))
                    continue;
                int score = minimax(false, player, game);

                if (lossDrawWin[score + 1] == null)
                    lossDrawWin[score + 1] = new Move(x, y);
                game.undoMove(x, y);

                // наш ход и найден выигрышный ход, дальше искать нет смысла
                if (weGo && lossDrawWin[2] != null)
                    break;

                // ход оппонента и найден проигрышный ход, дальше анализировать бесполезно
                if (!weGo && lossDrawWin[0] != null)
                    break;
            }
        }

        if (needMakeMove) {
            for (int score = 1; score >= -1; --score) {
                Move m = lossDrawWin[score + 1];
                if (m != null) {
                    game.move(m.x, m.y);
                    return score; // needMakeMove = true только в наш ход, значит лучший найденый ход задает score
                }
            }
            assert (false); // не может чтобы не было ни победного, ни ничейного, ни проигрышного хода
        }

        if (weGo) {
            if (lossDrawWin[2] != null)
                return 1;
            else if (lossDrawWin[1] != null)
                return 0;
            else
                return -1;
        } else {
            if (lossDrawWin[0] != null)
                return -1;
            else if (lossDrawWin[1] != null)
                return 0;
            else
                return 1;
        }
    }
}
