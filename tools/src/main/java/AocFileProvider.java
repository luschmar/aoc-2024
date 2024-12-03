import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

public class AocFileProvider implements ArgumentsProvider, AnnotationConsumer<AocFileSource> {
	private Map<Stream<String>, String> resourceStreamWithExpected;
	private final Pattern AOC_URL_MATCHER = Pattern.compile("https://adventofcode.com/(\\d{4})/day/(\\d{1,2})/input");

	@Override
	public void accept(AocFileSource aocFileSource) {
		this.resourceStreamWithExpected = Arrays.stream(aocFileSource.inputs()).collect(Collectors.toMap(k -> loadFileAsStream(k.input()), AocInputMapping::expected));
	}

	private Stream<String> loadFileAsStream(String resourceName) {
		var m = AOC_URL_MATCHER.matcher(resourceName);
		if (m.find()) {
			var aocDay = LocalDate.of(parseInt(m.group(1)), 12, parseInt(m.group(2)));
			if(aocDay.isAfter(LocalDate.now())) {
				return Stream.of();
			}

			var localCache = getClass().getClassLoader().getResourceAsStream("input.txt.cache");
			if (localCache != null) {
				return loadStreamAsLines(localCache);
			}
			try {
				var request = HttpRequest.newBuilder().uri(new URI(resourceName)).headers("Cookie", System.getenv("AOC_SESSION_COOKIE")).GET().build();
				var response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("src/test/resources/input.txt.cache")));
				return loadStreamAsLines(new FileInputStream(response.body().toFile()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return loadStreamAsLines(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName)));
	}

	private Stream<String> loadStreamAsLines(InputStream stream) {
		return new BufferedReader(new InputStreamReader(stream)).lines();
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return resourceStreamWithExpected.entrySet().stream().map(e -> () -> new Object[]{e.getKey(), e.getValue()});
	}
}
