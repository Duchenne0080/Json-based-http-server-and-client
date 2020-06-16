import org.kohsuke.args4j.Option;
public class CommandLineValues {
			@Option(required = true, name = "-p", aliases = {"--port"}, usage="Port Address")
			private int port = 4444;

			public int getPort() {
				return port;
			}
		}


