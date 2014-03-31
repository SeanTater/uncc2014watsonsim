package uncc2014watsonsim.uima;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.TOP;

public class UimaTools {
  public static <T extends TOP> T getSingleton(JCas cas, int classTypeInt)
          throws UimaToolsException {
    FSIterator<? extends TOP> iterator = cas.getJFSIndexRepository().getAllIndexedFS(classTypeInt);
    if (!iterator.hasNext())
      return null;
    @SuppressWarnings("unchecked")
    T retObject = (T) iterator.next();
    if (iterator.hasNext())
      throw new UimaToolsException("More than one instance of type found in the CAS");
    return retObject;
  }

  public static FSList addToFSList(FSList list, TOP entry) throws UimaToolsException {
    if (list == null)
      throw new UimaToolsException(new NullPointerException());

    if (list instanceof EmptyFSList) {
      NonEmptyFSList nonEmptyList;
      try {
        nonEmptyList = new NonEmptyFSList(list.getCAS().getJCas());
        nonEmptyList.setHead(entry);
        nonEmptyList.setTail(new EmptyFSList(list.getCAS().getJCas()));
      } catch (CASException e) {
        throw new UimaToolsException(e);
      }
      return nonEmptyList;
    }

    if (!(list instanceof NonEmptyFSList))
      throw new UimaToolsException("List is neither an EmptyFSList nor a NonEmptyFSList");

    FSList curPtr = list;
    FSList tail = ((NonEmptyFSList) list).getTail();
    while (tail != null && !(tail instanceof EmptyFSList)) {
      if (!(tail instanceof NonEmptyFSList))
        throw new UimaToolsException("Tail is neither an EmptyFSList nor a NonEmptyFSList");
      curPtr = tail;
      tail = ((NonEmptyFSList) tail).getTail();
    }
    NonEmptyFSList nonEmptyList;
    try {
      nonEmptyList = new NonEmptyFSList(list.getCAS().getJCas());
      nonEmptyList.setHead(entry);
      nonEmptyList.setTail(new EmptyFSList(list.getCAS().getJCas()));
    } catch (CASException e) {
      throw new UimaToolsException(e);
    }
    ((NonEmptyFSList) curPtr).setTail(nonEmptyList);
    return list;
  }

  public static <T extends TOP> Collection<T> getFSCollection(JCas cas, int type) {
    Iterator<? extends TOP> it = cas.getJFSIndexRepository().getAllIndexedFS(type);
    Collection<T> retSet = new HashSet<T>();
    while (it.hasNext()) {
      @SuppressWarnings("unchecked")
      T obj = (T) it.next();
      retSet.add(obj);
    }
    return retSet;
  }

  @SuppressWarnings("unchecked")
  public static <T extends TOP> List<T> getFSList(FSList list) throws UimaToolsException {
    List<T> retList = new ArrayList<T>();
    if (list == null || list instanceof EmptyFSList)
      return retList;
    FSList cur = list;
    while (cur != null && !(cur instanceof EmptyFSList)) {
      if (!(cur instanceof NonEmptyFSList))
        throw new UimaToolsException("List is neither an EmptyFSList nor a NonEmptyFSList");
      retList.add((T) ((NonEmptyFSList) cur).getHead());
      cur = ((NonEmptyFSList) cur).getTail();
    }
    return retList;
  }

  public static boolean casContainsView(JCas arg0, String view) throws CASException {
    return arg0.getViewIterator(view).hasNext();
  }

  public static boolean casContainsView(CAS cas, String view) {
    return cas.getViewIterator(view).hasNext();
  }
}
