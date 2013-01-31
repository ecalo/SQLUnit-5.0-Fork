/*
 * $Id: GUITool.java,v 1.10 2004/11/30 21:49:20 spal Exp $
 * $Source: /cvsroot/sqlunit/sqlunit/src/net/sourceforge/sqlunit/tools/GUITool.java,v $
 * SQLUnit - a test harness for unit testing database stored procedures.
 * Copyright (C) 2003  The SQLUnit Team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.sqlunit.tools;

import net.sourceforge.sqlunit.TypeMapper;
import net.sourceforge.sqlunit.beans.Param;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * The GUITool provides a GUI interface for generating SQLUnit tests.
 * @author riyer (riyer@users.sourceforge.net)
 * @version $Revision: 1.10 $
 */
public class GUITool extends javax.swing.JFrame {

    private static final String TOOLNAME = "$RCSfile: GUITool.java,v $";

    // all the different sizes
    private static final int INSET = 5;
    private static final int WINDOWSIZE_X = 550;
    private static final int WINDOWSIZE_Y = 600;
    private static final int LABEL_DIMENSION_X = 80;
    private static final int LABEL_DIMENSION_Y = 20;
    private static final int TEXT_DIMENSION_X = 150;
    private static final int TEXT_DIMENSION_Y = 19;
    private static final int CALLSTMT_DIMENSION_X = 300;
    private static final int CALLSTMT_DIMENSION_Y = 60;
    private static final int TEXT_DIMENSION_TRUNC_X = 64;
    private static final int TEXT_DIMENSION_LONG_X = 250;
    private static final int PARAM_DIMENSION_X = 453;
    private static final int PARAM_DIMENSION_Y = 60;
    private static final int OUTPUT_DIMENSION_X = 400;
    private static final int OUTPUT_DIMENSION_Y = 150;

    private static final int BUTTON_DIMENSION_X = 50;
    private static final int BUTTON_DIMENSION_Y = 20;

    private static final Dimension LABEL_DIMENSION =
        new Dimension(LABEL_DIMENSION_X, LABEL_DIMENSION_Y);
    private static final Dimension OUTPUT_DIMENSION =
        new Dimension(OUTPUT_DIMENSION_X, OUTPUT_DIMENSION_Y);
    private static final Dimension TEXT_DIMENSION =
        new Dimension(TEXT_DIMENSION_X, TEXT_DIMENSION_Y);
    private static final Dimension PSWD_DIMENSION =
        new Dimension(TEXT_DIMENSION_TRUNC_X, TEXT_DIMENSION_Y);
    private static final Dimension CALLSTMT_DIMENSION =
        new Dimension(CALLSTMT_DIMENSION_X, CALLSTMT_DIMENSION_Y);
    private static final Dimension PARAM_DIMENSION =
        new Dimension(PARAM_DIMENSION_X, PARAM_DIMENSION_Y);
    private static final Dimension BUTTON_DIMENSION =
        new Dimension(BUTTON_DIMENSION_X, BUTTON_DIMENSION_Y);
    private static final int TEXTAREA_NUM_ROWS = 10;
    private static final int TEXTAREA_NUM_COLS = 60;
    private static final int GRID_BASE_X = 0;
    private static final int GRID_BASE_Y = 0;
    private static final int PARAM_METADATA_LEN = 5;
    private static final int PARAM_TYPE_INDEX = 1;
    private static final int PARAM_ISNULL_INDEX = 2;
    private static final int PARAM_INOUT_INDEX = 3;
    private static final int PARAM_VALUE_INDEX = 4;

    private Connection con = null;
    private boolean connecting = false;
    private PrintWriter captureFile = null;

