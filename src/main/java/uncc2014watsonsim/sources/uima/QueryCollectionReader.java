package uncc2014watsonsim.sources.uima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.util.XMLInputSource;

import uncc2014watsonsim.uima.types.QueryString;

/**
 * This class implements a UIMA collection reader that reads in a file containing input queries for
 * the Semantic Search project pipeline.
 *
 * @author Jonathan Shuman (unccwatsonsim repackage and edits)
 */
public class QueryCollectionReader extends CollectionReader_ImplBase {

  public static final String PARAM_INPUT_FILE = "InputFile";

  private static final String COMPONENT_ID = "QueryCollectionReader";

  private static final boolean DEBUG = true;

  private List<String> queries;

  private int nextIndex;

  /**
   * Initialize the data structures in this collection reader
   */
  @Override
  public void initialize() throws ResourceInitializationException {
    super.initialize();
    String inputFile = (String) getConfigParameterValue(PARAM_INPUT_FILE);
    queries = new ArrayList<String>();
    try {
      BufferedReader cin = new BufferedReader(new FileReader(inputFile));
      String line = null;
      while ((line = cin.readLine()) != null) {
        line = line.trim();
        if (line.equals(""))
          continue;
        String query = new String(line);
        queries.add(query);
        if (DEBUG)
          System.err.println("QueryCollectionReader read query: " + query);
      }
      cin.close();
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    nextIndex = 0;
  }

  /**
   * Populate the CAS with the next query in the collection
   */
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    if (nextIndex < 0 || nextIndex >= queries.size())
      throw new CollectionException(new Throwable("No more queries in collection"));

    JCas jcas = null;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    if (jcas == null)
      throw new CollectionException(new Throwable("Null jCAS for given document"));

    String qString = queries.get(nextIndex);
    JCas queryView = null;
    try {
      queryView = jcas.createView("QUERY");
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    queryView.setDocumentLanguage("en");
    //queryView.setDocumentText(qString);

    //This is an annotation. And its positions need to be set.
    QueryString queryStringAnnotation = new QueryString(queryView, 0, qString.length());
    queryStringAnnotation.setQuery(qString);
    queryStringAnnotation.addToIndexes();
    
    SourceDocumentInformation sdi = new SourceDocumentInformation(jcas);
    sdi.setUri("file:/" + nextIndex++);
    sdi.addToIndexes();
  }

  /**
   * Clean-up after end of collection processing
   */
  @Override
  public void close() throws IOException {
  }

  /**
   * Return the percentage progress through this collection processing
   *
   * @return the progress through this collection
   */
  @Override
  public Progress[] getProgress() {
    Progress progress = new ProgressImpl(nextIndex, queries.size(), Progress.ENTITIES);
    return new Progress[] { progress };
  }

  /**
   * Are there more queries in this collection
   *
   * @return true if there are more queries in the collection, false otherwise
   */
  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return (nextIndex < queries.size());
  }

  /**
   * Parses and returns the descriptor for this collection reader. The descriptor is stored in the
   * uima.jar file and located using the ClassLoader.
   *
   * @return an object containing all of the information parsed from the descriptor.
   *
   * @throws InvalidXMLException
   *           if the descriptor is invalid or missing
   */
  public static CollectionReaderDescription getDescription() throws InvalidXMLException {
    InputStream descriptorStream = QueryCollectionReader.class
            .getResourceAsStream("QueryCollectionReader.xml");
    return UIMAFramework.getXMLParser().parseCollectionReaderDescription(
            new XMLInputSource(descriptorStream, null));
  }

}
