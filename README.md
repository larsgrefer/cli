cli
===

Command Line Interface for Java 8

## Usage

### Command Line Syntax

Options can have a short name (one char) and a long name.  
Muliple short names can collapse into one declaration
In the collapsed form only the last option can take arguments

```bash
-f
--foo

-f --bar
-f -b
-fb

--names n1 n2 n3
-n n1 n2 n3

-n n1 n2 n3 -fb
-fbn n1 n2 n3

--map key1=value1 key2=value2
```

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
