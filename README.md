cli
===

Command Line Interface for Java 8

## Usage
### Annotations

```java
public class Application {

    public static void main( String[] args ) {
        Application app = new Application();
        try {
			AnnotationHandler ah = new AnnotationHandler();
			ah.fillOptions(app, args);
		} catch (DuplicateOptionException | NoArgumentAllowedException | IllegalArgumentException | IllegalAccessException ex) {
			log.catching(ex);
		}
		app.run();
    }
    
    @CliOption(name = 'u')
	private boolean updateOnly;

	@CliOption(name = 'h', longName = "help")
	private boolean showHelp;

	@CliOption(name = 'f', longName = "file")
	private File configFile;

	@CliOption(name = 'i', longName = "ids")
	private List<Integer> ids;

	@CliOption(name = 'n', longName = "names")
	private List<String> names;

	@Override
	public void run() { 
	    //do your work here
	}
}
```
