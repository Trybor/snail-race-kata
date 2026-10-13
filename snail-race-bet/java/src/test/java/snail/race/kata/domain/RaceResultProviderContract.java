package snail.race.kata.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class RaceResultProviderContract {

    protected abstract RaceResultProvider createProvider();

    @Test
    void provides_races_with_podium() {
        var race = createProvider().races().races().get(0);

        assertThat(race.raceId()).isNotZero();
        assertThat(race.timestamp()).isNotZero();
        assertThat(race.podium()).isNotNull();
    }
}
