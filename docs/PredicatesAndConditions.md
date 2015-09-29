# Predicates and Conditions for GA4GH

This note starts exploring how and why to write and use Predicates and Conditions.

## Java 8 Predicates
- use with `matches` see [AssertJ example](http://joel-costigliola.github.io/assertj/assertj-core-news.html#assertj-core-3.0.0-matches-assertions)

## AssertJ Conditions
The [AssertJ introduction to Conditions](http://joel-costigliola.github.io/assertj/assertj-core-conditions.html) is a good starting point, though not specific to GA4GH.

Conditions can even be defined using a Predicate and a description:

```java
Condition<String> fairyTale = new Condition<String>(s -> s.startsWith("Once upon a time"), 
                                                    "a %s tale", "fairy");
String littleRedCap = "Once upon a time there was a dear little girl ...";
assertThat(littleRedCap).is(fairyTale);
```