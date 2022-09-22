package org.cups4j.ipp.attributes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "tag")
public class Tag {

  @XmlAttribute(required = true)
  protected String value;
  @XmlAttribute(required = true)
  protected String name;
  @XmlAttribute
  protected String description;
  @XmlAttribute
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
