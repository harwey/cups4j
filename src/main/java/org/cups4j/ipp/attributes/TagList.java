package org.cups4j.ipp.attributes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "tag-list")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TagList {
  
  protected String schemaLocation;
  
  protected List<Tag> tag;
  
  /**
   * @return list of tags
   */
  @XmlElement(name = "tag", required = true)
  public List<Tag> getTag() {
    if (tag == null) {
      tag = new ArrayList<>();
    }
    return tag;
  }
  
  /**
   * @param tag list of tags to set
   */
  public void setTag(List<Tag> tag) {
    this.tag = tag;
  }
  
  /**
   * @return schema location
   */
  @XmlAttribute
  public String getSchemaLocation() {
    return schemaLocation;
  }
  
  /**
   * @param schemaLocation the schema location to set
   */
  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }

}
