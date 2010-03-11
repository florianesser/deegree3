//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-792 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.11 at 03:47:38 PM CET 
//


package org.deegree.commons.datasource.configuration;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.deegree.org/datasource}RasterFileDataSourceType">
 *       &lt;attribute name="crs" type="{http://www.w3.org/2001/XMLSchema}string" default="EPSG:4326" />
 *       &lt;attribute name="originLocation" type="{http://www.deegree.org/datasource}OriginLocation" default="center" />
 *       &lt;attribute name="readWorldFiles" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="nodata" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="nodataType" type="{http://www.deegree.org/datasource}NoDataType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class RasterDataSource
    extends RasterFileDataSourceType
{

    @XmlAttribute
    protected String crs;
    @XmlAttribute
    protected OriginLocation originLocation;
    @XmlAttribute
    protected Boolean readWorldFiles;
    @XmlAttribute
    protected BigDecimal nodata;
    @XmlAttribute
    protected NoDataType nodataType;

    /**
     * Gets the value of the crs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrs() {
        if (crs == null) {
            return "EPSG:4326";
        } else {
            return crs;
        }
    }

    /**
     * Sets the value of the crs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrs(String value) {
        this.crs = value;
    }

    /**
     * Gets the value of the originLocation property.
     * 
     * @return
     *     possible object is
     *     {@link OriginLocation }
     *     
     */
    public OriginLocation getOriginLocation() {
        if (originLocation == null) {
            return OriginLocation.CENTER;
        } else {
            return originLocation;
        }
    }

    /**
     * Sets the value of the originLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginLocation }
     *     
     */
    public void setOriginLocation(OriginLocation value) {
        this.originLocation = value;
    }

    /**
     * Gets the value of the readWorldFiles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReadWorldFiles() {
        if (readWorldFiles == null) {
            return true;
        } else {
            return readWorldFiles;
        }
    }

    /**
     * Sets the value of the readWorldFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadWorldFiles(Boolean value) {
        this.readWorldFiles = value;
    }

    /**
     * Gets the value of the nodata property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNodata() {
        return nodata;
    }

    /**
     * Sets the value of the nodata property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNodata(BigDecimal value) {
        this.nodata = value;
    }

    /**
     * Gets the value of the nodataType property.
     * 
     * @return
     *     possible object is
     *     {@link NoDataType }
     *     
     */
    public NoDataType getNodataType() {
        return nodataType;
    }

    /**
     * Sets the value of the nodataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link NoDataType }
     *     
     */
    public void setNodataType(NoDataType value) {
        this.nodataType = value;
    }

}
