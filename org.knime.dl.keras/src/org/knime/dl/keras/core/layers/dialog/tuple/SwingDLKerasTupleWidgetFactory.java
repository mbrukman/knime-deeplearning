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
package org.knime.dl.keras.core.layers.dialog.tuple;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.dl.keras.base.nodes.learner.view.jfreechart.DocumentAdapter;
import org.knime.dl.keras.core.layers.DLParameterValidationUtils;
import org.knime.dl.keras.core.struct.Member;
import org.knime.dl.keras.core.struct.dialog.AbstractSwingWidget;
import org.knime.dl.keras.core.struct.dialog.SwingWidget;
import org.knime.dl.keras.core.struct.dialog.SwingWidgetFactory;
import org.knime.dl.keras.core.struct.instance.MemberReadInstance;
import org.knime.dl.keras.core.struct.instance.MemberWriteInstance;
import org.knime.dl.keras.core.struct.param.FieldParameterMember;
import org.knime.dl.keras.core.struct.param.ParameterMember;
import org.knime.dl.keras.core.struct.param.Required;
import org.scijava.util.ClassUtils;

import net.miginfocom.swing.MigLayout;

/**
 * @author David Kolb, KNIME GmbH, Konstanz, Germany
 */
public class SwingDLKerasTupleWidgetFactory implements SwingWidgetFactory<DLKerasTuple> {

    @Override
    public boolean supports(final Member<?> member) {
        return ClassUtils.canCast(member.getRawType(), DLKerasTuple.class)
            && ((ParameterMember<?>)member).getOptionalStatus().equals(Required.Required);
    }

    @Override
    public SwingWidget<DLKerasTuple> create(final Member<DLKerasTuple> model) {
        return new Widget(model);
    }

    // -- Helper classes --

    private class Widget extends AbstractSwingWidget<DLKerasTuple> {

        private JPanel panel;

        private final TupleTextField m_textField;

        private final DLKerasTuple m_referenceTuple;

        public Widget(final Member<DLKerasTuple> model) {
            super(model);
            
            m_textField = new TupleTextField();
            // TODO This only works because all members are FieldParameterMembers, otherwise we get a problem            
            if (member() instanceof FieldParameterMember) {
                FieldParameterMember<?> fpm = (FieldParameterMember<?>)member();
                m_referenceTuple = (DLKerasTuple)fpm.getDefault();
                m_textField.setReferenceTuple(m_referenceTuple);
            } else {
                throw new IllegalStateException(
                    "The member must be of type FieldParameterMember for DLKerasTupleWidgets.");
            }
        }

        @Override
        public JPanel getComponent() {
            if (panel != null)
                return panel;

            panel = new JPanel(new MigLayout("fillx,ins 0 0 0 0", "[fill,grow]"));
            panel.add(m_textField, "growx");
            return panel;
        }

        @Override
        public void loadFrom(MemberReadInstance<DLKerasTuple> instance, final PortObjectSpec[] spec)
            throws InvalidSettingsException {
            instance.load();
            m_textField.setTuple(instance.get().getTuple());
        }

        @Override
        public void saveTo(MemberWriteInstance<DLKerasTuple> instance) throws InvalidSettingsException {
            instance.set(new DLKerasTuple(m_textField.getTuple(), m_referenceTuple.getMinLength(),
                m_referenceTuple.getMaxLength(), m_referenceTuple.getConstraints(), m_textField.isEnabled()));
            instance.save();
        }

        @Override
        public void setEnabled(final boolean enabled) {
            m_textField.setEnabled(enabled);
        }
    }

    /**
     * A JTextField that turns red if no double number is entered.
     */
    private class TupleTextField extends JPanel {

        private static final long serialVersionUID = 1L;

        private DLKerasTuple m_refernceTuple;

        private JTextField m_tuple = new JTextField();

        private JLabel m_errorMessage = new JLabel();

        JXCollapsiblePane m_errorPanel = new JXCollapsiblePane();

        public TupleTextField() {
            m_tuple.getDocument().addDocumentListener((DocumentAdapter)e -> updateStatus());
            this.setLayout(new MigLayout("fillx,ins 0 0 0 0", "[fill,grow]"));
            this.add(m_tuple, "wrap, growx");

            m_errorMessage.setForeground(Color.RED);
            m_errorPanel.setAnimated(false);
            m_errorPanel.add(m_errorMessage);
            m_errorPanel.setCollapsed(true);
            this.add(m_errorPanel, "growx");
        }

        private boolean checkText() {
            final String text = m_tuple.getText();
            final String stripped = text.replaceAll("\\s+", "");

            if (!stripped.isEmpty()) {
                if (m_refernceTuple.isPartialAllowed()) {
                    if (!stripped.matches(DLParameterValidationUtils.PARTIAL_SHAPE_PATTERN)) {
                        m_errorMessage.setText("Invalid tuple format: '" + m_tuple.getText() + "' Must be digits"
                            + (m_refernceTuple.isPartialAllowed() ? " or a question mark" : "")
                            + " separated by a comma.");
                        return false;
                    }
                } else {
                    if ((!stripped.matches(DLParameterValidationUtils.SHAPE_PATTERN))) {
                        m_errorMessage.setText(
                            "Invalid tuple format: '" + m_tuple.getText() + "' Must be digits separated by a comma.");
                        return false;
                    }
                }
            }

            try {
                // just allocate for error checking
                @SuppressWarnings("unused")
                DLKerasTuple testTuple = new DLKerasTuple(stripped, m_refernceTuple.getMinLength(),
                    m_refernceTuple.getMaxLength(), m_refernceTuple.getConstraints(), true);
            } catch (IllegalArgumentException e) {
                m_errorMessage.setText(e.getMessage());
                return false;
            }
            // Clear the error message if the text is OK
            m_errorMessage.setText("");
            return true;
        }

        private void updateStatus() {
            if (!checkText()) {
                m_tuple.setForeground(Color.RED);
                m_errorPanel.setCollapsed(false);
            } else {
                // TextField content is valid, hence reset color to default
                m_tuple.setForeground(null);
                // Hide the error message
                m_errorPanel.setCollapsed(true);
            }
        }

        public void setReferenceTuple(final DLKerasTuple ref) {
            m_refernceTuple = ref;
        }

        public void setTuple(final Long[] tuple) {
            m_tuple.setText(DLKerasTuple.tupleToString(tuple));
        }

        public Long[] getTuple() throws InvalidSettingsException {
            if (checkText()) {
                return DLKerasTuple.stringToTuple(m_tuple.getText());
            } else {
                throw new InvalidSettingsException(m_errorMessage.getText());
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            m_tuple.setEnabled(enabled);
            m_errorMessage.setEnabled(enabled);
            if (enabled) {
                updateStatus();
            } else {
                m_errorPanel.setCollapsed(true);
            }
        }
        
        @Override
        public boolean isEnabled() {
            return m_tuple.isEnabled();
        }
    }
}