    private JButton btnConnect;
    private JButton btnGenerate;
    private JButton btnParams;
    private JTextArea txtCallStmt;
//    private JTextField txtCallStmt;
    private JTextField txtTestName;
    private JTextField txtFailureMessage;
    private JTextField txtAssert;
    private JLabel lblTestName;
    private JLabel lblFailureMessage;
    private JLabel lblAssert;
    private JLabel lblCapturefile;
    private JLabel lblCallStmt;
    private JLabel lblDriver;
    private JLabel lblPassword;
    private JLabel lblURL;
    private JLabel lblUser;
    private JPanel pnlCallStmt;
    private JPanel pnlConnection;
    private JPanel pnlTest;
    private JScrollPane scrOutput;
    private JScrollPane scrProcedureColumns;
    private JTable tblProcedureColumns;
    private JPasswordField pwdPassword;
    private JTextField txtDriver;
    private JTextField txtURL;
    private JTextField txtUser;
    private JTextArea txtTestCase;
    private JComboBox cbDataType;
    private JComboBox cbInOut;
    private JComboBox cbIsNull;


    private Properties guiConfig = new Properties();

    /**
     * This is called from the user terminal. No arguments expected.
     * @param argv the command line arguments
     */
    public static void main(final String[] argv) {
        new GUITool().show();
    }

