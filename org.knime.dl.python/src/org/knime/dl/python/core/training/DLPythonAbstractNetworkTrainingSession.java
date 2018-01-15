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
package org.knime.dl.python.core.training;

import java.io.IOException;
import java.util.Set;

import org.knime.dl.core.DLCanceledExecutionException;
import org.knime.dl.core.DLInvalidEnvironmentException;
import org.knime.dl.core.DLMissingExtensionException;
import org.knime.dl.core.DLTensorFactory;
import org.knime.dl.core.DLTensorSpec;
import org.knime.dl.core.DLNetworkInputPreparer;
import org.knime.dl.core.training.DLAbstractNetworkTrainingSession;
import org.knime.dl.core.training.DLTrainingConfig;
import org.knime.dl.core.training.DLTrainingMonitor;
import org.knime.dl.core.training.DLTrainingStatus;
import org.knime.dl.python.core.DLPythonCommands;
import org.knime.dl.python.core.DLPythonNetwork;
import org.knime.dl.python.core.DLPythonNetworkHandle;
import org.knime.dl.python.core.DLPythonNetworkLoaderRegistry;

/**
 * @author Marcel Wiedenmann, KNIME GmbH, Konstanz, Germany
 * @author Christian Dietz, KNIME GmbH, Konstanz, Germany
 */
public abstract class DLPythonAbstractNetworkTrainingSession<S extends DLTrainingStatus, N extends DLPythonNetwork, //
		CFG extends DLTrainingConfig, C extends DLPythonCommands>
	extends DLAbstractNetworkTrainingSession<S, N, CFG> implements DLPythonNetworkTrainingSession<S> {

	/**
	 * Is instantiated via {@link #createCommands()} at the beginning of the first call of
	 * {@link #trainInternal(DLTrainingMonitor)}.
	 */
	protected C m_commands;

	protected DLPythonNetworkHandle m_handle;

	protected DLPythonAbstractNetworkTrainingSession(final N network, final CFG trainingConfig,
			final Set<DLTensorSpec> executionInputSpecs, final DLNetworkInputPreparer inputPreparer,
			final DLTensorFactory tensorFactory) {
		super(network, trainingConfig, executionInputSpecs, inputPreparer, tensorFactory);
	}

	/**
	 * The caller is responsible for {@link AutoCloseable#close() closing} the command.
	 */
	protected abstract C createCommands() throws DLInvalidEnvironmentException;

	protected abstract void setNetworkTrainingConfig(DLPythonNetworkHandle handle, CFG config)
			throws DLInvalidEnvironmentException, IOException;

	@Override
	public void close() throws Exception {
		super.close();
		if (m_commands != null) {
			m_commands.close();
		}
	}

	@Override
	protected void trainInternal(final DLTrainingMonitor<? extends S> monitor)
			throws DLCanceledExecutionException, Exception {
		if (m_commands == null) {
			m_commands = createCommands();
			m_handle = DLPythonNetworkLoaderRegistry.getInstance().getNetworkLoader(m_network.getClass()).orElseThrow(
					() -> new DLMissingExtensionException("Python back end '" + m_network.getClass().getCanonicalName()
							+ "' could not be found. Are you missing a KNIME Deep Learning extension?"))
					.load(m_network.getSource(), m_commands.getContext(), true);
			setNetworkTrainingConfig(m_handle, m_trainingConfig);
		}
		m_commands.trainNetwork(m_handle, m_inputProvider, monitor);
		m_commands.getTrainingResults(m_handle);
	}
}
