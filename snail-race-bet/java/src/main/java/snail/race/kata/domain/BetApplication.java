package snail.race.kata.domain;

import java.util.Comparator;
import java.util.List;

import snail.race.kata.domain.RaceResultProvider.SnailRace;

/** Application timestamps are expressed in epoch milliseconds. */
public class BetApplication {
    private static final long MINIMUM_BET_LEAD_TIME_MILLIS = 3_000L;

    private final BetRepository betRepository;
    private final RaceResultProvider raceResultProvider;

    BetApplication(
            BetRepository betRepository,
            RaceResultProvider raceResultProvider) {
        this.betRepository = betRepository;
        this.raceResultProvider = raceResultProvider;
    }

    void placeBet(
             String gambler,
             long timestamp,
             int first,
             int second,
             int third) {
        betRepository.register(new Bet(
                gambler, new PodiumPronostic(first, second, third), timestamp));
    }

    List<Winner> getWinnersForLastRace() {
        var races = raceResultProvider.races().races().stream()
                .sorted(Comparator.comparingLong(SnailRace::timestamp).reversed())
                .toList();
        if (races.isEmpty()) {
            return List.of();
        }

        var lastRace = races.get(0);
        long from = races.size() > 1 ? races.get(1).timestamp() + 1 : Long.MIN_VALUE;
        long to = lastRace.timestamp() - MINIMUM_BET_LEAD_TIME_MILLIS + 1;
        if (from >= to) {
            return List.of();
        }

        var podium = lastRace.podium();
        var winningPronostic = new PodiumPronostic(
                podium.first().number(), podium.second().number(), podium.third().number());
        return betRepository.findByDateRange(from, to).stream()
                .filter(bet -> bet.pronostic().equals(winningPronostic))
                .map(bet -> new Winner(bet.gambler()))
                .toList();
    }
}