    /**
     * Creates new form.
     */
    public GUITool() {
        System.out.println("SQLUnit GUI Tool");
        System.out.println("Copyright(c) 2003 The SQLUnit Team");
        String rcfile = System.getProperty("rcfile");

        if (rcfile != null) {
            try {
                guiConfig = ToolUtils.getConfiguration(rcfile);
            } catch (Exception e) {
                System.out.println("Configuration file " + rcfile
                    + " not found");
            }
        }
        TypeMapper mapper = TypeMapper.getTypeMapper();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the
     * form.
     */
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        pnlConnection = new JPanel();
        lblDriver = new JLabel();
        txtDriver = new JTextField();
        lblURL = new JLabel();
        txtURL = new JTextField();
        lblUser = new JLabel();
        txtUser = new JTextField();
        lblPassword = new JLabel();
        pwdPassword = new JPasswordField();
        btnConnect = new JButton();
        pnlCallStmt = new JPanel();
        pnlTest = new JPanel();
        lblTestName  = new JLabel();
        lblCapturefile = new JLabel();
        lblFailureMessage = new JLabel();
        lblAssert = new JLabel();
        txtTestName  = new JTextField();
        txtFailureMessage = new JTextField();
        txtAssert = new JTextField();
        lblCallStmt = new JLabel();
//        txtCallStmt = new JTextField();
        txtCallStmt = new JTextArea();
        cbDataType = new JComboBox();
        cbInOut = new JComboBox();
        cbIsNull = new JComboBox();
        scrProcedureColumns = new JScrollPane();
        scrOutput = new JScrollPane();
        tblProcedureColumns = new JTable();
        btnGenerate = new JButton();
        txtTestCase = new JTextArea();
        btnParams = new JButton();

        getContentPane().setLayout(new GridBagLayout());

        setTitle("SQLUnit GUI Tool");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent evt) {
                exitForm();
            }
        });

        cbDataType.setPreferredSize(LABEL_DIMENSION);

        int typeIdx = 0;

        String dtype = new String();

        while ((dtype = getConfiguredValue("dtype[" + typeIdx + "]")) != "") {
          cbDataType.addItem(dtype);
          typeIdx++;
        }

        cbInOut.addItem("in");
        cbInOut.addItem("out");
        cbInOut.addItem("inout");
        cbInOut.setSelectedItem("in");

        cbIsNull.addItem("true");
        cbIsNull.addItem("false");
        cbIsNull.setSelectedItem("false");

        pnlConnection.setLayout(new GridBagLayout());

        pnlConnection.setBorder(new CompoundBorder(
            new EtchedBorder(),
            new EmptyBorder(
            new Insets(INSET, INSET, INSET, INSET))));
        int xGridOffset = 0;
        int yGridOffset = 0;
        int xPnlGridOffset = 0;
        int yPnlGridOffset = 0;
        lblDriver.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDriver.setLabelFor(txtDriver);
        lblDriver.setText("Driver");
        lblDriver.setMaximumSize(LABEL_DIMENSION);
        lblDriver.setMinimumSize(LABEL_DIMENSION);
        lblDriver.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, INSET, INSET);
        pnlConnection.add(lblDriver, gridBagConstraints);

        xGridOffset++;
        txtDriver.setText(getConfiguredValue("driver"));
        txtDriver.setPreferredSize(TEXT_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, INSET, INSET);
        pnlConnection.add(txtDriver, gridBagConstraints);

        xGridOffset++;
        lblURL.setHorizontalAlignment(SwingConstants.RIGHT);
        lblURL.setLabelFor(txtURL);
        lblURL.setText("URL");
        lblURL.setMaximumSize(LABEL_DIMENSION);
        lblURL.setMinimumSize(LABEL_DIMENSION);
        lblURL.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, INSET, INSET);
        pnlConnection.add(lblURL, gridBagConstraints);

        xGridOffset++;
        txtURL.setText(getConfiguredValue("url"));
        txtURL.setPreferredSize(TEXT_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, INSET, 0);
        pnlConnection.add(txtURL, gridBagConstraints);

        xGridOffset = 0;
        yGridOffset++;
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUser.setLabelFor(txtUser);
        lblUser.setText("User");
        lblUser.setMaximumSize(LABEL_DIMENSION);
        lblUser.setMinimumSize(LABEL_DIMENSION);
        lblUser.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, 0, INSET);
        pnlConnection.add(lblUser, gridBagConstraints);

        xGridOffset++;
        txtUser.setText(getConfiguredValue("user"));
        txtUser.setPreferredSize(TEXT_DIMENSION);
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, INSET);
        pnlConnection.add(txtUser, gridBagConstraints);

        xGridOffset++;
        lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPassword.setLabelFor(pwdPassword);
        lblPassword.setText("Password");
        lblPassword.setMaximumSize(LABEL_DIMENSION);
        lblPassword.setMinimumSize(LABEL_DIMENSION);
        lblPassword.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, 0, INSET);
        pnlConnection.add(lblPassword, gridBagConstraints);

        xGridOffset++;
        pwdPassword.setText(getConfiguredValue("password"));
        pwdPassword.setPreferredSize(PSWD_DIMENSION);
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, INSET);
        pnlConnection.add(pwdPassword, gridBagConstraints);

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                btnConnectActionPerformed();
            }
        });

        xGridOffset++;
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        pnlConnection.add(btnConnect, gridBagConstraints);

        xPnlGridOffset = 0;
        yPnlGridOffset = 0;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(INSET * 2, INSET * 2, 0,
            INSET * 2);
        getContentPane().add(pnlConnection, gridBagConstraints);

        xGridOffset = 0;
        yGridOffset = 0;

        pnlTest.setLayout(new GridBagLayout());
        pnlTest.setBorder(
            new CompoundBorder(new EtchedBorder(),
            new EmptyBorder(new Insets(INSET, INSET, INSET, INSET))));

        lblTestName.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTestName.setLabelFor(txtTestName);
        lblTestName.setText("Test Name");
        lblTestName.setMaximumSize(LABEL_DIMENSION);
        lblTestName.setMinimumSize(LABEL_DIMENSION);
        lblTestName.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(0, 0, INSET, INSET);
        pnlTest.add(lblTestName, gridBagConstraints);

        xGridOffset++;
        txtTestName.setPreferredSize(TEXT_DIMENSION);
        txtTestName.setText("Test Generated by " + TOOLNAME);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, INSET, INSET);
        pnlTest.add(txtTestName, gridBagConstraints);

        yPnlGridOffset++;

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(INSET * 2, INSET * 2, 0,
            INSET * 2);
        getContentPane().add(pnlTest, gridBagConstraints);


        pnlCallStmt.setLayout(new GridBagLayout());

        pnlCallStmt.setBorder(
            new CompoundBorder(new EtchedBorder(),
            new EmptyBorder(new Insets(INSET, INSET, INSET, INSET))));

        xGridOffset = 0;
        yGridOffset = 0;

        lblCallStmt.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCallStmt.setLabelFor(txtCallStmt);
        lblCallStmt.setText("Call:Stmt");
        lblCallStmt.setMaximumSize(LABEL_DIMENSION);
        lblCallStmt.setMinimumSize(LABEL_DIMENSION);
        lblCallStmt.setPreferredSize(LABEL_DIMENSION);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(0, 0, 0, INSET);
        pnlCallStmt.add(lblCallStmt, gridBagConstraints);

        xGridOffset++;
        txtCallStmt.setEditable(false);
        txtCallStmt.setPreferredSize(CALLSTMT_DIMENSION);

