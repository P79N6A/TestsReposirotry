/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.empire.jsf2.components;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlOutcomeTargetLink;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.empire.commons.ObjectUtils;
import org.apache.empire.commons.StringUtils;
import org.apache.empire.data.DataType;
import org.apache.empire.jsf2.controls.InputControl;
import org.apache.empire.jsf2.controls.InputControlManager;
import org.apache.empire.jsf2.utils.StringResponseWriter;
import org.apache.empire.jsf2.utils.TagEncodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkTag extends UIOutput // implements NamingContainer
{
    // or HtmlOutcomeTargetLink
    // or HtmlOutputLink
    // or rather extend UIOutcomeTarget ?

    /*
    <h:link value="#{msg.menu_bookinglist}"
            outcome="/pages/bookings/bookingList.xhtml?faces-redirect=true" >
        <f:param name="action" value="doInit"/>
    </h:link>
     */

    // Logger
    private static final Logger log = LoggerFactory.getLogger(LinkTag.class);
    
    protected final TagEncodingHelper helper = new TagEncodingHelper(this, "eLink");

    private boolean creatingComponents = false;
    
    public LinkTag()
    {
        log.trace("component link created");
    }

    @Override
    public String getFamily()
    {
        return UINamingContainer.COMPONENT_FAMILY; 
    }
    
    /**
     * remember original clientId
     * necessary only inside UIData
     */
    private String treeClientId = null;
    
    @Override
    public boolean visitTree(VisitContext visitContext, VisitCallback callback) 
    {
        FacesContext context = visitContext.getFacesContext();
        treeClientId = this.getClientId(context);
        return super.visitTree(visitContext, callback);
    }

    @Override
    public String getClientId(FacesContext context)
    {
        // Check if dynamic components are being created
        if (this.treeClientId!=null && this.creatingComponents)
        {   // return the original tree client id
            return treeClientId; 
        }
        // default behavior
        return super.getClientId(context);
    }

    @Override
    public void encodeBegin(FacesContext context)
        throws IOException
    {
        // add label and input components when the view is loaded for the first time
        super.encodeBegin(context);

        // begin
        helper.encodeBegin();
        if (isLinkDisabled())
        {   // render as span
            // render components
            ResponseWriter writer = context.getResponseWriter();
            String tag = writeStartElement(writer);
            writer.write(StringUtils.toString(getLinkValue(helper.hasColumn()), ""));
            writer.endElement(tag);
        }
        else
        {   // Add component
            HtmlOutcomeTargetLink linkComponent = null;
            if (getChildCount() > 0)
            {
                UIComponent c = getChildren().get(0);
                if (c instanceof HtmlOutcomeTargetLink)
                {   // reuse
                    linkComponent = (HtmlOutcomeTargetLink)c;
                    helper.restoreComponentId(linkComponent);
                    // check image
                    if (linkComponent.getChildCount()>0)
                    {   // Check HtmlGraphicImage
                        int last = linkComponent.getChildCount()-1;
                        UIComponent lcc = linkComponent.getChildren().get(last);
                        if (lcc instanceof HtmlGraphicImage)
                            helper.restoreComponentId(lcc);
                    }
                }
                else
                {   // Something's wrong here?
                    log.info("INFO: Unexpected child node for {}! Child item type is {}.", getClass().getName(), c.getClass().getName());
                    // Check facetComponent
                    UIPanel facetComponent = (UIPanel)getFacets().get(UIComponent.COMPOSITE_FACET_NAME);
                    if (facetComponent==null)
                    {
                        log.warn("WARN: component's facetComponent has not been set! Using Default (javax.faces.Panel).");
                        log.warn("Problem might be related to Mojarra's state context saving for dynamic components (affects all versions > 2.1.6). See com.sun.faces.context.StateContext.java:AddRemoveListener");
                        facetComponent = (UIPanel)context.getApplication().createComponent("javax.faces.Panel");
                        facetComponent.setRendererType("javax.faces.Group");
                        getFacets().put(UIComponent.COMPOSITE_FACET_NAME, facetComponent);
                    }
                }    
            }
            if (linkComponent == null)
            {   try {
                    creatingComponents = true;
                    linkComponent = createOutcomeTargetLink(context);
                    this.getChildren().add(0, linkComponent);
                    helper.saveComponentId(linkComponent);
                    // encode image
                    String imagePath = helper.getTagAttributeString("image");
                    if (StringUtils.isNotEmpty(imagePath))
                    {   // Create image
                        HtmlGraphicImage img = encodeImage(context, linkComponent, imagePath);
                        linkComponent.getChildren().add(img);
                        helper.saveComponentId(linkComponent);
                    }    
                    // done
                } finally {
                    creatingComponents = false;
                }
            }
            // set params
            setLinkProperties(linkComponent);
            addOrSetParam(linkComponent, "idparam", "id");
            // encode link
            linkComponent.setRendered(true);
            linkComponent.encodeAll(context);
            linkComponent.setRendered(false); // Don't render twice!
        }
    }
    
    @Override 
    public void encodeChildren(FacesContext context) throws IOException 
    {
        if (!isLinkDisabled())
            super.encodeChildren(context);
    }
    
    protected String getLinkStyleClass()
    {
        return StringUtils.toString(getAttributes().get("styleClass"));
    }
    
    protected boolean isLinkDisabled()
    {
        Object v = getAttributes().get("disabled");
        if (v==null)
            return false;
        // return disabled attribute
        return ObjectUtils.getBoolean(v); 
    }
    
    protected Object getLinkValue(boolean hasColumn)
    {
        // Is a column provided?
        if (hasColumn)
        {
            InputControl control = helper.getInputControl();
            InputControl.ValueInfo vi = helper.getValueInfo(FacesContext.getCurrentInstance());
            // render value
            StringResponseWriter srw = new StringResponseWriter();
            try
            {
                control.renderValue(vi, srw);
            }
            catch (IOException e)
            {   // Error rendering value
                log.error("Failed to render value for "+vi.getColumn().getName()+" error is:"+e.getMessage(), e);
            }
            return srw.toString();
        }
        else
        {   // An ordinary link
            String text = helper.getTagAttributeString("text");
            if (text!=null)
                return helper.getTextResolver(FacesContext.getCurrentInstance()).resolveText(text);
            // Use value
            Object value = getValue();
            return value;
        }
    }

    protected void setLinkProperties(HtmlOutcomeTargetLink link)
    {
        boolean hasColumn = helper.hasColumn();
        Object value = getLinkValue(hasColumn);
        link.setValue(value);
        // css Style
        DataType dataType = (hasColumn ? helper.getColumn().getDataType() : DataType.UNKNOWN);
        link.setStyleClass(helper.getTagStyleClass(dataType, null, getLinkStyleClass()));
        // Set Attributes
        Map<String,Object> attr = getAttributes();
        // Set outcome
        Object page = attr.get("page");
        String outcome = StringUtils.toString(page);
        link.setOutcome(outcome);
        // Copy attributes
        if ((value=attr.get("style"))!=null)
            link.setStyle(StringUtils.toString(value));
        if ((value=attr.get("tabindex"))!=null)
            link.setTabindex(StringUtils.toString(value));
        if ((value=attr.get("onclick"))!=null)
            link.setOnclick(StringUtils.toString(value));
        // title
        String title = helper.getTagAttributeString("title");
        if (StringUtils.isNotEmpty(title))
            link.setTitle(title);
        // include view param
        link.setIncludeViewParams(false);
    }
    
    protected void addOrSetParam(HtmlOutcomeTargetLink link, String attribute, String paramName)
    {
        // Get Attribute
        String paramValue = StringUtils.toString(getAttributes().get(attribute));
        if (StringUtils.isEmpty(paramValue))
            return; 
        // find attribute
        List<UIComponent> l = link.getChildren();
        for (UIComponent c : l)
        {
            if (!(c instanceof UIParameter))
                continue;
            UIParameter p = (UIParameter)c; 
            if (p.getName().equalsIgnoreCase(paramName))
            {   // param existis
                p.setValue(paramValue);
                return;
            }
        }
        // Not found, hence add
        UIParameter param = new UIParameter();
        param.setName(paramName);
        param.setValue(paramValue);
        link.getChildren().add(param);
        helper.resetComponentId(param);
    }

    protected String writeStartElement(ResponseWriter writer)
        throws IOException
    {
        Map<String, Object> map = getAttributes();
        String tagName  = StringUtils.coalesce(StringUtils.toString(map.get("tag")), "span");
        String cssClass = helper.getTagStyleClass();
        Object style = map.get("style");
        Object title = helper.getTagAttributeValue("title");
        // Write tag
        writer.startElement(tagName, this);
        helper.writeAttribute(writer, "class", cssClass);
        helper.writeAttribute(writer, "style", style);
        helper.writeAttribute(writer, "title", helper.hasColumn() ? helper.getValueTooltip(title) : title);
        return tagName;
    }

    protected HtmlOutcomeTargetLink createOutcomeTargetLink(FacesContext context)
    {
        // OutcomeTargetLink link 
        HtmlOutcomeTargetLink link = InputControlManager.createComponent(context, HtmlOutcomeTargetLink.class);
        return link;
    }
    
    protected HtmlGraphicImage encodeImage(FacesContext context, HtmlOutcomeTargetLink parent, String imagePath)
    {
        HtmlGraphicImage img = InputControlManager.createComponent(context, HtmlGraphicImage.class);
        img.setValue(imagePath);
        return img;
    }
    
    /*
     * public String getLabelValue()
     * {
     * return StringUtils.toString(getValue());
     * }
     * public String getPageValue()
     * {
     * return (String) getAttributes().get("page");
     * }
     * public String getActionValue()
     * {
     * return (String) getAttributes().get("action");
     * }
     */
}
