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

package multij.xml;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static multij.tools.Tools.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import multij.tools.IllegalInstantiationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author codistmonk (creation 2010-07-02)
 */
public final class XMLTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private XMLTools() {
        throw new IllegalInstantiationException();
    }

    /**
     * {@value}.
     */
    public static final String XML_1_0_UTF8 =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    /**
     * {@value}.
     */
    public static final String XML_1_0_UTF8_STANDALONE_NO =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * {@value}.
     */
    public static final String XML_1_0_UTF8_STANDALONE_YES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_SUBTREE_MODIFIED = "DOMSubtreeModified";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_INSERTED = "DOMNodeInserted";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_REMOVED = "DOMNodeRemoved";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_ATTRIBUTE_MODIFIED = "DOMAttrModified";

    /**
     * {@value}.
     */
    public static final String DOM_EVENT_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";

    public static final List<String> DOM_EVENT_TYPES = unmodifiableList(asList(
            DOM_EVENT_ATTRIBUTE_MODIFIED,
            DOM_EVENT_CHARACTER_DATA_MODIFIED,
            DOM_EVENT_NODE_INSERTED,
            DOM_EVENT_NODE_INSERTED_INTO_DOCUMENT,
            DOM_EVENT_NODE_REMOVED,
            DOM_EVENT_NODE_REMOVED_FROM_DOCUMENT,
            DOM_EVENT_SUBTREE_MODIFIED
    ));

    /**
     *
     * @param node
     * <br>Not null
     * <br>Input-output
     * @param listener
     * <br>Not null
     * <br>Shared
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void addDOMEventListener(final Node node, final EventListener listener) {
        checkHasEventsFeature(node);

        for (final String eventType : DOM_EVENT_TYPES) {
            ((EventTarget) node).addEventListener(eventType, listener, false);
        }
    }

    /**
     *
     * @param node
     * <br>Not null
     * <br>Input-output
     * @param listener
     * <br>Not null
     * <br>Shared
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void removeDOMEventListener(final Node node, final EventListener listener) {
        checkHasEventsFeature(node);

        for (final String eventType : DOM_EVENT_TYPES) {
            ((EventTarget) node).removeEventListener(eventType, listener, false);
        }
    }

    /**
     *
     * @param node
     * <br>Not null
     * @param namespaceURI
     * <br>Maybe null
     * @param qualifiedName
     * <br>Not null
     * @return
     * <br>Not null
     * <br>Maybe new
     */
    public static final Node rename(final Node node, final String namespaceURI, final String qualifiedName) {
        final String oldQualifiedName = getQualifiedName(node);
        final Document document = getOwnerDocument(node);
        final Node result = document.renameNode(node, namespaceURI, qualifiedName);

        if (hasEventsFeature(node) && node.getNodeType() == Node.ELEMENT_NODE) {
            final MutationEvent event = (MutationEvent) ((DocumentEvent) document).createEvent("MutationEvent");

            event.initMutationEvent(DOM_EVENT_SUBTREE_MODIFIED, true, true,
                    node, oldQualifiedName, qualifiedName,
                    null, (short) 0);

            ((EventTarget) result).dispatchEvent(event);
        }

        return result;
    }

    /**
     *
     * @param node
     * <br>Not null
     * @throws IllegalArgumentException If {@code node} doesn't have the "events 2.0" feature
     */
    public static final void checkHasEventsFeature(final Node node) {
        if (!hasEventsFeature(node)) {
            throw new IllegalArgumentException("Events 2.0 feature unavailable for node " + node);
        }
    }

    /**
     *
     * @param node
     * <br>Not null
     * @return {@code true} if {@code node} has the "events 2.0" feature
     */
    public static final boolean hasEventsFeature(final Node node) {
        return XMLTools.getOwnerDocument(node).getImplementation().hasFeature("events", "2.0");
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
     * @param xmlInput
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     * @throws RuntimeException if an error occurs
     */
    public static final Document parse(final String xmlInput) {
        return parse(new ByteArrayInputStream(xmlInput.getBytes()));
    }

    /**
     *
     * @param xmlInputStream
     * <br>Not null
     * <br>Input-output
     * @return
     * <br>Not null
     * <br>New
     * @throws RuntimeException if an error occurs
     */
    public static final Document parse(final InputStream xmlInputStream) {
        return parse(new InputSource(xmlInputStream));
    }

    /**
     *
     * @param inputSource
     * <br>Not null
     * <br>Input-output
     * @return
     * <br>Not null
     * <br>New
     * @throws RuntimeException if an error occurs
     */
    public static final Document parse(final InputSource inputSource) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
        } catch (final Exception exception) {
            throw unchecked(exception);
        }
    }

    /**
     *
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Document newDocument() {
        try {
            final Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            result.setXmlStandalone(true);

            return result;
        } catch (final ParserConfigurationException exception) {
            throw unchecked(exception);
        }
    }

    /**
     *
     * @param document
     * <br>Not null
     * <br>Input-output
     * @return {@code document}
     * <br>Not null
     */
    public static final Document normalize(final Document document) {
        document.normalize();

        return document;
    }

    /**
     *
     * @param document
     * <br>Not null
     * <br>Input-output
     * @return {@code document}
     * <br>Not null
     */
    public static final Document standalone(final Document document) {
        document.setXmlStandalone(true);

        return document;
    }

    /**
     *
     * @param node
     * <br>Not null
     * @param outputFile
     * <br>Not null
     * <br>Input-output
     * @param indent
     * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
     */
    public static final void write(final Node node, final File outputFile, final int indent) {
        write(node, new StreamResult(outputFile.getAbsolutePath()), indent);
    }

    /**
     *
     * @param node
     * <br>Not null
     * @param output
     * <br>Not null
     * <br>Input-output
     * @param indent
     * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
     */
    public static final void write(final Node node, final OutputStream output, final int indent) {
        write(node, new StreamResult(new OutputStreamWriter(output)), indent);
    }

    /**
     *
     * @param node
     * <br>Not null
     * @param output
     * <br>Not null
     * <br>Input-output
     * @param indent
     * <br>Range: {@code [0 .. Integer.MAX_VALUE]}
     */
    public static final void write(final Node node, final Result output, final int indent) {
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();

            transformerFactory.setAttribute("indent-number", indent);

            final Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, indent != 0 ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    node instanceof Document ? "no" : "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            transformer.transform(new DOMSource(node), output);
        } catch (final Exception exception) {
            throw unchecked(exception);
        }
    }

    /**
     *
     * @param resourceName
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Source getResourceAsSource(final String resourceName) {
        final ClassLoader classLoader = getCallerClass().getClassLoader();
        final Source result = new StreamSource(classLoader.getResourceAsStream(resourceName));

        result.setSystemId(classLoader.getResource(resourceName).toString());

        return result;
    }

    /**
     *
     * @param nodeList
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final List<Node> toList(final NodeList nodeList) {
        final List<Node> result = new ArrayList<Node>();

        for (int i = 0; i < nodeList.getLength(); ++i) {
            result.add(nodeList.item(i));
        }

        return result;
    }

    /**
     * Validates the XML input against the specified DTD or schema.
     *
     * @param xmlInputStream
     * <br>Not null
     * @param dtdOrSchema
     * <br>Not null
     * @return An empty list if validation succeeds
     * <br>Not null
     * <br>New
     */
    public static final List<Throwable> validate(final InputStream xmlInputStream, final Source dtdOrSchema) {
        final List<Throwable> exceptions = new ArrayList<Throwable>();
        final String schemaLanguage = getSchemaLanguage(dtdOrSchema);

        try {
            if (schemaLanguage != null) {
                final Validator validator = SchemaFactory.newInstance(schemaLanguage)
                        .newSchema(dtdOrSchema).newValidator();

                validator.validate(new StreamSource(xmlInputStream));
            } else {
                final Transformer addDoctypeInformation = TransformerFactory.newInstance().newTransformer();

                addDoctypeInformation.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdOrSchema.getSystemId());

                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                addDoctypeInformation.transform(new StreamSource(xmlInputStream), new StreamResult(buffer));

                final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

                saxParserFactory.setValidating(true);
                saxParserFactory.newSAXParser().parse(
                        new ByteArrayInputStream(buffer.toByteArray()), new DefaultHandler() {

                    @Override
                    public final void error(final SAXParseException exception) throws SAXException {
                        exceptions.add(exception);

                        getLoggerForThisMethod().log(Level.WARNING, "", exceptions);
                    }

                    @Override
                    public final void fatalError(final SAXParseException exception) throws SAXException {
                        exceptions.add(exception);

                        getLoggerForThisMethod().log(Level.WARNING, "", exceptions);

                        throw exception;
                    }

                    @Override
                    public final void warning(final SAXParseException exception) throws SAXException {
                        getLoggerForThisMethod().log(Level.WARNING, "", exceptions);
                    }

                });
            }
        } catch (final TransformerConfigurationException exception) {
            throw unchecked(exception);
        } catch (final TransformerException exception) {
            exceptions.add(exception);
        } catch (final SAXException exception) {
            exceptions.add(exception);
        } catch (final Exception exception) {
            throw unchecked(exception);
        }

        return exceptions;
    }

    /**
     * Determines the schema language from the argument's system id.
     *
     * @param dtdOrSchema
     * <br>Not null
     * @return {@code null} for a DTD
     * <br>Maybe null
     * <br>Range: { {@code null},
     * {@link XMLConstants#W3C_XML_SCHEMA_NS_URI}, {@link XMLConstants#RELAXNG_NS_URI} }
     * @throws IllegalArgumentException If {@code dtdOrSchema}'s system id does not indicate
     * a DTD or a schema (XSD or RNG)
     */
    public static final String getSchemaLanguage(final Source dtdOrSchema) {
        final String dtdOrSchemaId = dtdOrSchema.getSystemId().toLowerCase(Locale.ENGLISH);

        if (dtdOrSchemaId.endsWith(".xsd")) {
            return XMLConstants.W3C_XML_SCHEMA_NS_URI;
        } else if (dtdOrSchemaId.endsWith(".rng") || dtdOrSchemaId.endsWith(".rnc")) {
            return XMLConstants.RELAXNG_NS_URI;
        } else if (dtdOrSchemaId.endsWith(".dtd")) {
            return null;
        }

        throw new IllegalArgumentException("Unsupported extension for " + dtdOrSchema +
                " (the name must end with .xsd, .rng, .rnc or .dtd (case-insensitive))");
    }

    /**
     * Calls {@link #getNode(java.lang.Object, java.lang.String)}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final Node getNode(final Node context, final String xPath) {
        return getNode((Object) context, xPath);
    }

    /**
     * Calls {@link #getNodeSet(java.lang.Object, java.lang.String)}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final NodeList getNodeList(final Node context, final String xPath) {
        return getNodeSet((Object) context, xPath);
    }

    /**
     * Calls {@link #toList(NodeList)} with the value returned by {@link #getNodeList(Node, String)}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final List<Node> getNodes(final Node context, final String xPath) {
        return toList(getNodeList(context, xPath));
    }

    /**
     * Calls {@link #get(java.lang.Object, java.lang.String, javax.xml.namespace.QName)}
     * with {@code returnType == XPathConstants.NODE}.
     *
     * @param <N> The expected node type
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    @SuppressWarnings("unchecked")
    public static final <N> N getNode(final Object context, final String xPath) {
        return (N) get(context, xPath, XPathConstants.NODE);
    }

    /**
     * Calls {@link #get(java.lang.Object, java.lang.String, javax.xml.namespace.QName)}
     * with {@code returnType == XPathConstants.NODESET}.
     *
     * @param <S> The expected node set type
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    @SuppressWarnings("unchecked")
    public static final <S> S getNodeSet(final Object context, final String xPath) {
        return (S) get(context, xPath, XPathConstants.NODESET);
    }

    /**
     * Calls {@link #get(java.lang.Object, java.lang.String, javax.xml.namespace.QName)}
     * with {@code returnType == XPathConstants.BOOLEAN}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final Boolean getBoolean(final Object context, final String xPath) {
        return (Boolean) get(context, xPath, XPathConstants.BOOLEAN);
    }

    /**
     * Calls {@link #get(java.lang.Object, java.lang.String, javax.xml.namespace.QName)}
     * with {@code returnType == XPathConstants.NUMBER}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final Number getNumber(final Object context, final String xPath) {
        return (Number) get(context, xPath, XPathConstants.NUMBER);
    }

    /**
     * Calls {@link #get(java.lang.Object, java.lang.String, javax.xml.namespace.QName)}
     * with {@code returnType == XPathConstants.STRING}.
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final String getString(final Object context, final String xPath) {
        return (String) get(context, xPath, XPathConstants.STRING);
    }

    /**
     * Evaluates the compiled XPath expression in the specified context
     * and returns the result as the specified type.
     *
     * @param <T> The expected return type
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @param returnType
     * <br>Not null
     * <br>Range: {
     *   {@link XPathConstants#BOOLEAN},
     *   {@link XPathConstants#NODE},
     *   {@link XPathConstants#NODESET},
     *   {@link XPathConstants#NUMBER},
     *   {@link XPathConstants#STRING}
     * }
     * @return
     * <br>Maybe null
     * @throws RuntimeException If an error occurs
     */
    @SuppressWarnings("unchecked")
    public static final <T> T get(final Object context, final String xPath, final QName returnType) {
        try {
            return (T) XPathFactory.newInstance().newXPath().compile(xPath)
                    .evaluate(context, returnType);
        } catch (final Exception exception) {
            throw unchecked(exception);
        }
    }

    /**
     * Evaluates the quasi-XPath expression in the specified context, and returns the corresponding node,
     * creating it if necessary.
     * <br>The second argument is called "quasi-XPath" because it allows non-XPath expressions
     * like {@code "a/b[]"} which means "add an element b at the end of a".
     * <br>If {@code quasiXPath} is a standard XPath expression and the corresponding node exists,
     * then that node is returned.
     * <br>When the node does not exist,
     * {@code quasiXPath} is broken down into path elements separated by slashes ("/").
     * <br>A path element can be created if it is of the form "@name", "name", "name[]" or "name[attributes]" where
     * "atributes" must be a sequence of "@attribute=value" separated by "and".
     * <br>Example of a valid quasi-XPath expression where each path element can be created if necessary:<ul>
     *  <li>{@code "a/b[]/c[@d='e' and @f=42]"}
     * </ul>
     * <br>Example of a valid quasi-XPath expression where
     * the path elements cannot be created if they don't exist:<ul>
     *  <li>{@code "a[last()]/b[@c<42]/d[position()=33]/e['f'=@g]"}
     * </ul>
     *
     *
     * @param context
     * <br>Not null
     * @param quasiXPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Maybe new
     */
    public static final Node getOrCreateNode(final Node context, final String quasiXPath) {
        final String[] pathElements = quasiXPath.split("/");
        Node result = context;

        try {
            for (int index = 0; index < pathElements.length && result != null; ++index) {
                final String pathElement = pathElements[index];

                if (pathElement.matches("\\w+\\[\\]")) {
                    result = addChild(result, pathElement);
                } else {
                    final Node parent = result;
                    result = getNode(result, pathElement);

                    if (result == null) {
                        final Map<String, String> attributes = getEqualityPredicates(pathElement);
                        final Integer nameEnd = pathElement.indexOf("[");
                        final String childName = nameEnd < 0 ? pathElement : pathElement.substring(0, nameEnd);

                        result = addChild(parent, childName);

                        for (final Map.Entry<String, String> entry : attributes.entrySet()) {
                            addChild(result, entry.getKey()).setNodeValue(entry.getValue());
                        }
                    }
                }
            }
        } catch (final Exception exception) {
            throw unchecked(exception);
        }

        return result;
    }

    /**
     * Creates and adds a new node to {@code parent}.
     * <br>If {@code xPathElement} starts with "@",
     * then the new node is an attribute, otherwise it is an element.
     *
     * @param parent
     * <br>Not null
     * <br>Input-output
     * @param xPathElement
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    public static final Node addChild(final Node parent, final String xPathElement) {
        final Boolean isAttribute = xPathElement.startsWith("@");
        final String name = isAttribute ? xPathElement.substring(1) : xPathElement.split("\\[")[0];
        final Document document = getOwnerDocument(parent);
        final Node result = isAttribute ? document.createAttribute(name) : document.createElement(name);

        if (isAttribute) {
            ((Element) parent).setAttributeNode((Attr) result);
        } else {
            parent.appendChild(result);
        }

        return result;
    }

    /**
     *
     * @param node
     * <br>Not null
     * @return
     * <br>Maybe null
     */
    public static final Document getOwnerDocument(final Node node) {
        if (node.getOwnerDocument() == null && node instanceof Document) {
            return (Document) node;
        }

        return node.getOwnerDocument();
    }

    /**
     *
     * @param xPathElement
     * <br>Not null
     * @return
     * <br>Not null
     * <br>New
     */
    private static final Map<String, String> getEqualityPredicates(final String xPathElement) {
        final LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        final String trimmed = xPathElement.trim();

        if (trimmed.endsWith("]")) {
            final String constraints = trimmed.substring(trimmed.indexOf("[") + 1, trimmed.length() - 1).trim();
            final StringBuilder buffer = new StringBuilder();
            String attributeName = null;
            int i = 0;
            ScannerState state = ScannerState.ATTRIBUTE_NAME;

            while (i < constraints.length()) {
                final char c = constraints.charAt(i);

                if (state == ScannerState.ATTRIBUTE_NAME && c == '=') {
                    attributeName = buffer.toString().trim();

                    buffer.setLength(0);

                    state = ScannerState.ATTRIBUTE_VALUE;
                } else if (state == ScannerState.ATTRIBUTE_VALUE && c == '\\') {
                    buffer.append(c);
                    buffer.append(constraints.charAt(++i));
                } else {
                    buffer.append(c);

                    final String trimmedBuffer = buffer.toString().trim();

                    if (state == ScannerState.ATTRIBUTE_VALUE && trimmedBuffer.length() > 1 && trimmedBuffer.charAt(0) == trimmedBuffer.charAt(trimmedBuffer.length() - 1)) {
                        result.put(attributeName, getString(null, trimmedBuffer));

                        buffer.setLength(0);

                        state = ScannerState.AND;
                    } else if (state == ScannerState.AND && "and".equalsIgnoreCase(trimmedBuffer)) {
                        buffer.setLength(0);

                        state = ScannerState.ATTRIBUTE_NAME;

                    }
                }

                ++i;
            }
        }

        return result;
    }

    /**
     * @author codistmonk (creation 2010-07-29)
     */
    private enum ScannerState {

        ATTRIBUTE_NAME, ATTRIBUTE_VALUE, AND;

    }

}