Watsonsim Question Answering System [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

## [Regular Updates on Development](http://watsonsim.blogspot.com)
Keep in mind that the program may change faster than its documentation. If you are experiencing problems, [contact a developer](mailto:stgallag@gmail.com).

## Quick Intro
Watsonsim works using a pipeline of operations on questions, candidate answers, and their supporting passages. In many ways it is similar to [IBM's Watson](http://en.wikipedia.org/wiki/Watson_%28computer%29), and [Petr's YodaQA](https://github.com/brmson/yodaqa). It's not all that similar to more logic based systems like [OpenCog](http://opencog.org/) or [Wolfram Alpha](www.wolframalpha.com). But there are significant differences even from Watson and YodaQA.

- We don't use a standard UIMA pipeline, which is a product of our student-project history. Sometimes this is a hindrance but typically it has little impact. We suspect it reduces the learning overhead and boilerplate code.
- Unlike YodaQA, we target Jeopardy! questions, but we do incorporate their method of Lexical Answer Type (LAT) checking, in addition to our own.
- Our framework is rather heavyweight in terms of computation. Depending on what modules are enabled, it can take between about 1 second and 2 minutes to answer a question. We use Indri to improve accuracy but if you prefer, you can disable it in the code for a large speedup. (We are investigating alternatives as well.)
- We include (relatively) large amounts of preprocessed article text from Wikipedia as our inputs. Be prepared to use about 100GB of space if you want to try it out at its full power.

## Technologies Involved
This list isn't exhaustive, but it should be a good overview

- Search
  - Text search from Lucene and Indri (Terrier upcoming)
  - Web search from Bing (Google is in the works)
  - Relational queries using PostgreSQL
  - Linked data queries using Jena
- Sources
  - Text from all the articles in Wikipedia, Wiktionary, and Wikiquotes
  - Linked data from DBPedia, used for LAT
  - Wikipedia pageviews organized by article
  - Source, target, and label from all links in Wikipedia
- Machine learning with Weka and libSVM
- Text parsing and dependency generation from CoreNLP and OpenNLP
- Parsing logic in Prolog (with TuProlog)

## Installing the Simulator
- For the program
  - [git](http://git-scm.com/downloads) clone https://github.com/SeanTater/uncc2014watsonsim.git
  - Java 8, either:
    - [Bundled with Eclipse](https://www.eclipse.org/downloads/)
    - Ubuntu utopic+: `sudo apt-get install openjdk-8-jdk`
    - Fedora 20+: `yum install java-1.8.0-openjdk`
    - [Windows, Mac, all others](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - [Indri search library (native)](http://www.lemurproject.org/indri.php), needs to be compiled with SWIG and Java, afterward copy the libindri_jni.* into uncc2014watsonsim/lib
    - To skip this step, set `indri_enabled = false` in config.properties.
  - libSVM machine learning library (native)
    - Ubuntu, Fedora: install `libsvm-java`
    - [Windows](http://www.csie.ntu.edu.tw/~cjlin/libsvm/) ([instructions](http://stackoverflow.com/questions/25060178/which-weka-and-libsvm-jar-files-to-use-in-java-code-for-svm-classification))
  - [Gradle](http://gradle.org/downloads) (just unzip; no install necessary, keep in mind it updates very often)
  - Run `gradle eclipse` in `uncc2014watsonsim/` to download platform-independent dependencies and create an Eclipse project. Your path to the gradle binary will be different. (e.g. `/home/josh/Downloads/gradle-2.22/bin/gradle eclipse`)
  - Install [Postgres](http://www.postgresql.org/download/) (we use 9.3)
- [Download the latest data](https://github.com/SeanTater/uncc2014watsonsim/wiki/Data-Sources). Decompress the whole archive, placing the content in the data/ folder.
  - Load the included database snapshot into Postgres using `pg_restore -d watsonsim [more options as necessary] data-snapshot.pgdump`.
- For Bing web search
  - copy config.properties.sample to config.properties
  - [Create an Azure account and sign up for Bing](https://datamarket.azure.com/dataset/bing/search), put the [API key](https://datamarket.azure.com/account/keys) the right variable in the config.
- For Google web search (which is disabled currently, so this is optional and will not (yet) be used)
  - Make a new [Google cloud app](https://cloud.google.com/console), and put the name in the config
    - Enable the Custom Search API, create a server public API key, and paste it into the config
  - Make your own [custom search engine](https://www.google.com/cse/create/new)
    - Choose "Search any site" (but you have to pick a domain, maybe wikipedia.org would be good)
    - Edit the custom search you just made. In "Sites to search", change "Search only included sites" to "Search the entire web but emphasize included sites"
    - Get the search engine ID, put it in the config
- For the scripts, which you do not need for simple queries:
  - Python 2.6+
  - psycopg2, which you can install with `pip install psycopg2`, or as python-psycopg2 in ubuntu and fedora

### Notes:
- We once used SQLite but with many connections (200-500+), corruption seems to be a problem. We may make the SQL pluggable to avoid this extra setup step but probably not until someone requests it.
- The data is sizable and growing, especially for statistics reports; 154.5 GB as of the time of this writing.
- Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.

## Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Examine the reference documentation](http://seantater.github.io/uncc2014watsonsim/)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
