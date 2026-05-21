package ui;

import javafx.application.Platform;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Intercepte System.out pour rediriger les messages du modèle vers l'UI.
 */
public class GameLog {

    private static final List<Consumer<String>> listeners = new ArrayList<>();
    private static PrintStream originalOut;

    public static void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void removeListener(Consumer<String> listener) {
        listeners.remove(listener);
    }

    public static void clearListeners() {
        listeners.clear();
    }

    public static void install() {
        originalOut = System.out;
        PrintStream interceptor = new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                buffer.append(c);
                if (c == '\n') {
                    String line = stripAnsi(buffer.toString().stripTrailing());
                    buffer.setLength(0);
                    if (!line.isBlank()) {
                        dispatch(line);
                    }
                }
            }

            @Override
            public void write(byte[] buf, int off, int len) {
                String s = new String(buf, off, len);
                for (char c : s.toCharArray()) write(c);
            }
        }, true);
        System.setOut(interceptor);
    }

    public static void uninstall() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
    }

    private static void dispatch(String line) {
        if (Platform.isFxApplicationThread()) {
            for (Consumer<String> l : new ArrayList<>(listeners)) l.accept(line);
        } else {
            Platform.runLater(() -> {
                for (Consumer<String> l : new ArrayList<>(listeners)) l.accept(line);
            });
        }
    }

    /** Retire les codes ANSI de couleur console. */
    public static String stripAnsi(String text) {
        return text.replaceAll("\\[[;\\d]*m", "");
    }
}
