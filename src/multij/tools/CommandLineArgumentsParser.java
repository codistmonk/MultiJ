/*
 *  The MIT License
 * 
 *  Copyright 2012 Codist Monk.
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

package multij.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author codistmonk (creation 2012-07-08)
 */
public final class CommandLineArgumentsParser implements Serializable {
	
	private final Map<String, String> map;
	
	public CommandLineArgumentsParser(final String... arguments) {
		this.map = toMap(arguments);
	}
	
	public final String get(final String key, final String defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		return valueRepresentation != null ? valueRepresentation : defaultValue;
	}
	
	public final int[] get(final String key, final int... defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		if (valueRepresentation == null) {
			return defaultValue;
		}
		
		final List<Integer> values = new ArrayList<>();
		
		for (final String rangeRepresentation : valueRepresentation.split(",")) {
	        final String[] rangeParameters = rangeRepresentation.split(":");
	        final int first = decodeInt(rangeParameters[0]);
	        final int step;
	        final int last;
	        
	        if (rangeParameters.length == 2) {
	            step = 1;
	            last = decodeInt(rangeParameters[1]);
	        } else if (rangeParameters.length == 3) {
	            step = decodeInt(rangeParameters[1]);
	            last = decodeInt(rangeParameters[2]);
	        } else {
	            step = 1;
	            last = first;
	        }
	        
	        values.addAll(newRange(first, step, last));
		}
		
		return toIntArray(values);
	}
	
	public final long[] get(final String key, final long... defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		if (valueRepresentation == null) {
			return defaultValue;
		}
		
		final List<Long> values = new ArrayList<>();
		
		for (final String rangeRepresentation : valueRepresentation.split(",")) {
			final String[] rangeParameters = rangeRepresentation.split(":");
			final long first = Long.decode(rangeParameters[0]);
			final long step;
			final long last;
			
			if (rangeParameters.length == 2) {
				step = 1L;
				last = Long.decode(rangeParameters[1]);
			} else if (rangeParameters.length == 3) {
				step = Long.decode(rangeParameters[1]);
				last = Long.decode(rangeParameters[2]);
			} else {
				step = 1L;
				last = first;
			}
			
			values.addAll(newRange(first, step, last));
		}
		
		return toLongArray(values);
	}
	
	public final float[] get(final String key, final float... defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		if (valueRepresentation == null) {
			return defaultValue;
		}
		
		final List<Float> values = new ArrayList<>();
		
		for (final String rangeRepresentation : valueRepresentation.split(",")) {
			final String[] rangeParameters = rangeRepresentation.split(":");
			final float first = decodeInt(rangeParameters[0]);
			final float step;
			final float last;
			
			if (rangeParameters.length == 2) {
				step = 1;
				last = Float.parseFloat(rangeParameters[1]);
			} else if (rangeParameters.length == 3) {
				step = Float.parseFloat(rangeParameters[1]);
				last = Float.parseFloat(rangeParameters[2]);
			} else {
				step = 1;
				last = first;
			}
			
			values.addAll(newRange(first, step, last));
		}
		
		return toFloatArray(values);
	}
	
	public final double[] get(final String key, final double... defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		if (valueRepresentation == null) {
			return defaultValue;
		}
		
		final List<Double> values = new ArrayList<>();
		
		for (final String rangeRepresentation : valueRepresentation.split(",")) {
			final String[] rangeParameters = rangeRepresentation.split(":");
			final double first = decodeInt(rangeParameters[0]);
			final double step;
			final double last;
			
			if (rangeParameters.length == 2) {
				step = 1;
				last = Double.parseDouble(rangeParameters[1]);
			} else if (rangeParameters.length == 3) {
				step = Double.parseDouble(rangeParameters[1]);
				last = Double.parseDouble(rangeParameters[2]);
			} else {
				step = 1;
				last = first;
			}
			
			values.addAll(newRange(first, step, last));
		}
		
		return toDoubleArray(values);
	}
	
	public final int get1(final String key, final int defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		return valueRepresentation == null ? defaultValue : decodeInt(valueRepresentation);
	}
	
	public final long get1(final String key, final long defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		return valueRepresentation == null ? defaultValue : Long.decode(valueRepresentation);
	}
	
	public final float get1(final String key, final float defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		return valueRepresentation == null ? defaultValue : Float.parseFloat(valueRepresentation);
	}
	
	public final double get1(final String key, final double defaultValue) {
		final String valueRepresentation = this.map.get(key);
		
		return valueRepresentation == null ? defaultValue : Double.parseDouble(valueRepresentation);
	}
	
	public static final Map<String, String> toMap(final String[] commandLineArguments) {
		final Map<String, String> result = new LinkedHashMap<>();
		
		for (int i = 0; i < commandLineArguments.length; i += 2) {
			result.put(commandLineArguments[i], commandLineArguments[i + 1]);
		}
		
		return result;
	}
	
	public static final List<Integer> newRange(final int first, final int step, final int last) {
		final List<Integer> result = new ArrayList<>();
		
		for (int i = first; first <= last ? i <= last : last <= i; i += step) {
			result.add(i);
		}
		
		return result;
	}
	
	public static final List<Long> newRange(final long first, final long step, final long last) {
		final List<Long> result = new ArrayList<>();
		
		for (long i = first; first <= last ? i <= last : last <= i; i += step) {
			result.add(i);
		}
		
		return result;
	}
	
	public static final List<Float> newRange(final float first, final float step, final float last) {
		final List<Float> result = new ArrayList<>();
		
		for (float i = first; first <= last ? i <= last : last <= i; i += step) {
			result.add(i);
		}
		
		return result;
	}
	
	public static final List<Double> newRange(final double first, final double step, final double last) {
		final List<Double> result = new ArrayList<>();
		
		for (double i = first; first <= last ? i <= last : last <= i; i += step) {
			result.add(i);
		}
		
		return result;
	}
	
	public static final int[] toIntArray(final List<Integer> list) {
		final int[] result = new int[list.size()];
		
		for (int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	public static final long[] toLongArray(final List<Long> list) {
		final long[] result = new long[list.size()];
		
		for (int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	public static final float[] toFloatArray(final List<Float> list) {
		final float[] result = new float[list.size()];
		
		for (int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	public static final double[] toDoubleArray(final List<Double> list) {
		final double[] result = new double[list.size()];
		
		for (int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		
		return result;
	}
	
	public static final int parseIfAvailable(final String[] arguments, final int index, final int defaultValue) {
		if (index < arguments.length) {
			return decodeInt(arguments[index]);
		}
		
		return defaultValue;
	}
	
	/**
	 * {@value}.
	 */
	private static final long serialVersionUID = -3572883077330633932L;
	
	public static final int decodeInt(final String s) {
		return Long.decode(s).intValue();
	}
	
}
