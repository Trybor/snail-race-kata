package snail.race.kata.domain;

import org.junit.jupiter.api.Test;
import snail.race.kata.adapters.BetRepositoryFake;
import snail.race.kata.adapters.RaceResultProviderFake;

import static org.assertj.core.api.Assertions.assertThat;

class BetApplicationTest {
    private static final long PREVIOUS_RACE = 10_000L;
    private static final long LAST_RACE = 20_000L;

    @Test
    void registers_a_bet_with_all_supplied_fields() {
        var repository = new BetRepositoryFake();
        var application = application(repository, raceProvider());
        application.placeBet("Alice", 4_000L, 3, 1, 2);
        assertThat(repository.findByDateRange(0L, 5_000L))
                .containsExactly(new Bet("Alice", new PodiumPronostic(3, 1, 2), 4_000L));
    }

    @Test
    void has_no_winners_when_there_are_no_races() {
        var application = application(new BetRepositoryFake(), raceProvider());
        assertThat(application.getWinnersForLastRace()).isEmpty();
    }

    @Test
    void returns_only_bets_with_an_exact_ordered_podium_for_the_last_race() {
        var repository = new BetRepositoryFake();
        var application = application(repository,
                raceProvider(race(PREVIOUS_RACE, 1, 2, 3), race(LAST_RACE, 3, 1, 2)));
        application.placeBet("match", 16_000L, 3, 1, 2);
        application.placeBet("wrong-podium", 16_000L, 1, 2, 3);
        application.placeBet("wrong-order", 16_000L, 3, 2, 1);
        assertThat(application.getWinnersForLastRace()).containsExactly(new Winner("match"));
    }

    @Test
    void applies_the_three_second_registration_boundary() {
        var application = application(new BetRepositoryFake(),
                raceProvider(race(PREVIOUS_RACE, 1, 2, 3), race(LAST_RACE, 1, 2, 3)));
        application.placeBet("one-millisecond-before", LAST_RACE - 3_001L, 1, 2, 3);
        application.placeBet("exact-cutoff", LAST_RACE - 3_000L, 1, 2, 3);
        application.placeBet("one-millisecond-late", LAST_RACE - 2_999L, 1, 2, 3);
        assertThat(application.getWinnersForLastRace()).containsExactly(
                new Winner("one-millisecond-before"), new Winner("exact-cutoff"));
    }

    @Test
    void considers_only_bets_registered_after_the_previous_race() {
        var application = application(new BetRepositoryFake(), raceProvider(
                race(LAST_RACE, 1, 2, 3), race(PREVIOUS_RACE, 9, 8, 7)));
        application.placeBet("before-previous", PREVIOUS_RACE - 1L, 1, 2, 3);
        application.placeBet("too-late-for-previous", PREVIOUS_RACE - 2_999L, 1, 2, 3);
        application.placeBet("at-previous", PREVIOUS_RACE, 1, 2, 3);
        application.placeBet("after-previous", PREVIOUS_RACE + 1L, 1, 2, 3);
        assertThat(application.getWinnersForLastRace()).containsExactly(new Winner("after-previous"));
    }

    @Test
    void allows_matching_bets_for_the_first_race_without_a_previous_race() {
        var application = application(new BetRepositoryFake(),
                raceProvider(race(LAST_RACE, 1, 2, 3)));
        application.placeBet("before-cutoff", LAST_RACE - 3_001L, 1, 2, 3);
        application.placeBet("at-cutoff", LAST_RACE - 3_000L, 1, 2, 3);
        application.placeBet("after-cutoff", LAST_RACE - 2_999L, 1, 2, 3);
        assertThat(application.getWinnersForLastRace()).containsExactly(
                new Winner("before-cutoff"), new Winner("at-cutoff"));
    }

    private static BetApplication application(BetRepositoryFake repository, RaceResultProviderFake provider) {
        return new BetApplication(repository, provider);
    }

    private static RaceResultProviderFake raceProvider(RaceResultProvider.SnailRace... races) {
        var provider = new RaceResultProviderFake();
        for (var race : races) provider.add(race);
        return provider;
    }

    private static RaceResultProvider.SnailRace race(long timestamp, int first, int second, int third) {
        return new RaceResultProvider.SnailRace((int) timestamp, timestamp,
                new RaceResultProvider.Podium(
                        new RaceResultProvider.Snail(first, "snail-" + first),
                        new RaceResultProvider.Snail(second, "snail-" + second),
                        new RaceResultProvider.Snail(third, "snail-" + third)));
    }
}
