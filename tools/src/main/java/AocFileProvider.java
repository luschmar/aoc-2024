import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AocFileProvider implements ArgumentsProvider, AnnotationConsumer<AocFileSource> {
	private Map<Stream<String>, String> resourceStreamWithExpected;

	@Override
	public void accept(AocFileSource aocFileSource) {
		this.resourceStreamWithExpected = Arrays.stream(aocFileSource.inputs()).collect(Collectors.toMap(k -> loadFileAsStream(k.input()), AocInputMapping::expected));
	}

	private Stream<String> loadFileAsStream(String resourceName) {
		var br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName))));
		return br.lines();
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return resourceStreamWithExpected.entrySet().stream().map(e -> () -> new Object[] {e.getKey(), e.getValue()});
	}
}
