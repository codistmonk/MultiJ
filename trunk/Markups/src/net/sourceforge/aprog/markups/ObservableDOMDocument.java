/*
 *  The MIT License
 * 
 *  Copyright 2010 Codist Monk.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.sourceforge.aprog.markups;

import net.sourceforge.aprog.events.AbstractObservable;
import net.sourceforge.aprog.tools.Tools;
import net.sourceforge.aprog.xml.XMLTools;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class ObservableDOMDocument extends AbstractObservable<ObservableDOMDocument.Listener> implements Document {

    private final Document document;

    /**
     *
     * @param document
     * <br>Not null
     * <br>Shared
     */
    public ObservableDOMDocument(final Document document) {
        this.document = document;
    }

    public ObservableDOMDocument() {
        this(XMLTools.newDocument());
    }

    @Override
    public final Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        final Object oldData = this.document.setUserData(key, data, handler);

        if (oldData != data) {
            this.new UserDataChangedEvent(key, oldData, data).fire();
        }

        return oldData;
    }

    @Override
    public final void setTextContent(final String textContent) throws DOMException {
        this.document.setTextContent(textContent);

        // According to the documentation, this method shouldn't have any effect
        assert this.getTextContent() == null;
    }

    @Override
    public final void setPrefix(final String prefix) throws DOMException {
        this.document.setPrefix(prefix);

        // According to the documentation, this method shouldn't have any effect
        assert this.getPrefix() == null;
    }

    @Override
    public final void setNodeValue(final String nodeValue) throws DOMException {
        this.document.setNodeValue(nodeValue);

        // According to the documentation, this method shouldn't have any effect
        assert this.getNodeValue() == null;
    }

    @Override
    public final Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        this.document.replaceChild(newChild, oldChild);

        this.new ChildReplacedEvent(oldChild, newChild).fire();

        return oldChild;
    }

    @Override
    public final Node removeChild(final Node oldChild) throws DOMException {
        // According to the documentation, this method shouldn't modify this object
        return this.document.removeChild(oldChild);
    }

    @Override
    public final void normalize() {
        this.document.normalize();

        this.new NodeNormalizedEvent().fire();
    }

    @Override
    public final String lookupPrefix(final String namespaceURI) {
        return this.document.lookupPrefix(namespaceURI);
    }

    @Override
    public final String lookupNamespaceURI(final String prefix) {
        return this.document.lookupNamespaceURI(prefix);
    }

    @Override
    public final boolean isSupported(final String feature, final String version) {
        return this.document.isSupported(feature, version);
    }

    @Override
    public final boolean isSameNode(final Node other) {
        return this.document.isSameNode(other);
    }

    @Override
    public final boolean isEqualNode(final Node other) {
        return this.document.isEqualNode(other);
    }

    @Override
    public final boolean isDefaultNamespace(final String namespaceURI) {
        return this.document.isDefaultNamespace(namespaceURI);
    }

    @Override
    public final Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        // I'm not sure about this one,
        // but I think the documentation says that
        // this method shouldn't modify this object
        return this.document.insertBefore(newChild, refChild);
    }

    @Override
    public final boolean hasChildNodes() {
        return this.document.hasChildNodes();
    }

    @Override
    public final boolean hasAttributes() {
        return this.document.hasAttributes();
    }

    @Override
    public final Object getUserData(final String key) {
        return this.document.getUserData(key);
    }

    @Override
    public final String getTextContent() throws DOMException {
        return this.document.getTextContent();
    }

    @Override
    public final Node getPreviousSibling() {
        return this.document.getPreviousSibling();
    }

    @Override
    public final String getPrefix() {
        return this.document.getPrefix();
    }

    @Override
    public final Node getParentNode() {
        return this.document.getParentNode();
    }

    @Override
    public Document getOwnerDocument() {
        return this.document.getOwnerDocument();
    }

    @Override
    public final String getNodeValue() throws DOMException {
        return this.document.getNodeValue();
    }

    @Override
    public final short getNodeType() {
        return this.document.getNodeType();
    }

    @Override
    public final String getNodeName() {
        return this.document.getNodeName();
    }

    @Override
    public final Node getNextSibling() {
        return this.document.getNextSibling();
    }

    @Override
    public final String getNamespaceURI() {
        return this.document.getNamespaceURI();
    }

    @Override
    public final String getLocalName() {
        return this.document.getLocalName();
    }

    @Override
    public final Node getLastChild() {
        return this.document.getLastChild();
    }

    @Override
    public final Node getFirstChild() {
        return this.document.getFirstChild();
    }

    @Override
    public final Object getFeature(final String feature, final String version) {
        return this.document.getFeature(feature, version);
    }

    @Override
    public final NodeList getChildNodes() {
        return this.document.getChildNodes();
    }

    @Override
    public final String getBaseURI() {
        return this.document.getBaseURI();
    }

    @Override
    public final NamedNodeMap getAttributes() {
        return this.document.getAttributes();
    }

    @Override
    public final short compareDocumentPosition(final Node other) throws DOMException {
        return this.document.compareDocumentPosition(other);
    }

    @Override
    public final Node cloneNode(final boolean deep) {
        return new ObservableDOMDocument((Document) this.document.cloneNode(deep));
    }

    @Override
    public final Node appendChild(final Node newChild) throws DOMException {
        this.document.appendChild(newChild);
        
        this.new ChildAppendedEvent(newChild).fire();

        return newChild;
    }

    @Override
    public final void setXmlVersion(final String xmlVersion) throws DOMException {
        final String oldXmlVersion = this.getXmlVersion();

        if (!Tools.equals(oldXmlVersion, xmlVersion)) {
            this.document.setXmlVersion(xmlVersion);

            this.new XmlVersionChangedEvent(oldXmlVersion, xmlVersion).fire();
        }
    }

    @Override
    public final void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
        final boolean oldXmlStandalone = this.getXmlStandalone();

        if (oldXmlStandalone != xmlStandalone) {
            this.document.setXmlStandalone(xmlStandalone);

            this.new XmlStandaloneChangedEvent(oldXmlStandalone, xmlStandalone).fire();
        }
    }

    @Override
    public final void setStrictErrorChecking(boolean strictErrorChecking) {
        this.document.setStrictErrorChecking(strictErrorChecking);
    }

    @Override
    public final void setDocumentURI(String documentURI) {
        this.document.setDocumentURI(documentURI);
    }

    @Override
    public final Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.renameNode(n, namespaceURI, qualifiedName);
    }

    @Override
    public final void normalizeDocument() {
        this.document.normalizeDocument();
    }

    @Override
    public final Node importNode(Node importedNode, boolean deep) throws DOMException {
        return this.document.importNode(importedNode, deep);
    }

    @Override
    public final String getXmlVersion() {
        return this.document.getXmlVersion();
    }

    @Override
    public final boolean getXmlStandalone() {
        return this.document.getXmlStandalone();
    }

    @Override
    public final String getXmlEncoding() {
        return this.document.getXmlEncoding();
    }

    @Override
    public final boolean getStrictErrorChecking() {
        return this.document.getStrictErrorChecking();
    }

    @Override
    public final String getInputEncoding() {
        return this.document.getInputEncoding();
    }

    @Override
    public final DOMImplementation getImplementation() {
        return this.document.getImplementation();
    }

    @Override
    public final NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return this.document.getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public final NodeList getElementsByTagName(String tagname) {
        return this.document.getElementsByTagName(tagname);
    }

    @Override
    public Element getElementById(String elementId) {
        return this.document.getElementById(elementId);
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return this.document.getDomConfig();
    }

    @Override
    public final String getDocumentURI() {
        return this.document.getDocumentURI();
    }

    @Override
    public Element getDocumentElement() {
        return this.document.getDocumentElement();
    }

    @Override
    public DocumentType getDoctype() {
        return this.document.getDoctype();
    }

    @Override
    public Text createTextNode(String data) {
        return this.document.createTextNode(data);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return this.document.createProcessingInstruction(target, data);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        return this.document.createEntityReference(name);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        return this.document.createElement(tagName);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return this.document.createDocumentFragment();
    }

    @Override
    public Comment createComment(String data) {
        return this.document.createComment(data);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return this.document.createCDATASection(data);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.document.createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        return this.document.createAttribute(name);
    }

    @Override
    public final Node adoptNode(Node source) throws DOMException {
        return this.document.adoptNode(source);
    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public abstract class AbstractEvent extends AbstractObservable<Listener>.AbstractEvent<ObservableDOMDocument, Listener> {
        // Deliberately left empty
    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     *
     * @param <T> The type of the data
     */
    public abstract class AbstractThingChangedEvent<T> extends AbstractEvent {

        private final T oldThing;

        private final T newThing;

        /**
         *
         * @param oldThing
         * <br>Maybe null
         * <br>Shared
         * @param newThing
         * <br>Maybe null
         * <br>Shared
         */
        public AbstractThingChangedEvent(final T oldThing, final T newThing) {
            this.oldThing = oldThing;
            this.newThing = newThing;
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        protected final T getNewThing() {
            return this.newThing;
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        protected final T getOldThing() {
            return this.oldThing;
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class UserDataChangedEvent extends AbstractThingChangedEvent<Object> {

        private final String key;

        /**
         *
         * @param key
         * <br>Maybe null
         * <br>Shared
         * @param oldUserData
         * <br>Maybe null
         * <br>Shared
         * @param newUserData
         * <br>Maybe null
         * <br>Shared
         */
        public UserDataChangedEvent(final String key, final Object oldUserData, final Object newUserData) {
            super(oldUserData, newUserData);
            this.key = key;
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getKey() {
            return this.key;
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final Object getNewUserData() {
            return this.getNewThing();
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final Object getOldUserData() {
            return this.getOldThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.userDataChanged(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class ChildReplacedEvent extends AbstractThingChangedEvent<Node> {

        /**
         * 
         * @param oldChild
         * <br>Not null
         * <br>Shared
         * @param newChild
         * <br>Not null
         * <br>Shared
         */
        public ChildReplacedEvent(final Node oldChild, final Node newChild) {
            super(oldChild, newChild);
        }

        /**
         * 
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getOldChild() {
            return this.getOldThing();
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getNewChild() {
            return this.getNewThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.childReplaced(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class NodeNormalizedEvent extends AbstractEvent {

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.nodeNormalized(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class ChildAppendedEvent extends AbstractEvent {

        private final Node appendedChild;

        /**
         *
         * @param appendedChild
         * <br>Not null
         * <br>Shared
         */
        public ChildAppendedEvent(final Node appendedChild) {
            this.appendedChild = appendedChild;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getAppendedChild() {
            return this.appendedChild;
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.childAppended(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public final class XmlVersionChangedEvent extends AbstractThingChangedEvent<String> {

        /**
         *
         * @param oldXmlVersion
         * <br>Maybe null
         * <br>Shared
         * @param newXmlVersion
         * <br>Maybe null
         * <br>Shared
         */
        public XmlVersionChangedEvent(final String oldXmlVersion, final String newXmlVersion) {
            super(oldXmlVersion, newXmlVersion);
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getOldXmlVersion() {
            return this.getOldThing();
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getNewXmlVersion() {
            return this.getNewThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.xmlVersionChanged(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public final class XmlStandaloneChangedEvent extends AbstractThingChangedEvent<Boolean> {

        /**
         *
         * @param oldXmlStandalone
         * <br>Maybe null
         * <br>Shared
         * @param newXmlStandalone
         * <br>Maybe null
         * <br>Shared
         */
        public XmlStandaloneChangedEvent(final boolean oldXmlStandalone, final boolean newXmlStandalone) {
            super(oldXmlStandalone, newXmlStandalone);
        }

        public final boolean getOldXmlStandalone() {
            return this.getOldThing();
        }

        public final boolean getNewXmlStandalone() {
            return this.getNewThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.xmlStandaloneChanged(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public static interface Listener {

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void userDataChanged(UserDataChangedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void childReplaced(ChildReplacedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void nodeNormalized(NodeNormalizedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void childAppended(ChildAppendedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void xmlVersionChanged(XmlVersionChangedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void xmlStandaloneChanged(XmlStandaloneChangedEvent event);

    }

}