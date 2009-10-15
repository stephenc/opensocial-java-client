package org.opensocial.parsers;

import java.util.Map;

import org.opensocial.Response;
import org.opensocial.data.Model;

public interface Parser {

  public Response getResponseObject(String in,
      final Class<? extends Model> modelClass, String version);

  public Map<String, Response> getResponseMap(String in,
      Map<String, Class<? extends Model>> modelClasses, String version);
}
