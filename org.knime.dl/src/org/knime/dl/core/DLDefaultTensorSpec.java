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
 *   May 8, 2017 (marcel): created
 */
package org.knime.dl.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Default implementation of a {@link DLTensorSpec tensor spec}.
 *
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
public class DLDefaultTensorSpec extends DLAbstractTensorSpec {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of this tensor spec that has a batch size.
	 *
	 * @param name the name of the tensor
	 * @param batchSize the batch size of the tensor. Must be greater than zero.
	 * @param shape the shape of the tensor. Does not include the batch size.
	 * @param dataType the data type of the tensor's elements
	 */
	public DLDefaultTensorSpec(final String name, final long batchSize, final DLTensorShape shape,
			final Class<?> dataType) {
		super(name, batchSize, shape, dataType);
	}

	/**
	 * Creates a new instance of this tensor spec that does not have a batch size.
	 *
	 * @param name the name of the tensor
	 * @param shape the shape of the tensor. Does not include the batch size.
	 * @param dataType the data type of the tensor's elements
	 */
	public DLDefaultTensorSpec(final String name, final DLTensorShape shape, final Class<?> dataType) {
		super(name, shape, dataType);
	}

	/**
	 * Creates a new instance of tensor spec that does not have a shape.
	 *
	 * @param name the name of the tensor
	 * @param batchSize the batch size of the tensor. Must be greater than zero.
	 * @param dataType the data type of the tensor's elements
	 */
	public DLDefaultTensorSpec(final String name, final long batchSize, final Class<?> dataType) {
		super(name, batchSize, dataType);
	}

	/**
	 * Creates a new instance of tensor spec that does not have a batch size or a shape.
	 *
	 * @param name the name of the tensor
	 * @param dataType the data type of the tensor's elements
	 */
	public DLDefaultTensorSpec(final String name, final Class<?> dataType) {
		super(name, dataType);
	}

	@Override
	protected void hashCodeInternal(final HashCodeBuilder b) {
		// no op - everything's handled in abstract base class
	}

	@Override
	protected boolean equalsInternal(final DLTensorSpec other) {
		// no op - everything's handled in abstract base class
		return true;
	}
}
