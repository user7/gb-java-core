package geekbrains;

record Wall(int height) implements Obstacle {
    @Override
    public boolean interact(Traveller traveller) {
        return traveller.jump(height());
    }
}
