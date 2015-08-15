# Testing using your IDE

If you happen to work with an IDE that provides JUnit support IDE (such as IntelliJ or Eclipse) you can directly run the tests and Suites. But, in order for this to work you'll need to configure your IDE to put the `ctk-cli`, `ctk-transport`, `ctk-domain`, and `ctk-schema` modules on the classpath for your tests module.

## Configuring IntelliJ
Check out the ctk-core project from github using the IntelliJ VCS support (VCS -> Checkout from Version Control -> Github) and let IntelliJ create the multi-module Maven project

Note: If you're working against a schema other than what's embedded in the CTK, this is when you'll want to configure that; for example, if you've set up a `.gitmodules` file you might need to a terminal window  and do `git submodule init` and `git submodule update` to get your schemas installed locally.

Now open the File -> Project Structure dialog, and select the `Modules` setting. Choose the `cts-java` module and then the Dependencies tab. Make sure you have `ctk-cli` and `ctk-transport` and `ctk-schemas` indicated as `compile` dependencies (if not add them, using the green '+' and setting them as as module dependencies of `cts-java`). Run `mvn package` at least once so all the class files are present.

Now you should be able to run the JUnit tests (`cts-java`) directly using a JUnit Run configuration. You may need to create a JUnit Run configuration, and possibly you'll want to create several: all tests, the test class you're working, a specific Test Suite, etc.

## Configuring Eclipse
TBD 
