package org.apache.maven.plugin.regex_copy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RecursiveRegexCopyMojoTest {
    @Rule
    public MojoRule rule = new MojoRule();

    private MavenProject mavenProject;

    private File pom;

    private static final File TARGET_DIRECTORY = new File("target");

    @Before
    public void setUp() throws Exception {
        mavenProject =
            rule.readMavenProject(
                new File("src/test/resources/recursive-example")
            );
        assertThat(mavenProject).isNotNull();
        pom = mavenProject.getFile();
        assertThat(pom).isNotNull().exists();
        if (!TARGET_DIRECTORY.exists()) {
            TARGET_DIRECTORY.mkdirs();
        }
    }

    @Test
    public void testRegexCopyGoal() throws Exception {
        Mojo mojo = rule.lookupMojo("regex-copy", pom);

        File sourceDirectory =
            File.class.cast(
                    rule
                        .getVariablesAndValuesFromObject(mojo)
                        .get("sourceDirectory")
                );

        rule.setVariableValueToObject(
            mojo,
            "sourceDirectory",
            new File(
                "src/test/resources/recursive-example/" +
                sourceDirectory.getPath()
            )
        );
        File destinationDirectory =
            File.class.cast(
                    rule
                        .getVariablesAndValuesFromObject(mojo)
                        .get("destinationDirectory")
                );

        rule.setVariableValueToObject(
            mojo,
            "destinationDirectory",
            new File(
                "target/test-recursive-example/" +
                destinationDirectory.getPath()
            )
        );

        mojo.execute();
        //        assertThat(tempDirectory.resolve("RegexCopyMojo/Test.java").toFile())
        //            .exists()
        //            .isFile();
    }
}
