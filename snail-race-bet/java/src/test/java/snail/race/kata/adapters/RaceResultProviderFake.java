package snail.race.kata.adapters;

import snail.race.kata.domain.RaceResultProvider;

import java.util.ArrayList;
import java.util.List;

public class RaceResultProviderFake implements RaceResultProvider {

    private final List<SnailRace> races = new ArrayList<>();

    public void add(SnailRace race) {
        races.add(race);
    }

    @Override
    public SnailRaces races() {
        return new SnailRaces(races);
    }
}
