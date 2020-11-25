package monitor;

import comunicacionserial.ArduinoExcepcion;
import comunicacionserial.ComunicacionSerial_Arduino;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class MONITOR extends javax.swing.JFrame {

    ComunicacionSerial_Arduino coneD = new ComunicacionSerial_Arduino();

    ComunicacionSerial_Arduino coneT = new ComunicacionSerial_Arduino();

    public MONITOR() {
        initComponents();

        try {
            coneD.arduinoRXTX("COM6", 9600, listen);
        } catch (ArduinoExcepcion ex) {
        }

        try {
            coneT.arduinoRXTX("COM5", 9600, listen);
        } catch (ArduinoExcepcion ex) {
        }

    }
    SerialPortEventListener listen = new SerialPortEventListener() {
        @Override
        public void serialEvent(SerialPortEvent spe) {
            try {
                if (coneD.isMessageAvailable() == true) {
                    //Dis.setText(coneD.printMessage());
                }
                if (coneT.isMessageAvailable() == true) {
                    //Dis.setText(coneD.printMessage());
                }
            } catch (SerialPortException ex) {
                Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ArduinoExcepcion ex) {
                Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    Timer timer = new Timer();
    int i = 0;
    TimerTask task = new TimerTask() {
        public void run() {
            String time = null;
            try {
                time = getTime(i);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
            }

            int t = 45;

            i++;
            if (i % t == 0) {
                PreparedStatement pps = null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
                }
                java.sql.Connection con = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql:"
                            + "//193.122.132.201:3306/Proyecto", "admin_default", "8524");
                } catch (SQLException ex) {
                    Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
                }
                String sql = "";
                String[] dato = new String[4];
                Date date = new Date();

                DateFormat hourdateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, 30);
                Date tempDate = cal.getTime();
                sql = "select * from ACT where FeHo >='" + hourdateFormat.format(date) + "' and  FeHo <= '" + hourdateFormat.format(tempDate) + "'";

                try {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(new Date());
                    Date Date = cal1.getTime();
                    Statement st = (Statement) con.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        dato[0] = rs.getString(1);
                        dato[1] = rs.getString(2);
                        dato[2] = rs.getString(3);

                        String ID = rs.getString(1);
                        String Actividad = rs.getString(2);
                        String FeHo = rs.getString(3);

                        switch (ID) {
                            case "LM35":
                                jTextArea1.setText("Sistema  :" + ID
                                        + " \r\nActividad :" + Actividad
                                        + " \r\nHora y Fecha :" + FeHo
                                        + " \r\n " + coneT.printMessage()
                                        + " \r\nFecha  y hora de revisión :" + Date);
                                jTextArea1.setEditable(false);
                                jTextArea1.setEditable(false);
                                setSize(450, 300);
                                setTitle("Monitor de Actividades");
                                setVisible(true);
                                break;
                            case "SR04":
                                jTextArea1.setText("Sistema  :" + ID
                                        + " \r\nActividad :" + Actividad
                                        + " \r\nHora y Fecha :" + FeHo
                                        + " \r\n " + coneD.printMessage()
                                        + " \r\nFecha  y hora de revisión :" + Date);
                                jTextArea1.setEditable(false);
                                setSize(450, 300);
                                setTitle("Monitor de Actividades");
                                setVisible(true);
                                break;
                        }

                    }
                } catch (SQLException e) {

                } catch (SerialPortException ex) {
                    Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ArduinoExcepcion ex) {
                    Logger.getLogger(MONITOR.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }
    };

    public void runTimer() {
        timer.schedule(task, 0, 1000);
    }

    static String getTime(int sec) throws ClassNotFoundException, SQLException {

        int hours = 0;
        int remainderOfHours = 0;
        int minutes = 0;
        int seconds = 0;
        if (sec >= 3600) {
            hours = sec / 3600;
            remainderOfHours = sec % 3600;

            if (remainderOfHours >= 60) {
                minutes = remainderOfHours / 60;
                seconds = remainderOfHours % 60;
            } else {
                seconds = remainderOfHours;
            }
        } else if (sec >= 60) {

            hours = 0;
            minutes = sec / 60;
            seconds = sec % 60;
        } else if (sec < 60) {

            hours = 0;
            minutes = 0;
            seconds = sec;
        }

        String strHours;
        String strMins;
        String strSecs;

        if (seconds < 10) {
            strSecs = "0" + Integer.toString(seconds);
        } else {
            strSecs = Integer.toString(seconds);
        }

        if (minutes < 10) {
            strMins = "0" + Integer.toString(minutes);
        } else {
            strMins = Integer.toString(minutes);
        }

        if (hours < 10) {
            strHours = "0" + Integer.toString(hours);
        } else {
            strHours = Integer.toString(hours);
        }

        String time = strHours + ":" + strMins + ":" + strSecs;
        return time;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel9 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel9.setFont(new java.awt.Font("Trebuchet MS", 3, 14)); // NOI18N
        jLabel9.setText("MONITOR DE ACTIVIDADES");

        jButton4.setText("Salir");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addComponent(jLabel9)
                        .addGap(0, 94, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(152, 152, 152))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        MONITOR tut = new MONITOR();
        tut.runTimer();
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MONITOR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MONITOR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MONITOR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MONITOR.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MONITOR().setVisible(true);

            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
