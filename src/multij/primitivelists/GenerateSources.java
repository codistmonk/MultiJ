package multij.primitivelists;

import static multij.gencode.GenCodeTools.SERIAL_VERSION_UID_TEMPLATE;
import static multij.gencode.GenCodeTools.instantiateTemplateSource;
import static multij.tools.Tools.array;
import static multij.tools.Tools.toUpperCamelCase;

import java.util.Date;
import java.util.Random;

import multij.gencode.$Primitive;
import multij.tools.IllegalInstantiationException;
import multij.tools.TicToc;

/**
 * @author codistmonk (creation 2013-01-16)
 */
public final class GenerateSources {
	
	private GenerateSources() {
		throw new IllegalInstantiationException();
	}
	
	/**
	 * @param arguments
	 * <br>Unused
	 */
	public static final void main(final String[] arguments) {
		final TicToc timer = new TicToc();
		
		System.out.println("Source generation started " + new Date(timer.tic()));
		
		final Random random = new Random();
		
		for (final String primitive : array("boolean", "byte", "char", "short", "int", "long", "float", "double")) {
			instantiateTemplateSource(
					"src/",
					PrimitiveListTemplate.class, toUpperCamelCase(primitive + "List"),
					$Primitive.class.getSimpleName(), primitive,
					SERIAL_VERSION_UID_TEMPLATE, random.nextLong() + "L"
			);
		}
		
		System.out.println("Source generation done in " + timer.toc() + " ms");
	}
	
}
