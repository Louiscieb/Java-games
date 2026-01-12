package io.github.louiscieb.towerdefence.lwjgl3;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import org.lwjgl.system.macosx.LibC;
import org.lwjgl.system.macosx.ObjCRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import static org.lwjgl.system.JNI.invokePPP;
import static org.lwjgl.system.JNI.invokePPZ;
import static org.lwjgl.system.macosx.ObjCRuntime.objc_getClass;
import static org.lwjgl.system.macosx.ObjCRuntime.sel_getUid;

/**
 * Classe utilitaire permettant de gérer le redémarrage de la JVM
 * lorsque cela est nécessaire pour assurer la compatibilité
 * avec certaines plateformes (Windows et macOS).
 * <p>
 * Elle permet notamment :
 * <ul>
 *     <li>D’éviter des bugs de chargement des DLL sous Windows</li>
 *     <li>De garantir le lancement sur le thread principal sous macOS</li>
 * </ul>
 */
public class StartupHelper {

    /**
     * <p>
     * Argument JVM utilisé pour éviter les boucles infinies de redémarrage.
     * </p>
     */
    private static final String JVM_RESTARTED_ARG = "jvmIsRestarted";

    /**
     * <p>
     * Constructeur privé pour empêcher l’instanciation de la classe.
     * </p>
     */
    private StartupHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Vérifie si la JVM doit être redémarrée et la relance si nécessaire.
     *
     * @param redirectOutput indique si la sortie standard doit être redirigée
     * @return {@code true} si la JVM a été redémarrée, {@code false} sinon

     */
    public static boolean startNewJvmIfRequired(boolean redirectOutput) {
        String osName = System.getProperty("os.name").toLowerCase(java.util.Locale.ROOT);

        /**
         * <p>
         * Cas Windows : évite un bug LWJGL lié aux caractères spéciaux
         * dans le nom d’utilisateur.
         * </p>
         */
        if (!osName.contains("mac")) {
            if (osName.contains("windows")) {
                String programData = System.getenv("ProgramData");
                if (programData == null) programData = "C:\\Temp\\";

                String prevTmpDir = System.getProperty("java.io.tmpdir", programData);
                String prevUser = System.getProperty("user.name", "libGDX_User");

                System.setProperty("java.io.tmpdir", programData + "/libGDX-temp");
                System.setProperty(
                    "user.name",
                    ("User_" + prevUser.hashCode() + "_GDX" + Version.VERSION)
                        .replace('.', '_')
                );

                Lwjgl3NativesLoader.load();

                System.setProperty("java.io.tmpdir", prevTmpDir);
                System.setProperty("user.name", prevUser);
            }
            return false;
        }

        /**
         * Cas GraalVM : aucun redémarrage nécessaire.
         */
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        /**
         * <p>
         * Vérifie si l’application est déjà lancée sur le thread principal macOS.
         * </p>
         */
        long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
        long NSThread = objc_getClass("NSThread");
        long currentThread = invokePPP(NSThread, sel_getUid("currentThread"), objc_msgSend);
        boolean isMainThread = invokePPZ(currentThread, sel_getUid("isMainThread"), objc_msgSend);
        if (isMainThread) return false;

        long pid = LibC.getpid();
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        /**
         * <p>
         * Protection contre les boucles infinies de redémarrage.
         * </p>
         */
        if ("true".equals(System.getProperty(JVM_RESTARTED_ARG))) {
            System.err.println(
                "There was a problem evaluating whether the JVM was started with the -XstartOnFirstThread argument."
            );
            return false;
        }

        /**
         * <p>
         * Relance la JVM avec l’option -XstartOnFirstThread (obligatoire sur macOS).
         * </p>
         */
        ArrayList<String> jvmArgs = new ArrayList<>();
        String separator = System.getProperty("file.separator", "/");
        String javaExecPath = System.getProperty("java.home") + separator + "bin" + separator + "java";

        if (!(new File(javaExecPath)).exists()) {
            System.err.println(
                "A Java installation could not be found. If you are distributing this app with a bundled JRE, be sure to set the -XstartOnFirstThread argument manually!"
            );
            return false;
        }

        jvmArgs.add(javaExecPath);
        jvmArgs.add("-XstartOnFirstThread");
        jvmArgs.add("-D" + JVM_RESTARTED_ARG + "=true");
        jvmArgs.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        jvmArgs.add("-cp");
        jvmArgs.add(System.getProperty("java.class.path"));

        String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (mainClass == null) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (trace.length > 0) {
                mainClass = trace[trace.length - 1].getClassName();
            } else {
                System.err.println("The main class could not be determined.");
                return false;
            }
        }
        jvmArgs.add(mainClass);

        try {
            if (!redirectOutput) {
                new ProcessBuilder(jvmArgs).start();
            } else {
                Process process = new ProcessBuilder(jvmArgs)
                    .redirectErrorStream(true)
                    .start();

                BufferedReader processOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
                );

                String line;
                while ((line = processOutput.readLine()) != null) {
                    System.out.println(line);
                }

                process.waitFor();
            }
        } catch (Exception e) {
            System.err.println("There was a problem restarting the JVM");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Variante simplifiée de {@link #startNewJvmIfRequired(boolean)}
     * avec redirection de la sortie activée.
     *
     * @return {@code true} si la JVM a été redémarrée, {@code false} sinon

     */
    public static boolean startNewJvmIfRequired() {
        return startNewJvmIfRequired(true);
    }
}
