package org.cups4j.ipp.attributes;

import org.simpleframework.xml.Root;

@Root(name = "tag")
public class Tag {

  @org.simpleframework.xml.Attribute(required = true)
  protected String value;
  @org.simpleframework.xml.Attribute(required = true)
  protected String name;
  @org.simpleframework.xml.Attribute(required = false)
  protected String description;
  @org.simpleframework.xml.Attribute(required = false)
  protected Short max;

  /**
   * Gets the value of the value property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the name property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the description property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
   * Gets the value of the max property.
   * 
   * @return possible object is {@link Short }
   * 
   */
  public Short getMax() {
    return max;
  }

  /**
   * Sets the value of the max property.
   * 
   * @param value
   *          allowed object is {@link Short }
   * 
   */
  public void setMax(Short value) {
    this.max = value;
  }

}
