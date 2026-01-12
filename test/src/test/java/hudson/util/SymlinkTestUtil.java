package hudson.util;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.TaskListener;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;

public final class SymlinkTestUtil {

    private SymlinkTestUtil() {
        // utility class
    }

     // Checks whether symbolic links are supported on this system.
     // Uses a regular temporary directory to avoid JenkinsRule dependency.

    public static void assumeSymlinksSupported() throws Exception {
        if (!Functions.isWindows()) {
            /* Unix file systems support symbolic links */
            return;
        }

        Path tempDir = Files.createTempDirectory("jenkins-symlink-test");

        FilePath ws = new FilePath(tempDir.toFile());
        FilePath target = ws.child("symlink-target.tmp");
        FilePath link = ws.child("symlink-link.tmp");

        boolean supported;
        try {
            target.write("test", "UTF-8");
            link.symlinkTo(target.getName(), TaskListener.NULL);
            supported = link.exists(); // symlinkTo can fail to create and return silently
        } catch (Exception e) {
            supported = false;
        } finally {
            try {
                link.delete();
                target.delete();
            } finally {
                Files.deleteIfExists(tempDir);
            }
        }
        Assumptions.assumeTrue(supported, "Symbolic links are not supported on this system");
    }
}
