/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Jun 6, 2017 (marcel): created
 */
package org.knime.dl.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.knime.dl.util.DLUtils.Preconditions.checkNotNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalLong;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.knime.core.util.FileUtil;
import org.knime.dl.core.DLFixedTensorShape;
import org.knime.dl.core.DLNetworkSpec;
import org.knime.dl.core.DLTensorShape;
import org.knime.dl.core.DLTensorSpec;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Various utility methods and classes.
 *
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
public final class DLUtils {

	private DLUtils() {
	}

	public static class Files {

		public static File getFileFromBundle(final String bundleName, final String relativePath) throws IOException {
			checkNotNullOrEmpty(bundleName);
			checkNotNullOrEmpty(relativePath);
			try {
				final Bundle bundle = Platform.getBundle(bundleName);
				final URL url = FileLocator.find(bundle, new Path(relativePath), null);
				return url != null ? FileUtil.getFileFromURL(FileLocator.toFileURL(url)) : null;
			} catch (final Exception e) {
				throw new IOException(
						"Failed to get file '" + relativePath + "' from bundle '" + bundleName + "': " + e.getMessage(),
						e);
			}
		}

		public static File getFileFromSameBundle(final Object caller, final String relativePath)
				throws IllegalArgumentException, IOException {
			checkNotNull(caller);
			checkNotNullOrEmpty(relativePath);
			final Bundle bundle = FrameworkUtil.getBundle(caller.getClass());
			if (bundle == null) {
				throw new IllegalArgumentException(
						"Failed to get file '" + relativePath + "' from the bundle of class '"
								+ caller.getClass().getCanonicalName() + "'. Bundle could not be resolved.");
			}
			return DLUtils.Files.getFileFromBundle(bundle.getSymbolicName(), relativePath);
		}

		public static String readAllUTF8(final File f) throws IOException {
			checkNotNull(f);
			return new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
		}
	}

	public static class Layers {

		private Layers() {
		}
	}

	public static class Networks {

		private Networks() {
		}

		public static Optional<DLTensorSpec> findSpec(final String name, final DLNetworkSpec networkSpec) {
			checkNotNullOrEmpty(name);
			checkNotNull(networkSpec);
			return findSpec(name, networkSpec.getInputSpecs(), networkSpec.getHiddenOutputSpecs(),
					networkSpec.getOutputSpecs());
		}

		public static Optional<DLTensorSpec> findSpec(final String name, final DLTensorSpec[]... specs) {
			checkNotNullOrEmpty(name);
			checkNotNull(specs);
			return Arrays.stream(specs).flatMap(Arrays::stream).filter(s -> s.getName().equals(name)).findFirst();
		}
	}

	/**
	 * Utility class that helps checking whether the preconditions of a method or constructor invocation have been met
	 * by the caller. <br>
	 * This class complements the functionality of {@link com.google.common.base.Preconditions}.
	 */
	public static class Preconditions {

		private Preconditions() {
		}

		/**
		 * Ensures that a string passed as a parameter to the calling method is not null or empty.
		 *
		 * @param string a string
		 * @return the non-null and non-empty reference that was validated
		 * @throws NullPointerException if the input is null
		 * @throws IllegalArgumentException if the input is empty
		 * @see com.google.common.base.Preconditions#checkNotNull(Object)
		 */
		public static String checkNotNullOrEmpty(final String string) {
			if (string == null) {
				throw new NullPointerException();
			}
			if (string.isEmpty()) {
				throw new IllegalArgumentException();
			}
			return string;
		}

		/**
		 * Ensures that a string passed as a parameter to the calling method is not null or empty.
		 *
		 * @param string a string
		 * @param errorMessage the exception message to use if the check fails; will be converted to a string using
		 *            {@link String#valueOf(Object)}
		 * @return the non-null and non-empty reference that was validated
		 * @throws NullPointerException if the input is null
		 * @throws IllegalArgumentException if the input is empty
		 * @see com.google.common.base.Preconditions#checkNotNull(Object, Object))
		 */
		public static String checkNotNullOrEmpty(final String string, final Object errorMessage) {
			if (string == null) {
				throw new NullPointerException(String.valueOf(errorMessage));
			}
			if (string.isEmpty()) {
				throw new IllegalArgumentException(String.valueOf(errorMessage));
			}
			return string;
		}
	}

	public static class Shapes {

		private Shapes() {
		}

		public static boolean isFixed(final DLTensorShape shape) {
			return shape instanceof DLFixedTensorShape;
		}

		public static Optional<long[]> getFixedShape(final DLTensorShape shape) {
			if (isFixed(shape)) {
				return Optional.of(((DLFixedTensorShape) shape).getShape());
			}
			return Optional.empty();
		}

		public static OptionalLong getFixedSize(final DLTensorShape shape) {
			final Optional<long[]> fixedShape = getFixedShape(shape);
			if (fixedShape.isPresent()) {
				return OptionalLong.of(getSize(fixedShape.get()));
			}
			return OptionalLong.empty();
		}

		public static long getSize(final long[] shape) {
			if (shape == null || shape.length == 0) {
				return 0;
			}
			long size = 1;
			for (int i = 0; i < shape.length; i++) {
				size *= shape[i];
			}
			return size;
		}
	}
}
