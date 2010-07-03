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
import static net.sourceforge.aprog.xml.XMLTools.*;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Automated tests using JUnit 4 for {@link XMLTools}.
 *
 * @author codistmonk (creation 2010-07-01)
 */
public final class XMLToolsTest {

    @Test
    public final void testParse() {
        assertNotNull(parse("<a/>"));
        assertNotNull(parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?><a/>"));
    }

    @Test(expected=RuntimeException.class)
    public final void testParseFailureMissingClosingTag() {
        assertNotNull(parse("<a>"));
    }

    @Test(expected=RuntimeException.class)
    public final void testParseFailureMultipleRoots() {
        assertNotNull(parse("<a/><b/>"));
    }

    @Test
    public final void testNewDocument() {
        assertNotNull(newDocument());
    }

    @Test
    public final void testNormalize() {
        final Document document = parse("<a/>");
        final Node root = document.getDocumentElement();

        assertEquals("a", root.getNodeName());

        // Make the document "not normalized" by adding an empty text node
        root.appendChild(document.createTextNode(""));

        assertEquals(1, root.getChildNodes().getLength());
        assertSame(document, normalize(document));
        assertEquals(0, root.getChildNodes().getLength());
    }

    @Test
    public final void testWrite() {
        final String xmlInput = "<a/>";
        final Document document = parse(xmlInput);

        {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            XMLTools.write(document, buffer, 0);

            assertEquals(XML_1_UTF8_NOT_STANDALONE + xmlInput, buffer.toString());
        }
        {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            XMLTools.write(standalone(document), buffer, 0);

            assertEquals(XML_1_UTF8_STANDALONE + xmlInput, buffer.toString());
        }
        {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            XMLTools.write(document.getDocumentElement(), buffer, 0);

            assertEquals(xmlInput, buffer.toString());
        }

    }

    @Test
    public final void testGetNode() throws Exception {
        final Document document = parse(
                "<a>" +
                "   <b c='d'/>" +
                "</a>"
        );

        assertEquals("d", getNode(document, "a/b/@c").getNodeValue());
        assertEquals("b", getNode(document, "a/b[@c='d']").getNodeName());
        assertNull(getNode(document, "a/b[@c='e']"));
    }

    @Test
    public final void testGetOrCreateNode() {
        final Document document = newDocument();

        getOrCreateNode(document, "aa[]");

        assertXMLEquals(
                "<aa/>"
                , document);

        getOrCreateNode(document, "aa/b");

        assertXMLEquals(
                "<aa>" +
                    "<b/>" +
                "</aa>"
                , document);

        getOrCreateNode(document, "aa/b[@c='d']");

        assertXMLEquals(
                "<aa>" +
                    "<b/>" +
                    "<b c=\"d\"/>" +
                "</aa>"
                , document);

        getOrCreateNode(document, "aa/b[]");

        assertXMLEquals(
                "<aa>" +
                    "<b/>" +
                    "<b c=\"d\"/>" +
                    "<b/>" +
                "</aa>"
                , document);

        getOrCreateNode(document, "aa/b[position()=2]/e");

        assertXMLEquals(
                "<aa>" +
                    "<b/>" +
                    "<b c=\"d\"><e/></b>" +
                    "<b/>" +
                "</aa>"
                , document);

        getOrCreateNode(document, "aa/b[position()=2]/e/@f");

        assertXMLEquals(
                "<aa>" +
                    "<b/>" +
                    "<b c=\"d\"><e f=\"\"/></b>" +
                    "<b/>" +
                "</aa>"
                , document);
    }

    @Test
    public final void testValidate() {
        // DTD validation
        testValidate("test.xml", "test.dtd", false);

        // XSD validation
        testValidate("test.xml", "test.xsd", false);

        // Relax-NG validation, if available
        testValidate("test.xml", "test.rng", true);

        // Compact Relax-NG validation, if available
        testValidate("test.xml", "test.rnc", true);
    }

    private static final String PATH = getCallerPackagePath();

    /**
     *
     * @param xmlFileName
     * <br>Not null
     * @param dtdOrSchemaFileName
     * <br>Not null
     * @param canBeUnavailable {@code true} if the validation can fail because the schema language is not available
     */
    private static final void testValidate(final String xmlFileName, final String dtdOrSchemaFileName,
            final boolean canBeUnavailable) {
        try {
            assertEquals(0, validate(
                    getResourceAsStream(PATH + xmlFileName),
                    getResourceAsSource(PATH + dtdOrSchemaFileName)).size());
        } catch (final IllegalArgumentException exception) {
            if (canBeUnavailable &&
                    exception.getMessage().startsWith("No SchemaFactory that implements the schema language specified")) {
                getLoggerForThisMethod().log(Level.INFO, debug(3, exception.getMessage()));
            } else {
                throw exception;
            }
        }
    }

    /**
     *
     * @param expectedXML
     * <br>Not null
     * @param document
     * <br>Not null
     */
    private static final void assertXMLEquals(final String expectedXML, final Document document) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final Result result = new StreamResult(buffer);

        write(normalize(document).getDocumentElement(), result, 0);

        assertEquals(expectedXML, buffer.toString());
    }

}