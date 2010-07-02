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

package net.sourceforge.aprog.xml;

import java.io.IOException;
import java.util.logging.Logger;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import static net.sourceforge.aprog.tools.Tools.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sourceforge.aprog.tools.IllegalInstantiationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

    public static final String XML_1_UTF8_NOT_STANDALONE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    public static final String XML_1_UTF8_STANDALONE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

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
        write(node, new StreamResult(output), indent);
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
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, node instanceof Document ? "no" : "yes");
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
    public static final List<Node> newList(final NodeList nodeList) {
        final List<Node> result = new ArrayList<Node>();

        for (int i = 0; i < nodeList.getLength(); ++i) {
            result.add(nodeList.item(i));
        }

        return result;
    }

    /**
     *
     * @param xmlInputStream IN NOT_NULL
     * @param dtdOrSchema IN NOT_NULL
     * @return NEW NOT_NULL
     */
    public static final List<Throwable> validate(final InputStream xmlInputStream, final Source dtdOrSchema) {
        final List<Throwable> exceptions = new ArrayList<Throwable>();
        final String schemaLanguage = getSchemaLanguage(dtdOrSchema);

        try {
            if (schemaLanguage != null) {
                final Validator validator = SchemaFactory.newInstance(schemaLanguage).newSchema(dtdOrSchema).newValidator();

                validator.validate(new StreamSource(xmlInputStream));
            } else {
                final Transformer addDoctypeInformation = TransformerFactory.newInstance().newTransformer();

                addDoctypeInformation.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdOrSchema.getSystemId());

                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                addDoctypeInformation.transform(new StreamSource(xmlInputStream), new StreamResult(buffer));

                final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

                saxParserFactory.setValidating(true);
                saxParserFactory.newSAXParser().parse(new ByteArrayInputStream(buffer.toByteArray()), new DefaultHandler() {

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
     *
     * @param dtdOrSchema
     * <br>Not null
     * @return {@code null} for a DTD
     * <br>Maybe null
     * <br>Range: { {@code null}, {@link XMLConstants#W3C_XML_SCHEMA_NS_URI}, {@link XMLConstants#RELAXNG_NS_URI} }
     * @throws IllegalArgumentException If {@code dtdOrSchema}'s system id does not indicate a DTD or a schema (XSD or RNG)
     */
    public static final String getSchemaLanguage(final Source dtdOrSchema) {
        final String dtdOrSchemaId = dtdOrSchema.getSystemId().toLowerCase();

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
        return getNode(context, xPath);
    }

    /**
     *
     * @param context
     * <br>Not null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Maybe New
     */
    public static final Node getOrCreateNode(final Node context, final String xPath) {
        return getOrCreateNode(context, xPath);
    }

    /**
     *
     * @param context
     * <br>Maybe null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Not New
     */
    public static final NodeList getNodes(final Node context, final String xPath) {
        return getNodes(context, xPath);
    }

    /**
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
    public static final <S> S getNodes(final Object context, final String xPath) {
        return (S) get(context, xPath, XPathConstants.NODESET);
    }

    /**
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
     *
     * @param <N> The expected node type
     * @param context
     * <br>Not null
     * @param xPath
     * <br>Not null
     * @return
     * <br>Maybe null
     * <br>Maybe new
     */
    public static final <N> N getOrCreateNode(final Object context, final String xPath) {
        return null; // TODO
    }

}