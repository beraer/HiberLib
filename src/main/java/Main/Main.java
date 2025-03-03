package Main;

import app.LoginFrame;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.h2.tools.Server;

import javax.swing.*;

public class Main {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void initializeEntityManager() {
        emf = Persistence.createEntityManagerFactory("LibraryPU");
        em = emf.createEntityManager();
    }

    public static EntityManager getEntityManager() {
        return em;
    }

    public static void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeEntityManager();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(getEntityManager());
            loginFrame.setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(Main::closeEntityManager));
    }
}