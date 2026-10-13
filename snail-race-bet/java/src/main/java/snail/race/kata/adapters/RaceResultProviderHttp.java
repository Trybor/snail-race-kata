package snail.race.kata.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import snail.race.kata.domain.RaceResultProvider;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class RaceResultProviderHttp implements RaceResultProvider {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public SnailRaces races() {
        try {
            Request request = new Request.Builder() 
                    .url("http://localhost:8000/results")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                ApiRaces api = mapper.readValue(response.body().string(), ApiRaces.class);
                return toDomain(api);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static SnailRaces toDomain(ApiRaces api) {
        return new SnailRaces(api.races().stream().map(race -> {
            List<Snail> podium = race.snails().stream()
                    .sorted(Comparator.comparingDouble(ApiSnail::duration))
                    .limit(3)
                    .map(s -> new Snail(s.number(), s.name()))
                    .toList();
            return new SnailRace(
                    race.raceId(),
                    race.timestamp(),
                    new Podium(podium.get(0), podium.get(1), podium.get(2))
            );
        }).toList());
    }

    record ApiRaces(List<ApiRace> races) {
    }

    record ApiRace(int raceId, long timestamp, List<ApiSnail> snails) {
    }

    record ApiSnail(int number, String name, double duration) {
    }
}
