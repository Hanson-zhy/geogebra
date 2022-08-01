package org.geogebra.common.kernel.interval.samplers;

import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpace;
import org.geogebra.common.kernel.interval.evaluators.DiscreteSpaceCentered;
import org.geogebra.common.kernel.interval.function.IntervalFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Class to provide samples of the given function as a
 * list of (x, y) pairs, where both x and y are intervals.
 *
 * @author Laszlo
 */
public class FunctionSampler implements IntervalFunctionSampler {

	private final IntervalFunction function;
	private EuclidianViewBounds bounds;
	private int numberOfSamples;
	private DiscreteSpace space;

	/**
	 * @param geoFunction function to get sampled
	 * @param range (x, y) range.
	 * @param numberOfSamples the sample rate.
	 */
	public FunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			int numberOfSamples) {
		this(geoFunction);
		this.numberOfSamples = numberOfSamples;
		createSpace();
		update(range);
	}

	private void createSpace() {
		space = new DiscreteSpaceCentered(bounds.range().getLength() / numberOfSamples);
	}

	/**
	 * @param geoFunction function to get sampled
	 * @param range (x, y) range.
	 * @param bounds {@link EuclidianView}
	 */
	public FunctionSampler(GeoFunction geoFunction, IntervalTuple range,
			EuclidianViewBounds bounds) {
		this(geoFunction);
		this.bounds = bounds;
		numberOfSamples = bounds.getWidth();
		createSpace();
		update(range);
	}

	FunctionSampler(GeoFunction geoFunction) {
		this.function = new IntervalFunction(geoFunction);
	}

	@Override
	public IntervalTupleList result() {
		return evaluate(space);
	}

	@Override
	public IntervalTupleList evaluate(Interval x) {
		return evaluate(x.getLow(), x.getHigh());
	}

	@Override
	public IntervalTupleList evaluate(double low, double high) {
		return processAsymptotes(evaluateOnStream(space.values(low, high)));
	}

	@Override
	public IntervalTupleList evaluate(DiscreteSpace space) {
		return processAsymptotes(evaluateOnStream(space.values()));
	}

	private static IntervalTupleList processAsymptotes(IntervalTupleList samples) {
		IntervalAsymptotes asymptotes = new IntervalAsymptotes(samples);
		asymptotes.process();
		return samples;
	}

	private IntervalTupleList evaluateOnStream(Stream<Interval> values) {
		IntervalTupleList tuples = new IntervalTupleList();
		values.forEach(x -> {
			IntervalTuple tuple = new IntervalTuple(x, function.evaluate(x));
			tuples.add(tuple);
		});
		return tuples;
	}

	/**
	 * Updates the range on which sampler has to run.
	 *
	 * @param range the new (x, y) range
	 */
	@Override
	public void update(IntervalTuple range) {
		space.update(range.x(), calculateNumberOfSamples());
	}

	private int calculateNumberOfSamples() {
		return numberOfSamples > 0 ? numberOfSamples : bounds.getWidth();
	}

	/**
	 * Extend and evaluate on interval [min, max]
	 * @param min lower bound
	 * @param max higher bound
	 * @return tuples evaluated on [min, max].
	 */
	@Override
	public IntervalTupleList extendDomain(double min, double max) {
		setInterval(min, max);
		return evaluate(space);
	}

	/**
	 * Sets plot interval without evaluation
	 * @param low bound.
	 * @param high bound.
	 */
	@Override
	public void setInterval(double low, double high) {
		space.update(low, high);
	}

	@Override
	public GeoFunction getGeoFunction() {
		return function.getFunction();
	}
}
