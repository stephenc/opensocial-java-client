package org.opensocial;

import org.opensocial.data.Model;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.parsers.JsonParser;
import org.opensocial.parsers.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {

  private Long startIndex;
  private Long totalResults;
  private Long itemsPerPage;
  private Boolean isFiltered;
  private List<Model> entries;

  public Response() {
    entries = new ArrayList<Model>();
  }

  public static Map<String, Response> parseRpcResponse(
      Map<String, Request> requests, HttpResponseMessage responseMessage,
      String version) {
    Parser parser = getParser(responseMessage.getResponse());

    Map<String, Class<? extends Model>> modelClasses =
        new HashMap<String, Class<? extends Model>>();
    for (Map.Entry<String, Request> requestEntry : requests.entrySet()) {
      Request request = requestEntry.getValue();
      modelClasses.put(requestEntry.getKey(), request.getModelClass());
    }

    return parser.getResponseMap(responseMessage.getResponse(), modelClasses,
        version);
  }

  public static Response parseRestResponse(Request request,
      HttpResponseMessage responseMessage, String version) {
    Parser parser = getParser(responseMessage.getResponse());

    return parser.getResponseObject(responseMessage.getResponse(),
        request.getModelClass(), version);
  }

  private static Parser getParser(String responseBody) {
    if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
      return new JsonParser();
    }

    return null;
  }

  public long getStartIndex() {
    return startIndex;
  }

  public long getTotalResults() {
    return totalResults;
  }

  public long getItemsPerPage() {
    return itemsPerPage;
  }

  public boolean isFiltered() {
    return isFiltered;
  }

  public List<Model> getEntries() {
    return entries;
  }

  public Model getEntry() {
    if (entries == null || entries.size() == 0) {
      return null;
    }

    return entries.get(0);
  }

  public void setStartIndex(Object startIndex) {
    this.startIndex = getLongValue(startIndex);
  }

  public void setTotalResults(Object totalResults) {
    this.totalResults = getLongValue(totalResults);
  }

  public void setItemsPerPage(Object itemsPerPage) {
    this.itemsPerPage = getLongValue(itemsPerPage);
  }

  public void setIsFiltered(Object isFiltered) {
    this.isFiltered = getBooleanValue(isFiltered);
  }

  private Long getLongValue(Object field) {
    if (field.getClass().equals(String.class)) {
      return Long.parseLong((String) field);
    } else if (field.getClass().equals(Number.class)) {
      return (Long) field;
    }

    return null;
  }

  private Boolean getBooleanValue(Object field) {
    if (field.getClass().equals(String.class)) {
      return Boolean.parseBoolean((String) field);
    } else if (field.getClass().equals(Boolean.class)) {
      return (Boolean) field;
    }

    return null;
  }
}
