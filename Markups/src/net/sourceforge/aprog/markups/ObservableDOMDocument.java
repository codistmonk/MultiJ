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
import net.sourceforge.aprog.events.Observable;
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
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 *
 * @author codistmonk (creation 2010-07-03)
 */
public final class ObservableDOMDocument extends AbstractProxyDOMNode<Document, ObservableDOMDocument.Listener> implements Document {

    /**
     *
     * @param document
     * <br>Not null
     * <br>Shared
     */
    public ObservableDOMDocument(final Document document) {
        super(document);
    }

    public ObservableDOMDocument() {
        this(XMLTools.newDocument());
    }

    @Override
    public final Object setUserData(final String key, final Object data, final UserDataHandler handler) {
        final Object oldData = this.getDelegate().setUserData(key, data, handler);

        if (oldData != data) {
            this.new UserDataChangedEvent(this, key, oldData, data).fire();
        }

        return oldData;
    }

    @Override
    public final void setTextContent(final String textContent) throws DOMException {
        this.getDelegate().setTextContent(textContent);

        // According to the documentation, this method shouldn't have any effect
        assert this.getTextContent() == null;
    }

    @Override
    public final void setPrefix(final String prefix) throws DOMException {
        this.getDelegate().setPrefix(prefix);

        // According to the documentation, this method shouldn't have any effect
        assert this.getPrefix() == null;
    }

    @Override
    public final void setNodeValue(final String nodeValue) throws DOMException {
        this.getDelegate().setNodeValue(nodeValue);

        // According to the documentation, this method shouldn't have any effect
        assert this.getNodeValue() == null;
    }

    @Override
    public final Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        this.getDelegate().replaceChild(newChild, oldChild);

        this.new ChildReplacedEvent(this, oldChild, newChild).fire();

