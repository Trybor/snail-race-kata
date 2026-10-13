package snail.race.kata.adapters;

import snail.race.kata.domain.Bet;
import snail.race.kata.domain.BetRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * In memory repository for fast tests without a db
 */
public class BetRepositoryFake implements BetRepository {

    private final List<Bet> bets = new ArrayList<>();

    @Override
    public void register(Bet bet) {
        bets.add(bet);
    }

    @Override
    public List<Bet> findByDateRange(long from, long to) {
        return bets.stream()
                   .filter(bet -> bet.timestamp() >= from && bet.timestamp() < to)
                   .toList();
    }
}
