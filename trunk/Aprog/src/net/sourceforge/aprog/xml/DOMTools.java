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

import static net.sourceforge.aprog.tools.Tools.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.aprog.tools.IllegalInstantiationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author codistmonk (creation 2010-07-02)
 */
public final class DOMTools {

    /**
     * @throws IllegalInstantiationException To prevent instantiation
     */
    private DOMTools() {
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

}