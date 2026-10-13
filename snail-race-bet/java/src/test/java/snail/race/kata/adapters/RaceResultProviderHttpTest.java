package snail.race.kata.adapters;

import org.junit.jupiter.api.Test;
import snail.race.kata.adapters.RaceResultProviderHttp.ApiRace;
import snail.race.kata.adapters.RaceResultProviderHttp.ApiRaces;
import snail.race.kata.adapters.RaceResultProviderHttp.ApiSnail;
import snail.race.kata.domain.RaceResultProvider;
import snail.race.kata.domain.RaceResultProviderContract;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RaceResultProviderHttpTest extends RaceResultProviderContract {

    @Override
    protected RaceResultProvider createProvider() {
        return new RaceResultProviderHttp();
    }

    @Test
    void podium_is_three_fastest_snails() {
        var api = new ApiRaces(List.of(new ApiRace(1, 1000L, List.of(
                new ApiSnail(10, "Slow", 50.0),
                new ApiSnail(1, "Fast", 5.0),
                new ApiSnail(3, "Third", 15.0),
                new ApiSnail(2, "Second", 10.0)
        ))));

        var podium = RaceResultProviderHttp.toDomain(api).races().get(0).podium();

        assertThat(podium.first().number()).isEqualTo(1);
        assertThat(podium.second().number()).isEqualTo(2);
        assertThat(podium.third().number()).isEqualTo(3);
    }
}
