//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.10.14 at 12:03:17 PM MESZ 
//

package org.cups4j.ipp.attributes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "attribute-list")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AttributeList {
  
  protected String schemaLocation;
  
  protected List<AttributeGroup> attributeGroup;
  
  protected String description;
  
  @XmlAttribute
  public String getSchemaLocation() {
    return schemaLocation;
  }
  
  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }
  
  /**
   * Gets the value of the attributeGroup property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the attributeGroup property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getAttributeGroup().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link AttributeGroup }
   * 
   * 
   */
  @XmlElement(name = "attribute-group")
  public List<AttributeGroup> getAttributeGroup() {
    if (attributeGroup == null) {
      attributeGroup = new ArrayList<>();
    }
    return this.attributeGroup;
  }
  
  public void setAttributeGroup(List<AttributeGroup> attributeGroup) {
    this.attributeGroup = attributeGroup;
  }
  
  /**
   * Gets the value of the description property.
   * 
   * @return possible object is {@link String }
   * 
   */
  @XmlAttribute
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

}
