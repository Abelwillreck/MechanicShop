package application;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class AuthGate {

    // Match your existing app directory approach
    private static final String APP_DIR =
            System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "MechanicShop";

    private static final Path AUTH_FILE = Paths.get(APP_DIR, "auth.txt");

    // PBKDF2 parameters
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;

    private AuthGate() {}

    public static boolean requireLogin() {
        try {
            Files.createDirectories(Paths.get(APP_DIR));

            // If no password set yet, force creation on first run
            if (!Files.exists(AUTH_FILE)) {
                return firstTimeSetup();
            }

            for (int attempts = 0; attempts < 5; attempts++) {
                String pw = promptPassword("MechanicShop Login", "Enter shop password:");
                if (pw == null) return false; // cancel

                if (verifyPassword(pw)) return true;

                alert("Incorrect password.", Alert.AlertType.ERROR);
            }

            alert("Too many failed attempts.", Alert.AlertType.ERROR);
            return false;

        } catch (Exception e) {
            // If auth system fails, safer to block access
            alert("Authentication error: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    private static boolean firstTimeSetup() throws Exception {
        alert("First-time setup: create a shop password.", Alert.AlertType.INFORMATION);

        while (true) {
            String pw1 = promptPassword("Create Password", "Create shop password:");
            if (pw1 == null) return false;

            String pw2 = promptPassword("Confirm Password", "Confirm shop password:");
            if (pw2 == null) return false;

            if (pw1.length() < 8) {
                alert("Password must be at least 8 characters.", Alert.AlertType.WARNING);
                continue;
            }
            if (!pw1.equals(pw2)) {
                alert("Passwords do not match.", Alert.AlertType.WARNING);
                continue;
            }

            savePasswordHash(pw1);
            alert("Password set. Please log in.", Alert.AlertType.INFORMATION);
            return true; // allow entry right after setup
        }
    }

    private static void savePasswordHash(String password) throws Exception {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);

        String line = "v1:" + ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);

        Files.writeString(AUTH_FILE, line, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static boolean verifyPassword(String password) throws Exception {
        String line = Files.readString(AUTH_FILE, StandardCharsets.UTF_8).trim();
        // format: v1:iterations:saltB64:hashB64
        String[] parts = line.split(":");
        if (parts.length != 4 || !parts[0].equals("v1")) return false;

        int iters = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expected = Base64.getDecoder().decode(parts[3]);

        byte[] actual = pbkdf2(password.toCharArray(), salt, iters, expected.length * 8);

        return constantTimeEquals(expected, actual);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLenBits) throws Exception {
        KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLenBits);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return f.generateSecret(spec).getEncoded();
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= (a[i] ^ b[i]);
        return r == 0;
    }

    private static String promptPassword(String title, String header) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.initStyle(StageStyle.UTILITY);

        PasswordField pf = new PasswordField();
        pf.setPromptText("Password");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.add(new Label("Password:"), 0, 0);
        gp.add(pf, 1, 0);

        dialog.getDialogPane().setContent(gp);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? pf.getText() : null);

        return dialog.showAndWait().orElse(null);
    }

    private static void alert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle("MechanicShop");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}