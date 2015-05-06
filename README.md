[Watsonsim Question Answering System](http://watsonsim.blogspot.com) [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

## Quick Intro
Watsonsim works using a pipeline of operations on questions, candidate answers, and their supporting passages. In many ways it is similar to [IBM's Watson](http://en.wikipedia.org/wiki/Watson_%28computer%29), and [Petr's YodaQA](https://github.com/brmson/yodaqa). It's not all that similar to more logic based systems like [OpenCog](http://opencog.org/) or [Wolfram Alpha](www.wolframalpha.com). But there are significant differences even from Watson and YodaQA.

- We don't use a standard UIMA pipeline, which is a product of our student-project history. Sometimes this is a hindrance but typically it has little impact. We suspect it reduces the learning overhead and boilerplate code.
- Unlike YodaQA, we target Jeopardy! questions, but we do incorporate their method of Lexical Answer Type (LAT) checking, in addition to our own.
- Our framework is rather heavyweight in terms of computation. Depending on what modules are enabled, it can take between about 1 second and 2 minutes to answer a question. We use Indri to improve accuracy but it is now an [optional feature](https://github.com/SeanTater/uncc2014watsonsim/wiki/Optional-Features) that we highly recommend. (We are investigating alternatives as well.)
- We include (relatively) large amounts of preprocessed article text from Wikipedia as our inputs. Be prepared to use about 100GB of space if you want to try it out at its full power.

## Installing the Simulator
- Use [git](http://git-scm.com/downloads) to clone this repository, as in: `git clone https://github.com/SeanTater/uncc2014watsonsim.git`
- Install Java 8, either:
  - [Bundled with Eclipse](https://www.eclipse.org/downloads/)
  - or on Ubuntu utopic+: `sudo apt-get install openjdk-8-jdk`
  - or on Fedora 20+: `yum install java-1.8.0-openjdk`
  - or on [Windows, Mac, all others](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- libSVM machine learning library (native)
  - For Ubuntu and Fedora: install `libsvm-java`
  - otherwise, for [Windows](http://www.csie.ntu.edu.tw/~cjlin/libsvm/) follow some  [instructions](http://stackoverflow.com/questions/25060178/which-weka-and-libsvm-jar-files-to-use-in-java-code-for-svm-classification)
- Download [Gradle](http://gradle.org/downloads) (just unzip it; keep in mind it updates very often)
- Download the [latest data](https://github.com/SeanTater/uncc2014watsonsim/wiki/Data-Sources) and place them in the data/ directory
- Copy the configuration file `config.properties.sample` to `config.properties` and customize to your liking
- Run `gradle eclipse -Ptarget` in `uncc2014watsonsim/` to download platform-independent dependencies and create an Eclipse project.
- Possibly enable some [Optional Features](https://github.com/SeanTater/uncc2014watsonsim/wiki/Optional-Features)

### Running the Simulator
We recommend running the simulator with Gradle:
```sh
gradle run -Ptarget=WatsonSim
```

But, if you prefer, you can also use Eclipse. First create a project.
```sh
gradle eclipse -Ptarget
```
Then remove apache-jena-libs-*.pom since Eclipse cannot handle .pom in the build path, and all the necessary dependencies it references will have already been included. Then you can run WatsonSim.java directly.

You can also run the accuracy tests using a script:
```sh
gradle run -Ptarget=scripts.ParallelStats
```

## Technologies Involved
This list isn't exhaustive, but it should be a good overview

- Search
  - Text search from Lucene and Indri (Terrier upcoming)
  - Web search from Bing (Google is in the works)
  - Relational queries using PostgreSQL and SQLite
  - Linked data queries using Jena
- Sources
  - Text from all the articles in Wikipedia, Simple Wikipedia, Wiktionary, and Wikiquotes
  - Linked data from DBPedia, used for LAT detection
  - Wikipedia pageviews organized by article
  - Source, target, and label from all links in Wikipedia
- Machine learning with Weka and libSVM
- Text parsing and dependency generation from CoreNLP and OpenNLP
- Parsing logic in Prolog (with TuProlog)

### Notes:
- You should probably consider using PostgreSQL if you scale this project to more than a few cores, or any distributed environment. It should support both engines nicely.
- The data is sizable and growing, especially for statistics reports; 154.5 GB as of the time of this writing.
- Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.

## Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Examine the reference documentation](http://seantater.github.io/uncc2014watsonsim/)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
