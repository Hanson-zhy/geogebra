package org.geogebra.common.exam.restrictions;

import javax.annotation.Nonnull;

/**
 * Apply custom restrictions during exams.
 */
public interface ExamRestrictable {

	/**
	 * Apply the restrictions when the exam starts.
	 *
	 * @param examRestrictions The restrictions for the current exam.
	 */
	void applyRestrictions(@Nonnull ExamRestrictions examRestrictions);

	/**
	 * Reverse the side effects of {@link #applyRestrictions(ExamRestrictions)}.
	 *
	 * @param examRestrictions The restrictions for the current exam.
	 */
	void revertRestrictions(@Nonnull ExamRestrictions examRestrictions);
}
