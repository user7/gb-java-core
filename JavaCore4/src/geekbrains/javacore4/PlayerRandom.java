package geekbrains.javacore4;

import java.util.ArrayList;
import java.util.Random;

class PlayerRandom implements StonesGamePlayer {
    @Override
    public void makeMove(StonesGame game) {
        ArrayList<int[]> possibleMoves = new ArrayList<>();
        for (int x = 1; x <= game.getMaxX(); ++x)
            for (int y = 1; y <= game.getMaxY(); ++y)
                if (game.at(x, y) == 0)
                    possibleMoves.add(new int[]{x, y});
        if (possibleMoves.size() == 0)
            throw new IllegalArgumentException("не осталось возможных ходов");
        Random r = new Random();
        int[] at = possibleMoves.get(r.nextInt(possibleMoves.size()));
        game.move(at[0], at[1]);
    }
}