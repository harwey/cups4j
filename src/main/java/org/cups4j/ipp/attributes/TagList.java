package org.cups4j.ipp.attributes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "tag-list")
public class TagList {
  @Attribute
  protected String schemaLocation;
  @ElementList(entry = "tag", inline = true, required = true)
  protected List<Tag> tag;

  public List<Tag> getTag() {
    if (tag == null) {
      tag = new ArrayList<Tag>();
    }
    return this.tag;
  }

  public String getSchemaLocation() {
    return schemaLocation;
  }

  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }

}
