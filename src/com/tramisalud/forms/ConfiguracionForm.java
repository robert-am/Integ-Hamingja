package com.tramisalud.forms;

import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.tramisalud.configuration.Configuration;

public class ConfiguracionForm extends JFrame {

  private JPanel contentPane;
  private JTextField txtWebService;
  private JTextField txtUsuario;
  private JPasswordField txtPassword;
  private JTextField txtBandejaEntrada;
  private JTextField txtBandejaSalida;

  Configuration conf = null;
  private JTextField txtBandejaErroneos;

  /**
   * Create the frame.
   */
  public ConfiguracionForm() {
    //setType(Type.POPUP);
    setTitle("Configuración");
    setResizable(false);
    setBounds(100, 100, 450, 321);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
        FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
        FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("max(9dlu;default)"),
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("max(6dlu;default)"),
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
        FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

    JLabel lblSugarcrmUrl = new JLabel("Web Service URL:");
    contentPane.add(lblSugarcrmUrl, "4, 4, left, default");

    txtWebService = new JTextField();
    contentPane.add(txtWebService, "6, 4, 9, 1, fill, default");
    txtWebService.setColumns(10);

    JLabel lblUsuario = new JLabel("Usuario:");
    contentPane.add(lblUsuario, "4, 6, left, default");

    txtUsuario = new JTextField();
    contentPane.add(txtUsuario, "6, 6, 9, 1, fill, default");
    txtUsuario.setColumns(10);

    JLabel lblPassword = new JLabel("Password:");
    contentPane.add(lblPassword, "4, 8, left, default");

    txtPassword = new JPasswordField();
    txtPassword.setEchoChar('*');
    contentPane.add(txtPassword, "6, 8, 9, 1, fill, default");

    JLabel lblDirectorioBandejaEntrada = new JLabel("Bandeja de entrada:");
    contentPane.add(lblDirectorioBandejaEntrada, "4, 12, left, default");

    txtBandejaEntrada = new JTextField();
    contentPane.add(txtBandejaEntrada, "6, 12, 9, 1, fill, default");
    txtBandejaEntrada.setColumns(10);

    final JButton btnNewButton = new JButton("...");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retValue = chooser.showOpenDialog(btnNewButton);
        if (retValue == JFileChooser.APPROVE_OPTION) {
          txtBandejaEntrada.setText(chooser.getSelectedFile().getPath());
        }
      }
    });
    contentPane.add(btnNewButton, "16, 12, fill, default");

    JLabel lblDirectorioBandejaDe = new JLabel("Bandeja de salida:");
    lblDirectorioBandejaDe.setHorizontalAlignment(SwingConstants.LEFT);
    contentPane.add(lblDirectorioBandejaDe, "4, 14, left, default");

    txtBandejaSalida = new JTextField();
    contentPane.add(txtBandejaSalida, "6, 14, 9, 1, fill, default");
    txtBandejaSalida.setColumns(10);

    JButton btnNewButton_1 = new JButton("...");
    btnNewButton_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retValue = chooser.showOpenDialog(btnNewButton);
        if (retValue == JFileChooser.APPROVE_OPTION) {
          txtBandejaSalida.setText(chooser.getSelectedFile().getPath());
        }
      }
    });
    contentPane.add(btnNewButton_1, "16, 14, fill, default");

    JLabel lblBandejaDeErroneos = new JLabel("Bandeja de Erroneos:");
    lblBandejaDeErroneos.setHorizontalAlignment(SwingConstants.LEFT);
    contentPane.add(lblBandejaDeErroneos, "4, 16, left, default");

    txtBandejaErroneos = new JTextField();
    txtBandejaErroneos.setText((String) null);
    txtBandejaErroneos.setColumns(10);
    contentPane.add(txtBandejaErroneos, "6, 16, 9, 1, fill, default");

    JButton button_2 = new JButton("...");
    button_2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retValue = chooser.showOpenDialog(btnNewButton);
        if (retValue == JFileChooser.APPROVE_OPTION) {
          txtBandejaErroneos.setText(chooser.getSelectedFile().getPath());
        }
      }
    });
    contentPane.add(button_2, "16, 16");

    Button button = new Button("Aceptar");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        conf = new Configuration();
        conf.load();
        conf.setWebServiceURL(txtWebService.getText());
        conf.setUserName(txtUsuario.getText());
        conf.setPassword(txtPassword.getText());
        conf.setDirectoryBandejaEntrada(txtBandejaEntrada.getText());
        conf.setDirectoryBandejaSalida(txtBandejaSalida.getText());
        conf.setDirectoryBandejaErroneos(txtBandejaErroneos.getText());
        conf.save();
        JOptionPane.showMessageDialog(null,
            "Los datos de configuracion se han almacenado correctamente", "Tramisalud",
            JOptionPane.INFORMATION_MESSAGE);
        closeForm();
      }
    });
    contentPane.add(button, "12, 20, center, top");

    Button button_1 = new Button("Cancelar");
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeForm();
      }
    });
    contentPane.add(button_1, "14, 20");
    initialize();
    this.pack();
   
  }

  private void closeForm() {
    this.setVisible(false);
    dispose();
  }

  private void initialize() {
    conf = new Configuration();
    conf.load();
    txtWebService.setText(conf.getWebServiceURL());
    txtUsuario.setText(conf.getUserName());
    txtPassword.setText(conf.getPassword());
    txtBandejaEntrada.setText(conf.getDirectoryBandejaEntrada());
    txtBandejaSalida.setText(conf.getDirectoryBandejaSalida());
    txtBandejaErroneos.setText(conf.getDirectoryBandejaErroneos());
  }
}