//        txtCallStmt.addActionListener(new ActionListener() {
//            public void actionPerformed(final ActionEvent evt) {
//                txtCallStmtActionPerformed();
//            }
//        });

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        pnlCallStmt.add(txtCallStmt, gridBagConstraints);

        btnParams.setText("Params");
        btnParams.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                btnParamsActionPerformed();
            }
        });
        xGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        pnlCallStmt.add(btnParams, gridBagConstraints);

        yPnlGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(INSET * 2, INSET * 2, 0,
            INSET * 2);
        getContentPane().add(pnlCallStmt, gridBagConstraints);

        scrProcedureColumns.setPreferredSize(PARAM_DIMENSION);
        scrProcedureColumns.setMaximumSize(PARAM_DIMENSION);
        tblProcedureColumns.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Name", "Type", "Is-Null", "InOut", "Value"
            }


        ) {
            private Class[] types = new Class [] {
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class,
                java.lang.String.class
            };

            public Class getColumnClass(final int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(final int rowIndex,
                    final int columnIndex) {
                return true;
            }
        });
        scrProcedureColumns.setViewportView(tblProcedureColumns);

        yPnlGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(INSET * 2, INSET * 2, INSET * 2,
            INSET * 2);
        getContentPane().add(scrProcedureColumns, gridBagConstraints);

        btnGenerate.setText("Generate");
        btnGenerate.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                btnGenerateActionPerformed();
            }
        });


        lblCapturefile.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCapturefile.setText("Capture File :  " + getConfiguredValue("capturefile"));
        yPnlGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(lblCapturefile, gridBagConstraints);

        yPnlGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        getContentPane().add(btnGenerate, gridBagConstraints);

        scrOutput.setPreferredSize(OUTPUT_DIMENSION);
        scrOutput.setBorder(
            new CompoundBorder(new EtchedBorder(),
            new EmptyBorder(new Insets(INSET, INSET, INSET, INSET))));
        txtTestCase.setText("Test Case. Copied from  capture file.");
        txtTestCase.setLineWrap(true);
        txtTestCase.setRows(TEXTAREA_NUM_ROWS);
        txtTestCase.setColumns(TEXTAREA_NUM_COLS);
        txtTestCase.setLineWrap(true);

        scrOutput.setViewportView(txtTestCase);

        yPnlGridOffset++;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = GRID_BASE_X + xPnlGridOffset;
        gridBagConstraints.gridy = GRID_BASE_Y + yPnlGridOffset;
        getContentPane().add(scrOutput, gridBagConstraints);

        pack();
        setSize(WINDOWSIZE_X, WINDOWSIZE_Y);
        setLocationRelativeTo(null);

    }


    /**
     * This method is called whenever an action is encountered in the
     * CallStmt TextField. The CallStmt contains the actual statement
     * representing the stored procedure or SQL call.
     */
    private void txtCallStmtActionPerformed() {
        populateProcedureCols();
    }

    /**
     * This method is called whenever Parameters Button is pressed.
     */
    private void btnParamsActionPerformed() {
        populateProcedureCols();
    }

    /**
     * Populates the JTable containing the param elements for this
     * Stored Procedure or SQL call.
     */
    private void populateProcedureCols() {
        try {
            if (connecting) { return; }
            String callStmt = txtCallStmt.getText();
            DefaultTableModel dtm =
                (DefaultTableModel) tblProcedureColumns.getModel();
            int numParams = countParams(callStmt);


            dtm.setRowCount(0);
            for (int i = 0; i < numParams; i++) {
                Vector rowData = new Vector(PARAM_METADATA_LEN);
                for (int j = 0; j < PARAM_METADATA_LEN; j++) {
                    rowData.addElement(new String());
                }
                dtm.addRow(rowData);
            }
            tblProcedureColumns.setModel(dtm);
            TableColumn dtypeColumn = tblProcedureColumns.getColumnModel().getColumn(PARAM_TYPE_INDEX);
            dtypeColumn.setCellEditor(new DefaultCellEditor(cbDataType));
            TableColumn inOutColumn = tblProcedureColumns.getColumnModel().getColumn(PARAM_INOUT_INDEX);
            inOutColumn.setCellEditor(new DefaultCellEditor(cbInOut));
            TableColumn isNullColumn = tblProcedureColumns.getColumnModel().getColumn(PARAM_ISNULL_INDEX);
            isNullColumn.setCellEditor(new DefaultCellEditor(cbIsNull));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method defines the action that is performed when the
     * Connect button is clicked. When the connect button is clicked,
     * a connection is established to the underlying database and
     * the Connection variable in the class is populated.
     */
    private void btnConnectActionPerformed() {
        connecting = true;
        try {
            if (con != null) { ToolUtils.releaseConnection(); }

            guiConfig.setProperty("driver", txtDriver.getText());
            guiConfig.setProperty("url", txtURL.getText());
            guiConfig.setProperty("user", txtUser.getText());
            guiConfig.setProperty("password",
                String.valueOf(pwdPassword.getPassword()));
            con = ToolUtils.getConnection(guiConfig);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        boolean error = false;
        try {
            txtCallStmt.setText("<SELECT|CALL>");
            txtCallStmt.setEditable(true);
            lblCallStmt.setText("Call:Stmt");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            error = true;
        }
        lblCallStmt.setVisible(!error);
        txtCallStmt.setVisible(!error);
        txtCallStmt.setEditable(!error);
        error = false;
        connecting = false;
    }

    /**
     * This method defines the action that is performed when the
     * Generate button is clicked. When the Generate button is clicked,
     * the XML for the test is generated and written out to the capture
     * file defined in the properties file.
     */
    private void btnGenerateActionPerformed() {
        try {
            String sql = txtCallStmt.getText();
            int numParams = tblProcedureColumns.getRowCount();
            Param[] params = new Param[numParams];
            for (int i = 0; i < numParams; i++) {
                params[i] = new Param();
                params[i].setId((new Integer(i + 1)).toString());
                params[i].setName("c" + (new Integer(i + 1)).toString());
                params[i].setType(
                    (String) tblProcedureColumns.getValueAt(i,
                    PARAM_TYPE_INDEX));
                params[i].setIsNull(
                    (String) tblProcedureColumns.getValueAt(i,
                    PARAM_ISNULL_INDEX));
                params[i].setInOut(
                    (String) tblProcedureColumns.getValueAt(i,
                    PARAM_INOUT_INDEX));
                params[i].setValue(
                    (String) tblProcedureColumns.getValueAt(i,
                    PARAM_VALUE_INDEX));
            }

            ToolUtils.makeTest(sql, params, txtTestName.getText(), guiConfig);

            String filename = new String(getConfiguredValue("capturefile"));

            txtTestCase.setText("Text captured to ..." +  filename + "\n\n");
            RandomAccessFile file = new RandomAccessFile(filename, "r");

            String text = new String();

            txtTestCase.setText("\n");

            while ((text = file.readLine()) != null) {
                 txtTestCase.append(text + "\n");
            }

            file.close();
            txtTestCase.selectAll();

        } catch (Exception e) {
            System.err.println("Exception caught in generate: "
                + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Exit the Application. Handles clean up tasks such as closing the
     * database connection and closing the capture file.
     */
    private void exitForm() {
        try {
            if (captureFile != null) {
                captureFile.flush();
                captureFile.close();
            }
            if (con != null) { con.close(); }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        System.exit(0);
    }

    /**
     * Returns the value defined for the given key in the property file.
     * @param propertyName the key representing this property.
     * @return the defined value of the property.
     */
    private String getConfiguredValue(final String propertyName) {
        String value = guiConfig.getProperty(propertyName);
        return (value == null ? "" : value);
    }

    /**
     * Counts the number of replaceable parameters in the SQL or Stored
     * procedure query. Each replaceable parameter is represented by a
     * a question mark character.
     * @param query the query to read.
     * @return the number of parameters in the query.
     */
    private static int countParams(final String query) {
        char[] chars = query.toCharArray();
        int numParams = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') { numParams++; }
        }
        return numParams;
    }

}
