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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 */
package org.knime.dl.core.data.convert;

import java.util.List;
import java.util.OptionalLong;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.data.IntValue;
import org.knime.dl.core.DLTensor;
import org.knime.dl.core.data.DLWritableFloatBuffer;

/**
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
public class DLIntValueToFloatTensorConverterFactory
	extends DLAbstractScalarDataValueToTensorConverterFactory<IntValue, DLWritableFloatBuffer> {

	@Override
	public String getName() {
		return ((ExtensibleUtilityFactory) IntValue.UTILITY).getName();
	}

	@Override
	public Class<IntValue> getSourceType() {
		return IntValue.class;
	}

	@Override
	public Class<DLWritableFloatBuffer> getBufferType() {
		return DLWritableFloatBuffer.class;
	}

	@Override
	public OptionalLong getDestCount(final List<DataColumnSpec> spec) {
		return OptionalLong.of(spec.size());
	}

	@Override
	public DLDataValueToTensorConverter<IntValue, DLWritableFloatBuffer> createConverter() {
		return new DLAbstractScalarDataValueToTensorConverter<IntValue, DLWritableFloatBuffer>() {

			@Override
			public void convert(final Iterable<? extends IntValue> input,
					final DLTensor<DLWritableFloatBuffer> output) {
				final DLWritableFloatBuffer buf = output.getBuffer();
				for (final IntValue val : input) {
					// implicit widening primitive conversion from int to float; loss of precision
					// NB: our type system treats int and float as incompatible, that's why we need this converter
					// explicitly (for UX reasons)
					buf.put(val.getIntValue());
				}
			}
		};
	}
}
