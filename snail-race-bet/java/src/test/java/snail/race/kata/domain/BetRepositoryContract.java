package snail.race.kata.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract every BetRepository implementation has to fulfil
 * Subclasses only decide which implementation is tested
 */
public abstract class BetRepositoryContract {

    private BetRepository repository;

    /**
     * Create an empty repository for each test
     */
    protected abstract BetRepository createRepository();

    @BeforeEach
    void setUpRepository() {
        repository = createRepository();
    }

    @Test
    void register_a_bets() {
        // given
        Bet bet = new Bet("Alice", new PodiumPronostic(1, 2, 3), 42L);

        // when
        repository.register(bet);

        // then
        assertThat(repository.findByDateRange(0L, 100L)).containsExactly(bet);
    }

    @Test
    void retrieve_only_bets_inside_the_time_range() {
        // given
        Bet beforeRange = new Bet("before", new PodiumPronostic(1, 2, 3), 999L);
        Bet atStart = new Bet("atStart", new PodiumPronostic(1, 2, 3), 1_000L);
        Bet inside = new Bet("inside", new PodiumPronostic(1, 2, 3), 1_500L);
        Bet atEnd = new Bet("atEnd", new PodiumPronostic(1, 2, 3), 2_000L);

        repository.register(beforeRange);
        repository.register(atStart);
        repository.register(inside);
        repository.register(atEnd);

        // when
        List<Bet> found = repository.findByDateRange(1_000L, 2_000L);

        // then: from is included, to is excluded -> from <= timestamp < to
        assertThat(found).hasSize(2).containsOnly(atStart, inside);
    }
}
