package geekbrains;

record Track(int length) implements Obstacle {
    @Override
    public boolean interact(Traveller traveller) {
        return traveller.run(length());
    }
}
