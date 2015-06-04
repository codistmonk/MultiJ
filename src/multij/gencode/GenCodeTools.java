package multij.gencode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import multij.tools.IllegalInstantiationException;

/**
 * @author codistmonk (creation 2013-01-13)
 */
public final class GenCodeTools {
	
	private GenCodeTools() {
		throw new IllegalInstantiationException();
	}
	
	/**
	 * {@value}.
	 */
	public static final long $SERIAL_VERSION_UID = -1L;
	
	/**
	 * {@value}.
	 */
	public static final String SERIAL_VERSION_UID_TEMPLATE = "jgencode.JGenCodeTools.$SERIAL_VERSION_UID";
	
	public static final void instantiateTemplateSource(final String rootDirectory, final Class<?> template, final String outputSimpleName,
			final String... replacements) {
		final Map<String, String> replacementMap = new HashMap<String, String>();
		
		for (int i = 0; i < replacements.length; i += 2) {
			replacementMap.put(replacements[i], replacements[i + 1]);
		}
		
		instantiateTemplateSource(rootDirectory, template, outputSimpleName, replacementMap);
	}
	
	public static final void instantiateTemplateSource(final String rootDirectory, final Class<?> template, final String outputSimpleName,
			final Map<String, String> replacements) {
		final String inputName = template.getName();
		final String outputName = template.getPackage().getName() + "." + outputSimpleName;
		final String inputPath = rootDirectory + inputName.replace('.', '/') + ".java";
		final String outputPath = rootDirectory + outputName.replace('.', '/') + ".java";
		
		replacements.put(template.getSimpleName(), outputSimpleName);
		
		try {
			final Scanner input = new Scanner(new File(inputPath));
			final PrintStream output = new PrintStream(outputPath);
			
			output.println("/* MACHINE-GENERATED FILE */");
			
			while (input.hasNext()) {
				String line = input.nextLine();
				
				for (final String replaced : replacements.keySet()) {
					if (line.startsWith("import ") && line.endsWith(replaced + ";")) {
						line = null;
						break;
					}
					
				}
				
				if (line != null) {
					for (final Map.Entry<String, String> entry : replacements.entrySet()) {
						line = line.replaceAll(Pattern.quote(entry.getKey()), entry.getValue());
					}
					
					output.println(line);
				}
			}
			
			input.close();
			output.close();
		} catch (final FileNotFoundException exception) {
			exception.printStackTrace();
		}
	}
		
}