        return oldChild;
    }

    @Override
    public final Node removeChild(final Node oldChild) throws DOMException {
        // According to the documentation, this method shouldn't modify this object
        return this.getDelegate().removeChild(oldChild);
    }

    @Override
    public final void normalize() {
        this.getDelegate().normalize();

        this.new NodeNormalizedEvent(this).fire();
    }

    @Override
    public final Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        // I'm not sure about this one,
        // but I think the documentation says that
        // this method shouldn't modify this object
        return this.getDelegate().insertBefore(newChild, refChild);
    }

    @Override
    public final Node cloneNode(final boolean deep) {
        return new ObservableDOMDocument((Document) this.getDelegate().cloneNode(deep));
    }

    @Override
    public final Node appendChild(final Node newChild) throws DOMException {
        this.getDelegate().appendChild(newChild);
        
        this.new ChildAppendedEvent(this, newChild).fire();

        return newChild;
    }

    @Override
    public final void setXmlVersion(final String xmlVersion) throws DOMException {
        final String oldXmlVersion = this.getXmlVersion();

        if (!Tools.equals(oldXmlVersion, xmlVersion)) {
            this.getDelegate().setXmlVersion(xmlVersion);

            this.new XmlVersionChangedEvent(oldXmlVersion, xmlVersion).fire();
        }
    }

    @Override
    public final void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
        final boolean oldXmlStandalone = this.getXmlStandalone();

        if (oldXmlStandalone != xmlStandalone) {
            this.getDelegate().setXmlStandalone(xmlStandalone);

            this.new XmlStandaloneChangedEvent(oldXmlStandalone, xmlStandalone).fire();
        }
    }

    @Override
    public final void setStrictErrorChecking(final boolean strictErrorChecking) {
        final boolean oldStrictErrorChecking = this.getStrictErrorChecking();

        if (oldStrictErrorChecking != strictErrorChecking) {
            this.getDelegate().setStrictErrorChecking(strictErrorChecking);

            this.new StrictErrorCheckingChangedEvent(oldStrictErrorChecking, strictErrorChecking).fire();
        }
    }

    @Override
    public final void setDocumentURI(final String documentURI) {
        final String oldDocumentURI = this.getDocumentURI();

        if (!Tools.equals(oldDocumentURI, documentURI)) {
            this.getDelegate().setDocumentURI(documentURI);

            this.new DocumentURIChangedEvent(oldDocumentURI, documentURI).fire();
        }
    }

    @Override
    public final Node renameNode(final Node node, final String namespaceURI, final String qualifiedName) throws DOMException {
        final String oldNamespaceURI = node.getNamespaceURI();
        final String oldQualifiedName = getQualifiedName(node);

        if (!Tools.equals(oldNamespaceURI, namespaceURI) || !Tools.equals(oldQualifiedName, qualifiedName)) {
            final Node newNode = this.getDelegate().renameNode(node, namespaceURI, qualifiedName);

            this.new NodeRenamedEvent(node, oldNamespaceURI, oldQualifiedName, newNode, namespaceURI, qualifiedName).fire();

            return newNode;
        }

        return node;
    }

    @Override
    public final void normalizeDocument() {
        this.getDelegate().normalizeDocument();

        this.new DocumentNormalizedEvent().fire();
    }

    @Override
    public final Node importNode(final Node node, final boolean deep) throws DOMException {
        final Node result = this.importSilently(node, deep);

        this.new NodeImportedEvent(result).fire();

        return result;
    }

    /**
     *
     * @param foreignNode
     * <br>Not null
     * @param deep
     * @return
     * <br>Not null
     * <br>New
     */
    private final AbstractProxyNode<? extends Node> importSilently(final Node foreignNode, final boolean deep) {
        switch (foreignNode.getNodeType()) {
            case ATTRIBUTE_NODE:
            case DOCUMENT_FRAGMENT_NODE:
            case DOCUMENT_NODE:
            case DOCUMENT_TYPE_NODE:
                throw new RuntimeException("TODO");
            case ELEMENT_NODE:
                final ProxyElement result = this.new ProxyElement(this.getDelegate().createElement(((Element) foreignNode).getTagName()));

                if (deep) {
                    // TODO
                }

                return result;
            case ENTITY_NODE:
            case ENTITY_REFERENCE_NODE:
            case NOTATION_NODE:
            case PROCESSING_INSTRUCTION_NODE:
            case TEXT_NODE:
            case CDATA_SECTION_NODE:
            case COMMENT_NODE:
                throw new RuntimeException("TODO");
            default:
                break;
        }

        throw new IllegalArgumentException("Unknown node type: " + foreignNode.getNodeType());
    }

    @Override
    public final String getXmlVersion() {
        return this.getDelegate().getXmlVersion();
    }

    @Override
    public final boolean getXmlStandalone() {
        return this.getDelegate().getXmlStandalone();
    }

    @Override
    public final String getXmlEncoding() {
        return this.getDelegate().getXmlEncoding();
    }

    @Override
    public final boolean getStrictErrorChecking() {
        return this.getDelegate().getStrictErrorChecking();
    }

    @Override
    public final String getInputEncoding() {
        return this.getDelegate().getInputEncoding();
    }

    @Override
    public final DOMImplementation getImplementation() {
        return this.getDelegate().getImplementation();
    }

    @Override
    public final NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
        return this.getDelegate().getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public final NodeList getElementsByTagName(final String tagname) {
        return this.getDelegate().getElementsByTagName(tagname);
    }

    @Override
    public final Element getElementById(final String elementId) {
        return this.getDelegate().getElementById(elementId);
    }

    @Override
    public final DOMConfiguration getDomConfig() {
        return this.getDelegate().getDomConfig();
    }

    @Override
    public final String getDocumentURI() {
        return this.getDelegate().getDocumentURI();
    }

    @Override
    public final Element getDocumentElement() {
        return this.getDelegate().getDocumentElement();
    }

    @Override
    public final DocumentType getDoctype() {
        return this.getDelegate().getDoctype();
    }

    @Override
    public final Text createTextNode(String data) {
        return this.getDelegate().createTextNode(data);
    }

    @Override
    public final ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return this.getDelegate().createProcessingInstruction(target, data);
    }

    @Override
    public final EntityReference createEntityReference(String name) throws DOMException {
        return this.getDelegate().createEntityReference(name);
    }

    @Override
    public final Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return this.getDelegate().createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    public final Element createElement(String tagName) throws DOMException {
        return this.getDelegate().createElement(tagName);
    }

    @Override
    public final DocumentFragment createDocumentFragment() {
        return this.getDelegate().createDocumentFragment();
    }

    @Override
    public final Comment createComment(final String data) {
        return this.getDelegate().createComment(data);
    }

    @Override
    public final CDATASection createCDATASection(final String data) throws DOMException {
        return this.getDelegate().createCDATASection(data);
    }

    @Override
    public final Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        return this.getDelegate().createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    public final Attr createAttribute(final String name) throws DOMException {
        return this.getDelegate().createAttribute(name);
    }

    @Override
    public final Node adoptNode(final Node source) throws DOMException {
        return this.getDelegate().adoptNode(source);
    }

    @Override
    public final ObservableDOMDocument getOwnerDocument() {
        return this;
    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public abstract class AbstractEvent extends AbstractObservable<Listener>.AbstractEvent<ObservableDOMDocument, Listener> {

        private final Node eventNode;

        /**
         *
         * @param eventNode
         * <br>Not null
         * <br>Shared
         */
        public AbstractEvent(final Node eventNode) {
            this.eventNode = eventNode;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getEventNode() {
            return this.eventNode;
        }

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
         * @param eventNode
         * <br>Not null
         * <br>Shared
         * @param oldThing
         * <br>Maybe null
         * <br>Shared
         * @param newThing
         * <br>Maybe null
         * <br>Shared
         */
        public AbstractThingChangedEvent(final Node eventNode, final T oldThing, final T newThing) {
            super(eventNode);
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
         * @param eventNode
         * <br>Not null
         * <br>Shared
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
        public UserDataChangedEvent(final Node eventNode,
                final String key, final Object oldUserData, final Object newUserData) {
            super(eventNode, oldUserData, newUserData);
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
         * @param eventNode
         * <br>Not null
         * <br>Shared
         * @param oldChild
         * <br>Not null
         * <br>Shared
         * @param newChild
         * <br>Not null
         * <br>Shared
         */
        public ChildReplacedEvent(final Node eventNode, final Node oldChild, final Node newChild) {
            super(eventNode, oldChild, newChild);
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

        /**
         *
         * @param node
         * <br>Not null
         * <br>Shared
         */
        public NodeNormalizedEvent(final Node node) {
            super(node);
        }

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
         * @param eventNode
         * <br>Not null
         * <br>Shared
         * @param appendedChild
         * <br>Not null
         * <br>Shared
         */
        public ChildAppendedEvent(final Node eventNode, final Node appendedChild) {
            super(eventNode);
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
            super(ObservableDOMDocument.this, oldXmlVersion, newXmlVersion);
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
            super(ObservableDOMDocument.this, oldXmlStandalone, newXmlStandalone);
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
     * @author codistmonk (creation 2010-07-04)
     */
    public final class StrictErrorCheckingChangedEvent extends AbstractThingChangedEvent<Boolean> {

        /**
         *
         * @param oldStrictErrorChecking
         * <br>Maybe null
         * <br>Shared
         * @param newStrictErrorChecking
         * <br>Maybe null
         * <br>Shared
         */
        public StrictErrorCheckingChangedEvent(final boolean oldStrictErrorChecking, final boolean newStrictErrorChecking) {
            super(ObservableDOMDocument.this, oldStrictErrorChecking, newStrictErrorChecking);
        }

        public final boolean getOldStrictErrorChecking() {
            return this.getOldThing();
        }

        public final boolean getNewStrictErrorChecking() {
            return this.getNewThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.strictErrorCheckingChanged(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public final class DocumentURIChangedEvent extends AbstractThingChangedEvent<String> {

        /**
         *
         * @param oldDocumentURI
         * <br>Maybe null
         * <br>Shared
         * @param newDocumentURI
         * <br>Maybe null
         * <br>Shared
         */
        public DocumentURIChangedEvent(final String oldDocumentURI, final String newDocumentURI) {
            super(ObservableDOMDocument.this, oldDocumentURI, newDocumentURI);
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getOldDocumentURI() {
            return this.getOldThing();
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getNewDocumentURI() {
            return this.getNewThing();
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.documentURIChanged(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    public final class NodeRenamedEvent extends AbstractThingChangedEvent<Node> {

        private final String oldNamespaceURI;

        private final String oldQualifiedName;

        private final String newNamespaceURI;

        private final String newQualifiedName;

        /**
         *
         * @param oldNode
         * <br>Not null
         * <br>Shared
         * @param oldNamespaceURI
         * <br>Maybe null
         * <br>Shared
         * @param oldQualifiedName
         * <br>Not null
         * <br>Shared
         * @param newNode
         * <br>Not null
         * <br>Shared
         * @param newNamespaceURI
         * <br>Maybe null
         * <br>Shared
         * @param newQualifiedName
         * <br>Not null
         * <br>Shared
         */
        public NodeRenamedEvent(
                final Node oldNode, final String oldNamespaceURI, final String oldQualifiedName,
                final Node newNode, final String newNamespaceURI, final String newQualifiedName) {
            super(ObservableDOMDocument.this, oldNode, newNode);
            this.oldNamespaceURI = oldNamespaceURI;
            this.oldQualifiedName = oldQualifiedName;
            this.newNamespaceURI = newNamespaceURI;
            this.newQualifiedName = newQualifiedName;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getOldNode() {
            return this.getOldThing();
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getOldNamespaceURI() {
            return this.oldNamespaceURI;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final String getOldQualifiedName() {
            return this.oldQualifiedName;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getNewNode() {
            return this.getNewThing();
        }

        /**
         *
         * @return
         * <br>Maybe null
         * <br>Shared
         */
        public final String getNewNamespaceURI() {
            return this.newNamespaceURI;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final String getNewQualifiedName() {
            return this.newQualifiedName;
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.nodeRenamed(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class DocumentNormalizedEvent extends AbstractEvent {

        public DocumentNormalizedEvent() {
            super(ObservableDOMDocument.this);
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.documentNormalized(this);
        }

    }

    /**
     *
     * @author codistmonk (creation 2010-07-03)
     */
    public final class NodeImportedEvent extends AbstractEvent {

        private final Node importedNode;

        /**
         *
         * @param importedNode
         * <br>Not null
         * <br>Shared
         */
        public NodeImportedEvent(final Node importedNode) {
            super(ObservableDOMDocument.this);
            this.importedNode = importedNode;
        }

        /**
         *
         * @return
         * <br>Not null
         * <br>Shared
         */
        public final Node getImportedNode() {
            return this.importedNode;
        }

        @Override
        protected final void notifyListener(final Listener listener) {
            listener.nodeImported(this);
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

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void strictErrorCheckingChanged(StrictErrorCheckingChangedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void documentURIChanged(DocumentURIChangedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void nodeRenamed(NodeRenamedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void documentNormalized(DocumentNormalizedEvent event);

        /**
         *
         * @param event
         * <br>Not null
         */
        public abstract void nodeImported(NodeImportedEvent event);

    }

    /**
     * 
     * @param node
     * <br>Not null
     * @return
     * <br>Not null
     */
    public static final String getQualifiedName(final Node node) {
        return node.getPrefix() == null ? node.getNodeName() : node.getPrefix() + ":" + node.getLocalName();
    }

    /**
     *
     * @author codistmonk (2010-07-04)
     *
     * @param <N> The actual delegate type
     */
    private abstract class AbstractProxyNode<N extends Node> extends AbstractProxyDOMNode<N, Void> {

        /**
         * 
         * @param delegate
         * <br>Not null
         * <br>Shared
         */
        protected AbstractProxyNode(final N delegate) {
            super(delegate);
        }

        @Override
        public final Document getOwnerDocument() {
            return ObservableDOMDocument.this;
        }

        @Override
        public final void setNodeValue(final String nodeValue) throws DOMException {
            // TODO event
            this.getDelegate().setNodeValue(nodeValue);
        }

        @Override
        public final Node insertBefore(final Node newChild, final Node referenceChild) throws DOMException {
            this.checkOwnerDocument(newChild);
            this.checkOwnerDocument(referenceChild);

            // TODO event
            return this.getDelegate().insertBefore(newChild, referenceChild);
        }

        @Override
        public final Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
            this.checkOwnerDocument(newChild);
            this.checkOwnerDocument(oldChild);

            if (oldChild != newChild) {
                this.getDelegate().replaceChild(newChild, oldChild);

                ObservableDOMDocument.this.new ChildReplacedEvent(this, oldChild, newChild).fire();
            }

            return oldChild;
        }

        @Override
        public final Node removeChild(final Node oldChild) throws DOMException {
            this.checkOwnerDocument(oldChild);
            // TODO event
            return this.getDelegate().removeChild(oldChild);
        }

        @Override
        public final Node appendChild(final Node newChild) throws DOMException {
            this.checkOwnerDocument(newChild);

            this.getDelegate().appendChild(newChild);

            ObservableDOMDocument.this.new ChildAppendedEvent(this, newChild).fire();

            return newChild;
        }

        @Override
        public void normalize() {
            this.getDelegate().normalize();

            ObservableDOMDocument.this.new NodeNormalizedEvent(this).fire();
        }

        @Override
        public final void setPrefix(final String prefix) throws DOMException {
            // TODO event
            this.getDelegate().setPrefix(prefix);
        }

        @Override
        public final void setTextContent(final String textContent) throws DOMException {
            // TODO event
            this.getDelegate().setTextContent(textContent);
        }

        @Override
        public final Object setUserData(final String key, final Object data, final UserDataHandler handler) {
            final Object oldData = this.getDelegate().setUserData(key, data, handler);

            if (oldData != data) {
                ObservableDOMDocument.this.new UserDataChangedEvent(this, key, oldData, data).fire();
            }

            return oldData;
        }

        /**
         *
         * @param node
         * <br>Not null
         * @throws IllegalArgumentException If {@code node}'s owner document is not {@code this.getOwnerDocument()}
         */
        protected final void checkOwnerDocument(final Node node) {
            if (node.getOwnerDocument() != this.getOwnerDocument()) {
                throw new IllegalArgumentException("The node belongs to another document: " + node);
            }
        }
        
    }

    /**
     *
     * @author codistmonk (creation 2010-07-04)
     */
    private final class ProxyElement extends AbstractProxyNode<Element> implements Element {

        /**
         *
         * @param delegate
         * <br>Not null
         * <br>Shared
         */
        public ProxyElement(final Element delegate) {
            super(delegate);
        }

        @Override
        public Node cloneNode(boolean deep) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public final String getTagName() {
            return this.getDelegate().getTagName();
        }

        @Override
        public final String getAttribute(final String name) {
            return this.getDelegate().getAttribute(name);
        }

        @Override
        public void setAttribute(String name, String value) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeAttribute(String name) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public final Attr getAttributeNode(final String name) {
            return this.getDelegate().getAttributeNode(name);
        }

        @Override
        public Attr setAttributeNode(Attr newAttr) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public final NodeList getElementsByTagName(final String name) {
            return this.getDelegate().getElementsByTagName(name);
        }

        @Override
        public final String getAttributeNS(final String namespaceURI, final String localName) throws DOMException {
            return this.getDelegate().getAttributeNS(namespaceURI, localName);
        }

        @Override
        public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public final Attr getAttributeNodeNS(final String namespaceURI, final String localName) throws DOMException {
            return this.getDelegate().getAttributeNodeNS(namespaceURI, localName);
        }

        @Override
        public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public final NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) throws DOMException {
            return this.getDelegate().getElementsByTagNameNS(namespaceURI, localName);
        }

        @Override
        public final boolean hasAttribute(final String name) {
            return this.getDelegate().hasAttribute(name);
        }

        @Override
        public final boolean hasAttributeNS(final String namespaceURI, final String localName) throws DOMException {
            return this.getDelegate().hasAttributeNS(namespaceURI, localName);
        }

        @Override
        public final TypeInfo getSchemaTypeInfo() {
            return this.getDelegate().getSchemaTypeInfo();
        }

        @Override
        public void setIdAttribute(String name, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

}

/**
 *
 * @author codistmonk (creation 2010-07-04)
 *
 * @param <L> The listener type
 */
abstract class AbstractProxyDOMNode<N extends Node, L> extends AbstractObservable<L> implements Node {

    private final N delegate;

    /**
     *
     * @param delegate
     * <br>Not null
     * <br>Shared
     */
    AbstractProxyDOMNode(final N delegate) {
        this.delegate = delegate;
    }

    @Override
    public final String lookupPrefix(final String namespaceURI) {
        return this.getDelegate().lookupPrefix(namespaceURI);
    }

    @Override
    public final String lookupNamespaceURI(final String prefix) {
        return this.getDelegate().lookupNamespaceURI(prefix);
    }

    @Override
    public final boolean isSupported(final String feature, final String version) {
        return this.getDelegate().isSupported(feature, version);
    }

    @Override
    public final boolean isSameNode(final Node other) {
        return this.getDelegate().isSameNode(other);
    }

    @Override
    public final boolean isEqualNode(final Node other) {
        return this.getDelegate().isEqualNode(other);
    }

    @Override
    public final boolean isDefaultNamespace(final String namespaceURI) {
        return this.getDelegate().isDefaultNamespace(namespaceURI);
    }

    @Override
    public final boolean hasChildNodes() {
        return this.getDelegate().hasChildNodes();
    }

    @Override
    public final boolean hasAttributes() {
        return this.getDelegate().hasAttributes();
    }

    @Override
    public final Object getUserData(final String key) {
        return this.getDelegate().getUserData(key);
    }

    @Override
    public final String getTextContent() throws DOMException {
        return this.getDelegate().getTextContent();
    }

    @Override
    public final Node getPreviousSibling() {
        return this.getDelegate().getPreviousSibling();
    }

    @Override
    public final String getPrefix() {
        return this.getDelegate().getPrefix();
    }

    @Override
    public final Node getParentNode() {
        return this.getDelegate().getParentNode();
    }

    @Override
    public final String getNodeValue() throws DOMException {
        return this.getDelegate().getNodeValue();
    }

    @Override
    public final short getNodeType() {
        return this.getDelegate().getNodeType();
    }

    @Override
    public final String getNodeName() {
        return this.getDelegate().getNodeName();
    }

    @Override
    public final Node getNextSibling() {
        return this.getDelegate().getNextSibling();
    }

    @Override
    public final String getNamespaceURI() {
        return this.getDelegate().getNamespaceURI();
    }

    @Override
    public final String getLocalName() {
        return this.getDelegate().getLocalName();
    }

    @Override
    public final Node getLastChild() {
        return this.getDelegate().getLastChild();
    }

    @Override
    public final Node getFirstChild() {
        return this.getDelegate().getFirstChild();
    }

    @Override
    public final Object getFeature(final String feature, final String version) {
        return this.getDelegate().getFeature(feature, version);
    }

    @Override
    public final NodeList getChildNodes() {
        return this.getDelegate().getChildNodes();
    }

    @Override
    public final String getBaseURI() {
        return this.getDelegate().getBaseURI();
    }

    @Override
    public final NamedNodeMap getAttributes() {
        return this.getDelegate().getAttributes();
    }

    @Override
    public final short compareDocumentPosition(final Node other) throws DOMException {
        return this.getDelegate().compareDocumentPosition(other);
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>Shared
     */
    protected final N getDelegate() {
        return this.delegate;
    }

}