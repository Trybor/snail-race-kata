package snail.race.kata.adapters;

import snail.race.kata.domain.RaceResultProvider;
import snail.race.kata.domain.RaceResultProviderContract;

public class RaceResultProviderFakeTest extends RaceResultProviderContract {

    @Override
    protected RaceResultProvider createProvider() {
        var fake = new RaceResultProviderFake();
        fake.add(new RaceResultProvider.SnailRace(
                1,
                1000L,
                new RaceResultProvider.Podium(
                        new RaceResultProvider.Snail(1, "A"),
                        new RaceResultProvider.Snail(2, "B"),
                        new RaceResultProvider.Snail(3, "C")
                )
        ));
        return fake;
    }
}
